package net.sourceforge.rcosjava;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;

//RCOS Software Components.
//animators
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.about.AboutFrame;
import net.sourceforge.rcosjava.software.animator.about.AboutAnimator;
import net.sourceforge.rcosjava.software.animator.cpu.CPUFrame;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.filesystem.FileSystemAnimator;
import net.sourceforge.rcosjava.software.animator.help.Help;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.animator.multimedia.MultimediaAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProgramManagerAnimator;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.Overview;
import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
//OS
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
//Messaging System Components.
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;

/**
 * Main startup file for RCOSjava Version 1.00
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 22/01/96  Will execute any given PCD file that uses output only, output
 * appears a bit buggy
 * </DD><DD>
 * 30/03/96  animators combined with OS<BR>
 * </DD><DD>
 * 01/01/97  Problem with loading images in Netscape fixed. All files are case
 * sensitive even in Windows 95/NT.
 * </DD><DD>
 * 13/10/98  Started converting to Java 1.1.  Version set to 1.00
 * </DD></DT>
 *
 * @author Andrew Newman
 * @created 21st January 1996
 * @version 1.00 $Date$
 */
public class RCOS extends java.applet.Applet implements Runnable
{
  //Messaging constants and objects.
  public final static String osPostOfficeId = "OSPOSTOFFICE";
  public final static String animatorPostOfficeId = "ANIMATORPOSTOFFICE";
  private AnimatorOffice animatorPostOffice;
  private OSOffice osPostOffice;

  // Recorder object.
  private UniversalMessageRecorder recorder;
  private UniversalMessagePlayer player;

  //OS Objects.
  private TerminalManager theTerminalManager;
  private Kernel theKernel;
  private MemoryManager theMemoryManager;
  private ProgramManager theProgramManager;
  private ProcessScheduler theProcessScheduler;
  private IPC theIPC;

  // Main thread.
  private Thread kernelThread;
  private volatile boolean running = false;

  //Defaults, screen size, location, authors.
  public int smallX, smallY;
  public int defX, defY;
  public int largeX, largeY;
  public final int screenX = 640;
  public final int screenY = 480;
  private final int maxTerminalCols = 4;
  private final int maxTerminalRows = 2;
  private final int maxTerminals = (maxTerminalCols * maxTerminalRows);
  private static final String menu1 = "System";
  private static final String menu2 = "Animators";
  private static final String menu3 = "System";
  private static final String menu4 = "Interface";
  private static final String menu5 = "Information";
  public String baseDomain;
  public String defaultDomain = new String("localhost");
  public int port;
  public String docBase;
  private static final String welcome = "Welcome to RCOSjava Version 1.00";
  private static final String info = "Copyright 1995-2001.\nVersion 1.00.\n" +
    "Authors: David Jones, Brett Carter, Bruce Jamieson, and Andrew Newman";

  // Images and sounds.
  public final static int numberPeople = 4;
  public final static int numberButtons = 3;
  private AudioClip clips[] = new AudioClip[1];
  private Image imgUpButtons[] = new Image[numberButtons];
  private Image imgDownButtons[] = new Image[numberButtons];
  private Image imgTerminal[] = new Image[4];
  private Image imgProcess[] = new Image[4];
  private Image imgProcessMan[] = new Image[2];
  private Image imgAbout[] = new Image[4];
  private Image imgIPC[] = new Image[1];

  // Help Variables.
  private static String helpURLStr;
  private static URL helpURL;
  private static URL docBaseURL;

  // animator objects
  private static ProcessSchedulerAnimator psAnimator;
  private static TerminalManagerAnimator tmAnimator;
  private static FileSystemAnimator fsAnimator;
  private static DiskSchedulerAnimator dsAnimator;
  private static CPUAnimator cpuAnimator;
  private static IPCManagerAnimator ipcAnimator;
  private static MultimediaAnimator mmAnimator;
  private static Overview overviewAnimator;
  private static AboutAnimator aboutAnimator;
  private static ProgramManagerAnimator pmAnimator;
  private static ProcessManagerAnimator pcmAnimator;
  private static TextArea tsStatusBar;

  // Main panels
  public Panel mainPanel, systemAnimatorsPanel, systemAnimatorsTitlePanel1,
    systemAnimatorsTitlePanel2, systemInterfacePanel, systemInterfaceTitlePanel,
    systemInterfaceTitlePanel1, systemInterfaceTitlePanel2, informationPanel,
    informationTitlePanel;

