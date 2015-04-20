package main;

import processing.core.PApplet;
import processing.core.PImage;

public class ButtonSimulation{
	public boolean b = false;
	public boolean s = false;
	public PApplet parent;
	public int w = 50;
	public int h = 50;
	public float x = 0;
	public float y = 0;
	public PImage image;
	public ButtonSimulation(PApplet parent_, PImage im){
		parent = parent_;
		image = im;
	}
	
	
	public void draw(float x_, float y_){
		x = x_;
		y = y_;
		checkBrushing();
		parent.textSize(12);
		
		if (s){
			parent.fill(200,0,0);
			parent.noStroke();
			parent.rect(x, y, w,h);
		}
		else if (b){
			parent.fill(150);
			parent.noStroke();
			parent.rect(x, y, w,h);
		}	
		parent.image(image, x, y, h,h);
	}
	
	public void mouseClicked() {
		s = !s;
	}
		
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<=mX && mX<=x+w && y<=mY && mY<=y+h){
			b =true;
			return;
		}
		b =false;
	}
	
}