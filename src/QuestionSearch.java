

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;



public class QuestionSearch {
	private Directory dir;
	private IndexReader reader;
	private IndexSearcher searcher;
	
	public QuestionSearch(String indexDir) throws IOException{
		dir = FSDirectory.open(Paths.get(indexDir));
		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}
	
	public void close() throws IOException{
		reader.close();
		dir.close();
	}
	
	public ArrayList<Document> search(String method, String querystr) throws IOException{
		ArrayList<Document> res = new ArrayList<Document>();
		Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.SIMPLE_MODE);
		QueryBuilder queryBuilder = new QueryBuilder(analyzer);
	
		// choice level search
		if (method.equals("choice")){
			Query query = queryBuilder.createBooleanQuery("choiceContent", querystr); 
			TopDocs hits = searcher.search(query, 10);
			
			for (ScoreDoc scoreDoc : hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				res.add(doc);
			}
		}else{
			Query query = queryBuilder.createBooleanQuery("questionContent", querystr); 
			TopDocs hits = searcher.search(query, 10);
			
			for (ScoreDoc scoreDoc : hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				res.add(doc);
			}
		}
		
		return res;
	}
	
}
