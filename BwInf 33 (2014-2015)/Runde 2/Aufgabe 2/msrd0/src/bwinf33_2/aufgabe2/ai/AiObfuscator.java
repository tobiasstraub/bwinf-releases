/*
 * AiObfuscator
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 */
package bwinf33_2.aufgabe2.ai;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Cleanup;
import lombok.delombok.Delombok;

/**
 * Diese Klasse "obfuscated" den Source Code einer angegebenen KI in Code, den der BwInf Turnier Server lesen kann.
 * Dabei wird auch delombok aufgerufen, d.h. lombok-Annotations können in der KI benutzt werden. Kommentare, Leerzeilen
 * u.ä. werden entfernt; der Code-Style wird von delombok verändert, es wird versucht diesen einigermaßen
 * wiederherzustellen.
 */
public class AiObfuscator
{
	private HashSet<String> aiimports = new HashSet<>();

	public File obfuscate (File dir, String file, boolean ai) throws IOException
	{
		// delombok aufrufen
		Delombok delombok = new Delombok();
		delombok.setOutput(new File("/tmp"));
		delombok.addFile(dir, file);
		delombok.delombok();

		// IO-Zeugs
		BufferedReader in = new BufferedReader(new FileReader(new File("/tmp", file)));
		File tmp = File.createTempFile("ai-", ".java");
		PrintWriter out = new PrintWriter(new FileWriter(tmp));
		if (ai)
			out.println("// Obfuscated for the BwInf Turnier Server by AiObfuscator");

		String className = "kompletterbulshitnonsense"; // bitte nix kompletterbulshitnonsense nennen ;)
		String dataVarName = "kompletterbulshitnonsense";
		boolean inComment = false;
		String line;
		for (int linecount = 1; (line = in.readLine()) != null; linecount++)
		{
			//System.out.println("> " + line);
			//out.println("// " + file + ":" + linecount + ": " + line);

			// weitere benötigte Klassen einbinden
			Matcher m = Pattern.compile("\\s*//\\s*ai-import\\s+([a-zA-Z0-9_]+)").matcher(line);
			if (m.matches() && !aiimports.contains(m.group(1)))
			{
				aiimports.add(m.group(1));
				File f = obfuscate(dir, m.group(1) + ".java", false);
				Reader r = new FileReader(f);
				char buf[] = new char[8192];
				int read;
				while ((read = r.read(buf)) > 0)
				{
					out.write(buf, 0, read);
					out.flush();
				}

				r.close();
				f.delete();

				continue;
			}

			// Kommentare sind unnötig
			if (inComment)
			{
				m = Pattern.compile(".*\\*/(.*)").matcher(line);
				if (m.matches())
				{
					line = m.group(1);
					inComment = false;
				}
				else
					continue;
			}
			line = line.replaceAll("/\\*.*\\*/", "");
			m = Pattern.compile("(.*)/\\*.*$").matcher(line);
			if (m.matches())
			{
				line = m.group(1);
				inComment = true;
			}

			// nicht existierendes bzw. unnötiges Zeugs und singe-line Kommentare ignorieren
			line = line.replaceAll("package\\s+[^;]*;", "");
			line = line.replaceAll("import\\s+(bwinf33_2|lombok)[^;]*;", "");
			line = line.replaceAll("implements\\s+AI", "");
			line = line.replaceAll("(^|\\s+|\\(|@)java.(lang|beans).([A-Z])", "$1$3");
			line = line.replaceAll("@(Override|NonNull)", "");
			if (!ai || !line.contains("delombok")) // delombok-Kommentar leben lassen
				line = line.replaceAll("//.*$", "");

			// wegen Inkompetenz des Servers muss alles in float gerechnet werden
			//line = line.replace("Double.doubleToLongBits", "Float.floatToIntBits");
			//line = line.replace("double", "float");
			//line = line.replace("Double", "Float");
			//line = line.replaceAll("([0-9]+.[0-9]+)([^f]|$)", "$1f$2");
			//line = line.replaceAll("(Math\\.PI|Math\\.sqrt|Math\\.tan|Math\\.cos|Math\\.sin)", "(float)$1");

			// Klassen- und Methodennamen korrigieren
			line = line.replaceAll("System.out.println\\((.+)\\);", "zug.ausgabe(($1).toString());");
			line = line.replaceAll("(^|\\s+|\\(|<)" + className, "$1AI");
			line = line.replaceAll("(^|\\s+|\\(|<)Cone", "$1Spiel.Zustand.Kegel");
			line = line.replaceAll("(\\.|\\s+|\\()getX\\s*\\(\\s*\\)", "$1xKoordinate()");
			line = line.replaceAll("(\\.|\\s+|\\()getY\\s*\\(\\s*\\)", "$1yKoordinate()");
			line = line.replaceAll("(\\.|\\s+|\\()isOverthrown\\s*\\(\\s*\\)", "$1umgeworfen()");
			line = line.replaceAll(dataVarName + "\\.getCones\\(\\)", "zustand.listeKegel()");
			line = line.replaceAll(dataVarName + "\\.getRadius\\(\\)", "zustand.listeKreis().get(0).radius()");
			line = line.replaceAll("(\\s+|\\(|,)" + dataVarName + "(\\s+|,|\\.|\\))", "$1zustand$2");
			line = line.replaceAll("(\\.|\\s+|\\()getCones\\s*\\(\\s*\\)", "$1listeKegel()");
			line = line.replaceAll(
					"(\\s*)return\\s*\\(?\\s*new\\s+Move\\s*\\(\\s*([a-zA-Z0-9_\\.]+)\\s*,\\s*([a-zA-Z0-9_\\.]+)\\s*,\\s*([a-zA-Z0-9_\\.]+)\\s*\\)\\s*\\)?\\s*;",
					"$1{\n"
					+ "$1\tdouble _degree_angle = Math.toDegrees($2);\n"
					+ "$1\twhile (_degree_angle <= 0)\n$1\t{\n$1\t\t_degree_angle += 180.0;\n$1\t}\n"
					+ "$1\twhile (_degree_angle > 180)\n$1\t{\n$1\t\t_degree_angle -= 180.0;\n$1\t}\n"
					+ "$1\tint _degree_angle_int = (int)Math.round(_degree_angle);\n"
					+ "$1\tzug.ausgabe(\"zug.werfen(\" + _degree_angle_int + \", \" + $3 + \", \" + $4 + \");\");\n"
					+ "$1\tzug.werfen(_degree_angle_int, (float)$3, (float)$4);\n"
					+ "$1\treturn;\n"
					+ "$1}");

			// die Klasse muss AI heißen
			m = Pattern.compile("(.*)public\\s+class\\s+([a-zA-Z0-9_]+)(\\s|\\{)(.*)$").matcher(line);
			if (ai && m.matches())
			{
				className = m.group(2);
				line = m.group(1) + "public class AI /* obfuscated from " + className + " */" + m.group(3) + m.group(4);
			}

			// die Methode muss zug heißen
			m = Pattern.compile("(\\s*)public\\s+Move\\s+move\\s*\\(\\s*SceneData\\s+([a-zA-Z0-9_]+)\\s*\\)(.*)")
					   .matcher(line);
			if (ai && m.matches())
			{
				dataVarName = m.group(2);
				out.println(m.group(1) + "public void zug (int id, Spiel.Zustand zustand, Spiel.Zug zug)\n"
							+ m.group(1) + "{\n"
							+ m.group(1) + "\tlong time = System.currentTimeMillis();\n"
							+ m.group(1) + "\tzug0(id, zustand, zug);\n"
							+ m.group(1)
							+ "\tzug.ausgabe(\"Zug beendet, time consumed: \" + (System.currentTimeMillis() - time) + \" ms\");\n"
							+ m.group(1) + "}\n"
							+ m.group(1) + "@SuppressWarnings(\"all\")");
				line = m.group(1) + "public void zug0 (int id, Spiel.Zustand zustand, Spiel.Zug zug)" + m.group(3);
			}

			// alle weiteren SceneData-Onjekte ummatchen
			line = line.replaceAll("(^|\\s+|\\(|<)SceneData", "$1Spiel.Zustand");

			// Code-Sytle wiederherstellen
			if (line.trim().length() > 1)
			{
				line = line.replaceAll(
						"^(\\s*)(if|else if|else|for|while)\\s+(\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*[^\\(\\)]*\\)[^\\(\\)]*)*[^\\(\\)]*\\))\\s*([^;]*;)\\s*$",
						"$1$2 $3\n$1{\n$1\t$6\n$1}");
				line = line.replaceAll("^(\\s*)([^\\n]+)\\{([^\\n]+)\\}\\s*$", "$1$2\n$1{\n$1\t$3\n$1}");
				line = line.replaceAll("^(\\s*)([^\n]+)\\{\\s*$", "$1$2\n$1{");
			}

			// Leerzeile ignorieren
			if (!line.trim().isEmpty())
			{
				out.println(line);
				out.flush();
				//System.out.println("< " + line);
			}
		}

		System.err.println("Source file " + file + " successfully obfuscated.");

		out.close();
		in.close();

		return tmp;
	}

