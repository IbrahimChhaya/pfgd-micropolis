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
import java.text.DecimalFormat;

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

	// private void applyChange()
	// {

	// 	loadBudgetNumbers(false);
	// }

	public BondDialog(Window owner, Micropolis engine)
	{
		super(owner);
		setTitle(strings.getString("bonddlg.title"));

		this.engine = engine;
		
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.setBorder(BorderFactory.createEmptyBorder(8, 18, 25, 18));
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

	private JComponent makeCurrentBondPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 50, 25, 50);

        // Left column components
		DecimalFormat df = new DecimalFormat("###,###,###");
        JLabel label = new JLabel(strings.getString("bonddlg.current_bond") + " $" + df.format(engine.budget.totalBond));
        mainPanel.add(label, gbc);

        // Right column components
		JButton repayBtn = new JButton(strings.getString("bonddlg.repay_bond"));
		if (engine.budget.totalBond == 0 || engine.budget.totalFunds < engine.budget.totalBond) {
			repayBtn.setEnabled(false);
		}
		repayBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				engine.spend(engine.budget.totalBond);
				engine.budget.totalBond = 0;
				dispose();
			}
		});

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(repayBtn, gbc);

		return mainPanel;
	}

	private JComponent makeNewBondPane() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Left column components
		JLabel label = new JLabel(strings.getString("bonddlg.issue_new_bond"));
		mainPanel.add(label, gbc);
		
		JButton issueBtn = new JButton(strings.getString("bonddlg.issue_bond"));
		issueBtn.setEnabled(false);

		JLabel repaymentLabel = new JLabel("");
		repaymentLabel.setVisible(false);
		gbc.gridy++;

		JRadioButton option1 = new JRadioButton("$20,000 @ 10%");
		option1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				DecimalFormat df = new DecimalFormat("###,###,###");
				repaymentLabel.setText("Repayment: $" + df.format(calculateBondRepayment(20000, 10, 10)) 
										+ " per year");
				repaymentLabel.setVisible(true);
				issueBtn.setEnabled(true);
			}
		});
		gbc.gridy++;
		mainPanel.add(option1, gbc);

		JRadioButton option2 = new JRadioButton("$50,000 @ 20%");
		option2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				DecimalFormat df = new DecimalFormat("###,###,###");
				repaymentLabel.setText("Repayment: $" + df.format(calculateBondRepayment(50000, 20, 10)) 
										+ " per year");
				repaymentLabel.setVisible(true);
				issueBtn.setEnabled(true);
			}
		});
		gbc.gridy++;
        mainPanel.add(option2, gbc);

        JRadioButton option3 = new JRadioButton("$100,000 @ 50%");
		option3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				DecimalFormat df = new DecimalFormat("###,###,###");
				repaymentLabel.setText("Repayment: $" + df.format(calculateBondRepayment(100000, 50, 10)) 
										+ " per year");
				repaymentLabel.setVisible(true);
				issueBtn.setEnabled(true);
			}
		});
        gbc.gridy++;
        mainPanel.add(option3, gbc);

        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);
        group.add(option3);

		gbc.gridy++;
		mainPanel.add(repaymentLabel, gbc);
		
        // Right column components
		issueBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if(option1.isSelected()) {
					engine.budget.totalBond += 20000;
					engine.spend(-20000);
				} else if(option2.isSelected()) {
					engine.budget.totalBond += 50000;
					engine.spend(-50000);
				} else if(option3.isSelected()) {
					engine.budget.totalBond += 100000;
					engine.spend(-100000);
				}
				dispose();
			}
		});

		JLabel bondExists = new JLabel(strings.getString("bonddlg.bond_exists"));
		bondExists.setVisible(false);
		gbc.gridy++;
		mainPanel.add(bondExists, gbc);
		
		if (engine.budget.totalBond > 0) {
			issueBtn.setEnabled(false);
			bondExists.setVisible(true);
			option1.setEnabled(false);
			option2.setEnabled(false);
			option3.setEnabled(false);
		}

		gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(issueBtn, gbc);

		return mainPanel;
	}

	private int calculateBondRepayment(int bondAmount, double interestRate, int years) {
		double bondRepayment = ((bondAmount * (interestRate / 100)) + bondAmount) / years;
		engine.budget.bondRepayment = (int)bondRepayment;
		return (int)bondRepayment;
	}
}