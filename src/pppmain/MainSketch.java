/*
 * Copyright (C) 2019 Key Parker from K.I.C
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package pppmain;

import pppplc.LcLinkedPLC;
import processing.core.PApplet;

/**
 *
 * @author Key Parker from K.I.C
 */
public class MainSketch extends PApplet{

  private static MainSketch self;
  
  public static int pbRoller=0;

  //===

  //=== overridden
  @Override public void setup(){

    //-- pre setting
    size(320, 240);
    frameRate(16);
    noStroke();
    noSmooth();
    textAlign(LEFT, TOP);
    ellipseMode(CENTER);
    frame.setTitle("ToolWMMonitorMkVII");
    self=this;
    
    //-- init package manager
    SubUIManager.ccGetReference().ccInit();
    SubActionManager.ccGetReference().ccInit();
    LcLinkedPLC.ccGetReference().ccInit();
    
    //-- post setting 
    println("-- setup over");

  }//+++

  @Override public void draw(){

    //-- pre drawing
    background(0);
    pbRoller++;
    pbRoller&=0x0f;

    //-- updating
    SubActionManager.ccGetReference().ccUpdate();
    LcLinkedPLC.ccGetReference().ccUpdate();
    SubUIManager.ccGetReference().pbTheCoordinator.ccUpdate();
    
    //-- rolling
    fill(0xFF);
    text(nf(pbRoller, 2), 5, 5);
    text("Lantency:"+Integer.toString((int)((16f-frameRate)*3)),35,5);

    //-- tagging
    
  }//+++

  @Override public void keyPressed(){
    SubActionManager.ccGetReference().ccKeyPressed(key);
  }//+++

  @Override public void mousePressed(){
    int lpMouseID=SubUIManager.ccGetReference()
      .pbTheCoordinator.ccGetMouseOverID();
    /* 4 */VcConst.ccLogln("mouseID", lpMouseID);
    SubActionManager.ccGetReference().ccMousePressed(lpMouseID);
  }//+++
  
  //=== entry
  
  public static final MainSketch ccGetSketch(){return self;}
  
  public static void main(String[] args){
    PApplet.main(MainSketch.class.getCanonicalName());
  }//..!

}//***eof
