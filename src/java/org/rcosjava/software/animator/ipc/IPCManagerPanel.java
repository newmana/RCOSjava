package org.rcosjava.software.animator.ipc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.*;

import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSBox;
import org.rcosjava.software.animator.support.RCOSQueue;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryReturn;
import org.rcosjava.software.util.FIFOQueue;

import org.apache.log4j.*;

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
   * Serial id.
   */
  private static final long serialVersionUID = 1389081753585298701L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(IPCManagerPanel.class);

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
   * Animator that I receive calls from and makes calls back when a user event
   * occurs.
   */
  private IPCManagerAnimator myAnimator;

  /**
   * Combo boxes for shared memory and semaphores.
   */
  private JComboBox shmOption, semOption;

  /**
   * The list of shared memory.
   */
  private JTextArea shmList;

  /**
   * The current value of the currently selected semaphore.
   */
  private JTextField semValue;

  /**
   * Map of current semaphores.
   */
  private HashMap semaphoreMap = new HashMap();

  /**
   * Map of current shared memory segments.
   */
  private HashMap sharedMemoryMap = new HashMap();

  /**
   * Semaphore queue to display.
   */
  private RCOSQueue semQueue;

  /**
   * Shared memory queue to display.
   */
  private RCOSQueue shmQueue;

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
    myAnimator = newIPCManager;
  }

  /**
   * Sets a new IPC Manager animator.
   *
   * @param newIPCManagerAnimator the new IPC Manager animator.
   */
  void setManager(IPCManagerAnimator newIPCManagerAnimator)
  {
    myAnimator = newIPCManagerAnimator;
  }

  /**
   * Sets up the layout of the paenl.  It creates two drop downs to display
   * shared memory and semaphores.
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    // Label and constraints used by all the following code for layout.
    JLabel tmpLabel;
    JComponent tmpPanel;
    GridBagConstraints tmpConstraints = new GridBagConstraints();
    GridBagLayout tmpGridBag = new GridBagLayout();

    // Setup the default colours and layout.
    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setLayout(new BorderLayout());

    // The main panel will have two other panels (semaphores and shared memory)
    // added to it
    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(defaultBgColour);
    mainPanel.setForeground(defaultFgColour);

    // Setup the shared memory and semaphore widgets
    shmOption = new JComboBox();
    shmList = new JTextArea();
    shmList.setBackground(defaultBgColour);
    shmList.setForeground(textBoxColour);
    shmList.setLineWrap(true);
    JScrollPane shmListPane = new JScrollPane(shmList);
    shmListPane.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    shmListPane.setPreferredSize(new Dimension(500, 90));

    shmOption.addItem(NONE);
    shmOption.setForeground(choiceColour);
    shmOption.setBackground(defaultBgColour);
    shmOption.setSelectedIndex(0);

    semOption = new JComboBox();
    semValue = new JTextField(2);
    semOption.addItem(NONE);
    semOption.setForeground(choiceColour);
    semOption.setBackground(defaultBgColour);
    semOption.setSelectedIndex(0);
    myAnimator.setSelectedSemaphoreName(NONE);

    // Setup the semaphore panel with a panel and border with title.
    JPanel semPanel = new JPanel();
    semPanel.setBackground(defaultBgColour);
    semPanel.setForeground(textBoxColour);
    TitledBorder semTitle = BorderFactory.createTitledBorder("Semaphores");
    semTitle.setTitleColor(defaultFgColour);
    semPanel.setBorder(BorderFactory.createCompoundBorder(semTitle,
        BorderFactory.createEmptyBorder(3,3,3,3)));

    tmpConstraints = new GridBagConstraints();
    tmpGridBag = new GridBagLayout();
    semPanel.setLayout(tmpGridBag);

    tmpConstraints.gridwidth = 1;
    tmpConstraints.gridheight = 1;
    tmpConstraints.weighty = 1;
    tmpConstraints.weightx = 1;
    tmpConstraints.anchor = GridBagConstraints.WEST;
    tmpConstraints.fill = GridBagConstraints.BOTH;
    tmpConstraints.insets = new Insets(1, 1, 1, 1);

    //Default panel size for all tmpPanels
    Dimension panelSize = new Dimension(100, 30);

    //Setup temporary component to be used to group items together.
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(panelSize);

    // Id label and value
    tmpLabel = new JLabel(" ID: ");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpPanel.add(tmpLabel);

    semOption.addItemListener(new SemaphoreSelection());
    tmpPanel.add(semOption);

    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    semPanel.add(tmpPanel);

    // Reset tmpPanel
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(new Dimension(200, 30));

    // Process queue label and queue value.
    tmpLabel = new JLabel(" Process Queue: ");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpPanel.add(tmpLabel);

    semQueue = new RCOSQueue(5, defaultFont);
    tmpPanel.add(semQueue);

    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    semPanel.add(tmpPanel);

    // Reset tmpPanel
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(panelSize);

    // Value label and value.
    tmpLabel = new JLabel(" Value: ");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpPanel.add(tmpLabel);

    semValue.setFont(defaultFont);
    semValue.setBackground(textBoxColour);
    semValue.setForeground(defaultFgColour);
    semValue.setBackground(defaultBgColour);
    tmpPanel.add(semValue);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    semPanel.add(tmpPanel);

    // Setup the shared memory panel with a border and title.
    JPanel sharedMemPanel = new JPanel();
    sharedMemPanel.setBackground(defaultBgColour);
    sharedMemPanel.setForeground(textBoxColour);
    TitledBorder sharedMemTitle = BorderFactory.createTitledBorder(
        "Shared Memory");
    sharedMemTitle.setTitleColor(defaultFgColour);
    sharedMemPanel.setBorder(BorderFactory.createCompoundBorder(
        sharedMemTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    sharedMemPanel.setLayout(tmpGridBag);

    tmpConstraints.gridwidth = 1;
    tmpConstraints.gridheight = 1;
    tmpConstraints.weighty = 1;
    tmpConstraints.weightx = 1;
    tmpConstraints.anchor = GridBagConstraints.WEST;
    tmpConstraints.fill = GridBagConstraints.BOTH;
    tmpConstraints.insets = new Insets(1, 1, 1, 1);

    // Reset tmpPanel
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(panelSize);

    // Setup id label and drop down box
    tmpLabel = new JLabel(" ID: ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpLabel.setFont(defaultFont);
    tmpPanel.add(tmpLabel);

    shmOption.addItemListener(new SharedMemorySelection());
    tmpPanel.add(shmOption);

    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    sharedMemPanel.add(tmpPanel);

    // Reset tmpPanel
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(new Dimension(200, 30));

    // Setup the process queue label and process queue.
    tmpLabel = new JLabel(" Process Queue: ");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpPanel.add(tmpLabel);

    shmQueue = new RCOSQueue(5, defaultFont);
    tmpPanel.add(shmQueue);

    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    sharedMemPanel.add(tmpPanel);

    // Reset tmpPanel
    tmpPanel = new JPanel();
    tmpPanel.setBackground(defaultBgColour);
    tmpPanel.setForeground(defaultFgColour);
    tmpPanel.setPreferredSize(panelSize);

    // Add an empty panel
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(tmpPanel, tmpConstraints);
    sharedMemPanel.add(tmpPanel);

    // Setup the value of the shared memory and it's value
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpLabel = new JLabel(" Value: ");
    tmpLabel.setFont(defaultFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    tmpGridBag.setConstraints(tmpLabel, tmpConstraints);
    sharedMemPanel.add(tmpLabel);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(shmListPane, tmpConstraints);
    sharedMemPanel.add(shmListPane);

    add(semPanel, BorderLayout.NORTH);
    add(sharedMemPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates a new semaphore graphic item of the given id and updates the
   * drop down of the current semaphores.
   *
   * @param semaphore that contains the semaphore id and other pertant details.
   */
  void semaphoreCreated(final Semaphore semaphore)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        SemaphoreSharedMemoryGraphic tmpGraphic = new SemaphoreSharedMemoryGraphic(
            semaphore.getCreatorId(), new Integer(semaphore.getValue()));

        semaphoreMap.put(semaphore.getName(), tmpGraphic);

        if (semOption.getItemCount() == 1)
        {
          semOption.removeAllItems();
          semOption.addItem(SOME);
          myAnimator.setSelectedSemaphoreName(SOME);
        }

        semOption.addItem(semaphore.getName());
        updateSemaphoreQueue();
      }
    });
  }

  /**
   * The semaphore has been opened - currently does nothing.
   *
   * @param semaphore that contains the semaphore id and other pertant details.
   * @param processId the semaphore that opened the semaphore.
   */
  void semaphoreOpened(Semaphore semaphore, int processId)
  {
  }

  /**
   * Sets the value of the shared memory graphic if it already exists.
   *
   * @param semaphore that contains the semaphore id and other pertant details.
   * @param processId the id of the process that is waiting.
   * @param value the current value of the semaphore.
   */
  void semaphoreWaiting(Semaphore semaphore, int processId, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphore.getName());

    if (tmpGraphic != null)
    {
      if (value == -1)
      {
        tmpGraphic.addProcess(processId);
      }
      else
      {
        tmpGraphic.setValue(new Integer(value));
      }
    }

    updateSemaphoreQueue();
  }

  /**
   * Sets the new value of the semaphore graphic if it exists.
   *
   * @param semaphore that contains the semaphore id and other pertant details.
   * @param signalledId the semaphore that was signalled remove from the queue.
   * @param value the value of the semaphore.
   */
  void semaphoreSignalled(Semaphore semaphore, int signalledId, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphore.getName());

    if (tmpGraphic != null)
    {
      if (signalledId != -1)
      {
        tmpGraphic.removeProcess(signalledId);
      }
      tmpGraphic.setValue(new Integer(value));
    }

    updateSemaphoreQueue();
  }

  /**
   * Removes the semaphore graphics if it exsts and then updates the drop downs
   * of current semaphores.
   *
   * @param semaphore that contains the semaphore id and other pertant details.
   */
  void semaphoreClosed(final Semaphore semaphore)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
            semaphoreMap.get(semaphore.getName());

        // Remove first process
        if (tmpGraphic != null)
        {
          tmpGraphic.removeFirstProcess();
        }

        if (log.isDebugEnabled())
        {
          log.debug("Number of attached process after removal: " +
                    tmpGraphic.attachedProcesses());
        }

        // Remove semaphore from all data structures if there are no attached
        // processes left
        if (tmpGraphic.attachedProcesses() == 0)
        {
          // Remove from semaphore map.
          semaphoreMap.remove(semaphore.getName());

          // Remove from semaphore options.
          semOption.removeItem(semaphore.getName());

          if (semOption.getItemCount() == 1)
          {
            semOption.removeAllItems();
            semOption.addItem(NONE);
            myAnimator.setSelectedSemaphoreName(NONE);
          }
        }
        updateSemaphoreQueue();
      }
    });
  }

  /**
   * Add a newly created shared memory to the options to be selected by the
   * user.
   *
   * @param sharedMemoryId the unique id of the shared memeory segment created.
   * @param processId the process id that is creating the shared memory segment.
   * @param memory the memory object being shared.
   */
  void sharedMemoryCreated(final String sharedMemoryId,
      final MemoryReturn memoryReturn, final Memory memory)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        SemaphoreSharedMemoryGraphic tmpGraphic = new SemaphoreSharedMemoryGraphic(
          memoryReturn.getPID(), memory.toString());

        sharedMemoryMap.put(sharedMemoryId, tmpGraphic);

        if (shmOption.getItemCount() == 1)
        {
          shmOption.removeAllItems();
          shmOption.addItem(SOME);
          myAnimator.setSelectedSemaphoreName(SOME);
        }
        myAnimator.setSelectedSharedMemoryName(sharedMemoryId);
        shmOption.addItem(sharedMemoryId);
        updateSharedMemoryQueue();
      }
    });
  }

  /**
   * Creates a new shared memory graphics and adds the process to its queue.
   *
   * @param sharedMemoryId the unique id of the shared memory segment opened.
   * @param processId the process id that is opening the shared memory segment.
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
   * Gets the shared memory graphic and removes it if it exists.  Also removes
   * the objects from the queue and updates the drop down of the current
   * shared memory segments.
   *
   * @param sharedMemoryId the unique id of the shared memory segment to close.
   * @param processId the process id that is closing the shared memory segment.
   */
  void sharedMemoryClosed(final String sharedMemoryId, final int pid)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
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

            // Update the drop down.
            if (shmOption.getItemCount() == 1)
            {
              shmOption.removeAllItems();
              shmOption.addItem(NONE);
              myAnimator.setSelectedSharedMemoryName(NONE);
            }
            else
            {
              myAnimator.setSelectedSharedMemoryName(SOME);
            }
          }
        }
        updateSharedMemoryQueue();
      }
    });
  }

  /**
   * Called when a memory segment is read.  Currently does nothing.
   *
   * @param semaphoreId the unique id of the shared memory segment to read.
   * @param memoryReturn the resultant object from a memory read.
   * @param memory the current memory segment object.
   */
  void sharedMemoryRead(String semaphoreId, MemoryReturn memoryReturn,
      Memory memory)
  {
  }

  /**
   * Sets the graphic of the shared memory object ot the value in the memory
   * object and updates the shared memory queue.
   *
   * @param sharedMemoryId the unique id of the shared memory segment written.
   * @param memory the current memory object being written.
   */
  void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    tmpGraphic.setValue(memory.toString());
    updateSharedMemoryQueue();
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
    if (!myAnimator.getSelectedSemaphoreName().startsWith(NONE) &&
        !myAnimator.getSelectedSemaphoreName().startsWith(SOME))
    {
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
          semaphoreMap.get(myAnimator.getSelectedSemaphoreName());

      // We may have a semaphore selected but it may not be available anymore
      // in the queue (due to the updates dones in invokeLater thread).
      if (tmpGraphic != null)
      {
        semValue.setText(((Integer) tmpGraphic.getValue()).toString());
        FIFOQueue queue = tmpGraphic.getAttachedProcesses();

        for (int index = 0; index < queue.size(); index++)
        {
          semQueue.addToQueue("P" + ((Integer) queue.peek(index)).toString());
        }
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

    //Check that it's a real shared memory queue.
    if (!myAnimator.getSelectedSharedMemoryName().startsWith(NONE) &&
        !myAnimator.getSelectedSharedMemoryName().startsWith(SOME))
    {
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(myAnimator.getSelectedSharedMemoryName());

      // We may have a semaphore selected but it may not be available anymore
      // in the queue (due to the updates dones in invokeLater thread).
      if (tmpGraphic != null)
      {
        String tmpValue = (String) tmpGraphic.getValue();
        shmList.setText(tmpValue);
        FIFOQueue queue = tmpGraphic.getAttachedProcesses();

        for (int index = 0; index < queue.size(); index++)
        {
          semQueue.addToQueue("P" + ((Integer) queue.peek(index)).toString());
        }
      }
    }
    shmQueue.repaint();
  }

  /**
   * Change the semaphore information displayed based on the selection.
   */
  class SemaphoreSelection implements ItemListener
  {
    /**
     * The item being viewed has changed.
     *
     * @param e the item that has changed - assumed casting to String on
     *   getItem.
     */
    public void itemStateChanged(final ItemEvent e)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          //Get new selection
          myAnimator.setSelectedSemaphoreName((String) e.getItem());
          updateSemaphoreQueue();
        }
      });
    }
  }

  /**
   * Change the shared memory information displayed based on the selection.
   */
  class SharedMemorySelection implements ItemListener
  {
    /**
     * The item being viewed has changed.
     *
     * @param e the item that has changed - assumed casting to String on
     *   getItem.
     */
    public void itemStateChanged(final ItemEvent e)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          //Get new selection
          myAnimator.setSelectedSemaphoreName((String) e.getItem());
          updateSharedMemoryQueue();
        }
      });
    }
  }
}
