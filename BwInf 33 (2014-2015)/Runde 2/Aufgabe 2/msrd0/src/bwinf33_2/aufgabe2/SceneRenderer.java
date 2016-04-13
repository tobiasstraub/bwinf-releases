/*
 * SceneRanderer
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2;

import bwinf33_2.aufgabe2.ai.Move;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import lombok.*;

/**
 * Diese Klasse dient zum Rendern eines SceneData-Objekts.
 */
@RequiredArgsConstructor
public class SceneRenderer
{
	public static final double UPPER_FKT = 1;
	public static final double MIDDLE_FKT = 0;
	public static final double LOWER_FKT = -1;

	/**
	 * Stellt die angegebene Geradengelcihung auf.
	 */
	public static Function<Double, Double> getFunction (@NonNull Move move, double z)
	{
		double angle = move.getAngle();
		while (angle > Math.PI / 2.0)
		{
			angle -= Math.PI;
		}
		while (angle < Math.PI / -2.0)
		{
			angle += Math.PI;
		}
		double a = Math.tan(angle);
		double b = move.getY() + z / Math.cos(angle) - a * move.getX();
		return (Double x) -> a * x + b;
	}

	@NonNull
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private SceneData data;

	/**
	 * Rendert das SceneData-Objekt in ein BufferedImage und gibt es zurück.
	 */
	public BufferedImage render ()
	{
		return render((Move)null);
	}

	/**
	 * Rendert das SceneData-Objekt und den angegebenen Move, insofern dieser nicht null ist, in ein
	 * BufferedImage ung gibt es zurück.
	 */
	public BufferedImage render (Move m)
	{
		int size = (int)Math.ceil(200 * data.getRadius());
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		render(img, m);
		return img;
	}

	/**
	 * Rendert das SceneData-Object in das übergebene BufferedImage.
	 */
	public void render (BufferedImage image)
	{
		render(image, null);
	}

	/**
	 * Rendert das SceneData-Objekt und den angegebenen Move, insofern dieser nicht null ist, in das
	 * übergebene BufferedImage.
	 */
	public void render (@NonNull BufferedImage image, Move m)
	{
		render(image.createGraphics(), m, image.getWidth(), image.getHeight());
	}

