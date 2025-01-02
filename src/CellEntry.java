public class CellEntry implements Index2D {
    private String index;

    public CellEntry(String index) {
        this.index = index;
    }

    @Override
    public boolean isValid() {
        if (index == null || index.isBlank()) return false;

        char column = Character.toUpperCase(index.charAt(0));
        String rowPart = index.substring(1);

        return Character.isLetter(column) && rowPart.matches("\\d+") && isRowInRange(rowPart) && isColumnInRange(column);
    }

    private boolean isRowInRange(String rowPart) {
        int row;
        try {
            row = Integer.parseInt(rowPart);
        } catch (NumberFormatException e) {
            return false;
        }
        return row >= 1 && row <= 99;
    }

    private boolean isColumnInRange(char column) {
        return column >= 'A' && column <= 'Z';
    }

    @Override
    public int getX() {
        validateIndex();
        return Character.toUpperCase(index.charAt(0)) - 'A';
    }

    @Override
    public int getY() {
        validateIndex();
        return Integer.parseInt(index.substring(1)) - 1;
    }

    private void validateIndex() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid index: " + index);
        }
    }

    @Override
    public String toString() {
        return isValid() ? index.toUpperCase() : "";
    }
}
