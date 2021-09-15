package solving;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The configuration of a Sudoku board.
 * Only works with {@link visualization.SudokuModel}
 *
 * @author Anh Nguyen
 */
public class SudokuConfig implements Configuration {

    /**
     * Dimension of the Sudoku board
     */
    public static int DIM = 9;

    /**
     * Representation of an empty cell is '0'
     */
    public static char EMPTY = '0';

    /**
     * 2D char array representation of the Sudoku board
     */
    private char[][] board = new char[DIM][DIM];

    /**
     * The current row
     */
    private int row;

    /**
     * The current column
     */
    private int col;

    /**
     * Gets the board.
     *
     * @return 2D char array of the board
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Creates a new Configuration by reading a given file.
     *
     * @param filename given filename
     * @throws IOException throws an error if the file doesn't exist
     */
    public SudokuConfig(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        for (int i = 0; i < DIM; i++) {
            // removes whitespaces with replaceAll
            char[] read = in.readLine().replaceAll("\\s+", "").toCharArray();
            System.arraycopy(read, 0, board[i], 0, DIM);
        }

        in.close();

        row = 0;
        col = -1;
    }

    /**
     * Creates a new Configuration with a given 2D char array.
     * Only used by customizing features
     *
     * @param givenBoard 2D char array of a pre-made Sudoku board
     */
    public SudokuConfig(char[][] givenBoard) {
        for (int i = 0; i < givenBoard.length; i++) {
            System.arraycopy(givenBoard[i], 0, board[i], 0, DIM);
        }

        row = 0;
        col = -1;
    }

    /**
     * Creates a new Configuration with an empty board (i.e all zeros).
     */
    public SudokuConfig() {
        for (int i = 0; i < DIM; i++) {
            // initializes empty board
            char[] emptyBoard = "000000000".toCharArray();
            System.arraycopy(emptyBoard, 0, board[i], 0, DIM);
        }

        row = 0;
        col = -1;
    }

    /**
     * Copies a Configuration.
     * Only used by the backtracking algorithm.
     *
     * @param other the Configuration to be copied to
     */
    private SudokuConfig(SudokuConfig other) {
        row = other.row;
        col = other.col;

        board = new char[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, DIM);
        }
    }

    /**
     * Public helper method that calls the copy method.
     *
     * @param other the Configuration to be copied to
     * @return a new SudokuConfig
     */
    public SudokuConfig copyConfig(SudokuConfig other) {
        return new SudokuConfig(other);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean isGoal() {
        int sum = 0;
        for (Character c : board[DIM - 1]) {
            sum += Character.getNumericValue(c);
        }
        // sum from 1 to 9 is 45, means filled
        // would be funny if the last row is already filled from the beginning
        return sum == 45;
    }

    /**
     * Validates the successor in a straight line (vertical and horizontal).
     *
     * @return true if valid
     */
    private boolean checkStraight() {
        char num = board[row][col];
        for (int i = 0; i < DIM; i++) {
            if (i != col && board[row][i] == num) {
                return false;
            }
            if (i != row && board[i][col] == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the successor within a square of size root of {@link #DIM}.
     *
     * @return true if valid
     */
    private boolean checkSquare() {
        Set<Character> squareSet = new HashSet<>();
        int rowLoc = row % 3;
        int colLoc = col % 3;
        int rowSave = rowLoc;
        int colSave = colLoc;

        int i = 0;
        int j = 0;
        // loop 3 times
        while (i++ < 3) {
            // index 2 of square wraps back to index 0
            if (rowLoc + 1 == 3) {
                rowLoc = -rowSave;
            }

            // loop 3 times
            while (j++ < 3) {
                if (rowLoc == 0 && colLoc == 0) continue;

                // index 2 of square wraps back to index 0
                if (colLoc + 1 == 3) {
                    colLoc = -colSave;
                }

                char cell = board[row + rowLoc][col + colLoc];
                if (!squareSet.isEmpty() && squareSet.contains(cell)) {
                    return false;
                }
                squareSet.add(cell);
                colLoc++;
            }

            rowLoc++;
            // reset column values for each row
            colLoc = colSave;
            j = 0;
        }
        return true;
    }

    /**
     * Gets the candidates that could be successors
     *
     * @return a List of char of candidates
     */
    private List<Character> getCandidates() {
        // all possible candidates
        List<Character> candidates = new LinkedList<>(List.of('1', '2', '3', '4', '5', '6', '7', '8', '9'));

        for (int i = 0; i < DIM; i++) {
            // check horizontal
            if (candidates.contains(board[row][i])) {
                candidates.remove((Character) board[row][i]);
            }
            // check vertical
            if (candidates.contains(board[i][col])) {
                candidates.remove((Character) board[i][col]);
            }
        }

        // check 3x3 squares
        int rowLoc = row % 3;
        int colLoc = col % 3;
        int rowSave = rowLoc;
        int colSave = colLoc;

        int i = 0;
        int j = 0;
        // loop 3 times
        while (i++ < 3) {
            // index 2 of square wraps back to index 0
            if (rowLoc + 1 == 3) {
                rowLoc = -rowSave;
            }

            // loop 3 times
            while (j++ < 3) {
                if (rowLoc == 0 && colLoc == 0) continue;

                // index 2 of square wraps back to index 0
                if (colLoc + 1 == 3) {
                    colLoc = -colSave;
                }

                if (candidates.contains(board[row + rowLoc][col + colLoc])) {
                    candidates.remove((Character) board[row + rowLoc][col + colLoc]);
                }
                colLoc++;
            }

            rowLoc++;
            // reset column values for each row
            colLoc = colSave;
            j = 0;
        }

        Collections.shuffle(candidates);

        return candidates;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Collection<Configuration> getSuccessor() {
        Collection<Configuration> suc = new LinkedHashSet<>();

        if (++col == DIM) {
            col = 0;
            row++;
        }

        if (board[row][col] == EMPTY) {
            for (char c : getCandidates()) {
                SudokuConfig child = new SudokuConfig(this);
                child.board[row][col] = c;
                suc.add(child);
            }
        } else {
            SudokuConfig filled = new SudokuConfig(this);
            suc.add(filled);
        }

        return suc;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean isValid() {
        if (row % 3 == 2 && col % 3 == 2 && !checkSquare()) {
            return false;
        }
        return checkStraight();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM - 1; j++) {
                out.append(board[i][j]).append(" ");
                if (j != DIM - 1 && j % 3 == 2) {
                    out.append("| ");
                }
            }
            out.append(board[i][DIM - 1]).append("\n");
            if (i != DIM - 1 && i % 3 == 2) {
                out.append("---------------------\n");
            }
        }

        return out.toString();
    }
}
