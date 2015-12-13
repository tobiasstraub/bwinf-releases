Public Class eingabe

    Public closing_ok As Boolean = False

    Private Sub Button1_Click(sender As Object, e As EventArgs) Handles Button1.Click

        field_x = NumericUpDown1.Value
        field_y = NumericUpDown2.Value

        nest_x = NumericUpDown9.Value
        nest_y = NumericUpDown10.Value

        If nest_x > field_y Or nest_y > field_y Then
            MsgBox("Stellen Sie sicher, dass sich die Position des Nests im Rahmen der Spielfeldbegrenzungen befindet.")
            Return
        End If

        futterquellen_anzahl = NumericUpDown7.Value
        futterquellen_kapazitaet = NumericUpDown8.Value

        anzahl_ameisen = NumericUpDown3.Value
        ameisen_movement_target_range = NumericUpDown6.Value
        ameisen_wahrscheinlichkeit_richtungsaenderung = TextBox1.Text / 100
        pheromoneAnzahl = NumericUpDown4.Value

        field_display_size = Double.Parse(TextBox2.Text)
        zoom_verzerrend = Double.Parse(TextBox3.Text)

        farbe_ameise_normal = color_ameise_normal.BackColor
        farbe_ameise_beladen = color_ameise_beladen.BackColor
        farbe_futter = color_futter.BackColor
        farbe_pheromone = color_pheromone.BackColor
        farbe_nest = color_nest.BackColor

        stift_ameise_normal = New SolidBrush(farbe_ameise_normal)
        stift_ameise_beladen = New SolidBrush(farbe_ameise_beladen)
        stift_futter = New SolidBrush(farbe_futter)
        stift_pheromone = New SolidBrush(farbe_pheromone)
        stift_nest = New SolidBrush(farbe_nest)
        stift_radieren = New SolidBrush(Form1.BackColor)

        timer_interval = NumericUpDown5.Value

        ameisen = New Ameise(anzahl_ameisen) {}
        field = New FieldElement(field_x, field_y) {}

        beachte_konzentration = CheckBox1.Checked

        closing_ok = True
        Me.Close()

    End Sub

    Private Sub Form1_FormClosing(sender As Object, e As FormClosingEventArgs) Handles Me.FormClosing
        If Not closing_ok Then
            End
        End If
    End Sub

    Private Sub color_ameise_normal_Click(sender As Object, e As EventArgs) Handles color_ameise_normal.Click
        ColorDialog1.ShowDialog()
        color_ameise_normal.BackColor = ColorDialog1.Color
    End Sub

    Private Sub color_ameise_beladen_Click(sender As Object, e As EventArgs) Handles color_ameise_beladen.Click
        ColorDialog1.ShowDialog()
        color_ameise_beladen.BackColor = ColorDialog1.Color
    End Sub

    Private Sub color_pheromone_Click(sender As Object, e As EventArgs) Handles color_pheromone.Click
        ColorDialog1.ShowDialog()
        color_pheromone.BackColor = ColorDialog1.Color
    End Sub

    Private Sub color_futter_Click(sender As Object, e As EventArgs) Handles color_futter.Click
        ColorDialog1.ShowDialog()
        color_futter.BackColor = ColorDialog1.Color
    End Sub

    Private Sub color_nest_Click(sender As Object, e As EventArgs) Handles color_nest.Click
        ColorDialog1.ShowDialog()
        color_nest.BackColor = ColorDialog1.Color
    End Sub

    Private Sub CheckBox1_CheckedChanged(sender As Object, e As EventArgs) Handles CheckBox1.CheckedChanged

    End Sub
End Class