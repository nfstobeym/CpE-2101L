package labex1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AirFryerSystem extends Application {

    String[] choices = {"ON", "OFF", "TIMER", "TEMP", "FRY"};

    int choiceIndex = 0;
    int setTime = 1;
    int setTemperature = 80;
    int remaining = 0;
    int heaterTemperature = 25;

    boolean cordIn = false;
    boolean basketIn = false;
    boolean turnedOn = false;
    boolean timerReady = false;
    boolean temperatureReady = false;
    boolean adjusting = false;
    boolean running = false;
    boolean flashBlue = false;

    Label modeText = new Label();
    Label valueText = new Label();
    Label fanText = new Label("Fan: OFF");
    Label heaterText = new Label("Heater: OFF");

    VBox screenPanel = new VBox();

    Timeline cookingClock;
    Timeline warningClock;

    @Override
    public void start(Stage stage) {

        Label heading = new Label("AIR FRYER");
        heading.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        buildScreen();

        Button previousButton = new Button("-");
        Button selectButton = new Button("PWR/SEL");
        Button nextButton = new Button("+");

        previousButton.setPrefSize(80, 50);
        selectButton.setPrefSize(100, 50);
        nextButton.setPrefSize(80, 50);

        ToggleButton cordButton = new ToggleButton("Cord: OUT");
        ToggleButton basketButton = new ToggleButton("Basket: OUT");

        cordButton.setPrefWidth(150);
        basketButton.setPrefWidth(150);

        previousButton.setOnAction(e -> controlPressed(-1));
        nextButton.setOnAction(e -> controlPressed(1));
        selectButton.setOnAction(e -> selectCurrent());

        cordButton.setOnAction(e -> {

            cordIn = cordButton.isSelected();

            if (cordIn) {
                cordButton.setText("Cord: IN");
            } else {
                cordButton.setText("Cord: OUT");
                stopIfNeeded();
            }
        });

        basketButton.setOnAction(e -> {

            basketIn = basketButton.isSelected();

            if (basketIn) {
                basketButton.setText("Basket: IN");
            } else {
                basketButton.setText("Basket: OUT");
                stopIfNeeded();
            }
        });

        HBox mainControls = new HBox(
                10,
                previousButton,
                selectButton,
                nextButton
        );

        mainControls.setAlignment(Pos.CENTER);

        HBox connectionControls = new HBox(
                10,
                cordButton,
                basketButton
        );

        connectionControls.setAlignment(Pos.CENTER);

        HBox machineInfo = new HBox(
                30,
                fanText,
                heaterText
        );

        machineInfo.setAlignment(Pos.CENTER);

        VBox root = new VBox(
                20,
                heading,
                screenPanel,
                mainControls,
                connectionControls,
                machineInfo
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 450, 400);

        stage.setTitle("Air Fryer Simulation");
        stage.setScene(scene);
        stage.show();

        refreshScreen();
    }

    public void buildScreen() {

        modeText.setMaxWidth(Double.MAX_VALUE);
        valueText.setMaxWidth(Double.MAX_VALUE);

        modeText.setAlignment(Pos.CENTER_LEFT);
        valueText.setAlignment(Pos.CENTER);

        modeText.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        valueText.setStyle(
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;"
        );

        screenPanel.setPrefSize(300, 100);
        screenPanel.setPadding(new Insets(10));

        screenPanel.getChildren().addAll(
                modeText,
                valueText
        );

        changeScreenColor("yellow");
    }

    public void controlPressed(int direction) {

        if (running) {
            moveThroughMenu(direction);
            return;
        }

        if (adjusting) {
            updateSetting(direction);
        } else {
            moveThroughMenu(direction);
        }
    }

    public void moveThroughMenu(int direction) {

        choiceIndex = choiceIndex + direction;

        if (choiceIndex > 4) {
            choiceIndex = 0;
        }

        if (choiceIndex < 0) {
            choiceIndex = 4;
        }

        if (choices[choiceIndex].equals("FRY")) {

            if (!readyForCooking()) {
                moveThroughMenu(direction);
                return;
            }
        }

        refreshScreen();
    }

    public void refreshScreen() {

        String selected = choices[choiceIndex];

        modeText.setText(selected);

        if (running) {
            valueText.setText(remaining + " sec");
            return;
        }

        switch (selected) {

            case "ON":
                valueText.setText(turnedOn ? "ON" : "OFF");
                break;

            case "OFF":
                valueText.setText("OFF");
                break;

            case "TIMER":
                valueText.setText(setTime + " sec");
                break;

            case "TEMP":
                valueText.setText(setTemperature + " °C");
                break;

            case "FRY":
                valueText.setText("FRY");
                break;
        }
    }

    public void selectCurrent() {

        String selected = choices[choiceIndex];

        if (running) {

            if (selected.equals("OFF")) {
                turnMachineOff();
            }

            return;
        }

        if (adjusting) {
            saveAdjustment();
            return;
        }

        switch (selected) {

            case "ON":
                tryTurningOn();
                break;

            case "OFF":
                turnMachineOff();
                break;

            case "TIMER":
                openTimerSetting();
                break;

            case "TEMP":
                openTemperatureSetting();
                break;

            case "FRY":
                beginFrying();
                break;
        }
    }

    public void tryTurningOn() {

        if (!cordIn || !basketIn) {
            valueText.setText("CHECK CORD/BASKET");
            return;
        }

        turnedOn = true;

        modeText.setText("ON");
        valueText.setText("ON");

        changeScreenColor("red");
    }

    public void openTimerSetting() {

        if (!turnedOn) {
            return;
        }

        adjusting = true;

        modeText.setText("TIMER");
        valueText.setText(setTime + " sec");
    }

    public void openTemperatureSetting() {

        if (!turnedOn) {
            return;
        }

        adjusting = true;

        modeText.setText("TEMP");
        valueText.setText(setTemperature + " °C");
    }

    public void updateSetting(int direction) {

        String selected = choices[choiceIndex];

        if (selected.equals("TIMER")) {

            setTime = setTime + direction;

            if (setTime < 1) {
                setTime = 1;
            }

            if (setTime > 60) {
                setTime = 60;
            }

            valueText.setText(setTime + " sec");
        }

        if (selected.equals("TEMP")) {

            setTemperature = setTemperature + (direction * 5);

            if (setTemperature < 80) {
                setTemperature = 80;
            }

            if (setTemperature > 200) {
                setTemperature = 200;
            }

            valueText.setText(setTemperature + " °C");
        }
    }

    public void saveAdjustment() {

        String selected = choices[choiceIndex];

        if (selected.equals("TIMER")) {
            timerReady = true;
            valueText.setText(setTime + " sec SET");
        }

        if (selected.equals("TEMP")) {
            temperatureReady = true;
            valueText.setText(setTemperature + " °C SET");
        }

        adjusting = false;

        if (readyForCooking()) {
            choiceIndex = 4;
            modeText.setText("FRY");
            valueText.setText("FRY");
        }
    }

    public boolean readyForCooking() {
        return turnedOn && timerReady && temperatureReady;
    }

    public void beginFrying() {

        if (!readyForCooking()) {
            return;
        }

        running = true;
        adjusting = false;

        remaining = setTime;
        heaterTemperature = 25;

        modeText.setText("FRY");
        valueText.setText(remaining + " sec");

        fanText.setText("Fan: SPINNING");
        heaterText.setText("Heater: " + heaterTemperature + " °C");

        if (remaining <= 5) {
            startWarningFlash();
        }

        cookingClock = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        e -> fryingStep()
                )
        );

        cookingClock.setCycleCount(Timeline.INDEFINITE);
        cookingClock.play();
    }

    public void fryingStep() {

        remaining--;

        raiseHeater();

        valueText.setText(remaining + " sec");

        if (remaining <= 5 && remaining > 0) {

            if (warningClock == null) {
                startWarningFlash();
            }
        }

        if (remaining <= 0) {
            turnMachineOff();
        }
    }

    public void raiseHeater() {

        if (heaterTemperature < setTemperature) {

            heaterTemperature += 10;

            if (heaterTemperature > setTemperature) {
                heaterTemperature = setTemperature;
            }

            heaterText.setText(
                    "Heater: " + heaterTemperature + " °C"
            );
        }
    }

    public void startWarningFlash() {

        warningClock = new Timeline(
                new KeyFrame(
                        Duration.seconds(0.5),
                        e -> alternateScreenColor()
                )
        );

        warningClock.setCycleCount(Timeline.INDEFINITE);
        warningClock.play();
    }

    public void alternateScreenColor() {

        if (flashBlue) {
            changeScreenColor("red");
        } else {
            changeScreenColor("blue");
        }

        flashBlue = !flashBlue;
    }

    public void stopIfNeeded() {

        if (turnedOn || running) {
            turnMachineOff();
        }
    }

    public void turnMachineOff() {

        stopTimelines();

        turnedOn = false;
        running = false;
        adjusting = false;

        timerReady = false;
        temperatureReady = false;

        flashBlue = false;

        setTime = 1;
        setTemperature = 80;
        heaterTemperature = 25;

        choiceIndex = 1;

        modeText.setText("OFF");
        valueText.setText("OFF");

        fanText.setText("Fan: OFF");
        heaterText.setText("Heater: OFF");

        changeScreenColor("yellow");
    }

    public void stopTimelines() {

        if (cookingClock != null) {
            cookingClock.stop();
            cookingClock = null;
        }

        if (warningClock != null) {
            warningClock.stop();
            warningClock = null;
        }
    }

    public void changeScreenColor(String color) {

        screenPanel.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
