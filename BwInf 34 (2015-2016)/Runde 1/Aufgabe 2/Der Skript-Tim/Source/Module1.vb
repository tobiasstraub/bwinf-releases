Module Module1

    Public field_x As Integer
    Public field_y As Integer

    Public nest_x As Integer
    Public nest_y As Integer

    Public futterquellen_anzahl As Integer
    Public futterquellen_kapazitaet As Integer

    Public anzahl_ameisen As Integer
    Public ameisen_movement_target_range As Integer
    Public ameisen_wahrscheinlichkeit_richtungsaenderung As Double
    Public pheromoneAnzahl As Integer

    Public field_display_size As Double
    Public zoom_verzerrend As Double

    Public timer_interval As Integer

    ' Farben
    Public farbe_ameise_normal As System.Drawing.Color
    Public farbe_ameise_beladen As System.Drawing.Color
    Public farbe_nest As System.Drawing.Color
    Public farbe_pheromone As System.Drawing.Color
    Public farbe_futter As System.Drawing.Color

    ' Stifte
    Public stift_ameise_normal As System.Drawing.SolidBrush
    Public stift_ameise_beladen As System.Drawing.SolidBrush
    Public stift_nest As System.Drawing.SolidBrush
    Public stift_pheromone As System.Drawing.SolidBrush
    Public stift_futter As System.Drawing.SolidBrush
    Public stift_radieren As System.Drawing.SolidBrush

    ' Laufzeitvariablen
    Public ameisen() As Ameise
    Public field(,) As FieldElement

    Public nest_futter_counter As Integer = 0
    Public zufall As New Random

    Public manipulation_window As Double = 50

    Public beachte_konzentration As Boolean = True

End Module

