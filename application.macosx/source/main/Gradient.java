package main;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;

public class Gradient{
  public ArrayList<Color> colors;
   
  // Constructor
  Gradient(){
    colors = new ArrayList<Color>();
  }
   
  void addColor(Color c){
    colors.add(c);
  }
   
  Color getGradient(float value){
    // make sure there are colors to use
    if(colors.size() == 0)
      return new Color(0,0,0);
     
    // if its too low, use the lowest value
    if(value <= 0.0)
      return colors.get(0);
    
    // if its too high, use the highest value
    if(value >= colors.size() - 1)
      return colors.get(colors.size() -  1);
     
    // lerp between the two needed colors
    int color_index = (int)value;
    Color c1 = colors.get(color_index);
    Color c2 = colors.get(color_index + 1);
     
    return new Color(PApplet.lerpColor(c1.getRGB(), c2.getRGB(), value - color_index, 1));
  }
}