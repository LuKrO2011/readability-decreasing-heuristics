import java.nio.file.Path;

public class NameConflicts {

    private static final int field;
    private static final int f0;
    public Path method() {
        int variable = 1 + f0;
        return null;
    }
    public Path m0() {
        int variable = 1 + f0;
        return null;
    }
}