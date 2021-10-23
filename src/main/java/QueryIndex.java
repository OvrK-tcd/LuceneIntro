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
import java.util.HashMap;

public class QueryIndex {

    //<! The maximum number of search results that are retrieved for a query
    private final short MAX_RESULTS = 10;

    /**
     *
     * @param queries
     * @param directoryLocation
     * @throws IOException
     * @throws ParseException
     */
    public void queryMap(HashMap<Integer,String> queries, String directoryLocation) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(directoryLocation));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        indexSearcher.setSimilarity(new BM25Similarity());
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "authors", "bibliography", "description"},
                new StandardAnalyzer());

        for (int id : queries.keySet()) {
            Query query = parser.parse(QueryParser.escape(queries.get(id)));
            ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS).scoreDocs;

            for (ScoreDoc hit : hits)
            {
                Document hitDoc = indexSearcher.doc(hit.doc);
                System.out.println(id + " 0 " + hitDoc.get("id") + " 0 " + hit.score + " OLIVER");
            }
        }

        // close everything we used
        directoryReader.close();
        directory.close();
    }
}
