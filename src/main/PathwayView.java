package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;


import processing.core.PApplet;
import GraphLayout.*;


public class PathwayView{
	public PApplet parent;
	public ArrayList<String> files;
	public static int nFiles;
	public static boolean isAllowedDrawing = false;
	
	// Read data 
	public static Map<String,String> mapProteinRDFId;
	public static Map<String,String> mapSmallMoleculeRDFId;
	public static Map<String,String> mapComplexRDFId;
	public static Map<String,Complex> mapComplexRDFId_Complex;
	
	public static  ArrayList<String> complexList = new ArrayList<String>(); 
	public ArrayList<String> proteinList = new ArrayList<String>();
	
	public ArrayList<Integer> rectSizeList;
	public ArrayList<Integer> rectFileList;
	
	
	public int maxSize = 0;
	public static Gradient gradient = new Gradient();
	public static float colorScale=0;
	public static Integrator[][] iS;
	public static float xCircular, yCircular, rCircular, rCentetButton; 
	
	
	public static Graph g;
	public static float xRight=0;
	public Slider2 slider2;
	public static PopupLayout popupLayout;
	public static CheckBox checkName;
	public static CheckBox checkEdges;
	
	public ThreadLoader5 loader5;
	public Thread thread5 = new Thread(loader5);
	
	
	// position
	public static float[] yTopological;
	public static float[] yTree;
	public static Integrator iTransition = new Integrator(0,0.1f,0.4f);
	
	// Hierarchy 
	public static Pathway2[] filePathway = null;
	public static Pathway2 rootPathway = null;
	public PopupPathway popupPathway;
	public static Pathway2 bPathway;
	
	public static float scale=1;
	boolean isBrushing =false;
	public static int setIntegrator =0;
	
	// Button to control the map
	public ButtonMap buttonReset;
	public ButtonMap buttonExpand;
	public ButtonMap buttonCollapse;
	
	// Depict a selected reaction
	public Integrator iX2; 
	public Integrator iY2; 
	
	public static float sat =220;
	
	public PathwayView(PApplet p){
		parent = p;
		buttonReset = new ButtonMap(parent);
		buttonExpand = new ButtonMap(parent);
		buttonCollapse = new ButtonMap(parent);
		
		loader5= new ThreadLoader5(parent);
		slider2 = new Slider2(parent);
		popupLayout = new PopupLayout(parent);
		checkName = new CheckBox(parent,"Reactions names");
		checkEdges = new CheckBox(parent,"Show causal links");
		checkEdges.s =true;
		
		popupPathway = new PopupPathway(parent);
		
		float v=0.5f;
		gradient.addColor(new Color(0,0,v));
		gradient.addColor(new Color(0,v,v));
		gradient.addColor(new Color(0,v,0));
		//gradient.addColor(new Color(v,v,0));
		gradient.addColor(new Color(v,0,0));
		gradient.addColor(new Color(v,0,v));
		gradient.addColor(new Color(0,0,v));
		
		xRight = parent.width-298;
		iX2 = new Integrator(xRight,0.25f,0.75f);
		iY2 = new Integrator(parent.height,0.25f,0.75f); 
	}
	
