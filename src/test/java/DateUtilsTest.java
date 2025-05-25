import jku.se.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {

    @Test
    void testFormatToDateAndTime() {
        // Arrange: Erstelle einen bekannten Timestamp
        LocalDateTime localDateTime = LocalDateTime.of(2024, 5, 25, 14, 30, 15);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        // Act: Verwende die Methode
        String result = DateUtils.formatToDateAndTime(timestamp);

        // Assert: Überprüfe das erwartete Ergebnis
        assertEquals("25-05-2024 14:30:15", result);
    }

    @Test
    void testFormatIsNotIncorrect() {
        // Arrange
        LocalDateTime localDateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        // Act
        String result = DateUtils.formatToDateAndTime(timestamp);

        // Assert: Stelle sicher, dass der String NICHT falsch formatiert ist
        Assertions.assertNotEquals("2024-12-31 23:59:59", result); // falsches Format (ISO statt deutsches)
    }
}
