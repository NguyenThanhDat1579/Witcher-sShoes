package com.example.witchersshoes.Model;

import java.util.HashMap;

public class Customer {
    private String id;
    private String username;
    private String password;

    public Customer(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public HashMap<String, Object> convertHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("tenDangNhap", username);
        map.put("matKhau", password);
        return map;
    }
}
