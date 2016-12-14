import java.util.*;

import javax.swing.plaf.synth.SynthSpinnerUI;

public class Resolution {
	static List<Integer> visitedList=new ArrayList<Integer>();
	static int flag;
	public static boolean resolve(Map<Integer, Sentence> tempKbMap) {
		List<String> newClauses=new ArrayList<String>();
		flag=0;
		resolveBegins(tempKbMap,tempKbMap.get(tempKbMap.size()));
		if(flag==1){
			return true;
			
		}
		else{
			return false;
		}
		/**/
		
		
	}

	private static void resolveBegins(Map<Integer, Sentence> tempKbMap, Sentence sentence) {
		// TODO Auto-generated method stub
		visitedList.add(tempKbMap.size());
		int i;
		for(i=1;i<=InferenceMain.kbMap.size();i++){
			if(flag==1){
				break; 
			}
			//System.out.println("valueofI:"+i);
			if(!visitedList.contains(i)){
				String resolvedClause=null;
				Sentence clause1=tempKbMap.get(i);
				Sentence clause2=sentence;
				//System.out.println("clause1:"+clause1.sentence);
				//System.out.println("clause2:"+clause2.sentence);
				if(checkContradiction(clause1.sentence,clause2.sentence)){
					flag=1;
					//System.out.println("hi guysss");
					break;
				}
				//System.out.println(visitedList);
				/*if(checkSentenceLength(clause1.sentence,clause2.sentence)){
					resolvedClause=resolution(clause1,clause2);
				}*/
				resolvedClause=resolution(clause1,clause2);
				 
				//System.out.println("resolvedClause:"+resolvedClause);
				if(resolvedClause!=null){
					if(hasDuplicates(resolvedClause)){
						resolvedClause=removeDuplicates(resolvedClause);
					}
					
				}
				if(resolvedClause!=null){
					String s1=tempKbMap.get(InferenceMain.kbMap.size()+1).sentence;
					//System.out.println(resolvedClause+":"+s1);
					if(ConvertToCNF.negateQuery(s1).equals(resolvedClause)){
						//System.out.println("NJBHJVKJVJGVJGVJGVKJGVKGKG");
						flag=1;
						break;
					}
					
				}
			
				if(resolvedClause==null &&InferenceMain.kbMap.get(InferenceMain.kbMap.size()).sentence.equals(clause1.sentence)){
					int keyOfClause2 = 0;
					for(Map.Entry<Integer, Sentence> entry:tempKbMap.entrySet()){
						if(entry.getValue().sentence.equals(clause2.sentence)){
							keyOfClause2=entry.getKey();
							//System.out.println("KEYOFCLAUSE@:"+keyOfClause2);
							break;
						}
					}
					int indexOfCreatorClause=visitedList.indexOf(keyOfClause2)-1;
					if(indexOfCreatorClause<0){
						continue;
					}
					List<Integer> copyOfVisitedList=new ArrayList<Integer>(visitedList);
					for(Integer entry:visitedList){
						if(entry>visitedList.get(indexOfCreatorClause) && entry<=(InferenceMain.kbMap.size())){
							copyOfVisitedList.remove(entry);
						}
					}
					//System.out.println("copy:"+copyOfVisitedList);
					visitedList.clear();
					//System.out.println("before:"+visitedList);
					visitedList=new ArrayList<Integer>(copyOfVisitedList);
					//System.out.println("after:"+visitedList);
					
				}
				if(resolvedClause!=null && flag!=1){
					//System.out.println("hi");
					if(resolvedClause.contains("#Copy")){
						resolvedClause=removeUnwantedPredicateNames(resolvedClause);
					}
					Sentence sent=new Sentence(resolvedClause);
					tempKbMap.put(tempKbMap.size()+1, sent);
					//System.out.println(tempKbMap);
					visitedList.add(i);
					if(InferenceMain.kbMap.get(InferenceMain.kbMap.size()).sentence.equals(clause1.sentence)){
						List<Integer> copyOfVisitedList=new ArrayList<Integer>(visitedList);
						for(Integer entry:visitedList){
							if(entry<(InferenceMain.kbMap.size())){
								copyOfVisitedList.remove(entry);
							}
						}
						//System.out.println("copy:"+copyOfVisitedList);
						visitedList.clear();
						//System.out.println("before:"+visitedList);
						visitedList=new ArrayList<Integer>(copyOfVisitedList);
						//System.out.println("after:"+visitedList);
					}
					resolveBegins(tempKbMap,sent);
				}
			}
			
			
			
		}
		
	}

