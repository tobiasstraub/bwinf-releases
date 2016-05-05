
// Berechnet den größten gemeinsamen Teiler von a und b.
func euclid(a: Int, _ b: Int) -> Int {
    if b == 0 {
        return a
    }
    return euclid(b, a % b)
}

// Rational stellt eine rationale Zahl dar. Sie besteht aus einem Zähler und einem Nenner.
// Es kann auch ein gerundeter Wert verwendet werden.
struct Rational : CustomStringConvertible {
    
    let counter: Int
    let denominator: Int
    
    let rounded: Double?
    
    // Wandelt die rationale Zahl in einen String um. Wenn er als ganze Zahl geschrieben werden kann,
    // wird diese als String zurückgegeben. Ansonsten ist er in der Form "Zähler/Nenner".
    // Bei einem gerundeten Wert wird er mit 2 Nachkommastellen verwendet.
    var description: String {
        if let rounded = rounded {
            return String(format: "%.2f", rounded)
        } else if denominator == 1 {
            return String(counter)
        } else if counter == 0 {
            return "0"
        } else {
            return "\(counter)/\(denominator)"
        }
    }
    
    // Wandelt die rationale Zahl in einen String mit Prozent um. Davor steht entweder ein Gleichheitszeichen
    // oder ein Rundungszeichen, abhängig davon, ob die angegebene Zahl exakt ist.
    var equalsPrecisionPercentage: String {
        if let rounded = rounded {
            return "≈ \(String(format: "%.2f", rounded * 100))%"
        }
        let string = String(format: "%.2f", Double(counter * 100) / Double(denominator))
        if (Double(counter * 10000) / Double(denominator)) % 1 == 0 {
            return "= \(string)%"
        } else {
            return "≈ \(string)%"
        }
    }
    
    // Wandelt die rationale Zahl in einen String um. Davor steht entweder ein Gleichheitszeichen oder ein
    // Rundungszeichen, abhängig davon, ob die angegebene Zahl exakt ist.
    var equalsPrecision: String {
        if let rounded = rounded {
            return "≈ \(String(format: "%.2f", rounded))"
        }
        let string = String(format: "%.2f", Double(counter) / Double(denominator))
        if (Double(counter * 100) / Double(denominator)) % 1 == 0 {
            return "= \(string)"
        } else {
            return "≈ \(string)"
        }
    }
    
    init(counter: Int, denominator: Int) {
        self.counter = counter
        self.denominator = denominator
        self.rounded = nil
    }
    
    init(number: Int) {
        counter = number
        denominator = 1
        rounded = nil
    }
    
    init(rounded: Double) {
        counter = 0
        denominator = 0
        self.rounded = rounded
    }
    
    // Kürzt den Bruch.
    func reduce() -> Rational {
        let greatestCommonDivisor = euclid(counter, denominator)
        return Rational(counter: counter / greatestCommonDivisor, denominator: denominator / greatestCommonDivisor)
    }
    
}

// Zahlreiche Operatoren zum Rechnen mit Brüchen.
// Wenn Integer Overflow entsteht, wird der gerundete Wert
// als "Bruch" verwendet.

func *(a: Rational, b: Rational) -> Rational {
    if a.rounded != nil || b.rounded != nil {
        let aRounded = a.rounded ?? (Double(a.counter) / Double(a.denominator))
        let bRounded = b.rounded ?? (Double(b.counter) / Double(b.denominator))
        return Rational(rounded: aRounded * bRounded)
    }
    
    let (counter, overflowCounter) = Int.multiplyWithOverflow(a.counter, b.counter)
    let (denominator, overflowDenominator) = Int.multiplyWithOverflow(a.denominator, b.denominator)
    
    if overflowCounter || overflowDenominator {
        return Rational(rounded: (Double(a.counter) / Double(a.denominator)) * (Double(b.counter) / Double(b.denominator)))
    }
    
    return Rational(counter: counter, denominator: denominator).reduce()
}

func *(a: Rational, b: Int) -> Rational {
    return a * Rational(number: b)
}

