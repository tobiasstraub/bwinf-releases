
import Foundation

enum MoveDirection {
    case North, East, South, West
}

struct Coordinates: Equatable {
    
    let row: Int
    let column: Int
    
    init(row: Int, column: Int) {
        self.row = row
        self.column = column
    }
    
    // Diese vier Methoden geben die Koordinaten ein Haus weiter
    // in der entsprechenden Himmelsrichtung zurück:
    func east() -> Coordinates {
        return Coordinates(row: row, column: column + 1)
    }
    
    func west() -> Coordinates {
        return Coordinates(row: row, column: column - 1)
    }
    
    func north() -> Coordinates {
        return Coordinates(row: row - 1, column: column)
    }
    
    func south() -> Coordinates {
        return Coordinates(row: row + 1, column: column)
    }
    
    // Gibt die Koordinaten in der angegebenen Himmelsrichtung zurück
    func inDirection(direction: MoveDirection) -> Coordinates {
        switch direction {
        case .North: return north()
        case .East: return east()
        case .South: return south()
        case .West: return west()
        }
    }
    
}

func ==(left: Coordinates, right: Coordinates) -> Bool {
    return left.row == right.row && left.column == right.column
}

// Mögliche Definitionen der Kantenpriorität
enum PriorityDefinition {
    case DistanceGoodEdges
    case DistanceAllEdges
}

// Mögliche Kriterien zur Auswahl von Zyklen
enum CycleSelection {
    case CycleLength
    case PrioritySum
    case PriorityAverage
}

// Mögliche Kriterien zur Auswahl von Zuständen
enum StateSelection {
    case FirstState
    case SmallestMaximumTotal
}

// Repräsentiert ein Paket
struct Package {
    
    let destination: Coordinates // Paketziel
    var distance = 0 // Distanz zu seinem Ziel
    var moveDirection: MoveDirection? = nil // Wurfrichtung
    var possibleMoves = [(direction: MoveDirection, priority: Int)]() // mögliche Wurfrichtungen
    
    init(destination: Coordinates) {
        self.destination = destination
    }
    
}

// Repräsentiert einen Weg im Graphen, wobei jeder enthaltene Knoten eine ausgehende Kante
// hat, damit später ein Zyklus daraus wird.
struct Path {
    
    // Verwendete Knoten und Kanten
    var vertices = [(vertex: Coordinates, direction: MoveDirection)]()
    var prioritySum = 0
    
    var length: Int {
        return vertices.count
    }
    var start: Coordinates {
        return vertices[0].0
    }
    var priorityAverage: Double {
        return Double(prioritySum) / Double(length)
    }
    
    // Fügt den Knoten mit seiner Kante zum Weg hinzu
    mutating func append(coordinates: Coordinates, moveDirection: MoveDirection, priority: Int) {
        vertices.append((coordinates, moveDirection))
        prioritySum += priority
    }
    
    // Entfernt den letzten Knoten und die Kantenpriorität aus dem Weg
    mutating func removeLast(priority priority: Int) {
        vertices.removeLast()
        prioritySum -= priority
    }
    
}

// Stellt einen Zustand dar.
struct State {
    
    var packages: [[Package]] // Pakete auf den Häusern
    var moves: [[String]] // Wurfrichtungen der Häuser
    
    var maxDist = 0 // Maximaldistanz
    var totalDist = 0 // Gesamtdistanz
    
    // Ermöglicht den Zugriff auf das Paket eines Hauses durch Koordinaten
    subscript(coordinates: Coordinates) -> Package {
        get {
            return packages[coordinates.row][coordinates.column]
        }
        set {
            packages[coordinates.row][coordinates.column] = newValue
        }
    }
    
