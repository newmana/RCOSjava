package org.rcosjava.hardware.disk;

/**
 * General simulation of a disk (floppy or hard drive). Extend this to implement
 * specific implementations.
 * <P>
 * @author Andrew Newman.
 * @created 10th of August 1999
 * @see org.rcosjava.hardware.disk.Disk
 * @version 1.00 $Date$
 */
public class SimpleDisk implements Disk
{
  /**
   * Description of the Field
   */
  private int tracks;
  /**
   * Description of the Field
   */
  private int sectorsPerTrack;
  /**
   * Description of the Field
   */
  private int bytesPerSector;
  /**
   * Description of the Field
   */
  private float diskLatency;
  /**
   * Description of the Field
   */
  private int startupTime, rpm;
  /**
   * Description of the Field
   */
  private byte[] disk;

  /**
   * Constructor for the SimpleDisk object
   *
   * @param newTracks Description of Parameter
   * @param newSectorsPerTrack Description of Parameter
   * @param newBytesPerSector Description of Parameter
   * @param newDiskLatency Description of Parameter
   * @param newStartupTime Description of Parameter
   * @param newRPM Description of Parameter
   */
  public SimpleDisk(int newTracks, int newSectorsPerTrack,
      int newBytesPerSector, float newDiskLatency, int newStartupTime,
      int newRPM)
  {
    tracks = newTracks;
    sectorsPerTrack = newSectorsPerTrack;
    bytesPerSector = newBytesPerSector;
    diskLatency = newDiskLatency;
    startupTime = newStartupTime;
    rpm = newRPM;
    disk = new byte[newTracks * newSectorsPerTrack * bytesPerSector];
  }

  /**
   * Gets the Tracks attribute of the SimpleDisk object
   *
   * @return The Tracks value
   */
  public int getTracks()
  {
    return tracks;
  }

  /**
   * Gets the SectorsPerTrack attribute of the SimpleDisk object
   *
   * @return The SectorsPerTrack value
   */
  public int getSectorsPerTrack()
  {
    return sectorsPerTrack;
  }

  /**
   * Gets the BytesPerSector attribute of the SimpleDisk object
   *
   * @return The BytesPerSector value
   */
  public int getBytesPerSector()
  {
    return bytesPerSector;
  }

  /**
   * Gets the DiskLatency attribute of the SimpleDisk object
   *
   * @return The DiskLatency value
   */
  public float getDiskLatency()
  {
    return diskLatency;
  }

  /**
   * Gets the StartUpTime attribute of the SimpleDisk object
   *
   * @return The StartUpTime value
   */
  public int getStartUpTime()
  {
    return startupTime;
  }

  /**
   * Gets the RPM attribute of the SimpleDisk object
   *
   * @return The RPM value
   */
  public int getRPM()
  {
    return rpm;
  }

  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public int calcSectorsToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead / getBytesPerSector()));
  }

  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public int calcTracksToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead /
        (getSectorsPerTrack() * getBytesPerSector())));
  }

  //Linear approximation of seek time
  /**
   * Description of the Method
   *
   * @param noTracks Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSeekTime(int noTracks)
  {
    return (getDiskLatency() * noTracks + getStartUpTime());
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public float calcAverageRotationalDelay()
  {
    return (60000 / rpm) / 2;
  }

  //Time it takes in ms to transfer iNoBytes
  /**
   * Description of the Method
   *
   * @param noBytes Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcTransferTime(int noBytes)
  {
    return (noBytes /
        ((rpm / (60 * 1000)) * (getSectorsPerTrack() * getBytesPerSector())));
  }

  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSuccessiveSectorRead(int noBytesToRead)
  {
    return (calcSeekTime(calcTracksToRead(noBytesToRead)) +
        calcAverageRotationalDelay() +
        calcTransferTime(getBytesPerSector()));
  }

  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSuccessiveTrackRead(int noBytesToRead)
  {
    return (calcAverageRotationalDelay() + calcTransferTime(noBytesToRead));
  }

  // Assumes the data is stored as compactly as possible on the
  // disk and there's no seek time between tracks.
  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSequentialAccessTime(int noBytesToRead)
  {
    return (calcSuccessiveTrackRead(noBytesToRead) *
        calcTracksToRead(noBytesToRead) +
        calcSeekTime(calcTracksToRead(noBytesToRead)));
  }

  // Assume the data is stored randomly on the disk.
  /**
   * Description of the Method
   *
   * @param noBytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcRandomAccessTime(int noBytesToRead)
  {
    return (calcSuccessiveSectorRead(noBytesToRead) *
        calcSectorsToRead(noBytesToRead));
  }

  /**
   * Description of the Method
   *
   * @param track Description of Parameter
   * @param sector Description of Parameter
   * @return Description of the Returned Value
   */
  public byte[] readSector(int track, int sector)
  {
    int offset = calcOffset(track, sector);
    byte[] returnData = new byte[getBytesPerSector()];

    for (int count = 0; count < getBytesPerSector(); count++)
    {
      returnData[count] = disk[offset + count];
    }
    return returnData;
  }

  /**
   * Description of the Method
   *
   * @param track Description of Parameter
   * @param sector Description of Parameter
   * @param data Description of Parameter
   */
  public void writeSector(int track, int sector, byte[] data)
  {
    int offset = calcOffset(track, sector);

    for (int count = 0; count < getBytesPerSector(); count++)
    {
      disk[offset + count] = data[count];
    }
  }

  /**
   * Description of the Method
   *
   * @param track Description of Parameter
   * @param sector Description of Parameter
   * @return Description of the Returned Value
   */
  private int calcOffset(int track, int sector)
  {
    return (track * getSectorsPerTrack() * getBytesPerSector()) +
        (sector * getBytesPerSector());
  }
}
