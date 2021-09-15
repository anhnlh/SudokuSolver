package solving;

import java.util.Collection;

/**
 * Configuration interface for the backtracking algorithm.
 *
 * @author Anh Nguyen
 */
public interface Configuration {

    /**
     * Checks if the configuration is valid or not.
     *
     * @return true if valid
     */
    boolean isValid();

    /**
     * Gets all the possible moves (successors) for the backtracking algorithm.
     *
     * @return a Collection of the possible Configurations
     */
    Collection<Configuration> getSuccessor();

    /**
     * Checks if it is the end of the backtracking algorithm.
     *
     * @return true if the algorithm has reached the end
     */
    boolean isGoal();
}
