package Hardware.Disk.CPM14;

import Hardware.Disk.SimpleDisk;

/**
 * A sumulated CPM14 floppy disk.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of August 1999
 * @see Hardware.Disk.SimpleDisk
 * @see Hardware.Disk.Disk
 */
public class CPM14Floppy extends SimpleDisk
{
  /**
   * Create a floppy with the given number of tracks, sectors and bytes per
   * sector.  The disk latency is 100ms, the start up time is 1000ms and the RPM
   * is 300.
   */
  public CPM14Floppy(int newTracks, int newSectorsPerTrack,
    int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, 100, 1000, 300);
  }
}