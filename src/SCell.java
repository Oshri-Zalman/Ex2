// Documentation:
// This class implements the Cell interface and represents a cell in a spreadsheet.
// Each cell can contain a number, text, or a formula.

import java.util.LinkedList;
import java.util.NoSuchElementException;
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

    private int determineType(String data) {
        if (data == null || data.isBlank()) {
            return TEXT;
        }
        if (isNumber(data)) {
            return NUMBER;
        }
        // אם זה מתחיל ב-= אבל לא עובר את בדיקת תקינות הנוסחה
        if (data.startsWith("=")) {
            return isValidFormula(data) ? FORM : ERR_WRONG_FORM;
        }
        return TEXT;
    }

    private boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidFormula(String formula) {
        if (formula == null || !formula.startsWith("=")) {
            return false;
        }

        String expression = formula.substring(1).trim();

        // בדיקה בסיסית שהביטוי לא ריק ולא מסתיים באופרטור
        if (expression.isEmpty() || "+-*/".contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
            return false;
        }

        // הפניה עצמית היא נוסחה תקינה (למרות שתגרום לשגיאת מחזוריות)
        if (expression.matches("[A-Za-z]\\d+")) {
            return true;
        }

        return true;
    }

    public String evaluate(Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (type == ERR_CYCLE_FORM) {
            return "ERR_CYCLE";
        }
        return switch (type) {
            case NUMBER -> data;
            case TEXT -> data;
            case FORM -> evaluateFormula(sheet, currentX, currentY, visited);
            case ERR_WRONG_FORM -> "ERR_FORM";
            default -> "ERR_FORM";
        };
    }

    private String evaluateFormula(Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        String currentCell = getCellName(currentX, currentY);

        // בדיקת הפניה עצמית ישירה
        String expression = data.substring(1).trim();
        if (expression.equalsIgnoreCase(currentCell)) {
            this.type = ERR_CYCLE_FORM;
            return "ERR_CYCLE";
        }

        if (visited.contains(currentCell)) {
            this.type = ERR_CYCLE_FORM;
            return "ERR_CYCLE";
        }

        visited.add(currentCell);
        try {
            double result = computeFormula(data, sheet, currentX, currentY, visited);
            visited.remove(currentCell);
            return String.valueOf(result);
        } catch (ArithmeticException e) {
            this.type = ERR_WRONG_FORM;
            return "ERR_FORM (Division by zero)";
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("cycle")) {
                this.type = ERR_CYCLE_FORM;
                return "ERR_CYCLE";
            }
            this.type = ERR_WRONG_FORM;
            return "ERR_FORM";
        }
    }

    private String getCellName(int x, int y) {
        String cellName = String.valueOf((char) ('A' + x)) + (y);
        System.out.println("Converting coordinates [" + x + "," + y + "] to cell name: " + cellName);
        return cellName;
    }

    private static double computeFormula(String formula, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        if (formula == null || !formula.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula");
        }

        String expression = formula.substring(1).replaceAll("\\s", "");
        expression = replaceReferencesWithValues(expression, sheet, currentX, currentY, visited);

        return evaluateExpression(expression);
    }

    private static String replaceReferencesWithValues(String formula, Ex2Sheet sheet, int currentX, int currentY, Set<String> visited) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < formula.length()) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                String cellName = extractCellName(formula, i);
                int[] coords = sheet.parseEntry(cellName);

                if (coords == null) {
                    throw new IllegalArgumentException("Invalid reference: " + cellName);
                }

                // בדיקת הפניה עצמית
                if (coords[0] == currentX && coords[1] == currentY) {
                    throw new IllegalArgumentException("cycle detected");
                }

                SCell referencedCell = sheet.get(coords[0], coords[1]);
                String cellValue = (referencedCell != null)
                        ? referencedCell.evaluate(sheet, coords[0], coords[1], visited)
                        : "0";

                if (cellValue.equals("ERR_CYCLE")) {
                    throw new IllegalArgumentException("cycle detected");
                }

                result.append(cellValue);
                i += cellName.length();
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    private static String extractCellName(String formula, int startIndex) {
        StringBuilder cellName = new StringBuilder();
        cellName.append(formula.charAt(startIndex));
        int i = startIndex + 1;

        while (i < formula.length() && Character.isDigit(formula.charAt(i))) {
            cellName.append(formula.charAt(i));
            i++;
        }

        return cellName.toString().toUpperCase();
    }

    private static double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", "");
        return evaluateWithParentheses(expression);
    }

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

    private static double evaluateWithoutParentheses(String expression) {
        LinkedList<Double> numbers = new LinkedList<>();
        LinkedList<Character> operators = new LinkedList<>();
        StringBuilder currentNumber = new StringBuilder();

        // בדיקה אם הביטוי ריק
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        try {
            for (int i = 0; i <= expression.length(); i++) {
                char c = (i < expression.length()) ? expression.charAt(i) : '\0';

                if (Character.isDigit(c) || c == '.' || (c == '-' && (i == 0 || "+-*/".contains("" + expression.charAt(i - 1))))) {
                    currentNumber.append(c);
                } else if ("+-*/".indexOf(c) != -1 || c == '\0') {
                    if (currentNumber.length() > 0) {
                        numbers.add(Double.parseDouble(currentNumber.toString()));
                        currentNumber.setLength(0);
                    }

                    while (!operators.isEmpty() && precedence(operators.getLast()) >= precedence(c)) {
                        if (numbers.size() < 2) {
                            throw new IllegalArgumentException("Invalid expression format");
                        }
                        double b = numbers.removeLast();
                        double a = numbers.removeLast();
                        char op = operators.removeLast();
                        numbers.add(applyOperator(a, op, b));
                    }

                    if (c != '\0') {
                        operators.add(c);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid character in formula: " + c);
                }
            }

            if (numbers.isEmpty()) {
                throw new IllegalArgumentException("No valid numbers in expression");
            }

            return numbers.getFirst();  // שימוש ב-getFirst במקום getLast
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Invalid expression structure");
        }
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }

    private static double applyOperator(double a, char operator, double b) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    @Override
    public String toString() {
        return data;
    }
}




