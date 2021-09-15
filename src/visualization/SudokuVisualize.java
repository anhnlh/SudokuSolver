package visualization;

import javafx.application.Platform;
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

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The View and Controller in MVC.
 * Creates the SudokuSolver GUI, where all the visualization happens.
 *
 * @author Anh Nguyen
 */
public class SudokuVisualize extends Application {
    /**
     * Image of an empty cell
     */
    private final Image none = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/none.png")));

    /**
     * Image of number 1
     */
    private final Image one = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/one.png")));

    /**
     * Image of number 2
     */
    private final Image two = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/two.png")));

    /**
     * Image of number 3
     */
    private final Image three = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/three.png")));

    /**
     * Image of number 4
     */
    private final Image four = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/four.png")));

    /**
     * Image of number 5
     */
    private final Image five = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/five.png")));

    /**
     * Image of number 6
     */
    private final Image six = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/six.png")));

    /**
     * Image of number 7
     */
    private final Image seven = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/seven.png")));

    /**
     * Image of number 8
     */
    private final Image eight = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/eight.png")));

    /**
     * Image of number 9
     */
    private final Image nine = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/nine.png")));

    /**
     * Image of the logo
     */
    private final Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/logo.png")));

    /**
     * Image of a vertical divider (3x3 regions)
     */
    private final Image VDivider = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/vertical.png")));

    /**
     * Image of a horizontal divider (3x3 regions)
     */
    private final Image HDivider = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/horizontal.png")));

    /**
     * HashMap that maps the numbers to their respective Images
     */
    private final HashMap<Integer, Image> graphicMap = new HashMap<>();

    /**
     * List of the individual cells of the board
     */
    private final List<ImageView> cellList = new LinkedList<>();

    /**
     * Value that indicates the solving of the model
     */
    private boolean solving;

    /**
     * Status text to be updated in different functions
     */
    private Label statusLabel;

    /**
     * The model to work with MVC
     */
    private SudokuModel model;

    /**
     * The primary window of the application
     */
    private final Stage customizeWindow = new Stage();

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initializes the model and adds the front to it.
     */
    @Override
    public void init() {
        statusLabel = new Label();
        statusLabel.setFont(new Font(20));
        statusLabel.setPadding(new Insets(5, 0, 0, 5));
        if (getParameters().getRaw().size() > 0) {
            String filename = getParameters().getRaw().get(0);
            String[] filenameArray = filename.split("/");
            setStatus("Loaded file: " + filenameArray[filenameArray.length - 1]);

            model = new SudokuModel(filename);
            model.addFront(this);
        } else {
            model = new SudokuModel();
            model.addFront(this);
            setStatus("No file entered, empty board generated instead.");
        }
        makeGraphicMap();
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    /**
     * Creates a map that maps the numbers to the respective images.
     */
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

    /**
     * Sets the image of a cell in the Sudoku board
     *
     * @param cell the image to be set
     * @param row  row of the cell
     * @param col  column of the cell
     */
    private void setCellGraphic(ImageView cell, int row, int col) {
        char[][] board = model.getBoard();
        cell.setImage(graphicMap.get(Character.getNumericValue(board[row][col])));
    }

    /**
     * Creates the grid representing the Sudoku board
     *
     * @param bp BorderPane for the grid to be added on
     */
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

    /**
     * Error dialog box with a given error message
     *
     * @param error error message
     */
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

    /**
     * Creates a new window for customization.
     * <p>
     * Can customize by entering numbers for each individual cell,
     * pasting a Sudoku board in plain text, or randomizing.
     * <p>
     * Entering and randomizing options have a mini grid
     * of TextFields to hold each cell's number.
     * <p>
     * Long function due to extensive features.
     */
    private void customizeBoard() {
        FlowPane optionPane = new FlowPane();
        optionPane.setAlignment(Pos.CENTER);
        optionPane.setPadding(new Insets(10, 0, 0, 0));
        optionPane.setHgap(30);

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

        LinkedList<TextField> textFieldList = new LinkedList<>();
        GridPane customGrid = new GridPane();
        char[][] currentBoard = model.getBoard();

        for (int row = 0; row < SudokuConfig.DIM; row++) {
            for (int col = 0; col < SudokuConfig.DIM; col++) {
                TextField tf = new TextField();
                tf.setText(currentBoard[row][col] != '0' ? String.valueOf(currentBoard[row][col]) : "");
                tf.setAlignment(Pos.CENTER);
                tf.setMaxWidth(25);
                // limit to 1 digit
                tf.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches(".?") ? c : null));
                customGrid.add(tf, col, row);

                textFieldList.add(tf);
            }
        }

        Button clearGridButton = new Button("Clear");
        clearGridButton.setFont(new Font(20));
        clearGridButton.setOnAction(e -> {
            for (TextField tf : textFieldList) {
                tf.setText("");
            }
        });

        optionPane.getChildren().addAll(customGrid, clearGridButton);

