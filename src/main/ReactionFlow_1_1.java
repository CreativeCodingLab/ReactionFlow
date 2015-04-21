package main;
/*
 * DARPA project
 *
 * Copyright 2014 by Tuan Dang.
 *
 * The contents of this file are subject to the Mozilla Public License Version 2.0 (the "License")
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 */

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.*;
import org.biopax.paxtools.model.level3.Process;



import processing.core.*;

public class ReactionFlow_1_1 extends PApplet {
	private static final long serialVersionUID = 1L;
	
	public String currentFile = "./level3/RAF-Cascade.owl";
//	public String currentFile = "./level3_2015/HIV_life_cycle.owl";
	public static ButtonBrowse buttonBrowse;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	// Contains the location and size of each gene to display
	public float size=0;
	public static String message="";
	
	public ThreadLoader1 loader1=new ThreadLoader1(this);
	public Thread thread1=new Thread(loader1);
	
	
	public ThreadLoader4 loader4=new ThreadLoader4(this);
	public Thread thread4=new Thread(loader4);
	
	
	// Allow to draw 
	public static float percent = 0;
	
	// New to read data 
	public static ReactionView reactionView;
	// Multiple pathways 
	
	public static void main(String args[]){
	  PApplet.main(new String[] { ReactionFlow_1_1.class.getName() });
    }

