import java.util.Stack;

class ValidParentheses {
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }

                char check = stack.pop();

                if ((check == '(' && c != ')') ||
                        (check == '{' && c != '}') ||
                        (check == '[' && c != ']')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