    // true, wenn sich alle Pakete an ihren Zielen befinden, ansonsten false
    var isFinished: Bool {
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                if packages[row][column].destination != Coordinates(row: row, column: column) {
                    return false
                }
            }
        }
        return true
    }
    
    // Die Lösung im Format der Ausgabedatei
    var solutionString: String {
        var solution = ""
        for column in 0..<packages.count {
            for row in 0..<packages.count {
                solution += "\(row) \(column) \(moves[row][column])\n"
            }
        }
        return solution
    }
    
    init(width: Int) {
        // Arrays zunächst mit Standardwerten initialisieren
        packages = [[Package]](count: width, repeatedValue: [Package](count: width, repeatedValue: Package(destination: Coordinates(row: 0, column: 0))))
        moves = [[String]](count: width, repeatedValue: [String](count: width, repeatedValue: ""))
    }
    
    // Berechnet die Maximaldistanz, die Gesamtdistanz und die Distanzen von allen Paketen zu ihren Zielen
    mutating func calculateDistances() {
        maxDist = 0
        totalDist = 0
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                packages[row][column].distance = abs(packages[row][column].destination.row - row) + abs(packages[row][column].destination.column - column)
                maxDist = max(maxDist, packages[row][column].distance)
                totalDist += packages[row][column].distance
            }
        }
    }
    
    // Fügt alle Kanten in die entsprechenden Arrays der Pakete ein und sortiert sie
    mutating func createEdges(priorityDefinition: PriorityDefinition) {
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                
                // Array zunächst von vorherigen Werten leeren
                packages[row][column].possibleMoves = [(direction: MoveDirection, priority: Int)]()
                
                switch priorityDefinition {
                case .DistanceGoodEdges:
                    // Wenn das Paket in die richtige Richtung geworfen wird, den Abstand
                    // zum Ziel in dieser Richtung verwenden, ansonsten 0
                    if row != 0 {
                        packages[row][column].possibleMoves.append((direction: .North, priority: packages[row][column].destination.row < row ? (row - packages[row][column].destination.row) : 0))
                    }
                    if column != packages.count - 1 {
                        packages[row][column].possibleMoves.append((direction: .East, priority: packages[row][column].destination.column > column ? (packages[row][column].destination.column - column) : 0))
                    }
                    if row != packages.count - 1 {
                        packages[row][column].possibleMoves.append((direction: .South, priority: packages[row][column].destination.row > row ? (packages[row][column].destination.row - row) : 0))
                    }
                    if column != 0 {
                        packages[row][column].possibleMoves.append((direction: .West, priority: packages[row][column].destination.column < column ? (column - packages[row][column].destination.column) : 0))
                    }
                case .DistanceAllEdges:
                    // Den Abstand zum Ziel in die Wurfrichtung verwenden
                    if row != 0 {
                        packages[row][column].possibleMoves.append((direction: .North, priority: row - packages[row][column].destination.row))
                    }
                    if column != packages.count - 1 {
                        packages[row][column].possibleMoves.append((direction: .East, priority: packages[row][column].destination.column - column))
                    }
                    if row != packages.count - 1 {
                        packages[row][column].possibleMoves.append((direction: .South, priority: packages[row][column].destination.row - row))
                    }
                    if column != 0 {
                        packages[row][column].possibleMoves.append((direction: .West, priority: column - packages[row][column].destination.column))
                    }
                }
                
                packages[row][column].possibleMoves.sortInPlace{ $0.1 > $1.1 }
            }
        }
    }
    
    // Fügt die ausgewählten Kanten zu moves hinzu, was den Plan um einen Schritt erweitert
    mutating func addCurrentMovesToSolution() {
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                if let moveDirection = packages[row][column].moveDirection {
                    switch moveDirection {
                    case .North: moves[row][column] += "N"
                    case .East: moves[row][column] += "O"
                    case .South: moves[row][column] += "S"
                    case .West: moves[row][column] += "W"
                    }
                } else {
                    // Wird nicht geworfen
                    moves[row][column] += "_"
                }
            }
        }
    }
    
    // Bewegt alle Pakete entlang ihrer Wurfrichtungen
    mutating func move() {
        // Alten Zustand kopieren:
        let old = self
        
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                // Wurfrichtungen aus dem alten Zustand verwenden:
                if old.packages[row][column].moveDirection != nil {
                    let newPosition = old.coordinatesInMoveDirection(Coordinates(row: row, column: column))
                    self[newPosition] = old.packages[row][column]
                    self[newPosition].moveDirection = nil
                }
            }
        }
    }
    
    // Gibt die Koordinaten in der Wurfrichtung des Paketes an den gegebenen Koordinaten zurück
    func coordinatesInMoveDirection(coordinates: Coordinates) -> Coordinates {
        switch self[coordinates].moveDirection! {
        case .North: return coordinates.north()
        case .East: return coordinates.east()
        case .South: return coordinates.south()
        case .West: return coordinates.west()
        }
    }
    
    // Erstellt ein Array mit allen Koordinaten und gibt es sortiert zurück
    func sortedCoordinatesArray() -> [Coordinates] {
        // Array erstellen:
        var array = [Coordinates]()
        for row in 0..<packages.count {
            for column in 0..<packages.count {
                array.append(Coordinates(row: row, column: column))
            }
        }
        
        // Array sortieren:
        return array.sort { (a, b) -> Bool in
            if self[a].distance == self[b].distance {
                // Bei gleicher Distanz nach Zeile/Spalte sortieren
                if a.row == b.row {
                    return a.column < b.column
                } else {
                    return a.row < b.row
                }
            } else {
                return self[a].distance > self[b].distance
            }
        }
    }
    
    // Prüft die Erreichbarkeit von to von coordinates aus. Alle Knoten mit true in visited
    // werden nicht erneut besucht.
    func floodFillReachability(coordinates: Coordinates, to: Coordinates, inout visited: [[Bool]]) -> Bool {
        if coordinates == to {
            // Ziel erreicht
            return true
        }
        if visited[coordinates.row][coordinates.column] || self[coordinates].moveDirection != nil {
            // Wurde schon besucht oder kann keine weitere Kante auswählen
            return false
        }
        
        // Knoten wird jetzt besucht:
        visited[coordinates.row][coordinates.column] = true
        
        // Alle erlaubten Kanten durchgehen und Flood Fill auf deren Ziele ausführen:
        for (direction, priority) in self[coordinates].possibleMoves {
            guard priority > 0 || self[coordinates].distance + 1 < maxDist else { continue }
            if floodFillReachability(coordinates.inDirection(direction), to: to, visited: &visited) {
                // Flood Fill hat Zielknoten erreicht
                return true
            }
        }
        
        // Von hier aus nicht gefunden:
        return false
    }
    
    // Prüft die Erreichbarkeit von coordinates von from aus.
    func isReachable(coordinates: Coordinates, from: Coordinates) -> Bool {
        // Alle Werte zunächst mit false initialisieren:
        var reachable = [[Bool]](count: packages.count, repeatedValue: [Bool](count: packages.count, repeatedValue: false))
        return floodFillReachability(from, to: coordinates, visited: &reachable)
    }
    
    // Sucht einen Zyklus von coordinates aus, wobei maximal remainingWrong weitere
    // schlechte Kanten benutzt werden. Alle durch die Rekursion aufgerufenen Knoten und 
    // Kanten werden in currentPath gespeichert, bestCycles beinhaltet die besten bisher
    // gefundenen Zyklen. cycleSelection ist das Kriterium zur Zyklenauswahl.
    mutating func searchCycle(coordinates coordinates: Coordinates, remainingWrong: Int, inout currentPath: Path, inout bestCycles: [Path], cycleSelection: CycleSelection) {
        
        if currentPath.length > 0 && currentPath.start == coordinates {
            // Zyklus gefunden
            
            if bestCycles.isEmpty {
                // Noch kein Zyklus in bestCycles, also einfach hinzufügen
                bestCycles.append(currentPath)
                return
            }
            
            switch cycleSelection {
            case .CycleLength:
                // Die längsten Zyklen werden verwendet
                if currentPath.length > bestCycles[0].length {
                    bestCycles.removeAll()
                    bestCycles.append(currentPath)
                } else if currentPath.length == bestCycles[0].length {
                    bestCycles.append(currentPath)
                }
            case .PrioritySum:
                // Die Zyklen mit der höchsten Summe aller Prioritäten werden verwendet
                if currentPath.prioritySum > bestCycles[0].prioritySum {
                    bestCycles.removeAll()
                    bestCycles.append(currentPath)
                } else if currentPath.prioritySum == bestCycles[0].prioritySum {
                    bestCycles.append(currentPath)
                }
            case .PriorityAverage:
                // Die Zyklen mit dem höchsten Durchschnitt aller Prioritätet werden verwendet
                if currentPath.priorityAverage > bestCycles[0].priorityAverage {
                    bestCycles.removeAll()
                    bestCycles.append(currentPath)
                } else if currentPath.priorityAverage == bestCycles[0].priorityAverage {
                    bestCycles.append(currentPath)
                }
            }
            return
        }
        
        // Hat bereits eine ausgehende Kante:
        guard self[coordinates].moveDirection == nil else { return }
        // Startknoten ist nicht erreichbar:
        guard currentPath.length == 0 || isReachable(currentPath.start, from: coordinates) else { return }
        
        for (direction, priority) in self[coordinates].possibleMoves {
            // Überprüfen, ob Kante gewählt werden darf:
            guard priority > 0 || (self[coordinates].distance + 1 < maxDist && remainingWrong > 0) else { continue }
            
            self[coordinates].moveDirection = direction
            
            // Knoten und Kante zum Weg hinzufügen und rekursiv den nächsten Knoten aufrufen
            currentPath.append(coordinates, moveDirection: direction, priority: priority)
            searchCycle(coordinates: coordinatesInMoveDirection(coordinates), remainingWrong: priority <= 0 ? remainingWrong - 1 : remainingWrong, currentPath: &currentPath, bestCycles: &bestCycles, cycleSelection: cycleSelection)
            currentPath.removeLast(priority: priority)
        }
        
        // Ausgewählte Kante des Knotens wieder entfernen
        self[coordinates].moveDirection = nil
    }
    
    // Sucht die besten Zyklen für den Knoten mit dem Index coordinatesIndex im Array
    // sortedCoordinates. Für jeden dieser Zyklen wird die Methoden rekursiv aufgerufen,
    // wobei der nächste Knoten in sortedCoordinates bearbeitet wird.
    mutating func roundCoordinates(inout bestStates bestStates: [State], inout sortedCoordinates: [Coordinates], coordinatesIndex: Int, cycleSelection: CycleSelection, stateSelection: StateSelection, maxStates: Int?) {
        
        if coordinatesIndex == sortedCoordinates.count {
            // Alle Pakete fertig bearbeitet
            
            // Zustand kopieren und Schritte durchführen
            var resultState = self
            resultState.addCurrentMovesToSolution()
            resultState.move()
            resultState.calculateDistances()
            
            if bestStates.isEmpty {
                // Vorher noch keinen Zustand gefunden, daher hinzufügen:
                bestStates.append(resultState)
                return
            }
            
            switch stateSelection {
            case .FirstState:
                // Einfach den ersten Zustand (und evtl. die dahinter) nehmen, daher einfach hinzufügen:
                bestStates.append(resultState)
            case .SmallestMaximumTotal:
                // Den Zustand mit der kleinsten Maximal- und Gesamtdistanz als besten Zustand werten:
                if resultState.maxDist < bestStates[0].maxDist || (resultState.maxDist == bestStates[0].maxDist && resultState.totalDist < bestStates[0].totalDist) {
                    bestStates.removeAll()
                    bestStates.append(resultState)
                } else if resultState.maxDist == bestStates[0].maxDist && resultState.totalDist == bestStates[0].totalDist {
                    bestStates.append(resultState)
                }
            }
            
            return
        }
        
        let coordinates = sortedCoordinates[coordinatesIndex]
        
        // Als erstes keine schlechte Kante verwenden, dann eine, zwei usw.
        for (var remainingWrong = 0; remainingWrong <= packages.count * packages.count; remainingWrong++) {
            
            // Zyklen mit remainingWrong schlechten Kanten suchen:
            var currentPath = Path()
            var bestCycles = [Path]()
            searchCycle(coordinates: coordinates, remainingWrong: remainingWrong, currentPath: &currentPath, bestCycles: &bestCycles, cycleSelection: cycleSelection)
            
            if !bestCycles.isEmpty {
                // Es wurden Zyklen gefunden, alle durchlaufen:
                for cycle in bestCycles {
                    // Alle Kanten im Zyklus auswählen:
                    for (cycleCoords, direction) in cycle.vertices {
                        self[cycleCoords].moveDirection = direction
                    }
                    
                    // Zyklen für das nächste Paket finden:
                    roundCoordinates(bestStates: &bestStates, sortedCoordinates: &sortedCoordinates, coordinatesIndex: coordinatesIndex + 1, cycleSelection: cycleSelection, stateSelection: stateSelection, maxStates: maxStates)
                    
                    // Die Auswahl der Kanten im Zyklus wieder rückgängig machen:
                    for (cycleCoords, _) in cycle.vertices {
                        self[cycleCoords].moveDirection = nil
                    }
                    
                    if let maxStates = maxStates where bestStates.count >= maxStates {
                        // Es wurden genügend Zustände gefunden, daher kann abgebrochen werden
                        return
                    }
                }
                return
            }
        }
        
        // Es war nicht möglich, einen Zyklus zu finden, daher ohne fortfahren:
        roundCoordinates(bestStates: &bestStates, sortedCoordinates: &sortedCoordinates, coordinatesIndex: coordinatesIndex + 1, cycleSelection: cycleSelection, stateSelection: stateSelection, maxStates: maxStates)
    }
    
}

