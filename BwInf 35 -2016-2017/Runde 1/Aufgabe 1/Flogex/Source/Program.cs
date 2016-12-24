using System;

namespace Flogex.BWINF.Aufgabe1
{
    internal class Program
    {
        public static void Main (string[] args)
        {
            Console.Title = "BWINF - Aufgabe 1: Sprichwort";

            #region Teilaufgabe 1

            Console.WriteLine("Teilaufgabe 1:");

            Date weihnachten = new Date(25, 12, DateTime.Today.Year);
            Date dummieDate = Date.ErzeugeDummieDateMitKorrekterTagesdifferenz(87);

            Date osternNachGregorianischemKalender = JonesOsterformel.BerechneGregorianischesOsterdatum(dummieDate.Jahr);
            Date osternNachJulianischemKalender = KalenderUmrechnung.GregorianischZuJulianisch(osternNachGregorianischemKalender);

            while (!osternNachJulianischemKalender.Equals(weihnachten))
            {
                dummieDate.Jahr++;
                osternNachGregorianischemKalender = JonesOsterformel.BerechneGregorianischesOsterdatum(dummieDate.Jahr);
                osternNachJulianischemKalender = KalenderUmrechnung.GregorianischZuJulianisch(osternNachGregorianischemKalender);
            }

            Console.WriteLine("Julianischer Kalender (Weihnachten): " + osternNachJulianischemKalender);
            Console.WriteLine("Gregorianischer Kalender (Ostern): " + osternNachGregorianischemKalender);

            #endregion Teilaufgabe 1

            #region Teilaufgabe 2

            Console.WriteLine();
            Console.WriteLine("Teilaufgabe 2:");

            dummieDate = Date.ErzeugeDummieDateMitKorrekterTagesdifferenz(244);

            osternNachJulianischemKalender = JonesOsterformel.BerechneJulianischesOsterdatum(dummieDate.Jahr);
            osternNachGregorianischemKalender = KalenderUmrechnung.JulianischZuGregorianisch(osternNachJulianischemKalender);

            while (!osternNachGregorianischemKalender.Equals(weihnachten))
            {
                dummieDate.Jahr++;
                osternNachJulianischemKalender = JonesOsterformel.BerechneJulianischesOsterdatum(dummieDate.Jahr);
                osternNachGregorianischemKalender = KalenderUmrechnung.JulianischZuGregorianisch(osternNachJulianischemKalender);
            }
            
            Console.WriteLine("Gregorianischer Kalender (Weihnachten): " + osternNachGregorianischemKalender);
            Console.WriteLine("Julianischer Kalender (Ostern): " + osternNachJulianischemKalender);

            #endregion Teilaufgabe 2

            Console.WriteLine("Zum Beenden beliebige Taste drücken...");
            Console.ReadKey();
        }
    }
}