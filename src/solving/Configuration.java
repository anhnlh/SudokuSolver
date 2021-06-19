package solving;

import java.util.Collection;

public interface Configuration {

    boolean isValid();

    Collection<Configuration> getSuccessor();

    boolean isGoal();
}
