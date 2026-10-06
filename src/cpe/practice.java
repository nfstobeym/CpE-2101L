import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ElectricFanApp extends Application {

    private RotateTransition rotation;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        Group fan = new Group();

        Circle outerGuard = new Circle(200, 200, 155);
        outerGuard.setFill(Color.TRANSPARENT);
        outerGuard.setStroke(Color.DARKSLATEGRAY);
        outerGuard.setStrokeWidth(12);

        Circle innerGuard1 = new Circle(200, 200, 125);
        innerGuard1.setFill(Color.TRANSPARENT);
        innerGuard1.setStroke(Color.LIGHTGRAY);
        innerGuard1.setStrokeWidth(3);

        Circle innerGuard2 = new Circle(200, 200, 85);
        innerGuard2.setFill(Color.TRANSPARENT);
        innerGuard2.setStroke(Color.LIGHTGRAY);
        innerGuard2.setStrokeWidth(2);

        Group blades = new Group();

        for (int i = 0; i < 3; i++) {
            Polygon blade = createBlade();
            blade.setRotate(i * 120);
            blades.getChildren().add(blade);
        }

        Circle hub = new Circle(200, 200, 25);
        hub.setFill(Color.DARKSLATEGRAY);
        hub.setStroke(Color.BLACK);
        hub.setStrokeWidth(3);

        Circle hubCenter = new Circle(200, 200, 9);
        hubCenter.setFill(Color.LIGHTGRAY);

        fan.getChildren().addAll(
                blades,
                hub,
                hubCenter,
                innerGuard2,
                innerGuard1,
                outerGuard
        );

        Rectangle neck = new Rectangle(183, 350, 34, 65);
        neck.setFill(Color.DARKSLATEGRAY);
        neck.setArcWidth(12);
        neck.setArcHeight(12);

        Rectangle base = new Rectangle(110, 405, 180, 45);
        base.setFill(Color.DARKSLATEGRAY);
        base.setArcWidth(25);
        base.setArcHeight(25);

        statusLabel = new Label("POWER: OFF");
        statusLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333333;"
        );

        Button offButton = new Button("OFF");
        Button lowButton = new Button("LOW");
        Button medButton = new Button("MED");
        Button hiButton = new Button("HI");

        Button[] buttons = {
                offButton, lowButton, medButton, hiButton
        };

        for (Button button : buttons) {
            button.setPrefWidth(90);
            button.setPrefHeight(45);
            button.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
        }

        offButton.setOnAction(e -> setFanSpeed(0));
        lowButton.setOnAction(e -> setFanSpeed(1));
        medButton.setOnAction(e -> setFanSpeed(2));
        hiButton.setOnAction(e -> setFanSpeed(3));

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
                offButton,
                lowButton,
                medButton,
                hiButton
        );

        Circle powerIndicator = new Circle(8);
        powerIndicator.setFill(Color.LIMEGREEN);

        Label acLabel = new Label("AC POWER");
        acLabel.setStyle("-fx-font-weight: bold;");

        HBox powerBox = new HBox(8);
        powerBox.setAlignment(Pos.CENTER);
        powerBox.getChildren().addAll(powerIndicator, acLabel);

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F6F7;");

        Label title = new Label("AC ELECTRIC FAN");
        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #263238;"
        );

        Label subtitle = new Label("3-Blade Abstract Model");
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #607D8B;"
        );

        Pane fanPane = new Pane();
        fanPane.setPrefSize(400, 470);
        fanPane.getChildren().addAll(
                fan,
                neck,
                base
        );

        root.getChildren().addAll(
                title,
                subtitle,
                fanPane,
                statusLabel,
                powerBox,
                controls
        );

        Scene scene = new Scene(root, 500, 720);

        stage.setTitle("AC Electric Fan - JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        setFanSpeed(0);
    }

    private Polygon createBlade() {
        Polygon blade = new Polygon();

        blade.getPoints().addAll(
                200.0, 200.0,
                185.0, 115.0,
                205.0, 65.0,
                235.0, 55.0,
                225.0, 100.0,
                215.0, 155.0
        );

        blade.setFill(Color.STEELBLUE);
        blade.setStroke(Color.DARKSLATEGRAY);
        blade.setStrokeWidth(2);

        return blade;
    }

    private void setFanSpeed(int speed) {
        if (rotation != null) {
            rotation.stop();
        }

        switch (speed) {
            case 0:
                statusLabel.setText("POWER: OFF");
                break;

            case 1:
                statusLabel.setText("POWER: ON  |  SPEED: LOW");
                startRotation(1800);
                break;

            case 2:
                statusLabel.setText("POWER: ON  |  SPEED: MED");
                startRotation(900);
                break;

            case 3:
                statusLabel.setText("POWER: ON  |  SPEED: HI");
                startRotation(350);
                break;

            default:
                statusLabel.setText("POWER: OFF");
        }
    }

    private void startRotation(double milliseconds) {
        if (statusLabel.getScene() == null) {
            return;
        }

        Group bladeGroup = findBladeGroup(
                statusLabel.getScene().getRoot()
        );

        if (bladeGroup == null) {
            return;
        }

        rotation = new RotateTransition(
                Duration.millis(milliseconds),
                bladeGroup
        );

        rotation.setAxis(Rotate.Z_AXIS);
        rotation.setByAngle(360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setInterpolator(
                javafx.animation.Interpolator.LINEAR
        );

        rotation.play();
    }

    private Group findBladeGroup(javafx.scene.Node node) {
        if (node instanceof Group) {
            Group group = (Group) node;

            if (!group.getChildren().isEmpty()
                    && group.getChildren().get(0) instanceof Polygon) {
                return group;
            }

            for (javafx.scene.Node child : group.getChildren()) {
                Group result = findBladeGroup(child);

                if (result != null) {
                    return result;
                }
            }
        }

        if (node instanceof Pane) {
            Pane pane = (Pane) node;

            for (javafx.scene.Node child : pane.getChildren()) {
                Group result = findBladeGroup(child);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
