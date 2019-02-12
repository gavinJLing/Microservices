package com.example.proxy.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple Proxy service request Object.
 * 
 * A logical request object containing details of the request to be relayed onto
 * the remote 3rd party service.
 * 
 * 
 * @author gavinling
 *
 */
public class ProxyRequest {
    // Mandatory: logical proxy service name. Resolves to persistent service
    // profile.
    @JsonProperty
    private String name;
    @JsonProperty
    private String method;

    // Optional: request object properties
    @JsonProperty
    private List<Header> headers = new ArrayList<>();
    
    @JsonProperty
    private Map<String, String> qparam = new HashMap<>();
    
    @JsonProperty
    private String payload;

    
    
    
    
    
    public ProxyRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public Map<String, String> getQparam() {
        return qparam;
    }

    public void setQparam(Map<String, String> qparam) {
        this.qparam = qparam;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * Is the Proxy request valid. The proxy request logical service 'name' is seen
     * as mandatory.
     * 
     * other aspect of the request may also be checker for validity e.g. query parma
     * Name / value pairs
     * 
     * @return boolean
     */
    @JsonIgnore
    public boolean isValid() {
        // simply enforce that service name is mandatory.
        return StringUtils.isNotBlank(name);
    }

    @JsonIgnore
    public void addQueryParam(String paramName, String value) {
        // TODO Auto-generated method stub

    }

    @JsonIgnore
    public void addHeader(String headerName, String value) {
        // TODO Auto-generated method stub

    }

    /**
     * Make the code easier to read. Simply invert the isValid method.
     * 
     * @return boolean
     */
    @JsonIgnore
    public boolean isNotValid() {
        return !isValid();
    }
}
