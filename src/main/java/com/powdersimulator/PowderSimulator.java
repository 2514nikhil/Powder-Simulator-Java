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
import java.util.Arrays;

public class PowderSimulator extends Application {
    // Grid settings
    static final int CELL_SIZE = 6;
    static final int GRID_W = 160;
    static final int GRID_H = 120;
    static final int UI_HEIGHT = 56;
    static final int TEXT_HEIGHT = 20; // Space for text at top

    enum CellType {
        Empty, Sand, Water, Stone, Metal, Fire, Oil
    }

    private CellType[] grid = new CellType[GRID_W * GRID_H];
    private int[] stoneVelocity = new int[GRID_W * GRID_H]; // Track stone falling velocity
    private int[] burnTimer = new int[GRID_W * GRID_H]; // Fire lifetime
    private int[] oilAge = new int[GRID_W * GRID_H]; // Help with oil spread/evaporation if needed

    private CellType selected = CellType.Sand;
    private int brush = 3;

    private Button[] buttons;
    private String[] btnLabels = { "Sand", "Water", "Stone", "Metal", "Fire", "Oil", "Erase", "Clear" };

    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer timer;
    private boolean mouseDown = false;
    private double mouseX = 0, mouseY = 0;
    private Label infoLabel;

    @Override
    public void start(Stage primaryStage) {
        Arrays.fill(grid, CellType.Empty);
        Arrays.fill(stoneVelocity, 0);
        Arrays.fill(burnTimer, 0);
        Arrays.fill(oilAge, 0);

        // Create main layout
        BorderPane root = new BorderPane();

        // Create info label
        infoLabel = new Label();
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setStyle("-fx-background-color: black; -fx-padding: 5;");
        updateInfoLabel();

        // Create canvas for drawing
        canvas = new Canvas(GRID_W * CELL_SIZE, GRID_H * CELL_SIZE);
        gc = canvas.getGraphicsContext2D();

        // Create button panel
        HBox buttonPanel = new HBox(5);
        buttonPanel.setPadding(new Insets(5));
        buttonPanel.setAlignment(Pos.CENTER);

        // Create buttons
        buttons = new Button[btnLabels.length];
        for (int i = 0; i < btnLabels.length; i++) {
            Button btn = new Button(btnLabels[i]);
            btn.setMinWidth(100);
            final int idx = i;
            btn.setOnAction(e -> handleButton(btnLabels[idx]));
            buttonPanel.getChildren().add(btn);
            buttons[i] = btn;
        }

        // Layout components
        VBox topSection = new VBox();
        topSection.getChildren().addAll(infoLabel, canvas);
        root.setTop(topSection);
        root.setBottom(buttonPanel);

        // Set up mouse and keyboard events
        setupEventHandlers();

        // Create scene
        Scene scene = new Scene(root, GRID_W * CELL_SIZE, GRID_H * CELL_SIZE + UI_HEIGHT + TEXT_HEIGHT);
        scene.setFill(Color.BLACK);

        primaryStage.setTitle("Powder Simulator - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Request focus for keyboard input
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        // Start animation timer
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handleMouseInput();
                simulate();
                draw();
                updateInfoLabel();
            }
        };
        timer.start();

