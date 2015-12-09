import Foundation

// Koordinaten in Quadratien
typealias Coordinates = (row: Int, column: Int)

// Größe für Quadratien
typealias Dimensions = (height: Int, width: Int)

// Ein Nachbar von einem Feld hat Koordinaten und die Himmelsrichtung, in die der Nachbar ist
typealias Neighbor = (coordinates: Coordinates, direction: String)

// Die Art eines Feldes
enum FieldType: Character {
  case Black = "#"
  case White = " "
  case Kassiopeia = "K"
}

struct Quadratien {
  
  var fields: [[FieldType]] // Die Feldarten in einem zweidimensionalen Array
  var dimensions: Dimensions // Die Größe von Quadratien
  var remainingWhiteFields: Int // Anzahl der restlichen weißen Felder
  var startingPoint: Coordinates // Anfangspunkt von Kassiopeia
  
  // Zugriff auf die Feldarten
  subscript(coordinates: Coordinates) -> FieldType {
    get {
      return fields[coordinates.row][coordinates.column]
    } set {
      fields[coordinates.row][coordinates.column] = newValue
    }
  }
  
  // Berechnet die Nachbarn des Feldes mit den gegebenen Koordinaten
  func neighborsForCoordinates(coordinates: Coordinates) -> [Neighbor] {
    var neighbors = [Neighbor]() // alle Nachbarn
    
    // In allen vier Himmelsrichtungen nachschauen:
    for neighbor in [
      (coordinates: (row: coordinates.row - 1, column: coordinates.column), direction: "N"),
      (coordinates: (row: coordinates.row, column: coordinates.column + 1), direction: "O"),
      (coordinates: (row: coordinates.row + 1, column: coordinates.column), direction: "S"),
      (coordinates: (row: coordinates.row, column: coordinates.column - 1), direction: "W")
      ] {
        if neighbor.coordinates.row >= 0 && neighbor.coordinates.column >= 0 && neighbor.coordinates.row < dimensions.height && neighbor.coordinates.column < dimensions.width {
          // Der Nachbar befindet sich noch in Quadratien, also der Liste hinzufügen
          neighbors.append(neighbor)
        }
    }
    
    return neighbors
  }
  
}

// Prüft, ob alle weißen Felder miteinander verbunden sind
class CheckConnectivityTask {
  
  var quadratien: Quadratien
  
  init(quadratien: Quadratien) {
    self.quadratien = quadratien
  }
  
  func run() -> Bool {
    return depthFirstSearchAtCoordinates(quadratien.startingPoint)
  }
  
  // Tiefensuche an den gegebenen Koordinaten
  func depthFirstSearchAtCoordinates(coordinates: Coordinates) -> Bool {
    
    // Feld wurde gefunden
    quadratien[coordinates] = .Black
    quadratien.remainingWhiteFields--
    
    if quadratien.remainingWhiteFields == 0 {
      // Alle Felder wurden gefunden
      return true
    }
    
    // Alle weißen Nachbarn durchlaufen
    for neighbor in quadratien.neighborsForCoordinates(coordinates) where quadratien[neighbor.coordinates] != .Black {
      if depthFirstSearchAtCoordinates(neighbor.coordinates) {
        // Tiefensuche konnte alle weißen Felder finden
        return true
      }
    }
    
    // Es wurden (noch) nicht alle weißen Felder gefunden
    return false
  }
  
}

// Prüft, ob Kassiopeia an alle weißen Felder rankommen kann, ohne ein Feld mehrfach zu überqueren
class SearchWayTask {
  
  var quadratien: Quadratien
  
  init(quadratien: Quadratien) {
    self.quadratien = quadratien
  }
  
  func run() -> String? {
    return depthFirstSearchAtCoordinates(quadratien.startingPoint, path: "")
  }
  
  // Tiefensuche an den gegebenen Koordinaten und dem bisherigen Pfad dorthin
  func depthFirstSearchAtCoordinates(coordinates: Coordinates, path: String) -> String? {
    
    // Feld wurde gefunden
    quadratien[coordinates] = .Black
    quadratien.remainingWhiteFields--
    
    if quadratien.remainingWhiteFields == 0 {
      // Alle Felder wurden gefunden
      return path
    }
    
    // Alle weißen Nachbarn durchlaufen
    for neighbor in quadratien.neighborsForCoordinates(coordinates) where quadratien[neighbor.coordinates] != .Black {
      if let path = depthFirstSearchAtCoordinates(neighbor.coordinates, path: path + neighbor.direction) {
        // Tiefensuche hat einen passenden Pfad gefunden
        return path
      }
    }
    
    // Feld muss wieder zurückgesetzt werden, da es hier nicht funktioniert hat, einen passenden Weg zu finden
    quadratien[coordinates] = .White
    quadratien.remainingWhiteFields++
    return nil
  }
  
}

// Prüft beide Aufgaben
class Task {
  
  var quadratien: Quadratien
  
  init(quadratien: Quadratien) {
    self.quadratien = quadratien
  }
  
  func run() -> (connected: Bool, way: String?) {
    // Prüfen, ob alle weißen Felder verbunden sind
    let connected = CheckConnectivityTask(quadratien: quadratien).run()
    
    // Prüfen, ob Kassiopeia alle weißen Felder ohne mehrfachen Besuch besuchen kann.
    // Da Quadratien ein 'value type' ist, wird es beim Funktionenaufruf kopiert und kann hier ein zweites Mal verwendet werden, ohne die veränderten Werte zurückzusetzen.
    let way = connected ? SearchWayTask(quadratien: quadratien).run() : nil
    
    return (connected, way)
  }
  
}