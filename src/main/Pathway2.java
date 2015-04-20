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

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

import GraphLayout.Edge;
import GraphLayout.Graph;
import GraphLayout.Node;


public class Pathway2{
  public Pathway2 parentPathway;
  public ArrayList<Pathway2> subPathwayList;
  public ArrayList<BiochemicalReaction> reactList;
  public ArrayList<Integer> nodeIdList;
  public int fileId = -1; 
  public int level = -1; 
  public String displayName = "?";
  
  // for drawing
  public float radius = 0;
  public float radiusCenter = 0;
  public int numReactions = 0;
  public boolean isExpanded = true;
  
  public float x = 100;
  public float y = 100;
  public float xEntry = 500;
  public float yEntry = 500;
  public float xCenterButton = 200;
  public float yCenterButton = 200;
  public float al = 0;
  private PApplet parent = null;
  public static float beginDarknessOfPathways = 180;
  
  // Draw the button threading
  public ArrayList<Edge> linkToParent;
  public ArrayList<Edge> linkFromParent;
  public ArrayList<Edge>[][] linkSubpathway = null;
  public ArrayList<Edge>[] linkReactionFromThisPathway = null;
  public ArrayList<Edge>[] linkReactionToThisPathway = null;
  public ArrayList<Integer> nodeListAll;
  public ArrayList<BiochemicalReaction> reactionListAll;
  public boolean isDrawn = false;
  
  // Brushing edges
  public static ArrayList<Edge> bEdges = new ArrayList<Edge>();
  public static String meassage = "";
  
  // Constructor
  Pathway2(PApplet parent_, Pathway2 parentPathway_, int f_, String dName, int level_, boolean isExpande_){
	  parent = parent_;
	  parentPathway = parentPathway_;
	  fileId=f_;
	  level = level_;
	  displayName = dName;
	  isExpanded = isExpande_;
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<BiochemicalReaction>();
	  nodeIdList = new ArrayList<Integer>();
  }
  
  public int computeSize(){
	  numReactions = reactList.size();
	  for (int i=0; i<subPathwayList.size();i++){
		  numReactions+=subPathwayList.get(i).computeSize();
	  }
	   return numReactions;
  }
  
  public void expandAll(){
	  isExpanded = true;
	  for (int p=0;p<subPathwayList.size();p++){
		  subPathwayList.get(p).expandAll();
	  }
  }
  public void collapseAll(){
	  isExpanded = false;
	  for (int p=0;p<subPathwayList.size();p++){
		  subPathwayList.get(p).collapseAll();
	  }
  }
		
		
  public void draw(float x_, float y_, float xEntry_, float yEntry_, float al_){
	  x=x_;
	  y=y_;
	  xEntry = xEntry_;
	  yEntry = yEntry_;
	  al = al_;
	  radius = PApplet.pow(numReactions,0.65f)*PathwayView.scale;
	  radiusCenter = radius/3f; // Set the distance to the parent pathway
	  parent.noStroke();
	  	if (isExpanded)
	  		drawExpanded();
	  	else 
	  		drawUnexpanded();
	  	
		if (PathwayView.bPathway==null && PApplet.dist(xCenterButton, yCenterButton, parent.mouseX, parent.mouseY)<radiusCenter){
			PathwayView.bPathway = this;
		}
		else{
				float v = beginDarknessOfPathways-(level)*16;  
		  	if (v<0)
		  		v=0;
			parent.fill(v);
			parent.strokeWeight(0.5f);
			parent.stroke(255);
			parent.ellipse(x, y, radius*2, radius*2);
			
		}
	}	
  public void drawWhenBrushing(){
	  parent.fill(255,220,255,230);
	  parent.noStroke();
	  parent.ellipse(x, y, radius*2, radius*2);
	  parent.fill(0);
	  parent.textAlign(PApplet.CENTER);
	  parent.textSize(12);
	  parent.text(displayName,x,y-10);
	  drawCenter(false);
  }
  
