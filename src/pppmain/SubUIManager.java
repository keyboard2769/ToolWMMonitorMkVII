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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import processing.core.PApplet;
import static processing.core.PApplet.ceil;
import static processing.core.PApplet.nf;
import static processing.core.PApplet.nfc;
import static processing.core.PConstants.BOTTOM;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.TOP;

/**
 *
 * @author Key Parker from K.I.C
 */
public final class SubUIManager{
  
  static private final PApplet O_OWNER = MainSketch.ccGetSketch();

  private static final SubUIManager SELF = new SubUIManager();
  public static SubUIManager ccGetReference(){return SELF;}
  private SubUIManager(){}
  
  //===
  
  public final EcBaseCoordinator pbTheCoordinator=new EcBaseCoordinator();

  public final EcElement pbInitPL=new EcElement("init");
  public final EcElement pbLinkPL=new EcElement("link");
  public final EcElement pbRecvPL=new EcElement("recv");
  
  public final EcButton pbDisconnectSW = new EcButton("Dis.C", 0xAA06);
  public final EcButton pbReconnectSW = new EcButton("Re.C", 0xAA07);
  
  public final EcPane pbRecvPane=new EcPane("RECV DATA : PLC->PC");
  public final EcPane pbSendPane=new EcPane("SEND DATA : PC->PLC");

  public final EcButton
    pbRecvAddrIncmSW=new EcButton("+", 0xAA13),
    pbRecvAddrDecmSW=new EcButton("-", 0xAA12),
    pbRecvAddrPgupSW=new EcButton(">", 0xAA14),
    pbRecvAddrPgdnSW=new EcButton("<", 0xAA11)
  ;//...

  public final EcButton
    pbSendAddrIncmSW=new EcButton("+", 0xAA23),
    pbSendAddrDecmSW=new EcButton("-", 0xAA22),
    pbSendAddrPgupSW=new EcButton(">", 0xAA24),
    pbSendAddrPgdnSW=new EcButton("<", 0xAA21)
  ;//...

  public final EcValueBox pbRecvAddrCB=new EcValueBox("WM+", "00000");
  public final EcTextBox pbRecvHexCB = new EcTextBox("hex", "HHHH");
  public final EcValueBox pbSendAddrCB=new EcValueBox("WM+", "00000");
  public final EcTextBox pbSendHexCB = new EcTextBox("hex", "HHHH");
  
  public final List<EcLamp> pbDesRecvBitPL
   = Collections.unmodifiableList(Arrays.asList(
    new EcLamp("0"),new EcLamp("1"),new EcLamp("2"),new EcLamp("3"),
    new EcLamp("4"),new EcLamp("5"),new EcLamp("6"),new EcLamp("7"),
    new EcLamp("8"),new EcLamp("9"),new EcLamp("A"),new EcLamp("B"),
    new EcLamp("C"),new EcLamp("D"),new EcLamp("E"),new EcLamp("F")
  ));
  
  public final List<EcButton> pbDesSendBitSW
   = Collections.unmodifiableList(Arrays.asList(
    new EcButton("0", 0xBA00),new EcButton("1", 0xBA01),
    new EcButton("2", 0xBA02),new EcButton("3", 0xBA03),
    //
    new EcButton("4", 0xBA04),new EcButton("5", 0xBA05),
    new EcButton("6", 0xBA06),new EcButton("7", 0xBA07),
    //
    new EcButton("8", 0xBA08),new EcButton("9", 0xBA09),
    new EcButton("A", 0xBA0A),new EcButton("B", 0xBA0B),
    //
    new EcButton("C", 0xBA0C),new EcButton("D", 0xBA0D),
    new EcButton("E", 0xBA0E),new EcButton("F", 0xBA0F)
  ));
  