  /**
   * Called by the init of the applet.  Calls getParameters, getImagesAndSounds,
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
   * Used to take parameters from the HTML.  Currently uses baseDomain or
   * uses localhost (used to take host as a parameter).  Still accepts the
   * port.  The default is 4242.
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
       docBaseURL = new URL("http://" + baseDomain);
       docBase = docBaseURL.toString();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    try
    {
      port = (Integer.parseInt(getParameter("port")));
    }
    catch(java.lang.NumberFormatException e)
    {
      port = 4242;
    }
    helpURLStr = docBase + "/Help/index.html";
  }

  /**
   * Loads all the images and sounds required for use by the animators and
   * the default panel.
   */
  public void getImagesAndSound()
  {
    String rootDir = "net/sourceforge/rcosjava/software/animator";

    clips[0] = getAudioClip(docBaseURL, rootDir + "/audio/start.au");

    MediaTracker tracker = new MediaTracker(this);

    for (int count = 0; count < numberPeople; count++)
    {
      imgAbout[count] = getImage(docBaseURL, rootDir + "/images/p" +
        count + ".jpg");
      tracker.addImage(imgAbout[count],0);
    }

    for (int count = 0; count < numberButtons; count++)
    {
      imgUpButtons[count] = getImage(docBaseURL, rootDir + "/images/b"
        + count + "up.jpg");
      imgDownButtons[count] = getImage(docBaseURL, rootDir + "/images/b"
        + count + "down.jpg");
      tracker.addImage(imgUpButtons[count],1);
      tracker.addImage(imgDownButtons[count],2);
    }

    imgTerminal[0] = getImage(docBaseURL, rootDir + "/images/termon.jpg");
    imgTerminal[1] = getImage(docBaseURL, rootDir + "/images/termoff.jpg");
    imgProcess[0] = getImage(docBaseURL, rootDir + "/images/process1.gif");
    imgProcess[1] = getImage(docBaseURL, rootDir + "/images/process2.gif");
    imgProcess[2] = getImage(docBaseURL, rootDir + "/images/rcoscpu.jpg");
    imgIPC[0] = getImage(docBaseURL, rootDir + "/images/memory.jpg");

    tracker.addImage(imgTerminal[0],3);
    tracker.addImage(imgTerminal[1],3);
    tracker.addImage(imgProcess[0],3);
    tracker.addImage(imgProcess[1],3);
    tracker.addImage(imgProcess[2],3);
    tracker.addImage(imgIPC[0],3);

    try
    {
      tracker.waitForAll(1);
    }
    catch (InterruptedException e)
    {
      System.out.println("Image Loading Failed!");
      updateStatusBar("Images failed to load from " + docBase +
        rootDir + "/images/");
    }
    imgTerminal[2] = imgUpButtons[1];
    imgTerminal[3] = imgDownButtons[1];
    imgProcessMan[0] = imgUpButtons[1];
    imgProcessMan[1] = imgDownButtons[1];
  }

