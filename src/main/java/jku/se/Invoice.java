package jku.se;

import java.time.LocalDate;

public class Invoice {

    private LocalDate date;
    private double sum;
    private InvoiceType typ;
    private InvoiceStatus status;

    // Konstruktor zum Erstellen einer neuen Invoice
    public Invoice(LocalDate date, double sum, InvoiceType typ, InvoiceStatus status) {
        this.date = date;
        this.sum = sum;
        this.typ = typ;
        this.status = status;
    }

    // Getter and Setter
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

    //A method to output the invoice information as a string
    @Override
    public String toString() {
        return "Rechnung [Datum=" + date + ", Summe=" + sum + " EUR, Typ=" + typ + "]";
    }
}