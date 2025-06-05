# Einleitung
Lunchify ist eine interne Softwarelösung zur automatisierten Rückvergütung von Essensausgaben für Mitarbeiter:innen. Ziel ist es, eine intuitive Plattform bereitzustellen, über die Essensrechnungen digital eingereicht, automatisch verarbeitet und zur Gehaltsabrechnung übergeben werden können. Das Projekt wird **agil** entwickelt, um schnelles Feedback und iterative Verbesserungen zu ermöglichen. Die Anwendung basiert auf **Java** und verwendet **Tesseract OCR** zur automatischen Auslesung von Belegdaten.

# Umgesetzte Anforderungen
* [Umgestzte Anforderungen](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team2/issues?q=is%3Aissue%20state%3Aclosed)

# Überblick über die Applikation aus Benutzersicht
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
Die Implementierung des Projekts folgt einem klaren und modularen Aufbau. Die wichtigsten Komponenten umfassen eine zentrale Datenbank-Interaktionsklasse, eine Login-Management-Klasse, eine abstrakte Controller-Basis sowie mehrere Enums zur Typisierung und Statusverwaltung.

---

### 1. **Klasse `Database`**
**Verantwortung:**  
Die Klasse `Database` bündelt alle Datenbankoperationen sowie Bild-Uploads über Supabase. Sie enthält ausschließlich `static` Methoden, wodurch ein einfacher Zugriff ohne Instanziierung möglich ist.

**Wichtige Methoden:**
```java
static Connection getConnection();
static void uploadInvoice(Connection connection, String username, double betrag, LocalDate datum, InvoiceType typ, InvoiceStatus status, File imageFile, Double refund, SubmitBillController controller);
static boolean updateInvoice(double betrag, Date datum, InvoiceType typ, String username, InvoiceStatus status, String image, double refund, int identifier);
static boolean deleteInvoice(Connection connection, String username, LocalDate date);
static boolean deleteImage(String imageUrl);
static String uploadImage(File imageFile);
static boolean invoiceExists(Connection connection, String username, LocalDate datum);
static void invoiceScanUpload(String path, SubmitBillController controller);
// Getter-Methoden für Rechnungsdaten
static LocalDate getInvoiceDate(int identifier);
static String getInvoiceImage(int identifier);
static double getInvoiceRefund(int identifier);
static String getInvoiceStatus(int identifier);
static String getInvoiceUsername(int identifier);
```

**Technologien/Bibliotheken:**
- JDBC für Datenbankzugriffe
- Supabase API/SDK für Datei-Uploads
- OCR-Bibliothek (z. B. Tesseract) für Texterkennung

---

### 2. **Klasse `Login`**
**Verantwortung:**  
Zentrale Verwaltung der Authentifizierung und Sitzungsverwaltung.

**Wichtige Methoden:**
```java
static boolean validateLogin(String email, String password, StringBuilder userRole, StringBuilder accountStatus);
static void logout();
static String getCurrentUserEmail();
static String getCurrentUsername();
static Role getCurrentUserRole();
static Status getCurrentUserStatus();
static int getMaxFailedAttempts();
static String getUsername();
```

---

### 3. **Abstrakte Klasse `Controller`**
**Verantwortung:**  
Stellt eine Basisklasse für alle GUI-Controller zur Verfügung und ermöglicht einheitliches Fehler- und Erfolgsmanagement sowie Szenenwechsel.

**Wichtige Methoden:**
```java
static void showError(String title, String message);
static void showInfo(String title, String message);
static void showSuccess(String title, String message);
protected void switchScene(ActionEvent event, String fxmlFile);
```

**Bekannte direkte Unterklassen:**
AddUserController, AdminPanelController, DashboardAdminController, DashboardUserController, EditInvoiceController, EditInvoiceUserController, ExportDataController, FilterPanelAdminController, FilterPanelUserController, LoginController, MessageAnomalyController, MessagesController, RefundController, RequestManagementController, StatisticsController, SubmitBillController, SubmittedBillsController, UserOverviewDashboardController, UserSearchController, UserSearchResultsController, UserTabularController

---

### 4. **Enums**
Definieren zentrale Status und Typen:
```java
enum InvoiceStatus { ACCEPTED, DENIED, PENDING }
enum InvoiceType { RESTAURANT, SUPERMARKET, UNDEFINED }
enum Role { ADMIN, USER }
enum Status { ACTIVE, BLOCKED }
```

---


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

* Testabdeckung

![image](https://github.com/user-attachments/assets/296340d5-9e91-43d4-8f91-2b7af89c9bce)

Die Testabdeckung in den Controller-Klassen ist gering, da diese die Backend-Klassen verwenden. 
Daher werden hauptsächlich die Backend-Klassen getestet.




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
