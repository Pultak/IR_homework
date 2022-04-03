package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.apache.lucene.analysis.cz.CzechAnalyzer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class HelloLucene {



  private static final String INDEX_DIRECTORY_PATH = "indexDir";
  private static final String STOP_WORDS_FILE_PATH = "stopwords-cs.txt";
  private static final String DATA_FILE_PATH = "2022-02-28_10_31_868.txt";

  private static final String DATE_CREATED_JSON = "dateCreated";
  private static final String PRICE_JSON = "price";
  private static final String SEEN_COUNT_JSON = "seenCount";
  private static final String LOCATION_JSON = "location";
  private static final String TITLE_JSON = "title";
  private static final String MAIN_TEXT_JSON = "mainText";

  private static final String PAGE_JSON = "page";




  public static boolean loadDocs(Directory index, IndexWriterConfig config){
    try {
      IndexWriter w = new IndexWriter(index, config);
      File f = new File(DATA_FILE_PATH);
      InputStream is = new FileInputStream(f);
      String jsonTxt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      JSONParser a = new JSONParser();
      JSONArray root = (JSONArray) a.parse(jsonTxt);

      root.forEach((object) -> {
        try {
          JSONObject o = (JSONObject) object;
          addMyDoc(w, o);

        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      w.close();
      return true;
    }catch(IOException | org.json.simple.parser.ParseException e){
      System.out.println("Something went wrong!");
        e.printStackTrace();
        return false;
    }
  }

  public static void main(String[] args) throws IOException, ParseException, org.json.simple.parser.ParseException {

    if(args.length < 1){
      System.err.println("Not enough parameters passed! Usage: app.jar <query> <>");
      return;
    }

    // 1. create the index
    Directory index;
    try{
       index = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
    }catch(IOException e){
      System.err.println("Something went wrong!");
      e.printStackTrace();
      return;
    }

    //read all stop words
    FileReader fr = new FileReader(new File(STOP_WORDS_FILE_PATH));
    BufferedReader br = new BufferedReader(fr);
    CharArraySet stopWords = new CharArraySet(423, true);
    String line;
    while((line=br.readLine()) != null){
      stopWords.add(line);
    }
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    Analyzer analyzer = new CzechAnalyzer(stopWords);
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    if(!DirectoryReader.indexExists(index)){
      if(!loadDocs(index, config)) return;
    }


    BufferedReader userReader = new BufferedReader(
            new InputStreamReader(System.in));

    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);

    while(true){
      // 2. query
      System.out.print("Insert new query: ");
      String querystr = userReader.readLine();
      if(querystr.equals("q")) break;

      // the "title" arg specifies the default field to use
      // when no field is explicitly specified in the query.
      Query q = new QueryParser(MAIN_TEXT_JSON, analyzer).parse(querystr);
      System.out.println("Searching for '" + querystr + "' using QueryParser");
      System.out.println("Type of query: " + q.getClass().getSimpleName());

      // 3. search
      int hitsPerPage = 10;
      TopDocs docs = searcher.search(q, 10000);
      ScoreDoc[] hits = docs.scoreDocs;


      // 4. display results
      System.out.println("Found " + hits.length + " hits.");

      for(int i = 1; i <= hits.length; ++i) {
        int docId = hits[i - 1].doc;
        Document d = searcher.doc(docId);
        System.out.println((i) + ". " + d.get("page") + "\t" + d.get("title") + " Score: " + hits[i - 1].score);
        if(i % hitsPerPage == 0){
          System.out.println("Next page? 'y' -> YES; other -> NO, return to query");
          String response = userReader.readLine();
          if(!response.toLowerCase().equals("y")){
            break;
          }

        }
      }
    }
    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }

  private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("isbn", isbn, Field.Store.YES));
    w.addDocument(doc);
  }


  private static void addMyDoc(IndexWriter w, JSONObject docObject) throws IOException {
    Document doc = new Document();


    doc.add(new TextField(DATE_CREATED_JSON, docObject.get(DATE_CREATED_JSON).toString(), Field.Store.YES));
    doc.add(new StringField(PRICE_JSON, docObject.get(PRICE_JSON).toString(), Field.Store.YES));
    doc.add(new StringField(SEEN_COUNT_JSON, docObject.get(SEEN_COUNT_JSON).toString(), Field.Store.YES));
    doc.add(new StringField(LOCATION_JSON, docObject.get(LOCATION_JSON).toString(), Field.Store.YES));
    doc.add(new TextField(TITLE_JSON, docObject.get(TITLE_JSON).toString(), Field.Store.YES));
    doc.add(new TextField(MAIN_TEXT_JSON, docObject.get(MAIN_TEXT_JSON).toString(), Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField(PAGE_JSON, docObject.get(PAGE_JSON).toString(), Field.Store.YES));
    w.addDocument(doc);
  }
}
