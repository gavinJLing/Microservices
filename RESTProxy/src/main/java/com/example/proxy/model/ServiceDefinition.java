package com.example.proxy.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.proxy.dto.Header;

@Document(collection="services")
public class ServiceDefinition {
    @Id
    private String id;
    
    private String name;
    private String description ="";
    private String url;
    private String method;
    private List<Header> staticHeaders = new ArrayList<>();
    private int timeoutSeconds = 20;    // Default 20 second remote srive synch. timeout (onRead) if not declared in Store 
    
    public ServiceDefinition() {}


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    
    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public List<Header> getStaticHeaders() {
        return staticHeaders;
    }


    public void setStaticHeaders(List<Header> staticHeaders) {
        this.staticHeaders = staticHeaders;
    }


    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }


    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    
    
    
}
