//*************************************************************************//
// FILENAME : Disk.java
// PACKAGE  : Hardware.Disk
// PURPOSE  : A simple interface for simulation of a disk.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/08/99 Created
//*************************************************************************//

package Hardware.Disk;

public interface Disk
{
  public int getTracks();
  public int getSectorsPerTrack();
  public int getBytesPerSector();
  //All measurements are in ms unless noted.
  public float getDiskLatency();
  public int getStartUpTime();
  public int getRPM();
  //run time calculations
  public int calcSectorsToRead(int iNoBytesToRead);
  public int calcTracksToRead(int iNoBytesToRead);
  public float calcSeekTime(int iNoTracks);
  public float calcAverageRotationalDelay();
  public float calcTransferTime(int iNoBytes);
  public float calcSuccessiveSectorRead(int iNoBytesToRead);
  public float calcSuccessiveTrackRead(int iNoBytesToRead);
  // Time taken to access a complete file (made of a number of bytes)
  public float calcSequentialAccessTime(int iNoBytesToRead);
  public float calcRandomAccessTime(int iNoBytesToRead);
  // Actual sector reading/writing
  public byte[] readSector(int iTrack, int iSector);
  public void writeSector(int iTrack, int iSector, byte[] bData);
}
