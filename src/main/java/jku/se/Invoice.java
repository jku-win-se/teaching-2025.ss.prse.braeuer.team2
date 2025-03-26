package jku.se;

import java.time.LocalDate;

public class Invoice {
    // Eigenschaften der Klasse
    private LocalDate date;  // Datum der Rechnung (z.B. "2025-03-17")
    private double sum;  // Gesamtsumme der Rechnung
    private InvoiceType typ;    // Typ der Rechnung (z.B. "Einkauf", "Dienstleistung")
    private InvoiceStatus status;

    // Konstruktor zum Erstellen einer neuen Invoice
    public Invoice(LocalDate date, double sum, InvoiceType typ, InvoiceStatus status) {
        this.date = date;
        this.sum = sum;
        this.typ = typ;
        this.status = status;
    }

    // Getter und Setter für die Eigenschaften
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public InvoiceType getTyp() {
        return typ;
    }

    public void setTyp(InvoiceType typ) {
        this.typ = typ;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    public InvoiceStatus getStatus() {
        return status;
    }

    // Eine Methode zum Ausgeben der Rechnungsinformationen als String
    @Override
    public String toString() {
        return "Rechnung [Datum=" + date + ", Summe=" + sum + " EUR, Typ=" + typ + "]";
    }
}