package visualization;

import solving.Configuration;
import solving.SudokuConfig;

import java.io.IOException;
import java.util.Optional;

public class SudokuModel implements Runnable {
    private SudokuConfig config;

    private SudokuVisualize front;

    private char[][] board;

    public SudokuModel(String filename) {
        try {
            this.config = new SudokuConfig(filename);
            this.board = config.getBoard();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public SudokuModel() {
        this.config = new SudokuConfig();
        this.board = config.getBoard();
    }

    public void addFront(SudokuVisualize front) {
        this.front = front;
    }

    public char[][] getBoard() {
        return config.getBoard();
    }

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

    public void solve() {
        solve(config);
    }

    private void updateBoard(boolean solved) {
        if (front != null) {
            front.update(solved);
        }
    }

    public void load(String filename) {
        try {
            config = new SudokuConfig(filename);
            board = config.getBoard();
            updateBoard(false);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void load(char[][] customNumbers) {
        config = new SudokuConfig(customNumbers);
        board = config.getBoard();
        updateBoard(false);
    }

    public void reset() { // redid reset so it doesn't rely on file
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
