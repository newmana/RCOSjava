//*************************************************************************//
// FILENAME : CPM14Floppy.java
// PACKAGE  : Hardware.Disk.CPM14
// PURPOSE  : A simulated CPM14 floppy disk.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/08/99 Created
//
//*************************************************************************//

package Hardware.Disk.CPM14;

import Hardware.Disk.SimpleDisk;

public class CPM14Floppy extends SimpleDisk
{
  public CPM14Floppy(int newTracks, int newSectorsPerTrack,
    int bytesPerSector)
  {
    super(newTracks, newSectorsPerTrack, bytesPerSector, 100, 1000, 300);
  }
}