# 🥗 Lunchify – Systemdokumentation

## 📌 1. Projektkontext

Lunchify ist eine interne Softwarelösung zur automatisierten Rückvergütung von Essensausgaben für Mitarbeiter:innen eines Linzer Unternehmens. Ziel ist es, eine intuitive Plattform bereitzustellen, über die Essensrechnungen digital eingereicht, automatisch verarbeitet und zur Gehaltsabrechnung übergeben werden können.

Das Projekt wird **agil** entwickelt, um schnelles Feedback und iterative Verbesserungen zu ermöglichen. Die Anwendung basiert auf **Java** und verwendet **Tesseract OCR** zur automatischen Auslesung von Belegdaten.

**Zielgruppen:**
- **Benutzer:innen:** Mitarbeiter:innen, die Rechnungen einreichen
- **Administrator:innen:** Personalverrechner:innen, die Rückerstattungen prüfen und exportieren

## 🎯 2. Zielsetzung

- Automatisierte Rückvergütung von Essensausgaben
- Entlastung der Personalverrechnung
- Minimierung manueller Fehler
- Transparenz für Mitarbeiter:innen

## 🧱 3. Systemübersicht

**Technologien:**
- Java 17
- JavaFX oder Spring Boot (je nach Zielplattform)
- SQLite/PostgreSQL
- Maven
- Tesseract (OCR)

**Benutzerrollen:**

| Rolle           | Rechte |
|-----------------|--------|
| Benutzer:in     | Rechnung hochladen, Verlauf einsehen, Korrekturen vornehmen |
| Administrator:in| Rückerstattungen prüfen, exportieren, Benutzer verwalten, Anomalien erkennen |

## ✅ 4. Funktionale Anforderungen

### 4.1 Authentifizierung
- Login mit E-Mail + Passwort (BCrypt)
- Sitzungsverwaltung

### 4.2 Rechnungseinreichung
- Upload von JPEG, PNG oder PDF
- Optional: manuelle Klassifizierung & Betragseingabe
- Automatisierte Klassifizierung via OCR
- Speicherung in Datenbank

### 4.3 Rückerstattungslogik
- **Restaurant:** max. 3 € ab 3 € Rechnungsbetrag
- **Supermarkt:** max. 2,50 € ab 2,50 € Rechnungsbetrag
- Darunter: voller Betrag wird erstattet

### 4.4 Historie
- Übersicht über alle eingereichten Rechnungen
- Korrektur & Löschung bis Monatsende
- Diagramm: Restaurant vs. Supermarkt (z. B. Balkendiagramm)

### 4.5 Administratorfunktionen
- Dashboard mit KPIs
- Exportfunktionen: CSV, PDF, JSON/XML
- Benutzerverwaltung
- Rechnungssuche + Bearbeitung
- Anomalie-Erkennung

## ⚙️ 5. Nicht-funktionale Anforderungen

- Schnelle Ladezeiten (< 3 Sekunden)
- Sicherer Login & Upload
- Fehlerresilienz
- Intuitive UI
- Grundlegende Barrierefreiheit

## 🏗️ 6. Systemarchitektur

**Schichten:**
- UI (JavaFX / Web)
- Business-Logik
- Persistenz (JDBC / JPA)
- Services (OCR, Export)

**Externe Komponenten:**
- Tesseract OCR (lokal)
- Dateiablage für Uploads
- Exportformate: CSV, PDF, JSON/XML

## 🗃️ 7. Datenmodell (vereinfacht)

### `users`
- `id`
- `email`
- `password_hash`
- `role` (USER / ADMIN)

### `receipts`
- `id`
- `user_id`
- `date`
- `image_path`
- `amount_entered`
- `amount_detected`
- `category`
- `reimbursement_amount`
- `status`

## 🔍 8. Anomalie-Erkennung

**Beispiele:**
- OCR-Betrag weicht stark vom Eingabebetrag ab
- Viele Korrekturen (> 10 pro Monat)
- Ungewöhnlich viele Einreichungen (> 20)
- Häufige identische Beträge

## 🕓 9. Projektverlauf (agil)

| Sprint | Dauer      | Inhalt                              |
|--------|------------|-------------------------------------|
| 1      | Woche 1–2  | Authentifizierung, Userverwaltung   |
| 2      | Woche 3–4  | Upload, OCR, Rückerstattung         |
| 3      | Woche 5–6  | Historie, Korrekturen               |
| 4      | Woche 7–8  | Admin-Tools, Export, Anomalien      |
| 5      | Woche 9–10 | Testing, Doku, Finalisierung        |

## 💡 10. Erweiterungsideen

- Mobile App (Android)
- Benachrichtigung bei Genehmigung
- Deep-Learning-basierte Belegerkennung
- Integration in Slack oder MS Teams

---

> 📁 Dieses Dokument ist Teil des Projekts *Lunchify*. Weitere technische Dokumentation und Code findest du im `/src`-Ordner und im Wiki dieses Repositories.


