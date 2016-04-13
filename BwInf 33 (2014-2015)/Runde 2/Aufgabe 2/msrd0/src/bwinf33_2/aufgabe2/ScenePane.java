/*
 * ScenePanel
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2;

import bwinf33_2.aufgabe2.ai.AI;
import bwinf33_2.aufgabe2.ai.Move;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Diese Klasse speichert ein SceneData-Object in einem SceneRenderer-Objekt und erweitert die Klasse JComponent um das
 * SceneData-Objekt in einer Swing-GUI darzustellen.
 */
public class ScenePane extends JComponent
{
	/**
	 * Der Standart-Rand, kann je nach Größe des Components variieren.
	 */
	private static final int defborder = 5;
	/**
	 * Der Standart-Zoom, kann je nach Größe des Compononts variieren.
	 */
	private static final int defzoom = 100;

	/**
	 * Der zum Zeichnen benutzte renderer. Speichert das SceneData-Objekt.
	 */
	private SceneRenderer renderer;

	/**
	 * Erstellt ein neues ScenePane mit dem angegebenen SceneData-Objekt.
	 */
	public ScenePane (@NonNull SceneData data)
	{
		renderer = new SceneRenderer(data);
	}

	/**
	 * Der aktuelle Mittelpunkt des Kreises.
	 */
	private Point2D.Double origin = new Point2D.Double();
	/**
	 * Der aktuelle Zoom.
	 */
	private double zoom;

	private MouseAdapter mouseAdapter;
	private Point lastClick;

	/**
	 * Der darzustellende Move.
	 */
	@Getter
	private Move move;

	/**
	 * Setzt den darzustellenden Move. Diese Methode ruft NICHT repaint auf!
	 */
	public void setMove (Move move)
	{
		if (this.move == null)
			this.move = move;
		else
		{
			synchronized (this.move)
			{
				this.move = move;
			}
		}
	}

	/**
	 * Wartet auf einen Mausklick und gibt die Koordinaten von diesem zurück.
	 */
	@SneakyThrows
	private Point getMouseClick ()
	{
		if (mouseAdapter == null)
		{
			mouseAdapter = new MouseAdapter()
			{
				@Override
				public void mouseClicked (MouseEvent e)
				{
					synchronized (ScenePane.this)
					{
						lastClick = new Point(e.getX(), e.getY());
						ScenePane.this.notifyAll();
					}
				}
			};
			addMouseListener(mouseAdapter);
		}

		synchronized (this)
		{
			lastClick = null;
			while (lastClick == null)
			{
				wait();
			}
			Graphics g = getGraphics();
			g.setColor(Color.CYAN);
			g.fillOval(lastClick.x - 5, lastClick.y - 5, 11, 11);
			return lastClick;
		}
	}

	/**
	 * Diese Methode erstellt eine KI, die vom Benutzer gesteuert wird.
	 */
	public AI createUserAi ()
	{
		return new AI()
		{
			@Override
			public Move move (SceneData data)
			{
				Point point = ScenePane.this.getMouseClick(), second = ScenePane.this.getMouseClick();
				double x, y, sx, sy;
				synchronized (origin)
				{
					x = (point.x - origin.x) / zoom;
					y = (point.y - origin.y) / zoom;
					sx = (second.x - origin.x) / zoom;
					sy = (second.y - origin.y) / zoom;
				}
				return new Move(Math.atan((y - sy) / (x - sx)), x, y);
			}

			@Override
			public String getName ()
			{
				return "Benutzer";
			}
		};
	}

	@Override
	protected void paintComponent (Graphics g1d)
	{
		super.paintComponent(g1d);
		Graphics2D g = (Graphics2D)g1d;

		// Hintergrund weiß zeichnen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// SceneRenderer den Rest machen lassen
		synchronized (origin)
		{
			zoom = (Math.min(getWidth(), getHeight()) - 2 * defborder) / (2 * renderer.getData().getRadius());
			origin = renderer.render(g, move, getWidth(), getHeight(), zoom);
		}
	}

	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension(
				Math.round(2 * renderer.getData().getRadius() * defzoom + 2 * defborder),
				Math.round(2 * renderer.getData().getRadius() * defzoom + 2 * defborder));
	}
}
