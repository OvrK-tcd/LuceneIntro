import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class LuceneSearchEngine {
    private static final String INDEX_DIRECTORY_LOCATION = "../index";

    public static void main(String[] args)
    {
        try{
            Parser parser = new Parser();
            if(parser.parseIndex(args[0],INDEX_DIRECTORY_LOCATION)) {
                HashMap<Integer,String> queries  = parser.parseQueries(args[1]);
                QueryIndex queryIndex = new QueryIndex();
                queryIndex.query(queries.get(1),INDEX_DIRECTORY_LOCATION);
            }
            else {
                System.out.println("Error. Could not create index");
            }
        } catch (IOException | ParseException e){
            e.printStackTrace();
        }
    }
}
