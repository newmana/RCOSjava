//*****************************************************************************/
// FILE     : RCOS.java
// PURPOSE  : Main startup file for RCOS.java Version 1.00
//
//
// AUTHOR   : Andrew Newman and David Jones
// MODIFIED :
// VERSION  : 1.00
// HISTORY  : 21/01/96  Created
//            22/01/96  Will execute any given PCD file that
//                      uses output only, output appears a bit buggy
//            30/03/96  Animators combined with OS
//            01/01/97  Problem with loading images in Netscape fixed.
//                      All files are case sensitive even in
//                      Windows 95/NT.
//            13/10/98  Started converting to Java 1.1.  Version
//                      set to 1.00
//*****************************************************************************/

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;

//RCOS Software Components.
//Animators
import Software.Animator.RCOSFrame;
import Software.Animator.About.AboutFrame;
import Software.Animator.About.AboutAnimator;
import Software.Animator.CPU.CPUFrame;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.Disk.DiskSchedulerAnimator;
import Software.Animator.FileSystem.FileSystemAnimator;
import Software.Animator.Help.Help;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Animator.Multimedia.MultimediaAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProgramManagerAnimator;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.Overview;
import Software.Animator.Terminal.TerminalManagerAnimator;
//OS
import Software.IPC.IPC;
import Software.Kernel.Kernel;
import Software.Memory.MemoryManager;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import Software.Terminal.TerminalManager;
//Messaging System Components.
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.Animator.AnimatorMessageRecorder;
import MessageSystem.PostOffices.OS.OSMessageRecorder;

public class RCOS extends java.applet.Applet implements Runnable
{
  //Messaging constants and objects.

  public final static String sOSPostOfficeID = "OSPOSTOFFICE";
  public final static String sAnimatorPostOfficeID = "ANIMATORPOSTOFFICE";
  private AnimatorOffice animatorOffice;
  private OSOffice postOffice;
  private AnimatorMessageRecorder animatorRecorder;
  private OSMessageRecorder osRecorder;

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
  public final int iScreenX = 640;
  public final int iScreenY = 480;
  private final int MaxTerminalCols = 4;
  private final int MaxTerminalRows = 2;
  private final int MaxTerminals = (MaxTerminalCols * MaxTerminalRows) + 1;
  private static final String sMenu1 = "System";
  private static final String sMenu2 = "Animators";
  private static final String sMenu3 = "System";
  private static final String sMenu4 = "Interface";
  private static final String sMenu5 = "Information";
  public String sBaseDomain;
  public String sDefaultDomain = new String("localhost");
  public int iPort;
  public String sDocBase;
  private static final String sWelcome = "Welcome to RCOS.java Version 1.00";
  private static final String sInfo = "Copyright 1995-2000.\nVersion 1.00.\n" +
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

  private static String sHelpURL;
  private static URL uHelpURL;
  private static URL uDocBase;

  // Animator objects

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

  public Panel pMain,
               pSystemAnimators,
               pSystemAnimatorsTitle1,
               pSystemAnimatorsTitle2,
               pSystemInterface,
               pSystemInterfaceTitle,
               pSystemInterfaceTitle1,
               pSystemInterfaceTitle2,
               pInformation,
               pInformationTitle;

  public void init()
  {
    getParameters();
    getImagesAndSound();
    setScreenSize();
    initialiseOperatingSystem();
    initialiseAnimators();
    initialiseScreen();
  }

  public void getParameters()
  {
    Toolkit tkToolkit = Toolkit.getDefaultToolkit();
    Dimension dScreenSize = tkToolkit.getScreenSize();
    uDocBase = getDocumentBase();
    sDocBase = uDocBase.toString();
    if (sDocBase.indexOf("RCOS.html",0) > 1)
    {
      sDocBase = sDocBase.substring(0,(sDocBase.indexOf("RCOS.html",0)-1));
    }
    sHelpURL = sDocBase + "/Help/index.html";
    try
    {
      sBaseDomain = getParameter("baseDomain");
    }
    catch (Exception e)
    {
      sBaseDomain = sDefaultDomain;
    }
    try
    {
      iPort = (Integer.parseInt(getParameter("port")));
    }
    catch(Exception e)
    {
      iPort = 4242;
    }
  }

