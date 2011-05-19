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
 * This class evaluates rules given by string variables.
 * Evaluation is performed either on a single rule or a block of rules.
 */
public class FuzzyEngine 
{
	//Action constants	
	private final int IF = 1;
	private final int THEN = 2;
	private final int IS = 3;
	private final int SET = 4;
	private final int AND = 5;
	private final int OR = 6;
	private final int LEFTP = 7;
	private final int RIGHTP = 8;
	private final int NOP = 9;
	private final int EXECUTE = 10;
	private final int HEDGE = 11;
	private final int RULE = 12;
	private final int UNDEFINED = 14;
	private final int WEIGHT = 15;
	private final int LV = 16;

	//Common subStates of the engine
	private final int READY = 100;
	private final int LV_READ = 101;
	private final int IS_READ = 102;
	private final int NOT_READ = 103;
	private final int HEDGE_READ = 104;
	private final int EXCEPTION = 105;

	//subStates of LABEL state
	private final int STORE_LABEL = 200;

	//subStates of EVALUATION state
	private final int COMPLETE_EVALUATION = 300;

	//subStates of EXECUTION state
	private final int RULE_READ = 400;
	private final int LABEL_READ = 401;
	private final int COMPLETE_EXECUTION = 402;

	//Fuzzy engine states
	private final int LABEL = 500;
	private final int EVALUATION = 501;
	private final int EXECUTION = 502;
	
	//Nesting stack class
	private class StackElement
	{
		public double accumulatedResult;
		public boolean flagAND;
		public boolean flagOR;

		StackElement(double acc, boolean and, boolean or)
			{ accumulatedResult = acc; flagAND = and; flagOR = or;}
	}

	//Hashtables
	private Hashtable lvHash = null;
	private Hashtable controlHash = null;
	private Hashtable hedgesHash = null;
	private Hashtable labelWeightsHash = null;

	//Global engine state
	private int engineState = UNDEFINED;

	//Nesting stack
	private Stack nestingStack = null;
	
