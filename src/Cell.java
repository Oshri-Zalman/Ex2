public class Cell {


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

    public static boolean isForm (String s) {
        boolean ans = true;

    }

}
