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

/**
 *
 * @author Key Parker from K.I.C
 */
public interface LiMemory{

  public int ccReadWord(int pxAddr);
  public boolean ccReadBit(int pxAddr, int pxBit);
  
  public void ccWriteWord(int pxAddr, int pxValue);
  public void ccWriteBit(int pxAddr, int pxBit, boolean pxValue);
  
}//***eof
