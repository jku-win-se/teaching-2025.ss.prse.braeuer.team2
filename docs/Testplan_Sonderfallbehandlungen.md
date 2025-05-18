# Invoice Test
![image](https://github.com/user-attachments/assets/87b6119d-01c3-45ce-9eff-6f6dd7179665)
Der Test testUpdateInvoiceRefundInvalid überprüft, ob die Methode updateInvoice korrekt auf einen ungültigen oder fehlerhaften Aktualisierungsversuch einer Rechnung reagiert.

Im Test wird die Methode updateInvoice mit spezifischen Parametern aufgerufen. Dabei wird erwartet, dass die Aktualisierung fehlschlägt, weshalb mit assertFalse(success) geprüft wird, dass die Methode false zurückgibt. Dieser Test sichert ab, dass die updateInvoice-Methode bei ungültigen Eingaben oder nicht erfüllten Bedingungen die Aktualisierung verweigert und keinen Erfolg meldet. So wird gewährleistet, dass fehlerhafte Daten nicht in die Datenbank übernommen werden.


# OCR Test
![image](https://github.com/user-attachments/assets/952e15cb-b0fc-47ea-89c6-da56036be6df)

Der Test testInvoiceExists_False überprüft, ob die Methode invoiceExists korrekt erkennt, dass für einen bestimmten Benutzer an einem bestimmten Datum keine Rechnung in der Datenbank vorhanden ist. Dazu wird eine Datenbankverbindung geöffnet. 

Dieses Beispiel wurde so gewählt, dass in der Datenbank keine Rechnung für diesen Benutzer an diesem Datum existiert. Die Methode führt eine SQL-Abfrage aus, die prüft, ob mindestens ein Eintrag in der Tabelle rechnungen mit dem angegebenen Benutzernamen und Datum vorhanden ist, und gibt entsprechend true oder false zurück. Im Test wird mit assertFalse überprüft, dass die Methode false zurückgibt, also bestätigt, dass keine Rechnung existiert. Damit stellt der Test sicher, dass die Methode invoiceExists korrekt funktioniert und keine falschen Treffer meldet, wenn tatsächlich keine entsprechende Rechnung vorliegt.

# Login Test
![image](https://github.com/user-attachments/assets/75422ad2-ce93-4e40-aaf1-7ddab5e22c92)

Der Test testFailedAttemptsResetAfterSuccessfulLogin überprüft die korrekte Handhabung der fehlgeschlagenen Loginversuche beim Login, insbesondere die Rücksetzung der Fehlversuche nach einem erfolgreichen Login.

Zu Beginn des Tests wird durch die Hilfsmethode setFailedAttempts für einen Benutzer mit der E-Mail USER_EMAIL der Wert der fehlgeschlagenen Anmeldeversuche direkt in der Datenbank auf 3 gesetzt. Dies simuliert, dass dieser Benutzer bereits drei erfolglose Versuche unternommen hat, sich einzuloggen. Dann wird die statische Methode Login.validateLogin mit den korrekten Zugangsdaten des Benutzers aufgerufen. Innerhalb der Transaktion wird der Account anhand der übergebenen E-Mail aus der Datenbank abgerufen. Falls der Account existiert, werden die Rolle und der Status des Benutzers aus den Account-Daten ausgelesen und in die übergebenen StringBuilder-Objekte eingefügt. Befindet sich der Account im Status BLOCKED, wird ebenfalls die Transaktion abgeschlossen und false zurückgegeben, da ein gesperrtes Konto keine Anmeldung erlaubt. Ist das Passwort korrekt, wird das Login als erfolgreich eingestuft. Wenn das Passwort hingegen falsch ist, wird der Zähler der fehlgeschlagenen Loginversuche durch die Methode incrementFailedAttempts um eins erhöht, die Transaktion wird abgeschlossen und false zurückgegeben.

Im Test wird nach dem Aufruf von validateLogin überprüft, ob das Ergebnis true ist, also ob das Login erfolgreich war. Dies sichert, dass die korrekten Anmeldedaten akzeptiert wurden. Zusätzlich wird abgefragt, ob der Zähler für fehlgeschlagene Versuche in der Datenbank für diesen Benutzer wieder auf 0 gesetzt wurde. Dies erfolgt durch den Aufruf der Methode getFailedAttempts, die den aktuellen Wert aus der Datenbank ausliest.

# Refund Test
![image](https://github.com/user-attachments/assets/5de042cb-dce2-4d82-85da-f77bb4134b64)

Der Test CalculateRefundSupermarktLower überprüft, ob die Rückerstattungsfunktion korrekt arbeitet, wenn der Rechnungsbetrag geringer ist als der vom System vorgegebene Rückerstattungssatz für Supermarkt-Rechnungen.

Im Test wird die Methode refundCalculation mit folgenden Parametern aufgerufen: ein Rechnungsbetrag von 2,0, der Rechnungstyp SUPERMARKET sowie das Rechnungsdatum 1. Januar 1999.

Da der Rückerstattungssatz für Supermarkt-Rechnungen, der über die Methode getRefundForDate ermittelt wird, in der Regel höher als 2,0 ist, prüft der Test, ob die tatsächlich zurückerstattete Summe auf den tatsächlichen Rechnungsbetrag begrenzt wird. Das erwartete Ergebnis ist, dass die Methode den Wert 2,0 zurückgibt, also den vollen Rechnungsbetrag, da die Rückerstattung niemals den Rechnungsbetrag übersteigen darf. Der Test stellt somit sicher, dass die Rückerstattung nicht mehr als die Rechnungssumme betragen kann, selbst wenn der Erstattungssatz höher liegt.


# Export Test
![image](https://github.com/user-attachments/assets/00600aee-2fdb-4cda-b736-73a7a65677ab)

Der Test testExportIncludesInvoicesAndSum überprüft die Funktionalität des Exports, welcher die eingereichten Rechnungen für einen bestimmten Monat aus der Datenbank abruft und dabei eine Gesamtsumme der Rückerstattungen berechnet. 

Im Test wird zunächst die Methode getInvoicesForMonth für April 2025 aufgerufen, die eine Instanz von InvoicesTotal zurückliefert. Diese enthält eine Liste von Rechnungen für den angegebenen Monat sowie eine Gesamtsumme der Rückerstattungsbeträge. Der Test prüft zunächst ob die Liste der Rechnungen mindestens einen Eintrag enthält. Anschließend wird die Summe der Rückerstattungswerte aller Rechnungen in der Liste berechnet, indem alle refund-Werte aufsummiert werden. Zum Schluss wird überprüft, dass diese berechnete Summe mit dem in InvoicesTotal gespeicherten Gesamtbetrag (totalRefund) übereinstimmt. Dabei wird eine kleine Toleranz (delta) von 0,01 zugelassen, um Rundungsfehler zu berücksichtigen. Damit stellt der Test sicher, dass die Exportfunktion die Rechnungen vollständig und korrekt abruft und dass die aggregierte Rückerstattungssumme im Ergebnis korrekt berechnet und ausgewiesen wird.
