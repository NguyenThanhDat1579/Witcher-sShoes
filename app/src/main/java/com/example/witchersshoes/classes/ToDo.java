package com.example.witchersshoes.classes;

import java.util.HashMap;

public class ToDo {
    public String id, title, content;
    public ToDo (String id , String title, String content){
        this.id=id;
        this.title=title;
        this.content=content;
    }
    public HashMap<String, Object> convertHashMap(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("id",id);
        map.put("title",title);
        map.put("content",content);
        return map;

    }
}
