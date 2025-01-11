// Documentation:
// This class implements the Sheet interface and represents a spreadsheet.
// It manages a 2D array of cells, where each cell can store text, numbers, or formulas.
// The spreadsheet supports operations like getting and setting cell values,
// evaluating formulas, computing computational depth, and saving/loading data.

import java.io.*;
import java.util.HashSet;

public class Ex2Sheet implements Sheet {
    private final SCell[][] table; // The 2D array of cells
    private final int width; // The number of columns in the sheet
    private final int height; // The number of rows in the sheet

    public Ex2Sheet(int width, int height) {
        this.width = width; // Set the width of the sheet
        this.height = height; // Set the height of the sheet
        this.table = new SCell[width][height]; // Initialize the cell array
        initializeTable(); // Fill the table with default cells
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Use default dimensions if none provided
    }

    private void initializeTable() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new SCell(""); // Initialize each cell with empty data
            }
        }
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height; // Check if coordinates are within bounds
    }

    @Override
    public int width() {
        return width; // Return the number of columns
    }

    @Override
    public int height() {
        return height; // Return the number of rows
    }

    @Override
    public void set(int x, int y, String c) {
        if (isIn(x, y)) {
            table[x][y].setData(c); // Set the content of a specific cell
        }
    }

    @Override
    public SCell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null; // Return the cell if within bounds
    }

    @Override
    public SCell get(String entry) {
        int[] coords = parseEntry(entry); // Parse the cell name to coordinates
        return (coords != null && isIn(coords[0], coords[1])) ? table[coords[0]][coords[1]] : null; // Return the cell
    }

    @Override
    public String value(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y].evaluate(this, x, y, new HashSet<>()); // Evaluate the cell and return its value
        }
        return "ERR_Cycle"; // Return error if out of bounds
    }

    @Override
    public String eval(int x, int y) {
        return value(x, y); // Alias for value method
    }

    @Override
    public void eval() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                value(i, j); // Evaluate all cells in the sheet
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width][height]; // Initialize depth matrix
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                depths[i][j] = computeDepth(i, j, new boolean[width][height]); // Compute depth for each cell
            }
        }
        return depths; // Return the computed depths
    }

    private int computeDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y) || visited[x][y]) {
            return -1; // Return -1 if out of bounds or already visited
        }

        visited[x][y] = true; // Mark the cell as visited
        SCell cell = table[x][y]; // Get the current cell
        if (cell.getType() != SCell.FORM) {
            return 0; // Depth is 0 if the cell is not a formula
        }

        String formula = cell.getData().substring(1); // Extract the formula (without "=")
        int maxDepth = 0; // Track the maximum depth of dependencies

        for (String ref : parseReferences(formula)) {
            int[] coords = parseEntry(ref); // Parse references in the formula
            if (coords != null) {
                maxDepth = Math.max(maxDepth, 1 + computeDepth(coords[0], coords[1], visited)); // Update maximum depth
            }
        }

        visited[x][y] = false; // Unmark the cell after computation
        return maxDepth; // Return the maximum depth
    }

    private String[] parseReferences(String formula) {
        return formula.split("[^A-Za-z0-9]"); // Split formula into references by non-alphanumeric characters
    }

    public int[] parseEntry(String entry) {
        if (entry == null || entry.length() < 2) return null; // Return null if invalid entry

        char column = Character.toUpperCase(entry.charAt(0)); // Extract column character
        String rowPart = entry.substring(1); // Extract row part

        try {
            int row = Integer.parseInt(rowPart); // Parse the row number
            int col = column - 'A'; // Convert column to a number

            if (col < 0 || col >= width || row < 0 || row >= height) {
                return null; // Return null if out of bounds
            }

            return new int[]{col, row}; // Return the parsed coordinates
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse row number: " + rowPart); // Print error if row is invalid
            return null;
        }
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("\n"); // Write an empty line as header

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    String data = table[i][j].getData(); // Get the cell data
                    if (!data.isEmpty()) {
                        writer.write(i + "," + j + "," + data + "\n"); // Write cell coordinates and data
                    }
                }
            }
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip the header line

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3); // Split line into parts
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0]); // Parse column index
                        int y = Integer.parseInt(parts[1]); // Parse row index
                        String data = parts[2]; // Extract cell data
                        set(x, y, data); // Set the cell data
                    } catch (NumberFormatException e) {
                        // Ignore invalid entries
                    }
                }
            }
        }
    }
}
