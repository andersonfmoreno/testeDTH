package br.com.ericsson.teltools.dth.logging;

import java.util.HashSet;

import cmg.stdapp.javaformatter.DataObject;

public class DataBaseResponse 
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
       String status = "OK";
       if (dataSource.getValueAsInt("DbLookUpResultCode") != 0) 
       {            
           status = "ERROR";
       }
       removeAllDataSource(dataSource);
       dataSource.setParameter("STATUS",status);
       dataSource.setParameter("ORIGIN","LOGGING");
       dataSource.setParameter("TARGET","LoggingController");
      
    }   
    
    private void removeAllDataSource(final DataObject dataSource) throws Exception
    {
        
            HashSet<String> retainDataSource = new HashSet<String>();
            retainDataSource.add("__GlobalTransactionID");
            retainDataSource.add("WS_requestID");
            retainDataSource.add("SESSION_ID");
            retainDataSource.add("LOGGING_LIST");            
            
            dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
      
    } 
}
