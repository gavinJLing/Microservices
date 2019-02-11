package com.example.proxy.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple Proxy service response Object.
 * 
 * An object containing details of the 3rd party service response
 * 
 * 
 * @author gavinling
 *
 */
public class ProxyResponse {

    @JsonProperty
    private String body;

    @JsonProperty
    private List<Header> headers = new ArrayList<Header>();

    
    public ProxyResponse() {}
    
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    

    // A Request/Response may contain duplicate Headers so store as simple list of
    // k,v.s
    @JsonIgnore
    public void addHeader(String name, String value) {
        headers.add(new Header(name, value));
    }

    @JsonIgnore
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach(header -> sb.append(header.toString()).append("  ,"));
        sb.append(body);
        return sb.toString();
    }
}
