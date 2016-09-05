package integrity;

public class FileCheckSum {
  private String key;
  private String fileLocation;
  private String fileName;
  private long checksum;
  public FileCheckSum(){}

  public FileCheckSum(String key, long cs, String fLoc, String fName){
    this.key = key;
    this.checksum = cs;
    this.fileLocation = fLoc;
    this.fileName = fName;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getChecksum() {
    return this.checksum;
  }

  public void setChecksum(long checksum) {
    this.checksum = checksum;
  }

  public String getFileLocation() {
    return this.fileLocation;
  }

  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}
