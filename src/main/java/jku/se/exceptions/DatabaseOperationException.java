package jku.se.exceptions;

// Custom exception for database-related errors
public class DatabaseOperationException extends RuntimeException {
  public DatabaseOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
