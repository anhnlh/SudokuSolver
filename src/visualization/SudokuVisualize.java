package visualization;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import solving.SudokuConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SudokuVisualize extends Application {
    private final Image none = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/none.png")));

    private final Image one = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/one.png")));

    private final Image two = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/two.png")));

    private final Image three = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/three.png")));

    private final Image four = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/four.png")));

    private final Image five = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/five.png")));

    private final Image six = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/six.png")));

    private final Image seven = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/seven.png")));

    private final Image eight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/eight.png")));

    private final Image nine = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/nine.png")));

    private final Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/logo.png")));

    private final Image VDivider = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/vertical.png")));

    private final Image HDivider = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/horizontal.png")));

    private final HashMap<Integer, Image> graphicMap = new HashMap<>();

    private final List<ImageView> cellList = new LinkedList<>();

    private boolean solving;

    private Label loadStatus;

    private SudokuModel model;

    private final Stage customizeWindow = new Stage();

    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void init() throws Exception {
        loadStatus = new Label();
        loadStatus.setFont(new Font(20));
        loadStatus.setPadding(new Insets(5, 0, 0, 5));
        if (getParameters().getRaw().size() > 0) {
            String filename = getParameters().getRaw().get(0);
            String[] filenameArray = filename.split("/");
            loadStatus.setText("Loaded file: " + filenameArray[filenameArray.length - 1]);

            model = new SudokuModel(filename);
            model.addFront(this);
        } else {
            model = new SudokuModel();
            model.addFront(this);
            loadStatus.setText("No file entered, empty board generated instead.");
        }
        makeGraphicMap();
    }

    private void makeGraphicMap() {
        graphicMap.put(0, none);
        graphicMap.put(1, one);
        graphicMap.put(2, two);
        graphicMap.put(3, three);
        graphicMap.put(4, four);
        graphicMap.put(5, five);
        graphicMap.put(6, six);
        graphicMap.put(7, seven);
        graphicMap.put(8, eight);
        graphicMap.put(9, nine);
    }

    private void setCellGraphic(ImageView cell, int r, int c) {
        char[][] board = model.getBoard();
        cell.setImage(graphicMap.get(Character.getNumericValue(board[r][c])));
    }

    private void makeGrid(BorderPane bp) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 0, 0, 10));

        for (int row = 0; row < SudokuConfig.DIM; row++) {
            for (int col = 0; col < SudokuConfig.DIM; col++) {
                ImageView cell = new ImageView();
                setCellGraphic(cell, row, col);

                grid.add(cell, col, row);
                cellList.add(cell);

                if (row != 0 && row % 3 == 0) {
                    // pad bottom to divide 3x3 squares (row)
                    grid.add(new HBox(new ImageView(HDivider)), col, row);
                }
                if (col != 0 && col % 3 == 0) {
                    grid.add(new VBox(new ImageView(VDivider)), col, row);
                }
            }
        }

        bp.setCenter(grid);
    }

    private void errorPopUp(String error) {
        Stage popUp = new Stage();

        Label message = new Label(error);
        message.setFont(new Font(20));

        Button closeButton = new Button("Close");
        closeButton.setFont(new Font(20));
        closeButton.setOnAction(e -> popUp.close());

        VBox vb = new VBox();
        vb.getChildren().addAll(message, closeButton);
        vb.setSpacing(40);
        vb.setPadding(new Insets(15));
        vb.setAlignment(Pos.CENTER);

        BorderPane background = new BorderPane();
        background.setPrefWidth(400);
        background.setPrefHeight(200);
        background.setCenter(vb);

        Scene scene = new Scene(background);
        popUp.setScene(scene);
        popUp.setTitle("Error");
        popUp.getIcons().add(logo);
        popUp.show();
    }

    private void customizeBoard() {
        FlowPane optionPane = new FlowPane();
        optionPane.setAlignment(Pos.CENTER);
        optionPane.setPadding(new Insets(10, 0, 0, 0));

        ToggleGroup group = new ToggleGroup();

        RadioButton radioEnter = new RadioButton("Enter numbers");
        radioEnter.setFont(new Font(20));
        radioEnter.setToggleGroup(group);
        radioEnter.setSelected(true);

        RadioButton radioPaste = new RadioButton("Paste numbers");
        radioPaste.setFont(new Font(20));
        radioPaste.setToggleGroup(group);

        RadioButton radioRandom = new RadioButton("Randomize numbers");
        radioRandom.setFont(new Font(20));
        radioRandom.setToggleGroup(group);

        Label fieldTitle = new Label("Numbers of cells to be randomly filled (Max: 81):");
        fieldTitle.setFont(new Font(20));
        fieldTitle.setPadding(new Insets(0, 0, 50, 0));

        TextField numOfRandom = new TextField();
        numOfRandom.setFont(new Font("Consolas", 20));
        numOfRandom.setMaxWidth(50);
        numOfRandom.setPrefHeight(44);
        numOfRandom.setDisable(true);
        // limit to 2 digits, thanks stackoverflow
        numOfRandom.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches(".{0,2}") ? c : null));

        TextArea customBoard = new TextArea();
        customBoard.setFont(new Font("Consolas", 20));
        customBoard.setPrefSize(215, 252);

        LinkedList<TextField> gridList = new LinkedList<>();
        GridPane customGrid = new GridPane();
        for (int row = 0; row < SudokuConfig.DIM; row++) {
            for (int col = 0; col < SudokuConfig.DIM; col++) {
                TextField tf = new TextField();
                tf.setMaxWidth(25);
                // limit to 1 digit
                tf.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches(".?") ? c : null));
                customGrid.add(tf, col, row);

                gridList.add(tf);
            }
        }
        optionPane.getChildren().add(customGrid);

        Button randomButton = new Button("Go!");
        randomButton.setFont(new Font(20));
        randomButton.setDisable(true);
        randomButton.setOnAction(e -> {
            try {
                int numDelete = Integer.parseInt(numOfRandom.getText());
                if (numDelete <= 81) {
                    SudokuModel solvedBoard = new SudokuModel();
                    solvedBoard.solve();
                    char[][] board = solvedBoard.getBoard();

                    Random rand = new Random();

                    // replace numDelete numbers on the board
                    for (int i = 0; i < SudokuConfig.DIM * SudokuConfig.DIM - numDelete; i++) {
                        while (true) {
                            int row = rand.nextInt(9);
                            int col = rand.nextInt(9);
                            if (board[row][col] != '0') {
                                board[row][col] = '0';
                                break;
                            }
                        }
                    }

                    // setting text of TextFields on the grid
                    int tfLoc = 0;
                    for (char[] row : board) {
                        for (char c : row) {
                            String val;
                            if (c == '0') {
                                val = " ";
                            } else {
                                val = String.valueOf(c);
                            }
                            gridList.get(tfLoc).setText(val);
                            tfLoc++;
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                errorPopUp("Needs to be a number!");
            }
        });


        // radio button choice listener
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                RadioButton rb = (RadioButton) group.getSelectedToggle();
                String choice = rb.getText();
                if ("Randomize numbers".equals(choice)) {
                    randomize(numOfRandom, customGrid, optionPane);
                } else if ("Paste numbers".equals(choice)) {
                    paste(numOfRandom, customBoard, randomButton, optionPane);
                } else {
                    enter(numOfRandom, customGrid, randomButton, optionPane);
                }
            }
        });

        // random num field listener to enable random button
        numOfRandom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                randomButton.setDisable(t1.equals("") && radioRandom.isSelected());
            }
        });
        HBox numBox = new HBox();
        numBox.setSpacing(5);
        numBox.getChildren().addAll(fieldTitle, numOfRandom, randomButton);

        VBox radioVB = new VBox();
        radioVB.setSpacing(35);
        radioVB.setPadding(new Insets(10));
        radioVB.getChildren().addAll(radioEnter, radioPaste, radioRandom, numBox);

        Button okCustom = new Button("OK");
        okCustom.setFont(new Font(20));
        okCustom.setOnAction(e -> {
            //TODO
            List<String> customNumbers = new LinkedList<>();
            if (radioPaste.isSelected()) {
                Pattern pattern = Pattern.compile("[^\\d]");
                customNumbers = Arrays.asList(customBoard.getText().split("\n"));
                try {
                    for (int i = 0; i < SudokuConfig.DIM; i++) {
                        String row = customNumbers.get(i);
                        if (row.length() < SudokuConfig.DIM) {
                            while (row.length() != SudokuConfig.DIM) {
                                customNumbers.set(i, row + "0");
                                row = customNumbers.get(i);
                            }
                        } else if (row.length() > SudokuConfig.DIM) {
                            errorPopUp("Too many numbers on row " + i + "!");
                            break;
                        }

                        Matcher matcher = pattern.matcher(row);
                        long matches = matcher.results().count();
                        if (matches != 0) {
                            errorPopUp("Sudoku does not allow characters!");
                            break;
                        } else if (i == SudokuConfig.DIM - 1) { // every row works
                            model = new SudokuModel(customNumbers);
                            model.addFront(this);
                            loadStatus.setText("Sudoku board generated");
                        }
                    }
                } catch (IndexOutOfBoundsException ioobe) {
                    errorPopUp("Not enough number of rows");
                }
            } else {
                StringBuilder row = new StringBuilder();
                for (TextField tf : gridList) {
                    row.append(tf.getText().matches("\\d") ? tf.getText() : "0");
                    if (row.length() == SudokuConfig.DIM) {
                        customNumbers.add(row.toString());
                        row = new StringBuilder();
                    }
                }
                model = new SudokuModel(customNumbers);
                model.addFront(this);
                loadStatus.setText("Randomized Sudoku board generated");
            }
        });

        Button cancelCustom = new Button("Cancel");
        cancelCustom.setFont(new Font(20));
        cancelCustom.setOnAction(e -> customizeWindow.close());

        Button saveCustom = new Button("Save to file");
        saveCustom.setFont(new Font(20));
        saveCustom.setOnAction(e -> {
            //TODO
        });

        HBox buttonHB = new HBox();
        buttonHB.setSpacing(30);
        buttonHB.setPadding(new Insets(10));
        buttonHB.setAlignment(Pos.CENTER);
        buttonHB.getChildren().addAll(okCustom, cancelCustom, saveCustom);

        GridPane gridCustom = new GridPane();
        gridCustom.add(radioVB, 0, 0);
        gridCustom.add(optionPane, 1, 0);

        BorderPane bpCustom = new BorderPane();
        bpCustom.setCenter(gridCustom);
        bpCustom.setBottom(buttonHB);
        Scene s = new Scene(bpCustom);
        customizeWindow.setScene(s);
        customizeWindow.getIcons().add(logo);
        customizeWindow.show();
    }

    private void enter(TextField num, GridPane customGrid, Button randomButton, FlowPane optionPane) {
        num.setDisable(true);
        randomButton.setDisable(true);
        optionPane.getChildren().clear();
        optionPane.getChildren().add(customGrid);
    }

    private void paste(TextField num, TextArea customBoard, Button randomButton, FlowPane optionPane) {
        num.setDisable(true);
        randomButton.setDisable(true);
        optionPane.getChildren().clear();
        optionPane.getChildren().add(customBoard);
    }

    private void randomize(TextField num, GridPane customGrid, FlowPane optionPane) {
        num.setDisable(false);
        optionPane.getChildren().clear();
        optionPane.getChildren().add(customGrid);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane border = new BorderPane();
        makeGrid(border);

        BorderPane leftPanel = new BorderPane();

        FlowPane titlePane = new FlowPane();
        titlePane.setAlignment(Pos.CENTER);
        titlePane.setPadding(new Insets(10));

        Label title = new Label("SudokuSolver");
        title.setFont(Font.font(null, FontWeight.BOLD, 48));
        Label funnyNumber = new Label("3000");
        funnyNumber.setFont(Font.font(null, FontWeight.BOLD, 48));

        titlePane.getChildren().addAll(title, funnyNumber);
        leftPanel.setTop(titlePane);

        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(10);

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(0, 0, 15, 3));
        hb.setSpacing(10);

        Button load = new Button("Load new data");
        load.setFont(new Font(20));

        FileChooser fc = new FileChooser();
        fc.setTitle("Choose data file for Sudoku puzzle");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() +
                File.separator + "data";
        fc.setInitialDirectory(new File(currentPath));

        load.setOnAction(e -> {
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                model.load("data/" + file.getName());
                loadStatus.setText("Loaded file: " + file.getName());
            }
            this.solving = true;
        });

        Button reset = new Button("Reset puzzle");
        reset.setFont(new Font(20));

        reset.setOnAction(e -> {
            model.reset();
            this.solving = true;
        });

        hb.getChildren().addAll(load, reset);
        border.setLeft(leftPanel);

        VBox vb1 = new VBox();
        vb1.setAlignment(Pos.CENTER);
        vb1.setSpacing(10);

        Button visualize = new Button("Visualize!");
        visualize.setFont(new Font(20));

        visualize.setOnAction(e -> {
            Thread thread = new Thread(() -> {
                Runnable updater = () -> update(false);
                Thread t = new Thread(model);
                t.start();
                while (solving) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(updater);
                }
            });
            thread.start();
        });

        Button customize = new Button("Customize Sudoku board");
        customize.setFont(new Font(20));
        customize.setOnAction(e -> customizeBoard());

        vb1.getChildren().addAll(visualize, customize);

        leftPanel.setCenter(vb1);

        vb.getChildren().addAll(loadStatus, hb);
        vb.setAlignment(Pos.CENTER);
        leftPanel.setBottom(vb);

        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(logo);
        stage.setTitle("SudokuSolver 3000");
        stage.setOnCloseRequest(e -> customizeWindow.close());
        stage.show();
    }

    @Override
    public void stop() throws Exception {

        super.stop();
    }

    public void update(boolean solved) {
        int i = 0;
        for (int row = 0; row < SudokuConfig.DIM; row++) {
            for (int col = 0; col < SudokuConfig.DIM; col++) {
                ImageView cell = cellList.get(i);
                setCellGraphic(cell, row, col);
                i++;
            }
        }
        if (solved) {
            this.solving = false;
        }
    }
}
