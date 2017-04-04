package test.br.com.ericsson.teltools.dth.core;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.br.com.ericsson.teltools.dth.baseTest.BaseTest;
import br.com.ericsson.teltools.dth.core.SearchAndValidation;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.Entity;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class SearchAndValidationTest {

	static String MAINID_12_FIRST = "551188226655";
	static String MAINID_12_SECOND = "551188446655";
	static String MAINID_12_THIRD_SP = "551188776655";
	
	static String MAINID_12_THIRD_NO_SP = "553488886655";

	static String MAINID_13_NO_SP = "553498877665544";
	
	static String MAINID_13_SECOND_WRONG_DIGIT = "5511988556655";
	
	static String MAINID_13_FIRST = "5511988226655";
	static String MAINID_13_SECOND = "5511988446655";
	static String MAINID_13_THIRD = "5511988776655";

	static String MAINID_MORE_THAN_13 = "55119887766554499";
	static String MAINID_LESS_THAN_12 = "988776655";

	static SearchAndValidation formater;
	static DataObject dataSource;
	
	static String sessionRegisterElegidos;

	Map<String, Object> statusOut;
	static BaseTest baseTest;
	
	@BeforeClass
	public static void init() {
		formater = new SearchAndValidation();
		dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		baseTest = new BaseTest(SearchAndValidation.class);
	}

	@Before
	public void addDbInfos() throws EntityException{
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		data.setParameter("TIMEZONE", "GMT-0800");
		data.setParameter("SERVICE_CLASS_VALIDATION", "40<:>600<;>20120813104607-0500<;>20120813104607-0500<;>SERVICE_CLASS_VALIDATION<;>1<;>1<;>SERVICE_CLASS_VALIDATION");

		Entity dbInfos = formater.createEntity("DB_INFOS");
		dbInfos.setData(data);
		formater.putEntity(dbInfos);		
	}
	
	@Before
	public void addServiceClassValidation() throws EntityException{
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		data.setParameter("52", "<:>402<;>0");
		Entity dbInfos = formater.createEntity("SERVICE_CLASS_VALIDATION");
		dbInfos.setData(data);
		formater.putEntity(dbInfos);		
	}
	
	@Before
	public void addAnatelMainIDRanges() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		data.setParameter("551188220000", "551188229999<;>9<;>20130621134034-0300<;>20130830134034-0300");
		data.setParameter("551188440000", "551188449999<;>9<;>20130521134034-0300<;>20130830134034-0300");
		data.setParameter("551188550000", "551188559999<;>7<;>20130521134034-0300<;>20130830134034-0300");
		data.setParameter("551188770000", "551188779999<;>9<;>20130521134034-0300<;>20130530134034-0300");
		data.setParameter("553488880000", "553488889999<;>9<;>20130521134034-0300<;>20130530134034-0300");

		Entity anatel = formater.createEntity("ANATEL_533");
		anatel.setData(data);
		formater.putEntity(anatel);
	}

	@Before
	public void addPolicyVsStatus() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		String valor = "";
		valor = "1<;>3<;>2<;>56<;>1";
		valor = valor.concat("<.>");
		valor = valor.concat("1<;>3<;>2<;>56<;>2");
		valor = valor.concat("<.>");
		valor = valor.concat("<;>2<;>2<;>56<;>5");
		valor = valor.concat("<.>");
		valor = valor.concat("1<;><;><;>56<;>3");
		valor = valor.concat("<.>");
		valor = valor.concat("<;>2<;><;>56<;>6");
		valor = valor.concat("<.>");
		valor = valor.concat("<;><;>2<;>56<;>7");
		valor = valor.concat("<.>");
		valor = valor.concat("<;><;><;>56<;>4");	
		data.setParameter("1", valor);

		Entity policy = formater.createEntity("POLICY");
		policy.setData(data);
		formater.putEntity(policy);	
	}
	
	@Before
	public void addStatusByName() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		data.setParameter("FULL", 			"1");
		data.setParameter("RESTRICTED", 	"2");
		data.setParameter("VOLUME_EXPIRED", "3");
		data.setParameter("TIME_EXPIRED", 	"4");
		data.setParameter("CANCELLED", 		"5");
		data.setParameter("SUSPENDED", 		"6");
		data.setParameter("FUNO_PLAN", 		"7");

		Entity status = formater.createEntity("STATUS");
		status.setData(data);
		formater.putEntity(status);
	}	
		
	@Before
	public void addLayouts() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());	
		data.setParameter("ANATEL_533", "MAINID_RANGE_END<;>NEW_DIGIT<;>START_PHASE2<;>START_PHASE3");
		data.setParameter("DB_INFOS", "VALUE");
		data.setParameter("POLICY", "thresholdId<;>subscriberTypeId<;>deviceTypeId<;>name<;>policyId");
		data.setParameter("STATUS", "statusId");
		data.setParameter("SERVICE_CLASS_VALIDATION", "FAF_ID<;>SERVICE_CLASS_VALIDATION_VALUE");

		Entity flowCommands = formater.createEntity("LAYOUT");
		flowCommands.setData(data);
		formater.putEntity(flowCommands);
	}

