package main;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;

public class PopupPathway{
	public static boolean bPopup = false;
	public static boolean sAll = false;
	public static int b = -1000;
	public PApplet parent;
	public float x = 0;
	public static float yBegin = 23;
	public static float yBeginList = 45;
	public int w1 = 98;
	public int w = 350;
	public int h = 28;
	public static int s=-100;
	public static float maxSize = 0;
	public Integrator[] iX, iY, iH;
	public int[] hightlightList;
	public float maxH = 17;
	public static ArrayList<Pathway2> pathwayList = new ArrayList<Pathway2>();
	public static ArrayList<Pathway2> redundantPathway = new ArrayList<Pathway2>();
	 
	
	public PopupPathway(PApplet parent_){
		parent = parent_;
	}
	
	public void setItems(){
		maxSize =0;
		s=-100;
		
		// compute the pathway List and remove redundent pathways.
		pathwayList = new ArrayList<Pathway2>();
		redundantPathway = new ArrayList<Pathway2>();
		ArrayList<Pathway2> processed = new  ArrayList<Pathway2>();
		int redundantLevel =1000; 
		
		
		for (int f=0;f<PathwayView.nFiles;f++){
			ArrayList<Pathway2> a = PathwayView.filePathway[f].printRecursively();
			redundantLevel =1000;   // reset the redundant option when go to the next file
			for (int i=0;i<a.size();i++){
				if (a.get(i).level>0) {  // Skip the file level
					if (isInList(processed,a.get(i).displayName) && a.get(i).level<redundantLevel) {  //break if found redundant pathway
						pathwayList.add(a.get(i)); 
						processed.add(a.get(i));
						Pathway2 path1 = getFirstMatch(processed,a.get(i).displayName);
						if (!redundantPathway.contains(path1))
							redundantPathway.add(path1);
						redundantPathway.add(a.get(i));
						redundantLevel = a.get(i).level;
					}
					else if (a.get(i).level<redundantLevel){
						pathwayList.add(a.get(i));
						processed.add(a.get(i));
						redundantLevel = 1000;
					}
					else{
						
					}
				}
			}
		}
		
		
		// positions of items
		iX = new Integrator[pathwayList.size()];
		iY = new Integrator[pathwayList.size()];
		iH = new Integrator[pathwayList.size()];
		for (int i=0;i<pathwayList.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
		}
		
		hightlightList =  new int[pathwayList.size()];
		for (int i=0;i<pathwayList.size();i++){
			hightlightList[i] = -1;
		}
	}
		
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(12);
		parent.fill(180);
		parent.rect(x,0,w1,23);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Pathways",x+w1/2,17);
		
		
		x = x_-50;
		
		
		if (hightlightList==null) return;
			
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
		if (bPopup == true || b>=-1){
			// Compute positions
			float itemH2 = (parent.height-yBeginList)/(pathwayList.size()-0.5f);
			if (itemH2>maxH)
				itemH2 =maxH;
			for (int i=0;i<pathwayList.size();i++){
				iY[i].target(yBeginList+i*itemH2);
				iH[i].target(itemH2);
			}
			
			for (int i=0;i<pathwayList.size();i++){
				iY[i].update();
				iH[i].update();
			}
		
		
			parent.fill(255);
			parent.stroke(0,100);
			parent.rect(x, yBegin, w,iY[pathwayList.size()-1].value-5);
			
			for (int i=0;i<pathwayList.size();i++) {
				String pathwayName = pathwayList.get(i).displayName;
				Color color = PathwayView.getColor(pathwayList.get(i).fileId);
				if (isInList(redundantPathway,pathwayName)){
					parent.noStroke();
					parent.fill(0,80);
					parent.rect(x+15+pathwayList.get(i).level*10,iY[i].value-iH[i].value, parent.textWidth(pathwayName)+10,iH[i].value);
				}
				
				float textSixe = PApplet.map(iH[i].value, 0, maxH, 4, 12);
				parent.textSize(textSixe);
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,iY[i].value-iH[i].value,w-25,iH[i].value);
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(0,15+parent.frameCount*22%240);
					parent.noStroke();
					parent.rect(x+10,iY[i].value-iH[i].value,w-25,iH[i].value);
					parent.fill(color.getRGB());
				}
				else{
					parent.fill(color.getRGB());
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(pathwayName,x+20+pathwayList.get(i).level*10,iY[i].value-iH[i].value/4);
				
				// Draw structures
				if (i==b){
					//int indexSet = getIndexInSet(b);
					//drawRelationshipDownStream(indexSet,b, 255,0,50,150, true,0);
					//drawRelationshipUpStream(entry,indexSet,b, 255,50,0,150, true,0);
				}
			}	
		}
	}
	
	public boolean isInList(ArrayList<Pathway2> list, String s){
		for (int i=0;i<list.size();i++){
			String pName = list.get(i).displayName;
			if (pName.equals(s))
				return true;
		}
		
		return false;
	}
	
	public Pathway2 getFirstMatch(ArrayList<Pathway2> list, String s){
		for (int i=0;i<list.size();i++){
			String pName = list.get(i).displayName;
			if (pName.equals(s)){
				return list.get(i);
			}	
		}
		
		return null;
	}
		
	 /*
	 public ArrayList<Integer> getUpSreamSetId(Map.Entry<Complex, Integer> entry, int indexSet, int indexHash) {
		 ArrayList<Integer> results = new ArrayList<Integer>();
		 int indexHashParent=0;
		 for (Map.Entry<Complex, Integer> entryParent : itemHash.entrySet()) {
			 if (indexHashParent<indexHash){
				 int indexSetParent = getIndexInSet(indexHashParent);
				 ArrayList<String> components = main.PathwayViewer_2_4.getComplexById(indexSetParent);
				 //System.out.println("    components="+components+"	"+entry.getKey().getRDFId());
				 if (components.contains(entry.getKey().getRDFId()))
					 results.add(indexSetParent);
			 }
			 indexHashParent++;
		 }
		 return results;
	 }
		 
	
	 // UP STREAM
	 public void drawRelationshipUpStream(Map.Entry<Complex, Integer> entry, int indexSet, int indexHash, int r, int g, int b, int alpha, boolean recursive, int level) {
		 ArrayList<Integer> parentSetIDs = getUpSreamSetId(entry,indexSet,indexHash);
		 for (int i=0;i<parentSetIDs.size();i++){
		 	int indexHash2 = getIndexInHash(parentSetIDs.get(i));
			float yy1 =  iY[indexHash].value-iH[indexHash].value/2;
			float yy2 =  iY[indexHash2].value-iH[indexHash2].value/2;
			float xx = x+30;
			float yy = (yy1+yy2)/2;
			parent.noFill();
			
			float num = main.PathwayViewer_2_4.proteinsInComplex[indexSet].size();
			float thickness = PApplet.map(PApplet.sqrt(num), 0, PApplet.sqrt(maxSize), 0, maxH/2);
			int g2 = g+40*level;
			if (g2>255)
				g2=255;
			int b2 = b+0*level;
			if (b2>255)
				b2=255;
			
			parent.noFill();
			parent.strokeWeight(thickness);
			parent.stroke(r,g2,b2,alpha);
			parent.arc(xx, yy, yy1-yy2,yy1-yy2, PApplet.PI/2, 3*PApplet.PI/2);
			
			if (recursive){
				int indexSet2 = parentSetIDs.get(i);
				drawRelationshipUpStream(getEntryHashId(indexHash2), indexSet2,indexHash2, r,g,b,150, true, level+1);
			}
		 }
		 parent.strokeWeight(1);
	 }	 

	 
	 
	// DOWN STREAM
	public void drawRelationshipDownStream(int indexSet, int indexHash, int r, int g, int b, int alpha, boolean recursive, int level) {
		 ArrayList<String> components = main.PathwayViewer_2_4.getComplexById(indexSet);
		 for (int i=0;i<components.size();i++){
			 if (main.PathwayViewer_2_4.mapComplexRDFId_index.get(components.get(i))!=null){
				 int indexSet2 = main.PathwayViewer_2_4.mapComplexRDFId_index.get(components.get(i));
				 int indexHash2 = getIndexInHash(indexSet2);
				float yy1 =  iY[indexHash].value-iH[indexHash].value/2;
				float yy2 =  iY[indexHash2].value-iH[indexHash2].value/2;
				float xx = x+30;
				float yy = (yy1+yy2)/2;
				parent.noFill();
				
				float num = main.PathwayViewer_2_4.proteinsInComplex[indexSet2].size();
				float thickness = PApplet.map(PApplet.sqrt(num), 0, PApplet.sqrt(maxSize), 0, maxH/2);
				int g2 = g+0*level;
				if (g2>255)
					g2=255;
				int b2 = b+40*level;
				if (b2>255)
					b2=255;
				
				parent.noFill();
				parent.strokeWeight(thickness);
				parent.stroke(r,g2,b2,alpha);
				parent.arc(xx, yy, yy2-yy1,yy2-yy1, PApplet.PI/2, 3*PApplet.PI/2);
				
				if (recursive){
					drawRelationshipDownStream(indexSet2,indexHash2, r,g,b,150, true, level+1);
				}
			}
		 }
		 parent.strokeWeight(1);
	 }
		
	
	*/
	
	 
	 
	 
	
	
	
		
	 public void mouseClicked() {
		if (b!=s)
			s = b;
		else
			s =-200;
	}
	 
	public void checkBrushing() {
		if (iH==null || iH.length==0) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (x<mX && mX<x+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else if (bPopup){
			if (x-20<mX && mX<x+w1 && yBegin<=mY && mY<=iY[0].value-iH[0].value){
				b=-1;
				return;
			}	
			for (int i=0; i<pathwayList.size(); i++){
				if (x-20<=mX && mX<=x+w && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					b =i;
					hightlightList[i] = 1; 
					return;
				}	
			}
		}
		bPopup=false;		
		b =-100;
	}
	
}