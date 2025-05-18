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

3.) Starte das Programm mit einem Doppelklick auf die batch-Datei


## Login
![Screenshot 2025-05-18 200756](https://github.com/user-attachments/assets/4edf5d1c-c83d-40bf-aa1a-5921befd3f67)

1. E-Mail-Adresse eingeben
2. Passwort eingeben 
3. Klick auf Login Button, um zu verifizieren

## User
![Screenshot 2025-05-18 203443](https://github.com/user-attachments/assets/1ac8b99d-92b8-4cfc-b081-58cf3aab06b9)


### 1. Rechnung einreichen

![Screenshot 2025-05-18 203606](https://github.com/user-attachments/assets/2ff53a3a-4832-43fe-b6ea-b42889274c62)

#### 1.)  Datei auswählen
Nach dem Klick auf den Button öffnet sich der Datei-Explorer. Dort kann eine Rechnung im PDF-, PNG- oder JPEG-Format hochgeladen werden.
Bitte achte darauf, dass die Rechnung gut lesbar ist und das Bild möglichst nur die Rechnung selbst zeigt – zugeschnitten und ohne störende Hintergründe – damit sie automatisch erkannt werden kann.

#### 2.) Dateiinformationen
Nachdem eine Datei ausgewählt wurde, werden deren Pfad und Dateiname hier angezeigt.

#### 3.) "Rechnung hochladen" Button
Nachdem eine Datei ausgewählt wurde, kann hier der Upload gestartet und die automatische Erkennung der Rechnung durchgeführt werden.

### Rechnungsdaten bestätigen
![Screenshot 2025-05-18 204042](https://github.com/user-attachments/assets/a1440666-e831-4b5a-8ea9-ea11402b9854)

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
![Screenshot 2025-05-18 203735](https://github.com/user-attachments/assets/1d9647f2-6299-404d-80db-b8fedc784883)

#### 1.) Anträge können bearbeitet werden

#### 2.) Filter öffnen
![Screenshot 2025-05-18 204203](https://github.com/user-attachments/assets/a01f4cf4-ed48-4dc0-abc6-ef1c23b8633f)

   1. nach Rechnungs-ID filtern
   2. nach Rechnungstyp filtern (SUPERMARKET | RESTAURANT)
   3. nach Rechnungsstatus filtern (ACCEPTED | PENDING | DENIED)
   4. nur nach aktuellem Monat filtern
   5. Filter anwenden

#### 3.) Zurück zu User Aktionen

### 3. Eingereichte Rechnungen bearbeiten

![Screenshot 2025-05-18 204342](https://github.com/user-attachments/assets/0f3010a9-d620-4192-bafa-4cd50509ab37)

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
 
### 4. Mitteilungszentrale
 ![Screenshot 2025-05-18 204500](https://github.com/user-attachments/assets/7b6ad83b-0f18-4344-a09f-43d3344a93fb)

 #### 1.) Text und Änderungszeitpunkt 
 Der Benutzer sieht eine Liste von Nachrichten, die verschiedene Änderungen an Rechnungen betreffen. Jede Nachricht beschreibt eine Änderung des 
 Status einer Rechnung, wie z.B. eine Änderung von "PENDING" auf "ACCEPTED" oder das Einreichen einer neuen Rechnung. Jede Nachricht enthält die 
 Rechnungs-ID, den Betrag, den Refund und den Status.
 
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
![Screenshot 2025-05-18 200806](https://github.com/user-attachments/assets/c7be5ac2-9e88-442e-9702-bae7dd650880)

### 1. User anzeigen
<img width="350" alt="image" src="https://github.com/user-attachments/assets/147c6332-22be-4d0c-8b31-7c59214720ad" />

#### 1. User anzeigen
<img width="350" alt="image" src="https://github.com/user-attachments/assets/a116bbb5-1893-4af5-b243-976601a46e6b" />

#### 2. User suchen
![Screenshot 2025-05-18 205151](https://github.com/user-attachments/assets/d7d1572f-b910-4ded-936c-d6c25a975431)

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
![Screenshot 2025-05-18 205319](https://github.com/user-attachments/assets/f8f2a13a-5df1-4fd9-b6bd-ea8689b9235a)

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
![Screenshot 2025-05-18 205436](https://github.com/user-attachments/assets/8e265104-d622-477c-a206-d14e27d67663)

Anträge können bearbeitet, Filter können über den Suchbutton eingestellt und zur vorherigen Seite zurückgekehrt werden.

#### 1.) Rechnungen bearbeiten:
![Screenshot 2025-05-18 205657](https://github.com/user-attachments/assets/f50d67b0-a748-4fda-82cb-2c013a6ea1b8)

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

![Screenshot 2025-05-18 204203](https://github.com/user-attachments/assets/8f1d4455-4b45-43c4-a289-687a02fa67bd)

   1. nach Rechnungs-ID filtern
   2. nach Rechnungstyp filtern (SUPERMARKET | RESTAURANT)
   3. nach Benutzer filtern
   4. nach Rechnungsstatus filtern (ACCEPTED | PENDING | DENIED)
   5. nur nach aktuellem Monat filtern
   6. Filter anwenden
3. Zurück zu Admin Aktionen

### 3. Statistik
![Screenshot 2025-05-18 205946](https://github.com/user-attachments/assets/f294f8da-2c75-48b1-81ed-a81e25add6d0)

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

![Screenshot 2025-05-18 210104](https://github.com/user-attachments/assets/e57af506-c580-4378-b213-675976a23fc9)

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

### 6. Mitteilungszentrale
![Screenshot 2025-05-18 200626](https://github.com/user-attachments/assets/d5ac599f-a30c-4de8-92a5-fb3238226ccf)
Der Admin bekommt eine Benachrichtung, wenn ein User mehrmals seine eigenen Rechnungen bearbeitet hat. Er kann über den Link (1) auf das Profil direkt zugreifen. Man sieht desweiteren auch den Zeitpunkt der Nachricht. Diese Nachricht kann auch wieder gelöscht werden (3).

### 7. Zurück zu Admin Rollenauswahl

### 8. Logout
