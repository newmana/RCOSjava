package org.rcosjava.hardware.disk.cpm14;
import org.rcosjava.hardware.disk.SimpleDisk;

/**
 * A simulated CPM14 hard disk drive.
 * <P>
 * @author Andrew Newman.
 * @created 10th of August 1999
 * @see org.rcosjava.hardware.disk.SimpleDisk
 * @see org.rcosjava.hardware.disk.Disk
 * @version 1.00 $Date$
 */
public class CPM14HardDrive extends SimpleDisk
{
  /**
   * Create a floppy with the given number of tracks, sectors and bytes per
   * sector. The disk latency is 0.3ms, the start up time is 20ms and the RPM is
   * 3600.
   *
   * @param newTracks Description of Parameter
   * @param newSectorsPerTrack Description of Parameter
   * @param bytesPerSector Description of Parameter
   */
  public CPM14HardDrive(int newTracks, int newSectorsPerTrack,
      int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, (float) 0.3, 20, 3600);
  }
}
