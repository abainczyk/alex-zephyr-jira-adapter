package de.alex.jirazapidemo.jira;

public class Execution {

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
        return "Execution{"
                + "id=" + id
                + ", status=" + status
                + ", comment='" + comment + '\''
                + '}';
    }

}
