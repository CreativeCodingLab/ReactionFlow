package main;
import java.awt.Color;
import processing.core.PApplet;

public class PopupView{
	public int b = -1;
	public int select=0;
	public PApplet parent;
	public float x = 800;
	public int y = 0;
	public int w1 = 98;
	public int w = 200;
	public int h;
	public int itemH = 20;
	public Color cGray  = new Color(240,240,240);
	public static String[] items={"Reaction View","Multiple pathways"}; 
	
	public PopupView(PApplet parent_){
		parent = parent_;
	}
	
	public void draw(float x_){
		x = x_;
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			parent.textSize(12);
			h=items.length*itemH+20;
			parent.fill(200);
			parent.stroke(0,150);
			parent.rect(x-20, y+23, w,h);
			
			
			for (int i=0;i<items.length;i++){
				if (i==select){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x-10,y+itemH*(i)+5+25,w-25,itemH+1);
					parent.fill(255,255,0);
				}
				else if (i==b){
					parent.fill(200,0,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(items[i],x,y+itemH*(i+1)+25);  // 
			}	
		}
		
		parent.fill(180);
		parent.noStroke();
		parent.rect(x,y,w1,23);
		
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.textSize(12);
		parent.text("View",x+w1/2,y+17);
		
	}
	
	 public void mouseClicked() {
		 if(select!=b && b>=0 && b<99){
			 select = b;
		}
	}
	 
	public void mouseMoved() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<x+w1 && y<=mY && mY<=itemH+5){
			b =100;
			return;
		}
		else{
			for (int i=0; i<items.length; i++){
				if (x<=mX && mX<=x+100 && y+itemH*i+25<=mY && mY<=y+itemH*(i+1)+6+25){
					b =i;
					return;
				}	
			}
			if (x<=mX && mX<=x+100 && y<=mY && mY<=y+h){
				return;
			}	
		}
		b =-1; 
	}
	
	
	
}