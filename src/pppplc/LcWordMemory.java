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

package pppplc;

import java.util.Arrays;

/**
 *
 * @author Key Parker from K.I.C
 */
public class LcWordMemory implements LiMemory{
  
  private final int[] cmData;
  private final int cmMask;

  public LcWordMemory(int pxWordSize) {
    cmData=new int[ccToPowerOfTwo(pxWordSize)];
    cmMask=cmData.length-1;
  }//+++
  
  //===
  
  public final int ccGetSize(){return cmData.length;}//+++
  
  public final int[] ccGetData(){
    return Arrays.copyOf(cmData, cmData.length);
  }//+++
  
  //===
  
  public final void ccTakeByteArray(byte[] pxData){
    int lpFixedLength
      =(pxData.length/2 < cmData.length)?
        pxData.length/2 : cmData.length;
    for(int i=0;i<lpFixedLength;i++){
      cmData[i]=ccCombine(pxData[i*2+1], pxData[i*2  ]);
    }//..~
  }//+++
  
  public final byte[] ccToByteArray(){
    int lpLength=cmData.length;
    byte[] lpRes=new byte[lpLength*2];
    for(int i=0;i<lpLength;i++){
      lpRes[i*2+1]=ccTrimLL(cmData[i]);
      lpRes[i*2  ]=ccTrimLH(cmData[i]);
    }//..~
    return lpRes;
  }//+++
  
  //===
  
  @Override public boolean ccReadBit(int pxAddr, int pxBit) {
    int lpTester=0x00008000;
    lpTester>>=(15-pxBit&0x0F);
    return (cmData[pxAddr&cmMask]&lpTester)!=0;
  }//+++
  
  @Override public int ccReadWord(int pxAddr) {
    return cmData[pxAddr&cmMask];
  }//+++
  
  @Override public void ccWriteBit(int pxAddr, int pxBit, boolean pxValue) {
    int lpTester=pxValue?0x00008000:0xFFFF7FFF;
    lpTester>>=(15-pxBit&0x0F);
    if(pxValue){
      cmData[pxAddr&cmMask]|=lpTester;
    }else{
      cmData[pxAddr&cmMask]&=lpTester;
    }//..?
  }//+++
  
  @Override public void ccWriteWord(int pxAddr, int pxValue) {
    cmData[pxAddr&cmMask]=pxValue&0x0000FFFF;
  }//+++
  
  //===
  
  public static final int ccToPowerOfTwo(int pxSource){
    int lpMasked=pxSource&0xFFFF;
    int lpTester=0x00008000;
    while(lpTester!=1){
      if( (lpTester&lpMasked)!=0 ){break;}
      lpTester>>=1;
    }//..~
    return lpTester==lpMasked?lpTester:lpTester*2;
  }//+++
  
  public static final int ccCombine(byte pxLow, byte pxHigh){
    int lpRes=0;
    lpRes|=(((int)pxLow)&0x00FF);
    lpRes|=(((int)pxHigh)&0x00FF)<<8;
    return lpRes;
  }//+++
  
  public static final byte ccTrimHH(int pxSource){
    return (byte)(
      (pxSource&0xFF000000)>>24
    );
  }//+++
  
  public static final byte ccTrimHL(int pxSource){
    return (byte)(
      (pxSource&0x00FF0000)>>16
    );
  }//+++
  
  public static final byte ccTrimLH(int pxSource){
    return (byte)(
      (pxSource&0x0000FF00)>>8
    );
  }//+++
  
  public static final byte ccTrimLL(int pxSource){
    return (byte)(
      (pxSource&0x000000FF)
    );
  }//+++
  
}//***eof
