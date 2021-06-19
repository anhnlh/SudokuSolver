package visualization;

import solving.Configuration;
import solving.SudokuConfig;

import java.io.IOException;
import java.util.Optional;

public class SudokuModel implements Runnable {
    private SudokuConfig config;

    private SudokuVisualize front;

    private String file;

    public SudokuModel(String filename) {
        file = filename;
        try {
            this.config = new SudokuConfig(filename);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void addFront(SudokuVisualize front) {
        this.front = front;
    }

    public char[][] getBoard() {
        return config.getBoard();
    }

    public Optional<Configuration> solve(Configuration config) {
        if (config.isGoal()) {
            updateBoard(true);
            return Optional.of(config);
        } else {
            for (Configuration c : config.getSuccessor()) {
                if (c.isValid()) {
                    this.config = new SudokuConfig((SudokuConfig) c);  // for visualization
                    Optional<Configuration> sol = solve(c);
                    if (sol.isPresent()) {
                        return sol;
                    }
                }
            }
        }
        return Optional.empty();
    }

    private void updateBoard(boolean solved) {
        front.update(solved);
    }

    public void load(String filename) {
        try {
            config = new SudokuConfig(filename);
            file = filename;
            updateBoard(false);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void reset() {
        load(file);
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
        solve(config);
    }
}
