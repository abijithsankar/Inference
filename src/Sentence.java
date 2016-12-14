import java.util.ArrayList;
import java.util.*;

class Sentence
{
	//List<AllSubstitution> allSubst;// = new LinkedList<AllSubstitution>();
 	//static List <Predicate> groundPredicateList = new ArrayList<Predicate>();
	//static Map<List<String>,List<Predicate>> predicateMap=new HashMap<List<String>,List<Predicate>>();
	String sentence;
	List<String> predicateNameList=new ArrayList<String>();
	Map<String,Predicate> predicateMap=new TreeMap<String,Predicate>();
	public Sentence(String cnf) {
		this.sentence=cnf;
		setPredicateMap(cnf);
		
	}

	public void setPredicateMap(String sentence)
	{
		String eachPredicate[] = sentence.split("\\|");	
		int i=0;
		for (String pred : eachPredicate) 
		{
			if(pred != null && pred != "")
			{
				Predicate predMap=new Predicate(pred);
				
				if(predicateMap.containsKey(predMap.predicateName)){
					predicateNameList.add(predMap.predicateName+"#Copy"+i);
					predicateMap.put(predMap.predicateName+"#Copy"+i, predMap);
				}
				else{
					predicateNameList.add(predMap.predicateName);
					predicateMap.put(predMap.predicateName,predMap);
				}
				 
			}
			i++;
		}
		
		
	}
	
	
}