  public void getImagesAndSound()
  {
    clips[0] = getAudioClip(uDocBase, "Software/Animator/audio/start.au");

    MediaTracker tracker = new MediaTracker(this);

    for (int count = 0; count < numberPeople; count++)
    {
      imgAbout[count] = getImage(uDocBase, "Software/Animator/images/p" + count
        + ".jpg");
      tracker.addImage(imgAbout[count],0);
    }

    for (int count = 0; count < numberButtons; count++)
    {
      imgUpButtons[count] = getImage(uDocBase, "Software/Animator/images/b" + count
        + "up.jpg");
      imgDownButtons[count] = getImage(uDocBase, "Software/Animator/images/b" + count
        + "down.jpg");
      tracker.addImage(imgUpButtons[count],1);
      tracker.addImage(imgDownButtons[count],2);
    }

    imgTerminal[0] = getImage(uDocBase, "Software/Animator/images/termon.jpg");
    imgTerminal[1] = getImage(uDocBase, "Software/Animator/images/termoff.jpg");
    imgProcess[0] = getImage(uDocBase, "Software/Animator/images/process1.gif");
    imgProcess[1] = getImage(uDocBase, "Software/Animator/images/process2.gif");
    imgProcess[2] = getImage(uDocBase, "Software/Animator/images/rcoscpu.jpg");
    imgIPC[0] = getImage(uDocBase, "Software/Animator/images/memory.jpg");

    tracker.addImage(imgTerminal[0],3);
    tracker.addImage(imgTerminal[1],3);
    tracker.addImage(imgProcess[0],3);
    tracker.addImage(imgProcess[1],3);
    tracker.addImage(imgProcess[2],3);
    tracker.addImage(imgIPC[0],3);

    try
    {
      tracker.waitForAll();
    }
    catch (InterruptedException e)
    {
      System.out.println("Image Loading Failed!");
      updateStatusBar("Images failed to load from " + sDocBase
                       + "Software/Animator/images/");
    }
    imgTerminal[2] = imgUpButtons[1];
    imgTerminal[3] = imgDownButtons[1];
    imgProcessMan[0] = imgUpButtons[1];
    imgProcessMan[1] = imgDownButtons[1];
  }

  public void setScreenSize()
  {
    smallX = (int) (iScreenX * 0.65);
    smallY = (int) (iScreenY * 0.65);

    defX = (int) (iScreenX * 0.85);
    defY = (int) (iScreenY * 0.92);

    largeX = (int) (iScreenX * 0.95);
    largeY = (int) (iScreenY * 0.95);
  }

  public void initialiseOperatingSystem()
  {
    //Start the Post Office (system messaging system).
    //Start the Animator PostOffice (animator messaging system).
    postOffice = new OSOffice(sOSPostOfficeID);

    osRecorder = new OSMessageRecorder("osRecorder", postOffice);

    // Start the Kernel and rest of os.
    theKernel = new Kernel(postOffice);
    theTerminalManager = new TerminalManager(postOffice, MaxTerminals);
    theProcessScheduler = new ProcessScheduler(postOffice);
    theIPC = new IPC(postOffice);
    theMemoryManager = new MemoryManager(postOffice);
    theProgramManager = new ProgramManager(postOffice,
			sBaseDomain, iPort, this);
  }

  public void initialiseAnimators()
  {
    animatorOffice = new AnimatorOffice(sAnimatorPostOfficeID, postOffice);

    animatorRecorder = new AnimatorMessageRecorder("animatorRecorder", animatorOffice);

    tmAnimator = new TerminalManagerAnimator(animatorOffice, defX,
			defY, imgTerminal, MaxTerminals, MaxTerminalCols, MaxTerminalRows);
    psAnimator = new ProcessSchedulerAnimator(animatorOffice, defX, defY,
			imgProcess);
    ipcAnimator = new IPCManagerAnimator(animatorOffice,
      defX, defY, imgIPC);
    cpuAnimator = new CPUAnimator(animatorOffice, smallX,
      smallY, null);
    pmAnimator = new ProgramManagerAnimator(
      animatorOffice, smallX, smallY, null);
    pcmAnimator = new ProcessManagerAnimator(animatorOffice, 250, 250,
			imgProcessMan);
    mmAnimator = new MultimediaAnimator(animatorOffice, smallX, smallY,
			imgProcessMan);
    aboutAnimator = new AboutAnimator(animatorOffice, largeX, largeY,
			imgAbout);
//  Overview
//    overviewAnimator = new Overview("OverviewAnimator", animatorOffice, defX,
//      defY, );
  }

