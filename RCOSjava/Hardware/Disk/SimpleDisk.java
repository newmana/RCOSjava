//*************************************************************************//
// FILENAME : SimpleDisk.java
// PACKAGE  : Hardware.Disk
// PURPOSE  : General simulation of a disk (floppy or hard drive).  Extend
//            this to implement specific implementations.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 10/08/99 Created
//*************************************************************************//

package Hardware.Disk;

public class SimpleDisk implements Disk
{
	private int iTracks, iSectorsPerTrack, iBytesPerSector;
	private float fDiskLatency;
	private int iStartupTime, iRPM;
	private byte[] bDisk;
	
	public SimpleDisk(int iNewTracks, int iNewSectorsPerTrack, 
		int iNewBytesPerSector, float fNewDiskLatency, int iNewStartupTime, 
		int iNewRPM)
	{
		iTracks = iNewTracks;
		iSectorsPerTrack = iNewSectorsPerTrack;
		iBytesPerSector = iNewBytesPerSector;
		fDiskLatency = fNewDiskLatency;
		iStartupTime = iStartupTime;
		iRPM = iNewRPM;
		bDisk = new byte[iNewTracks * iNewSectorsPerTrack * iBytesPerSector];
	}
	
	public int getTracks()
	{
		return iTracks;
	}

	public int getSectorsPerTrack()
	{
		return iSectorsPerTrack;
	}

	public int getBytesPerSector()
	{
		return iBytesPerSector;
	}
		
	public float getDiskLatency()
	{
		return fDiskLatency;
	}
	
	public int getStartUpTime()
	{
		return iStartupTime;
	}
	
	public int getRPM()
	{
		return iRPM;
	}
	
	public int calcSectorsToRead(int iNoBytesToRead)
	{
		return ((int) Math.ceil(iNoBytesToRead / getBytesPerSector()));
	}
	
	public int calcTracksToRead(int iNoBytesToRead)
	{
		return ((int) Math.ceil(iNoBytesToRead / 
			(getSectorsPerTrack() * getBytesPerSector())));
	}

	//Linear approximation of seek time
	public float calcSeekTime(int iNoTracks)
	{
		return (getDiskLatency() * iNoTracks + getStartUpTime());
	}
	
	public float calcAverageRotationalDelay()
	{
	  return (60000 / iRPM) / 2;  
	}

  //Time it takes in ms to transfer iNoBytes
	public float calcTransferTime(int iNoBytes)
	{
		return(iNoBytes / 
	    ((iRPM / (60 * 1000)) * (getSectorsPerTrack() * getBytesPerSector())));
	}

	public float calcSuccessiveSectorRead(int iNoBytesToRead)
	{
		return (calcSeekTime(calcTracksToRead(iNoBytesToRead)) + 
						calcAverageRotationalDelay() + 
						calcTransferTime(getBytesPerSector()));
	}
	
	public float calcSuccessiveTrackRead(int iNoBytesToRead)
	{
		return (calcAverageRotationalDelay() + calcTransferTime(iNoBytesToRead));
	}
	
	// Assumes the data is stored as compactly as possible on the
	// disk and there's no seek time between tracks.
	public float calcSequentialAccessTime(int iNoBytesToRead)
	{
	  return(calcSuccessiveTrackRead(iNoBytesToRead) * 
					 calcTracksToRead(iNoBytesToRead) + 
					 calcSeekTime(calcTracksToRead(iNoBytesToRead)));
	}
	
  // Assume the data is stored randomly on the disk.
	public float calcRandomAccessTime(int iNoBytesToRead)
	{
		return(calcSuccessiveSectorRead(iNoBytesToRead) * 
					 calcSectorsToRead(iNoBytesToRead));
	}
	
  private int calcOffset(int iTrack, int iSector)
	{
		return (iTrack * getSectorsPerTrack() * getBytesPerSector()) +
			(iSector * getBytesPerSector());
	}
	
	public byte[] readSector(int iTrack, int iSector)
	{
		int iOffset = calcOffset(iTrack, iSector);
		byte[] bReturnData = new byte[getBytesPerSector()];
		for (int iCount = 0; iCount < getBytesPerSector(); iCount++)
		{
			bReturnData[iCount] = bDisk[iOffset + iCount];
		}
		return bReturnData;
	}
	
  public void writeSector(int iTrack, int iSector, byte[] bData)
	{
		int iOffset = calcOffset(iTrack, iSector);
		for (int iCount = 0; iCount < getBytesPerSector(); iCount++)
		{
			bDisk[iOffset + iCount] = bData[iCount];
		}
	}
}
