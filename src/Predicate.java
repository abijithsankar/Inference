import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.TreeMap;

class Predicate
{
	String predicateName;
	List <String> args;
	Map<String,List<String>> predicateMapping=new TreeMap<String,List<String>>();
	
	public Predicate()
	{
		predicateName = "";
	//	args = new ArrayList<String>();
	}
	
	public Predicate(String pred) 
	{
		this();
		setPredicate(pred);		
	}
	public void setPredicate(String predicate)
	{
		String singlePredicate  = predicate.trim();
		int indexOfStartBracket = singlePredicate.indexOf('(');
		int indexOfCloseBracket = singlePredicate.indexOf(')');

		this.predicateName = singlePredicate.substring(0, indexOfStartBracket);
		String arguments[] = singlePredicate.substring(indexOfStartBracket+1,indexOfCloseBracket).split(",");
		//this.args = (ArrayList<String>)Arrays.asList(arguments);
	    this.args = new ArrayList<String>(Arrays.asList(arguments));
	    this.predicateMapping.put(predicateName, args);
	}
}