package test.br.com.ericsson.teltools.dth.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.ericsson.teltools.dth.logging.DataBaseFormatter;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;


public class DataBaseFormatterTest {
	
	private static DataBaseFormatter dbFormatter;
	private static DataObject dataSource;
	
	@BeforeClass
	public static void init(){
		dbFormatter = new DataBaseFormatter();
		dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
	}
	
	@Before
	public void populateLoggingList(){
        Map<String, Object> loggingList = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        
        dataSource.setParameter("LOGGING_LIST", loggingList);
        
        Map<String, Object> dbRequest  = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        Map<String, Object> dbAction   = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        Map<String, Object> dbResponse = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        
        Map<String, Object> db = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        
        db.put("REQUEST", dbRequest);
        db.put("ACTION", dbAction);
        db.put("RESPONSE", dbResponse);
        
        Map<String, Object> logging = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        logging.put("DB", db);
        
        loggingList.put("0.0", logging);
        
        dbRequest.put("sessionId","123456789");
        dbRequest.put("actionType","REQUEST");
        dbRequest.put("actionData","...really a lot of things!");
        dbRequest.put("mainID","8888888888");
        dbRequest.put("properties",0);
        dbRequest.put("subscriptionId","666");
        dbRequest.put("qosId","999");
        
        
        dbAction.put("sessionId","123456789");
        dbAction.put("actionType","ACTION");
        dbAction.put("actionData","...really a lot of things!");
        dbAction.put("mainID","8888888888");
        dbAction.put("properties",0);
        dbAction.put("subscriptionId","666");
        dbAction.put("qosId","999");
        
        dbResponse.put("sessionId","123456789");
        dbResponse.put("actionType","RESPONSE");
        dbResponse.put("actionData","...really a lot of things!");
        dbResponse.put("mainID","8888888888");
        dbResponse.put("properties",0);
        dbResponse.put("subscriptionId","666");
        dbResponse.put("qosId","999");

        Map<String, Object> dr = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        logging.put("DR", dr);
        
        dr.put("drEventID"                ,"123456789");
        dr.put("drTimestamp"            , "20130603");
        dr.put("drOperation"            ,"teste");
        dr.put("drOperationType"        ,"execute");
        dr.put("drAdditionalData"        ,"none");
        dr.put("drMAINID"                ,"8888888888");
        dr.put("drDeviceType"            ,"tijolo/telha");
        dr.put("drSubscriberType"        ,"dividiu em 12x no credito");
        dr.put("drProductName"            ,"smartphone");
        dr.put("drStatus"                ,"pagando a boleta");
        dr.put("drErrorDescription"        ,"");
        dr.put("drOriginatingNodeInfo"    ,"WS");
        dr.put("requestNumber"           , "0");
        
	}
	
	@After
	public void cleanLoggingList(){
		dataSource.removeParameter("LOGGING_LIST");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void mustNotFulfillRequest() throws Exception{
		Map<String,Object> loggingList = (Map<String, Object>) dataSource.getValue("LOGGING_LIST");
		Map<String,Object> logging = (Map<String, Object>) loggingList.get("0.0");
		Map<String,Object> db = (Map<String, Object>) logging.get("DB");
		db.put("REQUEST", new java.util.concurrent.ConcurrentHashMap<String, Object>());
		dataSource.setParameter("LOGGING_LIST", loggingList);
		
		dbFormatter.format(dataSource);
		
		assertEquals("",dataSource.getValueAsString("requestActionData"));
		assertEquals("",dataSource.getValueAsString("requestActionType"));
		assertEquals("",dataSource.getValueAsString("requestMainID"));
		assertEquals("",dataSource.getValueAsString("requestProperties"));
		assertEquals("",dataSource.getValueAsString("requestQosId"));
		assertEquals("",dataSource.getValueAsString("requestSessionId"));
		assertEquals("",dataSource.getValueAsString("requestSubscriptionId"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void mustNotFulfillAction() throws Exception{
		Map<String,Object> loggingList = (Map<String, Object>) dataSource.getValue("LOGGING_LIST");
		Map<String,Object> logging = (Map<String, Object>) loggingList.get("0.0");
		Map<String,Object> db = (Map<String, Object>) logging.get("DB");
		db.put("ACTION", new java.util.concurrent.ConcurrentHashMap<String, Object>());
		dataSource.setParameter("LOGGING_LIST", loggingList);
		
		dbFormatter.format(dataSource);
		
		assertEquals("",dataSource.getValueAsString("actionActionData"));
		assertEquals("",dataSource.getValueAsString("actionActionType"));
		assertEquals("",dataSource.getValueAsString("actionMainID"));
		assertEquals("",dataSource.getValueAsString("actionProperties"));
		assertEquals("",dataSource.getValueAsString("actionQosId"));
		assertEquals("",dataSource.getValueAsString("actionSessionId"));
		assertEquals("",dataSource.getValueAsString("actionSubscriptionId"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void mustNotFulfillResponse() throws Exception{
		Map<String,Object> loggingList = (Map<String, Object>) dataSource.getValue("LOGGING_LIST");
		Map<String,Object> logging = (Map<String, Object>) loggingList.get("0.0");
		Map<String,Object> db = (Map<String, Object>) logging.get("DB");
		db.put("RESPONSE", new java.util.concurrent.ConcurrentHashMap<String, Object>());
		dataSource.setParameter("LOGGING_LIST", loggingList);
		
		dbFormatter.format(dataSource);
		
		assertEquals("",dataSource.getValueAsString("responseActionData"));
		assertEquals("",dataSource.getValueAsString("responseActionType"));
		assertEquals("",dataSource.getValueAsString("responseMainID"));
		assertEquals("",dataSource.getValueAsString("responseProperties"));
		assertEquals("",dataSource.getValueAsString("responseQosId"));
		assertEquals("",dataSource.getValueAsString("responseSessionId"));
		assertEquals("",dataSource.getValueAsString("responseSubscriptionId"));
		
	}
	
	@Test
	public void mustFulfillAll() throws Exception{
		
		dbFormatter.format(dataSource);
		
		assertNotNull(dataSource.getValueAsString("responseActionData"));
		assertNotNull(dataSource.getValueAsString("responseActionType"));
		assertNotNull(dataSource.getValueAsString("responseMainID"));
		assertNotNull(dataSource.getValueAsString("responseProperties"));
		assertNotNull(dataSource.getValueAsString("responseQosId"));
		assertNotNull(dataSource.getValueAsString("responseSessionId"));
		assertNotNull(dataSource.getValueAsString("responseSubscriptionId"));
		
		assertNotNull(dataSource.getValueAsString("requestActionData"));
		assertNotNull(dataSource.getValueAsString("requestActionType"));
		assertNotNull(dataSource.getValueAsString("requestMainID"));
		assertNotNull(dataSource.getValueAsString("requestProperties"));
		assertNotNull(dataSource.getValueAsString("requestQosId"));
		assertNotNull(dataSource.getValueAsString("requestSessionId"));
		assertNotNull(dataSource.getValueAsString("requestSubscriptionId"));
		
		assertNotNull(dataSource.getValueAsString("actionActionData"));
		assertNotNull(dataSource.getValueAsString("actionActionType"));
		assertNotNull(dataSource.getValueAsString("actionMainID"));
		assertNotNull(dataSource.getValueAsString("actionProperties"));
		assertNotNull(dataSource.getValueAsString("actionQosId"));
		assertNotNull(dataSource.getValueAsString("actionSessionId"));
		assertNotNull(dataSource.getValueAsString("actionSubscriptionId"));

	}
			

}
