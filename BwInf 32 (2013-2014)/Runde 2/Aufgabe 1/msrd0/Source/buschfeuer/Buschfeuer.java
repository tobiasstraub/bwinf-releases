package buschfeuer;

import static javax.swing.SpringLayout.BASELINE;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import buschfeuer.impl.RasterUtils;

/**
 * Diese Klasse enthält die main-Methode der Buschfeuer-Aufgabe des 32. BwInf. Sie
 * öffnet ein Fenster, in dem der Benutzer angeben kann, welchen Feuerlöscher er
 * benutzen möchte und welche Größe der Wald haben soll. Die Liste der Feuerlöscher
 * wird automatisch bestimmt.
 * @author Dominic S. Meiser
 */
public class Buschfeuer extends JFrame
{
	private static final long serialVersionUID = -7148268448326951879L;
	
	/**
	 * Diese main-Methode öffnet ein Fenster, in dem der Benutzer angeben kann,
	 * welcher Feuerlöscher benutzt werden soll und wie groß der Wald sein soll.
	 * Außerdem wird das package buschfeuer.feuerloescher nach Feuerlöschern
	 * durchsucht, die dann dem Benutzer zur Auswahl stehen.
	 */
	public static void main (String[] args)
	{
		// tmpdir zur Verfügung stellen
		{
			File tmpdir = new File(System.getProperty("java.io.tmpdir"), "buschfeuer");
			if (tmpdir.exists())
			{
				if (!tmpdir.delete())
				{
					System.err.println("Couldn't delete "+tmpdir);
					String[] list = tmpdir.list();
					for (String str : list)
					{
						File f = new File(tmpdir, str);
						if (!f.delete())
							System.err.println("Couldn't delete "+f);
						else System.err.println("File "+f+" deleted");
					}
				}
			}
			tmpdir.mkdir();
		}
		
		// Feuerlöscher suchen
		String[] feuerloescher_files = new File("buschfeuer"+File.separator+"feuerloescher").list(
				(File dir, String name) ->
				((new File(dir, name).isFile()) && (name.indexOf('$') == -1) && name.endsWith(".class")) );
		if ((feuerloescher_files == null) || (feuerloescher_files.length == 0))
		{
			System.err.println("Keine Feuerlöscher gefunden!");
			return;
		}
		
		// Feuerlöscher umformatieren und ausgeben
		System.out.println("Feuerlöscher: "+Arrays.toString(feuerloescher_files));
		String[] feuerloescher = new String[feuerloescher_files.length];
		for (int i = 0; i < feuerloescher_files.length; i++)
			feuerloescher[i] = "buschfeuer.feuerloescher."+feuerloescher_files[i]
					.substring(0, feuerloescher_files[i].length()-6);
		System.out.println("Feuerlöscher: "+Arrays.toString(feuerloescher));
		System.out.println();
		
		// Fenster erstellen
		new Buschfeuer(feuerloescher);
	}
	
	
	protected JComboBox<String> FeuerLoescher;
	protected JSpinner width, height;
	protected JButton ok, save, open;
	
	/**
	 * Dieser Konstruktor öffnet ein Fenster, in dem der Benutzer angeben kann,
	 * welcher Feuerlöscher benutzt werden soll und wie groß der Wald sein soll.
	 * @param feuerloescher Alle verfügbaren Feuerlöscher
	 */
	public Buschfeuer (String[] feuerloescher)
	{
		super("Buschfeuer");
		
		JPanel cp = new JPanel ();
		SpringLayout layout = new SpringLayout();
		cp.setLayout(layout);
		setContentPane(cp);
		
		JLabel info0 = new JLabel ("Feuerlöscher-Klasse:");
		cp.add(info0);
		layout.putConstraint(NORTH, info0, 10, NORTH, cp);
		layout.putConstraint(WEST, info0, 10, WEST, cp);
		
		FeuerLoescher = new JComboBox<>(feuerloescher);
		cp.add(FeuerLoescher);
		layout.putConstraint(NORTH, FeuerLoescher, 7, SOUTH, info0);
		layout.putConstraint(WEST, FeuerLoescher, 15, WEST, info0);
		layout.putConstraint(EAST, FeuerLoescher, -15, EAST, cp);
		
		JLabel info1 = new JLabel ("Größe des Waldes:");
		cp.add(info1);
		layout.putConstraint(NORTH, info1, 20, SOUTH, FeuerLoescher);
		layout.putConstraint(WEST, info1, 0, WEST, info0);
		
		JLabel info2 = new JLabel ("Breite:");
		cp.add(info2);
		layout.putConstraint(NORTH, info2, 7, SOUTH, info1);
		layout.putConstraint(WEST, info2, 15, WEST, info1);
		
		width = new JSpinner(new SpinnerNumberModel(10, 2, 38, 1));
		cp.add(width);
		layout.putConstraint(BASELINE, width, 0, BASELINE, info2);
		layout.putConstraint(WEST, width, 15, EAST, info2);
		
		JLabel info3 = new JLabel ("Höhe:");
		cp.add(info3);
		layout.putConstraint(NORTH, info3, 7, SOUTH, info2);
		layout.putConstraint(WEST, info3, 0, WEST, info2);
		
		height = new JSpinner(new SpinnerNumberModel(10, 2, 38, 1));
		cp.add(height);
		layout.putConstraint(BASELINE, height, 0, BASELINE, info3);
		layout.putConstraint(WEST, height, 15, EAST, info3);
		
		ok = new JButton ("Weiter");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
				try
				{
					Feuerloescher f = (Feuerloescher) getClass().getClassLoader()
							.loadClass(FeuerLoescher.getSelectedItem().toString())
							.newInstance();
					Simulator.startSimulation(f, width.getValue(), height.getValue());
				}
				catch (ReflectiveOperationException roe)
				{
					roe.printStackTrace();
				}
			}
		});
		cp.add(ok);
		layout.putConstraint(SOUTH, ok, -20, SOUTH, cp);
		layout.putConstraint(HORIZONTAL_CENTER, ok, 0, HORIZONTAL_CENTER, cp);
		
		open = new JButton ("Öffnen");
		open.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				dispose();
				try
				{
					Feuerloescher f = (Feuerloescher) getClass().getClassLoader()
							.loadClass(FeuerLoescher.getSelectedItem().toString())
							.newInstance();
					Simulator.startSimulation(f, RasterUtils.load());
				}
				catch (ReflectiveOperationException roe)
				{
					roe.printStackTrace();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		});
		cp.add(open);
		layout.putConstraint(SOUTH, open, 0, SOUTH, ok);
		layout.putConstraint(WEST, open, 20, WEST, cp);
		
		setSize(600,230);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