  public final void ccInit(){
    
    //-- connection ** indicator
    pbInitPL.ccSetSize(48, 16);
    pbLinkPL.ccSetSize(pbInitPL);
    pbRecvPL.ccSetSize(pbLinkPL);
    pbInitPL.ccSetLocation(5, 22);
    pbLinkPL.ccSetLocation(pbInitPL, 4, 0);
    pbRecvPL.ccSetLocation(pbLinkPL, 4, 0);
    pbTheCoordinator.ccAddElement(pbInitPL);
    pbTheCoordinator.ccAddElement(pbLinkPL);
    pbTheCoordinator.ccAddElement(pbRecvPL);
    
    //-- connection ** button
    pbDisconnectSW.ccSetSize(pbInitPL);
    pbDisconnectSW.ccSetColor(0xFFEECCCC);
    pbReconnectSW.ccSetSize(pbInitPL);
    pbDisconnectSW.ccSetLocation(pbRecvPL, 16, 0);
    pbReconnectSW.ccSetLocation(pbDisconnectSW, 2, 0);
    pbTheCoordinator.ccAddElement(pbDisconnectSW);
    pbTheCoordinator.ccAddElement(pbReconnectSW);
    
    //-- pane layer
    pbRecvPane.ccSetLocation(5, 60);
    pbRecvPane.ccSetSize(320-10, (240-25-14)/2-20);
    pbSendPane.ccSetSize(pbRecvPane);
    pbSendPane.ccSetLocation(pbRecvPane, 0, 10);
 
    pbTheCoordinator.ccAddShape(pbRecvPane);
    pbTheCoordinator.ccAddShape(pbSendPane);

    //-- create ** recv group
    pbRecvAddrCB.ccSetSize(64,18);
    pbRecvAddrCB.ccSetNameAlign('l');
    pbRecvHexCB.ccSetSize(64,18);
    pbRecvHexCB.ccSetNameAlign('l');
    
    pbRecvAddrPgdnSW.ccSetSize(18,18);
    pbRecvAddrDecmSW.ccSetSize(18,18);
    pbRecvAddrIncmSW.ccSetSize(18,18);
    pbRecvAddrPgupSW.ccSetSize(18,18);

    pbRecvAddrPgdnSW.ccSetLocation(pbRecvPane, 5, 22);
    pbRecvAddrDecmSW.ccSetLocation(pbRecvAddrPgdnSW, 2, 0);
    pbRecvAddrIncmSW.ccSetLocation(pbRecvAddrDecmSW, 2, 0);
    pbRecvAddrPgupSW.ccSetLocation(pbRecvAddrIncmSW, 2, 0);
    pbRecvAddrCB.ccSetLocation(pbRecvAddrPgupSW, 45, 0);
    pbRecvHexCB.ccSetLocation(pbRecvAddrCB, 45, 0);

    pbTheCoordinator.ccAddElement(pbRecvAddrPgdnSW);
    pbTheCoordinator.ccAddElement(pbRecvAddrDecmSW);
    pbTheCoordinator.ccAddElement(pbRecvAddrIncmSW);
    pbTheCoordinator.ccAddElement(pbRecvAddrPgupSW);
    pbTheCoordinator.ccAddElement(pbRecvAddrCB);
    pbTheCoordinator.ccAddElement(pbRecvHexCB);
    
    //-- create ** send group
    pbSendAddrCB.ccSetNameAlign('l');
    pbSendAddrCB.ccSetSize(64,18);
    pbSendHexCB.ccSetNameAlign('l');
    pbSendHexCB.ccSetSize(64,18);
    
    pbSendAddrPgdnSW.ccSetSize(18,18);
    pbSendAddrDecmSW.ccSetSize(18,18);
    pbSendAddrIncmSW.ccSetSize(18,18);
    pbSendAddrPgupSW.ccSetSize(18,18);

    pbSendAddrPgdnSW.ccSetLocation(pbSendPane, 5, 22);
    pbSendAddrDecmSW.ccSetLocation(pbSendAddrPgdnSW, 2, 0);
    pbSendAddrIncmSW.ccSetLocation(pbSendAddrDecmSW, 2, 0);
    pbSendAddrPgupSW.ccSetLocation(pbSendAddrIncmSW, 2, 0);
    pbSendAddrCB.ccSetLocation(pbSendAddrPgupSW, 45, 0);
    pbSendHexCB.ccSetLocation(pbSendAddrCB, 45, 0);

    pbTheCoordinator.ccAddElement(pbSendAddrPgdnSW);
    pbTheCoordinator.ccAddElement(pbSendAddrDecmSW);
    pbTheCoordinator.ccAddElement(pbSendAddrIncmSW);
    pbTheCoordinator.ccAddElement(pbSendAddrPgupSW);
    pbTheCoordinator.ccAddElement(pbSendAddrCB);
    pbTheCoordinator.ccAddElement(pbSendHexCB);

    //-- create ** register bit buttons
    for(int i=0;i<16;i++){
      pbDesRecvBitPL.get(i).ccSetSize(16,16);
      pbDesSendBitSW.get(i).ccSetSize(16,16);
      pbTheCoordinator.ccAddElement(pbDesRecvBitPL.get(i));
      pbTheCoordinator.ccAddElement(pbDesSendBitSW.get(i));
    }//..~
    
    //-- create ** relocate bit buttons 
    pbDesRecvBitPL.get(15).ccSetLocation(pbRecvAddrPgdnSW, 0, 8);
    pbDesSendBitSW.get(15).ccSetLocation(pbSendAddrPgdnSW, 0, 8);
    for(int i=14;i>=0;i--){
      pbDesRecvBitPL.get(i).ccSetLocation(pbDesRecvBitPL.get(i+1), 2, 0);
      pbDesSendBitSW.get(i).ccSetLocation(pbDesSendBitSW.get(i+1), 2, 0);
    }//..~
    
    //-- post    
    
  }//..!
  
