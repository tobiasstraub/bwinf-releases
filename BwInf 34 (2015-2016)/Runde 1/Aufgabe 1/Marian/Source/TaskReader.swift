import Foundation

// Ein Fehlertyp vom TaskReader, der die Daten einliest
enum TaskReaderError: ErrorType {
    case FileNotFound
    case InputUnavailable
    case DimensionsFormatWrong
    case EmptyFile
    case NotEnoughRows
    case NotEnoughColumns
    case UnknownChar
    case KassiopeiaNotFound
    
    // Die Fehlernachricht
    var message: String {
        switch self {
        case .FileNotFound: return "Datei wurde nicht gefunden."
        case .InputUnavailable: return "Ein Fehler mit der Eingabe ist aufgetreten."
        case .DimensionsFormatWrong: return "Die Größe von Quadratien ist nicht korrekt formatiert."
        case .EmptyFile: return "Die Datei ist leer."
        case .NotEnoughRows: return "Nicht genug Zeilen in Quadratien."
        case .NotEnoughColumns: return "Nicht genug Spalten in Quadratien."
        case .UnknownChar: return "Zeichen wurde nicht erkannt."
        case .KassiopeiaNotFound: return "Kassiopeia wurde nicht gefunden."
        }
    }
}

// Liest die Daten ein
class TaskReader {
    
    // Liest die Zeichenkette ein, die in der Datei ist, dessen Pfad der Nutzer eingibt
    func readString() throws -> String {
        print("Pfad zur Datei mit der Eingabe eingeben:")
        
        // Konsoleneingabe lesen:
        let path = NSString(data: NSFileHandle.fileHandleWithStandardInput().availableData, encoding: NSUTF8StringEncoding)
        
        if let path = path {
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
    
    // Liest die Größe von Quadratien aus dem angegebenen String aus
    func readDimensions(string: String) throws -> Dimensions {
        
        // Durch Leerzeichen getrennt:
        let numbers = string.characters.split{ $0 == " " }.map { String($0) }
        
        guard numbers.count == 2 else {
            // Es müssen genau zwei Zahlen existieren
            throw TaskReaderError.DimensionsFormatWrong
        }
        
        if let number1 = Int(numbers[0]), let number2 = Int(numbers[1]) {
            return (number1, number2)
        } else {
            // Die Zeichenketten sind nicht in Zahlen konvertierbar
            throw TaskReaderError.DimensionsFormatWrong
        }
    }
    
    // Liest eine Datei, die der Nutzer eingibt und erstellt daraus eine Aufgabe
    func read() -> Task {
        while true {
            // Immer wieder ausführen, wenn die Eingabe nicht gelesen werden konnte
            do {
                let input = try readString()
                
                // Die durch \n getrennten Zeilen:
                let lines = input.characters.split{ $0 == "\n" }.map{ String($0) }
                
                guard lines.count > 0 else {
                    // Datei leer
                    throw TaskReaderError.EmptyFile
                }
                
                let dimensions = try readDimensions(lines[0])
                guard lines.count == dimensions.height + 1 else {
                    // Die Anzahl der Zeilen muss der angegebenen Höhe plus eins entsprechen, da oben noch eine Zeile mit der Größe steht
                    throw TaskReaderError.NotEnoughRows
                }
                
                var fields = [[FieldType]]()
                var whiteFields = 0
                var startingPoint: Coordinates?
                
                // Alle Zeilen durchlaufen:
                for lineIndex in 1..<lines.count {
                    let line = lines[lineIndex]
                    
                    guard line.characters.count == dimensions.width else {
                        // Die Anzahl der Spalten muss der angegebenen Breite entsprechen
                        throw TaskReaderError.NotEnoughColumns
                    }
                    
                    var row = [FieldType]() // Die Feldtypen in dieser Zeile
                    let characters = [Character](line.characters) // Die Buchstaben in dieser Zeile
                    
                    // Alle Spalten durchlaufen:
                    for charIndex in 0..<characters.count {
                        let char = characters[charIndex]
                        
                        // Zeichen in Feldart konvertieren:
                        if let type = FieldType(rawValue: char) {
                            row.append(type) // Der Zeile hinzufügen
                            
                            // Basierend auf Feldart weitere Aktionen durchführen:
                            switch type {
                            case .White:
                                whiteFields++
                            case .Kassiopeia:
                                startingPoint = (row: lineIndex - 1, column: charIndex)
                                whiteFields++
                            default: break
                            }
                            
                        } else {
                            // Zeichen konnte nicht in eine Feldart konvertiert werden
                            throw TaskReaderError.UnknownChar
                        }
                    }
                    
                    fields.append(row) // Zeile hinzufügen
                }
                
                if let theStartingPoint = startingPoint {
                    // Startpunkt von Kassiopeia wurde angegeben, Aufgabe erstellen:
                    return Task(quadratien: Quadratien(fields: fields, dimensions: dimensions, remainingWhiteFields: whiteFields, startingPoint: theStartingPoint))
                } else {
                    // Startpunkt wurde nicht angegeben
                    throw TaskReaderError.KassiopeiaNotFound
                }
            } catch {
                // Fehlermeldung ausgeben
                if let error = error as? TaskReaderError {
                    print(error.message)
                }
            }
        }
    }
    
}
