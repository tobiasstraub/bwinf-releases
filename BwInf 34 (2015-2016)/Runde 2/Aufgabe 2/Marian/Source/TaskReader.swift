import Foundation

// Ein Fehlertyp vom TaskReader, der die Daten einliest
enum TaskReaderError: ErrorType {
    case FileNotFound
    case InputUnavailable
    case FileEmpty
    case UnknownCharacter
    case InvalidParameters
    
    // Die Fehlernachricht
    var message: String {
        switch self {
        case .FileNotFound: return "Datei wurde nicht gefunden."
        case .InputUnavailable: return "Ein Fehler mit der Eingabe ist aufgetreten."
        case .FileEmpty: return "Die Datei ist leer."
        case .UnknownCharacter: return "Zeichen wurde nicht erkannt."
        case .InvalidParameters: return "Ungültige Parameter."
        }
    }
}

// Liest die Daten ein
class TaskReader {
    
    // Liest die Zeichenkette ein, die in der Datei ist, dessen Pfad der Nutzer eingibt
    func readString() throws -> String {
        print("Pfad zur Datei mit der Eingabe eingeben:")
        
        // Konsoleneingabe lesen:
        if let path = readLine() {
            // '~' durch den entsprechenden Pfad ersetzen und eingegebenen Pfad trimmen
            let location = NSString(string: path).stringByExpandingTildeInPath.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet()).stringByReplacingOccurrencesOfString("\\ ", withString: " ")
            
            if let string = try? NSString(contentsOfFile: location as String, encoding: NSUTF8StringEncoding) {
                // Datei konnte gelesen werden
                return string as String
            } else {
                // Datei wurde nicht gefunden
                throw TaskReaderError.FileNotFound
            }
        } else {
            throw TaskReaderError.InputUnavailable
        }
    }
    
    // Liest eine Datei, die der Nutzer eingibt sowie Parameter und erstellt daraus eine Aufgabe
    func read() -> Task {
        while true {
            // Immer wieder ausführen, wenn die Eingabe nicht gelesen werden konnte
            do {
                let input = try readString()
                
                // Die durch \n bzw. \r\n getrennten Zeilen:
                let lines = input.characters.split{ $0 == "\n" || $0 == "\r\n" }.map{ String($0) }
                
                guard lines.count > 0 else {
                    throw TaskReaderError.FileEmpty
                }
                
                guard let width = Int(lines[0]) else {
                    throw TaskReaderError.UnknownCharacter
                }
                
                var state = State(width: width)
                
                // Alle Zeilen durchlaufen und Paketziele abfragen
                for lineIndex in 1...(width*width) {
                    let line = lines[lineIndex]
                    let components = line.characters.split { $0 == " " }.map { String($0) }
                    if let fromRow = Int(components[0]), fromColumn = Int(components[1]), toRow = Int(components[2]), toColumn = Int(components[3]) {
                        state.packages[fromRow][fromColumn] = Package(destination: Coordinates(row: toRow, column: toColumn))
                    } else {
                        throw TaskReaderError.UnknownCharacter
                    }
                }
                
                print("Lass die Zeile leer und drücke Enter, um die Standardparameter zu verwenden. Ist die Zeile nicht leer, dann können die Parameter verändert werden.")
                
                var tryCombinations = true
                var tryCombinationsForEachStateSelection = false
                var tryRandom = false
                var priorityDefinitions: [PriorityDefinition] = [.DistanceGoodEdges, .DistanceAllEdges]
                var cycleSelections: [CycleSelection] = [.CycleLength, .PrioritySum, .PriorityAverage]
                var stateSelections: [StateSelection] = [.FirstState, .SmallestMaximumTotal]
                var maxChoosenStates = 2
                var goBackOnNotDecreasingMaxDistance = 0
                
                // Einstellungen einlesen:
                if let line = readLine() where !line.isEmpty {
                    print("Welche Strategien sollen verwendet werden? Gib ohne Leerzeichen hintereinander 'y' für ja oder 'n' für nein ein. Eine leere Zeile verwendet die Standardwerte. (Standard: ynn)")
                    print("1. Alle Kombinationen durchprobieren")
                    print("2. Jede mögliche Auswahl der Zustände durchprobieren und immer die beste Kombination der anderen Kriterien verwenden")
                    print("3. Zufällig die Definition und Kriterien ermitteln")
                    if let line = readLine() where !line.isEmpty {
                        if line.characters.count != 3 { throw TaskReaderError.InvalidParameters }
                        tryCombinations = line[line.startIndex.advancedBy(0)] == "y"
                        tryCombinationsForEachStateSelection = line[line.startIndex.advancedBy(1)] == "y"
                        tryRandom = line[line.startIndex.advancedBy(2)] == "y"
                    }
                    
                    print("Welche Kantenpriorität-Definitionen sollen verwendet werden? Gib ohne Leerzeichen hintereinander 'y' für ja oder 'n' für nein ein. Eine leere Zeile verwendet die Standardwerte. (Standard: yy)")
                    print("1. Distanz in der Kantenrichtung zum Ziel nur bei guten Kanten, ansonsten 0")
                    print("2. Distanz in der Kantenrichtung zum Ziel bei allen Kanten")
                    if let line = readLine() where !line.isEmpty {
                        if line.characters.count != 2 { throw TaskReaderError.InvalidParameters }
                        priorityDefinitions.removeAll()
                        if line[line.startIndex.advancedBy(0)] == "y" { priorityDefinitions.append(.DistanceGoodEdges) }
                        if line[line.startIndex.advancedBy(1)] == "y" { priorityDefinitions.append(.DistanceAllEdges) }
                    }
                    
                    print("Welche Kriterien zur Auswahl von Zyklen sollen verwendet werden? Gib ohne Leerzeichen hintereinander 'y' für ja oder 'n' für nein ein. Eine leere Zeile verwendet die Standardwerte. (Standard: yyy)")
                    print("1. Zyklenlänge")
                    print("2. Summe der Prioritäten")
                    print("3. Durchschnitt der Prioritäten")
                    if let line = readLine() where !line.isEmpty {
                        if line.characters.count != 3 { throw TaskReaderError.InvalidParameters }
                        cycleSelections.removeAll()
                        if line[line.startIndex.advancedBy(0)] == "y" { cycleSelections.append(.CycleLength) }
                        if line[line.startIndex.advancedBy(1)] == "y" { cycleSelections.append(.PrioritySum) }
                        if line[line.startIndex.advancedBy(2)] == "y" { cycleSelections.append(.PriorityAverage) }
                    }
                    
                    print("Welche Kriterien zur Auswahl von Zuständen sollen verwendet werden? Gib ohne Leerzeichen hintereinander 'y' für ja oder 'n' für nein ein. Eine leere Zeile verwendet die Standardwerte. (Standard: yy)")
                    print("1. Erster Zustand")
                    print("2. Kleinste Maximaldistanz, kleinste Gesamtdistanz")
                    if let line = readLine() where !line.isEmpty {
                        if line.characters.count != 2 { throw TaskReaderError.InvalidParameters }
                        stateSelections.removeAll()
                        if line[line.startIndex.advancedBy(0)] == "y" { stateSelections.append(.FirstState) }
                        if line[line.startIndex.advancedBy(1)] == "y" { stateSelections.append(.SmallestMaximumTotal) }
                    }
                    
                    print("Wie viele Zustände sollen maximal ausgewählt werden, im Fall, dass die Maximaldistanz nicht verkleinert wurde? Eine leere Zeile verwendet den Standardwert. (Standard: 2)")
                    if let line = readLine() where !line.isEmpty {
                        maxChoosenStates = Int(line)!
                    }
                    
                    print("Wie viele Schritte sollen zurückgegangen werden, im Fall, dass die Maximaldistanz nicht verkleinert wurde? Eine leere Zeile verwendet den Standardwert. (Standard: 0, dadurch wird gar kein Schritt rückgängig gemacht und der vorherige Parameter ist in diesem Fall egal)")
                    if let line = readLine() where !line.isEmpty {
                        goBackOnNotDecreasingMaxDistance = Int(line)!
                    }
                }
                
                return Task(startState: state, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections, maxChoosenStates: maxChoosenStates, goBackOnNotDecreasingMaxDistance: goBackOnNotDecreasingMaxDistance, tryCombinations: tryCombinations, tryCombinationsForEachStateSelection: tryCombinationsForEachStateSelection, tryRandom: tryRandom)
                
            } catch {
                // Fehlermeldung ausgeben
                if let error = error as? TaskReaderError {
                    print(error.message)
                }
            }
        }
    }
    
}