//	@Test(expected = Exception.class)
//	public void prepareMainIDLessThan12Digitis() throws Exception {
//		Method m = SearchAndValidation.class.getDeclaredMethod("prepareMainID", Calendar.class,String.class,Map.class);
//		m.setAccessible(true);
//		m.invoke(formater, Calendar.getInstance(), MAINID_LESS_THAN_12, statusOut);
//		//formater.prepareMainID(Calendar.getInstance(), MAINID_LESS_THAN_12, statusOut);
//	}

	@Test(expected = Exception.class)
	public void prepareMainIDMoreThan13Digitis() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		m.invoke(formater, Calendar.getInstance(), MAINID_MORE_THAN_13, statusOut);
	}

	@Test
	public void prepareMainID12DigitisFirstPeriod() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		String finalMainID = (String) m.invoke(formater, Calendar.getInstance(), MAINID_12_FIRST, statusOut);
		Assert.assertEquals(MAINID_12_FIRST, finalMainID);
	}

	@Test
	public void prepareMainID12DigitisSecondPeriod() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		String finalMainID = (String) m.invoke(formater, Calendar.getInstance(), MAINID_12_SECOND, statusOut);
		Assert.assertEquals(MAINID_12_SECOND, finalMainID);
	}

//	@Test(expected = Exception.class)
//	public void prepareMainID12DigitisSpThirdPeriod() throws Exception {
//		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
//		m.invoke(formater, Calendar.getInstance(), MAINID_12_THIRD_SP, statusOut);
//	}
	

	@Test
	public void prepareMainID12DigitisNoSpThirdPeriod() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		String finalMainID = (String) m.invoke(formater, Calendar.getInstance(), MAINID_12_THIRD_NO_SP, statusOut);
		Assert.assertEquals(MAINID_12_THIRD_NO_SP, finalMainID);
	}
	
	@Test(expected = Exception.class)
	public void prepareMainID13DigitisNoSp() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		m.invoke(formater, Calendar.getInstance(), MAINID_13_NO_SP, statusOut);
	}

	@Test(expected = Exception.class)
	public void prepareMainID13DigitisSpFirstPeriod() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		m.invoke(formater, Calendar.getInstance(), MAINID_13_FIRST, statusOut);
	}

	@Test(expected = Exception.class)
	public void prepareMainID13DigitisSpSecondPeriodWrongDigit() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		m.invoke(formater, Calendar.getInstance(), MAINID_13_SECOND_WRONG_DIGIT, statusOut);
	}
	
	@Test
	public void prepareMainID13DigitisSpSecondPeriod() throws Exception {
		Method m = baseTest.getMethodByNameAndParams("prepareMainID", Calendar.class,String.class,Map.class);
		String finalMainID = (String) m.invoke(formater, Calendar.getInstance(), MAINID_13_SECOND, statusOut);
		Assert.assertEquals(MAINID_12_SECOND, finalMainID);
	}

