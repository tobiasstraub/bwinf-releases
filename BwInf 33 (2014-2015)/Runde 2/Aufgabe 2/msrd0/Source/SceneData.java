/*
 * SceneData
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Diese Klasse speichert Informationen über den aktuellen Zustand, nämlich die Kegel und den Radius.
 */
@NoArgsConstructor
@AllArgsConstructor
public class SceneData
{
	/**
	 * Die Kegel.
	 */
	private Cone[] data;

	/**
	 * Gibt alle Kegel zurück.
	 */
	public Collection<Cone> getCones ()
	{
		return Arrays.asList(data);
	}

	/**
	 * Der Radius des Kreises.
	 */
	@Getter
	private float radius;

	/**
	 * Erstellt einen neuen Zustand mit dem angegebenen Radius und der angegebenen Kegelanzahl. Die Kegel werden dabei
	 * zufällig verteilt.
	 */
	public SceneData (float radius, int cones)
	{
		randomCones(radius, cones);
	}

	/**
	 * Setzt den Radius und verteilt die angegebene Anzahl Kegel zufällig im Kreis.
	 */
	public void randomCones (float radius, int cones)
	{
		data = new Cone[cones];
		this.radius = radius;

		long time = System.currentTimeMillis();
		for (int i = 0; i < cones; i++)
		{
			Cone cone;

			// Kartesische Koordinaten zufällig in einem Rechteck wählen und überprüfen, dass sie im Kreis liegen.
			do
			{
				cone = new Cone(Math.random() * 2 * radius - radius, Math.random() * 2 * radius - radius);
			}
			// Mithilfe des Satz des Phytagoras überprüfen dass der Punkt im Kreis liegt
			while (cone.x * cone.x + cone.y * cone.y > radius * radius);

			data[i] = cone;
		}
		System.out.println("SceneData::randomCones(): " + cones + " random cones spawn, time consumed: "
						   + (System.currentTimeMillis() - time) + " ms");
	}

	/**
	 * Löscht alle Daten in diesem Objekt.
	 *
	 * @see bwinf33_2.aufgabe2.SceneData#isNull()
	 */
	public void clear ()
	{
		data = null;
	}

	/**
	 * Gibt true zurück, falls dieses Objekt aktuell keine Daten enthält.
	 *
	 * @see bwinf33_2.aufgabe2.SceneData#clear()
	 */
	public boolean isNull ()
	{
		return (data == null);
	}
}
