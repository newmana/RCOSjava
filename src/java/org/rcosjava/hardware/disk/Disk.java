package org.rcosjava.hardware.disk;

/**
 * A Disk is the physical device which reads and writes data to a permanent
 * storage medium. The actual implementation of the properties of the disk.
 * <P>
 * @author Andrew Newman.
 * @created 10th of August 1999
 * @version 1.00 $Date$
 */
public interface Disk
{
  /**
   * Returns the total number of tracks that the disk stores.
   *
   * @return The Tracks value
   */
  public int getTracks();

  /**
   * Returns the total number of sectos per track (assumed constant).
   *
   * @return The SectorsPerTrack value
   */
  public int getSectorsPerTrack();

  /**
   * Returns the number of bytes stored on each sectory.
   *
   * @return The BytesPerSector value
   */
  public int getBytesPerSector();

  /**
   * The time taken (in ms) to traverse a cyclinder.
   *
   * @return The DiskLatency value
   */
  public float getDiskLatency();

  /**
   * The time taken (in ms) to get the drive ready in a read process.
   *
   * @return The StartUpTime value
   */
  public int getStartUpTime();

  /**
   * The speed at which the media travels underneath the read head at.
   *
   * @return The RPM value
   */
  public int getRPM();

  /**
   * Calculates the whole number of sectors to read.
   *
   * @param bytesToRead the total number of bytes that are to be used to
   *      calculate the number of sectors.
   * @return Description of the Returned Value
   */
  public int calcSectorsToRead(int bytesToRead);

  /**
   * Calculates the whole number of tracks to read.
   *
   * @param bytesToRead the total number of bytes that are to be used to
   *      calculate the number of tracks.
   * @return Description of the Returned Value
   */
  public int calcTracksToRead(int bytesToRead);

  /**
   * Calculates the seek time based on the physical properties of the disk.
   *
   * @param tracks the number of tracks to read.
   * @return Description of the Returned Value
   */
  public float calcSeekTime(int tracks);

  /**
   * Calculate the rotational delay of the disk or rather the time it takes for
   * the correct position of the disk to travel under the read head.
   *
   * @return Description of the Returned Value
   */
  public float calcAverageRotationalDelay();

  /**
   * Calculate the transfer speed that the data is taken from the disk. This is
   * usually a function of the rotation speed of the disk.
   *
   * @param bytes Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcTransferTime(int bytes);

  /**
   * Calculates the time taken to read a number of bytes from sector(s) assuming
   * that are stored contiguously. This only takes into consideration the raw
   * time of reading sectors (not track movement).
   *
   * @param bytesToRead the number of bytes to read from the sector(s).
   * @return Description of the Returned Value
   */
  public float calcSuccessiveSectorRead(int bytesToRead);

  /**
   * Calculates the time taken to read a number of bytes from track(s) assumin
   * that they are stored contiguously. This does not include the time taken to
   * read the sector data.
   *
   * @param bytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSuccessiveTrackRead(int bytesToRead);

  /**
   * Calculates the total time taken assuming that the data is contiguously
   * stored on the disk.
   *
   * @param bytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcSequentialAccessTime(int bytesToRead);

  /**
   * Calculates the total time taken assuming the data is randomly stored on the
   * disk media.
   *
   * @param bytesToRead Description of Parameter
   * @return Description of the Returned Value
   */
  public float calcRandomAccessTime(int bytesToRead);

  /**
   * Physical access to the media of the disk. Read block of data
   * (track/sector).
   *
   * @param track the disk section to read from.
   * @param sector the disk section to read from.
   * @return the value stored on the disk.
   */
  public byte[] readSector(int track, int sector);

  /**
   * Physical access to the media of the disk. Write a block of data to the
   * disk.
   *
   * @param track the disk section to write to.
   * @param sector the disk section to write to.
   * @param data the data to write to the disk.
   */
  public void writeSector(int track, int sector, byte[] data);
}
