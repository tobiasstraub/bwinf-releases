
import Foundation

// Die Art eines Knotens in Graphen (Feld / Ausgang). Hindernisse gehören nicht dazu.
enum VertexType {
    case Empty
    case Exit
}

// Eine Kante von einem Knoten zu einem anderen. Die besitzt das Ziel der Kante sowie die Kantenlänge.
struct Edge {
    let to: Vertex
    let length: Int
}

// Ein Knoten. Er besteht aus seinen Koordinaten sowie einer Knotenart.
// Die Kanten werden nach der Erstellung hinzugefügt. Alle anderen Werte werden für
// die Suche der Zusammenhangskomponenten oder für die Berechnung der Wahrscheinlichkeiten
// bzw. Erwartungsewrte benötigt.
class Vertex {
    
    let row: Int
    let column: Int
    let type: VertexType
    
    var edges = [Edge]()
    
    // Gibt an, als wievielter Knoten er besucht wurde
    var index = -1
    // Lowlink für den Algorithmus von Tarjan
    var lowlink = -1
    // Gibt an, ob sich der Knoten auf dem Stack befindet
    var isOnStack = false
    // Gibt an, ob sich der Knoten in der Komponente befindet, die gerade bearbeitet wird
    var isInCurrentComponent = false
    // Gibt an, ob der Knoten sicher ist
    var isSecure = false
    
    // Wahrscheinlichkeit und Erwartungswert sowie die dafür verwendeten Koeffizienten
    // für das Gleichungssystem. Die konstanten Zahlen in der Gleichung wird in
    // der Wahrscheinlichkeit bzw. dem Erwartungswert gespeichert.
    var probability = Rational(number: 0)
    var expectedValue = Rational(number: 0)
    var probabilityCoefficients = [Int: Rational]()
    var expectedValueCoefficients = [Int: Rational]()
    
    init(row: Int, column: Int, type: VertexType) {
        self.row = row
        self.column = column
        self.type = type
    }
    
    // Berechnet die Koeffizienten sowie die konstante Zahl für die Gleichung der Wahrscheinlichkeit.
    func calculateProbabilityCoefficients() {
        // Die Koeffizienten sind für alle Wahrscheinlichkeiten, die einen Koeffizient besitzen, am Anfang gleich:
        let coefficient = Rational(counter: 1, denominator: edges.count)
        
        for edge in edges {
            if edge.to.isInCurrentComponent {
                probabilityCoefficients[edge.to.index] = coefficient
            } else {
                // Nicht in der Komponente, daher wird die Wahrscheinlichkeit zur konstanten Zahl hinzuaddiert.
                probability += coefficient * edge.to.probability
            }
        }
    }
    
    // Berechnet die Koeffizienten sowie die konstante Zahl für die Gleichung des Erwartungswertes.
    func calculateExpectedValueCoefficients() {
        // Nur Kanten zu Knoten nehmen, deren Erwartungswert berechnet werden kann:
        let usedEdges = edges.filter { $0.to.expectedValue != -1 }
        let coefficient = Rational(counter: 1, denominator: usedEdges.count)
        
        for edge in usedEdges {
            if edge.to.isInCurrentComponent {
                expectedValueCoefficients[edge.to.index] = coefficient
                // Die konstante Zahl erhöht sich auch durch die Weglängen der Kanten:
                expectedValue += coefficient * edge.length
            } else {
                // Nicht in der Komponente, daher Wert zur konstanten Zahl hinzuaddieren.
                expectedValue += coefficient * (edge.to.expectedValue + edge.length)
            }
        }
        
        if usedEdges.count == 0 {
            // Es gibt keine ausgehende Kante, die auf einen Knoten zeigt, dessen Erwartungswert berechnet werden kann. Daher kann auch dieser Erwartungswert nicht berechnet werden.
            expectedValue = Rational(number: -1)
        }
    }
    
