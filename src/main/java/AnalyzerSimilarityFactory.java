import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

public class AnalyzerSimilarityFactory {
    private final String[] mStopWordList = {"test,test"};

    /**
     * This method constructs and returns different types of
     * analyzers based on an input string
     *
     * @param analyzerType the analyzer that should be returned
     * @return the respective analyzer
     */
    public static Analyzer getAnalyzer(String analyzerType){
        if(analyzerType.equalsIgnoreCase("standard")) {
            return new StandardAnalyzer();
        }
        if(analyzerType.equalsIgnoreCase("english")) {
            return new EnglishAnalyzer();
        }
        if(analyzerType.equalsIgnoreCase("whitespace")) {
            return new WhitespaceAnalyzer();
        }

        return null;
    }

    /**
     * This method constructs and returns different types of
     * similarities based on an input string
     *
     * @param similaritiesType the similarity that should be returned
     * @return the respective similarity
     */
    public static Similarity getSimilarity(String similaritiesType){
        if(similaritiesType.equalsIgnoreCase("bm25")) {
            return new BM25Similarity();
        }
        if(similaritiesType.equalsIgnoreCase("vsm")) {
            return new TFIDFSimilarity() {
                @Override
                public float tf(float v) {
                    return 0;
                }

                @Override
                public float idf(long l, long l1) {
                    return 0;
                }

                @Override
                public float lengthNorm(int i) {
                    return 0;
                }
            };
        }
        if(similaritiesType.equalsIgnoreCase("lmd")) {
            return new LMDirichletSimilarity();
        }

        return null;
    }
}
