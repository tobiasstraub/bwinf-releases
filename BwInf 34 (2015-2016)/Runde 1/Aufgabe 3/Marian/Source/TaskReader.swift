import Foundation

// Ein Fehlertyp vom TaskReader, der die Daten einliest
enum TaskReaderError: ErrorType {
    case FileNotFound
    case InputUnavailable
    case NotThreeRows
    case BottlesNotANumber
    case ContainersNotANumber
    case ContainersCountNotMatching
    case ContainerNotANumber
    
    // Die Fehlernachricht
    var message: String {
        switch self {
        case .FileNotFound: return "Datei wurde nicht gefunden."
        case .InputUnavailable: return "Ein Fehler mit der Eingabe ist aufgetreten."
        case .NotThreeRows: return "Die Datei enthält nicht genau 3 Zeilen."
        case .BottlesNotANumber: return "Die erste Zeile muss die Anzahl der Flaschen enthalten."
        case .ContainersNotANumber: return "Die zweite Zeile muss die Anzahl der Behälter enthalten."
        case .ContainersCountNotMatching: return "Die angegebene Anzahl der Behälter stimmt nicht mit der tatsächlichen Anzahl überein."
        case .ContainerNotANumber: return "Ein angegebener Behälter konnte nicht erkannt werden."
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
    
    // Liest eine Datei, die der Nutzer eingibt und erstellt daraus eine Aufgabe
    func read() -> Task {
        while true {
            // Immer wieder ausführen, wenn die Eingabe nicht gelesen werden konnte
            do {
                let input = try readString()
                
                // Die durch \n getrennten Zeilen:
                let lines = input.characters.split{ $0 == "\n" }.map{ String($0) }
                
                guard lines.count == 3 else {
                    // Datei muss drei Zeilen enthalten
                    throw TaskReaderError.NotThreeRows
                }
                
                let bottlesOptional = Int(lines[0])
                guard let bottles = bottlesOptional else {
                    // Anzahl der Flaschen konnte nicht erkannt werden
                    throw TaskReaderError.BottlesNotANumber
                }
                
                let containersCountOptional = Int(lines[1])
                guard let containersCount = containersCountOptional else {
                    // Anzahl der Behälter konnte nicht erkannt werden
                    throw TaskReaderError.ContainersNotANumber
                }
                
                let containerStrings = lines[2].characters.split { $0 == " " }.map { String($0) }
                guard containersCount == containerStrings.count else {
                    // Anzahl der Behälter stimmt nicht mit der tatsächlichen Anzahl überein
                    throw TaskReaderError.ContainersCountNotMatching
                }
                
                // Behälter einlesen:
                var containers = [Int]()
                for containerString in containerStrings {
                    guard let container = Int(containerString) else {
                        // Behälter ist keine Zahl
                        throw TaskReaderError.ContainerNotANumber
                    }
                    containers.append(container)
                }
                
                // Datei konnte erfolgreich gelesen werden
                return Task(bottles: bottles, containers: containers)
                
            } catch {
                // Fehlermeldung ausgeben
                if let error = error as? TaskReaderError {
                    print(error.message)
                }
            }
        }
    }
    
}
