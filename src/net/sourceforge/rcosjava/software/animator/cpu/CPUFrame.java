package net.sourceforge.rcosjava.software.animator.cpu;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.hardware.cpu.*;
import net.sourceforge.rcosjava.hardware.memory.Memory;

/**
 * The actual graphical representation of the P-Code CPU.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/01/97  Move to Animator.
 * </DD><DD>
 * 03/01/97  Changed Panel types to GridBagLayout.
 * </DD><DD>
 * 04/01/97  Got BasePointer and Code to work again.
 * </DD><DD>
 * 13/10/98  Converted to Java 1.1.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 3rd February 1996
 */
public class CPUFrame extends RCOSFrame
{
  private CPUAnimator myCPUAnimator;
  private NewLabel CPUtitle, IRtitle, PCtitle, SPtitle, BPtitle;
  private NewLabel IRvalue, PCvalue, SPvalue, BPvalue;
  private List stackList, codeList;
  private Image myImages[];

  public CPUFrame(int x, int y, Image[] cpuImages, CPUAnimator thisCPUAnimator)
  {
    super();
    setTitle("CPU Animator");
    myImages = cpuImages;
    myCPUAnimator = thisCPUAnimator;
  }

  /**
   * Called by the animator message to display this box when the users clicks
   * on the CPU in the process scheduler frame.
   */
  void showCPU()
  {
    this.setVisible(true);
    this.toFront();
  }

  /**
   * Sets up the layout of the frame.  It creates two text boxes either side
   * of the registers and pointers.  The left box represents the stack and the
   * right box represents the instructions.
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);
    setBackground(Color.black);
    setForeground(Color.white);

    setLayout(new BorderLayout());

    Panel pMain = new Panel();
    Panel pCPU = new Panel();
    Panel pClose = new Panel();
    NewLabel lTmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets=new Insets(3,3,3,3);
    constraints.anchor = GridBagConstraints.CENTER;

    // Headings
    lTmpLabel = new NewLabel("Stack", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth = 2;
    lTmpLabel = new NewLabel("CPU", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("Code", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    // Left box
    constraints.gridwidth = 1;
    stackList = new List(10,false);
    stackList.setBackground(listColour);
    gridBag.setConstraints(stackList,constraints);
    pMain.add(stackList);

    // Middle/CPU section
    pCPU.setLayout(new GridLayout(8,2));

    titleFont = new Font ("TimesRoman", Font.BOLD, 14);

    IRtitle = new NewLabel("Instruction Register",titleFont);
    IRvalue = new NewLabel("None",defaultFont);

    PCtitle = new NewLabel("Program Counter",titleFont);
    PCvalue = new NewLabel("0",defaultFont);

    SPtitle = new NewLabel("Stack Pointer",titleFont);
    SPvalue = new NewLabel("0",defaultFont);

    BPtitle = new NewLabel("Base Pointer",titleFont);
    BPvalue = new NewLabel("0",defaultFont);

    pCPU.add(IRtitle);
    pCPU.add(IRvalue);

    pCPU.add(PCtitle);
    pCPU.add(PCvalue);

    pCPU.add(SPtitle);
    pCPU.add(SPvalue);

    pCPU.add(BPtitle);
    pCPU.add(BPvalue);

    constraints.gridwidth = 2;
    gridBag.setConstraints(pCPU,constraints);
    pMain.add(pCPU);

    // Right box
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    codeList = new List(10,false);
    codeList.setBackground(listColour);
    gridBag.setConstraints(codeList,constraints);
    pMain.add(codeList);

    // Close section
    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    // Add the two panels to the frame.
    add("Center", pMain);
    add("South", pClose);
  }

  /**
   * Uses the current values in the CPU animator (the context) and sets the
   * values of the pointers and registers.
   */
  void setContext()
  {
    IRvalue.setText(myCPUAnimator.getContext().getInstructionRegister().toString());
    PCvalue.setText((new String()).valueOf(myCPUAnimator.getContext().
      getProgramCounter()));
    SPvalue.setText((new String()).valueOf(myCPUAnimator.getContext().
      getStackPointer()));
    BPvalue.setText((new String()).valueOf(myCPUAnimator.getContext().
      getBasePointer()));
  }

  /**
   * load codeList with new code
   */
  void loadCode(Memory processMemory)
  {
    int count, linesOfCode;

    if (codeList.getItemCount() != 0)
      codeList.removeAll();

    // test to see if the memory is not null
    if (processMemory != null)
    {
      // Each instruction is 8 bytes long.

      linesOfCode = (int) processMemory.getSegmentSize() / 8;

      for (count=0; count < linesOfCode; count++)
      {
        short instr1 = (short) processMemory.getOneMemorySegment(count*8+5);
        short instr2 = (short) processMemory.getOneMemorySegment(count*8+6);

        Instruction theInstruction = new Instruction(
          (processMemory.getOneMemorySegment(count*8) & 0xff),
          ((byte) processMemory.getOneMemorySegment(count*8+4)),
          ((short) ((instr1 & 255 << 8) + instr2 & 255)));
        codeList.add(theInstruction.toString());
      }
      // make the selected instruction the ProgramCounter
      codeList.select(0);
    }
  }

  /**
   * Make program counter instruction be in the middle of the code list
   */
  void updateCode()
  {
    int count;
    int programSize = codeList.getItemCount();
    int listSize = codeList.getRows();
    int visible = myCPUAnimator.getContext().getProgramCounter() + listSize/2;

    //int listSize = codeList.getRows();
    //getRows doesn't seem to be reliable

    if (programSize > myCPUAnimator.getContext().getProgramCounter())
    {
      codeList.makeVisible(myCPUAnimator.getContext().getProgramCounter());
    }
    else
    {
      codeList.makeVisible(programSize-1);
    }
    // make the selected instruction the ProgramCounter
    codeList.select(myCPUAnimator.getContext().getProgramCounter());
  }

  /**
   * Resets the stack, pointers, resgisters, etc to zero values.
   */
  void screenReset()
  {
    codeList.removeAll();
    stackList.removeAll();
    for (int count=0;count<5;count++)
    {
      stackList.add("Empty Stack");
    }
    IRvalue.setText("None");
    PCvalue.setText("0");
    SPvalue.setText("0");
    BPvalue.setText("0");
  }

  /**
   * Modify the stack list so that it represents the current state of the stack.
   *
   * @param theStack the new value of the stack to replace the current one with.
   */
  void updateStack(Memory theStack)
  {
    int count;

    stackList.removeAll();

    if (theStack != null)
    {
      if (myCPUAnimator.getContext().getStackPointer() <= 0)
      {
        for (count=0;count<5;count++)
        {
          stackList.add("Empty Stack");
        }
      }
      else
      {
        for (count=myCPUAnimator.getContext().getStackPointer();count != 0;count--)
        {
          stackList.add((new String()).valueOf(theStack.read(count)));
        }
      }
    }
  }
}
