import Foundation

// Ein Fehlertyp vom TaskReader, der die Daten einliest
enum TaskReaderError: ErrorType {
    case FileNotFound
    case UnknownInput
    case InputUnavailable
    case UnknownArgument
    case FileEmpty
    case UnknownCharacter
    case RowLengthWrong
    
    // Die Fehlernachricht
    var message: String {
        switch self {
        case .FileNotFound: return "Datei wurde nicht gefunden."
        case .UnknownInput: return "Die Eingabe wurde nicht erkannt (korrekt: \"Pfad [-p]\")."
        case .InputUnavailable: return "Ein Fehler mit der Eingabe ist aufgetreten."
        case .UnknownArgument: return "Ein angegebenes Argument konnte nicht erkannt werden."
        case .FileEmpty: return "Die Datei ist leer."
        case .UnknownCharacter: return "Zeichen wurde nicht erkannt."
        case .RowLengthWrong: return "Die Anzahl der Zeichen pro Reihe ist nicht immer dieselbe."
        }
    }
}

// Liest die Daten ein
class TaskReader {
    
    // Liest die Zeichenkette ein, die in der Datei ist, dessen Pfad der Nutzer eingibt
    func readString() throws -> (string: String, arguments: [String]) {
        print("Pfad zur Datei mit der Eingabe eingeben (-p hinzufügen, um ebenfalls Wahrscheinlichkeiten zu berechnen):")
        
        // Konsoleneingabe lesen:
        if let path = readLine() {
            // '~' durch den entsprechenden Pfad ersetzen und eingegebenen Pfad trimmen
            var input = NSString(string: path).stringByExpandingTildeInPath.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet()).stringByReplacingOccurrencesOfString("\\ ", withString: " ").characters.split(" ")
            
            var location = "" // Pfad zur Eingabe
            var arguments = [String]() // Argumente für die Eingabe
            
            // Als erstes Pfad einlesen...
            var inputIndex = 0
            for (; inputIndex < input.count; inputIndex++) {
                if let firstCharacter = input[inputIndex].first {
                    if firstCharacter == "-" {
                        break
                    } else {
                        if location.characters.count > 0 {
                            location += " "
                        }
                        location += String(input[inputIndex])
                    }
                }
            }
            
            // ... und dann Argumente einlesen
            for (; inputIndex < input.count; inputIndex++) {
                if let firstCharacter = input[inputIndex].first {
                    if firstCharacter == "-" {
                        arguments.append(String(input[inputIndex].dropFirst()))
                    } else {
                        throw TaskReaderError.UnknownInput
                    }
                }
            }
            
            if let string = try? NSString(contentsOfFile: location as String, encoding: NSUTF8StringEncoding) {
                // Datei konnte gelesen werden
                return (string: string as String, arguments: arguments)
            } else {
                // Datei wurde nicht gefunden
                throw TaskReaderError.FileNotFound
            }
        } else {
            throw TaskReaderError.InputUnavailable
        }
    }
    
    // Liest eine Datei, die der Nutzer eingibt und erstellt daraus eine Aufgabe
    func read() -> Task {
        while true {
            // Immer wieder ausführen, wenn die Eingabe nicht gelesen werden konnte
            do {
                let (input, arguments) = try readString()
                
                // Die durch \n bzw. \r\n getrennten Zeilen:
                let lines = input.characters.split{ $0 == "\n" || $0 == "\r\n" }.map{ String($0) }
                
                // Nach dem Argument für Wahrscheinlichkeiten/Erwartungswerte suchen
                var checkProbabilities = false
                for argument in arguments {
                    if argument == "probability" || argument == "probabilities" || argument == "p" {
                        checkProbabilities = true
                    } else {
                        throw TaskReaderError.UnknownArgument
                    }
                }
                
                guard lines.count > 0 else {
                    throw TaskReaderError.FileEmpty
                }
                
                let width = lines[0].characters.count
                var world = [[Vertex?]]()
                world.reserveCapacity(lines.count)
                
                // Alle Zeilen durchlaufen:
                for lineIndex in 0..<lines.count {
                    let line = lines[lineIndex]
                    
                    guard line.characters.count == width else {
                        // Die Anzahl der Spalten muss der Breite aller Zeilen entsprechen
                        throw TaskReaderError.RowLengthWrong
                    }
                    
                    var row = [Vertex?]() // Die Knoten in dieser Zeile
                    row.reserveCapacity(width)
                    let characters = [Character](line.characters) // Die Buchstaben in dieser Zeile
                    
                    // Alle Spalten durchlaufen:
                    for (columnIndex, char) in characters.enumerate() {
                        // Zeichen in Knoten konvertieren:
                        switch char {
                        case "#": row.append(nil)
                        case " ": row.append(Vertex(row: lineIndex, column: columnIndex, type: .Empty))
                        case "E": row.append(Vertex(row: lineIndex, column: columnIndex, type: .Exit))
                        default: throw TaskReaderError.UnknownCharacter
                        }
                    }
                    
                    world.append(row) // Zeile hinzufügen
                }
                
                return Task(width: width, height: lines.count, world: world, checkProbabilities: checkProbabilities)
                
            } catch {
                // Fehlermeldung ausgeben
                if let error = error as? TaskReaderError {
                    print(error.message)
                }
            }
        }
    }
    
}
