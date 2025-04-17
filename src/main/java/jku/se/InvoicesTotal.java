package jku.se;

import java.util.List;

public class InvoicesTotal {
    private List<InvoiceExport> invoices;
    private double totalRefund;
    private double refundToPay;

    public InvoicesTotal(List<InvoiceExport> invoices, double totalRefund, double refundToPay) {
        this.invoices = invoices;
        this.totalRefund = totalRefund;
        this.refundToPay = refundToPay;
    }

    public List<InvoiceExport> getInvoices() {
        return invoices;
    }

    public double getTotalRefund() {
        return totalRefund;
    }

    public double getRefundToPay() {
        return refundToPay;
    }
}

