# Benutzerdoku Lunchify

## User

## Admin

### Statistik:
![Statistik](https://github.com/user-attachments/assets/62355f8c-0e8e-45ab-b464-72aa12848293)

#### 1.) Statistik-Filter:
Hier kann man filtern zwischen:
* Rückvergütung pro Monat
* Anzahl Rechnungen pro Monat
* Durchschnitt Rechnungen pro Benutzer

#### 2.) Rechnungstyp-Filter:
Hier kann man filtern zwischen:
* beide (also es werden sowohl die Rechnungen vom Restaurant als auch vom Supermarkt angezeigt)
* Supermarkt
* Restaurant

#### 3.) Rechnungsstatus-Filter:
Hier kann man filtern zwischen:
* alle (jede Rechnung wird angezeigt egal welcher Typ)
* nur akzeptiert
* nur abgelehnte
* nur ausstehende

Diese 3 Filter werden automatisch auf die beiden Diagramme und den beiden Exporten angewendet.

#### 4.) Balkendiagramm:
Die letzten 12 Monate ab dem aktuellen Datum werden gemäß der gesetzten Filter angezeigt. Fährt man mit der Maus über einen Balken, ändert dieser seine Farbe und zeigt den entsprechenden absoluten Wert an, der ebenfalls grafisch dargestellt ist.

#### 5.) Kreisdiagramm:
Angezeigt wird die Verteilung der letzten 12 Monate – abhängig von den gewählten Filtern.

#### 6.) Export:
Es stehen zwei Buttons zur Verfügung – einer für den PDF-Export und einer für den CSV-Export. Nach dem Klick kann der gewünschte Speicherort ausgewählt werden. Die CSV-Datei ist so optimiert, dass sie sich direkt als Tabelle in Excel öffnen lässt. Beim PDF-Export werden sowohl das Balkendiagramm als auch eine Tabelle mit den entsprechenden Daten exportiert.



### Edit Invoice Admin:
![Screenshot 2025-04-21 185218](https://github.com/user-attachments/assets/5633560d-a176-47d0-8248-d8c898beaf37)

#### 1.) RechnungsID:
Der Benutzer kann die ID der Rechnung sehen, die er im Moment bearbeitet.

#### 2.) Betrag:
Der Benutzer kann den Betrag der Rechnung bearbeiten.

#### 3.) Datum: 
Der Benutzer sieht und kann das Rechnungsdatum bearbeiten.

#### 4.) Typ: 
Der Benutzer kann den Typ der Rechnung auswählen ("SUPERMARKET" oder "RESTAURANT").

#### 5.) Username: 
Der Benutzer sieht den Benutzernamen, dem die Rechnung zugeordnet ist, und kann ihn gegebenenfalls ändern.

#### 6.) Status: 
Der Benutzer kann den Status der Rechnung auswählen (z.B. "PENDING").

#### 7.) Image: 
Der Benutzer sieht den Link zu einem Bild der Rechnung und kann ihn bei Bedarf ändern.

#### 8.) Refund: 
Der Benutzer sieht den Betrag für die Rückerstattung. Dieser wird je nach ausgewähltem Rechnungstyp automatisch angepasst.

#### 9.) Back: 
Der Benutzer kann zur vorherigen Seite zurückkehren.

#### 10.) Delete: 
Der Benutzer kann die Rechnung löschen.

#### 11.)Save: 
Der Benutzer kann die bearbeitete Rechnung speichern.



### Edit Invoices User:
![Screenshot 2025-04-21 191949](https://github.com/user-attachments/assets/ff0e5445-5e29-4293-a1ac-ed14765f36f5)

#### 1.) RechnungsID:
Der Benutzer kann die ID der Rechnung sehen, die er im Moment bearbeitet.

#### 2.) Betrag:
Der Benutzer kann den Betrag der Rechnung bearbeiten.

#### 3.) Datum: 
Der Benutzer sieht und kann das Rechnungsdatum bearbeiten.

#### 4.) Typ: 
Der Benutzer kann den Typ der Rechnung auswählen ("SUPERMARKET" oder "RESTAURANT").

#### 5.) Back: 
Der Benutzer kann zur vorherigen Seite zurückkehren.

#### 6.) Delete: 
Der Benutzer kann die Rechnung löschen.

#### 7.) Save: 
Der Benutzer kann die bearbeitete Rechnung speichern.



### Export Data Admin:
![Screenshot 2025-04-21 190509](https://github.com/user-attachments/assets/8dd72ad1-a8d7-4e90-9133-a4674c2c791c)

#### 1.) Datumsauswahl:
Der Benutzer (Admin) kann den Monat und das Jahr auswählen, für den er die Daten exportieren möchte (z.B. „April 2025“). Das erleichtert es, gezielt den gewünschten Zeitraum für den Export festzulegen. Es wird ein Tagesfeld auch angezeigt, aber dies ist nicht relevant, es zählt nur das Monat in dem sich der ausgewählte Tag befindet. Ob der 1. April oder der 30. April ausgewählt wird, ändert nichts am Ergebnis, es werden in beiden Fällen alle eingereichten Rechnungen für den Monat April ausgegeben.

#### 2.) JSON Export: 
Der Admin kann die Daten im JSON-Format exportieren. Durch den Export im JSON-Format erhält der Admin eine strukturierte und maschinenlesbare Datei, die für die Weiterverarbeitung oder Analyse in anderen Systemen genutzt werden kann. Der Nutzer kann sich den Speicherort selber aussuchen. Es wird unter dem Format "invoices-Monatsnamen-Jahr.json" abgespeichert.


### Messages User:
![Screenshot 2025-04-21 192311](https://github.com/user-attachments/assets/962c9c25-6c9f-4da2-89cb-50bde1805c59)

#### 1.) Text und Änderungszeitpunkt: 
Der Benutzer sieht eine Liste von Nachrichten, die verschiedene Änderungen an Rechnungen betreffen. Jede Nachricht beschreibt eine Änderung des Status einer Rechnung, wie z.B. eine Änderung von "PENDING" auf "ACCEPTED" oder das Einreichen einer neuen Rechnung. Jede Nachricht enthält die Rechnungs-ID, den Betrag, den Refund und den Status.

Der Benutzer sieht den Zeitstempel, wann die jeweilige Änderung vorgenommen wurde. Das hilft, den Verlauf der Änderungen nachzuvollziehen.

#### 2.) Zurück: 
Der Benutzer kann zur vorherigen Seite zurückkehren, um weitere Änderungen vorzunehmen oder die Übersicht zu verlassen.

#### 3.) Delete: 
Der Benutzer kann jede Nachricht löschen. Dies ermöglicht es, den Verlauf zu bereinigen, wenn beispielsweise veraltete oder irrelevante Nachrichten entfernt werden sollen.