//	@Test
//	public void prepareMainID13DigitisSpThirdPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_13_THIRD, statusOut);
//
//		Assert.assertEquals(MAINID_13_THIRD, finalMainID);
//	}
//
	@Test
	public void parseDefaultStringToCalendar() throws Exception{
		String date = "20120520143512-0300";
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmssZ");
		Calendar expected = Calendar.getInstance();
		expected.setTimeInMillis(dateFormater.parse(date).getTime());
		expected.setTimeZone(TimeZone.getTimeZone("GMT-0800"));
		Method m = baseTest.getMethodByNameAndParams("parseDefaultStringToCalendarWithDbTimeZone", String.class);
		Calendar finalDate = (Calendar) m.invoke(formater, date);
		
		Assert.assertEquals(expected, finalDate);
	}
	
	@Test
	public void parseCalendarToDefaultStringWithDbTimeZone() throws Exception{
		String expected = "20120520143512-0800";
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmssZ");
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT-0800"));
		date.setTimeInMillis(dateFormater.parse(expected).getTime());
		
		Method m = baseTest.getMethodByNameAndParams("parseCalendarToDefaultStringWithDbTimeZone", Calendar.class);
		String finalDate = (String) m.invoke(formater, date);
		Assert.assertEquals(expected, finalDate);
	}
//
///*	@Test
//	public void seachPolicy() throws Exception{
//		String expected = "<;>2<;>2<;>56<;>5";
//		String returned = "";
//		HashMap<String, Object> containerData;
//		
//		containerData = formater.getContainerData("POLICY", "1", "POLICY", false);
//		returned = formater.getPolicieByStatus("1", null, "2", "2");
//		containerData.put("POLICY", returned);
//		
//		//TEST IF CONTAINER LOAD SUCCESS
//		assertEquals(true, formater.loadStatus(containerData));
//		//TEST IF SEARCH WAS SUCCESS
//		assertEquals(expected, returned);
//		
//	}*/
//	
//	
//	@Test
//	public void verifyStatusSubscription(){
//		
//		HashMap<String, Object> subscription = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		subscription.put("STATUS_NAME", "ACTIVE");
//		assertEquals(true, formater.subscriptionHasTheseStatus(subscription,"ACTIVE"));
//		subscription.put("STATUS_NAME", "RESTRICTED");
//		assertEquals(true, formater.subscriptionHasTheseStatus(subscription,"ACTIVE|RESTRICTED"));
//		subscription.put("STATUS_NAME", "SUSPEND");
//		assertEquals(false, formater.subscriptionHasTheseStatus(subscription,"ACTIVE|RESTRICTED"));
//	}
//	
//	
//	
//	@Test
//	public void decideWhichStatusToSet(){
//		HashMap<String, Object> subscription = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		subscription.put("STATUS_NAME", "FULL");
//		HashMap<String, Object> subscriptions = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		subscriptions.put("0", subscription);
//		assertEquals(true, "CANCELLED".equals(formater.decideStatusToUpdate(subscriptions)));
//		subscription.put("STATUS_NAME", "CANCELLED");
//		assertEquals(true , "NO_PLAN".equals(formater.decideStatusToUpdate(subscriptions)));
//		subscription.put("STATUS_NAME", "SUSPENDED");
//		assertEquals(false, "NO_PLAN".equals(formater.decideStatusToUpdate(subscriptions)));
//	}
//	
//	@Test
//	public void testPolicy() throws Exception{
//		formater.getPolicieByStatus("1","1","3","2");
//	}
	
	
//	@Test
//	public void getServiceClassValidation()throws Exception{
//		//dataSource.setParameter("SERVICE_CLASS_VALIDATION", "40<:>600<;>20120813104607-0500<;>20120813104607-0500<;>SERVICE_CLASS_VALIDATION<;>1<;>1<;>SERVICE_CLASS_VALIDATION");
//		assertEquals("0", formater.getServiceClassValidation("52"));
//	}
	
