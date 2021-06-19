package solving;

import java.io.IOException;
import java.util.Optional;

public class SudokuMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java solving.SudokuMain input-file");
        }
        try {
            SudokuConfig puzzle = new SudokuConfig(args[0]);
            System.out.println("Initial config:");
            System.out.println(puzzle);

            // start the clock
            double start = System.currentTimeMillis();

            // attempt to solve
            Optional<Configuration> sol = solve(puzzle);

            // compute the elapsed time
            System.out.println("Elapsed time: " +
                    (System.currentTimeMillis() - start) / 1000.0 + " seconds.");

            if (sol.isPresent()) {
                System.out.println("Solution:");
                System.out.println(sol.get());
            } else {
                System.out.println("No Solution!");
            }

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static Optional<Configuration> solve(Configuration config){
        if (config.isGoal()) {
            return Optional.of(config);
        } else {
            for (Configuration c : config.getSuccessor()) {
                if (c.isValid()) {
                    Optional<Configuration> sol = solve(c);
                    if (sol.isPresent()) {
                        return sol;
                    }
                }
            }
        }
        return Optional.empty();
    }
}
