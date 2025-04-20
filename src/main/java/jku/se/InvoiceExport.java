package jku.se;

import java.time.LocalDate;

public class InvoiceExport {

    private LocalDate date;
    private double sum;
    private InvoiceType typ;
    private InvoiceStatus status;
    private double refund;
    private int id;
    private String user;

    // Konstruktor zum Erstellen einer neuen Invoice
    public InvoiceExport(LocalDate date, double sum, InvoiceType typ, InvoiceStatus status, double refund, int id, String user) {
        this.date = date;
        this.sum = sum;
        this.typ = typ;
        this.status = status;
        this.refund = refund;
        this.id = id;
        this.user = user;
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

    public void setRefund(double refund) {
        this.refund = refund;
    }

    public double getRefund() {
        return refund;
    }

    public int getId(){return id;}
    public void setId(int id){this.id = id;}
    public String getUser() {return user;}
    public void setUser(String user){this.user = user;}

    //A method to output the invoice information as a string
    @Override
    public String toString() {
        return "Rechnung [Datum=" + date + ", Summe=" + sum + " EUR, Typ=" + typ + "]";
    }
}