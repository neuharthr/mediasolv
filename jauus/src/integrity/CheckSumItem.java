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

public class CheckSumItem {
  private String file;
  private long checksum;

  public String getFile() {
    return this.file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public long getChecksum() {
    return this.checksum;
  }

  public void setChecksum(long checksum) {
    this.checksum = checksum;
  }
}
