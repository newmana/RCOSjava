//*************************************************************************//
// FILENAME : CPM14HardDrive.java
// PACKAGE  : Hardware.Disk.CPM14
// PURPOSE  : A simulated CPM14 hard drive disk.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 10/08/99 Created
//            
//*************************************************************************//

package Hardware.Disk.CPM14;

import Hardware.Disk.SimpleDisk;

public class CPM14Harddrive extends SimpleDisk
{
	public CPM14Harddrive(int iNewTracks, int iNewSectorsPerTrack, 
		int iBytesPerSector)
	{
		super(iNewTracks, iNewSectorsPerTrack, iBytesPerSector, (float) 0.3, 20, 3600);
	}
}