        Button randomButton = new Button("Go!");
        randomButton.setFont(new Font(20));
        randomButton.setDisable(true);
        randomButton.setOnAction(e -> {
            try {
                int numDelete = Integer.parseInt(numOfRandom.getText());
                if (numDelete <= SudokuConfig.DIM * SudokuConfig.DIM) {
                    SudokuModel solvedModel = new SudokuModel();
                    solvedModel.solve();
                    char[][] solvedBoard = solvedModel.getBoard();

                    Random rand = new Random();

                    // replace numDelete numbers on the board
                    for (int i = 0; i < SudokuConfig.DIM * SudokuConfig.DIM - numDelete; i++) {
                        while (true) {
                            int row = rand.nextInt(SudokuConfig.DIM);
                            int col = rand.nextInt(SudokuConfig.DIM);
                            if (solvedBoard[row][col] != '0') {
                                solvedBoard[row][col] = '0';
                                break;
                            }
                        }
                    }

                    // setting text of TextFields on the grid
                    int tfLoc = 0;
                    for (char[] row : solvedBoard) {
                        for (char c : row) {
                            String val;
                            val = c != '0' ? String.valueOf(c) : "";
                            textFieldList.get(tfLoc).setText(val);
                            tfLoc++;
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                errorPopUp("Needs to be a number!");
            }
        });

        // radio button choice listener
        group.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            RadioButton rb = (RadioButton) group.getSelectedToggle();
            String choice = rb.getText();
            if ("Randomize numbers".equals(choice)) {
                randomize(numOfRandom, optionPane, customGrid, clearGridButton);
            } else if ("Paste numbers".equals(choice)) {
                paste(numOfRandom, optionPane, customBoard, randomButton);
            } else {
                enter(numOfRandom, optionPane, customGrid, randomButton, clearGridButton);
            }
        });

        // random num field listener to enable random button
        numOfRandom.textProperty().addListener((observableValue, s, t1) -> randomButton.setDisable(t1.equals("") && radioRandom.isSelected()));
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
            boolean error = false;

            List<String> customNumbers = new LinkedList<>();
            if (radioPaste.isSelected()) {
                customNumbers = Arrays.asList(customBoard.getText().split("\n"));
                try {
                    for (int i = 0; i < SudokuConfig.DIM; i++) {
                        String row = customNumbers.get(i);

                        // if the pasted has periods to separate columns
                        if (row.contains(".")) {
                            row = row.replaceAll("\\.", "0");
                        }

                        Pattern pattern = Pattern.compile("[^\\d]");
                        Matcher matcher = pattern.matcher(row);
                        long matches = matcher.results().count();
                        if (matches != 0) {
                            errorPopUp("Sudoku does not allow characters!");
                            error = true;
                            break;
                        }

                        if (row.length() < SudokuConfig.DIM) {
                            while (row.length() != SudokuConfig.DIM) {
                                customNumbers.set(i, row + "0");
                                row = customNumbers.get(i);
                            }
                        } else if (row.length() > SudokuConfig.DIM) {
                            errorPopUp("Too many numbers on row " + i + "!");
                            error = true;
                            break;
                        }
                    }
                } catch (IndexOutOfBoundsException ioobe) {
                    errorPopUp("Not enough number of rows");
                    error = true;
                }
            } else {
                error = extractTextFields(textFieldList, customNumbers);
                if (error) {
                    errorPopUp("Sudoku does not allow characters!");
                }
            }
            // loads the given board
            if (!error) {
                model.load(listTo2DArray(customNumbers));
                setStatus("Custom Sudoku board generated");
                customizeWindow.close();
            }
        });

        Button cancelCustom = new Button("Cancel");
        cancelCustom.setFont(new Font(20));
        cancelCustom.setOnAction(e -> customizeWindow.close());

        Button saveCustom = new Button("Save to file");
        saveCustom.setFont(new Font(20));
        saveCustom.setOnAction(e -> {

            FileChooser fc = new FileChooser();
            fc.setTitle("Save your sudoku board");
            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Text Documents (*.txt)", "*.txt");
            fc.getExtensionFilters().add(extFilter);

            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "data";
            fc.setInitialDirectory(new File(currentPath));

            //Show save file dialog
            File file = fc.showSaveDialog(customizeWindow);
            if (file != null) {
                List<String> customNumbers = new LinkedList<>();
                boolean error = extractTextFields(textFieldList, customNumbers);
                if (!error) {
                    saveBoard(listTo2DArray(customNumbers), file);
                } else {
                    errorPopUp("Sudoku does not allow characters!\nFile was not saved.");
                }
            }
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

    /**
     * Extract the text from all TextFields of the grid to a
     * List of Strings of each row (i.e length of list will be {@link SudokuConfig#DIM}
     *
     * @param textFieldList List of all TextFields
     * @param customNumbers List of String row representation
     * @return true if there were characters instead of numbers in a cell else false.
     */
    private boolean extractTextFields(LinkedList<TextField> textFieldList, List<String> customNumbers) {
        StringBuilder row = new StringBuilder();
        for (TextField tf : textFieldList) {
            if (tf.getText().matches("[^\\d]")) {
                return true;
            }
            row.append(tf.getText().matches("\\d") ? tf.getText() : "0");

            // adds to list of string when row has SudokuConfig.DIM numbers
            if (row.length() == SudokuConfig.DIM) {
                customNumbers.add(row.toString());
                row = new StringBuilder();
            }
        }
        return false;
    }

    /**
     * Runs when Enter radio button is selected.
     * Disables the random number field and button. Hides the
     * TextArea to show the TextField grid.
     *
     * @param randNum         random number field
     * @param optionPane      side pane
     * @param customGrid      TextField grid
     * @param randomButton    random button
     * @param clearGridButton clear grid button
     */
    private void enter(TextField randNum, FlowPane optionPane, GridPane customGrid, Button randomButton, Button clearGridButton) {
        randNum.setDisable(true);
        randomButton.setDisable(true);
        optionPane.getChildren().clear();
        optionPane.getChildren().addAll(customGrid, clearGridButton);
    }

    /**
     * Runs when Paste radio button is selected.
     * Disables the random number field and button. Hides the
     * TextField grid to show the TextArea for pasting text.
     *
     * @param randNum      random number field
     * @param optionPane   side pane
     * @param customBoard  TextField grid
     * @param randomButton random button
     */
    private void paste(TextField randNum, FlowPane optionPane, TextArea customBoard, Button randomButton) {
        randNum.setDisable(true);
        randomButton.setDisable(true);
        optionPane.getChildren().clear();
        optionPane.getChildren().add(customBoard);
    }

    /**
     * Runs when Randomize radio button is selected.
     * Enables the random number field, but does not enable
     * the button because that's done by a listener in {@link SudokuVisualize#customizeBoard()}
     *
     * @param randNum         random number field
     * @param optionPane      side pane
     * @param customGrid      TextField grid
     * @param clearGridButton clear grid button
     */
    private void randomize(TextField randNum, FlowPane optionPane, GridPane customGrid, Button clearGridButton) {
        randNum.setDisable(false);
        optionPane.getChildren().clear();
        optionPane.getChildren().addAll(customGrid, clearGridButton);
    }

    /**
     * Method used when saving the custom Sudoku board.
     * Writes to a file.
     *
     * @param boardContent content
     * @param file         given file
     */
    private void saveBoard(char[][] boardContent, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new PrintWriter(file));
            for (char[] row : boardContent) {
                for (int i = 0; i < SudokuConfig.DIM; i++) {
                    String line = i != SudokuConfig.DIM - 1 ? row[i] + " " : String.valueOf(row[i]);
                    writer.write(line);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException ex) {
            errorPopUp(ex.getMessage());
        }
    }

    /**
     * Useful method to convert a list of String to a 2D char array.
     *
     * @param list given list
     * @return 2D char array
     */
    private char[][] listTo2DArray(List<String> list) {
        char[][] result = new char[SudokuConfig.DIM][SudokuConfig.DIM];

        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i).toCharArray();
        }

        return result;
    }

    /**
     * Starts the JavaFX Application.
     * Creates the SudokuSolver 3000 GUI.
     * <p>
     * Features include visualizing the backtracking algorithm
     * used to solve the Sudoku board, customizing the Sudoku
     * board, loading files of the board, and resetting the puzzle.
     *
     * @param stage the main window
     */
    @Override
    public void start(Stage stage) {
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

        load.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose data file for Sudoku puzzle");
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "data";
            fc.setInitialDirectory(new File(currentPath));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                model.load("data/" + file.getName());
                setStatus("Loaded file: " + file.getName());
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
                Thread t = new Thread(model);
                t.start();
                while (solving) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(() -> update(false));
                }
            });
            thread.start();
        });

        Button customize = new Button("Customize Sudoku board");
        customize.setFont(new Font(20));
        customize.setOnAction(e -> customizeBoard());

        vb1.getChildren().addAll(visualize, customize);

        leftPanel.setCenter(vb1);

        vb.getChildren().addAll(statusLabel, hb);
        vb.setAlignment(Pos.CENTER);
        leftPanel.setBottom(vb);

        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(logo);
        stage.setTitle("SudokuSolver 3000");
        stage.setOnCloseRequest(e -> {
            solving = false;
            customizeWindow.close();
        });
        stage.show();
    }

    /**
     * Updates the board with new values from the model.
     *
     * @param solved the solved
     */
    public void update(boolean solved) {
        int i = 0;
        for (int row = 0; row < SudokuConfig.DIM; row++) {
            for (int col = 0; col < SudokuConfig.DIM; col++) {
                ImageView cell = cellList.get(i);
                setCellGraphic(cell, row, col);
                i++;
            }
        }
        this.solving = !solved;
    }

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }
}