	private static String removeUnwantedPredicateNames(String resolvedClause) {
		// TODO Auto-generated method stub
		String[] split=resolvedClause.split("\\|");
		StringBuilder clauseBuilder=new StringBuilder();
		for(String iter:split){
			if(iter.contains("#Copy")){
				int indexOfHash=iter.indexOf('#');
				int indexOfParen=iter.indexOf('(');
				iter=iter.substring(0,indexOfHash)+iter.substring(indexOfParen);
				//System.out.println("iter:"+iter);
				clauseBuilder.append(iter+"|");
			}
			else{
				clauseBuilder.append(iter+"|");
			}
			
		}
		clauseBuilder.deleteCharAt(clauseBuilder.lastIndexOf("|"));
		String returnValue;
		if(hasDuplicates(clauseBuilder.toString())){
			returnValue=removeDuplicates(clauseBuilder.toString());
			return returnValue;
		}
		return clauseBuilder.toString();
	}

	private static boolean hasDuplicates(String resolvedClause) {
		// TODO Auto-generated method stub
		String[] resolvedSplit=resolvedClause.split("\\|");
		Set<String> resolverSet=new HashSet<String>();
		for(String iter: resolvedSplit){
			if(resolverSet.add(iter)==false){
				return true;
			}
		}
		return false;
	}

	private static String removeDuplicates(String resolvedClause) {
		// TODO Auto-generated method stub
		String[] resolvedSplit=resolvedClause.split("\\|");
		Set<String> resolverSet=new HashSet<String>();
		for(String iter: resolvedSplit){
			resolverSet.add(iter);
		}
		//System.out.println("resolverSet:"+resolverSet);
		StringBuilder returnValueBuilder=new StringBuilder();
		for(String iter: resolverSet){
			returnValueBuilder.append(iter+"|");
			
		}
		returnValueBuilder.deleteCharAt(returnValueBuilder.lastIndexOf("|"));
		//System.out.println("returnValueBuilder:"+returnValueBuilder);
		return returnValueBuilder.toString();
	}

	private static boolean checkContradiction(String clause1, String clause2) {
		// TODO Auto-generated method stub
		if(ConvertToCNF.negateQuery(clause1).equals(clause2) || clause1.equals(ConvertToCNF.negateQuery(clause2))){
			return true;
		}
		return false;
	}


	private static String resolution(Sentence clause1, Sentence clause2) {
		//List<Sentence>
		StringBuilder resolventBuilder=new StringBuilder();
		String resolvent = null;
		Iterator<String> itr = null;
		if(clause1.sentence.split("\\|").length>1 && clause2.sentence.split("\\|").length==1){
			itr=clause1.predicateNameList.iterator();
			if((resolvent=isDirectSimpleResolutionPossible(clause1,clause2,1))!=null){
				return resolvent;
			}
			resolvent=simpleResolutionBuilder(clause1,clause2,itr);
		}
		else if(clause2.sentence.split("\\|").length>1 && clause1.sentence.split("\\|").length==1){
			itr=clause2.predicateNameList.iterator();
			if((resolvent=isDirectSimpleResolutionPossible(clause1,clause2,2))!=null){
				return resolvent;
			}
			Sentence temp=clause1;
			clause1=clause2;
			clause2=temp;
			resolvent=simpleResolutionBuilder(clause1,clause2,itr);
		}
		else if(clause1.sentence.split("\\|").length==1 && clause2.sentence.split("\\|").length==1){
			itr=clause1.predicateNameList.iterator();
			resolvent=simpleResolutionBuilder(clause1,clause2,itr);
		}
		else if(clause1.sentence.split("\\|").length>1 && clause2.sentence.split("\\|").length>1){
			resolvent=complexResolutionBuilder(clause1,clause2);
		}
		
		return resolvent;
	}

	private static String isDirectSimpleResolutionPossible(Sentence clause1, Sentence clause2, int caseValue) {
		// TODO Auto-generated method stub
		//System.out.println("inside this");
		String[] splitter = null;
		String negatedResolver = null;
		if(caseValue==1)
		{
			splitter=clause1.sentence.split("\\|");
			negatedResolver=ConvertToCNF.negateQuery(clause2.sentence);
		}
		else if(caseValue==2){
			splitter=clause2.sentence.split("\\|");
			negatedResolver=ConvertToCNF.negateQuery(clause1.sentence);
		}
		StringBuilder simpleResolutionBuilder=new StringBuilder();
		if(Arrays.asList(splitter).contains(negatedResolver)){
			for(int i=0;i<splitter.length;i++){
				if(!splitter[i].equals(negatedResolver)){
					simpleResolutionBuilder.append(splitter[i]+"|");
				}
			}
			simpleResolutionBuilder.deleteCharAt(simpleResolutionBuilder.lastIndexOf("|"));
			return simpleResolutionBuilder.toString();
		}
		
		
		return null;
	}

