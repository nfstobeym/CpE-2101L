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

public class AirFryerSimulator extends Application {

    String[] options = {"ON", "OFF", "TIMER", "TEMP", "FRY"};

    int currentOption = 0;
    int selectedTime = 1;
    int selectedTemp = 80;
    int timeLeft;

    boolean plugged = false;
    boolean basketReady = false;
    boolean powered = false;
    boolean timeConfirmed = false;
    boolean tempConfirmed = false;
    boolean cooking = false;
    boolean blueScreen = false;

    Label topScreen = new Label();
    Label bottomScreen = new Label();
    Label fanStatus = new Label("Fan: OFF");
    Label heatStatus = new Label("Heater: OFF");

    VBox screen = new VBox();

    Timeline countdown;
    Timeline blinking;

    @Override
    public void start(Stage stage) {

        Label heading = new Label("AIR FRYER SIMULATOR");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        setupScreen();

        Button decrease = new Button("-");
        Button powerSelect = new Button("PWR/SEL");
        Button increase = new Button("+");

        decrease.setPrefSize(80, 50);
        powerSelect.setPrefSize(100, 50);
        increase.setPrefSize(80, 50);

        ToggleButton plugSwitch = new ToggleButton("Power Cord: OUT");
        ToggleButton basketSwitch = new ToggleButton("Basket: OUT");

        plugSwitch.setPrefWidth(160);
        basketSwitch.setPrefWidth(160);

        decrease.setOnAction(e -> pressMinus());
        increase.setOnAction(e -> pressPlus());
        powerSelect.setOnAction(e -> chooseOption());

        plugSwitch.setOnAction(e -> {
            plugged = plugSwitch.isSelected();

            if (plugged) {
                plugSwitch.setText("Power Cord: IN");
            } else {
                plugSwitch.setText("Power Cord: OUT");

                if (powered) {
                    shutDown();
                }
            }
        });

        basketSwitch.setOnAction(e -> {
            basketReady = basketSwitch.isSelected();

            if (basketReady) {
                basketSwitch.setText("Basket: IN");
            } else {
                basketSwitch.setText("Basket: OUT");

                if (powered) {
                    shutDown();
                }
            }
        });

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(decrease, powerSelect, increase);
        buttons.setAlignment(Pos.CENTER);

        HBox switches = new HBox(10);
        switches.getChildren().addAll(plugSwitch, basketSwitch);
        switches.setAlignment(Pos.CENTER);

        HBox machineStatus = new HBox(30);
        machineStatus.getChildren().addAll(fanStatus, heatStatus);
        machineStatus.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.getChildren().addAll(
                heading,
                screen,
                buttons,
                switches,
                machineStatus
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 450, 400);

        stage.setTitle("Air Fryer");
        stage.setScene(scene);
        stage.show();

        showCurrentOption();
    }

    public void setupScreen() {

        topScreen.setMaxWidth(Double.MAX_VALUE);
        bottomScreen.setMaxWidth(Double.MAX_VALUE);

        topScreen.setAlignment(Pos.CENTER_LEFT);
        bottomScreen.setAlignment(Pos.CENTER);

        topScreen.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        bottomScreen.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        screen.setPrefSize(300, 100);
        screen.setPadding(new Insets(10));

        screen.getChildren().addAll(topScreen, bottomScreen);

        yellowDisplay();
    }

    public void pressPlus() {

        String option = options[currentOption];

        if (!cooking && powered && option.equals("TIMER")) {
            increaseTimer();
        } else if (!cooking && powered && option.equals("TEMP")) {
            increaseTemperature();
        } else {
            moveOption(1);
        }
    }

    public void pressMinus() {

        String option = options[currentOption];

        if (!cooking && powered && option.equals("TIMER")) {
            decreaseTimer();
        } else if (!cooking && powered && option.equals("TEMP")) {
            decreaseTemperature();
        } else {
            moveOption(-1);
        }
    }

    public void moveOption(int movement) {

        currentOption += movement;

        if (currentOption >= options.length) {
            currentOption = 0;
        }

        if (currentOption < 0) {
            currentOption = options.length - 1;
        }

        if (options[currentOption].equals("FRY") && !readyToFry()) {
            moveOption(movement);
            return;
        }

        showCurrentOption();
    }

