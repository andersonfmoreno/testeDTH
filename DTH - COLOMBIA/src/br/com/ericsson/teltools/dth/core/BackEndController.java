package br.com.ericsson.teltools.dth.core;

/** HEADER STARTS **/
// Any changes between HEADER STARTS and HEADER ENDS will be discarded during validation and compilation
// Additions or alteration of import statements will be ignored and replaced with the original
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cmg.stdapp.javaformatter.DataObject;

//extends    br.com.ericsson.teltools.dth.debug.JavaFormatterBase
public class BackEndController 
    extends    br.com.ericsson.teltools.dth.debug.JavaFormatterBase
    implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
	private static final int SESSION_TIME_ALIVE_IN_MILI_SEC = 10000;
    private static final String ZERO = "0";
	private static final String PIPE = "|";
	private static final String INTERNAL = "internal";
	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
    Map<String, Object> mapVariables = new java.util.concurrent.ConcurrentHashMap<String, Object>();
   
	final String VARIABLES_CONFIG = "SUBJECT_ROLLBACK|MAY_CONTINUE_ON_ERROR|TAX_MONEY";

    private static final String EMPTY_STRING = "";
    
    private static final int NEVER_LOGGING = 0;
    private static final int ERROR_LOGGING = 1;
    private static final int ALWAYS_LOGGING = 2;
    private static final String DEFAULT_OPERATION = "default";
    
    final String ERROR_SOURCE_MODULE    = "dth"; 
    final String FIELD_DELIMITER         = "\\<\\;\\>";
    final String RECORD_DELIMITER        = "\\<\\.\\>";
    
    //-----------------------------------------------------------------
    // Error constants
    //-----------------------------------------------------------------

    final String REMOVE_ALL_DATA_SOURCE                 = "22000";   
    final String ORIGIN_NOT_FOUND                       = "22001";
    final String CORE_ERROR_CODE                        = "22002";
    final String FIND_BUSSINESS_RULES                   = "22003";
    final String BUSSINESS_RULES_NOT_FOUND              = "22004";
    final String VARIABLES_VALIDATION                   = "22005";
    final String ACTION_INVALID                         = "22006";
    final String GET_FIELD_COMPARE                      = "22007";
    final String FIELD_NOT_PRESENT                      = "22008";
    final String SET_DATA_MAP_VARIABLES                 = "22009";
    final String GET_TARGET_ACTION                      = "22010";
    final String GET_DATA_CONTAINER_ERROR               = "22011";
    final String DATA_NOT_MATCHED                       = "22012";
    final String GET_CONFIG_VARIABLES                   = "22013";
    final String VARIABLE_CONFIG_NOT_FOUND              = "22014";
    final String FIND_ACTIONS                           = "22015";
    final String NEXT_REQUEST_ACTION                    = "22016";
    final String GET_CURRENT_ACTION                     = "22017";
    final String PREVIOUS_COMMAND_ACTION                = "22018";
    final String PREVIOUS_SUBSCRIBER_ACTION             = "22019";
    final String SAVE_BACK_MODULE                       = "22020";
    final String GET_ALL_ELEMENTS_DATA_OBJECT           = "22021";
    final String NEXT_COMMAND_ACTION                    = "22022";
    final String NEXT_SUBSCRIBER_ACTION                 = "22023";
    final String FORMAT_MAP_EVENT_RESPONSE              = "22024";
    final String FORMAT_RESPONSE                        = "22025";
    final String LOGGING_MODULE                         = "22026";
    final String FORMAT_MAP_EVENT_TARGET                = "22027";
    final String SAVE_ROLLBACK_DATA                     = "22028";
    final String GET_QOS_ROLLBACK_DATA                  = "22029";
    final String GET_ORIGIN_NODE                        = "22030";
    final String GET_DR_PRODUCT_NAME                    = "22031";
    final String GET_ADDITIONAL_DATA_SUBSCRIPTION       = "22032";
    final String SET_AIR_VOLUME                         = "22033";
    final String FIELD_EXIST_MAPVARIABLES_AND_NOT_NULL  = "22034";
    final String GET_DB_TIME_ZONE                       = "22035";
    final String PARSE_CAL_STR_DB_TZ                    = "22036";    
    final String BITAND_PROPERTY                        = "22037";
    final String IS_PRESENT_PROPERTY                    = "22038";
    final String PARSE_STRING_TO_DATE                   = "22039";
    final String PARSE_DATA_TO_HUMAN                    = "22040";
    final String SET_AIR_INFO                    		= "22041";
    final String AUTHORIZE_LOGGING 						= "22042";
    final String ENTITY_NOT_FOUND 						= "22043";
    final String GET_ONLYONE_CONTAINER_DATA 			= "22044";
    final String CLEAN_MAP_VARIABLES                    = "22045";
    
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
        String sessionID = dataSource.getValueAsString("SESSION_ID");
        try 
        {
            if (!(dataSource.parameterExists("ORIGIN"))) 
            {
                throw createException(ORIGIN_NOT_FOUND, INTERNAL, ERROR_SOURCE_MODULE, "Origin not found");
            }
            else
            {
                if (dataSource.getValueAsString("ORIGIN").equals("SearchAndValidation"))
                {
                    Map<String, Object> map = (Map<String, Object>)dataSource.getValue("MAP_VARIABLES");
                    Map<String, Object> session = (Map<String, Object>)map.get(sessionID);

                    if (session.containsKey("ERROR_RESPONSE"))
                    {
                        Map<String, Object> error = (Map<String, Object>)session.get("ERROR_RESPONSE");
                        
                        removeAllDataSource(dataSource);
                        
                        StringBuffer errorCodeChanged          =  new StringBuffer();
                        StringBuffer errorDescriptionChanged   =  new StringBuffer();
                        StringBuffer errorSourceChanged        =  new StringBuffer();
                        StringBuffer errorTypeChanged          =  new StringBuffer();
                        
                        errorCodeChanged.append((String)error.get("ERROR_CODE"));
                        errorDescriptionChanged.append((String)error.get("ERROR_DESCRIPTION"));
                        errorSourceChanged.append((String)error.get("ERROR_SOURCE"));
                        errorTypeChanged.append((String)error.get("ERROR_TYPE"));
                        
                        String operationError = "REQUEST_INCONSISTENT";
                        
                        if(fieldExistMapVariablesAndNotNull("REQUEST.0.operation", session))
                        {
                            operationError = (String)getDataMapVariables("REQUEST.0.operation", session);
                        }
                        
                        changeError(operationError,errorCodeChanged,errorDescriptionChanged,errorSourceChanged,errorTypeChanged);
                        
                        dataSource.setParameter("ORIGIN"              , "BackEndController");
                        dataSource.setParameter("TARGET"              , "CoreResponse");
                        dataSource.setParameter("0.0.status"          , "error");
                        dataSource.setParameter("0.0.errorDescription", errorDescriptionChanged.toString());
                        dataSource.setParameter("0.0.errorSource"      , errorSourceChanged.toString());
                        dataSource.setParameter("0.0.errorType"          , errorTypeChanged.toString());
                        dataSource.setParameter("0.0.errorCode"          , errorCodeChanged.toString());
                        
                        return;
                    }
                    
                    mapVariables.put(sessionID, session);

                    if((((String)getDataMapVariables("REQUEST.0.operation",session)).equals("cleanMapVariables")) && !(dataSource.parameterExists("AIRCLEANED")))
                    {
                    	cleanMapVariables(sessionID, "0", dataSource);
                        dataSource.setParameter("SESSION_ID"        , sessionID     );
                        dataSource.setParameter("ORIGIN"            , "CONTROLLER"  );
                        dataSource.setParameter("TARGET"            , "AIR"         );
                        dataSource.setParameter("STATUS"            , "OK"        );
                        dataSource.setParameter("cleanMapVariables", "OK");
                        
                        return;
                    }
                                

                    removeAllDataSource(dataSource);

                    findActions(sessionID);

                    String[] startAction = getCurrentAction(sessionID);
                    

                    if (!(startAction == null))
                    {
                        formatMapEventTarget(startAction,dataSource,sessionID);
                    }
                    else 
                    {
                        formatResponse(sessionID);
                        loggingModule(sessionID,dataSource);
                    }
                    
                }

                else
                {
                	
                    if((dataSource.getValueAsString("ORIGIN").equals("AIR"))){
                        
                        if(dataSource.parameterExists("AIRCLEANED"))
                        {
                            dataSource.setParameter("SESSION_ID"        , sessionID     );
                            dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                            dataSource.setParameter("TARGET"            , "DATABASE"         );
                            dataSource.setParameter("STATUS"            , "OK"        );
                            dataSource.setParameter("cleanMapVariables", "OK");
                            
                            return;
                        }
                    }
                    
                    if (dataSource.parameterExists("LASTCLEANED"))
                    {
                    	dataSource.setParameter("TARGET", "CoreResponse");
                    	dataSource.removeParameter("LASTCLEANED");
                    	dataSource.removeParameter("STATUS");
                    	dataSource.setParameter("0.0.status","ok");
                    	return;
                    }
                    
                    if (dataSource.getValueAsString("ORIGIN").equals("LOGGING"))
                    {
                        removeAllDataSource(dataSource);
                        formatMapEventResponse(sessionID,dataSource);
                        mapVariables.remove(sessionID);
                    }
                    else
                    {
                        String statusBack         = dataSource.getValueAsString("STATUS");
                        saveBackModule(sessionID,dataSource);
                        Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
                        int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", session)); 
                        int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", session)); 
                        int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", session)); 
                        
                        Map<String, Object> statusActionMap = ((Map<String, Object>)getDataMapVariables("ACTION."+requestPosition, session));
                        
                        if (statusBack.equals("OK"))
                        {
                            if (((String)statusActionMap.get("STATUS")).equals("PROCESSING_OK"))
                            {
                                
                                if (((commandGroupPosition+1) == Integer.parseInt((String)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+".NUMBER_COMMAND", session))) &&    ((subscriberPosition+1) == Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+requestPosition+".NUMBER_SUBSCRIBER", session))))
                                {
                                    setDataMapVariables("ACTION."+requestPosition+".STATUS", "OK", session);
                                }
                                nextCommandAction(sessionID);
                            }
                            else if (((String)statusActionMap.get("STATUS")).equals("PROCESSING_ROLLBACK"))
                            {
                                if (commandGroupPosition == 0 && subscriberPosition == 0)
                                {
                                    setDataMapVariables("ACTION."+requestPosition+".STATUS", "ERROR", session);
                                    nextRequestAction(sessionID);
                                }
                                else
                                {
                                    previousCommandAction(sessionID);
                                }
                            }
                        }
                        else
                        {
                            String commandProperty = (String)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".PROPERTY", session);
                            
                            if (((String)statusActionMap.get("STATUS")).equals("PROCESSING_OK") && !(isPresentProperty(commandProperty, "MAY_CONTINUE_ON_ERROR", session)))
                            {
                                setDataMapVariables("ACTION."+requestPosition+".STATUS", "PROCESSING_ROLLBACK", session);
                            }
                            
                            if (((String)statusActionMap.get("STATUS")).equals("PROCESSING_ROLLBACK"))
                            {
            	
                                if (commandGroupPosition == 0 && subscriberPosition == 0)
                                {
                                    setDataMapVariables("ACTION."+requestPosition+".STATUS", "ERROR", session);
                                }
                            }
                            
                            if (((String)statusActionMap.get("STATUS")).equals("ERROR"))
                            {
                                nextRequestAction(sessionID);
                            }
                            else if (((String)statusActionMap.get("STATUS")).equals("PROCESSING_ROLLBACK"))
                            {
                                previousCommandAction(sessionID);
                            }
                            else
                            {
                                nextCommandAction(sessionID);
                            }
                        }

                        String[] startAction = getCurrentAction(sessionID);

                        if (!(startAction == null))
                        {
                            formatMapEventTarget(startAction,dataSource,sessionID);
                        }
                        else 
                        {
                            formatResponse(sessionID);
                            loggingModule(sessionID,dataSource);  
                        }
                    }
                }
            }    
        } 
        catch (Exception error) 
        {
            removeAllDataSource(dataSource);
            
            if(mapVariables.containsKey(sessionID))
            {
                mapVariables.remove(sessionID);
            }
            
            String errorCode        = CORE_ERROR_CODE;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
            
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors     = error.getMessage().split("\\<\\&\\>");
                errorCode           = errors[1];
                errorType           = errors[2];
                errorSource         = errors[3];
                errorDescription    = errors[4];
            }            
            
            dataSource.setParameter("ORIGIN", "BackEndController");
            dataSource.setParameter("TARGET", "CoreResponse");
            dataSource.setParameter("0.0.status","error");
            dataSource.setParameter("0.0.errorDescription",errorDescription);
            dataSource.setParameter("0.0.errorSource",errorSource);
            dataSource.setParameter("0.0.errorType",errorType);
            dataSource.setParameter("0.0.errorCode",errorCode);   
        }
        
    }
    
    public Map<String, Object> getMapVariables() {
		return mapVariables;
	}
    @SuppressWarnings("unchecked")
    private void cleanMapVariables(String sessionID, String responseID, DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String,Object> session  = (Map<String,Object>)mapVariables.get(sessionID);
            expungeExpiredSessions(dataSource);                
            
            Map<String,Object> resultID =(Map<String, Object>) dataSource.getValue("Expunge");

            if (resultID == null) {
                resultID = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                resultID.put("CLEANED","NOTHING TO CLEAN");
            }
            
            if(session == null || session.isEmpty()){
            	throw createException(SAVE_BACK_MODULE,INTERNAL,ERROR_SOURCE_MODULE,"MAPVARIABLES: " + mapVariables.toString() + " SESSIONID: " + sessionID);
            }

            setDataMapVariables("ACTION."+responseID+".DATA_SEARCH.DATABASE", resultID, session);
            
            setDataMapVariables("ACTION."+responseID+".O.NUMBER_COMMAND", "1", session);
            
        } 
        catch (Exception e) 
        {
            throw createException(CLEAN_MAP_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }    
    
    @SuppressWarnings("unchecked")
    private void expungeExpiredSessions(DataObject dataSource) throws Exception {
        
        try {
            for (String sessionID : mapVariables.keySet()) {
                Long timestamp = (Long) ((Map<String,Object>) mapVariables.get(sessionID)).get("TIMESTAMP");
                if(timestamp==null)
                {
                    timestamp = Calendar.getInstance().getTimeInMillis();
                    ((Map<String,Object>) mapVariables.get(sessionID)).put("TIMESTAMP", timestamp);
                }
                Long expungeTimestamp = timestamp + SESSION_TIME_ALIVE_IN_MILI_SEC;
                
                if (expungeTimestamp < Calendar.getInstance().getTimeInMillis()) {
                    mapVariables.remove(sessionID);
                }
            }
        }
        catch (Exception e) {
            throw createException("Expunge Expired Session",INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }   
    
    private void changeError(String operation,StringBuffer errorCodeChanged,StringBuffer errorDescriptionChanged,StringBuffer errorSourceChanged,StringBuffer errorTypeChanged)
    {
        long lErrorCode = Long.parseLong(errorCodeChanged.toString());
        
        String errorDescription = errorDescriptionChanged.toString(); 
        if(lErrorCode < 11000 || lErrorCode >= 13000)
        {
            if (operation.toString().equals("createNewSubscription"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("12002");
                errorDescriptionChanged.append("Can't create the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");
                
            }
            else if (operation.toString().equals("cancelSubscription"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("12003");
                errorDescriptionChanged.append("Can't cancel  the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");
            }
            else if (operation.toString().equals("suspendSubscription"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("12000");
                errorDescriptionChanged.append("Can't suspend the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");
            }
            else if (operation.toString().equals("reactivateSubscription"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("12001");
                errorDescriptionChanged.append("Can't reactivate the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("reSubscription"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("12007");
                errorDescriptionChanged.append("Can't create again the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("enquireElegido"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30009");
                errorDescriptionChanged.append("Can't format the enquireElegidos. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }            
            else if (operation.toString().equals("getAllCommercialProducts"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30004");
                errorDescriptionChanged.append("Can't recreate the subscription. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("getSubscriptions"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30005");
                errorDescriptionChanged.append("Can't query the subscriptions for the requested subscriber. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("changeSubscriberDetails"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30006");
                errorDescriptionChanged.append("Can't change the subscriber's details. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("subscribeCampaign"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30010");
                errorDescriptionChanged.append("Can't subscribe campaign. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("unsubscribeCampaign"))
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30011");
                errorDescriptionChanged.append("Can't unsubscribe campaign. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");

            }
            else if (operation.toString().equals("thresholdVolume"))
            {
            }
            else if (operation.toString().equals("thresholdTime"))
            {
            }   
            else
            {
                String errorCode = errorCodeChanged.toString();
                
                errorCodeChanged.setLength(0);
                errorDescriptionChanged.setLength(0);
                errorSourceChanged.setLength(0);
                errorTypeChanged.setLength(0);
                
                errorCodeChanged.append("30007");
                errorDescriptionChanged.append("Can't execute operation. Reason: "+errorCode+"."+errorDescription);
                errorSourceChanged.append("dth");
                errorTypeChanged.append("internal");                
            }
        }
    }
    
    /**
     * @param sessionID
     * @param dataSource
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void saveBackModule(String sessionID, DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String, Object> sessionMap        = ((Map<String, Object>)mapVariables.get(sessionID));
            if(sessionMap == null || sessionMap.isEmpty()){
            	throw createException(SAVE_BACK_MODULE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(new Exception("Parameter not Found in 'sessionMap'")));
            }
            int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", sessionMap));
            int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", sessionMap));
            int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", sessionMap));
            
            long endTask = Calendar.getInstance().getTimeInMillis();
            setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".endTask", String.valueOf(endTask), sessionMap);
            
            Map<String, Object> action             = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("ACTION")).get(String.valueOf(requestPosition)));
            Map<String, Object> mapEvent          = getAllElementsDataObject(dataSource);

            if(sessionMap == null || sessionMap.isEmpty())
            	throw createException(SAVE_BACK_MODULE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(new Exception("Parameter not Found in 'sessionMap'")));
            	
            
            if (mapEvent.containsKey("ERROR_CODE"))
            {
                action.put("ERROR_CODE"            , mapEvent.get("ERROR_CODE"));
                action.put("ERROR_DESCRIPTION"    , mapEvent.get("ERROR_DESCRIPTION"));
                action.put("ERROR_SOURCE"        , mapEvent.get("ERROR_SOURCE"));
                action.put("ERROR_TYPE"            , mapEvent.get("ERROR_TYPE"));
            }
            String status = (String)action.get("STATUS");
            if(mapEvent.containsKey("DATA_ROLLBACK") && (status.equals("PROCESSING_OK")))
            {
                setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", (Map<String, Object>)mapEvent.get("DATA_ROLLBACK"), sessionMap);
            }
                      
            if (((String)action.get("TYPE")).equals("QUERY"))
            {                
                setDataMapVariables("ACTION."+requestPosition+".DATA_SEARCH."+((String)mapEvent.get("ORIGIN")), (Map<String, Object>)mapEvent.get("DATA_SEARCH"), sessionMap);
            }
            if (fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".LOG_COMMAND", sessionMap))
            {
                Map<String, Object> log = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".LOG_COMMAND", sessionMap);
                int logLength = log.size();
                
                if(fieldExistMapVariablesAndNotNull("DATA_LOGGING", mapEvent)){
                	log.put(String.valueOf(++logLength), (String)mapEvent.get("DATA_LOGGING"));
                }else{
                	log.put(String.valueOf(++logLength), "null");
                }
            }
            else
            {
                setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".LOG_COMMAND.0.", ((String)mapEvent.get("DATA_LOGGING")), sessionMap);
            }
            if (mapEvent.containsKey("MSG_DATA"))
            {
                Map<String, Object> msgData = (Map<String, Object>)mapEvent.get("MSG_DATA");
                for(String idx : msgData.keySet())
                {
                    setDataMapVariables("ACTION."+requestPosition+".MSG_DATA."+idx, msgData.get(idx), sessionMap);
                    setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+".MSG_DATA."+idx, msgData.get(idx), sessionMap);
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(SAVE_BACK_MODULE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private Map<String, Object> getAllElementsDataObject(DataObject dataSource) throws  Exception
    {
        try 
        {
        	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> mapEvent = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            Set<String> keys = dataSource.getParameterContainer().keySet();

            for (String key : keys)
            {
                if (dataSource.getValue(key) != null){
                    mapEvent.put(key,dataSource.getValue(key));
                } else {
                    mapEvent.put(key,EMPTY_STRING);
                }
                
            }
            removeAllDataSource(dataSource);
            return mapEvent;
        } 
        catch (Exception e) 
        {
            throw createException(GET_ALL_ELEMENTS_DATA_OBJECT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void nextCommandAction(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> sessionMap = ((Map<String, Object>)mapVariables.get(sessionID));
            Map<String, Object> position   = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("CONTROLLER")).get("POSITION"));
            
            int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", sessionMap)); 
            int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", sessionMap)); 
            int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", sessionMap)); 
             
            Map<String, Object> action  = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("ACTION")).get(String.valueOf(requestPosition)));
            
            String status             = (String)action.get("STATUS");
            int numberCommand         = Integer.parseInt((String)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+".NUMBER_COMMAND", sessionMap));

            if (status.equals("ERROR"))
            {
                nextRequestAction(sessionID);
            }
            else if ((numberCommand-1) != commandGroupPosition)
            {
                position.put("COMMAND",String.valueOf(++commandGroupPosition));
            }
            else
            {
                nextSubscriberAction(sessionID);
            }
        } 
        catch (Exception e) 
        {
            throw createException(NEXT_COMMAND_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void nextSubscriberAction(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> controller    = ((Map<String, Object>)((Map<String, Object>)mapVariables.get(sessionID)).get("CONTROLLER"));
            Map<String, Object> position     = ((Map<String, Object>)controller.get("POSITION"));
            int requestPosition                   = Integer.parseInt((String)position.get("REQUEST")); 
            Map<String, Object> request      = ((Map<String, Object>)((Map<String, Object>)controller.get("COUNT_TRANSACTION")).get(String.valueOf(requestPosition)));
            int subscriberPosition               = Integer.parseInt((String)position.get("SUBSCRIBER")); 
            int numberSubscriber                  = Integer.parseInt((String)request.get("NUMBER_SUBSCRIBER"));

            if ((numberSubscriber-1) != subscriberPosition)
            {
                position.put("SUBSCRIBER", String.valueOf(++subscriberPosition));
                position.put("COMMAND",ZERO);
            }
            else
            {   
                nextRequestAction(sessionID);
            }
        } 
        catch (Exception e) 
        {
            throw createException(NEXT_SUBSCRIBER_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatMapEventResponse(String sessionID,DataObject dataSource) throws Exception
    {
        
        try 
        {
            final Map<String, Object> sessionMap = ((Map<String, Object>)mapVariables.get(sessionID));
            int numberRequest  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST",sessionMap));
            dataSource.setParameter("TARGET", "CoreResponse");
            dataSource.setParameter("ORIGIN", "BackEndController");
            dataSource.setParameter("SESSION_ID",sessionID);

            for (int requestId = 0; requestId < numberRequest ; requestId++)
            {
                Map<String, Object> response = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("RESPONSE")).get(String.valueOf(requestId)));
                if (response.containsKey("STATUS") && (!(((String)response.get("STATUS")).equals("OK"))))
                {
                    StringBuffer errorCodeChanged          =  new StringBuffer();
                    StringBuffer errorDescriptionChanged =  new StringBuffer();
                    StringBuffer errorSourceChanged      =  new StringBuffer();
                    StringBuffer errorTypeChanged          =  new StringBuffer();
                    
                    errorCodeChanged.append((String)response.get("ERROR_CODE"));
                    errorDescriptionChanged.append((String)response.get("ERROR_DESCRIPTION"));
                    errorSourceChanged.append((String)response.get("ERROR_SOURCE"));
                    errorTypeChanged.append((String)response.get("ERROR_TYPE"));
                    
                    changeError((String)getDataMapVariables("REQUEST."+requestId+".operation", sessionMap),errorCodeChanged,errorDescriptionChanged,errorSourceChanged,errorTypeChanged);
                    
                    dataSource.setParameter(requestId+".0.status"          ,((String)response.get("STATUS")).toLowerCase());
                    dataSource.setParameter(requestId+".0.errorDescription", errorDescriptionChanged.toString());
                    dataSource.setParameter(requestId+".0.errorSource"       , errorSourceChanged.toString());
                    dataSource.setParameter(requestId+".0.errorType"       , errorTypeChanged.toString());
                    dataSource.setParameter(requestId+".0.errorCode"       , errorCodeChanged.toString());
                    
                }
                else if(((String)getDataMapVariables("REQUEST."+requestId+".operation", sessionMap)).matches("getAllCommercialProducts|getSubscriptions|enquireElegido"))
                {
                    Set<String> keysResultID = response.keySet();
                    for (String key: keysResultID)
                    {
                        if ((key.matches("[0-9]+")))
                        {
                            Map<String, Object> resultIDs = ((Map<String, Object>)response.get(key));
                            Set<String> keysValues = resultIDs.keySet();
                            for (String keyValue: keysValues)
                            {
                                dataSource.setParameter(requestId+"."+key+"."+keyValue, (String)resultIDs.get(keyValue));
                            }
                        }
                    }
                }
                else
                {
                    dataSource.setParameter(requestId+".0.status",((String)response.get("STATUS")).toLowerCase());

                    //Map<String, Object> actions = (Map<String, Object>) getDataMapVariables("ACTION."+r, sessionMap);

                    /*
                    for (String key : actions.keySet())
                    {
                        if((key.matches("[0-9]+")))
                        {
                            
                            int numberCommand =  Integer.parseInt((String)getDataMapVariables("ACTION."+r+"."+key+".NUMBER_COMMAND", sessionMap));

                            
                            for(int i = 0; i < numberCommand;i++)
                            {
                                long startTask = Long.parseLong((String)getDataMapVariables("ACTION."+r+"."+key+"."+i+".startTask", sessionMap));
                                long endTask = Long.parseLong((String)getDataMapVariables("ACTION."+r+"."+key+"."+i+".endTask", sessionMap));
                                long result = endTask - startTask;
                                
                                String module = (String)getDataMapVariables("ACTION."+r+"."+key+"."+i+".MODULE", sessionMap);
                                String businessRuleId = (String)getDataMapVariables("ACTION."+r+"."+key+"."+i+".BUSINESS_RULE_ID", sessionMap);
                                String flowSeq = (String)getDataMapVariables("ACTION."+r+"."+key+"."+i+".FLOW_SEQ", sessionMap);
                                
                                //dataSource.setParameter(String.format("%s.%s.%s.%s.%s.time",r,key,module,businessRuleId,flowSeq),String.valueOf(result));
                            }
                            
                        }
                    }
                    */
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_MAP_EVENT_RESPONSE,"internal",ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    
    @SuppressWarnings("unchecked")
    private void formatResponse(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
            
            int numberRequest = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST", session));
            
            for (int requestId = 0; requestId < numberRequest ; requestId++)
            {
                Map<String, Object> response = ((Map<String, Object>)((Map<String, Object>)session.get("RESPONSE")).get(String.valueOf(requestId)));
                Map<String, Object> action   = ((Map<String, Object>)((Map<String, Object>)session.get("ACTION")).get(String.valueOf(requestId)));
                Map<String, Object> request  = ((Map<String, Object>)((Map<String, Object>)session.get("REQUEST")).get(String.valueOf(requestId)));
                
                String actionStatus = ((String)action.get("STATUS"));
                
                if (!(actionStatus.equals("OK")))
                {
                    response.put("STATUS","error");
                    response.put("ERROR_DESCRIPTION",((String)action.get("ERROR_DESCRIPTION")));
                    response.put("ERROR_CODE"        ,((String)action.get("ERROR_CODE")));
                    response.put("ERROR_SOURCE"        ,((String)action.get("ERROR_SOURCE")));
                    response.put("ERROR_TYPE"        ,((String)action.get("ERROR_TYPE")));
                }
                else 
                {
                    String requestOperation = ((String)request.get("operation"));

                    if (requestOperation.matches("getAllCommercialProducts|getSubscriptions"))
                    {   
                        Map<String, Object> queryData = ((Map<String, Object>)getDataMapVariables("ACTION."+requestId+".DATA_SEARCH.DATABASE", session));
                        setDataMapVariables("RESPONSE."+requestId, queryData, session);

                        if (requestOperation.equals("getSubscriptions"))
                        {
                            String volumeFlag = (String) getDataMapVariables("REQUEST."+ requestId + ".0.getVolume", session);
                            if(volumeFlag.equals("yes"))
                            {
                                setAirVolume(queryData, ((Map<String, Object>)getDataMapVariables("ACTION."+requestId+".DATA_SEARCH.AIR", session)));
                            }
                        }
                    }
                    else if (requestOperation.matches("enquireElegido"))
                    {  
                    	Map<String, Object> queryData = ((Map<String, Object>)getDataMapVariables("ACTION."+requestId+".DATA_SEARCH.DATABASE", session));
                    	Map<String, Object> airData = ((Map<String, Object>)getDataMapVariables("ACTION."+requestId+".DATA_SEARCH.AIR", session));
                        setAirInformations(String.valueOf(requestId), queryData, airData, request);
                        setDataMapVariables("RESPONSE."+requestId, queryData, session);
                    }                    
                    else
                    {
                        response.put("STATUS",actionStatus);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_RESPONSE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private void setAirInformations(String requestId, Map<String, Object> subscriptions,
			Map<String, Object> airData, Map<String, Object> request) throws Exception {
    	try{
    		@SuppressWarnings("unchecked")
			Map<String, Object> subscription = (Map<String, Object>) subscriptions.get(ZERO); 
    		@SuppressWarnings("unchecked")
			String requestfafIndicator = (String) ((Map<String, Object>)request.get(requestId)).get("FaFIdList");
	    	StringBuilder fafList = new StringBuilder();
	    	
	    	for(String index : airData.keySet()){
	    		@SuppressWarnings("unchecked")
				Map<String, Object> fafSubscriber = (Map<String, Object>) airData.get(index);
		    	if(requestfafIndicator.equals(fafSubscriber.get("fafIndicator"))){
		    		fafList.append(fafSubscriber.get("fafNumber"));
		    		fafList.append(PIPE);
		    	}
	    	}
	    	
	    	subscription.put("FaFList", fafList.toString());
    	}catch(Exception error){
    		throw createException(SET_AIR_INFO,INTERNAL,ERROR_SOURCE_MODULE, request.toString());
    	}
	}

	@SuppressWarnings("unchecked")
    private void setAirVolume(Map<String, Object> dataBaseData,Map<String, Object> airData) throws Exception
    {
        try{
            for(String idx:dataBaseData.keySet())
            {
                if (((String) ((Map<String, Object>) dataBaseData.get(idx)).get("subscriptionStatus")).matches("ACTIVE|SUSPENDED"))
                {
                    String productId = (String) ((Map<String, Object>) dataBaseData.get(idx)).get("productID");
                    String airVolume = (String) ((Map<String, Object>) airData.get(productId)).get("AIR_VOLUME_DEDICATED_ACCOUNT_VALUE");
                    ((Map<String, Object>) dataBaseData.get(idx)).put("volume",airVolume);
                }
            }
        }
        catch (Exception e) 
        {
            throw createException(SET_AIR_VOLUME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    private boolean fieldExistMapVariablesAndNotNull(String field, Map<String, Object> session) throws Exception
        {
            try 
            {
                if (fieldExistMapVariables(field, session))
                {
                    String object = (String)getDataMapVariables(field, session);
                    return (object != null && !object.isEmpty() && !object.toLowerCase().equals("null"));
                }
                return false;
            }
            catch (Exception e) 
            {
                throw createException(FIELD_EXIST_MAPVARIABLES_AND_NOT_NULL,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
            }
        }
    
    @SuppressWarnings("unchecked")
    private void loggingModule(String sessionID,DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String, Object> sessionMap = ((Map<String, Object>)mapVariables.get(sessionID));
            removeAllDataSource(dataSource);
            dataSource.setParameter("SESSION_ID",sessionID);
            dataSource.setParameter("ORIGIN", "BackEndController");
            dataSource.setParameter("TARGET", "LOGGING");
            dataSource.setParameter("STATUS", "EXECUTE");

            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> loggingList = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            dataSource.setParameter("LOGGING_LIST", loggingList);
            
            dataSource.setParameter("PROPERTIES", ZERO); // to do
            int numberRequest = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST", sessionMap));
            
            int subscriptionID;
            int QoS;
                
            for (int i = 0; i < numberRequest ; i++ )
            {
                subscriptionID=0;
                QoS = 0;
                int numberSubscribers = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+i+".NUMBER_SUBSCRIBER", sessionMap));
                
                for (int j = 0; j < 1; j++)
                {
                
                    String requestOperation = "";
                    if(fieldExistMapVariablesAndNotNull("TABLE."+i+"."+j+".SUBSCRIPTION.SUBSCRIPTION_ID", sessionMap))
                    {
                        subscriptionID = Integer.parseInt((String) getDataMapVariables("TABLE."+i+".0.SUBSCRIPTION.SUBSCRIPTION_ID", sessionMap));
                    }
                    
                    if (fieldExistMapVariables("REQUEST."+i+".operation", sessionMap))
                    {
                        requestOperation = (String)getDataMapVariables("REQUEST."+i+".operation", sessionMap);
                    }

                    if(fieldExistMapVariablesAndNotNull("ACTION."+i+"."+j+".MSG_DATA.QOS_ID", sessionMap))    
                    {
                        QoS = Integer.parseInt((String)getDataMapVariables("ACTION."+i+"."+j+".MSG_DATA.QOS_ID",sessionMap));
                    }
                    
                    if(fieldExistMapVariablesAndNotNull("ACTION."+i+"."+j+".MSG_DATA.MASTER_SUBSCRIPTION_ID", sessionMap))
                    {
                        subscriptionID = Integer.parseInt((String) getDataMapVariables("ACTION."+i+".MSG_DATA.MASTER_SUBSCRIPTION_ID",sessionMap));
                    }
                    else if(fieldExistMapVariablesAndNotNull("ACTION."+i+"."+j+".MSG_DATA.SUBSCRIPTION_ID", sessionMap))
                    {
                        subscriptionID = Integer.parseInt((String) getDataMapVariables("ACTION."+i+"."+j+".MSG_DATA.SUBSCRIPTION_ID",sessionMap));
                    }

                    //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> dbRequest  = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    Map<String, Object> dbAction   = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    Map<String, Object> dbResponse = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    
                    Map<String, Object> db = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    
                    db.put("REQUEST", dbRequest);
                    db.put("ACTION", dbAction);
                    db.put("RESPONSE", dbResponse);
                    
                    Map<String, Object> logging = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    logging.put("DB", db);
                    
                    loggingList.put(i+"."+j, logging);
                    
                    long mainID = 0;
                    
                    if(fieldExistMapVariables("REQUEST."+i+"."+j+".MAINID", sessionMap))
                    {
                        try 
                        {
                            mainID = Long.parseLong((String)getDataMapVariables("REQUEST."+i+"."+j+".MAINID", sessionMap));    
                        }
                        catch (Exception e) 
                        {
                            mainID = 0;
                        }
                        
                    }
                    
                    if(isLoggingAuthorized(sessionMap, "REQUEST")){
                    	dbRequest.put("sessionId",sessionID);
                    	dbRequest.put("actionType","REQUEST");
                    	dbRequest.put("mainID",mainID);
                    	dbRequest.put("properties",0);
                    	dbRequest.put("subscriptionId",subscriptionID);
                    	dbRequest.put("qosId",QoS);
                    }
                    
                    
                    if(isLoggingAuthorized(sessionMap, "ACTION")){
                    	dbAction.put("sessionId",sessionID);
                    	dbAction.put("actionType","ACTION");
                    	dbAction.put("mainID",mainID);
                    	dbAction.put("properties",0);
                    	dbAction.put("subscriptionId",subscriptionID);
                    	dbAction.put("qosId",QoS);
                    }
                    
                    
                    if(isLoggingAuthorized(sessionMap, "RESPONSE")){
                    	dbResponse.put("sessionId",sessionID);
                    	dbResponse.put("actionType","RESPONSE");
                    	dbResponse.put("mainID",mainID);
                    	dbResponse.put("properties",0);
                    	dbResponse.put("subscriptionId",subscriptionID);
                    	dbResponse.put("qosId",QoS);
                    }
                    
                    
                    if(fieldExistMapVariables("REQUEST."+i, sessionMap) && isLoggingAuthorized(sessionMap, "REQUEST"))
                    {
                    	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                        Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                        dataSource.setParameter("REQUEST", mapRequest);
                        Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+i, sessionMap);

                        for (final String idx : (Set<String>)mapRequestWs.keySet())
                        {
                            if (!(idx.matches("[0-9]+")))
                            {
                                mapRequest.put(idx,mapRequestWs.get(idx));
                                continue;
                            }
                            
                            if (idx.equals(String.valueOf(j)))
                            {
                                mapRequest.put(String.valueOf(j), mapRequestWs.get(idx));
                                continue;
                            }
                        }
                        
                        dbRequest.put("actionData",mapRequest.toString());
                    }
                    
                    if(requestOperation.matches("getAllCommercialProducts|getSubscriptions|enquireElegido"))
                    {
                    	if(isLoggingAuthorized(sessionMap, "ACTION")){
                    		dbAction.put("actionData","Query products data.");
                    	}
                    	if(isLoggingAuthorized(sessionMap, "RESPONSE")){
                    		dbResponse.put("actionData","List of products");
                    	}
                    }
                    else
                    {
                        if(fieldExistMapVariables("ACTION."+i, sessionMap) && isLoggingAuthorized(sessionMap, "ACTION"))
                        {
                        	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                            Map<String, Object> mapAction = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                            dataSource.setParameter("ACTION", mapAction);
                            Map<String, Object> mapActionWs = (Map<String, Object>)getDataMapVariables("ACTION."+i, sessionMap);

                            for (final String idx : (Set<String>)mapActionWs.keySet())
                            {
                                if (!(idx.matches("[0-9]+")))
                                {
                                    mapAction.put(idx,mapActionWs.get(idx));
                                    continue;
                                }
                                
                                if (idx.equals(String.valueOf(j)))
                                {
                                    mapAction.put(String.valueOf(j), mapActionWs.get(idx));
                                    continue;
                                }
                            }
                            int numberCommmandDataRollback = Integer.parseInt((String)getDataMapVariables("ACTION."+i+"."+j+".NUMBER_COMMAND", sessionMap));
                            for(int idx = 0; idx < numberCommmandDataRollback;idx++)
                            {
                                //throw createException("99999", "errorType", "errorSource", "ACTION."+i+"."+j+"."+idx+" | "+sessionMap);
                                if(fieldExistMapVariables("ACTION."+i+"."+j+"."+idx, sessionMap))
                                {
                                    Map<String, Object> command = (Map<String, Object>)getDataMapVariables("ACTION."+i+"."+j+"."+idx, sessionMap);    
                                    if(command.containsKey("DATA_ROLLBACK"))
                                    {
                                        command.remove("DATA_ROLLBACK");
                                    }
                                }
                                
                            }
                            dbAction.put("actionData",mapAction.toString());
                        }
                        
                        if(fieldExistMapVariables("RESPONSE."+i, sessionMap) && isLoggingAuthorized(sessionMap, "RESPONSE"))
                        {
                        	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                            Map<String, Object> mapResponse = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                            dataSource.setParameter("RESPONSE", mapResponse);
                            Map<String, Object> mapResponseWs = (Map<String, Object>)getDataMapVariables("RESPONSE."+i, sessionMap);

                            for (final String idx : (Set<String>)mapResponseWs.keySet())
                            {
                                if (!(idx.matches("[0-9]+")))
                                {
                                    mapResponse.put(idx,mapResponseWs.get(idx));
                                    continue;
                                }
                                
                                if (idx.equals(String.valueOf(j)))
                                {
                                    mapResponse.put(String.valueOf(j), mapResponseWs.get(idx));
                                    continue;
                                }
                            }
                            
                            dbResponse.put("actionData",mapResponse.toString());
                        }
                    }
                    
                    //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> dr = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    logging.put("DR", dr);
                    
                    String sOperation              = EMPTY_STRING;
                    String sOperationType          = EMPTY_STRING;
                    String sAdditionalData         = EMPTY_STRING;
                    String sDeviceType                = EMPTY_STRING;
                    String sProdutName             = EMPTY_STRING;
                    String sSubscriberType         = EMPTY_STRING;
                    String status                  = "ERROR" ;
                    String sErrorDescription     = EMPTY_STRING;
                    String sOriginatingNodeInfo = getOriginNode("REQUEST."+i+"."+j+".originatingNodeInfo", sessionMap);
                    
                    
                    if(fieldExistMapVariables("REQUEST."+i+"."+j+".deviceType", sessionMap))
                    {
                        sDeviceType = (String)getDataMapVariables("REQUEST."+i+"."+j+".deviceType", sessionMap);
                    }
                    else if(fieldExistMapVariables("TABLE."+i+"."+j+".MAINID.SUBSCRIBER.deviceType",sessionMap))
                    {
                        sDeviceType = (String)getDataMapVariables("TABLE."+i+"."+j+".MAINID.SUBSCRIBER.deviceType", sessionMap);
                    }
                    
                    if(fieldExistMapVariables("REQUEST."+i+"."+j+".subscriberType", sessionMap))
                    {
                        sSubscriberType = (String)getDataMapVariables("REQUEST."+i+"."+j+".subscriberType", sessionMap);
                    }
                    else if(fieldExistMapVariables("TABLE."+i+"."+j+".MAINID.SUBSCRIBER.subscriberType",sessionMap))
                    {
                        sSubscriberType = (String)getDataMapVariables("TABLE."+i+"."+j+".MAINID.SUBSCRIBER.subscriberType", sessionMap);
                    }
                    
                    if (fieldExistMapVariables("REQUEST."+i+".productName", sessionMap)) 
                    {
                        sProdutName = (String)getDataMapVariables("REQUEST."+i+".productName", sessionMap);
                    }
                    
                    if (fieldExistMapVariables("ACTION."+i+".STATUS", sessionMap)) 
                    {
                        String statusAction = (String)getDataMapVariables("ACTION."+i+".STATUS", sessionMap);
                        if(statusAction.matches("(.)*OK(.)*"))
                        {
                            status = "SUCCESS" ;
                        }
                    }
                    if (fieldExistMapVariablesAndNotNull("ACTION."+i+".ERROR_DESCRIPTION", sessionMap)) 
                    {
                        sErrorDescription = (String)getDataMapVariables("ACTION."+i+".ERROR_DESCRIPTION", sessionMap);
                    }
                    
                    if (fieldExistMapVariables("REQUEST."+i+".operation", sessionMap))
                    {
                        if(requestOperation.equals("createNewSubscription"))
                        {
                            if(!sOriginatingNodeInfo.equals("SDP"))
                            {
                                sOriginatingNodeInfo = "WS";    
                            }
                            
                            sOperation = "SUBSCRIPTION";
                            String finalPrice = "";

                            if(fieldExistMapVariables("TABLE."+i+"."+j+".PRODUCT.FINAL_PRICE", sessionMap))
                            {
                                finalPrice = (String)getDataMapVariables("TABLE."+i+"."+j+".PRODUCT.FINAL_PRICE", sessionMap);
                            }
                            
                            String finalVolume = "";
                            
                            if(fieldExistMapVariables("TABLE."+i+"."+j+".PRODUCT.FINAL_VOLUME", sessionMap))
                            {
                                finalVolume = (String)getDataMapVariables("TABLE."+i+"."+j+".PRODUCT.FINAL_VOLUME", sessionMap);
                            }
                            
                            String expiredDate = "";
                            
                            if ( fieldExistMapVariables("INTERNAL."+i+"."+j+".PROGRAMMED_FLAG", sessionMap) && ((String)getDataMapVariables("INTERNAL."+i+"."+j+".PROGRAMMED_FLAG", sessionMap)).equals("1"))
                            {
                                sOperationType = "PROGRAMMED";
                            }
                            else
                            {
                                sOperationType = "IMMEDIATE";
                            }
                            
                            if(fieldExistMapVariables("TABLE."+i+"."+j+".SUBSCRIPTION.END_TIMESTAMP", sessionMap))
                            {
                                String subscriptionEndTimeStamp = (String)getDataMapVariables("TABLE."+i+"."+j+".SUBSCRIPTION.END_TIMESTAMP", sessionMap);
                                if(subscriptionEndTimeStamp!=null && !subscriptionEndTimeStamp.isEmpty() && !subscriptionEndTimeStamp.toLowerCase().equals("null"))
                                {
                                    expiredDate = parseDefaultDateFormatToHumanDateFormat(subscriptionEndTimeStamp);    
                                }                                
                            }
                            
                            sAdditionalData = finalPrice+","+finalVolume+","+expiredDate;
                            sProdutName     = getDrProductName(sessionID, i, j) ;
                        }
                        if(requestOperation.equals("reSubscription"))
                        {
                            sOriginatingNodeInfo = "OLM";
                            sOperation      = "SUBSCRIPTION";
                            sOperationType  = "RECURRENT";
                            sProdutName     = getDrProductName(sessionID, i, j) ;
                        }
                        else if(requestOperation.equals("cancelSubscription"))
                        {
                            sOriginatingNodeInfo = "WS";
                            sOperation      = "CANCELLATION";
                            sOperationType  = "EXTERNAL_REQUEST";
                            if(fieldExistMapVariablesAndNotNull("REQUEST."+i+"."+j+".timeExpiredFlag", sessionMap))
                            {
                                if(((String)getDataMapVariables("REQUEST."+i+"."+j+".timeExpiredFlag", sessionMap)).equals("true"))
                                {
                                    sOperationType = "OLM_EXPIRY";
                                    sOriginatingNodeInfo = "OLM";
                                }
                            }
                            sProdutName     = getDrProductName(sessionID, i, j) ;
                        }
                        else if(requestOperation.equals("suspendSubscription"))
                        {
                            sOriginatingNodeInfo = "WS";
                            sOperation  = "SUSPENSION";
                            sProdutName = getDrProductName(sessionID, i, j) ;
                        }
                        else if(requestOperation.equals("reactivateSubscription"))
                        {
                            sOriginatingNodeInfo = "WS";
                            sOperation  = "REACTIVATION";
                            sProdutName = getDrProductName(sessionID, i, j) ;
                        }
                        else if(requestOperation.equals("thresholdTime"))
                        {
                            sOriginatingNodeInfo = "OLM";
                            sOperation = "QOS";
                            sOperationType = "REDUCTION";
                            sAdditionalData = getQoSRollbackData((Map<String, Object>)getDataMapVariables("TABLE."+i+"."+j+".SUBSCRIPTION", sessionMap),(Map<String, Object>)getDataMapVariables("TABLE."+i+"."+j+".QOS", sessionMap));
                        }
                        else if(requestOperation.equals("thresholdVolume"))
                        {
                            sOriginatingNodeInfo = "SDP";
                            sOperation         = "QOS";
                            sOperationType     = "REDUCTION";
                            if(((String)getDataMapVariables("INTERNAL."+i+"."+j+".CANCEL_FLAG", sessionMap)).equals("1"))
                            {
                                sOperation         = "CANCELLATION";
                                sOperationType     = "OLM_VOLUME";
                            }
                            sAdditionalData = getQoSRollbackData((Map<String, Object>)getDataMapVariables("TABLE."+i+"."+j+".SUBSCRIPTION", sessionMap),(Map<String, Object>)getDataMapVariables("TABLE."+i+"."+j+".QOS", sessionMap));
                            sProdutName     = getDrProductName(sessionID, i, j) ;
                        }
                    }
                    dr.put("drEventID"                ,sessionID);
                    
                    String mainID_string = String.valueOf(mainID);
                    if (fieldExistMapVariablesAndNotNull("TABLE."+i+"."+j+".PRODUCT.categoryName", sessionMap))
                    {
                         if (((String) getDataMapVariables("TABLE."+i+"."+j+".PRODUCT.categoryName", sessionMap)).matches("Shared Plan|Chuveirinho"))
                         {
                             if (fieldExistMapVariablesAndNotNull("REQUEST."+i+"."+j+".subscriberRole", sessionMap))
                            {
                                 mainID_string = getMAINIDChildren(i,numberSubscribers,sessionMap);
                            }
                            else
                            {
                                if(fieldExistMapVariablesAndNotNull("REQUEST."+i+"."+j+".masterSubscriberMAINID", sessionMap))
                                {
                                    String masterSubscriberMAINID = (String) getDataMapVariables("REQUEST."+i+"."+j+".masterSubscriberMAINID", sessionMap);
                                    mainID_string =  masterSubscriberMAINID+","+mainID_string;
                                }
                            }
                             
                         }
                    }
                        
                    String internalDateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", sessionMap);
                    dr.put("drTimestamp"            , parseDefaultDateFormatToHumanDateFormat(internalDateTimeExecution));
                    dr.put("drOperation"            ,sOperation);
                    dr.put("drOperationType"        ,sOperationType);
                    dr.put("drAdditionalData"        ,sAdditionalData);
                    dr.put("drMAINID"                ,mainID_string);
                    dr.put("drDeviceType"            ,sDeviceType);
                    dr.put("drSubscriberType"        ,sSubscriberType);
                    dr.put("drProductName"            ,sProdutName);
                    dr.put("drStatus"                ,status);
                    dr.put("drErrorDescription"        ,sErrorDescription.replaceAll("\\|", " or "));
                    dr.put("drOriginatingNodeInfo"    ,sOriginatingNodeInfo);
                    dr.put("requestNumber"           , String.valueOf(i));
                    
                }
                
            }
        } 
        catch (Exception e) 
        {
            throw createException(LOGGING_MODULE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
 
   //TODO
   private boolean isLoggingAuthorized(Map<String,Object> sessionMap, String loggingTag) throws Exception {
	   try {
		String DSMNOperation = (String)getDataMapVariables("REQUEST."+ZERO+".operation", sessionMap);
		String status = ((String) getDataMapVariables("ACTION."+ZERO+".STATUS", sessionMap)).trim().toLowerCase();

		Map<String,Object> loggingLevelByTag = null;
		
		try{
			loggingLevelByTag = getContainerData("OPERATION_LOG_LEVEL", DSMNOperation, "OPERATION_LOG_LEVEL", true);
			
		}catch(Exception e){
			loggingLevelByTag = getContainerData("OPERATION_LOG_LEVEL", DEFAULT_OPERATION, "OPERATION_LOG_LEVEL", true);
		}
		
		int loggingTagValue = Integer.valueOf((String) loggingLevelByTag.get(loggingTag));
		
		if(loggingTagValue == NEVER_LOGGING){
			return false;
		}else if(loggingTagValue == ERROR_LOGGING && status.equals("error")){
			return true;
		}else if(loggingTagValue == ALWAYS_LOGGING){
			return true;
		}else{
			return false;
		}
	   } catch (Exception e) {
		   throw createException(AUTHORIZE_LOGGING,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
	   }
	}

private String getMAINIDChildren(int requestId, int numberSubscribers, Map<String, Object> sessionMap) throws Exception
    {
       String separator = "";
       StringBuffer mainID = new StringBuffer();
       for (int i = 0; i < numberSubscribers; i++)
       {
             mainID.append(separator).append((String)getDataMapVariables("REQUEST."+requestId+"."+i+".MAINID", sessionMap));
             separator = ",";
       }
       return mainID.toString();
    }

   private String parseDefaultDateFormatToHumanDateFormat(String defaultDate) throws Exception
   {
       try
       {
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
       catch (Exception e) 
       {
               throw createException(PARSE_DATA_TO_HUMAN,INTERNAL,ERROR_SOURCE_MODULE,"Date not matched with layout YYYYMMDDHH24MISS-GMT ["+defaultDate+"]"+formatErroDescription(e));
       }

    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private String getAdditionalDataSubscription(String endTimeStamp,int request, int subscriber, String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            
            NumberFormat formatter = new DecimalFormat("0.00");
            
            String actualPrice     = EMPTY_STRING;
            String actualVolume = EMPTY_STRING;
            String expiryDate    = EMPTY_STRING;
            
            if(!endTimeStamp.isEmpty() && endTimeStamp.matches("20([0-9]{12})(\\-|\\+)([0-9]{4})"))
            {
                expiryDate = parseDefaultDateFormatToHumanDateFormat(endTimeStamp);
            }
            
            if(fieldExistMapVariables("TABLE."+request+"."+subscriber+".PRODUCT", session))
            {
                Map<String, Object> product = (Map<String, Object>)getDataMapVariables("TABLE."+request+"."+subscriber+".PRODUCT", session);
                
                if(product.containsKey("FINAL_PRICE"))
                {
                    actualPrice = (String)product.get("FINAL_PRICE");
                }
                
                if(product.containsKey("productVolume"))
                {
                    actualVolume = (String)product.get("productVolume");
                }
            }
            
            if(fieldExistMapVariables("TABLE."+request+"."+subscriber+".PROMOTION", session))
            {
                Map<String, Object> promotion = (Map<String, Object>)getDataMapVariables("TABLE."+request+"."+subscriber+".PROMOTION", session);
                
                if(promotion.containsKey("VOLUME"))
                {
                    actualVolume = (String)promotion.get("VOLUME");
                }
            }
            
            return formatter.format(Float.parseFloat(actualPrice))+","+actualVolume+","+expiryDate;
        }
        catch (Exception e) 
        {
            throw createException(GET_ADDITIONAL_DATA_SUBSCRIPTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
    private String getDrProductName(String sessionID, int request, int subscriber) throws Exception
    {
        try 
        {
            Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
            
            String productName    = EMPTY_STRING;
            String productVersion = EMPTY_STRING;
            String productRegion  = EMPTY_STRING;
            
            if(fieldExistMapVariables("TABLE."+request+"."+subscriber+".PRODUCT", session))
            {
                Map<String, Object> product = (Map<String, Object>)getDataMapVariables("TABLE."+request+"."+subscriber+".PRODUCT", session);
                
                productName    = (String)product.get("productName");
                productVersion = (String)product.get("productVersion");
                productRegion  = (String)product.get("productRegion");
                
            }
            
            return productName+"-"+productVersion+"-"+productRegion;
        }
        catch (Exception e) 
        {
            throw createException(GET_DR_PRODUCT_NAME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private String getQoSRollbackData(Map<String, Object> subscription, Map<String, Object> qos) throws Exception
    {
        try 
        {
            String qosDownloadBefore = (String)subscription.get("QOS_DOWNLOAD");
            String qosUploadBefore   = (String)subscription.get("QOS_UPLOAD");
            String qosDownloadAfter  = (String)qos.get("QOS_DOWNLOAD");
            String qosUploadAfter    = (String)qos.get("QOS_UPLOAD");
            
            return qosDownloadBefore+","+qosUploadBefore+","+qosDownloadAfter+","+qosUploadAfter;
        }
        catch (Exception e) 
        {
            throw createException(GET_QOS_ROLLBACK_DATA,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatMapEventTarget(String[] startAction,DataObject dataSource,String sessionID) throws Exception
    {
        try 
        {
            String businessRule = startAction[0];
            String flowSeq      = startAction[1];
            String module       = startAction[2];
            
            Map<String, Object> sessionMap        = ((Map<String, Object>)mapVariables.get(sessionID));
            
            int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", sessionMap)); 
            int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", sessionMap)); 
            int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", sessionMap)); 

            Map<String, Object> request    = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("REQUEST")).get(String.valueOf(requestPosition)));
            Map<String, Object> action   = ((Map<String, Object>)((Map<String, Object>)sessionMap.get("ACTION")).get(String.valueOf(requestPosition)));
            
            String actionID           = requestPosition+"."+subscriberPosition+"."+commandGroupPosition;
            String status             = ((String)action.get("STATUS"));

            if(status.equals("PROCESSING_OK"))
            {
                status = "EXECUTE";
            }
            else 
            {
                status = "ROLLBACK";
            }

            long startTask = Calendar.getInstance().getTimeInMillis();
            setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".startTask", String.valueOf(startTask), sessionMap);
            
            if (module.compareTo("AIR") == 0)
            {
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ACTION_ID"            , actionID        );
                dataSource.setParameter("ORIGIN"            , "CONTROLLER"  );
                dataSource.setParameter("TARGET"            , "AIR"         );
                dataSource.setParameter("STATUS"            , status        );
                dataSource.setParameter("BUSINESS_RULE_ID"    , businessRule    );
                dataSource.setParameter("FLOW_SEQ"            , flowSeq        );
                dataSource.setParameter("MAINID"            , ((String)((Map<String, Object>)request.get(String.valueOf(subscriberPosition))).get("MAINID")));
                
                if(fieldExistMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("INTERNAL", (Map<String, Object>)getDataMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("TABLE", (Map<String, Object>)getDataMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("REQUEST."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    dataSource.setParameter("REQUEST", mapRequest);
                    Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+requestPosition, sessionMap);

                    for (final String idx : (Set<String>)mapRequestWs.keySet())
                    {
                        if (!(idx.matches("[0-9]+")))
                        {
                            mapRequest.put(idx,mapRequestWs.get(idx));
                            continue;
                        }
                        
                        if (idx.equals(String.valueOf(subscriberPosition)))
                        {
                            mapRequest.put("SUBSCRIBER", mapRequestWs.get(idx));
                            continue;
                        }
                    }
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap))
                {
                    Map<String, Object> dataRollback = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap);
                    dataSource.setParameter("DATA_ROLLBACK", dataRollback);
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap))
                {
                    Map<String, Object> msgData = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap);
                    dataSource.setParameter("MSG_DATA", msgData);
                }
                
                
            }
            else if (module.compareTo("SMSC") == 0)
            {
                dataSource.setParameter("TARGET"        , "SMSC"                );
                dataSource.setParameter("SESSION_ID"    , sessionID             );
                dataSource.setParameter("ORIGIN"        , "BackEndController"      );
                dataSource.setParameter("ACTION_ID"     , actionID              );
                dataSource.setParameter("BUSINESS_RULE_ID" , businessRule          );
                dataSource.setParameter("FLOW_SEQ",flowSeq);
                dataSource.setParameter("STATUS"            , status        );
                
                if(fieldExistMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("INTERNAL", (Map<String, Object>)getDataMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("TABLE", (Map<String, Object>)getDataMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("REQUEST."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    dataSource.setParameter("REQUEST", mapRequest);
                    Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+requestPosition, sessionMap);

                    for (final String idx : (Set<String>)mapRequestWs.keySet())
                    {
                        if (!(idx.matches("[0-9]+")))
                        {
                            mapRequest.put(idx,mapRequestWs.get(idx));
                            continue;
                        }
                        
                        if (idx.equals(String.valueOf(subscriberPosition)))
                        {
                            mapRequest.put("SUBSCRIBER", mapRequestWs.get(idx));
                            continue;
                        }
                    }
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap))
                {
                    Map<String, Object> dataRollback = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap);
                    dataSource.setParameter("DATA_ROLLBACK", dataRollback);
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap))
                {
                    Map<String, Object> msgData = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap);
                    dataSource.setParameter("MSG_DATA", msgData);
                }                
                
                return;
            }
            else if (module.compareTo("DATABASE") == 0)
            {
                dataSource.setParameter("TARGET"            , "DATABASE"    );
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                dataSource.setParameter("ACTION_ID"         , actionID      );
                dataSource.setParameter("BUSINESS_RULE_ID"    , businessRule  );
                dataSource.setParameter("FLOW_SEQ"            , flowSeq        );
                dataSource.setParameter("STATUS"            , status        );

                if(fieldExistMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("INTERNAL", (Map<String, Object>)getDataMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("TABLE", (Map<String, Object>)getDataMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("REQUEST."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    dataSource.setParameter("REQUEST", mapRequest);
                    Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+requestPosition, sessionMap);

                    for (final String idx : (Set<String>)mapRequestWs.keySet())
                    {
                        if (!(idx.matches("[0-9]+")))
                        {
                            mapRequest.put(idx,mapRequestWs.get(idx));
                            continue;
                        }
                        
                        if (idx.equals(String.valueOf(subscriberPosition)))
                        {
                            mapRequest.put("SUBSCRIBER", mapRequestWs.get(idx));
                            continue;
                        }
                    }
                }
                if(fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap))
                {
                    Map<String, Object> dataRollback = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap);
                    dataSource.setParameter("DATA_ROLLBACK", dataRollback);
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap))
                {
                    Map<String, Object> msgData = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap);
                    dataSource.setParameter("MSG_DATA", msgData);
                }
                
                return;
                
            }
            else if (module.compareTo("SAPC") == 0)
            {
                dataSource.setParameter("TARGET"            , "SAPC"         );
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                dataSource.setParameter("ACTION_ID"         , actionID      );
                dataSource.setParameter("STATUS"            , status        );
                dataSource.setParameter("BUSINESS_RULE_ID"  , businessRule  );
                dataSource.setParameter("FLOW_SEQ",flowSeq);
                
                if(fieldExistMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("INTERNAL", (Map<String, Object>)getDataMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("TABLE", (Map<String, Object>)getDataMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("REQUEST."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    dataSource.setParameter("REQUEST", mapRequest);
                    Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+requestPosition, sessionMap);

                    for (final String idx : (Set<String>)mapRequestWs.keySet())
                    {
                        if (!(idx.matches("[0-9]+")))
                        {
                            mapRequest.put(idx,mapRequestWs.get(idx));
                            continue;
                        }
                        
                        if (idx.equals(String.valueOf(subscriberPosition)))
                        {
                            mapRequest.put("SUBSCRIBER", mapRequestWs.get(idx));
                            continue;
                        }
                    }
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap))
                {
                    Map<String, Object> dataRollback = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap);
                    dataSource.setParameter("DATA_ROLLBACK", dataRollback);
                }
                
                
                if(fieldExistMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap))
                {
                    Map<String, Object> msgData = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap);
                    dataSource.setParameter("MSG_DATA", msgData);
                }
                
                
                return;
                
            }
            else if (module.compareTo("BLACK_BERRY") == 0)
            {
                dataSource.setParameter("TARGET", "BLACK_BERRY");
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                dataSource.setParameter("ACTION_ID"         , actionID      );
                dataSource.setParameter("STATUS"            , status        );
                dataSource.setParameter("BUSINESS_RULE_ID"  , businessRule  );
                dataSource.setParameter("FLOW_SEQ",flowSeq);
                
                if(fieldExistMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    dataSource.setParameter("INTERNAL", (Map<String, Object>)getDataMapVariables("INTERNAL."+requestPosition+"."+subscriberPosition, sessionMap));
                }

                if(fieldExistMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    dataSource.setParameter("TABLE", (Map<String, Object>)getDataMapVariables("TABLE."+requestPosition+"."+subscriberPosition, sessionMap));
                    Map<String, Object> envelope = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    envelope.put("RESUME", populateXML().toString());
                    dataSource.setParameter("ENVELOPE", envelope);
                }

                if(fieldExistMapVariables("REQUEST."+requestPosition+"."+subscriberPosition, sessionMap))
                {
                    Map<String, Object> mapRequest = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    dataSource.setParameter("REQUEST", mapRequest);
                  //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    Map<String, Object> mapRequestWs = (Map<String, Object>)getDataMapVariables("REQUEST."+requestPosition, sessionMap);

                    for (final String idx : (Set<String>)mapRequestWs.keySet())
                    {
                        if (!(idx.matches("[0-9]+")))
                        {
                            mapRequest.put(idx,mapRequestWs.get(idx));
                            continue;
                        }
                        
                        if (idx.equals(String.valueOf(subscriberPosition)))
                        {
                            mapRequest.put("SUBSCRIBER", mapRequestWs.get(idx));
                            continue;
                        }
                    }
                }
                
                if(fieldExistMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap))
                {
                    Map<String, Object> dataRollback = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".DATA_ROLLBACK", sessionMap);
                    dataSource.setParameter("DATA_ROLLBACK", dataRollback);
                }
                
                
                if(fieldExistMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap))
                {
                    Map<String, Object> msgData = (Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+".MSG_DATA", sessionMap);
                    dataSource.setParameter("MSG_DATA", msgData);
                }
                
                
                return;
            } else if (module.equals("MOCK")) {
                dataSource.setParameter("TARGET", "MOCK");
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                dataSource.setParameter("ACTION_ID"         , actionID      );
                dataSource.setParameter("STATUS"            , status        );
                dataSource.setParameter("BUSINESS_RULE_ID"  , businessRule  );
                dataSource.setParameter("FLOW_SEQ",flowSeq);
            } else if (module.equals("MOCK_ERROR")) {
                dataSource.setParameter("TARGET", "MOCK_ERROR");
                dataSource.setParameter("SESSION_ID"        , sessionID     );
                dataSource.setParameter("ORIGIN"            , "BackEndController"  );
                dataSource.setParameter("ACTION_ID"         , actionID      );
                dataSource.setParameter("STATUS"            , status        );
                dataSource.setParameter("BUSINESS_RULE_ID"  , businessRule  );
                dataSource.setParameter("FLOW_SEQ",flowSeq);
            } 
            
            else 
            {
                throw createException(FORMAT_MAP_EVENT_TARGET,INTERNAL,ERROR_SOURCE_MODULE,"Module ["+module+"] not found!");
            }
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_MAP_EVENT_TARGET,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private static StringBuilder populateXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version='1.0' encoding='UTF-8'?>");
        xml.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        xml.append("<SOAP-ENV:Body>");
        xml.append("<ns1:submitSync xmlns:ns1=\"urn:ProvisioningRequestServer\"");
        xml.append("SOAP- ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
        xml.append("<request xmlns:ns2=\"java:provision.services.web.rpc.ejb\"");
        xml.append("xsi:type=\"ns2:ProvisionRequest\">");
        xml.append("<body xmlns:ns3=\"http://schemas.xmlsoap.org/soap/encoding/\"");
        xml.append("xsi:type=\"ns3:Array\" ns3:arrayType=\"ns2:ProvisionReqEntity[1]\">");
        xml.append("<item xsi:type=\"ns2:ProvisionReqEntity\">");
        xml.append("<items xsi:type=\"ns3:Array\" ns3:arrayType=\"ns2:ProvisionDataItem[2]\">");
        xml.append("<item xsi:type=\"ns2:ProvisionDataItem\">");
        xml.append("<name xsi:type=\"xsd:string\">MAINID</name>");
        xml.append("<optionalTokens xsi:type=\"ns3:Array\"");
        xml.append("ns3:arrayType=\"ns2:ReservedToken[]\" xsi:null=\"true\" />");
        xml.append("<data xsi:type=\"xsd:string\">#[MAINID]</data>");
        xml.append("</item>");
        xml.append("<item xsi:type=\"ns2:ProvisionDataItem\">");
        xml.append("<name xsi:type=\"xsd:string\">IMSI</name>");
        xml.append("<optionalTokens xsi:type=\"ns3:Array\"");
        xml.append("ns3:arrayType=\"ns2:ReservedToken[]\" xsi:null=\"true\" />");
        xml.append("<data xsi:type=\"xsd:string\">#[IMSI]</data>");
        xml.append("</item>");
        xml.append("</items>");
        xml.append("<subEntities xsi:type=\"ns3:Array\" ns3:arrayType=\"ns2:ProvisionReqEntity[]\"");
        xml.append("xsi:null=\"true\" />");
        xml.append("<name xsi:type=\"xsd:string\">subscriber</name>");
        xml.append("<optionalTokens xsi:type=\"ns3:Array\" ns3:arrayType=\"ns2:ReservedToken[]\"");
        xml.append("xsi:null=\"true\" />");
        xml.append("<type xsi:type=\"xsd:string\" xsi:null=\"true\" />");
        xml.append("</item>");
        xml.append("</body>");
        xml.append("<version xsi:type=\"xsd:string\">1.2</version>");
        xml.append("<optionalTokens xmlns:ns4=\"http://schemas.xmlsoap.org/soap/encoding/\"");
        xml.append("xsi:type=\"ns4:Array\" ns4:arrayType=\"ns2:ReservedToken[]\" xsi:null=\"true\" />");
        xml.append("<productType xsi:type=\"xsd:string\">Blackberry</productType>");
        xml.append("<transactionType xsi:type=\"xsd:string\">Resume</transactionType>");
        xml.append("<transactionId xsi:type=\"xsd:string\">1234567890</transactionId>");
        xml.append("<header xsi:type=\"ns2:ProvisionReqHeader\">");
        xml.append("<timeStamp xsi:type=\"xsd:string\">2002-07-16T12:12:12Z</timeStamp>");
        xml.append("<optionalTokens xmlns:ns5=\"http://schemas.xmlsoap.org/soap/encoding/\"");
        xml.append("xsi:type=\"ns5:Array\" ns5:arrayType=\"ns2:ReservedToken[]\" xsi:null=\"true\" />");
        xml.append("<sender xsi:type=\"ns2:ProvisionSender\">");
        xml.append("<password xsi:type=\"xsd:string\">utelme</password>");
        xml.append("<name xsi:type=\"xsd:string\" xsi:null=\"true\" />");
        xml.append("<optionalTokens xmlns:ns6=\"http://schemas.xmlsoap.org/soap/encoding/\"");
        xml.append("xsi:type=\"ns6:Array\" ns6:arrayType=\"ns2:ReservedToken[]\"");
        xml.append("xsi:null=\"true\" />");
        xml.append("<loginId xsi:type=\"xsd:string\">telus</loginId>");
        xml.append("<id xsi:type=\"xsd:string\" xsi:null=\"true\" />");
        xml.append("</sender>");
        xml.append("<timeFormat xsi:type=\"xsd:string\">CCYY-MM-DDThh:mm:ssTZD</timeFormat>");
        xml.append("</header>");
        xml.append("</request>");
        xml.append("</ns1:submitSync>");
        xml.append("</SOAP-ENV:Body>");
        xml.append("</SOAP-ENV:Envelope>");
        
        return xml;
    }
    
    @SuppressWarnings("unchecked")
    private String[] getCurrentAction(String sessionID) throws Exception
    {
        try 
        {
            String[] action = null;
            Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
            
            int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", session)); 
            int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", session));
            int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", session)); 

            if (requestPosition == -1)
            {
                return action;
            }

            String actionStatus = ((String)getDataMapVariables("ACTION."+requestPosition+".STATUS", session));  
            
            if (actionStatus.equals("ERROR") || actionStatus.equals("OK"))
            {
                nextRequestAction(sessionID);
                return getCurrentAction(sessionID);
            }
            
            String commandProperty = (String)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition+".PROPERTY", session);
            
            if (actionStatus.equals("PROCESSING_ROLLBACK") && !(isPresentProperty(commandProperty, "SUBJECT_ROLLBACK", session)))
            {
                previousCommandAction(sessionID);
                return getCurrentAction(sessionID);
            }            
            else
            {
                Map<String, Object> commandAction    = ((Map<String, Object>)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+"."+commandGroupPosition, session));
                
                action = new String[4];
                action[0] = (String)commandAction.get("BUSINESS_RULE_ID");
                action[1] = (String)commandAction.get("FLOW_SEQ");
                action[2] = (String)commandAction.get("MODULE");
                action[3] = (String)commandAction.get("PROPERTY");
                
                return action;
            }
        } 
        catch (Exception e) 
        {
            throw createException(GET_CURRENT_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private boolean isPresentProperty(String property, String propertyName, Map<String, Object> session) throws Exception
    {
        try 
        {
            String propertyValue     = (String)getDataMapVariables("CONFIG."+propertyName, session);
            return bitAndProperty(property,propertyValue);    
        }
        catch (Exception e) 
        {
            throw createException(IS_PRESENT_PROPERTY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
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
            throw createException(BITAND_PROPERTY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    
    
    @SuppressWarnings("unchecked")
    private void previousCommandAction(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            
            int commandGroupPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.COMMAND", session)); 

            if (commandGroupPosition != 0)
            {
                setDataMapVariables("CONTROLLER.POSITION.COMMAND", String.valueOf(--commandGroupPosition), session);
            }
            else
            {
                previousSubscriberAction(sessionID);
            }
        } 
        catch (Exception e) 
        {
            throw createException(PREVIOUS_COMMAND_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void previousSubscriberAction(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);

            int requestPosition       = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", session)); 
            int subscriberPosition    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", session)); 

            if (subscriberPosition != 0)
            {
                setDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER", String.valueOf(--subscriberPosition), session);
                int numberCommand =  Integer.parseInt((String)getDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+".NUMBER_COMMAND", session));
                setDataMapVariables("ACTION."+requestPosition+"."+subscriberPosition+".NUMBER_COMMAND", String.valueOf(numberCommand-1), session);
                setDataMapVariables("CONTROLLER.POSITION.COMMAND", String.valueOf(numberCommand-1), session);
            }
            else
            {   
                nextRequestAction(sessionID);
            }
        } 
        catch (Exception e) 
        {
            throw createException(PREVIOUS_SUBSCRIBER_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private long getConfigVariables(String sessionID,String key) throws Exception
    {
        try 
        {
            Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
            
            if (fieldExistMapVariables("CONFIG."+key, session))
            {
                return Long.parseLong((String)getDataMapVariables("CONFIG."+key, session));
            }
            else
            {
                throw createException(VARIABLE_CONFIG_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Variable of config "+key+" not found");
            }
        } 
        catch (Exception e) 
        {
            throw createException(GET_CONFIG_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void nextRequestAction(String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
            
            int requestPosition  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.POSITION.REQUEST", session)); 
            int numberRequest    = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST", session)); 
            
            if ((numberRequest-1) == requestPosition )
            {
                setDataMapVariables("CONTROLLER.POSITION.REQUEST"   ,"-1", session);   
                setDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER","-1", session);
                setDataMapVariables("CONTROLLER.POSITION.COMMAND"   ,"-1", session);
            }
            else
            {
                setDataMapVariables("CONTROLLER.POSITION.REQUEST"   ,String.valueOf(++requestPosition), session);   
                setDataMapVariables("CONTROLLER.POSITION.SUBSCRIBER",ZERO, session);
                setDataMapVariables("CONTROLLER.POSITION.COMMAND"   ,ZERO, session);
            }
        } 
        catch (Exception e) 
        {
            throw createException(NEXT_REQUEST_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void findActions(String sessionID) throws Exception
    {
        final Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));

        try 
        {
            
            final int countResquest  = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST", session));
            
            for (int r = 0; r < countResquest; r++ )
            {
                int countSubscriber =Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+r+".NUMBER_SUBSCRIBER", session));

                for (int s = 0; s < countSubscriber ; s++ )
                {
                    String originNode = getOriginNode("REQUEST."+r+"."+s+".originatingNodeInfo", session);
                    
                    Map<String, Object> Target = findBussinessRules(sessionID,r,s,originNode);
                    
                    Target.put("NUMBER_COMMAND",String.valueOf(Target.size()));
                    setDataMapVariables("ACTION."+r+"."+s, Target, session);
                }
            }            
        } 
        catch (Exception e) 
        {
            throw createException(FIND_ACTIONS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    private String getOriginNode(String field, Map<String, Object> session) throws Exception 
    {
        try 
        {
            if(!fieldExistMapVariables(field, session))
            {
                return "WS";
            }
            
            String node = (String)getDataMapVariables(field, session);
            
            if(node.isEmpty())
            {
                return "WS";
            }
            
            if(node.equals("SDP"))
            {
                return "SDP";
            }

            if(node.equals("dth"))
            {
                return "dth";
            }

            return "WS";
            
        }
        catch (Exception e) 
        {
            throw createException(GET_ORIGIN_NODE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> findBussinessRules(String sessionID, int resquesID, int subscriberID, String OriginNode) throws Exception
    {
        Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
        try 
        {
        	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> hReturn = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            String  actionStatus = (String)getDataMapVariables("ACTION."+resquesID+".STATUS", session);

            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            if (actionStatus.equals("ERROR"))
            {
            	Map<String, Object> log = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            	log.put("LOG_COMAND", "SENT TO ERROR");
                hReturn.put(ZERO,log);
            }
            else if (actionStatus.equals("OK"))
            {
                Map<String, Object> log = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            	log.put("LOG_COMAND", "QUERY");
                hReturn.put(ZERO,log);
            }
            else 
            {
                 DataObject cBusinessRulesOriginFrontEnd = getEntity("BUSINESS_RULES_BY_ORIGIN_FRONT_END").getData();
                 String rulesList  = cBusinessRulesOriginFrontEnd.getValueAsString(OriginNode);
                 Map<String, Object> rules = getDataContainer("BUSINESS_RULES_BY_ORIGIN_FRONT_END", rulesList);
                 int rulesLength = rules.size();
                
                 for (int i = 0 ; i < rulesLength ; i++)
                 {
                     String ruleID = (String)((Map<String, Object>)rules.get(String.valueOf(i))).get("RULE_ID");
                     DataObject cBusinessRules= getEntity("BUSINESS_RULES_BY_ID").getData();
                     String fieldComparationList  = cBusinessRules.getValueAsString(ruleID);
                     Map<String, Object> rulesID = (Map<String, Object>)getDataContainer("BUSINESS_RULES_BY_ID", fieldComparationList);
                     
                     if (variablesValidation(rulesID,resquesID,subscriberID,sessionID))
                     {
                         hReturn = getTargetAction(ruleID);
                         break;
                     }     
                 }
            }

            if (hReturn.isEmpty())
            {
                throw createException(BUSSINESS_RULES_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Business rule not found!");
            }
            
            return hReturn;

        } 
        catch (Exception e) 
        {
            throw createException(FIND_BUSSINESS_RULES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }        
    }

    
    private Map<String,Object> getContainerData(String entityName, String key, String layout, boolean onlyOne) throws Exception
    {
        try 
        {
            Map<String,Object> containerData = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            DataObject cContainerData;
            try 
            {
                cContainerData = getEntity(entityName).getData();
            }
            catch (Exception e) 
            {
                throw createException(ENTITY_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Entity ["+entityName+"] not exist in container Controller");
            }
            
            if(key == null || key.isEmpty())
            {
                throw createException(ENTITY_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Entity ["+entityName+"] dataSource [NULL] not exist in container Controller");
            }
            
            if(!(cContainerData.parameterExists(key)))
            {
                throw createException(ENTITY_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Entity ["+entityName+"] dataSource ["+key+"] not exist in container Controller");
            }
            
            String dataObject = cContainerData.getValueAsString(key);
            
            containerData = getDataContainer(layout, dataObject, onlyOne);  
            
            return containerData;
            
        } 
        catch (Exception e)
        {
            throw createException(GET_ONLYONE_CONTAINER_DATA,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    
    @SuppressWarnings({ "unchecked" })
    private Map<String, Object> getDataContainer(String layout, String data,boolean single) throws Exception
    {
        try 
        {
            Map<String, Object> dataContainer = getDataContainer(layout,data);
            if (single)
            {
                return (Map<String, Object>)dataContainer.get(ZERO);
            }
            return dataContainer;
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER_ERROR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }        
    }
    
    private Map<String, Object> getDataContainer(String layout, String data) throws Exception
    {
        try 
        {
            DataObject layoutContainer = getEntity("LAYOUT").getData();
            String[] fields = layoutContainer.getValueAsString(layout).split(FIELD_DELIMITER);
            int countFields = fields.length;
            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> lineDataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            String[] lines = data.split(RECORD_DELIMITER);
            int i = 0;
            
            for(final String line : lines)
            {
            	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                Map<String, Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                String[] dataFields = line.split(FIELD_DELIMITER);
                
                if((dataFields == null) || (dataFields.length != countFields))
                {
                    throw createException(DATA_NOT_MATCHED,INTERNAL,ERROR_SOURCE_MODULE,"The data not matched the layout ["+layout+"]");
                }
                
                int j = 0;
                
                for(final String dataField : dataFields)
                {
                    dataContainer.put(fields[j], dataField);
                    j++;
                }
                
                j = 0;
                lineDataContainer.put(String.valueOf(i),dataContainer);
                i++;
            }

            return lineDataContainer;    
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER_ERROR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getTargetAction(String Rule) throws Exception
    {
        try 
        {
        	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> commands = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            Map<String, Object> dataCommands = new java.util.concurrent.ConcurrentHashMap<String, Object>();

            DataObject cBusinessRules = getEntity("COMMAND_GROUP").getData();
            String businessRulesList = cBusinessRules.getValueAsString(Rule);
            
            Map<String, Object> businessRules = getDataContainer("COMMAND_GROUP", businessRulesList);
            
            for ( final String idx : (Set<String>)businessRules.keySet() )
            {
            	//Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                dataCommands = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                dataCommands.put("BUSINESS_RULE_ID",Rule);
                dataCommands.put("FLOW_SEQ",((Map<String, Object>)businessRules.get(idx)).get("FLOW_SEQ"));
                dataCommands.put("MODULE",((Map<String, Object>)businessRules.get(idx)).get("MODULE"));
                dataCommands.put("PROPERTY",((Map<String, Object>)businessRules.get(idx)).get("PROPERTY"));

                commands.put(idx,dataCommands);
            }
            
            return commands;            
        } 
        catch (Exception e) 
        {
            throw createException(GET_TARGET_ACTION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean variablesValidation(Map<String, Object> ListComp,int resquesID, int subscriberID,String sessionID) throws Exception
    {
        try 
        {
            
            for (final String compareIdx : (Set<String>)ListComp.keySet())
            {
                String sField  = getFieldCompare((String)((Map<String, Object>)ListComp.get(compareIdx)).get("FIELD"),resquesID,subscriberID,sessionID);
                String sAction = (String)(((Map<String, Object>)ListComp.get(compareIdx)).get("ACTION"));
                String sValue  = (String)((Map<String, Object>)ListComp.get(compareIdx)).get("VALUE");
                
                if (sAction.compareTo("==") == 0)
                {
                    if (sField.compareTo(sValue) != 0)
                    {
                        return false;
                    }
                }
                else if (sAction.compareTo("!=") == 0)
                {
                    if (sField.compareTo(sValue) == 0)
                    {
                        return false;
                    }
                }
                else if (sAction.compareTo("<") == 0)
                {
                    if (sField.compareTo(sValue) >= 0)
                    {
                        return false;
                    }
                }
                else if (sAction.compareTo(">") == 0)
                {
                    if (sField.compareTo(sValue) <= 0)
                    {
                        return false;
                    }
                }
                else if (sAction.compareTo("<=") == 0)
                {
                    if (sField.compareTo(sValue) > 0)
                    {
                        return false;
                    }
                }
                else if (sAction.compareTo(">=") == 0)
                {
                    if (sField.compareTo(sValue) < 0)
                    {
                        return false;
                    }
                }
                else
                {
                    throw createException(ACTION_INVALID,INTERNAL,ERROR_SOURCE_MODULE,"Action not Found ["+sAction+"]");
                }
            }

            return true;
        } 
        catch (Exception e) 
        {
            throw createException(VARIABLES_VALIDATION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private String getFieldCompare(String comparator,int resquesID, int subscriberID,String sessionID) throws Exception
    {
        try 
        {
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            
            String[] parse =  comparator.split("\\.");

            if (parse.length == 2)
            {
                String path = parse[0]+"."+resquesID+"."+parse[1];
                if(fieldExistMapVariables(path, session))
                {
                    return ((String)getDataMapVariables(path, session));
                }
                else
                {
                    return "!@#$%???&*(";
                }
            }
            else if (parse.length >= 3)
            {
                StringBuilder path = new StringBuilder(parse[0]+"."+resquesID+"."+subscriberID);
                for(int index = 2; index < parse.length; index++)
                {
                    path.append("."+parse[index]);
                }
                if(fieldExistMapVariables(path.toString(), session))
                {
                    return ((String)getDataMapVariables(path.toString(), session));
                }
                else
                {
                    return "!@#$%???&*(";
                }
            }
            else
            {
                return "VARIABLES_NOT_FOUND";
            }
        } 
        catch (Exception e) 
        {
            throw createException(GET_FIELD_COMPARE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
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
                throw createException(FIELD_NOT_PRESENT,INTERNAL,ERROR_SOURCE_MODULE,"Field "+field+" not present in dataSource");
            }
            
            fieldValue = ((Map<String, Object>)fieldValue).get(levels[i]);
            i++;
        }
        
        return fieldValue;
    }
    
    @SuppressWarnings("unchecked")
    private boolean fieldExistMapVariables(String field, Map<String, Object> mapVariables) throws Exception
    {
        String[] levels = field.split("\\.");
        int countLevel = levels.length;
        int i = 0;
        Object fieldValue = mapVariables;
         
        while (i < countLevel)
        {
            if (!(((Map<String, Object>)fieldValue).containsKey(levels[i])))
            {
                return false;
            }
            
            fieldValue = ((Map<String, Object>)fieldValue).get(levels[i]);
            i++;
        }
        
        return true;
    }

		/**
         * Method to set a key/value pair into Datasource
         * @param key
         * @param value
         * @param vars
         * @throws Exception
         */
        @SuppressWarnings("unchecked")
        private void setDataMapVariables(String key, Object value, Map<String,Object> vars) throws Exception {
            try {
				if (key == null || key.isEmpty()) {
					throw new Exception("setDataMapVariables(key == null)");
				}
				
				if (value == null) {
					/*
					 * if value == null then set itself to an "null" String
					 * Workarround done to attempt all Map<String, Object> and new ConcurrentHashMap<String, Map>
					 * change that force a (cast) on a null reference
					 */
					value = "null";
				}
				
				if (vars == null) {
					throw new Exception("setDataMapVariables(vars == null)");
				}
				
                String[] keyNames = key.split("\\.");
                
                int levels = keyNames.length - 1;
                int level = 0;
                
                Map<String,Object> record = vars;
                 
                while (level < levels) {
                	Object levelValue = record.get(keyNames[level]);
                	
					if (levelValue == null) {
						record.put(keyNames[level], new java.util.concurrent.ConcurrentHashMap<String, Object>());
					}
					
					record = (Map<String, Object>) record.get(keyNames[level]);
                    level++;
                }
                
                record.put(keyNames[level], value);
            } catch (Exception e) {
                throw createException(SET_DATA_MAP_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
            }
        }
 
    private Exception createException(String errorCode,String errorType, String errorSource, String errorDescription) throws Exception
    {
        if ((errorDescription != null) && (errorDescription.matches("(.*)CORE_ERROR(.+)")))
        {
            return new Exception(errorDescription);
        }
        
        return new Exception("CORE_ERROR<&>"+errorCode+"<&>"+errorType+"<&>"+errorSource+"<&>"+errorDescription);
    }
    
    private String formatErroDescription(Exception e) 
    {
        return new StringBuilder(e.getClass().getCanonicalName()).append(":").append(e.getMessage()).toString();
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
            throw createException(REMOVE_ALL_DATA_SOURCE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
}
