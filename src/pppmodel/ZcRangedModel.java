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
 * a ranged model can be anything. <br>
 * to me, basically it is a cylinder. <br>
 */
public class ZcRangedModel {
  
  /**
   * range bound
   */
  protected int cmMin,cmMax;
  
  //==
  
  /**
   * ##
   * @param pxMin the lower bound
   * @param pxRange the distance between lower bound and higher bound
   */
  public ZcRangedModel(int pxMin, int pxRange)
    {ccSetRange(pxMin, pxRange);}//+++
  
  //==
  
  /**
   * only changes the upper bound of the range
   * @param pxRange will be masked into 0-65535
   */
  public final void ccSetRange(int pxRange)
    {cmMax=cmMin+pxRange&0xFFFF;}//+++
  
  /**
   * ##
   * @param pxMin the lower bound
   * @param pxRange the distance between lower bound and higher bound
   */
  public final void ccSetRange(int pxMin, int pxRange)
    {cmMin=pxMin;cmMax=pxMin+pxRange&0xFFFF;}//+++
  
  //==
  
  /**
   * ##
   * @param pxSource #
   * @return will get stuck at range bounds
   */
  public final int ccLimit(int pxSource){
    int lpRes=pxSource>cmMax?cmMax:pxSource;
    lpRes=lpRes<cmMin?cmMin:lpRes;
    return lpRes;
  }//+++
  
  /**
   * ##
   * @param pxSource #
   * @return will wrap over range bounds
   */
  public final int ccWarp(int pxSource) {
    int lpRes=pxSource>cmMax?cmMin:pxSource;
    lpRes=lpRes<cmMin?cmMax:lpRes;
    return lpRes;
  }//+++
  
  //==
  
  /**
   * ##
   * @param pxSource #
   * @return [min,max)
   */
  public final boolean ccContains(int pxSource)
    {return (pxSource>=cmMin)&&(pxSource<cmMax);}//+++
  
  /**
   * 
   * @return #
   */
  public final int ccGetMin()
    {return cmMin;}//+++
  
  /**
   * 
   * @return #
   */
  public final int ccGetMax()
    {return cmMax;}//+++
  
  /**
   * i used call this span.
   * but traditionally what we have called "span"
   * actually means the maximum value here.<br>
   * and we call the minimum value "offset".
   * @return #
   */
  public final int ccGetRange()
    {return cmMax-cmMin;}//+++
  
}//***eof
