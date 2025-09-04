package pltop6.java;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.effect.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.*;

public class ToggleSwitch extends Parent {

    Callback<Void, Void> cb;

    private boolean finished = true;
    private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));

    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    private double height, width;

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }

    public ToggleSwitch(int height) {
        this.height = (double) height;
        width = 2.0 * height;
        construct();
    }

    public ToggleSwitch(int height, int width) {
        this.height = height;
        this.width = (double) width;
        construct();
    }

    public ToggleSwitch(double height) {
        this.height = height;
        width = 2 * height;
        construct();
    }

    public ToggleSwitch(double height, double width) {
        this.height = height;
        this.width = width;
        construct();
    }

    public ToggleSwitch() {
        height = 8;
        width = 16;
        construct();
    }

    private void construct() {
        Rectangle background = new Rectangle(width, height);
        background.setArcWidth(height);
        background.setArcHeight(height);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        Circle trigger = new Circle(height / 2);
        trigger.setCenterX(height / 2);
        trigger.setCenterY(height / 2);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.LIGHTGRAY);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(1);
        trigger.setEffect(shadow);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        getChildren().addAll(background, trigger);
        switchedOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState.booleanValue();
            if (cb != null) cb.call(null);
            translateAnimation.setToX(isOn ? width - height : 0);
            fillAnimation.setFromValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
            fillAnimation.setToValue(isOn ? Color.LIGHTGREEN : Color.WHITE);

            animation.play();
            animation.setOnFinished(ev -> {
                finished = true;
            });
        });
        setOnMouseClicked(event -> {
            switchedOn.set(!switchedOn.get());
        });
    }

    public void switchOff() {
        switchedOn.set(false);
    }

    public void switchOn() {
        switchedOn.set(true);
    }

    public boolean isOn() {
        return switchedOn.get();
    }

    public boolean isFinished() {
        return finished;
    }

    public void setActive() {
        finished = false;
    }

    public void setOnAction(Callback<Void, Void> cb) {
        this.cb = cb;
    }

}
