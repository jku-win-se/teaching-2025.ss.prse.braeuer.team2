# Einleitung
Überblick über das Projekt.

# Umgesetzte Anforderungen
* Welche der Anforderungen haben Sie umgesetzt, wer aus dem Team war verantwortlich und welches Stundenausmaß ist damit verknüpft?
* Falls Anforderungen nicht umgesetzt wurden muss das pro Anforderung begründet werden.
* [Closed Issues](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team2/issues?q=is%3Aissue%20state%3Aclosed)

# Überblick über die Applikation aus Benutzersicht
* Wie wurden die Anforderungen in der Benutzeroberfläche umgesetzt (Screenshots der 
Benutzeroberfläche und Beschreibung der Funktionalität anhand von Szenarien)
* Wie wird die Applikation verwendet?
* [Benutzerdoku](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team2/blob/main/docs/benutzerdoku.md)

# Überblick über die Applikation aus Entwicklersicht
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
## Entwurf

### Überblick über die Applikation
* UML Diagramm mit Erläuterungen
* Verwendete Design Muster (z.B. Model-View-Controller)
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

### Wichtige Design Entscheidungen
#### Entscheidung 1: Einführung eines MVC-Architekturmusters

**Begründung:**  
Das MVC-Pattern trennt die Verantwortlichkeiten in Model, View und Controller. Dadurch bleibt der Code modular, leichter wartbar und testbar.

**Alternativen, die in Betracht gezogen wurden:**  
- Monolithische Struktur ohne klare Trennung  

**Annahmen:**  
- Das Projekt wird wachsen, und es werden viele Änderungen an der Benutzeroberfläche nötig sein.  
- Entwickler sind mit MVC vertraut.

**Konsequenzen:**  
- Höherer initialer Implementierungsaufwand.  
- Leichtere Wartung und Erweiterbarkeit des Systems.  
- Bessere Testbarkeit durch Trennung von Logik und Darstellung.

---

#### Entscheidung 2: Verwendung von JavaFX für die GUI

**Begründung:**  
JavaFX bietet eine moderne Oberfläche und ist gut integriert in das Java-Ökosystem. Es unterstützt FXML, was das UI-Design vereinfacht.

**Alternativen, die in Betracht gezogen wurden:**  
- Java Swing  

**Annahmen:**  
- Zielplattform ist der Desktop.  
- Kein großer Bedarf an Webkompatibilität.

**Konsequenzen:**  
- Moderne Benutzeroberfläche.  
- Zusätzliche Lernkurve bei FXML und SceneBuilder.  

---

#### Entscheidung 3: Verwendung einer relationalen Datenbank (SupaBase)

**Begründung:**  
Relationale Datenbanken sind den Studierenden nach Abschluss von Vorlesung \/ Übung Datenmodellierung bekannt. SupaBase bietet als Backend-as-a-Service eine einfache Anbindung, automatische Authentifizierung und Skalierbarkeit.

**Alternativen, die in Betracht gezogen wurden:**  
- Objektrelationale Datenbanken

**Annahmen:**  
- Datenbeziehungen sind komplex.  
- Skalierbarkeit und einfache Bereitstellung sind wichtige Kriterien.

**Konsequenzen:**  
- Einfachere Bereitstellung durch gehostete Infrastruktur.  
- Einschränkungen bei spezifischen Anpassungen im Vergleich zu selbst verwalteten Datenbanken.  
- Möglichkeit komplexer Abfragen und Integritätsprüfungen.

## Implementierung
Beschreibung wichtiger Aspekte der Implementierung (eventuell mit ausgewählten 
Codestücken), Projektstruktur, Abhängigkeiten, verwendete Bibliotheken.

## Code Qualität

Zur Sicherstellung der Codequalität wurde das Analysetool **PMD** eingesetzt. Die Analyse lieferte eine Vielzahl an Findings, die vor allem kleinere Verbesserungsmöglichkeiten aufzeigten. Die wichtigsten Kategorien der Beanstandungen waren:

**Verwendung von System.out.println:**  
PMD beanstandete die Nutzung von `System.out.println` für Debug-Ausgaben. Diese sollten durch ein Logging-Framework ersetzt werden, um eine bessere Protokollierung und Steuerung der Ausgaben zu ermöglichen.

**Nicht verwendete lokale Variablen:**  
Mehrere deklarierte, jedoch ungenutzte lokale Variablen wurden identifiziert. Diese deuten auf toten Code hin und wurden entfernt, um die Lesbarkeit und Wartbarkeit zu verbessern.

**Nicht verwendete Imports:**  
PMD meldete ungenutzte Import-Anweisungen. Diese überflüssigen Importe wurden entfernt, um die Übersichtlichkeit des Codes zu erhöhen.

**Fehlende final-Deklarationen:**  
Es wurde empfohlen, Variablen als `final` zu deklarieren, sofern sie nach der Initialisierung nicht mehr geändert werden. Dadurch wird die Unveränderlichkeit erhöht und potenzielle Fehlerquellen werden reduziert.

**Unnötige Klammern in if-Statements:**  
PMD wies auf vereinfachbare bedingte Ausdrücke hin. Unnötige Klammern wurden entfernt, um die Klarheit und Eleganz des Codes zu verbessern.

Alle genannten Findings wurden im Zuge der Qualitätssicherung behoben.

## Testen
Überblick über erstellte JUnit Tests (eventuell mit ausgewählten Tests), Testabdeckung
Beschreibung der Akzeptanztests für 3 ausgewählte Requirements.
* [Testplan](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team2/blob/main/docs/Testplan.md)

# JavaDoc für wichtige Klassen, Interfaces und Methoden
* [JavaDoc](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team2/blob/main/docs/Javadoc%20f%C3%BCr%20wichtigste%20Klassen.md)

# Installationsanleitung
Beschreibung wie man die Applikation installiert und startet.
1.) Lade Java Runtime Environment 21 oder höher herunter bzw. überprüfe ob es vorhanden ist 

2.) Lade die Lunchify .jar-Datei herunter.

3.) Starte das Programm mit einem Doppelklick.


Falls dies nicht funktionieren sollte kann die .jar_Datei auch so geöffnet werden.

1.) Lade zusätlich zu Java auch noch javafx-sdk herunter

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
