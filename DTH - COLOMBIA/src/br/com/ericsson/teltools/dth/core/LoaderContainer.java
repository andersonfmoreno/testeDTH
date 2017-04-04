package br.com.ericsson.teltools.dth.core;

import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.Entity;

public class LoaderContainer 
 extends    cmg.stdapp.javaformatter.definition.JavaFormatterBase 
 implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
    final String FIELD_DELIMITER        = "\\<\\;\\>";
    final String RECORD_DELIMITER        = "\\<\\|\\>";
    final String KEY_DELIMITER            = "\\<\\:\\>";
    
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
    	try {
    		String entityName  = dataSource.getValueAsString("ENTITY_NAME");
    		String entityValue = dataSource.getValueAsString("ENTITY_VALUE");
    		
    		dataSource.getParameterContainer().clear();
    		
    		if(entityValue.equals("DELETE"))
    		{
    			removeEntity(entityName);
    			return;
    		}
    		
    		String[] values = entityValue.split(RECORD_DELIMITER);
    		
    		for( String value : values)
    		{
    			String[] fields = value.split(KEY_DELIMITER);
    			dataSource.setParameter(String.valueOf(fields[0]), new String(fields[1].getBytes("UTF-8"),java.nio.charset.Charset.defaultCharset().toString()));
    		}
    		
    		Entity entity = createEntity(entityName);
    		entity.setData(dataSource);
    		putEntity(entity);     
		} catch (ArrayIndexOutOfBoundsException error) {
		}
    }    
}
