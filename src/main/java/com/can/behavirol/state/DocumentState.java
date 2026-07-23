package com.can.behavirol.state;

public interface DocumentState {
    String publish(DocumentContext context);
    String edit(DocumentContext context, String newContent);

    default String reject(DocumentContext context, String reason) {
        return "Bu durumdaki doküman reddedilemez.";
    }

    String getName();
}
