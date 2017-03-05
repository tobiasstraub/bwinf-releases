using System;
using System.Text;

namespace Flogex.BWINF.Aufgabe1
{
    internal class Date
    {
        private int _Tag;

        public int Tag
        {
            get
            {
                return _Tag;
            }
            set
            {
                if (value > 0 && value <= 31)
                {
                    _Tag = value;
                }
                else
                {
                    throw new InvalidDateException("Tag existiert nicht");
                }
            }
        }

        private int _Monat;

        public int Monat
        {
            get
            {
                return _Monat;
            }
            set
            {
                if (value >= 1 && value <= 12)
                {
                    _Monat = value;
                }
                else
                {
                    throw new InvalidDateException("Monat existiert nicht");
                }
            }
        }

        public int Jahr { get; set; }

        public Date (int tag, int monat, int jahr)
        {
            Tag = tag;
            Monat = monat;
            Jahr = jahr;
        }

        public override string ToString ()
        {
            StringBuilder datum = new StringBuilder(Tag < 10 ? "0" + Tag + "." : Tag.ToString() + ".");
            datum.Append(Monat < 10 ? "0" + Monat + "." : Monat.ToString() + ".");
            datum.Append(Jahr.ToString());
            return datum.ToString();
        }

        /// <summary>
        /// Überprüft, ob zwei Date-Objekte in Tag und Monat übereinstimmen
        /// </summary>
        public override bool Equals (object obj)
        {
            Date d = (Date) obj;
            if (d == null)
                return false;

            return d.Tag == this.Tag &&
                d.Monat == this.Monat;
        }

        public static Date ErzeugeDummieDateMitKorrekterTagesdifferenz (int maximaleTagessdifferenz)
        {
            int tagesdifferenz = 0;
            Date dummieDate = new Date(1, 1, DateTime.Today.Year / 100 * 100);
            do
            {
                dummieDate.Jahr += 100;
                //Nur Jahr entscheidend
                tagesdifferenz = KalenderUmrechnung.BerechneTagesdifferenz(dummieDate);
            }
            while (tagesdifferenz < maximaleTagessdifferenz);
            return dummieDate;
        }
    }
}