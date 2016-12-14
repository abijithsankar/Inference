import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertToCNF {
	public static String convertToCNF(String sentence){
		sentence=sentence.replaceAll("\\s+", "");
		//System.out.println("sentence:"+sentence);
		if(sentence.contains("=>")){
			sentence=removeImplication(sentence);
			//System.out.println("sentence after implication removal:"+sentence);
			return sentence;
		}
		if(sentence.matches("\\((.*)&(.*)\\)") && isSimpleAndStatement(sentence)){
			//System.out.println("in and :"+sentence);
			String[] predAndCons=sentence.split("\\&");
			StringBuilder complexAndBuilder=new StringBuilder();
			if(predAndCons.length>2){
				//System.out.println("inside complexAndBuilder");
				for(String iter:predAndCons){
					complexAndBuilder.append(convertToCNF(iter)+"&");
				}
				complexAndBuilder.deleteCharAt(complexAndBuilder.lastIndexOf("&"));
				//System.out.println("complexAndBuilder:"+complexAndBuilder);
				return complexAndBuilder.toString();
			}
			else{
				String lhsOfAnd=predAndCons[0];
				String rhsOfAnd=predAndCons[1];
				//System.out.println("lhsOfAnd:"+lhsOfAnd);
				//System.out.println("rhsOfAnd:"+rhsOfAnd);
				return (convertToCNF(lhsOfAnd.substring(1, lhsOfAnd.length()))+"&"+convertToCNF(rhsOfAnd.substring(0, rhsOfAnd.length()-1)));
			}
			
			
		}
		if(sentence.matches("\\(~\\((.*)")){
			//System.out.println("propagate not via demorgans law on:"+sentence);
			/*if(sentence.substring(1,sentence.length()-1).matches("~\\((.*)&(.*)\\)")){
				sentence=sentence.substring(1,sentence.length()-1);
				//System.out.println("demorgan on and");
				sentence=sentence.substring(2,sentence.length()-1);
				String[] predAndCons=sentence.split("\\&");
				String lhsOfAnd=predAndCons[0];
				String rhsOfAnd=predAndCons[1];
				lhsOfAnd=negatePredicate(lhsOfAnd);
				rhsOfAnd=negatePredicate(rhsOfAnd);
				sentence=convertToCNF(lhsOfAnd+"|"+rhsOfAnd);
			}
			else if(sentence.substring(1,sentence.length()-1).matches("~\\((.*)\\|(.*)\\)")){
				sentence=sentence.substring(1,sentence.length()-1);
				//System.out.println("demorgan on or");
				sentence=sentence.substring(2,sentence.length()-1);
				String[] predAndCons=sentence.split("\\|");
				String lhsOfOr=predAndCons[0];
				String rhsOfOr=predAndCons[1];
				lhsOfOr=negatePredicate(lhsOfOr);
				rhsOfOr=negatePredicate(rhsOfOr);
				sentence=convertToCNF(lhsOfOr+"&"+rhsOfOr);
			}*/
			StringBuilder demorganBuilderOnAnd=new StringBuilder();
			StringBuilder demorganBuilderOnOr=new StringBuilder();
			int orAndFlag=0;
			if(sentence.contains("&") && !sentence.contains("|")){
				//System.out.println("demorgan on and");
				String[] andSplitter=sentence.substring(2,sentence.length()-1).split("\\&");
				for(String iter:andSplitter){
					iter=removeUnwantedBrackets(iter);
					//System.out.println("iter:"+iter);
					if(iter.contains("~")){
						iter=iter.replaceAll("~", "");
					}
					else{
						iter="~"+iter;
					}
					//System.out.println("iter after:"+iter);
					demorganBuilderOnAnd.append(iter+"|");
				}
				demorganBuilderOnAnd.deleteCharAt(demorganBuilderOnAnd.lastIndexOf("|"));
				orAndFlag=1;
			}
			else if(!sentence.contains("&") && sentence.contains("|")){
				//System.out.println("demorgan on or");
				String[] andSplitter=sentence.substring(2,sentence.length()-1).split("\\|");
				for(String iter:andSplitter){
					iter=removeUnwantedBrackets(iter);
					//System.out.println("iter:"+iter);
				if(iter.contains("~")){
						iter=iter.replaceAll("~", "");
					}
					else{
						iter="~"+iter;
					}
					//System.out.println("iter after:"+iter);
					demorganBuilderOnOr.append(iter+"&");
				}
				demorganBuilderOnOr.deleteCharAt(demorganBuilderOnOr.lastIndexOf("&"));
				orAndFlag=2;
			}
			if(orAndFlag==1){
				sentence=demorganBuilderOnAnd.toString();
				//System.out.println("sentenceAfterDemorganAnd:"+sentence);
			}
			else if(orAndFlag==2){
				sentence=demorganBuilderOnOr.toString();
				//System.out.println("sentenceAfterDemorganOr:"+sentence);
			}
				
			
		}
		if(sentence.matches("(.*)\\|(.*)")){
			
			//System.out.println("inside p or q");
			if(isInCNF(sentence)){
				sentence=removeUnwantedBrackets(sentence);
				return sentence;
			}
			Pattern p=Pattern.compile("\\((.*)\\)\\|");
			Matcher m=p.matcher(sentence);
			String lhsOfOr = null;
			if(m.find()){
				lhsOfOr=sentence.substring(0,m.end()-1);
				//System.out.println("inside | check: lhsOfOr:"+lhsOfOr);
			}
			String rhsOfOr=sentence.substring(m.end());
			//System.out.println("inside | check: rhsOfOr:"+rhsOfOr);
			sentence=convertToCNF(lhsOfOr)+"|"+convertToCNF(rhsOfOr);
			
			
			
		}
		if(sentence.matches("~\\((.*)")){
			//System.out.println("hi abiiiiiiii");
			if(sentence.matches("~\\((.*)&(.*)\\)")){
				sentence=sentence.substring(2,sentence.length()-1);
				String[] predAndCons=sentence.split("\\&");
				String lhsOfAnd=predAndCons[0];
				String rhsOfAnd=predAndCons[1];
				lhsOfAnd=negatePredicate(lhsOfAnd);
				rhsOfAnd=negatePredicate(rhsOfAnd);
				sentence=convertToCNF(lhsOfAnd+"|"+rhsOfAnd);
			}
			else if(sentence.matches("~\\((.*)\\|(.*)\\)")){
				sentence=sentence.substring(2,sentence.length()-1);
				String[] predAndCons=sentence.split("\\|");
				String lhsOfOr=predAndCons[0];
				String rhsOfOr=predAndCons[1];
				lhsOfOr=negatePredicate(lhsOfOr);
				rhsOfOr=negatePredicate(rhsOfOr);
				sentence=convertToCNF(lhsOfOr+"&"+rhsOfOr);
			}
				
			
		}
		String distributedTerm;
		if((distributedTerm=isDistributive(sentence))!=null){
			//System.out.println("stuff:"+distributedTerm);
			sentence=distributedTerm;
		}
		
		return sentence;
	}

	private static String isDistributive(String sentence) {
		// TODO Auto-generated method stub
		//System.out.println("distributive check:"+sentence);
		if(sentence.contains("|") && sentence.contains("&")){
			StringBuilder distributiveBuilder=new StringBuilder();
			Pattern q=Pattern.compile("(.*)&(.*)\\|(.*)");
			Pattern p=Pattern.compile("(.*)\\|(.*)&(.*)");
			Matcher r=q.matcher(sentence);
			Matcher m=p.matcher(sentence);
			String lhs=null;
			String rhs=null;
			if(r.find()){
				String[] tempSplit=sentence.split("\\|");
				String[] lhsSplit=tempSplit[0].split("&");
				if(lhsSplit.length>2){
					for(String iter:lhsSplit){
						distributiveBuilder.append(iter+"|"+tempSplit[1]+":");
					}
					distributiveBuilder.deleteCharAt(distributiveBuilder.lastIndexOf(":"));
					//System.out.println(distributiveBuilder);
					return removeUnwantedBrackets(distributiveBuilder.toString());
				}
				else{
					distributiveBuilder.append(lhsSplit[0]+"|"+tempSplit[1]+":"+lhsSplit[1]+"|"+tempSplit[1]);
					//System.out.println(distributiveBuilder);
					return distributiveBuilder.toString();
					
				}
				
				
			}
			else if(m.find()){
				String[] tempSplit=sentence.split("\\|");
				String[] rhsSplit=tempSplit[1].split("&");
				if(rhsSplit.length>2){
					for(String iter:rhsSplit){
						distributiveBuilder.append(tempSplit[0]+"|"+iter+":");
					}
					distributiveBuilder.deleteCharAt(distributiveBuilder.lastIndexOf(":"));
					//System.out.println(distributiveBuilder);
					return removeUnwantedBrackets(distributiveBuilder.toString());
					
				}
				else{
					distributiveBuilder.append(tempSplit[0]+"|"+rhsSplit[0]+":"+tempSplit[0]+"|"+rhsSplit[1]);
					//System.out.println(distributiveBuilder);
					return distributiveBuilder.toString();
				}
				
				
			}
		}
		
		
		return null;
	}

	static String removeUnwantedBrackets(String sentence) {
		char[] sentenceCharArray=sentence.toCharArray();
		for(int i=0;i<sentenceCharArray.length;i++){
			if(sentenceCharArray[i]=='(' || sentenceCharArray[i]==')'){
				if(i-1>=0){
					if((sentenceCharArray[i-1]>=65 && sentenceCharArray[i-1]<=90) || (sentenceCharArray[i-1]>=97 && sentenceCharArray[i-1]<=122))
					{
						
					}
					else{
						sentenceCharArray[i]=' ';
					}
				}
				else{
					sentenceCharArray[i]=' ';
				}
					
			}
			
		}
		String revambedSentence=String.valueOf(sentenceCharArray);
		revambedSentence=revambedSentence.replaceAll("\\s+", "");
		//System.out.println(revambedSentence);
		return revambedSentence;
	}

	private static boolean isInCNF(String sentence) {
		// TODO Auto-generated method stub
		if(sentence.contains("|") && !sentence.contains("&") && !sentence.contains("~("))
		{
			return true;
		}
		return false;
	}

	private static boolean isSimpleAndStatement(String sentence) {
		// TODO Auto-generated method stub
		if(sentence.contains("&") && !sentence.contains("|") && !sentence.contains("~(")){
			return true;
		}
		return false;
	}

	private static String removeImplication(String sentence) {
		// TODO Auto-generated method stub
		String[] predAndCons=sentence.trim().split("=>");
		StringBuilder multipleImplicationSentence=new StringBuilder();
		if(predAndCons.length>2){
			String[] newPredAndCons=removeUnwantedBrackets(sentence).split("=>");
			String preds,cons;
			
			for(int i=0;i<newPredAndCons.length-1;i++){
				if(newPredAndCons[i].matches("\\((.*)\\)")){
					if(simpleNegatedStatement(newPredAndCons[i])){
						preds=negatePredicate(newPredAndCons[i].substring(1, newPredAndCons[i].length()-1));
						//System.out.println("simpleNegatedPred:"+preds);
					}
					else{
						preds="("+negatePredicate(newPredAndCons[i])+")";
					}			
				}
				else{
					preds=negatePredicate(newPredAndCons[i]);
				}
				multipleImplicationSentence.append(convertToCNF(preds)+"|");
				
			}
			if(newPredAndCons[newPredAndCons.length-1].matches("\\(~(.*)\\)") && simpleNegatedStatement(newPredAndCons[newPredAndCons.length-1])){
				cons=newPredAndCons[newPredAndCons.length-1].substring(1, newPredAndCons[newPredAndCons.length-1].length()-1);
			}
			else{
				cons=convertToCNF(newPredAndCons[newPredAndCons.length-1]);
			}
			multipleImplicationSentence.append(convertToCNF(cons));
			return convertToCNF(multipleImplicationSentence.toString());
			
			
		}
		String pred = null;
		String cons = null;
		if(predAndCons[0].matches("\\((.*)\\)")){
			if(simpleNegatedStatement(predAndCons[0])){
				pred=negatePredicate(predAndCons[0].substring(1, predAndCons[0].length()-1));
				//System.out.println("simpleNegatedPred:"+pred);
			}
			else{
				pred="("+negatePredicate(predAndCons[0])+")";
			}			
		}
		else{
			pred=negatePredicate(predAndCons[0]);
		}
		if(predAndCons[1].matches("\\(~(.*)\\)") && simpleNegatedStatement(predAndCons[1])){
			cons=predAndCons[1].substring(1, predAndCons[1].length()-1);
		}
		else{
			cons=convertToCNF(predAndCons[1]);
		}
		String returnValue=convertToCNF(pred)+"|"+convertToCNF(cons);		
		return convertToCNF(returnValue);
		
	}

	private static boolean simpleNegatedStatement(String statement) {
		// TODO Auto-generated method stub
		if(statement.contains("~") && !statement.contains("&") && !statement.contains("|")){
			return true;
		}
		return false;
	}

	private static String negatePredicate(String pred) {
		// TODO Auto-generated method stub
		//System.out.println("inside negator:"+pred);
		String negatedPred;
		if(pred.charAt(0)=='~'){
			negatedPred=pred.substring(1,pred.length());
		}
		else{
			negatedPred="~"+pred;
			
		}
		//System.out.println("negatedPredicate:"+negatedPred);
		if(negatedPred.matches("~\\(~(.*)\\)")){
			negatedPred=negatedPred.substring(3,negatedPred.length()-1);
		}
		return negatedPred;
		
	}
	public static String negateQuery(String query) {
		// TODO Auto-generated method stub
		String negatedQuery;
		if(query.charAt(0)=='~'){
			negatedQuery=query.substring(1,query.length());
		}
		else{
			negatedQuery="~"+query;
			
		}
		return negatedQuery;
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a="((Owns(x,y) & Rabbit(y)) => ((Rabbit(w) & Chase(z,w)) => Hates(x,z)))";
		String sentence=convertToCNF(a.substring(1, a.length()-1));
		sentence=removeUnwantedBrackets(sentence);
		//System.out.println("FinalCNFSentence:"+sentence);
		/*(((~A(x))&B(x))|C(y))
		 * ((((American(x) & Weapon(y)) & Sells(x,y,z)) & Hostile(z)) => Criminal(x))
		 * (Owns(Nono,M1) & Missile(M1))
		 * (~H(y))
		 * (((~AnimalLover(x)) | (~Animal(y))) | (~Kills(x,y)))
		 * (D(x,y) => (~H(y)))
		 * ( P(x,y) => ( A(x,y) & C(z) ) )
		 * ((~Mother(x,y)) | Parent(x,y))
		 * ((A(x)|B(x))=>C(x))
		 * (~( A(x,y) & B (x,y)))
		 * (A(x) => H(x))
		 * ((B(x,y) & C(x,y)) => A(x))*/

	}

}
