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

	//instantiate a new BondDialog
	public BondDialog(Window owner, Micropolis engine)
	{
		super(owner);
		//title of the dialog window
		setTitle(strings.getString("bonddlg.title"));

		this.engine = engine;
		
		//size up the dialog window
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.setBorder(BorderFactory.createEmptyBorder(8, 18, 25, 18));
		add(mainBox, BorderLayout.CENTER);

		//call the makeCurrentBondPane method to add current bond pane 
		mainBox.add(makeCurrentBondPane());

		//separator line
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		mainBox.add(sep);

		//call the makeCurrentBondPane method to add make new bond pane
		mainBox.add(makeNewBondPane());

		//set size, location, and close operation
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
		//instantiate new panel, grids, and insets
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 50, 25, 50);
		
		//format amount
		DecimalFormat df = new DecimalFormat("###,###,###");
		//current bond label
        JLabel label = new JLabel(strings.getString("bonddlg.current_bond") + " $" + df.format(engine.budget.totalBond));
        mainPanel.add(label, gbc);

        //repay button
		JButton repayBtn = new JButton(strings.getString("bonddlg.repay_bond"));
		//only enable repay button if there is a bond to repay and funds are available
		if (engine.budget.totalBond == 0 || engine.budget.totalFunds < engine.budget.totalBond) {
			repayBtn.setEnabled(false);
		}
		//onclick of the button
		repayBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//repay the bond, clear it, and close the window
				engine.spend(engine.budget.totalBond);
				engine.budget.totalBond = 0;
				dispose();
			}
		});

		//grid setup
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(repayBtn, gbc);

		return mainPanel;
	}

	private JComponent makeNewBondPane() {
		//instantiate new panel, grids, and insets
		JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // issue new bond label
		JLabel label = new JLabel(strings.getString("bonddlg.issue_new_bond"));
		mainPanel.add(label, gbc);
		
		//issue bond button
		JButton issueBtn = new JButton(strings.getString("bonddlg.issue_bond"));
		issueBtn.setEnabled(false);

		//repayment label
		JLabel repaymentLabel = new JLabel("");
		repaymentLabel.setVisible(false);
		gbc.gridy++;

		//radio buttons for bond options
		//option 1
		JRadioButton option1 = new JRadioButton("$20,000 @ 10%");
		//onclick of the button
		option1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//number format
				//show the repayment amount
				DecimalFormat df = new DecimalFormat("###,###,###");
				repaymentLabel.setText("Repayment: $" + df.format(calculateBondRepayment(20000, 10, 10)) 
										+ " per year");
				repaymentLabel.setVisible(true);
				issueBtn.setEnabled(true);
			}
		});
		gbc.gridy++;
		mainPanel.add(option1, gbc);

		//option 2
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

		//option 3
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

		//group the radio buttons, possible unneccessary
        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);
        group.add(option3);

		gbc.gridy++;
		mainPanel.add(repaymentLabel, gbc);
		
        // issue button onclick, requires option radio buttons to be selected
		issueBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//spend bond amount and close window
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

		//bond exists label
		JLabel bondExists = new JLabel(strings.getString("bonddlg.bond_exists"));
		bondExists.setVisible(false);
		gbc.gridy++;
		mainPanel.add(bondExists, gbc);
		
		//if a bond exists, disable the issue button and radio buttons
		if (engine.budget.totalBond > 0) {
			issueBtn.setEnabled(false);
			bondExists.setVisible(true);
			option1.setEnabled(false);
			option2.setEnabled(false);
			option3.setEnabled(false);
		}

		//grid setup
		gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(issueBtn, gbc);

		return mainPanel;
	}

	//calculate the bond repayment amount
	private int calculateBondRepayment(int bondAmount, double interestRate, int years) {
		//get total interest by multiplying the bond amount
		//add bond amount to the total interest to get total repayment
		//divide by years to get yearly repayment
		//double used because of integer division
		//cast to int for display
		double bondRepayment = ((bondAmount * (interestRate / 100)) + bondAmount) / years;
		engine.budget.bondRepayment = (int)bondRepayment;
		return (int)bondRepayment;
	}
}