package com.example.newdocsapp_backend.dto.request;

import com.example.newdocsapp_backend.models.User;

import java.util.UUID;

public class DocumentRequest {
    private String title;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
