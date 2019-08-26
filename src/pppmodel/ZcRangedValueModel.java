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
 * i made a mistake.<br>
 * cylinder is not a ranged model. cylinder is a range value model.<br>
 */
public class ZcRangedValueModel extends ZcRangedModel{
  
  /**
   * this stuff moves
   */
  protected int cmValue;
  
  /**
   * value will be set to minimum
   * @param pxMin the lower bound
   * @param pxRange the distance between lower bound and higher bound
   */
  public ZcRangedValueModel(int pxMin, int pxRange)
    {super(pxMin, pxRange);cmValue=pxMin;}//++!
  
  //===
    
  /**
   * ##
   * @param pxValue #
   */
  public final void ccSetValue(int pxValue)
    {cmValue=ccLimit(pxValue);}//+++
  
  /**
   * pretend min is 50, max is 100, and say, 0.5f is passed,
   * than value is gonna be like 75.
   * @param pxZeroToOne does NOT check, don t get cross.
   */
  public final void ccSetValue(float pxZeroToOne){
    ccSetValue((int)(pxZeroToOne*(cmMax-cmMin))+cmMin);
  }//+++
  
  /**
   * stuck its self at range bounds via ccLimit()
   * @param pxOffset #
   */
  public final void ccShift(int pxOffset)
    {cmValue=ccLimit(cmValue+pxOffset);}//+++
  
  /**
   * starts over again after across bounds via ccWarp()
   * @param pxOffset #
   */
  public final void ccRoll(int pxOffset)
    {cmValue=ccWarp(cmValue+pxOffset);}//+++
  
  //===
  
  /**
   * 
   * @return #
   */
  public final int ccGetValue()
    {return cmValue;}//+++
    
}//***eof
