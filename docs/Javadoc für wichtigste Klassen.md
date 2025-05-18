# Klasse Database
Verantworklich für die Interaktion mit der Datenbank.

## Methodenübersicht 
static boolean deleteImage(String imageUrl) // Fotos aus der Datenbank löschen

static boolean deleteInvoice(Connection connection, String username, LocalDate date) // Rechnung aus Datenbank löschen

static Connection getConnection() // Verbindung zu Datenbank herstellen

static boolean invoiceExists(Connection connection, String username, LocalDate datum) // Prüft, ob der Benutzer für diesen Tag bereits eine Rechnung hochgeladen ha

static void invoiceScanUpload(String path, SubmitBillController controller) // OCR + Daten hochladen

static boolean updateInvoice(double betrag, Date datum, InvoiceType typ, String username, InvoiceStatus status, String image, double refund, int identifier) // Rechnung bearbeiten

static String uploadImage(File imageFile) // lädt das Bild/PDF der Rechnung in den Supabase-Speicher und generiert einen Link dazu

static void uploadInvoice(Connection connection, String username, double betrag, LocalDate datum, InvoiceType typ, InvoiceStatus status, File imageFile, Double refund, SubmitBillController controller) // Lädt die Rechnungsdaten in die Tabelle Rechnungen

static LocalDate getInvoiceDate(int identifier) // Rechnungsdatum erahlten

static String getInvoiceImage(int identifier) // Foto von Rechnung erhalten

static double getInvoiceRefund(int identifier) // get refund

static String getInvoiceStatus(int identifier) //get status of the Invoice

static String getInvoiceUsername(int identifier) // get username of the invoice

- # Abstract Klasse Controller
Dient als Vorlage für alle verwendeten Controller Klassen.

## Methodenübersicht 
static void showError(String title, String message) // Pop-Up Fehler 
 
static void showInfo(String title, String message) // Pop-Up Info 
 
static void showSuccess(String title, String message) // Pop-Up Erfolgreich 
 
protected void switchScene(javafx.event.ActionEvent event, String fxmlFile) // zwischen FXML-Dateien wechseln

### Bekannte direkte Unterklassen:
AddUserController, AdminPanelController, DashboardAdminController, DashboardUserController, EditInvoiceController, EditInvoiceUserController, ExportDataController, FilterPanelAdminController, FilterPanelUserController, LoginController, MessageAnomalyController, MessagesController, RefundController, RequestManagementController, StatisticsController, SubmitBillController, SubmittedBillsController, UserOverviewDashboardController, UserSearchController, UserSearchResultsController, UserTabularController


# Enums
## InvoiceStatus
Rechnungsstatus:
- ACCEPTED
- DENIED
- PENDING

## InvoiceType
Rechnungsart: 
- RESTAURANT
- SUPERMARKET
- UNDEFINED
 
## Role
Rolle des Benutzeraccounts:
- ADMIN
- USER

## Status
Status des Benutzeraccounts:
- ACTIVE
- BLOCKED
