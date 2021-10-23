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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;

public class QueryIndex {

    //<! The maximum number of search results that are retrieved for a query
    private final short MAX_RESULTS = 1400;
    //<! The location where the file with the rankings of the queries is stored
    private final String RANKINGS_LOCATION = "../../rankings.txt";
    //<!
    private String mAnalyzerString;
    //<!
    private String mSimilarityString;

    QueryIndex(String analyzer, String similarity) {
        mAnalyzerString = analyzer;
        mSimilarityString = similarity;
    }


    /**
     *  This function executes a set of queries on a given index and writes the resulting
     *  hit scores into a file
     *
     * @param queries a map <Integer,String> which maps id of a query to its search text
     * @param indexDirectoryLocation location where the created index should be stored
     * @throws IOException when the directory could not be opened
     * @throws ParseException when a query could not be parsed
     */
    public void queryMap(HashMap<Integer,String> queries, String indexDirectoryLocation) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryLocation));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        indexSearcher.setSimilarity(AnalyzerSimilarityFactory.getSimilarity(mSimilarityString));
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{FieldNames.TITLE.getName(),
                FieldNames.AUTHOR.getName(),
                FieldNames.BIBLIOGRAPHY.getName(),
                FieldNames.DESCRIPTION.getName()},
                AnalyzerSimilarityFactory.getAnalyzer(mAnalyzerString));

        PrintWriter writer = new PrintWriter(RANKINGS_LOCATION, StandardCharsets.UTF_8);

        for (int id : queries.keySet()) {
            Query query = parser.parse(QueryParser.escape(queries.get(id)));
            ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS).scoreDocs;

            for (ScoreDoc hit : hits)
            {
                Document hitDoc = indexSearcher.doc(hit.doc);
                writer.println(id + " 0 " + hitDoc.get("id") + " 0 " + hit.score + " OLIVER");
            }
        }

        writer.close();
        directoryReader.close();
        directory.close();
    }
}
