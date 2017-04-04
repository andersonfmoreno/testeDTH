package br.com.ericsson.teltools.dth.sms;

/** HEADER STARTS **/
//Any changes between HEADER STARTS and HEADER ENDS will be discarded during validation and compilation
//Additions or alteration of import statements will be ignored and replaced with the original
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmg.stdapp.javaformatter.DataObject;

public class SMSController 
 extends    br.com.ericsson.teltools.dth.debug.JavaFormatterBase 
 implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
	private static final String FLOW_SEQ_PARAM = "FLOW_SEQ";

	private static final String BUSINESS_RULE_PARAM = "BUSINESS_RULE_ID";

	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
	Map<String, Object> logginParams = new java.util.concurrent.ConcurrentHashMap<String, Object>();
    Map<String, Object> mapVariables = new java.util.concurrent.ConcurrentHashMap<String, Object>();
    final String VARIABLES_CONFIG = "COMMAND";
    
    
  //-----------------------------------------------------------------
    // Error constants
    //-----------------------------------------------------------------
    
    final String ERROR_SOURCE_MODULE = "SMS";
    final String ERROR_TYPE = "internal";
    
    
    final String FORMAT_ERRO            	= "24000";
    final String ASSIGN_DATA_SMS        	= "24001";
    final String GET_DATA_CONTAINER     	= "24002";
    final String GET_CONFIG_VARIABLES   	= "24003";
    final String GET_VALUE_KEYWORD      	= "24004"; 
    final String GET_NOTIFICATION       	= "24005";
    final String GET_DATA_MAP_VARIABLES 	= "24006";
    final String GET_VALUE_FIELD        	= "24007";
    final String GET_CHANGE_KEYWORD     	= "24008";
    final String NUMBER_OF_COMMAND_INVALID	= "24009";
    final String COMMAND_PROPERTY_INVALID	= "24010";
    final String GET_DATA_CONTAINER_ERROR	= "24011";
    final String GET_EXPIRATION_DATE		= "24012";
    final String BITAND_PROPERTY			= "24013";
    
    public void initialize()
    {
    }
 
    @SuppressWarnings("unchecked")
    public void format(DataObject dataSource) throws Exception
    {
        @SuppressWarnings("unused")
		String sessionID = UUID.randomUUID().toString();
        try 
        {            
            DataObject configSystem = getConfigVariables();
            
            String businessRuleID   = dataSource.getValueAsString(BUSINESS_RULE_PARAM);
            logginParams.put(BUSINESS_RULE_PARAM, businessRuleID);
            String flowSeq          = dataSource.getValueAsString(FLOW_SEQ_PARAM);
            logginParams.put(FLOW_SEQ_PARAM, flowSeq);
            
            DataObject smsContainer  = getEntity("FLOWS_COMMANDS").getData();
            String  commandData      = smsContainer.getValueAsString(businessRuleID+"."+flowSeq);
            
            Map<String, Object> commandList = getDataContainer("FLOWS_COMMANDS",commandData);
            
            if (commandList.size() != 1)
            {
            	throw  createException(NUMBER_OF_COMMAND_INVALID, ERROR_TYPE, ERROR_SOURCE_MODULE, "Number of command invalid.");
            }
            
            Map<String, Object> command = (Map<String, Object>)commandList.get("0");
            String commandListProperty = ((String)command.get("COMMAND_PROPERTY"));
            String COMMAND = configSystem.getValueAsString("COMMAND");
            
            if (!bitAndProperty(commandListProperty, COMMAND))
            {
            	throw  createException(COMMAND_PROPERTY_INVALID, ERROR_TYPE, ERROR_SOURCE_MODULE, "Command property invalid.");
            }
            
            DataObject smsContainerCM  = getEntity("COMMANDS").getData();
            String  params      = smsContainerCM.getValueAsString((String)command.get("COMMAND_ID"));
            
            Map<String, Object> parameters = getDataContainer("COMMANDS",params); 
            
            assignDataSMS(dataSource,parameters);
            
        } 
        catch (Exception error) 
        {
            String errorCode        = FORMAT_ERRO;
            String errorType        = ERROR_TYPE;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
            
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)SMS_ERROR(.+)")))
            {
                String[] errors     = error.getMessage().split("\\<\\&\\>");
                errorCode           = errors[1];
                errorType           = errors[2];
                errorSource         = errors[3];
                errorDescription    = errors[4];
            }
            
          formatDataSourceError(dataSource,errorDescription,errorCode,errorType,errorSource,"");
          
        } finally {
            dataSource.setParameter("DATA_LOGGING", logginParams.toString());    
        }
    }
    
    @SuppressWarnings("unused")
	private Map<String, String> prepareErrorResponse(final String errorCode, final String errorDescription,String errorSource, String errorType)
    {
    	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
        final Map<String, String> errorResponseMap = new java.util.concurrent.ConcurrentHashMap<String, String>();
        errorResponseMap.put("ERROR_CODE", errorCode);
        errorResponseMap.put("ERROR_DESCRIPTION", errorDescription);
        errorResponseMap.put("ERROR_SOURCE", errorSource);
        errorResponseMap.put("ERROR_TYPE", errorType);

        return errorResponseMap;
    }
    
    private void removeAllDataSource(final DataObject dataSource) throws Exception
    {
        HashSet<String> retainDataSource = new HashSet<String>();
        retainDataSource.add("__GlobalTransactionID");
        retainDataSource.add("HTTPSessionID");
        retainDataSource.add("WS_requestID");
        
        dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
    }
    
    @SuppressWarnings("unchecked")
    private void assignDataSMS(DataObject dataSource,Map<String, Object> parameters) throws Exception
    {
        try 
        {
            String sessionID = dataSource.getValueAsString("SESSION_ID");
            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> dataSourceHashMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            for (final String parameter : (Set<String>)dataSource.getParameterContainer().keySet())
            {
                dataSourceHashMap.put(parameter, dataSource.getValue(parameter));
            }
            
            removeAllDataSource(dataSource);
            
            dataSource.setParameter("SESSION_ID"    , sessionID           );
            dataSource.setParameter("ORIGIN"        , "SMSC"              );
            dataSource.setParameter("TARGET"        , "BackEndController"    );
            dataSource.setParameter("STATUS"        , "OK"               );
            
            //Map<String, Object> dataLogCommand = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            logginParams.put("NUMBER_LOG_COMMAND", 1);
            //Very big message
            //dataLog.put("0",dataLogCommand);
            
            for (final String key : (Set<String>)parameters.keySet())
            {
                Map<String, Object> index = (Map<String, Object>)parameters.get(key);
                String field = (String)index.get("FIELD");
                Object value = getValueField(index, dataSourceHashMap);
                dataSource.setParameter(field,value);
                
                //dataLogCommand.put(field,value);
            }
        }
        catch (Exception e) 
        {
        	throw createException(ASSIGN_DATA_SMS,"internal",ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    /**
     * Method to format the return data with error
     * @param dataSource - variables of return for controller
     * @param errorDescription - description of error 
     * @param errorCode - code error (see errors file)
     * @param errorType - type error ( business, internal or malformed)
     * @param errorSource - source error ( dth, database, air, sapc, rim ...), they are the target modules 
     * @param dataLogging
     * @throws Exception
     */
    private void formatDataSourceError(DataObject dataSource, String errorDescription, String errorCode, String errorType, String errorSource,String dataLogging) throws Exception
    {
        String sessionID = dataSource.getValueAsString("SESSION_ID");
        
        
        removeAllDataSource(dataSource);
        
        dataSource.setParameter("SESSION_ID"    , sessionID           );
        dataSource.setParameter("ORIGIN"        , "SMSC"              );
        dataSource.setParameter("TARGET"        , "BackEndController"    );
        dataSource.setParameter("STATUS"        , "ERROR"             );
        dataSource.setParameter("DESCRIPTION"   , errorDescription  );
        dataSource.setParameter("ERROR_SOURCE"  , errorSource         );
        dataSource.setParameter("ERROR_TYPE"    , errorType            );
        dataSource.setParameter("ERROR_CODE"    , errorCode            );
        dataSource.setParameter("DATA_LOGGING"  , dataLogging        );
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataContainer(String layout, String data,boolean single) throws Exception
    {
        try 
        {
            Map<String, Object> dataContainer = getDataContainer(layout,data);
            if (single)
            {
                return (Map<String, Object>)dataContainer.get("0");
            }
            return dataContainer;
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER_ERROR,"internal",ERROR_SOURCE_MODULE,formatErroDescription(e));
        }  
    }
    
    private Map<String, Object> getDataContainer(String layout, String data) throws Exception
    {
    	try 
        {
    		DataObject layoutContainer = getEntity("LAYOUT").getData();
            String[] fields = layoutContainer.getValueAsString(layout).split("<\\;>");
            int countFields = fields.length;
            
            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> lineDataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            String[] lines = data.split("<\\.>");
            int i = 0;
            
            for(final String line : lines)
            {
            	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                Map<String, Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                String[] dataFields = line.split("<\\;>");
                
                if((dataFields == null) || (dataFields.length != countFields))
                {
                    throw  createException(GET_DATA_CONTAINER, ERROR_TYPE, ERROR_SOURCE_MODULE, "Data do not match the layout ");
                }
                
                int j = 0;
                
                for(final String dataField : dataFields)
                {
                	dataContainer.put(fields[j], dataField);
                    j++;
                }
                j = 0;
                lineDataContainer.put(i+"",dataContainer);
                i++;
            }

            return lineDataContainer;    
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private DataObject getConfigVariables() throws Exception
    {
        try 
        {
            DataObject container   = getEntity("CONFIG").getData();
            String[] variablesList = VARIABLES_CONFIG.split("\\|");
            
            for (String variable : variablesList)
            {
                if(!container.parameterExists(variable))
                {
                    throw  createException(GET_CONFIG_VARIABLES, ERROR_TYPE, ERROR_SOURCE_MODULE, "Not exist "+variable+" in variables of config");
                }
            }

            return container;
        } 
        catch (Exception e) 
        {
            throw  createException(GET_CONFIG_VARIABLES, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private String getValueKeyWord(String keyWord,Map<String, Object>mapVariables) throws Exception
    {
        try 
        {
        	DataObject keyWordContainer = getEntity("NOTIFICATION_KEYWORDS").getData();
            String  keyWordParams = keyWordContainer.getValueAsString(keyWord);
            
            Map<String, Object> parameters = getDataContainer("NOTIFICATION_KEYWORDS",keyWordParams,true);
            
            String keyWordValue = null;
            String action = (String)parameters.get("ACTION");
            String value  = (String)parameters.get("VALUE");
            
            if (action.equals("FIXED"))
            {
                keyWordValue = value;
                return keyWordValue;
            }
            else if (action.equals("FUNCTION"))
            {
            	if(value.equals("getExpirationDate"))
            	{
            		return getExpirationDate((String)getDataMapVariables("TABLE.SUBSCRIPTION.END_TIMESTAMP", mapVariables));
            	}
            	else
            	{
            		throw createException(GET_VALUE_KEYWORD, ERROR_TYPE, ERROR_SOURCE_MODULE, "Action not known");	
            	}
            }
            else if (action.equals("MAP_VARIABLES"))
            {
                String[] levels = value.split("\\.");
                int countLevel = levels.length;
                Map<String, Object> field = mapVariables;
                int i = 0;
                
                while (i < countLevel-1)
                {
                    field = (Map<String, Object>)field.get(levels[i]);
                    i++;
                }
                
                keyWordValue = (String)field.get(levels[i]);
            }
            else
            {
                throw createException(GET_VALUE_KEYWORD, ERROR_TYPE, ERROR_SOURCE_MODULE, "Action not known");
            }
            
            return keyWordValue;
		}
        catch (Exception e) 
        {
        	throw  createException(GET_VALUE_KEYWORD, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
		}
    	
    }
    
    private String getExpirationDate(String endTimeStamp) throws Exception
    {
    	try 
    	{
            return parseDefaultDateFormatToHumanDateFormat(endTimeStamp);	
		} 
    	catch (Exception e) 
    	{
    		throw  createException(GET_EXPIRATION_DATE, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
		}
    }
    
    private
    String parseDefaultDateFormatToHumanDateFormat(String defaultDate){
        StringBuilder humanDate = new StringBuilder();
        
        //Year
        humanDate.append(defaultDate.substring(0,4));
        humanDate.append("/");
        
        //Month
        humanDate.append(defaultDate.substring(4,6));
        humanDate.append("/");
        
        //Day
        humanDate.append(defaultDate.substring(6,8));
        humanDate.append(" ");
        
        //Hour
        humanDate.append(defaultDate.substring(8,10));
        humanDate.append(":");
        
        //Minute
        humanDate.append(defaultDate.substring(10,12));
        humanDate.append(":");
        
        //Second
        humanDate.append(defaultDate.substring(12,14));
        humanDate.append(" ");
        
        //TimeZone
        humanDate.append(defaultDate.substring(14, defaultDate.length()));
        
        return humanDate.toString();
    }    
    
    private String getChangeKeyWord(String msg,Map<String, Object>mapVariables) throws Exception
    {
    	try
		{
    		Pattern regexp = null;
        	StringBuffer message = null;
        	Matcher m = null;
        	
        	regexp = Pattern.compile("(.*)<<([A-Z|0-9|a-z]+)>>(.*)");
        	message = new StringBuffer();
        	m = regexp.matcher(msg);
        	
        	if(!m.find())
        	{
        		return msg;
        	}
                
        	m.reset();
        	
            while (m.find())
            {
            	message = new StringBuffer();
                message.append(m.group(1));
                message.append(getValueKeyWord(m.group(2),mapVariables));
                message.append(m.group(3));
                m = regexp.matcher(message.toString());
            }
                
            return message.toString();
		}
		catch (Exception e)
		{
			throw createException(GET_CHANGE_KEYWORD, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
		}
    	
    }
    
    private String getNotification(String entity, String key, String msgType,Map<String, Object>mapVariables) throws Exception
    {
        return getNotification(entity,key, msgType,false,null,null,mapVariables);
    }
    
    @SuppressWarnings("unchecked")
    private String getNotification(String entity, String key, String msgType, boolean level, String parameterLevel, String parameterLevelValue,Map<String, Object>mapVariables) throws Exception
    {
    	try
			{
				
	        DataObject notificationContainer = getEntity(entity).getData();
	        String  keyWordParams = notificationContainer.getValueAsString(key);
	        
	        if (level)
	        {
	            Map<String, Object> parameters = getDataContainer(entity,keyWordParams);
	            
	            
	            for (final String index : (Set<String>)parameters.keySet())
	            {
	            		
	            	Map<String, Object> param = (Map<String, Object>)parameters.get(index);
	            	
	                int paramLevelValue = Integer.parseInt(parameterLevelValue);
	                int paramValue = Integer.parseInt((String)param.get(parameterLevel));
	                
	                if (paramLevelValue >=  paramValue)
	                {
	                    if (msgType.equals("EMAIL_TEXT") || msgType.equals("SMS_TEXT"))
	                    {
	                    	return getChangeKeyWord((String)param.get(msgType),mapVariables);
	                    }
	                    else
	                    {
	                        throw createException(GET_NOTIFICATION, ERROR_TYPE, ERROR_SOURCE_MODULE, "Message type invalid");
	                    }
	                }               
	            }
	            return " ";
	        }
	        else
	        {
	            Map<String, Object> parameters = getDataContainer(entity,keyWordParams,true);
	            if (msgType.equals("EMAIL_TEXT") || msgType.equals("SMS_TEXT"))
	            {
	                return getChangeKeyWord((String)parameters.get(msgType),mapVariables);
	            }
	            else
	            {
	            	
	                throw createException(GET_NOTIFICATION, ERROR_TYPE, ERROR_SOURCE_MODULE, " ");
	            }
	        }
		}
		catch (Exception e)
		{
			throw createException(GET_NOTIFICATION, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
		}
    }
    
    private String getShortCode(String productId) throws Exception
    {
    	try 
    	{
        	Map<String, Object> shortCode = getDataContainer("SHORT_CODE_BY_PRODUCT_ID", productId, true);
        	return (String)shortCode.get("SHORT_CODE");
		} 
    	catch (Exception e) 
    	{
			return "CLARO";
		}
        
    }
    
    
    private String getSMSNotificationByProductAndType(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_TYPE", key, "SMS_TEXT",mapVariables);
    }

    private String getEMAILNotificationByProductAndType(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_TYPE", key, "EMAIL_TEXT",mapVariables);
    }
    
    private String getSMSNotificationByProductAndTreshold(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_THRESHOLD", key, "SMS_TEXT",mapVariables);
    }

    private String getEMAILNotificationByProductAndTreshold(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_THRESHOLD", key, "EMAIL_TEXT",mapVariables);
    }
    
    
    @SuppressWarnings("unused")
	private String getNotificationByTresholdsProductVolume(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_THRESHOLD", key, "SMS_TEXT", true, "VOLUME", (String)getDataMapVariables("TABLE.THRESHOLD_VOLUME", mapVariables), mapVariables);
    }

    @SuppressWarnings("unused")
	private String getNotificationByTresholdsProductTime(String key, Map<String, Object> mapVariables) throws Exception
    {
        return getNotification("NOTIFICATION_BY_PRODUCT_AND_TYPE", key, "EMAIL_TEXT",mapVariables);
    }
    
    @SuppressWarnings("unchecked")
    private Object getDataMapVariables(String field, Map<String, Object> mapVariables) throws Exception
    {
        String[] levels = field.split("\\.");
        int countLevel = levels.length;
        int i = 0;
        Object fieldValue = mapVariables;
         
        while (i < countLevel)
        {
            if (!(((Map<String, Object>)fieldValue).containsKey(levels[i])))
            {
                throw createException(GET_DATA_MAP_VARIABLES, ERROR_TYPE, ERROR_SOURCE_MODULE, "Field not present in dataSource "+field);
            }
            
            fieldValue = ((Map<String, Object>)fieldValue).get(levels[i]);
            i++;
        }
        
        return fieldValue;
    }
    
    
    
    private Object getValueField(Map<String, Object> index,Map<String, Object> mapVariables) throws Exception
    {
        try 
        {    
            String action = (String)index.get("ACTION");
            String value  = (String)index.get("VALUE");
            
            Object fieldValue = null;
            
            if(action.equals("FUNCTION"))
            {
                if (value.equals("getSMSNotificationByProductAndType"))
                {
                    return getSMSNotificationByProductAndType((String)(getDataMapVariables("TABLE.PRODUCT.productID",mapVariables))+"."+getDataMapVariables("REQUEST.operation",mapVariables),mapVariables);
                }
                else if (value.equals("getEMAILNotificationByProductAndType"))
                {
                    return getEMAILNotificationByProductAndType((String)(getDataMapVariables("TABLE.PRODUCT.productID",mapVariables))+"."+getDataMapVariables("REQUEST.operation",mapVariables),mapVariables);
                }
                else if (value.equals("getSMSNotificationByProductAndTreshold"))
                {
                    return getSMSNotificationByProductAndTreshold((String)getDataMapVariables("TABLE.PRODUCT.productID", mapVariables)+"."+(String)getDataMapVariables("TABLE.THRESHOLD.thresholdId", mapVariables),mapVariables);
                }
                else if (value.equals("getEMAILNotificationByProductAndTreshold"))
                {
                    return getEMAILNotificationByProductAndTreshold((String)getDataMapVariables("TABLE.PRODUCT.productID", mapVariables)+"."+(String)getDataMapVariables("TABLE.THRESHOLD.thresholdId", mapVariables),mapVariables);
                }
                else if(value.equals("getShortCode"))
                {
                	return getShortCode((String)getDataMapVariables("TABLE.PRODUCT.productID", mapVariables));
                }
                else
                {
                    throw createException(GET_VALUE_FIELD, ERROR_TYPE, ERROR_SOURCE_MODULE, "Action not known");
                }
            }
            else if (action.equals("JAVA_CLASS"))
            {
                fieldValue = instantiateJavaClass(value);
            }
            else if (action.equals("FIXED"))
            {
                fieldValue = value;
            }
            else if (action.equals("MAP_VARIABLES"))
            {
                return getDataMapVariables(value,mapVariables);
            }
            else
            {
                throw createException(GET_VALUE_FIELD, ERROR_TYPE, ERROR_SOURCE_MODULE, "Action not known");
            }
            
            return fieldValue;
        } 
        catch (Exception e) 
        {
            throw createException(GET_VALUE_FIELD, ERROR_TYPE, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
	private boolean bitAndProperty(String property, String propertyConfig) throws Exception
	{
		try 
		{
			java.math.BigInteger bProperty     = new java.math.BigInteger(property);
			java.math.BigInteger bPropertyName = new java.math.BigInteger(propertyConfig);
			return bProperty.and(bPropertyName).equals(bPropertyName);	
		}
		catch (Exception e) 
		{
			throw createException(BITAND_PROPERTY,"internal",ERROR_SOURCE_MODULE,formatErroDescription(e));
		}
		
	}
	
    private Object instantiateJavaClass(final String javaClassLiteral) throws Exception
    {
        final Matcher m = Pattern.compile("(.+)\\((.+)\\)").matcher(javaClassLiteral);
        m.find();
        return Class.forName(m.group(1)).getConstructor(String.class).newInstance(m.group(2));
    }
    
    
    private Exception createException(String errorCode,String errorType, String errorSource, String errorDescription) throws Exception
    {
        if ((errorDescription != null) && (errorDescription.matches("(.*)SMS_ERROR(.+)")))
        {
            return new Exception(errorDescription);
        }
        
        return new Exception("SMS_ERROR<&>"+errorCode+"<&>"+errorType+"<&>"+errorSource+"<&>"+errorDescription);
    }
    
    private String formatErroDescription(Exception e) 
    {
        return new StringBuilder(e.getClass().getCanonicalName()).append(":").append(e.getMessage()).append("(").append(Arrays.toString(e.getStackTrace())).append(")").toString();
    }
    
    
}