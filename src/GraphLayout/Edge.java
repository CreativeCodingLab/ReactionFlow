package GraphLayout;

import java.awt.Color;
import java.util.ArrayList;

import main.Pathway2;
import main.PathwayView;
import main.Slider2;
import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Edge {
	float k=0.08f; //stiffness
	public float naturalLength=1; //natural length.  ehmm uh, huh huh stiffness. natural length ;-)
	Node to;
	Node from;
	int type = -1;  
	Graph g;
	PApplet parent;
	public int countFrom=0;
	public int countTo=0;
	
	
	public Edge(Node from_, Node to_,int type_, PApplet papa) {
		parent = papa;
		from = from_;
		to = to_;
		type = type_;  	// type=0: causality
						// type=1: same input
						// type=2: same output
	   naturalLength = Slider2.val;
	}
	
	public float getNaturalLength() {
	    return naturalLength;
	 }
	  
	public void setGraph(Graph h) {
		g = h;
	}

	public Node getTo() {
		return to;
	}

	public Node getFrom() {
		return from;
	}

	public void setTo(Node n) {
		to = n;
	}

	public void setFrom(Node n) {
		from = n;
	}

	public float dX() {
		return to.getX() - from.getX();
	}

	public float dY() {
		return to.getY() - from.getY();
	}

	public Vector3D getForceTo() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    if (l<0) return null;
	    return new Vector3D(-f*dx/l, -f*dy/l, 0);
	  }
	    
	  public Vector3D getForceFrom() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    if (l<0) return null;
	    return new Vector3D(f*dx/l, f*dy/l, 0);
	  }

	  public void draw() {
		if (parent!=null && g!=null){
	    	parent.strokeWeight(0.5f);
	        if (g.getHoverNode() ==null){
    	    	drawLink(240);
    	    }
	    	else if (g.getHoverNode().equals(from) ||
	    		g.getHoverNode().equals(to)){ 
	    		drawLink(255);
		     	from.isConnected =true;
			    to.isConnected = true;
	    	}
	    	else{
	    		parent.strokeWeight(0.5f);
		        drawLink(15);
	     	}
	 	}
	  }
	  
	  
	 public void drawLink(float sat) {
		 if (!PathwayView.rootPathway.isExpanded){
			 float x1 = from.iX.value;
			 float y1 = from.iY.value;
				
			 float x2 = to.iX.value;
			 float y2 = to.iY.value;
				
			 float xCenter = PathwayView.rootPathway.x;
			 float yCenter = PathwayView.rootPathway.y;
			 if (x1==xCenter || x2==xCenter) return;
			 float al1 = PApplet.atan((y1-yCenter)/(x1-xCenter));
			 float al2 = PApplet.atan((y2-yCenter)/(x2-xCenter));
			 
			 //float xCenter, float yCenter
			 drawArc(x1,y1, al1, x2, y2, al2, PathwayView.rootPathway.x, PathwayView.rootPathway.y, sat, false);
		 }
		 else{
			 drawLink2(sat);
		 }
		 
	 }
	  
			
	 public void drawLink2(float sat) {
		Pathway2 pathwayFrom = from.parentPathway;
		Pathway2 pathwayTo = to.parentPathway;
		if (pathwayFrom==null || pathwayTo==null) return;
		
		while (!pathwayFrom.parentPathway.isExpanded){
			pathwayFrom = pathwayFrom.parentPathway;
			if (pathwayFrom==null) break;
		}
		while (!pathwayTo.parentPathway.isExpanded){
			pathwayTo = pathwayTo.parentPathway;
			if (pathwayTo==null) break;
		}
		
		if (PathwayView.popupLayout.s==0 || PathwayView.popupLayout.s==1 ||
				 PathwayView.popupLayout.s==2 || pathwayFrom.equals(pathwayTo)){
			 float alFrom = from.iAlpha.target;
			 float x1 = from.iX.value;
			 float y1 = from.iY.value;
					 
			 float alTo = to.iAlpha.target;
			 float x2 = to.iX.value;
			 float y2 = to.iY.value;
			
			 //float xCenter, float yCenter
			 if (PathwayView.popupLayout.s==0 || PathwayView.popupLayout.s==1 ||
					 PathwayView.popupLayout.s==2)
				 drawArc(x1,y1, alFrom, x2, y2, alTo, pathwayFrom.x, pathwayFrom.y, sat, true);
			 else {
				 float dx1 = x1-pathwayFrom.x;
				 float dy1 = y1-pathwayFrom.y;
				 float d1 = PApplet.sqrt(dx1*dx1+dy1*dy1);
				 float portion1 = 1-from.size*0.5f/d1; 
				 x1 = pathwayFrom.x+portion1*dx1;
				 y1 = pathwayFrom.y+portion1*dy1;
				
				 float dx2 = x2-pathwayTo.x;
				 float dy2 = y2-pathwayTo.y;
				 float d2 = PApplet.sqrt(dx2*dx2+dy2*dy2);
				 float portion2 = 1-to.size*0.5f/d2; 
				 x2 = pathwayTo.x+portion2*dx2;
				 y2 = pathwayTo.y+portion2*dy2;
				
				 drawArc(x1,y1, alFrom, x2, y2, alTo, pathwayFrom.x, pathwayFrom.y, sat, true);
			 }	 
		 }
		 else{
			 if (sat<200) return;
			 //System.out.println(pathwayFrom+"	"+pathwayTo);
			 
			 float xPathwayFrom = pathwayFrom.x;
			 float yPathwayFrom = pathwayFrom.y;
			 float xPathwayTo = pathwayTo.x;
			 float yPathwayTo = pathwayTo.y;
			
			 countFrom=0;
			 countTo = 0;
			 ArrayList<Pathway2> a  = drawPathwayFromUpLink(pathwayFrom, pathwayTo, sat);
			 Pathway2 newPathwayFrom = a.get(0);
			 ArrayList<Pathway2> b  = drawPathwayToUpLink(pathwayFrom, pathwayTo, sat);
			 Pathway2 newPathwayTo = b.get(0);
			 
			 
			 float xFrom = from.iX.value;
			 float yFrom = from.iY.value;
			 float xTo = to.iX.value;
			 float yTo = to.iY.value;
			
			 float dx1 = xFrom-xPathwayFrom;
			 float dy1 = yFrom-yPathwayFrom;
			 float d1 = PApplet.sqrt(dx1*dx1+dy1*dy1);
			 float portion1 = 1-from.size*0.5f/d1; 
			 xFrom = xPathwayFrom+portion1*dx1;
			 yFrom = yPathwayFrom+portion1*dy1;
			
			 
			 float dx2 = xTo-xPathwayTo;
			 float dy2 = yTo-yPathwayTo;
			 float d2 = PApplet.sqrt(dx2*dx2+dy2*dy2);
			 float portion2 = 1-to.size*0.5f/d2; 
			 xTo = xPathwayTo+portion2*dx2;
			 yTo = yPathwayTo+portion2*dy2;
			
			 
			 drawPathwayLink(newPathwayFrom, newPathwayTo, sat);
			 if (countFrom>0){
			 	int reactionIndex = pathwayFrom.reactionListAll.indexOf(from.reaction);
				if (reactionIndex>=0){
					pathwayFrom.linkReactionFromThisPathway[reactionIndex].add(this);
				}
				 	
			 }
			 else if (pathwayFrom.equals(b.get(1).parentPathway)){
				 float x1 = xFrom;
				 float y1 = yFrom;
				 float x2 = b.get(1).xEntry;    
				 float y2 = b.get(1).yEntry;
				 float xCenter = pathwayFrom.x;
				 float yCenter = pathwayFrom.y;
				
				 if (countTo==0){
					 x2 = xTo;
					 y2 = yTo;
				 }
				 if (x1==xCenter || x2==xCenter) return;
				 float al1 = PApplet.atan((y1-yCenter)/(x1-xCenter));
				 float al2 = PApplet.atan((y2-yCenter)/(x2-xCenter));
				 drawArc(x1,y1, al1, x2, y2, al2, xCenter, yCenter, sat,false);
			 }
				 
			 if (countTo>0){
			 	 int reactionIndex = pathwayTo.reactionListAll.indexOf(to.reaction);
				 if (reactionIndex>=0){
					 pathwayTo.linkReactionToThisPathway[reactionIndex].add(this);
				 }
				 
			 }	 
			 else if (pathwayTo.equals(a.get(1).parentPathway)){
				 float x2 = xTo;
				 float y2 = yTo;
				 
				 float x1 = a.get(1).xEntry;    
				 float y1 = a.get(1).yEntry;
				
				 if (countFrom==0){
					 x1 = xFrom;
					 y1 = yFrom;
				 }
				 
				 float xCenter = pathwayTo.x;
				 float yCenter = pathwayTo.y;
				 
				 if (x1==xCenter || x2==xCenter) return;
				 float al1 = PApplet.atan((y1-yCenter)/(x1-xCenter));
				 float al2 = PApplet.atan((y2-yCenter)/(x2-xCenter));
				 drawArc(x1,y1, al1, x2, y2, al2, xCenter, yCenter, sat,false);
			 }
			 
		 }
		
	 }
	 public  ArrayList<Pathway2> drawPathwayFromUpLink(Pathway2 pathwayFrom, Pathway2 pathwayTo, float sat ) {
		 ArrayList<Pathway2> a = new ArrayList<Pathway2>();
		 Pathway2 newPathwayFrom = pathwayFrom;
		 Pathway2 currentPathway = pathwayFrom;
		 if (pathwayTo.level>0){
			 while(newPathwayFrom.level>pathwayTo.level){
				 currentPathway = newPathwayFrom;
				 if (newPathwayFrom.parentPathway.isExpanded){
					 if(!newPathwayFrom.parentPathway.equals(pathwayTo)){   // if they have the same parent
					//	 parent.stroke(200,200,0,100);
					//	 parent.line(newPathwayFrom.xEntry, newPathwayFrom.yEntry, 
					//			 newPathwayFrom.parentPathway.x, newPathwayFrom.parentPathway.y);
						 newPathwayFrom.linkToParent.add(this);
						 
					 }	 
					 countFrom++;
				 }		
				 newPathwayFrom = newPathwayFrom.parentPathway;
			 }
		 }
		 a.add(newPathwayFrom);
		 a.add(currentPathway);
		 return a;
	 }
	 
	 public ArrayList<Pathway2> drawPathwayToUpLink(Pathway2 pathwayFrom, Pathway2 pathwayTo, float sat ) {
		 ArrayList<Pathway2> a = new ArrayList<Pathway2>();
		 Pathway2 newPathwayTo = pathwayTo;
		 Pathway2 currentPathway = pathwayTo;
		 if (pathwayFrom.level>0){
			 while(newPathwayTo.level>pathwayFrom.level){
				 currentPathway = newPathwayTo;
				 if (newPathwayTo.parentPathway.isExpanded){
					 if(!newPathwayTo.parentPathway.equals(pathwayFrom)) { // if they have the same parent
						// parent.stroke(255,0,0,150);
						// parent.line(newPathwayTo.parentPathway.x, newPathwayTo.parentPathway.y, 
						//		 newPathwayTo.xEntry, newPathwayTo.yEntry);
						 newPathwayTo.linkFromParent.add(this);
							
					 }	
					 countTo++;
				 }
				 newPathwayTo = newPathwayTo.parentPathway;
			 }
		 }
		 a.add(newPathwayTo);
		 a.add(currentPathway);
		 return a;
	 }
	 
	public void drawPathwayLink(Pathway2 pathwayFrom, Pathway2 pathwayTo,float sat) {
	Pathway2 newPathwayFrom = pathwayFrom;
		Pathway2 newPathwayTo = pathwayTo;
		while (!newPathwayFrom.parentPathway.equals(newPathwayTo.parentPathway)) {
			if (newPathwayFrom.parentPathway.isExpanded) {
				/*parent.stroke(200, 200, 0, 100);
				parent.line(newPathwayFrom.xEntry, newPathwayFrom.yEntry,
						newPathwayFrom.parentPathway.x,
						newPathwayFrom.parentPathway.y);*/
				newPathwayFrom.linkToParent.add(this);
				countFrom++;
			}
			if (newPathwayTo.parentPathway.isExpanded) {
				/*parent.stroke(0, 150);
				parent.line(newPathwayTo.parentPathway.x,
						newPathwayTo.parentPathway.y, newPathwayTo.xEntry,
						newPathwayTo.yEntry);*/
				newPathwayTo.linkFromParent.add(this);
				//System.out.println(newPathwayTo.displayName+" "+newPathwayTo.linkToParent);
				countTo++;
			}
			newPathwayFrom = newPathwayFrom.parentPathway;
			newPathwayTo = newPathwayTo.parentPathway;
		}

		if (!newPathwayFrom.equals(newPathwayTo)
				&& newPathwayFrom.parentPathway.isExpanded) {
			int index1 = newPathwayFrom.parentPathway.subPathwayList.indexOf(newPathwayFrom);
			int index2 = newPathwayFrom.parentPathway.subPathwayList.indexOf(newPathwayTo);
			newPathwayFrom.parentPathway.linkSubpathway[index1][index2].add(this);
			
			//System.out.println("newPathwayFrom.parentPathway = "+newPathwayFrom.parentPathway.displayName +"	"+newPathwayFrom.parentPathway.linkSubpathway[index1][index2]);
			
			countFrom++;
			countTo++;
		 }
		 
	 }
	
	
	 
		
	 public void drawGradientLine(float x1, float y1, float x2, float y2, Color color) {
		 int numSec =6;
		 for (int i=1;i<=numSec;i++){
				float sss = (float) i/numSec;
				float x3 = x1+(x2-x1)*sss;
				float y3 = y1+(y2-y1)*sss;
				//float r = 200*sss;
				
				parent.stroke(color.getRGB());
				parent.line(x1,y1,x3,y3);
				x1=x3;
				y1=y3;
		 }
	 }
			
			
	  public void drawArc(float x1, float y1, float alFrom, float x2, float y2, float alTo, float xCenter, float yCenter, float sat, boolean isLinkedTwoReaction) {
			float dis = (y2-y1)*(y2-y1)+(x2-x1)*(x2-x1);
			float dd = PApplet.sqrt(dis);
			
			float alCircular = PApplet.PI -PApplet.abs(alTo-alFrom);
			// the atan formular is wrong because it gives values from -PI/2 tp PI/2
			// In other words if two points are distributed on two side of the vertical center, the atan is wrong 
			// Alpha of reactions are computed separately (not use atan) so it does not need correction 
			if (!isLinkedTwoReaction && ((x1<=xCenter && xCenter<=x2) || (x2<=xCenter && xCenter<=x1))){
				alCircular = PApplet.abs(alTo-alFrom);
			}
			
			if (PathwayView.popupLayout.s==1 || PathwayView.popupLayout.s==0)
				 alCircular += PathwayView.iTransition.value;
			  else if (PathwayView.popupLayout.s==2)
				 alCircular *= PathwayView.iTransition.value;
			  else if (PathwayView.popupLayout.s==3)
					 alCircular *= PathwayView.iTransition.value;
				
			 if (alCircular<0.01f)
				 alCircular=0.01f;
			 else if (alCircular>PApplet.PI-0.01f)
				 alCircular = PApplet.PI-0.01f;
			 float newR = (dd/2)/PApplet.sin(alCircular/2);
	    	 float d3 = PApplet.dist(x1,y1,x2,y2);
	    	 float x11 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float y11 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float x22 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float y22 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
		
	    	 float x3 =0, y3=0;
	    	 float d11 = PApplet.dist(x11, y11, xCenter, yCenter);
	    	 float d22 = PApplet.dist(x22, y22, xCenter, yCenter);
	    	 if (d11>d22){
	    		 x3=x11;
	    		 y3=y11;
	    	 }
	    	 else if (d11<d22){
	    		 x3=x22;
	    		 y3=y22;
	    	 }
		   
			float delX1 = (x1-x3);
			float delY1 = (y1-y3);
			float delX2 = (x2-x3);
			float delY2 = (y2-y3);
			float al1 = PApplet.atan2(delY1,delX1);
			float al2 = PApplet.atan2(delY2,delX2);
			if (al1-al2>PApplet.PI)
				al1=al1-2*PApplet.PI;
			else if (al2-al1>PApplet.PI)
				al2=al2-2*PApplet.PI;
			parent.noFill();
			
			if (type==0){
				if (al1<al2){
					 if(Pathway2.isBrushingArc(x3, y3, newR, 1, al1, al2, parent.mouseX, parent.mouseY)){  // Brushing 7/6
				 		if (!Pathway2.isContainsEdge(Pathway2.bEdges,this))
		  					Pathway2.bEdges.add(this);
			  		 }
				 	drawArc(x1, y1, x2, y2, x3, y3, newR*2,  al1, al2, sat);
				 	//
				 	
				}	
				else{
					if(Pathway2.isBrushingArc(x3, y3, newR, 1, al2, al1, parent.mouseX, parent.mouseY)){  // Brushing 7/6
						if (!Pathway2.isContainsEdge(Pathway2.bEdges,this))
			  				Pathway2.bEdges.add(this);
			  		}
					drawArc(x1, y1, x2, y2, x3, y3, newR*2,  al2, al1, sat);
				//	drawGradientLine(x1,y1,x2,y2,Color.BLUE);
				}	
			}
			else if (type==1){
				parent.stroke(255,0,255);
				if (al1<al2)
					parent.arc(x3, y3, newR*2, newR*2,  al1, al2);
				else
					parent.arc(x3, y3, newR*2, newR*2,  al2, al1);
			}
			else if (type==2){
				parent.stroke(0,255,0);
				if (al1<al2)
					parent.arc(x3, y3, newR*2, newR*2,  al1, al2);
				else
					parent.arc(x3, y3, newR*2, newR*2,  al2, al1);
			}
	  }

	  
	  public void drawArc(float x1, float y1, float x2, float y2 ,float x3, float y3, float d3, float al1, float al2, float sat){
		    float x5 = x3+d3/2*PApplet.cos(al1);
			float y5 = y3+d3/2*PApplet.sin(al1);
			boolean down = true;
			if (PApplet.dist(x5, y5, x1, y1)
					>PApplet.dist(x5, y5, x2, y2))
				down = false;
			
			if (PathwayView.popupLayout.s==0 && PApplet.dist(from.iX.value,from.iY.value,from.iX.target,from.iY.target)<2){
				float x11 = from.iX.value;
				float y11 = from.iY.value;
				float x22 = to.iX.value;
				float y22 = to.iY.value;
				x3 = (x11+x22)/2;
				y3 = (y11+y22)/2;
				al1 = -PApplet.PI/2;
				al2 = PApplet.PI/2;
				if (from.iY.value<to.iY.value)
					down = true;
				else
					down = false;
			}
			if (PathwayView.popupLayout.s==1 && PApplet.dist(from.iX.value,from.iY.value,from.iX.target,from.iY.target)<2){
				float x11 = from.iX.value;
				float y11 = from.iY.value;
				float x22 = to.iX.value;
				float y22 = to.iY.value;
				x3 = (x11+x22)/2;
				y3 = (y11+y22)/2;
				
				if (from.iY.value<to.iY.value){
					al1 = PApplet.PI/2;
					al2 = PApplet.PI*3/2;
				}
				else{
					al1 = -PApplet.PI/2;
					al2 = PApplet.PI/2;
				}
				down = false;
			}
			
			int numSec =(int) (PApplet.dist(x1, y1, x2, y2)/3);
			if (numSec<3)
				numSec=3;
			else if (numSec>20)
				numSec=20;
			float beginAngle = al1;
			if (al2<al1)
				beginAngle = al2;
			for (int k=0;k<=numSec;k++){
				float endAngle = al1+k*(al2-al1)/numSec;
				parent.noFill();
				float sss = (float) k/numSec;
				if (!down)
					sss = (float) (numSec-k)/numSec;
				float r = PathwayView.sat - PathwayView.sat*sss;
				parent.stroke(r,r,0,sat);
				parent.arc(x3, y3, d3,d3, beginAngle, endAngle);
				beginAngle = endAngle;
			}
			
			
	}	  
}
