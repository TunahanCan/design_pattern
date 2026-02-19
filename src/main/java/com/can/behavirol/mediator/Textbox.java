package com.can.behavirol.mediator;

public class Textbox extends Component {
    private String text = "";
    private boolean visible = true;

    public Textbox(Mediator mediator) {
        super(mediator);
    }

    public void enterText(String text) {
        this.text = text;
        mediator.notify(this, "input");
    }

    public String getText() {
        return text;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