  //===
  
  public static final boolean ccIsKeyPressed(char pxKey){
    return O_OWNER.keyPressed && (O_OWNER.key==pxKey);
  }//+++
  
  public static final boolean ccIsMousePressed(int pxID){
    return O_OWNER.mousePressed
      && (SELF.pbTheCoordinator.ccGetMouseOverID()==pxID);
  }//+++
  
  //===
  
  public class EcPoint{

    /*
    static protected PApplet pbOwner=null;
    static public final void ccInitOwner(PApplet pxOwner)
      {pbOwner=pxOwner;}//+++
    static public final boolean ccHasOwner()
      {return pbOwner!=null;}//+++
     */
    protected int cmX=8, cmY=8;

    protected final void drawPoint(int pxColor){
      O_OWNER.set(cmX, cmY, pxColor);
    }//+++

    public final void ccSetLocation(int pxX, int pxY){
      if(pxX>=0){
        cmX=pxX;
      }
      if(pxY>=0){
        cmY=pxY;
      }
    }//+++

    public final void ccShiftLocation(int pxOffsetX, int pxOffsetY){
      cmX+=pxOffsetX;
      cmY+=pxOffsetY;
    }//+++

    public final int ccGetX(){
      return cmX;
    }//+++

    public final int ccGetY(){
      return cmY;
    }//+++

  }//***

  public class EcRect extends EcPoint{

    protected int cmW=8, cmH=8;

    protected final void drawRect(int pxColor){
      O_OWNER.fill(pxColor);
      O_OWNER.rect(cmX, cmY, cmW, cmH);
    }//+++

    public final void ccSetLocation(
      EcRect pxTarget, int pxOffsetX, int pxOffsetY
    ){
      cmX=pxOffsetX;
      cmY=pxOffsetY;
      if(pxTarget!=null){
        cmX+=pxTarget.cmX+(pxOffsetY==0?pxTarget.cmW:0);
        cmY+=pxTarget.cmY+(pxOffsetX==0?pxTarget.cmH:0);
      }//..?
    }//+++

    public final void ccSetSize(int pxW, int pxH){
      if(pxW!=0){
        cmW=pxW;
      }
      if(pxH!=0){
        cmH=pxH;
      }
    }//+++

    public final void ccSetSize(EcRect pxTarget){
      if(pxTarget!=null){
        cmW=pxTarget.ccGetW();
        cmH=pxTarget.ccGetH();
      }
    }//+++

    public final void ccSetSize(EcRect pxTarget, boolean pxInW, boolean pxInH){
      if(pxTarget!=null){
        if(pxInW){
          cmW=pxTarget.ccGetW();
        }
        if(pxInH){
          cmH=pxTarget.ccGetH();
        }
      }//..?
    }//+++

    public final void ccSetSize(EcRect pxTarget, int pxOffsetW, int pxOffsetH){
      if(pxTarget!=null){
        cmW=pxTarget.ccGetW()+pxOffsetW;
        cmH=pxTarget.ccGetH()+pxOffsetH;
      }else{
        cmW+=pxOffsetW;
        cmH+=pxOffsetH;
      }//..?
    }//+++

    public final void ccShiftSize(int pxOffsetW, int pxOffsetH){
      cmW+=pxOffsetW;
      cmH+=pxOffsetH;
    }//+++

    public final void ccSetEndPoint(int pxEndX, int pxEndY){
      if(pxEndX>=0){
        cmW=pxEndX-cmX;
      }
      if(pxEndY>=0){
        cmH=pxEndY-cmY;
      }
    }//+++

