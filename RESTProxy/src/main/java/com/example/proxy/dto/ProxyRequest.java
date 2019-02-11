package com.example.proxy.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple Proxy service request Object.
 * 
 * A logical request object containing details of the request to be relayed onto the remote 3rd party service.
 * 
 * 
 * @author gavinling
 *
 */
public class ProxyRequest {
    // Mandatory: logical proxy service name.  Resolves to persistent service profile.
    private String name;
    
    // Optional: request object properties
//    private ArrayList<header> headers = new ArrayList<>();
//    private ArrayList<Queryparam> queryParams = new ArrayList<>();
//    private String payload;

    public ProxyRequest() {
    }
    
    
 
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public boolean isValid() {
        return StringUtils.isBlank(name);
    }



    public void addQueryParam(String paramName, String value) {
        // TODO Auto-generated method stub
        
    }



    public void addHeader(String headerName, String value) {
        // TODO Auto-generated method stub
        
    }
}
