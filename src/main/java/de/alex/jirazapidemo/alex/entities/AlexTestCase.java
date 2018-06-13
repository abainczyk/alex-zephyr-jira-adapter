package de.alex.jirazapidemo.alex.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlexTestCase {

    private String type = "case";

    private Long id;

    private String name;

    private Long project;

    private Long parent;

    private List<AlexTestCaseStep> steps;

    public AlexTestCase() {
        this.steps = new ArrayList<>();
    }

    public AlexTestCase(String name, Long project, Long parent) {
        this();
        this.name = name;
        this.project = project;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<AlexTestCaseStep> getSteps() {
        return steps;
    }

    public void setSteps(List<AlexTestCaseStep> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "AlexTestCase{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", project=" + project +
                ", parent=" + parent +
                ", steps=" + steps +
                '}';
    }

}
