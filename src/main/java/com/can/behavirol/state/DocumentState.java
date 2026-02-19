package com.can.behavirol.state;

public interface DocumentState {
    String publish(DocumentContext context);
    String edit(DocumentContext context, String newContent);
    String getName();
}
