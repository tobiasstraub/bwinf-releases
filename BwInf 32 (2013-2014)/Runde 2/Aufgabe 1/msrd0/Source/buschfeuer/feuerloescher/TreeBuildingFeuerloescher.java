package buschfeuer.feuerloescher;

import static buschfeuer.impl.Raster.BRAND;
import static buschfeuer.impl.Raster.GELOESCHT;
import static buschfeuer.impl.Raster.WALD;
import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import buschfeuer.Feuerloescher;
import buschfeuer.impl.Raster;
import buschfeuer.impl.RasterUtils;
import buschfeuer.impl.RasterValue;

/**
 * Dieser Feuerlöscher durchsucht das Raster etwas intelligenter als der
 * Bruteforce-Feuerlöscher, indem es versucht die kürzeste und nicht die
 * erste Lösung zu finden. Der Bruteforce-Feuerlöscher durchsucht das Raster
 * etwa so:<br>
 * [p0]<br>
 * [p0, p1]<br>
 * [p0, p1, p2]<br>
 * [p0, p1, p2, p3]<br>
 * [p0, p1, p4]<br>
 * ...<br>
 * Hierbei findet er jetzt zum Beispiel zuerst die Lösung [p0, p1, p2, p3]
 * und erst später [p0, p1, p4]. Deshalb sucht dieser Feuerlöscher so:<br>
 * [p0]<br>
 * [p9]<br>
 * [p0, p1]<br>
 * [p0, p7]<br>
 * [p9, p8]<br>
 * [p9, p3]<br>
 * [p0, p1, p2]<br>
 * [p0, p1, p4]<br>
 * Jetzt hat er die beste Lösung gefunden und muss nicht die der Bruteforce-Feuerlöscher
 * jede weitere Möglichkeit durchsuchen. Dadurch ist dieser Feuerlöscher schneller,
 * aber auch anfälliger für OutOfMemoryErrors.
 * @author Dominic S. Meiser
 */
@SuppressWarnings("unchecked")
public class TreeBuildingFeuerloescher extends Feuerloescher
{
	protected List<Point> solution;
	protected int bestSolution = 0;
	protected int bestTree=0, treeIgnorance=20;
	
