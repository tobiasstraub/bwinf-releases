package org.timschmidt.bwinf.a2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Kreis extends JPanel{
	
	public Kreis(){
		setDoubleBuffered(true);
	}

	public void paintComponent(Graphics g) {
		int zoom = Main.zoom,
				r = Main.getRadius();
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		// Hintergrund und Umrandung
		g2d.setColor(new Color(150,150,150));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
		
		// Mitte notieren
		Point mid = new Point((getWidth() >> 1), (getHeight() >> 1));
		
		// Kreis
		g2d.setColor(Color.WHITE);
		int x = mid.x - zoom*r,
			y = mid.y - zoom*r,
			hb = zoom*2*r; // Kreis => Höhe = Breite
		g2d.fillOval(x, y, hb, hb);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(x, y, hb, hb);
		
		// Wurfbahn
			g2d.setColor(Main.curSpieler.getBahnColor());
			
			// Linie
			Point2D.Double ziel = Main.curZiel;
			double winkel = Main.curWinkel; // TODO: Convert to new m and b shid
			Line2D.Double wurflinie = Utility.calcWurflinie(ziel, winkel, zoom);
			
			// Wurfbahn
			Path2D.Double wurfbahn = Utility.calcWurfbahn(wurflinie, winkel, zoom);
			
			
			// Zeichnen
			g2d.setColor(Main.curSpieler.getBahnColor());
			drawMid(wurfbahn, g2d, true);
			g2d.setColor(Color.BLACK);
			drawMid(wurflinie, g2d, false);
			
		// Kegel
			int inBahn = 0;
			for (Kegel k : Main.kegellist){
				if (!k.umgeworfen()){
					if (wurfbahn.contains(k)) {
						g.setColor(new Color(255,106,0,255));	// In der Wurfbahn befindliche Kegel anders einfärben
						inBahn++;								// Diese zählen
					} else
						g.setColor(new Color(75,0,255,255));
						
					g.fillRect((int)Math.round(mid.x + k.getX()-2), 
							(int)Math.round(mid.y + k.getY()-2), 
							5, 
							5);
				}
			}
			Main.gui.setE(inBahn);
	}
	
	@SuppressWarnings("unused")
	private double distance(double x1, double y1, double x2, double y2){
		double dX = x1-x2, dY = y1-y2;
		return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
	}
	
	private void drawMid(Shape shape, Graphics2D g2d, boolean fill){
		AffineTransform trOld = g2d.getTransform(),
				trNew = new AffineTransform(trOld);
		
		// Shape um die Hälfte der Containerbreite/-höhe verschieben
		trNew.translate(getWidth() >> 1,
				getHeight() >> 1);
		
		// Shape zeichnen
		g2d.setTransform(trNew);
		if (fill)
			g2d.fill(shape);
		else
			g2d.draw(shape);
		g2d.setTransform(trOld);
	}
	
	public void refresh(){
		revalidate();
		repaint();
	}
}
