import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clause {

    // List to store the literals of the clause
    private List<String> literals;

    // Constructor
    public Clause() {
        this.literals = new ArrayList<>();
    }

    // Method to add a literal to the clause
    public void addLiteral(String literal) {
        // System.out.println("Clause addLiteral--");
        literals.add(literal);
    }

    // Method to add a negated literal to the clause
    public void addNegatedLiteral(String literal) {
        // System.out.println("Clause addNegatedLiteral--");
        literals.add("not " + literal);
    }

    // Method to get a string representation of the clause
    @Override
    public String toString() {
        // Start with the opening curly brace
        StringBuilder sb = new StringBuilder("{");
        // Iterate over the literals to append them to the string
        for (int i = 0; i < literals.size(); i++) {
            sb.append(literals.get(i));
            // Add a comma if this is not the last literal
            if (i < literals.size() - 1) {
                sb.append(", ");
            }
        }
        // End with the closing curly brace
        sb.append("}");
        return sb.toString();
    }

    // Getter for the literals (optional, depending on further use cases)
    public List<String> getLiterals() {
        // System.out.println("Clause getLiterals--");
        return literals;
    }

    // Method to set the Clause to the clause that the specified String represents.
    public void set(String clauseString) {
        // System.out.println("Clause set--");
        // Remove the surrounding curly braces and trim whitespace.
        clauseString = clauseString.replaceAll("[{}]", "").trim();
        
        // Use a regular expression to match literals, including negated ones.
        Pattern pattern = Pattern.compile("not\\s+\\w+|\\w+");
        Matcher matcher = pattern.matcher(clauseString);
        
        // Clear existing literals before parsing new ones.
        literals.clear();

        while (matcher.find()) {
            // Extract the matched literal.
            String literal = matcher.group().trim();

            // Add the literal to the list. If it starts with 'not', it's a negated literal.
            if (literal.startsWith("not")) {
                addNegatedLiteral(literal.substring(4).trim());
            } else {
                addLiteral(literal);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Clause other = (Clause) obj;
        return literals.equals(other.literals);
    }

    @Override
    public int hashCode() {
        // Assuming literals properly implements hashCode
        return literals.hashCode();
    }
}