	public void init (Raster raster)
	{
		itSeek(raster);
		if (solution == null)
		{
			System.out.print("[ERGEBNIS] ");
			printBashText("failed (no solution)\n", "31");
			solution = new LinkedList<>();
			return;
		}
		System.out.println("[ERGEBNIS] "+solution);
		System.out.println("[ERGEBNIS] "+bestSolution);
		
		File file = new File(System.getProperty("java.io.tmpdir")+File.separator+"buschfeuer",
				"raster_treebuilding.bfr");
		try { RasterUtils.save(raster, solution, file); }
		catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	/**
	 * Diese Unterklasse von Point hat eine statische Liste aller bis jetzt gebrauchten
	 * Punkte. Es werden somit nur Punkte erzeugt, die noch nicht existieren.
	 * @author Dominic S. Meiser
	 */
	protected static class MemorySavePoint extends Point
	{
		private static final long serialVersionUID = -5208906917530817216L;
		
		/** Alle Punkte. */
		private static final List<MemorySavePoint> all = new LinkedList<>();
		
		/**
		 * Sucht den Punkt (x|y). Ist er noch nicht vorhanden wird er hinzugefügt.
		 * Anschließend wird er zurückgegeben.
		 */
		public static MemorySavePoint newPoint (int x, int y)
		{
			// Punkt suchen
			for (MemorySavePoint p : all)
				if ((p.x == x) && (p.y == y))
					return p;
			
			// Punkt erstellen
			MemorySavePoint p = new MemorySavePoint(x, y);
			all.add(p);
			return p;
		}
		
		/** Erstellt einen normalen Punkt. */
		private MemorySavePoint (int x, int y) { super(x, y); }
		
		/** Gibt eine (sehr kurze) Beschreiung des Punkts zurück. */
		public String toString ()
		{
			return "("+x+"|"+y+")";
		}
	}
	
	/**
	 * Diese Klasse speichert eine Möglichkeit zum nächsten Schritt in der
	 * Brandlöschung.
	 * @author Dominic S. Meiser
	 */
	protected static class Possibility
	{
		/** Die auf diese Möglichkeit folgenden Möglichkeiten. */
		public List<Possibility> nextPossibilities;
		/** Diese Möglichkeit. */
		public Point thisPossibility;
		
		/**
		 * Erzeugt eine neue Möglichkeit mit der angegebenen Position. Die
		 * Parameter werden in einen <code>MemorySavePoint</code> umgewandelt.
		 */
		public Possibility (int x, int y)
		{
			this(MemorySavePoint.newPoint(x, y));
		}
		/**
		 * Erzeugt eine neue Möglichkeit mit der angegebenen Position.
		 */
		public Possibility (MemorySavePoint p)
		{
			thisPossibility = p;
			nextPossibilities = new LinkedList<>();
		}
		/**
		 * Erzeugt eine neue Möglichkeit mit der angegebenen Position. Der
		 * Parameter wird in einen <code>MemorySavePoint</code> umgewandelt.
		 */
		public Possibility (Point p)
		{
			this(p.x, p.y);
		}
		/**
		 * Erzeugt eine neue Möglichkeit, bei der keine Position gespeichert
		 * wird.
		 */
		public Possibility ()
		{
			thisPossibility = null;
			nextPossibilities = new LinkedList<>();
		}
	}
	
	/**
	 * Dieser Frame zeigt dem Benutzer Informationen über den aktuell laufenden
	 * Prozess an.
	 * @author Dominic S. Meiser
	 */
	protected class ProcessFrame extends JFrame
	{
		private static final long serialVersionUID = -3450340340719622555L;
		
		private JLabel ebene, best, time, store;
		private JButton stop;
		private long startTime;
		
		public ProcessFrame ()
		{
			super("TreeBuildingFeuerlöscher - Prozess Informationen");
			
			JPanel cp = new JPanel ();
			SpringLayout layout = new SpringLayout();
			cp.setLayout(layout);
			
			ebene = new JLabel("Ebene: <unknown>");
			cp.add(ebene);
			layout.putConstraint(NORTH, ebene, 10, NORTH, cp);
			layout.putConstraint(WEST, ebene, 10, NORTH, cp);
			layout.putConstraint(EAST, ebene, -10, EAST, cp);
			
			best = new JLabel("Beste Lösung: <unknown>");
			cp.add(best);
			layout.putConstraint(NORTH, best, 10, SOUTH, ebene);
			layout.putConstraint(WEST, best, 10, NORTH, cp);
			layout.putConstraint(EAST, best, -10, EAST, cp);
			
			time = new JLabel("Verbrauchte Zeit: <unknown>");
			cp.add(time);
			layout.putConstraint(NORTH, time, 10, SOUTH, best);
			layout.putConstraint(WEST, time, 10, NORTH, cp);
			layout.putConstraint(EAST, time, -10, EAST, cp);
			
			store = new JLabel("Verbrauchter Arbeitsspeicher: <unknown>");
			cp.add(store);
			layout.putConstraint(NORTH, store, 10, SOUTH, time);
			layout.putConstraint(WEST, store, 10, NORTH, cp);
			layout.putConstraint(EAST, store, -10, EAST, cp);
			
			stop = new JButton("Aktuelle Lösung übernehmen");
			stop.addActionListener((ActionEvent e) -> poss.clear());
			cp.add(stop);
			layout.putConstraint(HORIZONTAL_CENTER, stop, 0, HORIZONTAL_CENTER, cp);
			layout.putConstraint(SOUTH, stop, -10, SOUTH, cp);
			
			setContentPane(cp);
			setSize(350,200);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
		
		public void open ()
		{
			startTime = System.currentTimeMillis();
			setAlwaysOnTop(true);
			setVisible(true);
			new Thread((() -> autoUpdate()), "ProcessInfo").start();
		}
		
		private void autoUpdate ()
		{
			while (isVisible())
			{
				best.setText("Beste Lösung: "+bestSolution);
				
				long d = (long) ((System.currentTimeMillis() - startTime) / 1000.0 / 60 / 60 / 24);
				long h = (long) ((System.currentTimeMillis() - startTime) / 1000.0 / 60 / 60 - d * 24);
				long m = (long) ((System.currentTimeMillis() - startTime) / 1000.0 / 60 - d * 24 * 60 - h * 60);
				long s = (long) ((System.currentTimeMillis() - startTime) / 1000.0 - d * 24 * 60 * 60 - h * 60 * 60 - m * 60);
				time.setText("Verbrauchte Zeit: "+(d<10 ? "0"+d : d)+":"+(h<10 ? "0"+h : h)+":"+(m<10 ? "0"+m : m)+":"+(s<10 ? "0"+s : s));
				
				if ((Runtime.getRuntime().totalMemory() >
						Integer.parseInt(System.getProperty("treebuilding.maxstore"))*1024*1024)
								&& (treeIgnorance > 5))
					treeIgnorance--;
				store.setText("Verbrauchter Arbeitsspeicher: "+(Runtime.getRuntime().totalMemory() / 1024.0 / 1024)+" MiB");
				
				try { Thread.sleep(500); } catch (Exception e) {}
			}
		}
		
		public void update (int depth)
		{
			ebene.setText("Ebene: "+depth);
		}
	}
	
	/** Das ProcessPane das den aktuellen Prozess visulaisiert. */
	protected final ProcessFrame processFrame = new ProcessFrame();
	
	/** Eine Liste, in der für jede Position verschiedene Punkte gespeichert werden.
	 * Hierbei handelt es sich um den Baum, der gebildet wird. */
	List<Possibility> poss = new LinkedList<>();
	/**
	 * Diese Methode sucht iterativ im Raster r nach Möglichkeiten. Dabei wird
	 * ein immer weiter wachsender Baum aufgestellt. Die beste Möglichkeit wird
	 * anschließend in solution gespeichert.. Zwischendurch wird die beste Lösung
	 * auch schon in solution gespeichert.
	 */
	protected void itSeek (Raster r)
	{
		processFrame.open();
		
		// Für verschiedene Tiefen im Baum durchloopen und Lösungen finden.
		for (int depth = 1; (depth!=1 ? poss.size()!=0 : true) &&
				(depth < r.getSize().width*r.getSize().height); depth++)
		{
			processFrame.update(depth);
			
			bestTree = -1;
			
			// Das Raster vorbereiten
			Raster raster = RasterUtils.clone(r);
			RasterUtils.burn(raster);
			
			// Beim 1. Mal müssen alle Möglichkeiten erstmal bereitgestellt werden
			if (depth == 1)
			{
				List<RasterValue> braende = getBraende(raster);
				for (RasterValue rv : braende)
					poss.add(new Possibility(rv.pos));
				
				continue;
			}
			
			// Sonst für jede Möglichkeit weitere Möglichkeiten erstellen und schauen ob
			// bei einer Möglichkeit der Brand gelöscht wurde. Wenn Möglichkeiten dabei
			// entfernt werden sollen dies tun.
			List<Possibility> remove = new LinkedList<>();
			for (Possibility p : poss)
			{
				Raster clone = RasterUtils.clone(raster);
				clone.set(p.thisPossibility.x, p.thisPossibility.y, BRAND+GELOESCHT);
				LinkedList<Point> added = new LinkedList<>();
				added.add(p.thisPossibility);
				if (!itSeek0(clone, p, added))
					remove.add(p);
			}
			for (Possibility p : remove)
				poss.remove(p);
		}
		
		// fertig
		processFrame.dispose();
	}
	/**
	 * Diese Methode sucht rekursiv die Enden von p und schaut welche weiteren Lösungen zur
	 * Verfügung stehen.  
	 * @param raster Das Raster. Es wird in dieser Methode verändert, es sollte also ein
	 * geklontes Raster übergeben werden.
	 * @return Ob p weiterhin in der Liste enthalten sein soll.
	 */
	protected boolean itSeek0 (Raster raster, Possibility p, LinkedList<Point> added)
	{
		// schauen, ob diese Möglichkeit noch benötigt wird
		int wald = raster.count(WALD);
//		System.out.println("[TREE] "+wald+": "+added);
		if (wald <= bestSolution)
			return false;
		
		if (p.thisPossibility != null)
			raster.set(p.thisPossibility.x, p.thisPossibility.y, BRAND+GELOESCHT);
		
		// Raster weiterbrennen lassen
		RasterUtils.burn(raster);
		
		// Wenn hier keine weitere Möglichkeit mehr ist, diese Möglichkeit begutachten
		if (p.nextPossibilities.size() == 0)
		{
			// Zuerst diese Möglichkeit in relation zur bisher besten bewerten 
			if (wald < bestTree-treeIgnorance)
				return false;
			if (wald > bestTree)
				bestTree = wald;
			
			
			// Wenn jetzt zu wenig Wald noch da ist, diese Möglichkeit verwerfen
			wald = raster.count(WALD);
			if (wald <= bestSolution) return false;
			
			// Weitere Möglichkeiten suchen
			List<RasterValue> braende = getBraende(raster);
			for (RasterValue rv : braende)
			{
				raster.set(rv.pos.x, rv.pos.y, BRAND+GELOESCHT);
				
				if (RasterUtils.istGeloescht(raster))
				{
					bestSolution = wald;
					added.add(rv.pos);
					solution = (List<Point>) added.clone();
					System.out.println("[GELÖSCHT] "+wald+": "+added);
					added.removeLast();
				}
				else p.nextPossibilities.add(new Possibility(rv.pos));
				
				raster.set(rv.pos.x, rv.pos.y, BRAND);
			}
		}
		
		// Ansonsten rekursiv weiter nach Enden suchen
		else
		{
			List<Possibility> remove = new LinkedList<>();
			
			for (Possibility p0 : p.nextPossibilities)
			{
				added.add(p0.thisPossibility);
				if (!itSeek0(RasterUtils.clone(raster), p0, added))
					remove.add(p0);
				added.removeLast();
			}
			
			// Die nicht mehr gebrauchten Möglichkeiten löschen
			for (Possibility p0 : remove)
				p.nextPossibilities.remove(p0);
			if (p.nextPossibilities.size() == 0)
				return false;
		}
		
		return true;
	}
	/**
	 * Diese Methode sucht alle Brandfelder mit mindestens einem Waldstück in der Umgebung und
	 * gibt eine Liste derer zurück.
	 */
	protected List<RasterValue> getBraende (Raster raster)
	{
		List<RasterValue> braende = new LinkedList<>();
		
		// Jedes Rasterfeld durchsuuchen
		for (int i = 0; i < raster.getSize().width; i++)
		{
			for (int j = 0; j < raster.getSize().height; j++)
			{
				// Brennt das Waldstück überhaupt?
				if (raster.get(i, j) == BRAND)
				{
					// nicht brenndenden Wald in Umgebung suchen
					int wald_in_umgebung = 0;
					if (i > 0)
						if (raster.get(i-1, j) == WALD)
							wald_in_umgebung++;
					if (j > 0)
						if (raster.get(i, j-1) == WALD)
							wald_in_umgebung++;
					if (i < raster.getSize().width-1)
						if (raster.get(i+1, j) == WALD)
							wald_in_umgebung++;
					if (j < raster.getSize().height-1)
						if (raster.get(i, j+1) == WALD)
							wald_in_umgebung++;
					// Brandstück hinzufügen, wenn mindestens 1 Waldstück in der
					// Umgebung liegt
					if (wald_in_umgebung != 0)
						braende.add(new RasterValue(i, j, BRAND, wald_in_umgebung));
				}
			}
		}
		
		return braende;
	}
	
	
	int i = -1;
	public Point delete (Raster raster)
	{
		if (++i >= solution.size()) return null;
		return solution.get(i);
	}
}
