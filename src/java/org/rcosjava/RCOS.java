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
  public final static String osPostOfficeId = "OSPOSTOFFICE";

  /**
   * Messaging constant  for Animator Post Office.
   */
  public final static String animatorPostOfficeId = "ANIMATORPOSTOFFICE";

  /**
   * Number of images of people.
   */
  public final static int numberPeople = 4;

  /**
   * Number of images for buttons.
   */
  public final static int numberButtons = 3;

  /**
   * If we are running the thread or not.
   */
  private static volatile boolean running = false;

  /**
   * Menu name 1.
   */
  private final static String menu1 = "System";

  /**
   * Menu name 2.
   */
  private final static String menu2 = "Animators";

  /**
   * Menu name 3.
   */
  private final static String menu3 = "System";

  /**
   * Menu name 4.
   */
  private final static String menu4 = "Interface";

  /**
   * Menu name 5.
   */
  private final static String menu5 = "Information";

  /**
   * Welcome message to be displayed.
   */
  private final static String welcome = "Welcome to RCOSjava Version 0.5";

  /**
   * Copyright notice.
   */
  private final static String info = "Copyright 1995-2002.\nVersion 0.588888888888888.\n" +
      "Authors: David Jones, Brett Carter, Bruce Jamieson, and Andrew Newman";

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
   * Small x,y window sizes.
   */
  public int smallX, smallY;

  /**
   * Default x,y windows sizes.
   */
  public int defX, defY;

  /**
   * Large x,y windows sizes.
   */
  public int largeX, largeY;

  /**
   * Screen size width.
   */
  public final int screenX = 640;

  /**
   * Screen size height.
   */
  public final int screenY = 480;

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
   * How many terminals wide.
   */
  private final int maxTerminalCols = 4;

  /**
   * How many termianl tall.
   */
  private final int maxTerminalRows = 2;

  /**
   * Maximum number of terminals
   */
  private final int maxTerminals = (maxTerminalCols * maxTerminalRows);

  /**
   * Audio clips.
   */
  private AudioClip clips[] = new AudioClip[1];

  /**
   * Up button images.
   */
  private ImageIcon upButtons[] = new ImageIcon[numberButtons];

  /**
   * Down button images.
   */
  private ImageIcon downButtons[] = new ImageIcon[numberButtons];

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
   * Sets the screen size. Used to be dynamic. Should either be removed or
   * rewritten at the point. Shouldn't have much impact.
   */
  public void setScreenSize()
  {
    smallX = (int) (screenX * 0.65);
    smallY = (int) (screenY * 0.65);

    defX = (int) (screenX * 0.85);
    defY = (int) (screenY * 0.92);

    largeX = (int) (screenX * 0.95);
    largeY = (int) (screenY * 0.95);
  }

  /**
   * Initialises the animator layouts based on the existence (now) of a consumer
   * for them.
   */
  public void setupAnimatorLayouts()
  {
    tmAnimator.setupLayout(this);
    psAnimator.setupLayout(this);
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

    for (int count = 0; count < numberPeople; count++)
    {
      tmpURL = RCOS.class.getClassLoader().getSystemResource(rootDir +
        "/images/p" + count + ".jpg");

      aboutImages[count] = new ImageIcon(tmpURL);
    }

    for (int count = 0; count < numberButtons; count++)
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
    return (info);
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
    setScreenSize();
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
    osPostOffice = new OSOffice(osPostOfficeId);

    //Start the animator PostOffice (animator messaging system).
    animatorPostOffice = new AnimatorOffice(animatorPostOfficeId, osPostOffice);

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
    theTerminalManager = new TerminalManager(osPostOffice, maxTerminals);
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
    tmAnimator = new TerminalManagerAnimator(animatorPostOffice, defX,
        defY, terminalImages, maxTerminals, maxTerminalCols, maxTerminalRows);

    psAnimator = new ProcessSchedulerAnimator(animatorPostOffice, defX, defY,
        processImages);

    ipcAnimator = new IPCManagerAnimator(animatorPostOffice, defX, defY,
        ipcImages);

    cpuAnimator = new CPUAnimator(animatorPostOffice, smallX, smallY, null);

    pmAnimator = new ProgramManagerAnimator(animatorPostOffice, smallX, smallY,
        null);

    pcmAnimator = new ProcessManagerAnimator(animatorPostOffice, 250, 250,
        processManagerImages);

    mmAnimator = new MultimediaAnimator(animatorPostOffice, 250, 250,
        processManagerImages, recorder, player);

    aboutAnimator = new AboutAnimator(animatorPostOffice, largeX, largeY,
        aboutImages);
//    overviewanimator = new Overview("Overviewanimator", animatorPostOffice, defX,
//      defY, );
  }

  /**
   * Initialise the screen for the applet representation.
   */
  public void initialiseScreen()
  {
    setBackground(RCOSFrame.defaultBgColour);
    setForeground(RCOSFrame.defaultFgColour);

    JLabel aLabel;

    GraphicButton tempButton;

    mainPanel = new JPanel();

    mainPanel.setBackground(RCOSFrame.defaultBgColour);
    mainPanel.setForeground(RCOSFrame.defaultFgColour);

    systemAnimatorsPanel = new JPanel();

    systemAnimatorsTitlePanel1 = new JPanel();
    systemAnimatorsTitlePanel1.setBackground(RCOSFrame.defaultBgColour);
    systemAnimatorsTitlePanel1.setForeground(RCOSFrame.defaultFgColour);

    systemAnimatorsTitlePanel2 = new JPanel();
    systemAnimatorsTitlePanel2.setBackground(RCOSFrame.defaultBgColour);
    systemAnimatorsTitlePanel2.setForeground(RCOSFrame.defaultFgColour);

    systemInterfacePanel = new JPanel();
    systemInterfaceTitlePanel = new JPanel();

    systemInterfaceTitlePanel1 = new JPanel();
    systemInterfaceTitlePanel1.setBackground(RCOSFrame.defaultBgColour);
    systemInterfaceTitlePanel1.setForeground(RCOSFrame.defaultFgColour);

    systemInterfaceTitlePanel2 = new JPanel();
    systemInterfaceTitlePanel2.setBackground(RCOSFrame.defaultBgColour);
    systemInterfaceTitlePanel2.setForeground(RCOSFrame.defaultFgColour);

    informationPanel = new JPanel();
    informationTitlePanel = new JPanel();
    informationTitlePanel.setBackground(RCOSFrame.defaultBgColour);
    informationTitlePanel.setForeground(RCOSFrame.defaultFgColour);

    getContentPane().setLayout(new BorderLayout());

    NewLabel lAnimators1 = new NewLabel(menu1, RCOSFrame.titleFont);
    NewLabel lAnimators2 = new NewLabel(menu2, RCOSFrame.titleFont);
    NewLabel lInterface1 = new NewLabel(menu3, RCOSFrame.titleFont);
    NewLabel lInterface2 = new NewLabel(menu4, RCOSFrame.titleFont);
    NewLabel lInformation = new NewLabel(menu5, RCOSFrame.titleFont);

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    mainPanel.setLayout(gridBag);

    systemAnimatorsTitlePanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemAnimatorsTitlePanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemAnimatorsTitlePanel1.add(lAnimators1);
    systemAnimatorsTitlePanel2.add(lAnimators2);

    constraints.gridheight = 1;
    constraints.gridwidth = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    gridBag.setConstraints(systemAnimatorsTitlePanel1, constraints);
    mainPanel.add(systemAnimatorsTitlePanel1);

    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "Terminal Manager", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(tmAnimator));

    constraints.gridwidth = GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "Process Scheduler", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(psAnimator));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "IPC Manager", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(ipcAnimator));

    constraints.gridwidth = 1;
    gridBag.setConstraints(systemAnimatorsTitlePanel2, constraints);
    mainPanel.add(systemAnimatorsTitlePanel2);

    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "File System", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth = GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "Disk Scheduler", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(upButtons[0].getImage(),
      downButtons[0].getImage(), "CPU", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(cpuAnimator));

    systemInterfaceTitlePanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemInterfaceTitlePanel1.add(lInterface1);
    systemInterfaceTitlePanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemInterfaceTitlePanel2.add(lInterface2);
    systemInterfaceTitlePanel.setLayout(new GridLayout(2, 1));
    systemInterfaceTitlePanel.add(systemInterfaceTitlePanel1);
    systemInterfaceTitlePanel.add(systemInterfaceTitlePanel2);

    constraints.gridwidth = 1;

    gridBag.setConstraints(systemInterfaceTitlePanel, constraints);
    mainPanel.add(systemInterfaceTitlePanel);

    tempButton = new GraphicButton(upButtons[1].getImage(),
      downButtons[1].getImage(), "New Process", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pmAnimator));

    constraints.gridwidth = GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(upButtons[1].getImage(),
      downButtons[1].getImage(), "Process Manager", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pcmAnimator));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(upButtons[1].getImage(),
      downButtons[1].getImage(), "Multimedia Tour", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(mmAnimator));

    informationTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    informationTitlePanel.add(lInformation);

    constraints.gridwidth = 1;
    constraints.gridheight = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(informationTitlePanel, constraints);
    mainPanel.add(informationTitlePanel);

    tempButton = new GraphicButton(upButtons[2].getImage(),
      downButtons[2].getImage(), "About", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(aboutAnimator));

    constraints.gridwidth = GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(upButtons[2].getImage(),
      downButtons[2].getImage(), "Overview", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(upButtons[2].getImage(),
      downButtons[2].getImage(), "Help", RCOSFrame.defaultFont,
      RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton, constraints);
    mainPanel.add(tempButton);

    setFont(RCOSFrame.defaultFont);
    statusBar = new JTextArea(3, 70);
    statusBar.setEditable(false);
    statusBar.setBackground(RCOSFrame.defaultBgColour);
    statusBar.setForeground(RCOSFrame.defaultFgColour);
    getContentPane().add("South", statusBar);

    getContentPane().add("Center", mainPanel);

    updateStatusBar(welcome);
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
   */
  public void stop()
  {
    theProgramManager.close();
    psAnimator.disposeFrame();
    pmAnimator.disposeFrame();
    tmAnimator.disposeFrame();
    ipcAnimator.disposeFrame();
//    fsAnimator.disposeFrame();
//    dsAnimator.disposeFrame();
    cpuAnimator.disposeFrame();
    aboutAnimator.disposeFrame();
    theTerminalManager.disposeFrame();
    if (kernelThread != null)
    {
      kernelThread = null;
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
