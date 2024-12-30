import java.util.ArrayList;
public class Functions {


    // checks if the input is number
    public static boolean isNumber(String s) {
        boolean ans = true;
        try {
            double d = Double.parseDouble(s);
        } catch (Exception e) {
            ans = false;
        }
        return ans;
    }

    // checks if the input is not number if its Text its valid
    public static boolean isText(String s) {
        boolean ans = true;
        if (isNumber(s)) {
            ans = false;
        }
            return ans;
    }

    // function that checks if the formula is valid or not.
    public static boolean isForm(String s) {
        boolean ans = true;

        // Check if the input starts with "=" and has content after it
        if (s == null || s.length() < 2 || s.charAt(0) != '=') {
            return false;
        }

        // Extract the part after =
        String formula = s.substring(1);


        // Allows numbers, arithmetic operators (+, -, *, /), and ().
        String regex = "^[0-9+\\-*/()\\s]+$";

        if (!formula.matches(regex)) {
            return false;
        }

        // Check for balanced parentheses
        if (!areParenthesesBalanced(formula)) {
            return false;
        }
        // Check if the formula ends with an operator
        if (formula.matches(".*[+\\-*/]$")) {
            return false;
        }

        return ans;
    }

    private static boolean areParenthesesBalanced(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // If balance is negative, parentheses are unbalanced
            if (balance < 0) {
                return false;
            }
        }
        // Parentheses are balanced if balance is 0 at the end
        return balance == 0;
    }

    public static Double computeForm(String form) {
        if (!isForm(form)) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        // Remove the '=' at the beginning
        String formula = form.substring(1);

        // Evaluate the formula
        return evaluateExpression(formula);
    }

    private static double evaluateExpression(String expression) {
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // If it's a digit or '.', parse the number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                numbers.add(Double.parseDouble(sb.toString()));
                continue;
            }

            // If it's a '(', add it to the operator list
            if (c == '(') {
                operators.add(c);
            }
            // If it's a ')', resolve the inner expression
            else if (c == ')') {
                while (!operators.isEmpty() && operators.get(operators.size() - 1) != '(') {
                    numbers.add(applyOperator(operators.remove(operators.size() - 1),
                            numbers.remove(numbers.size() - 1),
                            numbers.remove(numbers.size() - 1)));
                }
                operators.remove(operators.size() - 1); // Remove '('
            }
            // If it's an operator, resolve precedence
            else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.get(operators.size() - 1)) >= precedence(c)) {
                    numbers.add(applyOperator(operators.remove(operators.size() - 1),
                            numbers.remove(numbers.size() - 1),
                            numbers.remove(numbers.size() - 1)));
                }
                operators.add(c);
            }
            i++;
        }

        // Resolve the remaining operations
        while (!operators.isEmpty()) {
            numbers.add(applyOperator(operators.remove(operators.size() - 1),
                    numbers.remove(numbers.size() - 1),
                    numbers.remove(numbers.size() - 1)));
        }

        return numbers.remove(numbers.size() - 1);
    }

    // Check if a character is an operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Get the precedence of an operator
    private static int precedence(char op) {
        if (op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        }
        return 0;
    }

    // Apply an operator to two numbers
    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
