import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class QueryIndex {
    private final short MAX_RESULTS = 10;

    public void query(String queryTerm, String directoryLocation) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(directoryLocation));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        indexSearcher.setSimilarity(new BM25Similarity());
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "authors", "bibliography", "description"},
                new StandardAnalyzer());
        Query query = parser.parse(QueryParser.escape(queryTerm));
        ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS).scoreDocs;

        System.out.println("Documents: " + hits.length);
        for (int i = 0; i < hits.length; i++)
        {
            Document hitDoc = indexSearcher.doc(hits[i].doc);
            System.out.println(i + ") " + hitDoc.get("title") + " " + hits[i].score);
        }

        // close everything we used
        directoryReader.close();
        directory.close();
    }
}
