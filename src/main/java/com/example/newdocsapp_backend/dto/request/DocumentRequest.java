package com.example.newdocsapp_backend.dto.request;

import com.example.newdocsapp_backend.models.User;

public class DocumentRequest {
    private String title;
    private User owner;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
