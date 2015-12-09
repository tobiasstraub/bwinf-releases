import Foundation

let solution = TaskReader().read().run() // Aufgabe einlesen und ausführen

if solution.connected {
  print("Es ist möglich, alle weißen Felder zu erreichen.")
  if let way = solution.way {
    print(way)
  } else {
    print("Es gibt jedoch keinen Weg, mit dem kein weißes Feld mehrmals betreten werden muss.")
  }
} else {
  print("Es ist nicht möglich, alle weißen Felder zu erreichen.")
}