    public final void ccSetEndPoint(
      EcRect pxTarget, int pxOffsetX, int pxOffsetY
    ){
      if(pxTarget==null){
        ccSetEndPoint(pxOffsetX, pxOffsetY);
        return;
      }
      cmW=pxTarget.ccEndX()-cmX+pxOffsetX;
      cmH=pxTarget.ccEndY()-cmY+pxOffsetY;
    }//+++

    public final void ccSetBound(int pxX, int pxY, int pxW, int pxH){
      cmX=pxX;
      cmY=pxY;
      cmW=pxW;
      cmH=pxH;
    }//+++

    public final int ccGetW(){
      return cmW;
    }//+++

    public final int ccGetH(){
      return cmH;
    }//+++

    public final int ccCenterX(){
      return cmX+cmW/2;
    }//+++

    public final int ccCenterY(){
      return cmY+cmH/2;
    }//+++

    public final int ccEndX(){
      return cmX+cmW;
    }//+++

    public final int ccEndY(){
      return cmY+cmH;
    }//+++

    public final boolean ccContains(int pxX, int pxY){
      return (pxX>cmX)&&(pxX<(cmX+cmW))
        &&(pxY>cmY)&&(pxY<(cmY+cmH));
    }//+++

    /*static*/ public final
    void ccFlowLayout(
      ArrayList<EcRect> pxList,
      int pxGap, boolean pxIsVertical, boolean pxIsReversed)
    { if(pxList==null){
        return;
      }
      if(pxList.isEmpty()){
        return;
      }
      if(pxIsReversed){
        for(int i=1; i<pxList.size(); i++){
          EcRect it=pxList.get(i);
          EcRect prev=pxList.get(i-1);
          int lpX=pxIsVertical
            ?prev.ccGetX()
            :prev.ccGetX()-prev.ccGetW()-pxGap;
          int lpY=pxIsVertical
            ?prev.ccGetY()-prev.ccGetH()-pxGap
            :prev.ccGetY();
          it.ccSetLocation(lpX, lpY);
        }//..~
      }else{
        for(int i=1; i<pxList.size(); i++){
          EcRect it=pxList.get(i);
          EcRect prev=pxList.get(i-1);
          it.ccSetLocation(prev, pxIsVertical?0:pxGap, pxIsVertical?pxGap:0);
        }//..~
      }//..?
    }//+++

  }//***

  public class EcElement extends EcRect implements EiUpdatable{

    private static final int
      //-- pix
      C_NAME_GAP=2,
      C_TEXT_MARG_X=3,
      C_TEXT_ADJ_Y=-2,
      C_DEFAULT_AUTOSIZE_MARGIN=2,
      C_DEFAULT_AUTOSIZE_HEIGHT=18
    ;//...

    protected int
      cmID=0xFFFF,//EcFactory.C_ID_IGNORE,
      cmOnColor=0xFFEEEE33,//EcFactory.C_YELLOW,
      cmOffColor=0xFF777777,//EcFactory.C_DIM_GRAY,
      cmNameColor=0xFFCCCCCC,//EcFactory.C_LIT_GRAY,
      cmTextColor=0xFF333333//EcFactory.C_DARK_GRAY
    ;//...

    protected String
      cmKey="<key/>",
      cmName="<name/>",
      cmText="<text/>"
    ;//...

    protected boolean
      cmIsActivated=false,
      cmIsEnabled=true,
      cmIsVisible=true
    ;//...

    protected char
      cmNameAlign='x',
      cmTextAlign='c'
    ;//...

    public EcElement(){
      super();
    }//..!
    
    public EcElement(String pxKey){
      super();
      ccSetupKey(pxKey);
      ccSetSize();
    }//..!
    
    public EcElement(String pxKey, int pxID){
      super();
      ccSetupKey(pxKey);
      ccSetID(pxID);
      ccSetSize();
    }//..!
    
    @Override public void ccUpdate(){

      if(!cmIsVisible){
        return;
      }
      drawRect(ccActColor());
      drawText(cmTextColor);
      drawName(cmNameColor);
    }//+++

