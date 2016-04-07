package com.example.seasidelee.sportsclock;

/**
* Created by Seaside Lee on 2015/12/19.
        */
public class Songs {
    private String title;
    private Long id;

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setUrl(Long url){
        this.id = url;
    }

    public Long getUrl(){
        return id;
    }
}
