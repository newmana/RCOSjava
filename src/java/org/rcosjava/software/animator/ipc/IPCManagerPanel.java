package org.rcosjava.software.animator.ipc;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSQueue;
import org.rcosjava.software.animator.support.RCOSBox;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Based on the commands sent and received to the IPC and displays graphically
 * what is happening to shared memory and semaphores.
 * <P>
 * @author Andrew Newman
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class IPCManagerPanel extends RCOSPanel
{
  /**
   * Default display when there is no semaphore or shared memory segments to
   * display.
   */
  private static String NONE = " None      ";

  /**
   * Default display when there are semaphores or shared memory segments to
   * display.
   */
  private static String SOME = " Select";

  /**
   * Images used to display UI.
   */
  private Image myImages[];

  /**
   * Semaphore queue to display.
   */
  private RCOSQueue semQueue;

  /**
   * Shared memory queue to display.
   */
  private RCOSQueue shmQueue;

  /**
   * Animator that I receive calls from and makes calls back when a user event
   * occurs.
   */
  private IPCManagerAnimator myAnimator;

  /**
   * Component used for UI display.
   */
  private Component myComponent;

  /**
   * Window dimensions.
   */
  private int windowWidth, windowHeight;

  /**
   * Map of current semaphores.
   */
  private HashMap semaphoreMap;

  /**
   * Map of current shared memory segments.
   */
   private HashMap sharedMemoryMap;

  /**
   * Description of the Field
   */
  private JComboBox shmOption, semOption;

  /**
   * List models for options.
   */
  private DefaultListModel shmListModel, semListModel;

  /**
   * Description of the Field
   */
  private JList shmList;

  /**
   * Description of the Field
   */
  private JTextField semValue;

  /**
   * Description of the Field
   */
  private String selectedSemaphoreName;

  /**
   * Description of the Field
   */
  private String selectedSharedMemoryName;

  /**
   * Constructor for the IPCManagerFrame object
   *
   * @param images images used in display of ui.
   * @param newIPCManager animator used to receive and make messages.
   */
  public IPCManagerPanel(ImageIcon[] images, IPCManagerAnimator newIPCManager)
  {
    super();
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    windowWidth = this.getWidth();
    windowHeight = this.getHeight();
    myAnimator = newIPCManager;
    semaphoreMap = new HashMap();
    sharedMemoryMap = new HashMap();
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(defaultBgColour);
    mainPanel.setForeground(defaultFgColour);

    JLabel tmpLabel;

    shmOption = new JComboBox();

    shmListModel = new DefaultListModel();
    shmList = new JList(shmListModel);
    shmList.setBackground(defaultBgColour);
    shmList.setForeground(textBoxColour);
    shmList.setVisibleRowCount(3);
    shmList.setModel(shmListModel);

    semOption = new JComboBox();
    semValue = new JTextField(2);

    shmOption.addItem("None      ");
    shmOption.setBackground(Color.black);
    shmOption.setForeground(Color.white);
    shmOption.setSelectedIndex(0);

    semOption.addItem(NONE);
    semOption.setBackground(Color.black);
    semOption.setForeground(Color.white);
    semOption.setSelectedIndex(0);
    selectedSemaphoreName = NONE;

    JPanel sharedMemPanel = new JPanel();
    sharedMemPanel.setBackground(defaultBgColour);
    sharedMemPanel.setForeground(textBoxColour);

    GridBagConstraints tmpConstraints = new GridBagConstraints();
    GridBagLayout tmpGridBag = new GridBagLayout();

    sharedMemPanel.setLayout(tmpGridBag);

    tmpConstraints.weightx = 1;
    tmpConstraints.gridwidth = 1;
    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new JLabel("ID:");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpLabel.setFont(defaultFont);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    sharedMemPanel.add(tmpLabel);

    tmpLabel = new JLabel("Process Queue:");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    sharedMemPanel.add(tmpLabel);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpLabel = new JLabel("Value:");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    sharedMemPanel.add(tmpLabel);

    tmpConstraints.gridwidth = 1;
    tmpGridBag.setConstraints(shmOption, tmpConstraints);
    shmOption.addItemListener(new SharedMemorySelection());
    sharedMemPanel.add(shmOption);

    shmQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(shmQueue, tmpConstraints);
    sharedMemPanel.add(shmQueue);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(shmList, tmpConstraints);
    sharedMemPanel.add(shmList);

    JPanel semPanel = new JPanel();
    semPanel.setBackground(defaultBgColour);
    semPanel.setForeground(textBoxColour);

    tmpConstraints = new GridBagConstraints();
    tmpGridBag = new GridBagLayout();
    semPanel.setLayout(tmpGridBag);

    tmpConstraints.gridwidth = 1;
    tmpConstraints.gridheight = 1;
    tmpConstraints.weighty = 1;
    tmpConstraints.weightx = 1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = 1;
    tmpLabel = new JLabel("ID:");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    semPanel.add(tmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpLabel = new JLabel("Process Queue:");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    semPanel.add(tmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new JLabel("Value:");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(textBoxColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    semPanel.add(tmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = 1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(semOption, tmpConstraints);
    semOption.addItemListener(new SemaphoreSelection());
    semPanel.add(semOption);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(semQueue, tmpConstraints);
    semPanel.add(semQueue);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semValue.setFont(defaultFont);
    semValue.setBackground(textBoxColour);
    semValue.setForeground(defaultFgColour);
    semValue.setBackground(defaultBgColour);
    tmpGridBag.setConstraints(semValue, tmpConstraints);
    semPanel.add(semValue);

    add(sharedMemPanel, BorderLayout.NORTH);
    add(semPanel, BorderLayout.SOUTH);
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreCreated(String semaphoreId, int pid, int value)
  {
    if (semOption.getItemCount() == 1)
    {
      semOption.removeAll();
      semOption.addItem(SOME);
      selectedSemaphoreName = SOME;
    }
    semOption.addItem(semaphoreId);
    semaphoreMap.put(semaphoreId, new SemaphoreSharedMemoryGraphic(pid,
        new Integer(value)));
    updateSemaphoreQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreOpened(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.addProcess(pid);
    }
    updateSemaphoreQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreWaiting(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.setValue(new Integer(value));
    }
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreSignalled(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.setValue(new Integer(value));
    }
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreClosed(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.removeFirstProcess();
    }
    if (tmpGraphic.attachedProcesses() == 0)
    {
      semOption.removeItem(semaphoreId);
      if (semOption.getItemCount() == 1)
      {
        semOption.removeAll();
        semOption.addItem(NONE);
        selectedSemaphoreName = NONE;
      }
      else
      {
        selectedSemaphoreName = SOME;
      }
    }
    updateSemaphoreQueue();
  }

  /**
   * Add a newly created shared memory to the options to be selected by the
   * user.
   *
   * @param sharedMemoryId the name of the shared memory id.
   * @param memory the process id, memory type and how much memory was created.
   */
  void sharedMemoryCreated(String sharedMemoryId, MemoryReturn memoryReturn,
      Memory memory)
  {
    if (shmOption.getItemCount() == 1)
    {
      shmOption.removeAll();
      shmOption.addItem(SOME);
      selectedSharedMemoryName = SOME;
    }
    shmOption.addItem(sharedMemoryId);
    sharedMemoryMap.put(sharedMemoryId, new SemaphoreSharedMemoryGraphic(
      memoryReturn.getPID(), memory.toString()));
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   */
  void sharedMemoryOpened(String sharedMemoryId, int pid)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    if (tmpGraphic != null)
    {
      tmpGraphic.addProcess(pid);
    }
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   */
  void sharedMemoryClosed(String sharedMemoryId, int pid)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    if (tmpGraphic != null)
    {
      tmpGraphic.removeProcess(pid);

      // Remove the last graphic.
      if (tmpGraphic.attachedProcesses() == 0)
      {
        shmOption.removeItem(sharedMemoryId);
        if (shmOption.getItemCount() == 1)
        {
          shmOption.removeAll();
          shmOption.addItem(NONE);
          selectedSharedMemoryName = NONE;
        }
        else
        {
          selectedSharedMemoryName = SOME;
        }
      }
    }
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param memory Description of Parameter
   */
  void sharedMemoryRead(String semaphoreId, MemoryReturn memoryReturn,
      Memory memory)
  {
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   * @param memory Description of Parameter
   */
  void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    tmpGraphic.setValue(memory.toString());
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param processId Description of Parameter
   */
  void shmQueueAdd(String processId)
  {
    shmQueue.addToQueue(processId);
  }

  /**
   * Description of the Method
   */
  void shmQueueRemove()
  {
    shmQueue.removeFromQueue();
  }

  /**
   * Update the semaphore queue with the currently selected item.
   */
  private void updateSemaphoreQueue()
  {
    //Reset values
    semQueue.removeAllFromQueue();
    semValue.setText("");
    //Check that it's a real semaphore
    if (!selectedSemaphoreName.startsWith(NONE) &&
        !selectedSemaphoreName.startsWith(SOME))
    {
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
          semaphoreMap.get(selectedSemaphoreName);

      semValue.setText(((Integer) tmpGraphic.getValue()).toString());

      Iterator tmpIter = tmpGraphic.getAttachedProcesses();

      while (tmpIter.hasNext())
      {
        semQueue.addToQueue("P" + ((Integer) tmpIter.next()).toString());
      }
    }
    semQueue.repaint();
  }

  /**
   * Update the shared memory queue with the currently selected item.
   */
  private void updateSharedMemoryQueue()
  {
    //Reset values
    shmQueue.removeAllFromQueue();
    shmList.removeAll();

    //Check that it's a real semaphore
    if (!selectedSharedMemoryName.startsWith(NONE) &&
        !selectedSharedMemoryName.startsWith(SOME))
    {
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
          sharedMemoryMap.get(selectedSharedMemoryName);

      shmListModel.addElement((String) tmpGraphic.getValue());

      Iterator tmpIter = tmpGraphic.getAttachedProcesses();

      while (tmpIter.hasNext())
      {
        shmQueue.addToQueue("P" + ((Integer) tmpIter.next()).toString());
      }
    }
    shmQueue.repaint();
  }

  /**
   * Change the semaphore information displayed based on the selection.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SemaphoreSelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      //Get new selection
      selectedSemaphoreName = (String) e.getItem();
      updateSemaphoreQueue();
    }
  }

  /**
   * Change the shared memory information displayed based on the selection.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SharedMemorySelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      //Get new selection
      selectedSharedMemoryName = (String) e.getItem();
      updateSharedMemoryQueue();
    }
  }
}
