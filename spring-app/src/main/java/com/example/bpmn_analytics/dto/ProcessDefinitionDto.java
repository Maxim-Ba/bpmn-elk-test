package com.example.bpmn_analytics.dto;

public class ProcessDefinitionDto {
    private String id;
    private String key;
    private String name;
    private int version;
    private String resource;
    
    // конструктор, геттеры, сеттеры
    public ProcessDefinitionDto() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
}
