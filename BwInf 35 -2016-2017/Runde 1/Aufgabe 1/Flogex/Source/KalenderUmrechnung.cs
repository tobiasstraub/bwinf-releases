using System;

namespace Flogex.BWINF.Aufgabe1
{
    internal class KalenderUmrechnung
    {
        public static Date JulianischZuGregorianisch (Date datum)
        {
            return DatumKorrigieren_Jul_Greg(datum, BerechneTagesdifferenz(datum));
        }

        public static Date GregorianischZuJulianisch (Date datum)
        {
            return DatumKorrigieren_greg_jul(datum, BerechneTagesdifferenz(datum));
        }

        public static int BerechneTagesdifferenz (Date datum)
        {
            int jahrhundert = datum.Monat <= 2 ? (datum.Jahr - 1) / 100 : datum.Jahr / 100;
            int a = jahrhundert / 4;
            int b = jahrhundert % 4;
            int tagesdifferenz = 3 * a + b - 2;

            return tagesdifferenz;
        }

        private static Date DatumKorrigieren_greg_jul (Date datum, int tagesdifferenz)
        {
            int tag = datum.Tag - tagesdifferenz;
            int monat = datum.Monat;
            int jahr = datum.Jahr;

            while (tag <= 0)
            {
                // Schaltjahr in Februar muss extra betrachtet werden
                if (monat != 3)
                {
                    DateTime d = new DateTime(2016, monat, 15);
                    tag = DateTime.DaysInMonth(2016, d.AddMonths(-1).Month) + tag;
                }
                else
                {
                    if (jahr % 4 == 0) //julianische Schaltjahresregel
                    {
                        tag = 29 + tag;
                    }
                    else
                    {
                        tag = 28 + tag;
                    }
                }
                
                if (monat == 1)
                {
                    jahr--;
                    monat = 12;
                }
                else
                {
                    monat--;
                }
            }

            return new Date(tag, monat, jahr);
        }

        private static Date DatumKorrigieren_Jul_Greg (Date datum, int tagesdifferenz)
        {
            int tag = datum.Tag + tagesdifferenz;
            int monat = datum.Monat;
            int jahr = datum.Jahr;

            while (tag > (jahr % 4 == 0 ? 29 : 28) && monat == 2 ||
                tag > DateTime.DaysInMonth(2016, monat) && monat != 2)
            {
                if (monat == 2)
                {
                    //Julianisches Schaltjahr
                    tag = tag - (jahr % 4 == 0 ? 29 : 28);
                }
                else
                {
                    tag = tag - DateTime.DaysInMonth(2016, monat);
                }

                if (monat == 12)
                {
                    jahr++;
                    monat = 1;
                }
                else
                {
                    monat++;
                }
            }
            return new Date(tag, monat, jahr);
        }
    }
}