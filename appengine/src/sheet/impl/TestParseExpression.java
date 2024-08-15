package sheet.impl;
import expression.api.*;
import expression.impl.*;
import expression.impl.Number;
import expression.impl.function.PlusFunction;
import sheet.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;

// Inside TestParseExpression.java

import java.util.Scanner;

public class TestParseExpression {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CellImpl cell = new CellImpl(0, 0);

        // Prompt the user to enter an expression
        System.out.println("Enter an expression (e.g., {PLUS, 3, 7} or {CONCAT, \"Hello \", \"World\"}):");
        String inputExpression = scanner.nextLine();

        // Set the original value from the user input
        cell.setOriginalValue(inputExpression);

        // Calculate the effective value
        cell.calculateEffectiveValue();

        // Display the result
        System.out.println("Effective Value: " + cell.getEffectiveValue().getValue());
    }
}