  	//Draw center
	public void drawCenter(boolean isRecursive){
		if (isRecursive){
			for (int p=0;p<subPathwayList.size();p++){
				subPathwayList.get(p).drawCenter(isRecursive);
			}
		}
		
		if ((PathwayView.g.getHoverNode()!=null && !isDrawn)   // Brushing a node
				|| (Pathway2.bEdges!=null &&Pathway2.bEdges.size()>0 && !isDrawn)    // Brushing a link
				|| ( PathwayView.g.getHoverNode()==null&& !parentPathway.isExpanded)) {
			return;
		}
		Color color2 = PathwayView.getColor(fileId).darker().darker();
		float sat = 200+level*10;
	  	if (sat>255)
	  		sat=255;
	  	parent.fill(color2.getRed(),color2.getGreen(),color2.getBlue(),sat);
	  	xCenterButton = x-radiusCenter/2*PApplet.cos(al);
	  	yCenterButton = y-radiusCenter/2*PApplet.sin(al);
	  	parent.noStroke();
	  	parent.ellipse(xCenterButton, yCenterButton, radiusCenter, radiusCenter);
		
	  	parent.strokeWeight(radiusCenter/20);
		parent.stroke(150);
		parent.line(xCenterButton-(radiusCenter*0.8f)/2,yCenterButton, xCenterButton+(radiusCenter*0.8f)/2,yCenterButton);
		if (!isExpanded)
			parent.line(xCenterButton,yCenterButton-(radiusCenter*0.8f)/2, xCenterButton,yCenterButton+(radiusCenter*0.8f)/2);
		
		// draw file pathway name in the first level
		parent.textSize(12);
		if (level==1 ){//&& !isExpanded){
			if (-PApplet.PI/2<=al && al<PApplet.PI/2){
				parent.textAlign(PApplet.CENTER);
				parent.textSize(20);
				parent.translate(x,y);
			//	parent.rotate(al);
				parent.fill(255,100);
				parent.text(displayName, 4, 5);
				parent.fill(PathwayView.getColor(fileId).getRGB());
				parent.text(displayName, 3, 4);
			//	parent.rotate(-al);
				parent.translate(-x,-y);
			}
			else{
				parent.textAlign(PApplet.RIGHT);
				parent.translate(x,y);
				parent.rotate(al-PApplet.PI);
				parent.fill(255,100);
				parent.text(displayName, -2, 4);
				parent.fill(PathwayView.getColor(fileId).getRGB());
				parent.text(displayName, -3, 3);
				parent.rotate(-(al-PApplet.PI));
				parent.translate(-x,-y);
			}
		}
		
  }
		
		
  
  public void drawUnexpanded(){
	  ArrayList<Integer> a = getAllNodeId();
	  float numNode = a.size();  // Number of points on the circles including reactions and pathways 
	  
	  // Draw reactions
	  for (int i=0; i<a.size();i++){
		  Node node = Graph.nodes.get(a.get(i));
		  float al2 = al+PApplet.PI*0.55f -(i+1f)/(numNode+1f)*PApplet.PI*1.1f;  // Right
		  //System.out.println("node="+node);
		  if (node==null) return;
		  float xR2 = x + (radius+node.size/2)*PApplet.cos(al2);
		  float yR2 = y + (radius+node.size/2)*PApplet.sin(al2);
		  setNodePosistion(node, xR2,yR2,al2);
	  }
  }
  