//	@Test
//	public void getFafIdTest()throws Exception{
//		assertEquals("600", formater.getFafId("Elegido600"));
//	}
	
	
//	@Test
//	public void registerElegidoFromBackEndController() throws Exception {
//		InputStream file = getClass().getResourceAsStream(
//				"ContainerCore.txt");
//		GenerateContainerMock container = new GenerateContainerMock(formater);
//		container.populateContainer(file);
//		
//		String mainID = "573000000011";
//		
//		dataSource.setParameter("0.operation", "registerElegido");
//		dataSource.setParameter("0.productName", "Elegido402");
//		dataSource.setParameter("0.0.MAINID", mainID);
//		dataSource.setParameter("0.0.IMSI", "1234567890");
//		dataSource.setParameter("0.0.IMEI", "1234567890");
//		dataSource.setParameter("0.0.ICCID", "1234567890");
//		dataSource.setParameter("0.0.EMAIL", "t@teltools.com.br");
//		dataSource.setParameter("0.0.subscriberType", "prepaid");
//		dataSource.setParameter("0.0.deviceType", "moden");
//		dataSource.setParameter("0.0.originatingNodeInfo", "WS");
//		dataSource.setParameter("0.0.externalData", "WS");
//		dataSource.setParameter("0.0.charged", "yes");
//		dataSource.setParameter("0.0.FaFIdList", "20");
//	    dataSource.setParameter("0.0.MAINIDElegidoToRegister", "573000000022");
//	    
//	    dataSource.setParameter("WS_requestID", "32");
//	    dataSource.setParameter("__GlobalTransactionID", "208");   
//		
//		formater.format(dataSource);
//		
//		assertEquals("OK", dataSource.getValueAsString("STATUS"));
//		assertEquals("SearchAndValidation", dataSource.getValueAsString("ORIGIN"));
//		assertEquals("SubscriberAndSubscriptionQuery", dataSource.getValueAsString("TARGET"));
//		
//		assertEquals("MAINID", dataSource.getValueAsString("FIELD_NAME"));
//		assertEquals(mainID, dataSource.getValueAsString("FIELD_QUERY"));
//		
//		assertTrue(dataSource.parameterExists("SESSION_ID"));
//		sessionRegisterElegidos = dataSource.getValueAsString("SESSION_ID");
//	}

	

//	@Test
//	@SuppressWarnings("unchecked")
//	public void registerElegidoFromSearchThereIsNotSubscriber() throws Exception {
//		InputStream file = getClass().getResourceAsStream(
//				"ContainerCore.txt");
//		GenerateContainerMock container = new GenerateContainerMock(formater);
//		container.populateContainer(file);
//		
//		String mainID = "573000000011";
//		
//		dataSource.setParameter("DbLookUpResultCode", 0);
//		dataSource.setParameter("DbLookUpResultDesc", "OK");
//		dataSource.setParameter("FIELD_NAME", "MAINID");
//		dataSource.setParameter("FIELD_QUERY", mainID);
//		dataSource.setParameter("FLAG_SUBSCRIBERS", 1);
//		dataSource.setParameter("FLAG_SUBSCRIPTION", 1);
//		dataSource.setParameter("ORIGIN", "subscriberAndSubscriptionQuery");
//		dataSource.setParameter("REQUEST_NUMBER", "0");
//		dataSource.setParameter("SESSION_ID", sessionRegisterElegidos);
//		dataSource.setParameter("STATUS", "OK");
//		
//		
//		Map<String, Object> subscriberData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		
//		dataSource.setParameter("SUBSCRIBER_DATA", subscriberData);
//		dataSource.setParameter("SUBSCRIBER_DATA_DB", "");
//		dataSource.setParameter("SUBSCRIBER_NUMBER", "0");
//		
//		Map<String, Object> subscriptionsData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		
//		dataSource.setParameter("SUBSCRIPTIONS_DATA", subscriptionsData);
//	    dataSource.setParameter("SUBSCRIPTIONS_DATA_DB", "");
//	    dataSource.setParameter("TARGET", "SearchAndValidation");   
//		
//		formater.format(dataSource);
//		
//		Map<String, Object> returns = (Map<String, Object>) dataSource.getValue("MAP_VARIABLES");
//		returns = (Map<String, Object>) returns.get(returns.keySet().iterator().next());
//		
//		Map<String, Object> action = (Map<String, Object>)returns.get("ACTION");
//		assertEquals(dataSource.getValueAsString("ORIGIN"), "SearchAndValidation");
//		assertEquals(dataSource.getValueAsString("TARGET"), "ConnectedSearchAndValidation");
//		
//		Map<String, Object> table = (Map<String, Object>)returns.get("TABLE");
//		
//	}	

