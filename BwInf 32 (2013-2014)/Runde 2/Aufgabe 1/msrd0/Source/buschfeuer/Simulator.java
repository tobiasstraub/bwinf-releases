package buschfeuer;

import static buschfeuer.Feuerloescher.printBashText;
import static buschfeuer.impl.Raster.GELOESCHT;
import static buschfeuer.impl.Raster.WALD;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import buschfeuer.impl.Raster;
import buschfeuer.impl.RasterFile;
import buschfeuer.impl.RasterUtils;

/**
 * Diese Klasse simuliert den Brand und "verwaltet" den Feuerlöscher.
 * @author Dominic S. Meiser
 */
public class Simulator implements Runnable
{
	/**
	 * Startet die Simulation für den Feuerlöscher f und ein Raster mit der Größe
	 * width und height. width und height müssen dabei Subklassen von Number sein.
	 */
	public static void startSimulation (Feuerloescher f, Object width, Object height)
	{
		int w = ((Number)width).intValue();
		int h = ((Number)height).intValue();
		startSimulation(f, w, h);
	}
	
	/**
	 * Startet die Simulation für den Feuerlöscher f und ein Raster mit der Größe
	 * width und height.
	 */
	public static void startSimulation (Feuerloescher f, int width, int height)
	{
		final MainFrame mf = new MainFrame (new Raster(width, height, WALD));
		mf.addActionListener((ActionEvent e) ->
				new Thread(new Simulator(mf.getRaster(), f, mf), "Simulator").start());
	}
	
	/**
	 * Startet die Simulation mit dem Feuerlöscher f und dem im RasterFile rf
	 * enthaltenen Raster. Wenn rf eine Lösung erhält, wird der Benutzer gefragt,
	 * ob er diese benutzen möchte.
	 */
	public static void startSimulation (Feuerloescher f, RasterFile rf)
	{
		if (rf.solution != null)
		{
			int choice = JOptionPane.showConfirmDialog(null, "Das angegebene Raster enthält eine Lösung."+
					"Soll trotzdem eine neue Lösung mit dem Feuerlöscher "+f.getClass().getCanonicalName()+
					"erstellt werden?", "Raster enthält Lösungsvorschlag", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION)
				startSimulation(f, rf.raster);
			else
			{
				startSimulation(new Feuerloescher()
				{
					int count = -1;
					public Point delete (Raster raster)
					{
						if (++count >= rf.solution.size()) return null;
						return rf.solution.get(count);
					}
				}, rf.raster);
			}
		}
		else startSimulation(f, rf.raster);
	}
	
	/**
	 * Startet die Simulation mit dem Feuerlöscher f und dem Raster raster.
	 */
	public static void startSimulation (Feuerloescher f, Raster raster)
	{
		MainFrame mf = new MainFrame (raster);
		mf.addActionListener((ActionEvent e) ->
				new Thread(new Simulator(mf.getRaster(), f, mf), "Simulator").start());
		mf.skipOK();
	}
	
	
	/**
	 * Initialisiert den Simulator mit den angegebenen Parametern.
	 */
	private Simulator (Raster r, Feuerloescher fl, MainFrame mf)
	{
		super();
		raster = r;
		f = fl;
		frame = mf;
	}
	private Raster raster;
	private Feuerloescher f;
	private MainFrame frame;
	
	/**
	 * Startet den Simulator
	 */
	public void run ()
	{
		// Das Raster speichern.
		File file = new File(System.getProperty("java.io.tmpdir")+File.separator+"buschfeuer", "raster.bfr");
		try { RasterUtils.save(raster, null, file); }
		catch (IOException ioe) { ioe.printStackTrace(); }
		
		// Anzahl Wald im Raster ermitteln
		int wald = raster.count(WALD);
		
		// Zuerst den Feuerloescher initialisieren
		System.out.println("[SIMULATOR] initializing Feuerlöscher ...");
		Raster clone = RasterUtils.clone(raster);
		long time = System.currentTimeMillis();
		f.init(clone);
		System.out.print("[SIMULATOR] "); printBashText("finished ("+((System.currentTimeMillis()-time)/1000.0)+" sec)\n", "38;5;44");
		
		// Jetzt den Brand auf dem Bildschirm ausgeben
		while (!RasterUtils.istGeloescht(raster))
			burn();
		
		System.out.println("[SIMULATOR] Geretteter Wald: "+raster.count(WALD)+"/"+wald);
	}
	
	/**
	 * Simuliert einen Schritt des Brandes und lässt den Feuerlöscher aktiv werden.
	 */
	private void burn ()
	{
		RasterUtils.burn(raster);
		long time = System.currentTimeMillis(); 
		Point p = f.delete(raster);
		System.out.print("[SIMULATOR] "); printBashText("result get in  "+((System.currentTimeMillis()-time)/1000.0)+" sec\n", "38;5;44");
		if (p != null)
			raster.set(p.x, p.y, raster.get(p.x, p.y)+GELOESCHT);
		frame.refresh();
		try { Thread.sleep(1000); } catch (InterruptedException ie) { System.out.println("[FEHLER] "+ie); }
	}
}