  // Does not check redundant reactions
  public ArrayList<BiochemicalReaction> getAllReaction(){
	  ArrayList<BiochemicalReaction> a =  new ArrayList<BiochemicalReaction>();
	  for (int i=0; i<reactList.size();i++){
		  a.add(reactList.get(i));
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<BiochemicalReaction> b = subPathwayList.get(p).getAllReaction();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }	
  
  // Does not check redundant reactions
  public ArrayList<Integer> getAllNodeId(){
	  ArrayList<Integer> a =  new ArrayList<Integer>();
	  for (int i=0; i<nodeIdList.size();i++){
		  a.add(nodeIdList.get(i));
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<Integer> b = subPathwayList.get(p).getAllNodeId();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }	
  
  public void drawExpanded(){
		  float numSect = nodeIdList.size();  // Number of points on the circles including reactions and pathways 
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			   numSect += PApplet.pow(pathway.numReactions,1f);
		  }
		  // Draw reactions
		  int countReactionLeft = 0;
		  int countReactionRight = 0;
		  
		  float add = PApplet.map(subPathwayList.size(), 0, 18, 0, 1);
		  float range = 0.8f+add;
		  
		  float leftAl = al-PApplet.PI*range/2;
		  float rightAl = al+PApplet.PI*range/2;
		  for (int i=0; i<nodeIdList.size();i++){
			  Node node = Graph.nodes.get(nodeIdList.get(i));
			  float al2=0;
			  if (i%2==1){
				  al2 = al+PApplet.PI*range/2 -(countReactionRight+1f)/(numSect+1f)*PApplet.PI*range;  // Right
				  rightAl = al2;
				  countReactionRight++;
			  }
			  else{
			  	  al2 = al-PApplet.PI*range/2 +(countReactionLeft+1f)/(numSect+1f)*PApplet.PI*range;
			  	  leftAl = al2;
				  countReactionLeft++;
			  }
			  //System.out.println("node="+node);
			  if (node==null) return;
			  float xR2 = x + (radius+node.size/2)*PApplet.cos(al2);
			  float yR2 = y + (radius+node.size/2)*PApplet.sin(al2);
			  setNodePosistion(node, xR2,yR2,al2);
		  }
		  
		  // Draw subpathway
  		  float total = 0;
		  float dif =(rightAl-leftAl)*0.96f;
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			  total+=PApplet.pow(pathway.numReactions,0.5f);
		  }
		  
		  
		  Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			  float score = pathway.getNumberOfLevel()*1000+pathway.getMaxBranches()*50+pathway.getAllNodeId().size();
			  if (pathway.subPathwayList.size()>0 || pathway.reactList.size()>0)
				  unsortMap.put(i, score);
		  }	  
		  
			
		  Map<Integer, Float> sortedMap = sortByComparator2(unsortMap,false);
		  ArrayList<Integer> a =  new ArrayList<Integer>();
		  int i5 = 0;
		  for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int index = entry.getKey();
				a.add(a.size()/2, index);
				i5++;
		  }
		  	
		  // Draw subpathways
		  float sum = 0;
		  for (int j=0; j<a.size();j++){
			  int index =  a.get(j);
			  Pathway2 pathway = subPathwayList.get(index);
			  float percent = (sum+PApplet.pow(pathway.numReactions,0.5f)/2)/total;
			  float al = leftAl +percent*dif+(rightAl-leftAl)*0.03f;
			  float xR2 = x + (radius+pathway.radiusCenter)*PApplet.cos(al);
			  float yR2 = y + (radius+pathway.radiusCenter)*PApplet.sin(al);
			  float xR3 = x + (radius)*PApplet.cos(al);
			  float yR3 = y + (radius)*PApplet.sin(al);
			  pathway.draw(xR2, yR2, xR3, yR3, al);
			  sum+=PApplet.pow(pathway.numReactions, 0.5f);
		  }
  }
  
