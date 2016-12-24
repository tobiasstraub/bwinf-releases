using System;

namespace Flogex.BWINF.Aufgabe1
{
    [Serializable]
    public class InvalidDateException : Exception
    {
        public InvalidDateException () : base("Datum existiert nicht")
        {
        }

        public InvalidDateException (string message) : base(message)
        {
        }

        public InvalidDateException (string message, Exception inner) : base(message, inner)
        {
        }

        protected InvalidDateException (
          System.Runtime.Serialization.SerializationInfo info,
          System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
    }
}