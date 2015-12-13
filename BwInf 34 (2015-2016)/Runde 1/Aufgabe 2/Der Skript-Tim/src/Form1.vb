Public Class Form1

    Public locked As Boolean = False
    Public ende As Boolean = False

    Private Sub Form1_Activated(sender As Object, e As EventArgs) Handles Me.Activated

        ' Fenstergröße anpassen
        Me.Size = New System.Drawing.Size(field_display_size * field_x + manipulation_window, field_display_size * field_y + manipulation_window)
        ' Timer starten
        Timer1.Enabled = True

    End Sub

    Private Sub Form1_Load(sender As Object, e As EventArgs) Handles Me.Load
        eingabe.ShowDialog()

        ' Feld mit neuen Zuständen füllen
        For x As Integer = 1 To field.GetLength(0)
            For y As Integer = 1 To field.GetLength(1)
                field(x - 1, y - 1) = New FieldElement
            Next
        Next

        ' Ameisen erschafen
        For a As Integer = 0 To ameisen.Count - 1
            ameisen(a) = New Ameise
        Next

        ' Futter platzieren
        For i As Integer = 1 To futterquellen_anzahl
            field(zufall.Next(0, field_x), zufall.Next(0, field_y)).futterpunkte += futterquellen_kapazitaet
        Next

        ' Timer-Intervall anpassen
        Timer1.Interval = timer_interval

    End Sub

    Private Sub Timer1_Tick(sender As Object, e As EventArgs) Handles Timer1.Tick
        If locked Or ende Then Return
        locked = True

        ' Simulation zu Ende?
        If nest_futter_counter = futterquellen_anzahl * futterquellen_kapazitaet Then
            ende = True

            MsgBox("Die Simulation ist beendet; alle " & (futterquellen_anzahl * futterquellen_kapazitaet) & " Futterpunkte wurden ins Nest gebracht." & vbNewLine & "Es wurden " & Me.Text & " Zeiteinheiten benötig.")
        End If

        ' Ameisen-Handlungen
        For Each a As Ameise In ameisen
            a.act()
        Next

        ' Pheromone verdunsten
        For Each f As FieldElement In field
            If f.duftpunkte > 0 Then
                f.duftpunkte -= 1
            End If
        Next

        ' Zeichnen
        drawField()

        ' Frames
        Me.Text += 1

        locked = False
    End Sub

    ' Spielfeld zeichnen
    Private Sub drawField()

        Dim formGraphics As System.Drawing.Graphics
        formGraphics = Me.CreateGraphics()

        ' Spielfeld löschen
        formGraphics.FillRectangle(stift_radieren, New Rectangle(0, 0, field_display_size * field_x + manipulation_window, field_display_size * field_y + manipulation_window))

        ' Pheromone zeichnen
        For x As Integer = 0 To field.GetLength(0) - 1
            For y As Integer = 0 To field.GetLength(1) - 1
                If field(x, y).duftpunkte > 0 Then
                    formGraphics.FillRectangle(stift_pheromone, New Rectangle(x * field_display_size, y * field_display_size, field_display_size, field_display_size))
                End If
            Next
        Next

        ' Ameisen zeichnen
        For Each a As Ameise In ameisen
            Dim brush As System.Drawing.SolidBrush

            If a.is_beladen Then
                ' Beladene Ameise
                brush = stift_ameise_beladen
            Else
                ' Normale Ameise
                brush = stift_ameise_normal
            End If
            formGraphics.FillRectangle(brush, New Rectangle(a.posX * field_display_size, a.posY * field_display_size, zoom_verzerrend * field_display_size, zoom_verzerrend * field_display_size))
        Next

        ' Nest zeichnen
        formGraphics.FillRectangle(stift_nest, New Rectangle(nest_x * field_display_size, nest_y * field_display_size, zoom_verzerrend * field_display_size, zoom_verzerrend * field_display_size))

        ' Futterpunkte zeichnen
        For x As Integer = 0 To field.GetLength(0) - 1
            For y As Integer = 0 To field.GetLength(1) - 1
                If field(x, y).futterpunkte > 0 Then
                    formGraphics.FillRectangle(stift_futter, New Rectangle(x * field_display_size, y * field_display_size, field_display_size * zoom_verzerrend, field_display_size * zoom_verzerrend))
                End If
            Next
        Next

        ' Ausgabe von Informationen
        Dim drawBrush As New SolidBrush(Color.Black)
        formGraphics.DrawString(nest_futter_counter.ToString, New Font("Arial", 8), drawBrush, New Rectangle(0, 0, 8 * 10, 20 * 8))

        formGraphics.Dispose()

    End Sub

    ' Simulation pausieren
    Private Sub Form1_Click(sender As Object, e As EventArgs) Handles Me.Click
        Timer1.Enabled = Not Timer1.Enabled
    End Sub
End Class