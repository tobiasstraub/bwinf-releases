package buschfeuer;

import static buschfeuer.impl.Raster.BRAND;
import static buschfeuer.impl.Raster.SCHNEISE;
import static buschfeuer.impl.Raster.WALD;
import static java.awt.event.ActionEvent.ACTION_PERFORMED;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import buschfeuer.impl.Raster;
import buschfeuer.impl.RasterPane;

/**
 * Diese Klasse ist das Hauptfenster. Es zeigt das Raster an und gibt dem Benutzer
 * am Anfang die Option, dieses Raster zu verändern, bevor der Simulator den Feuerlöscher
 * in Aktion versetzt. 
 * @author Dominic S. Meiser
 */
public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 1522271261689230987L;
	
	/** Gibt das aktuelle Raster zurück. */
	public Raster getRaster ()
	{
		return raster;
	}
	
	protected Raster raster;
	protected RasterPane pane;
	protected JLabel info;
	protected JButton ok;
	
	/**
	 * Ordnet die Komponenten im Fenster an und öffnet das Fenster.
	 * @param r Das zu benutzende Raster.
	 */
	public MainFrame (Raster r)
	{
		super("Buschfeuer");
		
		if (r == null) throw new NullPointerException("Brauche ein Raster!");
		raster = r;
		
		JPanel cp = new JPanel ();
		SpringLayout layout = new SpringLayout();
		cp.setLayout(layout);
		setContentPane(cp);
		
		pane = new RasterPane(raster);
		pane.setReplacement(WALD, SCHNEISE);
		cp.add(pane);
		layout.putConstraint(NORTH, pane, 15, NORTH, cp);
		layout.putConstraint(HORIZONTAL_CENTER, pane, 0, HORIZONTAL_CENTER, cp);
		
		info = new JLabel ("Wählen Sie die Brandschneisen.");
		cp.add(info);
		layout.putConstraint(NORTH, info, 10, SOUTH, pane);
		layout.putConstraint(HORIZONTAL_CENTER, info, 0, HORIZONTAL_CENTER, cp);
		
		ok = new JButton ("Weiter");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				if (ok.getText().equals("Weiter"))
				{
					ok.setText("Simulation starten");
					info.setText("Wählen Sie die Brandflächen");
					pane.setReplacement(WALD, BRAND);
				}
				else
				{
					ok.setVisible(false);
					info.setVisible(false);
					pane.setReplacement(-1, -1);
					for (ActionListener l : listeners)
						l.actionPerformed(new ActionEvent(this, ACTION_PERFORMED, ok.getText()));
				}
			}
		});
		cp.add(ok);
		layout.putConstraint(NORTH, ok, 10, SOUTH, info);
		layout.putConstraint(HORIZONTAL_CENTER, ok, 0, HORIZONTAL_CENTER, cp);
		
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Überspringt die Benutzerveränderung des Rasters.
	 */
	public void skipOK ()
	{
		ok.setVisible(false);
		info.setVisible(false);
		pane.setReplacement(-1, -1);
		for (ActionListener l : listeners)
			l.actionPerformed(new ActionEvent(this, ACTION_PERFORMED, ok.getText()));
	}
	
	/** Ruft die repaint()-Methode des RasterPane auf. */
	public void refresh ()
	{
		pane.repaint();
	}
	
	private List<ActionListener> listeners = new LinkedList<>();
	/** Fügt einen Listener hinzu, der aufgerufen wird, wenn das Raster gelöscht werden soll. */
	public void addActionListener (ActionListener l)
	{
		listeners.add(l);
	}
}
