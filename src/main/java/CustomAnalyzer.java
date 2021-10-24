import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.HyphenatedWordsFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tr.ApostropheFilter;

import java.util.Arrays;

public class CustomAnalyzer extends Analyzer {
    private String[] mStopWordList = {
            "a", "an", "and", "are","aren't", "as", "at", "be", "but", "by","can","can't", "does","how",
            "for", "if", "in", "into", "is", "it","have","haven't","why","has",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "what", "was", "will", "with"
    };

    private CharArraySet mStopWordCharArrayList = new CharArraySet(Arrays.asList(mStopWordList),true);

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        final Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new ClassicFilter(tokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new HyphenatedWordsFilter(tokenStream);
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, mStopWordCharArrayList);
        //tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }




}