import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{
    // <!Directory where the search index will be saved
    private final String EMPTY_STRING = "";

    /**
        TODO: Description

        @param input test
        @param regex test
        @return Test
     */
    private String extractPattern(String input, String regex) {
        Pattern p = Pattern.compile(regex,Pattern.DOTALL);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group(0);
        }
        return EMPTY_STRING;
    }

    /**
        TODO: Description

        @param file
        @return
     */
    public boolean parseIndex(String file, String indexDirectoryLocation) throws IOException {

        Analyzer analyzer = new StandardAnalyzer();
        ArrayList<Document> documents = new ArrayList<Document>();
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryLocation));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file)));
            System.out.printf("Indexing \"%s\"\n", file);
            String[]entries = content.split("(?=.I \\d{1,4}\\n)");
            System.out.println(entries[0]);
            for (String currEntry : entries)
            {
                Document doc = new Document();
                boolean empty = true;
                String id = extractPattern(currEntry,"(?<=.I )\\d{1,4}");
                if(!id.isEmpty()) {
                    doc.add(new TextField("id", id, Field.Store.YES));
                }

                String title = extractPattern(currEntry,"(?<=.T\\n).*?(?=.A\\n)");
                if(!title.isEmpty()) {
                    doc.add(new TextField("title", title, Field.Store.YES));
                    empty = false;
                }

                String authors = extractPattern(currEntry,"(?<=.A\\n).*?(?=.B\\n)");
                if(!authors.isEmpty()) {
                    doc.add(new TextField("authors", authors, Field.Store.YES));
                    empty = false;
                }

                String bibliography = extractPattern(currEntry,"(?<=.B\\n).*?(?=.W\\n)");
                if(!bibliography.isEmpty()) {
                    doc.add(new TextField("bibliography", bibliography, Field.Store.YES));
                    empty = false;
                }

                String description = extractPattern(currEntry,"(?<=.W\\n).*");
                if(!description.isEmpty()) {
                    doc.add(new TextField("description", description, Field.Store.YES));
                    empty = false;
                }
                if(!empty)
                {
                    documents.add(doc);
                }
            }
        }
        catch (InvalidPathException | NoSuchFileException exception){
            System.out.println("Invalid path! Can not index: " + file);
            iwriter.close();
            directory.close();
            return false;
        }

        iwriter.addDocuments(documents);
        iwriter.close();
        directory.close();
        System.out.println("Indexing finished successfully");
        return true;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public HashMap<Integer,String> parseQueries(String file) throws IOException {
        HashMap<Integer,String> resultMap = new HashMap<>();
        String content = new String(Files.readAllBytes(Paths.get(file)));
        String[]entries = content.split("(?=.I \\d{1,4}\\n)");
        for (String currEntry : entries)
        {
            int id;
            try {
                id = Integer.parseInt(extractPattern(currEntry,"(?<=.I )\\d{1,4}"));
            }
            catch (NumberFormatException e)
            {
                System.out.println("parseQueries: entry had no valid id.");
                continue;
            }
            String description = extractPattern(currEntry,"(?<=.W\\n).*");
            if(!description.isEmpty()) {
                resultMap.put(id,description);
            }
        }

        return resultMap;
    }
}
