package org.rcosjava;

import java.applet.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.applet.AppletContext;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import javax.swing.*;

import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.about.AboutAnimator;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import org.rcosjava.software.animator.filesystem.FileSystemAnimator;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.animator.process.ProgramManagerAnimator;
import org.rcosjava.software.animator.support.ErrorDialog;
import org.rcosjava.software.animator.support.GraphicButton;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.animator.support.Overview;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.ProgramManager;
import org.rcosjava.software.terminal.TerminalManager;

import org.apache.log4j.*;

/**
 * Main startup file for RCOSjava Version 0.4.1
 * <P>
 * <DT> <B>History:</B>
 * <DD> 22/01/96 Will execute any given PCD file that uses output only, output
 * appears a bit buggy </DD>
 * <DD> 30/03/96 animators combined with OS<BR>
 * </DD>
 * <DD> 01/01/97 Problem with loading images in Netscape fixed. All files are
 * case sensitive even in Windows 95/NT. </DD>
 * <DD> 13/10/98 Started converting to Java 1.1.</DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 21st January 1996
 * @version 0.5 $Date$
 */
public class RCOS extends javax.swing.JApplet implements Runnable
{
  /**
   * Serial id.
   */
  private static final long serialVersionUID = 8487458619933951295L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(RCOS.class);

  /**
   * Messaging constant for OS Post Office.
   */
  public final static String OS_POST_OFFICE_ID = "OSPOSTOFFICE";

  /**
   * Messaging constant  for Animator Post Office.
   */
  public final static String ANIMATOR_POST_OFFICE_ID = "ANIMATORPOSTOFFICE";

  /**
   * Welcome message to be displayed.
   */
  private final static String WELCOME = "Welcome to RCOSjava Version 0.5";

  /**
   * Get line separator
   */
  private final static String lineSeparator =
      System.getProperties().getProperty("line.separator");

  /**
   * Copyright notice.
   */
  private final static String INFO = "Copyright 1995-2002." + lineSeparator +
      "Version 0.5." + lineSeparator + "Authors: David Jones, Brett Carter, " +
      "Bruce Jamieson, and Andrew Newman";

  /**
   * How many terminals wide.
   */
  private static final int MAX_TERMINAL_COLUMNS = 4;

  /**
   * How many termianl tall.
   */
  private static final int MAX_TERMINAL_ROWS = 2;

  /**
   * Maximum number of terminals
   */
  private static final int MAX_TERMINALS = (MAX_TERMINAL_COLUMNS *
      MAX_TERMINAL_ROWS);

  /**
   * If we are running the thread or not.
   */
  private static volatile boolean running = false;

  /**
   * Process scheduler animator object.
   */
  private static ProcessSchedulerAnimator psAnimator;

  /**
   * Terminal manager animator object.
   */
  private static TerminalManagerAnimator tmAnimator;

  /**
   * File system  animator object.
   */
  private static FileSystemAnimator fsAnimator;

  /**
   * Disk scheduler animator object.
   */
  private static DiskSchedulerAnimator dsAnimator;

  /**
   * CPU animator object.
   */
  private static CPUAnimator cpuAnimator;

  /**
   * Memory manager animator object.
   */
  private static MemoryManagerAnimator memoryAnimator;

  /**
   * IPC animator object.
   */
  private static IPCManagerAnimator ipcAnimator;

  /**
   * Multimedia animator object.
   */
  private static MultimediaAnimator mmAnimator;

  /**
   * About animator object.
   */
  private static AboutAnimator aboutAnimator;

  /**
   * Program manager animator object.
   */
  private static ProgramManagerAnimator pmAnimator;

  /**
   * Process scheduler animator object.
   */
  private static ProcessManagerAnimator pcmAnimator;

  /**
   * Base domain of location of applet.
   */
  private static String baseDomain;

  /**
   * Default domain of the applet.
   */
  private static String defaultDomain = "localhost";

  /**
   * Port to connect to to send/receive messages from the server.
   */
  private static int port;