    // Setzt die Gleichung des angegebenen Knotens in die Gleichung dieses Knotens ein.
    // Zu additionValue werden alle entstehenden konstanten Zahlen hinzuaddiert.
    // otherAdditionalValue ist die konstante Zahl der einzusetztenden Gleichung.
    // coefficients sind die Koeffizienten der eigenen Gleichung, otherCoefficients die
    // der eingesetzten. defaultValue wird als entstehender Wert der Gleichung verwendet,
    // wenn die Gleichung nicht eindeutig lösbar ist. Dies ist bei Erwartungswerten der
    // Fall, wenn es keine ausgehende Kante aus der Komponente gibt, die zu einem Knoten
    // führt, dessen Erwartungswert eindeutig bestimmt werden konnte.
    func replaceCoefficientOfVertex(vertex: Vertex, inout additionalValue: Rational, otherAdditionalValue: Rational, inout coefficients: [Int: Rational], otherCoefficients: [Int: Rational], defaultValue: Rational) {
        
        if let coefficient = coefficients[vertex.index] {
            coefficients.removeValueForKey(vertex.index) // aus Gleichung entfernen
            
            // Erhöhen des zusätzlichen Wertes:
            additionalValue += coefficient * otherAdditionalValue
            
            var selfCoefficient: Rational? // Koeffizient der eigenen Variablen
            for (index, value) in otherCoefficients {
                if index == self.index {
                    // Koeffizient der eigenen Variablen gefunden
                    selfCoefficient = coefficient * value
                } else if let previousCoefficient = coefficients[index] {
                    // Koeffizient gibt es bereits in der eigenen Gleichung
                    coefficients[index] = previousCoefficient + (coefficient * value)
                } else {
                    // Koeffizient war in der eigenen Gleichung zuvor 0
                    coefficients[index] = coefficient * value
                }
            }
            
            if let selfCoefficient = selfCoefficient {
                if selfCoefficient == 1 {
                    // Gleichung ist nicht eindeutig lösbar, da auf beiden Seiten nur
                    // die eigene Variable mit einer 1 als Koeffizient steht
                    additionalValue = defaultValue
                } else {
                    // Ansonsten alle Koeffizienten sowie den zusätzlichen Wert multiplizieren, sodass auf der linken Seite wieder eine 1 als Koeffizient der eigenen Variablen steht
                    let multiplier = 1 / (1 - selfCoefficient)
                    additionalValue *= multiplier
                    for (index, value) in coefficients {
                        coefficients[index] = value * multiplier
                    }
                }
            }
        }
    }
    
    // Setzt die Gleichung des gegebenen Knotens in die eigene ein, um die Wahrscheinlichkeiten zu berechnen
    func replaceProbabilityCoefficientOfVertex(vertex: Vertex) {
        replaceCoefficientOfVertex(vertex, additionalValue: &self.probability, otherAdditionalValue: vertex.probability, coefficients: &probabilityCoefficients, otherCoefficients: vertex.probabilityCoefficients, defaultValue: Rational(number: 0))
    }
    
    // Setzt die Gleichung des gegebenen Knotens in die eigene ein, um die Erwartungswerte zu berechnen
    func replaceExpectedValueCoefficientOfVertex(vertex: Vertex) {
        replaceCoefficientOfVertex(vertex, additionalValue: &self.expectedValue, otherAdditionalValue: vertex.expectedValue, coefficients: &expectedValueCoefficients, otherCoefficients: vertex.expectedValueCoefficients, defaultValue: Rational(number: -1))
    }
    
}

// VertexStack wird als Stack für Knoten verwendet. Er ist als Array implementiert.
struct VertexStack {
    
    var items = [Vertex]()
    
    mutating func push(item: Vertex) {
        items.append(item)
    }
    
    mutating func pop() -> Vertex {
        return items.removeLast()
    }
    
}

