package test.br.com.ericsson.teltools.dth.core;

import java.util.Map;

import org.junit.BeforeClass;
import br.com.ericsson.teltools.dth.core.BackEndController;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class BackEndControllerTest {
	

	private static final String SESSION_ID = "6666";

	private static BackEndController backEndController;
	private static DataObject dataSource;
	
	Map<String,Object> request;
	Map<String,Object> action;
	Map<String,Object> sessionMap;
	
	@BeforeClass
	public static void init(){
		backEndController = new BackEndController();
		dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		Map<String,Object> sessionMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		Map<String,Object> requestMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		Map<String,Object> actionMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		Map<String,Object> zeroRequestMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		Map<String,Object> zeroResponseMap = new java.util.concurrent.ConcurrentHashMap<String, Object>();
		
		dataSource.setParameter("SESSION_ID", SESSION_ID);
		backEndController.getMapVariables().put(SESSION_ID, sessionMap);
		
		sessionMap.put("REQUEST", requestMap );
		sessionMap.put("ACTION", actionMap);
		
		zeroRequestMap.put("operation", "default");
		zeroResponseMap.put("STATUS", "ok");
		
		requestMap.put("0", zeroRequestMap);
		actionMap.put("0", zeroResponseMap);
		
	}

	/*
	 * BEFORE UNCOMMENT THESE TESTS,MAKE SURE TO CHANGE THE HERITANCE OF BackendController
	 * TO "MockJavaFormatterBase" AND CHANGE THE ACCESS MODIFIER OF isLoggingAuthorized METHOD
	 * TO public.
	 * 
	 * AND PLEASE AFTER RUNNING THESE, UNDO THE CHANGES ON BackEndController CLASS.
	 * 
	 */
	
//	@Before
//	public void populateContainer() throws IOException {
//		//InputStream file = getClass().getResourceAsStream("ContainerCore.txt");
//		FileInputStream f = new FileInputStream("./src/test/resources/ContainerCore.txt");
//				
//		GenerateContainerMock container = new GenerateContainerMock(backEndController);
//		container.populateContainer(f);
//	}	
//	@SuppressWarnings("unchecked")
//	@Before
//	public void setMapsToTesting(){
//		request = (Map<String, Object>) ((Map<String, Object>)((Map<String,Object>) 
//				backEndController.mapVariables.get(SESSION_ID)).get("REQUEST")).get("0");
//		action = (Map<String, Object>) ((Map<String, Object>)((Map<String,Object>) 
//				backEndController.mapVariables.get(SESSION_ID)).get("ACTION")).get("0");
//		sessionMap = (Map<String, Object>) backEndController.mapVariables.get(SESSION_ID);
//	}
//	
//	@Test
//	public void testContainercreation() throws EntityException, ParameterMissingException{
//		System.out.println(backEndController.getEntity("OPERATION_LOG_LEVEL").getData().getValueAsString("default"));
//		System.out.println("dataSpurce: "+dataSource.getParameterContainer().toString());
//		System.out.println("mapVariables: "+backEndController.mapVariables.toString());
//	}
//	
//	@Test
//	public void isLoggingAuthorized_2_2_2_statusOk() throws Exception{
//		request.put("operation", "default");
//		action.put("STATUS", "ok");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));
//	}
//	@Test
//	public void isLoggingAuthorized_2_2_2_statusError() throws Exception{
//		request.put("operation", "default");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));
//	}
//	
//	@Test
//	public void isLoggingAuthorized_0_0_0_statusOk() throws Exception{
//		request.put("operation", "default2");
//		action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));
//
//	}
//	@Test
//	public void isLoggingAuthorized_0_0_0_statusError() throws Exception{
//		request.put("operation", "default2");
//		action.put("STATUS", "error");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));
//
//	}
//	
//	@Test
//	public void isLoggingAuthorized_1_1_1_statusOk() throws Exception{
//		request.put("operation", "default3");
//		action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));
//	}
//	@Test
//	public void isLoggingAuthorized_1_1_1_statusError() throws Exception{
//		request.put("operation", "default3");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	
//	@Test
//	public void isLoggingAuthorized_0_1_2_statusOk() throws Exception{
//		request.put("operation", "default4");
//		action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_0_1_2_statusError() throws Exception{
//		request.put("operation", "default4");
//		action.put("STATUS", "error");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	
//	@Test
//	public void isLoggingAuthorized_0_2_1_statusOk() throws Exception{
//		request.put("operation", "default5");
//		action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_0_2_1_statusError() throws Exception{
//		request.put("operation", "default5");
//		action.put("STATUS", "error");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	
//	@Test
//	public void isLoggingAuthorized_1_0_2_statusOk() throws Exception{
//		this.request.put("operation", "default6");
//		this.action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(this.sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(this.sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(this.sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_1_0_2_statusError() throws Exception{
//		request.put("operation", "default6");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	
//	@Test
//	public void isLoggingAuthorized_1_2_0_statusOk() throws Exception{
//		request.put("operation", "default7");
//		action.put("STATUS", "ok");
//		
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_1_2_0_statusError() throws Exception{
//		request.put("operation", "default7");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//
//	@Test
//	public void isLoggingAuthorized_2_0_1_statusOk() throws Exception{
//		request.put("operation", "default8");
//		action.put("STATUS", "ok");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_2_0_1_statusError() throws Exception{
//		request.put("operation", "default8");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//
//	@Test
//	public void isLoggingAuthorized_2_1_0_statusOk() throws Exception{
//		request.put("operation", "default9");
//		action.put("STATUS", "ok");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
//	@Test
//	public void isLoggingAuthorized_2_1_0_statusError() throws Exception{
//		request.put("operation", "default9");
//		action.put("STATUS", "error");
//		
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "REQUEST"));
//		assertTrue(backEndController.isLoggingAuthorized(sessionMap, "ACTION"));
//		assertFalse(backEndController.isLoggingAuthorized(sessionMap, "RESPONSE"));	
//	}
	
	
}
