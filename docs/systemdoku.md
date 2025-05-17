# Lunchify – Systemdokumentation

## 1. Projektkontext

Lunchify ist eine interne Softwarelösung zur automatisierten Rückvergütung von Essensausgaben für Mitarbeiter:innen. Ziel ist es, eine intuitive Plattform bereitzustellen, über die Essensrechnungen digital eingereicht, automatisch verarbeitet und zur Gehaltsabrechnung übergeben werden können.

Das Projekt wird **agil** entwickelt, um schnelles Feedback und iterative Verbesserungen zu ermöglichen. Die Anwendung basiert auf **Java** und verwendet **Tesseract OCR** zur automatischen Auslesung von Belegdaten.

**Zielgruppen:**
- **Benutzer:innen:** Mitarbeiter:innen, die Rechnungen einreichen
- **Administrator:innen:** Personalverrechner:innen, die Rückerstattungen prüfen und exportieren

## 2. Zielsetzung

- Automatisierte Rückvergütung von Essensausgaben
- Entlastung der Personalverrechnung
- Minimierung manueller Fehler
- Transparenz für Mitarbeiter:innen

## 3. Systemübersicht

**Technologien:**

| Komponente           | Technologie |
|-----------------|--------|
| GUI             | JavaFX, FXML, |
| Datenbank       | PostgresSQL (Supabase) |
| Build-Tool |Maven |
| OCR-Erkennung |Tesseract |
| Tests | JUnit |

**Projekt-Strutur:**

```
src/
├── main/
│   ├── java/
│   │   └── at/jku/se/            → Unterordner + Business-Logik-Klassen
│   │       ├── controller        → GUI-Controller für FXML-Views
│   │       ├── exceptions          
│   └── resources/                → Unterordner + FXML-Datein
│       └── Tesseract-OCR         → OCR Tesseract
└── test/
    ├── java                      → Testklassen
    ├── ressources                → Foto einer Testrechnung
    
```

**Benutzerrollen:**

| Rolle           | Rechte |
|-----------------|--------|
| Benutzer:in     | Rechnung hochladen, Verlauf einsehen, Korrekturen vornehmen |
| Administrator:in| Rückerstattungen prüfen, exportieren, Benutzer verwalten, Anomalien erkennen |

## 4. Funktionale Anforderungen

### 4.1 Authentifizierung
- Login mit E-Mail + Passwort
- Sitzungsverwaltung

### 4.2 Rechnungseinreichung
- Upload von JPEG, PNG oder PDF
- Automatisierte Klassifizierung via OCR
- falls OCR nicht funktioniert: manuelle Klassifizierung & Betragseingabe
- Speicherung in Datenbank

### 4.3 Rückerstattungslogik
- voller Erstattungsbetrag: Rechnungsbetrag >= Erstattung
- nur Rechnungsbetrag als Erstattung: Rechnungsbetrag < Erstattung

### 4.4 Historie
- Übersicht über alle eingereichten Rechnungen
- Korrektur & Löschung bis Monatsende (für aktuelle Monat)

### 4.5 Administratorfunktionen
- Dashboard mit KPIs (Diagramme)
- Exportfunktionen: CSV, PDF, JSON
- Benutzerverwaltung
- Rechnungssuche + Bearbeitung
- Anomalie-Erkennung

## ⚙️ 5. Nicht-funktionale Anforderungen
- Schnelle Ladezeiten (< 3 Sekunden)
- Sicherer Login & Upload
- Fehlerresilienz
- Intuitive UI

##  6. Systemarchitektur

```
┌────────────────────────────────────────┐
│               Benutzer                 │
│           (GUI in JavaFX)              │
└────────────────────────┬───────────────┘
                         │
                         ▼
┌────────────────────────────────────────┐
│         Präsentationsschicht           │
│  → JavaFX Controller-Klassen           │
│  → FXML-Views (UI)                     │
└────────────────────────┬───────────────┘
                         │
                         ▼
┌────────────────────────────────────────┐
│            Business-Logik              │
│  → Service-Klassen (z. B. InvoiceScan) │
└────────────────────────┬───────────────┘
                         │
                         ▼
┌────────────────────────────────────────┐
│         Datenzugriffsschicht           │
│  → JDBC               │
│  → Verbindung zu PostgreSQL            │
└────────────────────────┬───────────────┘
                         │
                         ▼
┌────────────────────────────────────────┐
│         PostgreSQL-Datenbank           │
│  → Tabellen:                           |
|   Account, Rechnungen, Message, Refund │
└────────────────────────────────────────┘
```

**Externe Komponenten:**
- Tesseract OCR (lokal)
- Exportformate: CSV, PDF, JSON

##  7. Datenmodell (vereinfacht)

### `accounts`
- `username`
- `createdAt`
- `first_name`
- `last_name`
- `role` (USER / ADMIN)
- `email`
- `password`
- `failed_attempts`
- `status` (ACTIVE / BLOCKED)

### `rechnungen`
- `id`
- `betrag`
- `datum`
- `typ` (SUPERMARKET / RESTAURANT)
- `username`
- `status` (PENDING / ACCEPTED, DENIED)
- `image`
- `refund`

### `message`
- `id`
- `rechnung_id`
- `username`
- `created_at`
- `new_message`

### `refunds`
- `change_date`
- `restaurant`
- `supermarket`
- `admin`


##  8. Anomalie-Erkennung

**Beispiele:**
- OCR-Betrag weicht vom Eingabebetrag ab
- Viele Korrekturen (> 10 pro Monat)
- Ungewöhnlich viele Einreichungen (> 20)
- Häufige identische Beträge

##  9. Projektverlauf (agil)

| Sprint | Dauer      | Inhalt                              |
|--------|------------|-------------------------------------|
| 1      |  21.03-11.04 | Authentifizierung,  Upload, OCR, UI  |
| 2      |  12.04-02.05 | Userverwaltung, Rückerstattung, Statistics, Export |
| 3      |  03.05-23.05 | Anomalie-Erkennung               |


## 10. Erweiterungsideen

- Mobile App (Android / IOS)
- Deep-Learning-basierte Belegerkennung
- Integration in Slack oder MS Teams

## 11. Links
[Supabase](https://supabase.com)

[Tesseract-OCR](https://github.com/tesseract-ocr/tesseract)

---

> 📁 Dieses Dokument ist Teil des Projekts *Lunchify*. Weitere technische Dokumentation und Code findest du im `/src`-Ordner.


