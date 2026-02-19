package com.can.behavirol.mediator;

public class Button extends Component {
    private final String name;

    public Button(Mediator mediator, String name) {
        super(mediator);
        this.name = name;
    }

    public void click() {
        mediator.notify(this, "click");
    }

    public String getName() {
        return name;
    }
}
