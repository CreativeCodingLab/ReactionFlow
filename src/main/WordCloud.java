package main;

/**
 * Tag Cloud by Wray Bowling

 * For more information: http://processing.org/discourse/yabb2/YaBB.pl?num=1214504959
 */
import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class WordCloud{
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int height;
	public PFont font2;
	float baseline_ratio = (float)0.28;
	int large_font = 14;
	int small_font = 10;
	float spring = (float)0.02;
	int numWords;
	public Tag[] words; 
	PApplet parent;
	int maxCount = 0;
	public int b = -1;
	public int s = -1;
	public Color  color;
	public String desc;
	
	
	//CHANGE HERE PARAMETERS OF CLOUD
	public WordCloud( PApplet p, 
			int x1_, int x2_, int y1_, int y2_, Color color_, String desc_){
		//change these for location of tags
		parent = p;
		x1 = x1_;
		x2 = x2_;
		y1 = y1_;
		y2 = y2_;
		color = color_;
		desc = desc_;
		font2 = parent.loadFont("Arial-BoldMT-18.vlw");
		parent.textFont(font2);
		b = -1;
		s=-1;
	}

	// Update the tags
	public void updateTags(String[] strings, int[] counts) {
		maxCount = counts[0];
		words = new Tag[strings.length];
		numWords = strings.length;
		for (int i = 0; i < numWords; i++) {
			words[i] = new Tag(words, i, strings[i], counts[i], maxCount , x1, y1);
		}
	}
	
	public void updateX() {
		
	}

	public void draw(PApplet p) {
		parent.fill(color.getRGB());
		parent.textSize(12);
		parent.text(desc,x1-70,y1-12);
		
		for (int i = 0; i < numWords; i++) {
			if (words[i].checkBrushing(p)>=0) break;
		}	
		for (int i = 0; i < numWords; i++) {
			words[i].collide();
			words[i].move();
			words[i].display(p);  
		}
    }
	
	
	
	
	public void mouseClicked(){
		if (b>=0){
			if (s==b){
				s=-100;
				main.ReactionView.textbox1.searchText = "";
				ReactionView.textbox1.updateReactions();
				main.PathwayViewer_3.reactionView.mouseMoved();  // to update protein names in current reactions
			}
			else{
				s=b;
				main.ReactionView.textbox1.searchText = words[b].word.toLowerCase();
				ReactionView.textbox1.updateReactions();
				main.PathwayViewer_3.reactionView.mouseMoved();	// to update protein names in current reactions
			}	
		}
		else{
			s=-1;
		}
	}
	
	
	class Tag {
		float x, y;
		float word_width;
		float  font_size;
		public String word;
		float vx = 0;
		float vy = 0;
		int id;
		Tag[] others;
		public float saturaton=0.5f; 
		
		Tag(Tag[] others, int id, String str, int count, int maxCount,  int x, int y) {
			this.x = x + parent.random(0, x2-x1-50);
			this.y = y + parent.random(0, y2-y1-150);
			
			float dif = large_font - small_font;
			float large2 = large_font;
			if (maxCount*2<dif){
				large2 = small_font+maxCount*2;
			}
			
			font_size = (PApplet.map(PApplet.sqrt(count), 0, PApplet.sqrt(maxCount), small_font, large2));
			
			saturaton = PApplet.map(count, 0, maxCount, 0.5f, 0.7f);
			
			
			word = str;
			parent.textFont(font2, font_size-1);
			word_width = parent.textWidth(word);
			while (word_width>x2-x1 && word.length()>1){
				word = word.substring(0, word.length()-1);
				word_width = parent.textWidth(word);
			}
			this.id = id;
			this.others = others;
		} 
		
	
		/* I mucked with this function pretty heavily, but to no avail :( */
		void collide() {
			for (int i = id + 1; i < numWords; i++) {
				float dx = others[i].x + others[i].word_width/2 - x + word_width/2;
				float dy = others[i].y + others[i].font_size/2 - y + font_size/2;
				float minDistX = word_width + others[i].word_width;
				float minDistY = font_size + others[i].font_size;

				if ( (dx < minDistX) && (dy < minDistY) ){ 
					float angle = PApplet.atan2(dy, dx);
					float targetX = x + PApplet.cos(angle) * minDistX;
					float ax = (targetX - others[i].x) * spring;
					vx -= ax;
					others[i].vx += ax;
					float targetY = y + minDistY;
					float ay = (targetY - others[i].y) * spring;
					vy -= ay;
					others[i].vy += ay;
				}
			}   
		}

		void move() {
			vx *= 0.1;
			vy *= 0.9;
			x = x1;
			y += vy;
			if (x + word_width/2 > x2) {
				x = x2 - word_width/2;
				vx *= -0.5; 
			}
			else if (x - word_width/2 < x1) {
				x = x1+word_width/2;
				vx *= -0.5;
			}
			if (y + font_size/2 > y2) {
				y = y2 - font_size/2;
				vy *= -0.5; 
			} 
			else if (y - font_size/2 < y1) {
				y = y1+font_size/2;
				vy *= -0.5;
			}
		}

		public int checkBrushing(PApplet p) {
			if (x-word_width/2<=parent.mouseX && parent.mouseX<=x+word_width/2 &&
				y-font_size<=parent.mouseY && parent.mouseY<=y){
				b=id;
				return b;
			}	
			else{
				b=-200;
				return b;
			}
		}
		
		
		void display(PApplet p) {
			parent.textAlign(PApplet.LEFT);
			parent.fill(color.getRed(),color.getGreen(),color.getBlue(),saturaton*255);
			if (id==s)
				parent.fill(255,0,0);
			if (id==b){
				parent.fill(255,100,0);
			}
				
			parent.textFont(font2, font_size);
					
			parent.text(word, (int)(x - word_width/2), y);
		}
	} 
}