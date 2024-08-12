public class MainToCheck {
    public static void main(String[] args) {
        // Addition
        MathExpression addExpr = new PlusFunction(new Number(4.0), new Number(5.0));
        System.out.println("Function: " + addExpr.getName() + ", Result: " + addExpr.eval()); // Outputs: Function: PLUS, Result: 9.0

        // Subtraction
        MathExpression subExpr = new MinusFunction(new Number(10), new Number(3));
        System.out.println("Function: " + subExpr.getName() + ", Result: " + subExpr.eval()); // Outputs: Function: MINUS, Result: 7.0

        // Multiplication
        MathExpression mulExpr = new TimesFunction(new Number(4), new Number(6.0));
        System.out.println("Function: " + mulExpr.getName() + ", Result: " + mulExpr.eval()); // Outputs: Function: TIMES, Result: 24.0

        // Division
        MathExpression divExpr = new DivideFunction(new Number(10.0), new Number(2.0));
        System.out.println("Function: " + divExpr.getName() + ", Result: " + divExpr.eval()); // Outputs: Function: DIVIDE, Result: 5.0

        // Modulo
        MathExpression modExpr = new ModFunction(new Number(10.0), new Number(3.0));
        System.out.println("Function: " + modExpr.getName() + ", Result: " + modExpr.eval()); // Outputs: Function: MOD, Result: 1.0

        // Power
        MathExpression powExpr = new PowFunction(new Number(2.0), new Number(3.0));
        System.out.println("Function: " + powExpr.getName() + ", Result: " + powExpr.eval()); // Outputs: Function: POW, Result: 8.0

        // Absolute
        MathExpression absExpr = new AbsFunction(new Number(-10.0));
        System.out.println("Function: " + absExpr.getName() + ", Result: " + absExpr.eval()); // Outputs: Function: ABS, Result: 10.0


            // CONCAT function usage
            Expression<String> concatExpr = new ConcatFunction(new StringLiteral("Hello, "), new StringLiteral("World!"));
            System.out.println("Result of CONCAT: " + concatExpr.eval()); // Outputs: Hello, World!

            // SUB function usage
            Expression<String> subExpr2 = new SubFunction(new StringLiteral("Hello, World!"), new Number(7.0), new Number(11.0));
            System.out.println("Result of SUB: " + subExpr2.eval()); // Outputs: World

            // Another SUB function usage
            Expression<String> subExpr3 = new SubFunction(new StringLiteral("OpenAI is awesome!"), new Number(7.0), new Number(8.0));
            System.out.println("Result of SUB: " + subExpr3.eval()); // Outputs:

            // CONCAT with SUB function usage
            Expression<String> combinedExpr = new ConcatFunction(
                    new SubFunction(new StringLiteral("Hello, World!"), new Number(0.0), new Number(4.0)),
                    new SubFunction(new StringLiteral("OpenAI"), new Number(0.0), new Number(3.0))
            );
            System.out.println("Result of combined CONCAT and SUB: " + combinedExpr.eval()); // Outputs: HelloOpen
        }
}
