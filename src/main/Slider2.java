package main;

import java.awt.Color;
import java.text.DecimalFormat;

import GraphLayout.Edge;

import processing.core.PApplet;

public class Slider2{
	int count =0;
	public int pair =-1;
	public PApplet parent;
	public float x=0,y=0;
	public int w; 
	public int u =-1;
	public static float val =-1;
	
	
	public int bSlider = -1;
	public int sSlider = -1;
	
	
	public Slider2(PApplet parent_){
		parent = parent_;
		w= 200;
		u = 20;
		update();
	}
		
	public void update(){
		val = (float) u;
	}
	public void updateEdgeLegth(){
		for (int i=0; i<PathwayView.g.edges.size();i++){
			PathwayView.g.edges.get(i).naturalLength = val;
		}
	}
		
		
	public void draw(String text, float x_, float y_){
		x = x_;
		y = y_;
		checkBrushingSlider();
		
		float xx2 = x+u;
		DecimalFormat df = new DecimalFormat("#.##");
		parent.stroke(0,200);
		parent.strokeWeight(1.0f);
		for (int j=0; j<=2; j++ ){
			parent.line(x+j*100, y-3, x+j*100, y+3);
			if (j==2) break;
			for (int k=1; k<10; k++ ){
				parent.line(x+j*100+k*10, y-1, x+j*100+k*10, y+1);
			}
		}
		
		//Upper range
		Color c;
		if (sSlider==1){
			c= new Color(255,0,0);
		}	
		else if (bSlider==1){
			c= new Color(255,100,0);
		}	
		else{
			c = new Color(0,0,0,200);
		}
		parent.fill(c.getRGB());
		parent.triangle(xx2-5, y+10, xx2+5, y+10, xx2, y);
		
		parent.textAlign(PApplet.CENTER);
		parent.textSize(11);
		parent.text(df.format(val), xx2,y-2);
		parent.textAlign(PApplet.LEFT);
		
		parent.textSize(11);
		parent.noStroke();
		parent.textAlign(PApplet.RIGHT);
		parent.fill(0);
		parent.text(text, x-5,y+4);
		
		count++;
	    if (count==10000)
	    	count=200;
	   
	}
	
	
	
	public void checkBrushingSlider() {
		float xx2 = x+u;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (xx2-20<mX && mX < xx2+20 && y<mY && mY<y+25){
			bSlider =1; 
			return;
		}
		bSlider =-1;
	}
	
	public void checkSelectedSlider1() {
		sSlider = bSlider;
	}
	public void checkSelectedSlider2() {
		sSlider = -1;
	}	
	public int checkSelectedSlider3() {
		if (sSlider==1){
			u += (parent.mouseX - parent.pmouseX);
			if (u<1) u=1;
			if (u>w)  u=w;
			update();
			updateEdgeLegth();
		}
		return sSlider;
	}
		
}