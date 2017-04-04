package br.com.ericsson.teltools.dth.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cmg.stdapp.javaformatter.DataObject;



/** HEADER STARTS **/
//Any changes between HEADER STARTS and HEADER ENDS will be discarded during validation and compilation
//Additions or alteration of import statements will be ignored and replaced with the original


public class DataBaseFormatter 
 extends    br.com.ericsson.teltools.dth.debug.JavaFormatterBase  
 implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
    
    final String LOGGING_ERROR            ="10400";
    final String REMOVE_ALL_DATA_SOURCE    = "10401";
    final String ERROR_SOURCE_MODULE    = "10402";
    final String VALIDATION_MATCH_ERROR = "10403";
    
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
    @SuppressWarnings("unchecked")
    public void format(DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String,Object> loggingList = (Map<String,Object>)dataSource.getValue("LOGGING_LIST");
            
            String firstElemt = "";
            
            for (final String index : (Set<String>)loggingList.keySet())
            {
                firstElemt = index;
                break;
            }
            
            Map<String,Object> logging = (Map<String,Object>)loggingList.get(firstElemt);
            
            Map<String,Object> db = (Map<String,Object>)logging.get("DB");
            Map<String,Object> dr = (Map<String,Object>)logging.get("DR");
            
            loggingList.remove(firstElemt);
            
            
			Map<String,Object> request = null;  // = (HashMap<String,Object>)db.get("REQUEST");
			Map<String,Object> action = null;   //  = (HashMap<String,Object>)db.get("ACTION");
			Map<String,Object> response = null; // = (HashMap<String,Object>)db.get("RESPONSE");
			
			for(Map.Entry<String,Object> entry : db.entrySet()){
				if(entry.getKey().equals("REQUEST")){
					request = (Map<String, Object>) entry.getValue();
				}else if(entry.getKey().equals("ACTION")){
					action = (Map<String, Object>) entry.getValue();
				}else if(entry.getKey().equals("RESPONSE")){
					response = (Map<String, Object>) entry.getValue();
				}
			}
            
            populateDataSourceWithAllMaps(dataSource, dr, request, action,response);
            
        } 
        catch (Exception error) 
        {
            removeAllDataSource(dataSource);
            dataSource.setParameter("STATUS","ERROR");
            dataSource.setParameter("ORIGIN","LOGGING");
            dataSource.setParameter("TARGET","LoggingController");
            
            dataSource.setParameter("ERROR_CODE"        ,LOGGING_ERROR);
            dataSource.setParameter("ERROR_DESCRIPTION"    ,error.getMessage());
            dataSource.setParameter("ERROR_TYPE"        ,"internal");
            dataSource.setParameter("ERROR_SOURCE"        ,"LOGGING");        
            
        }
    }

	/**
	 * @param dataSource
	 * @param dr
	 * @param request
	 * @param action
	 * @param response
	 * @throws Exception
	 */
	private void populateDataSourceWithAllMaps(DataObject dataSource,
			Map<String, Object> dr, Map<String, Object> request,
			Map<String, Object> action, Map<String, Object> response)
			throws Exception {
		String requestNumber = (String)dr.get("requestNumber");
		Pattern requestNumberPattern = Pattern.compile("([0-9])+");
		if(validateStringWithPatternAndSize(requestNumber,requestNumberPattern,20, false)){
			dataSource.setParameter("requestNumber"         ,Integer.valueOf(requestNumber));
		}
		//msisidn,properties,susbcription_id,qos_id
		
		
		//REQUEST
		
		if( request != null && !request.isEmpty() ){
			
			String requestSessionId = String.valueOf(request.get("sessionId"));
			if(validateStringWithPatternAndSize(requestSessionId, null, 50, false)){
				dataSource.setParameter("requestSessionId"      ,requestSessionId);//varchar(50)
			}
			
			String requestActionType = String.valueOf(request.get("actionType"));
			Pattern requestActionTypePattern = Pattern.compile("[a-zA-Z]+");
			if(validateStringWithPatternAndSize(requestActionType, requestActionTypePattern, 25, false)){
				dataSource.setParameter("requestActionType"     ,requestActionType);//varchar(25)
			}
			
			String requestActionData = String.valueOf(request.get("actionData")).replaceAll("\'", "");
			if(validateStringWithPatternAndSize(requestActionData, null, 2000, false)){
				dataSource.setParameter("requestActionData"     ,requestActionData);//varchar(2000)
			}
			
			String requestMainID = String.valueOf(request.get("mainID"));
			Pattern requestMainIDPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(requestMainID, requestMainIDPattern, 20, true)){
				dataSource.setParameter("requestMainID"         ,requestMainID);//varchar(20)
			}
			
			String requestProperties = String.valueOf(request.get("properties"));
			Pattern requestPropertiesPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(requestProperties, requestPropertiesPattern, 38,true)){
				dataSource.setParameter("requestProperties"     ,requestProperties);//number(38)
			}
			
			String requestSubscriptionId = String.valueOf(request.get("subscriptionId"));
			Pattern requestSubscriptionIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(requestSubscriptionId, requestSubscriptionIdPattern, 15,true)){
				dataSource.setParameter("requestSubscriptionId" ,requestSubscriptionId);//number(15)
			}
			
			String requestQosId = String.valueOf(request.get("qosId"));
			Pattern requestQosIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(requestQosId, requestQosIdPattern, 15,true)){
				dataSource.setParameter("requestQosId"          ,requestQosId);//number(15)
			}
			
		}else{
			populateDataSourceWithEmptyValuesForLogging(dataSource, "request");
		}
		
		
		//ACTION
		
		if( action != null && !action.isEmpty() ) {
			
			String actionSessionId = String.valueOf(action.get("sessionId"));
			if(validateStringWithPatternAndSize(actionSessionId, null, 50, false)){
				dataSource.setParameter("actionSessionId"      ,actionSessionId);//varchar(50)
			}
			
			String actionActionType = String.valueOf(action.get("actionType"));
			Pattern actionActionTypePattern = Pattern.compile("[a-zA-Z]+");
			if(validateStringWithPatternAndSize(actionActionType, actionActionTypePattern, 25, false)){
				dataSource.setParameter("actionActionType"     ,actionActionType);//varchar(25)
			}
			
			String actionActionData = String.valueOf(action.get("actionData")).replaceAll("\'", "");
			if(validateStringWithPatternAndSize(actionActionData, null, 2000, false)){
				dataSource.setParameter("actionActionData"     ,actionActionData);//varchar(2000)
			}
			
			String actionMainID = String.valueOf(action.get("mainID"));
			Pattern actionMainIDPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(actionMainID, actionMainIDPattern, 20,true)){
				dataSource.setParameter("actionMainID"         ,actionMainID);//varchar(20)
			}
			
			String actionProperties = String.valueOf(action.get("properties"));
			Pattern actionPropertiesPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(actionProperties, actionPropertiesPattern, 38,true)){
				dataSource.setParameter("actionProperties"     ,actionProperties);//number(38)
			}
			
			String actionSubscriptionId = String.valueOf(action.get("subscriptionId"));
			Pattern actionSubscriptionIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(actionSubscriptionId, actionSubscriptionIdPattern, 15,true)){
				dataSource.setParameter("actionSubscriptionId" ,actionSubscriptionId);//number(15)
			}
			
			String actionQosId = String.valueOf(action.get("qosId"));
			Pattern actionQosIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(actionQosId, actionQosIdPattern, 15,true)){
				dataSource.setParameter("actionQosId"          ,actionQosId);//number(15)
			}
			
		}else{
			populateDataSourceWithEmptyValuesForLogging(dataSource, "action");
		}
		        
		//RESPONSE
		
		if( response != null && !response.isEmpty() ) {
			
			String responseSessionId = String.valueOf(response.get("sessionId"));
			if(validateStringWithPatternAndSize(responseSessionId, null, 50, false)){
				dataSource.setParameter("responseSessionId"      ,responseSessionId);//varchar(50)
			}
			
			
			String responseActionType = String.valueOf(response.get("actionType"));
			Pattern responseActionTypePattern = Pattern.compile("[a-zA-Z]+");
			if(validateStringWithPatternAndSize(responseActionType, responseActionTypePattern, 25, false)){
				dataSource.setParameter("responseActionType"     ,responseActionType);//varchar(25)
			}
			
			String responseActionData = String.valueOf(response.get("actionData")).replaceAll("\'", "");
			if(validateStringWithPatternAndSize(responseActionData, null, 2000, false)){
				dataSource.setParameter("responseActionData"     ,responseActionData);//varchar(2000)
			}
			
			String responseMainID = String.valueOf(response.get("mainID"));
			Pattern responseMainIDPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(responseMainID, responseMainIDPattern, 20,true)){
				dataSource.setParameter("responseMainID"         ,responseMainID);//varchar(20)
			}
			
			String responseProperties = String.valueOf(response.get("properties"));
			Pattern responsePropertiesPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(responseProperties, responsePropertiesPattern, 38,true)){
				dataSource.setParameter("responseProperties"     ,responseProperties);//number(38)
			}
			
			String responseSubscriptionId = String.valueOf(response.get("subscriptionId"));
			Pattern responseSubscriptionIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(responseSubscriptionId, responseSubscriptionIdPattern, 15,true)){
				dataSource.setParameter("responseSubscriptionId" ,responseSubscriptionId);//number(15)
			}
			
			String responseQosId = String.valueOf(response.get("qosId"));
			Pattern responseQosIdPattern = Pattern.compile("([0-9])+");
			if(validateStringWithPatternAndSize(responseQosId, responseQosIdPattern, 15,true)){
				dataSource.setParameter("responseQosId"          ,responseQosId);//number(15)
			}
			
		}else{
			populateDataSourceWithEmptyValuesForLogging(dataSource, "response");
		}

		
		
		//DR
		
		String drEventId = String.valueOf(dr.get("drEventID"));
		if(validateStringWithPatternAndSize(drEventId, null, 2000, false)){
			dataSource.setParameter("drEventID"             ,drEventId            );// ?
		}
		
		String drTimestamp = String.valueOf(dr.get("drTimestamp"));
		Pattern drTimestampPattern = Pattern.compile("((20[0-9]{2})(((01|03|05|07|08|10|12)(((0)[1-9])|((1|2)[0-9])|(3(0|1))))|" +
				"((04|06|09|11)(((0)[1-9])|((1|2)[0-9])|(30)))|((02)(((0)[1-9])|((1|2)[0-9]))))((([0-1][0-9])|(2[0-3]))([0-5][0-9])" +
				"([0-5][0-9]))((\\-|\\+)(([0-1][0-9])|(2[0-3]))([0-5][0-9])))*");
		if(validateStringWithPatternAndSize(drTimestamp, drTimestampPattern, 0, false)){
			dataSource.setParameter("drTimestamp"           ,drTimestamp          );//tem regexp
		}
		
		String drOperation = String.valueOf(dr.get("drOperation"));
		Pattern drOperationPattern = Pattern.compile("[a-zA-Z]+");
		if(validateStringWithPatternAndSize(drOperation, drOperationPattern, 50, true)){
			dataSource.setParameter("drOperation"           ,drOperation          );//varchar(50)
		}
		
		String drOperationType = String.valueOf(dr.get("drOperationType"));
		if(validateStringWithPatternAndSize(drOperationType, null, 2000, true)){
			dataSource.setParameter("drOperationType"       ,drOperationType      );//?
		}
		
		String drAdditionalData = String.valueOf(dr.get("drAdditionalData"));
		if(validateStringWithPatternAndSize(drAdditionalData, null, 2000, true)){
			dataSource.setParameter("drAdditionalData"      ,drAdditionalData     );//?
		}
		
		String drMainID = String.valueOf(dr.get("drMAINID"));
		Pattern drMainIDPattern = Pattern.compile("([0-9])+");
		if(validateStringWithPatternAndSize(drMainID, drMainIDPattern, 20,true)){
			dataSource.setParameter("drMAINID"              ,drMainID             );//varchar(20)
		}
		
		String drDeviceType = String.valueOf(dr.get("drDeviceType"));
		Pattern drActionTypePattern = Pattern.compile("[a-zA-Z]+");
		if(validateStringWithPatternAndSize(drDeviceType, drActionTypePattern, 50,true)){
			dataSource.setParameter("drDeviceType"          ,drDeviceType         );//varchar(50)
		}
		
		String drSubscriberType = String.valueOf(dr.get("drSubscriberType"));
		if(validateStringWithPatternAndSize(drSubscriberType, drActionTypePattern, 50,true)){
			dataSource.setParameter("drSubscriberType"      ,drSubscriberType     );//varchar(50)
		}

		String drProductName = String.valueOf(dr.get("drProductName"));
		Pattern drProductNamePattern = Pattern.compile(".{1,50}");
		if(validateStringWithPatternAndSize(drProductName, drProductNamePattern, 50,true)){
			dataSource.setParameter("drProductName"         ,drProductName        );//varchar(50)
		}
		
		String drStatus = String.valueOf(dr.get("drStatus"));
		if(validateStringWithPatternAndSize(drStatus, null, 2000,true)){
			dataSource.setParameter("drStatus"              ,drStatus             );//varchar(50)
		}
		
		String drErrorDescription = String.valueOf(dr.get("drErrorDescription"));
		if(validateStringWithPatternAndSize(drErrorDescription, null, 2000,true)){
			dataSource.setParameter("drErrorDescription"    ,drErrorDescription   );//?
		}
		
		String drOriginatingNodeInfo = String.valueOf(dr.get("drOriginatingNodeInfo"));
		if(validateStringWithPatternAndSize(drOriginatingNodeInfo, null, 2000,true)){
			dataSource.setParameter("drOriginatingNodeInfo" ,drOriginatingNodeInfo);//?
		}
	}   
    private void populateDataSourceWithEmptyValuesForLogging(DataObject dataSource, String prefix) {
		
    	dataSource.setParameter(prefix.concat("SessionId")      ,"");
		dataSource.setParameter(prefix.concat("ActionType")     ,"");
		dataSource.setParameter(prefix.concat("ActionData")     ,"");
		dataSource.setParameter(prefix.concat("MainID")         ,"");
		dataSource.setParameter(prefix.concat("Properties")     ,"");
		dataSource.setParameter(prefix.concat("SubscriptionId") ,"");
		dataSource.setParameter(prefix.concat("QosId")          ,"");       
		
	}

	private boolean validateStringWithPatternAndSize(String parameter, Pattern regexp, int size, boolean verifyNullable) throws Exception {

    	boolean result;
    	if(size <= 0){
    		result = regexp.matcher(parameter).find();
    	}else if (regexp == null){
    		result = parameter.length() <= size;
    	}else{
    		result = regexp.matcher(parameter).find() && parameter.length() <= size;
    	}
    	
    	if(verifyNullable){
    		if(parameter.equals("") || parameter == null){
    			result = true;
    		}
    	}
    	
    	
    	if(!result){
    		throw createException(VALIDATION_MATCH_ERROR,"internal",ERROR_SOURCE_MODULE,"The data "+parameter+"(size of ="+parameter.length()+") did no match the regexp layout "+regexp.toString()+"   or the max size of "+size+".");
    	}
    	return result;
    	
	}

	private void removeAllDataSource(final DataObject dataSource) throws Exception
    {
        try 
        {
            HashSet<String> retainDataSource = new HashSet<String>();
            retainDataSource.add("__GlobalTransactionID");
            retainDataSource.add("WS_requestID");
            
            dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
        }
        catch (Exception e) 
        {
            throw createException(REMOVE_ALL_DATA_SOURCE,"internal",ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    private String formatErroDescription(Exception e) 
    {
        return new StringBuilder(e.getClass().getCanonicalName()).append(":").append(e.getMessage()).append("(").append(Arrays.toString(e.getStackTrace())).append(")").toString();
    }
    
    private Exception createException(String errorCode,String errorType, String errorSource, String errorDescription) throws Exception
    {
        if ((errorDescription != null) && (errorDescription.matches("(.*)DATABASE_ERROR(.+)")))
        {
            return new Exception(errorDescription);
        }
        
        return new Exception("DATABASE_ERROR|"+errorCode+"|"+errorType+"|"+errorSource+"|"+errorDescription);
    }
}
