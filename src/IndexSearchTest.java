import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class IndexSearchTest {

	public static void main(String[] args) throws IOException{
		// choice level
		String indexDir = "indexDir/choice";
		IndexSearchTest test = new IndexSearchTest();
		test.buildIndex(indexDir, "choice");
		test.doSearch(indexDir, "choice", "北京当日的气温比杭州高");
	}
	
	// delete old indexing data, and then build new index
	public void buildIndex(String indexDir, String method) throws IOException{
		String inputFilePath = "data/CQs.txt";
		String outputFilePath = "data/CQs.out.txt";
		
		QuestionIndex indexer = null;
		try{
			indexer = new QuestionIndex(indexDir);
			indexer.clearIndex();
			
			if (method.equals("choice")){
				indexer.processRawData(inputFilePath, outputFilePath, "choice");
				
				int indexedChoiceNum = indexer.indexByChoice(outputFilePath);
				System.out.println("Indexed choice number:"+indexedChoiceNum);
			}
			
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			indexer.close();
		}
	}
	
	// search query in index directory
	public void doSearch(String indexDir, String method, String querystr) throws IOException{
		QuestionSearch qs = new QuestionSearch(indexDir);
		ArrayList<Document> res = qs.search("choice", querystr);
		qs.close();
		
		for (Document doc : res){
			System.out.println(doc.getField("choiceContent"));
		}
	}

}
