/*
 * Simulator
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2;

import bwinf33_2.aufgabe2.ai.AI;
import bwinf33_2.aufgabe2.ai.Move;
import com.google.common.base.Stopwatch;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import lombok.*;

/**
 * Diese Klasse ist der Simulator des Spiels. Er nimmt die SceneData und die beiden
 * KIs entgegen und simuliert mit startSimulation() das Spiel. Dabei werden die
 * übergebenen grafischen Elemente korrekt geupdated.
 */
@RequiredArgsConstructor
public class Simulator
{
	/**
	 * Die SceneData mit den Spieldaten.
	 */
	@Getter
	@NonNull
	private SceneData data;

	/**
	 * Die mitspielenden KIs.
	 */
	@NonNull
	private AI ai0, ai1;

	/**
	 * Die Punkte der KIs.
	 */
	@Getter
	private int points0, points1;

	/**
	 * Die verbrauchte Rechenzeit der KIs.
	 */
	@Getter
	private long time0, time1;

	/**
	 * Startet die Simulation mit den dem Konstruktor übergebenen Daten und KIs. Dabei
	 * werden die dieser Methode als Parameter übergebenen grafischen Elemente
	 * entsprechend geupdated.
	 */
	public void startSimulation (ScenePane scene, JLabel result0, JLabel result1) throws InterruptedException
	{
		// herausfinden, wie viele Kegel vorhanden sind. Dabei wird vorrausgesetzt, dass noch alle Kegel stehen
		int standing = data.getCones().size();

		// die Bilder in eine GIF-Datei schreiben
		LinkedList<String> images = new LinkedList<>();
		SceneRenderer renderer = new SceneRenderer(data);

		boolean turn = true;
		int i;
		for (i = 0; standing > 0; i++)
		{
			// die Hintergrundfarbe der Labels zeigt an, welche KI gerade am Zug ist
			if (turn)
			{
				result0.setBackground(Color.RED);
				result1.setBackground(Color.WHITE);
			}
			else
			{
				result0.setBackground(Color.WHITE);
				result1.setBackground(Color.RED);
			}
			result0.repaint();
			result1.repaint();

			// die KI aufrufen
			Stopwatch stopwatch = new Stopwatch().start();
			Move move = (turn ? ai0 : ai1).move(data);
			System.out.println(
					"[SIMULATOR] KI »" + (turn ? ai0 : ai1).getName() + "« hat den Zug " + move
					+ " in " + stopwatch.stop() + " berechnet.");
			if (move == null
				|| move.getX() * move.getX() + move.getY() * move.getY() > data.getRadius() * data.getRadius() * 1.01)
				System.out.println("[SIMULATOR] KI »" + (turn ? ai0 : ai1).getName()
								   + "« hat einen ungültigen Wurf abgegeben.");
			// speichert die umgeworfenen Kegel
			Set<Cone> overthrown = new HashSet<>();

			if (move != null)
			{
				// die beiden Funktionen für den Zug herausfinden
				Function<Double, Double> upper = SceneRenderer.getFunction(move, SceneRenderer.UPPER_FKT);
				Function<Double, Double> lower = SceneRenderer.getFunction(move, SceneRenderer.LOWER_FKT);

				// durch jeden Kegel durchloopen und evtl. umschmeißen
				for (Cone cone : data.getCones())
				{
					if (cone.isOverthrown())
						continue;

					if ((cone.getY() <= upper.apply(cone.getX())) && (cone.getY() >= lower.apply(cone.getX())))
						overthrown.add(cone);
				}
			}

			// das aktuelle Bild rendern
			images.addLast(drawPicture(renderer, i));

			// die Punkte updaten
			if (turn)
			{
				points0 += overthrown.size();
				time0 += stopwatch.elapsedMillis();
				result0.setText(Integer.toString(points0));
			}
			else
			{
				points1 += overthrown.size();
				time1 += stopwatch.elapsedMillis();
				result1.setText(Integer.toString(points1));
			}

			// speichern, wieviele Kegel noch stehen
			standing -= overthrown.size();
			System.out.println("Overthrown: " + overthrown.size() + "\n\n");

			// den Status der umgeworfenen Kegel updaten, die GUI neu rendern, und das Bild speichern
			for (Cone c : overthrown)
			{
				c.setState(Cone.OVERTHROWING);
			}
			images.addLast(drawPicture(renderer, move, i));
			scene.setMove(move);
			scene.repaint();
			Thread.sleep(1000);

			for (Cone c : overthrown)
			{
				c.setState(Cone.OVERTHROWN);
			}

			scene.setMove(null);
			scene.repaint();
			Thread.sleep(1000);

			// die nächste KI ist dran
			turn = !turn;
		}

		// anzeigen, welche KI gewonnen hat
		if (points0 >= points1)
			result0.setBackground(Color.GREEN);
		else
			result0.setBackground(Color.WHITE);
		if (points1 >= points0)
			result1.setBackground(Color.GREEN);
		else
			result1.setBackground(Color.WHITE);
		result0.repaint();
		result1.repaint();

		// das Ergebnis ausgeben
		if (points0 == points1)
			System.out.println(
					"[SIMULATOR] Die beiden KIs »" + ai0.getName() + "« (" + time0 / 1000.0
					+ " sec) und »" + ai1.getName() + "« (" + time1 / 1000.0
					+ " sec) haben mit jeweils " + points0 + " Punkten einen Gleichstand erzielt.");
		else
			System.out.println(
					"[SIMULATOR] Die KI »" + (points0 > points1 ? ai0 : ai1).getName() + "« ("
					+ (points0 > points1 ? time0 : time1) / 1000.0 + " sec) hat mit "
					+ points0 + ":" + points1 + " gegen die KI »"
					+ (points0 > points1 ? ai1 : ai0).getName() + "« ("
					+ (points0 > points1 ? time1 : time0) / 1000.0 + " sec) gewonnen");

		// drei leere Bilder hinzufügen, um das Ende etwas länger zu machen
		for (int j = 0; j < 3; j++)
		{
			images.addLast(drawPicture(renderer, i));
		}

		// das komplette Spiel in einer GIF-Animation speichern
		try
		{
			String cmd[] = new String[images.size() + 6];
			cmd[0] = "convert";
			cmd[1] = "-loop";
			cmd[2] = "0";
			cmd[3] = "-delay";
			cmd[4] = "200";
			for (int j = 0; j < images.size(); j++)
			{
				cmd[j + 5] = images.get(j);
			}
			cmd[cmd.length - 1] = System.getProperty("user.home") + "/game-"
								  + ai0.getName() + "-" + ai1.getName()
								  + "-" + System.currentTimeMillis() + ".gif";
			System.out.println(Arrays.toString(cmd));
			Process p = new ProcessBuilder(cmd).start();
			System.out.println(p.waitFor());
			for (String image : images)
			{
				new File(image).delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Malt ein Bild des aktuellen Zustands mithilfe des angegebenen SceneRenderer's und speichert es lokal ab.
	 *
	 * @param renderer
	 * 		Der SceneRenderer mit dem das Bild gezeichnet werden soll.
	 * @param i
	 * 		Die Nummer der aktuellen Runde.
	 * @return Den Pfad zur Bilddatei.
	 */
	private String drawPicture (SceneRenderer renderer, int i)
	{
		return drawPicture(renderer, null, i);
	}

	/**
	 * Malt ein Bild des aktuellen Zustands und des angegebenen Move, insofern dieser nicht null ist, mithilfe des
	 * angegebenen SceneRenderer's und speichert es lokal ab.
	 *
	 * @param renderer
	 * 		Der SceneRenderer mit dem das Bild gezeichnet werden soll.
	 * @param m
	 * 		Der zu zeichnende Move.
	 * @param i
	 * 		Die Nummer der aktuellen Runde.
	 * @return Den Pfad zur Bilddatei
	 */
	@SneakyThrows
	private String drawPicture (@NonNull SceneRenderer renderer, Move m, int i)
	{
		File tmp = File.createTempFile("panoramakegeln-", ".png");

		// Bild erstellen und den SceneRenderer aufrufen
		BufferedImage img = new BufferedImage(800, 900, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		renderer.render(g, m, 800, 800);
		g.setColor(Color.BLACK);
		g.fillRect(0, 800, 800, 100);

		// Fonts bereitstellen
		Font f0 = new Font("SansSerif", Font.BOLD, 17);
		Font f1 = new Font("Monospaced", Font.BOLD, 30);
		Font f2 = new Font("SansSerif", Font.PLAIN, 13);
		FontMetrics fm0 = g.getFontMetrics(f0);
		FontMetrics fm1 = g.getFontMetrics(f1);
		FontMetrics fm2 = g.getFontMetrics(f2);

		// zusätzliche Informationen schreiben
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(1));
		g.fillRect(5, 805, 790, 10 + fm0.getHeight());
		g.fillRect(300, 805, 200, 20 + fm1.getHeight());
		g.setColor(Color.BLACK);
		g.setFont(f0);
		g.drawString(ai0.getName(),
					 10, 810 + fm0.getAscent());
		g.drawString(ai1.getName(),
					 790 - fm0.stringWidth(ai1.getName()), 810 + fm0.getAscent());
		g.setFont(f1);
		g.drawString(Integer.toString(getPoints0()),
					 390 - fm1.stringWidth(Integer.toString(getPoints0())), 815 + fm1.getAscent());
		g.drawString(":", 400 - fm1.charWidth(':') / 2, 815 + fm1.getAscent());
		g.drawString(Integer.toString(getPoints1()),
					 410, 815 + fm1.getAscent());
		g.setColor(Color.WHITE);
		g.setFont(f2);
		g.drawString(Long.toString(getTime0()),
					 15, 820 + fm0.getHeight() + fm2.getAscent());
		g.drawString(Long.toString(getTime1()),
					 785 - fm2.stringWidth(Long.toString(getTime1())), 820 + fm0.getHeight() + fm2.getAscent());
		String info = "Radius: " + data.getRadius() + " - Kegel: " + data.getCones().size() + " - Runde: " + i
					  + " - Verbrauchte Rechenzeit: " + (getTime0() + getTime1());
		g.drawString(info, 400 - fm2.stringWidth(info) / 2, 890 - fm2.getDescent());

		// Bild speichern
		ImageIO.write(img, "png", tmp);

		// Pfad zurückgeben
		return tmp.getAbsolutePath();
	}
}
