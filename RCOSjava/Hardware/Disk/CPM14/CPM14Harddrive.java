package Hardware.Disk.CPM14;

import Hardware.Disk.SimpleDisk;

/**
 * A sumulated CPM14 hard disk drive.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of August 1999
 * @see Hardware.Disk.SimpleDisk
 * @see Hardware.Disk.Disk
 */
public class CPM14Harddrive extends SimpleDisk
{
  /**
   * Create a floppy with the given number of tracks, sectors and bytes per
   * sector.  The disk latency is 0.3ms, the start up time is 20ms and the RPM
   * is 3600.
   */
  public CPM14Harddrive(int newTracks, int newSectorsPerTrack,
    int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, (float) 0.3, 20, 3600);
  }
}