package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PImage;

public class SliderSpeed{
	public int pair =-1;
	public PApplet parent;
	public float x,y;
	public int w; 
	
	
	public boolean b = false;
	public boolean s = false;
	public float v =w;
	public static float transitionProcess =0;
	public static float speed =0.9f;
	public PImage imRabbit =  null;
	public PImage imTurtle =  null;
	
	public SliderSpeed(PApplet parent_){
		parent = parent_;
		w= 200;
		v=w/4;
		 imRabbit =  parent.loadImage("img/speedRabbit.png");
		 imTurtle =  parent.loadImage("img/speedTurtle.png");
	}
		
	
		
	public void draw(float x_, float y_){
		checkBrushingSlider();
		x = x_;
		y = y_;
		
		
		parent.image(imTurtle, x-50, y-15,50,33);
		parent.image(imRabbit, x+w, y-14, 48,30);
			for (int k=0; k<=10; k++ ){
			parent.stroke(0,100);
			parent.line(x, y, x+w, y);
		}
		
		Color color = new Color(0,0,0);
		if (s){
			color= new Color(200,0,0);
		}
		else if (b){
			color = new Color(200,150,0);
		}
		else{
			color = new Color(0,0,0,100);
		}
		
		int lastIndex = ReactionView.simulationRectList.size()-1;
		if (lastIndex<0) return;
		
		DecimalFormat df = new DecimalFormat("#.#");
		float xx2 = x+ v;
		parent.stroke(0,100);
		parent.fill(color.getRGB());
		parent.triangle(xx2-6, y+10, xx2+6, y+10, xx2, y);
		parent.textAlign(PApplet.RIGHT);
		
		// Decide the text on slider
		parent.textAlign(PApplet.CENTER);
		parent.textSize(12);
		
		float rate = PApplet.pow(speed/0.9f,1.9125f); // to make sure the fastest speed is 10 times faster than the default
		parent.text(df.format(rate)+"x", xx2,y-2);
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
			if (v<0) v=0;
			if (v>w)  v=w;
			setSpeed(v/w);
		}
	}
	// Set speed **********************************
	public void setSpeed(float speed_) {
		speed = 0.2f+speed_*2.8f;     // initial value v =0.25; speed = 0.2+0.25*2.8f = 0.9f
		for (int i=0; i< ReactionView.rectList.size();i++){
			ReactionView.iS1[i].attraction = speed;
			ReactionView.iS2[i].attraction = speed;
			ReactionView.iS3[i].attraction = speed;
			ReactionView.iS4[i].attraction = speed;
			for (int j=0;j<ReactionView.rectList.size();j++){
				ReactionView.iS[i][j].attraction = speed/2;
			}
		}
	}
		
	
	
}