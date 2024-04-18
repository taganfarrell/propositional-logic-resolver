/*
 * Node.java
 * Copyright (c) 2017 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

 import java.io.Serializable;

 public class Node implements Serializable {
 
   Type type;
   String s;
   Node left;
   Node right;
 
   // Constructor for propositions
   public Node(String s) {
     this.type = Type.PROP;
     this.s = s;
     this.left = null;
     this.right = null;
   }
 
   // Constructor for unary operators
   public Node(Type type, Node left) {
     if (type != Type.NOT) {
       throw new IllegalArgumentException("Unary operator must be NOT.");
     }
     this.type = type;
     this.s = null;
     this.left = left;
     this.right = null;
   }
 
   // Constructor for binary operators
   public Node(Type type, Node left, Node right) {
     if (type == Type.PROP || type == Type.NOT) {
       throw new IllegalArgumentException("Binary operator must be AND, OR, or COND.");
     }
     this.type = type;
     this.s = null;
     this.left = left;
     this.right = right;
   }
 
   @Override
   public String toString() {
       if (type == Type.PROP) {
           // Ensure propositions are always enclosed in parentheses
           return "(" + s + ")";
       } else if (type == Type.NOT) {
           // Correctly handle unary operator, ensuring the operand is also enclosed
           String operandStr = left != null ? left.toString() : "()";
           return "(" + type.toString().toLowerCase() + " " + operandStr + ")";
       } else {
           // Handle binary operators, ensuring both operands are also enclosed
           String leftStr = left != null ? left.toString() : "()";
           String rightStr = right != null ? right.toString() : "()";
           return "(" + type.toString().toLowerCase() + " " + leftStr + " " + rightStr + ")";
       }
   }
   
    
 }
 