	/**
	 * Rendert das SceneData-Objekt in das übergebene Graphics-Object mit der angegebenen Breite und
	 * Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (Graphics2D g, double width, double height)
	{
		return render(g, null, width, height);
	}

	/**
	 * Rendert das SceneData-Objekt und den angegebenen Move, insofern dieser nicht null ist, in das
	 * übergebene Graphics-Object mit der angegebenen Breite und Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (Graphics2D g, Move m, double width, double height)
	{
		return render(g, m, 0, 0, width, height);
	}

	/**
	 * Rendert das SceneData-Objekt und den angegebenen Move, insofern dieser nicht null ist, in das
	 * übergebene Graphics-Object an der Stelle x0|y0 mit der angegebenen Breite und Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (Graphics2D g, Move m, double x0, double y0, double width, double height)
	{
		return render(g, m, x0, y0, width, height, Math.min(width, height) / (2 * data.getRadius()));
	}

	/**
	 * Rendert das SceneData-Objekt in das übergebene Graphics-Object mit der angegebenen Breite und
	 * Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (Graphics2D g, double width, double height, double zoom)
	{
		return render(g, null, width, height, zoom);
	}

	/**
	 * Rendert das SceneData-Object und den angegebenen Move, insofern dieser nicht null ist, in das
	 * übergebene Graphics-Object mit der angegebenen Breite und Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (Graphics2D g, Move m, double width, double height, double zoom)
	{
		return render(g, m, 0, 0, width, height, zoom);
	}

	/**
	 * Rendert das SceneData-Object und den angegebenen Move, insofern dieser nicht null ist, in das
	 * übergebene Graphics-Objectan der Stelle x0|y0 mit der angegebenen Breite und Höhe und dem angegebenen Zoom.
	 */
	public Point2D.Double render (@NonNull Graphics2D g, Move m, double x0, double y0, double width, double height,
								  double zoom)
	{
		// Hintergrund weiß malen
		g.setColor(Color.WHITE);
		g.fill(new Rectangle2D.Double(x0, y0, width, height));

		// Clippingbereich setzen
		Shape oldclip = g.getClip();
		g.setClip(new Rectangle2D.Double(x0, y0, width, height));

		// Werte berechnen
		double r = data.getRadius();
		double conesize = Math.max(5.0, zoom / 20.0);
		double xborder = (width - zoom * 2 * r) / 2.0;
		double yborder = (height - zoom * 2 * r) / 2.0;

		// ein Gitter mit dem Abstand des Radius zeichnen
		g.setColor(new Color(200, 200, 200, 100));
		for (int i = -(int)Math.floor(r); i <= r; i++)
		{
			double x = zoom(i, zoom, r, xborder);
			double y = zoom(i, zoom, r, yborder);
			g.draw(new Line2D.Double(x0 + x, y0 + yborder, x0 + x, y0 + yborder + r * 2 * zoom));
			g.draw(new Line2D.Double(x0 + xborder, y0 + y, x0 + xborder + r * 2 * zoom, y0 + y));
		}

		// wenn ein Move angegeben wurde, diesen zeichenen
		if (m != null)
		{
			// die Funktionen der drei Geraden herausfinden
			Function<Double, Double> upper = getFunction(m, UPPER_FKT);
			Function<Double, Double> middle = getFunction(m, MIDDLE_FKT);
			Function<Double, Double> lower = getFunction(m, LOWER_FKT);

			// Den grauen Bereich malen
			GeneralPath path = new GeneralPath();
			path.moveTo(x0 + xborder, y0 + zoom(upper.apply(-r), zoom, r, yborder));
			path.lineTo(x0 + xborder + r * zoom * 2, y0 + zoom(upper.apply((double)r), zoom, r, yborder));
			path.lineTo(x0 + xborder + r * zoom * 2, y0 + zoom(lower.apply((double)r), zoom, r, yborder));
			path.lineTo(x0 + xborder, y0 + zoom(lower.apply(-(double)r), zoom, r, yborder));
			path.lineTo(x0 + xborder, y0 + zoom(upper.apply(-(double)r), zoom, r, yborder));
			g.setColor(new Color(150, 150, 150, 100));
			g.fill(path);

			// Die Linie durch den Punkt, wo die Kugel hingeworfen wird, zeichnen
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke((float)conesize));
			g.draw(new Line2D.Double(x0 + xborder, y0 + zoom(middle.apply(-(double)r), zoom, r, yborder),
									 x0 + xborder + r * zoom * 2, y0 + zoom(middle.apply((double)r), zoom, r, yborder)));

			// Den Punkt, wo die Kugel hingeworfen wird, zeichnen
			g.setColor(Color.GREEN);
			g.fill(new Ellipse2D.Double(x0 + zoom(m.getX(), zoom, r, xborder) - conesize,
										y0 + zoom(m.getY(), zoom, r, yborder) - conesize,
										conesize * 2, conesize * 2));

		}

		// den Kreis zeichnen
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2f));
		g.draw(new Ellipse2D.Double(x0 + xborder, y0 + yborder,
									2 * data.getRadius() * zoom, 2 * data.getRadius() * zoom));

		// die Kegel zeichnen
		for (Cone d : data.getCones())
		{
			switch (d.getState())
			{
				case Cone.STANDING:
					g.setColor(Color.BLUE);
					break;
				case Cone.OVERTHROWING:
					g.setColor(Color.RED);
					break;
				default:
					continue;
			}
			g.fill(new Ellipse2D.Double(x0 + zoom(d.x, zoom, data.getRadius(), xborder) - conesize / 2.0,
										y0 + zoom(d.y, zoom, data.getRadius(), yborder) - conesize / 2.0,
										conesize, conesize));
		}

		// den alten Clippingbereich wiederherstellen
		g.setClip(oldclip);

		// den Mittelpunkt des Kreises zurückgeben
		return new Point2D.Double(x0 + xborder + r * zoom, y0 + yborder + r * zoom);
	}

	/**
	 * Diese Methode zoomt den Punkt d und verschiebt den Koordinatenursprung an die richtige Stelle.
	 */
	private double zoom (double d, double zoom, double radius, double border)
	{
		return (d * zoom + radius * zoom + border);
	}
}