//	@Test
//	@SuppressWarnings("unchecked")
//	public void registerElegidoFromSearch() throws Exception {
//		InputStream file = getClass().getResourceAsStream(
//				"ContainerCore.txt");
//		GenerateContainerMock container = new GenerateContainerMock(formater);
//		container.populateContainer(file);
//		
//		String mainID = "573000000011";
//		
//		dataSource.setParameter("DbLookUpResultCode", 0);
//		dataSource.setParameter("DbLookUpResultDesc", "OK");
//		dataSource.setParameter("FIELD_NAME", "MAINID");
//		dataSource.setParameter("FIELD_QUERY", mainID);
//		dataSource.setParameter("FLAG_SUBSCRIBERS", 1);
//		dataSource.setParameter("FLAG_SUBSCRIPTION", 1);
//		dataSource.setParameter("ORIGIN", "subscriberAndSubscriptionQuery");
//		dataSource.setParameter("REQUEST_NUMBER", "0");
//		dataSource.setParameter("SESSION_ID", sessionRegisterElegidos);
//		dataSource.setParameter("STATUS", "OK");
//		
//		
//		Map<String, Object> subscriberData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		
//		subscriberData.put("subscriberType", "postpaid");
//		subscriberData.put("DEVICE_TYPE_DESCRIPTION", "handset");
//		subscriberData.put("SUBSCRIBER_TYPE_DESCRIPTION", "postpaid");
//		subscriberData.put("SUBSCRIBER_TYPE_ID", "3");
//		subscriberData.put("IMEI", "0");
//		subscriberData.put("deviceType", "handset");
//		subscriberData.put("PROPERTIES", "0");
//		subscriberData.put("IMSI", "0");
//		subscriberData.put("EMAIL", "teste@user.org");
//		subscriberData.put("MAINID", mainID);
//		subscriberData.put("DEVICE_TYPE_ID", "1");
//		subscriberData.put("ICCID", "0");
//		
//		dataSource.setParameter("SUBSCRIBER_DATA", subscriberData);
//		dataSource.setParameter("SUBSCRIBER_DATA_DB", mainID + "<:>0<;>0<;>0<;>0<;>3<;>1<;><#>teste@user.org<;>handset<;>handset<;>postpaid<;>postpaid<;>");
//		dataSource.setParameter("SUBSCRIBER_NUMBER", "0");
//		
//		Map<String, Object> subscriptionsData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		
//		Map<String, Object> zero = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//		
//		subscriptionsData.put("0", zero);
//		
//		zero.put("SUBSCRIPTION_ID", "151083");
//		zero.put("PRODUCT_ID", "52");
//		zero.put("PROPERTIES", "0");
//		zero.put("PROMOTION_ID", "");
//		zero.put("SUBSCRIPTION_POLICY_ID", "123");
//		zero.put("END_TIMESTAMP", "20120912104607-0500");
//		zero.put("START_TIMESTAMP", "20120813104607-0500");
//		zero.put("PROMOTION_USES", "");
//		zero.put("MAX_RENEWALS_LEFT", "0");
//		zero.put("LAST_RENEWAL_RETRY", "20120813104607-0500");
//		zero.put("MASTER_SUBSCRIPTION_ID", "");
//		zero.put("QOS_DOWNLOAD", "8000000");
//		zero.put("TIMESTAMP_POLICE", "20120813104607-0500");
//		zero.put("QOS_UPLOAD", "1472000");
//		zero.put("RENEWAL_RETRIES_LEFT", "0");
//		zero.put("MAINID", mainID);
//		zero.put("POLICY_ID", "4");
//		zero.put("STATUS_NAME", "FULL");
//		
//		dataSource.setParameter("SUBSCRIPTIONS_DATA", subscriptionsData);
//	    
//	    dataSource.setParameter("SUBSCRIPTIONS_DATA_DB", mainID+"<:>0<;><;>0<;>0<;>1472000<;>8000000<;>39<;>123<;>4<;><;><;>151083<;><#>20120813104607-0500<;>FULL<;>20120813104607-0500<;>20120912104607-0500<;>20120813104607-0500<;>");
//	    dataSource.setParameter("TARGET", "SearchAndValidation");   
//		
//		formater.format(dataSource);
//		
//		Map<String, Object> returns = (Map<String, Object>) dataSource.getValue("MAP_VARIABLES");
//		returns = (Map<String, Object>) returns.get(returns.keySet().iterator().next());
//		
//		Map<String, Object> action = (Map<String, Object>)returns.get("ACTION");
//		Map<String, Object> request = (Map<String, Object>)action.get("0");
//		String status = (String) request.get("STATUS");
//		assertEquals(status, "PROCESSING_OK");
//		assertEquals(dataSource.getValueAsString("ORIGIN"), "SearchAndValidation");
//		assertEquals(dataSource.getValueAsString("TARGET"), "ConnectedSearchAndValidation");
//		
//		Map<String, Object> table = (Map<String, Object>)returns.get("TABLE");
//		request = (Map<String, Object>)table.get("0");
//		Map<String, Object> subscriber = (Map<String, Object>)request.get("0");
//		Map<String, Object> product = (Map<String, Object>) subscriber.get("PRODUCT");
//		
//		assertTrue(product.containsKey("categoryID"));
//		assertTrue(product.containsKey("productID"));
//		assertTrue(product.containsKey("productProperties"));
//		assertTrue(product.containsKey("taxValue"));
//		
//		assertEquals("20000",product.get("FINAL_PRICE"));
//		
//		assertTrue(subscriber.containsKey("DEDICATED_ACCOUNT"));
//		assertTrue(subscriber.containsKey("OFFER"));
//	}

