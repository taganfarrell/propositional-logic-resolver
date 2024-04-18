import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
      try {
        
        if (args.length < 1) {
            System.out.println("Please provide a filename as a command-line argument.");
            return;
        }

        String filename = args[0];
        System.out.println("Reading from file: " + filename);

        System.out.println("\nPremises:");

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line does not start with "//"
                if (!line.startsWith("//")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }

        // Attempt to prove the conclusion follows from the premises
        boolean result = LinearResolution.prove(filename);

        if (result) {
            System.out.println("\nTrue, the conclusion follows from the premises.");
        } else {
            System.out.println("\nFalse, the conclusion does not follow from the premises.");
        }

        // The proof result is already printed inside the prove method

      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }
  
