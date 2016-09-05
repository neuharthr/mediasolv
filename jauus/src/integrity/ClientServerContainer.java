/*************************************************************************
 Copyright (C) 2005  Steve Gee
 ioexcept@cox.net
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *************************************************************************/

package integrity;

import java.util.Hashtable;
import java.io.Serializable;

public class ClientServerContainer extends Hashtable implements Serializable{
  private TagReader tReader;
  private TagReader fileSet;
  private long serverCheckSum;

  public TagReader getTReader() {
    return this.tReader;
  }

  public void setTReader(TagReader tReader) {
    this.tReader = tReader;
  }

  public long getServerCheckSum() {
    return this.serverCheckSum;
  }

  public void setServerCheckSum(long serverCheckSum) {
    this.serverCheckSum = serverCheckSum;
  }

  public TagReader getFileSet() {
    return this.fileSet;
  }

  public void setFileSet(TagReader fileSet) {
    this.fileSet = fileSet;
  }

}
