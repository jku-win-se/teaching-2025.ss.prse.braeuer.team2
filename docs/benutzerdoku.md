# Benutzerdoku Lunchify

## Installation

1.) Lade Java Runtime Environment 21 oder höher herunter bzw. überprüfe ob es vorhanden ist 

2.) Lade die Lunchify .jar-Datei herunter.

3.) Starte das Programm mit einem Doppelklick.

Falls dies nicht funktionieren sollte kann die .jar_Datei auch so geöffnet werden.

1.) Lade zusaätlich zu Java auch noch javafx-sdk herunter

2.) Erstelle eine batch-datei mit diesem Inhalt:

@echo off
set PATH_TO_FX="<Pfad zu javafxsdk>"
set PATH_TO_JAR="<Pfad zur .jar Datei>" 
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar %PATH_TO_JAR%
pause

z.B.:

@echo off
set PATH_TO_FX="C:\Users\Lukas\Documents\javafx-sdk-21.0.6\lib"
set PATH_TO_JAR="%~dp0lunchify.jar"   Das JAR im gleichen Verzeichnis wie die Batch-Datei
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar %PATH_TO_JAR%
pause

3.) Doppelklick auf batch-Datei


## Login
![image](https://github.com/user-attachments/assets/2a8867b7-38f8-4bb9-afb5-7450392d97be)

1. E-Mail-Adresse eingeben
2. Passwort eingeben 
3. Klick auf Login Button, um zu verifizieren

## User
![image](https://github.com/user-attachments/assets/e1bbba09-26ee-4ba5-baca-5fb2fdc8a41a)

### 1. Rechnung einreichen

![Rechnung-hochladen](https://github.com/user-attachments/assets/6eb93743-ce0c-4429-9383-571e73d2070a)


#### 1.)  Datei auswählen
Nach dem Klick auf den Button öffnet sich der Datei-Explorer. Dort kann eine Rechnung im PDF-, PNG- oder JPEG-Format hochgeladen werden.
Bitte achte darauf, dass die Rechnung gut lesbar ist und das Bild möglichst nur die Rechnung selbst zeigt – zugeschnitten und ohne störende Hintergründe – damit sie automatisch erkannt werden kann.

#### 2.) Dateiinformationen
Nachdem eine Datei ausgewählt wurde, werden deren Pfad und Dateiname hier angezeigt.

#### 3.) "Rechnung hochladen" Button
Nachdem eine Datei ausgewählt wurde, kann hier der Upload gestartet und die automatische Erkennung der Rechnung durchgeführt werden.

### Rechnungsdaten bestätigen
![rechnung-bestaetigen](https://github.com/user-attachments/assets/587c6561-41e2-4f7b-864d-fac2b0349167)


Sobald die automatische Texterkennung abgeschlossen ist oder fehlende Angaben manuell eingegeben wurden, erscheint eine Übersicht der Rechnungsdaten.

#### 1.) Datum
Hier wird das Datum angezeigt, das die Software automatisch anhand des Rechnungsfotos erkannt hat. Dieses Datum kann bei Bedarf manuell angepasst werden.
Es sind nur Werktage als gültige Daten erlaubt – sollte ein Wochenende oder Feiertag ausgewählt werden, erscheint ein Hinweis und das Datum kann nicht bestätigt werden. 

#### 2.) Betrag
Hier wird der auf der Rechnung erkannte Betrag angezeigt. Dieser kann bei Bedarf manuell angepasst werden.

#### 3.) Rechnungstyp
Hier wird der erkannte Rechnungstyp angezeigt. Dieser kann bei Bedarf umgeändert werden.

#### 4.) Speichern-Button
Mit einem Klick auf diesen Button wird die Rechnung bestätigt – vorausgesetzt, alle erforderlichen Angaben wurden korrekt ausgefüllt. Anschließend wird die Rechnung hochgeladen und erscheint in der Übersicht der eingereichten Rechnungen. 

#### 5.) Abbrechen-Button
Mit einem Klick auf diesen Button wird der Vorgang abgebrochen, und der Prozess muss von vorne begonnen werden. Das bedeutet, dass ein neues Bild hochgeladen werden muss. 

Wenn alle Daten von der Software korrekt erkannt wurden und keine Änderungen mehr erforderlich sind, wird die Rechnung automatisch bestätigt und erfordert keine manuelle Überprüfung durch einen Administrator.

### 2. Eingereichte Rechnungen
![image](https://github.com/user-attachments/assets/8e781675-76c5-4902-9ad2-431a2aae4dd6)
#### 1.) Anträge können bearbeitet werden

#### 2.) Filter öffnen
![image](https://github.com/user-attachments/assets/b4bd6f6a-7712-4e49-8fbf-927593d42b68)
   1. nach Rechnungs-ID filtern
   2. nach Rechnungstyp filtern (SUPERMARKET | RESTAURANT)
   3. nach Rechnungsstatus filtern (ACCEPTED | PENDING | DENIED)
   4. nur nach aktuellem Monat filtern
   5. Filter anwenden

#### 3.) Zurück zu User Aktionen

### 3. Genehmigte Rechnungen
### 4. Offene Anträge
### 5. Eingereichte Rechnungen bearbeiten
![Screenshot 2025-04-21 191949](https://github.com/user-attachments/assets/ff0e5445-5e29-4293-a1ac-ed14765f36f5)
 #### 1.) RechnungsID
 Der Benutzer kann die ID der Rechnung sehen, die er im Moment bearbeitet.
 
 #### 2.) Betrag
 Der Benutzer kann den Betrag der Rechnung bearbeiten.
 
 #### 3.) Datum
 Der Benutzer sieht und kann das Rechnungsdatum bearbeiten.
 
 #### 4.) Typ
 Der Benutzer kann den Typ der Rechnung auswählen ("SUPERMARKET" oder "RESTAURANT").
 
 #### 5.) Back
 Der Benutzer kann zur vorherigen Seite zurückkehren.
 
 #### 6.) Delete 
 Der Benutzer kann die Rechnung löschen.
 
 #### 7.) Save
 Der Benutzer kann die bearbeitete Rechnung speichern.
 
### 6. Gesamterstttungsbetrag
### 7. Mitteilungszentrale
 ![Screenshot 2025-04-21 192311](https://github.com/user-attachments/assets/962c9c25-6c9f-4da2-89cb-50bde1805c59)
 
 #### 1.) Text und Änderungszeitpunkt 
 Der Benutzer sieht eine Liste von Nachrichten, die verschiedene Änderungen an Rechnungen betreffen. Jede Nachricht beschreibt eine Änderung des Status einer Rechnung, wie z.B. eine Änderung von "PENDING" auf "ACCEPTED" oder das Einreichen einer neuen Rechnung. Jede Nachricht enthält die Rechnungs-ID, den Betrag, den Refund und den Status.
 
 Der Benutzer sieht den Zeitstempel, wann die jeweilige Änderung vorgenommen wurde. Das hilft, den Verlauf der Änderungen nachzuvollziehen.
 
 #### 2.) Zurück 
 Der Benutzer kann zur vorherigen Seite zurückkehren, um weitere Änderungen vorzunehmen oder die Übersicht zu verlassen.
 
 #### 3.) Delete 
 Der Benutzer kann jede Nachricht löschen. Dies ermöglicht es, den Verlauf zu bereinigen, wenn beispielsweise veraltete oder irrelevante Nachrichten entfernt werden sollen.
### 8. Logout



## Admin
### Admin Rollenauswahl
![image](https://github.com/user-attachments/assets/e743e583-d2b7-49b6-a04b-c0496669f430)

1. User-Rolle (siehe Kapitel User)
2. Admin-Rolle
3. Logout

#### Admin Aktionen
![image](https://github.com/user-attachments/assets/385f3ea8-bf9d-48c4-a028-4959c441b365)
### 1. User anzeigen
<img width="350" alt="image" src="https://github.com/user-attachments/assets/147c6332-22be-4d0c-8b31-7c59214720ad" />

#### 1. User anzeigen
<img width="350" alt="image" src="https://github.com/user-attachments/assets/a116bbb5-1893-4af5-b243-976601a46e6b" />

#### 2. User suchen
![image](https://github.com/user-attachments/assets/6897e135-2cb2-4ca4-9388-a78d24201508)

 1.) Nutzernamen eingeben
 
 2.) Suchen --> neues Fenster öffnet sich
 
 <img width="298" alt="image" src="https://github.com/user-attachments/assets/4c66b737-472a-4ac3-aa80-5e29832e4700" />

  1. Vorname (änderbar)

  2. Nachname (änderbar)

  3. Username (nicht änderbar)
     
  4. Email (änderbar)

  5. Password (änderbar)
     
  6. Rolle (änderbar: USER | ADMIN)
      
  7. Status (änderbar: ACTIVE | BLOCKED)
      
  8. Fehlgeschlagene Anmeldeversuche (änderbar - ab 10 Benutzer --> BLOCKED)
      
  9. Erstellungsdatum des Benutzers (nicht änderbar)

  10. Speicher

  11. Abbrechen

  12. Zurück

 3.) Zurück zu User suchen Startseite

#### 3. User hinzufügen
![image](https://github.com/user-attachments/assets/ae2d6be2-5ef7-4122-8117-c1dfe4d85c33)

1.) Vorname 

2.) Nachname

3.) Username

4.) Email

5.) Passwort

6.) Passwort bestätigen

7.) Rolle (USER | ADMIN)

8.) Speichern

9.) Abbrechen

