package org.rcosjava.software.filesystem;

import org.rcosjava.software.filesystem.msdos.MSDOSFile;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;
import org.rcosjava.software.filesystem.msdos.MSDOSFileSystem;

/**
 * Title:        RCOS
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      UFPE
 * @author Danielly Cruz
 * @version 1.0
 */

public interface FileSystemInterface{

  public void mount(String mvNewMountPoint, String mvNewDeviceName);

  public FileSystemData allocate(int iRequestID, String sFileName);

  public FileSystemData eof(int iRequestID, int iFSFileNo);

  public FileSystemData delete(int iRequestID, int iFSFileNo);

  public FileSystemData create(int iRequestID, int iFSFileNo);

  public FileSystemData close(int iRequestID, int iFSFileNo);

  public FileSystemData open(int iRequestID, int iFSFileNo);

  public FileSystemData read(int iRequestID,
                              MSDOSFile msdosFile,
                              String path) throws MSDOSFATException, MSDOSDirectoryException;

  public FileSystemData write(int iRequestID,
                              MSDOSFile msdosFile,
                              String data,
                              String path) throws MSDOSFATException, MSDOSDirectoryException;

  public FileSystemData delete(int iRequestID,
                              MSDOSFile msdosFile,
                              String path) throws MSDOSFATException, MSDOSDirectoryException;

  public void recordeSystemFile();

  public void requestSystemFile();

  public int getDirectoryPosition(String mvFilename);

  public int getFreeEntry(int mvDeviceNumber);

  public boolean diskFull(int mvDeviceNumber);

  public int getFreeBlock(int mvDeviceNumber);

  public int getNextDirectoryEntry(int mvDirEntry, int mvDeviceNumber);

  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber);

  public String getMountPoint(String mvFilename);
}
