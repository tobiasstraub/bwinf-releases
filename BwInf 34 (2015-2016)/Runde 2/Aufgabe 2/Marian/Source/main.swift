
import Foundation
import Darwin

let task = TaskReader().read()

// Asynchron die Befehle abfragen:
dispatch_async(dispatch_queue_create("missglueckte_drohnenlieferung_commands", DISPATCH_QUEUE_CONCURRENT)) {
    print("Verwende 'help' für eine Übersicht der Befehle.")
    while true {
        if let input = readLine() {
            if input == "h" || input == "help" {
                print("help / h - Hilfeübersicht anzeigen")
                print("quit / q - Programm beenden")
                print("info / i - Aktuell beste Schrittanzahl anzeigen")
                print("save / s - Bisher beste Lösung speichern")
            } else if input == "q" || input == "quit" {
                exit(0)
            } else if input == "i" || input == "info" {
                if let bestSolution = task.bestSolution {
                    print("Die beste gefundene Lösung benötigt \(bestSolution.moves[0][0].characters.count) Schritte.")
                } else {
                    print("Es wurde noch keine Lösung gefunden.")
                }
            } else if input == "s" || input == "save" {
                if let bestSolution = task.bestSolution {
                    do {
                        try bestSolution.solutionString.writeToFile("solution.txt", atomically: false, encoding: NSUTF8StringEncoding)
                        print("Die zurzeit beste Lösung wurde in der Datei 'solution.txt' gespeichert.")
                    } catch {
                        print("Die Datei konnte nicht erstellt werden.")
                    }
                } else {
                    print("Es wurde noch keine Lösung gefunden.")
                }
            }
        }
    }
}

task.run()

print("Das Programm ist nun fertig. Es können weiterhin Befehle eingegeben werden.")

// Endlosschleife, damit weiterhin Befehle abgefragt werden können:
while true {}
