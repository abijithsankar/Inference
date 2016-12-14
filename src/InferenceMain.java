import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InferenceMain {
	static Map<String,List<String>> groundKB=new HashMap<String,List<String>>();
	static Map<Integer,Sentence> kbMap=new TreeMap<Integer,Sentence>();
	static List<String> queryList=new ArrayList<String>();
	static List<String> cnfClauses=new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader buffReader = new BufferedReader(new FileReader("Z:/MS Books/AI/input.txt"));
		BufferedWriter buffWriter = new BufferedWriter(new FileWriter("Z:/MS Books/AI/output.txt"));
		int numQueries=Integer.parseInt(buffReader.readLine());
		int iterator;
		for(iterator=0;iterator<numQueries;iterator++){
			queryList.add(buffReader.readLine());
		} 
		int kbSize=Integer.parseInt(buffReader.readLine());
		for(iterator=0;iterator<kbSize;iterator++){
			String sentence=buffReader.readLine();
			if(sentence.charAt(0)=='('){
				String cnfSentence=ConvertToCNF.convertToCNF(sentence.substring(1,sentence.length()-1));
				//System.out.println("cnfSentence:"+cnfSentence);
				if(cnfSentence.contains(":")){
						String[] split=cnfSentence.split(":");
						for(String iter:split){
							
							cnfClauses.add(iter.replaceAll("\\s+",""));
							
						}
				}
				else if(cnfSentence.contains("&")){
					String[] split=cnfSentence.split("\\&");
					for(String iter:split){
						iter=ConvertToCNF.removeUnwantedBrackets(iter);
						cnfClauses.add(iter.replaceAll("\\s+",""));
							
					}
				}
				else{
					cnfClauses.add(cnfSentence);
				}
		        
			}			
			else{
				sentence=sentence.replaceAll("\\s+", "");
				cnfClauses.add(sentence);
			}
			
			
			
		}
		System.out.println(cnfClauses);
		InferenceMain.processKB(cnfClauses);
		for(String query:queryList){
			query=query.replaceAll("\\s+", "");
			Map<Integer,Sentence> tempKbMap=new TreeMap<Integer,Sentence>(kbMap);
			//System.out.println(tempKbMap);
			String negatedQuery=ConvertToCNF.negateQuery(query);
			Sentence sent=new Sentence(negatedQuery);	
			tempKbMap.put(tempKbMap.size()+1, sent);
			System.out.println("tempKbMap:"+tempKbMap);
			boolean answer=Resolution.resolve(tempKbMap);
			System.out.println("ANSWERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR:"+answer);
			buffWriter.write(String.valueOf(answer).toUpperCase());
			buffWriter.newLine();
			Resolution.visitedList.clear();
		}
		buffReader.close();
		buffWriter.close();

	}

	private static void processKB(List<String> cnfClauses) {
		// TODO Auto-generated method stub
		int i=1;
		for(String cnf: cnfClauses){
			Sentence sent=new Sentence(cnf);
			kbMap.put(i, sent);
			i++;
		}	
	}

}
