package br.com.ericsson.teltools.dth.database;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.ericsson.teltools.dth.debug.Entity;

import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;

public class DBController
    extends    br.com.ericsson.teltools.dth.debug.JavaFormatterBase 
    implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface 
/** HEADER ENDS **/
{
    
    private static final String INTERNAL = "internal";


	private static final Long SESSION_TIME_ALIVE_IN_MILI_SEC = 10000l;


    final String ANATEL_ENTITY_CONTAINER = "ANATEL_533";

    //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
    Map<String, Object> mapVariables = new java.util.concurrent.ConcurrentHashMap<String, Object>();
    final String VARIABLES_CONFIG = "COMMAND|ROLLBACK|INDEBT_SUBSCRIBER|UNREGISTERED_SUBSCRIBER";
    
    final String FIELD_DELIMITER            = "\\<\\;\\>";
    final String PARAMETER_DELIMITER        = "\\<\\.\\.\\>";
    final String PARAMETER_FIELD_DELIMITER  = "\\<\\:\\:\\>";
    final String RECORD_DELIMITER           = "\\<\\|\\>";
    final String LINE_DELIMITER             = "\\<\\.\\>";
    final String ERROR_DELIMITER            = "<&>";
    
    final String ERROR_SOURCE_MODULE    = "DATABASE";
    
    final String FORMAT_DATE_TIME = "yyyyMMddHHmmssZ";
    
    //-----------------------------------------------------------------
    // Error constants
    //-----------------------------------------------------------------
    final String REMOVE_ALL_DATA_SOURCE         = "23000";       
    final String NOT_EXIST_VARIABLE             = "23001";       
    final String SET_CONFIG_VARIABLES           = "23002";       
    final String FORMAT_PROPERTY_SUBSCRIBER     = "23003";       
    final String FIELD_NOT_PRESENT              = "23004";       
    final String SET_DATA_MAP_VARIABLES         = "23005";       
    final String ORIGIN_NOT_FOUND               = "23006";       
    final String ORIGIN_NOT_DETERMINATED        = "23007";       
    final String DATABASE_ERROR_CODE            = "23008";       
    final String FORMAT_PARAMETER_BACKEND       = "23010";       
    final String GET_FUNC_BACKEND               = "23011";       
    final String GET_VALUE_FIELD                = "23012";       
    final String GET_FUNC_FORMATTED             = "23013";       
    final String GET_COMMAND                    = "23014";       
    final String DATA_NOT_MATCH_WITH_LAYOUT     = "23015";       
    final String GET_DATA_CONTAINER             = "23016";       
    final String GET_CONFIG_VARIABLES           = "23017";       
    final String GET_REQUEST_DATA               = "23018";       
    final String PARAMETER_MANDATORY            = "23019";
    final String PREPARE_MAINID                 = "23020";
    final String PROCESS_SP_THIRD_DIGITS        = "23021";
    final String REMOVE_NINTH_DIGIT             = "23022";
    final String PROCESS_TWELVE_DIGITS          = "23023";
    final String IS_TWELVE_DIGITS               = "23024";
    final String IS_SP_THIRD_DIGITS             = "23025";
    final String IS_FIRST_PERIOD                = "23026";
    final String IS_THIRD_PERIOD                = "23027";
    final String FIND_ANATEL_MAINID_RANGE       = "23028";
    final String GET_NINTH_DIGIT                = "23029";    
    final String IS_PRESENT_PROPERTY            = "23030";
    final String BITAND_PROPERTY                = "23031";
    final String GET_BLACK_BERRY_REQUEST_DATA   = "23032";
    final String GET_DB_TIME_ZONE               = "23034";
    final String PARSE_CAL_STR_DB_TZ            = "23035";
    final String IN_FIVE_MINUTES            = "23036";
    final String CLEAN_MAP_VARIABLES  			= "23036";
    
    public void initialize()
    {
        // This method will be called once during creation of class, add any initialization needed here
    }
    
    /** 
     * Executed for each MapEvent.
     *
     * @param obj, data object containing all
     * parameters in the MapEvent. Also contain
     * helpful methods to get the parameters
     * with suitable datatypes
     */
    @SuppressWarnings("unchecked")
    public void format(DataObject dataSource) throws Exception
    {
    	backupDataSource(dataSource);
    	
    	Entity entity = getEntity("BACKUP_DATA_SOURCE");
    	DataObject dataObject = entity.getData();
    	entity.setLifetime(10L);
    	String sessionID = dataObject.getValueAsString("SESSION_ID");
    	
        try 
        {
            if(dataObject.parameterExists("ORIGIN"))
            {
                String origin      = dataObject.getValueAsString("ORIGIN");
                
                
                if (origin.equals("BackEndController")) // Request coming from controller.
                {
                    
                    //-----------------------------------------------------------------------------
                    // Includ session id in mapVariables
                    //-----------------------------------------------------------------------------
                    //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                    mapVariables.put(sessionID, new java.util.concurrent.ConcurrentHashMap<String, Object>());
                    
                    //------------------------------------------------------
                    // Find variables of config
                    //------------------------------------------------------
                    
                    setConfigVariables(sessionID);
                    
                    // BEGIN: CLEAN MAP VARIABLES
                    if (dataSource.parameterExists("cleanMapVariables"))
                    {
                        cleanMapVariables(sessionID, "0", dataSource);
                        removeAllDataSource(dataSource);
                        dataSource.setParameter("TARGET", "DBResponse");
                        dataSource.setParameter("LASTCLEANED", "OK");
                        dataSource.setParameter("STATUS", "OK");
                        
                        return;
                    } 
                    // END: CLEAN MAP VARIABLES
                    
                    
                    //------------------------------------------------------
                    // Create back up of the request
                    //------------------------------------------------------
                    
                    doBackupMapVaribles(dataObject,sessionID);
                    
                    // ------------------------------------------------------
                    // Prepare mainID
                    // ------------------------------------------------------
                    
                    prepareMainID(sessionID);                    
                    
                    //------------------------------------------------------
                    // Create back up of the request
                    //------------------------------------------------------
                    
                    formatParameterBackEnd(sessionID, (String)((Map<String, Object>)mapVariables.get(sessionID)).get("STATUS"), dataObject);
                    
                }
                else if (origin.equals("DSMN_DATABASE") || (dataObject.parameterExists("DbLookUpResultCode"))) // Request coming from DSMN data base.
                {
                    if(dataObject.getValueAsInt("DbLookUpResultCode") != 0) // coming from DATABASE with error of the application
                    {
                        formatDataSourceError(sessionID,dataObject,dataObject.getValueAsString("DbLookUpResultDesc"),String.valueOf(dataObject.getValueAsInt("DbLookUpResultCode")),INTERNAL,"database","");
                    }
                    else // coming from DATABASE without error of the application
                    {
                        String[] resultDB = dataObject.getValueAsString("DB_LOOKUP_DATA").split(PARAMETER_DELIMITER);
                        
                        String status = (resultDB[0].split(PARAMETER_FIELD_DELIMITER))[1];
                        
                        if(status.equals("OK")) // Successes transaction
                        {
                            int idxResultDB         = resultDB.length;
                            String dataLogging      = null;
                            String rollBackLayout   = null;
                            String dataRollBack     = null;
                            String queryDataLayout  = null;
                            String dataSearch       = null;
                            String msg              = null;
                            String msgLayout        = null;
                            
                            for(int i = 1 ; i < idxResultDB ;i++)
                            {
                                String parameterName = (resultDB[i].split(PARAMETER_FIELD_DELIMITER))[0];
                                String parameterValue = (resultDB[i].split(PARAMETER_FIELD_DELIMITER))[1];
                                
                                if(parameterName.equals("DATA_LOGGING"))
                                {
                                    dataLogging = parameterValue;
                                }
                                else if(parameterName.equals("DATA_ROLLBACK"))
                                {
                                    dataRollBack = parameterValue;
                                }
                                else if(parameterName.equals("ROLLBACK_LAYOUT"))
                                {
                                    rollBackLayout = parameterValue;
                                }
                                else if(parameterName.equals("QUERY_DATA_LAYOUT"))
                                {
                                    queryDataLayout = parameterValue;
                                }
                                else if(parameterName.equals("DATA_SEARCH"))
                                {
                                    dataSearch = parameterValue;
                                }
                                else if(parameterName.equals("MSG"))
                                {
                                    msg = parameterValue;
                                }
                                else if(parameterName.equals("MSG_LAYOUT"))
                                {
                                    msgLayout = parameterValue;
                                }
                                else
                                {
                                    throw createException(PARAMETER_MANDATORY, INTERNAL, ERROR_SOURCE_MODULE, "The field dont know["+parameterName+"].");
                                }
                            }
                            
                            if(dataLogging == null || rollBackLayout == null || dataRollBack == null)
                            {
                                throw createException(PARAMETER_MANDATORY, INTERNAL, ERROR_SOURCE_MODULE, "The field DATA_LOGGING, DATA_ROLLBACK and ROLLBACK_LAYOUT are mandatory.");
                            }

                            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                            Map<String, Object> dataRollback = getReturnDataDB(rollBackLayout, dataRollBack);
                            Map<String, Object> queryData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                            Map<String, Object> msgData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                            
                            if(dataSearch != null && queryDataLayout != null)
                            {
                                queryData = getData(queryDataLayout, dataSearch);
                            }
                            
                            if(msg != null && msgLayout != null)
                            {
                                msgData = getReturnDataDB(msgLayout, msg);
                            }
                            
                            formatResponseController(dataObject,sessionID,dataRollback,dataLogging,queryData,msgData);
                               
                        }
                        else // Transaction with error
                        {
                            String errorCode        = (resultDB[1].split(PARAMETER_FIELD_DELIMITER))[1];
                            String errorType        = (resultDB[2].split(PARAMETER_FIELD_DELIMITER))[1];
                            String errorSource      = (resultDB[3].split(PARAMETER_FIELD_DELIMITER))[1];
                            String errorDescription = (resultDB[4].split(PARAMETER_FIELD_DELIMITER))[1];
                            String dataLogging      = (resultDB[5].split(PARAMETER_FIELD_DELIMITER))[1];
                            formatDataSourceError(sessionID,dataObject,errorDescription,errorCode,errorType,errorSource,dataLogging);
                        }
                    }
                }
                else
                {
                    throw createException(ORIGIN_NOT_FOUND, INTERNAL, ERROR_SOURCE_MODULE, "The field ORIGIN not known");
                }
            }
            else
            {
                throw createException(ORIGIN_NOT_DETERMINATED, INTERNAL, ERROR_SOURCE_MODULE, "The field ORIGIN not determinated");
            }
            
        } 
        catch (Exception error) 
        {
            
            String errorCode        = DATABASE_ERROR_CODE;
            String errorType        = INTERNAL;
            String errorSource      = ERROR_SOURCE_MODULE;
            String errorDescription = formatErroDescription(error);
            
            if ((error.getMessage() != null) && (error.getMessage().matches("(.*)DATABASE_ERROR(.+)")))
            {
                String[] errors     = error.getMessage().split("<&>");
                errorCode           = errors[1];
                errorType           = errors[2];
                errorSource         = errors[3];
                errorDescription    = errors[4];
            }            
            
            formatDataSourceError(sessionID,dataObject,errorDescription,errorCode,errorType,errorSource,"");
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

	private void backupDataSource(DataObject dataSource){
    	Entity backupDataSource = createEntity("BACKUP_DATA_SOURCE");
    	backupDataSource.setData(dataSource);
    	try {
			putEntity(backupDataSource);
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void writeEntiy(String nameEntity, DataObject dataEntity){
    	Entity entity = createEntity(nameEntity);
    	
    	entity.setData(dataEntity);
    	
    	try {
			putEntity(entity);
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private void writeEntiy(String nameEntity, Map map, DataObject dataObject , String keyMap){
    	
    	dataObject.setParameter(keyMap, map);
    	
    	Entity entity = createEntity(nameEntity);
    	
    	entity.setData(dataObject);
    	
    	try {
			putEntity(entity);
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void fazTudo(DataObject dataObject,String nameEntity,Map map,String keyMap) throws Exception{
    	
    	removeAllDataSource(dataObject);
    	
    	writeEntiy(nameEntity, map, dataObject, keyMap);
    	
    }
    
    
    
    
    

    
    @SuppressWarnings("unchecked")
    private void prepareMainID(String sessionID) throws Exception {
        Map<String, Object> session = (Map<String, Object>) mapVariables.get(sessionID);

        String dateTimeExecution = (String) getDataMapVariables("INTERNAL.DATE_TIME_EXECUTION", session);
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
        now.setTime(simpleDateFormat.parse(dateTimeExecution));

        String mainID = (String) getDataMapVariables("REQUEST.SUBSCRIBER.MAINID", session);
        mainID = prepareMainID(now, mainID, session);
        setDataMapVariables("REQUEST.SUBSCRIBER.MAINID", mainID, session);
    }

    /**
     * Begin MAINID the same code of the SearchAndValidation
     */
    private String prepareMainID(Calendar requestDate, String mainID,
            Map<String, Object> statusOut) throws Exception{
        try {
            if (isNoSpTwelveDigits(mainID)) {
                return mainID;
            }
            
            if (isSpTwelveDigits(mainID)) {
                return processSpTwelveDigits(requestDate, mainID);
            }
            
            if (isSpThirdDigits(mainID)) {
                return processSpThirdDigits(requestDate, mainID);
            }
            
            throw new Exception("Invalid MAINID");
        } catch (Exception e) {
            throw createException(PREPARE_MAINID,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }

    private String processSpThirdDigits(Calendar requestDate, String mainID) throws Exception
             {
        try {
            String mainID8Digitis = removeNinthDigit(mainID);
            
            Map<String, Object> range = findAnatelMainIDRange(mainID8Digitis);
            String  anatelNinthDigit = (String) range.get("NEW_DIGIT");
            
            if(incorrectNinthDigit(getNinthDigit(mainID), anatelNinthDigit)){
                throw new Exception(String.format("Invalid ninth digit [%s] for MAINID [%s]", anatelNinthDigit, mainID));
            }
            if (isFirstPeriod(mainID8Digitis, requestDate, range)) {
                throw new Exception(String.format("Invalid MAINID [%s] for this period [%s]", mainID8Digitis, requestDate));
            }
            if (isThirdPeriod(mainID8Digitis, requestDate, range)){
                return mainID;
            }
            return mainID8Digitis;          
        } catch (Exception e) {
            throw createException(PROCESS_SP_THIRD_DIGITS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }

    private boolean incorrectNinthDigit(String ninthDigit, String anatelNinthDigit) {
        return !ninthDigit.equals(anatelNinthDigit);
    }

    private String getNinthDigit(String mainID) throws Exception {
        try {
            return mainID.substring(4, 5);      
        } catch (Exception e) {
            throw createException(GET_NINTH_DIGIT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }

    private String removeNinthDigit(String mainID) throws Exception {
        try {
            return mainID.substring(0, 4) + mainID.substring(5);            
        } catch (Exception e) {
            throw createException(REMOVE_NINTH_DIGIT,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }

    private String processSpTwelveDigits(Calendar requestDate, String mainID)
            throws Exception {
        try {
            Map<String, Object> range = findAnatelMainIDRange(mainID);
            if (isThirdPeriod(mainID, requestDate, range)){
                throw new Exception(String.format("Invalid MAINID [%s] for this period [%s]", mainID, requestDate));
            }
            return mainID;          
        } catch (Exception e) {
            throw createException(PROCESS_TWELVE_DIGITS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }

    private boolean isSpTwelveDigits(String mainID) throws Exception{
        try {
            Pattern twelve = Pattern.compile("^\\d{2}11\\d{8}$");
            Matcher matcher  = twelve.matcher(mainID);
            return matcher.find() && mainID.length() == 12;         
        } catch (Exception e) {
            throw createException(IS_TWELVE_DIGITS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }
    
    private boolean isNoSpTwelveDigits(String mainID) throws Exception{
        try {
            Pattern twelveThirdNoOne = Pattern.compile("^\\d{2}[^1]\\d{9}$");
            Pattern twelveFourthNoOne = Pattern.compile("^\\d{3}[^1]\\d{8}$");
            return twelveThirdNoOne.matcher(mainID).find() || twelveFourthNoOne.matcher(mainID).find();         
        } catch (Exception e) {
            throw createException(IS_TWELVE_DIGITS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }
    
    private boolean isSpThirdDigits(String mainID) throws Exception{
        try {
            Pattern third = Pattern.compile("^\\d{2}11\\d{9}$");
            return third.matcher(mainID).find();
        } catch (Exception e) {
            throw createException(IS_SP_THIRD_DIGITS,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    }   
    
    private boolean isFirstPeriod(String mainID, Calendar requestDate, Map<String, Object> range) throws Exception{ 
        try {
            Calendar startPhase2 = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
            
            startPhase2.setTime(simpleDateFormat.parse((String) range.get("START_PHASE2")));

            if (requestDate.before(startPhase2)) {
                return true;
            }   
            
            return false;           
        } catch (Exception e) {
            throw createException(IS_FIRST_PERIOD,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }

    }   
    
    private boolean isThirdPeriod(String mainID, Calendar requestDate, Map<String, Object> range) throws Exception{ 
        try {
            Calendar startPhase3 = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
            
            startPhase3.setTime(simpleDateFormat.parse((String) range.get("START_PHASE3")));

            if (requestDate.after(startPhase3)) {
                return true;
            }   
            
            return false;           
        } catch (Exception e) {
            throw createException(IS_THIRD_PERIOD,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e)+mapVariables);
        }
    } 
    
    private Map<String, Object> findAnatelMainIDRange(String mainID) throws Exception
    {
        try 
        {
            if (!isSpTwelveDigits(mainID)) {
                throw new Exception("Invalid MAINID to find MAINID Range.");
            }
            
            DataObject cMainIDRange = getEntity(ANATEL_ENTITY_CONTAINER).getData();
            Object[] mainIDRangeListQuery = cMainIDRange.getParameterContainer().keySet().toArray();
            
            Arrays.sort(mainIDRangeListQuery);
            
            int index = Math.abs(Arrays.binarySearch(mainIDRangeListQuery, mainID+";Z"))-2;
            
            String mainIDRangeList = cMainIDRange.getValueAsString((String)mainIDRangeListQuery[index]);
            
            Map<String, Object> range = getDataContainer(ANATEL_ENTITY_CONTAINER,mainIDRangeList,true);
            
            if (mainID.compareTo((String)range.get("MAINID_RANGE_END")) > 0) {
                throw new Exception("MAINID Range not found.");
            }
            
            return getDataContainer(ANATEL_ENTITY_CONTAINER,mainIDRangeList,true);
            
        } 
        catch (Exception e) 
        {
            throw createException(FIND_ANATEL_MAINID_RANGE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
    }
    /**
     * End MAINID the same code of the SearchAndValidation
     */    
    
    /**
     * @param dataSource
     * @param sessionID
     * @param dataRollback
     * @param dataLogging
     * @throws Exception
     */
    private void formatResponseController(DataObject dataSource, String sessionID,Map<String, Object> dataRollback, String dataLogging, Map<String, Object> queryData, Map<String, Object> msgData) throws Exception
    {
        removeAllDataSource(dataSource);
        
        if(mapVariables.containsKey(sessionID))
        {
            mapVariables.remove(sessionID);
        }
        
        dataSource.setParameter("STATUS"        , "OK"          );
        dataSource.setParameter("DATA_ROLLBACK" , dataRollback  );
        dataSource.setParameter("DATA_LOGGING"  , dataLogging   );        
        dataSource.setParameter("TARGET"        ,"DBResponse"   );
        dataSource.setParameter("ORIGIN"        ,"DATABASE"     );    
        dataSource.setParameter("DATA_SEARCH"   , queryData     );
        dataSource.setParameter("MSG_DATA"      , msgData       ); 
        
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
    private void formatDataSourceError(String sessionID, DataObject dataSource, String errorDescription, String errorCode, String errorType, String errorSource,String dataLogging) throws Exception
    {
        
        removeAllDataSource(dataSource);
        
        if(mapVariables.containsKey(sessionID))
        {
            mapVariables.remove(sessionID);
        }
        
        dataSource.setParameter("SESSION_ID"        , sessionID            );
        dataSource.setParameter("ORIGIN"            , "DATABASE"           );
        dataSource.setParameter("TARGET"            , "DBResponse"       );
        dataSource.setParameter("STATUS"            , "ERROR"              );
        dataSource.setParameter("ERROR_DESCRIPTION" , errorDescription   );
        dataSource.setParameter("ERROR_SOURCE"      , errorSource          );
        dataSource.setParameter("ERROR_TYPE"        , errorType             );
        dataSource.setParameter("ERROR_CODE"        , errorCode             );
        dataSource.setParameter("DATA_LOGGING"         , dataLogging);
    }
    
    /**
     *  
     * @param dataSource
     * @param sessionID
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void doBackupMapVaribles(DataObject dataSource, String sessionID) throws Exception
    {
        Map<String, Object> session = ((Map<String, Object>)mapVariables.get(sessionID));
        
        for(final String params : dataSource.getParameterContainer().keySet())
        {
            session.put(params,dataSource.getValue(params));
        }
        
        removeAllDataSource(dataSource);
    }
    
    @SuppressWarnings("unchecked")
    private void formatParameterBackEnd(String sessionID,String status, DataObject dataSource) throws Exception
    {
        try 
        {
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            
            String businessRuleID = (String)session.get("BUSINESS_RULE_ID");
            String flowSeq          = (String)session.get("FLOW_SEQ");
            
            String commandID = getCommand(sessionID,businessRuleID,flowSeq,status);
            
            if(commandID == null)
            {
                throw new Exception("Function formatParameterBackEnd - Command not found");
            }
            
            removeAllDataSource(dataSource);
            
            String functionDB = getFuncBackEnd(commandID,sessionID);
            
            dataSource.setParameter("ORIGIN"    , "DBController");
            dataSource.setParameter("TARGET"    , "DSMN_DB");
            dataSource.setParameter("SESSION_ID", sessionID);
            dataSource.setParameter("FUNC_DB"    , functionDB);
            
            
            
        } 
        catch (Exception e) 
        {
            throw createException(FORMAT_PARAMETER_BACKEND, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    
    
    /**
     * @param commandID
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getFuncBackEnd(String commandID,String sessionID) throws Exception
    {
        try 
        {
            DataObject containerDB = getEntity("COMMANDS").getData();
            Map<String, Object> commandLines = getDataContainer("COMMANDS",containerDB.getValueAsString(commandID));
            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> fieldValue   = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            
            for(final String key : (Set<String>)commandLines.keySet())
            {
                Map<String, Object> commandLine = (Map<String, Object>)commandLines.get(key);
                fieldValue.put((String)commandLine.get("FIELD"), getValueField(commandLine,sessionID));
                
            }
            
            return getFuncFormatted(fieldValue);
        }
        catch (Exception e) 
        {
            throw createException(GET_FUNC_BACKEND, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private Object instantiateJavaClass(final String javaClassLiteral) throws Exception
    {
        final Matcher m = Pattern.compile("(.+)\\((.+)\\)").matcher(javaClassLiteral);
        m.find();
        return Class.forName(m.group(1)).getConstructor(String.class).newInstance(m.group(2));
    }
    

    
    /**
     * @param sessionID
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getRequestData(String sessionID) throws Exception
    {
        try 
        {
            StringBuffer requestString = new StringBuffer();

            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            Map<String, Object> request = (Map<String, Object>)session.get("REQUEST");

            requestString.append("0.operation<|>"+(String)request.get("operation"));
            requestString.append("<#>0.productName<|>"+(String)request.get("productName"));
            
            Map<String, Object> subscriber = (Map<String, Object>)request.get("SUBSCRIBER");
            
            for (final String item : (Set<String>)subscriber.keySet())
            {
                requestString.append("<#>0.0."+item+"<|>"+(String)subscriber.get(item));
            }
        
            return requestString.toString();
        } 
        catch (Exception e) 
        {
            throw createException(GET_REQUEST_DATA, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
        
    }
    
    
    /**
     * This method is used in DB commands when the request has a BlackBerry product.
     * 
     * @param sessionID
     * @return the request on the PendingAction format.
     * @throws Exception
     */
    private String getBlackBerryRequestData(String sessionID) throws Exception
    {
        try 
        {
            String blackBerryCheck = "<#>0.0.blackBerryCheck<|>true";
            String requestData = getRequestData(sessionID).replace(blackBerryCheck, "");
            
            StringBuffer requestString = new StringBuffer();
            requestString.append(requestData);
            requestString.append(blackBerryCheck);
            
            return requestString.toString();
        } 
        catch (Exception e) 
        {
            throw createException(GET_BLACK_BERRY_REQUEST_DATA, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private Object getValueField(Map<String, Object> commandLine,String sessionID) throws Exception
    {
        try 
        {    
            String action = (String)commandLine.get("ACTION");
            String value  = (String)commandLine.get("VALUE");
            Map<String, Object> session = (Map<String, Object>)mapVariables.get(sessionID);
            Object fieldValue = null;
            
            if(action.equals("FUNCTION"))
            {
                if (value.equals("getRequestData"))
                {
                    fieldValue = getRequestData(sessionID);
                }
                else if (value.equals("getBlackBerryRequestData"))
                {
                    fieldValue = getBlackBerryRequestData(sessionID);
                }                
                else if (value.equals("inFiveMinutes"))
                {
                    fieldValue = inFiveMinutes();
                }
                else
                {
                    throw new Exception("Function getValueField - Function "+value+" not known");
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
                return getDataMapVariables(value, session);
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
    
    /**
     * @param fieldValue
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getFuncFormatted(Map<String, Object> fieldValue) throws Exception
    {
        try 
        {
            StringBuffer functionDB = new StringBuffer();
            
            if(!fieldValue.containsKey("FUNCTION"))
            {
                throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "Id function not found");
            }
            
            String functionName = (String)fieldValue.get("FUNCTION");
            
            DataObject containerDB     = getEntity("LAYOUT_FUNC").getData();
            
            Map<String, Object> params = getDataContainer("LAYOUT_FUNC",containerDB.getValueAsString(functionName));
            
            if(params == null || params.isEmpty()){
            	throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "The function ["+functionName+ "] does not have LAYOUT_FUNC on DATA_CONTAINER_DB.");
            }
            
            functionDB.append(fieldValue.get("FUNCTION")+"(");
            
            boolean flag = true;
            String separatorParameter = "";
            
            int paramsLength = params.size();

            //-----get validation_expression data
            Entity entity = getEntity("VALIDATION_EXPRESSION");
            DataObject validationData = null;
            if(entity == null){
            	throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "There is not entity [VALIDATION_EXPRESSION] on DATA_CONTAINER_DB.");
            }else{
            	validationData = entity.getData();
            }
            
            if(validationData == null){
            	throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "There is not data of VALIDATION_EXPRESSION on DATA_CONTAINER_DB.");
            }
            
            Map<String,Object> validationParams = getDataContainer("VALIDATION_EXPRESSION",validationData.getValueAsString(functionName));
            
            if(validationParams == null || validationParams.isEmpty()){
            	throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "The function ["+functionName+ "] does not have validation expression on DATA_CONTAINER_DB.");
            }

            //-----
            
            for(int i = 0; i < paramsLength ; i++)
            {
                Map<String,Object> parameters = (Map<String,Object>)params.get(String.valueOf(i));
                String parameter = (String)parameters.get("PARAMETER");
                String type      = (String)parameters.get("TYPE");
                
                //-----validate expression data 
                Map<String,Object> currentParam = (Map<String, Object>) validationParams.get(String.valueOf(i));
                String validationParameter = (String) currentParam.get("PARAMETER");
                Pattern validationPattern = Pattern.compile((String)currentParam.get("REGEXP_VALUES"));
                
                for(Map.Entry<String, Object> entry : fieldValue.entrySet()){
                    String parameterValue ="";
                    if(entry.getValue() != null){
                        parameterValue = entry.getValue().toString();
                    }
                    if(!parameterValue.equals("") && !parameterValue.equalsIgnoreCase("null")){
                        if(entry.getKey().equals(validationParameter)){
                            if(!validationPattern.matcher(parameterValue).find() ){
                                throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, "The data "+parameterValue+" did not match the regexp layout "+validationPattern.toString()+"   .");
                            }
                        }
                    }
                }
                //-----
                
                if (type.equals("String"))
                {
                    functionDB.append(separatorParameter+"'"+(String)fieldValue.get(parameter)+"'");
                }
                else
                {
                    functionDB.append(separatorParameter+(String)fieldValue.get(parameter));
                }
                
                if (flag)
                {
                    separatorParameter = ",";
                    flag = false;
                }
            }
            functionDB.append(")");
            return functionDB.toString();
        } 
        catch (Exception e) 
        {
            throw createException(GET_FUNC_FORMATTED, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    
	@SuppressWarnings("unchecked")
	private String getFuncFormattedFunc(Map<String, Object> fieldValue)	throws Exception {
		try {
			StringBuffer functionDB = new StringBuffer();
			if (!fieldValue.containsKey("FUNCTION")) {
				throw createException(GET_FUNC_FORMATTED, INTERNAL,	ERROR_SOURCE_MODULE, "Id function not found");
			}
			String functionName = (String) fieldValue.get("FUNCTION");
			DataObject containerDB = getEntity("LAYOUT_FUNC").getData();
			Map<String, Object> params = getDataContainer("LAYOUT_FUNC",containerDB.getValueAsString(functionName));
			functionDB.append(fieldValue.get("FUNCTION") + "(");
			boolean flag = true;
			String separatorParameter = "";
			int paramsLength = params.size();
			for (int i = 0; i < paramsLength; i++) {
				Map<String, Object> parameters = (Map<String, Object>) params
						.get(String.valueOf(i));
				String parameter = (String) parameters.get("PARAMETER");
				String type = (String) parameters.get("TYPE");
				if (type.equals("String")) {
					functionDB.append(separatorParameter + "'" + (String) fieldValue.get(parameter) + "'");
				} else {
					functionDB.append(separatorParameter + (String) fieldValue.get(parameter));
				}
				if (flag) {
					separatorParameter = ",";
					flag = false;
				}
			}
			functionDB.append(")");
			return functionDB.toString();
		} catch (Exception e) {
			throw createException(GET_FUNC_FORMATTED, INTERNAL,	ERROR_SOURCE_MODULE, formatErroDescription(e));
		}
	}
    
    @SuppressWarnings("unchecked")
    private String getCommand(String sessionID, String businessRuleID, String flowSeq, String status) throws Exception
    {
        try 
        {
            Map<String, Object> session      = ((Map<String, Object>)mapVariables.get(sessionID));
            DataObject containerDB              = getEntity("FLOWS_COMMANDS").getData();
            Map<String, Object> commandLine = getDataContainer("FLOWS_COMMANDS",containerDB.getValueAsString(businessRuleID+"."+flowSeq));

            for (final String command  : (Set<String>)commandLine.keySet())
            {
                Map<String, Object> com = (Map<String, Object>)commandLine.get(command);
                String commandProperty = (String)com.get("COMMAND_PROPERTY");
                
                if(status.equals("EXECUTE"))
                {
                    if(isPresentProperty(commandProperty, "COMMAND", session))
                    {
                        return ((String)com.get("COMMAND_ID"));
                    }
                }
                else if (status.equals("ROLLBACK"))
                {
                    if(isPresentProperty(commandProperty, "ROLLBACK", session))
                    {
                        return ((String)com.get("COMMAND_ID"));
                    }
                }
            }
            
            return null;    
        } 
        catch (Exception e) 
        {
            throw createException(GET_COMMAND, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
        
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
            throw createException(GET_DATA_CONTAINER,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }        
    }


    private Map<String, Object> getData(String layout, String data) throws Exception
    {
        try 
        {
            String[] fields = layout.split(FIELD_DELIMITER);
            int countFields = fields.length;
            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> lineDataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            String[] lines = data.split(LINE_DELIMITER);
            int i = 0;
            
            for(final String line : lines)
            {
                //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                Map<String, Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                String[] dataFields = line.split(FIELD_DELIMITER);
                
                if((dataFields == null) || (dataFields.length != countFields))
                {
                    throw createException(DATA_NOT_MATCH_WITH_LAYOUT, INTERNAL, ERROR_SOURCE_MODULE, "Data do not match the layout ["+layout+"] data ["+data+"]");
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
            throw createException(GET_DATA_CONTAINER, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
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
            String[] lines = data.split(LINE_DELIMITER);
            int i = 0;
            
            for(final String line : lines)
            {
                //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
                Map<String, Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
                String[] dataFields = line.split(FIELD_DELIMITER);
                
                if((dataFields == null) || (dataFields.length != countFields))
                {
                    throw createException(DATA_NOT_MATCH_WITH_LAYOUT, INTERNAL, ERROR_SOURCE_MODULE, "Data do not match the layout ["+layout+"] data ["+data+"]");
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
            throw createException(GET_DATA_CONTAINER, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    private Map<String, Object> getReturnDataDB(String layout, String data) throws Exception
    {
        try 
        {
            String[] fields = layout.split(FIELD_DELIMITER);
            int countFields = fields.length;

            //Replaced old 'HashMap' implementation with 'ConcurrentHashMap' to improve thread safety
            Map<String, Object> dataContainer = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            String[] dataFields = data.split(FIELD_DELIMITER);
            
            if((dataFields == null) || (dataFields.length != countFields))
            {
                throw createException(DATA_NOT_MATCH_WITH_LAYOUT, INTERNAL, ERROR_SOURCE_MODULE, "Data do not match the layout ["+layout+"] data ["+data+"]");
            }
            
            int j = 0;
            
            for(final String dataField : dataFields)
            {
                dataContainer.put(fields[j], dataField);
                j++;
            }
            
            return dataContainer;    
        } 
        catch (Exception e) 
        {
            throw createException(GET_DATA_CONTAINER, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void setConfigVariables(String sessionID) throws Exception
    {
        try 
        {
            DataObject container             = getEntity("CONFIG").getData();
            Map<String, Object> session   = ((Map<String, Object>)mapVariables.get(sessionID));

            String[] variablesList = VARIABLES_CONFIG.split("\\|");

            for (String variable : variablesList)
            {
                if(container.parameterExists(variable))
                {
                    setDataMapVariables("CONFIG."+variable, container.getValueAsString(variable), session);
                }
                else
                {
                    throw createException(NOT_EXIST_VARIABLE,INTERNAL,ERROR_SOURCE_MODULE,"Not exist "+variable+" in variables of config");
                }
            }
        } 
        catch (Exception e) 
        {
            throw createException(SET_CONFIG_VARIABLES,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
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
    
    private void removeAllDataSource(final DataObject dataSource) throws Exception
    {
        try 
        {
            HashSet<String> retainDataSource = new HashSet<String>();
            retainDataSource.add("__GlobalTransactionID");
            retainDataSource.add("WS_requestID");
            retainDataSource.add("SESSION_ID");
            
            dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
        }
        catch (Exception e) 
        {
            throw createException(REMOVE_ALL_DATA_SOURCE,INTERNAL,ERROR_SOURCE_MODULE,formatErroDescription(e));
        }
        
    }
    
    private Exception createException(String errorCode,String errorType, String errorSource, String errorDescription) throws Exception
    {
        if ((errorDescription != null) && (errorDescription.matches("(.*)DATABASE_ERROR(.+)")))
        {
            return new Exception(errorDescription);
        }
        
        return new Exception("DATABASE_ERROR<&>"+errorCode+"<&>"+errorType+"<&>"+errorSource+"<&>"+errorDescription);
    }
    
    private String formatErroDescription(Exception e) 
    {
        return new StringBuilder(e.getClass().getCanonicalName()).append(":").append(e.getMessage()).toString();
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
    
    @SuppressWarnings({ "unchecked", "unused" })
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
    
    /**
     * @return the current date updated in five minutes
     * @throws Exception
     */
    private String inFiveMinutes() throws Exception {
        try 
        {
            DbTimeZone zone = new DbTimeZone();
            
            Calendar time = Calendar.getInstance(TimeZone.getTimeZone(zone.getOnlyNumberId()));
            time.add(Calendar.MINUTE, 5);
        
            return parseCalendarToDefaultStringWithDbTimeZone(time);
        } catch (Exception e) {
            throw createException(IN_FIVE_MINUTES, INTERNAL, ERROR_SOURCE_MODULE, formatErroDescription(e));
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
    
    private StringBuilder appendTwoDigits(StringBuilder parsedDate, int date)
    {
     if (date <= 9) {
         parsedDate.append(0);
     }
     parsedDate.append(date);
     return parsedDate;
    }    
    
    private class DbTimeZone 
    {
        
        public TimeZone dbTimeZone;

        public DbTimeZone() throws Exception 
        {
            try {
                DataObject dbInfo = getEntity("DB_INFOS").getData();
                dbTimeZone = TimeZone.getTimeZone(dbInfo.getValueAsString("TIMEZONE"));
            } catch (Exception e) {
                throw new Exception("CORE_ERROR<&>"+GET_DB_TIME_ZONE+"<&>"+INTERNAL+"<&>"+ERROR_SOURCE_MODULE+"<&>"+e.getMessage());
            }
        }

        public TimeZone getTimeZone() 
        {
            return (TimeZone) dbTimeZone.clone();
        }

        public String getFullId() 
        {
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
        public String getOnlyNumberId() 
        {
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
}