	private static String complexResolutionBuilder(Sentence clause1, Sentence clause2) {
		// TODO Auto-generated method stub
		StringBuilder resolventBuilder=new StringBuilder();
		Map<String,String> substMap=new TreeMap<String,String>();
		List<String> remainingSentenceList=new ArrayList<String>();
		List<String> resolventList=new ArrayList<String>();
		//System.out.println("inside complex resolver");
		//System.out.println("clause1:"+clause1.sentence);
		//System.out.println("clause2:"+clause2.sentence);
		Iterator<String> newSentenceIterator=clause2.predicateNameList.iterator();
		Iterator<String> copyOfNewSentenceIterator=clause2.predicateNameList.iterator();
		String matchedNewSentencePredicate = null;
		while(newSentenceIterator.hasNext()){
			String newSentencePredicateName=newSentenceIterator.next();
			//System.out.println("newSentencePredicateName:"+newSentencePredicateName);
			Iterator<String> kbSentenceIterator=clause1.predicateNameList.iterator();
			while(kbSentenceIterator.hasNext()){
				String kbSentencePredicateName=kbSentenceIterator.next();
				//System.out.println("kbSentencePredicateName:"+kbSentencePredicateName);
				String negatedNewSentencePredicateName=ConvertToCNF.negateQuery(newSentencePredicateName);
				//System.out.println("negatedNewSentencePredicateName:"+negatedNewSentencePredicateName);
				if(kbSentencePredicateName.equals(negatedNewSentencePredicateName)){
					matchedNewSentencePredicate=newSentencePredicateName;
					substMap=unify(clause1.predicateMap.get(kbSentencePredicateName).predicateMapping.get(kbSentencePredicateName),
							clause2.predicateMap.get(newSentencePredicateName).predicateMapping.get(newSentencePredicateName));
					//System.out.println("SUBSTITUTION MAP:"+substMap);
					
				}
				/*else{
					resolventList.add(kbSentencePredicateName);
				}*/
				
			}
			
		}
		for(String nonMatcher:clause1.predicateNameList){
			if(matchedNewSentencePredicate!=null && !nonMatcher.equals(ConvertToCNF.negateQuery(matchedNewSentencePredicate))){
				resolventList.add(nonMatcher);
			}
		}
		//System.out.println("resolventList:"+resolventList);
		while(copyOfNewSentenceIterator.hasNext()){
			String predName=copyOfNewSentenceIterator.next();
			if(!predName.equals(matchedNewSentencePredicate)){
				remainingSentenceList.add(predName);
			}
		}
		//System.out.println("remainingSentenceList:"+remainingSentenceList);
		if(substMap.isEmpty()){
			return null;
		}
		for(String iterator:resolventList){
			resolventBuilder.append(iterator+"(");
			List<String> resolvingArgList=clause1.predicateMap.get(iterator).args;
			for(String newIter:resolvingArgList){
				if(substMap.containsKey(newIter)){
					resolventBuilder.append(substMap.get(newIter)+",");
				}
				else{
					resolventBuilder.append(newIter+",");
				}
				
			}
			resolventBuilder.deleteCharAt(resolventBuilder.lastIndexOf(","));
			resolventBuilder.append(")|");
		}
		if(!resolventList.equals(remainingSentenceList)){
			for(String iterator:remainingSentenceList){
				resolventBuilder.append(iterator+"(");
				List<String> resolvingArgList=clause2.predicateMap.get(iterator).args;
				for(String newIter:resolvingArgList){
					resolventBuilder.append(newIter+",");
				}
				resolventBuilder.deleteCharAt(resolventBuilder.lastIndexOf(","));
				resolventBuilder.append(")|");
			}
		}
		
		//System.out.println("resolventBuilder:"+resolventBuilder);
		resolventBuilder.deleteCharAt(resolventBuilder.lastIndexOf("|"));
		return resolventBuilder.toString();
	}

