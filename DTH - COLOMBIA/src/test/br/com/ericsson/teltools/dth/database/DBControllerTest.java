package test.br.com.ericsson.teltools.dth.database;

import java.util.Map;

import org.junit.Before;
import br.com.ericsson.teltools.dth.database.DBController;
import br.com.ericsson.teltools.dth.debug.Entity;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class DBControllerTest {

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

	DBController formater = new DBController();

	Map<String, Object> statusOut;

	@Before
	public void addAnatelMainIDRanges() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		data.setParameter("551188220000", "551188229999<;>9<;>20120621134034-0300<;>20120830134034-0300");
		data.setParameter("551188440000", "551188449999<;>9<;>20120521134034-0300<;>20120830134034-0300");
		data.setParameter("551188550000", "551188559999<;>7<;>20120521134034-0300<;>20120830134034-0300");
		data.setParameter("551188770000", "551188779999<;>9<;>20120521134034-0300<;>20120530134034-0300");
		data.setParameter("553488880000", "553488889999<;>9<;>20120521134034-0300<;>20120530134034-0300");

		Entity anatel = formater.createEntity("ANATEL_533");
		anatel.setData(data);
		formater.putEntity(anatel);
	}

	@Before
	public void addLayouts() throws EntityException {
		DataObject data = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());	
		data.setParameter("ANATEL_533", "MAINID_RANGE_END<;>NEW_DIGIT<;>START_PHASE2<;>START_PHASE3");

		Entity flowCommands = formater.createEntity("LAYOUT");
		flowCommands.setData(data);
		formater.putEntity(flowCommands);
	}
//
//	@Test(expected = Exception.class)
//	public void prepareMainIDLessThan12Digitis() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_LESS_THAN_12, statusOut);
//	}
//
//	@Test(expected = Exception.class)
//	public void prepareMainIDMoreThan13Digitis() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_MORE_THAN_13, statusOut);
//	}
//
//	@Test
//	public void prepareMainID12DigitisFirstPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_12_FIRST, statusOut);
//
//		assertEquals(MAINID_12_FIRST, finalMainID);
//	}
//
//	@Test
//	public void prepareMainID12DigitisSecondPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_12_SECOND, statusOut);
//
//		assertEquals(MAINID_12_SECOND, finalMainID);
//	}
//
//	@Test(expected = Exception.class)
//	public void prepareMainID12DigitisSpThirdPeriod() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_12_THIRD_SP, statusOut);
//	}
//
//	@Test
//	public void prepareMainID12DigitisNoSpThirdPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_12_THIRD_NO_SP, statusOut);
//		
//		assertEquals(MAINID_12_THIRD_NO_SP, finalMainID);
//	}
//	
//	@Test(expected = Exception.class)
//	public void prepareMainID13DigitisNoSp() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_13_NO_SP, statusOut);
//	}
//
//	@Test(expected = Exception.class)
//	public void prepareMainID13DigitisSpFirstPeriod() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_13_FIRST, statusOut);
//	}
//
//	@Test(expected = Exception.class)
//	public void prepareMainID13DigitisSpSecondPeriodWrongDigit() throws Exception {
//		formater.prepareMainID(Calendar.getInstance(), MAINID_13_SECOND_WRONG_DIGIT, statusOut);
//	}
//	
//	@Test
//	public void prepareMainID13DigitisSpSecondPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_13_SECOND, statusOut);
//
//		assertEquals(MAINID_12_SECOND, finalMainID);
//	}
//
//	@Test
//	public void prepareMainID13DigitisSpThirdPeriod() throws Exception {
//		String finalMainID = formater.prepareMainID(Calendar.getInstance(), MAINID_13_THIRD, statusOut);
//
//		assertEquals(MAINID_13_THIRD, finalMainID);
//	}

}
