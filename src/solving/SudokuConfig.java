package solving;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SudokuConfig implements Configuration {

    public static int DIM = 9;

    public static char EMPTY = '0';

    private char[][] board = new char[DIM][DIM];

    private int row;

    private int col;

    public char[][] getBoard() {
        return board;
    }

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

    public SudokuConfig(List<String> customNumbers) {
        for (int i = 0; i < DIM; i++) {
            // populates board with given string
            char[] row = customNumbers.get(i).replaceAll("\\s+", "").toCharArray();
            System.arraycopy(row, 0, board[i], 0, DIM);
        }

        row = 0;
        col = -1;
    }

    public SudokuConfig() {
        for (int i = 0; i < DIM; i++) {
            // initializes empty board
            char[] emptyBoard = "000000000".toCharArray();
            System.arraycopy(emptyBoard, 0, board[i], 0, DIM);
        }

        row = 0;
        col = -1;
    }

    private SudokuConfig(SudokuConfig other) {
        row = other.row;
        col = other.col;

        board = new char[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, DIM);
        }
    }

    public SudokuConfig copyConfig(SudokuConfig other) {
        return new SudokuConfig(other);
    }

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

    private List<Character> getCandidates() {
        // all possible candidates
        List<Character> cand = new LinkedList<>(List.of('1', '2', '3', '4', '5', '6', '7', '8', '9'));

        for (int i = 0; i < DIM; i++) {
            // check horizontal
            if (cand.contains(board[row][i])) {
                cand.remove((Character) board[row][i]);
            }
            // check vertical
            if (cand.contains(board[i][col])) {
                cand.remove((Character) board[i][col]);
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

                if (cand.contains(board[row + rowLoc][col + colLoc])) {
                    cand.remove((Character) board[row + rowLoc][col + colLoc]);
                }
                colLoc++;
            }

            rowLoc++;
            // reset column values for each row
            colLoc = colSave;
            j = 0;
        }

        Collections.shuffle(cand);

        return cand;
    }

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

    @Override
    public boolean isValid() {
        if (row % 3 == 2 && col % 3 == 2 && !checkSquare()) {
            return false;
        }
        return checkStraight();
    }

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
