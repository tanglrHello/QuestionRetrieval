import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class IndexSearchTest {

	public static void main(String[] args) throws IOException{
		IndexSearchTest test = new IndexSearchTest();
		
		// choice level
//		String choiceIndexDir = "indexDir/choice";
//		test.buildIndex(choiceIndexDir, "choice");
//		test.doSearch(choiceIndexDir, "choice", "北京当日的气温比杭州高");
		
		// question level
		String questionIndexDir = "indexDir/question";
		test.buildIndex(questionIndexDir, "question");
		test.doSearch(questionIndexDir, "question", "北京当日的气温比杭州高");
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
				int indexedChoiceNum = indexer.index(outputFilePath,"choice");
				System.out.println("Indexed choice number:"+indexedChoiceNum);
			}else if (method.equals("question")){
				indexer.processRawData(inputFilePath, outputFilePath, "question");
				int indexedQuestionNum = indexer.index(outputFilePath,"question");
				System.out.println("Indexed question number:"+indexedQuestionNum);
			}
			
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			indexer.close();
		}
	}
	
	// search query in index directory
	public void doSearch(String indexDir, String method, String querystr) throws IOException{
		if (method.equals("choice")){
			QuestionSearch qs = new QuestionSearch(indexDir);
			ArrayList<Document> res = qs.search("choice", querystr);
			qs.close();
			
			for (Document doc : res){
				System.out.println(doc.getField("choiceContent"));
			}
		}else{
			QuestionSearch qs = new QuestionSearch(indexDir);
			ArrayList<Document> res = qs.search("question", querystr);
			qs.close();
			
			for (Document doc : res){
				Question q = new Question(doc.getField("questionContent").stringValue());
				System.out.println(q.toString());
			}
		}
		
	}

}