	public void setup() {
		textFont(metaBold,14);
		size(1440, 900);
		//size(1280, 750);
		if (frame != null) {
		    frame.setResizable(true);
		  }
		background(0);
		frameRate(12);
		curveTightness(0.7f); 
		smooth();
		
		buttonBrowse = new ButtonBrowse(this);
		reactionView = new ReactionView(this);
		
		//VEN DIAGRAM
		if (!currentFile.equals("")){
			thread1=new Thread(loader1);
			thread1.start();
		}
		
		// Loading multiple pathways
	//	thread2=new Thread(loader2);
	//	thread2.start();
		
		// Loading sub pathways in a pathway
	//	thread3=new Thread(loader3);
	//	thread3.start();
		
		
		// enable the mouse wheel, for zooming
		addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				mouseWheel(evt.getWheelRotation());
			}
		});
	}		
	
	
	
	public void draw() {
		background(255);
		//this.scale(4);
	  /*	if (this.keyPressed){
			translate(width/3,mouseY); // use translate around scale
		  scale(2.5f);
		  translate(-width/3,-mouseY); // to scale from the center
		}*/
		// Draw 
		try{
			// Print message when loading multiple pathways
			if (percent<=1){
				this.fill((this.frameCount*22)%256, 255- (this.frameCount*22)%256,0);
				this.noStroke();
				this.arc(100,this.height-20, 30, 30, 0, PApplet.PI*2*(percent));
				
				this.textSize(12);
				this.textAlign(PApplet.LEFT);
				this.text(message, 120,this.height-14);
			}
		
					// Draw file name
			this.fill(128);
			this.textAlign(PApplet.LEFT);
			this.textSize(12);
			String[] str = currentFile.split("/");
	        String nameFile = str[str.length-1];
	  	    this.text("File: "+nameFile, 80, 16);
			
			reactionView.drawReactions(180);
			buttonBrowse.draw();
		}
		catch (Exception e){
			System.out.println();
			System.out.println("*******************Catch ERROR*******************");
			e.printStackTrace();
			return;
		}
	}	
	
	
				
	public void mousePressed() {
		reactionView.mousePressed();
	}
	public void mouseReleased() {
		reactionView.mouseReleased();
		
	}
	public void mouseDragged() {
		reactionView.mouseDragged();
	}
		
	public void mouseMoved() {
		ReactionView.popupCausality.mouseMoved();
		ReactionView.popupReactionOrder.mouseMoved();
		reactionView.checkReactionBrushing();
		if (ReactionView.isAllowedDrawing && ReactionView.simulationRectList.size()==0){
			reactionView.mouseMoved();
		}
		
	}
		
	public void mouseClicked() {
		if (buttonBrowse.b>=0){
			thread4=new Thread(loader4);
			thread4.start();
		}
		
		reactionView.mouseClicked();
		
	}
	
	public String loadFile (Frame f, String title, String defDir, String fileType) {
		  FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
		  fd.setFile(fileType);
		  fd.setDirectory(defDir);
		  fd.setLocation(50, 50);
		  fd.show();
		  String path = fd.getDirectory()+fd.getFile();
	      return path;
	}
	
	
	public void keyPressed() {
		if (ReactionView.textbox1.b){
			ReactionView.textbox1.keyPressed();
			return;
		}
	}
	
	
	// Thread for Venn Diagram
	class ThreadLoader1 implements Runnable {
		PApplet p;
		public ThreadLoader1(PApplet parent_) {
			p = parent_;
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			ReactionView.isAllowedDrawing =  false;
			
			ReactionView.textbox1.searchText="";
			File modFile = new File(currentFile);
			//File outFile = new File("output.txt");
			SimpleIOHandler io = new SimpleIOHandler();
			Model model;
			try{
				System.out.println();
				System.out.println("***************** Load data: "+modFile+" ***************************");
				long t1 = System.currentTimeMillis();
				model = io.convertFromOWL(new FileInputStream(modFile));
				long t2 = System.currentTimeMillis();
				System.out.println(t2-t1);
				ReactionView.mapProteinRDFId = new HashMap<String,String>();
				ReactionView.mapSmallMoleculeRDFId =  new HashMap<String,String>();
				ReactionView.mapComplexRDFId_index =  new HashMap<String,Integer>();
				
				 Set<Protein> proteinSet = model.getObjects(Protein.class);
				 for (Protein currentProtein : proteinSet){
					 ReactionView.mapProteinRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
				 }
				 System.out.println(proteinSet.size());
					
				 Set<SmallMolecule> set = model.getObjects(SmallMolecule.class);
				 for (SmallMolecule currentMolecule : set){
					 ReactionView.mapProteinRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 ReactionView. mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
				 }
				 
				 
				 Set<PhysicalEntity> physicalEntitySet = model.getObjects(PhysicalEntity.class);
				 for (PhysicalEntity current : physicalEntitySet){
					 if (current.getRDFId().contains("PhysicalEntity")){
						 ReactionView.mapProteinRDFId.put(current.getRDFId().toString(), current.getDisplayName());
					 }	 
				 }
				 
				 Set<Complex> complexSet = model.getObjects(Complex.class);
				 ReactionView.complexList = new ArrayList<Complex>();
				 int i2=0;
				 for (Complex current : complexSet){
					 ReactionView.mapComplexRDFId_index.put(current.getRDFId().toString(), i2);
					 ReactionView.complexList.add(current);
					 i2++;
				 }
				 i2=0;
				 
				 
				 // Compute proteins in complexes
				 ReactionView.proteinsInComplex = new ArrayList[complexSet.size()];
				 for (int i=0; i<ReactionView.complexList.size();i++){
					 ReactionView.proteinsInComplex[i] = getProteinsInComplexById(i);
				 }
				 
				 // Reaction set
				 ReactionView.reactionSet = model.getObjects(BiochemicalReaction.class);
				
				
				 /*
				 Set<PathwayStep> set3 = 	 model.getObjects(PathwayStep.class);
				 i2=0;
				 System.out.println("SET2 ******:"+set3);
				 for (PathwayStep current : set3){
				//	 System.out.println("PathwayStep"+i2+"	"+current.getNextStepOf()+"	"+current.getPathwayOrderOf());
				//	 System.out.println("		"+current.getNextStep());
					 i2++;
				 }*/
				  
				 
				 /*
				 // Figure 1 in the paper
				 i2=0;
				 ReactionView.reactionSet = new HashSet<BiochemicalReaction>();
				 for (Pathway aPathway : model.getObjects(Pathway.class)){
					 System.out.println("*"+aPathway.getDisplayName()+"*");
					 if (aPathway.getDisplayName().contains("Early Phase")){
						 
						 /// if (aPathway.getDisplayName().equals("ERK1 activation")){
					 //if (aPathway.getDisplayName().equals("GPCR ligand binding")){
						 System.out.println("--------------:" +aPathway.getDisplayName());
						 ArrayList<BiochemicalReaction> b = processPathwayForFigure1(aPathway);
						 for (int i=0;i<b.size();i++){
							 ReactionView.reactionSet.add(b.get(i));
						 }
					 }
					  i2++;
				 }
				 for (Pathway aPathway : model.getObjects(Pathway.class)){
					 	 System.out.println("--------------*"+aPathway.getDisplayName());
				 }*/
				
			}
			catch (FileNotFoundException e){
				e.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(p, "File not found: " + modFile.getPath());
				return;
			}
			
			System.out.println();
			ReactionView.check11.s=true;   // Fade small molecule
			ReactionView.check5.s=false;
			reactionView.setItems();
			
			ReactionView.isAllowedDrawing =  true;  //******************* Start drawing **************
		}
	}
	
	
	public ArrayList<BiochemicalReaction> processPathwayForFigure1(Pathway aPathway) {
		ArrayList<BiochemicalReaction> a = new ArrayList<BiochemicalReaction>();
		for (Process aProcess : aPathway.getPathwayComponent()) {
			if (aProcess instanceof Pathway) { // Dig into the nested structure
				ArrayList<BiochemicalReaction> b = processPathwayForFigure1((Pathway) aProcess);
				for (int i=0;i<b.size();i++){
					a.add(b.get(i));
				}
			} else if (aProcess instanceof BiochemicalReaction) {// It must be an Interaction
				a.add((BiochemicalReaction) aProcess);
			}
		}
		return a;
	}
	
	
	
	// Thread for grouping
	class ThreadLoader4 implements Runnable {
		PApplet parent;
		public ThreadLoader4(PApplet p) {
			parent = p;
		}
		public void run() {
			String fileName =  loadFile(new Frame(), "Open your file", "..", ".txt");
			if (fileName.equals("..null"))
				return;
			else{
				currentFile = fileName;
				// Read data for Reaction View
				thread1=new Thread(loader1);
				thread1.start();
				
				// Read data for Pathway View
			//	thread3=new Thread(loader3);
			//	thread3.start();
			}
		}
	}	
	
	
	public  boolean isContainReaction(ArrayList<BiochemicalReaction> a, String s) {
		if (a==null || s==null)
			return false;
		for (int r=0;r<a.size();r++){
			if (a.get(r)==null || a.get(r).getDisplayName()==null) continue;
			
			if (a.get(r).getDisplayName().equals(s))
				return true;
		}
		return false;
	}
		
	
	// This function returns all the files in a directory as an array of Strings
	public  ArrayList<String> listFileNames(String dir, String imgType) {
		File file = new File(dir);
		ArrayList<String> a = new ArrayList<String>();
		if (file.isDirectory()) { // Do
			String names[] = file.list();
			for (int i = 0; i < names.length; i++) {
				ArrayList<String> b = listFileNames(dir + "/" + names[i], imgType);
				for (int j = 0; j < b.size(); j++) {
					a.add(b.get(j));	
				}
				
			}
		} else if (dir.endsWith(imgType)) {
			a.add(dir);
		}
		return a;
	}
	
	
	
	
	public static String getProteinName(String ref){	
		String s1 = ReactionView.mapProteinRDFId.get(ref);
		return s1;
	}
	
	public static boolean isSmallMolecule(String name){	
		if (ReactionView.mapSmallMoleculeRDFId.containsValue(name))
			return true;
		else
			return false;
	}
	
	
	
	
	public static ArrayList<String> getComplexById(int id){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = ReactionView.complexList.get(id);
		  Object[] s2 = com.getComponent().toArray();
		  for (int i=0;i<s2.length;i++){
			  if (getProteinName(s2[i].toString())!=null)
				  components.add(getProteinName(s2[i].toString()));
			  else
				  components.add(s2[i].toString());
		 }
		return	 components;
	}
	
	
		
	public static ArrayList<String> getProteinsInComplexById(int id){	
		ArrayList<String> components = new ArrayList<String>(); 
		
		 Complex com = ReactionView.complexList.get(id);
		  Object[] s2 = com.getComponent().toArray();
		  for (int i=0;i<s2.length;i++){
			  if (getProteinName(s2[i].toString())!=null)
				  components.add(getProteinName(s2[i].toString()));
			  else {
				  if (ReactionView.mapComplexRDFId_index.get(s2[i].toString())==null){
					  String name = s2[i].toString();
					  components.add(name);
				  }
				  else{
					  int id4 = ReactionView.mapComplexRDFId_index.get(s2[i].toString());
					  ArrayList<String> s4 = getProteinsInComplexById(id4);
					  for (int k=0;k<s4.size();k++){
						  components.add(s4.get(k));
					  }
				  }
			  }
		 }
		 return components;
	}
	
	void mouseWheel(int delta) {
	}
}
