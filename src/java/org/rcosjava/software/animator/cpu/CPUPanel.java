package org.rcosjava.software.animator.cpu;

import java.awt.*;
import javax.swing.*;
import org.rcosjava.hardware.cpu.*;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSPanel;

/**
 * The actual graphical representation of the P-Code CPU.
 * <P>
 * @author Andrew Newman.
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class CPUPanel extends RCOSPanel
{
  /**
   * My peer animator.
   */
  private CPUAnimator cpuAnimator;

  /**
   * Titles for the various sections of the CPU.
   */
  private JLabel cpuTitle, irTitle, pcTitle, spTitle, bpTitle;

  /**
   * Values for the various sections of the CPU.
   */
  private JLabel irValue, pcValue, spValue, bpValue;

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
  private Image images[];

  /**
   * Constructor for the CPUFrame object
   *
   * @param newImages Description of Parameter
   * @param newCPUAnimator Description of Parameter
   */
  public CPUPanel(ImageIcon[] newImages, CPUAnimator newCPUAnimator)
  {
    super();
    if (images != null)
    {
      images = new Image[newImages.length];
      for (int index = 0; index < images.length; index++)
      {
        images[index] = newImages[index].getImage();
      }
    }
    cpuAnimator = newCPUAnimator;
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

    setLayout(new BorderLayout());

    JPanel main = new JPanel();
    main.setBackground(defaultBgColour);
    main.setForeground(defaultFgColour);
    JPanel cpu = new JPanel();
    cpu.setBackground(defaultBgColour);
    cpu.setForeground(defaultFgColour);

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
    tmpLabel.setForeground(defaultFgColour);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    constraints.gridwidth = 2;
    tmpLabel = new JLabel("CPU");
    tmpLabel.setForeground(defaultFgColour);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpLabel = new JLabel("Code");
    tmpLabel.setForeground(defaultFgColour);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setFont(titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    main.add(tmpLabel);

    // Left box
    constraints.gridwidth = 1;
    stackList = new JList();
    stackListModel = new DefaultListModel();
    stackList.setVisibleRowCount(10);
    stackList.setFixedCellWidth(150);
    stackList.setModel(stackListModel);
    stackList.setForeground(defaultFgColour);
    stackList.setBackground(listColour);
    gridBag.setConstraints(stackList, constraints);
    JScrollPane stackListPane = new JScrollPane(stackList);
    stackListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    stackListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    main.add(stackListPane);

    // Middle/CPU section
    cpu.setLayout(new GridLayout(8, 2));
    Font registerFont = new Font("TimesRoman", Font.PLAIN, 14);
    Font registerFontTitle = new Font("TimesRoman", Font.BOLD, 14);

    irTitle = new JLabel("Instruction Register", JLabel.CENTER);
    irTitle.setForeground(defaultFgColour);
    irTitle.setBackground(defaultBgColour);
    irTitle.setFont(registerFontTitle);
    irValue = new JLabel("None", JLabel.CENTER);
    irValue.setForeground(defaultFgColour);
    irValue.setBackground(defaultBgColour);
    irValue.setFont(registerFont);

    pcTitle = new JLabel("Program Counter", JLabel.CENTER);
    pcTitle.setForeground(defaultFgColour);
    pcTitle.setBackground(defaultBgColour);
    pcTitle.setFont(registerFontTitle);
    pcValue = new JLabel("0", JLabel.CENTER);
    pcValue.setForeground(defaultFgColour);
    pcValue.setBackground(defaultBgColour);
    pcValue.setFont(registerFont);

    spTitle = new JLabel("Stack Pointer", JLabel.CENTER);
    spTitle.setForeground(defaultFgColour);
    spTitle.setBackground(defaultBgColour);
    spTitle.setFont(registerFontTitle);
    spValue = new JLabel("0", JLabel.CENTER);
    spValue.setForeground(defaultFgColour);
    spValue.setBackground(defaultBgColour);
    spValue.setFont(registerFont);

    bpTitle = new JLabel("Base Pointer", JLabel.CENTER);
    bpTitle.setForeground(defaultFgColour);
    bpTitle.setBackground(defaultBgColour);
    bpTitle.setFont(registerFontTitle);
    bpValue = new JLabel("0", JLabel.CENTER);
    bpValue.setForeground(defaultFgColour);
    bpValue.setBackground(defaultBgColour);
    bpValue.setFont(registerFont);

    cpu.add(irTitle);
    cpu.add(irValue);

    cpu.add(pcTitle);
    cpu.add(pcValue);

    cpu.add(spTitle);
    cpu.add(spValue);

    cpu.add(bpTitle);
    cpu.add(bpValue);

    constraints.gridwidth = 2;
    gridBag.setConstraints(cpu, constraints);
    main.add(cpu);

    // Right box
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    codeList = new JList();
    codeListModel = new DefaultListModel();
    codeList.setModel(codeListModel);
    codeList.setVisibleRowCount(10);
    codeList.setFixedCellWidth(150);
    codeList.setForeground(defaultFgColour);
    codeList.setBackground(listColour);
    gridBag.setConstraints(codeList, constraints);
    JScrollPane codeListPane = new JScrollPane(codeList);
    codeListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    codeListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    main.add(codeListPane);

    // Add the two panels to the frame.
    add(main, BorderLayout.CENTER);

    screenReset();
  }

  /**
   * Uses the current values in the CPU animator (the context) and sets the
   * values of the pointers and registers.
   */
  void setContext()
  {
    irValue.setText(cpuAnimator.getContext().getInstructionRegister().toString());
    pcValue.setText((new String()).valueOf(cpuAnimator.getContext().
        getProgramCounter()));
    spValue.setText((new String()).valueOf(cpuAnimator.getContext().
        getStackPointer()));
    bpValue.setText((new String()).valueOf(cpuAnimator.getContext().
        getBasePointer()));
  }

  /**
   * Load codeList with new code
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

        Instruction theInstruction = Instruction.INSTRUCTIONS[
          processMemory.getOneMemorySegment(count * 8) & 0xff];
        theInstruction.setByteParameter((byte)
          processMemory.getOneMemorySegment(count * 8 + 4));
        theInstruction.setWordParameter(loc);

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
        int visible = cpuAnimator.getContext().getProgramCounter() + listSize / 2;

        if (codeListModel.size() > cpuAnimator.getContext().getProgramCounter())
        {
          codeList.setSelectedIndex(cpuAnimator.getContext().getProgramCounter());
          codeList.ensureIndexIsVisible(cpuAnimator.getContext().getProgramCounter());
        }
        else
        {
          codeList.ensureIndexIsVisible(codeListModel.size() - 1);
        }

        // make the selected instruction the ProgramCounter
        codeList.setSelectedIndex(cpuAnimator.getContext().getProgramCounter());
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
          codeListModel.addElement("No code");
          stackListModel.addElement("Empty Stack");
        }
        irValue.setText("None");
        pcValue.setText("0");
        spValue.setText("0");
        bpValue.setText("0");
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
          if (cpuAnimator.getContext().getStackPointer() <= 0)
          {
            for (count = 0; count < 5; count++)
            {
              stackListModel.addElement("Empty Stack");
            }
          }
          else
          {
            for (count = cpuAnimator.getContext().getStackPointer(); count != 0; count--)
            {
              stackListModel.addElement((new String()).valueOf(theStack.read(count)));
            }
          }
        }
      }
    });
  }
}
