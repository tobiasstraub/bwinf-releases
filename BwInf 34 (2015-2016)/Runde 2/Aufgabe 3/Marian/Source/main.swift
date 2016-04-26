
import Foundation

let task = TaskReader().read()

let startDate = NSDate()
task.run()
let time = NSDate().timeIntervalSinceDate(startDate)

print("In \(time) Sekunden wurde die Lösung gefunden.")

// Welt mit den sicheren Feldern ausgeben
for row in task.world {
    for vertex in row {
        if let vertex = vertex { // Feld oder Ausgang
            if vertex.type == .Exit {
                print("E", terminator: "")
            } else {
                if vertex.isSecure {
                    print("o", terminator: "")
                } else {
                    print(" ", terminator: "")
                }
            }
        } else { // Hindernis
            print("#", terminator: "")
        }
    }
    print("")
}

// Liste der sicheren Felder ausgeben
print("\nFolgende Felder sind sicher:")
for secureVertex in task.result {
    print("\(secureVertex.row) \(secureVertex.column)")
}

// Liste der Felder mit Wahrscheinlichkeiten und Erwartungswerte ausgeben
if task.checkProbabilities {
    print("\nDie Wahrscheinlichkeiten und Erwartungswerte für das Erreichen eines Zieles:")
    for vertex in task.vertices where vertex.type == .Empty {
        print("\(vertex.row)|\(vertex.column)  -  \(vertex.probability) \(vertex.probability.equalsPrecisionPercentage)", terminator: "")
        if vertex.expectedValue == -1 {
            print("")
        } else {
            print("  -  \(vertex.expectedValue) \(vertex.expectedValue.equalsPrecision)")
        }
    }
}