// Task findet Lösungen zu einer gegebenen Paketverteilung:
class Task {
    
    // Startstatus und Einstellungen:
    var startState: State
    let priorityDefinitions: [PriorityDefinition]
    let cycleSelections: [CycleSelection]
    let stateSelections: [StateSelection]
    let maxChoosenStates: Int
    let goBackOnNotDecreasingMaxDistance: Int
    let tryCombinations: Bool
    let tryCombinationsForEachStateSelection: Bool
    let tryRandom: Bool
    
    // Beste gefundene Lösung:
    var bestSolution: State? = nil
    
    // Kleinste Schrittanzahl für die aktuelle Strategie und die aktuelle Kombination
    // von Auswahlkriterien etc.:
    var currentBest = 0
    
    // Zeitpunkt, an dem angefangen wurde, mit der aktuellen Strategie und der
    // aktuellen Kombination von Auswahlkriterien etc. zu suchen:
    var startDate: NSDate?
    
    // Eine Aufgabe mit dem Startstatus und den Einstellungen initialisieren:
    init(startState: State, priorityDefinitions: [PriorityDefinition], cycleSelections: [CycleSelection], stateSelections: [StateSelection], maxChoosenStates: Int, goBackOnNotDecreasingMaxDistance: Int, tryCombinations: Bool, tryCombinationsForEachStateSelection: Bool, tryRandom: Bool) {
        self.startState = startState
        self.priorityDefinitions = priorityDefinitions
        self.cycleSelections = cycleSelections
        self.stateSelections = stateSelections
        self.maxChoosenStates = maxChoosenStates
        self.goBackOnNotDecreasingMaxDistance = goBackOnNotDecreasingMaxDistance
        self.tryCombinations = tryCombinations
        self.tryCombinationsForEachStateSelection = tryCombinationsForEachStateSelection
        self.tryRandom = tryRandom
    }
    
