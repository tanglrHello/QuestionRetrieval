

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

public class QuestionIndex {

	private IndexWriter indexWriter;
	
	public QuestionIndex(String indexDir) throws IOException{
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		//Analyzer analyzer = new IKAnalyzer();
		
		Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.SIMPLE_MODE);  
		
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		indexWriter = new IndexWriter(dir, iwc);
	}
	
	public void close() throws IOException{
		indexWriter.close();
	}
	
	// process data exported from sql database
	// output is used for choice/question level retrieval (indicated by parameter "method")
	public void processRawData(String inputFilePath, String outputFilePath, String method){
		File infile = new File(inputFilePath);
		
		BufferedReader reader = null;
		FileWriter writer = null;
		
		int qid=0;
		int bgtext = 2;
		int qtext = 8;
		int choiceStart = 9;
		int isMulti = 13;
		int smallChoiceStart = 14;
		int answer = 23;
		int pyear = 27;
		int ptype = 28;
		int pdesc = 29;
		
		try{
			reader = new BufferedReader(new FileReader(infile));
			writer = new FileWriter(outputFilePath);
			
			String tmpString = null;
			int rownum = 0;
			while ((tmpString = reader.readLine()) != null){
				rownum++;
				String fields[] = tmpString.split("\t");
				
				// output for choice level retrieval
				if (method.equals("choice")){
					if (fields[isMulti].equals("1")){
						for (int i=0;i<9;i++){
							writer.write(fields[qid]+"\t");
							writer.write(fields[bgtext]+"\t");
							writer.write(fields[qtext]+fields[smallChoiceStart+i]+"\t");
							writer.write(fields[pyear]+"\t");
							writer.write(fields[ptype]+"\t");
							writer.write(fields[pdesc]+"\r\n");
						}
					}else{
						for (int i=0;i<4;i++){
							writer.write(fields[qid]+"\t");
							writer.write(fields[bgtext]+"\t");
							writer.write(fields[qtext]+fields[choiceStart+i]+"\t");
							writer.write(fields[pyear]+"\t");
							writer.write(fields[ptype]+"\t");
							writer.write(fields[pdesc]+"\r\n");
						}
					}
					
				// output for question level retrieval
				}else if (method.equals("question")){
					
				}	
			}
			System.out.println("process_raw_data() complete.");
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (reader != null){
				try{
					reader.close();
				}catch(IOException e1){
					e1.printStackTrace();
				}
			}
			if (writer != null){
				try{
					writer.close();
				}catch(IOException e2){
					e2.printStackTrace();
				}
			}
		}
	}

	// index data for choice level retrieval
	// return the indexed choice number
	public int indexByChoice(String choiceDataFilePath) throws IOException{
		int choice_num=0;
		
		File datafile = new File(choiceDataFilePath);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(datafile));
		String rowstr = null;
		
		while ((rowstr = reader.readLine())!=null){
			String fields[] = rowstr.split("\t");
			Document doc = getDocument(fields, "choice");
			indexWriter.addDocument(doc);
			choice_num++;
		}
		
		reader.close();
		
		return choice_num;
	}
	
	// index data for question level retrieval
	// return the indexed question number 
	public int indexByQuestion(String questionDataFilePath) {
		return 0;
	}
	
	// transform string data into Document object (for both choice/question level retrieval)
	public Document getDocument(String[] fields, String method){
		Document doc = null;
		if (method.equals("choice")){
			int qid = 0;
			int bgtext = 1;
			int choice = 2;
			int pyear = 3;
			int ptype = 4;
			int pdesc = 5;
			
			doc = new Document();
			doc.add(new StringField("qid",fields[qid],Field.Store.YES));
			doc.add(new TextField("bgtext", fields[bgtext], Field.Store.YES));
			doc.add(new TextField("choiceContent", fields[choice], Field.Store.YES));
			doc.add(new StringField("pyear", fields[pyear], Field.Store.YES));
			doc.add(new StringField("ptype", fields[ptype], Field.Store.YES));
			doc.add(new StringField("pdesc", fields[pdesc], Field.Store.YES));
			
		}else if (method.equals("question")){
			
		}
		
		return doc;
	}
	
	public void clearIndex() throws IOException{
		indexWriter.deleteAll();
	}
	
}
