package com.powdersimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PowderSimulator extends Application {
    // UI layout settings
    static final int CELL_SIZE = 6;
    static final int GRID_W = 160;
    static final int GRID_H = 120;
    static final int UI_HEIGHT = 56;
    static final int TEXT_HEIGHT = 20;

    private PowderEngine engine;
    private long frameCount = 0;

    private CellType selected = CellType.Sand;
    private int brush = 3;

    private Button[] buttons;
    private String[] btnLabels = { "Sand", "Water", "Stone", "Metal", "Fire", "Oil", "Lava", "Wood", "Steam", "Acid", "TNT", "Erase", "Clear" };

    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer timer;
    private boolean mouseDown = false;
    private double mouseX = 0, mouseY = 0;
    private Label infoLabel;

    @Override
    public void start(Stage primaryStage) {
        engine = new PowderEngine(GRID_W, GRID_H);

        BorderPane root = new BorderPane();

        infoLabel = new Label();
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setStyle("-fx-background-color: black; -fx-padding: 5;");
        updateInfoLabel();

        canvas = new Canvas(GRID_W * CELL_SIZE, GRID_H * CELL_SIZE);
        gc = canvas.getGraphicsContext2D();

        HBox buttonPanel = new HBox(5);
        buttonPanel.setPadding(new Insets(5));
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setStyle("-fx-background-color: #222222;");

        buttons = new Button[btnLabels.length];
        for (int i = 0; i < btnLabels.length; i++) {
            Button btn = new Button(btnLabels[i]);
            btn.setMinWidth(70);
            final int idx = i;
            btn.setOnAction(e -> handleButton(btnLabels[idx]));
            buttonPanel.getChildren().add(btn);
            buttons[i] = btn;
        }

        VBox topSection = new VBox();
        topSection.getChildren().addAll(infoLabel, canvas);
        root.setTop(topSection);
        root.setBottom(buttonPanel);

        setupEventHandlers();

        Scene scene = new Scene(root, GRID_W * CELL_SIZE, GRID_H * CELL_SIZE + UI_HEIGHT + TEXT_HEIGHT);
        scene.setFill(Color.BLACK);

        primaryStage.setTitle("Powder Simulator - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handleMouseInput();
                engine.step();
                draw();
                updateInfoLabel();
            }
        };
        timer.start();

        updateButtonAppearance();
    }

    private double getNoise(int x, int y) {
        int n = x * 331 + y * 3331;
        n = (n ^ (n >> 13)) * 0x5bd1e995;
        return (n & 0x7FFFFFFF) / (double) 0x7FFFFFFF;
    }

    private Color getColorFor(CellType t, int x, int y) {
        double noise = getNoise(x, y);
        switch (t) {
            case Sand: 
                return Color.rgb(194 + (int)(noise*15), 178 + (int)(noise*15), 128 + (int)(noise*10));
            case Water: 
                double wave = Math.sin(x * 0.2 + frameCount * 0.05) * 0.5 + 0.5;
                return Color.rgb(40 + (int)(wave*20), 140 + (int)(wave*30), 200 + (int)(wave*35));
            case Stone: 
                if (engine.getVelocity(x, y) > 5) return Color.rgb(200, 200, 200); 
                return Color.rgb(100 + (int)(noise*20), 100 + (int)(noise*20), 100 + (int)(noise*20));
            case Metal: 
                int band = (y % 4 == 0) ? 200 : 180;
                return Color.rgb(band, band, band + 20);
            case Fire: 
                int timer = engine.getBurnTimer(x, y);
                if (timer > 15) return Color.rgb(255, 255, 150); // White-hot
                if (timer > 8) return Color.rgb(255, 130 + (int)(noise*40), 40); // Vibrant orange
                return Color.rgb(200, 30, 30); // Deep red dying embers
            case Oil: 
                return Color.rgb(40 + (int)(noise*10), 25 + (int)(noise*10), 15);
            case Lava: 
                double pulse = Math.sin(frameCount * 0.08 + x * 0.1 + y * 0.1) * 0.5 + 0.5;
                return Color.rgb(200 + (int)(pulse * 55), 40 + (int)(pulse * 30), 0);
            case Wood: 
                if (x % 3 == 0) return Color.rgb(101, 67, 33); 
                return Color.rgb(139, 69, 19);
            case Steam: 
                return Color.rgb(200, 200, 220); 
            case Acid: 
                double acidPulse = Math.sin(frameCount * 0.1 + x * 0.2 + y * 0.2) * 0.5 + 0.5;
                return Color.rgb(100 + (int)(acidPulse * 50), 255, 50); 
            case TNT: 
                if (((x / 4) + (y / 4)) % 2 == 0) return Color.rgb(220, 40, 40);
                return Color.rgb(240, 240, 240);
            default: return Color.BLACK;
        }
    }

    private void handleButton(String label) {
        switch (label) {
            case "Sand": selected = CellType.Sand; break;
            case "Water": selected = CellType.Water; break;
            case "Stone": selected = CellType.Stone; break;
            case "Metal": selected = CellType.Metal; break;
            case "Fire": selected = CellType.Fire; break;
            case "Oil": selected = CellType.Oil; break;
            case "Lava": selected = CellType.Lava; break;
            case "Wood": selected = CellType.Wood; break;
            case "Steam": selected = CellType.Steam; break;
            case "Acid": selected = CellType.Acid; break;
            case "TNT": selected = CellType.TNT; break;
            case "Erase": selected = CellType.Empty; break;
            case "Clear": engine.clear(); break;
        }
        updateButtonAppearance();
    }

    private void updateButtonAppearance() {
        for (int i = 0; i < buttons.length; i++) {
            String label = btnLabels[i];
            boolean isSelected = (label.equals("Sand") && selected == CellType.Sand) ||
                    (label.equals("Water") && selected == CellType.Water) ||
                    (label.equals("Stone") && selected == CellType.Stone) ||
                    (label.equals("Metal") && selected == CellType.Metal) ||
                    (label.equals("Fire") && selected == CellType.Fire) ||
                    (label.equals("Oil") && selected == CellType.Oil) ||
                    (label.equals("Lava") && selected == CellType.Lava) ||
                    (label.equals("Wood") && selected == CellType.Wood) ||
                    (label.equals("Steam") && selected == CellType.Steam) ||
                    (label.equals("Acid") && selected == CellType.Acid) ||
                    (label.equals("TNT") && selected == CellType.TNT) ||
                    (label.equals("Erase") && selected == CellType.Empty);

            if (isSelected) {
                buttons[i].setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
            } else {
                buttons[i].setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
            }
        }
    }

    private void updateInfoLabel() {
        int sandCount = engine.count(CellType.Sand);
        int waterCount = engine.count(CellType.Water);
        int fireCount = engine.count(CellType.Fire);
        int oilCount = engine.count(CellType.Oil);
        int lavaCount = engine.count(CellType.Lava);
        
        infoLabel.setText("Tool: " + selected + "   Brush:" + brush +
                "   Sand:" + sandCount + " Water:" + waterCount +
                " Fire:" + fireCount + " Oil:" + oilCount + " Lava:" + lavaCount +
                "  Keys: 1-8 tool, Up/Down brush");
    }

    private void draw() {
        frameCount++;
        
        // Soft clear for motion blur effect
        gc.setFill(Color.rgb(5, 5, 8, 0.45));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < engine.getHeight(); y++) {
            for (int x = 0; x < engine.getWidth(); x++) {
                CellType cell = engine.getCell(x, y);
                if (cell != CellType.Empty) {
                     gc.setFill(getColorFor(cell, x, y));
                     // Draw slightly smaller to leave a nice 1px voxel grid gap
                     gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                }
            }
        }
    }

    private void handleMouseInput() {
        if (mouseDown && mouseY >= 0 && mouseY < engine.getHeight() * CELL_SIZE) {
            int gx = (int) (mouseX / CELL_SIZE);
            int gy = (int) (mouseY / CELL_SIZE);
            for (int oy = -brush; oy <= brush; oy++) {
                for (int ox = -brush; ox <= brush; ox++) {
                    int x = gx + ox, y = gy + oy;
                    if (engine.inb(x, y) && ox * ox + oy * oy <= brush * brush) {
                        engine.setCell(x, y, selected);
                    }
                }
            }
        }
    }

    private void setupEventHandlers() {
        canvas.setOnMousePressed(e -> {
            mouseDown = true;
            mouseX = e.getX();
            mouseY = e.getY();
        });

        canvas.setOnMouseReleased(e -> {
            mouseDown = false;
        });

        canvas.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        canvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        canvas.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case DIGIT1: selected = CellType.Sand; break;
                case DIGIT2: selected = CellType.Water; break;
                case DIGIT3: selected = CellType.Stone; break;
                case DIGIT4: selected = CellType.Metal; break;
                case DIGIT5: selected = CellType.Fire; break;
                case DIGIT6: selected = CellType.Oil; break;
                case DIGIT7: selected = CellType.Lava; break;
                case DIGIT8: selected = CellType.Wood; break;
                case DIGIT9: selected = CellType.Acid; break;
                case DIGIT0: selected = CellType.TNT; break;
                case MINUS: selected = CellType.Steam; break;
                case UP: brush = Math.min(30, brush + 1); break;
                case DOWN: brush = Math.max(1, brush - 1); break;
                default: break;
            }
            updateButtonAppearance();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
