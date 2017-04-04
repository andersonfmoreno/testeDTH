/** HEADER STARTS **/
// Any changes between HEADER STARTS and HEADER ENDS will be discarded during validation and compilation
// Additions or alteration of import statements will be ignored and replaced with the original
package br.com.ericsson.teltools.dth.sms;

import cmg.stdapp.javaformatter.DataObject;

public class SMSResponse 
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
        // TODO Insert formatting code here
        
        // int value1 = dataSource.getValueAsInt("value1");
        // int value2 = dataSource.getValueAsInt("value2");
        // int sum = value1 + value2;
        // dataSource.setParameter("sum", sum);
    }    
}
