import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class IndexSearchTest {

	public static void main(String[] args) throws IOException{
		IndexSearchTest test = new IndexSearchTest();
		
		// choice level
		String choiceIndexDir = "indexDir/choice";
//		test.buildIndex(choiceIndexDir, "choice");
//		test.doSearch(choiceIndexDir, "choice", "北京当日的气温比杭州高");
		
		// question level
		String questionIndexDir = "indexDir/question";
		//test.buildIndex(questionIndexDir, "question");
		//test.doSearch(questionIndexDir, "question", "雅典所处的自然带是	亚热带常绿阔叶林带	亚热带常绿硬叶林带	温带落叶阔叶林带	温带混交林带");
		//test.doSearch(questionIndexDir, "question", "影响日资家电组装工厂不断转移的主要因素是	0	市场规模	劳动力成本	原材料成本	技术水平");
		//test.doSearch(questionIndexDir, "question", "影响该地气温特征的主导因素是	纬度位置	海陆位置	地形	植被");
		//test.doSearch(questionIndexDir, "question", "循环农业对建设美丽乡村的主要作用是		提高经济效益	加快城镇发展	提供清洁能源	促进民居集中");
		//test.doSearch(questionIndexDir, "question", "5月23日，当太阳直射墨西哥某城市（1030W）时，北京时间是	0	24日2时52分	24日2时08分	23日3时08分	22日2时52分");
		//test.doSearch(questionIndexDir, "question", "有人称丙处山峰为“飞来峰”，其岩石可能是	0	石灰岩   大理岩	花岗岩   流纹岩	大理岩   石灰岩	安山岩    玄武岩");
		test.doSearch(choiceIndexDir, "choice", "关于图3中各河流水文特征的叙述，正确的是()	③区河流含沙量大，有结冰期");
		
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
				System.out.println("qid:"+doc.getField("qid").stringValue());
				System.out.println(q.toString());
			}
		}
		
	}

}
