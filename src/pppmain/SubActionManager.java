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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import pppmain.SubUIManager.EcButton;
import pppmodel.MainModel;
import pppmodel.ZcOnDelayTimer;
import pppmodel.ZcPulser;
import pppplc.LcLinkedPLC;
import static processing.core.PApplet.hex;

/**
 *
 * @author Key Parker from K.I.C
 */
public final class SubActionManager implements EiUpdatable{

  private static final SubActionManager SELF = new SubActionManager();
  public static SubActionManager ccGetReference(){return SELF;}
  private SubActionManager(){}
  
  //=== 
  
  public final ZcOnDelayTimer cmLinkageWDT
    = new ZcOnDelayTimer(16*3);//...3S
  public final ZcPulser cmLinkageWDTPulser
    = new ZcPulser();
  
  //=== system action
  
  private final EiTriggerable cmActTest = new EiTriggerable() {
    @Override public void ccTrigger(){
      
      System.err.println(".cmActTest::you are not supposed to use this action");    
      
    }//+++
  };
  
  private final EiTriggerable cmActQuit = new EiTriggerable() {
    @Override public void ccTrigger(){
      
      //-- flush
      LcLinkedPLC.ccGetReference().ccCloseClient();

      //-- default
      System.out.println("..cmQuitACtion.::called exit()");
      MainSketch.ccGetSketch().exit();
      
    }//+++
  };
  
  //=== connection action
  
  private final EiTriggerable cmActDisconnect = new EiTriggerable() {
    @Override public void ccTrigger(){
      LcLinkedPLC.ccGetReference().ccCloseClient();
    }//+++
  };
  
  private final EiTriggerable cmActReconnect = new EiTriggerable() {
    @Override public void ccTrigger(){
      LcLinkedPLC.ccGetReference().ccOpenClient();
    }//+++
  };
    
  //=== page shift action ** receive
  