func *=(inout a: Rational, b: Rational) {
    a = a * b
}

func /(a: Rational, b: Rational) -> Rational {
    if a.rounded != nil || b.rounded != nil {
        let aRounded = a.rounded ?? (Double(a.counter) / Double(a.denominator))
        let bRounded = b.rounded ?? (Double(b.counter) / Double(b.denominator))
        return Rational(rounded: aRounded / bRounded)
    }
    
    return a * Rational(counter: b.denominator, denominator: b.counter)
}

func /(a: Int, b: Rational) -> Rational {
    return Rational(number: a) / b
}

func +(a: Rational, b: Rational) -> Rational {
    if a.rounded != nil || b.rounded != nil {
        let aRounded = a.rounded ?? (Double(a.counter) / Double(a.denominator))
        let bRounded = b.rounded ?? (Double(b.counter) / Double(b.denominator))
        return Rational(rounded: aRounded + bRounded)
    }
    
    let (counter1, overflowCounter1) = Int.multiplyWithOverflow(a.counter, b.denominator)
    let (counter2, overflowCounter2) = Int.multiplyWithOverflow(b.counter, a.denominator)
    
    if overflowCounter1 || overflowCounter2 {
        return Rational(rounded: (Double(a.counter) / Double(a.denominator)) + (Double(b.counter) / Double(b.denominator)))
    }
    
    let (counter, overflowCounter) = Int.addWithOverflow(counter1, counter2)
    let (denominator, overflowDenominator) = Int.multiplyWithOverflow(a.denominator, b.denominator)
    
    if overflowCounter || overflowDenominator {
        return Rational(rounded: (Double(a.counter) / Double(a.denominator)) + (Double(b.counter) / Double(b.denominator)))
    }
    
    return Rational(counter: counter, denominator: denominator).reduce()
}

func +(a: Rational, b: Int) -> Rational {
    return a + Rational(number: b)
}

func +=(inout a: Rational, b: Rational) {
    a = a + b
}

func -(a: Rational, b: Rational) -> Rational {
    if a.rounded != nil || b.rounded != nil {
        let aRounded = a.rounded ?? (Double(a.counter) / Double(a.denominator))
        let bRounded = b.rounded ?? (Double(b.counter) / Double(b.denominator))
        return Rational(rounded: aRounded - bRounded)
    }
    
    let (counter1, overflowCounter1) = Int.multiplyWithOverflow(a.counter, b.denominator)
    let (counter2, overflowCounter2) = Int.multiplyWithOverflow(b.counter, a.denominator)
    
    if overflowCounter1 || overflowCounter2 {
        return Rational(rounded: (Double(a.counter) / Double(a.denominator)) - (Double(b.counter) / Double(b.denominator)))
    }
    
    let (counter, overflowCounter) = Int.subtractWithOverflow(counter1, counter2)
    let (denominator, overflowDenominator) = Int.multiplyWithOverflow(a.denominator, b.denominator)
    
    if overflowCounter || overflowDenominator {
        return Rational(rounded: (Double(a.counter) / Double(a.denominator)) - (Double(b.counter) / Double(b.denominator)))
    }
    
    return Rational(counter: counter, denominator: denominator).reduce()
}

func -(a: Int, b: Rational) -> Rational {
    return Rational(number: a) - b
}

func -=(inout a: Rational, b: Rational) {
    a = a - b
}

func ==(a: Rational, b: Rational) -> Bool {
    if a.rounded != nil || b.rounded != nil {
        let aRounded = a.rounded ?? (Double(a.counter) / Double(a.denominator))
        let bRounded = b.rounded ?? (Double(b.counter) / Double(b.denominator))
        return aRounded == bRounded
    }
    
    return a.counter == b.counter && a.denominator == b.denominator
}

func !=(a: Rational, b: Rational) -> Bool {
    return !(a == b)
}

func ==(a: Rational, b: Int) -> Bool {
    return a == Rational(number: b)
}

func !=(a: Rational, b: Int) -> Bool {
    return !(a == b)
}
