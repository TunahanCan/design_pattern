package com.can.behavirol.mediator;

public class Label extends Component {
    private String text;

    public Label(Mediator mediator, String text) {
        super(mediator);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
