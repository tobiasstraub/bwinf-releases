/*
 * AiTester
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai;

import bwinf33_2.aufgabe2.Cone;
import bwinf33_2.aufgabe2.SceneData;
import bwinf33_2.aufgabe2.SceneRenderer;
import com.google.common.base.Stopwatch;
import org.reflections.Reflections;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import lombok.*;

/**
 * Diese Klasse testet die vorhandenen KIs mit verschiedenen zufälligen Zuständen.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(of = { "points", "time" })
public class AiTester implements AI, Comparable<AiTester>
{
	@NonNull
	@Getter
	private AI ai;

	@Getter
	private int points;

	@Getter
	@Setter
	private int lastpoints;

	@Getter
	private long time;

	@Getter
	@Setter
	private long lasttime;

	@Getter
	@Setter
	private Move lastmove;

	@Override
	public Move move (SceneData data)
	{
		return getAi().move(data);
	}

	@Override
	public String getName ()
	{
		return getAi().getName();
	}

	public int compareTo (@NonNull AiTester other)
	{
		return other.getPoints() - getPoints();
	}

	public static void main (String args[]) throws ReflectiveOperationException
	{
		// Die KIs suchen
		Reflections reflections = new Reflections("bwinf33_2.aufgabe2.ai.impl");
		Set<Class<? extends AI>> kiset = reflections.getSubTypesOf(AI.class);
		@SuppressWarnings("unchecked")
		Class<? extends AI>[] kiclasses = new Class[kiset.size()];
		int i = 0;
		for (Class<? extends AI> c : kiset)
		{
			kiclasses[i++] = c;
		}

		// Die KIs instantiieren
		AiTester ais[] = new AiTester[kiclasses.length];
		for (i = 0; i < kiclasses.length; i++)
		{
			ais[i] = new AiTester(kiclasses[i].newInstance());
		}

		// die Bilder in eine GIF-Datei schreiben
		LinkedList<String> images = new LinkedList<>();

		// KIs testen
		for (int r = 2; r <= 8; r++)
		{
			for (int c = 20; c <= 200; c += 20)
			{
				SceneData data = new SceneData(r, c);
				images.addLast(test(ais, data));

				Move m = new Move(Math.random() * Math.PI, 0, 0);
				Function<Double, Double> upper = SceneRenderer.getFunction(m, SceneRenderer.UPPER_FKT);
				Function<Double, Double> lower = SceneRenderer.getFunction(m, SceneRenderer.LOWER_FKT);
				data.getCones()
					.stream()
					.filter(cone -> (cone.getY() >= lower.apply(cone.getX()))
									&& (cone.getY() <= upper.apply(cone.getX())))
					.forEach(cone -> cone.setState(Cone.OVERTHROWN));
				images.addLast(test(ais, data));
			}
		}

		// Ergebnis ausgeben
		Arrays.sort(ais);
		for (AiTester ai : ais)
		{
			System.err.println("[AITESTER] »" + ai.getName() + "«: " + ai);
		}

		// den kompletten Test in einer GIF-Animation speichern
		try
		{
			String cmd[] = new String[images.size() + 6];
			cmd[0] = "convert";
			cmd[1] = "-loop";
			cmd[2] = "1";
			cmd[3] = "-delay";
			cmd[4] = "300";
			for (int j = 0; j < images.size(); j++)
			{
				cmd[j + 5] = images.get(j);
			}
			cmd[cmd.length - 1] = System.getProperty("user.home") + "/aitest-" + System.currentTimeMillis() + ".gif";
			System.out.println(Arrays.toString(cmd));
			Process p = new ProcessBuilder(cmd).start();
			System.out.println(p.waitFor());
			for (String image : images)
			{
				new File(image).delete();
			}
			new ProcessBuilder("xdg-open", cmd[cmd.length - 1]).start();
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private static String test (AiTester ais[], SceneData data)
	{
		Arrays.asList(ais).parallelStream().forEach(ai ->
		{
			Stopwatch stopwatch = new Stopwatch();
			Move m = null;
			try
			{
				stopwatch.start();
				m = ai.move(data);
				stopwatch.stop();
			}
			catch (Exception e)
			{
				System.out.println("[AITESTER] Die KI »" + ai.getName() + "« hat eine Exception geworfen:");
				e.printStackTrace(System.out);
			}
			int overthrown = 0;
			if (m != null)
			{
				Function<Double, Double> upper = SceneRenderer.getFunction(m, SceneRenderer.UPPER_FKT);
				Function<Double, Double> lower = SceneRenderer.getFunction(m, SceneRenderer.LOWER_FKT);

				for (Cone cone : data.getCones())
				{
					if (!cone.isOverthrown()
						&& (cone.getY() >= lower.apply(cone.getX()))
						&& (cone.getY() <= upper.apply(cone.getX())))
						overthrown++;
				}
			}
			System.err.println("[AITESTER] Die KI »" + ai.getName() + "« hat den Zug " + m
							   + " in " + stopwatch + " berechnet. Umgeworfene Kegel: " + overthrown);
			ai.setLasttime(stopwatch.elapsedMillis());
			ai.setLastpoints(overthrown);
			ai.setLastmove(m);
			ai.time += stopwatch.elapsedMillis();
			ai.points += overthrown;
		});
		return drawPicture(data, ais);
	}

	@SneakyThrows
	private static String drawPicture (SceneData data, AiTester ais[])
	{
		SceneRenderer renderer = new SceneRenderer(data);
		File tmp = File.createTempFile("aitest-", ".png");

		int gridsize = (int)Math.ceil(Math.sqrt(ais.length));
		BufferedImage img = new BufferedImage(gridsize * 300, gridsize * 350, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());

		Font f = new Font("SansSerif", Font.BOLD, 13);
		FontMetrics fm = g.getFontMetrics(f);

		for (int i = 0; i < ais.length; i++)
		{
			int row = i / gridsize;
			int col = i - row * gridsize;

			renderer.render(g, ais[i].getLastmove(), row * 300, col * 350, 300, 300);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.drawString(ais[i].getName(),
						 row * 300 + 10,
						 col * 350 + 310 + fm.getAscent());
			g.drawString(Integer.toString(ais[i].getLastpoints()),
						 row * 300 + 150 - fm.stringWidth(Integer.toString(ais[i].getLastpoints())) / 2f,
						 col * 350 + 310 + fm.getAscent());
			g.drawString(Long.toString(ais[i].getLasttime()),
						 row * 300 + 290 - fm.stringWidth(Long.toString(ais[i].getLasttime())),
						 col * 350 + 310 + fm.getAscent());
		}

		// Bild speichern
		ImageIO.write(img, "png", tmp);

		// Pfad zurückgeben
		return tmp.getAbsolutePath();
	}
}