	public static void main (String args[]) throws IOException, InterruptedException
	{
		long time = System.currentTimeMillis();

		// KI-Klasse abfragen und obfuscaten
		String ai = args.length > 0
					? args[0]
					: JOptionPane.showInputDialog(null, "AI Class Name:", "Choose AI …", JOptionPane.QUESTION_MESSAGE);
		File tmp0 = new AiObfuscator().obfuscate(new File(System.getProperty("user.dir")
									   + "/src/bwinf33_2/aufgabe2/ai/impl/"), ai + ".java", true);

		// imports korrigieren
		HashSet<String> imports = new HashSet<>();
		imports.add("java.beans"); // @ConstructorProperties
		{
			@Cleanup
			BufferedReader in = new BufferedReader(new FileReader(tmp0));
			String line;
			Pattern p = Pattern.compile("\\s*import\\s+([^;]+)\\.([a-zA-Z0-9_]+|\\*);");
			while ((line = in.readLine()) != null)
			{
				Matcher m = p.matcher(line);
				if (m.matches())
					imports.add(m.group(1));
			}
		}
		File tmp = File.createTempFile("ai-", ".java");
		{
			@Cleanup
			BufferedReader in = new BufferedReader(new FileReader(tmp0));
			@Cleanup
			PrintWriter out = new PrintWriter(tmp);

			// die imports schreiben
			for (String pkg : imports)
			{
				out.println("import " + pkg + ".*;");
			}

			// den Rest schreiben
			String line;
			while ((line = in.readLine()) != null)
			{
				if (!line.trim().startsWith("import"))
					out.println(line);
			}
		}

		System.out.println("Obfuscating finished in " + (System.currentTimeMillis() - time) + " ms");

		// KI-Code öffnen
		Process p = new ProcessBuilder("xdg-open", tmp.getAbsolutePath()).start();
		p.waitFor();
	}
}
