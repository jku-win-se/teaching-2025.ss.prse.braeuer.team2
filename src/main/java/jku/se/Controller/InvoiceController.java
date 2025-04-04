package jku.se.Controller;

public class InvoiceController {
    private String filePath;
    private String status; // Zum Beispiel: "hochgeladen", "fehlgeschlagen", etc.
    private static String message;

    private double amount;
    public InvoiceController(String filePath) {
        this.filePath = filePath;
        this.status = "neu";  // initialer Status
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Methode zum Abrufen des Betrags
    public double getAmount() {
        return amount;
    }
    public void setStatus(String status) {
        this.status = status;
        setMessageBasedOnStatus(); // Update der Nachricht basierend auf dem Status*/
    }

    public static String getMessage() {
        return message;
    }
    public String setMessageBasedOnStatus() {
        switch (this.status) {
            case "Die Rechnung wurde erfolgreich hochgeladen!":
                this.message = "Ihre Rechnung wurde hochgeladen! Rückerstattung: " + getAmount();
                break;
            default:
                this.message = "Es scheint als hätte es ein Problem mit Ihrer Rechnung gegeben!";
        }
        return this.message;
    }
}


