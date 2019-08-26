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

import java.net.InetAddress;
import pppmain.EiUpdatable;
import pppmain.VcConst;
import processing.net.Client;

import static processing.core.PApplet.hex;
import static processing.core.PApplet.str;

/**
 *
 * @author Key Parker from K.I.C
 */
public final class LcLinkedPLC
  implements EiUpdatable,LiMemory
{ 
  //-- LAN setting
  public static final String C_PLC_IP = "10.57.7.3";
  public static final int C_PLC_PORT = 507;
  //-- memory map
  public static final int C_COMMAND_MAX_BYTESIZE=512;
  public static final int C_COMMAND_DATA_BYTESIZE=480;//..from 26th, up to 492
  public static final int C_CHANNEL_OFFSET = 500;
  public static final int C_RECV_HEAD_WORD_ADDR = 65000;
  public static final int C_SEND_HEAD_WORD_ADDR = 65500;
      
  private static final int
    I_READ_OR_WRITE=14,
    I_DATA_BYTE_LENGTH=18,
    I_MEMORY_TYPE=20,
    I_MEMORY_ADDR_L=21,
    I_MEMORY_ADDR_M=22,
    I_MEMORY_ADDR_H=23,
    I_WORD_LENGTH=24,
    I_DATA_HEAD=26
  ;//...
  
  //===

  private static final LcLinkedPLC SELF=new LcLinkedPLC();
  public static LcLinkedPLC ccGetReference(){return SELF;}//..!
  private LcLinkedPLC(){}//..!

  //===
  
  private Client cmClient;

  private final LcWordMemory cmSendMemory
   = new LcWordMemory(C_COMMAND_DATA_BYTESIZE/2);

  private final LcWordMemory cmRecvMemory 
   = new LcWordMemory(C_COMMAND_DATA_BYTESIZE/2);

  private final byte[] cmCommand=new byte[C_COMMAND_MAX_BYTESIZE];
  
  private final byte[] cmAnswer=new byte[C_COMMAND_MAX_BYTESIZE];
  
  public volatile boolean cmIsInitiated;

  public volatile int
    cmLinkFlasher=0,
    cmRecvFlasher=0;
  //...
  
  private boolean cmRequestToggeler;

  //===

  public final void ccInit(){

    //-- construct
    ssSetupCommand();
    System.out.println(".LcLinkedPLC.ccInit()::tring to open client.");
    cmIsInitiated=ccOpenClient();
    
  }//..!

  @Override public void ccUpdate(){
    
    if(!cmIsInitiated){return;}
    
    cmRequestToggeler=!cmRequestToggeler;
    if(cmRequestToggeler){
      ccSendReadRequest();
    }else{
      ccSendWriteRequest();
    }//..?
    ccRecieve();
  
  }//+++
  
  //===

  public final void ccSendCommand(
    char pxMode_rw, char pxType_ml, int pxAddr, byte[] pxData)
  { if(!cmIsInitiated){return;}
    ccSetupCommand(pxMode_rw, pxType_ml, pxAddr, pxData);
    ccSendRequest();
  }//+++

  public final void ccSendReadRequest(){
    if(!cmIsInitiated){return;}
    ccSetupCommand('r', 'm', C_RECV_HEAD_WORD_ADDR, cmRecvMemory.ccToByteArray());
    ccSendRequest();
  }//+++  

  public final void ccSendWriteRequest(){
    if(!cmIsInitiated){return;}
    ccSetupCommand('w', 'm', C_SEND_HEAD_WORD_ADDR, cmSendMemory.ccToByteArray());
    ccSendRequest();
  }//+++

  public final void ccRecieve(){
    if(!cmIsInitiated){return;}
    while(cmClient.available()>0){
      if(cmClient.readBytes(cmAnswer)>0){
        cmLinkFlasher++;cmLinkFlasher&=0x07;
        //[todo]::add some command content dump utility here
        if(cmAnswer[I_READ_OR_WRITE]==0){
          cmRecvFlasher++;cmRecvFlasher&=0x07;
          int lpLength=Byte.toUnsignedInt(cmAnswer[I_WORD_LENGTH])*2;
          byte[] lpReverseBuffer=new byte[lpLength];
          if(lpLength<C_COMMAND_MAX_BYTESIZE){
            for(int i=0; i<lpLength; i+=2){
              lpReverseBuffer[i+1]=cmAnswer[I_DATA_HEAD+i];
              lpReverseBuffer[i]=cmAnswer[I_DATA_HEAD+1+i];
            }//..~
          }else{
            System.err.println(".ZcPLCLinker.ccRecieve()::"
              +"answered length too long:"
              +Integer.toString(lpLength)
            );
          }//..?
          //[LEGACY]::cmRecievedAddr=cmData[22]*256+cmData[21];
          //         ... i just cant remember what is this
          cmRecvMemory.ccTakeByteArray(lpReverseBuffer);
        }//..?
      }//..?
    }//..~
  }//+++

  private void ccSetupCommand(
    char pxMode_rw, char pxType_ml, int pxAddr, byte[] pxData
  ){
    
    //-- word length for both read and write
    cmCommand[I_WORD_LENGTH]=(byte)(C_COMMAND_DATA_BYTESIZE/2);

    //--- operate mode and data length
    switch(pxMode_rw){
      case 'r':
        cmCommand[I_READ_OR_WRITE]=(byte)(0x00);
        cmCommand[I_DATA_BYTE_LENGTH]=(byte)(6);
      break;
      case 'w':
        cmCommand[I_READ_OR_WRITE]=(byte)(0x01);
        cmCommand[I_DATA_BYTE_LENGTH]=(byte)(6+C_COMMAND_DATA_BYTESIZE);
      break;
      default:break;
    }//..?

    //-- memory type
    switch(pxType_ml){
      case 'm':cmCommand[I_MEMORY_TYPE]=(byte)(0x02);break;
      case 'l':cmCommand[I_MEMORY_TYPE]=(byte)(0x04);break;
      default:break;
    }//..?

    //-- memory address
    cmCommand[I_MEMORY_ADDR_L]=LcWordMemory.ccTrimLL(pxAddr&0xFFFFFF);
    cmCommand[I_MEMORY_ADDR_M]=LcWordMemory.ccTrimLH(pxAddr&0xFFFFFF);
    cmCommand[I_MEMORY_ADDR_H]=LcWordMemory.ccTrimHL(pxAddr&0xFFFFFF);
    
    //-- reverse memory arrange
    int lpFixedByteLength
     = pxData.length>C_COMMAND_DATA_BYTESIZE?
       C_COMMAND_DATA_BYTESIZE : pxData.length;
    for(int i=0; i<lpFixedByteLength; i+=2){
      cmCommand[i+I_DATA_HEAD]=pxData[i+1];
      cmCommand[i+1+I_DATA_HEAD]=pxData[i];
    }//..?

  }//+++

  private boolean ccSendRequest(){
    if(cmClient==null){return false;}
    if(!cmClient.active()){return false;}
    boolean lpRes;
    try{
      cmClient.write(cmCommand);
      lpRes=true;
    }catch(Exception e){
      System.err.println(".ZcPLCLinker.ccSendRequest()::"+e.toString());
      lpRes=false;
    }//..?
    return lpRes;
  }//+++

  synchronized public final boolean ccGetLinkFlasher(){
    return cmLinkFlasher>3;
  }//+++
  
  synchronized public final boolean ccGetRecvFlasher(){
    return cmRecvFlasher>3;
  }//+++
  
  synchronized public final boolean ccIsInitiated(){
    return cmIsInitiated;
  }//+++

  /**
   * let's say, we'll call the whole memory map, the "map". 
   * than, the map has two part which got the exactly same size, 
   *   we'll the the "half map", a "channel".
   * obviously, we have two channel : receive channel and send channel.
   * since one request can only carry one channel, 
   *   which is its data part,
   *   up to 492 byte, that is 246 word...
   * so actually, the whole map's the word size value   
   *   is exactly the the same as the data part's byte size.
   * @return hard coded command data byte size.
   */
  public final int ccGetMappingWordSize(){
    return C_COMMAND_DATA_BYTESIZE;
  }//+++
  
  private void ssSetupCommand(){
    
    //-- command ** prototol header
    cmCommand[0]=(byte)(0xFB);//
    cmCommand[1]=(byte)(0x80);//
    cmCommand[2]=(byte)(0x80);//
    cmCommand[3]=(byte)(0x00);//

    //-- command ** body
    cmCommand[4]=(byte)(0xFF);//C-status
    cmCommand[5]=(byte)(0x7B);//connection option
    cmCommand[6]=(byte)(0xFE);//ID_L
    cmCommand[7]=(byte)(0x00);//ID_H

    //-- command ** constant
    cmCommand[8]=(byte)(0x11);//DONT TOUCH
    cmCommand[9]=(byte)(0x00);//DONT TOUCH
    cmCommand[10]=(byte)(0x00);//DONT TOUCH
    cmCommand[11]=(byte)(0x00);//DONT TOUCH
    cmCommand[12]=(byte)(0x00);//DONT TOUCH
    cmCommand[13]=(byte)(0x00);//DONT TOUCH

    //-- command **  set
    cmCommand[14]=(byte)(0x00);//command
    cmCommand[15]=(byte)(0x00);//mode
    cmCommand[16]=(byte)(0x00);//DONT TOUCH
    cmCommand[17]=(byte)(0x01);//DONT TOUCH

    //-- command ** amount
    cmCommand[18]=(byte)(0x06);//reg L
    cmCommand[19]=(byte)(0x00);//reg H

    //-- command ** request
    cmCommand[20]=(byte)(0x02);//memory type
    cmCommand[21]=(byte)(0xE8);//memory adr L
    cmCommand[22]=(byte)(0x03);//memory adr M
    cmCommand[23]=(byte)(0x00);//memory adr H
    cmCommand[24]=(byte)(0x01);//memory(word) amount L
    cmCommand[25]=(byte)(0x00);//memory(word) amount H

    //-- command ** data
    cmCommand[26]=(byte)(0x00);//--00..data write L
    cmCommand[27]=(byte)(0x00);//    ..data write H
    cmCommand[28]=(byte)(0x00);//--01
    cmCommand[29]=(byte)(0x00);//
    cmCommand[30]=(byte)(0x00);//--02
    cmCommand[31]=(byte)(0x00);//
    cmCommand[32]=(byte)(0x00);//--03
    cmCommand[33]=(byte)(0x00);//
    cmCommand[38]=(byte)(0x00);//--04
    cmCommand[39]=(byte)(0x00);//
    cmCommand[40]=(byte)(0x00);//--05
    cmCommand[41]=(byte)(0x00);//
    cmCommand[42]=(byte)(0x00);//--06
    cmCommand[43]=(byte)(0x00);//
    cmCommand[44]=(byte)(0x00);//--07
    cmCommand[45]=(byte)(0x00);//
    cmCommand[46]=(byte)(0x00);//--08
    cmCommand[47]=(byte)(0x00);//
    cmCommand[48]=(byte)(0x00);//--09
    cmCommand[49]=(byte)(0x00);//
    cmCommand[50]=(byte)(0x00);//--10
    cmCommand[51]=(byte)(0x00);//
    cmCommand[52]=(byte)(0x00);//--11
    cmCommand[53]=(byte)(0x00);//
    cmCommand[54]=(byte)(0x00);//--12
    cmCommand[55]=(byte)(0x00);//
    cmCommand[56]=(byte)(0x00);//--13
    cmCommand[57]=(byte)(0x00);//
    cmCommand[58]=(byte)(0x00);//--14
    cmCommand[59]=(byte)(0x00);//
    cmCommand[60]=(byte)(0x00);//--15
    cmCommand[61]=(byte)(0x00);//
    cmCommand[62]=(byte)(0x00);//--NC
    cmCommand[63]=(byte)(0x00);//
    
  }//+++
    
  /**
   * for some thread reason this can be called only once per VM.
   * trying to make a reconnect function with this would be kind useless.
   * @return client.active()
   */
  public final boolean ccOpenClient(){
    
    //-- pre
    if(cmClient!=null){
      System.err.println("pppplc.LcLinkedPLC.ccCloseClient()::"
        + "client is still not null!!");
      return cmClient.active();
    }//..?
    
    //-- check
    boolean lpTester=false;
    try{
      InetAddress lpAdd=InetAddress.getByName(C_PLC_IP);
      lpTester=lpAdd.isReachable(128);
    }catch(Exception e){
      System.err.println(".ZcPLCLinker.ccInit()::"+e.toString());
      lpTester=false;
    }//..?
    if(!lpTester){
      System.err.println("pppplc.LcLinkedPLC.ccInit()::"
        + "failed to open client.");
      return false;
    }else{
      System.out.println(".LcLinkedPLC.ccOpenClient()::"
        + "the hardcoded ip might be reachable!!");
    }//..?
    
    //-- open
    System.out.println(".LcLinkedPLC.ccOpenClient()::construct new client");
    cmClient=new Client(
      pppmain.MainSketch.ccGetSketch(),
      C_PLC_IP,
      C_PLC_PORT
    );
    if(cmClient==null){
      System.err.println(".LcLinkedPLC.ccOpenClient()::"
        + "failed to construc new client");
      return false;
    }else{
      
      System.out.println(".LcLinkedPLC.ccOpenClient()::"
        + "client should be activated");
      cmClient.run();
      return cmClient.active();
    }//..?
  }//+++

  public final void ccCloseClient(){
    if(cmClient==null){
      System.err.println("pppplc.LcLinkedPLC.ccCloseClient()::"
        + "client is already null!!");
      return;
    }//..?
    cmClient.clear();
    System.out.println(".LcLinkedPLC.ccCloseClient()::cmClient.stop()");
    cmClient.stop();
    cmClient.dispose();
    cmClient=null;
    cmIsInitiated=false;
    cmLinkFlasher=0;
    cmRecvFlasher=0;
  }//+++
  
  //===

  @Override public boolean ccReadBit(int pxAddr, int pxBit){
    if(pxAddr<C_CHANNEL_OFFSET){
      return cmRecvMemory.ccReadBit(pxAddr,pxBit);
    }else{
      return cmSendMemory.ccReadBit(pxAddr-C_CHANNEL_OFFSET,pxBit);
    }//..?
  }//+++

  @Override public int ccReadWord(int pxAddr){
    if(pxAddr<C_CHANNEL_OFFSET){
      return cmRecvMemory.ccReadWord(pxAddr);
    }else{
      return cmSendMemory.ccReadWord(pxAddr-C_CHANNEL_OFFSET);
    }//..?
  }//+++

  @Override public void ccWriteBit(int pxAddr, int pxBit, boolean pxValue){
    if(pxAddr<C_CHANNEL_OFFSET){
      cmRecvMemory.ccWriteBit(pxAddr,pxBit,pxValue);
    }else{
      cmSendMemory.ccWriteBit(pxAddr-C_CHANNEL_OFFSET,pxBit,pxValue);
    }//..?
  }//+++

  @Override public void ccWriteWord(int pxAddr, int pxValue){
    if(pxAddr<C_CHANNEL_OFFSET){
      cmRecvMemory.ccWriteWord(pxAddr, pxValue);
    }else{
      cmSendMemory.ccWriteWord(pxAddr-C_CHANNEL_OFFSET, pxValue);
    }//..?
  }//+++
  
  //===
  
  @Deprecated public final void tstReadupMemoryContent(){
    String lpRecv=VcConst.ccToHexTableString(cmRecvMemory.ccGetData(), 8);
    System.out.println(".LcLinkedPLC.tstReadupMemoryContent()::\n"
      + lpRecv);
  }//+++
  
  @Deprecated public final void tstDummyupMemoryContent(int pxHeadValue){
    for(int i=0,s=cmSendMemory.ccGetSize();i<s;i++){
      cmRecvMemory.ccWriteWord(i, pxHeadValue+s-i);
      cmSendMemory.ccWriteWord(i, pxHeadValue+i);
    }//..~
  }//+++
  
  @Deprecated public final void tstReadupPLCAnswer(){
    String lpTitle="PLCAnswer";
    int cmDataLen=cmAnswer.length;
    if(cmDataLen<=0){
      System.err.println(">>> cant read up");
      return;
    }//..?
    for(int i=0; i<cmDataLen; i++){
      String lpMark="..";
      switch(i){
        case 14:lpMark="..COMMAND!!";break;
        case 18:lpMark="..COMMAND AMOUNT!!";break;
        case 20:lpMark="..MEMORY TYPE!!";break;
        case 21:lpMark="..MEMORY ADDR LOW";break;
        case 22:lpMark="..MEMORY ADDR MID";break;
        case 23:lpMark="..MEMORY ADDR HI";break;
        case 24:lpMark="..DATA AMOUNT";break;
        case 26:lpMark="..DATA WRITE LOW";break;
        case 27:lpMark="..DATA WRITE HI";break;
        default: break;
      }//..?
      System.out
        .println(lpTitle+hex(cmAnswer[i], 2)+"] $$_"
          +str(i)+"of"+str(cmDataLen)+lpMark);
    }//..~
    System.out.println(">>> over for"+lpTitle);
  }//+++

  @Deprecated public final void tstReadupAddressSetting(){
    System.out.println(".cmRecvStartAddr:"+C_RECV_HEAD_WORD_ADDR);
    System.out.println(".cmSendStartAddr:"+C_SEND_HEAD_WORD_ADDR);
  }//+++

}//***eof
