package org.rcosjava.hardware.disk.msdos;

import org.rcosjava.hardware.disk.SimpleDisk;

/**
 * A simulated MSDOS floppy disk.
 * <P>
 * @author Andrew Newman.
 * @created 10th of August 1999
 * @see org.rcosjava.hardware.disk.SimpleDisk
 * @see org.rcosjava.hardware.disk.Disk
 * @version 1.00 $Date$
 */
public class MSDOSFloppy extends SimpleDisk
{
  /**
   * Create a floppy with the given number of tracks, sectors and bytes per
   * sector. The disk latency is 100ms, the start up time is 1000ms and the RPM
   * is 300.
   *
   * @param newTracks Description of Parameter
   * @param newSectorsPerTrack Description of Parameter
   * @param bytesPerSector Description of Parameter
   */
  public MSDOSFloppy(int newTracks, int newSectorsPerTrack,
      int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, 100, 1000, 300);
  }
}
