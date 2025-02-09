package az.caspian.core.tree.node;

import az.caspian.core.utils.Asserts;
import az.caspian.core.utils.StringUtils;
import com.google.common.base.Objects;

public class DataNodeLocation {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char NULL_CHAR = '\u0000';
    private final String location;
    private final String level;
    private final int order;

    public DataNodeLocation(String level, int order) {
        Asserts.required(level, "node level cannot be null");
        Asserts.checkArgument(order >= 0, "node order cannot be less than 0");

        this.level = level;
        this.order = order;
        this.location = StringUtils.combine(this.level, this.order);
    }

    DataNodeLocation(String location, String level, int order) {
        Asserts.required(location, "node location cannot be null");
        Asserts.required(level, "node level cannot be null");
        Asserts.checkArgument(order >= 0, "node order cannot be less than 0");

        this.location = location;
        this.level = level;
        this.order = order;
    }

    static DataNodeLocation first() {
        return new DataNodeLocation(String.valueOf(LETTERS.charAt(0)), 0);
    }

    // TODO: Fix this method if need to create 2 letter combinations
    String nextLevel() {
        char nextLetter = nextLetterAfter(this.level.charAt(0));
        if (nextLetter == NULL_CHAR) {
            throw new UnsupportedOperationException();
        }

        return String.valueOf(nextLetter);
    }

    int nextOrder() {
        return this.order + 1;
    }

    public DataNodeLocation nextLocation() {
        return nextLocation(false);
    }

    public DataNodeLocation nextLocation(boolean newLevel) {
        String nextLevel = newLevel ? nextLevel() : this.level;
        int nextOrder = newLevel ? 0 : nextOrder();
        String location = StringUtils.combine(nextLevel, nextOrder);
        return new DataNodeLocation(location, nextLevel, nextOrder);
    }

    private char nextLetterAfter(char letter) {
        int index = LETTERS.indexOf(letter);
        int lastIndex = StringUtils.lastIndex(LETTERS);

        if (index == lastIndex) {
            return NULL_CHAR;
        }

        return LETTERS.charAt(index + 1);
    }

    public int getOrder() {
        return order;
    }

    public String getLevel() {
        return level;
    }

    public boolean isAfter(DataNodeLocation location) {
        if(java.util.Objects.equals(this.level, location.level)) {
            return this.order > location.order;
        }

        return compareLevel(this.level, location.level) > 0;
    }

    public boolean isBefore(DataNodeLocation location) {
        if(java.util.Objects.equals(this.level, location.level)) {
            return this.order < location.order;
        }

        return compareLevel(this.level, location.level) < 0;
    }

    // TODO: Change method implementation for two letters levels, e.g. AB, BC
    public static int compareLevel(String firstLevel, String secondLevel) {
        char first = firstLevel.charAt(0);
        char second = secondLevel.charAt(0);
        return Character.compare(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataNodeLocation that = (DataNodeLocation) o;
        return order == that.order && Objects.equal(location, that.location) && Objects.equal(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location, level, order);
    }

    @Override
    public String toString() {
        return "DataNodeLocation{" +
                "location='" + location + '\'' +
                ", level='" + level + '\'' +
                ", order=" + order +
                '}';
    }
}