// Task findet alle sicheren Felder einer Welt und, wenn gefordert, die Wahrscheinlichkeiten
// und Erwartungswerte aller Felder.
class Task {
    
    // Die Breite und Höhe der Welt
    let width: Int
    let height: Int
    // Alle Quadrate als zweidimensionales Array. Hindernisse entsprechen nil.
    let world: [[Vertex?]]
    // Wenn true, werden zusätzlich Wahrscheinlichkeiten und Erwartungswerte berechnet
    let checkProbabilities: Bool
    
    // Beinhaltet alle Knoten
    var vertices = [Vertex]()
    // Index für den nächsten entdeckten Knoten
    var index = 0
    // Stack vom Algorithmus von Tarjan
    var stack = VertexStack()
    // Liste mit den Koordinaten aller sicheren Knoten
    var result = [(row: Int, column: Int)]()
    
    init(width: Int, height: Int, world: [[Vertex?]], checkProbabilities: Bool) {
        self.width = width
        self.height = height
        self.world = world
        self.checkProbabilities = checkProbabilities
    }
    
    // Startet die Berechnung. Es werden zunächst die Knoten und Kanten erstellt,
    // danach wird der Algorithmus von Tarjan gestartet.
    func run() {
        findVertices()
        findEdges()
        
        for vertex in vertices where vertex.index == -1 {
            // Noch nicht entdeckt, also Algorithmus von Tarjan mit diesem Knoten starten
            tarjan(vertex)
        }
    }
    
    // Fügt alle Knoten der Welt in das Array ein.
    func findVertices() {
        for row in world {
            for vertex in row {
                if let vertex = vertex {
                    vertices.append(vertex)
                }
            }
        }
    }
    
    // Sucht alle Kanten in allen Zeilen und Spalten, vorwärts und rückwärts.
    func findEdges() {
        for row in world {
            // Jede Zeile überprüfen
            findEdges(row)
            findEdges(row.reverse())
        }
        for columnIndex in 0..<width {
            // Jede Spalte überprüfen, indem aus jeder Zeile das entsprechende
            // Quadrat genommen und zur Spalte hinzugefügt wird
            let column = world.map{ $0[columnIndex] }
            findEdges(column)
            findEdges(column.reverse())
        }
    }
    
    // Sucht alle Kanten der gegebenen Zeile bzw. Spalte, in der Reihenfolge
    // des Arrays.
    func findEdges(vertices: [Vertex?]) {
        // Knoten, auf den die nächsten Kanten zeigen, ausgehend von Knoten weiter
        // hinten im Array
        var edge: Vertex? = nil
        // Die Länge der Kante bis zum gespeicherten Knoten
        var edgeLength = 0
        
        for vertex in vertices {
            if let vertex = vertex {
                if vertex.type == .Exit {
                    // Ausgang gefunden, die nächsten Felder müssen eine Kante dorthin
                    // besitzen, wenn kein Hindernis dazwischen ist
                    edge = vertex
                    edgeLength = 0
                } else {
                    // Es wurde ein freies Feld gefunden
                    if let edge = edge {
                        // Es wurde ein Knoten gespeichert, daher muss eine
                        // Kante dorthin hinzugefügt werden mit der entsprechenden Länge
                        edgeLength++
                        vertex.edges.append(Edge(to: edge, length: edgeLength))
                    } else {
                        // Kein gespeichertes Feld, daher bekommen die nächsten Felder
                        // eine Kante hierhin, wenn kein Hindernis dazwischen ist
                        edge = vertex
                        edgeLength = 0
                    }
                }
            } else {
                // Hindernis gefunden, also gespeicherten Knoten zurücksetzen
                edge = nil
                edgeLength = 0
            }
        }
    }
    
