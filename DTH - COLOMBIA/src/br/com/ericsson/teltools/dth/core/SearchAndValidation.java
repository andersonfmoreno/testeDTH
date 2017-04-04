package br.com.ericsson.teltools.dth.core;
    

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.Entity;

public class SearchAndValidation 
extends cmg.stdapp.javaformatter.definition.JavaFormatterBase implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface
/** HEADER ENDS **/
{
    
    private static final int SESSION_TIME_ALIVE_IN_MILI_SEC = 10000;
    private static final String TRUE = "TRUE";
    private static final String PIPE = "|";
    private static final String DOT = ".";
    private static final String ZERO = "0";
    private static final String EMPTY = "";
    final String INTERNAL = "internal";
    final String ANATEL_ENTITY_CONTAINER = "ANATEL_533";

    Map<String,Object> mapVariables  = new java.util.concurrent.ConcurrentHashMap<String, Object>();
    
    final String FIELD_DELIMITER         = "\\<\\;\\>";
    final String RECORD_DELIMITER        = "\\<\\|\\>";
    final String LINE_DELIMITER          = "\\<\\.\\>";
    
    //STATUS
    final String FULL                    = "FULL";
    final String RESTRICTED              = "RESTRICTED";
    final String VOLUME_EXPIRED          = "VOLUME_EXPIRED";
    final String TIME_EXPIRED            = "TIME_EXPIRED";
    final String CANCELLED               = "CANCELLED";
    final String SUSPENDED               = "SUSPENDED";
    final String NO_PLAN                 = "NO_PLAN";
    final String QUEUED                   = "QUEUED";
    
    final String[] PRODUCT_PRIORITY = { "PRIORITY_0","PRIORITY_1","PRIORITY_2","PRIORITY_3","PRIORITY_4","PRIORITY_5","PRIORITY_6","PRIORITY_7","PRIORITY_8","PRIORITY_9","PRIORITY_10","PRIORITY_11","PRIORITY_12","PRIORITY_13","PRIORITY_14","PRIORITY_15","PRIORITY_16","PRIORITY_17","PRIORITY_18","PRIORITY_19","PRIORITY_20","PRIORITY_21","PRIORITY_22","PRIORITY_23","PRIORITY_24","PRIORITY_25","PRIORITY_26","PRIORITY_27","PRIORITY_28","PRIORITY_29","PRIORITY_30","PRIORITY_31","PRIORITY_32","PRIORITY_33","PRIORITY_34","PRIORITY_35","PRIORITY_36","PRIORITY_37","PRIORITY_38","PRIORITY_39","PRIORITY_40","PRIORITY_41","PRIORITY_42","PRIORITY_43"};
    final String[] VARIABLES_CONFIG = { "SUBJECT_ROLLBACK","MAY_CONTINUE_ON_ERROR","HIDDEN_PRODUCT","INDEBT_SUBSCRIBER","UNREGISTERED_SUBSCRIBER","PRIORITY_0","PRIORITY_1","PRIORITY_2","PRIORITY_3","PRIORITY_4","PRIORITY_5","PRIORITY_6","PRIORITY_7","PRIORITY_8","PRIORITY_9","PRIORITY_10","PRIORITY_11","PRIORITY_12","PRIORITY_13","PRIORITY_14","PRIORITY_15","PRIORITY_16","PRIORITY_17","PRIORITY_18","PRIORITY_19","PRIORITY_20","PRIORITY_21","PRIORITY_22","PRIORITY_23","PRIORITY_24","TAX_MONEY","BILLING_CYCLE","PRIORITY_25","PRIORITY_26","PRIORITY_27","PRIORITY_28","PRIORITY_29","PRIORITY_30","PRIORITY_31","PRIORITY_32","PRIORITY_33","PRIORITY_34","PRIORITY_35","PRIORITY_36","PRIORITY_37","PRIORITY_38","PRIORITY_39","PRIORITY_40","PRIORITY_41","PRIORITY_42","PRIORITY_43"};
    final String[] PROPERTIES_GET_ALL_COMERCIAL_PRODUCTS = { "productProperties","taxProperties","categoryProperties", "recurrenceProperties"};

    final String OPERATIONS_THAT_NEED_OF_SUBSCRIBERS_AND_SUBSCRIPTIONS = "changeMainID|activateSubscriber|unsubscribeCampaign|subscribeCampaign|thresholdTime|thresholdVolume|createNewSubscription|cancelSubscription|suspendSubscription|reactivateSubscription|reSubscription|changeSubscriberDetails|getSubscriptions|registerElegido|registerMultiplesElegido|unregisterElegido|modifyElegido|enquireElegido|renewalElegido|registerMultipleElegidoWithValidity|modifyMultiplesElegido|deleteElegidos";
    final String COMPARE_FIELDS_SUBSCRIBER = "IMSI|subscriberType|deviceType";
    final String INACTIVE_STATUS = CANCELLED.concat(PIPE).concat(NO_PLAN).concat(PIPE).concat(TIME_EXPIRED).concat(PIPE).concat(VOLUME_EXPIRED).concat(PIPE).concat(SUSPENDED);
    
    private static final Pattern REQUEST_PARAMETER_PATTERN = Pattern.compile("([0-9]+)\\.(.+)");
    private static final Pattern SUBSCRIBER_PARAMETER_PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)\\.(.+)");
    private static final Pattern FIELD_REQUEST = Pattern.compile("([0-9]+)(\\.([0-9]+))*\\.(.+)");

    private static final String FORMAT_DATE_TIME_WITH_TZ = "yyyyMMddHHmmssZ";
    private static final String MODEM = "modem";
    private static final String HANDSET = "handset";
    private static final String PREPAID = "prepaid";
    private static final String POSTPAID = "postpaid";
    private static final String CONTROLLED = "controlled";
	

  
	
	
    
    

    //-----------------------------------------------------------------
    // DSMN Error Codes
    //-----------------------------------------------------------------
    
    final String INVALID_REQUEST_ID                             = "10000";
    final String INVALID_OPERATION                              = "10001";
    final String INVALID_SUBSCRIBER_ID                          = "10002";
    final String VALIDATE_REQUEST_PARAMETER                     = "10003";
    // General error in the request. Verify the valid WSDL syntax. 10004
    // Invalid parameter $value. Valid parameters for this operation are: $value1, $value2, .... 10005
    final String BILLING_CYCLE_DATE_IS_NOT_PRESENT              = "10006";
    final String PRORATA_TYPE_IS_NOT_PRESENT                    = "10007";
    
    final String PRODUCT_NAME_NOT_EXIST                         = "11000";
    final String NOT_FOUND_PRODUCT_MASTER_SUBSCRIBER_MAINID     = "11003";
    final String SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS               = "11005";
    final String SUBSCRIBER_HAS_PRODUCT_WITH_THIS_PRIORITY      = "11006"; 
    final String INDEBT_SUBSCRIBER                              = "21009";
    final String MASTER_INDEBT_SUBSCRIBER                       = "21010";
    final String UNREGISTERED_SUBSCRIBER                        = "21011";
    final String MASTER_UNREGISTERED_SUBSCRIBER                 = "21012";
    final String PRODUCT_RESTRICT_BY_SUBSRIBER_TYPE             = "11014";
    final String PRODUCT_RESTRICT_BY_DEVICE_TYPE                = "11015";
    final String PRODUCT_RESTRICT_TO_PROGRAMMED                 = "11016";
    final String THE_PRODUCT_IS_NOT_LIMITED_BY_VOLUME           = "11017";
    final String LEAST_MASTER_AND_DEPENDANT                     = "11018";
    final String GET_ACTUAL_TIMESTAMP                           = "11019";
    final String ANIDADOS_VALIDATE_RECURRENT                    = "11034";
    final String ANIDADOS_VALIDATE_INSTALL                        = "11035";
    final String ANIDADOS_VALIDATE_MAX_USES                        = "11036";
    final String ANIDADOS_VALIDATE_ELEGIDO                        = "11037";
    final String ANIDADOS_VALIDATE_ENQUEUE                        = "11038";

    //-----------------------------------------------------------------
    // DSMN Error Reasons
    //-----------------------------------------------------------------
    
    final String REMOVE_ALL_DATA_SOURCE                         = "21001";
    final String COUNT_TRANSACTIONS                             = "21002";
    final String FORMAT_ERRO                                    = "21003";
    final String NOT_EXIST_VARIABLE                             = "21004";
    final String SET_CONFIG_VARIABLES                           = "21005";
    final String MOUNT_BACKUP_MAP                               = "21006";
    final String COMPLETED_REVIEW_REQUEST                       = "21007";
    final String BUILD_QUERY_LIST                               = "21008";
    final String GET_ELEMENT_QUERY                              = "21009";
    final String FORMAT_REQUEST_QUERY                           = "21010";
    final String FORMAT_DATASOURCE_FOR_BACKENDCONTROLLER        = "21011";
    final String FORMAT_RESPONSE_ERROR                          = "21012";
    final String GET_ALL_COMMERCIAL_PRODUCTS                    = "21013";
    final String GET_ALL_PRODUCTS                               = "21014";
    final String DATA_NOT_MATCHED                               = "21015";
    final String GET_DATA_CONTAINER_ERROR                       = "21016";
    final String CREATE_NEW_SUBSCRIPTION                        = "21017";
    final String GET_LATEST_PRODUCT                             = "21018"; 
    final String SET_DATA_MAP_VARIABLES                         = "21019";
    final String CARE_RETURN_OF_QUERY                           = "21020";
    final String GET_PRODUCT_PARAMETER                          = "21021";
    final String PRODUCT_NOT_FOUND                              = "21022";
    final String GET_PROMOTION_BY_PRODUCT_ID                    = "21023";
    final String PROMOTION_ID_NOT_FOUND                         = "21024";
    final String FIRST_IS_GREAT_THAN_OR_EQUAL_SECOND            = "21025";
    final String COMPARE_SUBSCRIBER_DATA                        = "21026";
    final String SUBSCRIBER_IS_DIFFERENT_WITH_DATABASE          = "21027";
    final String GET_PRODUCT_PRIORITY                           = "21028";
    final String GET_PRODUCT_BY_ID                              = "21029";
    final String IS_THERE_PRODUCT_WITH_PRIORITY                 = "21030";
    final String VALIDATION_ALL_REQUEST_PARAMETER               = "21031";
    final String GET_FIELD_COMPARE                              = "21032";
    final String IS_THERE_PRODUCT_WITH_LESS_PRIORITY            = "21033";
    final String GET_DATA_PRODUCT_VS_THRESHOLD                  = "21034";
    final String RESTRUCT_REQUEST_WITH_MASTER                   = "21035";
    final String GET_PRODUCT_WITH_THIS_NAME                     = "21036";
    final String APPLY_PRORATA_IN_BILLING_CYCLE                 = "21037";
    final String PRODUCT_MAINID_NOT_FOUND                       = "21038";
    final String CANCEL_SUBSCRIPTION                            = "21039";
    final String SUBSCRIBER_NOT_FOUND                           = "21040";
    final String THRESHOLD_CREATE_NEW_SUBSCRIPTION              = "21043";
    final String THRESHOLD_NOT_FOUND                            = "21044";
    final String CHANGE_SUBSCRIBER_DETAILS                      = "21045";
    final String FORMAT_DATA_CHANGE_SUBSCRIBER_DETAILS          = "21046";
    final String FIELD_NOT_PRESENT                              = "21047";   
    final String FIELD_EXIST_MAPVARIABLES_AND_NOT_NULL          = "21048";
    final String REQUEST_FIELD_IS_EMPTY                         = "21049";
    final String GET_THRESHOLDS_BY_PRODUCTID                    = "21050";
    final String GET_ONLYONE_CONTAINER_DATA                     = "21051";
    final String ENTITY_NOT_FOUND                               = "21052";
    final String FORMAT_CREATE_NEW_SUBSCRIPTION_DATA            = "21053";
    final String GET_END_BILLING_CYCLE                          = "21054";
    final String GET_DAY_BETWEEN                                = "21055";
    final String ADD_HOUR_IN_DATE_TIME                          = "21056";
    final String THRESHOLDS_NOT_EXIST                           = "21057";
    final String CHANGE_PROPERTIES                              = "21058";
    final String GET_PROPERTY_AS_STRING                         = "21059";
    final String SUSPEND_SUBSCRIPTION                           = "21060";
    final String GET_COPY_HASH_MAP                              = "21061";
    final String IS_THERE_PRODUCT_WITH_GREAT_PRIORITY           = "21062";
    final String CANCEL_TYPE_INVALID                            = "21063";
    final String GET_ACCUMULATOR                                = "21064";
    final String PRODUCT_HAS_NOT_INITIAL_THRESHOLD              = "21065";
    final String THRESHOLD_VOLUME                               = "21066";
    final String GET_PRODUCT_PRICE_WITH_TAXES_AND_PROMOTION     = "21067";
    final String REACTIVATE_SUBSCRIPTION                        = "21068";
    final String GET_ALL_ACCUMULATOR                            = "21069";
    final String IS_THERE_CONFLICT_RULES_RECURRENCE             = "21070";
    final String CALCULATE_FINAL_PRICE                          = "21071";
    final String FIND_MAINID_RANGE                              = "21072";
    final String THRESHOLD_TIME                                 = "21073";
    final String RE_SUBSCRIPTION                                = "21074";
    final String NOT_AIR_VOLUME                                 = "21075";
    final String GET_SUBSCRIPTIONS                              = "21076";
    final String GET_AIR_VOLUME_LIST                            = "21077";
    final String INVALID_PRORATA_TYPE_VOLUME                    = "21078";
    final String INVALID_PRORATA_TYPE_PRICE                     = "21079";
    final String GET_POLICY_BY_STATUS                           = "21080";
    final String PREPARE_MAINID                                 = "21081";
    final String PROCESS_SP_THIRD_DIGITS                        = "21082";
    final String REMOVE_NINTH_DIGIT                             = "21083";
    final String PROCESS_TWELVE_DIGITS                          = "21084";
    final String IS_TWELVE_DIGITS                               = "21085";
    final String IS_SP_THIRD_DIGITS                             = "21086";
    final String IS_FIRST_PERIOD                                = "21087";
    final String IS_THIRD_PERIOD                                = "21088";
    final String FIND_ANATEL_MAINID_RANGE                       = "21089";
    final String GET_NINTH_DIGIT                                = "21090";
    final String PARSE_STRING_TO_DATE                           = "21091";
    final String GET_DB_TIME_ZONE                               = "21092";
    final String PARSE_CAL_STR_DB_TZ                            = "21093";  
    final String GET_STATUS_BY_NAME                             = "21094";
    final String LOAD_STATUS                                    = "21095";
    final String GET_DEVICE_TYPE                                = "21096";
    final String GET_SUBSCRIBER_TYPE                            = "21097";
    final String DEPENDENT_CAN_NOT_BE_RECURRENCY                = "21098";
    final String CALCULATE_FINAL_TIME                           = "21099";
    final String CALCULATE_FINAL_VOLUME                         = "21100";
    final String NEXT_BILLING_CYCLE_DATE                        = "21101";
    final String GET_TIME_BETWEEN                               = "21102";
    final String MAINID_RANGE_NOT_FOUND                         = "21103";
    final String INVALID_MAINID_TO_FIND_MAINID_RANGE            = "21104";
    final String SET_ALL_ACCUMULATOR                            = "21105";
    final String IS_PRESENT_PROPERTY                            = "21106";
    final String BITAND_PROPERTY                                = "21107";
    final String REMOVE_ELEMENT_SUBSCRIPTIONS                   = "21108";
    final String STATUS_IS_NOT_VALID                            = "21109";
    final String SET_ALL_DEDICATED_ACCOUNT                      = "21110";
    final String ADD_MASTER_PRICE                               = "21111";
    final String SET_SAPC_GROUP_ID                              = "21112";
    final String REGISTER_ELEGIDOS                              = "21113";
    final String GET_VALUE_FROM_EMPTY_MAP                       = "21114";
    final String GET_VALUE_FROM                                 = "21116";
    final String GET_VALUE_FROM_LINE_NOT_FOUND                  = "21117";
    final String UNREGISTER_ELEGIDOS                            = "21118";
    final String GET_EXPERY_DATE                                = "21119";
    final String GET_FINAL_PRICE                                = "21120";
    final String MODIFY_ELEGIDOS                                = "21121";
    final String HAS_MANY_SUBSCRIBERS                           = "21122";
    final String ENQUIRE_ELEGIDO                                = "21123";
    final String RENEWAL_ELEGIDO                                = "21124";
    final String THERE_IS_NOT_SUBSCRIBER                        = "21125";
    final String THERE_IS_NOT_SUBSCRIPTION                      = "21126";
    final String SET_FINAL_PRICE                                = "21127";
    final String REGISTER_MUTIPLES_ELEGIDOS                     = "21128";
    final String REGISTER_MUTIPLES_ELEGIDOS_DATE                = "21129";
    final String REPLACE_VALIDITY_DATE                          = "21130";
    final String GET_VALIDITY_DATE                              = "21131";
    final String ELEGIDOS_TO_REGISTER                           = "21132";
    final String EXTRACT_DATA_SEARCH                            = "21133";
    final String MAINID_NOT_PRESENT_IN_MAINID_RANGE             = "21134";
    final String UNSUBSCRIBE_CAMPAIGN                           = "21135";
    final String MAINID_HAS_CAMPAIGN                            = "21136";
    final String SUBSCRIBE_CAMPAIGN                             = "21137";
    final String GET_START_NEXT_PERIOD_CAMPAING                 = "21138";
    final String MAINID_HAS_NOT_CAMPAIGN                        = "21139";
    final String MAINID_IS_NOT_DATABASE                         = "21140";
    final String GET_BOUBLE_DADOS                               = "21141";
    final String GET_VALUE_FIELD                                = "21142";
    final String ACTIVATE_SUBSCRIBER                            = "21143";
    final String SUBSCRIBER_TO_ACTIVATE                         = "21144";
    final String PREPARE_DATA_FOR_ACTIVATION                    = "21145";
    final String PREPARE_REQUEST_FOR_ACTIVATION                 = "21146";
    final String CHANGE_MAINID                                  = "21147";
    final String DELETE_ELEGIDO                                 = "21148";
    final String CLEAN_MAP_VARIABLES                            = "21149";
    final String NO_SUBSCRIBER_TO_ACTIVATE                      = "21150";    
    final String GET_ALL_ANIDADOS_PRODUCTS                      = "21151";
    final String IS_ANIDADOS_PRODUCT                            = "21152";
    final String GET_PRE_ACTIVATE_SUBSCRIBER                    = "21153";
    final String INSTALLED_PROPERTY                             = "21154";
    final String SET_DEDICATED_ACCOUNT_AND_OFFERS_TO_AIR        = "21155";
    final String PREPARE_DATA_TO_SPECIAL_ELEGIDO                = "21156";
	final String VARIABLES_VALIDATION 							= "21157";
	final String FIND_BUSSINESS_RULES 							= "21158";
	final String HAS_SPECIAL_ELEGIDO_FLAG 						= "21159";
    final String GET_LIFE_TIME_CONTAINER						= "21160";
	final String SET_LIFE_TIME_CONTAINER 						= "21161";
	final String SET_DATA_SOURCE_ON_CONTAINER                   = "21162";
    //Errors for Comcel;
    final String SET_OFFER                                      = "21200";

    final String ERROR_SOURCE_MODULE = "dth";

    
    public void initialize()
    {
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
        if (!(dataSource.parameterExists("ORIGIN")))   // Requisition coming from web service
         {
            String sessionID = UUID.randomUUID().toString();
            try
            {
            	
                //-----------------------------------------------------------------------------
                // Inclui na memoria o ID da sessao e o backup do dataSource
                //-----------------------------------------------------------------------------
                mapVariables.put(sessionID, mountBackupMap(dataSource));
                
                //-----------------------------------------------------------------------------
                // removendo os dados do datasource
                //-----------------------------------------------------------------------------
                
                removeAllDataSource(dataSource);
                
                //-----------------------------------------------------------------------------
                // Buscando as variaveis de configuracao
                //-----------------------------------------------------------------------------
                
                setConfigVariables(sessionID);
                
                //-----------------------------------------------------------------------------
                // Build query list of subscriptions and subscribers
                //-----------------------------------------------------------------------------
                
                buildQueryList(sessionID);
                
                //-----------------------------------------------------------------------------
                // Get element query 
                //-----------------------------------------------------------------------------

                String[] element = getElementQuery(sessionID);
                
                if (element != null)
                {
                    formatRequestQuery(sessionID,dataSource,element);
                    return;
                }
                else
                {
                    formatDataSourceForBackEndController(sessionID,dataSource);
                    
                    if(mapVariables.containsKey(sessionID))
                    {
                        mapVariables.remove(sessionID);
                    }
                }

            }
            catch (Exception error)
            {
                String errorCode        = FORMAT_ERRO;
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
                
                removeAllDataSource(dataSource);
                Map<String,Object> errorResponse = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                errorResponse.put("ERROR_RESPONSE", prepareErrorResponse(errorCode, errorDescription,errorSource,errorType));
                Map<String,Object> sessionIdMap = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                if(mapVariables.containsKey(sessionID))
                {
                    mapVariables.remove(sessionID);
                }
                
                sessionIdMap.put(sessionID, errorResponse);
                dataSource.setParameter("MAP_VARIABLES", sessionIdMap         );
                dataSource.setParameter("SESSION_ID"   , sessionID            );
                dataSource.setParameter("STATUS"       , "OK"                 );
                dataSource.setParameter("ORIGIN"       , "SearchAndValidation");
                dataSource.setParameter("TARGET"       , "ConnectedSearchAndValidation"  );
                
                
            }
         }
         else // Requisition coming from DsmnQuery
         {
             String sessionID = dataSource.getValueAsString("SESSION_ID");
             
             try 
             {
                 careReturnOfQuery(dataSource,sessionID);
                 
                 String[] element = getElementQuery(sessionID);
                
                 if (element != null)
                 {
                      formatRequestQuery(sessionID,dataSource,element);
                      return;
                 }
                 else
                 {
                     formatDataSourceForBackEndController(sessionID,dataSource);
                     
                     if(mapVariables.containsKey(sessionID))
                     {
                        mapVariables.remove(sessionID);
                     }
                 }
             } 
             catch (Exception error) 
             {
                 String errorCode        = FORMAT_ERRO;
                 String errorType        = INTERNAL;
                 String errorSource      = ERROR_SOURCE_MODULE;
                 String errorDescription = formatErroDescription(error);
                   
                 if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
                 {
                     String[] errors  = error.getMessage().split("\\<\\&\\>");
                     errorCode        = errors[1];
                     errorType        = errors[2];
                     errorSource      = errors[3];
                     errorDescription = errors[4];
                 }            
                    
                 removeAllDataSource(dataSource);
                 Map<String,Object> errorResponse = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                 errorResponse.put("ERROR_RESPONSE", prepareErrorResponse(errorCode, errorDescription,errorSource,errorType));
                 Map<String,Object> sessionIdMap = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                 sessionIdMap.put(sessionID, errorResponse);
                 
                 if(mapVariables.containsKey(sessionID))
                 {
                    mapVariables.remove(sessionID);
                 }
                 
                 dataSource.setParameter("MAP_VARIABLES", sessionIdMap         );
                 dataSource.setParameter("STATUS"       , "OK"                 );
                 dataSource.setParameter("ORIGIN"       , "SearchAndValidation");
                 dataSource.setParameter("TARGET"       , "ConnectedSearchAndValidation"  );
                 dataSource.setParameter("SESSION_ID"   , sessionID  );
             }
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
    
    @SuppressWarnings("unchecked")
    private void careReturnOfQuery(DataObject dataSource, String sessionID) throws Exception
    {
        try 
        {
            Map<String,Object> session  = (Map<String,Object>)mapVariables.get(sessionID);
            String request = dataSource.getValueAsString("REQUEST_NUMBER");
            String subscriber = dataSource.getValueAsString("SUBSCRIBER_NUMBER");
            String fieldName = dataSource.getValueAsString("FIELD_NAME");
            String statusSeach = dataSource.getValueAsString("STATUS");
            
            if(statusSeach.equals("ERROR"))
            {
                throw createException(CARE_RETURN_OF_QUERY,INTERNAL,ERROR_SOURCE_MODULE,dataSource.getValueAsString("errorDescription"));
            }
            
            if (dataSource.parameterExists("SUBSCRIBER_DATA"))
            {
                if(!(((Map<String,Object>)dataSource.getValue("SUBSCRIBER_DATA")).isEmpty()))
                {
                    setDataMapVariables("TABLE."+request+DOT+subscriber+DOT+fieldName+".SUBSCRIBER", (Map<String,Object>)dataSource.getValue("SUBSCRIBER_DATA"), session);
                }
            }
            
            if (dataSource.parameterExists("SUBSCRIPTIONS_DATA"))
            {                               
                if(!(((Map<String,Object>)dataSource.getValue("SUBSCRIPTIONS_DATA")).isEmpty()))
                {
                    setDataMapVariables("TABLE."+request+DOT+subscriber+DOT+fieldName+".SUBSCRIPTIONS", (Map<String,Object>)dataSource.getValue("SUBSCRIPTIONS_DATA"), session);
                }
            }
            
        } 
        catch (Exception e) 
        {
            throw createException(CARE_RETURN_OF_QUERY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
      
    @SuppressWarnings("unchecked")
    private void formatDataSourceForBackEndController(String sessionID,DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String,Object> session  = (Map<String,Object>)mapVariables.get(sessionID);
            int numberRequest = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST",session));
            
            for (int i=0; i < numberRequest; i++)
            {
                String operation = (String)getDataMapVariables("REQUEST."+i+".operation",session);
                
                setDataMapVariables("ACTION."+i+".TYPE", getType(operation), session);
                
                 if (operation.equals("cleanMapVariables"))
                {
                    cleanMapVariables(sessionID,String.valueOf(i), dataSource);
                }
                else if (operation.equals("createNewSubscription"))
                {
                    createNewSubscription(sessionID,String.valueOf(i));
                }
                else if (operation.equals("cancelSubscription"))
                {
                    cancelSubscription(sessionID,String.valueOf(i));
                }
                else if (operation.equals("suspendSubscription"))
                {
                    suspendSubscription(sessionID,String.valueOf(i));
                }
                else if (operation.equals("reactivateSubscription"))
                {
                    reactivateSubscription(sessionID,String.valueOf(i));
                }
                else if (operation.equals("reSubscription"))
                {
                    reSubscription(sessionID,String.valueOf(i));
                }
                else if (operation.equals("getAllCommercialProducts"))
                {
                    getAllCommercialProducts(sessionID,String.valueOf(i));
                }
                else if (operation.equals("getSubscriptions"))
                {
                    getSubscriptions(sessionID,String.valueOf(i));
                }
                else if (operation.equals("changeSubscriberDetails"))
                {
                    changeSubscriberDetails(sessionID,String.valueOf(i));
                }
                else if (operation.equals("thresholdVolume"))
                {
                    thresholdVolume(sessionID,String.valueOf(i));
                }
                else if (operation.equals("thresholdTime"))
                {
                    thresholdTime(sessionID,String.valueOf(i));
                }
                else if (operation.equals("registerElegido"))
                {
                    registerElegido(sessionID,String.valueOf(i));
                }
                else if (operation.equals("registerMultiplesElegido"))
                {
                    registerMultiplesElegido(sessionID,String.valueOf(i));
                }
                else if (operation.equals("unregisterElegido"))
                {
                    unregisterElegido(sessionID,String.valueOf(i));
                }                       
                else if (operation.equals("modifyElegido"))
                {
                    modifyElegido(sessionID,String.valueOf(i));
                }
                else if (operation.equals("enquireElegido"))
                {
                    enquireElegido(sessionID,String.valueOf(i));
                }
                else if (operation.equals("renewalElegido"))
                {
                    renewalElegido(sessionID,String.valueOf(i));
                }                   
                else if (operation.equals("registerMultipleElegidoWithValidity"))
                {
                    registerMultipleElegidoWithValidity(sessionID,String.valueOf(i));
                }    
                else if (operation.equals("modifyMultiplesElegido"))
                {
                    modifyMultiplesElegido(sessionID,String.valueOf(i));
                }
                else if (operation.equals("subscribeCampaign"))
                {
                    subscribeCampaign(sessionID,String.valueOf(i));
                }
                else if (operation.equals("unsubscribeCampaign"))
                {
                    unSubscribeCampaign(sessionID,String.valueOf(i));
                }
                else if (operation.equals("activateSubscriber"))
                {
                    activateSubscriber(sessionID,String.valueOf(i));
                }
                else if (operation.equals("changeMainID"))
                {
                    changeMainID(sessionID,String.valueOf(i));
                }
                else if (operation.equals("deleteElegidos"))
                {
                    deleteElegidos(sessionID,String.valueOf(i));
                }
                else
                {
                    formatErrorResponse(String.valueOf(i), session, INVALID_OPERATION, "malformed", ERROR_SOURCE_MODULE, "invalid Operation "+operation+DOT);
                }
            }
            removeAllDataSource(dataSource);
            Map<String,Object> sessionIdMap = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            sessionIdMap.put(sessionID, session);

            dataSource.setParameter("MAP_VARIABLES", sessionIdMap         );
            dataSource.setParameter("SESSION_ID"   , sessionID            );
            dataSource.setParameter("STATUS"       , "OK"                 );
            dataSource.setParameter("ORIGIN"       , "SearchAndValidation");
            dataSource.setParameter("TARGET"       , "ConnectedSearchAndValidation"  );
            
            mapVariables.remove(sessionID);
            
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_DATASOURCE_FOR_BACKENDCONTROLLER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }

    }


    @SuppressWarnings("unchecked")
    private void activateSubscriber(String sessionID, String requestID) throws Exception {
        try {
            Map<String, Object> session = (Map<String,Object>) mapVariables.get(sessionID);
          
            Map<String,Object> requestTable = ((Map<String,Object>)((Map<String,Object>)session.get("TABLE")).get(requestID));             
       
            String currentTable = "TABLE.".concat(requestID).concat(DOT).concat(String.valueOf(ZERO)).concat(DOT);
            
            thereIsSubscriber(session, currentTable);
            thereIsSubscription(session, currentTable);
            
            String mainID = getDataMapVariables("REQUEST.".concat(requestID).concat(DOT).concat(String.valueOf(ZERO)).concat(".MAINID"),session);                  
            setDataMapVariables("REQUEST.".concat(String.valueOf(ZERO)).concat(".operation"),"activateSubscriber",session);

            Map<String,Object> mainIDTable = (Map<String, Object>) ((Map<String,Object>)requestTable.get(String.valueOf(ZERO))).get("MAINID");

            Map<String,Object> subscriptionTable =  getPreActivateSubscriptions((Map<String, Object>) mainIDTable.get("SUBSCRIPTIONS"));

            int numberSubscriptions = subscriptionTable.keySet().size();
            
            if(numberSubscriptions==0)
            {
             throw createException(NO_SUBSCRIBER_TO_ACTIVATE,INTERNAL,ERROR_SOURCE_MODULE, "There is no subscriber to activate.");
            }               
            
            setDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+requestID+".NUMBER_SUBSCRIBER", String.valueOf(numberSubscriptions), session);

            for(int currentSubscription = 0; currentSubscription < numberSubscriptions; currentSubscription++){
                
                prepareRequestForActivation(requestID, session, mainID,mainIDTable, subscriptionTable, currentSubscription);
                
                prepareDataForActivation(session, requestID, String.valueOf(currentSubscription));
                                        
            }

            addSubscriberToActivateOnFirstSubscriber(session,requestID);
        } catch (Exception error) {
            throw createException(ACTIVATE_SUBSCRIBER,INTERNAL,ERROR_SOURCE_MODULE, formatErroDescription(error));
        }            
        
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPreActivateSubscriptions(Map<String, Object> subscriptions) throws Exception        {
     try
     {
      Map<String,Object> subscriptionTable = new java.util.concurrent.ConcurrentHashMap<String, Object>();
      int i=0;
      for(String idx : (Set<String>)subscriptions.keySet())
      {
       String status = (String) ((Map<String, Object>)subscriptions.get(idx)).get("STATUS_NAME");
       String endTimestamp = (String) ((Map<String, Object>)subscriptions.get(idx)).get("END_TIMESTAMP");
       if(status.equals("FULL") && (endTimestamp==null || endTimestamp.isEmpty()))
       {
        subscriptionTable.put(String.valueOf(i),(Map<String, Object>)subscriptions.get(idx));
        i++;
       }
      }
      
      return subscriptionTable;
        } catch (Exception error) {
            throw createException(GET_PRE_ACTIVATE_SUBSCRIBER,INTERNAL,ERROR_SOURCE_MODULE, formatErroDescription(error));
        }            
     
    }
    private void prepareRequestForActivation(String requestID,Map<String, Object> session, String mainID, Map<String, Object> mainIDTable, Map<String, Object> subscriptionTable, int currentSubscription) throws Exception {
        try{        

            setDataMapVariables("TABLE.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription).concat(".MAINID")), mainIDTable, session);
            setDataMapVariables("REQUEST.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription).concat(".MAINID")),mainID, session);
            
            @SuppressWarnings("unchecked")
            Map<String,Object> currentSubscriptionTable = (Map<String, Object>) subscriptionTable.get(String.valueOf(currentSubscription));
            setDataMapVariables("TABLE.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription)).concat(".SUBSCRIPTION"),currentSubscriptionTable,session);
            
            String productID = (String) currentSubscriptionTable.get("PRODUCT_ID");
            Map<String,Object> product = getContainerData("PRODUCTS_BY_ID", productID, "PRODUCTS_BY_ID", true);
            
            setDataMapVariables("REQUEST.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription)).concat(".PRODUCT_NAME"), product.get("productName"), session);
            setDataMapVariables("REQUEST.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription).concat(".originatingNodeInfo")),"SDP",session);
        
        }catch(Exception e){
            throw createException(PREPARE_REQUEST_FOR_ACTIVATION,INTERNAL,ERROR_SOURCE_MODULE, formatErroDescription(e));                
        }
    }

    private void prepareDataForActivation(Map<String, Object> session, String requestID, String subscriberID) throws Exception {
        try{
            String currentTable = "TABLE.".concat(requestID).concat(DOT).concat(subscriberID).concat(DOT);

            
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            setDataMapVariables("INTERNAL.".concat(requestID).concat(DOT).concat(subscriberID).concat(DOT).concat("DATE_TIME_EXECUTION"),dateTimeExecution, session);
            
            String currentRequest = "REQUEST.".concat(requestID).concat(DOT).concat(subscriberID).concat(DOT);
            String productName = getDataMapVariables(currentRequest.concat("PRODUCT_NAME"),session);
            String mainID = getDataMapVariables(currentRequest.concat("MAINID"), session);
            
            findMainIDRange(mainID, Integer.parseInt(requestID), Integer.valueOf(subscriberID), session);
            String regionId = getDataMapVariables(currentTable.concat("MAINID_RANGES.REGION_ID"), session);
            
            Map<String,Object> product = getTheLatestVersionOfProductByName(productName,regionId);
            setDataMapVariables(currentTable.concat("PRODUCT"), product, session);
            
            setDataMapVariables(currentTable.concat("PRODUCT.FINAL_PRICE"), ZERO, session);
            
            modifyEndTimestamp(session, requestID, subscriberID);
            
            String executionTimestamp = getDataMapVariables(currentTable.concat("SUBSCRIPTION").concat(DOT).concat("END_TIMESTAMP"), session);
            setDataMapVariables(currentTable.concat("SUBSCRIPTION").concat(DOT).concat("EXECUTION_TIMESTAMP"), executionTimestamp, session); 
            
            String creationTimestamp = getActualTimestamp(session, requestID, subscriberID);
            setDataMapVariables(currentTable.concat("SUBSCRIPTION").concat(DOT).concat("CREATION_TIMESTAMP"), creationTimestamp, session);
            
            setDataMapVariables(currentTable.concat("DEDICATED_ACCOUNT"), getContainerData("DEDICATED_ACCOUNT", (String)product.get("productID"), "DEDICATED_ACCOUNT", false), session);
            setDataMapVariables(currentTable.concat("OFFER"), getContainerData("OFFER", (String)product.get("productID"), "OFFER", false), session);
            
        }catch(Exception e){
            throw createException(PREPARE_DATA_FOR_ACTIVATION,INTERNAL,ERROR_SOURCE_MODULE,e.getMessage());
        }
    }

    private void addSubscriberToActivateOnFirstSubscriber(Map<String, Object> session, String requestID) throws Exception {
            try{
                int subscribersCount = countSubscriber(session, requestID);
                String subscriberToActivateParam = "INTERNAL.".concat(requestID).concat(DOT).concat(ZERO).concat(DOT).concat("SUBSCRIBER_TO_ACTIVATE");
                setDataMapVariables(subscriberToActivateParam, String.valueOf(subscribersCount), session);
            } catch (Exception error){
                throw createException(SUBSCRIBER_TO_ACTIVATE,INTERNAL,ERROR_SOURCE_MODULE,
                        error.getMessage());
            }
    }

    private String getType(String operation) throws Exception
    {
        if(operation.matches("getSubscriptions|getAllCommercialProducts|enquireElegido"))
        {
            return "QUERY";
        }
        return "EXECUTION";
    }
    
    private void changeMainID(String sessionId, String requestId) throws Exception{
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            int numberSubscribers = countSubscriber(session, requestId);
            for(int subscriberId = 0; subscriberId < numberSubscribers; subscriberId++){
                String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(String.valueOf(subscriberId)).concat(DOT);

                thereIsSubscriber(session, currentTable);           
                //thereIsSubscription(session, currentTable);
                String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
                setDataMapVariables("INTERNAL.".concat(requestId).concat(DOT).concat(String.valueOf(subscriberId)).concat(DOT).concat("DATE_TIME_EXECUTION"),dateTimeExecution, session);
            }                
        } catch(Exception error) {
            throw createException(CHANGE_MAINID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }
    
    
    
    
    private void renewalElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);                
            
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            
            int numberSubscribers = countSubscriber(session, requestId);
            for(int subscriberId = 0; subscriberId < numberSubscribers; subscriberId++){
            	
                String id = String.valueOf(subscriberId);
                prepareDataToElegido(session, requestId, id, "REGISTER_ELEGIDO_PRICE");
                
                String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                changeItelOriginatingNodeInfo(requestId, subscriberId, originNode, session);
                originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                
                if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), subscriberId, originNode)){
                    prepareDataToSpecialElegido(session, requestId, id);
                }

                setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));

                modifyEndTimestamp(session, requestId, id);
                
                setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
            }
        } catch (Exception error) {
            throw createException(RENEWAL_ELEGIDO,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }
    
    private void changeItelOriginatingNodeInfo(String requestID, int currentSubscription, String origin, Map<String, Object> session) throws Exception{
    	if(origin.equals("ITEL")){
    		setDataMapVariables("REQUEST.".concat(requestID).concat(DOT).concat(String.valueOf(currentSubscription).concat(".originatingNodeInfo")),"WS",session);
    	}
    }    

    private void modifyEndTimestamp(Map<String, Object> session,
            String requestId, String id) throws Exception {
        DbTimeZone zone = new DbTimeZone();
        Calendar now = Calendar.getInstance(zone.getTimeZone());
        
        String currentTable = "TABLE.".concat(requestId).concat(DOT);
        int hours = Integer.valueOf((String) getDataMapVariables(currentTable.concat(id).concat(DOT).concat("PRODUCT.productTime"), session));
        
        now.add(Calendar.HOUR, hours);
        replaceValidityDate(session, requestId, id, parseCalendarToDefaultStringWithDbTimeZone(now));
    }
    
    private String getActualTimestamp(Map<String,Object> session, String requestId, String subscriberId) throws Exception{
        try{
            DbTimeZone zone = new DbTimeZone();
            Calendar now = Calendar.getInstance(zone.getTimeZone());
            
            return parseCalendarToDefaultStringWithDbTimeZone(now);
            
        }catch(Exception e){
            throw createException(GET_ACTUAL_TIMESTAMP,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));                
        }
    }
    
    private void enquireElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            Map<String,Object> dataSearch = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            int numberSubscribers = countSubscriber(session, requestId);
            for(int subscriberId = 0; subscriberId < numberSubscribers; subscriberId++){
                prepareDataToElegido(session, requestId, String.valueOf(subscriberId), "REGISTER_ELEGIDO_PRICE");
                
                String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                changeItelOriginatingNodeInfo(requestId, subscriberId, originNode, session);
                originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                
                if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), 0, originNode)){
                    prepareDataToSpecialElegido(session, requestId, String.valueOf(subscriberId));
                }
                
                dataSearch.put(String.valueOf(subscriberId), extractDataSearch(session,requestId,String.valueOf(subscriberId)));
            }                
            setDataMapVariables("ACTION."+requestId+".DATA_SEARCH.DATABASE", dataSearch, session);
        } catch (Exception error) {
            throw createException(ENQUIRE_ELEGIDO,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> extractDataSearch(Map<String,Object> session, String requestId, String subscriberId) throws Exception{
        try {
            String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
            Map<String,Object> subscriber = (Map<String,Object>) getDataMapVariables(currentTable.concat("MAINID.SUBSCRIBER"), session);
            Map<String,Object> subscription = (Map<String,Object>) getDataMapVariables(currentTable.concat("SUBSCRIPTION"), session);
            String regionId = getDataMapVariables(currentTable.concat("MAINID_RANGES.REGION_ID"), session);
            
            String currentRequest = "REQUEST.".concat(requestId).concat(DOT);
            String productName = getDataMapVariables(currentRequest.concat("productName"),session);
            Map<String,Object> product = getTheLatestVersionOfProductByName(productName,regionId);
            
            Map<String,Object> params = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            params.put("subscriberIMSI", subscriber.get("IMSI"));
            params.put("subscriptionID", subscription.get("SUBSCRIPTION_ID"));
            params.put("productID", product.get("productID"));
            params.put("productName", product.get("productName"));
            params.put("productVersion", product.get("productVersion"));
            params.put("productRegion", product.get("productRegion"));
            params.put("productDescription", product.get("productDescription"));
            params.put("productPrice", product.get("productPrice"));
            
            return params;
        } catch (Exception error) {
            throw createException(EXTRACT_DATA_SEARCH,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private void registerElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            hasOnlyOneSubscriber(session, requestId);
            prepareDataToElegido(session, requestId, ZERO, "REGISTER_ELEGIDO_PRICE");
            
            String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            changeItelOriginatingNodeInfo(requestId, 0, originNode, session);
            originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            
            if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), 0, originNode)){
                prepareDataToSpecialElegido(session, requestId, ZERO);
            }
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
            addElegidosToRegisterOnFirstSubscriber(session, requestId);
        } catch (Exception error) {
            throw createException(REGISTER_ELEGIDOS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }
    
    private void registerMultiplesElegido(String sessionId, String requestId) throws Exception{
        try{
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            int subscribersCount = countSubscriber(session, requestId);
            
            prepareDataToElegido(session, requestId, ZERO, "REGISTER_ELEGIDO_PRICE");
            
            String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            changeItelOriginatingNodeInfo(requestId, 0, originNode, session);
            originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            
            if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), 0, originNode)){
                prepareDataToSpecialElegido(session, requestId, ZERO);
            }
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
            addElegidosToRegisterOnFirstSubscriber(session, requestId);   

            String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(ZERO).concat(DOT);
            
            List<String> fafNumberList = new ArrayList<String>();
            setDataMapVariables(currentTable.concat("FAF_NUMBERS_TO_REGISTER"), fafNumberList, session);
            
            for(int i = 0; i < subscribersCount; i++) {
                fafNumberList.add(((String)getDataMapVariables("REQUEST."+requestId+"."+i+".MAINIDElegidoToRegister", session)));
            }
            setDataMapVariables("CONTROLLER.COUNT_TRANSACTION.".concat(requestId).concat(".NUMBER_SUBSCRIBER"), "1", session);
            
        }catch(Exception error){
            throw createException(REGISTER_MUTIPLES_ELEGIDOS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }        

    private void registerMultipleElegidoWithValidity(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            int subscribersCount = countSubscriber(session, requestId);

            for(int aId = 0; aId < subscribersCount; aId++) {
                String subscriberId = String.valueOf(aId);
                prepareDataToElegido(session, requestId, subscriberId, "REGISTER_ELEGIDO_PRICE");
                
                String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                changeItelOriginatingNodeInfo(requestId, aId, originNode, session);
                originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                
                if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), aId, originNode)){
                    prepareDataToSpecialElegido(session, requestId, subscriberId);
                }
                
                setValidityDate(session, requestId, subscriberId);
            }
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
            addElegidosToRegisterOnFirstSubscriber(session, requestId);
        } catch (Exception error) {
            throw createException(REGISTER_MUTIPLES_ELEGIDOS_DATE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private String getValidityDate(Map<String, Object> session,
            String requestId, String subscriberId) throws Exception {
        try{
            String requestValidityDate = "REQUEST.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT).concat("validityDate");
            String validityDate = getDataMapVariables(requestValidityDate, session);
            return validityDate;                
        } catch (Exception error) {
            throw createException(GET_VALIDITY_DATE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }

    }
    
    private void replaceValidityDate(Map<String,Object> session, String requestId,
            String subscriberId, String validityDate) throws Exception {
        try {
            String subscriptionEndTimestamp = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT).concat("SUBSCRIPTION.END_TIMESTAMP");
            setDataMapVariables(subscriptionEndTimestamp, validityDate, session);
        } catch (Exception error) {
            throw createException(REPLACE_VALIDITY_DATE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private boolean hasValidityDate(Map<String, Object> session,
            String subscriberId, String requestId) throws Exception {
        String request = "REQUEST.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
        return fieldExistMapVariables(request.concat("validityDate"), session);
    }
    
    private void unregisterElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            hasOnlyOneSubscriber(session, requestId);
            prepareDataToElegido(session, requestId, ZERO, "UNREGISTER_ELEGIDO_PRICE");
            
            String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            changeItelOriginatingNodeInfo(requestId, 0, originNode, session);
            originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);            
            
            if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), 0, originNode)){
                prepareDataToSpecialElegido(session, requestId, ZERO);
            }
            
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
        } catch (Exception error) {
            throw createException(UNREGISTER_ELEGIDOS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }
    
    private void deleteElegidos(String sessionId, String requestId) throws Exception
    {
        try
        {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            int subscribersCount = countSubscriber(session, requestId);
            for(int id = 0; id < subscribersCount; id++) {
                
                String subscriberId = String.valueOf(id);                    
              //  String productName = "REQUEST.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT).concat("productName");
                String productName = getDataMapVariables("REQUEST.".concat(requestId).concat(DOT).concat("productName"),session);
                
                if (isForTheAllElegidos(productName))
                {
                    prepareDataToAllElegidos(session, requestId, subscriberId, "UNREGISTER_ELEGIDO_PRICE");
                }else{
                    prepareDataToElegido(session, requestId, subscriberId, "UNREGISTER_ELEGIDO_PRICE");
                }
                setFinalPrice(session, requestId, subscriberId, getElegidoValue(session, requestId, subscriberId));
            }
        }
        catch (Exception error)
        {
            throw createException(DELETE_ELEGIDO,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private boolean isForTheAllElegidos(String productName)
    {
        return productName.equals("*");
    }
    
   
    private void modifyElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            hasOnlyOneSubscriber(session, requestId);
            
            validateParam("MAINIDElegidoToRegister", session, requestId, ZERO);
            
            prepareDataToElegido(session, requestId, ZERO, "MODIFY_ELEGIDO_PRICE");
            
            String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);
            changeItelOriginatingNodeInfo(requestId, 0, originNode, session);
            originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+ZERO+DOT+"originatingNodeInfo", session);            
            
            if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), 0, originNode)){
                prepareDataToSpecialElegido(session, requestId, ZERO);
            }
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
            
            setValidityDate(session, requestId, ZERO);
        } catch (Exception error) {
            throw createException(REGISTER_ELEGIDOS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private void validateParam(String param,
            Map<String, Object> session, String requestId, String subscriberId) throws Exception {
        Map<String, Object> request = getDataMapVariables("REQUEST".concat(DOT).concat(requestId).concat(DOT).concat(subscriberId), session);
        if(!request.containsKey(param)){
            throw createException(VALIDATE_REQUEST_PARAMETER,INTERNAL,ERROR_SOURCE_MODULE, String.format("Parameter %s is mandatory.", param));
        }
    }
    
    private void modifyMultiplesElegido(String sessionId, String requestId) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> session = (Map<String,Object>) mapVariables.get(sessionId);
            
            int subscribersCount = countSubscriber(session, requestId);
            
            for(int id = 0; id < subscribersCount; id++) {
                String subscriberId = String.valueOf(id);
                prepareDataToElegido(session, requestId, subscriberId, "MODIFY_ELEGIDO_PRICE");
                
                String originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                changeItelOriginatingNodeInfo(requestId, id, originNode, session);
                originNode = (String)getDataMapVariables("REQUEST"+DOT+requestId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                
                if(findBusinessRulesForSpecialElegido(sessionId, Integer.parseInt(requestId), id, originNode)){
                    prepareDataToSpecialElegido(session, requestId, subscriberId);
                }
                
                setValidityDate(session, requestId, subscriberId);
            }
            
            String currentInternal = "INTERNAL.".concat(requestId).concat(DOT).concat(ZERO).concat(DOT);
            setDataMapVariables(currentInternal.concat("MASTER"), TRUE, session);
            setFinalPrice(session, requestId, ZERO, getElegidoValue(session, requestId, ZERO));
        } catch (Exception error) {
            throw createException(REGISTER_MUTIPLES_ELEGIDOS_DATE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(error));
        }
    }

    private void setValidityDate(Map<String, Object> session,
            String requestId, String subscriberId)
            throws Exception {
        if(hasValidityDate( session, requestId, subscriberId)){
            String validityDate = getValidityDate(session, requestId, subscriberId);
            replaceValidityDate(session, requestId, subscriberId, validityDate);
        }
    }        
    
    private void hasOnlyOneSubscriber(Map<String,Object> session, String requestId) throws Exception {
        int numberSubscribers = countSubscriber(session, requestId);
        if(numberSubscribers > 1) {
            throw createException(HAS_MANY_SUBSCRIBERS,INTERNAL,ERROR_SOURCE_MODULE,"This operation accept only one subscriber.");
        }
    }
    
    private int countSubscriber(Map<String,Object> session, String requestId) throws Exception {
        return Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.".concat(requestId).concat(".NUMBER_SUBSCRIBER"),session));
    }
    
    private String getElegidoValue(Map<String,Object> session, String requestId, String subscriberId) throws Exception {
        String currentInternal = "INTERNAL.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
        return getDataMapVariables(currentInternal.concat("PRODUCT.VALUE"), session);
    }
    
    private void setFinalPrice(Map<String,Object> session, String requestId, String subscriberId, String finalPrice) throws Exception {
        try{
            
            String finalPriceParam = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT).concat("PRODUCT.FINAL_PRICE");
            String currentRequestParam = "REQUEST.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT).concat("charged");
            
            String charged = getDataMapVariables(currentRequestParam, session);
            if(isNot(charged)){
                finalPrice = ZERO;
            } 
            setDataMapVariables(finalPriceParam, finalPrice, session);
            
        } catch (Exception error){
            throw createException(SET_FINAL_PRICE,INTERNAL,ERROR_SOURCE_MODULE,
                    error.getMessage());
        }
    }

    private boolean isNot(String param){
        return "no".equals(param);
    }
    
    private boolean is(String param){
        return "yes".equals(param);
    }

    private void addElegidosToRegisterOnFirstSubscriber(Map<String,Object> session, String requestId) throws Exception {
        try{
            int subscribersCount = countSubscriber(session, requestId);
            
            String elegidosToRegisterParam = "INTERNAL.".concat(requestId).concat(DOT).concat(ZERO).concat(DOT).concat("ELEGIDOS_TO_REGISTER");
            setDataMapVariables(elegidosToRegisterParam, String.valueOf(subscribersCount), session);
        } catch (Exception error){
            throw createException(ELEGIDOS_TO_REGISTER,INTERNAL,ERROR_SOURCE_MODULE,
                    error.getMessage());
        }
    }
    
    @SuppressWarnings("unused")
	private void accumulateElegidoFinalPriceOnFirstSubscriber(Map<String,Object> session, String requestId) throws Exception {
        int subscribersCount = countSubscriber(session, requestId);

        // 1 - calculating total price(sum value of products)
        // 2 - Putting Zero value in final price for all subscribers(only the first one should be a valid price)
        java.math.BigDecimal finalPrice = java.math.BigDecimal.ZERO;
        for(int subscriberId = 0; subscriberId < subscribersCount; subscriberId++) {
            finalPrice = finalPrice.add(new java.math.BigDecimal(getElegidoValue(session, requestId, String.valueOf(subscriberId))));
            setFinalPrice(session, requestId, String.valueOf(subscriberId), ZERO);
        }

        //The first Elegido should have price, the others should be ZERO
        setFinalPrice(session, requestId, ZERO, finalPrice.toPlainString());
    }
    
    private void prepareDataToAllElegidos(Map<String, Object> session, String requestId, String subscriberId, String costParameter) throws Exception
    {
        //The table are create in subscriber level.
        String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);

        thereIsSubscriber(session, currentTable);           
        thereIsSubscription(session, currentTable);
        
        Map<String, Object> subscriptions = getDataMapVariables(currentTable.concat("MAINID.SUBSCRIPTIONS"), session);

        String currentRequest = "REQUEST.".concat(requestId).concat(DOT);
        String mainID = getDataMapVariables(currentRequest.concat(subscriberId).concat(".MAINID"), session);

        findMainIDRange(mainID, Integer.parseInt(requestId), Integer.valueOf(subscriberId), session);
        String regionId = getDataMapVariables(currentTable.concat("MAINID_RANGES.REGION_ID"), session);
        
        java.math.BigDecimal cost  = calcPriceToAllElegidos(session,subscriptions, regionId, costParameter);
        
        String currentInternal = "INTERNAL.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
        //setDataMapVariables(currentInternal.concat("PRODUCT.VALUE"), cost, session);
        setDataMapVariables(currentInternal.concat("PRODUCT.VALUE"), cost.toPlainString(), session);
        
        Map<String,Object> emptyHashForOffers = new java.util.concurrent.ConcurrentHashMap<String,Object>();
        setDataMapVariables(currentTable.concat("DEDICATED_ACCOUNT"), emptyHashForOffers, session);
        setDataMapVariables(currentTable.concat("OFFER"), emptyHashForOffers, session);
        
    }
    

    private java.math.BigDecimal calcPriceToAllElegidos(Map<String, Object> session, Map<String, Object> subscriptions, String regionId, String costParameter) throws Exception
    {
        java.math.BigDecimal cost = new java.math.BigDecimal(0);
        for (String idx : subscriptions.keySet())
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> subscription  = (Map<String, Object>) subscriptions.get(idx);
            //String productName = (String) subscription.get("productName"); <- nao existe productName na subscription, tem so o PRODUCT_ID
            String productID = (String) subscription.get("PRODUCT_ID");
            String productName = (String) getProductByID(productID).get("productName");
            //--
            Map<String,Object> product = getTheLatestVersionOfProductByName(productName,regionId);
            Map<String,Object> parameters = getProductParameter(session,(String)product.get("productID"));     
            
            if (productName.startsWith("Elegido"))
            {
                cost = cost.add(new java.math.BigDecimal(getProductPrice(parameters, product, costParameter)));
            }
        }
        return cost;
    }

    private void prepareDataToElegido(Map<String,Object> session, String requestId, String subscriberId, String costParameter) throws Exception {
        //The table are create in subscriber level.
        String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);

        thereIsSubscriber(session, currentTable);           
        thereIsSubscription(session, currentTable);
        
        Map<String, Object> subscriptions = getDataMapVariables(currentTable.concat("MAINID.SUBSCRIPTIONS"), session);

        String currentRequest = "REQUEST.".concat(requestId).concat(DOT);
        String mainID = getDataMapVariables(currentRequest.concat(subscriberId).concat(".MAINID"), session);

        findMainIDRange(mainID, Integer.parseInt(requestId), Integer.valueOf(subscriberId), session);
        String regionId = getDataMapVariables(currentTable.concat("MAINID_RANGES.REGION_ID"), session);

        String productName = getDataMapVariables(currentRequest.concat("productName"),session);
        
        Map<String,Object> product = getTheLatestVersionOfProductByName(productName,regionId);
        setDataMapVariables(currentTable.concat("PRODUCT"), product, session);

        Map<String, Object> subscription  = getOneSubscription(productName,subscriptions,true,INACTIVE_STATUS);
        setDataMapVariables(currentTable.concat("SUBSCRIPTION"), subscription, session);

        Map<String,Object> parameters = getProductParameter(session,(String)product.get("productID"));     
        setDataMapVariables(currentTable.concat("PRODUCT_VS_PARAMETER"), parameters, session);

        String currentInternal = "INTERNAL.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
        setDataMapVariables(currentInternal.concat("PRODUCT.VALUE"), getProductPrice(parameters, product, costParameter), session);

        setDataMapVariables(currentTable.concat("DEDICATED_ACCOUNT"), getContainerData("DEDICATED_ACCOUNT", (String)product.get("productID"), "DEDICATED_ACCOUNT", false), session);
        setDataMapVariables(currentTable.concat("OFFER"), getContainerData("OFFER", (String)product.get("productID"), "OFFER", false), session);

    }
    
    private void prepareDataToSpecialElegido(Map<String,Object> session, String requestId, String subscriberId) throws Exception{
    	
    	setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".IS_SPECIAL_ELEGIDO","1",session);
    	String currentTable = "TABLE.".concat(requestId).concat(DOT).concat(subscriberId).concat(DOT);
        String currentRequest = "REQUEST.".concat(requestId).concat(DOT);
        String regionId = getDataMapVariables(currentTable.concat("MAINID_RANGES.REGION_ID"), session);
    	
    	String productName = getDataMapVariables(currentRequest.concat("productName"),session);
    	Map<String,Object> product = getTheLatestVersionOfProductByName(productName,regionId);
    	String productId = (String)product.get("productID");
    	String serviceClassValidation = getServiceClassValidation(productId);
    	setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".SERVICE_CLASS_VALIDATION",serviceClassValidation,session);
    	if(serviceClassValidation == null){
    		throw createException(PREPARE_DATA_TO_SPECIAL_ELEGIDO, INTERNAL, ERROR_SOURCE_MODULE, "Cant find serviceClassValidation");
    	}
    	
    	if(ZERO.equals(serviceClassValidation)){
        	String statusAccountValidation = getAccountStatusValidation(productId);
        	if(ZERO.equals(statusAccountValidation)){
        		setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".ACCOUNT_STATUS_VALIDATION",statusAccountValidation,session);
        	}else{
        		String activeDay = getActiveDay(productId);
            	setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".ACTIVE_DAY",activeDay,session);
            	setDataMapVariables("INTERNAL."+requestId+DOT+subscriberId+".ACCOUNT_STATUS_VALIDATION",statusAccountValidation,session);
        	}
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
    private boolean findBusinessRulesForSpecialElegido(String sessionID, int resquesID, int subscriberID, String originNode) throws Exception{
        try 
        {
			DataObject cBusinessRulesOriginFrontEnd = getEntity("BUSINESS_RULES_BY_ORIGIN_FRONT_END").getData();
			String rulesList  = cBusinessRulesOriginFrontEnd.getValueAsString(originNode);
			Map<String, Object> rules = getDataContainer("BUSINESS_RULES_BY_ORIGIN_FRONT_END", rulesList);
			int rulesLength = rules.size();
			for (int i = 0 ; i < rulesLength ; i++){
				
				String ruleID = (String)((Map<String, Object>)rules.get(String.valueOf(i))).get("RULE_ID");
				DataObject cBusinessRules= getEntity("BUSINESS_RULES_BY_ID").getData();
				String fieldComparationList  = cBusinessRules.getValueAsString(ruleID);
				Map<String, Object> rulesID = (Map<String, Object>)getDataContainer("BUSINESS_RULES_BY_ID", fieldComparationList);
				if (findBusinessRules(rulesID,resquesID,subscriberID,sessionID)){
				    return true;
				}     
			}
            return false;

        } 
        catch (Exception e) 
        {
            throw createException(FIND_BUSSINESS_RULES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }        
    }
    
    @SuppressWarnings("unchecked")
    private boolean findBusinessRules(Map<String, Object> listComp,int resquesID, int subscriberID,String sessionID) throws Exception
    {
        try {
        	Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
        	String operation = (String)getDataMapVariables("REQUEST."+resquesID+".operation",session);
        	//boolean hasSpecialElegido = false;
            for (final String compareIdx : (Set<String>)listComp.keySet()){
                String field  = (String)(((Map<String, Object>)listComp.get(compareIdx)).get("FIELD"));
                String value  = (String)((Map<String, Object>)listComp.get(compareIdx)).get("VALUE");
                
                if(field.equals("REQUEST.operation") && !operation.equals(value)){
                	return false;
                }else if(field.equals("IS_SPECIAL_ELEGIDO") && !"1".equals(value)){
                	return false;
                }
                return true;
                
            }
            return false;
        } 
        catch (Exception e) {
            throw createException(VARIABLES_VALIDATION,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private Map<String, Object> getOneSubscription(String productName,
            Map<String, Object> subscriptions, boolean isRestricted,
            String restrictions) throws Exception {
        Map<String,Object> parameters = getSubscriptionWithThisProductName(productName,subscriptions,isRestricted,restrictions);
        
        if(parameters.size() == 0){
            throw createException(THERE_IS_NOT_SUBSCRIPTION,INTERNAL,ERROR_SOURCE_MODULE,
                    "There is no subscription");                
        }
        
        return parameters;
    }

    private void thereIsSubscription(Map<String, Object> session,
            String table) throws Exception {
        if (!fieldExistMapVariables(table.concat("MAINID.SUBSCRIPTIONS"), session)){
            throw createException(THERE_IS_NOT_SUBSCRIPTION,INTERNAL,ERROR_SOURCE_MODULE,
                    "There is no subscription");
        }
    }

    private void thereIsSubscriber(Map<String, Object> session,
            String table) throws Exception {
        if (!fieldExistMapVariables(table.concat("MAINID"), session)){
            throw createException(THERE_IS_NOT_SUBSCRIBER,INTERNAL,ERROR_SOURCE_MODULE,
                    "There is not subscriber.");
        }
    }

    private String getProductPrice(Map<String,Object> parameters, Map<String,Object> product, String costParameter) throws Exception{
        try{
            
            java.math.BigDecimal finalPrice = null; 
            Map<String, Object> parameter = getLineFrom(parameters, "PARAMETER_TYPE", costParameter);

            java.math.BigDecimal value = new java.math.BigDecimal(String.valueOf(parameter.get("PARAMETER_VALUE")));
            if(isValidString((String)parameter.get("taxID"))){
                if(product.get("taxType").equals("monetary")){
                    finalPrice = value.add(new java.math.BigDecimal(String.valueOf(product.get("taxValue"))));
                } else {
                    java.math.BigDecimal percentage = new java.math.BigDecimal(String.valueOf(product.get("taxValue")));
                    percentage.add(java.math.BigDecimal.ONE);
                    finalPrice = value.multiply(percentage);
                }
            } else {
                finalPrice = value;
            }
            
            return finalPrice.toPlainString();
        } catch(Exception error){
            throw createException(GET_EXPERY_DATE,INTERNAL,ERROR_SOURCE_MODULE,error.getMessage());
        }
    }
    
    
    private String getServiceClassValidation(String productId) throws Exception{
    	Map<String, Object> parameter = getContainerData("SERVICE_CLASS_VALIDATION", productId, "SERVICE_CLASS_VALIDATION", true);
    	return (String) parameter.get("SERVICE_CLASS_VALIDATION_VALUE");
    }
    
    private String getAccountStatusValidation(String productId) throws Exception{
    	Map<String, Object> parameter = getContainerData("ACCOUNT_STATUS_VALIDATION", productId, "ACCOUNT_STATUS_VALIDATION", true);
    	return (String) parameter.get("ACCOUNT_STATUS_VALIDATION_VALUE");
    }
    
    private String getActiveDay(String productId) throws Exception{
    	Map<String, Object> parameter = getContainerData("ACTIVE_DAY_ACCOUNT_STATUS", productId, "ACTIVE_DAY_ACCOUNT_STATUS", true);
    	return (String) parameter.get("ACTIVE_DAY");
    }
    
    private boolean isValidString(String value) {
        return value != null && !value.isEmpty();
    }
    
    private Map<String, Object> getLineFrom(Map<String, Object> listMap, String hasAKey, String withValue) throws Exception {
        try{
            if(listMap.isEmpty()){
                throw createException(GET_VALUE_FROM_EMPTY_MAP,INTERNAL,ERROR_SOURCE_MODULE,"The map is empty.");
            }
            for(String index: listMap.keySet()){
                @SuppressWarnings("unchecked")
                Map<String, Object> line = (Map<String, Object>) listMap.get(index);
                if(String.valueOf(line.get(hasAKey)).equals(withValue)){
                    return line;
                }
            }
            throw createException(GET_VALUE_FROM_LINE_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,String.format("There is not a key %s with the value %s in any line of %s", hasAKey, withValue, listMap));
        } catch(Exception error){
            throw createException(GET_VALUE_FROM,INTERNAL,ERROR_SOURCE_MODULE,error.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getActiveAndQueuePendingSubscriptions(Map<String, Object> subscriptions) {
        Map<String, Object> active = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        for(String index : subscriptions.keySet()){
            Map<String, Object> subscription = (Map<String, Object>) subscriptions.get(index);
            String status = (String) subscription.get("STATUS_NAME");
            if(status.equals(FULL) || status.equals(RESTRICTED) || status.equals(QUEUED)){
                active.put(index, subscription);
            }	
        }
        return active;
    }
    
    @SuppressWarnings("unchecked")
    private 
    void createNewSubscription(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            String productName = (String)getDataMapVariables("REQUEST."+responseID+".productName",session);
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            setDataMapVariables("ACTION."+responseID+".MSG_DATA.FINAL_PRICE_MASTER",ZERO,session);
            Map<String,Object> product = null;

            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            
            for (int subscriberId = 0; subscriberId < numberSubscriber; subscriberId++)
            {
                Map<String, Object> allSubscriptions = new java.util.concurrent.ConcurrentHashMap<String, Object>();

                if(fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session)){
                    allSubscriptions =  getActiveSubscriptions((Map<String, Object>) getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session));                        
                }
                
                String originNode = (String)getDataMapVariables("REQUEST"+DOT+responseID+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                changeItelOriginatingNodeInfo(responseID, subscriberId, originNode, session);
                originNode = (String)getDataMapVariables("REQUEST"+DOT+responseID+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".CREATE_NEW_SUBSCRIPTION_DATA.PROPERTY", "0", session);
                
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".PROGRAMMED_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_SUBSCRIBER_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_QOS_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_CAMPAIGN","0",session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".UPDATE_CAMPAIGN","0",session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".IS_ANIDADO","0",session);
                
                findMainIDRange((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseID), subscriberId, session);
                String regionId = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID_RANGES.REGION_ID", session);
                product = getTheLatestVersionOfProductByName(productName,regionId);
                Map<String,Object> productVSparameter = getProductParameter(sessionID,(String)product.get("productID"));  
                
                String productId = (String)product.get("productID");
                if(isAnidadosProduct(productId))
                {
                    if(product.containsKey("recurrenceID") && (String)product.get("recurrenceID") != null && !(((String)product.get("recurrenceID")).equals(EMPTY)))                      {
                        throw createException(ANIDADOS_VALIDATE_RECURRENT,"business",ERROR_SOURCE_MODULE,"Product "+productName+" is anidados and can't be recurrent.");                            
                    }
                    
                    String install = getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".externalData", session);
                    if(install.equals("install")){
                        throw createException(ANIDADOS_VALIDATE_INSTALL,"business",ERROR_SOURCE_MODULE,"Product "+productName+" is anidados and can't be install.");
                    }
                    
                    if(fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session))
                    {
                        Map<String,Object> subscriptions = getActiveAndQueuePendingSubscriptions((Map<String, Object>) getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session));
                        if(subscriptions.size()>0 && !canINestMoreSubscription(subscriptions, productId))
                        {
                            throw createException(ANIDADOS_VALIDATE_MAX_USES,"business",ERROR_SOURCE_MODULE,"Product "+productName+" already has the limit of subscriptions allowed.");
                        }
                    }
                    if (productName.startsWith("Elegido"))
                    {
                        throw createException(ANIDADOS_VALIDATE_ELEGIDO,"business",ERROR_SOURCE_MODULE,"Product "+productName+" is anidados and can't be elegido.");
                    }
                    
                    if(isValidField(product, "productTime") && !isValidField(product, "productVolume") && getAnidadosType(productId).equals("Aggregate"))
                    {
                        throw createException(ANIDADOS_VALIDATE_ENQUEUE,"business",ERROR_SOURCE_MODULE,"Product "+productName+" is not limited by time and can't be aggregate enqueue type.");
                    }

                    //SET ANIDADOS FLAG
                    setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".IS_ANIDADO","1",session);
                    
                    verifyIsOnlyForCharge(responseID, session, subscriberId, allSubscriptions, productId);
                }
                            
                if(isElegido(productName)){
                	
                    if(findBusinessRulesForSpecialElegido(sessionID, Integer.parseInt(responseID), subscriberId, originNode)){
                        prepareDataToSpecialElegido(session, responseID, String.valueOf(subscriberId));
                    }
                }
                
                validationAllRequestParameter((String)product.get("categoryID"),"createNewSubscription",responseID,String.valueOf(subscriberId),session);
                
                if(product.isEmpty())
                {
                    throw createException(PRODUCT_NAME_NOT_EXIST,"business",ERROR_SOURCE_MODULE,"Product "+productName+" does not exist.");
                }
                
                if (fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".masterSubscriberMAINID",session))
                {
                    Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIPTIONS",session);
                 
                    product = getProductWithThisName(productName,subscriptions);
                    
                    if(product.isEmpty())
                    {
                        formatErrorResponse(responseID,session,NOT_FOUND_PRODUCT_MASTER_SUBSCRIBER_MAINID,"business",ERROR_SOURCE_MODULE,"Specified master subscriber has not a master role for this requested product.");
                        break;
                    }
                    
                    if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIBER.PROPERTIES", session))
                    {
                        String masterSubscriberProperties = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIBER.PROPERTIES", session);
                        
                        if(isPresentProperty(masterSubscriberProperties, "INDEBT_SUBSCRIBER", session))
                        {
                            formatErrorResponse(responseID,session,MASTER_INDEBT_SUBSCRIBER,"business",ERROR_SOURCE_MODULE,"Can't create the subscription. The subscriber is indebt.");
                            break;
                        }
                        
                        if(isPresentProperty(masterSubscriberProperties, "UNREGISTERED_SUBSCRIBER", session))
                        {
                            formatErrorResponse(responseID,session,MASTER_UNREGISTERED_SUBSCRIBER,"business",ERROR_SOURCE_MODULE,"Can't create the subscription. The subscriber is unregistered.");
                            break;
                        }
                    }
                    
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT",product,session);
                    
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIPTION",getsubscriptionWithThisProductName((String)product.get("productName"), subscriptions, dateTimeExecution),session);
                    
                }
                else
                {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT",product,session);
                }
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", product.get("productVolume"), session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_TIME", product.get("productTime"), session);
                
                Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId,session);
                
                if(subscriber.containsKey("programmedActivationDate"))
                {
                    String programmedActivationDate = (String)subscriber.get("programmedActivationDate");
                    
                    if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,programmedActivationDate)))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".PROGRAMMED_FLAG","1",session);
                        
                        String productStartTimestamp = (String)product.get("productStartTimestamp");
                        String productEndTimestamp = (String)product.get("productEndTimestamp");
                        
                        if((firstIsGreatThanOrEqualSecond(productStartTimestamp,programmedActivationDate)) || (firstIsGreatThanOrEqualSecond(programmedActivationDate,productEndTimestamp)))
                        {
                            formatErrorResponse(responseID,session,PRODUCT_RESTRICT_TO_PROGRAMMED,"business",ERROR_SOURCE_MODULE,"The product will not be available on this date.");
                            break;
                        }
                        
                        continue;
                    }
                }
                
                String subscriberType = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberType", session);
                String deviceType = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".deviceType", session);
                
                if(isValidField(product, "subscriberType"))
                {
                    String productSubscriberType = (String)product.get("subscriberType");
                    if(!subscriberType.equals(productSubscriberType))
                    {
                        formatErrorResponse(responseID,session,PRODUCT_RESTRICT_BY_SUBSRIBER_TYPE,"business",ERROR_SOURCE_MODULE,"The product is restrict by subscriber type.");
                        break;
                    }
                }
                
                if(isValidField(product, "deviceType"))
                {
                    String productDeviceType = (String)product.get("deviceType");
                    if(!deviceType.equals(productDeviceType))
                    {
                        formatErrorResponse(responseID,session,PRODUCT_RESTRICT_BY_DEVICE_TYPE,"business",ERROR_SOURCE_MODULE,"The product is restrict by device type.");
                        break;
                    }
                }

                setSapcGroupId(subscriberType,deviceType,responseID,subscriberId,session);

                if(isValidField(product, "categoryName"))
                {
                    String categoryName = (String)product.get("categoryName");

                    if(categoryName.equals("Sob-Medida"))
                    {
                        String requestVolume = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".volume", session);
                        String requestPrice  = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".price", session);
                        
                        product.put("productVolume", requestVolume);
                        product.put("productPrice", requestPrice);
                    }
                }
                
                
                if(isValidField(product, "productProperties"))
                {
                    if(isPresentProperty((String)product.get("productProperties"), "BILLING_CYCLE",session))
                    {
                        if(!fieldExistMapVariablesAndNotNull("REQUEST."+responseID+DOT+subscriberId+".billingCycleDate", session))
                        {
                            formatErrorResponse(responseID,session,BILLING_CYCLE_DATE_IS_NOT_PRESENT,"malformed",ERROR_SOURCE_MODULE,"The field billingCycleDate is not present in request.");
                            break;
                        }
                        if(!fieldExistMapVariablesAndNotNull("REQUEST."+responseID+DOT+subscriberId+".proRataType", session))
                        {
                            formatErrorResponse(responseID,session,PRORATA_TYPE_IS_NOT_PRESENT,"malformed",ERROR_SOURCE_MODULE,"The field proRataType is not present in request.");
                            break;
                        }

                    }
                }
                
                if(product.containsKey("categoryMasterCategoryName") && (String)product.get("categoryMasterCategoryName") != null && !(((String)product.get("categoryMasterCategoryName")).equals(EMPTY)))
                {
                    if(product.containsKey("recurrenceID") && (String)product.get("recurrenceID") != null && !(((String)product.get("recurrenceID")).equals(EMPTY)))
                    {
                        formatErrorResponse(responseID,session,DEPENDENT_CAN_NOT_BE_RECURRENCY,"business",ERROR_SOURCE_MODULE,"Dependent can not be recurrency.");
                        break;
                    }
                }
                
                if (fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER",session))
                {
                    if (!(compareSubscriberData(subscriber,(Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER",session))))
                    {
                        formatErrorResponse(responseID,session,SUBSCRIBER_IS_DIFFERENT_WITH_DATABASE,"business",ERROR_SOURCE_MODULE,"Subscriber request is different with database");
                        break;
                    }
                    
                    if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.PROPERTIES", session))
                    {
                        String subscriberProperties = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.PROPERTIES", session);
                        
                        if(isPresentProperty(subscriberProperties, "INDEBT_SUBSCRIBER", session))
                        {
                            formatErrorResponse(responseID,session,INDEBT_SUBSCRIBER,"business",ERROR_SOURCE_MODULE,"Can't create the subscription. The subscriber is indebt.");
                            break;
                        }
                        
                        if(isPresentProperty(subscriberProperties, "UNREGISTERED_SUBSCRIBER", session))
                        {
                            formatErrorResponse(responseID,session,UNREGISTERED_SUBSCRIBER,"business",ERROR_SOURCE_MODULE,"Can't create the subscription. The subscriber is unregistered.");
                            break;
                        }
                    }
                }
                else
                {
                    setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_SUBSCRIBER_FLAG","1",session);
                }
                
                Map<String,Object> campaign = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                String categoryID = (String)product.get("categoryID");
                
                if(existDataContainer(campaign, categoryID, "CAMPAIGN_BY_CATEGORY_ID", "CAMPAIGN_BY_CATEGORY_ID", true))
                {
                    String insertSubscriber = (String)getDataMapVariables("INTERNAL."+responseID+"."+subscriberId+".INSERT_SUBSCRIBER_FLAG", session);
                    
                    if(insertSubscriber.equals("0"))
                    {
                        String mainIDCampaign = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.CAMPAIGN_ID", session);
                        
                        if(mainIDCampaign != null && !mainIDCampaign.isEmpty())
                        {
                            formatErrorResponse(responseID,session,MAINID_HAS_CAMPAIGN,"business",ERROR_SOURCE_MODULE,"Subscriber already has a campaign.");
                            break;
                        }
                    }
                    
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN", campaign, session);
                    
                    int period             = Integer.parseInt((String)campaign.get("PERIOD"));
                    String periodType     = (String)campaign.get("PERIOD_TYPE");
                    int interval           = Integer.parseInt((String)campaign.get("INTERVAL"));
                    String billingCycleDate = null;
                    
                    if(fieldExistMapVariablesAndNotNull("REQUEST."+responseID+"."+subscriberId+".billingCycleDate", session))
                    {
                        billingCycleDate = (String)getDataMapVariables("REQUEST."+responseID+"."+subscriberId+".billingCycleDate", session);
                    }
                    
                    Map<String,Object> periodData = getStartNextPeriodCampaing(dateTimeExecution, periodType, interval, billingCycleDate, dateTimeExecution, dateTimeExecution);
                    
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.CAMPAIGN_ID"           , (String)campaign.get("CAMPAIGN_ID"), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.PERIOD"                , String.valueOf(period), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.PERIOD_TYPE"           , (String)campaign.get("PERIOD_TYPE"), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.USES_IN_PERIOD"        , (String)campaign.get("MAX_USES_IN_PERIOD"), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.START_NEXT_PERIOD"     , (String)periodData.get("START_NEXT_PERIOD_CAMPAIGN"), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.END_CURRENT_PERIOD"     , (String)periodData.get("END_CURRENT_PERIOD_CAMPAIGN"), session);
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.START_TIMESTAMP"       , dateTimeExecution, session);
                    
                    
                    Map<String,Object> lastPeriodCampaign = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                    
                    lastPeriodCampaign.put("START_NEXT_PERIOD_CAMPAIGN", dateTimeExecution);
                    lastPeriodCampaign.put("END_CURRENT_PERIOD_CAMPAIGN", "");
                    
                    for(int p = 0; p < period ; p++)
                    {
                        lastPeriodCampaign = getStartNextPeriodCampaing(dateTimeExecution, periodType, interval, billingCycleDate, (String)lastPeriodCampaign.get("START_NEXT_PERIOD_CAMPAIGN"),(String)lastPeriodCampaign.get("START_NEXT_PERIOD_CAMPAIGN"));
                        billingCycleDate = null;
                    }
                    
                    setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.END_TIMESTAMP" , (String)lastPeriodCampaign.get("END_CURRENT_PERIOD_CAMPAIGN"), session);
                    setDataMapVariables("INTERNAL."+responseID+"."+subscriberId+".INSERT_CAMPAIGN","1",session);
                }

                formatCreateNewSubscriptionData(sessionID,  responseID, String.valueOf(subscriberId));

                Map<String,Object> thresholds = getInitialThresholdsByProductID((String)product.get("productID"),(String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session));
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".STATUS.statusId",getStatusByName(FULL),session);
                //preparing for insert on subscriptionPolicies
                String thresholdId = null;
                
                if(!thresholds.isEmpty())
                {
                    thresholdId = (String)thresholds.get("THRESHOLD_ID");
                }
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(FULL), thresholdId, getSubscriberTypeId(subscriberType), getDeviceTypeId(deviceType)),session);
                String policyName = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY.name", session);
                

                if (fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session))
                {
                    String productPriority = getProductPriority((String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.productProperties",session),session);
                    
                    if (!isAnidadosProduct(productId) && isThereProductWithThisPriority(productPriority,(Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),dateTimeExecution))
                    {
                        formatErrorResponse(responseID,session,SUBSCRIBER_HAS_PRODUCT_WITH_THIS_PRIORITY,"business",ERROR_SOURCE_MODULE,"Subscriber already has a current active subscription for a product with the same priority as the requested product");
                        break;
                    }
                    
                    if(!(thresholds.isEmpty()))
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD", (String)thresholds.get("QOS_DOWNLOAD"), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD"  , (String)thresholds.get("QOS_UPLOAD"), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.STYPE"       , getStype((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberType", session)), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.CRBN"       , policyName, session);
                        
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_QOS_FLAG","1",session);
                    }
                    
                    if (!(isThereProductWithGreatPriority(productPriority,(Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),session,dateTimeExecution)))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC","1",session);
                    }
                }
                else
                {
                    
                    if(!(thresholds.isEmpty()))
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD", (String)thresholds.get("QOS_DOWNLOAD"), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD"  , (String)thresholds.get("QOS_UPLOAD"), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.STYPE"       , getStype((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberType", session)), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.CRBN"       , policyName, session);
                        
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_QOS_FLAG","1",session);
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC","1",session);
                    }
                    else
                    {
                        formatErrorResponse(responseID,session,PRODUCT_HAS_NOT_INITIAL_THRESHOLD,"business",ERROR_SOURCE_MODULE,"The product ["+(String)product.get("productID")+"] hasn't initial threshold");
                        break;
                    }
                }

                if (!(productVSparameter.isEmpty()))
                {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT_VS_PARAMETER",productVSparameter,session);
                }
                
                String mainID = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".MAINID",session);

                
                String promotionID = (String)product.get("promotionID");
                Map<String,Object> promotion = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                if (promotionID != null && !(promotionID.isEmpty()))
                {
                    String mainIDRangeID = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID_RANGES.MAINID_RANGE_ID", session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.promotionID","null",session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.MAX_USES","null",session);
                    String imei = ZERO;
                    if(fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".IMEI",session))
                    {
                        imei = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".IMEI",session);
                    }
                    promotion = getPromotionByID((String)product.get("promotionID"),mainID,mainIDRangeID,imei,session);
                    
                    if (!promotion.isEmpty())
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.",promotion,session); 
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.promotionID",(String)product.get("promotionID"),session);
                        
                        //if product is limited by time
                        if(isValidField(product, "productTime"))
                        {
                            applyPromotionTime(product, promotion, Integer.parseInt(responseID), subscriberId, session);
                        }
                        //if product is limited by volume
                        if(isValidField(product, "productVolume"))
                        {
                            applyPromotionVolume(product, promotion, Integer.parseInt(responseID), subscriberId, session);
                        }
                    }
                }
                
                calculateFinalPrice(product, promotion, Integer.parseInt(responseID), subscriberId, session);
                
             
                if (((String)product.get("categoryName")).matches("Shared Plan|Chuveirinho"))
                {
                    if ((fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberRole", session) && (((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberRole", session)).equals("dependant"))) || (fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".masterSubscriberMAINID", session)))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".mainIDDependant", "1", session);
                    }
                    if(((String)product.get("categoryName")).equals("Chuveirinho"))
                    {
                        addMasterPrice(Integer.parseInt(responseID), subscriberId, session);
                    }
                }
                
                setDedicatedAccountAndOffersToAir(responseID, session, product, subscriberId, allSubscriptions);
                
                //----------------------------------------------------------------
                // set volume for AIR in dedicated_account field
                //----------------------------------------------------------------
                
                Map<String,Object> airVolumeDedicatedAccount = getContainerData("AIR_VOLUME_DEDICATED_ACCOUNT", (String)product.get("productID"), "AIR_VOLUME_DEDICATED_ACCOUNT", true);
                
                String airVolumeDedicatedAccountID = (String)airVolumeDedicatedAccount.get("AIR_VOLUME_DEDICATED_ACCOUNT_ID");
                
                if (((String)product.get("categoryName")).matches("Shared Plan"))
                {
                    setDedicatedAccount((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", session), airVolumeDedicatedAccountID, ZERO);
                }
                else
                {
                    if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", session))
                    {
                        String airVolume = (String) getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", session);
                        setDedicatedAccount((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", session), airVolumeDedicatedAccountID, airVolume);
                    }
                }
                
                if (isPresentProperty((String)product.get("productProperties"), "BILLING_CYCLE",session))
                {
                    applyProRataInBillingCycle(dateTimeExecution, (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".billingCycleDate", session), (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".proRataType", session), session, responseID, subscriberId+EMPTY);
                    if (!(productVSparameter.isEmpty()))
                    {
                        if(isValidField(productVSparameter, "AIR_VOLUME_ACCUMULATOR"))
                        {
                            String airVolumeAccumulatorID = (String)productVSparameter.get("AIR_VOLUME_ACCUMULATOR");
                            setAccumulator((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".ACCUMULATOR", session), airVolumeAccumulatorID, (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", session));
                        }
                        
                        if(isValidField(productVSparameter, "AIR_FLAG_BILLING_CYCLE"))
                        {
                            int finalProductVolume = Integer.parseInt((String)product.get("FINAL_VOLUME"));
                            int productVolume = Integer.parseInt((String)product.get("productVolume"));
                            if(finalProductVolume < productVolume)
                            {
                                String airVolumeAccumulatorID = (String)productVSparameter.get("AIR_FLAG_BILLING_CYCLE");
                                setAccumulator((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".ACCUMULATOR", session), airVolumeAccumulatorID, "1");
                            }
                        }
                    }
                }
                else
                {
                    if(!isValidField(product, "productTime"))
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", "null", session);
                    }
                     else
                    {
                        String endTimeStampCurrentSubscription = addHourInDateTime((String)product.get("productTime"),dateTimeExecution);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", endTimeStampCurrentSubscription, session);
                        
                        if (isAnidadosProduct(productId)) {
                            if (getAnidadosType(productId).equals("Aggregate")) {
                                String groupId = getAnidadosGroupId(productId);
                                List<Map<String, Object>> allAnidadosSubscriptions = getAnidadosSubscriptions(getActiveSubscriptions(allSubscriptions));
                                List<Map<String, Object>> anidadosSubscriptionsByGroup = getAnidadosSubscriptionsByGroup(allAnidadosSubscriptions, groupId);
                                
                                if (!anidadosSubscriptionsByGroup.isEmpty()) {
                                    String endTimestampAggregated = (String) anidadosSubscriptionsByGroup.get(0).get("END_TIMESTAMP");
                                    
                                
                                    SimpleDateFormat sdp = new SimpleDateFormat("yyyyMMddHHmmssZ");
                                
                                    Date endTimeStampSubscription = sdp.parse(endTimestampAggregated);
                                    Date endTimeStampCurrent = sdp.parse(endTimeStampCurrentSubscription);
                                    if(endTimeStampCurrent.before(endTimeStampSubscription))
                                    {
                                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", endTimestampAggregated, session);
                                    }
                                }
                            }
                        }
                    }
                }
                
                if(isDailyPlan((String) product.get("categoryName"))){
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", getEndHourDay(dateTimeExecution), session);
                }
                
                if (((String)product.get("categoryName")).matches("standaloneNavigation"))
                {
                    if(((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".standaloneNavigationType",session)).matches("PDP"))
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".STATUS.statusId",getStatusByName(TIME_EXPIRED),session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", dateTimeExecution, session);
                    }
                    else if(((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".standaloneNavigationType",session)).matches("period"))
                    {
                        if(fieldExistMapVariablesAndNotNull("REQUEST."+responseID+DOT+subscriberId+".standaloneNavigationEndDate", session))
                        {
                            String standaloneNavigationEndDate = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".standaloneNavigationEndDate", session);
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", standaloneNavigationEndDate, session);
                        }
                        else
                        {
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", getEndMoth(), session);
                        }                                
                    }
                }
                
                //----------------------------------------------------
                // adding END_TIMESTAMP in a dependant equals master  
                //----------------------------------------------------
                
                if (((String)product.get("categoryName")).matches("Shared Plan|Chuveirinho"))
                {
                    if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIPTION.END_TIMESTAMP", session))
                    {
                        String endTimeStampMaster = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".masterSubscriberMAINID.SUBSCRIPTION.END_TIMESTAMP", session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", endTimeStampMaster, session);
                    }
                }
            
                String finalPrice = getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_PRICE", session);
                setFinalPrice(session, responseID, String.valueOf(subscriberId), finalPrice);
                
                String externalData = getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".externalData", session);
                if(externalData.equals("install")){
                    
                    //if there is a subscriber in database
                    if( fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.MAINID", session) ){
                        
                        //if the subscriber does not has a installed property
                        if( !bitAndProperty((String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.PROPERTIES", session),INSTALLED_PROPERTY) ){
                            
                            //put the flag to change the property
                            setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_SUBSCRIBER_PROPERTY_FLAG", "1", session);
                            
                            //if it does not has subscriptions 
                            if( !fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session)  ){
                                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_SUBSCRIBER_PROPERTY_FLAG", "1", session);
                            }
                            //or if it has more subscriptions active or suspense, it can`t be changed.
                            else{
                                String[] status = {"VOLUME_EXPIRED","TIME_EXPIRED", "CANCELLED","NO_PLAN"};
                                List<String> deadctivatedSubscriptionStatus = Arrays.asList(status);
                                
                                for( Map.Entry<String,Object> subscriptions : ((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session)).entrySet() ){
                                    Map<String,Object> currentSusbcriprion = (Map<String,Object>)subscriptions.getValue();
                                    String propertyName = (String)currentSusbcriprion.get("STATUS_NAME");
                                    
                                    if( !deadctivatedSubscriptionStatus.contains(propertyName) ){
                                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_SUBSCRIBER_PROPERTY_FLAG", "0", session);
                                    }
                                }
                            }
                        }
                    }
                    
                 setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", null, session);
                }

                String campaignId = null;
                
                if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.CAMPAIGN_ID", session))
                {
                    campaignId = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.CAMPAIGN_ID", session);
                }
                
                if(campaignId != null && isPresentProperty((String)product.get("productProperties"), "CAMPAIGN", session))
                {
                    
                    String endTimeStampCampaign = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.END_TIMESTAMP_CAMPAIGN", session);
                    
                    if(firstIsGreatThanOrEqualSecond(endTimeStampCampaign,dateTimeExecution))
                    {
                        campaign =getContainerData("CAMPAIGN_BY_NAME", (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.CAMPAIGN_NAME", session), "CAMPAIGN_BY_NAME", true) ;
                        
                        int period                      = Integer.valueOf((String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.PERIOD_CAMPAIGN", session));
                        String periodType               = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.PERIOD_TYPE", session);
                        String interval                 = (String)campaign.get("INTERVAL");
                        String startCampaign            = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.START_TIMESTAMP_CAMPAIGN", session);
                        int campaignUsage               = Integer.valueOf((String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.USES_IN_PERIOD_CAMPAIGN", session));
                        String startNextPeriodCampaign  = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.START_NEXT_PERIOD_CAMPAIGN", session);
                        String endCurrentPeriodCampaign = (String)getDataMapVariables("TABLE."+responseID+"."+subscriberId+".MAINID.SUBSCRIBER.END_CURRENT_PERIOD_CAMPAIGN", session);
                        
                        Map<String,Object> periodData = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                        
                        periodData.put("START_NEXT_PERIOD_CAMPAIGN", startNextPeriodCampaign);
                        periodData.put("END_CURRENT_PERIOD_CAMPAIGN", endCurrentPeriodCampaign);
                        
                        
                        while(period > 0 && firstIsGreatThanOrEqualSecond(dateTimeExecution,endCurrentPeriodCampaign))
                        {
                            periodData = getStartNextPeriodCampaing(startCampaign, periodType, Integer.valueOf(interval), null, (String)periodData.get("START_NEXT_PERIOD_CAMPAIGN"), dateTimeExecution);
                            period--;
                            campaignUsage = Integer.valueOf((String)campaign.get("MAX_USES_IN_PERIOD"));
                        }
                        
                        if(campaignUsage > 0)
                        {
                            --campaignUsage;
                            
                            setDataMapVariables("INTERNAL."+responseID+"."+subscriberId+".UPDATE_CAMPAIGN","1",session);
                            
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.CAMPAIGN_ID"           , (String)campaign.get("CAMPAIGN_ID"), session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.PERIOD"                , String.valueOf(period), session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.PERIOD_TYPE"           , periodType, session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.USES_IN_PERIOD"        , campaignUsage, session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.START_NEXT_PERIOD"     , (String)periodData.get("START_NEXT_PERIOD_CAMPAIGN"), session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.END_CURRENT_PERIOD"     , (String)periodData.get("END_CURRENT_PERIOD_CAMPAIGN"), session);
                            setDataMapVariables("TABLE."+responseID+"."+subscriberId+".CAMPAIGN_USAGE.START_TIMESTAMP"       , startCampaign, session);
                                
                            Map<String,Object> campaignActions = getContainerData("CAMPAIGN_ACTION", campaignId, "CAMPAIGN_ACTION", false);
                            
                            for(String idx : campaignActions.keySet())
                            {
                                Map<String,Object> campaignAction = (Map<String,Object>)campaignActions.get(idx);
                                String fieldName = getFieldName((String)campaignAction.get("FIELD"),responseID,String.valueOf(subscriberId),session);
                                Object fieldValue = getValueField(campaignAction, responseID,String.valueOf(subscriberId),session);
                                setDataMapVariables(fieldName, fieldValue, session);
                            }
                        }
                    }
                }
            }
            
            if (((String)product.get("categoryName")).matches("Shared Plan|Chuveirinho"))
            {
                reStructRequestWithMaster(responseID,session);
            }
            

            
        }
        catch (Exception error) 
        {
            String errorCode        = CREATE_NEW_SUBSCRIPTION;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);

        }
    }

    private void setDedicatedAccountAndOffersToAir(String responseID,
            Map<String, Object> session, Map<String, Object> productRequest,
            int subscriberId, Map<String, Object> allSubscriptions) throws Exception {
     try{
            String productIdRequest = (String)productRequest.get("productID"); 
            
            String businessRuleAnidados = "0";
            if(fieldExistMapVariablesAndNotNull("INTERNAL."+ZERO+DOT+subscriberId+".IS_ANIDADO", session)){
            	businessRuleAnidados = getDataMapVariables("INTERNAL."+ZERO+DOT+subscriberId+".IS_ANIDADO", session);
            }
            
            if (businessRuleAnidados.equals("1")) {
                Map<String, Object> dedicatedAccounts = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                Map<String, Object> offers = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                Map<String, Object> thresholds = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                List<Map<String, Object>> anidadosSubscriptionsByGroup = new ArrayList<Map<String,Object>>();
                
                if(!allSubscriptions.isEmpty())
                {
                    List<Map<String, Object>> anidadosSubscriptions =  getAnidadosSubscriptions(allSubscriptions);
                    String groupIdCurrentProductId = getAnidadosGroupId(productIdRequest);
                    anidadosSubscriptionsByGroup = getAnidadosSubscriptionsByGroup(anidadosSubscriptions, groupIdCurrentProductId);
                }
                
                if (anidadosSubscriptionsByGroup.isEmpty()) {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", getContainerData("DEDICATED_ACCOUNT", productIdRequest , "DEDICATED_ACCOUNT", false), session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", getContainerData("OFFER", productIdRequest , "OFFER", false), session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".THRESHOLD_UPDATE_INFORMATION", getContainerData("THRESHOLD_UPDATE_INFORMATION", productIdRequest, "THRESHOLD_UPDATE_INFORMATION", false), session);
                }else{
                    int index = 0;
                    thresholds.put(Integer.toString(index),  getThresholdUpdateInfoBy(productIdRequest));
                    dedicatedAccounts.put(Integer.toString(index), getDedicatedAccountBy(productIdRequest));
                    offers.put(Integer.toString(index), getOffersBy(productIdRequest));
                    List<String> productAlreadyAdded = new ArrayList<String>();
                    productAlreadyAdded.add(productIdRequest);
                    Integer numberProductIdActive = 1;
                    for (Map<String, Object> subscription : anidadosSubscriptionsByGroup) {
                        String productIdSubscription = "";
                        productIdSubscription = (String) subscription.get("PRODUCT_ID");
                        if(productIdRequest.equals(productIdSubscription)){
                            numberProductIdActive++;
                        }
                        if (!productAlreadyAdded.contains(productIdSubscription)) {
                            index++;
                            offers.put(Integer.toString(index), getOffersBy(productIdSubscription));    
                            if(getRequestOperation(session, responseID).equals("cancelSubscription")){
                                dedicatedAccounts.put(Integer.toString(index), getDedicatedAccountBy(productIdSubscription));
                            }
                            
                            productAlreadyAdded.add(productIdSubscription);
                        }
                    }
                    
                    if (isAnidadoAggregateType(productIdRequest))
                    {
                        Map<String, Object> thresholdAggregate = (Map<String, Object>) getThresholdUpdateInfoBy(productIdRequest);
                        Integer valueUpdateThresholdNew = numberProductIdActive * Integer.parseInt((String) thresholdAggregate.get("USAGE_THRESHOLD_VALUE_NEW"));
                        thresholdAggregate.put("USAGE_THRESHOLD_VALUE_NEW", valueUpdateThresholdNew.toString());
                        
                        thresholds.put(ZERO, thresholdAggregate);
                    }else{
                        thresholds.put(ZERO, getThresholdUpdateInfoBy(productIdRequest));
                    }
                    
                    
                    
                    if (useJustThisProductParameters(session, responseID, subscriberId)) {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", getContainerData("DEDICATED_ACCOUNT", productIdRequest , "DEDICATED_ACCOUNT", false), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", getContainerData("OFFER", productIdRequest , "OFFER", false), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".THRESHOLD_UPDATE_INFORMATION", getContainerData("THRESHOLD_UPDATE_INFORMATION", productIdRequest, "THRESHOLD_UPDATE_INFORMATION", false), session);
                    }else{
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", dedicatedAccounts , session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", offers, session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".THRESHOLD_UPDATE_INFORMATION",thresholds, session);
                    }
                }
            }else{
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", getContainerData("DEDICATED_ACCOUNT", productIdRequest , "DEDICATED_ACCOUNT", false), session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", getContainerData("OFFER", productIdRequest , "OFFER", false), session);
            }
       }catch (Exception e) 
       {
           throw createException(SET_DEDICATED_ACCOUNT_AND_OFFERS_TO_AIR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
       }
    }
       
   private String getRequestOperation(Map<String, Object> session, String responseId) throws Exception
   {
       return getDataMapVariables("REQUEST."+responseId+".operation",session);
   }

    @SuppressWarnings("unchecked")
	private Map<String, Object> getOffersBy(String productId) throws Exception {
        return (Map<String, Object>) getContainerData("OFFER", productId, "OFFER", false).get("0");
    }

    @SuppressWarnings("unchecked")
	private Map<String, Object> getDedicatedAccountBy(String productId) throws Exception {
        return (Map<String, Object>) getContainerData("DEDICATED_ACCOUNT", productId , "DEDICATED_ACCOUNT", false).get("0");
    }
    
     @SuppressWarnings("unchecked")
	private Map<String, Object> getThresholdUpdateInfoBy(String productId) throws Exception {
        return (Map<String, Object>) getContainerData("THRESHOLD_UPDATE_INFORMATION", productId , "THRESHOLD_UPDATE_INFORMATION", false).get("0");
    }
    
    private 
    String getAnidadosType(String productId) throws Exception
    {
        try 
        {
            String groupName = getAnidadosGroupId(productId);
            String[] values = groupName.split("\\.");
            return values[1];
        } 
        catch (Exception e) 
        {
            throw createException(IS_ANIDADOS_PRODUCT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
	private String getAnidadosGroupId(String productId) throws Exception{
        try 
        {
            Map<String, Object> groupList = getContainerData("ANIDADOS_GROUP", "ANIDADOS", "ANIDADOS_GROUP", false);
            for (String groupName : groupList.keySet()) {
                Map<String, Object> group = (Map<String, Object>) groupList.get(groupName);
                Map<String, Object> anidadosInfo = getContainerData("ANIDADOS_INFO", (String) group.get("GROUP"), "ANIDADOS_INFO", false);
                for (String anidadoInfo : anidadosInfo.keySet()) {
                    Map<String, Object> anidado = (Map<String, Object>) anidadosInfo.get(anidadoInfo);
                    
                    if (anidado.get("PRODUCT_ID").equals(productId)) {
                        return (String) group.get("GROUP");
                    }
                }
            }
            throw createException(IS_ANIDADOS_PRODUCT,INTERNAL,ERROR_SOURCE_MODULE,"This product ["+productId+"] is not in any anidados group");
        } 
        catch (Exception e) 
        {
            throw createException(IS_ANIDADOS_PRODUCT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    private String getDobleDados(String resquesID, String subscriberID, Map<String,Object>session) throws Exception
    {
        try 
        {
            String productVolume = (String)getDataMapVariables("TABLE."+resquesID+"."+subscriberID+".PRODUCT.FINAL_VOLUME", session);
            
            Double dobleDados = Double.parseDouble(productVolume) * 2;
            DecimalFormat fmt = new DecimalFormat("###");          
            return fmt.format(dobleDados); 
        }
        catch (Exception e) 
        {
            throw createException(GET_BOUBLE_DADOS, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
        
    }
    
    private Object getValueField(Map<String,Object> commandLine,String resquesID, String subscriberID, Map<String,Object> session) throws Exception
    {
        try 
        {    
            String action = (String)commandLine.get("ACTION");
            String value  = (String)commandLine.get("VALUE");
            Object fieldValue = null;
            
            if(action.equals("FUNCTION"))
            {
                if (value.equals("getDobleDados"))
                {
                    fieldValue = getDobleDados(resquesID, subscriberID, session);
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
                return getFieldCompare(value, Integer.parseInt(resquesID), Integer.parseInt(subscriberID), session);
            }
            else
            {
                throw createException(GET_VALUE_FIELD, INTERNAL, ERROR_SOURCE_MODULE, "Action not known");
            }
            
            return fieldValue;
        } 
        catch (Exception e) 
        {
            throw createException(GET_VALUE_FIELD, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private Object instantiateJavaClass(final String javaClassLiteral) throws Exception
    {
        final Matcher m = Pattern.compile("(.+)\\((.+)\\)").matcher(javaClassLiteral);
        m.find();
        return Class.forName(m.group(1)).getConstructor(String.class).newInstance(m.group(2));
    }
    
    private boolean existDataContainer(Map<String, Object> containerData, String key,String entityName, String layout, boolean onlyOne) throws Exception 
    {
        try 
        {                        
            containerData.putAll(getContainerData(entityName, key, layout, onlyOne));
            return true;
        } 
        catch (Exception e) 
        {
            return false;
        }
        
    }
    @SuppressWarnings("unchecked")
    private void setDedicatedAccount(Map<String,Object> dedicatedAccountList, String dedicatedAccountID,String value) throws Exception
     {
         try 
         {
             for(String idx : dedicatedAccountList.keySet())
             {
                 if(dedicatedAccountID.equals((String)((Map<String,Object>)dedicatedAccountList.get(idx)).get("DEDICATED_ACCOUNT_ID")))
                 {
                     ((Map<String,Object>)dedicatedAccountList.get(idx)).put("DEDICATED_ACCOUNT_VALUE", value);
                     return;
                 }
             }
             
             throw createException(SET_ALL_DEDICATED_ACCOUNT,INTERNAL,ERROR_SOURCE_MODULE,"There isn't DedicatedAccountID "+dedicatedAccountID+" in dedicatedAccountList."+dedicatedAccountList.toString());
         } 
         catch (Exception e) 
         {
             throw createException(SET_ALL_DEDICATED_ACCOUNT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
         }
         
     }
 

    @SuppressWarnings({ "unchecked", "unused" })
    private String getProductVsParameterValue(Map<String,Object> productVsParameterList, String parameterKey,String findBy,String returnField) throws Exception
     {
         try 
         {
             for(String idx : productVsParameterList.keySet())
             {
                 if(parameterKey.equals((String)((Map<String,Object>)productVsParameterList.get(idx)).get("findBy")))
                 {
                     return (String)((Map<String,Object>)productVsParameterList.get(idx)).get(returnField);
                 }
             }
              return EMPTY;
         } 
         catch (Exception e) 
         {
             throw createException(SET_ALL_DEDICATED_ACCOUNT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
         }
         
     }

    
 @SuppressWarnings("unchecked")


private void setAccumulator(Map<String,Object> accumulatorList, String accumulatorID,String value) throws Exception
 {
     try 
     {
         for(String idx : accumulatorList.keySet())
         {
             if(accumulatorID.equals((String)((Map<String,Object>)accumulatorList.get(idx)).get("ACCUMULATOR_ID")))
             {
                 ((Map<String,Object>)accumulatorList.get(idx)).put("ACCUMULATOR_VALUE", value);
                 return;
             }
         }
         
         throw createException(SET_ALL_ACCUMULATOR,INTERNAL,ERROR_SOURCE_MODULE,"There isn't AccumulatorID in accumulatorList.");
     } 
     catch (Exception e) 
     {
         throw createException(SET_ALL_ACCUMULATOR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
     }
     
 }
 
@SuppressWarnings("unused")
private String returnCRBN(String deviceType, String value)
{
    if (deviceType.toUpperCase().equals(MODEM)) 
    {
        return value.substring(1);  
    }
    return value;
}
    
    //Verify if is the last subscription active and in this case the status is no_plan else the subscription status is cancelled
    @SuppressWarnings("unchecked")
    private String decideStatusToUpdate(Map<String,Object> subscriptions, String subscriptionId, boolean timeExpiredFlag)
    {
        
        boolean noPlan = true;
       
        for(String idx : (Set<String>)subscriptions.keySet())
        {
            Map<String, Object> subscription  = (Map<String, Object>) subscriptions.get(idx);
            String statusName = (String) subscription.get("STATUS_NAME");
            String subsIdLocal = (String) subscription.get("SUBSCRIPTION_ID");
                
            if(statusName.matches("FULL|RESTRICTED|SUSPENDED") && !subsIdLocal.matches(subscriptionId))
            {
                noPlan = false;
            }
        }
        
        if(noPlan)
        {
            return NO_PLAN;
        }
        
        if(timeExpiredFlag)
        {
            return "TIME_EXPIRED";
        }
        
        return CANCELLED;
    }
    
    @SuppressWarnings("unchecked") 
    private void cancelSubscription(String sessionID, String responseId) throws Exception
    {
        try
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            String productName = (String)getDataMapVariables("REQUEST."+responseId+".productName",session);
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseId+".NUMBER_SUBSCRIBER",session));
            String single = "SINGLE";
            
            if (productName.equals("generalCancellation"))
            {
                for (int subscriberId = 0; subscriberId < numberSubscriber; subscriberId++)
                {
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG",ZERO,session);
                    Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId,session);
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                    
                    String originNode = (String)getDataMapVariables("REQUEST"+DOT+responseId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                    changeItelOriginatingNodeInfo(responseId, subscriberId, originNode, session);
                    originNode = (String)getDataMapVariables("REQUEST"+DOT+responseId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                    
                    if(!fieldExistMapVariablesAndNotNull("REQUEST."+responseId+DOT+subscriberId+".mode", session)){
                    	setDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".mode", single, session);
                    }
                    
                    if (!isSubscriberInOurDatabase(responseId, session, subscriberId))
                    {
                        formatErrorResponse(responseId,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                        break;
                    }
                                            
                    Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session);
                    subscriptions = getActiveSubscriptions(subscriptions);
                    
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".STATUS.statusId",getStatusByName(decideStatusToUpdate(subscriptions,".+",false)),session);

                    //preparing for insert on subscriptionPolicies
                    String subscriberTypeId = (String) getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.SUBSCRIBER_TYPE_ID", session);
                    String deviceTypeId = (String) getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.DEVICE_TYPE_ID", session);
                    
                    setSapcGroupId(getSubscriberType(subscriberTypeId),getDeviceType(deviceTypeId),responseId,subscriberId,session);
                    
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(FULL), ZERO,subscriberTypeId,deviceTypeId),session);
                    String policyName = (String)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".POLICY.name", session);
                    
                    validationAllRequestParameter("cancelSubscription",responseId,String.valueOf(subscriberId),session);
                    
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG",ZERO,session);

                    if(subscriber.containsKey("programmedCancellationDate"))
                    {
                        if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedCancellationDate"))))
                        {
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG","1",session);
                            continue;
                        }
                    }
                    
                    if (thereIsActiveSubscriptions(subscriptions))
                    {
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".OFFER", getAllContainerParameters(subscriptions,"OFFER"), session);
                        findMainIDRange((String)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseId), subscriberId, session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".DEDICATED_ACCOUNT", getAllContainerParameters(subscriptions, "DEDICATED_ACCOUNT"), session);
                    }
                    else
                    {
                        throw createException(THERE_IS_NOT_SUBSCRIPTION,INTERNAL,ERROR_SOURCE_MODULE,"There is not active subscriptions");
                        //setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", new java.util.concurrent.ConcurrentHashMap<String,Object>(), session);
                        //setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", new java.util.concurrent.ConcurrentHashMap<String,Object>(), session);
                    }
                    
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".SET_QOS_SAPC","1",session);
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_DOWNLOAD",ZERO,session);
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_UPLOAD",ZERO,session);
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.STYPE", getStype((String)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session)), session);
                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.CRBN"       , policyName, session);                
                }
            }
            else
            {
                for (int subscriberId = 0; subscriberId < numberSubscriber; subscriberId++)
                {
                    String cancelType = (String)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".cancelType", session);
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG",ZERO,session);
                    Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId,session);
                    setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                    
                    Map<String,Object> subscriptions = new java.util.concurrent.ConcurrentHashMap<String,Object>();                 
                    Map<String,Object> subscription  = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                    String policyName = null;
                    Map<String,Object> product = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                    Map<String, Object> initialThreshold = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                    
                    String originNode = (String)getDataMapVariables("REQUEST"+DOT+responseId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                    changeItelOriginatingNodeInfo(responseId, subscriberId, originNode, session);
                    originNode = (String)getDataMapVariables("REQUEST"+DOT+responseId+DOT+subscriberId+DOT+"originatingNodeInfo", session);
                    
                    if(cancelType.matches("active|recurrence"))
                    {
                         if (!isSubscriberInOurDatabase(responseId, session, subscriberId))
                        {
                            formatErrorResponse(responseId,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                            break;
                        }

                        if (!isSubscriberHasActiveSubscriptionWithThisProduct(responseId, session, subscriberId))
                        {
                            formatErrorResponse(responseId,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+productName+DOT);
                            break;
                        }
                        
                        subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session);
                        subscriptions = getActiveSubscriptions(subscriptions);
                        String restrictions = iWontCancelSubscriptionsWithTheseRestrictions();
                        subscription  = getSubscriptionWithThisProductName(productName,subscriptions,true,restrictions);
                        findMainIDRange((String)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseId), subscriberId, session);
                        String regionId = getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID_RANGES.REGION_ID", session);
                        String requestProductId = getProductIdByName(productName, regionId);
                        
                        String fieldNestedCancelType = "REQUEST."+responseId+DOT+subscriberId+".nested";
                        String fieldInternalNestedCancelType = "INTERNAL."+responseId+DOT+subscriberId+".TYPE_CANCEL";
                        
                        String nestedCancelType = getNestedCancelType(fieldNestedCancelType, session);
                        
                        setNestedCancelTypeInMapVariables(session, fieldNestedCancelType, fieldInternalNestedCancelType);

                        String externalData = getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".externalData", session);
                        
                        if (isAnidadosProduct(requestProductId)){
                        	//when a request is coming from sdp and group is aggregate the cancel flow is normal and not anidados.
                        	if(externalData.equals("SDP") && isAnidadoAggregateType(requestProductId)){
                        		setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".IS_ANIDADO",ZERO,session);
                        	}
                        	else{
                        		setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".IS_ANIDADO","1",session);
                        	}
                            	
                        	 if (isAnidadoEnqueueType(requestProductId) &&  (nestedCancelType.equals("first") || nestedCancelType.equals("all"))){

                                 //validating if there is a subscription enqueue to be cancelled
                                   Map<String,Object> anidadosSubscriptions = getActiveAndQueuePendingSubscriptions((Map<String, Object>) getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIPTIONS", session));
                                   subscription = getSubscriptionWithThisProductName(productName,anidadosSubscriptions,true,FULL);
                                   
                                   if(subscription.isEmpty()){
                                       formatErrorResponse(responseId,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no queued subscription to the product ["+productName+"]"+DOT);
                                       continue;
                                   }
                                   //end validating

                                   //setting variables necessary and rules to go just on database because enqueue type is not active on sdp.
                                   setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(CANCELLED), "null", "null", "null"), session);
                                   setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".STATUS.statusId",getStatusByName(CANCELLED), session);
                                   
                                   setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".SUBSCRIPTION.PRODUCT_ID",requestProductId,session);
                                   setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".SUBSCRIPTION.SUBSCRIPTION_ID",null,session);
                                   
                                   setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".ONLY_DATABASE","1",session);
                                   
                                   break;   
                            }else{
                                setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".ONLY_DATABASE",ZERO,session);
                            }
                        }else{
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".ONLY_DATABASE",ZERO,session);
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".IS_ANIDADO",ZERO,session);
                        }
                        
                        
                        if(subscription.isEmpty())
                        {
                            formatErrorResponse(responseId,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+productName+DOT);
                            continue;
                        }                      

                        
                        String statuses = FULL+PIPE+RESTRICTED+PIPE+PIPE+SUSPENDED;
                        
                        if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                            formatErrorResponse(responseId, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                        }
                        
                        product = getProductByID((String)subscription.get("PRODUCT_ID"));
                        
                        boolean timeExpiredFlag = false;
                        
                        if(fieldExistMapVariablesAndNotNull("REQUEST."+responseId+DOT+subscriberId+".timeExpiredFlag", session))
                        {
                            String timeExpired = (String)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".timeExpiredFlag", session);
                            if(timeExpired.equals("true"))
                            {
                                timeExpiredFlag = true;
                            }
                        }
                        
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".STATUS.statusId",getStatusByName(decideStatusToUpdate(subscriptions,((String)subscription.get("SUBSCRIPTION_ID")),timeExpiredFlag)),session);
                       
                      //preparing for insert on subscriptionPolicies
                        String subscriberTypeId = (String) getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.SUBSCRIBER_TYPE_ID", session);
                        String deviceTypeId = (String) getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.DEVICE_TYPE_ID", session);
                        
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(decideStatusToUpdate(subscriptions,((String)subscription.get("SUBSCRIPTION_ID")),timeExpiredFlag)), ZERO, subscriberTypeId, deviceTypeId),session);
                        policyName = (String)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".POLICY.name", session);
                        setSapcGroupId(getSubscriberType(subscriberTypeId),getDeviceType(deviceTypeId),responseId,subscriberId,session);
                    }
                    
                                        
                    if(cancelType.equals("active"))
                    {
                       
                        
                        findMainIDRange((String)getDataMapVariables("REQUEST."+responseId+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseId), subscriberId, session);

                       
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".PRODUCT",product,session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".SUBSCRIPTION",subscription,session);
                        validationAllRequestParameter((String)product.get("categoryID"),"cancelSubscription",responseId,String.valueOf(subscriberId),session);

                        if(subscriber.containsKey("programmedCancellationDate"))
                        {
                            if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedCancellationDate"))))
                            {
                                setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG","1",session);
                                continue;
                            }
                        }
                        
                        String productPriority = getProductPriority((String)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".PRODUCT.productProperties",session),session);
                        String productID = (String) product.get("productID");

                        setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".SET_QOS_SAPC",ZERO,session);
                        if (productName.startsWith("Elegido"))
                        {
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".IS_ELEGIDO","1",session);                                
                        } else {
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".IS_ELEGIDO",ZERO,session);                                
                        }

                        String fieldNestedCancelType = "REQUEST."+responseId+DOT+subscriberId+".nested";
                        String fieldInternalNestedCancelType = "INTERNAL."+responseId+DOT+subscriberId+".TYPE_CANCEL";
                        
                        setNestedCancelTypeInMapVariables(session, fieldNestedCancelType, fieldInternalNestedCancelType);

                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_DOWNLOAD",ZERO,session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_UPLOAD",ZERO,session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.STYPE", getStype((String)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session)), session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.CRBN"       , policyName, session);
                        
                        if (!(isThereProductWithGreatPriority(productPriority,(Map<String,Object>)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),session,dateTimeExecution)))
                        {
                            
                            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".SET_QOS_SAPC","1",session);
                            
                            Map<String,Object> subscriptionWithImmediateLessPriority = getSubscriptionWithProductImmediateLessPriority(productPriority,(Map<String,Object>)getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),session);
                            
                            if(!(subscriptionWithImmediateLessPriority.isEmpty()))
                            {
                                setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_DOWNLOAD",(String)(subscriptionWithImmediateLessPriority.get("QOS_DOWNLOAD")),session);
                                setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_UPLOAD",(String)(subscriptionWithImmediateLessPriority.get("QOS_UPLOAD")),session);
                            }
                            else
                            {
                                if(isDailyPlan((String) product.get("categoryName")))
                                {
                                     initialThreshold = getInitialThresholdsByProductID(productID, dateTimeExecution);
                                     setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_DOWNLOAD",(String)initialThreshold.get("QOS_DOWNLOAD"),session);
                                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_UPLOAD",(String)initialThreshold.get("QOS_UPLOAD"),session);
                                 }
                                else
                                {
                                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_DOWNLOAD",ZERO,session);
                                    setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".QOS.QOS_UPLOAD",ZERO,session);
                                }
                            }
                        }
                        
                        setDedicatedAccountAndOffersToAir(responseId, session, product, subscriberId, subscriptions);
                        
                        
                        
                        //HashMap<String,Object> accumulators = getContainerData("ACCUMULATOR", (String)subscription.get("PRODUCT_ID"), "ACCUMULATOR", false);
                        Map<String,Object> dedicatedAccounts = getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".DEDICATED_ACCOUNT", session); 
                        Map<String,Object> offers = getDataMapVariables("TABLE."+responseId+DOT+subscriberId+".OFFER", session);
                        
                         if (!isAnidadosProduct(productID)) {
                             //removeElementThatIsBeingUsedByOthersSubscriptions(accumulators,"ACCUMULATOR",subscriptions,subscription);
                            removeElementThatIsBeingUsedByOthersSubscriptions(dedicatedAccounts,"DEDICATED_ACCOUNT",subscriptions,    subscription);
                            removeElementThatIsBeingUsedByOthersSubscriptions(offers,"OFFER",subscriptions,subscription);
                        } 
                        
                        //setDataMapVariables("TABLE."+responseID+"."+i+".ACCUMULATOR", accumulators, session);
                        
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".DEDICATED_ACCOUNT", dedicatedAccounts, session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".OFFER", offers, session);
                        
                        
                        
                    }
                    else if(cancelType.equals("recurrence"))
                    {
                        
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".CANCEL_SUBSCRIPTION",subscription,session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".PRODUCT",product,session);
                        setDataMapVariables("TABLE."+responseId+DOT+subscriberId+".SUBSCRIPTION",subscription,session);
                        
                        if(subscriber.containsKey("programmedCancellationDate"))
                        {
                            if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedCancellationDate"))))
                            {
                                setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG","1",session);
                                continue;
                            }
                        }
                    }
                    else if(cancelType.equals("programmed"))
                    {
                        setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG",ZERO,session);
                        
                        if(subscriber.containsKey("programmedCancellationDate"))
                        {
                            if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedCancellationDate"))))
                            {
                                setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".PROGRAMMED_FLAG","1",session);
                            }
                        }
                    }
                    else
                    {
                        formatErrorResponse(responseId,session,CANCEL_TYPE_INVALID,"business",ERROR_SOURCE_MODULE,"Cancel type invalid");
                        break;
                    }
                    
                }   
            }
        }
        catch (Exception error) 
        {
            String errorCode        = CANCEL_SUBSCRIPTION;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseId,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    private String getProductIdByName(String productName, String regionId) throws Exception {
        Map<String,Object> product = getContainerData("PRODUCTS_BY_NAME", productName + DOT + regionId, "PRODUCTS_BY_NAME", true);
        
        return (String) product.get("PRODUCT_ID");
    }
    
    private void verifyIsOnlyForCharge(String responseId,
            Map<String, Object> session, int subscriberId,
            Map<String, Object> subscriptions, String productId) throws Exception {
        String groupId = getAnidadosGroupId(productId);
        List<Map<String, Object>> allAnidadosSubscriptions = getAnidadosSubscriptions(getActiveSubscriptions(subscriptions));
        List<Map<String, Object>> anidadosSubscriptionsByGroup = getAnidadosSubscriptionsByGroup(allAnidadosSubscriptions, groupId);
        if(thereIsAnyAnidadoSubscriptionEqueue(anidadosSubscriptionsByGroup) && isOnlyCharge(session, responseId, subscriberId))
        {
            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".ENQUEUE","1",session);
        }else{
            setDataMapVariables("INTERNAL."+responseId+DOT+subscriberId+".ENQUEUE",ZERO,session);
        }
    }
    
    private boolean isOnlyCharge(Map<String, Object> session,String responseID, int subscriberId) throws Exception {
        String externalData = getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".externalData", session);
        
        return !externalData.equals("ANIDADOS");
    }

    private boolean thereIsAnyAnidadoSubscriptionEqueue(
            List<Map<String, Object>> anidadosSubscriptions) throws Exception {
        
        for (Map<String, Object> anidadoSubscription : anidadosSubscriptions) {
            String productId = (String) anidadoSubscription.get("PRODUCT_ID"); 
            if (isAnidadoEnqueueType(productId)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isAnidadoEnqueueType(String productID) throws Exception {
        return getAnidadosType(productID).equals("Enqueue");
    }
    
    private boolean isAnidadoAggregateType(String productID) throws Exception {
        return getAnidadosType(productID).equals("Aggregate");
    }        

    private boolean isElegido(String productName) {
        return productName.startsWith("Elegido");
    }
    
    private String setNestedCancelTypeInMapVariables(
            Map<String, Object> session, String fieldNestedCancelType,
            String fieldInternalNestedCancelType) throws Exception {
        String nestedCancelType;
        
        nestedCancelType = getNestedCancelType(fieldNestedCancelType, session);
        
        setDataMapVariables(fieldInternalNestedCancelType,nestedCancelType,session);
        return nestedCancelType;
    }
    

    private String getNestedCancelType(String fieldNestedCancelType, Map<String, Object> session)
            throws Exception {
        String nestedCancelType;
        if(fieldExistMapVariables(fieldNestedCancelType, session)){
            nestedCancelType = getDataMapVariables(fieldNestedCancelType, session);
        }else{
            nestedCancelType = "active";
        }
        return nestedCancelType;
    }

    
   private boolean thereIsActiveSubscriptions(Map<String, Object> subscriptions) {
        return !subscriptions.isEmpty();
    }

private Map<String, Object> getActiveSubscriptions(Map<String, Object> subscriptions) {
            Map<String, Object> active = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            for(String index : subscriptions.keySet()){
                @SuppressWarnings("unchecked")
                Map<String, Object> subscription = (Map<String, Object>) subscriptions.get(index);
                String status = (String) subscription.get("STATUS_NAME");
                if(status.equals(FULL) || status.equals(RESTRICTED)){
                    active.put(index, subscription);
            }
        }
        return active;
    }

    private String iWontCancelSubscriptionsWithTheseRestrictions()
    {
        return CANCELLED+PIPE+NO_PLAN+PIPE+TIME_EXPIRED+PIPE+VOLUME_EXPIRED;
    }

    private boolean isSubscriberHasActiveSubscriptionWithThisProduct(String responseID, Map<String, Object> session, int i) throws Exception
    {
        return fieldExistMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session);
    }

    private boolean isSubscriberInOurDatabase(String responseID, Map<String, Object> session, int i) throws Exception
    {
        return fieldExistMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIBER",session);
    }

    private void setSapcGroupId(String subscriberType, String deviceType, String responseID, int i, Map<String, Object> session) throws Exception
    {
        try
        {
            String groupId = EMPTY;
            
            if(deviceType.equals(HANDSET))
            {
                groupId = "HS_";
            }
            else if(deviceType.equals(MODEM))
            {
                groupId = "BL_";
            }
            else
            {
                throw createException(SET_SAPC_GROUP_ID,"business",ERROR_SOURCE_MODULE,"deviceType "+deviceType+" does not have groupId mapped.");
            }
            
            if(subscriberType.equals(PREPAID))
            {
                groupId = groupId+"PRE";
            }
            else if(subscriberType.equals(POSTPAID))
            {
                groupId = groupId+"POS";
            }
                            else if(subscriberType.equals(CONTROLLED))
            {
                groupId = groupId+"CONTROLE";
            }
            else
            {
                throw createException(SET_SAPC_GROUP_ID,"business",ERROR_SOURCE_MODULE,"subscriberType "+subscriberType+" does not have groupId mappe.");
            }
            
            setDataMapVariables("TABLE."+responseID+DOT+i+".QOS.groupId", groupId, session);
            
        }
        catch (Exception error)
        {
            formatErrorResponse(responseID, session, SET_SAPC_GROUP_ID, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(error));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void suspendSubscription(String sessionID, String responseID) throws Exception
    {
        
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".PROGRAMMED_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                
                if (!isSubscriberInOurDatabase(responseID, session, i))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                if (!fieldExistMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription.");
                    break;
                }
                
                validationAllRequestParameter("suspendSubscription",responseID,String.valueOf(i),session);
                
                Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session);
                String productName = (String)getDataMapVariables("REQUEST."+responseID+".productName", session);

                Map<String,Object> subscription = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                Map<String,Object> product = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                if(isForTheAllElegidos(productName))
                {    
                    
                    setDataMapVariables("TABLE."+responseID+DOT+i+".SUBSCRIPTION.SUBSCRIPTION_ID",null,session);
                }
                else
                {
                    subscription = getSubscriptionWithThisProductName(productName,subscriptions,true,iWontCancelSubscriptionsWithTheseRestrictions());

                    if(subscription.isEmpty())
                    {
                        formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+productName+DOT);
                        break;
                    }
                    product = getProductByID((String)subscription.get("PRODUCT_ID"));
                    setDataMapVariables("TABLE."+responseID+DOT+i+".PRODUCT",product,session);
                    setDataMapVariables("TABLE."+responseID+DOT+i+".SUBSCRIPTION",subscription,session);
                    setDataMapVariables("ACTION."+responseID+DOT+i+".MSG_DATA.SUBSCRIPTION_ID", (String)subscription.get("SUBSCRIPTION_ID"), session);

                    String statuses = FULL+PIPE+RESTRICTED;
                    
                    if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                        formatErrorResponse(responseID, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                    }
                }
                
                

                Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+DOT+i,session);
               
                if(subscriber.containsKey("programmedSuspensionDate"))
                {
               
                    if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedSuspensionDate"))))
                    {
               
                        setDataMapVariables("INTERNAL."+responseID+DOT+i+".PROGRAMMED_FLAG","1",session);
                    }
                }
                
            }
            
        } 
        catch (Exception error) 
        {
            
            String errorCode        = SUSPEND_SUBSCRIPTION;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
            
        }
    }

    
    @SuppressWarnings("unchecked")
    private void reactivateSubscription(String sessionID, String responseID) throws Exception
    {    
        
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                        
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".PROGRAMMED_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                
                if (!isSubscriberInOurDatabase(responseID, session, i))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                if (!fieldExistMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"The subscriber hasn't subscriptions.");
                    break;
                }
                
                validationAllRequestParameter("reactivateSubscription",responseID,String.valueOf(i),session);
                
                Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session);
                String productName = (String)getDataMapVariables("REQUEST."+responseID+".productName", session);

                Map<String,Object> subscription = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                Map<String,Object> product = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                if(isForTheAllElegidos(productName))
                {    
                    setDataMapVariables("TABLE."+responseID+DOT+i+".SUBSCRIPTION.SUBSCRIPTION_ID",null,session);
                }
                else
                {
                        subscription = getSubscriptionWithThisProductName(productName,subscriptions,true,iWontCancelSubscriptionsWithTheseRestrictions());
                    if(subscription.isEmpty())
                    {
                        formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+productName+DOT);

                        break;
                    }
                
               
                    product = getProductByID((String)subscription.get("PRODUCT_ID"));
                    setDataMapVariables("TABLE."+responseID+DOT+i+".PRODUCT",product,session);
                    setDataMapVariables("TABLE."+responseID+DOT+i+".SUBSCRIPTION",subscription,session);
                    setDataMapVariables("ACTION."+responseID+DOT+i+".MSG_DATA.SUBSCRIPTION_ID", (String)subscription.get("SUBSCRIPTION_ID"), session);
                
            String statuses = SUSPENDED;
                    if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                        formatErrorResponse(responseID, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                    }
                }   
                Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+DOT+i,session);
                
                if(subscriber.containsKey("programmedActivationDate"))
                {
                    if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedActivationDate"))))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+i+".PROGRAMMED_FLAG","1",session);
                    }
                }
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = REACTIVATE_SUBSCRIPTION;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void reSubscription (String sessionID, String responseID) throws Exception
    {
        try
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            for (int subscriberId = 0; subscriberId < numberSubscriber; subscriberId++)
            {
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_QOS_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC",ZERO,session);
                
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                String productID = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".productID", session);
                
                Map<String,Object> product = getProductByID(productID);
                
                if(product.isEmpty())
                {
                    throw createException(PRODUCT_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Product not found");
                }
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT", product, session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", product.get("productVolume"), session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_TIME", product.get("productTime"), session);
                
                if (!fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                if (!fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"The subscriber hasn't subscriptions.");
                    break;
                }
                
                Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session);
                Map<String,Object> subscription = getSubscriptionWithThisProductName((String)product.get("productName"),subscriptions,true, iWontCancelSubscriptionsWithTheseRestrictions());
                //IF SUBSCRIPTION IS SUSPENDED THE OPERARTION RESUBSCRIPTIONS IS NOT ALLOWED
                if(subscription.isEmpty())
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+(String)product.get("productName")+DOT);
                    break;
                }
                
                String statuses = FULL+PIPE+RESTRICTED+PIPE+VOLUME_EXPIRED+PIPE+TIME_EXPIRED;
                if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                    formatErrorResponse(responseID, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                }
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION", subscription, session);
                findMainIDRange((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseID), subscriberId, session);
                Map<String,Object> productVSparameter = getProductParameter(sessionID,productID);
                
                if (!(productVSparameter.isEmpty()))
                {
                    
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT_VS_PARAMETER",productVSparameter,session);
                }
                String mainID = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".MAINID",session);
                String imei = ZERO;
                
                if(fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".IMEI",session))
                {
                    imei = (String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".IMEI",session);
                }                    
                String promotionID = (String)product.get("promotionID");
                
                if(!fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_ID", session))
                {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_ID", "null", session);
                }
                
                if(!fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES", session))
                {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES", "null", session);
                }

                Map<String,Object> promotion = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                Boolean applyPromotion = false;
                
                if (promotionID != null && !(promotionID.isEmpty()))
                {
                    String mainIDRangeID = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID_RANGES.MAINID_RANGE_ID", session);
                    promotion = getPromotionByID((String)product.get("promotionID"),mainID,mainIDRangeID,imei,session);

                    if (!promotion.isEmpty())
                    {
                        applyPromotion = true;
                        if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES", session))
                        {
                            int promotionUses = Integer.parseInt((String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES", session));
                            //DECREMENT THE LEFT USES OF PROMOTION
                            if(promotionUses > 0)
                            {
                                promotionUses--;
                                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES",String.valueOf(promotionUses),session);
                            }
                            //PROMOTION IS ALREADY USE THE MAX TIMES
                            else
                            {
                                applyPromotion = false;
                            }
                        }
                        //FIRST TIME USING THE PROMOTION
                        else
                        {
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.PROMOTION_USES",(String)promotion.get("MAX_USES"),session);
                        }
                        
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.",promotion,session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PROMOTION.promotionID",(String)product.get("promotionID"),session);
                        
                        //if product is limited by time
                        if(isValidField(product, "productTime") && applyPromotion)
                        {
                            applyPromotionTime(product, promotion, Integer.parseInt(responseID), subscriberId, session);
                        }
                        //if product is limited by volume
                        if(isValidField(product, "productVolume") && applyPromotion)
                        {
                            applyPromotionVolume(product, promotion, Integer.parseInt(responseID), subscriberId, session);
                        }
                    }
                }
                
                calculateFinalPrice(product, promotion, Integer.parseInt(responseID), subscriberId, session,applyPromotion);
                
                if (((String)product.get("categoryName")).matches("Shared Plan|Chuveirinho"))
                {
                    if ((fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberRole", session) && (((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".subscriberRole", session)).equals("dependant"))) || (fieldExistMapVariables("REQUEST."+responseID+DOT+subscriberId+".masterSubscriberMAINID", session)))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".mainIDDependant", "1", session);
                    }
                    if(((String)product.get("categoryName")).equals("Chuveirinho"))
                    {
                        addMasterPrice(Integer.parseInt(responseID), subscriberId, session);
                    }
                }
                
                
               setDedicatedAccountAndOffersToAir(responseID, session,
                        product, subscriberId, subscriptions);
                
                if (((String)product.get("categoryName")).matches("Shared Plan"))
                {
                    if(isValidField(productVSparameter, "AIR_VOLUME_DEDICATED_ACCOUNT"))
                    {
                        String airVolumeDedicatedAccountID = (String)productVSparameter.get("AIR_VOLUME_DEDICATED_ACCOUNT");
                        setAccumulator((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", session), airVolumeDedicatedAccountID, ZERO);
                    }
                }

                
                if(isPresentProperty((String)product.get("productProperties"), "BILLING_CYCLE",session))                        
                {
                    String nextBillingCycleDate = nextBillingCycleDate((String) subscription.get("END_TIMESTAMP"));
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP",nextBillingCycleDate,session);
                }
                else
                {
                    if(((String)product.get("productTime")).isEmpty())
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", "null", session);
                    }
                    else
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION.END_TIMESTAMP", addHourInDateTime((String)product.get("productTime"),dateTimeExecution), session); 
                    }
                    
                }
                
                String productPriority = getProductPriority((String)product.get("productProperties"),session);
                Map<String,Object> thresholds = getInitialThresholdsByProductID(productID,dateTimeExecution);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".STATUS.statusId",getStatusByName(FULL),session);
                
                //preparing for insert on subscriptionPolicies
                String subscriberType = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session);
                String deviceType = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.deviceType",session);
                String thresholdId = null;
                setSapcGroupId(subscriberType,deviceType,responseID,subscriberId,session);
                
                if(!thresholds.isEmpty())
                {
                    thresholdId = (String)thresholds.get("THRESHOLD_ID");
                }
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(FULL), thresholdId, getSubscriberTypeId(subscriberType), getDeviceTypeId(deviceType)),session);
                String policyName = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY.name", session);
                
                if(!(thresholds.isEmpty()))
                {
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD", (String)thresholds.get("QOS_DOWNLOAD"), session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD"  , (String)thresholds.get("QOS_UPLOAD"), session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.STYPE", getStype((String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session)), session);
                    setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.CRBN"       , policyName, session);                    
                    setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".INSERT_QOS_FLAG","1",session);
                }
                
                if (!(isThereProductWithGreatPriority(productPriority,(Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),session,dateTimeExecution)))
                {
                    setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC","1",session);
                }
                
                
                //----------------------------------------------------------------
                // set volume for AIR in dedicated_account field
                //----------------------------------------------------------------
                
                if(isValidField(productVSparameter, "AIR_VOLUME_DEDICATED_ACCOUNT"))
                {
                    String airVolumeDedicatedAccountID = (String)productVSparameter.get("AIR_VOLUME_DEDICATED_ACCOUNT");
                    if (((String)product.get("categoryName")).matches("Shared Plan"))
                    {
                        setDedicatedAccount((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", session), airVolumeDedicatedAccountID, ZERO);
                    }
                    else
                    {
                        if(fieldExistMapVariablesAndNotNull("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", session))
                        {
                            String airVolume = (String) getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_VOLUME", session);
                            setDedicatedAccount((Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", session), airVolumeDedicatedAccountID, airVolume);
                        }
                    }
                }
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".STATUS.statusId",getStatusByName(FULL),session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".THRESHOLD.thresholdId",thresholdId,session);
                
                String finalPrice = getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT.FINAL_PRICE", session);
                setFinalPrice(session, responseID, String.valueOf(subscriberId), finalPrice);
            }
        }
        catch (Exception error) 
        {
            String errorCode        = RE_SUBSCRIPTION;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void changeSubscriberDetails(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                validationAllRequestParameter("changeSubscriberDetails",responseID,i+EMPTY,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                if (!isSubscriberInOurDatabase(responseID, session, i))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                formatChangeSubscriberDetailsData(sessionID,  responseID);
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = CHANGE_SUBSCRIBER_DETAILS;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void thresholdVolume(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            String productName = (String)getDataMapVariables("REQUEST."+responseID+".productName", session);
            String statusSubscription = null;
            
            for (int subscriberId = 0; subscriberId < numberSubscriber; subscriberId++)
            {
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".CANCEL_FLAG", ZERO, session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".QOS_FLAG", ZERO, session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".SET_QOS_SAPC", ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                
                findMainIDRange((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".MAINID", session), Integer.parseInt(responseID), subscriberId, session);
                
                if (!isSubscriberInOurDatabase(responseID, session, subscriberId))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                if (!fieldExistMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"The subscriber hasn't subscriptions.");
                    break;
                }
                
                Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session);
                
                Map<String,Object> subscription = getOneSubscription(productName,subscriptions,true,INACTIVE_STATUS);
                
                if(subscription.isEmpty())
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+productName+DOT);
                    break;
                }
                
                String statuses = FULL+PIPE+RESTRICTED;
                if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                    formatErrorResponse(responseID, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                }
                
                Map<String,Object> product = getProductByID((String)subscription.get("PRODUCT_ID"));
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".PRODUCT",product,session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".SUBSCRIPTION",subscription,session);
                statusSubscription = RESTRICTED;
                
                String productVolume = (String)product.get("productVolume");
                long thresholdVolume = Long.parseLong((String)getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".thresholdVolume", session));
                
                String sProductVolume = (String)product.get("productVolume");
                Long lProductVolume = null;
                
                if(sProductVolume != null && !sProductVolume.isEmpty())
                {
                    lProductVolume = Long.parseLong((String)product.get("productVolume"));
                }
                
                //preparing for insert on subscriptionPolicies
                String subscriberType = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session);
                String deviceType = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.deviceType",session);
                String thresholdId = null;

                Map<String,Object> threshold = getLimitThresholdsByVolume(thresholdVolume,(String)subscription.get("PRODUCT_ID"), dateTimeExecution);
                thresholdId = (String)threshold.get("THRESHOLD_ID");
                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".THRESHOLD", threshold, session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.STYPE", getStype((String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIBER.subscriberType", session)), session);
                setSapcGroupId(subscriberType,deviceType,responseID,subscriberId,session);
                
                if((((String)threshold.get("QOS_DOWNLOAD")) != null && ((String)threshold.get("QOS_DOWNLOAD")).matches("[0-9]+")) || (((String)threshold.get("QOS_UPLOAD")) != null && ((String)threshold.get("QOS_UPLOAD")).matches("[0-9]+")))
                {
                    setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".QOS_FLAG", "1", session);
                    
                    if( lProductVolume != null && productVolume.matches("[0-9]+") && (thresholdVolume >= lProductVolume))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+subscriberId+".CANCEL_FLAG", "1", session);
                        
                        Map<String,Object> subscriptionWithImmediateLessPriority = getSubscriptionWithProductImmediateLessPriority((String)product.get("productProperties"),(Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".MAINID.SUBSCRIPTIONS",session),session);
                        
                        if(subscriptionWithImmediateLessPriority != null && !subscriptionWithImmediateLessPriority.isEmpty())
                        {
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD",(String)(subscriptionWithImmediateLessPriority.get("QOS_DOWNLOAD")),session);
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD",(String)(subscriptionWithImmediateLessPriority.get("QOS_UPLOAD")),session);
                        }
                        else
                        {
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD",ZERO,session);
                            setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD",ZERO,session);
                        }
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".CANCEL_SUBSCRIPTION",subscription,session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".DEDICATED_ACCOUNT", getContainerData("DEDICATED_ACCOUNT", (String)subscription.get("PRODUCT_ID"), "DEDICATED_ACCOUNT", false), session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".OFFER", getContainerData("OFFER", (String)subscription.get("PRODUCT_ID"), "OFFER", false), session);
                        statusSubscription = VOLUME_EXPIRED;
                    }
                    else
                    {
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_DOWNLOAD",(String)threshold.get("QOS_DOWNLOAD"),session);
                        setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.QOS_UPLOAD",(String)threshold.get("QOS_UPLOAD"),session);
                    }
                }

                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".STATUS.statusId",getStatusByName(statusSubscription),session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY",getPolicieByStatus(getStatusByName(statusSubscription), thresholdId, getSubscriberTypeId(subscriberType), getDeviceTypeId(deviceType)),session);
                String policyName = (String)getDataMapVariables("TABLE."+responseID+DOT+subscriberId+".POLICY.name", session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberId+".QOS.CRBN", policyName, session);
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = THRESHOLD_VOLUME;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    @SuppressWarnings({ "unchecked" })
    private void subscribeCampaign(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                validationAllRequestParameter("subscribeCampaign",responseID,i+"",session);
                setDataMapVariables("INTERNAL."+responseID+"."+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                setDataMapVariables("INTERNAL."+responseID+"."+i+".PROGRAMMED_FLAG","0",session);
                
                Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+"."+i,session);
                
                if(subscriber.containsKey("programmedActivationDate"))
                {
                    if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedActivationDate"))))
                    {
                        setDataMapVariables("INTERNAL."+responseID+"."+i+".PROGRAMMED_FLAG","1",session);
                        continue;
                    }
                }
                
                if (isSubscriberInOurDatabase(responseID, session, i))
                {
                    String mainIDCampaign = (String)getDataMapVariables("TABLE."+responseID+"."+i+".MAINID.SUBSCRIBER.CAMPAIGN_ID", session);
                    if(mainIDCampaign != null && !mainIDCampaign.isEmpty())
                    {
                        formatErrorResponse(responseID,session,MAINID_HAS_CAMPAIGN,"business",ERROR_SOURCE_MODULE,"Subscriber already has a campaign.");
                        break;
                    }
                }
                else
                {
                    formatErrorResponse(responseID,session,MAINID_IS_NOT_DATABASE,"business",ERROR_SOURCE_MODULE,"Subscriber is not in dth base.");
                    break;
                }
                
                String campaignName = (String)getDataMapVariables("REQUEST."+responseID+".productName", session);
                Map<String,Object> campaign = getContainerData("CAMPAIGN_BY_NAME", campaignName, "CAMPAIGN_BY_NAME", true);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN", campaign, session);
                
                int period             = Integer.parseInt((String)campaign.get("PERIOD"));
                String periodType     = (String)campaign.get("PERIOD_TYPE");
                int interval           = Integer.parseInt((String)campaign.get("INTERVAL"));
                String billingCycleDate = null;
                
                if(fieldExistMapVariablesAndNotNull("REQUEST."+responseID+"."+i+".billingCycleDate", session))
                {
                    billingCycleDate = (String)getDataMapVariables("REQUEST."+responseID+"."+i+".billingCycleDate", session);
                }
                
                Map<String,Object> periodData = getStartNextPeriodCampaing(dateTimeExecution, periodType, interval, billingCycleDate, dateTimeExecution, dateTimeExecution);
                
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.CAMPAIGN_ID"           , (String)campaign.get("CAMPAIGN_ID"), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.PERIOD"                , String.valueOf(period), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.PERIOD_TYPE"           , (String)campaign.get("PERIOD_TYPE"), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.USES_IN_PERIOD"        , (String)campaign.get("MAX_USES_IN_PERIOD"), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.START_NEXT_PERIOD"     , (String)periodData.get("START_NEXT_PERIOD_CAMPAIGN"), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.END_CURRENT_PERIOD"     , (String)periodData.get("END_CURRENT_PERIOD_CAMPAIGN"), session);
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.START_TIMESTAMP"       , dateTimeExecution, session);
                
                
                Map<String,Object> lastPeriodCampaign = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                
                lastPeriodCampaign.put("START_NEXT_PERIOD_CAMPAIGN", dateTimeExecution);
                lastPeriodCampaign.put("END_CURRENT_PERIOD_CAMPAIGN", "");
                
                for(int p = 0; p < period ; p++)
                {
                    lastPeriodCampaign = getStartNextPeriodCampaing(dateTimeExecution, periodType, interval, billingCycleDate, (String)lastPeriodCampaign.get("START_NEXT_PERIOD_CAMPAIGN"),(String)lastPeriodCampaign.get("START_NEXT_PERIOD_CAMPAIGN"));
                    billingCycleDate = null;
                }
                
                setDataMapVariables("TABLE."+responseID+"."+i+".CAMPAIGN_USAGE.END_TIMESTAMP" , (String)lastPeriodCampaign.get("END_CURRENT_PERIOD_CAMPAIGN"), session);
                
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = SUBSCRIBE_CAMPAIGN;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    @SuppressWarnings({ "unchecked" })
    private void unSubscribeCampaign(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                validationAllRequestParameter("subscribeCampaign",responseID,i+"",session);
                setDataMapVariables("INTERNAL."+responseID+"."+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                setDataMapVariables("INTERNAL."+responseID+"."+i+".PROGRAMMED_FLAG","0",session);
                
                Map<String,Object> subscriber = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+"."+i,session);
                
                if(subscriber.containsKey("programmedActivationDate"))
                {
                    if (!(firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)subscriber.get("programmedActivationDate"))))
                    {
                        setDataMapVariables("INTERNAL."+responseID+"."+i+".PROGRAMMED_FLAG","1",session);
                        continue;
                    }
                }
                
                if (isSubscriberInOurDatabase(responseID, session, i))
                {
                    String mainIDCampaign = (String)getDataMapVariables("TABLE."+responseID+"."+i+".MAINID.SUBSCRIBER.CAMPAIGN_ID", session);
                    if(mainIDCampaign == null || mainIDCampaign.isEmpty())
                    {
                        formatErrorResponse(responseID,session,MAINID_HAS_NOT_CAMPAIGN,"business",ERROR_SOURCE_MODULE,"Subscriber has not a campaign.");
                        break;
                    }
                }
                else
                {
                    formatErrorResponse(responseID,session,MAINID_HAS_NOT_CAMPAIGN,"business",ERROR_SOURCE_MODULE,"Subscriber has not a campaign.");
                    break;
                }
                
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = UNSUBSCRIBE_CAMPAIGN;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }

    private static void addPeriodCampaingMonth(Calendar startPeriodCampaing,Calendar startNextPeriodCampaing) throws Exception
    {
        int dayLastPeriodCampaing = startNextPeriodCampaing.get(Calendar.DAY_OF_MONTH);
        startNextPeriodCampaing.add(Calendar.MONTH, 1);

        int startDayCampaign = startPeriodCampaing.get(Calendar.DAY_OF_MONTH);
        
        if(startDayCampaign == startPeriodCampaing.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            startNextPeriodCampaing.set(Calendar.DAY_OF_MONTH, startNextPeriodCampaing.getActualMaximum(Calendar.DAY_OF_MONTH));
        } 
        else if(startDayCampaign > dayLastPeriodCampaing)
        {
            startNextPeriodCampaing.set(Calendar.DAY_OF_MONTH, startDayCampaign);
        }
    }
    
    private Map<String,Object> getStartNextPeriodCampaing(String startDateTime, String periodType, int interval, String billingCycleDate, String lastDateTime,String dateTimeExecution) throws Exception
    {
        try 
        {
            Map<String,Object> periodCampaignData = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            periodCampaignData.put("START_NEXT_PERIOD_CAMPAIGN", "");
            periodCampaignData.put("END_CURRENT_PERIOD_CAMPAIGN", "");
            
            Calendar startPeriodCampaign = parseDefaultStringToCalendarWithDbTimeZone(startDateTime);
            Calendar startNextPeriodCampaign = parseDefaultStringToCalendarWithDbTimeZone(lastDateTime);
            
            if(periodType.equals("CYCLE"))
            {
                setNextPeriodCampaignCycle(billingCycleDate, dateTimeExecution,    startNextPeriodCampaign,interval,periodCampaignData);
            }
            else if(periodType.equals("MONTH"))
            {
                setNextPeriodCampaignMonth(interval, startPeriodCampaign,startNextPeriodCampaign,periodCampaignData);
            }
            else if(periodType.equals("DAY"))
            {
                setNextPeriodCampaignDay(dateTimeExecution, startNextPeriodCampaign, interval, periodCampaignData);
            }
            else
            {
                throw createException(GET_START_NEXT_PERIOD_CAMPAING, INTERNAL, ERROR_SOURCE_MODULE, "Period invalid ["+periodType+"]");
            }
            return periodCampaignData;
        }
        catch (Exception e) 
        {
            throw createException(GET_START_NEXT_PERIOD_CAMPAING, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }

    private boolean useJustThisProductParameters(Map<String, Object> session,String responseID, int subscriberId) throws Exception {
        String externalData = getDataMapVariables("REQUEST."+responseID+DOT+subscriberId+".externalData", session);
        
        return externalData.equals("ANIDADOS");
    }

    private void setNextPeriodCampaignCycle(String billingCycleDate,    String dateTimeExecution, Calendar startNextPeriodCampaign, int interval,Map<String,Object>periodCampaignData)    throws Exception 
    {
        startNextPeriodCampaign.add(Calendar.MONTH, 1);
        
        if((billingCycleDate != null && !(billingCycleDate.isEmpty())) && (!(billingCycleDate.substring(0, 8).equals(dateTimeExecution.substring(0,8)))))
        {
            startNextPeriodCampaign.setTimeInMillis(parseDefaultStringToCalendarWithDbTimeZone(billingCycleDate).getTimeInMillis());
        }
        
        periodCampaignData.put("END_CURRENT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
        
        for (int i = interval; i > 0; i--) 
        {
            startNextPeriodCampaign.add(Calendar.MONTH, 1);
        }
        
        periodCampaignData.put("START_NEXT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
    }
    
    private void setNextPeriodCampaignDay(String dateTimeExecution, Calendar startNextPeriodCampaign, int interval,Map<String,Object>periodCampaignData)    throws Exception 
    {
        startNextPeriodCampaign.add(Calendar.DAY_OF_MONTH, 1);
        
        periodCampaignData.put("END_CURRENT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
        
        for (int i = interval; i > 0; i--) 
        {
            startNextPeriodCampaign.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        periodCampaignData.put("START_NEXT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
    }

    private void setNextPeriodCampaignMonth(int interval, Calendar startPeriodCampaign, Calendar startNextPeriodCampaign,Map<String,Object>periodCampaignData) throws Exception 
    {
        addPeriodCampaingMonth(startPeriodCampaign,startNextPeriodCampaign);
        
        periodCampaignData.put("END_CURRENT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
        
        for (int i = interval; i > 0; i--) 
        {
            addPeriodCampaingMonth(startPeriodCampaign,startNextPeriodCampaign);
        }
        
        periodCampaignData.put("START_NEXT_PERIOD_CAMPAIGN", parseCalendarToDefaultStringWithDbTimeZone(startNextPeriodCampaign));
    }
    
    @SuppressWarnings("unchecked")
    private void thresholdTime(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            int numberSubscriber = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            
            for (int i = 0; i < numberSubscriber; i++)
            {
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".QOS_FLAG", ZERO, session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".SET_QOS_SAPC",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);

                if (!isSubscriberInOurDatabase(responseID, session, i))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }

                if (!fieldExistMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"The subscriber hasn't subscriptions.");
                    break;
                }
                
                String productID = (String) getDataMapVariables("REQUEST."+responseID+DOT+i+".productID", session);
                Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS",session);
                Map<String,Object> product = getProductByID(productID);
                Map<String,Object> subscription = getSubscriptionWithThisProductName((String)getDataMapVariables("REQUEST."+responseID+".productName", session),subscriptions,true,iWontCancelSubscriptionsWithTheseRestrictions());                

                
                if(subscription.isEmpty())
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_HAS_NOT_SUBSCRIPTIONS,"business",ERROR_SOURCE_MODULE,"Subscriber has no active subscription to the product "+(String)product.get("productName")+DOT);
                    break;
                }
                
                String statuses = FULL+PIPE+RESTRICTED;
                if (!subscriptionHasTheseStatus(subscription, statuses)){ 
                    formatErrorResponse(responseID, session, STATUS_IS_NOT_VALID, "business", ERROR_SOURCE_MODULE, "Subscription status ["+ (String) subscription.get("STATUS_NAME")+"] is not valid for this operation");
                }

                setDataMapVariables("TABLE."+responseID+DOT+i+".SUBSCRIPTION",subscription,session);
                setDataMapVariables("TABLE."+responseID+DOT+i+".PRODUCT",product,session);
                
                String thresholdId   = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".thresholdID", session);
                String thresholdTime = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".thresholdTime", session);
                
                String qosDownload   = null;
                String qosUpload     = null;
                if (fieldExistMapVariablesAndNotNull("REQUEST."+responseID+DOT+i+".QOS_DOWNLOAD", session)) {
                    qosDownload   = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".QOS_DOWNLOAD", session);
                }
                if (fieldExistMapVariablesAndNotNull("REQUEST."+responseID+DOT+i+".QOS_UPLOAD", session)) {
                    qosUpload     = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".QOS_UPLOAD", session);    
                }
                
                String statusSubscription = null;
                
                if(qosDownload != null && qosUpload != null && qosDownload.matches("[0-9]+") && qosUpload.matches("[0-9]+"))
                {
                    setDataMapVariables("TABLE."+responseID+DOT+i+".QOS.STYPE", getStype((String)getDataMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIBER.subscriberType", session)), session);
                    setDataMapVariables("TABLE."+responseID+DOT+i+".QOS.QOS_DOWNLOAD",qosDownload,session);
                    setDataMapVariables("TABLE."+responseID+DOT+i+".QOS.QOS_UPLOAD",qosUpload,session);
                    
                    setDataMapVariables("INTERNAL."+responseID+DOT+i+".QOS_FLAG", "1", session);
                    
                    statusSubscription = RESTRICTED;
                    
                    String productPriority = getProductPriority((String)getDataMapVariables("TABLE."+responseID+DOT+i+".PRODUCT.productProperties",session),session);

                    //preparing for insert on subscriptionPolicies
                    String subscriberType = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".subscriberType", session);
                    String deviceType = (String)getDataMapVariables("REQUEST."+responseID+DOT+i+".deviceType",session);

                    if (!(isThereProductWithGreatPriority(productPriority,subscriptions,session,dateTimeExecution)))
                    {
                        setDataMapVariables("INTERNAL."+responseID+DOT+i+".SET_QOS_SAPC","1",session);
                        setSapcGroupId(subscriberType,deviceType,responseID,i,session);
                    }
                    
                    setDataMapVariables("TABLE."+responseID+DOT+i+".THRESHOLD.thresholdId",thresholdId,session);
                    setDataMapVariables("TABLE."+responseID+DOT+i+".THRESHOLD.thresholdTime",thresholdTime,session); 
                    setDataMapVariables("TABLE."+responseID+DOT+i+".STATUS.statusId",getStatusByName(statusSubscription),session);              
                    setDataMapVariables("TABLE."+responseID+DOT+i+".POLICY",getPolicieByStatus(getStatusByName(statusSubscription), thresholdId, getSubscriberTypeId(subscriberType), getDeviceTypeId(deviceType)),session);
                }
                
            }
        } 
        catch (Exception error) 
        {
            String errorCode        = THRESHOLD_TIME;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
              
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
            {
                String[] errors  = error.getMessage().split("\\<\\&\\>");
                errorCode        = errors[1];
                errorType        = errors[2];
                errorSource      = errors[3];
                errorDescription = errors[4];
            }            
            formatErrorResponse(responseID,(Map<String,Object>)mapVariables.get(sessionID),errorCode,errorType,errorSource,errorDescription);
        }
    }
    
    private String addHourInDateTime(String productTime, String dateTimeExecution) throws Exception
    {
        try 
        {
            long addTime = Long.parseLong(productTime) * 3600000;
            Calendar dateExecution = parseDefaultStringToCalendarWithDbTimeZone(dateTimeExecution);

            long execution = dateExecution.getTimeInMillis();

            dateExecution.setTimeInMillis(addTime + execution);

            return parseCalendarToDefaultStringWithDbTimeZone(dateExecution);
        } catch (Exception e) {
            throw createException(ADD_HOUR_IN_DATE_TIME, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    @SuppressWarnings("unchecked")
    private void removeElementThatIsBeingUsedByOthersSubscriptions(    Map<String, Object> elements, String container, Map<String, Object> subscriptions, Map<String, Object> subscription) throws Exception  
    {
        try 
        {
            List<String> listId = getAllIdAccumulatorsOrDedicatedAccounts(subscriptions, container, (String)subscription.get("PRODUCT_ID"));
            List<String> retainedList = new ArrayList<String>(); 
            for(String idx : elements.keySet())
            {
                String elementId =  (String)((Map<String,Object>)elements.get(idx)).get(container+"_ID");
                
                if(!listId.contains(elementId))
                {
                    retainedList.add(idx);
                }
            }
            
            elements.keySet().retainAll(retainedList);
            
        }
        catch (Exception e) 
        {
            throw createException(REMOVE_ELEMENT_SUBSCRIPTIONS, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
        
    }

    private String parseCalendarToDefaultStringWithDbTimeZone(Calendar date) throws Exception 
    {
        try 
        {
            StringBuilder parsedDate = new StringBuilder();
            parsedDate.append(date.get(Calendar.YEAR));
            
            //In Calendar.MONTH January is 0 and December is 11 
            if (date.get(Calendar.MONTH) < 9) 
            {
                parsedDate.append(0);
            }
        
            parsedDate.append(date.get(Calendar.MONTH)+1);

            appendTwoDigits(parsedDate, date.get(Calendar.DAY_OF_MONTH));
            appendTwoDigits(parsedDate, date.get(Calendar.HOUR_OF_DAY));
            appendTwoDigits(parsedDate, date.get(Calendar.MINUTE));
            appendTwoDigits(parsedDate, date.get(Calendar.SECOND));
            
            DbTimeZone zone = new DbTimeZone();
            parsedDate.append(zone.getOnlyNumberId());
            
            return parsedDate.toString();
        } 
        catch (Exception e) 
        {
            throw createException(PARSE_CAL_STR_DB_TZ, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
     
    private     StringBuilder appendTwoDigits(StringBuilder parsedDate, int date)
     {
         if (date <= 9) {
             parsedDate.append(0);
         }
         parsedDate.append(date);
         return parsedDate;
     }

    private     Calendar parseDefaultStringToCalendarWithDbTimeZone(String date) throws Exception {
        return parseStringToCalendar(date, FORMAT_DATE_TIME_WITH_TZ);
    }

    private String getEndMoth() throws Exception 
    {
        Calendar cal = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
        return parseCalendarToDefaultStringWithDbTimeZone(cal2);
    }
    
    private Calendar parseStringToCalendar(String date, String format) throws Exception {
        try {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            calendar.setTime(simpleDateFormat.parse(date));

            DbTimeZone zone = new DbTimeZone();
            calendar.setTimeZone(zone.getTimeZone());

            return calendar;
        } catch (Exception e) {
            throw createException(PARSE_STRING_TO_DATE, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private class DbTimeZone {

        private TimeZone dbTimeZone;

        public DbTimeZone() throws Exception {
            try {
                DataObject dbInfo = getEntity("DB_INFOS").getData();
                dbTimeZone = TimeZone.getTimeZone(dbInfo.getValueAsString("TIMEZONE"));
                  //dbTimeZone = TimeZone.getTimeZone("GMT-0500");

            } catch (Exception e) {
                throw new Exception("CORE_ERROR<&>"+GET_DB_TIME_ZONE+"<&>"+INTERNAL+"<&>"+ERROR_SOURCE_MODULE+"<&>"+e.getMessage());
//              throw createException(GET_DB_TIME_ZONE, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
            }
        }

        public TimeZone getTimeZone() {
            return (TimeZone) dbTimeZone.clone();
        }

        @SuppressWarnings("unused")
        public String getFullId() {
            return dbTimeZone.getID();
        }

        /**
         * Return the number part of {@link TimeZone} id.
         * 
         * <b>This method works if, only if, the {@link TimeZone} id starts with "GMT"
         * otherwise this method will return the full id.</b>
         * 
         * @return the number part of {@link TimeZone} id.
         */
        public String getOnlyNumberId() {
            String full = dbTimeZone.getID();
            Pattern pattern = Pattern.compile("^GMT");
            Matcher startWithGMT = pattern.matcher(full);
            if (startWithGMT.find()) {
                int length = full.length();
                return full.substring(length-6,length-3)+full.substring(length-2,length);
            }

            return full;
        }
    } 
    
    @SuppressWarnings("unchecked")
    private List<String> getAllIdAccumulatorsOrDedicatedAccounts (Map<String,Object> subscriptions, String container, String productIDRestricted ) throws Exception
    {
        try 
        {
            List<String> accumulatorsList = new ArrayList<String>();
            String idKey = container + "_ID";
            
            for(String idx : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(idx)).get("PRODUCT_ID");
                String statusName = (String)((Map<String,Object>)subscriptions.get(idx)).get("STATUS_NAME");
                if(statusName.matches(FULL+PIPE+RESTRICTED))
                {
                    Map<String,Object> accumulator = getContainerData(container, productID, container, false);
                    if(!(productID.equals(productIDRestricted)))
                    {
                        for(String idxAccumulator : (Set<String>)accumulator.keySet())
                        {
                            accumulatorsList.add((String)((Map<String,Object>)accumulator.get(idxAccumulator)).get(idKey));
                        }    
                    }
                }                    
            }
            
            return accumulatorsList;
        }
        catch (Exception e) 
        {
            throw createException(GET_ALL_ACCUMULATOR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    @SuppressWarnings("unchecked") 
    private Map<String,Object> getAllContainerParameters (Map<String,Object> subscriptions, String container ) throws Exception
    {
        try 
        {
            Map<String,Object> finalParams = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            Map<String,Object> tmpParams = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            List<String> productAlreadyAdded = new ArrayList<String>();
            for(String idx : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(idx)).get("PRODUCT_ID");
                if (!productAlreadyAdded.contains(productID)) {
                    tmpParams.put(idx, getContainerData(container, productID, container, false));
                    productAlreadyAdded.add(productID);
                }
                
            }
            for(String idx : tmpParams.keySet()){
                Map<String,Object> params = (Map<String,Object>) tmpParams.get(idx);
                for(String index : params.keySet())
                {
                    Map<String,Object> param = (Map<String, Object>) params.get(index);
                    Map<String,Object> element = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                    
                    String id = container+"_ID";
                    element.put(id, param.get(id));
                    element.put(container+"_VALUE", ZERO);
                    
                    if("DEDICATED_ACCOUNT".equals(container)){
                        String unitType = container+"_UNIT_TYPE";
                        element.put(unitType, param.get(unitType));    
                    }
                    
                    finalParams.put(idx, element);
                }
            }             
            return finalParams;
        }
        catch (Exception e) 
        {
            throw createException(GET_ALL_ACCUMULATOR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getInitialThresholdsByProductID(String productID,String ExecutionDateTime) throws Exception
    {
        try 
        {
            Map<String,Object> thresholds = getContainerData("THRESHOLDS_BY_PRODUCT_ID", productID, "THRESHOLDS_BY_PRODUCT_ID", false);
                        
            if ((thresholds.isEmpty()))
            {
                throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Initial Thresholds not exist for this product ["+productID+"]");
            }
            
            for(String idx : (Set<String>)thresholds.keySet())
            {
                Map<String,Object> thresholdsTmp = (Map<String,Object>)thresholds.get(idx) ;
                String sVolume = (String)thresholdsTmp.get("VOLUME");
                
                if (!sVolume.isEmpty() && sVolume.matches("[0-9]+"))
                {
                    int volume = Integer.parseInt(sVolume);
                    String startTime = (String)thresholdsTmp.get("START_TIMESTAMP");
                    String endTime = (String)thresholdsTmp.get("END_TIMESTAMP");
                    
                    if(firstIsGreatThanOrEqualSecond(ExecutionDateTime ,startTime ) && firstIsGreatThanOrEqualSecond(endTime, ExecutionDateTime))
                    {
                        if(volume == 0)
                        {
                            return thresholdsTmp;
                        }
                    }   
                }
            }
            
            throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Initial Thresholds not exist for this product ["+productID+"]");
        } 
        catch (Exception e) 
        {
            throw createException(GET_THRESHOLDS_BY_PRODUCTID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private Map<String,Object> getLimitThresholdsByProductID(Map<String,Object> product,String ExecutionDateTime) throws Exception
    {
        try 
        {
            String productID = (String)product.get("productID");
            Map<String,Object> thresholds = getContainerData("THRESHOLDS_BY_PRODUCT_ID", productID, "THRESHOLDS_BY_PRODUCT_ID", false);
                        
            if ((thresholds.isEmpty()))
            {
                throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Limit Thresholds not exist for this product ["+productID+"]");
            }
            
            for(String idx : (Set<String>)thresholds.keySet())
            {
                Map<String,Object> thresholdsTmp = (Map<String,Object>)thresholds.get(idx) ;
                String sVolume = (String)thresholdsTmp.get("VOLUME");
                
                if (!sVolume.isEmpty() && sVolume.matches("[0-9]+"))
                {
                    int volume = Integer.parseInt(sVolume);
                    int productVolume = Integer.parseInt((String)product.get("productVolume"));
                    String startTime = (String)thresholdsTmp.get("START_TIMESTAMP");
                    String endTime = (String)thresholdsTmp.get("END_TIMESTAMP");
                    
                    if(firstIsGreatThanOrEqualSecond(ExecutionDateTime ,startTime ) && firstIsGreatThanOrEqualSecond(endTime, ExecutionDateTime))
                    {
                        if(volume >= productVolume)
                        {
                            return thresholdsTmp;
                        }
                    }   
                }
            }
            
            throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Limit Thresholds not exist for this product ["+productID+"]");
        } 
        catch (Exception e) 
        {
            throw createException(GET_THRESHOLDS_BY_PRODUCTID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    @SuppressWarnings("unchecked")
    private Map<String,Object> getLimitThresholdsByVolume(long thresholdVolume,String productID,String ExecutionDateTime) throws Exception
    {
        try 
        {
            Map<String,Object> thresholds = getContainerData("THRESHOLDS_BY_PRODUCT_ID", productID, "THRESHOLDS_BY_PRODUCT_ID", false);
            long thresholdVolumeLimit = 0;
            String idxThresholdVolumeLimit = EMPTY;
            
                    
            if ((thresholds.isEmpty()))
            {
                throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Thresholds not exist for this product ["+productID+"]");
            }
            
            for(String idx : (Set<String>)thresholds.keySet())
            {
                Map<String,Object> thresholdsTmp = (Map<String,Object>)thresholds.get(idx) ;
                long volume = Long.parseLong((String)thresholdsTmp.get("VOLUME"));
                String startTime = (String)thresholdsTmp.get("START_TIMESTAMP");
                String endTime = (String)thresholdsTmp.get("END_TIMESTAMP");
                
                if(firstIsGreatThanOrEqualSecond(ExecutionDateTime ,startTime ) && firstIsGreatThanOrEqualSecond(endTime, ExecutionDateTime))
                {
                    if((thresholdVolume >= volume) && (thresholdVolumeLimit <= volume))
                    {
                        idxThresholdVolumeLimit = idx;
                        thresholdVolumeLimit = volume;
                    }
                }
            }
            
            if(!idxThresholdVolumeLimit.equals(EMPTY))
            {
                return (Map<String,Object>)thresholds.get(idxThresholdVolumeLimit); 
            }
            
            throw createException(THRESHOLDS_NOT_EXIST,INTERNAL,ERROR_SOURCE_MODULE,"Thresholds not exist for this product ["+productID+"]");
        } 
        catch (Exception e) 
        {
            throw createException(GET_THRESHOLDS_BY_PRODUCTID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    

    @SuppressWarnings("unchecked")
    //SEARCH FOR POLICY THAT IS EQUALS CRITERIA PARAMETER AND RETURN THE FIRST OCURRENCE ONE
    private      Map<String,Object> getPolicieByStatus(String statusId, String thresholdId, String subscriberTypeId, String deviceTypeId) throws Exception
    {
        try 
        {
            if(statusId == null || thresholdId == null || subscriberTypeId == null || deviceTypeId == null)
            {
                throw createException(GET_POLICY_BY_STATUS,INTERNAL,ERROR_SOURCE_MODULE,"Policy with ["+ statusId +"," + thresholdId +","+subscriberTypeId +","+deviceTypeId+"] inconsistence parameters");
            }
            
            Map<String,Object> policiesList = getContainerData("POLICY", statusId, "POLICY", false);
            int policiesCount = policiesList.size();
            
            for (int idx = 0 ; idx < policiesCount ; idx++)
            {
                String lThresholdId = (String)((Map<String,Object>)policiesList.get(String.valueOf(idx))).get("thresholdId");
                String lSubscriberTypeId = (String)((Map<String,Object>)policiesList.get(String.valueOf(idx))).get("subscriberTypeId");
                String lDeviceTypeId = (String)((Map<String,Object>)policiesList.get(String.valueOf(idx))).get("deviceTypeId");
                
                if(lThresholdId == null || thresholdId.equals(lThresholdId) ||  lThresholdId.isEmpty() )
                {
                    if(lSubscriberTypeId == null || subscriberTypeId.equals(lSubscriberTypeId) || lSubscriberTypeId.isEmpty())
                    {
                        if(lDeviceTypeId == null || deviceTypeId.equals(lDeviceTypeId) || lDeviceTypeId.isEmpty())
                        {
                            return (Map<String,Object>)policiesList.get(String.valueOf(idx));
                        }   
                    }   
                }
            }
            
            throw createException(GET_POLICY_BY_STATUS,INTERNAL,ERROR_SOURCE_MODULE,"Policy with ["+ statusId +"," + thresholdId +","+subscriberTypeId +","+deviceTypeId+"] not found in dataSource");
        } 
        catch (Exception e)
        {
            throw createException(GET_POLICY_BY_STATUS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    //SEARCH FOR A STATUS NAME AND RETURN A STATUS ID
    private    String getStatusByName(String status) throws Exception
    {
        try 
        {
            return (String)getContainerData("STATUS",status,"STATUS",true).get("statusId");
        } 
        catch (Exception e)
        {
            throw createException(GET_STATUS_BY_NAME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    //SEARCH FOR DEVICE TYPE AND RETURN A DEVICE TYPE ID
    private    String getDeviceTypeId(String deviceType) throws Exception
    {
        try 
        {
            return (String)getContainerData("DEVICE_TYPE",deviceType,"DEVICE_TYPE",true).get("deviceTypeId");
        } 
        catch (Exception e)
        {
            throw createException(GET_DEVICE_TYPE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    //SEARCH FOR DEVICE TYPE ID AND RETURN A DEVICE TYPE
    private    String getDeviceType(String deviceTypeId) throws Exception
    {
        try 
        {
            String deviceModem = getDeviceTypeId(MODEM);
            String deviceHandset = getDeviceTypeId(HANDSET);
            if(deviceTypeId.equals(deviceModem))
            {
                return MODEM;
            }
            else if(deviceTypeId.equals(deviceHandset))
            {
                return HANDSET;
            }
            else
            {
                throw createException(GET_DEVICE_TYPE,INTERNAL,ERROR_SOURCE_MODULE,"Device type is not mapped ["+deviceTypeId + "]");
            }
                
        } 
        catch (Exception e)
        {
            throw createException(GET_DEVICE_TYPE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }        
    
    //SEARCH FOR A SUBSCRIBER TYPE NAME AND RETURN A SUBSCRIBER TYPE ID
    private    String getSubscriberTypeId(String subscriberType) throws Exception
    {
        try 
        {
            return (String)getContainerData("SUBSCRIBER_TYPE",subscriberType,"SUBSCRIBER_TYPE",true).get("subscriberTypeId");
        } 
        catch (Exception e)
        {
            throw createException(GET_SUBSCRIBER_TYPE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    //SEARCH FOR DEVICE TYPE ID AND RETURN A DEVICE TYPE
    private    String getSubscriberType(String subscriberTypeId) throws Exception
    {
        try 
        {
            String subscriberPre = getSubscriberTypeId(PREPAID);
            String subscriberPos = getSubscriberTypeId(POSTPAID);
            String subscriberControlled = getSubscriberTypeId(CONTROLLED);
            
            if(subscriberTypeId.equals(subscriberPre))
            {
                return PREPAID;
            }
            else if(subscriberTypeId.equals(subscriberPos))
            {
                return POSTPAID;
            }
            else if(subscriberTypeId.equals(subscriberControlled))
            {
                return CONTROLLED;
            }
            else
            {
                throw createException(GET_SUBSCRIBER_TYPE,INTERNAL,ERROR_SOURCE_MODULE,"Subscriber type is not mapped ["+subscriberTypeId + "]");
            }
                
        } 
        catch (Exception e)
        {
            throw createException(GET_DEVICE_TYPE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
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
    
    @SuppressWarnings({ "unchecked", "unused" })
    private void formatSubscriberData(String sessionID, String requestID, String subscriberID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            Map<String,Object> subscriberDataRequest = (Map<String,Object>)getDataMapVariables("REQUEST."+requestID+DOT+subscriberID, session);
            
            setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.MAINID", (String)subscriberDataRequest.get("MAINID"), session);
            setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.IMSI", (String)subscriberDataRequest.get("IMSI"), session);
            
            if(subscriberDataRequest.containsKey("IMEI"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.IMEI", (String)subscriberDataRequest.get("IMEI"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.IMEI", EMPTY, session);
            }
            
            if(subscriberDataRequest.containsKey("ICCID"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.ICCID", (String)subscriberDataRequest.get("ICCID"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.ICCID", EMPTY, session);
            }
            
            if(subscriberDataRequest.containsKey("EMAIL"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.EMAIL", (String)subscriberDataRequest.get("EMAIL"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.EMAIL", EMPTY, session);
            }
            
            setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.subscriberType", (String)subscriberDataRequest.get("subscriberType"), session);
            setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.deviceType", (String)subscriberDataRequest.get("deviceType"), session);

        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_CREATE_NEW_SUBSCRIPTION_DATA,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatCreateNewSubscriptionData(String sessionID, String requestID, String subscriberID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            Map<String,Object> subscriberDataRequest = (Map<String,Object>)getDataMapVariables("REQUEST."+requestID+DOT+subscriberID, session);
            
            if(subscriberDataRequest.containsKey("IMEI"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.IMEI", (String)subscriberDataRequest.get("IMEI"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.IMEI", EMPTY, session);
            }
            
            if(subscriberDataRequest.containsKey("ICCID"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.ICCID", (String)subscriberDataRequest.get("ICCID"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.ICCID", EMPTY, session);
            }
            
            if(subscriberDataRequest.containsKey("EMAIL"))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.EMAIL", (String)subscriberDataRequest.get("EMAIL"), session);
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.EMAIL", (String)subscriberDataRequest.get("EMAIL"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.EMAIL", EMPTY, session);
                if(!fieldExistMapVariablesAndNotNull("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.EMAIL", session))
                {
                    setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".MAINID.SUBSCRIBER.EMAIL", EMPTY, session);
                }
            }
            
            if(fieldExistMapVariablesAndNotNull("TABLE."+requestID+DOT+subscriberID+".QOS.QOS_DOWNLOAD", session))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.QOS_DOWNLOAD_AFTER", (String)getDataMapVariables("TABLE."+requestID+DOT+subscriberID+".QOS.QOS_DOWNLOAD", session), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.QOS_DOWNLOAD_AFTER", EMPTY, session);
            }
            
            if(fieldExistMapVariablesAndNotNull("TABLE."+requestID+DOT+subscriberID+".QOS.QOS_UPLOAD", session))
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.QOS_UPLOAD_AFTER", (String)getDataMapVariables("TABLE."+requestID+DOT+subscriberID+".QOS.QOS_UPLOAD", session), session);
            }
            else
            {
                setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".CREATE_NEW_SUBSCRIPTION_DATA.QOS_UPLOAD_AFTER", EMPTY, session);
            }
            
            if(fieldExistMapVariablesAndNotNull("TABLE."+requestID+DOT+subscriberID+".masterSubscriberMAINID.SUBSCRIPTION.SUBSCRIPTION_ID", session))
            {
                setDataMapVariables("ACTION."+requestID+".MSG_DATA.MASTER_SUBSCRIPTION_ID", (String)getDataMapVariables("TABLE."+requestID+DOT+subscriberID+".masterSubscriberMAINID.SUBSCRIPTION.SUBSCRIPTION_ID", session), session);
            }
            else
            {
                setDataMapVariables("ACTION."+requestID+".MSG_DATA.MASTER_SUBSCRIPTION_ID", "null", session);
            }
            
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_CREATE_NEW_SUBSCRIPTION_DATA,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void formatChangeSubscriberDetailsData(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            Map<String,Object> subscriberData = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+".0.MAINID.SUBSCRIBER", session);
            Map<String,Object> subscriberDataRequest = (Map<String,Object>)getDataMapVariables("REQUEST."+responseID+".0", session);
            setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.MAINID", (String)subscriberDataRequest.get("MAINID"), session);
            
            if(subscriberDataRequest.containsKey("IMSI"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.IMSI", (String)subscriberDataRequest.get("IMSI"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.IMSI", (String)subscriberData.get("IMSI"), session);
            }
            
            if(subscriberDataRequest.containsKey("IMEI"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.IMEI", (String)subscriberDataRequest.get("IMEI"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.IMEI", (String)subscriberData.get("IMEI"), session);
            }
            
            if(subscriberDataRequest.containsKey("ICCID"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.ICCID", (String)subscriberDataRequest.get("ICCID"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.ICCID", (String)subscriberData.get("ICCID"), session);
            }
            
            if(subscriberDataRequest.containsKey("EMAIL"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.EMAIL", (String)subscriberDataRequest.get("EMAIL"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.EMAIL", (String)subscriberData.get("EMAIL"), session);
            }
            
            if(subscriberDataRequest.containsKey("subscriberType"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.subscriberType", (String)subscriberDataRequest.get("subscriberType"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.subscriberType", (String)subscriberData.get("subscriberType"), session);
            }
            
            if(subscriberDataRequest.containsKey("deviceType"))
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.deviceType", (String)subscriberDataRequest.get("deviceType"), session);
            }
            else
            {
                setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.deviceType", (String)subscriberData.get("deviceType"), session);
            }
            
            String subscriberProperty = "-1";
            
            try 
            {
                subscriberProperty = (String)subscriberData.get("PROPERTIES");
            }
            catch (Exception e) 
            {
                subscriberProperty = "-1";
            }
            
            
            if(!(subscriberProperty.equals("-1")) && ((subscriberDataRequest.containsKey("subscriberPaymentStatus")) || (subscriberDataRequest.containsKey("registeredSubscriber"))))
            {
                String inDebitSubscriber = (String)getDataMapVariables("CONFIG.INDEBT_SUBSCRIBER", session);
                String unRegisteredSubscriber = (String)getDataMapVariables("CONFIG.UNREGISTERED_SUBSCRIBER", session);

                if (subscriberDataRequest.containsKey("subscriberPaymentStatus"))
                {
                    
                    String subscriberPaymentStatus = (String)subscriberDataRequest.get("subscriberPaymentStatus");

                    if(subscriberPaymentStatus.equals("indebt"))
                    {
                        if(!bitAndProperty(subscriberProperty,inDebitSubscriber))
                        {
                            subscriberProperty = (new java.math.BigInteger(subscriberProperty).add(new java.math.BigInteger(inDebitSubscriber))).toString();
                        }
                    }

                    if(subscriberPaymentStatus.equals("ok"))
                    {
                        if(bitAndProperty(subscriberProperty,inDebitSubscriber))
                        {
                            subscriberProperty = (new java.math.BigInteger(subscriberProperty).subtract(new java.math.BigInteger(inDebitSubscriber))).toString();
                        }
                    }
                }
                
                if (subscriberDataRequest.containsKey("registeredSubscriber"))
                {
                    String registeredSubscriber = (String)subscriberDataRequest.get("registeredSubscriber");
                    
                    if(registeredSubscriber.equals("no"))
                    {
                        if(!bitAndProperty(subscriberProperty,unRegisteredSubscriber))
                        {
                            subscriberProperty = (new java.math.BigInteger(subscriberProperty).add(new java.math.BigInteger(unRegisteredSubscriber))).toString();
                        }
                    }
                    
                    if(is(registeredSubscriber))
                    {
                        if(bitAndProperty(subscriberProperty,unRegisteredSubscriber))
                        {
                            subscriberProperty = (new java.math.BigInteger(subscriberProperty).subtract(new java.math.BigInteger(unRegisteredSubscriber))).toString();
                        }
                    }
                }
            }
                
            setDataMapVariables("TABLE."+responseID+".0.MAINID.CHANGE_SUBSCRIBER_DATA.PROPERTIES", String.valueOf(subscriberProperty), session);
        }
        catch (Exception e) 
        {
            throw createException(FORMAT_DATA_CHANGE_SUBSCRIBER_DETAILS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private Map<String,Object> getsubscriptionWithThisProductName(String productName, Map<String,Object> subscriptions, String executionTime) throws Exception
    {
        return getSubscriptionWithThisProductName(productName,subscriptions,false,"-1");
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getSubscriptionWithThisProductName(String productName, Map<String, Object> subscriptions, boolean flagRestricted, String restrictionByStatus) throws Exception
    {
        try 
        {
            
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)(((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID"));
                Map<String,Object> product = getProductByID(productID);
                String productNameCompare = (String)product.get("productName");                  
                String subscriptionStatus = (String) ((Map<String, Object>) subscriptions.get(index)).get("STATUS_NAME");
                if (productName.equals(productNameCompare) && !(flagRestricted && subscriptionStatus.matches(restrictionByStatus)))
                {
                    return (Map<String,Object>)subscriptions.get(index);
                }
            }
            
            return new java.util.concurrent.ConcurrentHashMap<String,Object>();
        } 
        catch (Exception e) 
        {
            throw createException(GET_PRODUCT_WITH_THIS_NAME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private boolean isValidField(Map<String,Object> object, String fieldName) throws Exception
    {
        if(object.containsKey(fieldName))
        {
            if((String)object.get(fieldName) != null)
            {
                if(!((String)object.get(fieldName)).isEmpty() && !((String)object.get(fieldName)).toLowerCase().equals("null"))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    //UPDATE THE TIME VALUE OF PRODUCT TO THE TIME VALUE OF PROMOTION
    private void applyPromotionTime(Map<String,Object> product,Map<String,Object> promotion,int request ,int subscriber,Map<String,Object> session) throws Exception
    {
        try 
        {
            if(isValidField(promotion, "TIME"))
            {
                String time = (String)promotion.get("TIME");
                setDataMapVariables("TABLE."+request+DOT+subscriber+".PRODUCT.FINAL_TIME", String.valueOf(time), session);
            }
        }
        catch (Exception e) 
        {
            throw createException(CALCULATE_FINAL_TIME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    //UPDATE THE VOLUME VALUE OF PRODUCT TO THE VOLUME VALUE OF PROMOTION
    private void applyPromotionVolume(Map<String,Object> product,Map<String,Object> promotion,int request ,int subscriber,Map<String,Object> session) throws Exception
    {
        try 
        {
            if(isValidField(promotion, "VOLUME"))
            {
                String volume = (String)promotion.get("VOLUME");
                setDataMapVariables("TABLE."+request+DOT+subscriber+".PRODUCT.FINAL_VOLUME", String.valueOf(volume), session);
            }
        }
        catch (Exception e) 
        {
            throw createException(CALCULATE_FINAL_VOLUME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    private void addMasterPrice(int requestId, int subscriberId, Map<String,Object> session ) throws Exception
    {
        try
        {
            if(fieldExistMapVariablesAndNotNull("TABLE."+requestId+DOT+subscriberId+".PRODUCT.FINAL_PRICE", session))
            {
                float masterAccumulatorPrice = Float.parseFloat((String)getDataMapVariables("ACTION."+requestId+".MSG_DATA.FINAL_PRICE_MASTER", session));
                float productFinalPrice = Float.parseFloat((String)getDataMapVariables("TABLE."+requestId+DOT+subscriberId+".PRODUCT.FINAL_PRICE", session));
                masterAccumulatorPrice = masterAccumulatorPrice + productFinalPrice;
                setDataMapVariables("ACTION."+requestId+".MSG_DATA.FINAL_PRICE_MASTER",String.valueOf(masterAccumulatorPrice),session);
            }
        }
        catch (Exception e) 
        {
            throw createException(ADD_MASTER_PRICE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }            
    }
    
    private void calculateFinalPrice(Map<String,Object> product,Map<String,Object> promotion,int request ,int subscriber,Map<String,Object> session) throws Exception
    {
        calculateFinalPrice(product, promotion, request, subscriber, session,true);
    }         
    
    private void calculateFinalPrice(Map<String,Object> product,Map<String,Object> promotion,int request ,int subscriber,Map<String,Object> session,boolean applyPromotionFlag) throws Exception
    {
        try 
        {
            NumberFormat formatter = new DecimalFormat("0.00");
            
            String price    = "0.00";
            String taxType  = "percentage";
            float taxValue  = 0;
            boolean flagTax = false;
            
            if(product.isEmpty())
            {
                throw createException(PRODUCT_NOT_FOUND,INTERNAL,ERROR_SOURCE_MODULE,"Product not found.");
            }
            
            if(applyPromotionFlag && !promotion.isEmpty() && promotion.containsKey("PRICE") && (!((String)promotion.get("PRICE")).isEmpty()) && (!((String)promotion.get("PRICE")).toLowerCase().equals("null")))
            {
                price = (String)promotion.get("PRICE");
                
                if(isValidField(promotion,"TAX_ID"))
                {
                    flagTax = true;
                    String taxProperty = (String)promotion.get("TAX_PROPERTIES");
                    taxValue = Float.parseFloat((String)promotion.get("TAX_VALUE"));
                    
                    if(isPresentProperty(taxProperty, "TAX_MONEY", session))
                    {
                        taxType = "monetary";
                    }
                }
            }
            else
            {
                if(product.containsKey("productPrice"))
                {
                    price = (String)product.get("productPrice");
                    
                    if(isValidField(product,"taxID"))
                    {
                        flagTax  = true;
                        
                        if(((String)product.get("taxType")).matches("monetary"))
                        {
                            taxType = "monetary";
                        }
                        
                        taxValue = Float.parseFloat((String)product.get("taxValue"));
                    }
                }
            }
            
            if(flagTax)
            {
                if(taxType.equals("monetary"))
                {
                    price = formatter.format(Float.parseFloat(price) + taxValue);
                }
                else
                {
                    price = formatter.format(Float.parseFloat(price) * (1 + (taxValue/100)));
                }    
            }

            setDataMapVariables("TABLE."+request+DOT+subscriber+".PRODUCT.FINAL_PRICE", String.valueOf(price), session);

        }
        catch (Exception e) 
        {
            throw createException(CALCULATE_FINAL_PRICE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
     private String nextBillingCycleDate(String date) throws Exception
     {
         try
         {
             int year = Integer.parseInt(date.substring(0, 4));
             int month = Integer.parseInt(date.substring(4, 6));
             if(month == 12)
             {
                 year++;
                 month = 1;
             }
             else
             {
                 month++;
             }
             String newMonth = 0+String.valueOf(month);
             newMonth = newMonth.substring(newMonth.length()-2);
             String newDate = String.valueOf(year)+newMonth+date.substring(6);
             return newDate;
         }
        catch (Exception e) 
        {
            throw createException(NEXT_BILLING_CYCLE_DATE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }     
     }

    @SuppressWarnings("unchecked")
    private void applyProRataInBillingCycle(String dateTimeExecution,String billingCycleDate, String proRataType, Map<String,Object> session,String responseID,String subscriberID) throws Exception
    {
        try 
        {
            Map<String,Object> billingCycleData = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            Map<String,Object> product = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+subscriberID+".PRODUCT", session);
            
            String billingCycleEndDate = getEndBillingCycle(billingCycleDate);
            
            long timeNumber = getTimeBetween(dateTimeExecution,billingCycleEndDate);
            long volumeWithProRata = 0;
            float priceWithProRata = 0;
            
            if(proRataType.matches("volume|default")){
                try
                {
                    if (!isValidField(product, "FINAL_VOLUME")) {
                        throw createException(THE_PRODUCT_IS_NOT_LIMITED_BY_VOLUME,INTERNAL,ERROR_SOURCE_MODULE,"The product is not limited by volume.");
                    }
                    volumeWithProRata = Long.parseLong((String)product.get("FINAL_VOLUME"))/720*timeNumber;
                }
                catch (Exception e) 
                {
                    throw createException(INVALID_PRORATA_TYPE_VOLUME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
                }               
                
            }

            if(proRataType.matches("price|default")){
                try
                {
                    priceWithProRata = Float.parseFloat((String)product.get("FINAL_PRICE"))/720*timeNumber;
                }
                catch (Exception e) 
                {
                    throw createException(INVALID_PRORATA_TYPE_PRICE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
                }               
            }
            
            setDataMapVariables("TABLE."+responseID+DOT+subscriberID+".SUBSCRIPTION.END_TIMESTAMP",billingCycleEndDate,session);
            
            billingCycleData.put("billingCycleEndDate", billingCycleEndDate);

            if(proRataType.equals("price"))
            {                
                setDataMapVariables("TABLE."+responseID+DOT+subscriberID+".PRODUCT.FINAL_PRICE",String.valueOf(priceWithProRata),session);
            }
            else if(proRataType.equals("volume"))
            {
                setDataMapVariables("TABLE."+responseID+DOT+subscriberID+".PRODUCT.FINAL_VOLUME",String.valueOf(volumeWithProRata),session);
            }
            else if(proRataType.equals("default"))
            {
                setDataMapVariables("TABLE."+responseID+DOT+subscriberID+".PRODUCT.FINAL_PRICE",String.valueOf(priceWithProRata),session);
                setDataMapVariables("TABLE."+responseID+DOT+subscriberID+".PRODUCT.FINAL_VOLUME",String.valueOf(volumeWithProRata),session);
            }
        }
        catch (Exception e) 
        {
            throw createException(APPLY_PRORATA_IN_BILLING_CYCLE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unused")
    private long getDayBetween(String dateTimeExecution,String billingCycleEndDate) throws Exception
    {
        try 
        {
            SimpleDateFormat sdf_day = new SimpleDateFormat("yyyyMMdd");

            Date first = sdf_day.parse(billingCycleEndDate.substring(0, 8));
            Date second = sdf_day.parse(dateTimeExecution.substring(0, 8));

            long dtFirst = first.getTime();
            long dtSecond = second.getTime();

            long result = (dtFirst - dtSecond) / 86400000;

            return result;
        } catch (Exception e) {
            throw createException(GET_DAY_BETWEEN, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }

    private long getTimeBetween(String dateTimeExecution,String billingCycleEndDate) throws Exception
    {
        try 
        {
            SimpleDateFormat sdf_day = new SimpleDateFormat("yyyyMMddhhmmss");

            Date first = sdf_day.parse(billingCycleEndDate.substring(0, 14));
            Date second = sdf_day.parse(dateTimeExecution.substring(0, 14));

            long dtFirst = first.getTime();
            long dtSecond = second.getTime();

            long result = (dtFirst - dtSecond) / 3600000;

            return result;
        } catch (Exception e) {
            throw createException(GET_TIME_BETWEEN, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
 
    @SuppressWarnings("unchecked")
    private void validationAllRequestParameter(String operation, String resquesID, String subscriberID, Map<String,Object> session) throws Exception
    {
        try 
        {
            DataObject cRequestParameters = getEntity("REQUEST_PARAMETERS_BY_OPERATION").getData();

            if (cRequestParameters.parameterExists(operation))
            {
                String requestParametersList = cRequestParameters.getValueAsString(operation);
                Map<String,Object> index = getDataContainer("REQUEST_PARAMETERS_BY_OPERATION", requestParametersList);

                for (String indexInterator : (Set<String>)index.keySet())
                {
                    Map<String,Object> requestParameter = (Map<String,Object>)index.get(indexInterator);
                    
                    String fieldValue = getFieldCompare((String)requestParameter.get("PARAMETER"),Integer.parseInt(resquesID),Integer.parseInt(subscriberID),session);
                    String exprString = (String)requestParameter.get("REGEXP_VALUES");
                    
                    CharSequence inputStr = fieldValue;  
                    Pattern pattern = Pattern.compile(String.valueOf(exprString));  
                    Matcher matcher = pattern.matcher(inputStr);
                       
                    if (!(matcher.matches()))
                    {
                        throw createException(VALIDATE_REQUEST_PARAMETER,"malformed",ERROR_SOURCE_MODULE,"Invalid "+(String)requestParameter.get("PARAMETER")+" = \""+fieldValue+"\". Valid values are described by the expression ["+exprString+"].");
                    }
                }
            }           
        } 
        catch (Exception e) 
        {
            throw createException(VALIDATION_ALL_REQUEST_PARAMETER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void validationAllRequestParameter(String categoryID, String operation, String resquesID, String subscriberID, Map<String,Object> session) throws Exception
    {
        try 
        {
            Map<String,Object> index = getContainerData("REQUEST_PARAMETERS", categoryID+DOT+operation, "REQUEST_PARAMETERS", false);
            
            for (final String indexInterator : (Set<String>)index.keySet())
            {
                Map<String,Object> requestParameter = (Map<String,Object>)index.get(indexInterator);
                
                
                String fieldValue = getFieldCompare((String)requestParameter.get("PARAMETER"),Integer.parseInt(resquesID),Integer.parseInt(subscriberID),session);
                String exprString = (String)requestParameter.get("REGEXP_VALUES");
                
                CharSequence inputStr = fieldValue;  
                Pattern pattern = Pattern.compile(String.valueOf(exprString));  
                Matcher matcher = pattern.matcher(inputStr);
                   
                if (!(matcher.matches()))
                {
                    throw createException(VALIDATE_REQUEST_PARAMETER,"malformed",ERROR_SOURCE_MODULE,"Invalid "+(String)requestParameter.get("PARAMETER")+" = \""+fieldValue+"\". Valid values are described by the expression ["+exprString+"].");
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(VALIDATION_ALL_REQUEST_PARAMETER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }


    @SuppressWarnings("unchecked")
    private Map<String,Object> getProductWithThisName(String productName, Map<String,Object> subscriptions) throws Exception
    {
        try 
        {
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID");
                Map<String,Object> product = getProductByID(productID);
                
                if (productName.equals((String)product.get("productName")))
                {
                    return product;
                }
            }
            
            return new java.util.concurrent.ConcurrentHashMap<String,Object>();
        } 
        catch (Exception e) 
        {
            throw createException(GET_PRODUCT_WITH_THIS_NAME,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void reStructRequestWithMaster(String requestID, Map<String,Object> session) throws Exception
    {
        try 
        {
            Map<String,Object> request = (Map<String,Object>)getDataMapVariables("REQUEST."+requestID, session);
            
            String subscriberIDMaster = null;
            
            for (final String subscriberID : (Set<String>)request.keySet())
            {
                if (subscriberID.matches("[0-9]") && fieldExistMapVariables("REQUEST."+requestID+DOT+subscriberID+".subscriberRole", session))
                {
                    String subscriberRole = (String)getDataMapVariables("REQUEST."+requestID+DOT+subscriberID+".subscriberRole", session);
                    if(subscriberRole.equals("master"))
                    {
                        subscriberIDMaster = subscriberID;
                        break;  
                    }
                }
            }
            if (subscriberIDMaster != null )
            {
                Map<String,Object> tableMaster      = (Map<String,Object>)getDataMapVariables("TABLE."+requestID+DOT+subscriberIDMaster, session);
                Map<String,Object> subscriberMaster = (Map<String,Object>)getDataMapVariables("REQUEST."+requestID+DOT+subscriberIDMaster, session);

                if(!(subscriberIDMaster.equals(ZERO)))
                {
                    Map<String,Object> table        = (Map<String,Object>)getDataMapVariables("TABLE."+requestID+".0", session);
                    Map<String,Object> subscriber   = (Map<String,Object>)getDataMapVariables("REQUEST."+requestID+".0", session);
                    setDataMapVariables("TABLE."+requestID+".0", tableMaster, session);
                    setDataMapVariables("REQUEST."+requestID+".0", subscriberMaster, session);
                    
                    setDataMapVariables("TABLE."+requestID+DOT+subscriberIDMaster, table, session);
                    setDataMapVariables("REQUEST."+requestID+DOT+subscriberIDMaster, subscriber, session);
                }
                
                for (final String index : (Set<String>)request.keySet())
                {
                    if ( index.matches("[0-9]+") &&  (((Map<String,Object>)request.get(index)).containsKey("subscriberRole")) && (((String)getDataMapVariables("REQUEST."+requestID+DOT+index+".subscriberRole", session)).equals("dependant")))
                    {
                        setDataMapVariables("REQUEST."+requestID+DOT+index+".masterSubscriberMAINID",(String)subscriberMaster.get("MAINID") , session);
                        
                        if(fieldExistMapVariables("TABLE."+requestID+DOT+0+".MAINID.SUBSCRIBER", session))
                        {
                            setDataMapVariables("TABLE."+requestID+DOT+index+".masterSubscriberMAINID.SUBSCRIBER", (Map<String,Object>)getDataMapVariables("TABLE."+requestID+DOT+0+".MAINID.SUBSCRIBER", session), session);
                        }
                        if(fieldExistMapVariables("TABLE."+requestID+DOT+0+".MAINID.SUBSCRIPTIONS", session))
                        {
                            setDataMapVariables("TABLE."+requestID+DOT+index+".masterSubscriberMAINID.SUBSCRIPTIONS", (Map<String,Object>)getDataMapVariables("TABLE."+requestID+DOT+0+".MAINID.SUBSCRIPTIONS", session), session);
                        }
                    }
                }
            }
            
            if ((subscriberIDMaster != null) && (request.keySet().size() < 2))
            {
                throw createException(LEAST_MASTER_AND_DEPENDANT,INTERNAL,ERROR_SOURCE_MODULE,"At least a master and a dependent must exist in the request");
            }
        }
        catch (Exception e) 
        {
            throw createException(RESTRUCT_REQUEST_WITH_MASTER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private void getDataProductVsThreshold(int ThresholdVolume, String productID, String requestID, String subscriberID, Map<String,Object> session) throws Exception
    {
        try 
        {
            DataObject cProductVsThreshold = getEntity("PRODUCT_VS_THRESHOLD").getData();
            
            if(cProductVsThreshold.parameterExists(productID))
            {
                String productVsThresholdFull = cProductVsThreshold.getValueAsString(productID);
                Map<String,Object> productVsThresholdList = getDataContainer("PRODUCT_VS_THRESHOLD", productVsThresholdFull);
                
                for (final String index : (Set<String>)productVsThresholdList.keySet())
                {
                    if (Integer.parseInt((String)((Map<String,Object>)productVsThresholdList.get(index)).get("VOLUME")) <= ThresholdVolume)
                    {
                        setDataMapVariables("TABLE."+requestID+DOT+subscriberID+".PRODUCT_VS_THRESHOLD", (Map<String,Object>)productVsThresholdList.get(index), session);
                    }
                }
            }
        }
        catch (Exception e) 
        {
            throw createException(GET_DATA_PRODUCT_VS_THRESHOLD,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private String getFieldName(String field,String resquesID, String subscriberID,Map<String,Object> session) throws Exception
    {
        try 
        {
            
            String[] parse =  field.split("\\.");

            if (parse.length == 2)
            {
                return  parse[0]+"."+resquesID+"."+parse[1];
            }
            else if (parse.length >= 3)
            {
                StringBuilder path = new StringBuilder(parse[0]+"."+resquesID+"."+subscriberID);
                for(int index = 1; index < parse.length; index++)
                {
                    path.append("."+parse[index]);
                }
                return path.toString();
            }
            else
            {
                throw createException(GET_FIELD_COMPARE,INTERNAL,ERROR_SOURCE_MODULE,"Field name not found.");
            }
        } 
        catch (Exception e) 
        {
            throw createException(GET_FIELD_COMPARE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }    
    
    private String getFieldCompare(String field,int resquesID, int subscriberID,Map<String,Object> session) throws Exception
    {
        try 
        {
            String[] parse =  field.split("\\.");

            if (parse.length == 2)
            {
                if(fieldExistMapVariablesAndNotNull(parse[0]+DOT+resquesID+DOT+parse[1],session))
                {
                    return (String)getDataMapVariables(parse[0]+DOT+resquesID+DOT+parse[1],session); 
                }
                return EMPTY;
            }
            else if (parse.length == 3)
            {
                if(fieldExistMapVariablesAndNotNull(parse[0]+DOT+resquesID+DOT+subscriberID+DOT+parse[2],session))
                {
                    return (String)getDataMapVariables(parse[0]+DOT+resquesID+DOT+subscriberID+DOT+parse[2],session); 
                }
                return EMPTY;
            }
            else
            {
                throw createException(GET_FIELD_COMPARE,INTERNAL,ERROR_SOURCE_MODULE,"Level of variable not know");
            }
        } 
        catch (Exception e) 
        {
            throw createException(GET_FIELD_COMPARE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    } 
    
    private     boolean fieldExistMapVariablesAndNotNull(String field, Map<String,Object> session) throws Exception
    {
        try 
        {
            if (fieldExistMapVariables(field, session))
            {
                String object = (String)getDataMapVariables(field, session);
                return (object != null && !object.isEmpty());
            }
            return false;
        }
        catch (Exception e) 
        {
            throw createException(FIELD_EXIST_MAPVARIABLES_AND_NOT_NULL,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getSubscriptionWithProductImmediateLessPriority(String productPriority, Map<String,Object> subscriptions, Map<String,Object> session ) throws Exception
    {
        try 
        {
            int idx = 0;
            java.math.BigInteger priorityTmp = new java.math.BigInteger(ZERO);
            
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID");
                Map<String,Object> product = getProductByID(productID);
                String productPriorityParam = getProductPriority(productPriority, session);
                String productPriorityLocal = getProductPriority((String)product.get("productProperties"),session);
                
                java.math.BigInteger bProductProperty = new java.math.BigInteger(productPriorityParam);
                java.math.BigInteger bProductPriorityLocal = new java.math.BigInteger(productPriorityLocal);
                
                String statusName = (String)(((Map<String,Object>)subscriptions.get(index)).get("STATUS_NAME"));
                
                if ((bProductPriorityLocal.compareTo(bProductProperty) < 0) && (bProductPriorityLocal.compareTo(priorityTmp) > 0) && (isActiveSubscription(statusName)))
                {
                    idx = Integer.parseInt(index);
                    priorityTmp = bProductPriorityLocal;
                }
            }
            
            if(!priorityTmp.toString().equals(ZERO))
            {
                return (Map<String,Object>)subscriptions.get(String.valueOf(idx));
            }
            
            return new java.util.concurrent.ConcurrentHashMap<String,Object>();
        } 
        catch (Exception e) 
        {
            throw createException(IS_THERE_PRODUCT_WITH_LESS_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private boolean isThereProductWithLessPriority(String productPriority, Map<String,Object> subscriptions, Map<String,Object> session ) throws Exception
    {
        try 
        {
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID");
                Map<String,Object> product = getProductByID(productID);
                String productPriorityParam = getProductPriority(productPriority, session);
                String productPriorityLocal = getProductPriority((String)product.get("productProperties"),session);
                
                java.math.BigInteger bProductProperty = new java.math.BigInteger(productPriorityParam);
                java.math.BigInteger bProductPriorityLocal = new java.math.BigInteger(productPriorityLocal);
                
                if (bProductPriorityLocal.compareTo(bProductProperty) < 0)
                {
                    return true;
                }
            }
            
            return false;
        } 
        catch (Exception e) 
        {
            throw createException(IS_THERE_PRODUCT_WITH_LESS_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean isThereProductWithGreatPriority(String productPriority, Map<String,Object> subscriptions, Map<String,Object> session, String executionTime ) throws Exception
    {
        try 
        {
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID");
                Map<String,Object> product = getProductByID(productID);
                String productPriorityParam = getProductPriority(productPriority, session);
                String productPriorityLocal = getProductPriority((String)product.get("productProperties"), session);
                
                java.math.BigInteger bProductProperty = new java.math.BigInteger(productPriorityParam);
                java.math.BigInteger bProductPriorityLocal = new java.math.BigInteger(productPriorityLocal);
                
                
                String statusName = (String)(((Map<String,Object>)subscriptions.get(index)).get("STATUS_NAME"));
                
                if ((bProductPriorityLocal.compareTo(bProductProperty) > 0) && (isActiveSubscription(statusName)))
                {
                    return true;
                }
            }
            
            return false;
        } 
        catch (Exception e) 
        {
            throw createException(IS_THERE_PRODUCT_WITH_GREAT_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private boolean isThereConflictRulesRecurrence(Map<String,Object> product, Map<String,Object> subscriptions,String executionTime ) throws Exception
    {
        try 
        {
            if(product.containsKey("recurrenceID"))
            {
                String recurrenceID = (String)product.get("recurrenceID");
                
                if( recurrenceID != null && !recurrenceID.isEmpty() && !(recurrenceID.toLowerCase().equals("null")))
                {
                    for(String Idx : (Set<String>)subscriptions.keySet())
                    {
                        String productID = (String)((Map<String,Object>)subscriptions.get(Idx)).get("PRODUCT_ID");
                        Map<String,Object> productSubscription = getProductByID(productID);
                        String statusName = (String)(((Map<String,Object>)subscriptions.get(Idx)).get("STATUS_NAME"));
                        
                        if(productSubscription.containsKey("recurrenceID") && (isActiveSubscription(statusName)))
                        {
                            String recurrenceIDproductSubscription = (String)product.get("recurrenceID");
                            
                            if(recurrenceIDproductSubscription != null && !recurrenceIDproductSubscription.isEmpty() && !(recurrenceIDproductSubscription.toLowerCase().equals("null")))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        catch (Exception e) 
        {
            throw createException(IS_THERE_CONFLICT_RULES_RECURRENCE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private          boolean subscriptionHasTheseStatus(Map<String, Object> subscription, String stasuses)
    {
         
        return subscription.get("STATUS_NAME").toString().matches(stasuses);
    }
    
    private boolean isActiveSubscription(String statusName)throws Exception
    {
        return statusName.equals(FULL) || statusName.equals(RESTRICTED) || statusName.equals(SUSPENDED); 
    }

    @SuppressWarnings("unused")
    private boolean statusIsRestricted(String statusID) throws Exception
    {
        return getStatusByName(RESTRICTED) == statusID;
    }

    @SuppressWarnings("unused")
    private boolean statusIsFull(String statusID) throws Exception
    {
        return getStatusByName(FULL) == statusID;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isThereProductWithThisPriority(String productPriority, Map<String,Object> subscriptions,String executionTime ) throws Exception
    {
        try 
        {
            
            for (final String index : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)(((Map<String,Object>)subscriptions.get(index)).get("PRODUCT_ID"));
                Map<String,Object> product = getProductByID(productID);
                String productProperty = (String)product.get("productProperties");
                
                String statusName = (String)(((Map<String,Object>)subscriptions.get(index)).get("STATUS_NAME"));
                
                if ((bitAndProperty(productProperty,productPriority)) && (isActiveSubscription(statusName)))
                {
                    return true;
                }
            }
            
            return false;
        } 
        catch (Exception e) 
        {
            throw createException(IS_THERE_PRODUCT_WITH_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    private void formatErrorResponse(String responseID,Map<String,Object> session,String errorCode, String errorType, String errorSource, String errorDescription) throws Exception
    {
        try 
        {
            setDataMapVariables("ACTION."+responseID+".STATUS"            , "ERROR"         , session);
            setDataMapVariables("ACTION."+responseID+".ERROR_CODE"        , errorCode       , session);
            setDataMapVariables("ACTION."+responseID+".ERROR_SOURCE"      , errorSource     , session);
            setDataMapVariables("ACTION."+responseID+".ERROR_TYPE"        , errorType       , session);
            setDataMapVariables("ACTION."+responseID+".ERROR_DESCRIPTION" , errorDescription, session);
            
        }
        catch (Exception e) 
        {
            throw createException(FORMAT_RESPONSE_ERROR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    private String getProductPriority(String productProperty, Map<String,Object> session) throws Exception
    {
        try 
        {
            @SuppressWarnings("unchecked")
            Map<String,Object> priorities = filterOnlyPriority((Map<String,Object>)getDataMapVariables("CONFIG",session));
            
            for (String priority : priorities.keySet())
            {
                String valuePriority = (String) priorities.get(priority);
                if (bitAndProperty(productProperty,valuePriority))
                {
                    return valuePriority;
                }
            }
            
            throw createException(GET_PRODUCT_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE, "Product does not have priority");
        } 
        catch (Exception e) 
        {
            throw createException(GET_PRODUCT_PRIORITY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    private Map<String,Object> filterOnlyPriority(Map<String,Object> configs){
     Map<String,Object> priorities = new java.util.concurrent.ConcurrentHashMap<String,Object>();
     
     for(String key : configs.keySet()){
      if(key.startsWith("PRIORITY_")){
       priorities.put(key, configs.get(key)); 
      }
     }
      
     return priorities;
    }
    
    private boolean isPresentProperty(String property, String propertyName, Map<String,Object> session) throws Exception
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
    
    private boolean compareSubscriberData(Map<String,Object> subscriber, Map<String,Object> dataBase) throws Exception
    {
        try 
        {
            String[] fieldsList = COMPARE_FIELDS_SUBSCRIBER.split("\\|");
            int lengthFieldsList =  fieldsList.length;
            
            for (int i = 0; i < lengthFieldsList; i++)
            {
                if (!((String)subscriber.get(fieldsList[i])).equals((String)dataBase.get(fieldsList[i])))
                {
                    return false;
                }
            }
            
            return true; 
            
        } 
        catch (Exception e) 
        {
            throw createException(COMPARE_SUBSCRIBER_DATA,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private String getEndBillingCycle(String billingCycle) throws Exception
    {
        try 
        {
            String billingCycledate = billingCycle.substring(0, 8) + "000000" + billingCycle.substring(14, 19);
            return billingCycledate;
        } catch (Exception e) {
            throw createException(GET_END_BILLING_CYCLE, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private boolean firstIsGreatThanOrEqualSecond(String firstDate, String secondDate ) throws Exception
    {
        try {
            Calendar first = parseDefaultStringToCalendarWithDbTimeZone(firstDate);
            Calendar second = parseDefaultStringToCalendarWithDbTimeZone(secondDate);

            long dtFirst = first.getTimeInMillis();
            long dtSecond = second.getTimeInMillis();

            long result = dtFirst - dtSecond;

            if (result >= 0) {
                return true;
            }

            return false;
        } catch (Exception e) {
            throw createException(FIRST_IS_GREAT_THAN_OR_EQUAL_SECOND, INTERNAL, ERROR_SOURCE_MODULE,
                    formatErroDescription(e));
        }
    }
    
    private void findMainIDRange(String mainID, int request, int subscriber, Map<String,Object> session) throws Exception
    {
        try 
        {
            DataObject cMainIDRange = getEntity("MAINID_RANGES").getData();
            Object[] mainIDRangeListQuery = cMainIDRange.getParameterContainer().keySet().toArray();
            
            Arrays.sort(mainIDRangeListQuery);
            
            String mainIDRangeList = cMainIDRange.getValueAsString((String)mainIDRangeListQuery[Math.abs(Arrays.binarySearch(mainIDRangeListQuery, mainID+";Z"))-2]);
            
            Map<String,Object> mainIDRange = getDataContainer("MAINID_RANGES",mainIDRangeList,true);
            
            if ((mainID.compareTo((String)mainIDRange.get("MAINID_RANGE_END")) <= 0))
            {
                setDataMapVariables("TABLE."+request+DOT+subscriber+".MAINID_RANGES", mainIDRange, session);
            }
            
        } 
        catch (Exception e) 
        {
            throw createException(FIND_MAINID_RANGE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    private Map<String,Object> getPromotionByID(String promotionID, String mainID, String mainIDRangeID, String imei, Map<String,Object> session) throws Exception
    {
     try 
        {
            Map<String,Object> promotions = getContainerData("PROMOTION_BY_ID", promotionID, "PROMOTION_BY_ID", true);
            if(promotions.isEmpty())
            {
                throw createException(PROMOTION_ID_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Promotion Id["+promotionID+"] not found");
            }
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
            if ( firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)promotions.get("START_TIMESTAMP")) && firstIsGreatThanOrEqualSecond((String)promotions.get("END_TIMESTAMP"),dateTimeExecution))
            {
                boolean promotionVSMainIDRange = false;
                boolean promotionVSImeiRange = false;
                
                DataObject cPromotionVsMainIDRange = null;
                try
                {
                    promotionVSMainIDRange = true;
                    cPromotionVsMainIDRange = getEntity("PROMOTION_VS_MAINID_RANGE").getData();
                    
                }
                catch (Exception e) 
                {
                }
                
                if(promotionVSMainIDRange)
                {
                    if (cPromotionVsMainIDRange.parameterExists(promotionID+DOT+mainIDRangeID))
                    {
                        String PromotionVsMainIDRangeList = cPromotionVsMainIDRange.getValueAsString(promotionID+DOT+mainIDRangeID);
                        Map<String,Object> promotionVsMainIDRange = getDataContainer("PROMOTION_VS_MAINID_RANGE",PromotionVsMainIDRangeList,true);
                        
                        if ( firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)promotionVsMainIDRange.get("START_TIMESTAMP")) && firstIsGreatThanOrEqualSecond((String)promotionVsMainIDRange.get("END_TIMESTAMP"),dateTimeExecution))
                        {
                            return promotions;
                        }
                    }
                }
                
                DataObject cImeiRange = null;
                try
                {
                    promotionVSImeiRange = true;
                    cImeiRange = getEntity("IMEI_RANGES").getData();
                    
                }
                catch (Exception e) 
                {
                }
                
                if(promotionVSImeiRange)
                {
                    Object[] imeiRangeListQuery = cImeiRange.getParameterContainer().keySet().toArray();
                    Arrays.sort(imeiRangeListQuery);
                    String imeiRangeList = cImeiRange.getValueAsString((String)imeiRangeListQuery[Math.abs(Arrays.binarySearch(imeiRangeListQuery, imei+";Z"))-2]);
                    Map<String,Object> imeiRange = getDataContainer("IMEI_RANGES",imeiRangeList,true);
                    if ((mainID.compareTo((String)imeiRange.get("IMEI_RANGE_END")) <= 0))
                    {
                        DataObject cPromotionVsImeiRange = null;
                        try
                        {
                            cPromotionVsImeiRange = getEntity("PROMOTION_VS_IMEI_RANGE").getData();
                        }
                        catch (Exception e)
                        {
                        }

                        if (cPromotionVsImeiRange != null)
                        {
                            if (cPromotionVsImeiRange.parameterExists(promotionID+DOT+(String)imeiRange.get("IMEI_RANGE_ID")))
                            {
                                String PromotionVsImeiRangeList = cPromotionVsImeiRange.getValueAsString(promotionID+DOT+(String)imeiRange.get("IMEI_RANGE_ID"));
                                Map<String,Object> promotionVsImeiRange = getDataContainer("PROMOTION_VS_IMEI_RANGE",PromotionVsImeiRangeList);
                                
                                if ( firstIsGreatThanOrEqualSecond(dateTimeExecution,(String)promotionVsImeiRange.get("START_TIMESTAMP")) && firstIsGreatThanOrEqualSecond((String)promotionVsImeiRange.get("END_TIMESTAMP"),dateTimeExecution))
                                {
                                    return promotions;
                                }
                            }
                        }
                            
                    }
                }
                
            }
                        
            return new java.util.concurrent.ConcurrentHashMap<String,Object>();
        }
        catch (Exception e) 
        {
            throw createException(GET_PROMOTION_BY_PRODUCT_ID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    private Map<String,Object> getProductParameter(String sessionID,String productID) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
        return getProductParameter(session, productID); 
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getProductParameter(Map<String,Object> session,String productID) throws Exception
    {
        try 
        {
            Map<String,Object> productParameter = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            DataObject cProductVsParameter = getEntity("PRODUCTS_VS_PARAMETER").getData();
            
            if (!(cProductVsParameter.parameterExists(productID)))
            {
                return productParameter;
            }
            
            String productVsParameterList = cProductVsParameter.getValueAsString(productID);
            productParameter = getDataContainer("PRODUCTS_VS_PARAMETER",productVsParameterList);
            
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session); 
            
            HashSet<String> retainProductParameter = new HashSet<String>();
            
            for (final String param : productParameter.keySet())
            {
                if (( firstIsGreatThanOrEqualSecond(dateTimeExecution, (String)((Map<String,Object>)productParameter.get(param)).get("START_TIMESTAMP"))) 
                   && firstIsGreatThanOrEqualSecond((String)((Map<String,Object>)productParameter.get(param)).get("END_TIMESTAMP"),dateTimeExecution))
                {
                    retainProductParameter.add(param);
                }
            }
            
            productParameter.keySet().retainAll(retainProductParameter);
            
            return productParameter;
        } 
        catch (Exception e) 
        {
            throw createException(GET_PRODUCT_PARAMETER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
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
    
    private boolean isAnidadosProduct(String productId) throws Exception
    {
        //case doesn't have any product associated with any group
        try{
            getEntity("ANIDADOS_INFO").getData();
        }catch(Exception e){
            return false;
        }
        
        try 
        {
         List<String> anidadosList = getAllAnidadosProducts();
         
         for (String anidadoProductId : anidadosList) {
             if(anidadoProductId.equals(productId))
                return true;
         }
         
         return false; 
         
        }catch (Exception e)
        {
            throw createException(IS_ANIDADOS_PRODUCT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> getTheLatestVersionOfProductByName(String productName, String regionId) throws Exception
    {
        try 
        {
            Map<String,Object> productTable = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            Map<String,Object> productTableTmp = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            Map<String,Object> parameters = getContainerData("PRODUCTS_BY_NAME", productName + DOT + regionId, "PRODUCTS_BY_NAME", false);
            
            int versionTmp = -1;
            
            for ( final String parameter : parameters.keySet() )
            {
                String productID = ((String)((Map<String,Object>)parameters.get(parameter)).get("PRODUCT_ID"));
                productTableTmp  = getProductByID(productID);
                int productVersion = Integer.parseInt((String)(productTableTmp.get("productVersion")));
                //String regionIdCompare = (String) productTableTmp.get("regionId");
                //if(regionId.equals(regionIdCompare))
                //{
                    if (productVersion > versionTmp)
                    {
                        productTable = getProductByID(productID);
                        productTable.put("productID", productID);
                        versionTmp = productVersion;
                    }
                //}
            }
            
            return productTable;
        } 
        catch (Exception e) 
        {
            throw createException(GET_LATEST_PRODUCT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private
 Map<String,Object> getProductByID(String productID) throws Exception
    {
        try 
        {
            DataObject cProductById = getEntity("PRODUCTS_BY_ID").getData();
            String product = cProductById.getValueAsString(productID);
            Map<String,Object> productMap = getDataContainer("PRODUCTS_BY_ID",product,true);
            productMap.put("productID", productID);
            return  productMap;
        }
        catch (Exception e) 
        {
            throw createException(GET_PRODUCT_BY_ID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
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

            setDataMapVariables("ACTION."+responseID+".DATA_SEARCH.DATABASE", resultID, session);
            
            setDataMapVariables("ACTION."+responseID+".O.NUMBER_COMMAND", "1", session);
            
        } 
        catch (Exception e) 
        {
            throw createException(CLEAN_MAP_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }    
    
    @SuppressWarnings("unchecked")
    private void getAllCommercialProducts(String sessionID, String responseID) throws Exception
    {
        try 
        {
            Map<String,Object> session  = (Map<String,Object>)mapVariables.get(sessionID);
            String queryType = (String)(getDataMapVariables("REQUEST."+responseID+".0.queryType",session));
            Map<String,Object> resultID = getAllProducts(queryType,sessionID);
            setDataMapVariables("ACTION."+responseID+".STATUS", "OK", session);
            setDataMapVariables("ACTION."+responseID+".DATA_SEARCH.DATABASE", resultID, session);
            
        } 
        catch (Exception e) 
        {
            throw createException(GET_ALL_COMMERCIAL_PRODUCTS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
            
    @SuppressWarnings("unchecked")
    private Map<String, Object> getAllProducts(String queryType, String sessionID) throws Exception
    {
        try
        {
            Map<String, Object> session = (Map<String, Object>) mapVariables.get(sessionID);
            String hiddenProduct = (String) getDataMapVariables("CONFIG.HIDDEN_PRODUCT", session);

            Map<String, Object> allProducts = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            DataObject cProducts = getEntity("PRODUCTS_BY_ID").getData();

            int lineNumber = 0;

            for (final String parameter : (Set<String>) cProducts.getParameterContainer().keySet())
            {
                String dataProduct = cProducts.getValueAsString(parameter);
                Map<String, Object> parameters = getDataContainer("PRODUCTS_BY_ID", String.valueOf(dataProduct), true);
                parameters.put("productID", parameter);

                String productProperty = (String) parameters.get("productProperties");
                changePropertiesGetAllComercialProducts(parameters, sessionID);

                if ((queryType.equals("all") || !bitAndProperty(productProperty, hiddenProduct)))
                {
                    allProducts.put(String.valueOf(lineNumber), parameters);
                    lineNumber++;
                }
            }

            return allProducts;
        }
        catch (Exception e)
        {
            throw createException(GET_ALL_PRODUCTS, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void getSubscriptions(String sessionID, String responseID) throws Exception    
    {
        try 
        {
            Map<String,Object> session  = (Map<String,Object>)mapVariables.get(sessionID);
            String subscriptionStatus = (String)(getDataMapVariables("REQUEST."+responseID+".0.subscriptionStatus",session));
            String getVolume = (String)(getDataMapVariables("REQUEST."+responseID+".0.getVolume",session));
            int numberRequest = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+responseID+".NUMBER_SUBSCRIBER",session));
            String dateTimeExecution = (String)getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);

            for (int i = 0; i < numberRequest; i++)
            {
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".AIR_VOLUME_FLAG",ZERO,session);
                setDataMapVariables("INTERNAL."+responseID+DOT+i+".DATE_TIME_EXECUTION",dateTimeExecution,session);
                
                if (!isSubscriberInOurDatabase(responseID, session, i))
                {
                    formatErrorResponse(responseID,session,SUBSCRIBER_NOT_FOUND,"business",ERROR_SOURCE_MODULE,"Subscriber not found in database");
                    break;
                }
                
                if (!(fieldExistMapVariables("REQUEST."+responseID+".0.fromDate",session)))
                {
                    setDataMapVariables("REQUEST."+responseID+".0.fromDate","NULL",session);
                }

                if (!(fieldExistMapVariables("REQUEST."+responseID+".0.toDate",session)))
                {
                    setDataMapVariables("REQUEST."+responseID+".0.toDate","NULL",session);
                }

                
                validationAllRequestParameter("getSubscriptions",responseID,String.valueOf(i),session);

                if(is(getVolume))
                {
                    setDataMapVariables("INTERNAL."+responseID+DOT+i+".AIR_VOLUME_FLAG","1",session);
                    
                    if(subscriptionStatus.matches("all|active|suspended"))
                    {
                        //FORMAT HASHMAP AND GO TO AIR TO GET VOLUME
                        if (isSubscriberHasActiveSubscriptionWithThisProduct(responseID, session, i))
                        {
                            Map<String,Object> subscriptions = (Map<String,Object>)getDataMapVariables("TABLE."+responseID+DOT+i+".MAINID.SUBSCRIPTIONS", session);                         
                            Map<String,Object> airVolumeList = getAirVolumeList(subscriptions, session);                            
                            setDataMapVariables("TABLE."+responseID+DOT+i+".AIR_VOLUME_DEDICATED_ACCOUNT", airVolumeList, session);
                            findMainIDRange((String)getDataMapVariables("REQUEST."+responseID+DOT+i+".MAINID", session), Integer.parseInt(responseID), i, session);
                        }
                    }
                    else
                    {
                        throw createException(NOT_AIR_VOLUME,"business",ERROR_SOURCE_MODULE,"Innactive and Programmed subscriptions doesnt have volume inside Air");
                    }                   
                }
            }
            
        } 
        catch (Exception e) 
        {
            throw createException(GET_SUBSCRIPTIONS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> getAirVolumeList(Map<String,Object> subscriptions, Map<String,Object> session) throws Exception
    {
        try
        {
            Map<String,Object> airVolumeList = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            for(String idx : (Set<String>)subscriptions.keySet())
            {
                String productID = (String)((Map<String,Object>)subscriptions.get(idx)).get("PRODUCT_ID");
                Map<String,Object> airVolumeDedicatedAccount = 
                        getContainerData("AIR_VOLUME_DEDICATED_ACCOUNT", productID, "AIR_VOLUME_DEDICATED_ACCOUNT", true);
                airVolumeList.put(productID, airVolumeDedicatedAccount);
            }
            
            return airVolumeList;
        }
        catch (Exception e) 
        {
            throw createException(GET_AIR_VOLUME_LIST,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    
    private void changePropertiesGetAllComercialProducts(Map<String, Object> parameters, String sessionID) throws Exception
    {
        try
        {
            for (String config : PROPERTIES_GET_ALL_COMERCIAL_PRODUCTS)
            {
                String property = (String) parameters.get(config);
                if (property.isEmpty() || property.equals(ZERO))
                {
                    parameters.put(config, EMPTY);
                }
                else
                {
                    parameters.put(config, getPropertyAsString(property, sessionID));
                }
            }
        }
        catch (Exception e)
        {
            throw createException(CHANGE_PROPERTIES, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private String getPropertyAsString(String property, String sessionID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            Map<String,Object> config = (Map<String,Object>)getDataMapVariables("CONFIG", session);
            
            StringBuffer propertyAsString = new StringBuffer();
            String delimiter = EMPTY;
            
            if(property.equals(ZERO))
            {
                return EMPTY;
            }
            
            for(String variable : config.keySet())
            {
                String propertyConfig = (String)config.get(variable);
                
                if(bitAndProperty(property, propertyConfig))
                {
                    propertyAsString.append(delimiter+variable);
                    delimiter = PIPE;
                }
            }
            
            return propertyAsString.toString();
            
        }
        catch (Exception e) 
        {
            throw createException(GET_PROPERTY_AS_STRING,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private void formatRequestQuery(String sessionID,DataObject dataSource, String[] element) throws Exception
    {
        try 
        {
            removeAllDataSource(dataSource);
            
            dataSource.setParameter("SESSION_ID"        , sessionID                         );
            dataSource.setParameter("STATUS"            , "OK"                              );
            dataSource.setParameter("ORIGIN"            , "SearchAndValidation"             );
            dataSource.setParameter("TARGET"            , "SubscriberAndSubscriptionQuery"  );
            dataSource.setParameter("FIELD_NAME"        , element[2]                        );
            dataSource.setParameter("FIELD_QUERY"       , element[3]                        );
            dataSource.setParameter("REQUEST_NUMBER"    , element[0]                        );
            dataSource.setParameter("SUBSCRIBER_NUMBER" , element[1]                        );
        }
        catch (Exception e) 
        {
            throw createException(FORMAT_REQUEST_QUERY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String[] getElementQuery(String sessionID) throws Exception
    {
        try 
        {
            Map<String,Object> session = (Map<String,Object>)mapVariables.get(sessionID);
            List queryList = ((List)getDataMapVariables("QUERY_LIST",session));

            if(queryList.size() > 0)
            {
              String[] value=(String[])queryList.get(0);
              queryList.remove(0);
              return value;
            }
            
            return null;
        } 
        catch (Exception e) 
        {
            throw createException(GET_ELEMENT_QUERY,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void buildQueryList(String sessionID) throws Exception
    {
        try 
        {
            Map<String,Object> session      = (Map<String,Object>)mapVariables.get(sessionID);
            List queryList = new ArrayList<String>();
            session.put("QUERY_LIST", queryList);
            
            int numberRequest = Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION.NUMBER_REQUEST",session));
            
            Calendar now = Calendar.getInstance();
            
            for (int i=0; i < numberRequest; i++)
            {
                if (needOfDataSubscriberSubscription((String)getDataMapVariables("REQUEST."+i+".operation",session)))
                {
                    int numberSubscribers =  Integer.parseInt((String)getDataMapVariables("CONTROLLER.COUNT_TRANSACTION."+i+".NUMBER_SUBSCRIBER",session));
                    
                    for (int j = 0; j < numberSubscribers; j++)
                    {
                        if (fieldExistMapVariables("REQUEST."+i+DOT+j+".MAINID", session))
                        {
                            String mainID = (String) getDataMapVariables("REQUEST." + i + DOT + j + ".MAINID", session);
                            String[] elements = { String.valueOf(i), String.valueOf(j), "MAINID", mainID };
                            queryList.add(elements);
                        }
                        
                        if (fieldExistMapVariables("REQUEST."+i+DOT+j+".masterSubscriberMAINID", session))
                        {
                            String mainID = (String) getDataMapVariables("REQUEST." + i + DOT + j + ".masterSubscriberMAINID", session);
                            String[] elements = { String.valueOf(i), String.valueOf(j), "masterSubscriberMAINID",
                                    mainID };
                            queryList.add(elements);
                        }                        
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(BUILD_QUERY_LIST,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    /**
     * End MAINID the same code of the DBController
     */    
    
    private boolean needOfDataSubscriberSubscription(String operation) throws Exception
    {
        return operation.matches(OPERATIONS_THAT_NEED_OF_SUBSCRIBERS_AND_SUBSCRIPTIONS);
    }
    
    
    @SuppressWarnings("unchecked")
    private boolean fieldExistMapVariables(String field, Map<String,Object> mapVariables) throws Exception
    {
        String[] levels = field.split("\\.");
        int countLevel = levels.length;
        int i = 0;
        Object fieldValue = mapVariables;
         
        while (i < countLevel)
        {
            if (!(((Map<String,Object>)fieldValue).containsKey(levels[i])))
            {
                return false;
            }
            
            fieldValue = ((Map<String,Object>)fieldValue).get(levels[i]);
            i++;
        }
        
        return true;
    }
    
    
    @SuppressWarnings("unchecked")
    private <R> R getDataMapVariables(String field, Map<String,Object> mapVariables) throws Exception
    {
        String[] levels = field.split("\\.");
        int countLevel = levels.length;
        int i = 0;
        Object fieldValue = mapVariables;
         
        while (i < countLevel)
        {
            if (!(((Map<String,Object>)fieldValue).containsKey(levels[i])))
            {
                throw createException(FIELD_NOT_PRESENT,INTERNAL,ERROR_SOURCE_MODULE,"Field "+field+" not present in dataSource");
            }
            
            fieldValue = ((Map<String,Object>)fieldValue).get(levels[i]);
            i++;
        }
        
        return (R)fieldValue;
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
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> countTransactions(Map<String, Object> mapResquest) throws Exception
    {
        try 
        {
            Map<String,Object> countTransactions = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            int numberRequest = mapResquest.size();
            countTransactions.put("NUMBER_REQUEST", String.valueOf(numberRequest));
            
            for (int i=0; i < numberRequest; i++)
            {
                Map<String,Object> mapSubscribers = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                mapSubscribers.put("NUMBER_SUBSCRIBER", String.valueOf(((Map<String,Object>)mapResquest.get(String.valueOf(i))).size()-2));
                countTransactions.put(i+EMPTY, mapSubscribers);
                
            }
            return countTransactions;
        } 
        catch (Exception e) 
        {
            throw createException(COUNT_TRANSACTIONS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    /**
     * Mount the backup map.
     * @param dataSource The data source containing the request data.
     * @return A backup map with all data related to the request.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> mountBackupMap(final DataObject dataSource) throws Exception
    {

        try 
        {
            final Map<String, Object> requestMap      = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            final Map<String, Object> actionMap       = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            final Map<String, Object> tableMap        = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            final Map<String, Object> responseMap     = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            final Map<String, Object> controllerMap   = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            final Map<String, Object> internalMap     = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            final Map<String, Object> positionMap     = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            positionMap.put("REQUEST", ZERO);
            positionMap.put("SUBSCRIBER", ZERO);
            positionMap.put("COMMAND", ZERO);

            controllerMap.put("POSITION", positionMap);

            final Map<String, Object> sessionIDMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            sessionIDMap.put("REQUEST", requestMap);
            sessionIDMap.put("ACTION", actionMap);
            sessionIDMap.put("TABLE", tableMap);
            sessionIDMap.put("RESPONSE", responseMap);
            sessionIDMap.put("CONTROLLER", controllerMap);
            sessionIDMap.put("INTERNAL", internalMap);
            
            Date date = new Date(System.currentTimeMillis());  
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssZ");
                
            String dateTimeExecution = formatter.format(date);
    
            internalMap.put("DATE_TIME_EXECUTION", parseCalendarToDefaultStringWithDbTimeZone(parseDefaultStringToCalendarWithDbTimeZone(dateTimeExecution)));

            final TreeSet<String> reversedTreeSet = new TreeSet<String>(Collections.reverseOrder());
            reversedTreeSet.addAll(dataSource.getParameterContainer().keySet());

            Matcher matcher; 

            int lastRequestID      = -1;
            int lastSubscriberID = -1;

            for (final String key : reversedTreeSet)
            {
                if ((matcher = SUBSCRIBER_PARAMETER_PATTERN.matcher(key)).find())
                {
                    final String requestID = matcher.group(1);
                    final String subscriberID = matcher.group(2);
                    final Map<String, String> subscriberValuesMap;
                    final Map<String, Object> requestValuesMap = (Map<String, Object>)requestMap.get(requestID);

                    if (requestValuesMap == null)
                    {
                        throw createException(INVALID_REQUEST_ID,"malformed",ERROR_SOURCE_MODULE,"Invalid requestID "+requestID+DOT);
                    }

                    final int subscriberIDAsInt = Integer.parseInt(subscriberID);

                    if ((subscriberIDAsInt != lastSubscriberID) && (lastSubscriberID != -1) && ((subscriberIDAsInt + 1) != lastSubscriberID))
                    {
                        throw createException(INVALID_SUBSCRIBER_ID,"malformed",ERROR_SOURCE_MODULE,"Invalid subscriberID "+subscriberIDAsInt+DOT);
                    }

                    lastSubscriberID = subscriberIDAsInt;

                    if (requestValuesMap.containsKey(subscriberID))
                    {
                        subscriberValuesMap = (Map<String, String>) requestValuesMap.get(subscriberID);
                    }
                    else
                    {
                        requestValuesMap.put(subscriberID, subscriberValuesMap = new java.util.concurrent.ConcurrentHashMap<String, String>());
                        ((Map<String, Object>) actionMap.get(requestID)).put(subscriberID, new java.util.concurrent.ConcurrentHashMap<String, Object>());

                    }

                    subscriberValuesMap.put(matcher.group(3), validFieldRequest(dataSource,key));
                }
                else if ((matcher = REQUEST_PARAMETER_PATTERN.matcher(key)).find())
                {
                    final String requestID   = matcher.group(1);
                    final int requestIDAsInt = Integer.parseInt(requestID);

                    if ((requestIDAsInt != lastRequestID) && (lastRequestID != -1) && ((requestIDAsInt + 1) != lastRequestID))
                    {
                        throw createException(INVALID_REQUEST_ID,"malformed",ERROR_SOURCE_MODULE,"Invalid requestID "+requestIDAsInt+DOT);
                    }

                    lastRequestID = requestIDAsInt;

                    final Map<String, Object> requestValuesMap;

                    if (requestMap.containsKey(requestID))
                    {
                        requestValuesMap = (Map<String, Object>)requestMap.get(requestID);
                    }
                    else
                    {
                        lastSubscriberID = -1;
                        requestMap.put(requestID, requestValuesMap = new java.util.concurrent.ConcurrentHashMap<String, Object>());

                        final Map<String, Object> actionValues = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                        actionValues.put("STATUS", "PROCESSING_OK");

                        actionMap.put(requestID, actionValues);
                        tableMap.put(requestID, new java.util.concurrent.ConcurrentHashMap<String, Object>());
                        responseMap.put(requestID, new java.util.concurrent.ConcurrentHashMap<String, Object>());

                    }

                    requestValuesMap.put(matcher.group(2), validFieldRequest(dataSource,key));
                }
            }
            controllerMap.put("COUNT_TRANSACTION", countTransactions(requestMap));

            return sessionIDMap;
        } 
        catch (Exception e) 
        {
            throw createException(MOUNT_BACKUP_MAP,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private String validFieldRequest(DataObject dataSource, String fieldName) throws Exception
    {
        String field = dataSource.getValueAsString(fieldName);
        
        if(field.isEmpty())
        {
            Matcher matcher;
            
            if( (matcher = FIELD_REQUEST.matcher(fieldName)).find())
            {
                fieldName = matcher.group(4);
            }
            
            throw createException(REQUEST_FIELD_IS_EMPTY,INTERNAL,ERROR_SOURCE_MODULE,"The Field "+fieldName+" cannot receive null value");
        }
        return field;
    }
    
    //-----------------------------------------------------------------------------
    // setConfigVariables
    // AUTOR       : Ericsson / TelTools
    // DESCRIPTION :
    //
    //-----------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked") 
    private void setConfigVariables(String sessionID) throws Exception
    {
        try 
        {
            DataObject container;
            
            try 
            {
                container = getEntity("CONFIG").getData();
            } 
            catch (Exception e) 
            {
                throw createException(SET_CONFIG_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,"Not exist entity CONFIG.");
            }
            
            
            Map<String,Object> session   = ((Map<String,Object>)mapVariables.get(sessionID));
            Map<String,Object> config    = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            session.put("CONFIG",config); 
            
            ArrayList<String> variableNeed = new ArrayList<String>();
            
            for(String property : VARIABLES_CONFIG)
            {
                variableNeed.add(property);
            }
            
            for(final String variable : (Set<String>)container.getParameterContainer().keySet())
            {
                config.put(variable,container.getValueAsString(variable));
                if(variableNeed.contains(variable))
                {
                    variableNeed.remove(variable);
                }
            }
            
            if(!(variableNeed.isEmpty()))
            {
                throw createException(NOT_EXIST_VARIABLE,INTERNAL,ERROR_SOURCE_MODULE,"Not exist "+variableNeed.get(0)+" in variables of config");
            }
            
        } 
        catch (Exception e) 
        {
            throw createException(SET_CONFIG_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private Map<String, String> prepareErrorResponse(final String errorCode, final String errorDescription,String errorSource, String errorType)
    {
        final Map<String, String> errorResponseMap = new java.util.concurrent.ConcurrentHashMap<String, String>();
        errorResponseMap.put("ERROR_CODE", errorCode);
        errorResponseMap.put("ERROR_DESCRIPTION", errorDescription);
        errorResponseMap.put("ERROR_SOURCE", errorSource);
        errorResponseMap.put("ERROR_TYPE", errorType);

        return errorResponseMap;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getDataContainer(String layout, String data,boolean single) throws Exception
    {
        try 
        {
            Map<String,Object> dataContainer = getDataContainer(layout,data);
            if (single)
            {
                return (Map<String,Object>)dataContainer.get(ZERO);
            }
            return dataContainer;
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER_ERROR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }        
    }
    
    private Map<String,Object> getDataContainer(String layout, String data) throws Exception
    {
        try 
        {
            DataObject layoutContainer = getEntity("LAYOUT").getData();
            String[] fields = layoutContainer.getValueAsString(layout).split(FIELD_DELIMITER);
            int countFields = fields.length;
            
            Map<String,Object> lineDataContainer = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            String[] lines = data.split(LINE_DELIMITER);
            int linesNumber = lines.length;
            for( int i = 0; i < linesNumber; i++)
            {
                Map<String,Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String,Object>();
                String[] dataFields = lines[i].split(FIELD_DELIMITER);
                if((dataFields == null) || (dataFields.length != countFields))
                {
                    throw createException(DATA_NOT_MATCHED,INTERNAL,ERROR_SOURCE_MODULE,"The data not matched the layout ["+layout+PIPE+dataFields.length+PIPE+countFields+"]");
                }
                
                int j = 0;
                
                for(String dataField : dataFields)
                {
                    dataContainer.put(fields[j], String.valueOf(dataField));
                    j++;
                }
                
                j = 0;
                lineDataContainer.put(String.valueOf(i),dataContainer);
            }

            return lineDataContainer;    
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER_ERROR,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private Map<String,Object> copyHashMap(Map<String,Object> variableCopied,Map<String,Object> variable) throws Exception
    {
        try 
        {
            for(String idx : (Set<String>)variableCopied.keySet())
            {
                if( (variable.get(idx)) instanceof Map)
                {
                    variable.put(idx, getCopyHashMap((Map<String,Object>)variableCopied.get(idx)));
                }
                else
                {
                    variable.put(idx, variableCopied.get(idx));
                }
            }
            
            return variable;
        }
        catch (Exception e) 
        {
            throw createException(GET_COPY_HASH_MAP,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getCopyHashMap(Map<String,Object> variable) throws Exception
    {
        try 
        {
            Map<String,Object> variableCopy = new java.util.concurrent.ConcurrentHashMap<String,Object>();
            
            for(String idx : (Set<String>)variable.keySet())
            {
                if( (variable.get(idx)) instanceof Map)
                {
                    variableCopy.put(idx, getCopyHashMap((Map<String,Object>)variable.get(idx)));
                }
                else
                {
                    variableCopy.put(idx, variable.get(idx));
                }
            }
            
            return variableCopy;
        }
        catch (Exception e) 
        {
            throw createException(GET_COPY_HASH_MAP,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    
    private String getStype(String type) throws Exception
    {
        if(type.equals("prepaid"))
        {
            return ZERO;
        }
        return "1";
    }

    private String getEndHourDay(String dateTimeExecution)
    {
        return dateTimeExecution.substring(0, 8)+"235959"+dateTimeExecution.substring(14);
    }

    private boolean isDailyPlan(String productCategory)
    {
        return productCategory.equals("DailyPlan");
    }

    
    @SuppressWarnings("serial")
    private class TheProductCanBeNestedException extends Exception{
        
    }
    
    private boolean canINestMoreSubscription(Map<String, Object> allSubscriptions, String productId) throws TheProductCanBeNestedException, Exception {
        if (!isAnidadosProduct(productId)) {
            throw new TheProductCanBeNestedException();
        }
        
        String groupId = getAnidadosGroupId(productId);
        List<Map<String, Object>> allAnidadosSubscriptions = getAnidadosSubscriptions(allSubscriptions);
        List<Map<String, Object>> anidadosSubscriptionsByGroup = getAnidadosSubscriptionsByGroup(allAnidadosSubscriptions, groupId);
        
        return anidadosSubscriptionsByGroup.size() < getAmountFromAnidadoInfoBy(groupId);
        
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getAllAnidadosProducts() throws Exception
    {
        try 
        {
            Map<String, Object> groupList = getContainerData("ANIDADOS_GROUP", "ANIDADOS", "ANIDADOS_GROUP", false);
            Map<String, Object> anidadosInfo = null;
            String productId = null;
            List<String> anidadosList = new ArrayList<String>();
            for (final String index : (Set<String>)groupList.keySet())
            {
                anidadosInfo = getAnidadoInfoInGroupList(groupList, index);
                for (String infoAnidado : anidadosInfo.keySet()) {
                    Map <String, Object> singleAnidado = (Map<String, Object>) anidadosInfo.get(infoAnidado);
                    productId = (String) singleAnidado.get("PRODUCT_ID");
                    anidadosList.add(productId);
                }
            }
            
            return anidadosList;
        } 
        catch (Exception e) 
        {
            throw createException(GET_ALL_ANIDADOS_PRODUCTS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAnidadoInfoInGroupList(
            Map<String, Object> groupList, final String index)
            throws Exception {
        Map<String, Object> anidadosInfo;
        String groupName = (String)((Map<String,Object>)groupList.get(index)).get("GROUP");
        anidadosInfo = getAnidadoInfoBy(groupName);
        return anidadosInfo;
    }
    
    private Map<String,Object> getAnidadoInfoBy(String groupName) throws Exception{
        return getContainerData("ANIDADOS_INFO", groupName, "ANIDADOS_INFO", false);
    }
    
    private Integer getAmountFromAnidadoInfoBy(String groupId) throws Exception{
        return Integer.valueOf((String)getContainerData("ANIDADOS_INFO", groupId, "ANIDADOS_INFO", true).get("MAX_AMOUNT"));
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getAnidadosSubscriptions(Map<String, Object> all_subscriptions) throws Exception {
        List<String> productsElegidos = getAllAnidadosProducts();
        List<Map<String, Object>> subscriptionElegidos = new ArrayList<Map<String, Object>>();
        
        for (String index : all_subscriptions.keySet()) {
            Map<String, Object> subscription = (Map<String, Object>) all_subscriptions.get(index);
            if (productsElegidos.contains(subscription.get("PRODUCT_ID"))){
                subscriptionElegidos.add(subscription);
            }
        }
        return subscriptionElegidos;
    }
    
    private List<Map<String, Object>> getAnidadosSubscriptionsByGroup(List<Map<String, Object>> anidadosSubscriptions, String groupId) throws Exception {
        List<Map<String, Object>> subscriptionsByGroup = new ArrayList<Map<String,Object>>();
        
        for (Map<String, Object> subscription : anidadosSubscriptions) {
            String productIdSubscription = (String) subscription.get("PRODUCT_ID");
            if (getAnidadosGroupId(productIdSubscription).equals(groupId)) {
                subscriptionsByGroup.add(subscription);
            }
        }
        return subscriptionsByGroup;
    }
}