    // Führt einen Schritt für state aus. Die Methode wird rekursiv aufgerufen, sodass auch alle
    // weiteren Schritte ausgeführt werden.
    func solve(var state: State, moves: Int, random: Bool, priorityDefinitions: [PriorityDefinition], cycleSelections: [CycleSelection], stateSelections: [StateSelection]) -> Int {
        
        if state.isFinished {
            // Es wurde eine Lösung gefunden. Wenn sie besser oder die erste ist, dann muss
            // das bekanntgegeben werden.
            if let bestSolution = bestSolution {
                if moves < bestSolution.moves[0][0].characters.count {
                    self.bestSolution = state
                    print("Es wurde eine bessere Lösung mit \(moves) Schritten nach \(NSDate().timeIntervalSinceDate(startDate!)) Sekunden gefunden.")
                }
            } else {
                bestSolution = state
                print("Es wurde eine Lösung mit \(moves) Schritten nach \(NSDate().timeIntervalSinceDate(startDate!)) Sekunden gefunden.")
            }
            
            // Außerdem currentBest aktualisieren:
            currentBest = min(currentBest, moves)
            
            return 0
        }
        
        // Alle für diesen Schritt verwendeten Definitionen und Auswahlkriterien:
        let usedPriorityDefinitions: [PriorityDefinition]
        let usedCycleSelections: [CycleSelection]
        let usedStateSelection: StateSelection
        
        if random {
            // Strategie 3, also eine zufällige Kombination auswählen:
            usedPriorityDefinitions = [priorityDefinitions[Int(arc4random_uniform(UInt32(priorityDefinitions.count)))]]
            usedCycleSelections = [cycleSelections[Int(arc4random_uniform(UInt32(cycleSelections.count)))]]
            usedStateSelection = stateSelections[Int(arc4random_uniform(UInt32(stateSelections.count)))]
        } else {
            // Strategie 1 oder 2, daher die gegebene(n) Kombination(en) verwenden.
            // stateSelections kann in diesem Fall nur einen Wert besitzen.
            usedPriorityDefinitions = priorityDefinitions
            usedCycleSelections = cycleSelections
            usedStateSelection = stateSelections[0]
        }
        
        var bestStates = [State]()
        var sortedCoordinates = state.sortedCoordinatesArray()
        
        // Alle Kombinationen durchlaufen (nur bei Strategie 2 gibt es mehr als eine Kombination):
        for priorityDefinition in usedPriorityDefinitions {
            for cycleSelection in usedCycleSelections {
                // Kanten mit der gewählten Definition erstellen:
                state.createEdges(priorityDefinition)
                
                // Die besten Zustände generieren:
                state.roundCoordinates(bestStates: &bestStates, sortedCoordinates: &sortedCoordinates, coordinatesIndex: 0, cycleSelection: cycleSelection, stateSelection: usedStateSelection, maxStates: usedStateSelection == .FirstState ? maxChoosenStates : nil)
            }
        }
        
        var i = 0 // Index des nächsten Zustandes in bestStates
        var remainingUp: Int // Anzahl der zurückzugehenden Schritte
        
        if bestStates[i].maxDist == state.maxDist {
            // Maximaldistanz wurde nicht verkleinert
            remainingUp = goBackOnNotDecreasingMaxDistance
            solve(bestStates[i++], moves: moves + 1, random: random, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections)
        } else {
            remainingUp = solve(bestStates[i++], moves: moves + 1, random: random, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections) - 1
        }
        
        // Wenn notwendig und so eingestellt noch weitere Zustände ausprobieren, bis
        // einer gefunden wird, wo keine Schritte mehr zurückgegangen werden müssen
        // oder die Maximalanzahl an verwendeten Zuständen erreicht ist
        var done = 1
        while done < maxChoosenStates && i < bestStates.count && remainingUp > 0 {
            if bestStates[i].maxDist == state.maxDist {
                // Maximaldistanz nicht verkleinert, daher
                // kann remainingUp sich auch nicht ändern
                solve(bestStates[i++], moves: moves + 1, random: random, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections)
            } else {
                // remainingUp ist das Minimum von seinem Wert davor
                // und dem jetzt berechneten
                remainingUp = min(remainingUp, solve(bestStates[i++], moves: moves + 1, random: random, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections) - 1)
            }
            done++
        }
        
        return remainingUp
    }
    
