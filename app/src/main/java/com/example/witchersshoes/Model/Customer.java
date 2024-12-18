package com.example.witchersshoes.Model;

import java.util.HashMap;

public class Customer {
    private String id;
    private String email;
    private String username;
    private String password;
    private String phone;
    private String address;

    public Customer(String id, String email, String username, String password, String phone, String address) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    public Customer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }




    public HashMap<String, Object> convertHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("khachHangID", id);
        map.put("tenKhachHang", username);
        map.put("email", email);
        map.put("matKhau", password);
        map.put("soDienThoai",phone);
        map.put("diaChi",address);
        return map;
    }
}