package org.rcosjava.software.animator.cpu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.*;
import org.rcosjava.hardware.cpu.*;
import org.rcosjava.hardware.cpu.Instruction;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.NewLabel;

/**
 * The actual graphical representation of the P-Code CPU.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/01/97 Move to Animator. </DD>
 * <DD> 03/01/97 Changed Panel types to GridBagLayout. </DD>
 * <DD> 04/01/97 Got BasePointer and Code to work again. </DD>
 * <DD> 13/10/98 Converted to Java 1.1. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 3rd February 1996
 * @version 1.00 $Date$
 */
public class CPUFrame extends RCOSFrame
{
  /**
   * My peer animator.
   */
  private CPUAnimator myCPUAnimator;

  /**
   * Titles for the various sections of the CPU.
   */
  private JLabel CPUtitle, IRtitle, PCtitle, SPtitle, BPtitle;

  /**
   * Values for the various sections of the CPU.
   */
  private JLabel IRvalue, PCvalue, SPvalue, BPvalue;

  /**
   * The process stack.
   */
  private JList stackList;

  /**
   * List model for the stack.
   */
  private DefaultListModel stackListModel;

  /**
   * The code stack.
   */
  private JList codeList;

  /**
   * List model for the code.
   */
  private DefaultListModel codeListModel;

  /**
   * Images used by the frame.
   */
  private Image myImages[];

  /**
   * Constructor for the CPUFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param cpuImages Description of Parameter
   * @param thisCPUAnimator Description of Parameter
   */
  public CPUFrame(int x, int y, ImageIcon[] images, CPUAnimator thisCPUAnimator)
  {
    super();
    setTitle("CPU Animator");
    if (images != null)
    {
      myImages = new Image[images.length];
      for (int index = 0; index < images.length; index++)
      {
        myImages[index] = images[index].getImage();
      }
    }
    myCPUAnimator = thisCPUAnimator;
  }

  /**
   * Sets up the layout of the frame. It creates two text boxes either side of
   * the registers and pointers. The left box represents the stack and the right
   * box represents the instructions.
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);
    setBackground(Color.black);
    setForeground(Color.white);

    setLayout(new BorderLayout());

    JPanel main = new JPanel();
    main.setBackground(Color.black);
    main.setForeground(Color.white);
    JPanel cpu = new JPanel();
    cpu.setBackground(Color.black);
    cpu.setForeground(Color.white);
    JPanel close = new JPanel();
    close.setBackground(Color.black);
    close.setForeground(Color.white);

    JLabel tmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    main.setLayout(gridBag);
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(3, 3, 3, 3);
    constraints.anchor = GridBagConstraints.CENTER;

    // Headings
    tmpLabel = new JLabel("Stack");
    tmpLabel.setForeground(Color.white);
    tmpLabel.setBackground(Color.black);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    constraints.gridwidth = 2;
    tmpLabel = new JLabel("CPU");
    tmpLabel.setForeground(Color.white);
    tmpLabel.setBackground(Color.black);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpLabel = new JLabel("Code");
    tmpLabel.setForeground(Color.white);
    tmpLabel.setBackground(Color.black);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    // Left box
    constraints.gridwidth = 1;
    stackList = new JList();
    stackListModel = new DefaultListModel();
    stackList.setVisibleRowCount(8);
    stackList.setModel(stackListModel);
    stackList.setForeground(Color.white);
    stackList.setBackground(Color.darkGray);
    gridBag.setConstraints(stackList, constraints);
    JScrollPane stackListPane = new JScrollPane(stackList);
    stackListPane.setMinimumSize(new Dimension(100, 200));
    main.add(stackListPane);

    // Middle/CPU section
    cpu.setLayout(new GridLayout(8, 2));

    titleFont = new Font("TimesRoman", Font.BOLD, 14);

    IRtitle = new JLabel("Instruction Register", JLabel.CENTER);
    IRtitle.setForeground(Color.white);
    IRtitle.setBackground(Color.black);
    IRtitle.setFont(titleFont);
    IRvalue = new JLabel("None", JLabel.CENTER);
    IRvalue.setForeground(Color.white);
    IRvalue.setBackground(Color.black);
    IRvalue.setFont(defaultFont);

    PCtitle = new JLabel("Program Counter", JLabel.CENTER);
    PCtitle.setForeground(Color.white);
    PCtitle.setBackground(Color.black);
    PCtitle.setFont(titleFont);
    PCvalue = new JLabel("0", JLabel.CENTER);
    PCvalue.setForeground(Color.white);
    PCvalue.setBackground(Color.black);
    PCvalue.setFont(defaultFont);

    SPtitle = new JLabel("Stack Pointer", JLabel.CENTER);
    SPtitle.setForeground(Color.white);
    SPtitle.setBackground(Color.black);
    SPtitle.setFont(titleFont);
    SPvalue = new JLabel("0", JLabel.CENTER);
    SPvalue.setForeground(Color.white);
    SPvalue.setBackground(Color.black);
    SPvalue.setFont(defaultFont);

    BPtitle = new JLabel("Base Pointer", JLabel.CENTER);
    BPtitle.setForeground(Color.white);
    BPtitle.setBackground(Color.black);
    BPtitle.setFont(titleFont);
    BPvalue = new JLabel("0", JLabel.CENTER);
    BPvalue.setForeground(Color.white);
    BPvalue.setBackground(Color.black);
    BPvalue.setFont(defaultFont);

    cpu.add(IRtitle);
    cpu.add(IRvalue);

    cpu.add(PCtitle);
    cpu.add(PCvalue);

    cpu.add(SPtitle);
    cpu.add(SPvalue);

    cpu.add(BPtitle);
    cpu.add(BPvalue);

    constraints.gridwidth = 2;
    gridBag.setConstraints(cpu, constraints);
    main.add(cpu);

    // Right box
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    codeList = new JList();
    codeListModel = new DefaultListModel();
    codeList.setModel(codeListModel);
    codeList.setVisibleRowCount(8);
    codeList.setForeground(Color.white);
    codeList.setBackground(Color.darkGray);
    gridBag.setConstraints(codeList, constraints);
    JScrollPane codeListPane = new JScrollPane(codeList);
    codeListPane.setMinimumSize(new Dimension(100, 200));
    main.add(codeListPane);

    // Close section
    close.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new JButton("Close");
    close.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    // Add the two panels to the frame.
    add("Center", main);
    add("South", close);

    screenReset();
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
   * Called by the animator message to display this box when the users clicks on
   * the CPU in the process scheduler frame.
   */
  void showCPU()
  {
    this.setVisible(true);
    this.toFront();
  }

