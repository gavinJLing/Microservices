package com.example.proxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Header {

    @JsonProperty
    private String name;

    @JsonProperty
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Header() {};
    
    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return String.format("%s: %s", name, value);
    }

}