    public void showCurrentOption() {

        String option = options[currentOption];

        topScreen.setText(option);

        if (cooking) {
            bottomScreen.setText(timeLeft + " sec");
            return;
        }

        switch (option) {

            case "ON":
                if (powered) {
                    bottomScreen.setText("ON");
                } else {
                    bottomScreen.setText("OFF");
                }
                break;

            case "OFF":
                bottomScreen.setText("OFF");
                break;

            case "TIMER":
                selectedTime = 1;
                bottomScreen.setText(selectedTime + " sec");
                break;

            case "TEMP":
                selectedTemp = 80;
                bottomScreen.setText(selectedTemp + " °C");
                break;

            case "FRY":
                bottomScreen.setText("FRY");
                break;
        }
    }

    public void chooseOption() {

        String choice = options[currentOption];

        if (cooking) {
            if (choice.equals("OFF")) {
                shutDown();
            }
            return;
        }

        switch (choice) {

            case "ON":
                powerOn();
                break;

            case "OFF":
                shutDown();
                break;

            case "TIMER":
                confirmTimer();
                break;

            case "TEMP":
                confirmTemperature();
                break;

            case "FRY":
                beginCooking();
                break;
        }
    }

    public void powerOn() {

        if (!plugged || !basketReady) {
            bottomScreen.setText("CHECK PLUG/BASKET");
            return;
        }

        powered = true;

        topScreen.setText("ON");
        bottomScreen.setText("ON");

        redDisplay();
    }

    public void confirmTimer() {

        if (!powered) {
            return;
        }

        timeConfirmed = true;
        bottomScreen.setText(selectedTime + " sec SET");

        goToFryIfReady();
    }

    public void confirmTemperature() {

        if (!powered) {
            return;
        }

        tempConfirmed = true;
        bottomScreen.setText(selectedTemp + " °C SET");

        goToFryIfReady();
    }

    public void goToFryIfReady() {

        if (readyToFry()) {
            currentOption = 4;
            topScreen.setText("");
            bottomScreen.setText("FRY");
        }
    }

    public boolean readyToFry() {
        return powered && timeConfirmed && tempConfirmed;
    }

    public void increaseTimer() {

        if (selectedTime < 60) {
            selectedTime++;
        }

        bottomScreen.setText(selectedTime + " sec");
    }

    public void decreaseTimer() {

        if (selectedTime > 1) {
            selectedTime--;
        }

        bottomScreen.setText(selectedTime + " sec");
    }

    public void increaseTemperature() {

        if (selectedTemp < 200) {
            selectedTemp += 5;
        }

        bottomScreen.setText(selectedTemp + " °C");
    }

    public void decreaseTemperature() {

        if (selectedTemp > 80) {
            selectedTemp -= 5;
        }

        bottomScreen.setText(selectedTemp + " °C");
    }

    public void beginCooking() {

        if (!readyToFry()) {
            return;
        }

        cooking = true;
        timeLeft = selectedTime;

        topScreen.setText("FRY");
        bottomScreen.setText(timeLeft + " sec");

        fanStatus.setText("Fan: SPINNING");
        heatStatus.setText("Heater: " + selectedTemp + " °C");

        countdown = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateCooking())
        );

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    public void updateCooking() {

        timeLeft--;

        bottomScreen.setText(timeLeft + " sec");

        if (timeLeft <= 5 && timeLeft > 0 && blinking == null) {
            beginBlueFlash();
        }

        if (timeLeft <= 0) {
            shutDown();
        }
    }

    public void beginBlueFlash() {

        blinking = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> switchFlashColor())
        );

        blinking.setCycleCount(Timeline.INDEFINITE);
        blinking.play();
    }

    public void switchFlashColor() {

        if (blueScreen) {
            redDisplay();
        } else {
            blueDisplay();
        }

        blueScreen = !blueScreen;
    }

    public void shutDown() {

        powered = false;
        cooking = false;
        timeConfirmed = false;
        tempConfirmed = false;
        blueScreen = false;

        stopTimers();

        currentOption = 1;

        topScreen.setText("OFF");
        bottomScreen.setText("OFF");

        fanStatus.setText("Fan: OFF");
        heatStatus.setText("Heater: OFF");

        yellowDisplay();
    }

    public void stopTimers() {

        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }

        if (blinking != null) {
            blinking.stop();
            blinking = null;
        }
    }

    public void redDisplay() {
        screen.setStyle(
                "-fx-background-color: red;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;"
        );
    }

    public void yellowDisplay() {
        screen.setStyle(
                "-fx-background-color: yellow;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;"
        );
    }

    public void blueDisplay() {
        screen.setStyle(
                "-fx-background-color: blue;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 3;"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
