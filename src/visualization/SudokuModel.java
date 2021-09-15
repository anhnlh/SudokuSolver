package visualization;

import solving.Configuration;
import solving.SudokuConfig;

import java.io.IOException;
import java.util.Optional;

/**
 * The model in MVC.
 * Works directly with {@link SudokuConfig} to manipulate the back end
 * of the Sudoku board.
 *
 * @author Anh Nguyen
 */
public class SudokuModel implements Runnable {
    /** Current configuration of the Sudoku board */
    private SudokuConfig config;

    /** The front (View) to perform update calls */
    private SudokuVisualize front;

    /** 2D char array of sudoku board for resetting board purposes */
    private char[][] board;

    /**
     * Instantiates a new Sudoku model with a given filename.
     *
     * @param filename name of the file
     */
    public SudokuModel(String filename) {
        try {
            this.config = new SudokuConfig(filename);
            this.board = config.getBoard();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    /**
     * Instantiates a new Sudoku model with an empty board.
     */
    public SudokuModel() {
        this.config = new SudokuConfig();
        this.board = config.getBoard();
    }

    /**
     * Adds the front to the model.
     *
     * @param front {@link SudokuVisualize}
     */
    public void addFront(SudokuVisualize front) {
        this.front = front;
    }

    /**
     * Gets the 2D char representation of the Sudoku board for
     * certain functionalities in {@link SudokuVisualize}
     *
     * @return 2D char array
     */
    public char[][] getBoard() {
        return config.getBoard();
    }

    /**
     * Backtracking algorithm that solves the Sudoku board.
     * Also calls the update method while passing in true to inform
     * the view that the puzzle is solved.
     *
     * @param config current configuration
     * @return Optional of the config or empty (to avoid null)
     */
    private Optional<Configuration> solve(Configuration config) {
        if (config.isGoal()) {
            updateBoard(true);
            return Optional.of(config);
        } else {
            for (Configuration c : config.getSuccessor()) {
                if (c.isValid()) {
                    SudokuConfig tmp = (SudokuConfig) c;
                    this.config = tmp.copyConfig(tmp);  // for visualization
                    Optional<Configuration> sol = solve(c);
                    if (sol.isPresent()) {
                        return sol;
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Public solve method.
     */
    public void solve() {
        solve(config);
    }

    /**
     * Update method that calls the update function in the view
     * while passing in either true or false representing the
     * solving status of the Sudoku configuration.
     *
     * @param solved solved or not
     */
    private void updateBoard(boolean solved) {
        if (front != null) {
            front.update(solved);
        }
    }

    /**
     * Loads a new Sudoku configuration from a file.
     *
     * @param filename filename
     */
    public void load(String filename) {
        try {
            config = new SudokuConfig(filename);
            board = config.getBoard();
            updateBoard(false);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    /**
     * Loads a new Sudoku configuration from a given 2D char array
     * Only used in {@link SudokuVisualize} when customizing the board
     * by entering or randomizing the board.
     *
     * @param customNumbers the custom numbers
     */
    public void load(char[][] customNumbers) {
        config = new SudokuConfig(customNumbers);
        board = config.getBoard();
        updateBoard(false);
    }

    /**
     * Resets the board configuration.
     */
    public void reset() {
        config = new SudokuConfig(board);
        updateBoard(false);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        solve();
    }
}