package org.rcosjava.software.animator.filesystem;

import java.awt.Component;
import javax.swing.ImageIcon;

import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;

import org.rcosjava.software.filesystem.FileSystemManager;
import org.rcosjava.software.filesystem.msdos.MSDOSFile;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;
import org.rcosjava.software.util.HorarioInvalidoException;

/**
 * Sends and receives messages for the graphical representation of the file
 * system.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 18/09/2002 Originally created by Danielly. </DD>
 * <DD> 24/07/2003 Finally imported as a panel into new RCOSjava AN. </DD> </DT>
 * <P>
 * @author Danielly Karine da Silva Cruz
 * @author Andrew Newman
 * @created 18 September 2002
 * @version 1.00 $Date$
 */
public class FileSystemAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "FileSystemAnimator";

  /**
   * Description of the Field
   */
  private RCOSPanel panel;

  /**
   * Constructor for the FileSystemAnimator object
   *
   * @param postOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param cpuImages Description of Parameter
   */
  public FileSystemAnimator(AnimatorOffice postOffice,
      FileSystemManager theFileSystemManager)
  {
    super(MESSENGING_ID, postOffice);

    panel = new FileSystemPanel(theFileSystemManager);
    panel.repaint();
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }
}
