import Foundation

// Berechnet die Anzahl der Möglichkeiten
class Task {
  
  let bottles: Int // Anzahl der Flaschne
  let containers: [Int] // Die Größen der Behälter
  
  // Bereits berechnete Anzahlen als zweidimensionales Array. Noch nicht berechnete Anzahlen sind nil.
  // results[x][y] ist die Anzahl der Möglichkeiten für alle Behälter von 0 bis x und y Flaschen.
  var results: [[BigInt?]]
  
  init(bottles: Int, containers: [Int]) {
    self.bottles = bottles
    self.containers = containers
    self.results = [[BigInt?]](count: containers.count, repeatedValue: [BigInt?](count: bottles + 1, repeatedValue: nil))
  }
  
  // Startet die Berechnung
  func run() -> BigInt {
    return search(containers.count-1, bottles: bottles)
  }
  
  // Berechnet die Anzahl der Möglichkeiten, wenn die Behälter 0 bis container
  // und bottles Flaschen verwendet werden
  func search(container: Int, bottles: Int) -> BigInt {
    
    if let result = results[container][bottles] {
      // Für diese Zahlen wurde bereits die Anzahl der Möglichkeiten
      // ausgerechnet, daher kann dieses Ergebnis zurückgegeben werden.
      return result
    }
    
    var result: BigInt
    
    if container == 0 {
      // Der erste Behälter
      if bottles <= containers[container] {
        result = BigInt(intNr: 1)
      } else {
        result = BigInt(intNr: 0)
      }
      
    } else if bottles == 0 {
      // Keine Flaschen, daher gibt es eine Möglichkeit.
      result = BigInt(intNr: 1)
      
    } else if let above = results[container][bottles - 1] {
      // Zelle darüber wurde bereits berechnet
      
      // f(i, j+1) = f(i, j) + f(i-1, j+1) berechnen:
      result = above + search(container - 1, bottles: bottles)
      
      if bottles - containers[container] - 1 >= 0 {
        // - f(i-1, j-b_i), wenn notwendig
        result = result - search(container - 1, bottles: bottles - containers[container] - 1)
      }
    } else {
      // Zelle darüber nicht berechnet, also Summe aus den benötigten anderen Zellen bilden:
      result = BigInt(intNr: 0)
      for i in max(0, bottles - containers[container])...bottles {
        result += search(container - 1, bottles: i)
      }
    }
    
    // Ergebnis abspeichern und zurückgeben
    results[container][bottles] = result
    return result
  }
  
}
