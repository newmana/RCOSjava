package net.sourceforge.rcosjava.hardware.disk;

/**
 * General simulation of a disk (floppy or hard drive).  Extend this to
 * implement specific implementations.
 * <P>
 * @see net.sourceforge.rcosjava.hardware.disk.Disk
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of August 1999
 */
public class SimpleDisk implements Disk
{
  private int tracks;
  private int sectorsPerTrack;
  private int bytesPerSector;
  private float diskLatency;
  private int startupTime, rpm;
  private byte[] disk;

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

  public int getTracks()
  {
    return tracks;
  }

  public int getSectorsPerTrack()
  {
    return sectorsPerTrack;
  }

  public int getBytesPerSector()
  {
    return bytesPerSector;
  }

  public float getDiskLatency()
  {
    return diskLatency;
  }

  public int getStartUpTime()
  {
    return startupTime;
  }

  public int getRPM()
  {
    return rpm;
  }

  public int calcSectorsToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead / getBytesPerSector()));
  }

  public int calcTracksToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead /
      (getSectorsPerTrack() * getBytesPerSector())));
  }

  //Linear approximation of seek time
  public float calcSeekTime(int noTracks)
  {
    return (getDiskLatency() * noTracks + getStartUpTime());
  }

  public float calcAverageRotationalDelay()
  {
    return (60000 / rpm) / 2;
  }

  //Time it takes in ms to transfer iNoBytes
  public float calcTransferTime(int noBytes)
  {
    return(noBytes /
      ((rpm / (60 * 1000)) * (getSectorsPerTrack() * getBytesPerSector())));
  }

  public float calcSuccessiveSectorRead(int noBytesToRead)
  {
    return (calcSeekTime(calcTracksToRead(noBytesToRead)) +
      calcAverageRotationalDelay() +
      calcTransferTime(getBytesPerSector()));
  }

  public float calcSuccessiveTrackRead(int noBytesToRead)
  {
    return (calcAverageRotationalDelay() + calcTransferTime(noBytesToRead));
  }

  // Assumes the data is stored as compactly as possible on the
  // disk and there's no seek time between tracks.
  public float calcSequentialAccessTime(int noBytesToRead)
  {
    return(calcSuccessiveTrackRead(noBytesToRead) *
      calcTracksToRead(noBytesToRead) +
      calcSeekTime(calcTracksToRead(noBytesToRead)));
  }

  // Assume the data is stored randomly on the disk.
  public float calcRandomAccessTime(int noBytesToRead)
  {
    return(calcSuccessiveSectorRead(noBytesToRead) *
      calcSectorsToRead(noBytesToRead));
  }

  private int calcOffset(int track, int sector)
  {
    return (track * getSectorsPerTrack() * getBytesPerSector()) +
    (sector * getBytesPerSector());
  }

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

  public void writeSector(int track, int sector, byte[] data)
  {
    int offset = calcOffset(track, sector);
    for (int count = 0; count < getBytesPerSector(); count++)
    {
      disk[offset + count] = data[count];
    }
  }
}
