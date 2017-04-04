package br.com.ericsson.teltools.dth.core;
import cmg.stdapp.javaformatter.DataObject;


public class BackEndControllerRouter {
	
	public void initialize()
    {
        // This method will be called when configuration is started 
    }
 /** Executed for each MapEvent.
  *
  * @param obj, data object containing all
  * parameters in the MapEvent. Also contain
  * helpful methods to get the parameters
  * with suitable datatypes:
  * To filter out a event, Return "FILTER_OUT".
  *
  * @return name of the activity to route
  * current MapEvent to.
  */
    public String getRouteDestination(DataObject dataSource) throws Exception
    {
        if(dataSource.parameterExists("LASTCLEANED"))
        {
            //return "CoreResponse";
            return "BackEndController";
        }    
        String Target = dataSource.getValueAsString("TARGET");
        return Target;
    }


}
