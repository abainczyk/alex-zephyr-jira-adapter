package de.alex.jirazapidemo.alex.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexParameterValue {

    private Long id;

    private String value;

    private AlexParameter parameter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AlexParameter getParameter() {
        return parameter;
    }

    public void setParameter(AlexParameter parameter) {
        this.parameter = parameter;
    }
}
