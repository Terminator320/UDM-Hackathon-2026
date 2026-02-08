public class rail {
 
    public static String decryptRailFence(String ciphertext, int numRails) {
        if (numRails <= 1 || numRails >= ciphertext.length()) {
            return ciphertext;
        }
        
        int length = ciphertext.length();
        
        // Create the rail fence structure
        char[][] fence = new char[numRails][length];
        
        // Initialize fence with null characters
        for (int i = 0; i < numRails; i++) {
            for (int j = 0; j < length; j++) {
                fence[i][j] = '\0';
            }
        }
        
        // Mark the positions where characters will go (zigzag pattern)
        int rail = 0;
        int direction = 1; // 1 = moving down, -1 = moving up
        
        for (int col = 0; col < length; col++) {
            fence[rail][col] = '*'; // Mark this position
            rail += direction;
            
            // Bounce at top and bottom rails
            if (rail == 0 || rail == numRails - 1) {
                direction *= -1;
            }
        }
        
        // Fill the marked positions with ciphertext characters
        int charIndex = 0;
        for (int row = 0; row < numRails; row++) {
            for (int col = 0; col < length; col++) {
                if (fence[row][col] == '*') {
                    fence[row][col] = ciphertext.charAt(charIndex);
                    charIndex++;
                }
            }
        }
        
        // Read the plaintext by following the zigzag pattern
        StringBuilder plaintext = new StringBuilder();
        rail = 0;
        direction = 1;
        
        for (int col = 0; col < length; col++) {
            plaintext.append(fence[rail][col]);
            rail += direction;
            
            if (rail == 0 || rail == numRails - 1) {
                direction *= -1;
            }
        }
        
        return plaintext.toString();
    }
    
    /**
     * Convenience method that uses the default 2 rails.
     * 
     * @param ciphertext The encrypted message to decrypt
     * @return The decrypted plaintext message
     */
    public static String decryptRailFence(String ciphertext) {
        return decryptRailFence(ciphertext, 2);
    }
    
    /**
     * Main method demonstrating the decryption of the challenge message.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // The encrypted challenge message
        String encrypted = "I_a__rgtcl_a_nArl_n_h_lcswr_tiigtite.twsabih_oddyi_pi,adtecok_eesrkn_hren";
        
        // Decrypt using s (a2 rails determined from analysis)
        String decrypted = decryptRailFence(encrypted, 2);
        
        System.out.println("Encrypted message:");
        System.out.println(encrypted);
        System.out.println("\nDecrypted message:");
        System.out.println(decrypted);
        System.out.println("\nReadable format (replacing underscores with spaces):");
        System.out.println(decrypted.replace('_', ' '));
        
        // Demonstrate with different number of rails
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TESTING WITH DIFFERENT RAIL COUNTS:");
        System.out.println("=".repeat(80));
        
        for (int rails = 2; rails <= 5; rails++) {
            String result = decryptRailFence(encrypted, rails);
            System.out.println("\nRails " + rails + ":");
            System.out.println(result);
        }
    }
}
