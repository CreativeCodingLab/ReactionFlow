package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;

import processing.core.PApplet;
import processing.core.PImage;

public class ReactionView{
	public static int bRect = -1000;
	public static ArrayList<Integer> sRectListByText = new ArrayList<Integer>();
	public static ArrayList<Integer> bRectListL = new ArrayList<Integer>();
	public static ArrayList<Integer> bRectListR = new ArrayList<Integer>();
	public PApplet parent;
	public float x = 0;
	public static float yBegin = 25;
	public static float yBeginList = 70;
	public int w1 = 100;
	public int w = 600;
	public int h = 28;
	public static float maxSize = 0;
	public Integrator[] iX, iY, iH;
	public float maxH = 20;
	float hProtein = 0;
	
	public static Set<BiochemicalReaction> reactionSet; 
	public static Map<BiochemicalReaction, Integer> rectHash =  new HashMap<BiochemicalReaction, Integer>();
	public static ArrayList<BiochemicalReaction> rectList =  new ArrayList<BiochemicalReaction>();
	public static ArrayList<String>[] rectWordList ;
	public static ArrayList<String>[] comWordList ;
	float itemH2 = 0; // height of items in the reaction
	
	public String[] proteins = null;
	public static  Map<String,Integer> mapProteinRDFId_index;
	public static  Map<String,String> mapProteinRDFId;
	public static  Map<String,String> mapSmallMoleculeRDFId;
	
	public static ArrayList<Complex> complexList; 
	public static  Map<String,Integer> mapComplexRDFId_index;
	public static ArrayList<String>[] proteinsInComplex; 
	
	
	
	public ArrayList<Integer> bProteinLeft = new ArrayList<Integer>();
	public ArrayList<Integer> bProteinRight = new ArrayList<Integer>();
	public Integrator[] iP;
	
	public static CheckBox check5;
	public static CheckBox check11;
	public static CheckBox check12;
	public static CheckBox check13;
	public static CheckBox check14;
	public static CheckBox check15;
	public static TextBox textbox1; 
	
	public float xL = x;
	public float xL2 = xL+200;
	public float xRect = x+400;
	public float xR = x+800;
	public float xR2 = xR-200;
	
	public static Color smallMoleculeColor = new Color(180,150,150);
	public static Color unidentifiedElementColor = new Color(130,70,130);
	public static Color formComplexColor = new Color(0,150,100);
	public static Color complexRectionColor = new Color(0,0,180);
	public static Color proteinRectionColor = new Color(180,0,0);
	
	public static WordCloud wordCloud1;
	public static WordCloud wordCloud2;
	public static int numTop =20;
	public ArrayList<String> unidentifiedList = new ArrayList<String>();
	
	public ArrayList<Integer> complexIdList = new ArrayList<Integer>();
	public ArrayList<Integer> complexListLeft = new ArrayList<Integer>();
	public ArrayList<Integer> complexListRight = new ArrayList<Integer>();
	public ArrayList<Integer> processedComplexLeft = new ArrayList<Integer>();
	public ArrayList<Integer> processedComplexRight = new ArrayList<Integer>();
	// Unidentified Elements
	public float yUFO = 0;
	
	public Integrator[] yComplexes; 
	public float[] rComplexes; 
	
	public int bProteinL =-1;
	public int bProteinR =-1;
	public int bComplexL =-1;
	public int bComplexR =-1;
	
	// Reaction simulation
	public static Integrator[][] iS;
	public static Integrator[] iS1;
	public static Integrator[] iS2;
	public static Integrator[] iS3;
	public static Integrator[] iS4;
	
	public int minSatSimulation =5;
	public int level1SatSimulation =32;
	public int stepSatSimulation =7;
	
	public static ArrayList<Integer> simulationRectList = new ArrayList<Integer>();
	public static ArrayList<Integer> simulationRectListLevel = new ArrayList<Integer>();
	public static ArrayList<Integer> simulationRectListAll = new ArrayList<Integer>();
	public static ArrayList<Integer> simulationRectListLevelAll = new ArrayList<Integer>();
	
	public static ArrayList<String> interElements = new ArrayList<String>();
	public static ArrayList<Integer> interElementsLevel = new ArrayList<Integer>();

	
	ArrayList<Integer> brushingProteinForCommonDownstream = new ArrayList<Integer>();
	ArrayList<Integer> brushingComplexForCommonDownstream = new ArrayList<Integer>();
	ArrayList<Integer> brushingReactionsForCommonDownstream = new ArrayList<Integer>();
	ArrayList<Integer> selectedProteinForCommonDownstream = new ArrayList<Integer>();
	ArrayList<Integer> selectedComplexForCommonDownstream = new ArrayList<Integer>();
	ArrayList<Integer> selectedReactionsForCommonDownstream = new ArrayList<Integer>();
	
	ArrayList<Integer> loopReactionList = new ArrayList<Integer>();
	ArrayList<Integer> deleteReactionList = new ArrayList<Integer>();
	Integrator iDelete =  new Integrator(0,0.1f,0.6f);
	
	
	public static boolean isAllowedDrawing =  true;
	public static PopupCausality popupCausality;
	public static PopupReactionOrder popupReactionOrder;
	
	//public static ButtonSimulation buttonPlay;
	public static ButtonSimulation buttonStop;
	public static ButtonSimulation buttonPause;
	public static ButtonSimulation buttonReset;
	public static ButtonSimulation buttonBack;
	public static ButtonSimulation buttonForward;
	public static SliderSimulation slider;
	public static SliderSpeed slider2;
	
	public static Integrator percentPositionComplex = new Integrator(0.1f,0.2f,0.5f);
	
	public ReactionView(PApplet parent_){
		parent = parent_;
		check5 = new CheckBox(parent, "Display names");
		check11 = new CheckBox(parent, "Fade links of Small molecules");
		check12 = new CheckBox(parent, "Fade links of Unidentified elements");
		check13 = new CheckBox(parent, "Fade links of Complex formation");
		check14 = new CheckBox(parent, "Fade links of Complex reaction");
		check15 = new CheckBox(parent, "Fade links of Protein reaction");
		textbox1 = new TextBox(parent, "Search");
		wordCloud1 = new WordCloud(parent, 0,200,190,430, Color.BLACK, "Popular terms in reactions:");
		wordCloud2 = new WordCloud(parent, 0,200,480,720, complexRectionColor, "Popular terms in complexes:");
		
		popupCausality = new PopupCausality(parent);
		popupReactionOrder =  new PopupReactionOrder(parent);
		
		//PImage im1 =  parent.loadImage("img/buttonPlay.png");
		//buttonPlay = new ButtonSimulation(parent, im1);
		PImage im2 =  parent.loadImage("img/buttonStop.png");
		buttonStop = new ButtonSimulation(parent, im2);
		PImage im3 =  parent.loadImage("img/buttonPause.png");
		buttonPause = new ButtonSimulation(parent, im3);
		PImage im4 =  parent.loadImage("img/buttonReset.png");
		buttonReset = new ButtonSimulation(parent, im4);
		PImage im5 =  parent.loadImage("img/buttonBack.png");
		buttonBack = new ButtonSimulation(parent, im5);
		PImage im6 =  parent.loadImage("img/buttonForward.png");
		buttonForward = new ButtonSimulation(parent, im6);
		 
		slider = new SliderSimulation(parent);
		slider2 = new SliderSpeed(parent);
	}
	
