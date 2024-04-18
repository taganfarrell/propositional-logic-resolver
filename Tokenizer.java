/*
 * Tokenizer.java
 * Copyright (c) 2024 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Tokenizer class demonstrates the use of the regular expression that
 * breaks formulas written in our propositional languages into tokens.
 */

public class Tokenizer {

  /**
   * Compliments of StackOverflow.  The crazy looking regular expression
   * (regex) "(?:\\s+)|(?<=[()])|(?=[()])" lets us break a string
   * containing a propositional formula into tokens.  Below is the detailed
   * explanation from the article with edits.  Note that the vertical bar (|)
   * lets us specify alternatives, so it functions like OR or an if-else.
   *
   *   (?:\\s+)|(?<=[()])|(?=[()])
   *
   *     1st Alternative: (?:\\s+)
   *       (?:\\s+) is a non-capturing group, where \\s+ matches any white-
   *          space character in [\r\n\t\f ].  The quantifier + matches
   *          between one and unlimited times, as many times as possible,
   *          giving back as needed [greedy]
   *     2nd Alternative: (?<=[()])
   *       (?<=[()]) defines a positive look-behind, which asserts that the
   *          following regex can be matched: [()], which matches a single
   *          character present in the list (i.e., '(' or ')').
   *     3rd Alternative: (?=[()])
   *       (?=[()]) defines a positive look-ahead, which asserts that the
   *          following regex can be matched: [()], which matches a single
   *          character present in the list (i.e., '(' or ')').
   *
   * See: https://stackoverflow.com/questions/38164677/parse-numbers-and-parentheses-from-a-string
   *
   */

  public static void main( String[] args ) {
    try {
      String wff = "(and (cond p q) (cond q r))";
      System.out.println( wff );
      Scanner s = new Scanner( wff );
      s.useDelimiter( "(?:\\s+)|(?<=[()])|(?=[()])" );
      while ( s.hasNext() ) {
        System.out.println( s.next() );
      } // while
    } // try
    catch( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  } // main

} // Tokenizer class
