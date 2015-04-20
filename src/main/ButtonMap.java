package main;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PFont;

public class ButtonMap{
	public boolean b = false;
	public PApplet parent;
	public float x = 0;
	public float y = 0;
	public int w = 64;
	public int h = 18;
	public int itemNum = 9;
	public Color cGray  = new Color(240,240,240);
	public ButtonMap(PApplet parent_){
		parent = parent_;
	}
	
	
	public void draw(String text_,float x_, float y_){
		x = x_;
		y = y_;
		checkBrushing();
		parent.textSize(10);
		if (b){
			parent.fill(255);
			parent.stroke(255,0,0);
			parent.rect(x, y, w, h);
		}	
		else{
			parent.fill(0,60);
			parent.noStroke();
			parent.rect(x, y, w, h);
		}
		parent.textAlign(PApplet.CENTER);
		parent.fill(0);
		parent.text(text_,x+w/2,y+13);
	}
	
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<x+w && y<mY && mY<y+h){
			b =true;
			return;
		}
		b =false;
	}
	
}