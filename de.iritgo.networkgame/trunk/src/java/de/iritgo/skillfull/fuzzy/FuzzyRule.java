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
 * Container for a fuzzy rule.
 * This class contains vectors of left-part and right-part expressions 
 * (before and after "then") for a fuzzy rule.
 */
public class FuzzyRule 
{
	//Label of this rule
	private String label=null;

	//Left and right sides 
	private Vector leftPartExpressions = null;
	private Vector rightPartExpressions = null;

	//Flag to indicate that this rules has fired
	private boolean flagRuleFired = false;

	//Nesting stack class
	private class StackElement
	{
		public double accumulatedResult;
		public boolean flagAND;
		public boolean flagOR;

		StackElement(double acc, boolean and, boolean or)
			{ accumulatedResult = acc; flagAND = and; flagOR = or;}
	}


	private double evaluationResult;
/**
 * Evaluate left part and execute right part of a pre-parsed rule.
 * Changes value of the private variable evaluationResult.
 * This call will set flagRuleFired to true if any expression fires.
 * @return java.lang.String Returns text representation of the rule
 * with all expressions evaluated.
 */
public String evaluateRuleText() throws EvaluationException 
{
	//Final result
	double accumulatedResult = 1.0;

	//Nesting Stack
	Stack nestingStack = new Stack();

	//Flags
	boolean flagAND = false;
	boolean flagOR = false;

	//Output string
	String out = new String();

	//Evaluate
	for (Enumeration en = leftPartExpressions.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyExpression tempExpression = (FuzzyExpression)en.nextElement();

	    //Extract flags
	    flagAND = tempExpression.flagAND;
	    flagOR = tempExpression.flagOR;

	    //If nesting up
	    for(int i=0; i<tempExpression.nestingUp; i++)
	    	{
			//Nesting
			nestingStack.push(new StackElement(accumulatedResult,flagAND,flagOR));
			flagAND=flagOR=false;
			accumulatedResult = 0.0;
	    	}

	    //Evaluate expression
	    double tempResult = 0.0;
	    try
	    {
	    	tempResult = tempExpression.evaluateExpression();

	    	//Add to output string
	    	out+=tempExpression.getTextExpression();
			String s = String.valueOf(tempResult);
			out += "(" + s.substring(0,s.length() > 3 ? 4 : 3) + ") ";
	    }
	    catch(Exception e)
	    {
		    //Add exception handling
		    System.out.println("Exception: "+e.getMessage());
		    throw new EvaluationException(e.getMessage());
	    }
	    

	    //If AND / OR / STORE operations
	    if(!flagAND && !flagOR)
	    	accumulatedResult = tempResult;
	    if(flagAND && !flagOR)
			accumulatedResult = accumulatedResult > tempResult ? tempResult : accumulatedResult;
	    if(!flagAND && flagOR)
			accumulatedResult = accumulatedResult < tempResult ? tempResult : accumulatedResult;

		//If nesting down
		for(int i=0; i<tempExpression.nestingDown; i++)
			{
			StackElement tempSE = (StackElement) nestingStack.pop();
			flagAND = tempSE.flagAND;
			flagOR = tempSE.flagOR;

	 	    if(flagAND && !flagOR)
				accumulatedResult = accumulatedResult > tempSE.accumulatedResult ? 
					tempSE.accumulatedResult : accumulatedResult;
	   		if(!flagAND && flagOR)
				accumulatedResult = accumulatedResult < tempSE.accumulatedResult ? 
					tempSE.accumulatedResult : accumulatedResult;
			}
		}

	evaluationResult = accumulatedResult;
	
	//Reset the flag
	flagRuleFired = false;
	
	//Execute assignments
	for (Enumeration en = rightPartExpressions.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyExpression tempExpression = (FuzzyExpression)en.nextElement();

		    try
		    {
	 	   	double temp = tempExpression.executeExpression(evaluationResult,getLabel());
			//Add to output string
	 	   	out+=tempExpression.getTextExpression();
			String s = String.valueOf(temp);
			out+="(" + s.substring(0,s.length() > 3 ? 4 : 3) + ") ";

			if(temp>0.0)	flagRuleFired = true;

	  	  	}
	   		catch(Exception e)
	    	{
		    //Add exception handling
		    System.out.println("Exception: "+e.getMessage());
		    throw new EvaluationException(e.getMessage());
	    	}
	    
		}
	return out;
}
/**
 * Evaluate left part and execute right part of a pre-parsed rule.
 * Changes value of the private variable evaluationResult.
 * This call will set flagRuleFired to true if any expression fires.
 */
public void evaluateRule() throws EvaluationException
{
	//Final result
	double accumulatedResult = 1.0;

	//Nesting Stack
	Stack nestingStack = new Stack();

	//Flags
	boolean flagAND = false;
	boolean flagOR = false;

	//Evaluate
	for (Enumeration en = leftPartExpressions.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyExpression tempExpression = (FuzzyExpression)en.nextElement();

	    //Extract flags
	    flagAND = tempExpression.flagAND;
	    flagOR = tempExpression.flagOR;

	    //If nesting up
	    for(int i=0; i<tempExpression.nestingUp; i++)
	    	{
			//Nesting
			nestingStack.push(new StackElement(accumulatedResult,flagAND,flagOR));
			flagAND=flagOR=false;
			accumulatedResult = 0.0;
	    	}

	    //Evaluate expression
	    double tempResult = 0.0;
	    try
	    {
	    	tempResult = tempExpression.evaluateExpression();

	    }
	    catch(Exception e)
	    {
		    //Add exception handling
		    System.out.println("Exception: "+e.getMessage());
		    throw new EvaluationException(e.getMessage());
	    }
	    

	    //If AND / OR / STORE operations
	    if(!flagAND && !flagOR)
	    	accumulatedResult = tempResult;
	    if(flagAND && !flagOR)
			accumulatedResult = accumulatedResult > tempResult ? tempResult : accumulatedResult;
	    if(!flagAND && flagOR)
			accumulatedResult = accumulatedResult < tempResult ? tempResult : accumulatedResult;

		//If nesting down
		for(int i=0; i<tempExpression.nestingDown; i++)
			{
			StackElement tempSE = (StackElement) nestingStack.pop();
			flagAND = tempSE.flagAND;
			flagOR = tempSE.flagOR;

	 	    if(flagAND && !flagOR)
				accumulatedResult = accumulatedResult > tempSE.accumulatedResult ? 
					tempSE.accumulatedResult : accumulatedResult;
	   		if(!flagAND && flagOR)
				accumulatedResult = accumulatedResult < tempSE.accumulatedResult ? 
					tempSE.accumulatedResult : accumulatedResult;
			}
		}

	evaluationResult = accumulatedResult;

	//Reset the flag
	flagRuleFired = false;
	
	//Execute assignments
	for (Enumeration en = rightPartExpressions.elements() ; en.hasMoreElements() ;) 
		{
	    FuzzyExpression tempExpression = (FuzzyExpression)en.nextElement();

		    try
		    {
	 	   		if(tempExpression.executeExpression(evaluationResult,getLabel())>0.0)
	 	   			flagRuleFired = true;
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
 * Returns the result of the left part evaluation.
 * @return double
 */
public double getEvaluationResult() 
{
	return evaluationResult;
}
/**
 * Return this rule's label.
 * @return java.lang.String
 */
public java.lang.String getLabel() {
	return label;
}
/**
 * Returns the java.lang.Vector containing all expression for the left part of the rule.
 * @return java.util.Vector
 */
public java.util.Vector getLeftPartExpressions() {
	return leftPartExpressions;
}
/**
 * Returns the java.lang.Vector containing all expression for the right part of the rule.
 * @return java.util.Vector
 */
public java.util.Vector getRightPartExpressions() 
{
	return rightPartExpressions;
}
/**
 * Returns true if the rule fired (during executeRule call).
 * @return boolean
 */
public boolean isRuleFired() 
{
	return flagRuleFired;
}
/**
 * Constructor.
 * @param lb This rule label (java.lang.String).
 */
public FuzzyRule(String lb) 
{
	label = lb;
	leftPartExpressions = new Vector(); 
	rightPartExpressions = new Vector();
}
/**
 * Set label for this rule.
 * @param newLabel java.lang.String
 */
public void setLabel(java.lang.String newLabel) 
{
	label = newLabel;
}
/**
 * Constructor.
 */
public FuzzyRule() 
{
	leftPartExpressions = new Vector();
	rightPartExpressions = new Vector();
}
}
