// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import micropolisj.engine.*;
import static micropolisj.gui.MainWindow.formatFunds;
import static micropolisj.gui.MainWindow.formatGameDate;

public class BondDialog extends JDialog
{
	Micropolis engine;

	static ResourceBundle strings = MainWindow.strings;

	private void applyChange()
	{

		loadBudgetNumbers(false);
	}

	private void loadBudgetNumbers(boolean updateEntries)
	{
		BudgetNumbers b = engine.generateBudget();
		
	}

	static void adjustSliderSize(JSlider slider)
	{
		Dimension sz = slider.getPreferredSize();
		slider.setPreferredSize(
			new Dimension(80, sz.height)
			);
	}

	public BondDialog(Window owner, Micropolis engine)
	{
		super(owner);
		setTitle(strings.getString("bonddlg.title"));

		this.engine = engine;
		
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		add(mainBox, BorderLayout.CENTER);

		mainBox.add(makeCurrentBondPane());

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		mainBox.add(sep);

		mainBox.add(makeNewBondPane());

		setAutoRequestFocus_compat(false);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private void setAutoRequestFocus_compat(boolean v)
	{
		try
		{
			if (super.getClass().getMethod("setAutoRequestFocus", boolean.class) != null) {
				super.setAutoRequestFocus(v);
			}
		}
		catch (NoSuchMethodException e) {
			// ok to ignore
		}
	}

	/*private JComponent makeBondPane() 
	{
		JPanel bondPane = new JPanel(new GridBagLayout());
		bondPane.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));

		GridBagConstraints c0 = new GridBagConstraints();
		GridBagConstraints c1 = new GridBagConstraints();

		c0.gridx = 0;
		c1.gridx = 1;
		c0.anchor = c1.anchor = GridBagConstraints.WEST;
		c0.gridy = c1.gridy = 0;
		c0.weightx = c1.weightx = 0.5;
				
		bondPane.add(new JLabel(strings.getString("bonddlg.current_bond") + "$" + "0.00"), c0);		
		JButton repayBtn = new JButton(strings.getString("bonddlg.repay_bond"));
		repayBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//repayBond();
			}});
		bondPane.add(repayBtn, c1);

		return bondPane;
	}*/

	private JComponent makeCurrentBondPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 0, 20);

        // Left column components
        JLabel label = new JLabel("Current bond: $x.xx");
        mainPanel.add(label, gbc);

        // Right column components
        JButton button = new JButton("Repay Bond");
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(button, gbc);

		return mainPanel;
	}

	/* private JComponent makeNewBondPane() 
	{
		JPanel bondPane = new JPanel(new GridBagLayout());
		bondPane.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));

		GridBagConstraints c0 = new GridBagConstraints();
		GridBagConstraints c1 = new GridBagConstraints();

		c0.gridx = 0;
		c1.gridx = 1;
		c0.anchor = c1.anchor = GridBagConstraints.WEST;
		c0.gridy = c1.gridy = 0;
		c0.weightx = c1.weightx = 0.5;
				
		bondPane.add(new JLabel(strings.getString("bonddlg.issue_new_bond") + "$" + "0.00"), c0);		
		JRadioButton radioButton1 = new JRadioButton("$20,000");
      	JRadioButton radioButton2 = new JRadioButton("$50,000");
      	JRadioButton radioButton3 = new JRadioButton("$100,000");

      	ButtonGroup group = new ButtonGroup();
		group.add(radioButton1);
		group.add(radioButton2);
		group.add(radioButton3);

		bondPane.add(radioButton1);
		bondPane.add(radioButton2);
		bondPane.add(radioButton3);
		
		JButton issueBtn = new JButton(strings.getString("bonddlg.issue_bond"));
		issueBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//repayBond();
			}});
		bondPane.add(issueBtn, c1);

		return bondPane;
	}*/

	private JComponent makeNewBondPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Left column components
        JLabel label = new JLabel("Issue new bond");
        mainPanel.add(label, gbc);

        JRadioButton option1 = new JRadioButton("$20,000");
        gbc.gridy++;
        mainPanel.add(option1, gbc);

        JRadioButton option2 = new JRadioButton("$50,000");
        gbc.gridy++;
        mainPanel.add(option2, gbc);

        JRadioButton option3 = new JRadioButton("$100,000");
        gbc.gridy++;
        mainPanel.add(option3, gbc);

        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);
        group.add(option3);

        // Right column components
        JButton button = new JButton("Issue Bond");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 4; // Span 4 rows
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(button, gbc);

		return mainPanel;
	}

}