    protected final void drawText(int pxColor){
      int lpX=ccCenterX();
      O_OWNER.textAlign(CENTER, CENTER);
      switch(cmTextAlign){

        case 'l':
          lpX=cmX+C_TEXT_MARG_X;
          O_OWNER.textAlign(LEFT, CENTER);
          break;

        case 'r':
          lpX=ccEndX()-C_TEXT_MARG_X;
          O_OWNER.textAlign(RIGHT, CENTER);
          break;

      }//..?
      O_OWNER.fill(pxColor);
      O_OWNER.text(cmText, lpX, ccCenterY()+C_TEXT_ADJ_Y);
      O_OWNER.textAlign(LEFT, TOP);
    }//+++

    protected final void drawName(int pxColor){
      int lpX=ccCenterX();
      int lpY=ccCenterY();
      switch(cmNameAlign){
        case 'a':
          lpY=cmY-C_NAME_GAP;
          O_OWNER.textAlign(CENTER, BOTTOM);
          break;
        case 'b':
          lpY=C_NAME_GAP+cmY+cmH;
          O_OWNER.textAlign(CENTER, TOP);
          break;
        case 'l':
          lpX=cmX-C_NAME_GAP;
          O_OWNER.textAlign(RIGHT, CENTER);
          break;
        case 'r':
          lpX=C_NAME_GAP+cmX+cmW;
          O_OWNER.textAlign(LEFT, CENTER);
          break;
        default:
          return;
      }
      //--
      O_OWNER.fill(pxColor);
      O_OWNER.text(cmName, lpX, C_TEXT_ADJ_Y+lpY);
      O_OWNER.textAlign(LEFT, TOP);
    }//+++

    protected final void ccActFill(){
      O_OWNER.fill(cmIsActivated?cmOnColor:cmOffColor);
    }//+++

    protected final int ccActColor(){
      return cmIsActivated?cmOnColor:cmOffColor;
    }//+++

    public final void ccSetID(int pxID){
      cmID=pxID;
    }//+++

    public final void ccSetName(String pxName){
      cmName=pxName;
    }//+++

    public final void ccSetText(String pxText){
      cmText=pxText;
    }//+++

    public final void ccSetNameAlign(char pxMode_ablr){
      cmNameAlign=pxMode_ablr;
    }//+++

    public final void ccSetTextAlign(char pxMode_lcr){
      cmTextAlign=pxMode_lcr;
    }//+++

    public final void ccSetColor(int pxOn, int pxOff){
      cmOnColor=pxOn;
      cmOffColor=pxOff;
    }//+++

    public final void ccSetColor(int pxOn){
      cmOnColor=pxOn;
    }//+++    

    public final void ccSetNameColor(int pxColor){
      cmNameColor=pxColor;
    }//+++

    public final void ccSetTextColor(int pxColor){
      cmTextColor=pxColor;
    }//+++

    public final void ccSetIsActivated(boolean pxStatus){
      cmIsActivated=pxStatus;
    }//+++

    public final void ccSetIsActivated(){
      cmIsActivated=!cmIsActivated;
    }//+++

    public final void ccSetIsActivated(int pxFocusedID){
      cmIsActivated=(cmID==pxFocusedID);
    }//+++

    public final void ccSetIsEnabled(boolean pxStatus){
      cmIsEnabled=pxStatus;
    }//+++

    public final void ccSetIsVisible(boolean pxStatus){
      cmIsVisible=pxStatus;
    }//+++

    public final void ccSetSize(){
      cmW = ceil(O_OWNER.textWidth(cmText))
            +C_DEFAULT_AUTOSIZE_MARGIN*2;
      cmH = C_DEFAULT_AUTOSIZE_HEIGHT;
      for(char it:cmText.toCharArray())
        {if(it=='\n'){cmH+=C_DEFAULT_AUTOSIZE_HEIGHT;}}
    }//+++

    public final void ccSetKey(String pxKey){
      cmKey=pxKey;
    }//+++

    public final void ccSetupKey(String pxKey){
      cmKey=pxKey;
      cmName=pxKey;
      cmText=pxKey;
    }//+++

    public final void ccMatchStyle(EcElement pxTarget){
      cmOnColor=pxTarget.cmOnColor;
      cmOffColor=pxTarget.cmOffColor;
      cmNameAlign=pxTarget.cmNameAlign;
      cmNameColor=pxTarget.cmNameColor;
      cmTextAlign=pxTarget.cmTextAlign;
      cmTextColor=pxTarget.cmTextColor;
    }//+++

    public final int ccTellMouseID(){
      return ccIsMouseHovered()?cmID:0;
    }//+++

