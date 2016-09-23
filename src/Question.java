import java.util.ArrayList;


public class Question {
	String qtext;
	boolean isMulti;
	ArrayList<String> choices;
	ArrayList<String> smallChoices;
	
	public Question(String qtext, Boolean isMulti, ArrayList<String> choices, ArrayList<String> smallChoices){
		this.qtext = qtext;
		this.isMulti = isMulti;
		this.choices = choices;
		this.smallChoices = smallChoices;
	}
	
	public Question(String questionStr) throws ExceptionInInitializerError{
		String contents[] = questionStr.split("\t");
		if (contents.length<5){
			throw new ExceptionInInitializerError("no enough info for question");
		}
		
		qtext = contents[0];
		choices = new ArrayList<String>();
		smallChoices = new ArrayList<String>();
		
		if (contents.length==5){
			for (int i=1;i<5;i++){
				choices.add(contents[i]);
			}
		}else{
			for (int i=1;i<contents.length;i++){
				smallChoices.add(contents[i]);
			}
		}
		
	}
	
	public String encodeForDocument(){
		String questionStr = this.qtext;
		if (!isMulti){
			for (String choice : choices){
				questionStr += "\t"+choice;
			}
		}else{
			for (String choice : smallChoices){
				if (!choice.equals(""))
					questionStr += "\t"+choice.substring(1);
			}
		}
		
		return questionStr;
	}
	
	public String toString(){
		String questionStr = "qtext:"+this.qtext+"()";
		for (int i=0;i<choices.size();i++){
			questionStr+="\n-"+choices.get(i);
		}
		for (int j=0;j<smallChoices.size();j++){
			questionStr+="\n-"+smallChoices.get(j);
		}
		return questionStr+"\n";
	}
	
}