	private static String simpleResolutionBuilder(Sentence clause1, Sentence clause2, Iterator<String> itr) {
		// TODO Auto-generated method stub
		StringBuilder resolventBuilder=new StringBuilder();
		String resolvent = null; 
		List<String> argList;
		List<String> resolventList =new ArrayList<String>();
		Map<String,String> substMap=new TreeMap<String,String>();
		while(itr.hasNext()){
			int copyFlag=0;
			String predicateName=itr.next();
			//System.out.println("predicateNameBefore:"+predicateName);
			if(predicateName.contains("Copy")){
				predicateName=predicateName.split("#")[0];
				copyFlag=1;
			}
			//System.out.println("predicateNameAfter:"+predicateName);
			String negatedPredicateName=ConvertToCNF.negateQuery(predicateName);
			if(clause2.predicateNameList.contains(negatedPredicateName)){
				//System.out.println(clause1.sentence+":"+clause2.sentence);
				
				substMap=unify(clause1.predicateMap.get(predicateName).predicateMapping.get(predicateName),
						clause2.predicateMap.get(negatedPredicateName).predicateMapping.get(negatedPredicateName));
				//System.out.println("substMap:"+substMap);
				if(clause1.sentence.split("\\|").length==1 && clause2.sentence.split("\\|").length==1 && !substMap.isEmpty()){
					flag=1;
					////System.out.println("BINDASSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
					return null;
				}
				
				
			}
			else{
				resolventList.add(predicateName);
			}
			if(copyFlag==1){
				resolventList.add(predicateName);
			}
		}
		////System.out.println(resolventList);
		////System.out.println(substMap);
		if(substMap.isEmpty()){
			return null;
		}
		//System.out.println("resolventList:"+resolventList);
		for(String iterator:resolventList){
			resolventBuilder.append(iterator+"(");
			List<String> resolvingArgList=clause1.predicateMap.get(iterator).args;
			for(String newIter:resolvingArgList){
				if(substMap.containsKey(newIter)){
					resolventBuilder.append(substMap.get(newIter)+",");
				}
				else{
					resolventBuilder.append(newIter+",");
				}
				
			}
			resolventBuilder.deleteCharAt(resolventBuilder.lastIndexOf(","));
			resolventBuilder.append(")|");
		}
		//System.out.println(resolventBuilder);
		resolventBuilder.deleteCharAt(resolventBuilder.lastIndexOf("|"));
		return resolventBuilder.toString();
	}

	private static Map<String, String> unify(List<String> clause1Args, List<String> clause2Args) {
		// TODO Auto-generated method stub
		Map<String,String> substMap=new TreeMap<String,String>();
		//System.out.println(clause1Args);
		//System.out.println(clause2Args);
		String[] clause1ArgsArray=new String[clause1Args.size()];
		String[] newClause1ArgsArray=new String[clause1Args.size()];
		clause1ArgsArray=clause1Args.toArray(clause1ArgsArray);
		String[] clause2ArgsArray=new String[clause2Args.size()];
		String[] newClause2ArgsArray=new String[clause2Args.size()];
		clause2ArgsArray=clause2Args.toArray(clause2ArgsArray);
		for(int i=0;i<clause1ArgsArray.length;i++){
			char clause1Var=clause1ArgsArray[i].charAt(0);
			//System.out.println("clause1Var:"+clause1Var);
			char clause2Var=clause2ArgsArray[i].charAt(0);
			//System.out.println("clause2Var:"+clause2Var);
			if(Character.isLowerCase(clause1Var) && Character.isUpperCase(clause2Var)){
				//System.out.println("hi");
				clause1ArgsArray[i]=clause2ArgsArray[i];
				
			}
			else if(Character.isLowerCase(clause2Var) && Character.isUpperCase(clause1Var)){
				//System.out.println("hi abi");
				clause2ArgsArray[i]=clause1ArgsArray[i];
			}
			else if(Character.isLowerCase(clause1Var) && Character.isLowerCase(clause2Var) && (clause1Var!=clause2Var)){
				clause1ArgsArray[i]=clause2ArgsArray[i];
			}
		}
		List<String> newClause1Args=new ArrayList<String>(Arrays.asList(clause1ArgsArray));
		List<String> newClause2Args=new ArrayList<String>(Arrays.asList(clause2ArgsArray));
		//System.out.println("newClause1Args:"+newClause1Args);
		//System.out.println("newClause2Args:"+newClause2Args);
		for(int j=0;j<clause1Args.size();j++){
			if(newClause1Args.equals(newClause2Args)){
				if(clause1Args.equals(clause2Args)){
					substMap.put(clause1Args.get(j), clause1Args.get(j));
				}
				if(!clause1Args.equals(newClause1Args)){
					substMap.put(clause1Args.get(j),newClause1Args.get(j));
				}
				if(!clause2Args.equals(newClause2Args)){
					substMap.put(clause2Args.get(j),newClause2Args.get(j));
				}
			}
			
			
			
		}
		
		//returnList.add(newClause2Args);
		return substMap;
		
		
		
	}
	
}
