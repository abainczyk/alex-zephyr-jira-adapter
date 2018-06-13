package de.alex.jirazapidemo.jira.entities;

public class FolderExecutionConfig extends CycleExecutionConfig {
    private Long folderId;

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }
}