  /**
   * Animator post office.
   */
  private static AnimatorOffice animatorPostOffice;

  /**
   * Operating system post office.
   */
  private static OSOffice osPostOffice;

  /**
   * Recorder of all messages.
   */
  private UniversalMessageRecorder recorder;

  /**
   * Player of recorded messages.
   */
  private UniversalMessagePlayer player;

  /**
   * Terminal manager operating system object.
   */
  private static TerminalManager theTerminalManager;

  /**
   * Kernel operating system object.
   */
  private static Kernel theKernel;

  /**
   * Memory manager operating system object.
   */
  private static MemoryManager theMemoryManager;

  /**
   * Program manager operating system object.
   */
  private static ProgramManager theProgramManager;

  /**
   * Process scheduler operating system object.
   */
  private static ProcessScheduler theProcessScheduler;

  /**
   * IPC manager operating system object.
   */
  private static IPC theIPC;

  /**
   * Main kernel thread.
   */
  private Thread kernelThread;

  /**
   * Audio clips.
   */
  private AudioClip clips[] = new AudioClip[1];

  /**
   * Images for terminal animator.
   */
  private static ImageIcon terminalImages[] = new ImageIcon[2];

  /**
   * Images for process scheduler.
   */
  private ImageIcon processImages[] = new ImageIcon[1];

  /**
   * Images for IPC manager.
   */
  private ImageIcon ipcImages[] = new ImageIcon[1];

  /**
   * Pause/Run menu item.
   */
  private static JMenuItem pauseRunMenuItem;

  /**
   * Gets the Running attribute of the RCOS class
   *
   * @return The Running value
   */
  public static boolean isRunning()
  {
    return running;
  }

  /**
   * Initialises the animator layouts based on the existence (now) of a consumer
   * for them.
   */
  public void setupAnimatorLayouts()
  {
    tmAnimator.setupLayout(this);
    psAnimator.setupLayout(this);
    pcmAnimator.setupLayout(this);
    memoryAnimator.setupLayout(this);
    ipcAnimator.setupLayout(this);
    cpuAnimator.setupLayout(this);
    pmAnimator.setupLayout(this);
    mmAnimator.setupLayout(this);
    aboutAnimator.setupLayout(this);

    // Ensure that it's repainted correctly
    getContentPane().invalidate();
    getContentPane().validate();
  }

  /**
   * Used to take parameters from the HTML. Currently uses baseDomain or uses
   * localhost (used to take host as a parameter). Still accepts the port. The
   * default is 4242.
   */
  public void getParameters()
  {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();

    try
    {
      baseDomain = getParameter("baseDomain");
      if (baseDomain == null)
      {
        baseDomain = defaultDomain;
      }
    }
    catch (Exception e)
    {
      log.error("Failed to get base domain parameter", e);
      baseDomain = defaultDomain;
    }

    try
    {
      port = (Integer.parseInt(getParameter("port")));
    }
    catch (java.lang.NumberFormatException e)
    {
      port = 4242;
    }

    // Set up XML serializer
    fr.dyade.koala.xml.koml.KOMLConstants.KOML_DTD = getCodeBase().toString() +
        "pll2/koml12.dtd";
    fr.dyade.koala.xml.koml.KOMLConstants.XML_PROPERTIES =
        getCodeBase().toString() + "XML.properties";
  }

  /**
   * Loads all the images and sounds required for use by the animators and the
   * default panel. Loads them directly out of the jar file.
   *
   * @return true if all images and sounds were successfully loaded.
   */
  public boolean getImagesAndSound()
  {
    String rootDir = "org/rcosjava/software/animator";

//    clips[0] = getAudioClip(getClass().getResource(rootDir +
//        "/audio/start.au"));

    URL tmpURL;

    try
    {
      // Get Terminal Manager images.
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/termon.jpg");
      terminalImages[0] = new ImageIcon(tmpURL);
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/termoff.jpg");
      terminalImages[1] = new ImageIcon(tmpURL);

      // Get Process Scheduler images.
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/rcoscpu.jpg");
      processImages[0] = new ImageIcon(tmpURL);

      // Get Memory Manager images.
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/memory.jpg");
      ipcImages[0] = new ImageIcon(tmpURL);
    }
    catch (NullPointerException npe)
    {
      new ErrorDialog("Fatal Error - Failed to Load Images", "Please ensure " +
          "that the images are available in the same place that RCOSjava was " +
          "started. Typically, in the JAR or project directory.").show();
      log.error("Failed to load images", npe);
      return false;
    }

    return true;
  }

