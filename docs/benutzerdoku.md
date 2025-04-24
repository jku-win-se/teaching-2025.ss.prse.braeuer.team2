# Benutzerdoku Lunchify
## Login
![image](https://github.com/user-attachments/assets/2a8867b7-38f8-4bb9-afb5-7450392d97be)

1. E-Mail-Adresse eingeben
2. Passwort eingeben
3. Klick auf Login Button, um zu verifizieren

## Admin
### Admin Rollenauswahl
![image](https://github.com/user-attachments/assets/e743e583-d2b7-49b6-a04b-c0496669f430)

1. User-Rolle (siehe Kapitel User)
2. Admin-Rolle
3. Logout

#### Admin Aktionen
![image](https://github.com/user-attachments/assets/385f3ea8-bf9d-48c4-a028-4959c441b365)
### 1. User anzeigen
![image](https://github.com/user-attachments/assets/d0c059ce-abb2-4eab-a731-303db6c9e645)
#### 1. User suchen
![image](https://github.com/user-attachments/assets/6897e135-2cb2-4ca4-9388-a78d24201508)
 1. Nutzernamen eingeben
 2. Suchen --> neues Fenster öffnet sich
    ![image](https://github.com/user-attachments/assets/aea05272-e43b-49d7-95a4-3b8a2be958dc)
  1. Vorname (änderbar)
  2. Nachname (änderbar)
  3. Username (nicht änderbar)
  4. Email (änderbar)
  5. Rolle (änderbar: USER | ADMIN)
  6. Status (änderbar: ACTIVE | BLOCKED)
  7. Fehlgeschlagene Anmeldeversuche (änderbar - ab 10 Benutzer --> BLOCKED)
  8. Erstellungsdatum des Benutzers (nicht änderbar)
 3. Zurück zu User suchen Startseite

#### 2. User hinzufügen
![image](https://github.com/user-attachments/assets/ae2d6be2-5ef7-4122-8117-c1dfe4d85c33)
1. Vorname 
2. Nachname
3. Username
4. Email
5. Passwort
6. Passwort bestätigen
7. Rolle (änderbar: USER | ADMIN)

### 2. Anträge bearbeiten
![image](https://github.com/user-attachments/assets/f634670a-fdb4-4910-bde2-b7c90cc4cc77)

1. Anträge können bearbeitet werden

![Screenshot 2025-04-21 185218](https://github.com/user-attachments/assets/5633560d-a176-47d0-8248-d8c898beaf37)

1.) RechnungsID:
Der Benutzer kann die ID der Rechnung sehen, die er im Moment bearbeitet.

2.) Betrag:
Der Benutzer kann den Betrag der Rechnung bearbeiten.

3.) Datum: 
Der Benutzer sieht und kann das Rechnungsdatum bearbeiten.

4.) Typ: 
Der Benutzer kann den Typ der Rechnung auswählen ("SUPERMARKET" oder "RESTAURANT").

5.) Username: 
Der Benutzer sieht den Benutzernamen, dem die Rechnung zugeordnet ist, und kann ihn gegebenenfalls ändern.

6.) Status: 
Der Benutzer kann den Status der Rechnung auswählen (z.B. "PENDING").

7.) Image: 
Der Benutzer sieht den Link zu einem Bild der Rechnung und kann ihn bei Bedarf ändern.

8.) Refund: 
Der Benutzer sieht den Betrag für die Rückerstattung. Dieser wird je nach ausgewähltem Rechnungstyp automatisch angepasst.

9.) Back: 
Der Benutzer kann zur vorherigen Seite zurückkehren.

10.) Delete: 
Der Benutzer kann die Rechnung löschen.

11.) Save
Der Benutzer kann die änderungen speichern. 
   
2. Filter öffnen

![image](https://github.com/user-attachments/assets/575d31b6-e7f1-4a25-b365-971d57f18bbd)

   1. nach Rechnungs-ID filtern
   2. nach Rechnungstyp filtern (SUPERMARKET | RESTAURANT)
   3. nach Benutzer filtern
   4. nach Rechnungsstatus filtern (ACCEPTED | PENDING | DENIED)
   5. nur nach aktuellem Monat filtern
   6. Filter anwenden
3. Zurück zu Admin Aktionen

### 3. Statistik
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


### 4. Erstattung bearbeiten

### 5. Export Daten

### 6. Zurück zu Admin Rollenauswahl

### 7. Logout




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