Public Class Ameise

    Public posX As Integer = nest_x
    Public posY As Integer = nest_y

    Public is_beladen As Boolean

    Public target_x As Integer = 0
    Public target_y As Integer = 0

    Public last_pheromone_fields As New List(Of _field)

    Public Sub act()
        ' Ameisen-Logik

        If is_beladen Then
            If posX = nest_x And posY = nest_y Then
                is_beladen = False
                nest_futter_counter += 1
                last_pheromone_fields.Clear()
            Else
                moveToNest()
                last_pheromone_fields.Clear()
            End If
        Else
            If Not sniffForFood() And Not followPheromones() Then
                performRandomMove()
            End If
        End If

    End Sub

    ' Bewegung zum Nest zurück (wenn beladen)
    Public Sub moveToNest()
        ' Pheromonpunkte absetzen und zum Nest bewegen
        field(posX, posY).duftpunkte += pheromoneAnzahl

        If posX > nest_x And posX > 0 Then
            posX -= 1
        ElseIf posX < nest_x
            posX += 1
        End If

        If posY > nest_y And posY > 0 Then
            posY -= 1
        ElseIf posY < nest_y
            posY += 1
        End If

    End Sub

    ' Zufällige Bewegung ausführen
    Public Sub performRandomMove()

        ' Temporäres Ziel
        If (target_x = 0 And target_y = 0) Or (posX = target_x And posY = target_y) Or zufall.Next(1, 101) / 100 < ameisen_wahrscheinlichkeit_richtungsaenderung Then
            ' Neues temporäres Ziel
            target_x = posX + zufall.Next(-ameisen_movement_target_range, ameisen_movement_target_range + 1)
            target_y = posY + zufall.Next(-ameisen_movement_target_range, ameisen_movement_target_range + 1)
        End If

        ' Ziel verfolgen
        If posX < target_x Then
            If posX < field.GetLength(0) - 1 Then
                posX += 1
            End If
        ElseIf posX > target_x
            If posX > 0 Then
                posX -= 1
            End If
        End If

        If posY < target_y Then
            If posY < field.GetLength(1) - 1 Then
                posY += 1
            End If
        ElseIf posY > target_y
            If posY > 0 Then
                posY -= 1
            End If
        End If
    End Sub

    ' Umliegende Felder auf Futter untersuchen
    Public Function sniffForFood()

        ' Links
        If posX > 0 Then

            If posY > 0 Then
                ' Links Oben: posX-1|posY-1
                If field(posX - 1, posY - 1).futterpunkte > 0 Then
                    field(posX - 1, posY - 1).futterpunkte -= 1
                    is_beladen = True
                    Return True
                End If
            End If

            ' Links Mitte: posX-1|posY
            If field(posX - 1, posY).futterpunkte > 0 Then
                field(posX - 1, posY).futterpunkte -= 1
                is_beladen = True
                Return True
            End If

            If posY < field_y Then
                ' Links unten: posX-1|posY+1
                If field(posX - 1, posY + 1).futterpunkte > 0 Then
                    field(posX - 1, posY + 1).futterpunkte -= 1
                    is_beladen = True
                    Return True
                End If
            End If

        End If

        ' Mitte
        If posY > 0 Then
            ' Mitte Oben
            If field(posX, posY - 1).futterpunkte > 0 Then
                field(posX, posY - 1).futterpunkte -= 1
                is_beladen = True
                Return True
            End If
        End If

        If posY < field_y Then
            ' Mitte unten
            If field(posX, posY + 1).futterpunkte > 0 Then
                field(posX, posY + 1).futterpunkte -= 1
                is_beladen = True
                Return True
            End If
        End If

        ' Rechts
        If posX < field_x Then
            If posY > 0 Then
                ' Rechts oben
                If field(posX + 1, posY - 1).futterpunkte > 0 Then
                    field(posX + 1, posY - 1).futterpunkte -= 1
                    is_beladen = True
                    Return True
                End If
            End If

            ' Rechts mitte
            If field(posX + 1, posY).futterpunkte > 0 Then
                field(posX + 1, posY).futterpunkte -= 1
                is_beladen = True
                Return True
            End If

            ' Rechts unten
            If posY < field_y Then
                If field(posX + 1, posY + 1).futterpunkte > 0 Then
                    field(posX + 1, posY + 1).futterpunkte -= 1
                    is_beladen = True
                    Return True
                End If
            End If

        End If

        Return False
    End Function

    ' Umliegende Felder auf Pheromone untersuchen und evtl. verfolgen
    Public Function followPheromones()

        Dim moeglichkeiten As New List(Of _field)
        Dim entfernung_current_field As Double = Math.Abs(Math.Sqrt((nest_x - (posX)) ^ 2 + (nest_y - (posY)) ^ 2))

        Dim konzentration_max As Integer = 0

        For dx As Integer = -1 To 1
            For dy As Integer = -1 To 1
                ' Nicht aktuelles Feld
                If Not (dx = 0 And dy = 0) Then
                    ' Feld erlaubt?
                    If posX + dx > 0 And posX + dx < field_x - 1 And posY + dy > 0 And posY + dy < field_y - 1 Then

                        ' Nicht in letzten 5 besuchten Pheromonfeldern
                        Dim f As Boolean = False
                        For Each a In last_pheromone_fields
                            If a.dx = posX + dx And a.dy = posY + dy Then
                                f = True
                            End If
                        Next

                        ' Mindestens ein Duftpunkt
                        If field(posX + dx, posY + dy).duftpunkte > 0 And Not f Then

                            Dim entfernung_temp As Double = Math.Abs(Math.Sqrt((nest_x - (posX + dx)) ^ 2 + (nest_y - (posY + dy)) ^ 2))
                            Dim konzentration As Integer = field(posX + dx, posY + dy).duftpunkte / pheromoneAnzahl

                            ' Bedingung: Muss vom Nest wegführen
                            If entfernung_temp > entfernung_current_field Then
                                moeglichkeiten.Add(New _field(dx, dy, konzentration))

                                If konzentration > konzentration_max Then
                                    konzentration_max = konzentration
                                End If

                            End If
                        End If

                    End If
                End If
            Next
        Next

        ' Dasjenige wegführende Feld mit der höchsten Konzentration ermitteln (sofern dies aktiviert ist)

        Dim moeglichkeiten_final As New List(Of _field)

        If beachte_konzentration Then
            For i As Integer = 0 To moeglichkeiten.Count - 1
                If moeglichkeiten(i).duftpunkte >= konzentration_max Then
                    moeglichkeiten_final.Add(moeglichkeiten(i))
                End If
            Next
        Else
            ' Pheromonkonzentration nicht beachten; Möglichkeiten bleiben unberührt
            moeglichkeiten_final = moeglichkeiten
        End If

        ' Eine zufällige Entscheidung wählen
        If moeglichkeiten_final.Count > 0 Then
            Dim satzRandom As _field = moeglichkeiten_final(zufall.Next(0, moeglichkeiten_final.Count))
            ' Bewegen
            posX += satzRandom.dx
            posY += satzRandom.dy

            If last_pheromone_fields.Count > 4 Then
                last_pheromone_fields.RemoveAt(0)
            End If

            last_pheromone_fields.Add(New _field(posX, posY, 0))

            Return True
        End If

        Return False
    End Function

    Public Class _field
        Public dx As Integer
        Public dy As Integer
        Public duftpunkte As Integer

        Public Sub New(ByVal x As Integer, ByVal y As Integer, ByVal k As Integer)
            dx = x
            dy = y
            duftpunkte = k
        End Sub

    End Class

End Class

Public Class FieldElement

    Public duftpunkte As Integer = 0
    Public futterpunkte As Integer = 0

End Class