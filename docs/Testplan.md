# Testplan

## Bild hochladen

### Die OCR kann das Bild der Rechnung erkennen

1.) Laden Sie ein Bild mit hohem Kontrast (gut gedruckt) und ohne Falten hoch

2.) Nach ein paar Sekunden öffnet sich ein Fenster, in dem der Benutzer die erkannten Werte sehen kann

#### Ändern Sie die Werte der OCR 
3.) Ändern Sie einen Wert, z.B. das Datum auf ein anderes Datum im aktuellen Monat oder den Betrag (Datum=06.05.2025, Betrag=5,00 und Typ=Supermarkt)       
    -> prüft, dass nur korrekte Daten (Werktag, innerhalb des aktuellen Monats und nicht bereits ein Bild an diesem Tag hochgeladen) 
    und Betrag akzeptiert werden (nicht Minus und eine Zahl)

4.) Gehe zu „Eingereichte Rechnungen“

5.) In dieser Tabelle sollten Sie die hochgeladene Rechnung sehen (refund=3.00, status=PENDING, typ=Supermarket date=06.05.2025 und user=yourUserName)

6.) Der Status sollte PENDING sein.

#### Übernehmen Sie die Werte der OCR
3.) Ändern Sie die Werte nicht, sondern klicken Sie nur auf die Schaltfläche „Speichern“ (date=06.05.2025, amount=5.00 and type=Supermarket)

4.) Gehen Sie zu „Eingereichte Rechnungen“

5.) In dieser Tabelle sollten Sie die hochgeladene Rechnung sehen (refund=3.00, status=ACCEPTING, typ=Supermarket date=06.05.2025 and user=yourUserName)

6.) Der Status sollte ACCEPTED sein.

### Die OCR kann das Bild der Rechnung nicht erkennen

1.) Laden Sie ein Bild hoch, das die OCR nicht erkennen kann

2.) Sie sollten für jeden Wert ein eigenes Fenster zum Eintragen der Werte erhalten 
    -> nur korrekte Eingaben werden akzeptiert

3.) Nach der Eingabe der Werte sollten Sie eine Übersicht über alle Ihre Eingaben erhalten

4.) Hier können Sie Ihre Eingaben ändern (Datum=06.05.2025, Betrag=5.00 und Typ=Supermarkt)

5.) Klicken Sie auf die Schaltfläche „Speichern“.

6.) Gehen Sie zu „Eingereichte Rechnungen“

7.) In dieser Tabelle sollten Sie die hochgeladene Rechnung sehen (refund=3.00, status=PENDING, typ=Supermarket date=06.05.2025 and user=yourUserName)

8.) Der Status sollte PENDING sein.


## Statistik

### Diagramme
1.) Melden Sie sich als Administrator an

2.) Gehen Sie zu „Statistik“ (Admin-Dashboard)

3.) Die Diagramme sollten geladen und angezeigt werden 

4.) Überprüfe, ob die Diagramme mit den Daten der Rechnungsübersicht übereinstimmen 

5.) Überprüfe, ob sich die Diagramme entsprechend den Filtern sich ändern

### Exporte

#### PDF
1.) Klicke auf den Button "PDF"

2.) Wähle einen Ort zum Speichern

3.) Prüfe, ob der Name der PDF-Datei Rückschlüsse auf die gewählten Filter erlaubt.

4.) Öffne PDF

5.) Die PDF-Datei entspricht den Daten sowie den gewählten Filtern.

#### CSV
1.) Klicke auf den Button "CSV"

2.) Wähle einen Ort zum Speichern

3.) Prüfe, ob der Name der PDF-Datei Rückschlüsse auf die gewählten Filter erlaubt.

4.) Öffne CSV mit Excel

5.) Die CSV-Datei entspricht den Daten sowie den gewählten Filtern.

6.) Die Daten sind werden in Excel als Tabelle richtig dargestellt.


