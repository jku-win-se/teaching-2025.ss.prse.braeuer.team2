package jku.se;

import java.util.List;

public class InvoicesTotal {
    private List<InvoiceExport> invoices;
    private double totalRefund;

    public InvoicesTotal(List<InvoiceExport> invoices, double totalRefund) {
        this.invoices = invoices;
        this.totalRefund = totalRefund;
    }

    public List<InvoiceExport> getInvoices() {
        return invoices;
    }

    public double getTotalRefund() {
        return totalRefund;
    }
}