  /**
   * Gets the AppletInfo attribute of the RCOS object
   *
   * @return The AppletInfo value
   */
  public String getAppletInfo()
  {
    return (this.INFO);
  }

  /**
   * Called by the init of the applet. Calls getParameters, getImagesAndSounds,
   * setScreenSize, initialiseOperatingSystem, initaliseAnimators,
   * initialiseRecorder, initialiseScreen().
   */
  public void init()
  {
    getParameters();
    boolean gotImages = getImagesAndSound();
    if (gotImages)
    {
      initialiseMessaging();
      initialiseOperatingSystem();
      initialiseAnimators();
      initialiseScreen();
    }
    else
    {
      System.exit(-1);
    }
  }

  /**
   * Initialise the message system. This includes the operating system and
   * animators post offices as well as the recording of all messages passed
   * between each sub-system.
   */
  public void initialiseMessaging()
  {
    //Start the Post Office (system messaging system).
    osPostOffice = new OSOffice(OS_POST_OFFICE_ID);

    //Start the animator PostOffice (animator messaging system).
    animatorPostOffice = new AnimatorOffice(ANIMATOR_POST_OFFICE_ID,
        osPostOffice);
    animatorPostOffice.startSending();

    //Start the recording subsystem
    recorder = new UniversalMessageRecorder(animatorPostOffice, osPostOffice,
        defaultDomain, port, "*Recorder");

    player = new UniversalMessagePlayer(animatorPostOffice, osPostOffice,
        defaultDomain, port, "*Player");
  }

  /**
   * Initialise all of the operating system components.
   */
  public void initialiseOperatingSystem()
  {
    // Start the Kernel and rest of OS.
    theKernel = new Kernel(osPostOffice);
    theTerminalManager = new TerminalManager(osPostOffice, MAX_TERMINALS);
    theProcessScheduler = new ProcessScheduler(osPostOffice);
    theIPC = new IPC(osPostOffice);
    theMemoryManager = new MemoryManager(osPostOffice);
    theProgramManager = new ProgramManager(osPostOffice, baseDomain, port,
        theKernel);
  }

  /**
   * Initialise all of the animator components.
   */
  public void initialiseAnimators()
  {
    tmAnimator = new TerminalManagerAnimator(animatorPostOffice, terminalImages,
        MAX_TERMINALS, MAX_TERMINAL_COLUMNS, MAX_TERMINAL_ROWS);

    psAnimator = new ProcessSchedulerAnimator(animatorPostOffice,
        processImages);

    memoryAnimator = new MemoryManagerAnimator(animatorPostOffice, ipcImages);

    ipcAnimator = new IPCManagerAnimator(animatorPostOffice, ipcImages);

    cpuAnimator = new CPUAnimator(animatorPostOffice);

    pmAnimator = new ProgramManagerAnimator(animatorPostOffice);

    pcmAnimator = new ProcessManagerAnimator(animatorPostOffice, this);

    mmAnimator = new MultimediaAnimator(animatorPostOffice, 400, 300, recorder,
        player);

    aboutAnimator = new AboutAnimator(animatorPostOffice, 300, 300);
  }

