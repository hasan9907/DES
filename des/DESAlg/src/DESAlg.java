import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DESAlg {

    public static void main(String[] args) {

        System.out.println("-".repeat(100));

        // Take text(Message) with scanner as a String
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your message: ");
        String text = scanner.nextLine();

        System.out.println("-".repeat(100));

        // Key
        String roundKeyText = "hi cmpe";
        // Convert text to hexadecimal
        String hexText = textToHex(text);
        String hexRKey = textToHex(roundKeyText);
        System.out.println("Hexadecimal: " + hexText);

        // Convert hexadecimal to binary (64-bit)
        String binaryText = hexToBinary(hexText);
        String binaryRKey = hexToBinary(hexRKey);
        System.out.println("Original Binary of Text (64-bit): " + binaryText);
        System.out.println("Orginal Binary of Key: " + binaryRKey);

        System.out.println("-".repeat(100));

        // Initial Permutation
        String initialPermutedText = initialPermutation(binaryText);
        System.out.println("After Initial Permutation: " + initialPermutedText);

        // Inverse Initial Permutation
        String inverseInitialPermutedText = inverseInitialPermutation(initialPermutedText);
        System.out.println("After Inverse Initial Permutation: " + inverseInitialPermutedText);

        // Left and Right side of Initial Permutation (32-bit left & 32-bit right)
        String InPerLeftHalf = initialPermutedText.substring(0, 32);
        System.out.println("Left 32-bit of Initial Permutation: " + InPerLeftHalf);
        String InPerRightHalf = initialPermutedText.substring(32);
        System.out.println("Right 32-bit Initial Permutation: " + InPerRightHalf);

        System.out.println("-".repeat(100));

        // Expansion/Permutation using right half of the Initial Permutation
        String expansion = expansionPermutation(InPerRightHalf);
        System.out.println("After Expansion/Permutation: " + expansion);

        System.out.println("-".repeat(100));

        // Round Key Generation
        System.out.println("Key Generation steps (" + roundKeyText + ")");
        // PC1 permutation to reduce 64-bit key to 56-bit
        String pc1Text = PC1(binaryRKey);
        System.out.println("After PC1 Permutation: " + pc1Text);
        // Split the 56-bit key into two 28-bit halves
        String PConeLeftHalf = pc1Text.substring(0, 28);
        String PConeRightHalf = pc1Text.substring(28);

        // Perform left shifts
        int numberOfShifts = 1; // left shift nubmer
        PConeLeftHalf = leftShift(PConeLeftHalf, numberOfShifts);
        PConeRightHalf = leftShift(PConeRightHalf, numberOfShifts);
        System.out.println("Left 28 bits after shift: " + PConeLeftHalf);
        System.out.println("Right 28 bits after shift: " + PConeRightHalf);
        // Combine the 28-bit halves
        String combinedKey = PConeLeftHalf + PConeRightHalf;
        // Perform PC2 permutation to obtain the 48-bit key
        String roundKey = PC2(combinedKey);
        System.out.println("Final 48-bit Key: " + roundKey);
        // Key Generation Finished

        System.out.println("-".repeat(100));

        // Xor Operation to expansion and round key
        String xorOp = xorOperation(expansion, roundKey);
        System.out.println("After xor(48-bit): " + xorOp);

        System.out.println("-".repeat(100));

        // Perform S-box 48-bit to 32-bit
        String sBoxOutput = sBox(xorOp);
        System.out.println("After S-box Substitution: " + sBoxOutput);

        System.out.println("-".repeat(100));

        // Perform 32-bit permutation
        String permutationResult = permutation32(sBoxOutput);
        System.out.println("After 32-bit Permutation: " + permutationResult);

        System.out.println("-".repeat(100));

        // Last xor operation and encrypted text
        String lastXOR = xorOperation(InPerLeftHalf, permutationResult);
        System.out.println("After last XOR operation: " + lastXOR);

        System.out.println("-".repeat(100));

        System.out.println("Encrypted form of the text: " + InPerRightHalf + lastXOR);

        // Close the scanner
        scanner.close();
    }

    // Function to convert text to hexadecimal
    private static String textToHex(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        BigInteger bigInt = new BigInteger(1, bytes);
        return bigInt.toString(16);
    }

    // Function to convert hexadecimal to 64-bit binary
    private static String hexToBinary(String hex) {
        String binaryString = new BigInteger(hex, 16).toString(2);

        int length = binaryString.length();
        if (length < 64) {
            binaryString = "0".repeat(64 - length) + binaryString;
        } else if (length > 64) {
            // If the binary string is longer than 64 bits delete rest
            binaryString = binaryString.substring(length - 64);
        }

        return binaryString;
    }

    // Initial Permutation
    private static String initialPermutation(String binaryString) {
        // Define the initial permutation table
        int[] IPTable = {
                58, 50, 42, 34, 26, 18, 10, 2,
                60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6,
                64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1,
                59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5,
                63, 55, 47, 39, 31, 23, 15, 7
        };

        // Permutation
        StringBuilder permutedBuilder = new StringBuilder();
        for (int i = 0; i < IPTable.length; i++) {
            int newPosition = IPTable[i];
            permutedBuilder.append(binaryString.charAt(newPosition - 1));
        }

        return permutedBuilder.toString();
    }

    // Function to do Inverse Initial Permutation
    private static String inverseInitialPermutation(String binaryString) {
        // Define the inverse initial permutation table
        int[] inverseIPTable = {
                40, 8, 48, 16, 56, 24, 64, 32,
                39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28,
                35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26,
                33, 1, 41, 9, 49, 17, 57, 25
        };

        // Perform the inverse permutation
        StringBuilder inversePermutedBuilder = new StringBuilder();
        for (int i = 0; i < inverseIPTable.length; i++) {
            int originalPosition = inverseIPTable[i];
            inversePermutedBuilder.append(binaryString.charAt(originalPosition - 1));
        }

        return inversePermutedBuilder.toString();
    }

    // Function to do the expansion permutation
    private static String expansionPermutation(String leftHalf) {
        // Define the expansion permutation table (E-box)
        int[] eTable = {
                32, 1, 2, 3, 4, 5, 4, 5,
                6, 7, 8, 9, 8, 9, 10, 11,
                12, 13, 12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21, 20, 21,
                22, 23, 24, 25, 24, 25, 26, 27,
                28, 29, 28, 29, 30, 31, 32, 1
        };

        StringBuilder expandedBuilder = new StringBuilder();
        for (int i = 0; i < eTable.length; i++) {
            expandedBuilder.append(leftHalf.charAt(eTable[i] - 1));
        }

        return expandedBuilder.toString();
    }

    // Function to do PC1
    private static String PC1(String binaryString) {
        // Define the PC1 table
        int[] pc1Table = {
                57, 49, 41, 33, 25, 17, 9, 1,
                58, 50, 42, 34, 26, 18, 10, 2,
                59, 51, 43, 35, 27, 19, 11, 3,
                60, 52, 44, 36, 63, 55, 47, 39,
                31, 23, 15, 7, 62, 54, 46, 38,
                30, 22, 14, 6, 61, 53, 45, 37,
                29, 21, 13, 5, 28, 20, 12, 4
        };

        StringBuilder pc1Builder = new StringBuilder();
        for (int i = 0; i < pc1Table.length; i++) {
            pc1Builder.append(binaryString.charAt(pc1Table[i] - 1));
        }

        return pc1Builder.toString();
    }

    // Function to do left circular shift
    private static String leftShift(String input, int numberOfShifts) {
        StringBuilder shifted = new StringBuilder(input.substring(numberOfShifts));
        shifted.append(input.substring(0, numberOfShifts));
        return shifted.toString();
    }

    // Function to do PC2
    private static String PC2(String binaryString) {
        // Define the PC2 table
        int[] pc2Table = {
                14, 17, 11, 24, 1, 5, 3, 28,
                15, 6, 21, 10, 23, 19, 12, 4,
                26, 8, 16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55, 30, 40,
                51, 45, 33, 48, 44, 49, 39, 56,
                34, 53, 46, 42, 50, 36, 29, 32
        };

        StringBuilder pc2Builder = new StringBuilder();
        for (int i = 0; i < pc2Table.length; i++) {
            pc2Builder.append(binaryString.charAt(pc2Table[i] - 1));
        }

        return pc2Builder.toString();
    }

    // Function to do XOR operation
    private static String xorOperation(String data, String key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            result.append(data.charAt(i) ^ key.charAt(i));
        }
        return result.toString();
    }

    // Function to do S-box
    private static String sBox(String xorResult) {
        // Split the 48-bit result into 8 groups of 6 bits each
        String[] sBoxInputs = new String[8];
        for (int i = 0; i < 8; i++) {
            sBoxInputs[i] = xorResult.substring(i * 6, (i + 1) * 6);
        }

        // S-boxes
        int[][][] sBoxes = {
                // S1
                {
                        { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
                        { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
                        { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
                        { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }
                },
                // S2
                {
                        { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
                        { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
                        { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
                        { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }
                },
                // S3
                {
                        { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
                        { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
                        { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
                        { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }
                },
                // S4
                {
                        { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
                        { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
                        { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
                        { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }
                },
                // S5
                {
                        { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
                        { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
                        { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
                        { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }
                },
                // S6
                {
                        { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
                        { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
                        { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
                        { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }
                },
                // S7
                {
                        { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
                        { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
                        { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
                        { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }
                },
                // S8
                {
                        { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
                        { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
                        { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
                        { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 }
                }

        };

        // Perform S-box
        StringBuilder sBoxOutput = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int row = Integer.parseInt("" + sBoxInputs[i].charAt(0) + sBoxInputs[i].charAt(5), 2);
            int col = Integer.parseInt(sBoxInputs[i].substring(1, 5), 2);
            int sBox = sBoxes[i][row][col];
            sBoxOutput.append(String.format("%4s", Integer.toBinaryString(sBox)).replace(' ', '0'));
        }

        return sBoxOutput.toString();
    }

    // 32-bit permutation
    private static String permutation32(String input) {
        // Permutation table
        int[] permutationTable = {
                16, 7, 20, 21, 29, 12, 28, 17,
                1, 15, 23, 26, 5, 18, 31, 10,
                2, 8, 24, 14, 32, 27, 3, 9,
                19, 13, 30, 6, 22, 11, 4, 25
        };

        // Map to store the values between old and new positions
        Map<Integer, Integer> positionMap = new HashMap<>();
        for (int i = 0; i < permutationTable.length; i++) {
            positionMap.put(i + 1, permutationTable[i]);
        }

        StringBuilder permutedBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            int newPosition = positionMap.get(i + 1);
            permutedBuilder.append(input.charAt(newPosition - 1));
        }

        return permutedBuilder.toString();
    }
}
