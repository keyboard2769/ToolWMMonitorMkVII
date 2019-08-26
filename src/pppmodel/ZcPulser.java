/*
 * Copyright (C) 2018 Key Parker from K.I.C.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pppmodel;

/**
 * triggers a pulse at he head and tail of a square wave. 
 * works like the out-plus and out-minus instruction on my controller. <br>
 * in the purpose of trigger an single action via mouse press or 
 * key press without firing an event. <br>
 */
public class ZcPulser {
  
  private boolean cmBuff=false;
  
  //===
  
  /**
   * supposedly should be called from draw(). <br>
   * like : pulser.ccPulse(keyPressed %AND% key=='a');
   * @param pxAct #
   * @return both head(stand up) and tail(sit down) side
   */
  public final boolean ccPulse(boolean pxAct){
    if(cmBuff!=pxAct){
      cmBuff=pxAct;
      return true;
    }
    return false;
  }//+++

  /**
   * supposedly should be called from draw(). <br>
   * like : pulser.ccUpPulse(keyPressed %AND% key=='a');
   * @param pxAct #
   * @return head side
   */
  public final boolean ccUpPulse(boolean pxAct)
    {return ccPulse(pxAct)&&(cmBuff);}//+++
  
  /**
   * supposedly should be called from draw(). <br>
   * like : pulser.ccDownPulse(keyPressed %AND% key=='a');
   * @param pxAct #
   * @return tail side
   */
  public final boolean ccDownPulse(boolean pxAct)
    {return ccPulse(pxAct)&&(!cmBuff);}//+++
   
}//***eof
