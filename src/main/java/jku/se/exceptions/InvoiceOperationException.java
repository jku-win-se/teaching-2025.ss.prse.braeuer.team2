package jku.se.exceptions;

// Custom exception for invoice-related operations
public class InvoiceOperationException extends RuntimeException {
    public InvoiceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
