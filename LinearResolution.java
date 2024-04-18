import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LinearResolution {

    public static boolean prove(Clauses clauses) {
        // A set to keep track of all new clauses generated during the resolution process
        Set<Clause> newClauses = new HashSet<>();
    
        // Debug: Print initial clauses
        // System.out.println("Initial clauses: " + clauses);
    
        // Initially, add all the clauses from the input to the set of new clauses
        for (Clause clause : clauses) {
            newClauses.add(clause);
        }
    
        // A set to keep track of clauses that have been processed
        Set<Clause> processedClauses = new HashSet<>();
    
        while (!newClauses.isEmpty()) {
            // Debug: Print current new and processed clauses
            // System.out.println("New clauses: " + newClauses);
            // System.out.println("Processed clauses: " + processedClauses);
    
            // Move clauses from newClauses to processedClauses and attempt resolution
            Set<Clause> tempNewClauses = new HashSet<>(newClauses);
            for (Clause newClause : tempNewClauses) {
                // Debug: Print the clause being resolved
                // System.out.println("Resolving clause: " + newClause);
    
                // int count = 0;
                // Attempt to resolve the new clause against all processed clauses
                for (Clause processedClause : processedClauses) {
                    // Debug: Print the pair of clauses being attempted for resolution
                    // System.out.println("Attempting resolution between: " + newClause + " and " + processedClause);
    
                    Clause resolvent = clauses.resolveTwoClauses(processedClause, newClause);
                    if (resolvent != null) {
                        // Debug: Print the generated resolvent
                        // System.out.println("Generated resolvent: " + resolvent);
    
                        // Check if the resolvent is an empty clause
                        if (resolvent.getLiterals().isEmpty()) {
                            // System.out.println("Empty clause derived. Proof successful.");
                            return true; // The empty clause was derived
                        }
                        // Add the resolvent to newClauses if it's indeed new
                        // Debug: Check if the clause is already in newClauses or processedClauses
                        // Debug: Print current state of newClauses and processedClauses
                        // System.out.println("Current newClauses: " + newClauses);
                        // System.out.println("Current processedClauses: " + processedClauses);
                        // boolean isInNewClauses = newClauses.contains(resolvent);
                        // boolean isInProcessedClauses = processedClauses.contains(resolvent);
                        // System.out.println("Is in newClauses: " + isInNewClauses + ", Is in processedClauses: " + isInProcessedClauses);
                        if (!processedClauses.contains(resolvent) && !newClauses.contains(resolvent)) {
                            // System.out.println("adding resolvent to newClauses");
                            newClauses.add(resolvent);
                        }
                    }
                    // // Increment the counter after each iteration
                    // count++;
                    // System.out.println(count);
                    
                    // // Check if the counter reaches 3, if yes, break out of the loop
                    // if (count == 3) {
                    //     break;
                    // }
                }
                // Move the clause from newClauses to processedClauses
                newClauses.remove(newClause);
                processedClauses.add(newClause);
            }
        }
    
        // If the loop exits, it means no empty clause could be derived
        // System.out.println("No empty clause derived. Proof unsuccessful.");
        return false;
    }
    
    public static boolean prove(String filename) {
        Clauses clauses = new Clauses();
        Formula formula = null;
        Formula conclusion = null;
    
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("//")) { // Ignore comments and empty lines
                    // Update conclusion to the newest formula found each time through the loop
                    conclusion = new Formula();
                    conclusion.set(line); // Set the current formula as the potential conclusion
                }
            }
            // Convert all but the last formula (the actual conclusion) to CNF and add to clauses
            scanner = new Scanner(new File(filename)); // Reset scanner to read the file again
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.equals(conclusion.toString()) && !line.isEmpty() && !line.startsWith("//")) { // Check if the current line is not the conclusion
                    formula = new Formula();
                    formula.set(line);
                    formula.cnf(); // Convert the current formula to CNF
                    clauses.addAll(formula.getClauses()); // Add the clauses from the CNF to the clauses collection
                }
            }
            // Negate the actual conclusion and add its clauses
            Formula negatedConclusion = conclusion.negate();
            negatedConclusion.cnf();
            clauses.addAll(negatedConclusion.getClauses());
    
            scanner.close();
    
            // Print the formulas and converted clauses
            System.out.println("\nClauses in CNF:");
            for (Clause c : clauses) {
                System.out.println(c);
            }
    
            // Attempt the proof
            boolean proofResult = LinearResolution.prove(clauses);
    
            // Print the result
            // System.out.println("Conclusion follows from the premises: " + proofResult);
    
            return proofResult;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            return false;
        }
    }    

}
