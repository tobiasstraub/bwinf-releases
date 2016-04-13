package buschfeuer.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Diese Klasse stellt ein Raster grafisch dar.
 * @author Dominic S. Meiser
 */
public class RasterPane extends JPanel
{
	private static final long serialVersionUID = 8031719145627072551L;
	
	public RasterPane (Raster r)
	{
		super();
		if (r == null)
			throw new NullPointerException("Brauche ein Raster zum Darstellen");
		raster = r;
		
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked (MouseEvent e)
			{
				// Koordinaten im Raster ermitteln
				int i = e.getX() / rasterSize;
				int j = e.getY() / rasterSize;
				// Jetzt Elemente vertauschen
				if (raster.get(i, j) == replacement0)
					raster.set(i, j, replacement1);
				else if (raster.get(i, j) == replacement1)
					raster.set(i, j, replacement0);
				// Änderungen einzeichnen
				repaint();
			}
		});
	}
	private Raster raster;
	
	private int replacement0=-1, replacement1=-1;
	/** Ändert die Werte, die der Benutzer vertauschen darf. */
	public void setReplacement (int r0, int r1)
	{
		replacement0 = r0;
		replacement1 = r1;
	}
	
	int imgcount = 0;
	/** Zeichnet das Raster auf den Bildschirm und in eine Datei im tmpdir. */
	protected void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		paintComponent(img.createGraphics());
		g.drawImage(img, 0, 0, null);
		try { ImageIO.write(img, "png", new File(System.getProperty("java.io.tmpdir")+File.separator+
				"buschfeuer", "img_"+(++imgcount)+".png")); }
		catch (IOException ioe) { ioe.printStackTrace(); }
	}
	/** Zeichnet das Raster. */
	protected void paintComponent (Graphics2D g)
	{
		super.paintComponent(g);
		
		// Felder einzeichnen
		for (int i = 0; i < raster.getSize().width; i++)
			for (int j = 0; j < raster.getSize().height; j++)
				draw(g, i, j);
		
		// Raster aufmalen
		g.setColor(new Color(0x000000));
		for (int i = rasterSize; i < getWidth(); i+=rasterSize)
			g.draw(new Line2D.Float(i, 0, i, getHeight()));
		for (int i = rasterSize; i < getHeight(); i+=rasterSize)
			g.draw(new Line2D.Float(0, i, getWidth(), i));
	}
	/** Zeichnet den Rasterwert an der angegebenen Position. */
	private void draw (Graphics2D g, int i, int j)
	{
		int value = raster.get(i, j);
		boolean geloescht = false;
		if (value > Raster.GELOESCHT) // GELOESCHT ist der größte Wert
		{
			value -= Raster.GELOESCHT;
			geloescht = true;
		}
		
		switch (value)
		{
			case Raster.WALD: g.setColor(new Color(0x00aa00)); break;
			case Raster.SCHNEISE: g.setColor(new Color(0xcccccc)); break;
			case Raster.BRAND: g.setColor(new Color(0xff0000)); break;
			default: g.setColor(new Color(0x000000));
		}
		g.fill(new Rectangle2D.Float(i*rasterSize, j*rasterSize, rasterSize, rasterSize));
		
		if (geloescht)
		{
			g.setColor(new Color(0x0055ff));
			g.fill(new Rectangle2D.Float(i*rasterSize+3, j*rasterSize+3, rasterSize-6, rasterSize-6));
		}
	}
	private static final int rasterSize = 20;
	
	/** Gibt die gewünschte Größe zurück. Dies ist abhängig von der Rastergröße */
	public Dimension getPreferredSize ()
	{
		return new Dimension(raster.getSize().width*rasterSize, raster.getSize().height*rasterSize);
	}
}