  public void initialiseScreen()
  {
    Label aLabel;

    GraphicButton tempButton;

    pMain = new Panel();
    pSystemAnimators = new Panel();
    pSystemAnimatorsTitle1 = new Panel();
    pSystemAnimatorsTitle2 = new Panel();
    pSystemInterface = new Panel();
    pSystemInterfaceTitle = new Panel();
    pSystemInterfaceTitle1 = new Panel();
    pSystemInterfaceTitle2 = new Panel();
    pInformation = new Panel();
    pInformationTitle = new Panel();

    setBackground(RCOSFrame.cDefaultBgColour);
    setForeground(RCOSFrame.cDefaultFgColour);

    setLayout(new BorderLayout());

    NewLabel lAnimators1 = new NewLabel(sMenu1, RCOSFrame.fTitleFont);
    NewLabel lAnimators2 = new NewLabel(sMenu2, RCOSFrame.fTitleFont);
    NewLabel lInterface1 = new NewLabel(sMenu3, RCOSFrame.fTitleFont);
    NewLabel lInterface2 = new NewLabel(sMenu4, RCOSFrame.fTitleFont);
    NewLabel lInformation = new NewLabel(sMenu5, RCOSFrame.fTitleFont);

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);

    pSystemAnimatorsTitle1.setLayout(new FlowLayout(FlowLayout.CENTER));
    pSystemAnimatorsTitle2.setLayout(new FlowLayout(FlowLayout.CENTER));
    pSystemAnimatorsTitle1.add(lAnimators1);
    pSystemAnimatorsTitle2.add(lAnimators2);

    constraints.gridheight=1;
    constraints.gridwidth=1;
    constraints.weighty=1;
    constraints.weightx=1;

    gridBag.setConstraints(pSystemAnimatorsTitle1,constraints);
    pMain.add(pSystemAnimatorsTitle1);

    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Terminal Manager", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(tmAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Process Scheduler", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(psAnimator));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "IPC Manager", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(ipcAnimator));

    constraints.gridwidth=1;
    gridBag.setConstraints(pSystemAnimatorsTitle2,constraints);
    pMain.add(pSystemAnimatorsTitle2);

    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "File System", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "Disk Scheduler", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    //tempButton.addMouseListener(new ShowAnimator(null));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[0], imgDownButtons[0],
      "CPU", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(cpuAnimator));

    pSystemInterfaceTitle1.setLayout(new FlowLayout(FlowLayout.CENTER));
    pSystemInterfaceTitle1.add(lInterface1);
    pSystemInterfaceTitle2.setLayout(new FlowLayout(FlowLayout.CENTER));
    pSystemInterfaceTitle2.add(lInterface2);
    pSystemInterfaceTitle.setLayout(new GridLayout(2,1));
    pSystemInterfaceTitle.add(pSystemInterfaceTitle1);
    pSystemInterfaceTitle.add(pSystemInterfaceTitle2);

    constraints.gridwidth=1;

    gridBag.setConstraints(pSystemInterfaceTitle,constraints);
    pMain.add(pSystemInterfaceTitle);

    tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "New Process", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pmAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "Process Manager", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(pcmAnimator));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
      tempButton = new GraphicButton(imgUpButtons[1], imgDownButtons[1],
      "Multimedia Tour", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(mmAnimator));

    pInformationTitle.setLayout(new FlowLayout(FlowLayout.CENTER));
    pInformationTitle.add(lInformation);

    constraints.gridwidth=1;
    constraints.gridheight=GridBagConstraints.REMAINDER;
    gridBag.setConstraints(pInformationTitle,constraints);
    pMain.add(pInformationTitle);

    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "About", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);
    tempButton.addMouseListener(new ShowAnimator(aboutAnimator));

    constraints.gridwidth=GridBagConstraints.RELATIVE;
    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "Overview", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    tempButton = new GraphicButton(imgUpButtons[2], imgDownButtons[2],
      "Help", RCOSFrame.fDefaultFont, RCOSFrame.cButtonColour, true);
    gridBag.setConstraints(tempButton,constraints);
    pMain.add(tempButton);

    setFont(RCOSFrame.fDefaultFont);
    tsStatusBar = new TextArea(3,70);
    tsStatusBar.setEditable(false);
    tsStatusBar.setBackground(RCOSFrame.cDefaultBgColour);
    add("South",tsStatusBar);

    add("Center",pMain);

    updateStatusBar(sWelcome);
//    clips[0].play();
  }

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
    return (sInfo);
  }

  public synchronized void startThread()
  {
    if (!running)
    {
      running = true;
      notify();
    }
  }

  public synchronized void stepThread()
  {
    running = false;
    notify();
  }

  public void run()
  {
    while (kernelThread != null)
    {
      try
      {
        synchronized (this)
        {
          while (!running)
          {
            wait();
          }
        }
        theKernel.performInstructionExecutionCycle();
        kernelThread.sleep(100);
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
