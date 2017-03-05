namespace Flogex.BWINF.Aufgabe1
{
    internal class JonesOsterformel
    {
        /// <summary>
        /// Berechnet das Datum im gregorianischen Kalender, an welchem Ostern im angegebenen Jahr stattfindet.
        /// </summary>
        /// <param name="jahr">Das Jahr, in welchem das gesuchte Datum liegt.</param>
        /// <returns>Gibt das Datum zurück, an dem Ostern im angegebenen Jahr stattfindet</returns>
        //Quelle: http://www.hib-wien.at/leute/wurban/mathematik/Ostern/Osterdatum.html
        public static Date BerechneGregorianischesOsterdatum (int jahr)
        {
            int a = jahr % 19;
            int b = jahr / 100;
            int c = jahr % 100;
            int d = b / 4;
            int e = b % 4;
            int f = (b + 8) / 25;
            int g = (b - f + 1) / 3;
            int h = (19 * a + b - d - g + 15) % 30;
            int i = c / 4;
            int j = c % 4;
            int k = (32 + 2 * e + 2 * i - h - j) % 7;
            int l = (a + 11 * h + 22 * k) / 451;
            int x = h + k - 7 * l + 114;
            int monat = x / 31;
            int tag = (x % 31) + 1;

            return new Date(tag, monat, jahr);
        }

        /// <summary>
        /// Berechnet das Datum im julianischen Kalender, an welchem Ostern im angegebenen Jahr stattfindet.
        /// </summary>
        /// <param name="jahr">Das Jahr, in welchem das gesuchte Datum liegt.</param>
        /// <returns>Gibt das Datum zurück, an dem Ostern im angegebenen Jahr stattfindet</returns>
        public static Date BerechneJulianischesOsterdatum (int jahr)
        {
            int a = jahr % 4;
            int b = jahr % 7;
            int c = jahr % 19;
            int d = (19 * c + 15) % 30;
            int e = (2 * a + 4 * b - d + 34) % 7;
            int x = d + e + 114;
            int monat = x / 31;
            int tag = (x % 31) + 1;

            return new Date(tag, monat, jahr);
        }
    }
}