# Klasse Controller
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
