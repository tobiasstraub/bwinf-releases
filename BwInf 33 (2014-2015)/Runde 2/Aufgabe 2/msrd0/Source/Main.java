/*
 * test
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2;

import static javax.swing.SpringLayout.*;

import bwinf33_2.aufgabe2.ai.AI;
import org.apache.commons.cli.*;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class Main
{
	/**
	 * Erstellt eine Option für Apache Commons CLI.
	 */
	static Option option (String shortOpt, String longOpt, String descr, String valName)
	{
		Option option = new Option(shortOpt, longOpt, true, descr);
		option.setArgName(valName);
		option.setArgs(1);
		option.setRequired(false);
		return option;
	}

	public static void main (String args[])
	{
		// Options erstellen
		Options options = new Options();
		options.addOption("h", "help", false, "Zeigt diese Hilfe an.");
		options.addOption(option("c", "cones", "Die Anzahl der Kegel", "cones"));
		options.addOption(option("r", "radius", "Der Radius des Kreises", "radius"));

		// cli parsen
		CommandLineParser parser = new PosixParser();
		CommandLine cli;
		try
		{
			cli = parser.parse(options, args);
		}
		catch (ParseException pe)
		{
			System.err.println("Fehler beim Auswerten der Argumente:");
			System.err.println(pe.getMessage());
			System.exit(1);
			return;
		}

		// evtl. Hilfe anzeigen
		if (cli.hasOption("h"))
		{
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.setWidth(120);
			helpFormatter.printHelp(Main.class.getCanonicalName(), options, true);
			return;
		}

		// Argumente parsen
		float radius = (cli.hasOption("r") ? Float.valueOf(cli.getOptionValue("r")) : 2f);
		int cones = (cli.hasOption("c") ? Integer.valueOf(cli.getOptionValue("c")) : 20);

		// SceneData erstellen
		SceneData data = new SceneData(radius, cones);

		// KI's suchen
		Reflections p = new Reflections("bwinf33_2.aufgabe2.ai.impl");
		Set<Class<? extends AI>> kiset = p.getSubTypesOf(AI.class);
		@SuppressWarnings("unchecked")
		Class<? extends AI>[] kiclasses = new Class[kiset.size()];
		int i = 0;
		for (Class<? extends AI> c : kiset)
		{
			kiclasses[i++] = c;
		}
		Arrays.sort(kiclasses,
					(Class<? extends AI> c0, Class<? extends AI> c1)
							-> c0.getSimpleName().compareToIgnoreCase(c1.getSimpleName()));
		String kinames[] = new String[kiset.size() + 1];
		kinames[0] = "Benutzer";
		i = 0;
		for (Class<? extends AI> c : kiclasses)
		{
			kinames[++i] = c.getSimpleName();
		}


		// GUI erstellen
		JFrame f = new JFrame("Panorama-Kegeln");

		JPanel cp = new JPanel();
		SpringLayout layout = new SpringLayout();
		cp.setLayout(layout);
		f.setContentPane(cp);

		JButton startGame = new JButton("Spiel starten");
		cp.add(startGame);
		layout.putConstraint(SOUTH, startGame, -10, SOUTH, cp);
		layout.putConstraint(HORIZONTAL_CENTER, startGame, 0, HORIZONTAL_CENTER, cp);

		JLabel result0 = new JLabel("0");
		result0.setOpaque(true);
		result0.setHorizontalAlignment(SwingConstants.CENTER);
		cp.add(result0);
		layout.putConstraint(NORTH, result0, 0, NORTH, startGame);
		layout.putConstraint(SOUTH, result0, 0, SOUTH, startGame);
		layout.putConstraint(WEST, result0, 10, WEST, cp);
		layout.putConstraint(EAST, result0, -10, WEST, startGame);

		JLabel result1 = new JLabel("0");
		result1.setOpaque(true);
		result1.setHorizontalAlignment(SwingConstants.CENTER);
		cp.add(result1);
		layout.putConstraint(NORTH, result1, 0, NORTH, startGame);
		layout.putConstraint(SOUTH, result1, 0, SOUTH, startGame);
		layout.putConstraint(WEST, result1, 10, EAST, startGame);
		layout.putConstraint(EAST, result1, -10, EAST, cp);

		JComboBox<String> player0 = new JComboBox<>(kinames);
		player0.setSelectedItem("Randy");
		cp.add(player0);
		layout.putConstraint(SOUTH, player0, -10, NORTH, startGame);
		layout.putConstraint(WEST, player0, 10, WEST, cp);
		layout.putConstraint(EAST, player0, -20, HORIZONTAL_CENTER, cp);

		JComboBox<String> player1 = new JComboBox<>(kinames);
		player1.setSelectedItem("Benutzer");
		cp.add(player1);
		layout.putConstraint(SOUTH, player1, 0, SOUTH, player0);
		layout.putConstraint(EAST, player1, -10, EAST, cp);
		layout.putConstraint(WEST, player1, 20, HORIZONTAL_CENTER, cp);

		ScenePane scene = new ScenePane(data);
		cp.add(scene);
		layout.putConstraint(NORTH, scene, 10, NORTH, cp);
		layout.putConstraint(SOUTH, scene, -10, NORTH, player0);
		layout.putConstraint(WEST, scene, 10, WEST, cp);
		layout.putConstraint(EAST, scene, -10, EAST, cp);

		// Listener registrieren
		startGame.addActionListener((ActionEvent e) -> new Thread(() -> {
			if (data.isNull())
			{
				data.randomCones(radius, cones);
				scene.repaint();
				result0.setBackground(Color.WHITE);
				result1.setBackground(Color.WHITE);
				result0.setText("0");
				result1.setText("0");
				player0.setEnabled(true);
				player1.setEnabled(true);
				startGame.setText("Spiel starten");
			}
			else
			{
				startGame.setEnabled(false);
				player0.setEnabled(false);
				player1.setEnabled(false);
				try
				{
					AI ai0 = (player0.getSelectedIndex() == 0
							  ? scene.createUserAi()
							  : kiclasses[player0.getSelectedIndex() - 1].newInstance());
					AI ai1 = (player1.getSelectedIndex() == 0
							  ? scene.createUserAi()
							  : kiclasses[player1.getSelectedIndex() - 1].newInstance());
					Simulator s = new Simulator(data, ai0, ai1);
					s.startSimulation(scene, result0, result1);
				}
				catch (InstantiationException | IllegalAccessException | InterruptedException ex)
				{
					ex.printStackTrace();
				}
				data.clear();
				startGame.setText("Neues Spiel");
				startGame.setEnabled(true);
			}
		}, "Simulator").start());

		// GUI öffnen
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(scene.getPreferredSize().width + 20,
				  scene.getPreferredSize().height + player0.getPreferredSize().height + startGame
						  .getPreferredSize().height + 100);
		f.setVisible(true);
	}
}
