import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CustomAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new ClassicFilter(tokenizer);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, getStopWprds());
        tokenStream = new PorterStemFilter(tokenStream);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }

    private CharArraySet getStopWprds(){
        CharArraySet stopwords = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("../../../src/stopwords.txt"));
            String[] words = new String(encoded, StandardCharsets.UTF_8).split("\n");
            stopwords =  new CharArraySet(Arrays.asList(words), true);
        } catch (IOException ioe) {
            System.out.println("Error Reading stopwords file");
        }
        return stopwords;
    }
}