  /**
   * Initialise the screen for the applet representation.
   */
  public void initialiseScreen()
  {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu;
    JMenuItem menuItem;

    setJMenuBar(menuBar);

    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(menu);

    JMenuItem processMenuItem = new JMenuItem("New Process");
    processMenuItem.addActionListener(new NewProcessListener());
    menu.add(processMenuItem);
    JMenuItem killMenuItem = new JMenuItem("Kill Process");
    killMenuItem.setEnabled(false);
    menu.add(killMenuItem);
    JMenuItem changeMenuItem = new JMenuItem("Change Priority");
    changeMenuItem.setEnabled(false);
    menu.add(changeMenuItem);

    pcmAnimator.addMenuItems(menu, processMenuItem, killMenuItem,
      changeMenuItem);

    menu = new JMenu("CPU");
    menu.setMnemonic(KeyEvent.VK_C);
    menuBar.add(menu);

    menuItem = new JMenuItem("Step");
    menuItem.setAccelerator(KeyStroke.getKeyStroke('s'));
    menuItem.addActionListener(new StepCPUListener());
    menu.add(menuItem);
    pauseRunMenuItem = new JMenuItem("Pause");
    pauseRunMenuItem.setAccelerator(KeyStroke.getKeyStroke(' '));
    pauseRunMenuItem.addActionListener(new PauseRunCPUListener());
    menu.add(pauseRunMenuItem);

    menu = new JMenu("Tour");
    menu.setMnemonic(KeyEvent.VK_T);
    menuBar.add(menu);

    menuItem = new JMenuItem("Play");
    menuItem.addActionListener(new PlayTourListener());
    menu.add(menuItem);

    menuItem = new JMenuItem("Record New");
    menuItem.addActionListener(new RecordNewListener());
    menu.add(menuItem);

    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menuBar.add(menu);

    menuItem = new JMenuItem("Help Topics");
    menu.add(menuItem);
    menuItem = new JMenuItem("About");
    menuItem.addActionListener(new AboutListener());
    menu.add(menuItem);

    Container contentPane = getContentPane();
    contentPane.setBackground(RCOSFrame.DEFAULT_BG_COLOUR);
    contentPane.setForeground(RCOSFrame.DEFAULT_FG_COLOUR);

    JTabbedPane tabbedPane = new JTabbedPane();

    tabbedPane.add(tmAnimator.getPanel(), "Terminals");
    tabbedPane.add(psAnimator.getPanel(), "Process Scheduler");
    tabbedPane.add(cpuAnimator.getPanel(), "CPU");
    tabbedPane.add(memoryAnimator.getPanel(), "Memory");
    tabbedPane.add(ipcAnimator.getPanel(), "IPC");
    tabbedPane.add(new JPanel(), "Disk Scheduler");
    tabbedPane.add(new JPanel(), "File System");

    contentPane.add(tabbedPane, BorderLayout.CENTER);
//    clips[0].play();
  }

  /**
   * Start the operating system
   */
  public void start()
  {
    setupAnimatorLayouts();
    startThread();
    if (kernelThread == null)
    {
      kernelThread = new Thread(this, "RCOS");
      kernelThread.setPriority(kernelThread.MIN_PRIORITY);
      kernelThread.start();
    }
  }

  /**
   * Starts the thread if it is stopped.
   */
  public synchronized void startThread()
  {
    if (!running)
    {
      running = true;
      notifyAll();
    }
  }

  /**
   * Executes the performInstructionExecutionCycle on the kernel.
   */
  public void run()
  {
    while (kernelThread != null)
    {
      try
      {
        synchronized (this)
        {
          if (!running)
          {
            this.wait();
          }
        }
        theKernel.performInstructionExecutionCycle();
        kernelThread.sleep(50);
      }
      catch (InterruptedException exc)
      {
      }
    }
  }

  /**
   * Cleanup and stop the currently running thread.
   */
  public void stop()
  {
    if (kernelThread != null)
    {
      kernelThread = null;
    }
  }

