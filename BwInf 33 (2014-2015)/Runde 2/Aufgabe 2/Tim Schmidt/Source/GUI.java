package org.timschmidt.bwinf.a2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class GUI extends JFrame{
	
	private JPanel panel = new JPanel();
	JTextField tfKreisRadius, tfWinkel, tfX, tfY;
	JSlider slWinkel, slX, slY;
	JButton btWurf, btWurfQ;
	JLabel lbSpieler, lbPAnna, lbPRandy, lbEAnna, lbERandy;
	
	public GUI(){
		super("Frame 1");
		
		// Frame einstellen
		setSize(1050,1000);
		setResizable(true);
		addWindowListener(new GUIListener());
		
		// Hauptpanel einstellen
		panel.setLayout(new BorderLayout());
		panel.setDoubleBuffered(true);
		
		// Kreis linken
		final Kreis kreis = Main.kreis;
		
		// Einstellungspanel
		JPanel settingspanel = new JPanel();
		
			// Kreisradius
				// Textbox
				tfKreisRadius = new JTextField("2",3);
				tfKreisRadius.setHorizontalAlignment(SwingConstants.RIGHT);
				tfKreisRadius.setEnabled(false); // TODO
				
			settingspanel.add(tfKreisRadius);
				
		// Spielerpanel
		JPanel spielerpanel = new JPanel();
		spielerpanel.setLayout(new BoxLayout(spielerpanel, BoxLayout.Y_AXIS));
		
			// Aktueller Spieler
				// Label
				lbSpieler = new JLabel("Aktueller Spieler: " + Main.curSpieler.getName());
				lbSpieler.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				// Button: Werfen und weiter
				btWurf = new JButton("Werfen und weiter!");
				btWurf.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				btWurfQ = new JButton("Werfen und neu!");
				btWurfQ.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			// Punkte
				JPanel punktepanel = new JPanel();
				punktepanel.setBorder(BorderFactory.createTitledBorder("Punktestand"));
				punktepanel.setLayout(new GridBagLayout());
				punktepanel.setMinimumSize(new Dimension(150, 50));
				punktepanel.setMaximumSize(new Dimension(150, 50));
				
				JLabel lbNAnna = new JLabel("Anna:");
				lbNAnna.setAlignmentX(Component.LEFT_ALIGNMENT);
				JLabel lbNRandy = new JLabel("Randy:");
				lbNRandy.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				lbPAnna = new JLabel(new Integer(Main.anna.getPunkte()).toString());
				lbPAnna.setHorizontalAlignment(JLabel.RIGHT);
				lbPRandy = new JLabel(new Integer(Main.randy.getPunkte()).toString());
				lbPRandy.setHorizontalAlignment(JLabel.RIGHT);
				
				lbEAnna = new JLabel("+0");
				lbEAnna.setHorizontalAlignment(JLabel.RIGHT);
				lbERandy = new JLabel("+0");
				lbERandy.setHorizontalAlignment(JLabel.RIGHT);
				
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = GridBagConstraints.RELATIVE;
				c.gridy = 0; c.weightx = 0.40;
				punktepanel.add(lbNAnna, c);
				punktepanel.add(lbPAnna, c);
				c.weightx = 0.2;
				punktepanel.add(lbEAnna, c);
				c.gridy = 1; c.weightx = 0.40;
				punktepanel.add(lbNRandy, c);
				punktepanel.add(lbPRandy, c);
				c.weightx = 0.2;
				punktepanel.add(lbERandy, c);
			
			// Winkel
				// Label
				JLabel lbWinkel = new JLabel("Winkel");
				lbWinkel.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				// Textbox
				tfWinkel = new JTextField("0.0");
				tfWinkel.setMaximumSize(new Dimension(70,20));
				tfWinkel.setHorizontalAlignment(SwingConstants.RIGHT);
				tfWinkel.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				// Slider
				slWinkel = new JSlider();
				slWinkel.setAlignmentX(Component.CENTER_ALIGNMENT);
				slWinkel.setValue(0);
				
					
			// Zielpunkt
				// X
					// Label
					JLabel lbX = new JLabel("Abszisse");
					lbX.setAlignmentX(Component.CENTER_ALIGNMENT);
					// Slider
					slX = new JSlider();
					slX.setAlignmentX(Component.CENTER_ALIGNMENT);
					// Textbox 
					tfX = new JTextField("0");
					tfX.setMaximumSize(new Dimension(70,20));
					tfX.setHorizontalAlignment(SwingConstants.RIGHT);
					tfX.setAlignmentX(Component.CENTER_ALIGNMENT);
			
				// Y
					// Label
					JLabel lbY = new JLabel("Ordinate");
					lbY.setAlignmentX(Component.CENTER_ALIGNMENT);
					// Slider
					slY = new JSlider();
					slY.setAlignmentX(Component.CENTER_ALIGNMENT);
					// Textbox
					tfY = new JTextField("0");
					tfY.setMaximumSize(new Dimension(70,20));
					tfY.setHorizontalAlignment(SwingConstants.RIGHT);
					tfY.setAlignmentX(Component.CENTER_ALIGNMENT);
				
			spielerpanel.add(lbSpieler);
			spielerpanel.add(btWurf);
			spielerpanel.add(btWurfQ);
			spielerpanel.add(Box.createVerticalStrut(50));
			spielerpanel.add(punktepanel);
			spielerpanel.add(Box.createVerticalStrut(200));
			spielerpanel.add(lbWinkel);
			spielerpanel.add(slWinkel);
			spielerpanel.add(tfWinkel);
			spielerpanel.add(Box.createVerticalStrut(25));
			spielerpanel.add(lbX);
			spielerpanel.add(slX);
			spielerpanel.add(tfX);
			spielerpanel.add(Box.createVerticalStrut(25));
			spielerpanel.add(lbY);
			spielerpanel.add(slY);
			spielerpanel.add(tfY);
				
		// ActionListener-Klasse für Buttons und TextFields definieren
		class AcLis implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				// Einzelne Objekte durchgehen und jeweilige Aktionen durchführen
				if (src.equals(tfKreisRadius)){
					Main.setRadius(Integer.parseInt(tfKreisRadius.getText()));
					
				} else if (src.equals(tfWinkel)) {
					// Prüfen, ob Textfeld leer. Wenn ja, abbrechen
					if (tfWinkel.getText().isEmpty())
						return;
					
					// Prüfen, ob Text im Feld zu Double konvertiert werden kann. Wenn nicht, Text löschen und abbrechen
					try {
						Double.parseDouble(tfWinkel.getText());
					} catch (NumberFormatException nfe) {
						tfWinkel.setText("");
						return;
					}
					
					// Falls Schieber nicht gerade gezogen wird, Schieber an Textfeld anpassen
					if (!slWinkel.getValueIsAdjusting())
						slWinkel.setValue((int)Math.round(Double.parseDouble(tfWinkel.getText()) / 1.8));
					
					// Letztendliche Änderung des Winkels wird durch den Schieber durchgeführt
					
				} else if (src.equals(tfX) || src.equals(tfY)) {
					// Prüfen, ob Textfeld leer. Wenn ja, abbrechen
					if (tfX.getText().isEmpty() || tfY.getText().isEmpty())
						return;
					
					// Prüfen, ob Text im Feld zu Double konvertiert werden kann. Wenn nicht, Text löschen und abbrechen
					try {
						Double.parseDouble(tfX.getText());
					} catch (NumberFormatException nfe) {
						tfX.setText("");
						return;
					}
					try {
						Double.parseDouble(tfY.getText());
					} catch (NumberFormatException nfe) {
						tfY.setText("");
						return;
					}
					
					Point2D.Double z = new Point2D.Double(Double.parseDouble(tfX.getText()),
							Double.parseDouble(tfY.getText()));
					Main.curZiel = z;
					kreis.refresh();
					
				} else if (src.equals(btWurf)){
					if (Main.curSpieler.equals(Main.anna))
						Main.wurf();
					Main.naechsterSpieler();
					
				} else if (src.equals(btWurfQ)){
					Main.wurf();
					Main.kegellist = new ArrayList<Kegel>();
					Main.fillKList();
					Main.naechsterSpieler();
				}
				
			}
		}
		
		// ChangeListener-Klasse für Slider definieren
		class SlLis implements ChangeListener{
			public void stateChanged(ChangeEvent e){
				JSlider src = (JSlider) e.getSource();
				if (src.getValueIsAdjusting())
					if (src.equals(slWinkel)){
						double val = (double)src.getValue()*1.8;
						tfWinkel.setText(new Double(Math.round(
								val*10.0)/10.0).toString());	// Werte des Sliders in Gradzahlen umwandeln und auf die 1. Nachkommastelle runden
						Main.curWinkel = src.getValue()*1.8;
					} else if (src.equals(slX)){
						double val = (double)src.getValue()/100 * Main.radius*2 - Main.radius;
						tfX.setText(new Double(Math.round(
										val* 10.0)/10.0).toString());	// Links = -Radius, Mitte = 0, Rechts = +Radius
						Main.curZiel = new Point2D.Double(val, Main.curZiel.getY());
					} else if (src.equals(slY)){
						double val = (double)src.getValue()/100 * Main.radius*2 - Main.radius;
						tfY.setText(new Double(Math.round(
								val* 10.0)/10.0).toString());	// Links = -Radius, Mitte = 0, Rechts = +Radius
						Main.curZiel = new Point2D.Double(Main.curZiel.getX(), val);
					}
				
				Main.kreis.refresh();
			}
		}
		
		// ActionListener erzeugen und zuweisen
		AcLis acLis = new AcLis(); 
		btWurf.addActionListener(acLis);
		btWurfQ.addActionListener(acLis);
		tfKreisRadius.addActionListener(acLis);
		tfWinkel.addActionListener(acLis);
		tfX.addActionListener(acLis);
		tfY.addActionListener(acLis);
		SlLis slLis = new SlLis();
		slWinkel.addChangeListener(slLis);
		slX.addChangeListener(slLis);
		slY.addChangeListener(slLis);
		
		// Elemente dem Hauptpanel hinzufügen
		panel.add(kreis, BorderLayout.CENTER);
		panel.add(settingspanel, BorderLayout.NORTH);
		panel.add(spielerpanel, BorderLayout.EAST);
		
		// Hauptpanel dem Frame hinzufügen
		add(panel);
		
		// Frame anzeigen
		setVisible(true);
	}
	
	private class GUIListener extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			e.getWindow().dispose();
			System.exit(0);
		}
	}
	
	public void enableUI(){
		tfKreisRadius.setEnabled(true);
		tfWinkel.setEnabled(true);
		tfX.setEnabled(true);
		tfY.setEnabled(true);
		slWinkel.setEnabled(true);
		slX.setEnabled(true);
		slY.setEnabled(true);
		btWurfQ.setEnabled(true);
	}
	
	public void disableUI(){
		tfKreisRadius.setEnabled(false);
		tfWinkel.setEnabled(false);
		tfX.setEnabled(false);
		tfY.setEnabled(false);
		slWinkel.setEnabled(false);
		slX.setEnabled(false);
		slY.setEnabled(false);
		btWurfQ.setEnabled(false);
	}
	
	public void updateSpieler(){
		lbSpieler.setText("Aktueller Spieler: " + Main.curSpieler.getName());
		lbPAnna.setText(new Integer(Main.anna.getPunkte()).toString());
		lbPRandy.setText(new Integer(Main.randy.getPunkte()).toString());
		lbEAnna.setText("+0");
		lbERandy.setText("+0");
	}
	
	public void setE(int i){
		if (Main.curSpieler.equals(Main.anna))
			lbEAnna.setText("+" + i);
		else
			lbERandy.setText("+" + i);
	}
	
}