        updateButtonAppearance();
    }

    private int idx(int x, int y) {
        return y * GRID_W + x;
    }

    private boolean inb(int x, int y) {
        return x >= 0 && x < GRID_W && y >= 0 && y < GRID_H;
    }

    private void setCell(int x, int y, CellType ct) {
        if (!inb(x, y))
            return;
        int i = idx(x, y);
        grid[i] = ct;
        stoneVelocity[i] = 0;
        burnTimer[i] = 0;
        oilAge[i] = 0;
        if (ct == CellType.Fire) {
            // base fire lifetime
            burnTimer[i] = 8 + (int) (Math.random() * 12); // 8..19 ticks
        } else if (ct == CellType.Oil) {
            oilAge[i] = 0;
        }
    }

    private void swapCell(int x1, int y1, int x2, int y2) {
        if (!inb(x1, y1) || !inb(x2, y2))
            return;
        int i1 = idx(x1, y1), i2 = idx(x2, y2);
        CellType tmp = grid[i1];
        grid[i1] = grid[i2];
        grid[i2] = tmp;

        int tmpVel = stoneVelocity[i1];
        stoneVelocity[i1] = stoneVelocity[i2];
        stoneVelocity[i2] = tmpVel;

        int tmpBurn = burnTimer[i1];
        burnTimer[i1] = burnTimer[i2];
        burnTimer[i2] = tmpBurn;

        int tmpOilAge = oilAge[i1];
        oilAge[i1] = oilAge[i2];
        oilAge[i2] = tmpOilAge;
    }

    private Color colorFor(CellType t) {
        switch (t) {
            case Sand:
                return Color.rgb(194, 178, 128);
            case Water:
                return Color.rgb(64, 164, 223);
            case Stone:
                return Color.rgb(120, 120, 120);
            case Metal:
                return Color.rgb(180, 180, 200);
            case Fire:
                return Color.rgb(255, 130, 40); // Bright orange
            case Oil:
                return Color.rgb(60, 40, 25); // Dark brown
            default:
                return Color.BLACK;
        }
    }

    private void handleButton(String label) {
        switch (label) {
            case "Sand":
                selected = CellType.Sand;
                break;
            case "Water":
                selected = CellType.Water;
                break;
            case "Stone":
                selected = CellType.Stone;
                break;
            case "Metal":
                selected = CellType.Metal;
                break;
            case "Fire":
                selected = CellType.Fire;
                break;
            case "Oil":
                selected = CellType.Oil;
                break;
            case "Erase":
                selected = CellType.Empty;
                break;
            case "Clear":
                Arrays.fill(grid, CellType.Empty);
                Arrays.fill(stoneVelocity, 0);
                Arrays.fill(burnTimer, 0);
                Arrays.fill(oilAge, 0);
                break;
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
                    (label.equals("Erase") && selected == CellType.Empty);

            if (isSelected) {
                buttons[i].setStyle("-fx-background-color: lightgray;");
            } else {
                buttons[i].setStyle("");
            }
        }
    }

    private void updateInfoLabel() {
        int sandCount = 0, waterCount = 0, fireCount = 0, oilCount = 0;
        for (CellType c : grid) {
            if (c == CellType.Sand)
                sandCount++;
            if (c == CellType.Water)
                waterCount++;
            if (c == CellType.Fire)
                fireCount++;
            if (c == CellType.Oil)
                oilCount++;
        }
        infoLabel.setText("Tool: " + selected + "   Brush:" + brush +
                "   Sand:" + sandCount + " Water:" + waterCount +
                " Fire:" + fireCount + " Oil:" + oilCount +
                "  Keys: 1-6 tool, Up/Down brush");
    }

    private void simulate() {
        // Iterate bottom-to-top to allow gravity-driven movement.
        for (int y = GRID_H - 2; y >= 0; y--) {
            for (int x = 0; x < GRID_W; x++) {
                CellType me = grid[idx(x, y)];

                // --- SAND ---
                if (me == CellType.Sand) {
                    int by = y + 1;
                    if (inb(x, by)) {
                        if (grid[idx(x, by)] == CellType.Empty) {
                            swapCell(x, y, x, by);
                        } else if (grid[idx(x, by)] == CellType.Water) {
                            // sand sinks through water (displace)
                            swapCell(x, y, x, by);
                        } else {
                            // diagonal slide preference using small randomness
                            boolean leftFirst = Math.random() < 0.5;
                            if (leftFirst) {
                                if (canSandFall(x - 1, by))
                                    swapCell(x, y, x - 1, by);
                                else if (canSandFall(x + 1, by))
                                    swapCell(x, y, x + 1, by);
                            } else {
                                if (canSandFall(x + 1, by))
                                    swapCell(x, y, x + 1, by);
                                else if (canSandFall(x - 1, by))
                                    swapCell(x, y, x - 1, by);
                            }
                        }
                    }
                }

                // --- WATER ---
                else if (me == CellType.Water) {
                    int by = y + 1;
                    if (inb(x, by)) {
                        CellType below = grid[idx(x, by)];
                        if (below == CellType.Empty) {
                            swapCell(x, y, x, by);
                            continue;
                        }
                        if (below == CellType.Oil) {
                            // Water is heavier than oil; let it move underneath while oil rises.
                            swapCell(x, by, x, y);
                            continue;
                        }
                    }

                    boolean moved = false;
                    int[] dirs = Math.random() < 0.5 ? new int[] { -1, 1 } : new int[] { 1, -1 };

                    // Spread sideways first so pools level out instead of piling like sand.
                    outer:
                    for (int distance = 1; distance <= 3; distance++) {
                        for (int dir : dirs) {
                            int nx = x + dir * distance;
                            if (!inb(nx, y))
                                continue;

                            CellType target = grid[idx(nx, y)];
                            if (target == CellType.Empty) {
                                swapCell(x, y, nx, y);
                                moved = true;
                                break outer;
                            }
                            if (target == CellType.Oil) {
                                // Slip under an oil pocket if the space beneath allows it.
                                int supportY = y + 1;
                                if (inb(nx, supportY)) {
                                    CellType support = grid[idx(nx, supportY)];
                                    if (support == CellType.Empty || support == CellType.Water) {
                                        swapCell(nx, y, x, y);
                                        moved = true;
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    if (moved)
                        continue;

                    // Fallback: diagonal slides for waterfalls or tight corners.
                    for (int dir : dirs) {
                        int nx = x + dir;
                        if (!inb(nx, by))
                            continue;
                        CellType diagonal = grid[idx(nx, by)];
                        if (diagonal == CellType.Empty) {
                            swapCell(x, y, nx, by);
                            moved = true;
                            break;
                        }
                        if (diagonal == CellType.Oil) {
                            swapCell(nx, by, x, y);
                            moved = true;
                            break;
                        }
                    }
                }

                // --- STONE ---
                else if (me == CellType.Stone) {
                    int by = y + 1;
                    int i = idx(x, y);
                    int vel = stoneVelocity[i];

                    if (inb(x, by) && grid[idx(x, by)] == CellType.Empty) {
                        // accelerate when falling
                        stoneVelocity[idx(x, by)] = Math.min(vel + 1, 12);
                        stoneVelocity[i] = 0;
                        swapCell(x, y, x, by);
                    } else if (inb(x, by) && grid[idx(x, by)] == CellType.Water) {
                        // displace water, lose some velocity
                        stoneVelocity[idx(x, by)] = Math.max(vel - 2, 0);
                        stoneVelocity[i] = 0;
                        swapCell(x, y, x, by);
                    } else if (inb(x, by) && grid[idx(x, by)] == CellType.Sand) {
                        if (vel >= 5) {
                            stoneVelocity[idx(x, by)] = Math.max(vel - 3, 0);
                            stoneVelocity[i] = 0;
                            swapCell(x, y, x, by);
                        } else {
                            stoneVelocity[i] = 0;
                            tryStoneRoll(x, y, vel);
                        }
                    } else {
                        stoneVelocity[i] = 0;
                        tryStoneRoll(x, y, vel);
                    }
                }

                // --- OIL ---
                else if (me == CellType.Oil) {
                    int i = idx(x, y);
                    oilAge[i]++;

                    int by = y + 1;
                    int above = y - 1;
                    // If oil somehow ends up under water, push it upward.
                    if (inb(x, above) && grid[idx(x, above)] == CellType.Water) {
                        swapCell(x, y, x, above);
                        continue;
                    }

                    // If below empty and not sitting on water, oil falls slowly (viscous)
                    if (inb(x, by) && grid[idx(x, by)] == CellType.Empty) {
                        // Fall with lower probability than sand (viscous)
                        if (Math.random() < 0.6)
                            swapCell(x, y, x, by);
                        continue;
                    }

                    // Spread horizontally faster than water (thin surface layer)
                    // attempt multi-cell spread to create thin slicks
                    if (Math.random() < 0.85) {
                        int dir = Math.random() < 0.5 ? -1 : 1;
                        if (inb(x + dir, y) && grid[idx(x + dir, y)] == CellType.Empty) {
                            swapCell(x, y, x + dir, y);
                            continue;
                        } else if (inb(x - dir, y) && grid[idx(x - dir, y)] == CellType.Empty) {
                            swapCell(x, y, x - dir, y);
                            continue;
                        }
                    }
                    // small chance to try diagonal down as fallback
                    int dir = Math.random() < 0.5 ? -1 : 1;
                    if (inb(x + dir, by) && grid[idx(x + dir, by)] == CellType.Empty && Math.random() < 0.25) {
                        swapCell(x, y, x + dir, by);
                    }
                }

                // --- FIRE ---
                else if (me == CellType.Fire) {
                    int i = idx(x, y);

                    // Decrease burn timer first
                    burnTimer[i] = Math.max(0, burnTimer[i] - 1);

                    // Only allow rising after fire has burned for several ticks AND has low burn
                    // timer
                    // This ensures newly placed fire stays put for multiple frames
                    if (burnTimer[i] <= 15) { // Only rise when burn timer is quite low
                        int above = y - 1;
                        if (inb(x, above) && grid[idx(x, above)] == CellType.Empty) {
                            swapCell(x, y, x, above);
                            continue; // skip spreading this tick
                        }
                    }

                    // Spread to neighbors (same as before)
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0)
                                continue;
                            int nx = x + dx, ny = y + dy;
                            if (!inb(nx, ny))
                                continue;
                            CellType target = grid[idx(nx, ny)];

                            if (target == CellType.Oil) {
                                if (Math.random() < 0.4) { // Reduced from 0.9 to 0.4 for slower spread
                                    setCell(nx, ny, CellType.Fire);
                                    burnTimer[idx(nx, ny)] += 6 + (int) (Math.random() * 6);
                                }
                            } else if (target == CellType.Sand) {
                                if (Math.random() < 0.06) {
                                    setCell(nx, ny, CellType.Metal);
                                }
                            } else if (target == CellType.Water) {
                                if (Math.random() < 0.6) {
                                    setCell(x, y, CellType.Empty);
                                    continue;
                                }
                            }
                        }
                    }

                    if (burnTimer[i] <= 0) {
                        setCell(x, y, CellType.Empty);
                    }
                }

                // other types (Empty, Metal) -> no action
            }
        }
    }

    private boolean canSandFall(int x, int y) {
        return inb(x, y) && (grid[idx(x, y)] == CellType.Empty || grid[idx(x, y)] == CellType.Water);
    }

    private void tryStoneRoll(int x, int y, int vel) {
        int by = y + 1;

        boolean canRollLeft = inb(x - 1, by) &&
                (grid[idx(x - 1, by)] == CellType.Empty || grid[idx(x - 1, by)] == CellType.Water);
        boolean canRollRight = inb(x + 1, by) &&
                (grid[idx(x + 1, by)] == CellType.Empty || grid[idx(x + 1, by)] == CellType.Water);

        boolean leftHasFallSpace = canRollLeft && inb(x - 1, by + 1) &&
                (grid[idx(x - 1, by + 1)] == CellType.Empty || grid[idx(x - 1, by + 1)] == CellType.Water);
        boolean rightHasFallSpace = canRollRight && inb(x + 1, by + 1) &&
                (grid[idx(x + 1, by + 1)] == CellType.Empty || grid[idx(x + 1, by + 1)] == CellType.Water);

        double rollProbability = Math.min(0.85, 0.25 + (vel * 0.1));

        if (leftHasFallSpace && rightHasFallSpace) {
            if (Math.random() < rollProbability) {
                int dir = Math.random() < 0.5 ? -1 : 1;
                stoneVelocity[idx(x + dir, by)] = Math.max(1, vel - 1);
                stoneVelocity[idx(x, y)] = 0;
                swapCell(x, y, x + dir, by);
            }
        } else if (leftHasFallSpace && Math.random() < rollProbability) {
            stoneVelocity[idx(x - 1, by)] = Math.max(1, vel - 1);
            stoneVelocity[idx(x, y)] = 0;
            swapCell(x, y, x - 1, by);
        } else if (rightHasFallSpace && Math.random() < rollProbability) {
            stoneVelocity[idx(x + 1, by)] = Math.max(1, vel - 1);
            stoneVelocity[idx(x, y)] = 0;
            swapCell(x, y, x + 1, by);
        } else if (canRollLeft && Math.random() < rollProbability * 0.45) {
            stoneVelocity[idx(x - 1, by)] = 0;
            stoneVelocity[idx(x, y)] = 0;
            swapCell(x, y, x - 1, by);
        } else if (canRollRight && Math.random() < rollProbability * 0.45) {
            stoneVelocity[idx(x + 1, by)] = 0;
            stoneVelocity[idx(x, y)] = 0;
            swapCell(x, y, x + 1, by);
        }
    }

    private void draw() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw grid
        for (int y = 0; y < GRID_H; y++) {
            for (int x = 0; x < GRID_W; x++) {
                gc.setFill(colorFor(grid[idx(x, y)]));
                gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void handleMouseInput() {
        if (mouseDown && mouseY >= 0 && mouseY < GRID_H * CELL_SIZE) {
            int gx = (int) (mouseX / CELL_SIZE);
            int gy = (int) (mouseY / CELL_SIZE);
            for (int oy = -brush; oy <= brush; oy++) {
                for (int ox = -brush; ox <= brush; ox++) {
                    int x = gx + ox, y = gy + oy;
                    if (inb(x, y) && ox * ox + oy * oy <= brush * brush) {
                        setCell(x, y, selected);
                    }
                }
            }
        }
    }

    private void setupEventHandlers() {
        // Mouse events
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

        // Keyboard events
        canvas.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case DIGIT1:
                    selected = CellType.Sand;
                    break;
                case DIGIT2:
                    selected = CellType.Water;
                    break;
                case DIGIT3:
                    selected = CellType.Stone;
                    break;
                case DIGIT4:
                    selected = CellType.Metal;
                    break;
                case DIGIT5:
                    selected = CellType.Fire;
                    break;
                case DIGIT6:
                    selected = CellType.Oil;
                    break;
                case UP:
                    brush = Math.min(30, brush + 1);
                    break;
                case DOWN:
                    brush = Math.max(1, brush - 1);
                    break;
                default:
                    break;
            }
            updateButtonAppearance();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