	@SuppressWarnings("unchecked")
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
		for (BiochemicalReaction current : reactionSet){
			Object[] s = current.getLeft().toArray();
			// Compute size of reaction
			int size = 0;
			for (int i3=0;i3<s.length;i3++){
				  String name = main.ReactionFlow_1_1.getProteinName(s[i3].toString());
				  if (name!=null){
					  size++;
				  }	  
				  else if (mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = mapComplexRDFId_index.get(s[i3].toString());
					  ArrayList<String> components = proteinsInComplex[id];
					  size += components.size();
				  }
				  else 
					  size++;
			}
			unsortMap.put(current, size);
			if (size>maxSize)
				maxSize = size;
			i++;
		}
		rectHash = sortByComparator(unsortMap);
		rectList =  new ArrayList<BiochemicalReaction>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			rectList.add(entry.getKey());
		}
			
		// Word cloud for reaction names
		rectWordList =  new ArrayList[rectHash.size()];
		WordCount wc1 = new WordCount(numTop);
		ArrayList<String> a = new ArrayList<String>();
		int r=0;
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			rectWordList[r] = new ArrayList<String>();
			String rectName = entry.getKey().getDisplayName();
			if (rectName!=null){
				String[] pieces = rectName.split(" ");
				for (int k=0;k<pieces.length;k++){
					String str = pieces[k].trim();
					a.add(str);
					rectWordList[r].add(str);
				}
			}
			r++;
		}
		wc1.countNames(a); 
		wordCloud1.updateTags(wc1.wordArray, wc1.counts); 
		
		// Word cloud for complex names
		comWordList =  new ArrayList[complexList.size()];
		WordCount wc2 = new WordCount(numTop);
		ArrayList<String> b = new ArrayList<String>();
		for (int j=0;j< complexList.size();j++){
			comWordList[j] = new ArrayList<String>();
			String comName = complexList.get(j).getDisplayName();
			if (comName!=null){
				String[] pieces = comName.split(" ");
				for (int k=0;k<pieces.length;k++){
					String str = pieces[k].trim();
					b.add(str);
					comWordList[j].add(str);
				}
			}
			r++;
		}
		
		wc2.countNames(b); 
		wordCloud2.updateTags(wc2.wordArray, wc2.counts); 
		
		System.out.println("setItems 1");
		// positions of items
		iX = new Integrator[rectHash.size()];
		iY = new Integrator[rectHash.size()];
		iH = new Integrator[rectHash.size()];
		iS = new Integrator[rectHash.size()][rectHash.size()];
		iS1 = new Integrator[rectHash.size()];
		iS2 = new Integrator[rectHash.size()];
		iS3 = new Integrator[rectHash.size()];
		iS4 = new Integrator[rectHash.size()];
		for (i=0;i<rectHash.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
			for (int j=0;j<rectHash.size();j++){
				iS[i][j] = new Integrator(0, 0.2f,SliderSpeed.speed/2);
			}
			iS1[i] = new Integrator(0, 0.2f,SliderSpeed.speed);
			iS2[i] = new Integrator(0, 0.2f,SliderSpeed.speed);
			iS3[i] = new Integrator(0, 0.2f,SliderSpeed.speed);
			iS4[i] = new Integrator(0, 0.2f,SliderSpeed.speed);
		}
		
		
		int numValid = mapProteinRDFId.size();
		mapProteinRDFId_index = new HashMap<String,Integer>();
		int p1=0;
		for (Map.Entry<String,String> entry : mapProteinRDFId.entrySet()){
			String displayName = entry.getValue();
			mapProteinRDFId_index.put(displayName, p1);
			p1++;
		}
		
		updateComplexList();
		
		updateUnidentifiedElements();
		int numInvalid = unidentifiedList.size();
		proteins =  new String[numValid+numInvalid];
		iP =  new Integrator[numValid+numInvalid];
		
		int p2=0;
		for (Map.Entry<String,String> entry : mapProteinRDFId.entrySet()){
			String displayName = entry.getValue();
			proteins[p2] = displayName;
			iP[p2] =   new Integrator(20, 0.5f,0.1f);
			p2++;
		}
		for (int p=0; p<numInvalid;p++){
			proteins[numValid+p] =  unidentifiedList.get(p);
			mapProteinRDFId_index.put(unidentifiedList.get(p), numValid+p);
			iP[numValid+p] =   new Integrator(20, 0.5f,0.1f);
		}
		simulationRectList = new ArrayList<Integer>();
		simulationRectListLevel = new ArrayList<Integer>();
		
		// For common downstream (PopupCausality==4)
		selectedReactionsForCommonDownstream = new ArrayList<Integer>();
		selectedProteinForCommonDownstream = new ArrayList<Integer>();
		selectedComplexForCommonDownstream = new ArrayList<Integer>();
		
		if (popupReactionOrder.s==0 || popupReactionOrder.s==1 || popupReactionOrder.s==2){
			updateProteinPositions();
			updateComplexPositions();
			updateReactionPositions(); // **********Update reactions when updating proteins **********
		}
		else if (popupReactionOrder.s==3){
			updateReactionPositions(); 
			updateProteinPositions();
			updateComplexPositions();
			updateReactionPositions(); 
			
		}
	}
	
	public void updateComplexList(){
		complexIdList = new ArrayList<Integer>();
		int maxID = 0;
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			for (int i3=0;i3<aLeft.length;i3++){
				  if (mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
					  int id = mapComplexRDFId_index.get(aLeft[i3].toString());
					  if (id>maxID)
						  maxID =id;
					  if(complexIdList.indexOf(id)<0)
						  complexIdList.add(id);
				  }
			}
			for (int i3=0;i3<aRight.length;i3++){
				  if (mapComplexRDFId_index.get(aRight[i3].toString())!=null){
					  int id = mapComplexRDFId_index.get(aRight[i3].toString());
					  if (id>maxID)
						  maxID =id;
					  if(complexIdList.indexOf(id)<0)
						  complexIdList.add(id);
				  }
			}
		}
		yComplexes =  new Integrator[maxID+1];
		rComplexes =  new float[maxID+1];
		for (int i=0;i<yComplexes.length;i++){
			yComplexes[i] = new Integrator(10, 0.5f,0.1f);
		}
		
	}
	
	public void updateUnidentifiedElements(){
		unidentifiedList = new ArrayList<String>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			
			ArrayList<String> a1 = getUnidentifiedElements2(aLeft);
			for (int i=0;i<a1.size();i++){
				String ufo = a1.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
			ArrayList<String> a2 = getUnidentifiedElements2(aRight);
			for (int i=0;i<a2.size();i++){
				String ufo = a2.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
		}
	}
	
	public ArrayList<String> getUnidentifiedElements2(Object[] s) {
		ArrayList<String> a = new ArrayList<String>();
		for (int i=0;i<s.length;i++){
			  String name = main.ReactionFlow_1_1.getProteinName(s[i].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
			  }
			  else  if (mapComplexRDFId_index.get(s[i].toString())!=null){
				  int id = mapComplexRDFId_index.get(s[i].toString());
				  ArrayList<String> components = proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					 if (mapProteinRDFId_index.get(components.get(k))==null){
						 if (!a.contains(components.get(k)))
							 a.add(components.get(k));
					 }	  
				  }
			  }
			  else{
				  if (!a.contains(s[i].toString()))
					  a.add(s[i].toString());
			
			 } 
		}
		return a;
	}
	
	public void updateProteinPositions(){
		if (popupReactionOrder.s==0){
			// Compute react Proteins --------------------------------
			ArrayList<Integer> reactProteinList = new ArrayList<Integer>();
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getAllInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (reactProteinList.indexOf(ind)<0){
						reactProteinList.add(ind);
					}	
				}
				ArrayList<Integer> a2 = getAllInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (reactProteinList.indexOf(ind)<0)
						reactProteinList.add(ind);
				}
			}
			hProtein = (parent.height-yBeginList)/(reactProteinList.size());  // Save 20 pixels for Unidentified elements
			if (hProtein>maxH)
				hProtein =maxH;
			
			int count = 0;
			for (int p=0; p<proteins.length;p++){
				if (reactProteinList.contains(p)){
					iP[p].target(yBeginList+hProtein*count);
					count++;
				}
			}
		}
		else if (popupReactionOrder.s==1 || popupReactionOrder.s==2){
			ArrayList<Integer> reactProteinList = new ArrayList<Integer>();
			ArrayList<Integer> reactComplexList = new ArrayList<Integer>();
			
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getAllInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (reactProteinList.indexOf(ind)<0){
						reactProteinList.add(ind);
					}	
				}
				ArrayList<Integer> a2 = getAllInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (reactProteinList.indexOf(ind)<0)
						reactProteinList.add(ind);
				}
				
				ArrayList<Integer> b1  = getComplexInOneSideOfReaction(aLeft);
				ArrayList<Integer> b2  = getComplexInOneSideOfReaction(aRight);
				for (int i=0;i<b1.size();i++){
					int ind = b1.get(i);
					if (reactComplexList.indexOf(ind)<0)
						reactComplexList.add(ind);
				}
				for (int i=0;i<b2.size();i++){
					int ind = b2.get(i);
					if (reactComplexList.indexOf(ind)<0)
						reactComplexList.add(ind);
				}
			}
			
			hProtein = (parent.height-yBeginList)/(reactProteinList.size()+1);  // Save 20 pixels for Unidentified elements
			if (popupReactionOrder.s==2) 
				hProtein = (parent.height-yBeginList)/(reactProteinList.size()+reactComplexList.size()/2+1);  // Save 20 pixels for Unidentified elements
				
			if (hProtein>maxH)
				hProtein =maxH;
			
			System.out.println("reactProteinList.size() = "+reactProteinList.size());
			System.out.println("yComplexes.length = "+yComplexes.length);
			
			ArrayList<Integer> a = orderBySimilarity();
			float count = 0;
			for (int i=0; i<a.size();i++){
				int index = a.get(i);
				if (index<proteins.length){  // Proteins
					if (reactProteinList.contains(index)){
						iP[index].target(yBeginList+hProtein*count);
						count++;
					}
					else{
						iP[index].target(parent.height+20);
					}
				}
				else if (popupReactionOrder.s==2){ // Comlexes
					if (reactComplexList.contains(index-proteins.length)){
						yComplexes[index-proteins.length].target(yBeginList+hProtein*(count-0.5f));
						count+=0.5f;
					}
				}
			}
		}
		else if (popupReactionOrder.s==3){
			float[] pPosistions = new float[proteins.length];
			int[] pCount = new int[proteins.length];
			for (int p=0; p<proteins.length; p++){
				pPosistions[p] = 0;
				pCount[p] = 0;
			}
			
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					pPosistions[ind]+=(iY[r].target-yBeginList)/itemH2;
					pCount[ind]++;
				}
				ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					pPosistions[ind]+=(iY[r].target-yBeginList)/itemH2;
					pCount[ind]++;
				}
				
				float score = 0;
				float size = 0;
				for (int i3=0;i3<aLeft.length;i3++){
					  String name = main.ReactionFlow_1_1.getProteinName(aLeft[i3].toString());
					  if (name==null)
						  name = aLeft[i3].toString();
					  if (mapProteinRDFId_index.get(name)!=null){
						  if (!main.ReactionFlow_1_1.isSmallMolecule(name)) {
							  int p =mapProteinRDFId_index.get(name);
							  score += iP[p].target;
							  size++;
						  }
					  }
					  else  if (mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aLeft[i3].toString());
						  
						  ArrayList<String> components = proteinsInComplex[id];
						  float yL2 = 0;
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  if (mapProteinRDFId_index.get(components.get(k))!=null){
								  yL2+= iP[mapProteinRDFId_index.get(components.get(k))].target-hProtein/4f;
								  numAvailableComponents++;
							  }	  
						  }
						  
						  
						  complexListLeft.add(id);
						  score += yComplexes[id].target;
						  size++;
					  }
				}	  
				for (int i3=0;i3<aRight.length;i3++){
					  String name = main.ReactionFlow_1_1.getProteinName(aRight[i3].toString());
					  if (name==null)
						  name = aRight[i3].toString();
					  if (mapProteinRDFId_index.get(name)!=null){
						  if (!main.ReactionFlow_1_1.isSmallMolecule(name)) {
							  int p =mapProteinRDFId_index.get(name);
							  score += iP[p].target;
							  size++;
						  }
					  }
					  else  if (mapComplexRDFId_index.get(aRight[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aRight[i3].toString());
						  complexListRight.add(id);
						  score += yComplexes[id].target;
						  size++;
					  }
				}	  
				
			}

			
			
			
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			int numActiveProtein = 0;
			for (int p=0; p<proteins.length; p++){
				if (pCount[p]==0)
					unsortMap.put(p, -100f);
				else{
					unsortMap.put(p, pPosistions[p]/pCount[p]);
					numActiveProtein++;
				}	
			}
			
			hProtein = (parent.height-yBeginList)/(numActiveProtein);  // Save 20 pixels for Unidentified elements
			if (hProtein>maxH)
				hProtein =maxH;
			
			Map<Integer, Float> sortedMap = sortByComparator2(unsortMap,false);
			int i5 = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int index = entry.getKey();
				float value = entry.getValue();
				if (value>=0 && index>=0){
					iP[index].target(yBeginList+i5*hProtein);
					i5++;
				}	
				else{
					iP[index].target(parent.height+20);
				}
			}
		}
			
	}
	
	
	
	// **************************************** Order by similarity ****************************************
	public ArrayList<Integer> orderBySimilarity(){	
		ArrayList<Integer> processed =  new ArrayList<Integer>();
		int beginIndex = 0;
		processed.add(beginIndex);
		
		int[][] scoresReaction = computeScoreReaction();
		int[][] scoresComplex = computeScoreComplex();
		while (true){
			int similarIndex =  getSimilarProtein(processed,scoresReaction,scoresComplex);
			if (similarIndex<0) break;
			processed.add(similarIndex);
			beginIndex = similarIndex;
		}
		return processed;
	}
	
	public int getSimilarProtein(ArrayList<Integer> processed, int[][] scoresReaction, int[][] scoresComplex){
		float maxScore = Float.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for (int p=0;p<proteins.length+yComplexes.length;p++){
			if (processed.contains(p)) continue;
			float sum = 0;
			for (int i=0;i<processed.size();i++){
				int index1 = processed.get(i);
				sum += scoresComplex[index1][p];
				sum += scoresReaction[index1][p]*1000;
			}
			if (sum>maxScore){
				maxScore = sum;
				maxIndex = p;
			}
		}
		return maxIndex;
	}
	
	public ArrayList<Integer> getDirectProteinsComplexInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  if (main.ReactionFlow_1_1.getProteinName(s[i3].toString())!=null){
				  String name = main.ReactionFlow_1_1.getProteinName(s[i3].toString());
				  int pIndex = mapProteinRDFId_index.get(name);
				  if (!main.ReactionFlow_1_1.isSmallMolecule(proteins[pIndex])) // do not include small molecules into reactions
					  a.add(pIndex);
			  }
			  else if (unidentifiedList.contains(s[i3].toString())){
				  int id=-1;
				  for (int i=0;i<proteins.length;i++){
					  if (proteins[i].equals(s[i3].toString())){
						  id=i;
					  }
				  }
				  if (id>=0)
					  a.add(id);
				  else{
					  System.out.println("Cound not find unidentified element in getDirectProteinsComplexInOneSideOfReaction");
				  }
				 
			  }
			  else if (mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = mapComplexRDFId_index.get(s[i3].toString());
				  a.add(proteins.length+id);
			  }
		}
		return a;
	}
	
	public ArrayList<Integer> getComplexInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  if (mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = mapComplexRDFId_index.get(s[i3].toString());
				  a.add(id);
			  }
		}
		return a;
	}
	
	public ArrayList<Integer> getAllInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  if (main.ReactionFlow_1_1.getProteinName(s[i3].toString())!=null){
				  String name = main.ReactionFlow_1_1.getProteinName(s[i3].toString());
				  int pIndex = mapProteinRDFId_index.get(name);
				  //if (!main.PathwayViewer_2_11.isSmallMolecule(proteins[pIndex])) // do not include small molecules into reactions
					  a.add(pIndex);
			  }
			  else if (unidentifiedList.contains(s[i3].toString())){
				  int id=-1;
				  for (int i=0;i<proteins.length;i++){
					  if (proteins[i].equals(s[i3].toString())){
						  id=i;
					  }
				  }
				  if (id>=0)
					  a.add(id);
				  else{
					  System.out.println("Cound not find unidentified element in getDirectProteinsComplexInOneSideOfReaction");
				  }
				 
			  }
			  else  if (mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id1 = mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = proteinsInComplex[id1];
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  a.add(mapProteinRDFId_index.get(components.get(k)));
					  }	  
					  else{
						  int id=-1;
						  for (int i=0;i<proteins.length;i++){
							  if (proteins[i]!=null && proteins[i].equals(components.get(k))){
								  id=i;
							  }
						  }
						  if (id>=0)
							  a.add(id);
						  else{
							  System.out.println("Cound not find unidentified element in getDirectProteinsComplexInOneSideOfReaction");
					  }
				  }
				  }
			  }	  
		}
		return a;
	}
	
	
	public int[][] computeScoreComplex(){
		int[][] score = new int [proteins.length+yComplexes.length][proteins.length+yComplexes.length];
		for (int c=0;c<complexIdList.size();c++){
			int idComplex = complexIdList.get(c);
			
			ArrayList<String> components = proteinsInComplex[complexIdList.get(c)];
			 for (int k=0;k<components.size();k++){
				 if (mapProteinRDFId_index.get(components.get(k))==null) continue; 
				 int index1 = mapProteinRDFId_index.get(components.get(k));
				 
				 score[index1][proteins.length+idComplex]+=2;
				 score[proteins.length+idComplex][index1]+=2;
				 for (int l=0;l<components.size();l++){
					 if (l==k) continue;
					 if (mapProteinRDFId_index.get(components.get(l))!=null){
						int index2 =  mapProteinRDFId_index.get(components.get(l));
						score[index1][index2]++;
						score[index2][index1]++;
					 }
					 else{
						// System.out.println("&&&&&&&&&&&&&& Can not find:"+components.get(l));
					 }
				 }	
			 }	
		}
		
		
		return score;
	}
		
	
	public int[][] computeScoreReaction(){
		int[][] score = new int [proteins.length+yComplexes.length][proteins.length+yComplexes.length];
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			
			ArrayList<Integer> a1 = getDirectProteinsComplexInOneSideOfReaction(aLeft);
			for (int i=0;i<a1.size();i++){
				int index1 = a1.get(i);
				for (int j=0;j<a1.size();j++){
					int index2 = a1.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
			
			ArrayList<Integer> a2 = getDirectProteinsComplexInOneSideOfReaction(aRight);
			for (int i=0;i<a2.size();i++){
				int index1 = a2.get(i);
				//System.out.println("	protein = "+proteins[index1]);
				for (int j=0;j<a2.size();j++){
					int index2 = a2.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
			
			for (int i=0;i<a1.size();i++){
				int index1 = a1.get(i);
				for (int j=0;j<a2.size();j++){
					int index2 = a2.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
		}
		return score;
	}
	
	public void updateReactionPositions(){
		// Compute positions
		
		float maxY= 0;
		for (int i=0;i<iP.length;i++){
			if (iP[i].target>maxY && iP[i].target<parent.height)
				maxY = iP[i].target;
		}
		for (int i=0;i<yComplexes.length;i++){
			if (yComplexes[i].target>maxY && yComplexes[i].target<parent.height)
				maxY = yComplexes[i].target;
		}
		
		float maxHeight = PApplet.min(parent.height, maxY);
		itemH2 = (maxHeight-yBeginList)/(rectHash.size());
		
		//if (itemH2>maxH)
		//	itemH2=maxH;
		for (int i=0;i<rectHash.size();i++){
			iH[i].target(itemH2);
		}	
		
		if (popupReactionOrder.s==0){
			for (int i=0;i<rectHash.size();i++){
				iY[i].target(yBeginList+i*itemH2);
			}
		}
		else if (popupReactionOrder.s==1 || popupReactionOrder.s==2){   // Order by similarity
			int indexOfItemHash=0;
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			complexListLeft = new ArrayList<Integer>();
			complexListRight = new ArrayList<Integer>();
			
			// Save the reaction id for complex which has no positions
			ArrayList<Integer>[] a = new ArrayList[yComplexes.length];
			for (int i=0;i<yComplexes.length;i++){
				a[i] =  new ArrayList<Integer>();
			}
			
			for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				float score = 0;
				float size = 0;
				for (int i3=0;i3<aLeft.length;i3++){
					  String name = main.ReactionFlow_1_1.getProteinName(aLeft[i3].toString());
					  if (name==null)
						  name = aLeft[i3].toString();
					  if (mapProteinRDFId_index.get(name)!=null){
						  if (!main.ReactionFlow_1_1.isSmallMolecule(name)) {
							  int p =mapProteinRDFId_index.get(name);
							  score += iP[p].target;
							  size++;
						  }
					  }
					  
					  else  if (mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aLeft[i3].toString());
						  complexListLeft.add(id);
						  if (yComplexes[id].target>0){
								score += yComplexes[id].target;
								size++;
						  }
						  else{  // Save the reaction id for complex which has no positions
							  a[id].add(indexOfItemHash);
						  }
					  }
				}	  
				for (int i3=0;i3<aRight.length;i3++){
					  String name = main.ReactionFlow_1_1.getProteinName(aRight[i3].toString());
					  if (name==null)
						  name = aRight[i3].toString();
					  if (mapProteinRDFId_index.get(name)!=null){
						  if (!main.ReactionFlow_1_1.isSmallMolecule(name)) {
							  int p =mapProteinRDFId_index.get(name);
							  score += iP[p].target;
							  size++;
						  }
					  }
					  
					  else  if (mapComplexRDFId_index.get(aRight[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aRight[i3].toString());
						  complexListRight.add(id);
						  if (yComplexes[id].target>0){
							  score += yComplexes[id].target;
							  size++;
						  }
						  else{  // Save the reaction id for complex which has no positions
							  a[id].add(indexOfItemHash);
						  }
					  }
				}	  
				
				if (size>0)
					score = score/size;
				
				
				//if (size==0)
				//	score = -1000;
				unsortMap.put(indexOfItemHash, score);	
				indexOfItemHash++;
			}
			
			Map<Integer, Float> sortedMap = sortByComparator2(unsortMap,false);
			int i5 = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int rectOrder = entry.getKey();
				iY[rectOrder].target(yBeginList+i5*itemH2);
				i5++;
			}
			

			// Update position for complex which has no positions
			for (int i=0; i<a.length;i++){
				if (a[i].size()>0){
					float sum=0;
					for (int j=0;j<a[i].size();j++){
						int reactionId = a[i].get(j);
						sum += iY[reactionId].target;
					}
					yComplexes[i].target(sum/a[i].size());
				}
			}

		}
		else if (popupReactionOrder.s==3){
			ArrayList<Integer> doneList = new ArrayList<Integer>();
			ArrayList<Integer> circleList = new ArrayList<Integer>();
			
			int count = 0;
			int r = getNoneUpstream(doneList);
			
			while (count<rectList.size()){
			//	System.out.println(count+"	doneList="+doneList+"	r="+r);
				if (r>=0){
					doneList.add(r);
					r = getNoneUpstream(doneList);
				}
				else{
					int randomReaction = getReactionMaxDownstream(doneList);
					doneList.add(randomReaction);
					circleList.add(randomReaction);
					r = getNoneUpstream(doneList);
				}	
				count++;
			}
			
			float totalH = itemH2*rectList.size();
			
			// Compute nonCausality reaction
			ArrayList<Integer> nonCausalityList = new ArrayList<Integer>();
			for (int i=0;i<doneList.size();i++){
				int index = doneList.get(i);
				if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0)
					nonCausalityList.add(index);
			}
			
			itemH2 = totalH/(rectList.size()+circleList.size()-nonCausalityList.size()+1);
			float circleGap = itemH2;
			float circleGapSum = 0;
			
			int count2 = 0;
			int count3 = 0;
			float yStartCausality = yBeginList+(rectList.size()-nonCausalityList.size()+circleList.size()+1)*itemH2;
			for (int i=0;i<doneList.size();i++){
				int index = doneList.get(i);
				// Compute nonCausality reaction
				if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0){
					iY[index].target(yStartCausality +count3*itemH2/4);
					count3++;
				}	
				else{
					if(circleList.contains(index)){
						iY[index].target(circleGapSum+ yBeginList+count2*itemH2+circleGap);
						circleGapSum +=circleGap;
					}
					else{
						iY[index].target(circleGapSum+yBeginList+count2*itemH2);
					}
					count2++;
				}
			}
			/*
			// Put non-causality reaction to the end of the list
			for (int i=0;i<nonCausalityList.size();i++){
				int index = nonCausalityList.get(i);
				iY[index].target(yBeginList+(count2+circleList.size()+1)*itemH2);
				count2++;
			}*/	
		}
	}
	
	
	public int getReactionMaxDownstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<rectList.size();i++){
			if (doneList.contains(i)) continue;
			a.add(i);
		}
		return getReactionMaxDownstreamIn(a);
	}
	
	public int getReactionMaxDownstreamIn(ArrayList<Integer> list){
		int numDownstream = 0;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> down = getDirectDownstream(index);
			if (down.size()>=numDownstream){
				numDownstream = down.size();
				react =index;
			}	
		}
		return react;
	}
	
	public int getReactionMinDownstreamIn(ArrayList<Integer> list){
		int numDownstream = Integer.MAX_VALUE;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> down = getDirectDownstream(index);
			if (down.size()<numDownstream){
				numDownstream = down.size();
				react =index;
			}	
		}
		return react;
	}
	
	public int getReactionMaxUpstreamIn(ArrayList<Integer> list){
		int numUpstream = 0;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> up = getDirectUpstream(index);
			if (up.size()>=numUpstream){
				numUpstream = up.size();
				react =index;
			}	
		}
		return react;
	}
	
	public int getNoneUpstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		/*for (int i=0;i<rectList.size();i++){
			if (doneList.contains(i)) continue;
			ArrayList<Integer> up = this.getDirectUpstream(i);
			if (up.size()==0)  {//No upstream
				a.add(i);
			}	
		}*/
		if (a.size()>0){
			return getReactionMinDownstreamIn(a);
		}
		else{
			ArrayList<Integer> b = new ArrayList<Integer>();
			for (int i=0;i<rectList.size();i++){
				if (doneList.contains(i)) continue;
				ArrayList<Integer> up = this.getDirectUpstream(i);
				if (isContainedAllUpInDoneList(up,doneList)){  // Upstream are all in the doneList;
				//	return i;
					b.add(i);
				}	
			}
			if (b.size()>0){
				return getReactionMaxUpstreamIn(b);
			}
			return -1;
		}
		
	}
	
	public boolean isContainedAllUpInDoneList(ArrayList<Integer> up, ArrayList<Integer> doneList){
		for (int i=0;i<up.size();i++){
			int r = up.get(i);
			if (!doneList.contains(r))
				return false;
		}
		return true;
	}
		
		
	
	
	public void updateComplexPositions(){
		if (popupReactionOrder.s==0 || popupReactionOrder.s==1 || popupReactionOrder.s==2){
			ArrayList<Integer>[] a = new ArrayList[yComplexes.length];
			for (int i=0;i<yComplexes.length;i++){
				a[i] =  new ArrayList<Integer>();
			}
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				for (int i3=0;i3<aLeft.length;i3++){
					  if (mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aLeft[i3].toString());
						  ArrayList<String> components = proteinsInComplex[id];
						  float yL2 = 0;
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  if (mapProteinRDFId_index.get(components.get(k))!=null){
								  yL2+= iP[mapProteinRDFId_index.get(components.get(k))].target-hProtein/4f;
								  numAvailableComponents++;
							  }	  
						  }
						  if (numAvailableComponents==0)
							  yL2 =iY[r].target-iH[r].target/2;
						  else 	  
							  yL2 /= numAvailableComponents;
						  
						  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 1, maxH/3);
						  if (popupReactionOrder.s!=2 || yComplexes[id].target<=0)
							  yComplexes[id].target(yL2);
						  rComplexes[id] = radius;
					  }
				}
				
				for (int i3=0;i3<aRight.length;i3++){
					  if (mapComplexRDFId_index.get(aRight[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aRight[i3].toString());
					 	  ArrayList<String> components = proteinsInComplex[id];
						  float yR2 = 0;
					      int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  if (mapProteinRDFId_index.get(components.get(k))!=null){
								  yR2+= iP[mapProteinRDFId_index.get(components.get(k))].target-hProtein/4f;
								  numAvailableComponents++;
							  }	  
						  }
						  if (numAvailableComponents==0)
							  yR2 =iY[r].target-iH[r].target/2;
						  else 	  
							  yR2 /= numAvailableComponents;
						  
						  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 1, maxH/3);
						  if (popupReactionOrder.s!=2 || yComplexes[id].target<=0)
								yComplexes[id].target(yR2);
						  rComplexes[id] = radius;
					  }
				}	 
				
			}
			
			/*
			// Compute the position of the last protein
			float maxY = 0;
			for (int p=0;p<iP.length;p++){
				float yy = iP[p].target;
				if (yy>maxY && yy<parent.height)
					maxY=yy;
			}   
			
			// Reorganize complexes to avoid  overlapping
			Map<Integer, Float> sortMap = sortByComparator2(unsortMap,false);
			int i=0;
			float prevousPos = 60;
			float gapY = (maxY-prevousPos-150)/sortMap.size();
			
			for (Map.Entry<Integer, Float> entry : sortMap.entrySet()) {
				int complexId = entry.getKey();
				float complexPos = entry.getValue();
				if (complexPos<prevousPos+gapY){
					complexPos = prevousPos+gapY;
					yComplexes[complexId].target(complexPos);
				}
				prevousPos = complexPos;
				i++;
			}*/
		}	
		else if (popupReactionOrder.s==3){
			float[] cPosistions = new float[mapComplexRDFId_index.size()];
			int[] cCount = new int[mapComplexRDFId_index.size()];
			for (int c=0; c<mapComplexRDFId_index.size(); c++){
				cPosistions[c] = 0;
				cCount[c] = 0;
			}
			
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				for (int i3=0;i3<aLeft.length;i3++){
					  if (mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aLeft[i3].toString());
						  cPosistions[id]+=(iY[r].target-yBeginList)/itemH2;
						  cCount[id]++;
						  
						  // Update Complex size
						  ArrayList<String> components = proteinsInComplex[id];
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  if (mapProteinRDFId_index.get(components.get(k))!=null){
								  numAvailableComponents++;
							  }	  
						  }
						  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
						  rComplexes[id] = radius;
					}
				}
				for (int i3=0;i3<aRight.length;i3++){
					  if (mapComplexRDFId_index.get(aRight[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(aRight[i3].toString());
						  cPosistions[id]+=(iY[r].target-yBeginList)/itemH2;
						  cCount[id]++;
						  
						  // Update Complex size
						  ArrayList<String> components = proteinsInComplex[id];
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  if (mapProteinRDFId_index.get(components.get(k))!=null){
								  numAvailableComponents++;
							  }	  
						  }
						  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
						  rComplexes[id] = radius;
					  }
				}
			}
			
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			int numActiveComplex = 0;
			for (int c=0; c<mapComplexRDFId_index.size(); c++){
				if (cCount[c]==0)
					unsortMap.put(c, -100f);
				else{
					unsortMap.put(c, cPosistions[c]/cCount[c]);
					numActiveComplex++;
				}	
			}
			
			float hComplex = (parent.height-yBeginList-2)/(numActiveComplex);  // Save 20 pixels for Unidentified elements
			if (hComplex>maxH)
				hComplex =maxH;
			
			Map<Integer, Float> sortedMap = sortByComparator2(unsortMap,false);
			int i5 = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int index = entry.getKey();
				float value = entry.getValue();
				if (value>=0 && index>=0){
					yComplexes[index].target(yBeginList+i5*hComplex);
					i5++;
				}	
				else{
				//	yComplexes[index].target(parent.height-200+index*2);
				}
			}
		}
	}
		
	// Sort decreasing order of Reaction by its size
	public static Map<BiochemicalReaction, Integer> sortByComparator(Map<BiochemicalReaction, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<BiochemicalReaction, Integer>> list = 
			new LinkedList<Map.Entry<BiochemicalReaction, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<BiochemicalReaction, Integer>>() {
			public int compare(Map.Entry<BiochemicalReaction, Integer> o1,
                                           Map.Entry<BiochemicalReaction, Integer> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<BiochemicalReaction, Integer> sortedMap = new LinkedHashMap<BiochemicalReaction, Integer>();
		for (Iterator<Map.Entry<BiochemicalReaction, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<BiochemicalReaction, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	// Sort Reactions by score (average positions of proteins)
	public static Map<Integer, Float> sortByComparator2(Map<Integer, Float> unsortMap, boolean decreasing) {
		// Convert Map to List
		List<Map.Entry<Integer, Float>> list = 
			new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		if (decreasing){
			Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
				public int compare(Map.Entry<Integer, Float> o1,
	                                           Map.Entry<Integer, Float> o2) {
						return -(o1.getValue()).compareTo(o2.getValue());
				}
			});
		}
		else{
			Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
				public int compare(Map.Entry<Integer, Float> o1,
	                                           Map.Entry<Integer, Float> o2) {
						return (o1.getValue()).compareTo(o2.getValue());
				}
			});
		}
 
		// Convert sorted map back to a Map
		Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Iterator<Map.Entry<Integer, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	
	
	
	
	public void drawReactions(float x_){
		if (!isAllowedDrawing) return;
		percentPositionComplex.target(0.12f);
		if (popupReactionOrder.s==2 || popupReactionOrder.s==0){
			percentPositionComplex.target(0.02f);
		}
		percentPositionComplex.update();
		
		xL = x_;
		float www = parent.width/2.4f;
		xL2 = xL+www*percentPositionComplex.value;
		xRect = x_+www*0.5f;
		xR = x_+www;
		xR2 = xR-www*percentPositionComplex.value;
		
		// Draw seach box
		textbox1.draw(xRect);
		parent.smooth();
		parent.strokeWeight(1);
		
		if (proteins==null) return;
			for (int i=0;i<rectHash.size();i++){
				iY[i].update();
				iH[i].update();
			}
			
			float maxY = 0;
			for (int p=0; p<proteins.length;p++){
				iP[p].update();
				if (iP[p].value>maxY)
					maxY = iP[p].value;
			}
			yUFO = maxY+20;
			  		
			// Draw Protein names ***************************************************************************************
			if (simulationRectList.size()>0){
				for (int p=0; p<proteins.length;p++){
					drawProteinLeft(p,20);
					drawProteinRight(p,20);
				}
				for (int i=0;i<simulationRectList.size();i++){
					int r = simulationRectList.get(i);
					
					int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
					int rectIndex = simulationRectList.indexOf(r);
					int rLevel = simulationRectListLevel.get(rectIndex);
					if (rLevel==currentLevel){
						BiochemicalReaction rect = rectList.get(r);
						ArrayList<Integer> lList = getProteinsInOneSideOfReaction(rect.getLeft().toArray());
						for (int j=0; j<lList.size();j++){
							int protein = lList.get(j);
							drawProteinLeft(protein,255);
						}
						ArrayList<Integer> rList = getProteinsInOneSideOfReaction(rect.getRight().toArray());
						for (int j=0; j<rList.size();j++){
							int protein = rList.get(j);
							drawProteinRight(protein,255);
						}
					}
				}
			}
			
			else if (bRect>=0 || bProteinL>=0 || bComplexL>=0 || bProteinR>=0 || bComplexR>=0 || !textbox1.searchText.equals("")){
				for (int p=0; p<proteins.length;p++){
					// Get protein in the brushing reactions
					if (p==bProteinL || bProteinLeft.indexOf(p)>=0)
						drawProteinLeft(p,255);
					else
						drawProteinLeft(p,25);
					
					if (p==bProteinR || bProteinRight.indexOf(p)>=0)
						drawProteinRight(p,255);
					else
						drawProteinRight(p,25);
				}
			}
			else{
				for (int p=0; p<proteins.length;p++){
					drawProteinLeft(p,200);
					drawProteinRight(p,200);
				}
			}
			
			// ******************************** Reaction Links ******************************************************************************************************************************
			processedComplexLeft =  new ArrayList<Integer>();
			processedComplexRight =  new ArrayList<Integer>();
			
			if (PopupCausality.s==2 )  // Shortest path
				computeShortestPath();
			
			if (simulationRectList.size()>0 ){
				for (int r=0;r<simulationRectList.size();r++) {
					BiochemicalReaction rect = rectList.get(simulationRectList.get(r));
					drawReactionLink(rect, simulationRectList.get(r), xL, xL2, xRect, xR, xR2, 255);
				}
			}
			else if (PopupCausality.s==0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect || bRectListL.indexOf(r)>=0) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 20);
				}
			}		
			else if (PopupCausality.s==1 ){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect || bRectListL.indexOf(r)>=0) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 20);
				}	
			}
			else if (PopupCausality.s==2 ){ 
				// Above Simulation 
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect || bRectListL.indexOf(r)>=0) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 20);
				}
			}
			else if (PopupCausality.s==3 ){ 
				// Above Simulation 
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect || bRectListL.indexOf(r)>=0) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 20);
				}
			}
			else if (PopupCausality.s==4){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 5);
				}
			}
			else if (bRect>=0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (!textbox1.searchText.equals("") ){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (sRectListByText.indexOf(r)>=0)
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (bProteinL>=0 || bComplexL>=0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (bRectListL.indexOf(r)>=0)
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (bProteinR>=0|| bComplexR>=0 ){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (bRectListR.indexOf(r)>=0){
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					}	
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else{
				if (PopupCausality.s!=0 && simulationRectList.size()==0){
					for (int r=0;r<rectList.size();r++) {
						BiochemicalReaction rect = rectList.get(r);
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 200);
					}
				}
			}
			
			
			
			// Draw reaction causation ******************************************************************************
			loopReactionList =new ArrayList<Integer>();
			
			if (simulationRectList.size()>0){  // Simulation always happens anytime.
				ArrayList<Integer> processedList = new ArrayList<Integer>();
				for (int r=0;r<simulationRectList.size();r++){
					int rect = simulationRectList.get(r);
					int level = simulationRectListLevel.get(r);
					processedList.add(rect);
					drawDownStreamReaction(rect, level,processedList, iS4[rect].value,255);
					
					
				}
			}
			else if (PopupCausality.s==0){
				if (bRect>=0 || bProteinL>=0 || bComplexL>=0 ){
					ArrayList<Integer> downstreamList = new ArrayList<Integer>();
					ArrayList<Integer> parentList = new ArrayList<Integer>();
					ArrayList<Integer> levelList = new ArrayList<Integer>();
					if (bRect>=0){
						downstreamList.add(bRect);
						listReactionDownStream( bRect, 0, downstreamList,  parentList, levelList);
					}
					else if (bRectListL.size()>0){
						for (int i=0;i<bRectListL.size();i++) {
							int r = bRectListL.get(i);
							downstreamList.add(r);
							listReactionDownStream( r, 0, downstreamList,  parentList, levelList);
						}
					}	
					
					for(int r=0;r<rectList.size();r++){
						ArrayList<Integer> processedList = new ArrayList<Integer>();
						processedList.add(r);
						if (downstreamList.indexOf(r)>=0){  
							drawDownStreamReaction(r,-100, processedList, 1000,255);
						}
						else{
							drawDownStreamReaction(r,-100, processedList, 1000,15);
						}
					}
					
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						if (downstreamList.indexOf(i)>=0){  
							drawReactionNode(entry, i, 200);
						}
						i++;
					}
				}
				else{
					ArrayList<Integer> processedList = new ArrayList<Integer>();
					for (int r=0;r<rectList.size();r++) {
						processedList.add(r);
						drawDownStreamReaction(r,-100, processedList, 1000,255);
					}
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						drawReactionNode(entry, i, 200);
						i++;
					}
				}
			}	
			else if (PopupCausality.s==1){
				if (bRect>=0 || bProteinL>=0 || bComplexL>=0 ){
					deleteReactionList = new ArrayList<Integer>();
					ArrayList<Integer> parentList = new ArrayList<Integer>();
					ArrayList<Integer> levelList = new ArrayList<Integer>();
					if (bRect>=0){
						deleteReactionList.add(bRect);
						listReactionDownStream( bRect, 0, deleteReactionList,  parentList, levelList);
					}
					else if (bRectListL.size()>0){
						for (int i=0;i<bRectListL.size();i++) {
							int r = bRectListL.get(i);
							deleteReactionList.add(r);
							listReactionDownStream( r, 0, deleteReactionList,  parentList, levelList);
						}
					}	
					
					if(deleteReactionList.size()>=0){
						iDelete.target(1);
						iDelete.update();
					}
					for(int r=0;r<rectList.size();r++){
						ArrayList<Integer> processedList = new ArrayList<Integer>();
						processedList.add(r);
						if (deleteReactionList.indexOf(r)>=0){  
							drawDownStreamReaction(r,-100, processedList, 1000,255);
						}
						else{
							drawDownStreamReaction(r,-100, processedList, 1000,200);
						}
					}
					
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						if (deleteReactionList.indexOf(i)>=0){  
							float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
							parent.noStroke();
							parent.fill(0,255);
							parent.ellipse(xRect,iY[i].value, r, r);
							
							
							float y3 = iY[i].value-iH[i].value*1/3;
							if (check5.s){
								parent.fill(0);
								parent.textSize(12);
								parent.textAlign(PApplet.CENTER);
								String rectName = entry.getKey().getDisplayName();
								parent.text(rectName,xRect,y3);
							}	
						}
						i++;
					}
				}
				else{
					ArrayList<Integer> processedList = new ArrayList<Integer>();
					for (int r=0;r<rectList.size();r++) {
						processedList.add(r);
						drawDownStreamReaction(r,-100, processedList, 1000,255);
					}
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						drawReactionNode(entry, i, 200);
						
						i++;
					}
				}
				
			}
			else if (PopupCausality.s==2){
				if (bRect>=0 || bProteinL>=0 || bComplexL>=0 ){
					ArrayList<Integer> downstreamList = new ArrayList<Integer>();
					ArrayList<Integer> parentList = new ArrayList<Integer>();
					ArrayList<Integer> levelList = new ArrayList<Integer>();
					if (bRect>=0){
						downstreamList.add(bRect);
						listReactionDownStream( bRect, 0, downstreamList,  parentList, levelList);
					}
					else if (bRectListL.size()>0){
						for (int i=0;i<bRectListL.size();i++) {
							int r = bRectListL.get(i);
							downstreamList.add(r);
							listReactionDownStream( r, 0, downstreamList,  parentList, levelList);
						}
					}	
					
					for(int r=0;r<rectList.size();r++){
						ArrayList<Integer> processedList = new ArrayList<Integer>();
						processedList.add(r);
						if (downstreamList.indexOf(r)>=0){  
							drawDownStreamReaction(r,-100, processedList, 1000,255);
						}
						else{
							drawDownStreamReaction(r,-100, processedList, 1000,15);
						}
					}
					
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						if (downstreamList.indexOf(i)>=0){  
							drawReactionNode(entry, i, 200);
						}
						i++;
					}
				}
				else{
					ArrayList<Integer> processedList = new ArrayList<Integer>();
					for (int r=0;r<rectList.size();r++) {
						processedList.add(r);
						drawDownStreamReaction(r,-100, processedList, 1000,255);
					}
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						drawReactionNode(entry, i, 200);
						i++;
					}
				}
			}	
			else if (PopupCausality.s==3){
				if (bRect>=0){
					ArrayList<Integer> loopList =  new ArrayList<Integer>();
					drawLoopFrom(bRect, loopList);
					loopReactionList = loopList;
				}
				else{
					for (int r=0;r<rectList.size();r++) {
						ArrayList<Integer> loopRectList =  new ArrayList<Integer>();
						drawLoopFrom(r, loopRectList);
						for (int j=0;j<loopRectList.size();j++){
							int index = loopRectList.get(j);
							if (loopReactionList.indexOf(index)<0)
								loopReactionList.add(index);
						}
					}
					
				}
				
			}	
			else if (PopupCausality.s==4){    // Common downstream
					for(int r=0;r<rectList.size();r++){
						ArrayList<Integer> processedList = new ArrayList<Integer>();
						processedList.add(r);
						drawDownStreamReaction(r,-100, processedList, 1000,20);
						
					}
					
					// Draw reaction nodes
					int i=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
						if (selectedReactionsForCommonDownstream.indexOf(i)>=0){  
							drawReactionNode(entry, i, 200);
						}
						else{
							drawReactionNode(entry, i, 20);
						}
						i++;
					}
			}	
			else{
				for (int r=0;r<rectList.size();r++) {
					for (int g=0;g<rectList.size();g++) {
						iS[r][g].set(0);
					}	
				}
			}
			
			// Draw reaction Nodes *************************************************************
			if (simulationRectList.size()>0){
				int r = 0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (simulationRectList.indexOf(r)>=0)
						drawReactionNodeSimulation(entry, r);
						
					r++;
				}
			}
			else if (PopupCausality.s==0 ){
				// In drawing causality
			}
			else if (PopupCausality.s==1 ){
				// In drawing causality
			}
			else if (PopupCausality.s==2 ){
				// In drawing causality
			}
			else if(PopupCausality.s==3){
				int i=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (loopReactionList.indexOf(i)>=0)
						drawReactionNode(entry, i, 255);
					else
						drawReactionNode(entry, i, 50);
					i++;
				}
			}
			else if (PopupCausality.s==4 ){
				// In drawing causality
			}
			else{
				int i=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (bRect>=0)
						drawReactionNode(entry, i, 25);
					else
						drawReactionNode(entry, i, 200);
					i++;
				}
			}
			
			float x7 = parent.width*0.75f;
			float y7 = 60;
			
			
			parent.strokeWeight(1);
			check5.draw((int) x7, (int) y7-19);
			
			
			// ****************** Draw output list ***********************************************************************
			if (simulationRectList.size()>0){
				float x2 = parent.width*3/4f;
				float y3 = 320;
				float y2 = 520;
				parent.fill(0,20);
				parent.noStroke();
				parent.rect(x2-30, y3-40, parent.width-x2+50, parent.height-y3+50);
				
				// Print out Reaction list
				parent.fill(0);
				parent.textSize(12);
				parent.text("Reaction list: ", x2-25,y2-18);
				
				boolean flashing = false;
				for (int i=0;i<simulationRectList.size();i++){
					int r = simulationRectList.get(i);
					parent.fill(0);
					String name = rectList.get(r).getDisplayName();
					
					int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
					int rectIndex = simulationRectList.indexOf(r);
					int rLevel = simulationRectListLevel.get(rectIndex);
					
					if (rLevel==currentLevel && iS4[r].value<990){
						float sat = 55+(parent.frameCount*20)%200;
						parent .fill(sat,0,0);
						flashing = true;
					}
					parent.text(name, x2+rLevel*25,y2+17*i);
					
				}
				
				// Print out intermediate proteins and complexes
				parent.fill(0);
				parent.textSize(12);
				parent.text("Intermediate proteins/complexes: ", x2-25,y3-18);
				for (int i=0;i<interElements.size();i++){
					String ref = interElements.get(i);
					parent.fill(0);
					String name = main.ReactionFlow_1_1.getProteinName(ref);
					  if (name==null){
						  String[] pieces = ref.split("/");
						  if (pieces.length>=1)
								name = pieces[pieces.length-1];
						  else{
								pieces = ref.split("/");
								if (pieces.length>1)
									name = pieces[pieces.length-1];
						  }
					  }	  
					  if (mapProteinRDFId_index.get(name)!=null){
						  name = proteins[mapProteinRDFId_index.get(name)];
							
					  }	  
					  else if (mapComplexRDFId_index.get(ref)!=null){
						  int id = mapComplexRDFId_index.get(ref);
						  name = proteinsInComplex[id].toString();
					  }
						
					int currentLevel = interElementsLevel.get(interElementsLevel.size()-1);
					int eLevel = interElementsLevel.get(i);
					
					if (eLevel==currentLevel && flashing){
						float sat = 55+ (parent.frameCount*20)%200;
						parent .fill(sat,0,0);
					}
					parent.text(name, x2+(eLevel)*25,y3+17*i);
					
				}
				
			}
			else if (PopupCausality.s==1 && (bRect>=0 || bProteinL>=0 || bComplexL>=0)){
				float x2 = parent.width*3/4f;
				float y2 = 180;
				parent.fill(0,20);
				parent.noStroke();
				parent.rect(x2-30, y2-40, parent.width-x2+50, parent.height-y2+50);
				
				parent.fill(0);
				parent.textSize(12);
				parent.text("Affected reactions: "+deleteReactionList.size()+" / "+rectList.size()+" biochemical reactions", x2-25,y2-20);
				
				for (int i=0;i<deleteReactionList.size();i++){
					int r = deleteReactionList.get(i);
					parent.fill(0);
					String name = rectList.get(r).getDisplayName();
					parent.text(name, x2 +25,y2+20*i);
				}
			}
			else if (PopupCausality.s==3){
				float x2 = parent.width*3/4f;
				float y2 = 180;
				parent.fill(0,20);
				parent.noStroke();
				parent.rect(x2-30, y2-40, parent.width-x2+50, parent.height-y2+50);
				
				parent.fill(0);
				parent.textSize(12);
				parent.text("Reactions loops: ", x2-25,y2-20);
				
				for (int i=0;i<loopReactionList.size();i++){
					int r = loopReactionList.get(i);
					parent.fill(0);
					String name = rectList.get(r).getDisplayName();
					parent.text(name, x2 +25,y2+20*i);
				}
			}
			else if (PopupCausality.s<0 || PopupCausality.s>=100){
				check11.draw((int) x7, (int) y7);
				check13.draw((int) x7, (int) y7+19);
				check14.draw((int) x7, (int) y7+38);
				check15.draw((int) x7, (int) y7+57);
				// Draw word cloud
				wordCloud1.x1= (int) (parent.width*0.88f); 
				wordCloud1.x2=parent.width; 
				wordCloud1.draw(parent);
				
				// Draw word relationship
				int[][] rel1 =  new int[numTop][numTop];
				for (int r=0;r<rectList.size();r++){
					for (int m=0;m<numTop;m++){
						for (int n=0;n<numTop;n++){
							if (wordCloud1.words[m].equals("") || wordCloud1.words[n].equals("")) 
								continue;
							if (rectWordList[r].contains(wordCloud1.words[m].word) && rectWordList[r].contains(wordCloud1.words[n].word))
								rel1[m][n]++;
						}
					}		
				}
				int minCount1 = 2; 
				drawRelationship(wordCloud1, rel1, Color.BLACK,minCount1);
				
				// Draw word cloud
				wordCloud2.x1= (int) (parent.width*0.88f); 
				wordCloud2.x2=parent.width; 
				wordCloud2.draw(parent);
				
				// Draw word relationship
				int[][] rel2 =  new int[numTop][numTop];
				for (int r=0;r<rectList.size();r++){
					for (int m=0;m<numTop;m++){
						for (int n=0;n<numTop;n++){
							if (wordCloud2.words[m].equals("") || wordCloud2.words[n].equals("")) 
								continue;
							if (rectWordList[r].contains(wordCloud2.words[m].word) && rectWordList[r].contains(wordCloud2.words[n].word))
								rel2[m][n]++;
						}
					}		
				}
				int minCount2 = 1; 
				drawRelationship(wordCloud2, rel2, complexRectionColor,minCount2);
			}
			
