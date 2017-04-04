package br.com.ericsson.teltools.dth.core;

import cmg.stdapp.javaformatter.DataObject;

public class CoreResponse 
 extends    cmg.stdapp.javaformatter.definition.JavaFormatterBase 
 implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
	public void initialize()
	{
	    // This method will be called once during creation of class, add any initialization needed here
	}
 
	 /** Executed for each MapEvent.
	   *
	   * @param obj, data object containing all
	   * parameters in the MapEvent. Also contain
	   * helpful methods to get the parameters
	   * with suitable datatypes
	   */
	public void format(DataObject dataSource) throws Exception
	{
		dataSource.removeParameter("TARGET");
		dataSource.removeParameter("ORIGIN");
		 
		dataSource.removeParameter("SESSION_ID");
	}    
}
