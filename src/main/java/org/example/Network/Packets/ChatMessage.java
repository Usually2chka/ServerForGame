package org.example.Network.Packets;

// Класс для сообщений (должен быть static для внутреннего класса)
public class ChatMessage {
    public String text;
    //public String sender;

    // Обязателен пустой конструктор для Kryo
    public ChatMessage() {}

    public ChatMessage(String sender, String text) {
        //this.sender = sender;
        this.text = text;
    }
}