    public final boolean ccIsMouseHovered(){
      return ccContains(O_OWNER.mouseX, O_OWNER.mouseY)&&cmIsEnabled;
    }//+++

    public final boolean ccIsActivated(){
      return cmIsActivated;
    }//+++

    public final boolean ccIsMousePressed(){
      return ccIsMouseHovered()&&O_OWNER.mousePressed;
    }//+++

    public final int ccGetID(){
      return cmID;
    }//+++

    public final String ccGetText(){
      return cmText;
    }//+++

  }//***

  public class EcButton extends EcElement{

    private static final int
      //-- pix
      C_STROKE_THICK=2,
      //-- color
      C_COLOR_STROKE=0xFF555555,
      C_COLOR_FILL_OVER=0xFFAACCAA,
      C_COLOR_FILL_PRESSED=0xFF99BB99,
      C_COLOR_FILL_NORMAL=0xFF999999
    ;//...

    public EcButton(){
      super();
    }//..!
    
    public EcButton(String pxKey, int pxID){
      super(pxKey, pxID);
    }//..!
    
    @Override public void ccUpdate(){

      drawDefualtButton();
      drawName(cmNameColor);
      drawText(ccActColor());

    }//+++

    protected final void drawDefualtButton(){

      O_OWNER.fill(C_COLOR_STROKE);
      O_OWNER.rect(cmX, cmY, cmW, cmH);

      O_OWNER.fill(ccIsMouseHovered()
        ?(O_OWNER.mousePressed
          ?C_COLOR_FILL_PRESSED
          :C_COLOR_FILL_OVER):C_COLOR_FILL_NORMAL
      );
      O_OWNER.rect(
        cmX+C_STROKE_THICK, cmY+C_STROKE_THICK,
        cmW-C_STROKE_THICK*2, cmH-C_STROKE_THICK*2
      );

    }//+++

  }//***

  public class EcLamp extends EcElement{

    private static final int //-- pix
      C_STROKE_THICK=4,
      //-- color
      C_COLOR_STROKE=0xFF555555;//...

    public EcLamp(){
      super();
    }//..!
    
    public EcLamp(String pxKey){
      super(pxKey);
    }//..!
    
    @Override public void ccUpdate(){

      drawRoundLamp(ccActColor());
      drawText(cmTextColor);
      drawName(cmNameColor);

    }//+++

    protected void drawRoundLamp(int pxColor){

      int lpCenterX=ccCenterX();
      int lpCenterY=ccCenterY();

      O_OWNER.fill(C_COLOR_STROKE);
      O_OWNER.ellipse(lpCenterX, lpCenterY, cmW, cmH);

      O_OWNER.fill(pxColor);
      O_OWNER.ellipse(
        lpCenterX,
        lpCenterY,
        cmW-C_STROKE_THICK,
        cmH-C_STROKE_THICK
      );

    }//+++

  }//***

  public class EcTextBox extends EcElement{

    private static final int //-- pix
      C_SHADOW_THICK=2,
      //-- color
      C_COLOR_SHADOW=0xFF555555;//...

    public EcTextBox(){
      super();
      ccSetTextAlign('l');
      ccSetTextColor(0xFFCCCCCC);
    }//..!
    
    public EcTextBox(String pxKey, String pxText){
      super();
      ccSetTextAlign('l');
      ccSetTextColor(0xFFCCCCCC);
      ccSetupKey(pxKey);
      ccSetText(pxText);
      ccSetSize();
    }//..!
    
    public EcTextBox(String pxKey, String pxText, int pxID){
      super();
      ccSetTextAlign('l');
      ccSetTextColor(0xFFCCCCCC);
      ccSetupKey(pxKey);
      ccSetText(pxText);
      ccSetID(pxID);
      ccSetSize();
    }//..!
    
    @Override public void ccUpdate(){

      drawDefaultTextBox();
      drawText(cmTextColor);
      drawName(cmNameColor);

    }//+++

    protected final void drawDefaultTextBox(){

      int lpW=cmW-C_SHADOW_THICK;
      int lpH=cmH-C_SHADOW_THICK;

      O_OWNER.fill(C_COLOR_SHADOW);
      O_OWNER.rect(cmX+C_SHADOW_THICK, cmY+C_SHADOW_THICK, lpW, lpH);
      O_OWNER.fill(cmIsActivated?cmOnColor:cmOffColor);
      O_OWNER.rect(cmX, cmY, lpW, lpH);

    }//+++

  }//***

