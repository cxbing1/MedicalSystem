package com.xbcheng.medicalsystem.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private Date createdDate;

    private Integer hasRead;

    private String conversationId;

    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId == null ? null : conversationId.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String toString(){
        JSONObject contentJsonObject = null;
        String result = new SimpleDateFormat("yyyy-MM-dd HH:mm EE").format(createdDate)+"\n";
        try{
            contentJsonObject = JSON.parseObject(content);
        }catch (Exception e){
            return result+content;
        }

        return result+contentJsonObject.getString("msg");
    }

}