	private boolean flagRuleFired = false;
/**
 * Add a hedge (derived from engine.Hedge) to the engine
 * @param hedge Implementation of a hedge
 */
public void addHedge(Hedge hedge) 
{
	hedgesHash.put(hedge.getName(),hedge);
}
/**
 * Evaluate block of rules (engine.FuzzyBlockOfRules)
 * @param block An instance of engine.FuzzyBlockOfRules
 */
public void evaluateBlock(FuzzyBlockOfRules block) throws EvaluationException
{
	block.evaluateBlock();
}
/**
 * Parse and evaluate a fuzzy rule in text form.
 * Input values for the linguistic variables participating in the rule
 * have to be set before calling this function.
 * Call to FuzzyEngine.isRuleFired() will return "true" if the rule fired.
 * @param rule String containing the rule
 */
public String evaluateRule(String rule) throws RulesParsingException
{
	//Reset the flag
	flagRuleFired = false;
	
	//Split input string into tokens and return delimeters along with tokens
	java.util.StringTokenizer tokens = new java.util.StringTokenizer(rule.trim(),new String("( )"),true);

	//Result string
	String result = null;
	
	try
	{
		result = parseExpression(tokens,null); 
	}
	catch(RulesParsingException e)
	{
		throw new RulesParsingException("\nError parsing rule: "+rule+"\n"+e.getMessage());
	}


	if(isRuleFired())	return "Fired: "+result;
	else				return null;
}
/**
 * Returns "true" if a rule has fired during the last call to FuzzyEngine.evaluateRule(String).
 * If a block of rules has been evaluated, the function FuzzyBlockOfRules.isRuleFired()
 * should be called instead.
 * @return "True" if the last evaluated rules has fired, "false" if not.
 */
public boolean isRuleFired() 
{
	return flagRuleFired;
}
/**
 * Parse a rule like: if a is b and/or hedge, hedge c is d then e is f.
 * Recursive nesting for multilevel logical dependencies. Recursion is organized
 * by employing nesting Stack instead of recursive calls because
 * it allows easy storage of compiled rules (in block parsing mode)
 * Two modes of operation (rule evaluation or block parsing mode) are determined
 * by the value of the parameter "rule": if "rule" is equal to null, then the engine is in
 * rule evaluation mode and input values of all linguistic variables should be set 
 * for the call to complete successfully. If "rule" points to an instance of FuzzyRule,
 * then only parsing is performed without performing actual evaluation of the rule.
 * Results of the parsing  are stored in the "rule".
 * @param tokens java.util.StringTokenizer Text representation of a fuzzy rule split into tokens.
 * @param rule FuzzyRule Container for the parsed rule (when operating in block parsing mode)
 * @return parsedRule java.lang.String Textual results of parsing are returned for review
 */
private String parseExpression(java.util.StringTokenizer tokens,FuzzyRule rule)
			 throws RulesParsingException
{

	//Conrol Word
	int controlWord = NOP;

	//Current state of the engine
	int engineState = LABEL;

	//Current subState
	int engineSubState = READY;

	//Current linguistic variable
	LinguisticVariable lVariable = null;

	//Current hedge
	Hedge hVariable = null;

	//Current membersip function by name
	String mVariable = null;

	//Storage variables
	double accumulatedResult = 0.0;
	String currentToken = null;

	//Flags
	boolean flagAND = false;
	boolean flagOR = false;

	//Nesting up counter - for block parsing mode
	int nestingUp = 0;

	//Hedges buffer
	Stack hedgesBuffer = new Stack();

	//Labels stuff
	String thisRuleLabel = null;
	String changeRuleLabel = null;

	//Output string
	String out = new String();

	//Process a rule
while(tokens.hasMoreTokens())
{
	//Reset control word
	controlWord = UNDEFINED;

	//Read next token
	currentToken = tokens.nextToken();

	//Add to the output string
	out+=currentToken;

	//Check control words hastable
	Object temp = controlHash.get(currentToken);
	if(temp!=null)	controlWord=((Integer)temp).intValue();
	else
	{
		//Check LV hashtable
		temp = lvHash.get(currentToken);
		if(temp!=null)
			{
			controlWord=LV;
			lVariable = (LinguisticVariable)temp;
			}
		else
		{
			//Check hedges
			temp = hedgesHash.get(currentToken);
			if(temp!=null)
				{
				controlWord=HEDGE;
				hVariable = (Hedge)temp;
				}
		}
	}

	//Switch according to the state/subState
	if(controlWord!=NOP)
	{
	switch(engineState)
	{
	case LABEL:
		switch(engineSubState)
		{
//LABEL:READY --------------------------------------------------------------------------------------------------
		case READY:
			switch(controlWord)
			{
				case LV:
				case HEDGE:
					throw new RulesParsingException("- A label cannot be the same as an LV or a hedge: "+out);
				case IF:
					engineState = EVALUATION;
					break;
				case SET:
					accumulatedResult = 1.0;
					engineState = EXECUTION;
					break;
				case UNDEFINED:
					thisRuleLabel = currentToken;
					engineSubState = STORE_LABEL;
					
					//Block parsing mode
					if(rule!=null)
						rule.setLabel(currentToken);
					
					break;
				default:
					throw new RulesParsingException(" - A rule should start with a label of an 'if': "+out);
			}
			break;
//LABEL:STORE_LABEL --------------------------------------------------------------------------------------------
		case STORE_LABEL:
			switch(controlWord)
			{
				case IF:
					//Store label in the label's hash and create a copy of WEIGHT LV for that label
					if(!labelWeightsHash.containsKey(thisRuleLabel))
						{
						//retrive WEIGHT LV
						LinguisticVariable tempLV = (LinguisticVariable)lvHash.get("weight");
						if(tempLV==null)	throw new RulesParsingException(" - WEIGHT LV is not registered but required for LABELS: "+out);
						
						tempLV = tempLV.copy();
						tempLV.setLVName(thisRuleLabel);
						labelWeightsHash.put(thisRuleLabel,tempLV);
						}
					engineState = EVALUATION;
					engineSubState = READY;
					break;
				case SET:
					//Store label in the label's hash and create a copy of WEIGHT LV for that label
					if(!labelWeightsHash.containsKey(thisRuleLabel))
						{
						//retrive WEIGHT LV
						LinguisticVariable tempLV = (LinguisticVariable)lvHash.get("weight");
						if(tempLV==null)	throw new RulesParsingException(" - WEIGHT LV is not registered but required for LABELS: "+out);
						
						tempLV = tempLV.copy();
						tempLV.setLVName(thisRuleLabel);
						labelWeightsHash.put(thisRuleLabel,tempLV);
						}
					accumulatedResult = 1.0;
					engineState = EXECUTION;
					engineSubState = READY;
					break;
				default:
					throw new RulesParsingException(" - Incorrect LABEL: "+out);
			}
			break;
		}
		break;
	case EVALUATION:
		switch(engineSubState)
		{
//EVALUATION:READY ---------------------------------------------------------------------------------------------
		case READY:
			switch(controlWord)
			{
				case LV:
					engineSubState = LV_READ;
					break;
				case AND:
					if(!flagAND && !flagOR)
						flagAND = true;
					else
						throw new RulesParsingException(" - Incorrect AND/OR operation"+out);
					break;
				case OR:
					if(!flagAND && !flagOR)
						flagOR = true;
					else
						throw new RulesParsingException(" - Incorrect AND/OR operation"+out);
					break;
				case LEFTP:
					//Nesting
					//Block parsing mode
					if(rule!=null)	nestingUp++;
					//Rule evaluation mode
					else
					{
					nestingStack.push(new StackElement(accumulatedResult,flagAND,flagOR));
					accumulatedResult = 0.0;
					flagAND = false;
					flagOR = false;
					}
					break;
				case RIGHTP:
					//Block parsing mode
					if(rule!=null)	
						((FuzzyExpression)rule.getLeftPartExpressions().lastElement()).nestingDown++;
					//Rule evaluation mode
					else
					{
					//Check for hanging AND/OR operations
					if(flagAND || flagOR)
						throw new RulesParsingException(" - Unmatched AND/OR operation: "+out);
				
					//Return from nesting
					StackElement tempSE = (StackElement) nestingStack.pop();
					flagAND = tempSE.flagAND;
					flagOR = tempSE.flagOR;

					//Add to display string
					String s = String.valueOf(accumulatedResult);
					out = out + "(" + s.substring(0,s.length() > 3 ? 4 : 3) + ") ";
	
					//A hedge cannot appear at this place
					//If both flagAND and flahOR are not set, keep the accumulatedResult
					if(flagAND)
						{
						flagAND = false;
						accumulatedResult = accumulatedResult > tempSE.accumulatedResult ? 
							tempSE.accumulatedResult : accumulatedResult;
						}
					if(flagOR)
						{
						flagOR = false;
						accumulatedResult = accumulatedResult < tempSE.accumulatedResult ? 
							tempSE.accumulatedResult : accumulatedResult;
						}
					}
					break;
				case THEN:
					engineState = EXECUTION;
					break;
				default:
					throw new RulesParsingException(" - Incorrect operation: "+out);
			}
			break;
//EVALUATION:LV_READ -------------------------------------------------------------------------------------------
		case LV_READ:
			//the next item should be 'is' - everything else is an exception
			switch(controlWord)
			{
				case IS:
					engineSubState = IS_READ;
					break;
				default:
					throw new RulesParsingException(" - only IS may be present at this place: "+out);
			}
			break;
//EVALUATION:IS_READ -------------------------------------------------------------------------------------------
		case IS_READ:
			//the next item may be a hedge or a membership function
			switch(controlWord)
			{
				case HEDGE:
					hedgesBuffer.push(hVariable);
					engineSubState = HEDGE_READ;
					break;
				case UNDEFINED:
					engineSubState = COMPLETE_EVALUATION;
					mVariable = currentToken;
					break;
				default:
					throw new RulesParsingException(" - Incorrect operation after IS: "+out);
					
			}
			break;
//EVALUATION:HEDGE_READ ----------------------------------------------------------------------------------------
		case HEDGE_READ:
			//the next item can only be a membership function or another hedge
			switch(controlWord)
			{
				case UNDEFINED:
					engineSubState = COMPLETE_EVALUATION;
					mVariable = currentToken;
					break;
				case HEDGE:
					hedgesBuffer.push(hVariable);
					engineSubState = HEDGE_READ;
					break;
				default:
					throw new RulesParsingException(" - Incorrect operation after HEDGE: "+out);
			}
			break;
//EVALUATION:EXCEPTION -----------------------------------------------------------------------------------------
		default:
			throw new RulesParsingException(" - Error in EVALUATION state: "+out);		
		}
//EVALUATION:COMPLETE_EVALUATION -------------------------------------------------------------------------------
		if(engineSubState==COMPLETE_EVALUATION)
		{
		//Block parsing mode
		if(rule!=null)
			{
			//Store hedges if needed
			Vector hVector = new Vector();
			while(!hedgesBuffer.empty())
				hVector.addElement((Hedge)hedgesBuffer.pop());
			
			FuzzyExpression tempExpression = new FuzzyExpression(lVariable,mVariable,hVector,out);
			//Store flags
			tempExpression.flagAND=flagAND;
			tempExpression.flagOR=flagOR;
			flagOR=flagAND=false;
			tempExpression.nestingUp = nestingUp;
			nestingUp=0;
			//Reset text represenation
			out = new String();
			//Store the expression
			rule.getLeftPartExpressions().addElement(tempExpression);

			}
		else
		//Rule evaluation mode
			{
			double tempResult = 0.0;

			//Complete evaluation
			tempResult = lVariable.is(mVariable);
			lVariable = null;

			if(tempResult == -1)
				throw new RulesParsingException(" - Unable to perform fuzzy evaluation: "+out);

			//Apply hedge if needed
			while(!hedgesBuffer.empty())
				tempResult = ((Hedge)hedgesBuffer.pop()).hedgeIt(tempResult);

			//Check if doing AND or OR ---> !(AND||OR) = STORE
			if(!flagAND && !flagOR)
			{
				accumulatedResult = tempResult;
			}
			else
			{
				if(flagAND && flagOR)
					throw new RulesParsingException(" - Incorrect AND/OR operation: "+out);
				if(flagAND)
					{
				flagAND = false;
				accumulatedResult = accumulatedResult > tempResult ? tempResult : accumulatedResult;
					}
				if(flagOR)
					{
					flagOR = false;
					accumulatedResult = accumulatedResult < tempResult ? tempResult : accumulatedResult;
					}
			}
			//Add to display string
			String s = String.valueOf(tempResult);
			out = out + "(" + s.substring(0,s.length() > 3 ? 4 : 3) + ") ";
			
			}//end of mode switch
			
			//Switch subState
			engineSubState = READY;
		}
		break;
	case EXECUTION:
		switch(engineSubState)
		{

//EXECUTION: READY --------------------------------------------------------------------------------------------
		case READY:
			switch(controlWord)
			{
			case LV:
				engineSubState = LV_READ;
				break;
			//Nothing to do if an 'and' is found
			case AND:
				break;
			//Change a rule's weight
			case RULE:
				engineSubState = RULE_READ;
				break;
			//Everything else generates exception
			default:
				throw new RulesParsingException(" - Incorrect operation after THEN: "+out);
			}
			break;
//EXECUTION: RULE_READ ----------------------------------------------------------------------------------------
		case RULE_READ:
			//The next item should be a label
			switch(controlWord)
			{
			case UNDEFINED:
				changeRuleLabel = currentToken;
				engineSubState = LABEL_READ;
				break;
			default:
				throw new RulesParsingException(" - A LABEL should follow RULE: "+out);
			}
			break;
//EXECUTION: LABEL_READ ---------------------------------------------------------------------------------------
		case LABEL_READ:
			//The next item should be WEIGHT
			switch(controlWord)
			{
			case WEIGHT:
				engineSubState = LV_READ;
				break;
			default:
				throw new RulesParsingException(" - An error after RULE LABEL (was 'weight' LV registered?): "+out);
			}
			break;
//EXECUTION: LV_READ ------------------------------------------------------------------------------------------
		case LV_READ:
			//The next item must be IS - anything else generates an exception
			switch(controlWord)
			{
			case IS:
				engineSubState = IS_READ;
				break;
			default:
				throw new RulesParsingException(" - Only IS can be present at this place: "+out);
			}
			break;
//EXECUTION: IS_READ ------------------------------------------------------------------------------------------
		case IS_READ:
			//The next item can be a HEDGE or a membership function
			switch(controlWord)
			{
			case HEDGE:
				hedgesBuffer.push(hVariable);
				engineSubState = HEDGE_READ;
				break;
			case UNDEFINED:
				mVariable = currentToken;
				engineSubState = COMPLETE_EXECUTION;
				break;
			default:
				throw new RulesParsingException(" - Incorrect sequence after IS: "+out);
			}
			break;
//EXECUTION: HEDGE_READ ----------------------------------------------------------------------------------------
		case HEDGE_READ:
			//The next item can be a membership function or another hedge
			switch(controlWord)
			{
			case HEDGE:
				hedgesBuffer.push(hVariable);
				engineSubState = HEDGE_READ;
				break;
			case UNDEFINED:
				mVariable = currentToken;
				engineSubState = COMPLETE_EXECUTION;
				break;
			default:
				throw new RulesParsingException(" - An error in EXECUTION stage: "+out);
				
			}
			break;
		}//end of switch (EXECUTION)
		
//EXECUTION: COMPLETE_EXECUTION -------------------------------------------------------------------------------
		if(engineSubState ==COMPLETE_EXECUTION)
		{

		//Block parsing mode
		if(rule!=null)
		{
			//Store hedges if needed
			Vector hVector = new Vector();
			while(!hedgesBuffer.empty())
				hVector.addElement((Hedge)hedgesBuffer.pop());

			//Check if this is a weight change operation
			if(changeRuleLabel!=null)
			{
				//Ensure that thisRuleLabel is not equal to changeRuleLabel
				if(thisRuleLabel!=null && changeRuleLabel!=null && thisRuleLabel.equals(changeRuleLabel))
					throw new RulesParsingException(" - A LABEL cannot be assigned to a RULE that changes that label's WEIGHT: "+out);
				//Fetch the label WEIGHT LV from the hash
				lVariable = (LinguisticVariable)labelWeightsHash.get(changeRuleLabel);
				if(lVariable==null)	
					throw new RulesParsingException(" - Unable to change WEIGHT for LABEL "+changeRuleLabel+" the LABEL hasn't yet been encountered: "+out);
			}
			
			FuzzyExpression tempExpression = new FuzzyExpression(lVariable,mVariable,hVector,out);
			//Reset text represenation
			out = new String();
			//Store the expression
			rule.getRightPartExpressions().addElement(tempExpression);
		}
		else
		//Rule evaluation mode
		{
		//A temporary variable to store results of hedging / negating
		double tempResult = accumulatedResult;

		
		
			//If something fired
			if(accumulatedResult > 0.0)
			{
				//If this is a weight change operation
				if(changeRuleLabel!=null)
				{
					//Ensure that thisRuleLabel is not equal to changeRuleLabel
					if(thisRuleLabel!=null && changeRuleLabel!=null && thisRuleLabel.equals(changeRuleLabel))
						throw new RulesParsingException(" - A LABEL cannot be assigned to a RULE that changes that label's WEIGHT: "+out);
					//Fetch the label WEIGHT LV from the hash
					lVariable = (LinguisticVariable)labelWeightsHash.get(changeRuleLabel);
					if(lVariable==null)	
						throw new RulesParsingException(" - Unable to change WEIGHT for LABEL "+changeRuleLabel+" the LABEL hasn't yet been encountered: "+out);
				}

				//Apply hedge if needed
				while(!hedgesBuffer.empty())
				tempResult = ((Hedge)hedgesBuffer.pop()).hedgeIt(tempResult);

				//Store result
				lVariable.set(thisRuleLabel, currentToken, tempResult);

				//Set fired flag
				flagRuleFired = true;

			}

			//Add to display string
			String s = String.valueOf(tempResult);
			out = out + "(" + s.substring(0,s.length() > 3 ? 4 : 3) + ") ";
			
		}//end of mode switching

			//Switch engineSubState
			engineSubState = READY;

		}

		break;
	} //end if switch(engineState)
	} //end of if(controlWord!=NOP)
	
} //end of while

return out;
}
/**
 * Equivalent of FuzzyEngine.evaluateRule(String), but for the blocks of rules.
 * @param rule Textual represenation of a fuzzy rule.
 * @return parsedRule Parsed rule.
 * @exception engine.RulesParsingException This exception is thrown if an error occurs during parsing.
 *  Exception.getMessage() will return the reason for error.
 */
public FuzzyRule parseRule(String rule) throws RulesParsingException 
{
	//Split input string into tokens and return delimeters along with tokens
	java.util.StringTokenizer tokens = new java.util.StringTokenizer(rule.trim(),new String("( )"),true);

	//Output rule
	FuzzyRule parsedRule = new FuzzyRule();

	try
	{
		parseExpression(tokens, parsedRule); 
	}
	catch(RulesParsingException e)
	{
		throw new RulesParsingException("\nError parsing rule: "+rule+"\n"+e.getMessage());
	}

	return parsedRule;
}
/**
 * Register a block of rules with the engine.
 * Every block has to be registered before it can be parsed.
 * @param block engine.FuzzyBlockOfRules to be registered.
 */
public void register(FuzzyBlockOfRules block) 
{
		block.setFuzzyEngine(this);
}
/**
 * Register a linguistic variable with the engine.
 * Any lingustic variable participating in fuzzy evaluations should be registered with the engine.
 * The same lingustic variable cannot be registered with different engines if labels are used.
 * @param function fuzzyEngine.LinguisticVariable to be registered with the engine.
 */
public void register(LinguisticVariable function) 
{
	//Store the LV itself
	this.lvHash.put(function.getLVName(),function);

	//Provide access from LV to labels' weights hash
	function.setLabelWeightsHash(labelWeightsHash);
}
/**
 * Reset all previously fired rules.
 * Call to this function clears all rules and resets the engine to its initial state.
 */
public void reset() 
{
	//Reset fired rules
	flagRuleFired = false;
	for (java.util.Enumeration en = lvHash.elements() ; en.hasMoreElements() ;) 
		{
	    	((LinguisticVariable)en.nextElement()).reset();
	 	}
	//Reset labels' weights changes
	for (java.util.Enumeration en = labelWeightsHash.elements() ; en.hasMoreElements() ;) 
		{
	    	((LinguisticVariable)en.nextElement()).reset();
	 	}
}
/**
 * Constructor.
 * Engine initialization is performed here.
 */
public FuzzyEngine() 
{
	super();
	
	//Create LV hashtable
	lvHash = new Hashtable();

	//Create nesting stack
	nestingStack = new Stack();

	//Initialize hedges hash
	hedgesHash = new Hashtable();
	addHedge(new HedgeNot());
	addHedge(new HedgeVery());
	addHedge(new HedgeSomewhat());

	//Create labels weights hash
	labelWeightsHash = new Hashtable();
	
	//Init. control controlHash hashtable
	controlHash = new Hashtable();

	controlHash.put(new String("if"), new Integer(IF));
	controlHash.put(new String("then"), new Integer(THEN));
	controlHash.put(new String("is"), new Integer(IS));
	controlHash.put(new String("and"), new Integer(AND));
	controlHash.put(new String("or"), new Integer(OR));
	controlHash.put(new String("("),  new Integer(LEFTP));
	controlHash.put(new String(")"), new Integer(RIGHTP));
	controlHash.put(new String(" "), new Integer(NOP));
	controlHash.put(new String("rule"), new Integer(RULE));
	controlHash.put(new String("weight"), new Integer(WEIGHT));
	controlHash.put(new String("set"), new Integer(SET));
	
}
}
