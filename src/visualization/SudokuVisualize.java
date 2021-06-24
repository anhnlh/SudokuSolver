package visualization;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private Label loadedFile;

    private SudokuModel model;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SudokuVisualize filename");
        } else {
            try {
                Application.launch(args);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void init() throws Exception {
        String filename = getParameters().getRaw().get(0);
        String[] filenameArray = filename.split("/");
        loadedFile = new Label();
        loadedFile.setFont(new Font(20));
        loadedFile.setPadding(new Insets(5, 0, 0, 0));
        loadedFile.setText("Loaded file: " + filenameArray[filenameArray.length - 1]);

        model = new SudokuModel(filename);
        model.addFront(this);
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
        grid.setPadding(new Insets(0, 3, 0, 10));

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
        hb.setPadding(new Insets(0, 0, 0, 3));
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
                loadedFile.setText("Loaded file: " + file.getName());
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

        vb1.getChildren().addAll(customize, visualize);

        leftPanel.setCenter(vb1);

        vb.getChildren().addAll(loadedFile, hb);
        leftPanel.setBottom(vb);


        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(logo);
        stage.setTitle("SudokuSolver 3000");
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