  /**
   * Returns a list of all the current animators and operating system
   * components.
   *
   * @return a list of all the current animators and operation system
   *   components.
   */
  public static List getRCOSComponents()
  {
    ArrayList list = new ArrayList();

    // Add animators
    list.add(tmAnimator);
    list.add(psAnimator);
//    list.add(memoryAnimator);
//    list.add(ipcAnimator);
//    list.add(cpuAnimator);
//    list.add(pmAnimator);
//    list.add(pcmAnimator);
//    list.add(mmAnimator);

    // Add operating system
//    list.add(theKernel);
    list.add(theTerminalManager);
    list.add(theProcessScheduler);
//    list.add(theMemoryManager);
//    list.add(theIPC);

    return list;
  }

  /**
   * Sets all of the RCOS components.  Order is presumed to be the same as
   * defined in getRCOSComponents.
   *
   * @param rcosComponents the list of RCOS components.
   */
  public static void setRCOSComponents(List rcosComponents)
  {
    // Load animators.
    tmAnimator = (TerminalManagerAnimator) rcosComponents.get(0);
    psAnimator = (ProcessSchedulerAnimator) rcosComponents.get(1);
//    memoryAnimator = (MemoryManagerAnimator) rcosComponents.get(2);
//    ipcAnimator = (IPCManagerAnimator) rcosComponents.get(3);
//    cpuAnimator = (CPUAnimator) rcosComponents.get(4);
//    pmAnimator = (ProgramManagerAnimator) rcosComponents.get(5);
//    pcmAnimator = (ProcessManagerAnimator) rcosComponents.get(6);
//    mmAnimator = (MultimediaAnimator) rcosComponents.get(7);

    // Load OS.
//    theKernel = (Kernel) rcosComponents.get(4);
    theTerminalManager = (TerminalManager) rcosComponents.get(2);
    theProcessScheduler = (ProcessScheduler) rcosComponents.get(3);
//    theMemoryManager = (MemoryManager) rcosComponents.get(5);
//    theIPC = (IPC) rcosComponents.get(8);
    theProgramManager = new ProgramManager(osPostOffice, baseDomain, port,
        theKernel);
  }

  /**
   * Returns the Animator PO.
   *
   * @return the Animator PO.
   */
  public static AnimatorOffice getAnimatorPostOffice()
  {
    return animatorPostOffice;
  }

  /**
   * Returns the OS PO.
   *
   * @return the OS PO.
   */
  public static OSOffice getOSPostOffice()
  {
    return osPostOffice;
  }

  /**
   * Returns the terminal manager animator.
   *
   * @return the terminal manager animator.
   */
  public static TerminalManagerAnimator getTerminalAnimator()
  {
    return tmAnimator;
  }

  /**
   * Returns the process scheduler animator.
   *
   * @return the process scheduler animator.
   */
  public static ProcessSchedulerAnimator getProcessSchedulerAnimator()
  {
    return psAnimator;
  }

  /**
   * Returns the IPC manager animator.
   *
   * @return the IPC manager animator.
   */
  public static IPCManagerAnimator getIPCManagerAnimator()
  {
    return ipcAnimator;
  }

  /**
   * Returns the memory manager animator.
   *
   * @return the memory manager animator.
   */
  public static MemoryManagerAnimator getMemoryManagerAnimator()
  {
    return memoryAnimator;
  }

