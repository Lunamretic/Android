package by.lunamretic.chat.entity;

public class Message {
    private long timestamp;

    public String uid;
    public String msg;
    public String author;
    public String id;

    public Message (String uid, String msg, long timestamp, String author, String id) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.author = author;
        this.msg = msg;
        this.id = id;
    }

}