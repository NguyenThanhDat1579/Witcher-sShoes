package com.example.witchersshoes.Model;

import java.util.HashMap;

public class ToDo {
    private String id;
    private String title;
    private String content;

    // Constructor không tham số (yêu cầu để Firestore có thể deserialize dữ liệu)
    public ToDo() {
    }

    // Constructor có tham số
    public ToDo(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Các getter và setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Phương thức để chuyển đối tượng thành HashMap nếu cần
    public HashMap<String, Object> convertHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("content", content);
        return map;
    }
}
