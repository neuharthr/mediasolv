package integrity;

import java.util.*;

public class LocalFileMasterCheckSum extends Hashtable{
  private boolean validCheckSum = false;
  private long masterCheckSum = -1;

  public void add(String key, long cs, String fLoc, String fName){
    this.put(key, new FileCheckSum(key,cs,fLoc,fName));
  }

  public long getMasterCheckSum() {
    return this.masterCheckSum;
  }

  public void setMasterCheckSum(long masterCheckSum) {
    this.masterCheckSum = masterCheckSum;
  }

  public long getFileCheckSum(String key){
    return ( (FileCheckSum) this.get(key) ).getChecksum();
  }

  public boolean isValid() {
    return this.validCheckSum;
  }

  public void validCheckSum() {
    this.validCheckSum = true;
  }
}
