package buschfeuer.impl;

import static buschfeuer.impl.Raster.BRAND;
import static buschfeuer.impl.Raster.WALD;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Diese Klasse enthält nützliche Methoden für Raster.
 * @author Dominic S. Meiser
 */
public class RasterUtils
{
	// keine Instanz nötig
	private RasterUtils () {}
	
	
	/**
	 * Gibt an, ob der Brand gelöscht wurde.
	 */
	public static boolean istGeloescht (Raster raster)
	{
		// Der Brand ist gelöscht, wenn sich keine Brandfläche mehr ausbreiten
		// kann.
		for (int i = 0; i < raster.getSize().width; i++)
		{
			for (int j = 0; j < raster.getSize().height; j++)
			{
				if (raster.get(i, j) == BRAND)
				{
					if (i > 0)
						if (raster.get(i-1, j) == WALD)
							return false;
					if (j > 0)
						if (raster.get(i, j-1) == WALD)
							return false;
					if (i < raster.getSize().width-1)
						if (raster.get(i+1, j) == WALD)
							return false;
					if (j < raster.getSize().height-1)
						if (raster.get(i, j+1) == WALD)
							return false;
				}
			}
		}
		return true;
	}
    
    
    /**
     * Läst das Raster weiterbrennen.
     */
	public static void burn (Raster raster)
	{
		// Zuerst das Array clonen, damit ich nicht die in Brand geratenen Waldstücke
		// nochmals weiter entzünden lasse
		Raster bevore = clone(raster);
		// Jetzt Wald entzünden
		for (int i = 0; i < bevore.getSize().width; i++)
		{
			for (int j = 0; j < bevore.getSize().height; j++)
			{
				if (bevore.get(i, j) == BRAND)
				{
					if (i > 0)
						if (bevore.get(i-1, j) == WALD)
							raster.set(i-1, j, BRAND);
					if (j > 0)
						if (bevore.get(i, j-1) == WALD)
							raster.set(i, j-1, BRAND);
					if (i < bevore.getSize().width-1)
						if (bevore.get(i+1, j) == WALD)
							raster.set(i+1, j, BRAND);
					if (j < bevore.getSize().height-1)
						if (bevore.get(i, j+1) == WALD)
							raster.set(i, j+1, BRAND);
				}
			}
		}
	}
    
    
    /**
     * Klont das Raster.
     */
    public static Raster clone (Raster raster0)
    {
    	Raster raster1 = new Raster(raster0.getSize().width, raster0.getSize().height, WALD);
    	for (int i = 0; i < raster0.getSize().width; i++)
    		for (int j = 0; j < raster0.getSize().height; j++)
    			raster1.set(i, j, raster0.get(i, j));
    	return raster1;
    }
    /**
     * Klont das Teilraster.
     */
    public static Teilraster clone (Teilraster raster0)
    {
    	Teilraster raster1 = new Teilraster(raster0.minx, raster0.maxx, raster0.miny, raster0.maxy, WALD);
    	for (int i = 0; i < raster0.getSize().width; i++)
    		for (int j = 0; j < raster0.getSize().height; j++)
    			raster1.set(i, j, raster0.get(i, j));
    	return raster1;
    }
    
    
    /**
     * Speichert das Raster. suggestions kann null sein.
     */
    public static void save (Raster raster, List<Point> suggestion, File file) throws IOException
    {
    	PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
    	
    	// 1. Zeile gibt an, ob ein Löschvorschlag enthalten ist
    	if (suggestion == null)
    		out.println(false);
    	else out.println(true);
    	
    	// Nächste Zeile enthält das Raster
    	out.print(raster.getSize().width+"\u2715"+raster.getSize().height); // Rastergröße
    	for (int i = 0; i < raster.getSize().width; i++)
    	{
    		out.print("\u204b");
    		for (int j = 0; j < raster.getSize().height; j++)
    		{
    			if (j != 0)
    				out.print("\u25c6");
    			out.print(raster.get(i, j));
    		}
    	}
    	out.println();
    	
    	// Evtl. den Löschvorgang schreiben
    	if (suggestion != null)
    	{
    		// Zuerst Anzahl der Lösungen
    		out.print(suggestion.size()+"\u204b");
    		// Jetzt die Lösungen
    		for (int i = 0; i < suggestion.size(); i++)
    		{
    			if (i != 0) out.print("\u25c6");
    			if (suggestion.get(i) == null) out.print("null");
    			else out.print(suggestion.get(i).x+"\u22b9"+suggestion.get(i).y);
    		}
    	}
    	
    	// Fertig
    	out.close();
    }
    
