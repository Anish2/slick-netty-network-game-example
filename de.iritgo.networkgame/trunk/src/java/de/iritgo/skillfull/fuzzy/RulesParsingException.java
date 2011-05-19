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

/**
 * This exception is thrown if an error occurs during parsing a fuzzy rule
 * Exception.getMessage() will return cause of the error
 */
public class RulesParsingException extends Exception 
{
/**
 * Constructor
 * @param s java.lang.String
 */
public RulesParsingException(String s) 
{
	super(s);
}
/**
 * Constructor
 */
public RulesParsingException() 
{
	super();
}
}
