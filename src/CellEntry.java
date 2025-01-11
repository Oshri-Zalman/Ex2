// Documentation:
// This class implements the Index2D interface and represents a reference to a cell in the spreadsheet.
// It validates the reference format and provides methods to extract row and column indices.

public class CellEntry implements Index2D {
    private String index; // The string representation of the cell index (e.g., "A1")

    public CellEntry(String index) {
        this.index = index; // Set the cell reference string
    }

    @Override
    public boolean isValid() {
        if (index == null || index.isBlank()) return false; // Check if index is null or empty

        char column = Character.toUpperCase(index.charAt(0)); // Extract column character
        String rowPart = index.substring(1); // Extract row part

        return Character.isLetter(column) && rowPart.matches("\\d+") // Validate format (e.g., "A1")
                && isRowInRange(rowPart) && isColumnInRange(column); // Check row and column ranges
    }

    private boolean isRowInRange(String rowPart) {
        int row;
        try {
            row = Integer.parseInt(rowPart); // Convert the row part to a number
        } catch (NumberFormatException e) {
            return false; // Return false if not a valid number
        }
        return row >= 1 && row <= 99; // Row must be between 1 and 99
    }

    private boolean isColumnInRange(char column) {
        return column >= 'A' && column <= 'Z'; // Column must be between A and Z
    }

    @Override
    public int getX() {
        validateIndex(); // Ensure the index is valid
        return Character.toUpperCase(index.charAt(0)) - 'A'; // Convert column letter to a number
    }

    @Override
    public int getY() {
        validateIndex(); // Ensure the index is valid
        return Integer.parseInt(index.substring(1)) - 1; // Convert row part to zero-based index
    }

    private void validateIndex() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid index: " + index); // Throw error if index is invalid
        }
    }

    @Override
    public String toString() {
        return isValid() ? index.toUpperCase() : ""; // Return the index in uppercase if valid
    }
}
