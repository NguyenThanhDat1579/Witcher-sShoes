package com.example.witchersshoes.classes;

import java.util.HashMap;
import java.util.Objects;

public class SanPham{
    public int masp;
    public String tensp;
    public int giaban;
    public int soluong;

    public SanPham(int masp, String tensp, int giaban, int soluong) {
        this.masp = masp;
        this.tensp = tensp;
        this.giaban = giaban;
        this.soluong = soluong;
    }

    public SanPham(String tensp, int giaban, int soluong) {
        this.tensp = tensp;
        this.giaban = giaban;
        this.soluong = soluong;
    }

    public HashMap<String, Object> convertHashMap(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("ma", masp);
        map.put("ten", tensp);
        map.put("gia",giaban);
        map.put("soluong", soluong);
        return map;

    }
}