    // Die Tiefensuche durch den Algorithmus von Tarjan.
    func tarjan(vertex: Vertex) {
        vertex.index = index
        vertex.lowlink = index
        index++
        
        if vertex.type == .Exit {
            // Der Knoten ist ein Ausgang. Er besitzt keine ausgehende
            // Kante und ist in einer eigenen, sicheren Komponente
            // mit Wahrscheinlichkeit 1 und Erwartungswert 0.
            vertex.isSecure = true
            vertex.probability = Rational(number: 1)
            vertex.expectedValue = Rational(number: 0)
            return
        }
        
        // Knoten auf den Stack legen
        stack.push(vertex)
        vertex.isOnStack = true
        
        // Alle Kanten prüfen, rekursiv Knoten aufrufen und Lowlink anpassen.
        for edge in vertex.edges {
            if edge.to.index == -1 {
                tarjan(edge.to)
                vertex.lowlink = min(vertex.lowlink, edge.to.lowlink)
            } else if edge.to.isOnStack {
                vertex.lowlink = min(vertex.lowlink, edge.to.index)
            }
        }
        
        // Der Knoten ist Wurzel einer starken Zusammenhangskomponente
        if vertex.lowlink == vertex.index {
            var componentVertices = [Vertex]()
            
            // Alle Knoten, die zur Komponente gehören, vom Stack entfernen,
            // die entsprechenden Variablen anpassen und zu componentVertices hinzufügen:
            var current: Vertex
            repeat {
                current = stack.pop()
                current.isOnStack = false
                current.isInCurrentComponent = true
                componentVertices.append(current)
            } while vertex !== current
            
            // Die Komponente ist sicher, wenn sie mindestens eine ausgehende Kante in eine
            // andere Komponente besitzt und alle Komponenten, zu denen sie eine Kante hat,
            // ebenfalls sicher sind:
            var hasOutgoingEdge = false
            var secure = true
            
            for vertex in componentVertices {
                for edge in vertex.edges where edge.to.isInCurrentComponent == false {
                    hasOutgoingEdge = true
                    if !edge.to.isSecure {
                        secure = false
                    }
                }
                
                if checkProbabilities {
                    // Bei der Gelegenheit gleich die Koeffizienten für die Berechnung
                    // der Wahrscheinlichkeiten und Erwartungswerte initialisieren:
                    vertex.calculateProbabilityCoefficients()
                    vertex.calculateExpectedValueCoefficients()
                }
            }
            
            if checkProbabilities {
                // Wahrscheinlichkeiten und Erwartungswerte berechnen
                
                // Alle Gleichungen, außer die erste, in alle anderen Gleichungen einsetzen:
                for replace in componentVertices.dropFirst() {
                    for replaceIn in componentVertices {
                        if replace !== replaceIn {
                            replaceIn.replaceProbabilityCoefficientOfVertex(replace)
                            replaceIn.replaceExpectedValueCoefficientOfVertex(replace)
                        }
                    }
                }
                
                // Für den ersten Knoten wurden die Werte bereits fertig berechnet. Alle
                // anderen müssen noch mithilfe der ersten Gleichung berechnet werden:
                for vertex in componentVertices.dropFirst() {
                    vertex.probability += vertex.probabilityCoefficients[componentVertices[0].index]! * componentVertices[0].probability
                    
                    if componentVertices[0].expectedValue == -1 {
                        // Erwartungswert konnte nicht berechnet werden
                        vertex.expectedValue = Rational(number: -1)
                    } else {
                        vertex.expectedValue += vertex.expectedValueCoefficients[componentVertices[0].index]! * componentVertices[0].expectedValue
                    }
                }
            }
            
            // isInCurrentComponent zurücksetzen und gegebenenfalls isSecure auf true
            // setzen. Außerdem Knoten zur Liste mit den sicheren Knoten hinzufügen,
            // wenn die Komponente sicher ist.
            for vertex in componentVertices {
                vertex.isInCurrentComponent = false
                if hasOutgoingEdge && secure {
                    vertex.isSecure = true
                    result.append((row: vertex.row, column: vertex.column))
                }
            }
        }
    }
    
}
