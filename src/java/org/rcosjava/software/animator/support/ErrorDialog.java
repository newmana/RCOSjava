package org.rcosjava.software.animator.support;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.rcosjava.RCOS;

/**
 * A simple error dialog class.  Creates a model dialog box with the given
 * title and error text.  Uses the default colour scheme of RCOSjava.
 * <P>
 * @author Andrew Newman
 * @created 21st January 2003
 * @version 1.00 $Date$
 */
public class ErrorDialog extends JDialog
{
  /**
   * The string to display at the top of the dialog and to repeat as a title
   * inside the content frame
   */
  private String titleText;

  /**
   * The helpful error message to display.
   */
  private String errorText;

  /**
   * Create a new Error Dialog.
   *
   * @param newTitleText the title of the dialog.  Repeated in the content pane
   *   in larger letters.
   * @param newErrorText the error message.  More description of what went wrong
   *   and possible how to fix it.
   */
  public ErrorDialog(String newTitleText, String newErrorText)
  {
    super(new JFrame(), newTitleText, true);

    titleText = newTitleText;
    errorText = newErrorText;

    getContentPane().setLayout(new BorderLayout(5, 1));
    getContentPane().setBackground(RCOS.DEFAULT_BG_COLOUR);
    getContentPane().setForeground(RCOS.DEFAULT_FG_COLOUR);
    getContentPane().setFont(RCOS.defaultFont);
    setSize(new Dimension(310, 200));

    // Create centered title panel
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    titlePanel.setBackground(RCOS.DEFAULT_BG_COLOUR);
    titlePanel.setForeground(RCOS.DEFAULT_FG_COLOUR);
    JLabel tmpTitle = new JLabel(titleText);
    tmpTitle.setBackground(RCOS.DEFAULT_BG_COLOUR);
    tmpTitle.setForeground(RCOS.DEFAULT_FG_COLOUR);
    tmpTitle.setFont(RCOS.titleFont);
    titlePanel.add(tmpTitle);

    // Create the text pane
    JTextArea text = new JTextArea(errorText);
    text.setBackground(RCOS.DEFAULT_BG_COLOUR);
    text.setForeground(RCOS.DEFAULT_FG_COLOUR);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);

    // Create the OK panel
    JPanel okPanel = new JPanel();
    okPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    okPanel.setBackground(RCOS.DEFAULT_BG_COLOUR);
    okPanel.setForeground(RCOS.DEFAULT_FG_COLOUR);
    JButton tmpOkayButton = new JButton("Ok");

    // Add a mouse listener to close this window.
    tmpOkayButton.addMouseListener(new OkayListener());

    // Add the button to the panel.
    okPanel.add(tmpOkayButton);

    // Blank panels.
    JPanel eastPanel = new JPanel();
    eastPanel.setBackground(RCOS.DEFAULT_BG_COLOUR);
    JPanel westPanel = new JPanel();
    westPanel.setBackground(RCOS.DEFAULT_BG_COLOUR);

    // Add all of the elements to the content pane.
    getContentPane().add(titlePanel, BorderLayout.NORTH);
    getContentPane().add(eastPanel, BorderLayout.EAST);
    getContentPane().add(text, BorderLayout.CENTER);
    getContentPane().add(westPanel, BorderLayout.WEST);
    getContentPane().add(okPanel, BorderLayout.SOUTH);
  }

  /**
   * Simple closes the dialog down when a mouse click is detected.
   *
   * @author Andrew Newman
   * @created 28 April 2003
   */
  private class OkayListener extends MouseAdapter
  {
    /**
     * Mouse is clicked, hide dialog.
     *
     * @param e mouse clicked event.
     */
    public void mouseClicked(MouseEvent e)
    {
      ErrorDialog.this.dispose();
    }
  }
}
