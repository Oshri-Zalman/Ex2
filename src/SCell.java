// Documentation:
// This class implements the Cell interface and represents a cell in a spreadsheet.
// Each cell can contain a number, text, or a formula.

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class SCell implements Cell {
    private String data; // The content of the cell (text, number, or formula)
    private int type; // The type of the cell (TEXT, NUMBER, FORM, or error types)
    private int order; // The computation order of the cell

    public static final int TEXT = 1; // Type for text
    public static final int NUMBER = 2; // Type for numbers
    public static final int FORM = 3; // Type for formulas
    public static final int ERR_CYCLE_FORM = -1; // Error for a formula cycle
    public static final int ERR_WRONG_FORM = -2; // Error for invalid formula

    public SCell(String data) {
        this.data = data; // Set the data of the cell
        this.type = determineType(data); // Determine the type of the cell
        this.order = 0; // Default computation order is 0
    }

    @Override
    public String getData() {
        return data; // Return the content of the cell
    }

    @Override
    public void setData(String data) {
        this.data = data; // Update the content of the cell
        this.type = determineType(data); // Update the type based on the new content
    }

    @Override
    public int getType() {
        return type; // Return the type of the cell
    }

    @Override
    public void setType(int type) {
        this.type = type; // Set the type of the cell
    }

    @Override
    public int getOrder() {
        return order; // Return the computation order
    }

    @Override
    public void setOrder(int order) {
        this.order = order; // Set the computation order
    }

    private int determineType(String data) {
        if (data == null || data.isBlank()) {
            return TEXT; // Empty or blank data is text
        }
        if (isNumber(data)) {
            return NUMBER; // Numeric data is a number
        }

        if (data.startsWith("=")) {
            return isValidFormula(data) ? FORM : ERR_WRONG_FORM; // Check if formula is valid
        }
        return TEXT; // Default type is text
    }

    private boolean isNumber(String value) {
        try {
            Double.parseDouble(value); // Try to convert to a number
            return true;
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }
    }

    private boolean isValidFormula(String formula) {
        if (formula == null || !formula.startsWith("=")) {
            return false; // Not a formula if it doesn't start with "="
        }

        String expression = formula.substring(1).trim(); // Remove the "=" and trim

        if (expression.isEmpty() || "+-*/".contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
            return false; // Invalid if empty or ends with an operator
        }

        if (expression.matches("[A-Za-z]\\d+")) {
            return true; // Valid if it matches cell reference pattern
        }

        return true; // Assume valid for other cases
    }

    public String evaluate(Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (type == ERR_CYCLE_FORM) {
            return "ERR_CYCLE"; // Return error if there is a cycle
        }
        return switch (type) {
            case NUMBER -> data; // Return the number as a string
            case TEXT -> data; // Return the text as is
            case FORM -> evaluateFormula(sheet, currentX, currentY, visited); // Evaluate the formula
            case ERR_WRONG_FORM -> "ERR_FORM"; // Return error for invalid formula
            default -> "ERR_FORM"; // Default to error
        };
    }

    private String evaluateFormula(Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        String currentCell = getCellName(currentX, currentY); // Get the name of the current cell

        String expression = data.substring(1).trim(); // Get the formula without "="
        if (expression.equalsIgnoreCase(currentCell)) {
            this.type = ERR_CYCLE_FORM; // Set type to error if self-referencing
            return "ERR_CYCLE"; // Return cycle error
        }

        if (visited.contains(currentCell)) {
            this.type = ERR_CYCLE_FORM; // Set type to error if cycle detected
            return "ERR_CYCLE"; // Return cycle error
        }

        visited.add(currentCell); // Mark the current cell as visited
        try {
            double result = computeFormula(data, sheet, currentX, currentY, visited); // Compute the formula
            visited.remove(currentCell); // Unmark the current cell after computation
            return String.valueOf(result); // Return the result as a string
        } catch (ArithmeticException e) {
            this.type = ERR_WRONG_FORM; // Set type to error if division by zero
            return "ERR_FORM (Division by zero)";
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("cycle")) {
                this.type = ERR_CYCLE_FORM; // Set type to error if cycle detected
                return "ERR_CYCLE";
            }
            this.type = ERR_WRONG_FORM; // Set type to error for other invalid formulas
            return "ERR_FORM";
        }
    }

    private String getCellName(int x, int y) {
        String cellName = String.valueOf((char) ('A' + x)) + (y); // Convert coordinates to cell name
        return cellName;
    }

    private static double computeFormula(String formula, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (formula == null || !formula.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula"); // Throw error if formula is invalid
        }

        String expression = formula.substring(1).replaceAll("\\s", ""); // Remove "=" and spaces
        expression = replaceReferencesWithValues(expression, sheet, currentX, currentY, visited); // Replace cell references with values

        return evaluateExpression(expression); // Evaluate the final expression
    }

    private static String replaceReferencesWithValues(String formula, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        StringBuilder result = new StringBuilder(); // For building the new expression
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                String cellName = extractCellName(formula, i); // Get the cell reference
                int[] coords = sheet.parseEntry(cellName); // Parse the reference to coordinates

                if (coords == null) {
                    throw new IllegalArgumentException("Invalid reference: " + cellName);
                }

                if (coords[0] == currentX && coords[1] == currentY) {
                    throw new IllegalArgumentException("cycle detected"); // Detect self referencing
                }

                SCell referencedCell = sheet.get(coords[0], coords[1]); // Get the referenced cell
                String cellValue = (referencedCell != null)
                        ? referencedCell.evaluate(sheet, coords[0], coords[1], visited) // Evaluate the referenced cell
                        : "0"; // Default to 0 if cell is null

                if (cellValue.equals("ERR_CYCLE")) {
                    throw new IllegalArgumentException("cycle detected"); // Detect cycle in referenced cell
                }

                result.append(cellValue); // Append the cell value to the formula
                i += cellName.length(); // Move to the next part of the formula
            } else {
                result.append(c); // Append non reference characters
                i++;
            }
        }

        return result.toString(); // Return the modified formula
    }

    private static String extractCellName(String formula, int startIndex) {
        StringBuilder cellName = new StringBuilder(); // For building the cell name
        cellName.append(formula.charAt(startIndex)); // Add the first character
        int i = startIndex + 1;

        while (i < formula.length() && Character.isDigit(formula.charAt(i))) {
            cellName.append(formula.charAt(i)); // Add digits to the cell name
            i++;
        }

        return cellName.toString().toUpperCase(); // Return the cell name in uppercase
    }

    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", ""); // Remove spaces from the expression
        return evaluateWithParentheses(expression); // Evaluate the expression with parentheses
    }

    private static double evaluateWithParentheses(String expression) {
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf('('); // Find the last "("
            int closeIndex = expression.indexOf(')', openIndex); // Find the matching ")"

            if (closeIndex == -1) {
                throw new IllegalArgumentException("Mismatched parentheses"); // Throw error if no matching ")"
            }

            double innerResult = evaluateWithoutParentheses(expression.substring(openIndex + 1, closeIndex)); // Evaluate inside parentheses
            expression = expression.substring(0, openIndex) + innerResult + expression.substring(closeIndex + 1); // Replace with result
        }

        return evaluateWithoutParentheses(expression); // Evaluate the remaining expression
    }

    private static double evaluateWithoutParentheses(String expression) {
        LinkedList<Double> numbers = new LinkedList<>(); // Stack for numbers
        LinkedList<Character> operators = new LinkedList<>(); // Stack for operators
        StringBuilder currentNumber = new StringBuilder(); // Current number being read

        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression"); // Throw error for empty expression
        }

        try {
            for (int i = 0; i <= expression.length(); i++) {
                char c = (i < expression.length()) ? expression.charAt(i) : '\0'; // Read character or end marker

                if (Character.isDigit(c) || c == '.' || (c == '-' && (i == 0 || "+-*/".contains("" + expression.charAt(i - 1))))) {
                    currentNumber.append(c); // Build the current number
                } else if ("+-*/".indexOf(c) != -1 || c == '\0') {
                    if (currentNumber.length() > 0) {
                        numbers.add(Double.parseDouble(currentNumber.toString())); // Add the number to the stack
                        currentNumber.setLength(0); // Reset the number builder
                    }

                    while (!operators.isEmpty() && precedence(operators.getLast()) >= precedence(c)) {
                        if (numbers.size() < 2) {
                            throw new IllegalArgumentException("Invalid expression format"); // Error if not enough numbers
                        }
                        double b = numbers.removeLast(); // Second operand
                        double a = numbers.removeLast(); // First operand
                        char op = operators.removeLast(); // Operator
                        numbers.add(applyOperator(a, op, b)); // Apply operator and add result
                    }

                    if (c != '\0') {
                        operators.add(c); // Add the current operator
                    }
                } else {
                    throw new IllegalArgumentException("Invalid character in formula: " + c); // Error for invalid character
                }
            }

            if (numbers.isEmpty()) {
                throw new IllegalArgumentException("No valid numbers in expression"); // Error if no numbers found
            }

            return numbers.getFirst(); // Return the final result
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Invalid expression structure"); // Error for structure issues
        }
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1; // Low precedence for + and -
            case '*', '/' -> 2; // High precedence for * and /
            default -> -1; // Invalid operator
        };
    }

    private static double applyOperator(double a, char operator, double b) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new ArithmeticException("Division by zero"); // Error for division by zero
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator); // Error for unknown operator
        };
    }

    @Override
    public String toString() {
        return data; // Return the cell content as a string
    }
}
