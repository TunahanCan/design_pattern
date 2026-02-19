package com.can.behavirol.state;

public class DocumentContext {
    private final String title;
    private String content;
    private String currentUserRole;
    private DocumentState state;

    public DocumentContext(String title, String content, String currentUserRole) {
        this.title = title;
        this.content = content;
        this.currentUserRole = currentUserRole;
        this.state = new DraftState();
    }

    public String publish() {
        return state.publish(this);
    }

    public String edit(String newContent) {
        return state.edit(this, newContent);
    }

    public void changeState(DocumentState newState) {
        this.state = newState;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public String getStateName() {
        return state.getName();
    }
}
