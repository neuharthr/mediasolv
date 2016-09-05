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

package integrity.util;

import java.util.*;
import java.io.*;

public class RecursiveDirectory extends Vector{
  private StringBuffer buffer = new StringBuffer();
  private int index;
  private long directorySum = 0l;
  public RecursiveDirectory(){}
  public RecursiveDirectory(String root, String dirName, Hashtable extensions, Hashtable exclusions, Hashtable excludeExtensions) throws Exception{
    this.recurse(root,dirName,extensions,exclusions,excludeExtensions);
  }

  public void recurse(String root, String dirName, Hashtable extensions, Hashtable exclusions, Hashtable excludeExtensions) throws Exception{
    String FILE_SEP = System.getProperties().getProperty("file.separator");
    File inFile = new File(root + FILE_SEP + dirName);
    String baseDir[] = inFile.list();
    File innerFile = null;
    RecursiveDirectory recFile = new RecursiveDirectory();
    StringBuffer innerStructure = new StringBuffer();
    StringBuffer innerCheckSum = new StringBuffer();
    int fileid = 0;
    boolean addFile = false;
    for(int base = 0;base < baseDir.length;base++){
      innerFile = new File(root + FILE_SEP + dirName + FILE_SEP + baseDir[base]);
      if(innerFile.isDirectory()){
        recFile = new RecursiveDirectory(root, dirName + FILE_SEP + baseDir[base],extensions,exclusions, excludeExtensions);
        buffer.append(recFile.getFileStructure());
      }else{
        addFile = false;
        index = baseDir[base].lastIndexOf(".");
        if(index > 0) {
//------ CHECK FOR VALID EXTENSTIONS -----
          if(extensions.size() == 0){
            addFile = true;
          }else if(extensions.containsKey(baseDir[base].substring(index + 1))){
            addFile = true;
          }
//------ CHECK FOR INVAD EXTENSTIONS -----
          if(addFile && excludeExtensions.size() > 0
             && excludeExtensions.containsKey(baseDir[base].substring(index + 1))){
            addFile = false;
          }
//------ CHECK FOR EXCLUDE FILE -----
          if(addFile && exclusions.size() > 0
             && exclusions.containsKey(baseDir[base])){
            addFile = false;
          }
        }else{
//------ CHECK FOR EXCLUDE FILE -----
          if(exclusions.size() > 0
             && !exclusions.containsKey(baseDir[base])){
            addFile = true;
          }else{
            addFile = true;
          }
        } //end if-else index > 0

        if(addFile){
            this.addElement("." + dirName + FILE_SEP + baseDir[base]);
            innerStructure.append("\t\t<file_" + fileid + ">" + baseDir[base] + "</file_" + fileid + ">" + (char) 13 + (char) 10);
            fileid++;
        }//end if-addFile
      }//end if-else isDirectory
    }//end for-loop
    buffer.insert(0,"\t</DIR" + dirName.replaceAll("\\" + FILE_SEP,":") + ">" + (char)13 + (char)10);
    buffer.insert(0,innerStructure);
    buffer.insert(0,"\t<DIR" + dirName.replaceAll("\\" + FILE_SEP,":") + ">" + (char)13 + (char)10);
  }

  public String getFileStructure(){
    return buffer.toString();
  }

}
