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
	
//	public String currentFile = "./level3RAS/1_RAF-Cascade.owl";
	public String currentFile = "./level3_2015/HIV_life_cycle.owl";
	public static ButtonBrowse buttonBrowse;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	// Contains the location and size of each gene to display
	public float size=0;
	public static String message="";
	
	public ThreadLoader1 loader1=new ThreadLoader1(this);
	public Thread thread1=new Thread(loader1);
	
	public static ThreadLoader12 loader12;
	public static Thread thread12=new Thread(loader12);
	
	// Read all pathways in a folder
	public ThreadLoader2 loader2=new ThreadLoader2(this);
	public Thread thread2=new Thread(loader2);
	
	// Read all sub-pathways in a pathway
	public ThreadLoader3 loader3=new ThreadLoader3(this);
	public Thread thread3=new Thread(loader3);
	
	public ThreadLoader4 loader4=new ThreadLoader4(this);
	public Thread thread4=new Thread(loader4);
	
	
	// Allow to draw 
	public static float percent = 0;
	
	// New to read data 
	public static ReactionView reactionView;
	// Multiple pathways 
	public PopupView popupView = new PopupView(this);
	public PathwayView pathwayView;
	
	public static void main(String args[]){
	  PApplet.main(new String[] { ReactionFlow_1_1.class.getName() });
    }

	public void setup() {
		loader12=new ThreadLoader12(this);
		textFont(metaBold,14);
		size(1440, 900);
		//size(2000, 1200);
		if (frame != null) {
		    frame.setResizable(true);
		  }
		background(0);
		frameRate(12);
		curveTightness(0.7f); 
		smooth();
		
		buttonBrowse = new ButtonBrowse(this);
		reactionView = new ReactionView(this);
		
		pathwayView = new PathwayView(this);
		
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
		if (this.keyPressed){
		translate(width/3,mouseY); // use translate around scale
		  scale(2.5f);
		  translate(-width/3,-mouseY); // to scale from the center
		}
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
		
				if (popupView.select==0){
					// Draw file name
					this.fill(128);
					this.textAlign(PApplet.LEFT);
					this.textSize(12);
					String[] str = currentFile.split("/");
			        String nameFile = str[str.length-1];
			  	    this.text("File: "+nameFile, 80, 16);
					
					reactionView.drawReactions(180);
				}
				else if (popupView.select==1){
					if (pathwayView!=null)
						pathwayView.draw();
				}
			
			popupView.draw(this.width-98);
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
		if (popupView.select==1){
			pathwayView.mousePressed();
		}
		else if (popupView.select==0){
			reactionView.mousePressed();
		}
	}
	public void mouseReleased() {
		if (popupView.select==1){
			pathwayView.mouseReleased();
		}
		else if (popupView.select==0){
				reactionView.mouseReleased();
		}
	}
	public void mouseDragged() {
		if (popupView.select==1){
			pathwayView.mouseDragged();
		}
		else if (popupView.select==0){
			reactionView.mouseDragged();
		}
	}
		
	public void mouseMoved() {
		popupView.mouseMoved();
		if (popupView.select==1){
			pathwayView.mouseMoved();
		}
		else if (popupView.select==0){
			ReactionView.popupCausality.mouseMoved();
			ReactionView.popupReactionOrder.mouseMoved();
			reactionView.checkReactionBrushing();
			if (ReactionView.isAllowedDrawing && ReactionView.simulationRectList.size()==0){
				reactionView.mouseMoved();
			}
		}
	}
		
	public void mouseClicked() {
		if (buttonBrowse.b>=0){
			thread4=new Thread(loader4);
			thread4.start();
		}
		
		popupView.mouseClicked();
		if (popupView.select==1){
			pathwayView.mouseClicked();
		}
		else if (popupView.select==0){
			reactionView.mouseClicked();
		}
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
		if (popupView.select==0 &&  ReactionView.textbox1.b){
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
				model = io.convertFromOWL(new FileInputStream(modFile));
				ReactionView.mapProteinRDFId = new HashMap<String,String>();
				ReactionView.mapSmallMoleculeRDFId =  new HashMap<String,String>();
				ReactionView.mapComplexRDFId_index =  new HashMap<String,Integer>();
				
				 Set<Protein> proteinSet = model.getObjects(Protein.class);
				 for (Protein currentProtein : proteinSet){
					 ReactionView.mapProteinRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
				 }
					
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
				 }
				
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
	
	
	// Thread for PathwayView, read a set of reactions
	class ThreadLoader12 implements Runnable {
		PApplet p;
		public ThreadLoader12(PApplet parent_) {
			p = parent_;
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			PathwayView.isAllowedDrawing =  false;
			
			ReactionView.textbox1.searchText="";
			System.out.println("");
			try{
				 ReactionView.mapSmallMoleculeRDFId =  PathwayView.mapSmallMoleculeRDFId;
				
				 // Protein map
				 ReactionView. mapProteinRDFId = new HashMap<String,String>();
				 ArrayList<BiochemicalReaction> a = PathwayView.getDistintReactions(Pathway2.bEdges);
				 ReactionView.reactionSet = new HashSet<BiochemicalReaction>(a);
				 
				 
				 //Complex Map/List
				 ReactionView.complexList = new ArrayList<Complex>();
				 ReactionView.mapComplexRDFId_index =  new HashMap<String,Integer>();
				 
				 for (BiochemicalReaction current : ReactionView.reactionSet){
					 Object[] aLeft = current.getLeft().toArray();
					 Object[] aRight = current.getRight().toArray();
					 
					 // Construct protein/complex map LEFT
					 for (int i3=0;i3<aLeft.length;i3++){
						 String ref = aLeft[i3].toString();
					 
						  if (PathwayView.mapProteinRDFId.containsKey(ref)){
							  String name = PathwayView.mapProteinRDFId.get(ref);
							  if (!ReactionView.mapProteinRDFId.containsKey(ref))
								  ReactionView.mapProteinRDFId.put(ref,name);
						  }
						  else  if (PathwayView.mapComplexRDFId.containsKey(ref)){
							  Map<String, String> m2 = PathwayView.getProteinsInComplexRDFId2(ref);
							  for (Map.Entry<String, String> entry : m2.entrySet()) {
								  if (!ReactionView.mapProteinRDFId.containsKey(entry.getKey()))
									  ReactionView.mapProteinRDFId.put(entry.getKey(),entry.getValue());
							  }
							  
							  // For Complex
							  Complex com = PathwayView.mapComplexRDFId_Complex.get(ref);
							  if (!ReactionView.mapComplexRDFId_index.containsKey(ref)){
								  ReactionView.mapComplexRDFId_index.put(ref,ReactionView.complexList.size());
								  ReactionView.complexList.add(com);
							  }
						  }	  
					}	  
					 
					// Construct protein/complex map RIGHT
					 for (int i3=0;i3<aRight.length;i3++){
						 String ref = aRight[i3].toString();
					 	 if (PathwayView.mapProteinRDFId.containsKey(ref)){
							  String name = PathwayView.mapProteinRDFId.get(ref);
							  if (!ReactionView.mapProteinRDFId.containsKey(ref))
								 ReactionView.mapProteinRDFId.put(ref,name);
						 }
						 else  if (PathwayView.mapComplexRDFId.containsKey(ref)){
							  Map<String, String> m2 = PathwayView.getProteinsInComplexRDFId2(ref);
							  for (Map.Entry<String, String> entry : m2.entrySet()) {
								  if (!ReactionView.mapProteinRDFId.containsKey(entry.getKey()))
									   ReactionView.mapProteinRDFId.put(entry.getKey(),entry.getValue());
							  }
							  
							  // For Complex
							  Complex com = PathwayView.mapComplexRDFId_Complex.get(ref);
							  if (!ReactionView.mapComplexRDFId_index.containsKey(ref)){
									ReactionView.mapComplexRDFId_index.put(ref,ReactionView.complexList.size());
									ReactionView.complexList.add(com);
							  }
						  }	  
					}	  
				 }
				 

				 // Compute proteins in complexes
				 ReactionView.proteinsInComplex = new ArrayList[ReactionView.complexList.size()];
				 for (int i=0; i<ReactionView.complexList.size();i++){
					 ReactionView.proteinsInComplex[i] = PathwayView.getProteinsInComplex(ReactionView.complexList.get(i));
				 }
			}
			catch (Exception e){
				e.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(p, "Something wrong: ??????");
				return;
			}
			ReactionView.check11.s=true;   // Fade small molecule
			ReactionView.popupReactionOrder.s=1;
			ReactionView.check5.s=false;
			PathwayView.isAllowedDrawing =  true;
			reactionView.setItems();
			Pathway2.meassage="DONE";
		}
	}
	
	
	// Read all pathways in a folder
	class ThreadLoader2 implements Runnable {
		PApplet parent;
		public ThreadLoader2(PApplet parent_) {
			parent = parent_;
		}
		public void run() {
			PathwayView.isAllowedDrawing =  false;
			
			// String path = "./level3_TestStructure";
			// String path = "./level3_2015";
			// String path = "./level3_Reactome";
			// String path = "./level3";
			 String path = "./Image43ForInfoVis";
			// String path = "./ImageUsecase";
			 String imgType = ".owl";
			 pathwayView.files = listFileNames(path, imgType); 
			 PathwayView.nFiles = pathwayView.files.size();
			 println("Number of Pathway: "+pathwayView.nFiles);
			
			 // Initialize best plots
			 try{
				pathwayView.mapProteinRDFId = new HashMap<String,String>();
				pathwayView.mapSmallMoleculeRDFId =  new HashMap<String,String>();
				pathwayView.mapComplexRDFId = new HashMap<String,String>();
				pathwayView.mapComplexRDFId_Complex = new HashMap<String,Complex>();
				pathwayView.rectSizeList = new ArrayList<Integer>();
				pathwayView.rectFileList = new ArrayList<Integer>();
				pathwayView.maxSize=0;
				pathwayView.filePathway = new Pathway2[pathwayView.nFiles];
				PathwayView.rootPathway = new Pathway2(parent,null,-1,"ROOT",0, true);
				
				for (int f=0;f<pathwayView.files.size();f++){
					percent = (float)(f+1)/(pathwayView.files.size()+2);
					message = "Reading "+(f+1)+"/"+pathwayView.files.size()+": "+pathwayView.files.get(f);
					File modFile = new File(pathwayView.files.get(f));
					SimpleIOHandler io = new SimpleIOHandler();
					Model model = io.convertFromOWL(new FileInputStream(modFile));
					
					System.out.println();
					System.out.println("***************** Load data: "+modFile+" ***************************");
					
					 Set<Protein> proteinSet = model.getObjects(Protein.class);
					 for (Protein currentProtein : proteinSet){
						 if (!pathwayView.mapProteinRDFId.containsKey(currentProtein.getRDFId().toString()))
							 pathwayView.mapProteinRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
					 }
						
					 Set<SmallMolecule> set = model.getObjects(SmallMolecule.class);
					 for (SmallMolecule currentMolecule : set){
						 if (!pathwayView.mapProteinRDFId.containsKey(currentMolecule.getRDFId().toString()))
							 pathwayView.mapProteinRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
						 if (!pathwayView.mapSmallMoleculeRDFId.containsKey(currentMolecule.getRDFId().toString()))
							 pathwayView.mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 }
					 
					 Set<Complex> complexSet = model.getObjects(Complex.class);
					 int i2=0;
					 for (Complex current : complexSet){
						 if (!pathwayView.mapComplexRDFId.containsKey(current.getRDFId().toString())){
							 pathwayView.mapComplexRDFId.put(current.getRDFId().toString(), current.getDisplayName());
						 	 pathwayView.mapComplexRDFId_Complex.put(current.getRDFId().toString(),current);
						 }	 
						 i2++;
					 }
					 i2=0;
					 
					// PATHWAY structure
					 i2=0;
					 String[] str = pathwayView.files.get(f).split("/");
					 String nameFile = str[str.length-1];
					 PathwayView.filePathway[f] = new Pathway2(parent,PathwayView.rootPathway,f,nameFile,1, false);
					 for (Pathway aPathway : model.getObjects(Pathway.class)){
						 Pathway2 newPathway = new Pathway2(parent,PathwayView.filePathway[f],
								 f,aPathway.getDisplayName(),PathwayView.filePathway[f].level+1,false);
						 PathwayView.filePathway[f].subPathwayList.add(newPathway);
						 processPathway(aPathway, newPathway, f);
				    	 i2++;
					 }
					 
					 
					 // Remove nested pathway from the top level of a file Because top contains all pathways
					 boolean isRedundentPathway = true;
					 while(isRedundentPathway){
						 isRedundentPathway = false;
						 for (int p1=0;p1< PathwayView.filePathway[f].subPathwayList.size();p1++){
							 Pathway2 path1 = PathwayView.filePathway[f].subPathwayList.get(p1);
							 int redundentPathwayIndex = -1;
							 for (int p2=0;p2< PathwayView.filePathway[f].subPathwayList.size();p2++){
								 if (p1==p2) continue;
								 Pathway2 path2 = PathwayView.filePathway[f].subPathwayList.get(p2);
								 if (path2.isContainPathway(path1.displayName)){
									 redundentPathwayIndex =p2;
								 }
							 }
							 
							 if (redundentPathwayIndex>=0){
								 PathwayView.filePathway[f].subPathwayList.remove(p1);
								 isRedundentPathway = true;
							 }
						 }
					 }
				}
				PathwayView.isAllowedDrawing =  true;
				
				// Compute size for 1 time
				for (int f=0;f<pathwayView.files.size();f++){
					PathwayView.filePathway[f].computeSize();
					PathwayView.rootPathway.subPathwayList.add(PathwayView.filePathway[f]);
				}
				
				// Set reactions to pathway view
				ArrayList<BiochemicalReaction> rectList = new ArrayList<BiochemicalReaction>();
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					ArrayList<BiochemicalReaction> a = pathway.getAllReaction();
					for (int i=0;i<a.size();i++){
						BiochemicalReaction react = a.get(i);
						rectList.add(react);
						pathwayView.rectFileList.add(f);
					}
				}
				
				pathwayView.setItems(rectList);
				percent = (float)(pathwayView.files.size()+1)/(pathwayView.files.size()+2);
				message = "Computing causalities";
				
				pathwayView.updateNodes(rectList);
				int newId = 0;
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					newId = pathway.setNodeId(newId);
				}
				
				System.out.println("Done pathwayView.updateNodes");
				pathwayView.updateEdges();
				System.out.println("Done pathwayView.updateEdges");
				pathwayView.popupPathway.setItems();
				System.out.println("Done setItems");
				percent = 1.1f;
				
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					pathway.setNodePathway();
				}
				pathwayView.printEdges();  // PRINT
				
				System.out.println("setNodePathway");
				pathwayView.order(rectList);
				System.out.println("Done order");
				
			 }
			 catch (FileNotFoundException e){
					e.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(parent, "Exception in multi pathways reading");
					return;
			 }
		}	 
	}
	
	
	// Read all sub-pathways in a pathway
	class ThreadLoader3 implements Runnable {
		PApplet parent;
		public ThreadLoader3(PApplet parent_) {
			parent = parent_;
		}
		public void run() {
			PathwayView.isAllowedDrawing =  false;
	
			// String path = "./level3_Reactome/AllCellCycle.owl";
			// String path = "./AllDesease.owl";
			// String path = "./AllTransmembrance.owl";
			 String path = "./AllSignaling.owl";
			 println("Input file: "+path);
			
			 // Initialize best plots
			 try{
				pathwayView.mapProteinRDFId = new HashMap<String,String>();
				pathwayView.mapSmallMoleculeRDFId =  new HashMap<String,String>();
				pathwayView.mapComplexRDFId = new HashMap<String,String>();
				pathwayView.mapComplexRDFId_Complex = new HashMap<String,Complex>();
				pathwayView.rectSizeList = new ArrayList<Integer>();
				pathwayView.rectFileList = new ArrayList<Integer>();
				pathwayView.maxSize=0;
				pathwayView.filePathway = new Pathway2[pathwayView.nFiles];
				PathwayView.rootPathway = new Pathway2(parent,null,-1,"ROOT",0, true);
				
				 File modFile = new File(path);
				 SimpleIOHandler io = new SimpleIOHandler();
				 Model model = io.convertFromOWL(new FileInputStream(modFile));
					
				System.out.println();
				System.out.println("***************** Load data: "+modFile+" ***************************");
					
				 Set<Protein> proteinSet = model.getObjects(Protein.class);
				 for (Protein currentProtein : proteinSet){
					 if (!PathwayView.mapProteinRDFId.containsKey(currentProtein.getRDFId().toString()))
						 PathwayView.mapProteinRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
				 }
						
				 Set<SmallMolecule> set = model.getObjects(SmallMolecule.class);
				 for (SmallMolecule currentMolecule : set){
					 if (!PathwayView.mapProteinRDFId.containsKey(currentMolecule.getRDFId().toString()))
						 PathwayView.mapProteinRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 if (!PathwayView.mapSmallMoleculeRDFId.containsKey(currentMolecule.getRDFId().toString()))
						 PathwayView.mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
				 }
					 
				 Set<Complex> complexSet = model.getObjects(Complex.class);
				 int i2=0;
				 for (Complex current : complexSet){
					 if (!pathwayView.mapComplexRDFId.containsKey(current.getRDFId().toString())){
						 pathwayView.mapComplexRDFId.put(current.getRDFId().toString(), current.getDisplayName());
					 	 pathwayView.mapComplexRDFId_Complex.put(current.getRDFId().toString(),current);
					 }	 
					 i2++;
				 }
				 i2=0;
					 
				 // PATHWAY structure
				 i2=0;
				 int file=0;
				 Pathway2 path0 = new Pathway2(parent,PathwayView.rootPathway,file,"Roooooot",-1, false);
				 for (Pathway aPathway : model.getObjects(Pathway.class)){
					 Pathway2 newPathway = new Pathway2(parent,path0,
							 file,aPathway.getDisplayName(),path0.level+1,false);
					 path0.subPathwayList.add(newPathway);
					 processPathway(aPathway, newPathway, file);
			    	 i2++;
				 }
					 
					 
				 // Remove nested pathway from the top level of a file Because top contains all pathways
				 boolean isRedundentPathway = true;
				 while(isRedundentPathway){
					 isRedundentPathway = false;
					 for (int p1=0;p1< path0.subPathwayList.size();p1++){
						 Pathway2 path1 = path0.subPathwayList.get(p1);
						 int redundentPathwayIndex = -1;
						 for (int p2=0;p2< path0.subPathwayList.size();p2++){
							 if (p1==p2) continue;
							 Pathway2 path2 = path0.subPathwayList.get(p2);
							 if (path2.isContainPathway(path1.displayName)){
								 redundentPathwayIndex =p2;
							 }
						 }
						 if (redundentPathwayIndex>=0){
							 path0.subPathwayList.remove(p1);
							 isRedundentPathway = true;
						 }
					 }
				 }
				
				
				 pathwayView.files = new ArrayList<String>();
				 file = 0;
				 for (int i=0;i< path0.subPathwayList.size();i++){
					 Pathway2 path1  = path0.subPathwayList.get(i);
					 for (int j=0;j< path1.subPathwayList.size();j++){
						 pathwayView.files.add(path1.subPathwayList.get(j).displayName);
						 file++;
					 }
				 }
				 
				 PathwayView.filePathway = new Pathway2[pathwayView.files.size()];
				 file = 0;
				 for (int i=0;i< path0.subPathwayList.size();i++){
					 Pathway2 path1  = path0.subPathwayList.get(i);
					 for (int j=0;j<path1.subPathwayList.size();j++){
						 PathwayView.filePathway[file] = path1.subPathwayList.get(j);
						 PathwayView.filePathway[file].computeSize();
						 setFilePathway(PathwayView.filePathway[file], file);
						 PathwayView.rootPathway.subPathwayList.add(PathwayView.filePathway[file]);
						 file++;
					 }
				 }
				 
				 for (int i=0;i< pathwayView.files.size();i++){
					 PathwayView.filePathway[i].parentPathway = PathwayView.rootPathway;
				 }
						
				 PathwayView.nFiles = pathwayView.files.size();
				
				 // Set reactions to pathway view
				ArrayList<BiochemicalReaction> rectList = new ArrayList<BiochemicalReaction>();
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					ArrayList<BiochemicalReaction> a = pathway.getAllReaction();
					for (int i=0;i<a.size();i++){
						BiochemicalReaction react = a.get(i);
						rectList.add(react);
						pathwayView.rectFileList.add(f);
					}
				}
				PathwayView.isAllowedDrawing =  true;
				pathwayView.setItems(rectList);
				pathwayView.updateNodes(rectList);
				int newId = 0;
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					newId = pathway.setNodeId(newId);
				}
				pathwayView.printEdges();  // PRINT
				
				System.out.println("Done pathwayView.updateNodes");
				pathwayView.updateEdges();
				
				System.out.println("Done pathwayView.updateEdges");
				pathwayView.popupPathway.setItems();
				System.out.println("Done setItems");
				
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					pathway.setNodePathway();
				}
				
				pathwayView.order(rectList);
				System.out.println("Done order");
				
			 }
			 catch (FileNotFoundException e){
					e.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(parent, "Exception in multi pathways reading");
					return;
			 }
		}	 
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
	
	// Set pathway File for subpathways of One pathway option
	public void setFilePathway(Pathway2 path2, int fileId) {
		path2.fileId = fileId;
		for (int i=0;i<path2.subPathwayList.size();i++){
			setFilePathway(path2.subPathwayList.get(i),fileId);
		}
	} 
	
	public void processPathway(Pathway aPathway, Pathway2 thisPathway, int f) {
		for (Process aProcess : aPathway.getPathwayComponent()) {
			if (aProcess instanceof Pathway) { // Dig into the nested structure
				Pathway2 newPathway = new Pathway2(this, thisPathway,f,aProcess.getDisplayName(),thisPathway.level+1,false);
				thisPathway.subPathwayList.add(newPathway);
				processPathway((Pathway) aProcess,newPathway,f);
			} else if (aProcess instanceof BiochemicalReaction) {// It must be an Interaction
				thisPathway.reactList.add((BiochemicalReaction) aProcess);
			} else { 
				 //System.out.println("		??? " + aProcess.getDisplayName());
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
		if (popupView.select==1){
			PathwayView.scale += delta/1000f;
			if (PathwayView.scale<0.1f)
				PathwayView.scale=0.1f;
			pathwayView.updateScale();
			PathwayView.setIntegrator = 4;
		}
	}
}
