package main;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;
import processing.core.PFont;


public class TextBox {
	public boolean b = false;
	public PApplet parent;
	public float x = 0;
	public int y = 0;
	public int h = 20;
	public int w = 130;
	public char pKey = ' ';
	public String text = "";
	public String searchText = "";
	
	// Draw search results
	public int nResults = 25;
	public int sIndex = -1;
	public int w2 = 370; 
	public int hBox = 640;
	public int mouseOnTextList = -1;
	
	public TextBox(PApplet parent_, String text_) {
		parent = parent_;
		text = text_;
	}

	public void draw(float x_) {
		x=x_;
		checkBrushing();
		
		parent.strokeWeight(1f);
		parent.textAlign(PApplet.LEFT);
		if (b){
			parent.fill(255,255,0);
			parent.stroke(0);
		}
		else{
			parent.fill(0,30);
			parent.stroke(50,100);
			if (!searchText.equals("")){
				parent.stroke(0);
				parent.fill(255,200,200);
			}
		}
		
		parent.rect(x-w/2, y, w, h);

		// Main Text
		//parent.textFont(font, 12);
		parent.textAlign(PApplet.LEFT);
		parent.textSize(12);
		parent.fill(0);
		parent.text(searchText, x -w/2+6, y + h - 4);

		// Explaining Text
		//parent.textSize(14);
		//parent.textAlign(PApplet.RIGHT);
		//parent.fill(0, 0, 0);
		//parent.text(text, x-4, y + 17);
	}

	public void keyPressed() {
		if (b) {
			String textBefore = searchText;
			char c = (char) parent.key;
			if (c == 8 && searchText.length() > 0) {
				searchText = searchText.substring(0, searchText.length() - 1);
				if (searchText.length() == 1 && searchText.equals(""))
					searchText = "";
			}
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || (c == ' ') || (c == '-')
					|| (c == '.')) {
				searchText = (searchText + parent.key).toLowerCase();
			}

			else if (c == 10)
				searchText = "";
			
			
			if (!textBefore.equals(searchText) && !searchText.equals("")){
				updateReactions();
				
			}
			else if (searchText.equals(""))
				main.ReactionView.sRectListByText =  new ArrayList<Integer>();
		}
	}
	public void updateReactions() {
		int indexOfItemHash=0;
		main.ReactionView.sRectListByText =  new ArrayList<Integer>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : main.ReactionView.rectHash.entrySet()) {
			String rectName = entry.getKey().getDisplayName();
			if (rectName.toLowerCase().contains(searchText)){
				main.ReactionView.sRectListByText.add(indexOfItemHash);
			}
			indexOfItemHash++;
		}
	}
		
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x-w/2< mX && mX < x + w/2 && y < mY && mY < y + h + 5) {
			b = true;
			return;
		}
		b = false;
	}

	public int mouseClicked() {
		
		return sIndex;
	}
}