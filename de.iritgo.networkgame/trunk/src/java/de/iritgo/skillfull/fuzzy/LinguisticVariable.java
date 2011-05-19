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
 * Class for linguistic variables.
 */
public class LinguisticVariable 
{
	private String thisLVname;
	private java.util.Hashtable storage;
	private double input_value;
	private java.util.Vector for_defuzzification;

	//Min and max support values for defuzzification
	private double minSupport;
	private double maxSupport;

	//Hashtable with labels' weights - value set by the fuzzy engine
	private Hashtable labelWeightsHash;

/**
 * Add a membership function.
 * @param mFunction MembershipFunction to be added.
 */
public void add(MembershipFunction mFunction) 
{
	double [] range = mFunction.getRange();
	storage.put(mFunction.getName(),mFunction);

	//Check if there should be a change in min and max support values
	if(range[0]<minSupport)	minSupport=range[0];
	if(range[3]>maxSupport)	maxSupport=range[3];
}
/**
 * Add a membership function given by its name and trapezoidal region.
 * @param name Name of the membership function.
 * @param start Min support value.
 * @param left_top Min top value.
 * @param right_top Max top value.
 * @param finish Max support value.
 */
public void add(String name,double start, double left_top, double right_top, double finish) 
{
	double [] range = {start,left_top,right_top,finish};
	MembershipFunction temp = new MembershipFunction(name,range);
	storage.put(name,temp);

	//Check if there should be a change in min and max support values
	if(start<minSupport)	minSupport=start;
	if(finish>maxSupport)	maxSupport=finish;
}
/**
 * Return an exact copy of itself.
 * The copy is exact except that rules fired since the last reset()
 * will not influence defuzzification result.
 * @return engine.LinguisticVariable
 */
public LinguisticVariable copy() 
{
	//Create new LV
	LinguisticVariable tempLV = new LinguisticVariable(this.getLVName());

	//Copy membership functions
	for(Enumeration e = this.storage.elements(); e.hasMoreElements(); )
		{
			tempLV.add((MembershipFunction)e.nextElement());
		}
	//Copy input value
	tempLV.setInputValue(input_value);
	
	return tempLV;
}
/**
 * Defuzzify using centroid.
 * Memebership functions are scaled by product and combined by summation.
 * This method has to be completely redone (eliminated operations on discrete array,
 * support different modes of scaling and summation).
 * @return double Result of defuzzification
 * @exception fuzzyEngine.NoRulesFiredException This exception is thrown if no rules have
 * fired for this Linguistic Variable.
 */
public double defuzzify() throws NoRulesFiredException 
{
	//Number of rules fired
	int fired = for_defuzzification.size();

	if(fired == 0)
		{
			NoRulesFiredException e = new NoRulesFiredException("No rules was fired for "+this.thisLVname);
			throw e;
		}

	//min and max X (support) values are already calculated in minSupport/maxSupport
	double step = Math.abs((maxSupport-minSupport)/100);

	//Find sum of scaled functions
	double [] scaled;
	double [] sum = new double [100];

	for(int i=0; i<(fired-1); i+=3)
	{
		scaled = this.getMembershipFunctionByName((String)for_defuzzification.elementAt(i+1))
						.plot(minSupport,maxSupport,100);

		//Scale and change weight if needed
		double scale = ((Double)for_defuzzification.elementAt(i+2)).doubleValue();
		double weight = 1.0;

		String label = (String)for_defuzzification.elementAt(i);
		if(label != null)
			{
			//Look-up label weight in hash
			Object temp = labelWeightsHash.get(label);

			//Catch exceptions if no weight change happened
			try{
				if(temp!=null)	weight = ((LinguisticVariable)temp).defuzzify();
				}
			catch(NoRulesFiredException e)
				{
					weight=1.0;
				}
			}

		//Add to the sum
		for(int j=0; j<100; j++)	sum[j]+=scaled[j]*scale*weight;
	}
			
	//Find centroid
	double nominator = 0.0;
	double denominator = 0.0;
	
	for(int i=0; i<100; i++)
	{
		nominator+=(minSupport+step*i)*sum[i];
		denominator+=sum[i];
	}

	return nominator/denominator;
}
/**
 * Return the name of this linguistic variable
 * @return java.lang.String
 */
public String getLVName() 
{
	return thisLVname;
}
/**
 * Return a MembershipFunction belonging to this
 * LinguisticVariable by its name.
 * @param name java.lang.String Name of the membership function. 
 * @return MemebershipFunction 
 */
public MembershipFunction getMembershipFunctionByName(String name) 
{
	return (MembershipFunction)storage.get(name);
}
/**
 * Perform fuzzification for an input value.
 * @param name Name of the membership function
 * @return double Returns result of fuzzification or -1 if fuzzification is impossible.
 */
public double is(String name) 
{
	MembershipFunction m = this.getMembershipFunctionByName(name);
	
	if(m!=null)		return m.fuzzify(input_value);
	else			return -1;
}
/**
 * Constructor
 * @param name Name of this linguistic variable
 */
public LinguisticVariable(String name) 
{ 
	super();
	this.thisLVname = name;
	storage = new java.util.Hashtable();
	for_defuzzification = new java.util.Vector();
}
/**
 * Reset all rules that have previously fired for this LinguisticVariable.
 */
public void reset() 
{
	//Remove fired rules
	for_defuzzification.removeAllElements();

}
/**
 * Set input value for this lingustic variable.
 * @param value double
 */
public void setInputValue(double value) 
{
	this.input_value = value;
}
/**
 * Provide access to the shared labels' weights hastable.
 * This method is used by FuzzyEngine in order to provide access to all labels
 * and weights by every rule. Current implementation requires that an instance of
 * LinguisticVariable can only be registered with one FuzzyEngine.
 * @param hash java.util.Hashtable
 */
public void setLabelWeightsHash(Hashtable hash) 
{
	labelWeightsHash = hash;
}
/**
 * Store result of a fired rule.
 * This methos is invoked if a rule fired. It stores fired rule's label,
 * name of the membership function for which the rule fired and membership value.
 * Stored information is used when calling defuzzify().
 * @param label Label of the fired rule, null if rule has no label.
 * @param name Name of the membership function
 * @param value Membership value.
 */
public void set(String label, String name, double value) throws RulesParsingException
{
	//Add label, name and value to the 'for_defuzzyfication' vector
	for_defuzzification.addElement(label);

	//Check that this name is a correct name for a MF
	if(getMembershipFunctionByName(name)!=null)		for_defuzzification.addElement(name);
	else	throw new RulesParsingException("Wrong name of a membership function");
	
	for_defuzzification.addElement(new Double(value));
}
/**
 * Set the name of this linguistic variable.
 * @return java.lang.String
 */
public void setLVName(String name) 
{
	thisLVname = name;
}
}