  private final EiTriggerable cmActRecvAddrPageDown = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedRecvAddr.ccRoll(-10);
    }//+++
  };
  
  private final EiTriggerable cmActRecvAddrDecrement = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedRecvAddr.ccRoll(-1);
    }//+++
  };
    
  private final EiTriggerable cmActRecvAddrIncrement = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedRecvAddr.ccRoll(1);
    }//+++
  };
      
  private final EiTriggerable cmActRecvAddrPageUp = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedRecvAddr.ccRoll(10);
    }//+++
  };
  
  //=== page shift action ** send
  
  private final EiTriggerable cmActSendAddrPageDown = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedSendAddr.ccRoll(-10);
    }//+++
  };
  
  private final EiTriggerable cmActSendAddrDecrement = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedSendAddr.ccRoll(-1);
    }//+++
  };
    
  private final EiTriggerable cmActSendAddrIncrement = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedSendAddr.ccRoll(1);
    }//+++
  };
      
  private final EiTriggerable cmActSendAddrPageUp = new EiTriggerable() {
    @Override public void ccTrigger(){
      pppmodel.MainModel.ccGetReference().cmSelectedSendAddr.ccRoll(10);
    }//+++
  };
  
  //===
  
  private final Queue<EiTriggerable> cmQueueOfLoopAction
    =new LinkedList<>();
  private final HashMap<Integer, EcButton> cmMapOfButton
    =new HashMap<>();
  private final HashMap<Integer, EiTriggerable> cmMapOfActionID
    =new HashMap<>();
  private final HashMap<Character, EiTriggerable> cmMapOfActionKeyEvent
    =new HashMap<>();

  public final void ccInit(){
    
    ccRegisterKeyevent('f', cmActTest);
    ccRegisterKeyevent('q', cmActQuit);
    
    ccRegisterKeyevent('w', cmActRecvAddrIncrement);
    ccRegisterKeyevent('s', cmActRecvAddrDecrement);
    ccRegisterKeyevent('a', cmActRecvAddrPageDown);
    ccRegisterKeyevent('d', cmActRecvAddrPageUp);
    
    ccRegisterButton(SubUIManager.ccGetReference().pbDisconnectSW, cmActDisconnect);
    ccRegisterButton(SubUIManager.ccGetReference().pbReconnectSW, cmActReconnect);
    
    ccRegisterButton
      (SubUIManager.ccGetReference().pbRecvAddrPgdnSW,cmActRecvAddrPageDown);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbRecvAddrDecmSW,cmActRecvAddrDecrement);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbRecvAddrIncmSW,cmActRecvAddrIncrement);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbRecvAddrPgupSW,cmActRecvAddrPageUp);
    
    ccRegisterButton
      (SubUIManager.ccGetReference().pbSendAddrPgdnSW,cmActSendAddrPageDown);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbSendAddrDecmSW,cmActSendAddrDecrement);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbSendAddrIncmSW,cmActSendAddrIncrement);
    ccRegisterButton
      (SubUIManager.ccGetReference().pbSendAddrPgupSW,cmActSendAddrPageUp);
    
  }//..!
  
  //===
  
  public final void ccRegisterKeyevent(char pxKey, EiTriggerable pxAction){
    cmMapOfActionKeyEvent.put(pxKey, pxAction);
  }//+++
  
  public final void ccKeyPressed(char pxKey){
    if(cmMapOfActionKeyEvent==null){return;}
    if(cmMapOfActionKeyEvent.isEmpty()){return;}
    if(!cmMapOfActionKeyEvent.containsKey(pxKey)){return;}
    EiTriggerable lpAction = cmMapOfActionKeyEvent.getOrDefault(pxKey, null);
    if(lpAction!=null){lpAction.ccTrigger();}
  }//+++
  
  public final void ccRegisterButton(EcButton pxButton,EiTriggerable pxAction){
    cmMapOfButton.put(pxButton.ccGetID(), pxButton);
    cmMapOfActionID.put(pxButton.ccGetID(), pxAction);
  }//+++
  
  public final void ccMousePressed(int pxButtonID){
    if(cmMapOfActionID==null){return;}
    if(cmMapOfActionID.isEmpty()){return;}
    if(!cmMapOfActionID.containsKey(pxButtonID)){return;}
    EiTriggerable lpAction = cmMapOfActionID.getOrDefault(pxButtonID, null);
    if(lpAction!=null){lpAction.ccTrigger();}
  }//+++
  
  //===

  @Override public void ccUpdate(){
    
    ssQueueKeep();
    
    ssBindInputUI();
    
    ssLocalLogic();
    
    ssBindOutputUI();
  
  }//+++
  
  private void ssQueueKeep(){
    if(cmQueueOfLoopAction.isEmpty()){return;}
    cmQueueOfLoopAction.poll().ccTrigger();
  }//+++
  
  private void ssBindInputUI(){
    
    for(int i=0;i<16;i++){
      LcLinkedPLC.ccGetReference().ccWriteBit(
        LcLinkedPLC.C_CHANNEL_OFFSET
          +MainModel.ccGetReference().cmSelectedSendAddr.ccGetValue(),
        i,
        SubUIManager.ccGetReference().pbDesSendBitSW.get(i).ccIsMousePressed()
      );
    }//+++
    
    
  }//+++
  
  private void ssBindOutputUI(){
    
    //-- linkage indicator
    SubUIManager.ccGetReference().pbInitPL.ccSetIsActivated
      (LcLinkedPLC.ccGetReference().ccIsInitiated());
    if(LcLinkedPLC.ccGetReference().ccGetLinkFlasher())
      {SubUIManager.ccGetReference().pbLinkPL.ccSetIsActivated();}
    if(LcLinkedPLC.ccGetReference().ccGetRecvFlasher())
      {SubUIManager.ccGetReference().pbRecvPL.ccSetIsActivated();}
    
    //-- linkage button
    SubUIManager.ccGetReference().pbDisconnectSW.ccSetIsActivated(
      cmLinkageWDT.ccIsUp()
    );
    
    //-- receive address modify buttons
    SubUIManager.ccGetReference().pbRecvAddrIncmSW.ccSetIsActivated(
        SubUIManager.ccGetReference().ccIsKeyPressed('w')
      ||SubUIManager.ccGetReference().pbRecvAddrIncmSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbRecvAddrDecmSW.ccSetIsActivated(
        SubUIManager.ccGetReference().ccIsKeyPressed('s')
      ||SubUIManager.ccGetReference().pbRecvAddrDecmSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbRecvAddrPgdnSW.ccSetIsActivated(
        SubUIManager.ccGetReference().ccIsKeyPressed('a')
      ||SubUIManager.ccGetReference().pbRecvAddrPgdnSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbRecvAddrPgupSW.ccSetIsActivated(
        SubUIManager.ccGetReference().ccIsKeyPressed('d')
      ||SubUIManager.ccGetReference().pbRecvAddrPgupSW.ccIsMousePressed()
    );
    
    //-- send address modify buttons
    SubUIManager.ccGetReference().pbSendAddrIncmSW.ccSetIsActivated(
      //[opt]::TcPane.ccGetReference().ccIsKeyPressed(%up%) ||
      SubUIManager.ccGetReference().pbSendAddrIncmSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbSendAddrDecmSW.ccSetIsActivated(
      //[opt]::TcPane.ccGetReference().ccIsKeyPressed(%down%) ||
      SubUIManager.ccGetReference().pbSendAddrDecmSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbSendAddrPgdnSW.ccSetIsActivated(
      //[opt]::TcPane.ccGetReference().ccIsKeyPressed(%left%) ||
      SubUIManager.ccGetReference().pbSendAddrPgdnSW.ccIsMousePressed()
    );
    SubUIManager.ccGetReference().pbSendAddrPgupSW.ccSetIsActivated(
      //[opt]::TcPane.ccGetReference().ccIsKeyPressed(%right%) ||
      SubUIManager.ccGetReference().pbSendAddrPgupSW.ccIsMousePressed()
    );
    
    //-- receive address 
    SubUIManager.ccGetReference().pbRecvAddrCB.ccSetValue
      (MainModel.ccGetReference().cmSelectedRecvAddr.ccGetValue());
    
    //-- receive value 
    int lpSelectdReceiveValue=LcLinkedPLC.ccGetReference().ccReadWord
      (MainModel.ccGetReference().cmSelectedRecvAddr.ccGetValue());
    SubUIManager.ccGetReference().pbRecvHexCB.ccSetText
      (hex(lpSelectdReceiveValue,4));
    
    //-- send address
    SubUIManager.ccGetReference().pbSendAddrCB.ccSetValue
      (MainModel.ccGetReference().cmSelectedSendAddr.ccGetValue());
    
    //-- send value
    int lpSelectdSendValue=LcLinkedPLC.ccGetReference().ccReadWord
      (MainModel.ccGetReference().cmSelectedSendAddr.ccGetValue()
        +LcLinkedPLC.C_CHANNEL_OFFSET);
    SubUIManager.ccGetReference().pbSendHexCB.ccSetText
      (hex(lpSelectdSendValue,4));
    
    //-- receive bit lamps
    for(int i=0;i<16;i++){
      SubUIManager.ccGetReference().pbDesRecvBitPL.get(i).ccSetIsActivated(
        LcLinkedPLC.ccGetReference().ccReadBit
          (MainModel.ccGetReference().cmSelectedRecvAddr.ccGetValue(), i)
      );
    }//..~
  
  }//+++
  
  private void ssLocalLogic(){
    
    cmLinkageWDT.ccAct(
      !LcLinkedPLC.ccGetReference().ccGetRecvFlasher()
      &&LcLinkedPLC.ccGetReference().ccIsInitiated()
    );
    if(cmLinkageWDTPulser.ccUpPulse(cmLinkageWDT.ccIsUp())){
      System.out.println("..ssLocalLogic()::the watch dog has found you!!");
      cmQueueOfLoopAction.offer(cmActDisconnect);
    }//..?
  
  }//+++
  
}//***eof
