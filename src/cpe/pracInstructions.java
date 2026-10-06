package cpe;

public class pracInstructions {
	import javafx.application.Application;
	import javafx.scene.canvas.Canvas;
	import javafx.scene.Scene;
	import javafx.scene.Group;
	import javafx.stage.Stage;
	import javafx.scene.canvas.GraphicsContext;
	import javafx.scene.shape.ArcType;
	import javafx.scene.control.Button;

	public class HappyFace extends Application {

	    public static void main(String[] args) {
	        launch(args);
	    }

	    @Override
	    public void start(Stage primaryStage) throws Exception {

	        Group root = new Group();
	        Scene scene = new Scene(root, 400, 350);

	        Canvas canvas = new Canvas(400, 300);
	        GraphicsContext gc = canvas.getGraphicsContext2D();

	        // Face
	        gc.strokeOval(100, 50, 200, 200);

	        // Eyes
	        gc.fillOval(155, 100, 10, 20);
	        gc.fillOval(230, 100, 10, 20);

	        // Default smile
	        drawSmile(gc);

	        // Happy button
	        Button happyButton = new Button("Happy");
	        happyButton.setLayoutX(80);
	        happyButton.setLayoutY(310);

	        // Sad button
	        Button sadButton = new Button("Sad");
	        sadButton.setLayoutX(150);
	        sadButton.setLayoutY(310);

	        // Neutral button
	        Button neutralButton = new Button("Neutral");
	        neutralButton.setLayoutX(210);
	        neutralButton.setLayoutY(310);

	        // Reset button
	        Button resetButton = new Button("Reset");
	        resetButton.setLayoutX(290);
	        resetButton.setLayoutY(310);

	        // Happy button action
	        happyButton.setOnAction(e -> {
	            clearMouth(gc);
	            drawSmile(gc);
	        });

	        // Sad button action
	        sadButton.setOnAction(e -> {
	            clearMouth(gc);
	            drawSadSmile(gc);
	        });

	        // Neutral button action
	        neutralButton.setOnAction(e -> {
	            clearMouth(gc);
	            drawNeutralMouth(gc);
	        });

	        // Reset button action
	        resetButton.setOnAction(e -> {
	            clearMouth(gc);
	            drawSmile(gc);
	        });

	        root.getChildren().addAll(
	            canvas,
	            happyButton,
	            sadButton,
	            neutralButton,
	            resetButton
	        );

	        primaryStage.setTitle("HappyFace in JavaFX");
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    }

	    // Happy smile
	    private void drawSmile(GraphicsContext gc) {
	        gc.strokeArc(
	            150, 160,
	            100, 50,
	            180, 180,
	            ArcType.OPEN
	        );
	    }

	    // Sad smile
	    private void drawSadSmile(GraphicsContext gc) {
	        gc.strokeArc(
	            150, 160,
	            100, 50,
	            0, 180,
	            ArcType.OPEN
	        );
	    }

	    // Neutral mouth
	    private void drawNeutralMouth(GraphicsContext gc) {
	        gc.strokeLine(150, 185, 250, 185);
	    }

	    // Clear only the mouth area
	    private void clearMouth(GraphicsContext gc) {
	        gc.clearRect(140, 150, 120, 70);
	    }
	}
}
