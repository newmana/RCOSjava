package net.sourceforge.rcosjava.hardware.disk.cpm14;

import net.sourceforge.rcosjava.hardware.disk.SimpleDisk;

/**
 * A simulated CPM14 hard disk drive.
 * <P>
 * @see net.sourceforge.rcosjava.hardware.disk.SimpleDisk
 * @see net.sourceforge.rcosjava.hardware.disk.Disk
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of August 1999
 */
public class CPM14HardDrive extends SimpleDisk
{
  /**
   * Create a floppy with the given number of tracks, sectors and bytes per
   * sector.  The disk latency is 0.3ms, the start up time is 20ms and the RPM
   * is 3600.
   */
  public CPM14HardDrive(int newTracks, int newSectorsPerTrack,
    int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, (float) 0.3, 20, 3600);
  }
}