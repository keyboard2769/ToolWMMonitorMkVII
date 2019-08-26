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
 * intuitively the easiest to understand. <br>
 * but i think it feels like a charger more than a timer. <br>
 */
public class ZcOnDelayTimer extends ZcTimer{
  
  /**
   * @param pxSpan #
   */
  public ZcOnDelayTimer(int pxSpan) {
    super(pxSpan);
    ccInit();
  }//++!
  
  private void ccInit(){
    cmJudge=cmMax-1;
  }//++!
  
  //===

  /**
   * {@inheritDoc }
   */
  @Override public void ccAct(boolean pxAct){
    if(pxAct){
      ccShift(1);
    }else{
      ccSetValue(0);
    }//..?
  }//+++

  @Override public boolean ccIsAct(){
    return ccGetValue()>0;
  }//+++
  
  /**
   * {@inheritDoc }
   */
  @Override public boolean ccIsUp(){
    return cmValue>=cmJudge;
  }//+++

  /**
   * {@inheritDoc }
   */
  @Override public void ccSetTime(int pxDiv){
    ccSetRange(ccLimitTimeValue(pxDiv));
    ccInit();
  }//+++
  
}//***eof
