package jku.se;

import java.util.ArrayList;
import java.util.List;

public class MessageStore {

    private static List<String> messages = new ArrayList<>();

    // Methode, um Nachrichten hinzuzufügen
    public static void addMessage(String message) {
        messages.add(message);
    }

    // Methode, um alle gespeicherten Nachrichten abzurufen
    public static List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    // Methode zum Zurücksetzen der Nachrichten
    public static void clearMessages() {
        messages.clear();
    }
}