  public class EcValueBox extends EcElement{

    protected int cmDigit=4;

    protected String cmUnit=" ";

    public EcValueBox(){
      super();
      ccSetTextAlign('r');
    }//..!
    
    public EcValueBox(String pxKey, String pxForm){
      super();
      ccSetTextAlign('r');
      ccSetupKey(pxKey);
      ccSetText(pxForm);
      ccSetSize();
    }//..!
    
    public EcValueBox(String pxKey, String pxForm, int pxID){
      super();
      ccSetTextAlign('r');
      ccSetupKey(pxKey);
      ccSetText(pxForm);
      ccSetID(pxID);
      ccSetSize();
    }//..!

    @Override public void ccUpdate(){

      drawDefaultValueBox();
      drawText(cmTextColor);
      drawName(cmNameColor);

    }//+++

    protected final void drawDefaultValueBox(){

      O_OWNER.stroke(0xCC);
      O_OWNER.fill(cmIsActivated?cmOnColor:cmOffColor);
      O_OWNER.rect(cmX, cmY, cmW, cmH);
      O_OWNER.noStroke();

    }//+++

    public final void ccSetValue(int pxVal){
      cmText=nf(pxVal, cmDigit)+cmUnit;
    }//+++

    public final void ccSetValue(int pxVal, int pxDigit){
      cmDigit=pxDigit;
      cmText=nf(pxVal, cmDigit)+cmUnit;
    }//+++

    public final void ccSetValue(float pxVal, int pxDigit){
      cmDigit=pxDigit;
      cmText=nfc(pxVal, cmDigit)+cmUnit;
    }//+++

    public final void ccSetUnit(String pxUnit){
      cmUnit=pxUnit;
    }//+++

  }//***

  public class EcShape extends EcRect implements EiUpdatable{

    protected int cmBaseColor=0xFFCCCCCC;//EcFactory.C_LIT_GRAY;

    public final void ccSetBaseColor(int pxColor){
      cmBaseColor=pxColor;
    }//+++

    @Override public void ccUpdate(){
      drawRect(cmBaseColor);
    }//+++

  }//***

  public class EcPane extends EcShape{

    private static final int C_TEXT_ADJ_Y=2;

    private String cmTitle="<title/>";
    
    public EcPane(){
      super();
    }//..!
    
    public EcPane(String pxTitle){
      super();
      ccSetTitle(pxTitle);
    }//..!

    @Override public void ccUpdate(){

      O_OWNER.fill(0xFF);
      O_OWNER.rect(cmX, cmY, cmW, cmH);

      O_OWNER.fill(0x0);
      O_OWNER.rect(cmX+2, cmY+18, cmW-4, cmH-21);

      O_OWNER.text(cmTitle, cmX+2, cmY+2+C_TEXT_ADJ_Y);

    }//+++

    public final void ccSetTitle(String pxTitle){
      cmTitle=pxTitle;
    }//+++

  }//***

  public class EcBaseCoordinator implements EiUpdatable{

    protected int pbMouseOverID;

    private final ArrayList<EiUpdatable> cmShapeList;

    private final ArrayList<EcElement> cmElementList;

    public EcBaseCoordinator(){
      pbMouseOverID=0;
      cmShapeList=new ArrayList();
      cmElementList=new ArrayList();
    }//..!

    @Override public void ccUpdate(){

      pbMouseOverID=0;

      for(EiUpdatable it: cmShapeList){
        it.ccUpdate();
      }//..~
      for(EcElement it: cmElementList){
        it.ccUpdate();
        if(it.ccIsMouseHovered()){
          pbMouseOverID=it.ccGetID();
        }
      }

    }//+++

    public final void ccAddElement(EcElement pxElement){
      cmElementList.add(pxElement);
    }//+++

    public final void ccAddElement(ArrayList<EcElement> pxList){
      cmElementList.addAll(pxList);
    }//+++

    public final void ccAddShape(EiUpdatable pxShape){
      cmShapeList.add(pxShape);
    }//+++

    public final void ccAddShape(ArrayList<EiUpdatable> pxList){
      cmShapeList.addAll(pxList);
    }//+++

    public final int ccGetMouseOverID(){
      return pbMouseOverID;
    }//+++

  }//***

}//***eof
