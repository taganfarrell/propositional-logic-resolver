/*
 * Formula.java
 * Copyright (c) 2022 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

 import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Formula {

  private Node root;

  public Formula() { }

  public void set(String s) {
    // System.out.println("Set formula: " + s); // Print the formula being set
    // System.out.println("Formula set--");
    parse(new Scanner(s));
  }

  private void parse(Scanner s) {
    // System.out.println("Start parsing");
    s.useDelimiter("(?:\\s+)|(?<=[()])|(?=[()])");
    Stack<Node> nodes = new Stack<>();
    Stack<Object> operators = new Stack<>();

    while (s.hasNext()) {
        String token = s.next().trim();
        // System.out.println("Token: " + token); // Print each token as it's read
        if (token.isEmpty()) continue;

        if (token.equals("(")) {
            operators.push(token);
        } else if (token.equals(")")) {
            while (!operators.isEmpty() && !operators.peek().equals("(")) {
                handleOperator(nodes, operators.pop().toString());
            }
            operators.pop(); // Remove "("
        } else if (isOperator(token)) {
            while (!operators.isEmpty() && getPrecedence(token) <= getPrecedence(operators.peek().toString())) {
                handleOperator(nodes, operators.pop().toString());
            }
            operators.push(token);
        } else { // Operand
            nodes.push(new Node(token));
        }
    }

    while (!operators.isEmpty()) {
        handleOperator(nodes, operators.pop().toString());
    }

    if (!nodes.isEmpty()) {
        root = nodes.pop();
        // System.out.println("Parsing complete. Root: " + root); // Print the root of the tree after parsing
    }
  }

  private void handleOperator(Stack<Node> nodes, String operator) {
    // System.out.println("Handling operator: " + operator); // Print the operator being handled
    // System.out.println("Formula handleOperator--");
    if (operator.equals("not")) {
        // Unary operator handling
        Node operand = nodes.pop();
        // System.out.println("Unary operator operand: " + operand); // Print operand for unary operator
        nodes.push(new Node(getTypeFromOperator(operator), operand)); // Adjusted to properly handle unary operators
    } else {
        // Binary operator handling
        Node right = nodes.pop();
        Node left = nodes.pop(); // This will always have a value for binary operators
        // System.out.println("Binary operator operands: " + left + ", " + right); // Print operands for binary operator
        nodes.push(new Node(getTypeFromOperator(operator), left, right));
    }
  }

  private boolean isOperator(String token) {
    // System.out.println("Formula isOperator--");
      return token.equals("not") || token.equals("and") || token.equals("or") || token.equals("cond");
  }

  private int getPrecedence(String operator) {
      switch (operator) {
          case "not":
              return 3;
          case "and":
              return 2;
          case "or":
              return 2;
          case "cond":
              return 1;
          default:
              return 0;
      }
  }

  private Type getTypeFromOperator(String operator) {
      switch (operator) {
          case "not":
              return Type.NOT;
          case "and":
              return Type.AND;
          case "or":
              return Type.OR;
          case "cond":
              return Type.COND;
          default:
              throw new IllegalArgumentException("Unknown operator: " + operator);
      }
  }

  // Method to start the CNF conversion process
  public void cnf() {
    // System.out.println("Formula cnf--");
    removeImplications(this.root);
    applyDeMorgans(this.root);
    simplifyDoubleNegations(this.root);
    distributeOrOverAnd(this.root);
    flattenConjunctionsAndDisjunctions(this.root);
    // eliminateDuplicatesAndTautologies(this.root);
    // combineClauses(this.root);
  }

  // Recursive method to remove implications
  private void removeImplications(Node node) {
    if (node == null) {
        return;
    }
    if (node.type == Type.COND) {

        node.type = Type.OR; // Replace implication with OR
        node.left = new Node(Type.NOT, node.left);
    }
    // Recursively apply to left and right children
    removeImplications(node.left);
    removeImplications(node.right);
  }
  private void applyDeMorgans(Node node) {
    if (node == null) {
        return;
    }
    // Apply De Morgan's laws if we encounter a negation of a conjunction or disjunction
    if (node.type == Type.NOT && node.left != null) {
        Node child = node.left;
        if (child.type == Type.AND || child.type == Type.OR) {
            // Swap AND to OR or OR to AND
            node.type = (child.type == Type.AND) ? Type.OR : Type.AND;
            // Apply negation to the child nodes
            node.left = new Node(Type.NOT, child.left);
            node.right = new Node(Type.NOT, child.right);
            // Recursively apply De Morgan's to the new negations
            applyDeMorgans(node.left);
            applyDeMorgans(node.right);
            // Remove the now unnecessary child node
            return;
        }
    }
    // Recursively apply to children
    applyDeMorgans(node.left);
    applyDeMorgans(node.right);
  }

  private void distributeOrOverAnd(Node node) {
      if (node == null) {
          return;
      }

      // Initially, no changes are made
      boolean changed;

      do {
          changed = false; // Reset the flag for each iteration

          // Check if distribution needs to be applied
          if (node.type == Type.OR) {
              if (node.left != null && node.left.type == Type.AND) {
                  // Apply distribution logic for the left child
                  node = distribute(node); // Assume this method correctly applies the distribution
                  changed = true;
              }
              if (node.right != null && node.right.type == Type.AND && !changed) {
                  // Apply distribution logic for the right child, only if no change was made in the previous step
                  node = distribute(node);
                  changed = true;
              }
          }

          // The loop will continue if a change was made, indicating the structure was modified and needs reassessment
      } while (changed);

      // Recurse on children after fully handling the current node to ensure all parts of the tree are processed
      distributeOrOverAnd(node.left);
      distributeOrOverAnd(node.right);
  }

  private Node distribute(Node node) {
    if (node == null) return null;
    
    // Check if we need to distribute OR over AND on the left side
    if (node.left != null && node.left.type == Type.AND) {
        // Create new OR nodes
        Node leftOr = new Node(Type.OR, node.left.left, node.right);
        Node rightOr = new Node(Type.OR, node.left.right, node.right);
        // Create new AND node with the new OR nodes as children
        node = new Node(Type.AND, distribute(leftOr), distribute(rightOr));
    }

    // Check if we need to distribute OR over AND on the right side
    if (node.right != null && node.right.type == Type.AND) {
        // Create new OR nodes
        Node leftOr = new Node(Type.OR, node.left, node.right.left);
        Node rightOr = new Node(Type.OR, node.left, node.right.right);
        // Create new AND node with the new OR nodes as children
        node = new Node(Type.AND, distribute(leftOr), distribute(rightOr));
    }

    return node;
  }

  private Node cloneNode(Node node) {
    if (node == null) {
        return null;
    }
    // Handle proposition nodes
    if (node.type == Type.PROP) {
        return new Node(node.s);
    }
    // Handle unary operator nodes specifically
    if (node.type == Type.NOT) {
        return new Node(node.type, cloneNode(node.left));
    }
    // Handle binary operator nodes
    // This assumes that for binary operators, both children should exist.
    // If your logic allows binary operators to have null children, you'd need additional checks.
    return new Node(node.type, cloneNode(node.left), cloneNode(node.right));
  }

  // Method to set the root of the expression tree manually, for testing purposes
  public void setRoot(Node root) {
    // System.out.println("Formula setRoot--");
    this.root = root;
  }

  private void simplifyDoubleNegations(Node node) {
    // Base case: if the current node is null, simply return.
    if (node == null) {
        return;
    }

    // Check if the current node is a 'NOT' node negating another 'NOT' node.
    if (node.type == Type.NOT && node.left != null && node.left.type == Type.NOT) {
        // Simplify 'NOT NOT X' to 'X' by bypassing the first 'NOT' node.
        // Here, 'node' is the first 'NOT', and 'node.left' is the second 'NOT'.
        // We replace the current 'NOT' node with its grandchild ('X').
        Node grandChild = node.left.left; // This is 'X' in 'NOT NOT X'.

        node.type = grandChild.type; // Update the type of the current node to 'X's type.
        node.s = grandChild.s; // Copy the proposition name if 'X' is a proposition.
        node.left = grandChild.left; // Update the left child.
        node.right = grandChild.right; // Update the right child.
    }

    // Recursively simplify double negations in the left and right subtrees.
    simplifyDoubleNegations(node.left);
    simplifyDoubleNegations(node.right);

    // Additional step to handle cases where simplification alters the parent node's children.
    // For example, if after simplification, the current node's left child becomes a double negation.
    if (node.left != null && node.left.type == Type.NOT && node.left.left != null && node.left.left.type == Type.NOT) {
        Node newLeft = node.left.left.left; // Bypass the double 'NOT'.
        node.left = newLeft;
    }
    // Similar for the right child.
    if (node.right != null && node.right.type == Type.NOT && node.right.left != null && node.right.left.type == Type.NOT) {
        Node newRight = node.right.left.left; // Bypass the double 'NOT'.
        node.right = newRight;
    }
  }

  private void flattenConjunctionsAndDisjunctions(Node node) {
    if (node == null) {
        return;
    }

    // Recursively flatten left and right children first
    flattenConjunctionsAndDisjunctions(node.left);
    flattenConjunctionsAndDisjunctions(node.right);

    // Flatten this node if it's an AND or OR and if its children are of the same type
    flattenNode(node);
}

// Helper method to flatten a node with its children if they are of the same type
private void flattenNode(Node node) {
    if (node.type != Type.AND && node.type != Type.OR) {
        return; // Only AND and OR nodes should be flattened
    }

    if (node.left != null && node.left.type == node.type) {
        // Merge left child's children into node
        mergeChildrenIntoNode(node, node.left);
    }
    if (node.right != null && node.right.type == node.type) {
        // Merge right child's children into node
        mergeChildrenIntoNode(node, node.right);
    }
}

// Merge children of the childNode into parentNode, assuming both are of the same type (AND or OR)
private void mergeChildrenIntoNode(Node parentNode, Node childNode) {
    ArrayList<Node> children = new ArrayList<>();
    collectChildren(childNode, children); // Collect all grandchildren

    if (parentNode.left == childNode) {
        parentNode.left = null; // Clear the link to avoid duplication
    } else if (parentNode.right == childNode) {
        parentNode.right = null; // Clear the link to avoid duplication
    }

    // Re-attach collected children directly to the parentNode
    for (Node grandchild : children) {
        if (parentNode.left == null) {
            parentNode.left = grandchild;
        } else if (parentNode.right == null) {
            parentNode.right = grandchild;
        } else {
            // If both children are occupied, create a new node of the same type to extend the tree
            parentNode.right = new Node(parentNode.type, parentNode.right, grandchild);
        }
    }
}

// Collect all descendants of a node recursively, ignoring AND/OR structure
private void collectChildren(Node node, ArrayList<Node> children) {
    if (node == null) return;
    if (node.type == Type.AND || node.type == Type.OR) {
        collectChildren(node.left, children);
        collectChildren(node.right, children);
    } else {
        children.add(node);
    }
}

  private Node pullUpGrandchild(Node node) {
      // Assuming the node has at least one child of the same type (AND/AND or OR/OR).
      // This method will pull up a grandchild to replace the child node.
      // Note: This approach works under specific conditions and may need adjustment based on your tree structure.
      if (node.left != null && node.type == node.left.type) {
          return node.left; // Pull up the left grandchild.
      } else if (node.right != null && node.type == node.right.type) {
          return node.right; // Pull up the right grandchild.
      }
      return node; // Return the node itself if no pull-up is performed.
  }


  // Break point ---------------------------------

  public Node getRoot() {
    // System.out.println("Formula getRoot--");
    return this.root;
  }

  @Override
  public String toString() {
    // System.out.println("Formula toString--");
    return treeToString(this.root);
  }

  private String treeToString(Node node) {
    if (node == null) {
        return "";
    }
    if (node.type == Type.PROP) {
        // Propositions are not enclosed in parentheses unless they are an operand in an expression
        return  node.s;
    } else {
        // Enclose operations in parentheses
        String leftStr = node.left != null ? treeToString(node.left) : "";
        String rightStr = node.right != null ? treeToString(node.right) : "";
        // For unary operators, don't include the right operand
        if (node.type == Type.NOT) {
            return "(" + node.type.toString().toLowerCase() + " " + leftStr + ")";
        } else {
            // For binary operators, include both operands
            // return "(" + leftStr + " " + node.type.toString().toLowerCase() + " " + rightStr + ")";
            return "(" + node.type.toString().toLowerCase() + " " + leftStr + " " + rightStr + ")";
        }
    }
} 

  // Method to convert the formula to clausal form and return the resulting clauses.
  public Clauses getClauses() {
    Clauses clauses = new Clauses();

    // Recursively collect clauses from the formula
    collectClauses(root, clauses);

    return clauses;
  }

  // Helper method to collect clauses from a formula
  private void collectClauses(Node node, Clauses clauses) {
      if (node == null) {
          return;
      }

      // If the node is an OR, it is part of a clause, and we collect literals.
      if (node.type == Type.OR) {
          Clause clause = new Clause();
          collectLiterals(node, clause);
          clauses.addClause(clause);
      } else if (node.type == Type.AND) {
          // If the node is an AND, its children are separate clauses.
          collectClauses(node.left, clauses);
          collectClauses(node.right, clauses);
      } else {
          // If the node is a literal or a NOT node, it is a single literal clause.
          Clause clause = new Clause();
          clause.addLiteral(node.toString());
          clauses.addClause(clause);
      }
  }

  // Helper method to collect literals for a single clause
  private void collectLiterals(Node node, Clause clause) {
      if (node == null) {
          return;
      }

      // If the node is OR, collect literals from both sides
      if (node.type == Type.OR) {
          collectLiterals(node.left, clause);
          collectLiterals(node.right, clause);
      } else {
          // If the node is a literal or NOT, add it to the clause
          clause.addLiteral(node.toString());
      }
  }

  // Method to negate the entire formula
  public Formula negate() {
    Formula negatedFormula = new Formula();

    // Negating the root of the formula. If the root is a NOT, remove it.
    if (this.root != null && this.root.type == Type.NOT) {
        // Double negation elimination: just take the child of the NOT node
        negatedFormula.root = this.root.left;
    } else {
        // Otherwise, create a new NOT node with the current root as its child
        negatedFormula.root = new Node(Type.NOT, this.root);
    }

    return negatedFormula;
  }
}

 