  /**
   * Sets the screen size.  Used to be dynamic.  Should either be removed or
   * rewritten at the point.  Shouldn't have much impact.
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
   * Initialise the message system.  This includes the operating system and
   * animators post offices as well as the recording of all messages
   * passed between each sub-system.
   */
  public void initialiseMessaging()
  {
    //Start the Post Office (system messaging system).
    osPostOffice = new OSOffice(osPostOfficeId);

    //Start the animator PostOffice (animator messaging system).
    animatorPostOffice = new AnimatorOffice(animatorPostOfficeId, osPostOffice);

    //Start the recording subsystem
    recorder = new UniversalMessageRecorder(this.defaultDomain, this.port,
      "*Recorder", this.osPostOffice, this.animatorPostOffice);

    player = new UniversalMessagePlayer(this.defaultDomain, this.port,
      "*Player", this.osPostOffice, this.animatorPostOffice);
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
      this);
  }

  /**
   * Initialise all of the animator components.
   */
  public void initialiseAnimators()
  {
    tmAnimator = new TerminalManagerAnimator(animatorPostOffice, defX,
      defY, imgTerminal, maxTerminals, maxTerminalCols, maxTerminalRows);

    psAnimator = new ProcessSchedulerAnimator(animatorPostOffice, defX, defY,
      imgProcess);

    ipcAnimator = new IPCManagerAnimator(animatorPostOffice, defX, defY, imgIPC);

    cpuAnimator = new CPUAnimator(animatorPostOffice, smallX, smallY, null);

    pmAnimator = new ProgramManagerAnimator(animatorPostOffice, smallX, smallY,
      null);

    pcmAnimator = new ProcessManagerAnimator(animatorPostOffice, 250, 250,
      imgProcessMan);

    mmAnimator = new MultimediaAnimator(animatorPostOffice, 250, 250,
      imgProcessMan, recorder, player);

    aboutAnimator = new AboutAnimator(animatorPostOffice, largeX, largeY, imgAbout);
//    overviewanimator = new Overview("Overviewanimator", animatorPostOffice, defX,
//      defY, );
  }

  /**
   * Initialise the screen for the applet representation.
   */
  public void initialiseScreen()
  {
    Label aLabel;

    GraphicButton tempButton;

    mainPanel = new Panel();
    systemAnimatorsPanel = new Panel();
    systemAnimatorsTitlePanel1 = new Panel();
    systemAnimatorsTitlePanel2 = new Panel();
    systemInterfacePanel = new Panel();
    systemInterfaceTitlePanel = new Panel();
    systemInterfaceTitlePanel1 = new Panel();
    systemInterfaceTitlePanel2 = new Panel();
    informationPanel = new Panel();
    informationTitlePanel = new Panel();

    setBackground(RCOSFrame.defaultBgColour);
    setForeground(RCOSFrame.defaultFgColour);

    setLayout(new BorderLayout());

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

    constraints.gridheight=1;
    constraints.gridwidth=1;
    constraints.weighty=1;
    constraints.weightx=1;

    gridBag.setConstraints(systemAnimatorsTitlePanel1,constraints);
    mainPanel.add(systemAnimatorsTitlePanel1);

    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Terminal Manager", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(tmAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Process Scheduler", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(psAnimator));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "IPC Manager", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(ipcAnimator));

    constraints.gridwidth=1;
    gridBag.setConstraints(systemAnimatorsTitlePanel2,constraints);
    mainPanel.add(systemAnimatorsTitlePanel2);

    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "File System", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Disk Scheduler", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "CPU", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(cpuAnimator));

    systemInterfaceTitlePanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemInterfaceTitlePanel1.add(lInterface1);
    systemInterfaceTitlePanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    systemInterfaceTitlePanel2.add(lInterface2);
    systemInterfaceTitlePanel.setLayout(new GridLayout(2,1));
    systemInterfaceTitlePanel.add(systemInterfaceTitlePanel1);
    systemInterfaceTitlePanel.add(systemInterfaceTitlePanel2);

    constraints.gridwidth=1;

    gridBag.setConstraints(systemInterfaceTitlePanel,constraints);
    mainPanel.add(systemInterfaceTitlePanel);

    tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "New Process", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pmAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "Process Manager", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pcmAnimator));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
      tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "Multimedia Tour", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(mmAnimator));

    informationTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    informationTitlePanel.add(lInformation);

    constraints.gridwidth=1;
    constraints.gridheight=GridBagConstraints.REMAINDER;
    gridBag.setConstraints(informationTitlePanel,constraints);
    mainPanel.add(informationTitlePanel);

    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "About", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(aboutAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "Overview", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "Help", RCOSFrame.defaultFont, RCOSFrame.buttonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    mainPanel.add(tempButton);

    setFont(RCOSFrame.defaultFont);
    tsStatusBar = new TextArea(3,70);
    tsStatusBar.setEditable(false);
    tsStatusBar.setBackground(RCOSFrame.defaultBgColour);
    add("South",tsStatusBar);

    add("Center",mainPanel);

    updateStatusBar(welcome);
//    clips[0].play();
  }

  /**
   * Start the operating system
   */
  public void start()
  {
    setupAnimatorLayouts();
    if (kernelThread == null)
    {
      kernelThread = new Thread(this, "RCOS");
      kernelThread.setPriority(kernelThread.MIN_PRIORITY);
      kernelThread.start();
    }
  }

  /**
   * Initialises the animator layouts based on the existence (now) of a
   * consumer for them.
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

  public String getAppletInfo()
  {
    return (info);
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
   * Stops the running of the thread a calls notify.
   */
  public synchronized void stepThread()
  {
    running = false;
    notify();
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
            this.wait();
        }
        theKernel.performInstructionExecutionCycle();
        kernelThread.sleep(50);
      }
      catch (InterruptedException exc)
      {
      }
    }
  }

  public static void updateStatusBar (String statusBar)
  {
      tsStatusBar.insert((statusBar.trim()) + "\n",0);
  }

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

  class ShowAnimator extends MouseAdapter
  {
    private Object oParent;

    public ShowAnimator(Object parent)
    {
      this.oParent = parent;
    }

    public void mouseClicked(MouseEvent me)
    {
      Class cAnimator = oParent.getClass();
      Method mShow = null;

      try
      {
        mShow = cAnimator.getMethod("showFrame",null);
        mShow.invoke(oParent,null);
      }
      catch (Exception e)
      {
        System.out.println(this + "- exception: " + e);
      }
    }
  }
}