    /**
     * Lädt einen RasterFile. Der Benutzer wird gefragt welchen.
     */
    public static RasterFile load () throws IOException
    {
    	JFileChooser jfc = new JFileChooser ();
		jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
		jfc.setFileFilter(new FileNameExtensionFilter("Buschfeuer-Raster (*.bfr)", "bfr"));
		jfc.showOpenDialog(null);
		return load(jfc.getSelectedFile());
    }
    /**
     * Lädt den RasterFile unter file.
     */
    public static RasterFile load (File file) throws IOException
    {
    	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    	
    	Raster raster = null;
    	List<Point> solution = null;
    	
    	// 1. Zeile gibt an, ob ein Löschvorschlag vorhanden ist. Ist dem so, dann
    	// eine neue Liste dafür erzeugen
    	String line = in.readLine();
    	if (line == null) {	in.close();	throw new IOException("Unerwartetes EOF"); }
    	if (Boolean.parseBoolean(line))
    		solution = new LinkedList<>();
    	
    	// Als nächstes das Raster (2. Zeile) lesen
    	line = in.readLine();
    	if (line == null) {	in.close();	throw new IOException("Unerwartetes EOF"); }
        int width = Integer.parseInt(line.substring(0, line.indexOf('\u2715')));
        int height = Integer.parseInt(line.substring(line.indexOf('\u2715')+1, line.indexOf('\u204b')));
        raster = new Raster(width, height, WALD);
        for (int i = 0; i < width; i++)
        {
        	line = line.substring(line.indexOf('\u204b')+1);
        	for (int j = 0; j < height; j++)
        	{
        		if (j != height-1)
        			raster.set(i, j, Integer.parseInt(line.substring(0, line.indexOf('\u25c6'))));
        		else if (i != width-1)
        			raster.set(i, j, Integer.parseInt(line.substring(0, line.indexOf('\u204b'))));
        		else
        			raster.set(i, j, Integer.parseInt(line));
        		if (j != height-1)
        			line = line.substring(line.indexOf('\u25c6')+1);
        	}
        }
        
        // Falls gespeichert, den Löschvorschlag lesen
        if (solution != null)
        {
        	line = in.readLine();
        	if (line == null) {	in.close();	throw new IOException("Unerwartetes EOF"); }
        	
        	int count = Integer.parseInt(line.substring(0, line.indexOf('\u204b')));
        	line = line.substring(line.indexOf('\u204b')+1);
        	
        	for (int i = 0; i < count; i++)
        	{
        		if ((line.length() >= 4) && line.substring(0, 4).equals("null"))
        			solution.add(null);
        		else
        		{
        			int x = Integer.parseInt(line.substring(0, line.indexOf('\u22b9')));
        			int y;
        			if (i != count-1)
        				y = Integer.parseInt(line.substring(line.indexOf('\u22b9')+1, line.indexOf('\u25c6')));
        			else
        				y = Integer.parseInt(line.substring(line.indexOf('\u22b9')+1));
        			solution.add(new Point(x, y));
        		}
        		line = line.substring(line.indexOf('\u25c6')+1);
        	}
        }
        
        in.close();
        return new RasterFile(raster, solution);
    }
}
