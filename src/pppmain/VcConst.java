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

import pppplc.LcWordMemory;
import static processing.core.PApplet.hex;

/**
 *
 * @author Key Parker from K.I.C
 */
public final class VcConst{
  
  private static final boolean C_DO_LOG = false;

  private static VcConst self;
  public static VcConst ccGetReference(){
    if(self==null){self=new VcConst();}//..?
    return self;
  }//..!
  private VcConst(){}//..!

  //===
  
  static public final void ccLogln(String pxTag, Object pxVal){
    if(!C_DO_LOG){return;}
    ccPrintln(pxTag, pxVal);
  }//+++
  
  static public final void ccPrintln(String pxLine){
    ccPrintln(pxLine,null);
  }//+++

  static public final void ccPrintln(String pxTag, Object pxVal){
    if(pxTag==null){return;}
    System.out.print("kosui.::"+pxTag+":");
    System.out.println(pxVal==null?"":pxVal.toString());
  }//+++

  static public final boolean ccIsValidString(String pxLine){
    if(pxLine==null){
      return false;
    }else{
      return !pxLine.isEmpty();
    }//..?
  }//+++
  
  public static String ccToHexTableString(int[] pxDesData, int pxWrap){
    if(pxDesData==null){return ".[0]";}
    if(pxDesData.length<=1){return ".[1]";}
    StringBuilder lpBuilder = new StringBuilder(".ccToHexTableString()::\n");
    int lpWrapCNT=0;
    int lpWrap=pxWrap<4?4:pxWrap;
    for(int i:pxDesData){
      lpBuilder.append(hex(i,8));
      lpBuilder.append("H ");
      lpWrapCNT++;
      if(lpWrapCNT==lpWrap){
        lpBuilder.append("\n");
        lpWrapCNT=0;
      }//..?
    }//..~
    return lpBuilder.toString();
  }//+++
  
  public static String ccToHexTableString(LcWordMemory pxMemory, int pxWrap){
    if(pxMemory==null){return ".[0]";}
    StringBuilder lpBuilder = new StringBuilder(".ccToHexTableString()::\n");
    int lpWrapCNT=0;
    int lpWrap=pxWrap<4?4:pxWrap;
    for(int i=0,s=pxMemory.ccGetSize();i<s;i++){
      lpBuilder.append(hex(pxMemory.ccReadWord(i),8));
      lpBuilder.append("H ");
      lpWrapCNT++;
      if(lpWrapCNT==lpWrap){
        lpBuilder.append("\n");
        lpWrapCNT=0;
      }//..?
    }//..~
    return lpBuilder.toString();
  }//+++
  
}//***eof