#### 4. Logout

#### 5. Zurück zum Admin Dashboard

### 2. Eingereichte Rechnungen bearbeiten
![image](https://github.com/user-attachments/assets/f634670a-fdb4-4910-bde2-b7c90cc4cc77)

1. Anträge können bearbeitet werden

![Screenshot 2025-04-21 185218](https://github.com/user-attachments/assets/5633560d-a176-47d0-8248-d8c898beaf37)

#### 1.) RechnungsID:
Der Benutzer kann die ID der Rechnung sehen, die er im Moment bearbeitet.

#### 2.) Betrag
Der Benutzer kann den Betrag der Rechnung bearbeiten.

#### 3.) Datum 
Der Benutzer sieht und kann das Rechnungsdatum bearbeiten.

#### 4.) Typ 
Der Benutzer kann den Typ der Rechnung auswählen ("SUPERMARKET" oder "RESTAURANT").

#### 5.) Username 
Der Benutzer sieht den Benutzernamen, dem die Rechnung zugeordnet ist, und kann ihn gegebenenfalls ändern.

#### 6.) Status
Der Benutzer kann den Status der Rechnung auswählen (z.B. "PENDING").

#### 7.) Image
Der Benutzer sieht den Link zu einem Bild der Rechnung und kann ihn bei Bedarf ändern.

#### 8.) Refund 
Der Benutzer sieht den Betrag für die Rückerstattung. Dieser wird je nach ausgewähltem Rechnungstyp automatisch angepasst.

#### 9.) Back 
Der Benutzer kann zur vorherigen Seite zurückkehren.

#### 10.) Delete
Der Benutzer kann die Rechnung löschen.

#### 11.) Save
Der Benutzer kann die änderungen speichern. 
   
### 2. Filter öffnen

![image](https://github.com/user-attachments/assets/575d31b6-e7f1-4a25-b365-971d57f18bbd)

   1. nach Rechnungs-ID filtern
   2. nach Rechnungstyp filtern (SUPERMARKET | RESTAURANT)
   3. nach Benutzer filtern
   4. nach Rechnungsstatus filtern (ACCEPTED | PENDING | DENIED)
   5. nur nach aktuellem Monat filtern
   6. Filter anwenden
3. Zurück zu Admin Aktionen

### 3. Statistik
![Statistik](https://github.com/user-attachments/assets/73ea8418-7476-4156-93b8-64b9c9efc4b3)


#### 1.) Statistik-Filter
Hier kann man filtern zwischen:
* Rückvergütung pro Monat
* Anzahl Rechnungen pro Monat
* Durchschnitt Rechnungen pro Benutzer

#### 2.) Rechnungstyp-Filter
Hier kann man filtern zwischen:
* beide (also es werden sowohl die Rechnungen vom Restaurant als auch vom Supermarkt angezeigt)
* Supermarkt
* Restaurant

#### 3.) Rechnungsstatus-Filter
Hier kann man filtern zwischen:
* alle (jede Rechnung wird angezeigt egal welcher Typ)
* nur akzeptiert
* nur abgelehnte
* nur ausstehende

Diese 3 Filter werden automatisch auf die beiden Diagramme und den beiden Exporten angewendet.

#### 4.) Balkendiagramm
Die letzten 12 Monate ab dem aktuellen Datum werden gemäß der gesetzten Filter angezeigt. Fährt man mit der Maus über einen Balken, ändert dieser seine Farbe und zeigt den entsprechenden absoluten Wert an, der ebenfalls grafisch dargestellt ist.

#### 5.) Kreisdiagramm
Angezeigt wird die Verteilung der letzten 12 Monate – abhängig von den gewählten Filtern.

#### 6.) Export
Es stehen zwei Buttons zur Verfügung – einer für den PDF-Export und einer für den CSV-Export. Nach dem Klick kann der gewünschte Speicherort ausgewählt werden. Die CSV-Datei ist so optimiert, dass sie sich direkt als Tabelle in Excel öffnen lässt. Beim PDF-Export werden sowohl das Balkendiagramm als auch eine Tabelle mit den entsprechenden Daten exportiert.


### 4. Erstattung bearbeiten

![Refund](https://github.com/user-attachments/assets/7e2157d1-5738-4a6b-82bc-8d75b64b2024)

#### 1.) Erstattung-Restaurant
Hier siehst du den aktuellen Erstattungsbetrag für Restaurant-Rechnungen. Du kannst ihn ändern, indem du einen neuen Wert eingibst und auf den Button „Aktualisieren“ klickst.
#### 2.) Erstattung-Supermarkt
Hier siehst du den aktuellen Erstattungsbetrag für Supermarkt-Rechnungen. Du kannst ihn ändern, indem du einen neuen Wert eingibst und auf den Button „Aktualisieren“ klickst.

#### 3.) Aktualisierungsbutton
Mit diesem Button werden die Erstattungsbeträge aktualisiert. Die Änderung tritt sofort mit dem heutigen Datum in Kraft und gilt bis zur nächsten Anpassung.
#### 4.) Erstattungshistorie-Tabelle
In dieser Tabelle ist die Historie der Änderungen an den Erstattungsbeträgen einsehbar. Sie zeigt die jeweiligen Beträge für Supermärkte und Restaurants, das Datum der Änderung sowie den Benutzer, der die Anpassung vorgenommen hat.

### 5. Export Daten
 ![Screenshot 2025-04-21 190509](https://github.com/user-attachments/assets/8dd72ad1-a8d7-4e90-9133-a4674c2c791c)
 
#### 1.) Datumsauswahl
Der Benutzer (Admin) kann den Monat und das Jahr auswählen, für den er die Daten exportieren möchte (z.B. „April 2025“). Das erleichtert es, gezielt den gewünschten Zeitraum für den Export festzulegen. Es wird ein Tagesfeld auch angezeigt, aber dies ist nicht relevant, es zählt nur das Monat in dem sich der ausgewählte Tag befindet. Ob der 1. April oder der 30. April ausgewählt wird, ändert nichts am Ergebnis, es werden in beiden Fällen alle eingereichten Rechnungen für den Monat April ausgegeben.
 
#### 2.) JSON Export 
Der Admin kann die Daten im JSON-Format exportieren. Durch den Export im JSON-Format erhält der Admin eine strukturierte und maschinenlesbare Datei, die für die Weiterverarbeitung oder Analyse in anderen Systemen genutzt werden kann. Der Nutzer kann sich den Speicherort selber aussuchen. Es wird unter dem Format "invoices-Monatsnamen-Jahr.json" abgespeichert.

### 6. Zurück zu Admin Rollenauswahl

### 7. Logout
