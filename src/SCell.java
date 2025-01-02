// Add your documentation below:
// This class implements the Cell interface and represents a cell in a spreadsheet.
// Each cell can contain a number, text, or a formula.
import java.util.LinkedList;
import java.util.Set;

public class SCell implements Cell {
    private String data;
    private int type;
    private int order;

    public static final int TEXT = 1;
    public static final int NUMBER = 2;
    public static final int FORM = 3;
    public static final int ERR_CYCLE_FORM = -1;
    public static final int ERR_WRONG_FORM = -2;

    // Constructor
    public SCell(String data) {
        this.data = data;
        this.type = determineType(data);
        this.order = 0;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
        this.type = determineType(data);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    // Determine the type of the cell based on its data
    private int determineType(String data) {
        if (data == null || data.isEmpty()) return TEXT;
        if (isNumber(data)) return NUMBER;
        if (isFormula(data)) return FORM;
        return TEXT;
    }

    // Check if the string represents a number
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if the string represents a formula
    private boolean isFormula(String s) {
        return s != null && s.startsWith("=");
    }

    // Evaluate the value of the cell (formula or plain data)
    public String evaluate(Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (type == NUMBER) return data;

        if (type == FORM) {
            String currentCell = (char) ('A' + currentX) + String.valueOf(currentY + 1);
            if (visited.contains(currentCell)) {
                type = ERR_CYCLE_FORM;
                return "ERR_Cycle";
            }
            visited.add(currentCell);

            try {
                double result = computeFormula(data, sheet, currentX, currentY, visited);
                visited.remove(currentCell);
                return String.valueOf(result);
            } catch (IllegalArgumentException e) {
                type = ERR_WRONG_FORM;
                return "ERR_FORM";
            }
        }

        if (type == TEXT) return data;

        return "ERR_FORM!";
    }

    // Compute the result of a formula
    private static double computeFormula(String input, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (input == null || !input.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula");
        }
        String formula = input.substring(1).replaceAll("\\s", "");
        formula = replaceReferencesWithValues(formula, sheet, currentX, currentY, visited);
        return evaluateExpression(formula);
    }

    // Replace cell references with actual values
    private static String replaceReferencesWithValues(String formula, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                StringBuilder cellName = new StringBuilder();
                cellName.append(c);
                i++;
                while (i < formula.length() && Character.isDigit(formula.charAt(i))) {
                    cellName.append(formula.charAt(i));
                    i++;
                }

                String cell = cellName.toString().toUpperCase();
                int[] coords = sheet.parseEntry(cell);

                if (coords == null || (coords[0] == currentX && coords[1] == currentY)) {
                    throw new IllegalArgumentException("Invalid reference: " + cell);
                }

                SCell referencedCell = sheet.get(coords[0], coords[1]);
                String cellValue = (referencedCell != null)
                        ? referencedCell.evaluate(sheet, coords[0], coords[1], visited)
                        : "0";

                result.append(cellValue);
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    // Evaluate the mathematical expression
    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", "");
        return evaluateWithParentheses(expression);
    }

    // Evaluate the expression taking parentheses into account
    private static double evaluateWithParentheses(String expression) {
        while (expression.contains("(")) {
            int openIndex = expression.lastIndexOf('(');
            int closeIndex = expression.indexOf(')', openIndex);
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            double innerResult = evaluateWithoutParentheses(expression.substring(openIndex + 1, closeIndex));
            expression = expression.substring(0, openIndex) + innerResult + expression.substring(closeIndex + 1);
        }
        return evaluateWithoutParentheses(expression);
    }

    // Evaluate the expression without parentheses
    private static double evaluateWithoutParentheses(String expression) {
        LinkedList<Double> numbers = new LinkedList<>();
        LinkedList<Character> operators = new LinkedList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i <= expression.length(); i++) {
            char c = (i < expression.length()) ? expression.charAt(i) : '\0';

            if (Character.isDigit(c) || c == '.' || (c == '-' && (i == 0 || !Character.isDigit(expression.charAt(i - 1))))) {
                currentNumber.append(c);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '\0') {
                if (currentNumber.length() > 0) {
                    numbers.add(Double.parseDouble(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
                while (!operators.isEmpty() && precedence(operators.getLast()) >= precedence(c)) {
                    double b = numbers.removeLast();
                    double a = numbers.removeLast();
                    char op = operators.removeLast();
                    numbers.add(applyOperator(a, op, b));
                }
                if (c != '\0') operators.add(c);
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }

        while (!operators.isEmpty()) {
            double b = numbers.removeLast();
            double a = numbers.removeLast();
            char op = operators.removeLast();
            numbers.add(applyOperator(a, op, b));
        }

        return numbers.getLast();
    }

    // Return the precedence of operators
    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        if (operator == '*' || operator == '/') return 2;
        return -1;
    }

    // Apply an operator to two operands
    private static double applyOperator(double a, char operator, double b) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            default: throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    @Override
    public String toString() {
        return data;
    }
}