  /**
   * load codeList with new code
   *
   * @param processMemory Description of Parameter
   */
  void loadCode(Memory processMemory)
  {
    int count;
    int linesOfCode;

    if (codeListModel.size() != 0)
    {
      codeListModel.removeAllElements();
    }

    // test to see if the memory is not null
    if (processMemory != null)
    {
      // Each instruction is 8 bytes long.

      linesOfCode = (int) processMemory.getSegmentSize() / 8;

      for (count = 0; count < linesOfCode; count++)
      {
        short instr1 = (short) processMemory.getOneMemorySegment(count * 8 + 5);
        short instr2 = (short) processMemory.getOneMemorySegment(count * 8 + 6);
        short loc = (short) ((256 * (instr1 & 255)) + (instr2 & 255));

        Instruction theInstruction = new Instruction(
            (processMemory.getOneMemorySegment(count * 8) & 0xff),
            ((byte) processMemory.getOneMemorySegment(count * 8 + 4)), loc);

        codeListModel.addElement(theInstruction.toString());
      }
      // make the selected instruction the ProgramCounter
      codeList.setSelectedIndex(0);
    }
  }

  /**
   * Make program counter instruction be in the middle of the code list
   */
  void updateCode()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        int count;
        int listSize = codeList.getVisibleRowCount();
        int visible = myCPUAnimator.getContext().getProgramCounter() + listSize / 2;

        //int listSize = codeList.getRows();
        //getRows doesn't seem to be reliable

        if (codeListModel.size() > myCPUAnimator.getContext().getProgramCounter())
        {
//      codeList.makeVisible(myCPUAnimator.getContext().getProgramCounter());
          codeList.setSelectedIndex(myCPUAnimator.getContext().getProgramCounter());
          codeList.ensureIndexIsVisible(myCPUAnimator.getContext().getProgramCounter());
        }
        else
        {
//      codeList.makeVisible(programSize - 1);
//      codeList.setSelectedIndex(programSize - 1);
          codeList.ensureIndexIsVisible(codeListModel.size() - 1);
        }
        // make the selected instruction the ProgramCounter
//    codeList.select(myCPUAnimator.getContext().getProgramCounter());
        codeList.setSelectedIndex(myCPUAnimator.getContext().getProgramCounter());
      }
    });
  }

  /**
   * Resets the stack, pointers, resgisters, etc to zero values.
   */
  void screenReset()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        codeListModel.removeAllElements();
        stackListModel.removeAllElements();

        for (int count = 0; count < 5; count++)
        {
          stackListModel.addElement("Empty Stack");
        }
        IRvalue.setText("None");
        PCvalue.setText("0");
        SPvalue.setText("0");
        BPvalue.setText("0");
      }
    });
  }

  /**
   * Modify the stack list so that it represents the current state of the stack.
   *
   * @param theStack the new value of the stack to replace the current one with.
   */
  void updateStack(final Memory theStack)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        int count;

        stackListModel.removeAllElements();

        if (theStack != null)
        {
          if (myCPUAnimator.getContext().getStackPointer() <= 0)
          {
            for (count = 0; count < 5; count++)
            {
              stackListModel.addElement("Empty Stack");
            }
          }
          else
          {
            for (count = myCPUAnimator.getContext().getStackPointer(); count != 0; count--)
            {
              stackListModel.addElement((new String()).valueOf(theStack.read(count)));
            }
          }
        }
      }
    });
  }
}
