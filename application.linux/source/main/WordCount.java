package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class WordCount {
	public int nWords ;
	public String[] wordArray; 
	public int[] counts; 
	
	public WordCount(int n){
		nWords = n;
		wordArray = new String[nWords];
		counts = new int[nWords]; 
		for (int i=0;i<nWords;i++){
			wordArray[i] = "";
			counts[i] =0;
		}
	}
	
	
	public void countNames(ArrayList<String> a) {
		Set stopWords = readStopWords();
		Map wordMap = new HashMap();
		int count;
		for (int u = 0; u < a.size(); u++) {
			String word = a.get(u);
			if (!stopWords.contains(word)) {
				if (wordMap.containsKey(word)) {
					count = (Integer) wordMap.get(word);
					count ++;
					wordMap.put(word, new Integer(count));
				} else {
					wordMap.put(word, new Integer(1));
				}
			}
			
			
		}
		maxWord(wordMap,nWords);
	}
	
	private static Set readStopWords() {
		java.io.BufferedReader fin;
		Set stopWords = new HashSet();
		stopWords.add("of");
		stopWords.add("and");
		stopWords.add("on");
		stopWords.add("in");
		stopWords.add("at");
		stopWords.add("by");
		stopWords.add("to");
		stopWords.add("from");
		stopWords.add("with");
		stopWords.add("the");
		stopWords.add("a");
		stopWords.add("an");
		stopWords.add("is");
		stopWords.add("1");
		
		return stopWords;
	}

	private void maxWord(Map<String, Integer> wordMap, int num) {
		String[] results = new String[num];
		int index = 0;

		Set set = wordMap.entrySet();
		Iterator im1 = set.iterator();
		while (index < num) {
			Iterator im2 = set.iterator();
			int max =0;
			String maxString ="";
			while (im2.hasNext()) {
				Map.Entry me = (Map.Entry) im2.next();
				if (((Integer) me.getValue()) > max && !isContained((String) me.getKey(), index)){
					max = (Integer) me.getValue();
					maxString = (String) me.getKey();
				}
			}
			wordArray[index] = maxString;
			counts[index] = max;
			index++;
			if (max==0)
				break;
		}
	}
	private boolean isContained(String s, int index) {
		for (int i=0;i<index;i++){
			if (wordArray[i].equals(s))
				return true;
		}
		return false;
	}
}
