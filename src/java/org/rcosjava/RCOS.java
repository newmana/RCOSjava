package org.rcosjava;

import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.applet.AppletContext;
import java.awt.event.*;
import java.io.*;

import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
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
   * Messaging constant for OS Post Office.
   */
  public final static String OS_POST_OFFICE_ID = "OSPOSTOFFICE";

  /**
   * Messaging constant  for Animator Post Office.
   */
  public final static String ANIMATOR_POST_OFFICE_ID = "ANIMATORPOSTOFFICE";

  /**
   * Number of images of people.
   */
  public final static int NUMBER_OF_PEOPLE = 4;

  /**
   * Number of images for buttons.
   */
  public final static int NUMBER_OF_BUTTONS = 3;

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
   * URL to access the help system
   */
  private static URL helpURL;

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
   * Overview animator object.
   */
  private static Overview overviewAnimator;

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
   * Status bar for messages.
   */
  private static JTextArea statusBar;

  /**
   * Base domain of location of applet.
   */
  public String baseDomain;

  /**
   * Default domain of the applet.
   */
  public String defaultDomain = new String("localhost");

  /**
   * Port to connect to to send/receive messages from the server.
   */
  public int port;

  /**
   * Main panels.
   */
  public JPanel mainPanel, systemAnimatorsPanel, systemAnimatorsTitlePanel1,
      systemAnimatorsTitlePanel2, systemInterfacePanel, systemInterfaceTitlePanel,
      systemInterfaceTitlePanel1, systemInterfaceTitlePanel2, informationPanel,
      informationTitlePanel;

  /**
   * Animator post office.
   */
  private AnimatorOffice animatorPostOffice;

  /**
   * Operating system post office.
   */
  private OSOffice osPostOffice;

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
  private TerminalManager theTerminalManager;

  /**
   * Kernel operating system object.
   */
  private Kernel theKernel;

  /**
   * Memory manager operating system object.
   */
  private MemoryManager theMemoryManager;

  /**
   * Program manager operating system object.
   */
  private ProgramManager theProgramManager;

  /**
   * Process scheduler operating system object.
   */
  private ProcessScheduler theProcessScheduler;

  /**
   * IPC manager operating system object.
   */
  private IPC theIPC;

  /**
   * Main kernel thread.
   */
  private Thread kernelThread;

  /**
   * Audio clips.
   */
  private AudioClip clips[] = new AudioClip[1];

  /**
   * Up button images.
   */
  private ImageIcon upButtons[] = new ImageIcon[NUMBER_OF_BUTTONS];

  /**
   * Down button images.
   */
  private ImageIcon downButtons[] = new ImageIcon[NUMBER_OF_BUTTONS];

  /**
   * Images for terminal animator.
   */
  private ImageIcon terminalImages[] = new ImageIcon[4];

  /**
   * Images for process scheduler.
   */
  private ImageIcon processImages[] = new ImageIcon[4];

  /**
   * Images for process manager.
   */
  private ImageIcon processManagerImages[] = new ImageIcon[2];

  /**
   * Images for about animator.
   */
  private ImageIcon aboutImages[] = new ImageIcon[4];

  /**
   * Images for IPC manager.
   */
  private ImageIcon ipcImages[] = new ImageIcon[1];

  /**
   * Pause/Run menu item.
   */
  private JMenuItem pauseRunMenuItem;

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
   * The GUI contains a status bar that shows any system messages. This adds a
   * new message to it.
   *
   * @param newMessage new message to add to the status bar.
   */
  public static void updateStatusBar(String newMessage)
  {
    statusBar.insert(newMessage + "\n", 0);
  }

  /**
   * Initialises the animator layouts based on the existence (now) of a consumer
   * for them.
   */
  public void setupAnimatorLayouts()
  {
    tmAnimator.setupLayout(this);
    psAnimator.setupLayout(this);
    memoryAnimator.setupLayout(this);
    ipcAnimator.setupLayout(this);
    cpuAnimator.setupLayout(this);
    pmAnimator.setupLayout(this);
    pcmAnimator.setupLayout(this);
    mmAnimator.setupLayout(this);
    aboutAnimator.setupLayout(this);
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
    }
    catch (Exception e)
    {
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
    //helpURLStr = getClass().getResource("/Help/index.html").toString();
  }

  /**
   * Loads all the images and sounds required for use by the animators and the
   * default panel. Loads them directly out of the jar file.
   */
  public void getImagesAndSound()
  {
    String rootDir = "org/rcosjava/software/animator";

//    clips[0] = getAudioClip(getClass().getResource(rootDir +
//        "/audio/start.au"));

    URL tmpURL;

    for (int count = 0; count < NUMBER_OF_PEOPLE; count++)
    {
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/p" + count + ".jpg");

      // TODO Check for null before calling this
      aboutImages[count] = new ImageIcon(tmpURL);
    }

    for (int count = 0; count < NUMBER_OF_BUTTONS; count++)
    {
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/b" + count + "up.jpg");
      upButtons[count] = new ImageIcon(tmpURL);

      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/b" + count + "down.jpg");
      downButtons[count] = new ImageIcon(tmpURL);
    }

    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/termon.jpg");
    terminalImages[0] = new ImageIcon(tmpURL);
    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/termoff.jpg");
    terminalImages[1] = new ImageIcon(tmpURL);
    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/process1.gif");
    processImages[0] = new ImageIcon(tmpURL);
    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/process2.gif");
    processImages[1] = new ImageIcon(tmpURL);
    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/rcoscpu.jpg");
    processImages[2] = new ImageIcon(tmpURL);
    tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
      "/images/memory.jpg");
    ipcImages[0] = new ImageIcon(tmpURL);

    terminalImages[2] = upButtons[1];
    terminalImages[3] = downButtons[1];
    processManagerImages[0] = upButtons[1];
    processManagerImages[1] = downButtons[1];
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
    getImagesAndSound();
    initialiseMessaging();
    initialiseOperatingSystem();
    initialiseAnimators();
    initialiseScreen();
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

    //Start the recording subsystem
    recorder = new UniversalMessageRecorder(defaultDomain, port, "*Recorder",
        osPostOffice, animatorPostOffice);

    player = new UniversalMessagePlayer(defaultDomain, port, "*Player",
        osPostOffice, animatorPostOffice);
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

    cpuAnimator = new CPUAnimator(animatorPostOffice, null);

    pmAnimator = new ProgramManagerAnimator(animatorPostOffice, null);

    pcmAnimator = new ProcessManagerAnimator(animatorPostOffice,
        processManagerImages);

    mmAnimator = new MultimediaAnimator(animatorPostOffice, 250, 250,
        processManagerImages, recorder, player);

    aboutAnimator = new AboutAnimator(animatorPostOffice, 250, 250,
        aboutImages);
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

    menuItem = new JMenuItem("New Process");
    menuItem.addActionListener(new NewProcessListener());
    menu.add(menuItem);
    menuItem = new JMenuItem("Kill Process");
    menu.add(menuItem);
    menuItem.addActionListener(new KillProcessListener());
    menuItem = new JMenuItem("Change Priority");
    menu.add(menuItem);
    menuItem.addActionListener(new ChangePriorityListener());

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
    menu.add(menuItem);
    menuItem = new JMenuItem("Record New");
    menu.add(menuItem);
    menuItem = new JMenuItem("Save and Record New");
    menu.add(menuItem);

    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menuBar.add(menu);

    menuItem = new JMenuItem("Help Topics");
    menu.add(menuItem);
    menuItem = new JMenuItem("About");
    menu.add(menuItem);

    Container contentPane = getContentPane();
    contentPane.setBackground(RCOSFrame.defaultBgColour);
    contentPane.setForeground(RCOSFrame.defaultFgColour);

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
      notify();
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

  private class NewProcessListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      pmAnimator.showFrame();
    }
  }

  private class KillProcessListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      System.err.println("Kill Process");
    }
  }

  private class ChangePriorityListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      System.err.println("Change Process");
    }
  }

  private class StepCPUListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      System.err.println("Step CPU");
      if (!theKernel.isPaused())
      {
        pauseRunMenuItem.setText("Run");
        theKernel.pause();
      }

      theKernel.step();
    }
  }

  private class PauseRunCPUListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      System.err.println("Pause/Run CPU");
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
   * A mouse adapter which attachs itself to the buttons displayed by the main
   * screen. It accepts the animator to call show on when the button is pressed.
   *
   * @author administrator
   * @created 28 April 2002
   */
  private class ShowAnimator extends MouseAdapter
  {
    /**
     * Description of the Field
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
