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
   * The number of tracks in the disk.
   */
  private int tracks;

  /**
   * The number of sectors per track.
   */
  private int sectorsPerTrack;

  /**
   * The number of bytes per sector.
   */
  private int bytesPerSector;

  /**
   * The time taken (in ms) to traverse a cyclinder.
   */
  private float diskLatency;

  /**
   * The time taken (in ms) to get the drive ready in a read process.
   */
  private int startupTime;

  /**
   * The speed at which the media travels underneath the read head at.
   * Rotations per minute.
   */
  private int rpm;

  /**
   * The raw bytes being stored on the disk.
   */
  private byte[] disk;

  /**
   * Create a new simple implementation of a disk.
   *
   * @param newTracks the number of tracks on the disk.
   * @param newSectorsPerTrack the number of sectors per track.
   * @param newBytesPerSector the number of bytes per sector.
   * @param newDiskLatency the latency (time in ms to traverse a cylinder) of
   *   the disk.
   * @param newStartupTime the start up (in ms) to start the disk for a read.
   * @param newRPM the rotations per minutes of the disk.
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
   * Returns the number of tracks on the disk.
   *
   * @return the number of tracks on the disk.
   */
  public int getTracks()
  {
    return tracks;
  }

  /**
   * Returns the number of sectors per track.
   *
   * @return the number of sectors per track.
   */
  public int getSectorsPerTrack()
  {
    return sectorsPerTrack;
  }

  /**
   * Returns the number of bytes per sector.
   *
   * @return the number of bytes per sector.
   */
  public int getBytesPerSector()
  {
    return bytesPerSector;
  }

  /**
   * Returns the latency (time taken in ms to traverse a cylinder).
   *
   * @return the latency (time taken in ms to traverse a cylinder).
   */
  public float getDiskLatency()
  {
    return diskLatency;
  }

  /**
   * Return the time it takes (in ms) for a disk to get ready before reading.
   *
   * @return the time it takes (in ms) for a disk to get ready before reading.
   */
  public int getStartUpTime()
  {
    return startupTime;
  }

  /**
   * Returns the rotations per minute that the drive spins at.
   *
   * @return the rotations per minute that the drive spins at.
   */
  public int getRPM()
  {
    return rpm;
  }

  /**
   * Calculates the whole number of sectors to read.
   *
   * @param bytesToRead the total number of bytes that are to be used to
   *      calculate the number of sectors.
   * @return total number of sectors.
   */
  public int calcSectorsToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead / getBytesPerSector()));
  }

  /**
   * Calculates the whole number of tracks to read.
   *
   * @param bytesToRead the total number of bytes that are to be used to
   *      calculate the number of tracks.
   * @return the total number of tracks.
   */
  public int calcTracksToRead(int noBytesToRead)
  {
    return ((int) Math.ceil(noBytesToRead /
        (getSectorsPerTrack() * getBytesPerSector())));
  }

  /**
   * Linear approximation of seek time.
   *
   * @param tracks the number of tracks to read.
   * @return the number of ms taken for seeking the given number of tracks.
   */
  public float calcSeekTime(int noTracks)
  {
    return (getDiskLatency() * noTracks + getStartUpTime());
  }

  /**
   * A simple average based rotational dely calculation.
   *
   * @return the number of ms taken for reaching the correct position.
   */
  public float calcAverageRotationalDelay()
  {
    return (60000 / rpm) / 2;
  }

  /**
   * Calculate the transfer speed that the data is taken from the disk based
   * on the the rotation speed of the disk.
   *
   * @param bytes the number of bytes to read.
   * @return the total number of ms taken to read the bytes.
   */
  public float calcTransferTime(int noBytes)
  {
    return (noBytes /
        ((rpm / (60 * 1000)) * (getSectorsPerTrack() * getBytesPerSector())));
  }

  /**
   * Calculates the time taken to read a number of bytes from sector(s) assuming
   * that are stored contiguously. This only takes into consideration the raw
   * time of reading sectors (not track movement).
   *
   * @param bytesToRead the number of bytes to read from the sector(s).
   * @return the number of ms taken to read the given number of bytes.
   */
  public float calcSuccessiveSectorRead(int noBytesToRead)
  {
    return (calcSeekTime(calcTracksToRead(noBytesToRead)) +
        calcAverageRotationalDelay() +
        calcTransferTime(getBytesPerSector()));
  }

  /**
    * Calculates the time taken to read a number of bytes from track(s) assuming
    * that they are stored contiguously. This does not include the time taken to
    * read the sector data.  Calculated by adding the
    * calcAverageRotationalDelay() and calcTransferTime(noBytesToRead).
    *
    * @param bytesToRead the number of bytes to read.
    * @return the number of ms taken to read the bytes.
   */
  public float calcSuccessiveTrackRead(int noBytesToRead)
  {
    return (calcAverageRotationalDelay() + calcTransferTime(noBytesToRead));
  }

  /**
   * Assumes the data is stored as compactly as possible on the disk and
   * there's no seek time between tracks.
   *
   * @param bytesToRead the number of bytes to read.
   * @return the number of ms taken to read the bytes.
   */
  public float calcSequentialAccessTime(int noBytesToRead)
  {
    return (calcSuccessiveTrackRead(noBytesToRead) *
        calcTracksToRead(noBytesToRead) +
        calcSeekTime(calcTracksToRead(noBytesToRead)));
  }

  /**
   * Assume the data is stored randomly on the disk.
   *
   * @param bytesToRead the number of bytes to read.
   * @return the number of ms taken to read the bytes.
   */
  public float calcRandomAccessTime(int noBytesToRead)
  {
    return (calcSuccessiveSectorRead(noBytesToRead) *
        calcSectorsToRead(noBytesToRead));
  }

  /**
   * Physical access to the media of the disk. Read block of data
   * (track/sector).
   *
   * @param track the disk section to read from.
   * @param sector the disk section to read from.
   * @return the value stored on the disk.
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
   * Physical access to the media of the disk. Read block of data
   * (index).
   *
   * @param the index (absolute number of sectors) to read from.
   * @return the value stored on the disk.
   */
  public byte[] readSector(int indexOffset)
  {
    byte[] returnData = new byte[getBytesPerSector()];
    int offset = getBytesPerSector() * indexOffset;
    for (int count = 0; count < getBytesPerSector(); count++)
    {
      returnData[count] = disk[offset + count];
    }
    return returnData;
  }

  /**
   * Physical access to the media of the disk. Write a block of data to the
   * disk.
   *
   * @param track the disk section to write to.
   * @param sector the disk section to write to.
   * @param data the data to write to the disk.
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
   * Physical access to the media of the disk. Write block of data
   * (index).
   *
   * @param the index (absolute number of sectors) to read from.
   * @param data the data to write to the disk.
   */
  public void writeSector(int indexOffset, byte[] data)
  {
    int offset = getBytesPerSector() * indexOffset;

    for (int count = 0; count < getBytesPerSector(); count++)
    {
      disk[offset + count] = data[count];
    }
  }

  /**
   * Calculates the location of the data given a track and sector.
   *
   * @param track the track where the data is located.
   * @param sector the sector where the data is located.
   * @return the offset into the byte array.
   */
  private int calcOffset(int track, int sector)
  {
    return (track * getSectorsPerTrack() * getBytesPerSector()) +
        (sector * getBytesPerSector());
  }
}