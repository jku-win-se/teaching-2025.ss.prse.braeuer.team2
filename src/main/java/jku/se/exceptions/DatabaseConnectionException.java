package jku.se.exceptions;

public class DatabaseConnectionException extends RuntimeException {
  public DatabaseConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}