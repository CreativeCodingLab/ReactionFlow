package main;

import processing.core.PApplet;

public class Button{
	public boolean b = false;
	public boolean s = false;
	public PApplet parent;
	public int y = 0;
	public int w = 98;
	public int h = 25;
	public float x = 0;
	public int w2 = 200;
	public int itemNum = 9;
	public String text = "";
	public Button(PApplet parent_, String str){
		parent = parent_;
		text = str;
	}
	
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(12);
		parent.noStroke();
		parent.fill(150);
		if (b)
			parent.stroke(255,0,0);
		parent.rect(x, y, w, h);
		
		if (s){
				
			if (text.contains("Loop")){
				float y1 = y+3;
				float y2 = y+h-3;
				float yy = (y1+y2)/2;
				
				
				float d = PApplet.abs(y2-y1);
				int numSec = 100;
				if (numSec==0) return;
				float beginAngle = PApplet.PI/2;
				if (y1>y2)
					beginAngle = -PApplet.PI/2;
				
				for (int k=0;k<=numSec;k++){
					float percent = 1;
					float endAngle = beginAngle+PApplet.PI/numSec;
					if ((float) k/numSec >=(1-percent)){
						parent.noFill();
						float sss = (float) k/numSec;
						sss = PApplet.pow(sss,0.75f);
						parent.stroke(255,sss*255,255-255*sss, 220-200*sss);
						parent.strokeWeight(4);
						
						parent.arc(x+w/2, yy, d*3,d, beginAngle, endAngle);
						parent.arc(x+w/2, yy, d*3,d, beginAngle-PApplet.PI, endAngle-PApplet.PI);
					}
					beginAngle = endAngle;
					
				}
				parent.strokeWeight(1);
			}
			else if (text.contains("Delete")){
				parent.strokeWeight(2);
				parent.stroke(250,0,0);
				parent.line(x+20,y+h/2+1,x+w-20,y+h/2+1);
			}	
			else
				parent.fill(50);
			
		}
			
		
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		if (text.contains("Delete") && s)
			parent.text(text.replace("Delete ", ""),x+w/2,y+18);
		else
			parent.text(text,x+w/2,y+18);
			
		
	}
	
	public void mouseClicked() {
		s = !s;
		
	}
		
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<=mX && mX<=x+w && y<=mY && mY<=h){
			b =true;
			return;
		}
		b =false;
	}
	
}