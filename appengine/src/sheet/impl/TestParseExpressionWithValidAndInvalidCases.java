//package sheet.impl;
//import expression.api.*;
//import expression.impl.*;
//import expression.impl.Number;
//import expression.impl.function.PlusFunction;
//import sheet.api.EffectiveValue;
//
//import java.util.ArrayList;
//import java.util.List;
//
//// Inside TestParseExpression.java
//
//import java.util.Scanner;
//import java.util.Scanner;
//import java.util.Scanner;
//
//import java.util.Scanner;
//
//public class TestParseExpressionWithValidAndInvalidCases {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        CellImpl cell = new CellImpl(0, "0");
//
//        // List of test cases with both valid and invalid expressions
//        String[] testCases = {
//                // Valid expressions
//                "{PLUS, 3, 7}", // Simple addition
//                "{CONCAT, \"Hello \", \"World\"}", // Simple concatenation
//                "{CONCAT, {CONCAT, \"Hello\", \" Stupid\"}, \"World\"}", // Nested concatenation
//                "{PLUS, {PLUS, 1, 2}, 3}", // Nested addition
//                "{PLUS, {PLUS, {PLUS, 1, 1}, 2}, 3}", // Deeply nested addition
//                "{CONCAT, {CONCAT, \"A\", \"B\"}, {CONCAT, \"C\", \"D\"}}", // Multiple nested CONCAT
//                "{PLUS, 0, 0}", // Edge case: Adding zeros
//                "{CONCAT, \"\", \"\"}", // Edge case: Concatenating empty strings
//
//                // Invalid expressions
//                "{PLUS, {CONCAT, \"Hello\", \"World\"}, {MULTIPLY, 2, 3}}", // Invalid: MULTIPLY is not defined
//                "{CONCAT, {PLUS, 2, 3}, 5}", // Invalid: CONCAT expects string arguments
//                "{CONCAT, \"Hello\", {PLUS, 1, 2, 3}}", // Invalid: PLUS function with too many arguments
//                "{PLUS, {PLUS, 1, 2}, {CONCAT, \"Hello\", 3}}", // Invalid: CONCAT expects strings, received number
//                "{PLUS, {CONCAT, \"String\", \"Another\"}}", // Invalid: PLUS requires numeric arguments
//                "{PLUS, {CONCAT, \"Valid\", \"String\"}, {CONCAT, \"Another\", \"String\"}}", // Invalid: PLUS with non-numeric arguments
//                "{PLUS, {PLUS, 5, 5}, {CONCAT, \"A\", \"B\"}}", // Invalid: Unclosed curly brace
//                "{CONCAT, {PLUS, 1, 2}, \"World\"", // Invalid: Unclosed quote in string
//                "{PLUS, \"a\", \"b\"}", // Invalid: PLUS requires numeric values
//                "{MULTIPLY, 5, 10}", // Invalid: MULTIPLY function not defined
//                "{CONCAT, 1, {PLUS, 2, 3}}", // Invalid: CONCAT expects string arguments
//                "{CONCAT, {PLUS, 2, 3}, {MULTIPLY, 4, 5}}", // Invalid: MULTIPLY function not defined
//                "{PLUS, \"String\", {CONCAT, \"Hello\", \"World\"}}", // Invalid: PLUS requires numeric arguments
//                "{PLUS}", // Invalid: Missing arguments for PLUS
//                "{CONCAT, \"String\"}", // Invalid: Missing second argument for CONCAT
//                "{PLUS, {PLUS, 1, 2}, {PLUS, 3}}", // Invalid: PLUS with missing argument
//        };
//
//        for (String testCase : testCases) {
//            try {
//                System.out.println("Testing: " + testCase);
//                cell.setOriginalValue(testCase);
//                cell.calculateEffectiveValue();
//                System.out.println("Effective Value: " + cell.getEffectiveValue().getValue());
//            } catch (Exception e) {
//                System.out.println("Exception: " + e.getMessage());
//            }
//            System.out.println(); // Empty line for readability
//        }
//
//        scanner.close();
//    }
//}