  /**
   * Start a new process by displaying the program manager frame.
   */
  private static class NewProcessListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      pmAnimator.showFrame();
    }
  }

  /**
   * Creates and returns a new KillProcessListener with the given
   * ProcessManagareAnimator.
   *
   * @param existingAnimator the animator to be called when an even occurs.
   */
  public KillProcessListener createNewKillListener(ProcessManagerAnimator
      existingAnimator)
  {
    return new KillProcessListener(existingAnimator);
  }

  /**
   * Listener to kill a selected process.
   */
  private static class KillProcessListener implements ActionListener
  {
    /**
     * Animator to use to send the kill message.
     */
    private ProcessManagerAnimator animator;

    /**
     * Create a new kill process listener.
     *
     * @param newAnimator the animator to call the change in process priority.
     */
    public KillProcessListener(ProcessManagerAnimator newAnimator)
    {
      animator = newAnimator;
    }

    /**
     * Actioned performed on the button in the UI to kill a process.
     *
     * @param e the event which contains the numeric id of the process to
     *     kill.
     */
    public void actionPerformed(ActionEvent e)
    {
      animator.sendKillMessage(Integer.parseInt(e.getActionCommand()));
    }
  }

  /**
   * Creates and returns a new ChangePriorityListener with the given
   * ProcessManagareAnimator.
   *
   * @param existingAnimator the animator to be called when an even occurs.
   */
  public ChangePriorityListener createChangePriorityListener(
      ProcessManagerAnimator existingAnimator)
  {
    return new ChangePriorityListener(existingAnimator);
  }

  /**
   * Listener to change the priority of a selected process.
   */
  private static class ChangePriorityListener implements ActionListener
  {
    /**
     * Animator to use to send the change in priority.
     */
    private ProcessManagerAnimator animator;

    /**
     * Create a new priority listener
     *
     * @param newAnimator the animator to call the change in process priority.
     */
    public ChangePriorityListener(ProcessManagerAnimator newAnimator)
    {
      animator = newAnimator;
    }

    /**
     * Actioned performed on the button in the UI to change a given process.
     *
     * @param e the event which contains the numeric id of the process to
     *     change.
     */
    public void actionPerformed(ActionEvent e)
    {
      animator.sendRequestProcessPriority(
          Integer.parseInt(e.getActionCommand()));
    }
  }

  /**
   * Listener to step through the execution of the process.
   */
  private static class StepCPUListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (!theKernel.isPaused())
      {
        pauseRunMenuItem.setText("Run");
        theKernel.pause();
      }
      theKernel.step();
    }
  }

  /**
   * Listens for a change in whether RCOS is running or paused.  Toggles it from
   * one state to the other.
   */
  private static class PauseRunCPUListener implements ActionListener
  {
    /**
     * The item has been selected.
     *
     * @param e the event cause by selecting the item.
     */
    public void actionPerformed(ActionEvent e)
    {
      if (!theKernel.isPaused())
      {
        pauseRunMenuItem.setText("Run");
        theKernel.pause();
      }
      else
      {
        pauseRunMenuItem.setText("Pause");
        theKernel.unpause();
      }
    }
  }

  /**
   * Listens for when a user selects to play a multimedia tour.
   */
  private static class PlayTourListener implements ActionListener
  {
    /**
     * The item has been selected.
     *
     * @param e the event cause by selecting the item.
     */
    public void actionPerformed(ActionEvent e)
    {
      mmAnimator.showPlayFrame();
    }
  }

  /**
   * Listens for when a user selects to play a multimedia tour.
   */
  private static class RecordNewListener implements ActionListener
  {
    /**
     * The item has been selected.
     *
     * @param e the event cause by selecting the item.
     */
    public void actionPerformed(ActionEvent e)
    {
      mmAnimator.showRecordFrame();
    }
  }

  /**
   * Listens for a change to show about information.
   */
  private static class AboutListener implements ActionListener
  {
    /**
     * The item has been selected.
     *
     * @param e the event cause by selecting the item.
     */
    public void actionPerformed(ActionEvent e)
    {
      aboutAnimator.showFrame();
    }
  }

  /**
   * A mouse adapter which attachs itself to the buttons displayed by the main
   * screen. It accepts the animator to call show on when the button is pressed.
   */
  private static class ShowAnimator extends MouseAdapter
  {
    /**
     * The object that contains the frame to show.
     */
    private Object parent;

    /**
     * Create a new show animator.
     *
     * @param newParent the object to call showFrame on.
     */
    public ShowAnimator(Object newParent)
    {
      parent = newParent;
    }

    /**
     * Display the animator now that the button has been pressed on.
     *
     * @param me mouse event being pressed.
     */
    public void mouseClicked(MouseEvent me)
    {
      Class animator = parent.getClass();
      Method show = null;

      try
      {
        show = animator.getMethod("showFrame", null);
        show.invoke(parent, null);
      }
      catch (Exception e)
      {
        System.err.println(this + "- exception: " + e);
      }
    }
  }
}
