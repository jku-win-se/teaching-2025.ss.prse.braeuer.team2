# Benutzerdoku Lunchify

## User

### Rechnung hochladen:

![Rechnung_hochladen](https://github.com/user-attachments/assets/cced8817-1f6f-4dfa-a01b-a021b3efc0b2)

#### 1.)  Datei auswählen:
Nach dem Klick auf den Button öffnet sich der Datei-Explorer. Dort kann eine Rechnung im PDF-, PNG- oder JPEG-Format hochgeladen werden.
Bitte achte darauf, dass die Rechnung gut lesbar ist und das Bild möglichst nur die Rechnung selbst zeigt – zugeschnitten und ohne störende Hintergründe – damit sie automatisch erkannt werden kann.

#### 2.) Dateiinformationen
Nachdem eine Datei ausgewählt wurde, werden deren Pfad und Dateiname hier angezeigt.

#### 3.) "Rechnung hochladen" Button
Nachdem eine Datei ausgewählt wurde, kann hier der Upload gestartet und die automatische Erkennung der Rechnung durchgeführt werden.

### Rechnungsdaten bestätigen:
![Rechnung_bestätigen](https://github.com/user-attachments/assets/a78cdf7e-2816-4567-968c-91964fd1146d) 

Sobald die automatische Texterkennung abgeschlossen ist oder fehlende Angaben manuell eingegeben wurden, erscheint eine Übersicht der Rechnungsdaten.

#### 1.) Datum:
Hier wird das Datum angezeigt, das die Software automatisch anhand des Rechnungsfotos erkannt hat. Dieses Datum kann bei Bedarf manuell angepasst werden.
Es sind nur Werktage als gültige Daten erlaubt – sollte ein Wochenende oder Feiertag ausgewählt werden, erscheint ein Hinweis und das Datum kann nicht bestätigt werden. 

#### 2.) Betrag:
Hier wird der auf der Rechnung erkannte Betrag angezeigt. Dieser kann bei Bedarf manuell angepasst werden.

#### 3.) Rechnungstyp:
Hier wird der erkannte Rechnungstyp angezeigt. Dieser kann bei Bedarf umgeändert werden.

#### 4.) Speichern-Button
Mit einem Klick auf diesen Button wird die Rechnung bestätigt – vorausgesetzt, alle erforderlichen Angaben wurden korrekt ausgefüllt. Anschließend wird die Rechnung hochgeladen und erscheint in der Übersicht der eingereichten Rechnungen. 

#### 5.) Abbrechen-Button
Mit einem Klick auf diesen Button wird der Vorgang abgebrochen, und der Prozess muss von vorne begonnen werden. Das bedeutet, dass ein neues Bild hochgeladen werden muss. 

Wenn alle Daten von der Software korrekt erkannt wurden und keine Änderungen mehr erforderlich sind, wird die Rechnung automatisch bestätigt und erfordert keine manuelle Überprüfung durch einen Administrator.

 

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

### Erstattung bearbeiten:

![Erstattung](https://github.com/user-attachments/assets/23d86f2e-d433-4dd4-8618-564c330e2ea0)

#### 1.)  Erstattung-Resturant:
Hier wird der aktuelle Betrag angezeigt, den man für eine hochgeladene Restaurantrechnung erhält. Dieser Betrag kann bei Bedarf geändert werden. Die Änderung gilt ab dem heutigen Datum bis zu dem Zeitpunkt, an dem die Erstattung erneut angepasst wird.

#### 2.)  Erstattung-Supermarkt:
Hier wird der aktuelle Betrag angezeigt, den man für eine hochgeladene Supermarktrechnung erhält. Dieser Betrag kann bei Bedarf geändert werden. Die Änderung gilt ab dem heutigen Datum bis zu dem Zeitpunkt, an dem die Erstattung erneut angepasst wird.

#### 3.) Aktualisieren-Button:
Durch Klicken auf den Button werden die neuen Erstattungsbeträge auf die Rechnungen angewendet.

#### 4.) Erstattungs-Historie:
Diese Tabelle zeigt die letzten Änderungen der Erstattungsbeträge. Sie gibt an, auf welchen Betrag die Erstattungen geändert wurden, wer die Änderung vorgenommen hat und an welchem Tag dies geschehen ist.


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