	public void setItems(ArrayList<BiochemicalReaction> rectList){
		// Causality integrator
		iS = new Integrator[rectList.size()][rectList.size()];
		for (int i=0;i<rectList.size();i++){
			for (int j=0;j<rectList.size();j++){
				iS[i][j] = new Integrator(0, 0.2f,SliderSpeed.speed/2);
			}
		}	
		
		// Compute proteinList and complexList
		complexList = new ArrayList<String>(); 
		proteinList = new ArrayList<String>();
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			for (int i=0;i<left.length;i++){
				String ref = left[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
			for (int i=0;i<right.length;i++){
				String ref = right[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
		}
		
		// Compute size of reaction
		maxSize =0;
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			
			ArrayList<Integer> proteinsL = getProteinsInOneSideOfReaction(left);
			ArrayList<Integer> proteinsR = getProteinsInOneSideOfReaction(right);
			int size = proteinsL.size()+ proteinsR.size();
			rectSizeList.add(size);   
			if (size>maxSize)
				maxSize = size;
		}
			
		colorScale = (float) (gradient.colors.size()-0.8f)/ (nFiles) ;
		resetPosistion();
		updateScale();
	}
	
	
	public void updateScale() {
		float countReactions=0;
		for (int i=0;i<filePathway.length;i++){
			countReactions+=filePathway[i].numReactions;
		}
		rCircular = PApplet.pow(countReactions,0.6f)*3*scale;
		
		// Update node size
		if (Graph.nodes==null) return;
		for (int i = 0; i < Graph.nodes.size(); i++) {
			updateNodeSize(i);
		}	
	}
		
	public void resetPosistion() {
		xCircular = xRight/2;
		yCircular = parent.height/2;
	}
	
	public void updateNodes(ArrayList<BiochemicalReaction> rectList) {
		g = new Graph();
		for (int i = 0; i < rectList.size(); i++) {
			int fileId = rectFileList.get(i);
			Node node = new Node(new Vector3D(20+parent.random(xRight-40), 20 + parent.random(parent.height-40), 0), parent) ;
			node.nodeId = i;
			node.reaction = rectList.get(i);
			node.color = getColor(fileId);//gradient.getGradient(colorScale*(transferID(fileId)));
			g.addNode(node);
		}	
		for (int i=0;i<Graph.nodes.size();i++){
			updateNodeSize(i);  // Update node size
		}
	}
	public void updateNodeSize(int i) {
		Graph.nodes.get(i).setMass(PApplet.pow(scale,0.75f) +PApplet.pow(rectSizeList.get(i),0.4f));
	}
		
	
	
	public static Color getColor(int fileId) {
		return gradient.getGradient(colorScale*(transferID(fileId)));
	}
		
	
	// Make sure pathways next to each other receive different colora
	public static float transferID(int id) {
		float newId = (id*(nFiles+1)/2)%nFiles;
		
		return newId;
	}
		
	
	public void updateEdges() {
		g.edges = new ArrayList<Edge>();
		g.edgesFrom = new HashMap<Node, ArrayList<Edge>>();
		g.edgesTo = new HashMap<Node, ArrayList<Edge>>();
		
		// Update slider value to synchronize the processes
		for (int r = 0; r < Graph.nodes.size(); r++) {
			Node node1 = Graph.nodes.get(r);
			ArrayList<Integer> a = getDirectDownstream(r);
			for (int j = 0; j < a.size(); j++) {
				int r2 = a.get(j);
				Node node2 = Graph.nodes.get(r2);
				Edge e = new Edge(node1, node2, 0, parent); //
				g.addEdge(e);
				node1.degree++;
			
			}
		}	
	}
	
	public void printEdges() {
		// Update slider value to synchronize the processes
		System.out.println("	************printEdges():");
		String[] out = new String[Graph.nodes.size()+2];
		for (int r = 0; r < Graph.nodes.size(); r++) {
			Node node1 = Graph.nodes.get(r);
			ArrayList<Integer> a = getDirectDownstream(r);
			ArrayList<String> b = new ArrayList<String>();
			String list = "[";
			for (int j = 0; j < a.size(); j++) {
				int r2 = a.get(j);
				Node node2 = Graph.nodes.get(r2);
				node1.degree++;
			
				String str = "\""+getPathwayPath(node2.parentPathway).replace(".ROOT", "root").replace(".owl", "").replace(",", "")+"."+ getNodeName(node2)+"\"";
				b.add(str);
				if (j==0)
					list+=str;
				else 
					list +=","+str;
			}
			if (r<Graph.nodes.size()-1)
				list +="]},";
			else  
				list +="]}";
			String source = "{\"name\":\""+getPathwayPath(node1.parentPathway).replace(".ROOT", "root").replace(".owl", "").replace(",", "")+"."+ getNodeName(node1)+"\",\"size\":1,\"imports\":";
			out[r+1] = source+list;
			System.out.println(out[r]);
		}
		out[0] ="[";
		out[Graph.nodes.size()+1] ="]";
		
		parent.saveStrings("./pathway.json", out);
	}
	
	public String getNodeName(Node node) {
		if (node==null || node.reaction==null || node.reaction.getDisplayName()==null)
			return "null";
		else
			return node.reaction.getDisplayName().replace(",", "").replace(".", "");
	}
		
	public String getPathwayPath(Pathway2 path2) {
		if (path2==null)
			return "";
		else
			return getPathwayPath(path2.parentPathway)+"." + path2.displayName;
	
	}
		
	
	public void order(ArrayList<BiochemicalReaction> rectList) {
		// Initialize topological ordering
		orderTree();
		yTopological =  new float[rectList.size()];
		orderTopological();
	}
		
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String ref = s[i3].toString();
			  if (mapProteinRDFId.get(ref)!=null){
				  String proteinName = mapProteinRDFId.get(ref);
				  int index = proteinList.indexOf(proteinName);
				  a.add(index);
			  }
			  else  if (mapComplexRDFId.get(ref)!=null){
				  ArrayList<String> components = getProteinsInComplexRDFId(ref);
				  for (int k=0;k<components.size();k++){
					  String proteinName = mapComplexRDFId.get(components.get(k));
					  int index = proteinList.indexOf(proteinName);
					  a.add(index);
				  }
			  }
			  else{
				  System.out.println("getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]+"-----SOMETHING WRONG");
			 } 
		  }
		return a;
	}
	
	// Get all protein names in a complex ref
	public static ArrayList<String> getProteinsInComplexRDFId(String ref){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = mapComplexRDFId_Complex.get(ref);
		Object[] s2 = com.getComponent().toArray();
		for (int i=0;i<s2.length;i++){
			String ref2 = s2[i].toString();
			 if (mapProteinRDFId.get(ref2)!=null)
				  components.add(mapProteinRDFId.get(ref2));
			 else if (mapComplexRDFId.get(ref2)!=null){
				  ArrayList<String> s4 = getProteinsInComplexRDFId(ref2);
				  for (int k=0;k<s4.size();k++){
					  components.add(s4.get(k));
				  }
			  }
		 }
		 return components;
	}
	
	// Get all protein RDFId_names in a complex ref
	public static Map<String,String> getProteinsInComplexRDFId2(String ref){	
		Map<String,String> m = new HashMap<String,String>(); 
		Complex com = mapComplexRDFId_Complex.get(ref);
		Object[] s2 = com.getComponent().toArray();
		for (int i=0;i<s2.length;i++){
			String ref2 = s2[i].toString();
			 if (mapProteinRDFId.get(ref2)!=null)
				  m.put(ref2,mapProteinRDFId.get(ref2));
			 else if (mapComplexRDFId.get(ref2)!=null){
				 Map<String,String> m2 = getProteinsInComplexRDFId2(ref2);
				 for (Map.Entry<String, String> entry : m2.entrySet()) {
					 m.put(entry.getKey(), entry.getValue());
				 }
			  }
		 }
		 return m;
	}
	
	public void draw(){
		if (g==null || Graph.nodes==null || g.edges==null || !isAllowedDrawing) return;
		
		
		for (int i=0;i<Graph.nodes.size();i++){
			Node node = Graph.nodes.get(i);
			node.iX.update();
			node.iY.update();
			node.iAlpha.update();
			
		}
		
		if (popupLayout.s==0){
			drawTree();
			
			iTransition.target(PApplet.PI);
			iTransition.update();
			g.drawNodes();
			g.drawEdges();
		}
		else if (popupLayout.s==1){
			iTransition.target(PApplet.PI);
			iTransition.update();
			g.drawNodes();
			g.drawEdges();
		}
		else if (popupLayout.s==2){
			iTransition.target(0);
			iTransition.update();
			if (g==null) return;
			doLayout();
			g.drawEdges();
			g.drawNodes();
		}
		else if (popupLayout.s==3){
			iTransition.target(1);
			iTransition.update();
			
			rootPathway.resetLinkParent();
			
			//checkEdges.draw(xRight-140, 5);
			
			drawPathways();
			
		   	g.drawNodes();
		   	if (checkEdges.s)
		   		g.drawEdges();
		   	rootPathway.drawLinkParent();
			rootPathway.drawSubpathwayLinks(); // This only done at root level, no recursive
			
			 // Draw center buttons
		//	drawCenter(xCircular, yCircular,rCentetButton);
			parent.noStroke();
			for (int f=0;f<filePathway.length;f++){
				filePathway[f].drawCenter(true);
			}		
 		}
		
		
		//checkName.draw(xRight+30, 80);
		parent.strokeWeight(1);
		if (popupLayout.s==3){
			// Draw button to control the map
		   	buttonReset.draw("Reset map",0, 25);
		   	buttonExpand.draw("Expand all",0, 45);
		   	buttonCollapse.draw("Collapse all",0, 65);
		   	
		   	// Background of fileNames
		   	parent.noStroke();
			parent.fill(200,200);
			parent.rect(xRight, 28, parent.width-xRight, nFiles*16+10);
		}
		
		// Print out file names
		parent.textSize(11);
		parent.textAlign(PApplet.LEFT);
		for (int f=0; f<nFiles; f++){
			float yy = 45+f*16;
			String[] str = files.get(f).split("/");
			String nameFile = str[str.length-1];
			Color color = gradient.getGradient(colorScale*(transferID(f)));
			parent.fill(color.getRGB());
			parent.text(nameFile, xRight+20,yy); 
		}	
		
		// Draw popups
		popupLayout.draw(parent.width-198);
		popupPathway.draw(parent.width-298);
		setIntegrator --;  // Set node to circular layout when dragging or changing scales
		
		
		// Draw brushing reaction
		/*
		if (g.getHoverNode()!=null){
			parent.fill(240,220,220,220);
			parent.noStroke();
			parent.rect(iX2.value, iY2.value-30, parent.width-iX2.value, parent.height-iY2.value+40);
			
			Node node =g.getHoverNode();
			drawBrushingNode(parent, node, 250);
		}
		// 	// Draw brushing Edges
		else if (popupLayout.s==3 && Pathway2.bEdges!=null && Pathway2.bEdges.size()>0){
		/*	parent.fill(240,220,220,220);
			parent.noStroke();
			parent.rect(parent.width-500, iY2.value-70, 501, parent.height-iY2.value+70);
			parent.fill(0);
			parent.textSize(11);
			parent.textAlign(PApplet.LEFT);
			parent.text("You are brushing "+Pathway2.bEdges.size()+" causal relationship(s) of " + 
					getDistintReactions(Pathway2.bEdges).size() +" reactions.",parent.width-490,iY2.value-50);
			parent.text("Please click to add these reactions to View -> Reaction View", parent.width-490,iY2.value-35);
			parent.fill(255,0,0);
			parent.text("     "+Pathway2.meassage);
			*/
		/*	
			float yReact=iY2.value;
			//float yReact = 	iY2.value;
			for (int e=0; e<Pathway2.bEdges.size();e++){
				Edge edge =Pathway2.bEdges.get(e);
				yReact = drawBrushingEdge(parent, edge, yReact, 700);   // Draw proteins/complexes in the brushing edges
			}	
			float totalH =  yReact-iY2.value;
			if (totalH>parent.height-250)
				totalH = parent.height-250;
			iY2.target(parent.height-totalH);
			iY2.update();
		}*/
	}	
	
	public static ArrayList<BiochemicalReaction> getDistintReactions(ArrayList<Edge> a) {
		ArrayList<BiochemicalReaction> b = new ArrayList<BiochemicalReaction>();
		for (int e=0;e<a.size();e++){
			 Edge edge = a.get(e);
			 if (!Pathway2.isContainsBiochemicalReaction(b,edge.getFrom().reaction))
				 b.add(edge.getFrom().reaction);
			 if (!Pathway2.isContainsBiochemicalReaction(b, edge.getTo().reaction))
				 b.add(edge.getTo().reaction);
		 }
		return b;
	}
	
		
		
	public void drawPathways() {
		float totalSize=0;
		float rate = 0.5f;
		for (int i=0;i<filePathway.length;i++){
			totalSize += PApplet.pow(filePathway[i].numReactions,rate);
		}
		
		if (rootPathway.isExpanded){
			float currentPos=0;
			bPathway = null;
			
			/*
			for (int i=0;i<filePathway.length;i++){
				float newPos = currentPos+PApplet.pow(filePathway[i].numReactions,rate)/2;
				float al = (newPos/totalSize)*2*PApplet.PI - PApplet.PI/2;
				float xR2 = PathwayView.xCircular + (PathwayView.rCircular+filePathway[i].radiusCenter)*PApplet.cos(al);
				float yR2 = PathwayView.yCircular + (PathwayView.rCircular+filePathway[i].radiusCenter)*PApplet.sin(al);
				float xR3 = PathwayView.xCircular + (PathwayView.rCircular)*PApplet.cos(al);
				float yR3 = PathwayView.yCircular + (PathwayView.rCircular)*PApplet.sin(al);
				filePathway[i].draw(xR2, yR2, xR3, yR3, al);
				currentPos += PApplet.pow(filePathway[i].numReactions,rate);
			}*/
			float xBegin = 35;
			float preR = 0;
			for (int i=0;i<filePathway.length;i++){
				float al =  -PApplet.PI/2;
				float xR2 = xBegin+filePathway[i].radius+preR+15;//filePathway[i].radius;
				float yR2 = PathwayView.yCircular + (PathwayView.rCircular+filePathway[i].radiusCenter)*PApplet.sin(al);
				float xR3 = xR2;
				float yR3 = PathwayView.yCircular + (PathwayView.rCircular)*PApplet.sin(al);
				filePathway[i].draw(xR2, yR2, xR3, yR3, al);
				currentPos += PApplet.pow(filePathway[i].numReactions,rate);
				if (i==1)
					xBegin += filePathway[i].radius+preR+180;
				else if (i==2)
					xBegin += filePathway[i].radius+preR+180;
				//else if (i==4)
				//	xBegin += +filePathway[i].radius+preR+150;
				else 
					xBegin += +filePathway[i].radius+preR+180;
				
				preR = filePathway[i].radius;
				
			}
		}
		else{
			// Print all reactions on a circle
			float beginAl = -PApplet.PI/2;
			for (int f=0;f<filePathway.length;f++){
				ArrayList<Integer> a = filePathway[f].getAllNodeId();
				float sec = (PApplet.sqrt(a.size())/totalSize)*PApplet.PI*1.8f;
				for (int i=0;i<a.size();i++){
					Node node =Graph.nodes.get(a.get(i));
					float al2 = beginAl+((float) i/a.size())*sec;
					if (node==null) return;
					float xR2 = xCircular + (rCircular+node.size/2)*PApplet.cos(al2);
					float yR2 = yCircular + (rCircular+node.size/2)*PApplet.sin(al2);
					Pathway2.setNodePosistion(node, xR2,yR2,al2);	
				}
				beginAl += sec + (PApplet.PI*0.2f)/filePathway.length;
			}
		}
		
		// Check brushing of the root pathway
		isBrushing =false;
		rCentetButton = rCircular/13;
		rootPathway.x = xCircular;
		rootPathway.y = yCircular;
		if (PApplet.dist(xCircular, yCircular, parent.mouseX, parent.mouseY)<rCentetButton){
			isBrushing = true;
		}
		parent.noStroke();
		parent.fill(Pathway2.beginDarknessOfPathways);  
		if (isBrushing)
			parent.fill(220,220,255,200);
		//parent.ellipse(xCircular, yCircular, rCircular*2, rCircular*2);
	
		
		// Draw brushing sub pathway
		parent.noStroke();
		if (bPathway!=null)
			bPathway.drawWhenBrushing();

		
		
	}
		
	
	
	// draw Reactions links
	public float drawBrushingEdge(PApplet parent, Edge edge, float yReact, float ww) {
		float yL=yReact;
		float yR=yReact;
		
		float xL = (parent.width-400-800);
		float xR = xL+ww;
		float gapY = 22;
		float gapYInComplex = 20;
		parent.strokeWeight(1);
		
		Object[] sRight1 = edge.getFrom().reaction.getRight().toArray();
		Object[] sLeft2 = edge.getTo().reaction.getLeft().toArray();
			
		ArrayList<String> a = compareInputOutput(sRight1, sLeft2);
		ArrayList<String> aRef = new ArrayList<String>();
		if (a==null || a.size()==0)
			System.out.println("Can NOT find the common proteins/complexes");
		else{ // Draw common proteins/complexes
			for (int i=0;i<a.size();i++){
				String ref = getRefFromName(sRight1, a.get(i));
				if (ref==null){
					System.out.println("Can NOT find the common proteins/complexes");
				}
				else{
					aRef.add(ref);
				}
			}
			yL = this.drawOutputs(parent, aRef, xL, xR, yReact, yL, gapY, gapYInComplex,true);
		}
		
		ArrayList<String> b = new ArrayList<String>();
		for (int i3=0;i3<sRight1.length;i3++){
			if (!aRef.contains(sRight1[i3].toString()))
				b.add(sRight1[i3].toString());
		}
		
		yL = this.drawOutputs(parent, b, xL, xR, yReact, yL, gapY, gapYInComplex, false);
			
		// Draw reaction node
		Node nodeFrom = edge.getFrom();
		parent.fill(nodeFrom.color.getRed(), nodeFrom.color.getGreen(), nodeFrom.color.getBlue(), 200);
		parent.noStroke();
		parent.ellipse(xL, yReact, nodeFrom.size, nodeFrom.size);
		parent.textSize(16);
		parent.textAlign(PApplet.CENTER);
		parent.text(nodeFrom.reaction.getDisplayName(), xL+30, yReact-nodeFrom.size/2-5);
		
		
		
		
		///////////*********** INPUT
		
		ArrayList<String> c = compareInputOutput(sRight1, sLeft2);
		ArrayList<String> cRef = new ArrayList<String>();
		if (c==null || c.size()==0)
			System.out.println("Can NOT find the common proteins/complexes");
		else{ // Draw common proteins/complexes
			for (int i=0;i<c.size();i++){
				String ref = getRefFromName(sLeft2, c.get(i));
				if (ref==null){
					System.out.println("Can NOT find the common proteins/complexes");
				}
				else{
					cRef.add(ref);
				}
			}
			yR = this.drawInputs(parent, cRef, xL, xR, yReact, yR, gapY, gapYInComplex,true);
		}
		
		
		
		
		yR = PApplet.max(yL, yR);
		
		ArrayList<String> d = new ArrayList<String>();
		for (int i3=0;i3<sLeft2.length;i3++){
			if (!cRef.contains(sLeft2[i3].toString()))
				d.add(sLeft2[i3].toString());
		}
		yR = this.drawInputs(parent, d, xL, xR, yReact, yR, gapY, gapYInComplex,false);
			
		// Draw reaction node TO
		Node nodeTo = edge.getTo();
		parent.fill(nodeTo.color.getRed(), nodeTo.color.getGreen(), nodeTo.color.getBlue(), 200);
		parent.noStroke();
		parent.ellipse(xR, yReact, nodeTo.size, nodeTo.size);
		parent.textSize(16);
		parent.textAlign(PApplet.CENTER);
		parent.text(nodeTo.reaction.getDisplayName(), xR-30, yReact-nodeTo.size/2-5);
		
		return PApplet.max(yL, yR)+20;
		
	 }
	
	// Reduce unidentified names
	public String reduceUnidentifiedName(String name_){
		String name = name_;
		if (name_==null) return "???";
		if (name_.contains("#")){
			System.out.println(name_);
			String[] pieces = name_.split("#");
			name = pieces[pieces.length-1];
		}	
		else if (name_.contains("http:/")){
			System.out.println(name_);
			String[] pieces = name_.split("/");
			name = pieces[pieces.length-1];
		}
		return name;
	}
	
	// Draw output proteins and complexes of a selected reaction  for a brushing edge
	public float drawOutputs(PApplet parent, ArrayList<String> a, float xL, float xR, float yReact, float yL_, float gapY, float gapYInComplex, boolean isCommonElements) {
		float yL = yL_;
		for (int i=0;i<a.size();i++){
			String name = mapProteinRDFId.get(a.get(i));
			
			  if (name!=null){
				  parent.stroke(0);
				  parent.line( xL, yReact, (xL+xR)/2, yL);
				
				  if (!isCommonElements){
					  parent.fill(0);
					  parent.textSize(15);
					  parent.textAlign(PApplet.CENTER);
					  name = reduceUnidentifiedName(name);
					  parent.text(name,(xL+xR)/2, yL+5);
				  }
				   yL+=gapY;
			  }
			  // Complex LEFT
			  else if (mapComplexRDFId_Complex.get(a.get(i))!=null){
				  Complex complex = mapComplexRDFId_Complex.get(a.get(i));
				  ArrayList<String> components = getProteinsInComplex(complex);
				  yL +=gapY/5;
					 
				  float beginY = yL;
				  float sizeYComplex = (components.size()-1)*gapYInComplex;
				  for (int k=0;k<components.size();k++){
					  float y2 = beginY+k*gapYInComplex;
					  parent.stroke(0,0,200);
				      parent.line(xL+(xR-xL)/6f, beginY+sizeYComplex/2, (xL+xR)/2, y2);
				      if (!isCommonElements){
						  parent.fill(0);
						  parent.textSize(14);
						  parent.textAlign(PApplet.CENTER);
						  String name2 = components.get(k);
						  name2 = reduceUnidentifiedName(name2);
						  parent.text(name2,(xL+xR)/2, y2+5);
					  }
				  }
				  
				  parent.stroke(0,0,200);
				  parent.line(xL, yReact, xL+(xR-xL)/6f, beginY+sizeYComplex/2);
				  parent.textAlign(PApplet.CENTER);
				  parent.fill(0,0,200);
				  parent.textSize(14);
				  parent.text(complex.getDisplayName(), xL+(xR-xL)/6f, beginY+sizeYComplex/2+20);
					
				  parent.noStroke();
				  parent.fill(0,0,255);
				  polygon(xL+(xR-xL)/6f, beginY+sizeYComplex/2,6,4);
				
				  yL+=components.size()*gapYInComplex+gapY/5;
			  }
			  else{
			  }
		}	
		return yL;
	}
	
	// Draw input proteins and complexes of a selected reaction  for a brushing edge
	public float drawInputs(PApplet parent, ArrayList<String> a, float xL, float xR, float yReact, float yR_, float gapY, float gapYInComplex, boolean isCommonElements) {
		float yR =yR_;
		for (int i=0;i<a.size();i++){
			String name = mapProteinRDFId.get(a.get(i));
			
			  if (name!=null){
				  parent.stroke(0);
				  parent.line((xL+xR)/2, yR, xR, yReact);
				  if (isCommonElements){
					  parent.fill(240,220,220,240);
					  parent.noStroke();
					  float textWidth = parent.textWidth(name)+4;
					  parent.rect((xL+xR)/2-textWidth/2, yR-5, textWidth, 14);
					  
					  parent.fill(255,0,0);
					  parent.textSize(14);
					  parent.textAlign(PApplet.CENTER);
					  name = reduceUnidentifiedName(name);
					 parent.text(name,(xL+xR)/2, yR+5);
				  }
				  else{
				 	  parent.fill(0);
					  parent.textSize(14);
					  parent.textAlign(PApplet.CENTER);
					  name = reduceUnidentifiedName(name);
					  parent.text(name,(xL+xR)/2, yR+5);
				  }
				  yR+=gapY;
			  }
			  // Complex LEFT
			  else if (mapComplexRDFId_Complex.get(a.get(i))!=null){
				  Complex complex = mapComplexRDFId_Complex.get(a.get(i));
				  ArrayList<String> components = getProteinsInComplex(complex);
				  yR +=gapY/5;
				  float beginY = yR;
				  float sizeYComplex = (components.size()-1)*gapYInComplex;
				  for (int k=0;k<components.size();k++){
					  float y2 = beginY+k*gapYInComplex;
					  parent.stroke(0,0,200);
				      parent.line((xL+xR)/2, y2, xR-(xR-xL)/6f,beginY+sizeYComplex/2);
				      if (isCommonElements){
				    	  String name2 = components.get(k);
				    	  name2 = reduceUnidentifiedName(name2);
						  parent.fill(255,240);
						  parent.noStroke();
						  float textWidth = parent.textWidth(name2)+4;
						  parent.rect((xL+xR)/2-textWidth/2, y2-5, textWidth, 14);
						
				    	  parent.fill(255,0,0);
						  parent.textSize(14);
						  parent.textAlign(PApplet.CENTER);
						  parent.text(name2.replace("6", "0"),(xL+xR)/2, y2+5);
				      }	  
				      else{
				    	  parent.fill(0);
						  parent.textSize(14);
						  parent.textAlign(PApplet.CENTER);
						  String name2 = components.get(k);
						  name2 = reduceUnidentifiedName(name2);
						  parent.text(name2,(xL+xR)/2, y2+5);
				      }
				  }
				  
				  parent.stroke(0,0,200);
				  parent.line(xR-(xR-xL)/6f, beginY+sizeYComplex/2, xR, yReact);
				  parent.textAlign(PApplet.CENTER);
				  parent.fill(0,0,200);
				  parent.text(complex.getDisplayName(),  xR-(xR-xL)/6f, beginY+sizeYComplex/2+20);
					
				  parent.noStroke();
				  parent.fill(0,0,255);
				  polygon(xR-(xR-xL)/6f,beginY+sizeYComplex/2,6,4);
				  yR+=components.size()*gapYInComplex+gapY/5;
			  }
			  else{
			  }
		  }
		return yR;
	}
		
	
	
	
	
	// draw Reactions links
	public void drawBrushingNode(PApplet parent, Node node, float ww) {
		Object[] sLeft = node.reaction.getLeft().toArray();
		float yReact = 	iY2.value;
		float yL=yReact;
		float yR=yReact;
		float xL = iX2.value+(parent.width-iX2.value-ww)/2;
		float xR = xL+ww;
		float gapY = 17;
		float gapYInComplex = 15;
		parent.strokeWeight(1);
		float maxTextWidth = 0;
		for (int i3=0;i3<sLeft.length;i3++){
			String name = mapProteinRDFId.get(sLeft[i3].toString());
			  if (name!=null){
				  parent.stroke(0);
				  parent.line(xL, yL, (xL+xR)/2, yReact);
				  parent.fill(0);
				  parent.textSize(11);
				  parent.textAlign(PApplet.RIGHT);
				  name = reduceUnidentifiedName(name);
				  parent.text(name,xL, yL+5);
				  float tWidth = parent.textWidth(name);
				  if (tWidth>maxTextWidth)
					  maxTextWidth = tWidth;
				  yL+=gapY;
			  }
			  // Complex LEFT
			  else if (mapComplexRDFId_Complex.get(sLeft[i3].toString())!=null){
				  Complex complex = mapComplexRDFId_Complex.get(sLeft[i3].toString());
				  ArrayList<String> components = getProteinsInComplex(complex);
				  yL +=gapY/2;
				  float beginY = yL;
				  float sizeYComplex = (components.size()-1)*gapYInComplex;
				  for (int i=0;i<components.size();i++){
					  float y2 = beginY+i*gapYInComplex;
					  parent.stroke(0,0,200);
				      parent.line(xL, y2, xL+(xR-xL)/6f,beginY+sizeYComplex/2);
					  parent.fill(0);
					  parent.textSize(11);
					  parent.textAlign(PApplet.RIGHT);
					  
					  String name2 = components.get(i);
					  name2 = reduceUnidentifiedName(name2);
					  parent.text(name2,xL, y2+5);
					  float tWidth = parent.textWidth(name2);
					  if (tWidth>maxTextWidth)
						  maxTextWidth = tWidth;
					  
			 	  }
				  
				  parent.stroke(0,0,200);
				  parent.line(xL+(xR-xL)/6f, beginY+sizeYComplex/2, xL+(xR-xL)/2, yReact);
				  
				  parent.noStroke();
				  parent.fill(0,0,150);
				  polygon(xL+(xR-xL)/6f,beginY+sizeYComplex/2,6,4);
				
				  yL+=components.size()*gapYInComplex+gapY/4;
			  }
			  else{
			  }
		  }
		Object[] sRight = node.reaction.getRight().toArray();
		for (int i3=0;i3<sRight.length;i3++){
			  String name = mapProteinRDFId.get(sRight[i3].toString());
			  if (name!=null){
				  parent.stroke(0);
				  parent.line( xR, yR, (xL+xR)/2, yReact);
				
				  parent.fill(0);
				  parent.textSize(11);
				  parent.textAlign(PApplet.LEFT);
				  name = reduceUnidentifiedName(name);
			      parent.text(name,xR, yR+5);
				  float tWidth = parent.textWidth(name);
				  if (tWidth>maxTextWidth)
					  maxTextWidth = tWidth;
				  yR+=gapY;
			  }
			  // Complex LEFT
			  else if (mapComplexRDFId_Complex.get(sRight[i3].toString())!=null){
				  Complex complex = mapComplexRDFId_Complex.get(sRight[i3].toString());
				  ArrayList<String> components = getProteinsInComplex(complex);
				  yR +=gapY/2;
					 
				  float beginY = yR;
				  float sizeYComplex = (components.size()-1)*gapYInComplex;
				  for (int i=0;i<components.size();i++){
					  float y2 = beginY+i*gapYInComplex;
					  parent.stroke(0,100,0);
				      parent.line(xL+(xR-xL)*5/6f, beginY+sizeYComplex/2, xR, y2);
					  parent.fill(0);
					  parent.textSize(11);
					  parent.textAlign(PApplet.LEFT);
					  
					  String name2 = components.get(i);
					  name2 = reduceUnidentifiedName(name2);
				      parent.text(name2,xR, y2+5);
					  float tWidth = parent.textWidth(name2);
					  if (tWidth>maxTextWidth)
						  maxTextWidth = tWidth;
				  }
				  
				  parent.stroke(0,0,200);
				  parent.line(xL+(xR-xL)/2, yReact, xL+(xR-xL)*5/6f, beginY+sizeYComplex/2);
				 	 
				  parent.noStroke();
				  parent.fill(0,0,150);
				  polygon(xL+(xR-xL)*5/6f, beginY+sizeYComplex/2,6,4);
				
				  yR+=components.size()*gapYInComplex+gapY;
			  }
			  else{
			  }
		  }
		
		int sat =(155+parent.frameCount*11%100);
		parent.fill(node.color.getRed(), node.color.getGreen(), node.color.getBlue(), sat);
		parent.noStroke();
		parent.ellipse((xL+xR)/2, yReact, node.size, node.size);
		parent.textAlign(PApplet.CENTER);
		parent.text(node.reaction.getDisplayName(), (xL+xR)/2, yReact-node.size/2-5);
		
		float reactionNameWidth = (parent.textWidth(node.reaction.getDisplayName())-ww)/2;
		if (reactionNameWidth>maxTextWidth)
			maxTextWidth = reactionNameWidth;
		
		float gap=PApplet.max(yL,yR)-yReact;
		iY2.target(parent.height-gap);
		iY2.update();
		iX2.target(parent.width-ww-2*maxTextWidth-50);
		iX2.update();
	 }
	
	
	public static ArrayList<String> getProteinsInComplex(Complex complex){	
		  ArrayList<String> components = new ArrayList<String>(); 
		  Object[] s2 = complex.getComponent().toArray();
		  for (int i=0;i<s2.length;i++){
			  String name = mapProteinRDFId.get(s2[i].toString());
			  if (name!=null)
				  components.add(name);
			  else {
				  if (mapComplexRDFId_Complex.get(s2[i].toString())==null){
					  String name2 = s2[i].toString();
					  components.add(name2);
				  }
				  else{
					  Complex subcomplex = mapComplexRDFId_Complex.get(s2[i].toString());
					  ArrayList<String> s4 = getProteinsInComplex(subcomplex);
					  for (int k=0;k<s4.size();k++){
						  components.add(s4.get(k));
					  }
				  }
			  }
		 }
		 return components;
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
	 public void drawCenter(float x_, float y_, float r_){
		parent.fill(20);
		parent.noStroke();
	  	parent.ellipse(x_, y_, r_*2, r_*2);
		
	  	parent.strokeWeight(r_/10);
		parent.stroke(150);
		parent.line(x_-(r_*1.6f)/2,y_, x_+(r_*1.6f)/2,y_);
		if (!rootPathway.isExpanded)
			parent.line(x_,y_-(r_*1.6f)/2, x_,y_+(r_*1.6f)/2);
	}
		
	
	public void drawTree() {
		parent.fill(0);
		parent.textSize(12);
		parent.textAlign(PApplet.LEFT);
		parent.text(nFiles+" files", 3, parent.height/2+5);
		float[] yF = new float[nFiles];
		float[] nF = new float[nFiles];
		for (int i=0; i<Graph.nodes.size(); i++){
			int f = rectFileList.get(i);
			yF[f] += g.nodes.get(i).iY.value;
			nF[f] ++;
		}
		parent.stroke(0);
		parent.strokeWeight(1);
		parent.textSize(11);
		for (int f=0; f<nFiles; f++){
			float yy = yF[f]/nF[f];
			parent.line(40,parent.height/2,250,yy);
			
		}
		for (int i=0; i<Graph.nodes.size(); i++){
			int f = rectFileList.get(i);
			float yy = yF[f]/nF[f];
			Color color = gradient.getGradient(colorScale*(transferID(f)));
			parent.stroke(color.getRed(), color.getGreen(), color.getBlue(),60);
			parent.line(250,yy, g.nodes.get(i).iX.value,g.nodes.get(i).iY.value);
		}
			
		for (int f=0; f<nFiles; f++){
			float yy = yF[f]/nF[f];
			String[] str = files.get(f).split("/");
			String nameFile = str[str.length-1];
			Color color = gradient.getGradient(colorScale*(transferID(f)));
			parent.fill(color.getRGB());
			parent.text(nameFile, 250,yy); 
		}
	}
		
	public void orderTopological() {
		ArrayList<Integer> doneList = new ArrayList<Integer>();
		ArrayList<Integer> circleList = new ArrayList<Integer>();
		
		int count = 0;
		int r = getNoneUpstream(doneList);
		while (count<Graph.nodes.size()){
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
		
		
		// Compute nonCausality reaction
		ArrayList<Integer> nonCausalityList = new ArrayList<Integer>();
		for (int i=0;i<doneList.size();i++){
			int index = doneList.get(i);
			if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0)
				nonCausalityList.add(index);
		}
		
		float totalH = parent.height-15;
		float itemH2 = totalH/(Graph.nodes.size()+circleList.size()-nonCausalityList.size()*0.8f+1);
		float circleGap = itemH2;
		float circleGapSum = 0;
		
		int count2 = 0;
		int count3 = 0;
		float yStartCausality = 10 +(Graph.nodes.size()-nonCausalityList.size()+circleList.size()+1)*itemH2;
		for (int i=0;i<doneList.size();i++){
			int index = doneList.get(i);
			// Compute nonCausality reaction
			if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0){
				yTopological[index] =  yStartCausality +count3*itemH2*0.2f;
				count3++;
			}	
			else{
				if(circleList.contains(index)){
					yTopological[index] = circleGapSum+ 10+count2*itemH2+circleGap;
					circleGapSum +=circleGap;
				}
				else{
					yTopological[index] = circleGapSum+10+count2*itemH2;
				}
				count2++;
			}
		}
	}
	
	public void orderTree() {
		yTree =  new float[Graph.nodes.size()];
		for (int i=0; i<Graph.nodes.size();i++){
			float totalH = parent.height-10;
			float itemH2 = (totalH-10*nFiles)/(Graph.nodes.size()-1);
			yTree[i] = 10+i*itemH2+10*rectFileList.get(i);
		}
	}
		
	public int getNoneUpstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<Graph.nodes.size();i++){
			if (doneList.contains(i)) continue;
			ArrayList<Integer> up = this.getDirectUpstream(i);
			if (up.size()==0)  {//No upstream
				a.add(i);
			}	
		}
		if (a.size()>0){
			return getReactionMinDownstreamIn(a);
		}
		else{
			ArrayList<Integer> b = new ArrayList<Integer>();
			for (int i=0;i<Graph.nodes.size();i++){
				if (doneList.contains(i)) continue;
				ArrayList<Integer> up = this.getDirectUpstream(i);
				if (isContainedAllUpInDoneList(up,doneList)){  // Upstream are all in the doneList;
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
	
	public int getReactionMaxDownstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<Graph.nodes.size();i++){
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
	
	public ArrayList<Integer> getReactionWithSameInput(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = Graph.nodes.get(r).reaction;
		Object[] sLeft1 = rectSelected.getLeft().toArray();
		for (int g=0;g<Graph.nodes.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = Graph.nodes.get(g).reaction;
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sLeft1, sLeft2);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	public ArrayList<Integer> getReactionWithSameOutput(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = Graph.nodes.get(r).reaction;
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<Graph.nodes.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = Graph.nodes.get(g).reaction;
			Object[] sRight2 = rect2.getRight().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sRight2);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	
	public ArrayList<Integer> getDirectDownstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = Graph.nodes.get(r).reaction;
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<Graph.nodes.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = Graph.nodes.get(g).reaction;
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	
	public ArrayList<Integer> getDirectUpstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = Graph.nodes.get(r).reaction;
		Object[] sLeft = rectSelected.getLeft().toArray();
		
		// List current reaction
		for (int g=0;g<Graph.nodes.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = Graph.nodes.get(g).reaction;
			Object[] sRight2 = rect2.getRight().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight2, sLeft);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	public void doLayout() {
		// calculate forces on each node
		// calculate spring forces on each node
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			ArrayList edges = (ArrayList) g.getEdgesFrom(n);
			n.setForce(new Vector3D(0, 0, 0));
			for (int j = 0; edges != null && j < edges.size(); j++) {
				Edge e = (Edge) edges.get(j);
				Vector3D f = e.getForceFrom();
				n.applyForce(f);
			}
			edges = (ArrayList) g.getEdgesTo(n);
			for (int j = 0; edges != null && j < edges.size(); j++) {
				Edge e = (Edge) edges.get(j);
				Vector3D f = e.getForceTo();
				n.applyForce(f);
			}
		}
		

		// calculate the anti-gravitational forces on each node
		// this is the N^2 shittiness that needs to be optimized
		// TODO: at least make it N^2/2 since forces are symmetrical
		for (int i = 0; i < g.getNodes().size(); i++)  {
			Node a = (Node) g.getNodes().get(i);
			for (int j = 0; j < g.getNodes().size(); j++) {
				Node b = (Node) g.getNodes().get(j);
				if (b != a) {
					float dx = b.getX() - a.getX();
					float dy = b.getY() - a.getY();
					float r = PApplet.sqrt(dx * dx + dy * dy);
					// F = G*m1*m2/r^2

					float f = 5*(a.getMass() * b.getMass() / (r * r));
					if (a.degree>0){
						f = PApplet.sqrt(a.degree)*f;
					}
					if (r > 0) { // don't divide by zero.
						Vector3D vf = new Vector3D(-dx * f, -dy * f, 0);
						a.applyForce(vf);
					}
				}
			}
		}
		
		float xCenter = xRight/2;
		float yCenter = parent.height/2;
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node a = (Node) g.getNodes().get(i);
			float dx = xCenter - a.getX();
			float dy = yCenter - a.getY();
			float r2 = dx * dx + dy * dy;
			float f =  r2/5000000;
			if (a.degree>0){
				Vector3D vf = new Vector3D(dx * f, dy * f, 0);
				a.applyForce(vf);
			}
		}

		// move nodes according to forces
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			if (n != g.getDragNode()) {
				n.setPosition(n.getPosition().add(n.getForce()));
			}
		}
	}
	
	
	public static float computeAlpha(int r){
		return PApplet.PI -((float)r)/(Graph.nodes.size())*2*PApplet.PI;
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
			else if (mapComplexRDFId.get(ref1)!=null){
				String complexName1 = mapComplexRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String complexName2 = mapComplexRDFId.get(ref2);
					if (complexName2!=null && complexName1.equals(complexName2)){
						 results.add(complexName1);
					}	
				}
			}
			
		}
		return results;
	}
	
	public String getRefFromName(Object[] a, String name){
		for (int i=0; i<a.length;i++){
			String ref1 = a[i].toString();
			if (mapProteinRDFId.get(ref1)!=null){
				String proteinName1 = mapProteinRDFId.get(ref1);
				if (proteinName1.equals(name))
					return ref1;
			}
			else if (mapComplexRDFId.get(ref1)!=null){
				String complexName1 = mapComplexRDFId.get(ref1);
				if (complexName1.equals(name))
					return ref1;
			}
			
		}
		return null;
	}
	
	
	
	

	
	
	public void keyPressed() {
		if (g!=null){
			ArrayList<Node> nodes= g.getNodes();
			/*if (parent.key == '+') {
				//g.removeNode(g.getNodes().get(1));
				return;
			} else if (parent.key == '-') {
				g.removeNode(nodes.get(4));
				return;
			}*/
		}
	}

	public void mousePressed() {
		if (g==null) return;
		g.setDragNode(null);
		slider2.checkSelectedSlider1();
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			if (n.containsNode(parent.mouseX, parent.mouseY)) {
				g.setDragNode(n);
			}
		}
	}
	
	public void mouseReleased() {
		if (g==null) return;
		g.setDragNode(null);
		slider2.checkSelectedSlider2();
	}

	public void mouseMoved() {
		if (g!=null && g.getDragNode() == null) {
			g.setHoverNode(null);
			for (int i = 0; i < g.getNodes().size(); i++) {
				Node n = (Node) g.getNodes().get(i);
				if (n.containsNode(parent.mouseX, parent.mouseY)) {
					g.setHoverNode(n);
				}
			}
			Pathway2.resetBrushingEdges();
		}
		popupLayout.mouseMoved();
	}
	
	public void mouseClicked() {
		if (g==null) return;
		
		if (Pathway2.bEdges!=null && Pathway2.bEdges.size()>0){
			ReactionFlow_1_1.thread12 =new Thread(ReactionFlow_1_1.loader12);
			ReactionFlow_1_1.thread12.start();
			
			System.out.println("mouseClicked()  bEdges");
		}
		else if (buttonExpand.b){
			rootPathway.expandAll();
		}
		else if (buttonCollapse.b){
			rootPathway.collapseAll();
		}
		else if (buttonReset.b){
			resetPosistion();
			scale = 1f;
			updateScale();
			setIntegrator = 4;
		}
		else if(isBrushing){
			rootPathway.isExpanded = !rootPathway.isExpanded;
			if (!rootPathway.isExpanded)
				rootPathway.collapseAll();   // When we close a pathway, close all sub-pathway recursively
		}
		else if (bPathway!=null){
			bPathway.isExpanded=!bPathway.isExpanded;
			if (!bPathway.isExpanded)
				bPathway.collapseAll();   // When we close a pathway, close all sub-pathway recursively
		}
		else if (popupLayout.b>=0){
			popupLayout.mouseClicked();

			if (popupLayout.s==0){
				orderTree();
			}
			else if (popupLayout.s==1){
				iTransition.target(PApplet.PI);
				thread5 = new Thread(loader5);
				thread5.start();
			}
			else if (popupLayout.s==2)
				iTransition.target(0);
			else  if (popupLayout.s==3){
				iTransition.target(1);
			}
		}
		else if(popupPathway.bPopup){
			popupPathway.mouseClicked();
		}
		else if (checkName.b)
			checkName.mouseClicked();
		else if (checkEdges.b)
			checkEdges.mouseClicked();
		else{
			g.setSelectedNode(null);
			for (int i = 0; i < g.getNodes().size(); i++) {
				Node n = (Node) g.getNodes().get(i);
				if (n.containsNode(parent.mouseX, parent.mouseY)) {
					g.setSelectedNode(n);
				}
			}
		}
	}

	public void mouseDragged() {
		slider2.checkSelectedSlider3();
		setIntegrator = 2;
		if (g==null) return;
		if (g.getDragNode() != null) {
			g.getDragNode()
					.setPosition(
							new Vector3D(parent.mouseX, parent.mouseY, 0));
		}
		else{
			xCircular += (parent.mouseX - parent.pmouseX)*PathwayView.scale;
			yCircular += (parent.mouseY - parent.pmouseY)*PathwayView.scale;
		}
	}
	

	// Thread for grouping
	class ThreadLoader5 implements Runnable {
		PApplet parent;
		public ThreadLoader5(PApplet p) {
			parent = p;
		}
		public void run() {
			orderTopological();
		}
	}	
	
}
	