//	@Test
//	public void registerElegidoFromBackEndControllerForMultiplesElegidos() throws Exception {
//		InputStream file = getClass().getResourceAsStream("ContainerCore.txt");
//		GenerateContainerMock container = new GenerateContainerMock(formater);
//		container.populateContainer(file);
//		
//		String mainID = "573000000011";
//
//		dataSource.setParameter("0.operation", "registerMultiplesElegido");
//		dataSource.setParameter("0.productName", "Elegido402");
//		dataSource.setParameter("0.0.MAINID", mainID);
//		dataSource.setParameter("0.0.IMSI", "1234567890");
//		dataSource.setParameter("0.0.IMEI", "1234567890");
//		dataSource.setParameter("0.0.ICCID", "1234567890");
//		dataSource.setParameter("0.0.EMAIL", "elegido01@teltools.com.br");
//		dataSource.setParameter("0.0.subscriberType", "prepaid");
//		dataSource.setParameter("0.0.deviceType", "moden");
//		dataSource.setParameter("0.0.originatingNodeInfo", "WS");
//		dataSource.setParameter("0.0.externalData", "WS");
//		dataSource.setParameter("0.0.charged", "yes");
//		dataSource.setParameter("0.0.FaFIdList", "20");
//	    dataSource.setParameter("0.0.MAINIDElegidoToRegister", "573000000022");
//
//	    dataSource.setParameter("0.1.MAINID", mainID);
//		dataSource.setParameter("0.1.IMSI", "0000000000");
//		dataSource.setParameter("0.1.IMEI", "0000000000");
//		dataSource.setParameter("0.1.ICCID", "0000000000");
//		dataSource.setParameter("0.1.EMAIL", "elegido02@teltools.com.br");
//		dataSource.setParameter("0.1.subscriberType", "prepaid");
//		dataSource.setParameter("0.1.deviceType", "moden");
//		dataSource.setParameter("0.1.originatingNodeInfo", "WS");
//		dataSource.setParameter("0.1.externalData", "WS");
//		dataSource.setParameter("0.1.charged", "yes");
//		dataSource.setParameter("0.1.FaFIdList", "20");
//	    dataSource.setParameter("0.1.MAINIDElegidoToRegister", "573000000666");
//
//	    dataSource.setParameter("WS_requestID", "32");
//	    dataSource.setParameter("__GlobalTransactionID", "208");
//		
//		formater.format(dataSource);
//		
//		assertEquals("OK", dataSource.getValueAsString("STATUS"));
//		assertEquals("SearchAndValidation", dataSource.getValueAsString("ORIGIN"));
//		assertEquals("SubscriberAndSubscriptionQuery", dataSource.getValueAsString("TARGET"));
//		
//		assertEquals("MAINID", dataSource.getValueAsString("FIELD_NAME"));
//		assertEquals(mainID, dataSource.getValueAsString("FIELD_QUERY"));
//		
//		assertTrue(dataSource.parameterExists("SESSION_ID"));
//		sessionRegisterElegidos = dataSource.getValueAsString("SESSION_ID");
//	}
//
//	@Test
//	public void registerMultiplesElegidoFromBackEndController() throws Exception {
//		InputStream file = getClass().getResourceAsStream("ContainerCore.txt");
//		GenerateContainerMock container = new GenerateContainerMock(formater);
//		container.populateContainer(file);
//		
//		String mainID = "573000000011";
//		
//		dataSource.setParameter("DbLookUpResultCode", 0);
//		dataSource.setParameter("DbLookUpResultDesc", "OK");
//		dataSource.setParameter("FIELD_NAME", "MAINID");
//		dataSource.setParameter("FIELD_QUERY", mainID);
//		dataSource.setParameter("FLAG_SUBSCRIBERS", 1);
//		dataSource.setParameter("FLAG_SUBSCRIPTION", 1);
//		dataSource.setParameter("ORIGIN", "subscriberAndSubscriptionQuery");
//		dataSource.setParameter("REQUEST_NUMBER", "0");
//		dataSource.setParameter("SESSION_ID", sessionRegisterElegidos);
//		dataSource.setParameter("STATUS", "OK");
//
//		Map<String, Object> subscriberData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//
//		subscriberData.put("subscriberType", "postpaid");
//		subscriberData.put("DEVICE_TYPE_DESCRIPTION", "handset");
//		subscriberData.put("SUBSCRIBER_TYPE_DESCRIPTION", "postpaid");
//		subscriberData.put("SUBSCRIBER_TYPE_ID", "3");
//		subscriberData.put("IMEI", "0");
//		subscriberData.put("deviceType", "handset");
//		subscriberData.put("PROPERTIES", "0");
//		subscriberData.put("IMSI", "0");
//		subscriberData.put("EMAIL", "teste@user.org");
//		subscriberData.put("MAINID", mainID);
//		subscriberData.put("DEVICE_TYPE_ID", "1");
//		subscriberData.put("ICCID", "0");
//
//		dataSource.setParameter("SUBSCRIBER_DATA", subscriberData);
//		dataSource.setParameter("SUBSCRIBER_DATA_DB", mainID + "<:>0<;>0<;>0<;>0<;>3<;>1<;><#>teste@user.org<;>handset<;>handset<;>postpaid<;>postpaid<;>");
//		dataSource.setParameter("SUBSCRIBER_NUMBER", "0");
//
//		Map<String, Object> subscriptionsData = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//
//		Map<String, Object> zero = new java.util.concurrent.ConcurrentHashMap<String, Object>();
//
//		subscriptionsData.put("0", zero);
//
//		zero.put("SUBSCRIPTION_ID", "151083");
//		zero.put("PRODUCT_ID", "52");
//		zero.put("PROPERTIES", "0");
//		zero.put("PROMOTION_ID", "");
//		zero.put("SUBSCRIPTION_POLICY_ID", "123");
//		zero.put("END_TIMESTAMP", "20120912104607-0500");
//		zero.put("START_TIMESTAMP", "20120813104607-0500");
//		zero.put("PROMOTION_USES", "");
//		zero.put("MAX_RENEWALS_LEFT", "0");
//		zero.put("LAST_RENEWAL_RETRY", "20120813104607-0500");
//		zero.put("MASTER_SUBSCRIPTION_ID", "");
//		zero.put("QOS_DOWNLOAD", "8000000");
//		zero.put("TIMESTAMP_POLICE", "20120813104607-0500");
//		zero.put("QOS_UPLOAD", "1472000");
//		zero.put("RENEWAL_RETRIES_LEFT", "0");
//		zero.put("MAINID", mainID);
//		zero.put("POLICY_ID", "4");
//		zero.put("STATUS_NAME", "FULL");
//
//		dataSource.setParameter("SUBSCRIPTIONS_DATA", subscriptionsData);
//
//	    dataSource.setParameter("SUBSCRIPTIONS_DATA_DB", mainID+"<:>0<;><;>0<;>0<;>1472000<;>8000000<;>39<;>123<;>4<;><;><;>151083<;><#>20120813104607-0500<;>FULL<;>20120813104607-0500<;>20120912104607-0500<;>20120813104607-0500<;>");
//	    dataSource.setParameter("TARGET", "SearchAndValidation");
//
//		formater.format(dataSource);
//
//		assertEquals("OK", dataSource.getValueAsString("STATUS"));
//		assertEquals("SearchAndValidation", dataSource.getValueAsString("ORIGIN"));
//		assertEquals("SubscriberAndSubscriptionQuery", dataSource.getValueAsString("TARGET"));
//		
//		assertEquals("MAINID", dataSource.getValueAsString("FIELD_NAME"));
//		assertEquals(mainID, dataSource.getValueAsString("FIELD_QUERY"));
//		
//		assertTrue(dataSource.parameterExists("SESSION_ID"));
//		sessionRegisterElegidos = dataSource.getValueAsString("SESSION_ID");
//	}
	
}