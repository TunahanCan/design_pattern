package com.can.behavirol.mediator;

public class Checkbox extends Component {
    private boolean checked;

    public Checkbox(Mediator mediator) {
        super(mediator);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        mediator.notify(this, "check");
    }

    public boolean isChecked() {
        return checked;
    }
}
