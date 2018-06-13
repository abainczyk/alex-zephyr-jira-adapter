package de.alex.jirazapidemo.alex.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexProject {

    private Long id;

    private List<AlexProjectUrl> urls;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AlexProjectUrl> getUrls() {
        return urls;
    }

    public void setUrls(List<AlexProjectUrl> urls) {
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