//Sort Reactions by score (average positions of proteins)
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

	public int getNumberOfLevel(){
		  if (subPathwayList.size()==0) return 1;
		  int level = 0;
		  for (int i=0; i<subPathwayList.size();i++){
			  int l = subPathwayList.get(i).getNumberOfLevel();
			  if(l>level)
				  level = l;
		  }
		  return (level+1);		
	}
	public int getMaxBranches(){
		  int num = subPathwayList.size();
		  for (int i=0; i<subPathwayList.size();i++){
			  int b = subPathwayList.get(i).getMaxBranches();
			  if(b>num)
				  num = b;
		  }
		  
		  return num;		
	}
  
  
  
  public static void setNodePosistion(Node node, float xR2, float yR2, float al2){
	  node.iAlpha.target(al2);
	  node.iX.target(xR2);
	  node.iY.target(yR2);
	  if (PathwayView.setIntegrator>0){
		  node.iAlpha.set(al2);
		  node.iX.set(xR2);
		  node.iY.set(yR2);
	  }
  }
  

	// Every reaction associated to a node id in the nodes of Graph class
	public int setNodeId(int nodeId) {
		int newId = nodeId;
		for (int i=0;i<reactList.size();i++){
			nodeIdList.add(newId);
			newId++;
		}
		for (int i=0;i<subPathwayList.size();i++){
			Pathway2 subpathway = subPathwayList.get(i);
			newId = subpathway.setNodeId(newId);
		}
		return newId;
	}
	
	
	// Every reaction associated to this pathway should contain this pathway as parent pathway
	public void setNodePathway() {
		for (int i=0;i<nodeIdList.size();i++){
			Graph.nodes.get(nodeIdList.get(i)).parentPathway= this;
		}
		for (int i=0;i<subPathwayList.size();i++){
			Pathway2 subpathway = subPathwayList.get(i);
			subpathway.setNodePathway();
		}
	}
	
			
	

  public boolean isContainReaction(String rName){
	  for (int r=0;r<reactList.size();r++){
		  String name = reactList.get(r).getDisplayName();
		  if (name==null) continue;
 		  if (name.equals(rName))
			  return true;
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  Pathway2 path = subPathwayList.get(p);
		  boolean result = path.isContainReaction(rName);
		  if (result)
			  return true;
	  }
	  return false;
  }
  
  public boolean isContainPathway(String pName){
	  //if (pName.equals(displayName))
	  //	  return true;
	  for (int p=0;p<subPathwayList.size();p++){
		  Pathway2 path = subPathwayList.get(p);
		  if (path.displayName.equals(pName) || path.isContainPathway(pName))
			  return true;
	  }
	  return false;
  }
  
  public ArrayList<Pathway2> printRecursively(){
	  ArrayList<Pathway2> a = new ArrayList<Pathway2>();
	  a.add(this);	
	  
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<Pathway2> b = subPathwayList.get(p).printRecursively();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }
  
  @SuppressWarnings("unchecked")
  public void resetLinkParent(){
	  linkToParent = new ArrayList<Edge>();
	  linkFromParent = new ArrayList<Edge>();
	  linkSubpathway =  new ArrayList[subPathwayList.size()][subPathwayList.size()];
	  for (int p1=0;p1<subPathwayList.size();p1++){
		  for (int p2=0;p2<subPathwayList.size();p2++){
			  linkSubpathway[p1][p2] = new ArrayList<Edge>();
		  }		
	  }
	  nodeListAll = getAllNodeId();
	  reactionListAll = getAllReaction();
	  linkReactionFromThisPathway = new ArrayList[nodeListAll.size()];
	  linkReactionToThisPathway = new ArrayList[nodeListAll.size()];
	  for (int p=0;p<nodeListAll.size();p++){
		  linkReactionFromThisPathway[p] = new ArrayList<Edge>();
		  linkReactionToThisPathway[p] = new ArrayList<Edge>(); 
	  }
	  isDrawn = false;
	  for (int p=0;p<subPathwayList.size();p++){
		  subPathwayList.get(p).resetLinkParent();
	  }
  }
  
  public static void resetBrushingEdges(){
	  bEdges = new ArrayList<Edge>();  
	  meassage ="";
  }
	
  public static boolean isContainsEdge(ArrayList<Edge> bEdges, Edge edge){
	  for (int e=0; e<bEdges.size();e++){
		  String rName1 = bEdges.get(e).getFrom().reaction.getDisplayName();
		  String rName2 = bEdges.get(e).getTo().reaction.getDisplayName();
		  String rNameL = edge.getFrom().reaction.getDisplayName();
		  String rNameR = edge.getTo().reaction.getDisplayName();
		  if (rName1.equals(rNameL) && rName2.equals(rNameR)){
			  return true;
		  }
	  }
	  return false;
  }
  
  	public static boolean isContainsBiochemicalReaction(ArrayList<BiochemicalReaction> a, BiochemicalReaction reaction) {
  		for (int i=0; i<a.size();i++){
  		  String rName1 = a.get(i).getDisplayName();
  		  String rName2 = reaction.getDisplayName();
  		  if (rName1.equals(rName2)){
  			  return true;
  		  }
  	  }
  	  return false;
	}
		
  
   
  public void drawLinkParent(){
	  for (int p=0;p<subPathwayList.size();p++){
		  subPathwayList.get(p).drawLinkParent();
	  }
	  
	  if (parentPathway==null)
		  return;
	   
	  drawSubpathwayLinks();
	  
	  if (linkToParent.size()>0){
		  parent.stroke(PathwayView.sat,PathwayView.sat,0);
		  drawArc(xEntry, yEntry, parentPathway.x, parentPathway.y,linkToParent, true);    // to parent
		  parentPathway.isDrawn = true; // to draw the center button
		  this.isDrawn = true; // to draw the center button
			
	  }
	  if (linkFromParent.size()>0){
		  parent.stroke(0);
		  drawArc(xEntry, yEntry, parentPathway.x, parentPathway.y,linkFromParent,false);
		  parentPathway.isDrawn = true; // to draw the center button
		  this.isDrawn = true; // to draw the center button
	  }
	  
	  for (int i=0;i<nodeListAll.size();i++){
		  if (linkReactionToThisPathway[i].size()>0){  // disable when level =1 to simplify the view
			  parent.stroke(0,0,0);
			  Node node = Graph.nodes.get(nodeListAll.get(i));
			  
			  float x2 = node.iX.value;
			  float y2 = node.iY.value;
			  float dx2 = x2-x;
			  float dy2 = y2-y;
			  float d2 = PApplet.sqrt(dx2*dx2+dy2*dy2);
			  float portion2 = 1-node.size*0.5f/d2; 
			  x2 = x+portion2*dx2;
			  y2 = y+portion2*dy2;
			  
			  drawArc(x, y, x2, y2,linkReactionToThisPathway[i],false);
			  //parent.stroke(255,0,150);
			 // parent.line(x, y, node.iX.value, node.iY.value);
			  isDrawn = true; // to draw the center button
		  }
		  if (linkReactionFromThisPathway[i].size()>0 && level!=1){// disable when level =1 to simplify the view
			  parent.stroke(PathwayView.sat,PathwayView.sat,0);
			  Node node = Graph.nodes.get(nodeListAll.get(i));
			  
			  float x1 = node.iX.value;
			  float y1 = node.iY.value;
			  float dx1 = x1-x;
			  float dy1 = y1-y;
			  float d2 = PApplet.sqrt(dx1*dx1+dy1*dy1);
			  float portion2 = 1-node.size*0.5f/d2; 
			  x1 = x+portion2*dx1;
			  y1 = y+portion2*dy1;
			  
		      drawArc(x, y, x1, y1,linkReactionFromThisPathway[i],true);
			//  parent.stroke(155,255,0);
			//  parent.line(x, y, node.iX.value, node.iY.value);
		      isDrawn = true; // to draw the center button
		  }
	  }
  }
  
  // Draw links between sub-pathways
  public void drawSubpathwayLinks(){
	  for (int p1=0;p1<subPathwayList.size();p1++){
		  for (int p2=0;p2<subPathwayList.size();p2++){
			  if (linkSubpathway[p1][p2].size()>0){
				//  System.out.println(linkSubpathway[p1][p2]);
				  Pathway2 path1 = subPathwayList.get(p1);
				  Pathway2 path2 = subPathwayList.get(p2);
				  if (p1<p2)
					  drawArc2(path1.xEntry,path1.yEntry, path2.xEntry, path2.yEntry,x,y, linkSubpathway[p1][p2], true);
				  else
					  drawArc2(path1.xEntry,path1.yEntry, path2.xEntry, path2.yEntry,x,y, linkSubpathway[p1][p2], false);
				  path1.isDrawn = true;
				  path2.isDrawn = true;
			  }
		  }
	  }
  }
		
  
  
  public void drawArc2(float x1, float y1, float x2, float y2, float xCenter, float yCenter, ArrayList<Edge> a,boolean isDown){
	  float weight = PApplet.pow(a.size(), 0.15f);
	  parent.strokeWeight(weight);
	  
	  float dis = (y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1);
		float dd = PApplet.sqrt(dis);
		
		float alFrom = PApplet.atan2((y1 - yCenter) , (x1 - xCenter));
		float alTo = PApplet.atan2((y2 - yCenter) , (x2 - xCenter));
		
		float alCircular = PApplet.PI-PApplet.abs(alTo-alFrom);
			
		if (!isDown)
			if (alCircular>PApplet.PI/4)
				alCircular+=0.1f+weight/(40);
		else	
			if (alCircular<PApplet.PI/4)
				alCircular-=0.1f+weight/(40);
		
		float newR = PApplet.abs((dd / 2) / PApplet.sin(alCircular/2));
		float d3 = PApplet.dist(x1, y1, x2, y2);
		float x11 = (x1 + x2) / 2 - ((y1 - y2) / 2)
				* PApplet.sqrt(PApplet.pow(newR * 2 / d3, 2) - 1);
		float y11 = (y1 + y2) / 2 + ((x1 - x2) / 2)
				* PApplet.sqrt(PApplet.pow(newR * 2 / d3, 2) - 1);
		float x22 = (x1 + x2) / 2 + ((y1 - y2) / 2)
				* PApplet.sqrt(PApplet.pow(newR * 2 / d3, 2) - 1);
		float y22 = (y1 + y2) / 2 - ((x1 - x2) / 2)
				* PApplet.sqrt(PApplet.pow(newR * 2 / d3, 2) - 1);

		float x3 = 0, y3 = 0;
		float d11 = PApplet.dist(x11, y11, xCenter, yCenter);
		float d22 = PApplet.dist(x22, y22, xCenter, yCenter);
		if (d11 > d22) {
			x3 = x11;
			y3 = y11;
		} else if (d11 < d22) {
			x3 = x22;
			y3 = y22;
		}

		float delX1 = (x1 - x3);
		float delY1 = (y1 - y3);
		float delX2 = (x2 - x3);
		float delY2 = (y2 - y3);
		float al1 = PApplet.atan2(delY1, delX1);
		float al2 = PApplet.atan2(delY2, delX2);
		if (al1 - al2 > PApplet.PI)
			al1 = al1 - 2 * PApplet.PI;
		else if (al2 - al1 > PApplet.PI)
			al2 = al2 - 2 * PApplet.PI;
		parent.noFill();

		if (level==0){
			al1 = 0;
			al2 =PApplet.PI;
			x3 = (x1+x2)/2;
			y3 = (y1+y2)/2;// - PApplet.abs((x1-x2)/4);
			drawArc22(x1, y1, x2, y2, x3, y3, PApplet.abs((x1-x2)), al1, al2, weight);
			return;
		}	
	
		// Adding weight
		if (al1 < al2){
			 if(isBrushingArc(x3, y3, newR, weight, al1, al2, parent.mouseX, parent.mouseY)){  // Brushing link 1/6
				for (int i=0; i<a.size();i++){
  					if (!isContainsEdge(bEdges,a.get(i)))
  						bEdges.add(a.get(i));
  				}
			 }
			 /*
			 if (this.displayName.equals("ROOT")){
				 parent.noStroke();
				parent.textAlign(PApplet.LEFT);
				parent.fill(255,0,0);
				parent.text("al1="+al1,x3+5,y3);
				parent.fill(200,0,0);
				parent.text("al2="+al2,x3+5,y3+14);
				parent.text("newR="+al2,x3+5,y3+14);
				parent.fill(0,200);
				parent.stroke(0,0,255);
				parent.noFill();
				parent.arc(x3, y3, newR * 2, newR * 2, al1, al2);
			 }*/
			  drawArc22(x1, y1, x2, y2, x3, y3, newR * 2, al1, al2, weight);
		}	
		else{
			if(isBrushingArc(x3, y3, newR, weight, al2, al1, parent.mouseX, parent.mouseY)){ // Brushing link 2/6
				for (int i=0; i<a.size();i++){
					if (!isContainsEdge(bEdges,a.get(i)))
	  					bEdges.add(a.get(i));
  				}
			}
			drawArc22(x1, y1, x2, y2, x3, y3, newR * 2, al2, al1, weight);
		}	
  }
  
  public void drawArc22(float x1, float y1, float x2, float y2 ,float x3, float y3, float d3, float al1, float al2, float weight){
	    float x5 = x3+d3/2*PApplet.cos(al1);
		float y5 = y3+d3/2*PApplet.sin(al1);
		boolean down = true;
		if (PApplet.dist(x5, y5, x1, y1)
				>PApplet.dist(x5, y5, x2, y2))
			down = false;
		
		int numSec =(int) (PApplet.dist(x1, y1, x2, y2)/3);
		if (numSec<3)
			numSec=3;
		float beginAngle = al1;
		if (al2<al1)
			beginAngle = al2;
		for (int k=0;k<=numSec;k++){
			float endAngle = al1+k*(al2-al1)/numSec;
			parent.noFill();
			float sss = (float) k/numSec;
			if (!down)
				sss = (float) (numSec-k)/numSec;
			float r = PathwayView.sat -  PathwayView.sat*sss;
			parent.stroke(r,r,0);
			if (level==0)  
				parent.arc(x3, y3, d3,d3/2, beginAngle, endAngle);
			else
				parent.arc(x3, y3, d3,d3, beginAngle, endAngle);	
			beginAngle = endAngle;
		}
		
		
}	  
  
  public void drawArc(float x1, float y1, float x2, float y2, ArrayList<Edge> a,boolean isToParent){
	  float weight = PApplet.pow(a.size(), 0.15f);
	  parent.strokeWeight(weight);
	  
	  
	  	float dis = (y2-y1)*(y2-y1)+(x2-x1)*(x2-x1);
		 float dd = PApplet.sqrt(dis);
		 float alCircular = PApplet.PI/25;
		
		 float newR = (dd/2)/PApplet.sin(alCircular);
    	 float d3 = PApplet.dist(x1,y1,x2,y2);
    	 float x11 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
    	 float y11 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
    	 float x22 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
    	 float y22 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
    	 
		
		// Adding weight
		if (isToParent){
			 float x3=x22;
			 float y3=y22;
			 float delX1 = (x1-x3);
			float delY1 = (y1-y3);
			float delX2 = (x2-x3);
			float delY2 = (y2-y3);
			float al1 = PApplet.atan2(delY1,delX1);
			float al2 = PApplet.atan2(delY2,delX2);
			parent.noFill();
				
    		 if (al1<al2){
				 if(isBrushingArc(x3, y3, newR, weight, al1, al2, parent.mouseX, parent.mouseY)){  // Brushing 3/6
	 				if (isToParent)
	 					parent.stroke(200,150,0);
	 				else
	 					parent.stroke(150,0,0);
 					
 				
	  				for (int i=0; i<a.size();i++){
	  					if (!isContainsEdge(bEdges,a.get(i)))
	  	  					bEdges.add(a.get(i));
	  				}
	  				//parent.text(bEdges.toString(), x3, y3);
		  		 }	
				parent.arc(x3, y3, newR*2, newR*2, al1, al2);
			}	
			else{
				 if(isBrushingArc(x3, y3, newR, weight, al1-2*PApplet.PI, al2, parent.mouseX, parent.mouseY)){ // Brushing 4/6
					if (isToParent)
	 					parent.stroke(200,150,0);
	 				else
	 					parent.stroke(150,0,0);
	 				for (int i=0; i<a.size();i++){
	 					if (!isContainsEdge(bEdges,a.get(i)))
	 	  					bEdges.add(a.get(i));
	  				}
	 			 }
			
	    		 parent.arc(x3, y3, newR*2, newR*2, al1-2*PApplet.PI, al2);
	       }	
		}
		else{
			float  x3=x11;
			float  y3=y11;
			float delX1 = (x1-x3);
			float delY1 = (y1-y3);
			float delX2 = (x2-x3);
			float delY2 = (y2-y3);
			float al1 = PApplet.atan2(delY1,delX1);
			float al2 = PApplet.atan2(delY2,delX2);
			parent.noFill();
			
    		 if (al1<al2){
				 if(isBrushingArc(x3, y3, newR, weight, al2-2*PApplet.PI, al1, parent.mouseX, parent.mouseY)){  // Brushing 5/6
					if (isToParent)
	 					parent.stroke(200,150,0);
	 				else
	 					parent.stroke(150,0,0);
					for (int i=0; i<a.size();i++){
						if (!isContainsEdge(bEdges,a.get(i)))
		  					bEdges.add(a.get(i));
	  				}
				 }	
				 parent.arc(x3, y3, newR*2, newR*2, al2-2*PApplet.PI, al1);
			}	
    		 
			else{
				if(isBrushingArc(x3, y3, newR, weight, al2, al1, parent.mouseX, parent.mouseY)) { // Brushing 6/6
					if (isToParent)
	 					parent.stroke(200,150,0);
	 				else
	 					parent.stroke(150,0,0);
					for (int i=0; i<a.size();i++){
						if (!isContainsEdge(bEdges,a.get(i)))
		  					bEdges.add(a.get(i));
	  				}
				}	
				parent.arc(x3, y3, newR*2, newR*2, al2, al1);
			}	
		}
  }	  
  
  public static boolean isBrushingArc(float xCenter, float yCenter, float radius,float weight, float al1, float al2, float mouseX, float mouseY){
	    float delX1 = (mouseX-xCenter);
		float delY1 = (mouseY-yCenter);
		float al = PApplet.atan2(delY1,delX1);
		//parent.text(al,xCenter,yCenter);
		
		if ((al1<al && al<al2) || (al1<al-2*PApplet.PI && al-2*PApplet.PI<al2)){
			float dis =PApplet.dist(xCenter, yCenter, mouseX, mouseY);
			if (radius-weight<=dis && dis<=radius+weight){
				return true;
			}
		}
	  return false;
  }
  
  
  Color getGradient(float value){
   return Color.RED;
  }
}