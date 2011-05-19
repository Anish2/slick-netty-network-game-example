//Fuzzy engine for Java 0.1a
//Copyright (C) 2000  Edward S. Sazonov (esazonov@usa.com)

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

package de.iritgo.skillfull.fuzzy;

import java.util.*;
/**
 * Class for a block of fuzzy rules
 * @author: Edward Sazonov
 */
public class FuzzyBlockOfRules 
{
	
	//Original text representation of rules
	private java.lang.String[] textRules;

	//Pointer to the fuzzy engine
	private FuzzyEngine fuzzyEngine;


	//Vector of all rules within block
	Vector allRules = null;

	//Flag indicating a rule has fired
	boolean flagRuleFired = false;
/**
 * This function evaluates this block, returns a lava.lang.String 
 * containing evaluation result for every expression in the rule.
 * This function can be called only after parseBlock().
 */
public String evaluateBlockText() throws EvaluationException 
{

	//Reset the flag
	flagRuleFired = false;
	
	String output = new String();
	
	//Perform evaluation and execution of every rule
	for (Enumeration en = allRules.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyRule tempRule = (FuzzyRule)en.nextElement();

		    try
		    {
			    String s = tempRule.evaluateRuleText();
			    if(tempRule.isRuleFired())
			    	{
			    	output+=s+"\n";
			    	flagRuleFired = true;
			    	}
	  	  	}
	   		catch(Exception e)
	    	{
		    //Add exception handling
   		    System.out.println("Exception: "+e.getMessage());
   		    throw new EvaluationException(e.getMessage());
	    	}
	    
		}

	return output;
}
/**
 * This function evaluates this block, does not return any diagnostic information.
 * This function can be called only after parseBlock().
 */
public void evaluateBlock() throws EvaluationException 
{
	//Reset the flag
	flagRuleFired = false;

	//Perform evaluation and execution of every rule
	for (Enumeration en = allRules.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyRule tempRule = (FuzzyRule)en.nextElement();

		    try
		    {
	 	   	tempRule.evaluateRule();
	 	   	if(tempRule.isRuleFired())	flagRuleFired = true;
	  	  	}
	   		catch(Exception e)
	    	{
		    //Add exception handling
   		    System.out.println("Exception: "+e.getMessage());
   		    throw new EvaluationException(e.getMessage());
	    	}
	    
		}

}
/**
 * Returns true if any rule in the block has fired during a call to evaluateBlock()
 * or evaluateBlockText().
 * @return boolean
 */
public boolean isRuleFired() 
{
	return flagRuleFired;
}
/**
 * Constructor
 * @param rules[] one-dimensional array of java.lang.String, containing all rules that will constitute this block
 */
public FuzzyBlockOfRules(String [] rules) 
{
	super();
	textRules = rules;

	allRules = new Vector();

}
/**
 * Constructor
 * @param rules a java.lang.String, containing all rules that will constitute this block (separated by \n)
 */
public FuzzyBlockOfRules(String rules) 
{
	super();

	int start = 0;
	int end = 0;
	int length = rules.length();

	Vector ruleStrings = new Vector();

	while(end<length)
	{
	end=rules.indexOf("\n",start);
	if(end==-1)	end=length;
	ruleStrings.addElement(rules.substring(start,end));
	start=end+1;
	}

	textRules = new String [ruleStrings.size()];
	for(int i=0; i<ruleStrings.size(); i++)
		textRules[i]=(String)ruleStrings.elementAt(i);
	
	allRules = new Vector();

}
/**
 * Parse the rules in the block. 
 * This call is only possible after the block has been registered with a fuzzy engine.
 * @exception engine.RulesParsingException Exception is thrown if any rule within block
 * generates a parsing error
 */
public void parseBlock() throws RulesParsingException 
{
	//Check if fuzzyEngine is Ok
	if(fuzzyEngine==null)
		throw new RulesParsingException(" - Cannot parse; the block should be registered first: ");
	
	//Check is rules are Ok
	if(textRules==null)		
		throw new RulesParsingException(" - Cannot parse an empty block of rules: ");

	//Clear the rules
	allRules.removeAllElements();
	
	//Parse each rule
	for(int i=0; i<textRules.length; i++)
		allRules.addElement(fuzzyEngine.parseRule(textRules[i]));

}
/**
 * Set the pointer to the fuzzy engine.
 * This function is for the call FuzzyEngine.register(FuzzyBlockOfRules)
 * @param newFuzzyEngine Pointer to the fuzzy engine that will parse and compile this block of rules
 */
public void setFuzzyEngine(FuzzyEngine newFuzzyEngine) 
{
	fuzzyEngine = newFuzzyEngine;
}
}
