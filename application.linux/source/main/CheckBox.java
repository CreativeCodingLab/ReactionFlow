package main;

import processing.core.PApplet;

public class CheckBox{
	public boolean s;
	public boolean b;
	public PApplet parent;
	public float x = 0;
	public float y = 0;
	public String text = "";
	
	public CheckBox(PApplet parent_, String text_){
		parent = parent_;
		text = text_;
	}
	
		
	public void draw(float  x_, float y_){
		x = x_;
		y = y_;
		
		checkBrushing();
		parent.textAlign(PApplet.LEFT);
		parent.textSize(11);
		parent.stroke(0);
		if (b)
			parent.fill(180);
		else
			parent.noFill();
		parent.rect(x, y, 15, 15);
		parent.fill(0);
		if (s){
			parent.noStroke();
			parent.fill(0);
			parent.ellipse(x+8, y+8, 12, 12);
		}
		else if (b)
			parent.fill(100);
		if (text.contains("Small molecules")){
			parent.text(text.replace("Small molecules", ""),x+20,y+13);
			parent.fill(main.ReactionView.smallMoleculeColor.getRed(),main.ReactionView.smallMoleculeColor.getGreen(),main.ReactionView.smallMoleculeColor.getBlue());
			parent.text("Small molecules",x+95,y+13);
		}
		else if (text.contains("Unidentified elements")){
		}
		else if (text.contains("Complex formation")){
			parent.text(text.replace("Complex formation", ""),x+20,y+13);
			parent.fill(main.ReactionView.formComplexColor.getRed(),main.ReactionView.formComplexColor.getGreen(),main.ReactionView.formComplexColor.getBlue());
			parent.text("Complex formation",x+95,y+13);
		}
		else if (text.contains("Complex formation")){
			parent.text(text.replace("Complex formation", ""),x+20,y+13);
			parent.fill(main.ReactionView.formComplexColor.getRed(),main.ReactionView.formComplexColor.getGreen(),main.ReactionView.formComplexColor.getBlue());
			parent.text("Complex formation",x+95,y+13);
		}
		else if (text.contains("Complex reaction")){
			parent.text(text.replace("Complex reaction", ""),x+20,y+13);
			parent.fill(main.ReactionView.complexRectionColor.getRed(),main.ReactionView.complexRectionColor.getGreen(),main.ReactionView.complexRectionColor.getBlue());
			parent.text("Complex reaction",x+95,y+13);
		}
		else if (text.contains("Protein reaction")){
			parent.text(text.replace("Protein reaction", ""),x+20,y+13);
			parent.fill(main.ReactionView.proteinRectionColor.getRed(),main.ReactionView.proteinRectionColor.getGreen(),main.ReactionView.proteinRectionColor.getBlue());
			parent.text("Protein reaction",x+95,y+13);
		//	parent.fill(main.PopupReaction.unidentifiedElementColor.getRed(),main.PopupReaction.unidentifiedElementColor.getGreen(),main.PopupReaction.unidentifiedElementColor.getBlue());
		//	parent.text("Unidentified elements",x+95,y+32);
		}
			
		else	
			parent.text(text,x+20,y+13);
	}
	
	public void checkBrushing() {
		if (ReactionView.popupReactionOrder.b>=0
				|| ReactionView.popupCausality.b>=0)
			return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x-10<mX && mX < x+200 && y<mY && mY<y+20){
			b=true;
		}
		else
			b = false;
	}
	
	public void mouseClicked() {
		if (b){
			s = !s;
		}
	}
	
}