    func run() {
        startState.calculateDistances()
        
        if tryCombinations {
            for priorityDefinition in priorityDefinitions {
                for cycleSelection in cycleSelections {
                    for stateSelection in stateSelections {
                        print("Mit den Einstellungen \(priorityDefinition) (Kantenpriorität), \(cycleSelection) (Zyklenauswahl) und \(stateSelection) (Zustandsauswahl) wird jetzt ein Plan erstellt.")
                        
                        currentBest = Int.max
                        startDate = NSDate()
                        solve(startState, moves: 0, random: false, priorityDefinitions: [priorityDefinition], cycleSelections: [cycleSelection], stateSelections: [stateSelection])
                        
                        print("Der beste Plan mit den Einstellungen benötigte \(currentBest) Schritte, es sind \(NSDate().timeIntervalSinceDate(startDate!)) Sekunden vergangen.")
                    }
                }
            }
        }
        
        if tryCombinationsForEachStateSelection {
            print("Es werden ab jetzt in jedem Schritt alle Kombinationen der Kantenpriorität und der Auswahl der Zyklen verwendet, das beste Ergebnis wird für den nächsten Schritt benutzt.")
            
            for stateSelection in stateSelections {
                print("Es wird ein Plan mit der Einstellung \(stateSelection) ertellt.")
                
                currentBest = Int.max
                startDate = NSDate()
                solve(startState, moves: 0, random: false, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: [stateSelection])
                
                print("Der beste Plan mit den Einstellungen benötigte \(currentBest) Schritte, es sind \(NSDate().timeIntervalSinceDate(startDate!)) Sekunden vergangen.")
            }
        }
        
        if tryRandom {
            print("Es werden jetzt zufällige Kriterien verwendet.")
            
            while true {
                print("Es wird ein Plan mit zufälligen Einstellungen ertellt.")
                
                currentBest = Int.max
                startDate = NSDate()
                solve(startState, moves: 0, random: true, priorityDefinitions: priorityDefinitions, cycleSelections: cycleSelections, stateSelections: stateSelections)
                
                print("Der beste Plan mit zufälligen Einstellungen benötigte \(currentBest) Schritte, es sind \(NSDate().timeIntervalSinceDate(startDate!)) Sekunden vergangen.")
            }
        }
    }
    
}
