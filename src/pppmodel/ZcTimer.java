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
 * this is the base of type of one pass delay timer. <br>
 * it assumes all your flow can get fit into checked value model. <br>
 */
public abstract class ZcTimer extends ZcRangedValueModel implements ZiTimer{
  
  /**
   * purpose may vary through implementation
   */
  protected int cmJudge;
  
  /**
   * update count basically. like for 16FPS, 16 means one second.
   * @param pxDiv will be trimmed to [3-65535]
   */
  public ZcTimer(int pxDiv){
    super(0, (pxDiv|0x03)&0xFFFF);
    cmJudge=0;
  }//++!
  
  /**
   * 
   * @param pxValue #
   * @return will be trimmed to [3-65535]
   */
  protected final int ccLimitTimeValue(int pxValue){
    return (pxValue|0x03)&0xFFFF;
  }//+++
  
  //===

  /**
   * 
   * @return #
   * @deprecated i still believe client code should not concern on this
   */
  @Deprecated public final int testGetCurrentCount()
    {return cmValue;}//+++
  
}//***eof