// ****************** Draw Button simulations ***********************************************************************************************
			if (!buttonPause.s){
				for (int r=0;r<rectList.size();r++) {
					iS1[r].update();
					iS2[r].update();
					iS3[r].update();
					iS4[r].update();
				}
			}
			if (simulationRectList.size()>0){
				int maxLevel = -1;
				for (int i=0;i<simulationRectListLevelAll.size();i++){
					int level = simulationRectListLevelAll.get(i);
					if (level>maxLevel)
						maxLevel = level;
				}
				float y3 = 130;
				float x3 = parent.width*3/4f-20;
				
				//buttonPlay.draw(parent.width-400, y3);
				buttonPause.draw(x3+20, y3);
				buttonStop.draw(x3+70, y3);
				buttonReset.draw(x3+120, y3);
				buttonBack.draw(x3+170, y3);
				buttonForward.draw(x3+220, y3);
				slider.draw(x3, y3+80, maxLevel);
				slider2.draw(x3+50, y3+110);
			}
				
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text(rectHash.size()+" Reactions",xRect,45);
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.RIGHT);
			parent.text("Input Proteins", xL, 45);
			parent.textAlign(PApplet.LEFT);
			parent.fill(complexRectionColor.getRGB());
			parent.text("Complexes", xL+25, 45);
			
			parent.textAlign(PApplet.RIGHT);
			parent.text("Complexes", xR-25, 45);
			parent.fill(0);
			parent.textAlign(PApplet.LEFT);
			parent.text("Proteins Output", xR, 45);
			
			
			// Draw color legend -------------------------------------
			/*
			float yy2 = 500;
			float xx2 = 100; 
			parent.noStroke();
			parent.fill(220);
			parent.rect(xx2-20, yy2-18, 280, 80);
			
			parent.strokeWeight(1);
			parent.textSize(12);
			parent.stroke(formComplexColor.getRGB());
			parent.line(xx2, yy2, xx2+50, yy2);
			parent.fill(formComplexColor.getRGB());
			parent.text("Complex formation links", xx2+55,yy2+5);
			yy2+=20;
			parent.stroke(complexRectionColor.getRGB());
			parent.line(xx2, yy2, xx2+50, yy2);
			parent.fill(complexRectionColor.getRGB());
			parent.text("Complex reaction links", xx2+55,yy2+5);
			yy2+=20;
			parent.stroke(proteinRectionColor.getRGB());
			parent.line(xx2, yy2, xx2+50, yy2);
			parent.fill(proteinRectionColor.getRGB());
			parent.text("Individual protein reaction links", xx2+55,yy2+5);
			*/
			
			
			// For the survey
			float beginX =290;
			float beginY =500;
			
			/*
			parent.fill(0);
			parent.textAlign(PApplet.RIGHT);
			parent.textSize(50);
			parent.text("Reaction 1",beginX-15,beginY+17);
			parent.textAlign(PApplet.LEFT);
			parent.text("Reaction 2",beginX+800+15,beginY+17);
			
			
			
			for (float i=0;i<800;i++){
				float xx = beginX+i;
				parent.noStroke();
				parent.strokeWeight(18);
				parent.smooth();
				if (i>700){
					float stroke = (800-i)*18f/100;
					System.out.println(stroke);
					parent.strokeWeight(stroke);
				}	
				parent.stroke((700f-i)*255/800,(700f-i)*255/800,0);
				parent.strokeCap(0);
				parent.line(xx, beginY, xx+1, beginY);
				
			}
			*/
			// Draw buttons
			parent.strokeWeight(1);
			popupReactionOrder.draw(parent.width-98);
			popupCausality.draw(parent.width-198);
	}

	// Compute shortest path
	public void computeShortestPath(){
		ArrayList<Integer> proteinsDownStreamList = new ArrayList<Integer>();
		ArrayList<Integer> proteinsDownStreamList2 = new ArrayList<Integer>();
		ArrayList<Integer> levelDownStreamList = new ArrayList<Integer>();
		ArrayList<Integer> levelDownStreamList2 = new ArrayList<Integer>();
		ArrayList<Integer> processedList = new ArrayList<Integer>();
		
		if (bRect>=0 || bProteinL>=0 || bComplexL>=0){
			proteinsDownStreamList = new ArrayList<Integer>();
			proteinsDownStreamList2 = new ArrayList<Integer>();
			levelDownStreamList = new ArrayList<Integer>();
			levelDownStreamList2 = new ArrayList<Integer>();
			processedList = new ArrayList<Integer>();
			
			if (bRect>=0){
				 processedList.add(bRect);
				 listProteinDownStream(bRect,0,proteinsDownStreamList, levelDownStreamList, proteinsDownStreamList2, levelDownStreamList2, processedList);
			}
			else{
				for (int i=0;i<bRectListL.size();i++) {
					int r = bRectListL.get(i);
					  processedList.add(r);
					 listProteinDownStream(r,0,proteinsDownStreamList, levelDownStreamList, proteinsDownStreamList2, levelDownStreamList2, processedList);
				}	
			}
			
			// Draw shortest path levels ***************************
			int currentLevel = -10;
			if (simulationRectListLevel!=null && simulationRectListLevel.size()>0)
				currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
			
			for (int i=0;i<proteinsDownStreamList.size();i++){
				int p=proteinsDownStreamList.get(i);
				int level=levelDownStreamList.get(i);
				float y3 = iP[p].value;
				parent.fill(proteinRectionColor.getRGB());
				if (level==currentLevel+1){
					float sat = (parent.frameCount*20)%200;
					parent.fill(proteinRectionColor.getRed(), proteinRectionColor.getGreen(), proteinRectionColor.getBlue(),sat);
						
				}
				if (!main.ReactionFlow_1_1.isSmallMolecule(proteins[p])){
					parent.textSize(12);
					parent.textAlign(PApplet.RIGHT);
					parent.text(level,xR-18,y3);
				}
			}
			for (int i=0;i<proteinsDownStreamList2.size();i++){
				int p=proteinsDownStreamList2.get(i);
				int level=levelDownStreamList2.get(i);
				float y3 = iP[p].value;
				parent.fill(formComplexColor.getRGB());
				if (level==currentLevel+1){
					float sat = (parent.frameCount*20)%200;
					parent.fill(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
				}
				parent.textSize(11);
				parent.textAlign(PApplet.RIGHT);
				parent.text(level,xR-9,y3);
			}
		}
	}
		
	
	public void drawLoopFrom(int beginReaction, ArrayList<Integer> loopRectList){
		ArrayList<Integer> downstreamList = new ArrayList<Integer>();
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<Integer> levelDownstreamList = new ArrayList<Integer>();
		listReactionDownStream(beginReaction,0,downstreamList, parentList,levelDownstreamList);
		if (downstreamList.indexOf(beginReaction)>=0){
			int indexLastReaction = downstreamList.indexOf(beginReaction);
			int parent = parentList.get(indexLastReaction);
			ArrayList<Integer> a = new ArrayList<Integer>();
			while (parent!=beginReaction){
				a.add(parent);
				indexLastReaction = downstreamList.indexOf(parent);
				parent = parentList.get(indexLastReaction);
			}
			a.add(parent);
			for (int i=a.size()-1;i>=0;i--){
				int index = a.get(i);
				loopRectList.add(index);
			}
		}
		
		// Draw loop
		for (int i=0;i<loopRectList.size();i++){
			int r = loopRectList.get(i);
			int g = -1;
			if (i<loopRectList.size()-1)
				g = loopRectList.get(i+1);
			else{
				g = loopRectList.get(0);
			}
			drawArc2(r,g);
		}
		
	}
		
	public void listProteinDownStream(int r, int recursive, ArrayList<Integer> downstreamList,ArrayList<Integer> levelDownStreamList, ArrayList<Integer> downstreamList2,ArrayList<Integer> levelDownStreamList2, ArrayList<Integer> processedList){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		// List output protein in current reaction
		 for (int i3=0;i3<sRight1.length;i3++){
			  String name = main.ReactionFlow_1_1.getProteinName(sRight1[i3].toString());
			  if (name==null)
				  name = sRight1[i3].toString();
			  if (mapProteinRDFId_index.get(name)!=null){
				  int pId = mapProteinRDFId_index.get(name);
				  if (downstreamList.indexOf(pId)<0){
					  downstreamList.add(pId);
					  levelDownStreamList.add(recursive+1);
				  }	  
			  }	
			  else if (mapComplexRDFId_index.get(name)!=null){
					  int id = mapComplexRDFId_index.get(name);
					  ArrayList<String> components = proteinsInComplex[id];
					  for (int i=0;i<components.size();i++){
						  int pId = mapProteinRDFId_index.get(components.get(i));
						  if (downstreamList2.indexOf(pId)<0){
							  downstreamList2.add(pId);
							  levelDownStreamList2.add(recursive+1);
						  }	 
					  }
			  }
		 }
		for (int g=0;g<rectList.size();g++) {
			if(g==r || processedList.indexOf(g)>=0) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				processedList.add(g);
				listProteinDownStream(g, recursive+1,downstreamList,levelDownStreamList,downstreamList2,levelDownStreamList2,  processedList);
			}
		}
	}
	
	public ArrayList<Integer> getDirectUpstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sLeft = rectSelected.getLeft().toArray();
		
		// List current reaction
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sRight2 = rect2.getRight().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight2, sLeft);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	public ArrayList<Integer> getDirectDownstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight = rectSelected.getRight().toArray();
		// List current reaction
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight, sLeft2);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	
	public void listReactionDownStream(int r, int recursive, ArrayList<Integer> downstreamList, ArrayList<Integer> parentList,ArrayList<Integer> levelDownStreamList){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		
		// List current reaction
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				if (downstreamList.indexOf(g)<0){
					downstreamList.add(g);
					parentList.add(r);
					levelDownStreamList.add(recursive+1);
					listReactionDownStream(g, recursive+1,downstreamList, parentList, levelDownStreamList);
				}	
			}
		}
	}
	
	public void drawDownStreamReaction(int r, int recursive, ArrayList<Integer> processedList, float iProcess, float sat){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				if (iProcess>990){
					iS[r][g].target(1000);
					if (!buttonPause.s)   // Pause Button clicked
						iS[r][g].update();
					drawArc(r,g, iS[r][g], recursive, sat);
					if (recursive>=0){
						if (processedList.indexOf(g)<0){
							if (iS[r][g].value>=990){
								processedList.add(r);
								if (simulationRectList.indexOf(g)<0){
									simulationRectList.add(g);
									simulationRectListLevel.add(recursive+1);
									
								}
							}
							// Add intermediate elements
							if (iS[r][g].value>=10){
								for (int i=0;i<commonElements.size();i++){
									String str = commonElements.get(i);
									if (!interElements.contains(str)){
										interElements.add(str);
										interElementsLevel.add(recursive+1);	
									}
								}
							}	
						
						}
						int lastIndex=simulationRectList.get(simulationRectList.size()-1);
						if (iS[r][g].value<=1000 && lastIndex==r){
							SliderSimulation.transitionProcess = iS[r][g].value;
						}
						else{
							SliderSimulation.transitionProcess =0;
						}
					}
				}
			}
			
		}
	}
	
	public void resetIntegrators(){
		for (int r=0;r<rectList.size();r++) {
			iS1[r].set(0);
			iS1[r].target(0);
			iS2[r].set(50);
			iS2[r].target(50);
			iS3[r].set(0);
			iS3[r].target(0);
			iS4[r].set(0);
			iS4[r].target(0);
		}
		iDelete.target(0);
		iDelete.set(0);
	}
	
	public void resetCausality(){
		for (int r=0;r<rectList.size();r++) {
			for (int g=0;g<rectList.size();g++) {
				iS[r][g].set(0);
			}
		}	
	}
		 
	
	
	public void drawArc(int r, int g, Integrator inter, int level, float sat){
		float y1 = iY[r].value;
		float y2 = iY[g].value;
		float yy = (y1+y2)/2;
		
		
		float d = PApplet.abs(y2-y1);
		int numSec = (int) d;
		if (numSec==0) return;
		float beginAngle = -PApplet.PI/2;
		if (y1>y2)
			beginAngle = PApplet.PI/2;
		
		for (int k=numSec;k>0;k--){
			float percent = inter.value/1000;
			float endAngle = beginAngle+PApplet.PI/numSec;
			if ((float) k/numSec >=(1-percent)){
				parent.noFill();
				float sss = (float) k/numSec;
				sss = PApplet.pow(sss,0.75f);
				
				float sat2 = 255-225*sss;
				//if (sat<255)
				//	sat2=sat;
					
				/*float red = 255;
				float green = sss*255;
				float blue = 255-255*sss;*/
				
				float red = sss*255;
				float green = sss*255;
				float blue = 0;
				
				
				parent.stroke(red,green,blue,sat2);
				parent.strokeWeight(3);
				if (k<numSec*0.3f)
					parent.strokeWeight(3.5f*k/(numSec*0.3f));
				if(deleteReactionList.indexOf(r)>=0){
					red = 255;
					green = sss*100;
					blue = 0;
					if (red<0) red=0;
					if (green<0) green=0;
					if (blue<0) blue=0;
					parent.stroke(red,green,blue,sat2);		
				}	
				else if(selectedReactionsForCommonDownstream.indexOf(r)>=0){
					parent.stroke(0,150,250,sat2);
				}
				parent.arc(xRect, yy, d,d, beginAngle, endAngle);
				
				if(brushingReactionsForCommonDownstream.indexOf(r)>=0){
					float sat3 = parent.frameCount*22%256;
					parent.stroke(0,0,0,(sss)*sat3);
					parent.strokeWeight(3);
					parent.arc(xRect, yy, d,d, beginAngle, endAngle);
				}
				
			}
			beginAngle = endAngle;
			parent.strokeWeight(1);
		}
	}
	
	public void drawArc2(int r, int g){
		float y1 = iY[r].value;
		float y2 = iY[g].value;
		float yy = (y1+y2)/2;
		
		float d = PApplet.abs(y2-y1);
		int numSec = (int) d;
		if (numSec==0) return;
		float beginAngle = -PApplet.PI/2;
		if (y1>y2)
			beginAngle = PApplet.PI/2;
		
		for (int k=0;k<=numSec;k++){
			float percent = 1;
			float endAngle = beginAngle+PApplet.PI/numSec;
			if ((float) k/numSec >=(1-percent)){
				parent.noFill();
				float sss = (float) k/numSec;
				float sat2 = 200*sss;
				parent.stroke(255-sss*255,255-sss*255,0, sat2);
				parent.strokeWeight(3);
				if (k>numSec*0.7f)
					parent.strokeWeight(3f*(numSec-k)/(numSec*0.3f));
				
				parent.arc(xRect, yy, d,d, beginAngle, endAngle);
			}
			beginAngle = endAngle;
		}
	}
	
	
	public ArrayList<String> compareInputOutput(Object[] a, Object[] b){
		ArrayList<String> results = new ArrayList<String>();
		for (int i=0; i<a.length;i++){
			String ref1 = a[i].toString();
			if (mapProteinRDFId.get(ref1)!=null){
				String proteinName1 = mapProteinRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String proteinName2 = mapProteinRDFId.get(ref2);
					if (proteinName2!=null && proteinName1.equals(proteinName2) 
							&& !mapSmallMoleculeRDFId.containsValue(proteinName1)){
							 results.add(proteinName1);
					}	
				}
			}
			else if (mapComplexRDFId_index.get(ref1)!=null){
				int index1 = mapComplexRDFId_index.get(ref1);
				String cName1 = complexList.get(index1).getDisplayName();
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					if (mapComplexRDFId_index.get(ref2)==null)
						continue;
					int index2 = mapComplexRDFId_index.get(ref2);
					String cName2 = complexList.get(index2).getDisplayName();
					if (cName1.equals(cName2)){
						 results.add(cName1);   
					}	
				}
			}
			
		}
		return results;
	}
		
	public void drawRelationship(WordCloud wc, int[][] rel, Color color, int minCount){
		int max = 0;
		for (int i=0;i<numTop;i++){
			for (int j=i+1;j<numTop;j++){
				if (rel[i][j]>max)
					max = rel[i][j];
			}
		}	
		
		float maxWeight = max;
		if (max<=2){
			maxWeight = 3;
		}
		
		int brushing = wc.b;
		 for (int i=0;i<numTop;i++){
			if (i==brushing) continue;  // skip drawing the brushing relations
			float y1 = wc.words[i].y-wc.words[i].font_size/3f;
			for (int j=i+1;j<numTop;j++){
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				// Filter relationship ************************************************
				if (rel[i][j]<minCount) continue;
				
				float wei = PApplet.map(PApplet.sqrt(rel[i][j]), 0, PApplet.sqrt(maxWeight), 0, 100);
				parent.stroke(color.getRed(),color.getGreen(),color.getBlue(),wei);
				parent.strokeWeight(wei/25);
				parent.arc(xx-2, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
			}
		}
		// Draw relationship of brushing term
		 if (brushing>=0){
		 	float y1 = wc.words[brushing].y-wc.words[brushing].font_size/3f;
			for (int j=0;j<numTop;j++){
				if (j==brushing) continue;
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				if (j>brushing && rel[brushing][j]>0){
					// Filter relationship ************************************************
					float wei = PApplet.map(PApplet.sqrt(rel[brushing][j]), 0, PApplet.sqrt(maxWeight), 0, 100);
					parent.stroke(255,100,0,wei+155);
					parent.strokeWeight(wei/25);
					parent.arc(xx-2, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
				}
				else if (j<brushing && rel[j][brushing]>0){
					// Filter relationship ************************************************
					float wei = PApplet.map(PApplet.sqrt(rel[j][brushing]), 0, PApplet.sqrt(maxWeight), 0, 100);
					parent.stroke(255,100,0,wei+155);
					parent.strokeWeight(wei/25);
					parent.arc(xx-2, yy, y1-y2,y1-y2, PApplet.PI/2, 3*PApplet.PI/2);
				}
			}
		 } 
	}
	
	public  void drawGradientLine(float x1, float y1, float x2, float y2, Color color, float sat) {
		float dis = PApplet.dist(x1, y1, x2, y2);
		int numPoints= (int) (dis/1.1f);
		float stepX = PApplet.abs(x2-x1)/numPoints;
		
		//parent.noStroke();
		parent.stroke(color.getRed(), color.getGreen(), color.getBlue(), 20);
		parent.line(x1, y1, x2, y2);
		
		/*
		for (float i = 0; i <= numPoints/3; i++) {
			  float x3 = x1+i*stepX;	
			  float x4 = x2-i*stepX;	
			  float y3 = (x3-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  float y4 = (x4-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  //float dis1 = PApplet.min(x3-x1, x2-x3);
			  float alpha = PApplet.map(PApplet.pow(i,0.5f), 0, PApplet.pow(numPoints/3,0.5f), sat*0.8f, 0);
			  if (alpha>=1){
				   Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(),(int) alpha);
				   parent.fill(c.getRGB());
				   parent.ellipse(x3,y3,1.25f,1.25f);
				   parent.ellipse(x4,y4,1.25f,1.25f);
			  } 
		}*/
	} 
	
	
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  if (main.ReactionFlow_1_1.getProteinName(s[i3].toString())!=null){
				  String name = main.ReactionFlow_1_1.getProteinName(s[i3].toString());
				   a.add(mapProteinRDFId_index.get(name));
			  }
			  else  if (mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  a.add(mapProteinRDFId_index.get(components.get(k)));
					  }	  
					  else{
						  System.out.println("getProteinsInOneSideOfReaction: -----SOMETHING WRONG");
					//	  int reverseIndex = -1-unidentifiedList.indexOf(components.get(k));
					//	  a.add(reverseIndex);
					  }
				  }
			  }
			  else if (unidentifiedList.contains(s[i3].toString())){
				   // int reverseIndex = -1-unidentifiedList.indexOf(s[i3].toString());
					//a.add(reverseIndex);
			  }
			  else{
				  System.out.println("getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]+"-----SOMETHING WRONG");
			 } 
		  }
		return a;
	}
	
	
		
	
	public void drawProteinLeft(int p, float sat) {
		float y3 = iP[p].value;
		float textSize = PApplet.map(hProtein, 0, maxH, 5, 12);
		parent.textSize(textSize);
		String name = proteins[p];
		Color c =  new Color(0,0,0);
		if (main.ReactionFlow_1_1.isSmallMolecule(proteins[p])){
			c = smallMoleculeColor;
		}
		else if (unidentifiedList.contains(proteins[p])){
			c = unidentifiedElementColor;
			String[] pieces = name.split("#");
			if (pieces.length>1)
				name = pieces[pieces.length-1];
			else{
				pieces = name.split("/");
				if (pieces.length>1)
					name = pieces[pieces.length-1];
			}
		}
		
		if (sat==255 && textSize<10){
			textSize = 10;
			parent.textSize(textSize);
		}
		if (sat==255 && p==bProteinL){
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(), (parent.frameCount*22)%255);
		}	
		else
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(),sat);
		parent.textAlign(PApplet.RIGHT);
		if (name!=null)
			parent.text(name, xL,y3);
		
		if (PopupCausality.s==1 && p==bProteinL){  // Knock out one protein
			parent.stroke(250,0,0);
			float ww = parent.textWidth(name);
			parent.line(xL-ww-8, y3-4, xL+5, y3-4);
		}
		else if (PopupCausality.s==4 && selectedProteinForCommonDownstream.contains(p)){  // Common downstream
			parent.fill(0,200,200,200);
			parent.noStroke();
			parent.rect(xL-parent.textWidth(name)-5, y3-textSize, parent.textWidth(name)+10, textSize+2);
			
			parent.fill(c.getRGB());
			parent.textAlign(PApplet.RIGHT);
			parent.text(name, xL,y3);
		}
		// Draw connecting nodes for simulations
		float rReaction = 0.8f*(parent.width/4f);   // The width of reaction = parent.width/2
		int numDash = 50;
		float gap = 2.5f;
		float w4 = rReaction/numDash-gap; // Width of a dash   
		for (int i=0;i<interElements.size();i++){
			int curLevel =  interElementsLevel.get(interElementsLevel.size()-1);
			int level = interElementsLevel.get(i);
			if (curLevel==level){
				String ref = interElements.get(i);
				String interProteinName = main.ReactionFlow_1_1.getProteinName(ref);
				if(interProteinName!=null && interProteinName.equals(name)){
					float x4 = xL;
					float max = 0;
					int currentReact = ReactionView.simulationRectList.get(ReactionView.simulationRectList.size()-1);
					if (10<=SliderSimulation.transitionProcess)
						max = (SliderSimulation.transitionProcess/1000)*255;
					else 
						max = ((1000-iS2[currentReact].value)/1001)*255;
					
					for (int k=0;k<numDash;k++){
						parent.stroke(0,max-k*max/numDash);
						parent.strokeWeight(1.5f-k*1.5f/numDash);
						parent.line(x4, y3-4, x4+w4, y3-4);
						x4+=w4+gap;
					}
				}
				parent.strokeWeight(1);
			}
		}
	}
	
	public void drawProteinRight(int p, float sat) {
		float y3 = iP[p].value;
		float textSize = PApplet.map(hProtein, 0, maxH, 5, 12);
		parent.textSize(textSize);
		String name = proteins[p];
		Color c =  new Color(0,0,0);
		if (main.ReactionFlow_1_1.isSmallMolecule(proteins[p])){
			c = smallMoleculeColor;
		}
		else if (unidentifiedList.contains(proteins[p])){
			c = unidentifiedElementColor;
			String[] pieces = name.split("#");
			if (pieces.length>1)
				name = pieces[pieces.length-1];
			else{
				pieces = name.split("/");
				if (pieces.length>1)
					name = pieces[pieces.length-1];
			}
		}
		
		if (sat>=255 && textSize<10)
			parent.textSize(10);
		if (sat==255 && p==bProteinR)
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(), (parent.frameCount*22)%255);
		else
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(),sat);
		
		parent.textAlign(PApplet.LEFT);
		if (name!=null)
			parent.text(name, xR,y3);
		
		
		// Draw connecting nodes for simulations
		float rReaction = 0.8f*(parent.width/4f);   // The width of reaction = parent.width/2
		int numDash = 50;
		float gap = 2.5f;
		float w4 = rReaction/numDash-gap; // Width of a dash   
		for (int i=0;i<interElements.size();i++){
			int curLevel =  interElementsLevel.get(interElementsLevel.size()-1);
			int level = interElementsLevel.get(i);
			if (curLevel==level){
				String ref = interElements.get(i);
				String interProteinName = main.ReactionFlow_1_1.getProteinName(ref);
				if(interProteinName!=null && interProteinName.equals(name)){
					float x4 = xR;
					// Compute the max value;
					int currentReact = ReactionView.simulationRectList.get(ReactionView.simulationRectList.size()-1);
					float max = 0;
					if (10<=SliderSimulation.transitionProcess)
						max = (SliderSimulation.transitionProcess/1000)*255;
					else 
						max = ((1000-iS2[currentReact].value)/1001)*255;
					
					for (int k=0;k<numDash;k++){
						parent.stroke(0,max-k*max/numDash);
						parent.strokeWeight(1.5f-k*1.5f/numDash);
						parent.line(x4, y3-textSize/3, x4-w4, y3-textSize/3);
						x4-=(w4+gap);
					}
				}
				parent.strokeWeight(1);
			}	
		}
	}
		
	public void drawReactionNode(Map.Entry<BiochemicalReaction, Integer> entry, int i, float sat) {
		float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 1, maxH/3);
		parent.noStroke();
		parent.fill(0,sat);
		
		if (PopupCausality.s==4){
			parent.fill(0,sat);
			parent.ellipse(xRect,iY[i].value, r, r);
			
			float sat3 = parent.frameCount*22%256;
			if(selectedReactionsForCommonDownstream.indexOf(i)>=0){
				parent.strokeWeight(1);
				parent.stroke(0,200);
				parent.fill(0,200,200);
				parent.ellipse(xRect,iY[i].value, r, r);
			}
			if(brushingReactionsForCommonDownstream.indexOf(i)>=0){
				parent.fill(0,sat3);
				parent.ellipse(xRect,iY[i].value, r, r);
			}
			
			return;
		}
		
		if (!textbox1.searchText.equals("")){
			if (sRectListByText.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value, r, r);
			}
		}	
		else if (bProteinL>=0 || bComplexL>=0){
			if (bRectListL.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value, r, r);
			}
		}
		else if (bProteinR>=0 || bComplexR>=0 ){
			if (bRectListR.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value, r, r);
			}
		}
		else 
			parent.ellipse(xRect,iY[i].value, r, r);
		
		//parent.text(i,xRect+5,iY[i].value+5); //draw reaction ID
		
		// Draw brushing reaction name
		String rectName = entry.getKey().getDisplayName();
		if (i==bRect || (!textbox1.searchText.equals("") && sRectListByText.indexOf(i)>=0) || (bRectListL.size()>0 && bRectListL.indexOf(i)>=0) || (bRectListR.size()>0 && bRectListR.indexOf(i)>=0)){
			parent.fill(0);
			parent.ellipse(xRect,iY[i].value, r, r);
			
			parent.fill(0);
			parent.textSize(11);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[i].value-iH[i].value*1/3;
			if (check5.s && rectName!=null)
				parent.text(rectName,xRect,y3+4);
		}
	}
	
	public void drawReactionNodeSimulation(Map.Entry<BiochemicalReaction, Integer> entry, int r) {
		float radus = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
		parent.noStroke();
		parent.fill(0);
		
		int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
		int rectIndex = simulationRectList.indexOf(r);
		int rLevel = simulationRectListLevel.get(rectIndex);
		if (rLevel==currentLevel && iS4[r].value<990){
			float sat = (parent.frameCount*20)%200;
			parent .fill(sat,0,0);
			float aditionalR = sat/50f;
			parent.ellipse(xRect,iY[r].value, radus+ aditionalR, radus+aditionalR);
		}
		else{	
			parent.ellipse(xRect,iY[r].value, radus, radus);
		}
		// Draw brushing reaction name
		if (check5.s || (rLevel==currentLevel &&  iS4[r].value<990)){
			parent.fill(0);
			parent.textSize(11);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[r].value-iH[r].value*1/3;
			String rectName = entry.getKey().getDisplayName();
			parent.text(rectName,xRect,y3+24);
		}
	}
	
	public float getReactionSaturation(int r){
		  int levelDif = 0;
		  if (simulationRectList.indexOf(r)>=0){
			  int levelIndex = simulationRectList.indexOf(r);
			  levelDif = simulationRectListLevel.get(simulationRectListLevel.size()-1)-simulationRectListLevel.get(levelIndex);
		  }
		  
		  if (levelDif==0){
			  float sat = 255*(1000-SliderSimulation.transitionProcess)/1000;
			  if (sat<level1SatSimulation)
				  sat = level1SatSimulation;
			  return sat;  
		  }
		  else{
			  float sat = level1SatSimulation-(levelDif-1)*stepSatSimulation;
			  if (sat<minSatSimulation)
				  sat = minSatSimulation;
			  return sat;
		  }
	}	  
		
		 
	// draw Reactions links
	public void drawReactionLink(BiochemicalReaction rect, int i2, float xL, float xL2, float xRect, float xR, float xR2, float sat) {
		Object[] sLeft = rect.getLeft().toArray();
		
		float newSatForSimulation = getReactionSaturation(i2);
		boolean isContainedComplexL =false; // checking if there is a complex;
		float yReact = iY[i2].value;
		  for (int i3=0;i3<sLeft.length;i3++){
			  String name = main.ReactionFlow_1_1.getProteinName(sLeft[i3].toString());
			  if (name==null)
				  name = sLeft[i3].toString();

			  if (mapProteinRDFId_index.get(name)!=null){
				  float y5 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  if (check11.s && main.ReactionFlow_1_1.isSmallMolecule(name))
					  drawGradientLine(xL, y5, xRect, yReact, smallMoleculeColor, sat);
				  else if (check15.s && !main.ReactionFlow_1_1.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xL, y5, xRect, yReact, proteinRectionColor, sat);
				  }
				  else {
					  if (sat==255){ // Draw simulation lines
						  if (iS1[i2].value>=990){
							  iS2[i2].target(1000);
						  }
						  else{
							  iS2[i2].set(50);
						  }
						  if (simulationRectList.size()==0)
							  iS2[i2].set(1000);
						  
						  float percent = iS2[i2].value/1000;
						  float xDel = (xRect-xL)*percent;
						  float yDel = (yReact-y5)*percent;
						  
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),newSatForSimulation);
						  if (main.ReactionFlow_1_1.isSmallMolecule(name)){
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),newSatForSimulation);
						  }
						  parent.line(xL, y5, xL+xDel, y5+yDel);
					  }
					  else {
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
					  	  if (main.ReactionFlow_1_1.isSmallMolecule(name)){
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						  }
						  parent.line(xL, y5, xRect, yReact);
					  }	  
				  }
			  }	  
			  // Complex LEFT
			  else if (mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
				  int id = mapComplexRDFId_index.get(sLeft[i3].toString());
				  isContainedComplexL = drawComplexLeft(i2, id, yReact, sat, newSatForSimulation);
			  }
			  else if (unidentifiedList.contains(sLeft[i3].toString())){
				  if (check12.s && sat==200)
					  drawGradientLine(xL, yUFO, xRect, yReact, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
					  parent.line(xL, yUFO, xRect, yReact);
				  }
			  }
			  else{
				//System.out.println("drawReactionLink Left: CAN NOT FIND ="+sLeft[i3]);
			  }
		  }
		  if (!isContainedComplexL)
			  iS1[i2].target(1000);
		  
		  boolean isContainedComplexR =false; // checking if there is a complex;
		  Object[] sRight = rect.getRight().toArray();
		  for (int i3=0;i3<sRight.length;i3++){
			  String name = main.ReactionFlow_1_1.getProteinName(sRight[i3].toString());
			  if (name==null)
				  name = sRight[i3].toString();
			  if (mapProteinRDFId_index.get(name)!=null){
				  float y6 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  if (check11.s && main.ReactionFlow_1_1.isSmallMolecule(name))
					  drawGradientLine(xRect, yReact, xR, y6, smallMoleculeColor, sat);
				  else if (check15.s && !main.ReactionFlow_1_1.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xRect, yReact, xR, y6, proteinRectionColor, sat);
				  }
				  else{
					   if (sat==255){ // Draw simulation lines
						  if (iS2[i2].value>=990){
							  iS3[i2].target(1000);
						  }
						  else{
							  iS3[i2].set(0);
						  }
						  if (simulationRectList.size()==0)
							  iS3[i2].set(1000);
						  
						  float percent = iS3[i2].value/1000;
						  float xDel = (xR-xRect)*percent;
						  float yDel = (y6-yReact)*percent;
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),newSatForSimulation);
						  if (main.ReactionFlow_1_1.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),newSatForSimulation);
						  parent.line(xRect, yReact, xRect+xDel, yReact+yDel);
						  
					  }
					  else{
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
						  if (main.ReactionFlow_1_1.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						 parent.line(xRect, yReact,xR, y6);
					  }	  
				  }	  
			  }
			  else if (mapComplexRDFId_index.get(sRight[i3].toString())!=null){
				  int id = mapComplexRDFId_index.get(sRight[i3].toString());
				  isContainedComplexR = drawComplexRight(i2, id, yReact, sat, newSatForSimulation);
			  }
			  else if (unidentifiedList.contains(sRight[i3].toString())){
				  if (check12.s && sat==200)
					  drawGradientLine(xRect, yReact, xR, yUFO, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),newSatForSimulation);
					  parent.line(xRect, yReact, xR, yUFO);
				  }
				  
			  }
			  else{
				//	 System.out.println("drawReactionLink Right: CAN NOT FIND ="+sRight[i3]);
			  }
		  }
		   if (!isContainedComplexR && iS3[i2].value>=990){
			 iS4[i2].set(1000);
		  }
	 }
	
	
	public boolean drawComplexLeft(int r, int id, float yReact, float sat, float newSatForSimulation) {
		boolean result = false;
		  ArrayList<String> components = proteinsInComplex[id];
		  yComplexes[id].update();
		  float yL2 = yComplexes[id].value;
		  if (processedComplexLeft.indexOf(id)<0 || sat==255){  // if not drawn yet
			  if (processedComplexLeft.indexOf(id)<0)
					  processedComplexLeft.add(id);
			  for (int k=0;k<components.size();k++){
				  result = true;
				  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
				  if (mapProteinRDFId_index.get(components.get(k))!=null){
					  float y4 = iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
					  if (check13.s && sat==200)
						  drawGradientLine(xL, y4, xL2, yL2, formComplexColor, sat);
					  else{
						  if (sat==255){ // Draw simulation lines
							  iS1[r].target(1000);
							  if (simulationRectList.size()==0)
								  iS1[r].set(1000);
							  float percent = iS1[r].value/1000;
							  float xDel = (xL2-xL)*percent;
							  float yDel = (yL2-y4)*percent;
							  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),newSatForSimulation);
							  parent.line(xL, y4, xL+xDel, y4+yDel);
						  }
						  else{
							  parent.line(xL, y4, xL2, yL2);
						  }	  
					  }	  
				  }	
				  else{
					  if (check12.s && sat==200)
						  drawGradientLine(xL, yUFO, xL2, yL2, unidentifiedElementColor, sat);
					  else{
						  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
						  parent.line(xL, yUFO, xL2, yL2);
					  }
				  }
			  }
			  
			  // Draw complex node
			  parent.noStroke();
			  if ( iS1[r].value>=990 && simulationRectList.size()>0)
				  parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
			  else
				  parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
			  
			  // In case of common downstream, make the node flashing
			  if (PopupCausality.s==4 ){
				  if (selectedComplexForCommonDownstream.contains(id)){
					  parent.fill(0,200,200);
					  parent.rect(xL2-rComplexes[id],yL2-rComplexes[id]*0.75f,rComplexes[id]*2,rComplexes[id]*1.5f);
					  parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue());
				  }	
				  if (id == bComplexL){
					  float sat3 = parent.frameCount*22%256;
					  parent.fill(0,sat3);
				  }
			  }
			  
			  parent. pushMatrix();
			  parent.translate(xL2, yL2);
			  if (complexListLeft.contains(id) && !complexListRight.contains(id))
				  triangle2(0,0,rComplexes[id]/2+1);
			  else
				  polygon(0, 0, rComplexes[id]/2+1, 4); 
			  parent.popMatrix();
			  
			  
			  if (check5.s && (sat==255 || (PopupCausality.s==4 && selectedComplexForCommonDownstream.contains(id)))){
				  parent.textAlign(PApplet.CENTER);
				  parent.textSize(12);
				  parent.text(complexList.get(id).getDisplayName(),xL2,yL2-5);
			  }
		  }
		  if (check14.s && sat==200)
			  drawGradientLine(xL2, yL2, xRect, yReact, complexRectionColor, sat);
		  else{	
			  if (sat==255){ // Draw simulation lines
				  if (iS1[r].value>=990){
					  iS2[r].target(1000);
				  }
				  else{
					  iS2[r].set(0);
				  }
				  if (simulationRectList.size()==0)
					  iS2[r].set(1000);
				 
				  float percent = iS2[r].value/1000;
				  float xDel = (xRect-xL2)*percent;
				  float yDel = (yReact-yL2)*percent;
				  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
				  parent.line(xL2, yL2, xL2+xDel, yL2+yDel);
			  }
			  else{
				  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
				  parent.line(xL2, yL2, xRect, yReact);
			  }	  
		  }
		  return result;
	}
	public boolean drawComplexRight(int r, int id, float yReact, float sat, float newSatForSimulation) {
		 boolean result =false;
		 ArrayList<String> components = proteinsInComplex[id];
		  yComplexes[id].update();
		  float yR2 = yComplexes[id].value;
		 
		  if (check14.s && sat==200)
			  drawGradientLine(xRect, yReact, xR2, yR2, complexRectionColor, sat);
		  else{	  
			  if (sat==255){ // Draw simulation lines
				  if (iS2[r].value>=990){
					  iS3[r].target(1000);
				  }
				  else{
					  iS3[r].set(0);
				  }
				  if (simulationRectList.size()==0)
					  iS3[r].set(1000);
				 
				  float percent = iS3[r].value/1000;
				  float xDel = (xR2-xRect)*percent;
				  float yDel = (yR2-yReact)*percent;
				  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
					 parent.line(xRect, yReact, xRect+xDel, yReact+yDel);
				  
			  }
			  else{
				  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
				 parent.line(xRect, yReact, xR2, yR2);
			  }	 
		  }
		  if (processedComplexRight.indexOf(id)<0 || sat==255){  // if not drawn yet
			  if (processedComplexRight.indexOf(id)<0)
				  processedComplexRight.add(id);
			
			  for (int k=0;k<components.size();k++){
				  result =true;
				  if (mapProteinRDFId_index.get(components.get(k))!=null){
					  float y4=iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
					  if (check13.s && sat==200)
						  drawGradientLine(xR2, yR2, xR, y4, formComplexColor, sat);
					  else{
						  if (sat==255){ // Draw simulation lines
							  if (iS3[r].value>=990){
								  iS4[r].target(1000);
							  }
							  else{
								  iS4[r].set(0);
							  }
							  if (simulationRectList.size()==0)
								  iS4[r].set(1000);
							 
							  float percent = iS4[r].value/1000;
							  float xDel = (xR-xR2)*percent;
							  float yDel = (y4-yR2)*percent;
							  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),newSatForSimulation);
							  parent.line(xR2, yR2, xR2+xDel, yR2+yDel);
							  
						  }
						  else{
							  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
							  parent.line(xR2, yR2, xR, y4);
						  }	  
					  }	  
				  }
				  else{
					  if (check12.s && sat==200)
						  drawGradientLine(xR2, yR2, xR, yUFO, unidentifiedElementColor, sat);
					  else{
						  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
						  parent.line(xR2, yR2, xR, yUFO);
					  }
				  }
			  }
			  // Draw complex node
			  parent.noStroke();
			  if (iS3[r].value>=990 && simulationRectList.size()>0)
					parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
			  else
					parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
							
			  parent. pushMatrix();
			  parent.translate(xR2, yR2);
			  if (complexListRight.contains(id) && !complexListLeft.contains(id))
				  triangle2(0,0,rComplexes[id]/2+1);
			  else
				  polygon(0, 0, rComplexes[id]/2+1, 4); 
			  parent.popMatrix();
			  
			  
			  if (sat==255 && check5.s){
				  parent.textAlign(PApplet.CENTER);
				  parent.textSize(12);
				  parent.text(complexList.get(id).getDisplayName(),xR2,yR2-5);
			  }
			  
			  // Draw connecting Complex for simulations
				float rReaction = 0.75f*(parent.width/6f);   // The width of reaction = parent.width/2
				float w4 = 5; // Width of a dash   
				int numDash = (int) (rReaction/w4);
				parent.noStroke();
				for (int i=0;i<interElements.size();i++){
					int curLevel =  interElementsLevel.get(interElementsLevel.size()-1);
					int level = interElementsLevel.get(i);
					if (curLevel==level){
						String ref = interElements.get(i);
						if (mapComplexRDFId_index.get(ref)!=null){
							  int complexId = mapComplexRDFId_index.get(ref);
							  if(complexId==id){
									float x1 = xL2;
									float x2 = xR2;
									float max = 0;
									if (10<=SliderSimulation.transitionProcess){
										max = (SliderSimulation.transitionProcess/1000)*255;
									}	
									else {//if (SliderSimulation.transitionProcess==0){
										int currentReact = ReactionView.simulationRectList.get(ReactionView.simulationRectList.size()-1);
										max = ((1000-iS2[currentReact].value)/1001)*255;
									}
									
									for (int k=0;k<numDash;k++){
										parent.fill(0,max-k*max/numDash);
										x1+=w4;
										x2-=w4;
										parent.ellipse(x1, yR2, 3, 3);
										parent.ellipse(x2, yR2, 3, 3);
									}
							  }
						 }
					}
				}
				parent.strokeWeight(1f);
		  }
		return result;
	}
		
		
	public void triangle2(float x, float y, float radius) {
		  parent.beginShape();
		  parent.vertex(x-radius*0.5f, y-radius*1.2f);
		  parent.vertex(x+radius, y);
		  parent.vertex(x-radius*0.5f, y+radius*1.2f);
		  parent.endShape(PApplet.CLOSE);
	}
	
	public void polygon(float x, float y, float radius, int npoints) {
		  float angle = 2*PApplet.PI / npoints;
		  parent.beginShape();
		  for (float a = 0; a <  2*PApplet.PI; a += angle) {
		    float sx = x +  PApplet.cos(a) * radius;
		    float sy = y + PApplet.sin(a) * radius;
		    parent.vertex(sx, sy);
		  }
		  parent.endShape(PApplet.CLOSE);
	}
	
	public ArrayList<Integer> getDirectReactionsOfProteinLeft(int protein) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] sLeft = rect.getLeft().toArray();
			for (int i3=0;i3<sLeft.length;i3++){
				 String name = main.ReactionFlow_1_1.getProteinName(sLeft[i3].toString());
				 if (name==null)
					  name = sLeft[i3].toString();
				 if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==protein){
					  if (!a.contains(r)){
					    a.add(r);
					  }	
				 }
				 /*
				  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
					  for (int k=0;k<components.size();k++){
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  if(mapProteinRDFId_index.get(components.get(k))==bProteinL){
								  sRectListL.add(r);
								  break;
							  }	  
						  }		
					  }
						  
				  }*/
			}	  
		}
		return a;
	}
	
	public ArrayList<Integer> getDirectReactionsOfProteinRight(int protein) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] sRight = rect.getRight().toArray();
			for (int i3=0;i3<sRight.length;i3++){
				 String name = main.ReactionFlow_1_1.getProteinName(sRight[i3].toString());
				  if (name==null)
					  name = sRight[i3].toString();
				  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinR){
					  if (!a.contains(r))
						  a.add(r);
				  }
				  /*
				  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
					  for (int k=0;k<components.size();k++){
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  if(mapProteinRDFId_index.get(components.get(k))==bProteinR){
								  sRectListR.add(r);
								  break;
							  }	  
						  }		
					  }
						  
				  }*/
			}	  
		}
		return a;
	}
		
	public void mouseMoved() {
		if (yComplexes==null) return;
		// Compute brushing complexes ************************************************************************
		bRectListL = new ArrayList<Integer>();
		bRectListR = new ArrayList<Integer>();
		int bComplexLold = bComplexL;
		bComplexL=-1;
		for (int c=0;c<yComplexes.length;c++){
			if (!complexListLeft.contains(c)) continue;
			if (PApplet.dist(xL2,yComplexes[c].value, parent.mouseX, parent.mouseY)<=rComplexes[c]){
				bComplexL = c;
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sLeft = rect.getLeft().toArray();
					for (int i3=0;i3<sLeft.length;i3++){
						  if (mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
							  int id = mapComplexRDFId_index.get(sLeft[i3].toString());
							  if (id==c && !bRectListL.contains(r))
								  bRectListL.add(r);
						  }	
					}	  
				 }
				break; // Only allow to brushing 1 complex
			 }
		}
		if (bComplexLold!=bComplexL && ReactionView.simulationRectList.size()==0){
			resetIntegrators();
		}
		int bComplexRold = bComplexR;
		bComplexR=-1;
		for (int c=0;c<yComplexes.length;c++){
			if (!complexListRight.contains(c)) continue;
			if (PApplet.dist(xR2,yComplexes[c].value, parent.mouseX, parent.mouseY)<=rComplexes[c]){
				bComplexR = c;
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sRight = rect.getRight().toArray();
					for (int i3=0;i3<sRight.length;i3++){
						  if (mapComplexRDFId_index.get(sRight[i3].toString())!=null){
							  int id = mapComplexRDFId_index.get(sRight[i3].toString());
							  if (id==c && !bRectListR.contains(r))
								  bRectListR.add(r);
						  }	
					}	  
				 }
				break; // Only allow to brushing 1 complex
			 }
		}
		if (bComplexRold!=bComplexR && ReactionView.simulationRectList.size()==0){
			resetIntegrators();
		}
		
		// Brushing proteins *******************************************************************
		int bProteinLold = bProteinL;
		bProteinL =-1;
		if (bRectListL.size()==0){
			for (int p=0; p<proteins.length;p++){
				if (xL-80<=parent.mouseX && parent.mouseX<= xL &&
						iP[p].value-hProtein*2/3<=parent.mouseY && parent.mouseY<=iP[p].value+hProtein*1/3){
					bProteinL =p;
					break;
				}	
			}
			bRectListL = getDirectReactionsOfProteinLeft(bProteinL);
			
			if (bProteinLold!=bProteinL && ReactionView.simulationRectList.size()==0){
				resetIntegrators();
			}
		}
		int bProteinRold = bProteinR;
		bProteinR =-1;
		if (bRectListR.size()==0){
	    	for (int p=0; p<proteins.length;p++){
				if (xR<=parent.mouseX && parent.mouseX<= xR+80 &&
						iP[p].value-hProtein<=parent.mouseY && parent.mouseY<=iP[p].value){
					bProteinR =p;
					break;
				}	
			}
			bRectListR = getDirectReactionsOfProteinRight(bProteinR);
			
			if (bProteinRold!=bProteinR && ReactionView.simulationRectList.size()==0){
				resetIntegrators();
			}
		}
		
		// Obtain the proteins on the left and right *****************************************************************************************************
		bProteinLeft =  new ArrayList<Integer>();
		bProteinRight =  new ArrayList<Integer>();
		
		if (bRect>=0){
			BiochemicalReaction rect = rectList.get(bRect);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			bProteinLeft = getProteinsInOneSideOfReaction(aLeft);
			bProteinRight = getProteinsInOneSideOfReaction(aRight);
		}	
		else if (!textbox1.searchText.equals("")){
			for (int r=0;r<sRectListByText.size();r++) {
				BiochemicalReaction rect = rectList.get(sRectListByText.get(r));
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (bProteinLeft.indexOf(ind)<0)
						bProteinLeft.add(ind);
				}
				ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (bProteinRight.indexOf(ind)<0)
						bProteinRight.add(ind);
				}
			}
		}
		else if (bRectListL.size()>0){
			for (int r=0;r<bRectListL.size();r++) {
				BiochemicalReaction rect = rectList.get(bRectListL.get(r));
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (bProteinLeft.indexOf(ind)<0)
						bProteinLeft.add(ind);
				}
				ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (bProteinRight.indexOf(ind)<0)
						bProteinRight.add(ind);
				}
			}
		}
		else if (bRectListR.size()>0){
			for (int r=0;r<bRectListR.size();r++) {
				BiochemicalReaction rect = rectList.get(bRectListR.get(r));
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (bProteinLeft.indexOf(ind)<0)
						bProteinLeft.add(ind);
				}
				ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (bProteinRight.indexOf(ind)<0)
						bProteinRight.add(ind);
				}
			}
		}
		
		// Retrieve downstream reactions *****************************************************************************************************
		if (PopupCausality.s == 4 && (bProteinL>=0 || bComplexL>=0)){
			if (bProteinL>=0)
			brushingReactionsForCommonDownstream = getDownstreamOfAprotein(bProteinL);
			else if (bComplexL>=0)
				brushingReactionsForCommonDownstream = getDownstreamOfAcomplex(bComplexL);
		}	
		else 
			brushingReactionsForCommonDownstream = new ArrayList<Integer>();
	}
	
	// Check brushing reaction when there is no simulations
	public void checkReactionBrushing() {
		int oldRect = bRect;
		bRect =-100;
		for (int i=0; i<rectList.size(); i++){
			if (xRect-50<=parent.mouseX && parent.mouseX<=xRect+50 && 
					iY[i].value-iH[i].value/2<=parent.mouseY && parent.mouseY<=iY[i].value+iH[i].value/2){
				if ((textbox1.searchText.equals("") || (!textbox1.searchText.equals("") && sRectListByText.indexOf(i)>=0)) && simulationRectList.size()==0) {
					bRect =i;
					if (oldRect!=bRect){
						resetIntegrators();
					}	
					return;
				}
			}	
		}
	}
	
	public void mousePressed() {
		if (slider.b)
			slider.mousePresses();
		else if (slider2.b)
			slider2.mousePresses();
	}
	public void mouseReleased() {
		slider.mouseReleased();
		slider2.mouseReleased();
	}
		
	public void mouseDragged() {
		if (slider.b)
			slider.mouseDragged();
		else if (slider2.b)
			slider2.mouseDragged();
	}
		
	
		
	public void mouseClicked() {
		SliderSimulation.transitionProcess =0;
		wordCloud1.s = -99;
		wordCloud2.s = -99;
		if (buttonStop.b){
			buttonStop.b = false;
			deleteReactionList = new ArrayList<Integer>();
			simulationRectList = new ArrayList<Integer>();
			simulationRectListLevel = new ArrayList<Integer>();
			interElements =  new ArrayList<String>();
			interElementsLevel =  new ArrayList<Integer>();
			resetIntegrators();
			resetCausality();
			this.mouseMoved();
		}
		else if (buttonPause.b){
			buttonPause.mouseClicked();
		}
		else if (buttonBack.b){
			int size = simulationRectList.size();
			int currentLevel = simulationRectListLevel.get(size-1);
			for (int i = ReactionView.simulationRectList.size()-1;i>=0;i--){
				int level = ReactionView.simulationRectListLevel.get(i);
				if (level==currentLevel || level==currentLevel-1){
					int currentReact = ReactionView.simulationRectList.get(i);
					SliderSimulation.resetLevel(currentReact);       // User the function in Slider Simulation
					if (level>0){
						ReactionView.simulationRectList.remove(i);
						ReactionView.simulationRectListLevel.remove(i);
					}
				}
			}
			// Remove interElements
			for (int i = ReactionView.interElements.size()-1;i>=0;i--){
				int level = ReactionView.interElementsLevel.get(i);
				if ((level==currentLevel || level==currentLevel-1) && level>0){
					ReactionView.interElements.remove(i);
					ReactionView.interElementsLevel.remove(i);
				}	
			}
		}
		else if (buttonForward.b){
			int size = simulationRectList.size();
			int currentLevel = simulationRectListLevel.get(size-1);
			for (int i = ReactionView.simulationRectList.size()-1;i>=0;i--){
				int level = ReactionView.simulationRectListLevel.get(i);
				if (level==currentLevel){
					int currentReact = ReactionView.simulationRectList.get(i);
					SliderSimulation.foward(currentReact);        // User the function in Slider Simulation
				}
			}
		}
		else if (buttonReset.b){
			resetSelectionSimulation();
		}
		else if (popupCausality.b>=0){
			popupCausality.mouseClicked();
			// reset simulation
			deleteReactionList = new ArrayList<Integer>();
			simulationRectList = new ArrayList<Integer>();
			simulationRectListLevel = new ArrayList<Integer>();
			interElements =  new ArrayList<String>();
			interElementsLevel =  new ArrayList<Integer>();
			resetIntegrators();
			resetCausality();
			textbox1.searchText="";
			
			// Reset common downstream 
			selectedReactionsForCommonDownstream = new ArrayList<Integer>();
			selectedProteinForCommonDownstream = new ArrayList<Integer>();
			selectedComplexForCommonDownstream = new ArrayList<Integer>();
		}
		else if (popupReactionOrder.b>=0){
			popupReactionOrder.mouseClicked();
			if (popupReactionOrder.s==0 || popupReactionOrder.s==1|| popupReactionOrder.s==2){
				updateProteinPositions();
				updateComplexPositions();
				updateReactionPositions(); 
			}
			else if (popupReactionOrder.s==3){
				updateReactionPositions(); 
				updateProteinPositions();
				updateComplexPositions();
			}
			
		}
		else if (wordCloud1.b>=0){
			wordCloud1.mouseClicked();
		}
		else if (wordCloud2.b>=0){
			//wordCloud2.mouseClicked();
		}
		else if (ReactionView.check11.b){
			ReactionView.check11.mouseClicked();
		}
		else if (ReactionView.check12.b){
			ReactionView.check12.mouseClicked();
		}
		else if (ReactionView.check13.b){
			ReactionView.check13.mouseClicked();
		}
		else if (ReactionView.check14.b){
			ReactionView.check14.mouseClicked();
		}
		else if (ReactionView.check15.b){
			ReactionView.check15.mouseClicked();
		}
		else if (ReactionView.check5.b){
			ReactionView.check5.mouseClicked();
		}
		else if (popupReactionOrder.b>=0){
			if (popupReactionOrder.s==1){
				ReactionView.check11.s = true;   // Fade small molecule links if order reactions to avoid crossing
				ReactionView.check12.s = true;   // Fade unidentified elements links if order reactions to avoid crossing
			}	
			updateReactionPositions();
		}
		
		else{
			if (PopupCausality.s==4){
				if (bProteinL>=0 || bComplexL>=0){
					if (bProteinL>=0){
						if(selectedProteinForCommonDownstream.contains(bProteinL)){
							int index = selectedProteinForCommonDownstream.indexOf(bProteinL);
							selectedProteinForCommonDownstream.remove(index);
						}
						else
							selectedProteinForCommonDownstream.add(bProteinL);
					}

					ArrayList<Integer> listDown1 = new ArrayList<Integer>();
					for (int i=0;i<selectedProteinForCommonDownstream.size();i++){
						int p = selectedProteinForCommonDownstream.get(i);
						if (i==0){
							listDown1 = getDownstreamOfAprotein(p);
						}
						else{
							ArrayList<Integer> list2 = getDownstreamOfAprotein(p);
							listDown1= intersect(listDown1,list2);
						}
					}
					
					if (bComplexL>=0 ){
						if(selectedComplexForCommonDownstream.contains(bComplexL)){
							int index = selectedComplexForCommonDownstream.indexOf(bComplexL);
							selectedComplexForCommonDownstream.remove(index);
						}
						else
							selectedComplexForCommonDownstream.add(bComplexL);
					}

					ArrayList<Integer> listDown2 = new ArrayList<Integer>();
					for (int i=0;i<selectedComplexForCommonDownstream.size();i++){
						int c = selectedComplexForCommonDownstream.get(i);
						if (i==0){
							listDown2 = getDownstreamOfAcomplex(c);
						}
						else{
							ArrayList<Integer> list3 = getDownstreamOfAcomplex(c);
							listDown2= intersect(listDown2,list3);
						}
					}
					
					// Intersect downstream by Proteins and Complexes
					if (selectedProteinForCommonDownstream.size()>0 && selectedComplexForCommonDownstream.size()>0){
						selectedReactionsForCommonDownstream = intersect(listDown1,listDown2); 
					}
					else if (selectedProteinForCommonDownstream.size()>0){
						selectedReactionsForCommonDownstream = listDown1;
					}
					else if (selectedComplexForCommonDownstream.size()>0){
						selectedReactionsForCommonDownstream = listDown2;
					}
					else{
						System.out.println("PopupCausality.s=4 --> THIS CAN NOT HAPPENNED"+selectedReactionsForCommonDownstream);
					}
				}
				else{  // Reset all selections
					selectedReactionsForCommonDownstream = new ArrayList<Integer>();
					selectedProteinForCommonDownstream = new ArrayList<Integer>();
					selectedComplexForCommonDownstream = new ArrayList<Integer>();
				}
			}
			else{
				if (simulationRectList.size()>0){ // running the simulation  -> exit simulation
					deleteReactionList = new ArrayList<Integer>();
					simulationRectList = new ArrayList<Integer>();
					simulationRectListLevel = new ArrayList<Integer>();
					interElements =  new ArrayList<String>();
					interElementsLevel =  new ArrayList<Integer>();
					resetIntegrators();
					resetCausality();
					this.mouseMoved();
					
				}
				else{
					this.mouseMoved();
					resetSelectionSimulation();
				}
			}
		}
	}
	
	public ArrayList<Integer> intersect(ArrayList<Integer> a, ArrayList<Integer> b) {
		ArrayList<Integer> c = new ArrayList<Integer>();
		for (int i=0;i<a.size();i++){
			int p1 = a.get(i);
			if (b.contains(p1))
				c.add(p1);
		}
		
		return c;
	}
	
	public ArrayList<Integer> getDownstreamOfAprotein(int sProtein) {
		ArrayList<Integer> selectedReactions = new ArrayList<Integer>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] sLeft = rect.getLeft().toArray();
			for (int i3=0;i3<sLeft.length;i3++){
				 String name = main.ReactionFlow_1_1.getProteinName(sLeft[i3].toString());
				  if (name==null)
					  name = sLeft[i3].toString();
				  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==sProtein){
					  if (!selectedReactions.contains(r))
						  selectedReactions.add(r);
				  }
			}	  
		}
		
		//System.out.println(deleteProtein+"	deleteReactionList1="+deleteReactionList);
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<Integer> levelDownStreamList = new ArrayList<Integer>();
		for (int i=0;i<selectedReactions.size();i++) {
			int r = selectedReactions.get(i);
			listReactionDownStream(r,0,selectedReactions,parentList,levelDownStreamList);
		}
		return selectedReactions;
	}
	
	public ArrayList<Integer> getDownstreamOfAcomplex(int sComplex) {
		ArrayList<Integer> selectedReactions = new ArrayList<Integer>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] sLeft = rect.getLeft().toArray();
			for (int i3=0;i3<sLeft.length;i3++){
				 String name = main.ReactionFlow_1_1.getProteinName(sLeft[i3].toString());
				  if (name==null)
					  name = sLeft[i3].toString();
				  if (mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
					  int id = mapComplexRDFId_index.get(sLeft[i3].toString());
					  if (id==sComplex &&!selectedReactions.contains(r))
						  selectedReactions.add(r);
				  }
			}	  
		}
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<Integer> levelDownStreamList = new ArrayList<Integer>();
		for (int i=0;i<selectedReactions.size();i++) {
			int r = selectedReactions.get(i);
			listReactionDownStream(r,0,selectedReactions,parentList,levelDownStreamList);
		}
		return selectedReactions;
	}
		
	
	public void resetSelectionSimulation() {
		if (bRect>=0){
			deleteReactionList = new ArrayList<Integer>();
			simulationRectList = new ArrayList<Integer>();
			simulationRectListLevel = new ArrayList<Integer>();
			interElements =  new ArrayList<String>();
			interElementsLevel =  new ArrayList<Integer>();
			
			resetIntegrators();
			resetCausality();
			
			simulationRectList.add(bRect);
			simulationRectListLevel.add(0);
			
			
			simulationRectListAll = new ArrayList<Integer>();
			simulationRectListAll.add(bRect);
			ArrayList<Integer> parentList = new ArrayList<Integer>();
			simulationRectListLevelAll = new ArrayList<Integer>();
			simulationRectListLevelAll.add(0);
			listReactionDownStream(bRect,0,simulationRectListAll, parentList,simulationRectListLevelAll);
		}
		else if (bProteinL>=0 || bComplexL>=0){
			deleteReactionList = new ArrayList<Integer>();
			simulationRectList = new ArrayList<Integer>();
			simulationRectListLevel = new ArrayList<Integer>();
			interElements =  new ArrayList<String>();
			interElementsLevel =  new ArrayList<Integer>();
			
			for (int i=0;i<bRectListL.size();i++){
				int r = bRectListL.get(i);
				if (!simulationRectList.contains(r)){
				    simulationRectList.add(r);
					simulationRectListLevel.add(0);
				}	
			}  
			
			simulationRectListAll = new ArrayList<Integer>();
			ArrayList<Integer> parentList = new ArrayList<Integer>();
			simulationRectListLevelAll = new ArrayList<Integer>();
			for (int i=0;i<bRectListL.size();i++){
				int r = bRectListL.get(i);
				simulationRectListAll.add(r);
				simulationRectListLevelAll.add(0);
				System.out.println("resetSelectionSimulation   bProteinL="+bProteinL);
				listReactionDownStream(r,0,simulationRectListAll, parentList,simulationRectListLevelAll);
			}
			
			resetIntegrators();
			resetCausality();
		}
	}
}