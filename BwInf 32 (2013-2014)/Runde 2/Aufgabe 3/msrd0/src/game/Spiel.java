package game;

import java.util.Arrays;
import java.util.List;

public class Spiel
{
	public static class Zustand 
	{
		public List<DanceRobot> listeDanceRobot ()
		{
			return Arrays.asList(new DanceRobot());
		}
		
		public class DanceRobot
		{
			public int identifikation ()
			{
				return -1;
			}
			
			public boolean istVortaenzer ()
			{
				return false;
			}
			
			public String letzterTanz ()
			{
				return "fehler";
			}
			
			public int strafpunkte ()
			{
				return -1;
			}
		}
	}
	
	public static class Zug
	{
		public boolean ausgabe = true;
		public void ausgabe (String str)
		{
			if (ausgabe) System.out.println("[AUSGABE] "+str);
		}
		
		public void tanzen (String tanz)
		{
			if (tanz == null) throw new NullPointerException();
			this.tanz = tanz;
		}
		
		private String tanz;
		public String getTanz ()
		{
			while (tanz == null) Thread.yield();
			return tanz;
		}
	}
	
	public Zug zug()
	{
		return null;
	}

	public Zustand zustand()
	{
		return null;
	}
}
