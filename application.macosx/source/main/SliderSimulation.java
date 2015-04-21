package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;

public class SliderSimulation{
	public int pair =-1;
	public PApplet parent;
	public float x,y;
	public int w; 
	
	
	public boolean b = false;
	public boolean s = false;
	public float ggg =10;
	public float v =0;
	public static float transitionProcess =0;
	public static float reactionSize =0;
	public static float sumStep =0;
	
	public SliderSimulation(PApplet parent_){
		parent = parent_;
		w= 300;
	}
		
	public void draw(float x_, float y_, int maxLevel_){
		checkBrushingSlider();
		int maxLevel2 = maxLevel_+1;
		if (maxLevel2<=0) return;
		x = x_;
		y = y_;
		
		reactionSize = w/maxLevel2;
		
		
		for (int k=0; k<maxLevel2; k++ ){
			float x1 = x+k*reactionSize;
			float x2 = x1+reactionSize/5;
			parent.stroke(ReactionView.formComplexColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			x2+=reactionSize/5;
			parent.stroke(ReactionView.complexRectionColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			parent.noStroke();
			parent.fill(0);
			parent.ellipse(x1, y, 8, 8);
			
			x2+=reactionSize/5;
			parent.stroke(ReactionView.complexRectionColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			x2+=reactionSize/5;
			parent.stroke(ReactionView.formComplexColor.getRGB());
			parent.line(x1, y, x2, y);
			
			if (k<maxLevel2-1){
				x1=x2;
				x2+=reactionSize/10;
				parent.stroke(200,200,0);
				parent.line(x1, y, x2, y);
				x1=x2;
				x2+=reactionSize/10;
				parent.stroke(0);
				parent.line(x1, y, x2, y);
			}
		}
		
		//System.out.println("s="+s+"	b="+b);
		Color color = new Color(0,0,0);
		if (s){
			color= new Color(200,0,0);
		}
		else if (b){
			color = new Color(200,150,0);
		}
		else{
			color = new Color(0,100,255);
		}
		
		int lastIndex = ReactionView.simulationRectList.size()-1;
		if (lastIndex<0) return;
		
		int currentLevel = ReactionView.simulationRectListLevel.get(lastIndex);
		int currentRect = ReactionView.simulationRectList.get(lastIndex);
		
		sumStep = ReactionView.iS1[currentRect].value+ReactionView.iS2[currentRect].value+
						ReactionView.iS3[currentRect].value+ReactionView.iS4[currentRect].value+
						transitionProcess;
	//	DecimalFormat df = new DecimalFormat("#.##");
		if (!(parent.mousePressed && b))
			v = currentLevel*reactionSize +(sumStep/1000)*reactionSize/5;;
		float xx2 = x+ v;
		parent.textSize(14);
		parent.stroke(0,100);
		parent.fill(color.getRGB());
		parent.triangle(xx2-12, y-10, xx2-12, y+10, xx2, y);
		parent.textAlign(PApplet.RIGHT);
		
		// Decide the text on slider
		parent.textAlign(PApplet.CENTER);
		parent.textSize(13);
		//parent.text(df.format(value), xx2,y+8);
		parent.textAlign(PApplet.LEFT);
	}
	
	
	
	public void checkBrushingSlider() {
		float x2 = x+v;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (x2-60<mX && mX < x2+50 && y-25<mY && mY<y+25){
			b=true; 
		}
		else{
			b = false;
		}
			
	}
	public void mousePresses() {
		s =true;
	}
	public void mouseReleased() {
		s = false;
	}
		
	public void mouseDragged() {
		if (s){
			v += (parent.mouseX - parent.pmouseX);
			float setLevel =  (v/reactionSize);
			
			// Remove simulation List
			for (int i = ReactionView.simulationRectList.size()-1;i>=0;i--){
				int currentLevel = ReactionView.simulationRectListLevel.get(i);
				int currentReact = ReactionView.simulationRectList.get(i);
				if (currentLevel>setLevel){
					if (currentLevel>0){
						ReactionView.simulationRectList.remove(i);
						ReactionView.simulationRectListLevel.remove(i);
					}
					resetLevel(currentReact);
				}
				else if (currentLevel<=setLevel && setLevel<currentLevel+1){
					float sec = (setLevel - (int) (v/reactionSize))*5;  // Position in simulating reaction
					resetLevelCurrent(currentReact, sec);
				}
				else{ // Move forward
					foward(currentReact);
				}
			}
			// Remove interElements
			for (int i = ReactionView.interElements.size()-1;i>=0;i--){
				int currentLevel = ReactionView.interElementsLevel.get(i);
				if (currentLevel>setLevel){
					ReactionView.interElements.remove(i);
					ReactionView.interElementsLevel.remove(i);
				}	
			}
			
			if (v<0) v=0;
			if (v>w)  v=w;
		}
	}
	public static void foward(int currentReact){
		ReactionView.iS1[currentReact].set(1000);
		ReactionView.iS1[currentReact].target(1000);
		ReactionView.iS2[currentReact].set(1000);
		ReactionView.iS2[currentReact].target(1000);
		ReactionView.iS3[currentReact].set(1000);
		ReactionView.iS3[currentReact].target(1000);
		ReactionView.iS4[currentReact].set(1000);
		ReactionView.iS4[currentReact].target(1000);
		for (int g=0;g<ReactionView.rectList.size();g++){
			ReactionView.iS[currentReact][g].set(1000);
			ReactionView.iS[currentReact][g].target(1000);
		}
	}
	public static void resetLevel(int currentReact){
		ReactionView.iS1[currentReact].set(0);
		ReactionView.iS1[currentReact].target(0);
		ReactionView.iS2[currentReact].set(0);
		ReactionView.iS2[currentReact].target(0);
		ReactionView.iS3[currentReact].set(0);
		ReactionView.iS3[currentReact].target(0);
		ReactionView.iS4[currentReact].set(0);
		ReactionView.iS4[currentReact].target(0);
		for (int g=0;g<ReactionView.rectList.size();g++){
			ReactionView.iS[currentReact][g].set(0);
			ReactionView.iS[currentReact][g].target(0);
		}
	}
	public static void resetLevelCurrent(int currentReact, float sec){
		transitionProcess = 0;
		if (0<=sec && sec<1){
			ReactionView.iS1[currentReact].set(sec*1000);
			ReactionView.iS1[currentReact].target(sec*1000);
			ReactionView.iS2[currentReact].set(0);
			ReactionView.iS2[currentReact].target(0);
			ReactionView.iS3[currentReact].set(0);
			ReactionView.iS3[currentReact].target(0);
			ReactionView.iS4[currentReact].set(0);
			ReactionView.iS4[currentReact].target(0);
			for (int g=0;g<ReactionView.rectList.size();g++){
				ReactionView.iS[currentReact][g].set(0);
				ReactionView.iS[currentReact][g].target(0);
			}
		}
		else if (1<=sec && sec<2)	{
			ReactionView.iS1[currentReact].set(1000);
			ReactionView.iS1[currentReact].target(1000);
			ReactionView.iS2[currentReact].set((sec-1)*1000);
			ReactionView.iS2[currentReact].target((sec-1)*1000);
			ReactionView.iS3[currentReact].set(0);
			ReactionView.iS3[currentReact].target(0);
			ReactionView.iS4[currentReact].set(0);
			ReactionView.iS4[currentReact].target(0);
			for (int g=0;g<ReactionView.rectList.size();g++){
				ReactionView.iS[currentReact][g].set(0);
				ReactionView.iS[currentReact][g].target(0);
			}
		}	
		else if (2<=sec && sec<3)	{
			ReactionView.iS1[currentReact].set(1000);
			ReactionView.iS1[currentReact].target(1000);
			ReactionView.iS2[currentReact].set(1000);
			ReactionView.iS2[currentReact].target(1000);
			ReactionView.iS3[currentReact].set((sec-2)*1000);
			ReactionView.iS3[currentReact].target((sec-2)*1000);
			ReactionView.iS4[currentReact].set(0);
			ReactionView.iS4[currentReact].target(0);
			for (int g=0;g<ReactionView.rectList.size();g++){
				ReactionView.iS[currentReact][g].set(0);
				ReactionView.iS[currentReact][g].target(0);
			}
		}	
		else if (3<=sec && sec<4)	{
			ReactionView.iS1[currentReact].set(1000);
			ReactionView.iS1[currentReact].target(1000);
			ReactionView.iS2[currentReact].set(1000);
			ReactionView.iS2[currentReact].target(1000);
			ReactionView.iS3[currentReact].set(1000);
			ReactionView.iS3[currentReact].target(1000);
			ReactionView.iS4[currentReact].set((sec-3)*1000);
			ReactionView.iS4[currentReact].target((sec-3)*1000);
			for (int g=0;g<ReactionView.rectList.size();g++){
				ReactionView.iS[currentReact][g].set(0);
				ReactionView.iS[currentReact][g].target(0);
			}
		}	
		else if (4<=sec && sec<=5)	{
			ReactionView.iS1[currentReact].set(1000);
			ReactionView.iS1[currentReact].target(1000);
			ReactionView.iS2[currentReact].set(1000);
			ReactionView.iS2[currentReact].target(1000);
			ReactionView.iS3[currentReact].set(1000);
			ReactionView.iS3[currentReact].target(1000);
			ReactionView.iS4[currentReact].set(1000);
			ReactionView.iS4[currentReact].target(1000);
			for (int g=0;g<ReactionView.rectList.size();g++){
				ReactionView.iS[currentReact][g].set((sec-4)*1000);
				ReactionView.iS[currentReact][g].target((sec-4)*1000);
			}
		}	
	}
	
}