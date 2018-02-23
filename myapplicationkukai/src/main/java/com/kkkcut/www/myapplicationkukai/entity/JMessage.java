package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2017/1/12.
 */

public class JMessage {
    private String title;
    private String introduce;
    private int state;
    private int type;
    private String content;
    private String time;
    private String id;
    public JMessage(String id, String title, String introduce, int state, int type, String content, String time) {
        this.id=id;
        this.title = title;
        this.introduce = introduce;
        this.state = state;
        this.type = type;
        this.content = content;
        this.time = time;
    }


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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int tyep) {
        this.type = tyep;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
