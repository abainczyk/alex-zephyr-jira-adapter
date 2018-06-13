package de.alex.jirazapidemo.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResult {

    public static final int PASSED = 1;

    public static final int FAILED = 2;

    private Long id;

    private int status;

    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "StepResult{"
                + "id=" + id
                + ", status=" + status
                + ", comment='" + comment + '\''
                + '}';
    }

}
