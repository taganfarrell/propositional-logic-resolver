import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clauses extends ArrayList<Clause> {

    // Constructor
    public Clauses() {
        super();
    }

    // Adding a new Clause to the list of Clauses
    public void addClause(Clause clause) {
        // System.out.println("Clauses addClause--");
        this.add(clause);
    }

    // Method to convert the formula to clausal form and return the resulting clauses.
    public Clauses getClauses() {
        // System.out.println("Clauses getClauses--");
        Clauses clauses = new Clauses();
        
        // Use a regular expression to match clauses within the formula.
        String formulaString = this.toString();
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(formulaString);
        
        while (matcher.find()) {
            // Extract the matched clause.
            String clauseString = matcher.group(1).trim(); // The content within the parentheses.
            
            // Create a new Clause object and set it with the extracted string.
            Clause clause = new Clause();
            clause.set(clauseString);
            
            clauses.addClause(clause);
        }
        
        return clauses; // Return the set of clauses.
    }

    // Override the toString method to return a string representation of the list of clauses
    @Override
    public String toString() {
        // System.out.println("Clauses toString--");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            sb.append(this.get(i).toString());
            if (i < this.size() - 1) {
                sb.append(" AND "); // Append the logical AND symbol between clauses
            }
        }
        return sb.toString();
    }

    // Method that resolves all of the clauses in Clauses with the Clause clause.
    public Clauses resolve(Clause clause) {
        // System.out.println("Clauses resolve--");
        // Create a new Clauses object to store the resolvents.
        Clauses resolvents = new Clauses();

        // Iterate over all clauses in Clauses.
        for (Clause currentClause : this) {
            // Attempt to resolve currentClause with the given clause.
            Clause resolvent = resolveTwoClauses(currentClause, clause);

            // If the resolvent is not null, add it to the resolvents.
            if (resolvent != null) {
                resolvents.addClause(resolvent);
            }
        }

        // Return the set of resolvents. If empty, it implies no resolution was possible.
        return resolvents;
    }

    // Helper method to resolve two clauses.
    public Clause resolveTwoClauses(Clause clause1, Clause clause2) {
        // System.out.println("Resolving between clause1: " + clause1 + " and clause2: " + clause2);
        Set<String> literals1 = new HashSet<>(clause1.getLiterals());
        Set<String> negatedLiterals1 = new HashSet<>();
        Set<String> literals2 = new HashSet<>(clause2.getLiterals());
        Set<String> negatedLiterals2 = new HashSet<>();
    
        // Populate sets for negated literals
        for (String literal : literals1) {
            // System.out.println(literal);
            // Adjust check to account for parentheses
            if (literal.startsWith("(not ")) {
                // Extract the literal name without "not " and the enclosing parentheses
                negatedLiterals1.add(literal.substring(5, literal.length() - 1));
            }
        }
        for (String literal : literals2) {
            // System.out.println(literal);
            // Adjust check to account for parentheses
            if (literal.startsWith("(not ")) {
                // System.out.println("starts with not");
                // Extract the literal name without "not " and the enclosing parentheses
                negatedLiterals2.add(literal.substring(5, literal.length() - 1));
            }
        }
    
        // System.out.println("Literals in clause1: " + literals1 + ", Negated literals in clause1: " + negatedLiterals1);
        // System.out.println("Literals in clause2: " + literals2 + ", Negated literals in clause2: " + negatedLiterals2);
    
        // Attempt to resolve
        for (String literal : literals1) {
            String negatedLiteral;
            if (literal.startsWith("(not ")) {
                // If it already starts with "not", remove the "not " part to get its positive form
                negatedLiteral = literal.substring(5, literal.length() - 1); // Remove "(not " and ")" to get the literal
            } else {
                // If it's not already negated, prepend "not " to negate it
                negatedLiteral = "(not " + literal + ")";
            }
            // System.out.println("negatedLiteral: " + negatedLiteral);
            if (literals2.contains(negatedLiteral) || negatedLiterals2.contains(literal)) {
                // System.out.println("Found complementary pair: " + literal);
                // System.out.println("Literal: " + literal);
                // System.out.println("Negated literal: " + negatedLiteral);
                Clause resolvent = new Clause();
                
                // Add all literals from both clauses except the complementary pair
                for (String l : literals1) {
                    if (!l.equals(literal) && !l.equals(negatedLiteral)) {
                        resolvent.addLiteral(l);
                        // System.out.println("Literal added1: " + l);
                    }
                }
                for (String l : literals2) {
                    if (!l.equals(literal) && !l.equals(negatedLiteral)) {
                        resolvent.addLiteral(l);
                        // System.out.println("Literal added2: " + l);
                    }
                }

                // System.out.println("Generated resolvent: " + resolvent);
                return resolvent;
            }
        }
        return null;
    }
}
