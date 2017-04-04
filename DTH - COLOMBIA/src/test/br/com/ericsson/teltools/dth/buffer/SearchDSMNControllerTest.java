package test.br.com.ericsson.teltools.dth.buffer;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import br.com.ericsson.teltools.dth.buffer.SearchDSMNController;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class SearchDSMNControllerTest
{
	static final String INPUT_TEXT_VALUE_SUBSCRIPTION = "05/12/11 08:00:00,000000 -02:00<;>21/02/12 18:06:00,000000 AMERICA/SAO_PAULO<;>05/12/11 08:00:00,000000 -02:00<;><.>21/02/12 10:11:24,000000 AMERICA/SAO_PAULO<;>";
	static final String RESULT_TEXT_VALUE_SUBSCRIPTION = "20111205080000-0200<;>20120221180600-0200<;>20111205080000-0200<;><.>20120221101100-0200<;>";
	static final String BUFFER_NUMBER_VALUE = "123<;>456<;>789";
	static final String INPUT_TEXT_VALUE_SUBSCRIBERS = "df@s.com<;>handset<;>blablabla<;>2handset<;>auhuhae<;>";
	static final String RESULT_TEXT_VALUE_SUBSCRIBERS = "df@s.com<;>handset<;>blablabla<;>2handset<;>auhuhae<;>";
	
	static final String TIMESTAMP_RESULT = "1323079200000<;>1329854760000<;>1323079200000<;><.>1329826260000<;>";
	
	static final String SUBSCRIBER_CONTAINER_INPUT = "234234<;>1<;>24234<;>234234<;>0<;>1<;>1<;><|>df@s.com<;>handset<;>blablabla<;>2handset<;>auhuhae<;>";
	static final String SUBSCRIBER_CONTAINER_RESULT = "234234<;>1<;>24234<;>234234<;>0<;>1<;>1<;><|>df@s.com<;>handset<;>blablabla<;>2handset<;>auhuhae<;>";
	static final String SUBSCRIPTION_CONTAINER_RESULT = "1<;> 0<;> 1<;> 11<;> 9<;> <;><.> 2<;> 0<;> 1<;> 1<;> 9<;> <;><|>20120403150900-0300<;>20120403150900-0300<;>20120403150900-0300<;><.>20120403153900-0300<;>20120403153900-0300<;>20120403153900-0300<;>";
	static final String SUBSCRIPTION_CONTAINER_INPUT = "1<;> 0<;> 1<;> 11<;> 9<;> <;><.> 2<;> 0<;> 1<;> 1<;> 9<;> <;><|>03/04/12 15:09:47,450000 AMERICA/SAO_PAULO<;>03/04/12 15:09:51,194000 AMERICA/SAO_PAULO<;>03/04/12 15:09:54,636000 AMERICA/SAO_PAULO<;><.>03/04/12 15:39:15,212000 AMERICA/SAO_PAULO<;>03/04/12 15:39:19,459000 AMERICA/SAO_PAULO<;>03/04/12 15:39:22,423000 AMERICA/SAO_PAULO<;>";
	
	
	
	static final String SUBSCRIBERS = "subscribers";
	static final String SUBSCRIPTIONS = "subscriptions";
	static String CLEAR = "clearArrays";

	static DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
	static DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());

	static SearchDSMNController searchDSMNController;

	static final String SUBSCRIBER_MEMORY_PARAM = "SUBSCRIBER_DATA_MEMORY";
	static final String SUBSCRIPTIONS_MEMORY_PARAM = "SUBSCRIPTIONS_DATA_MEMORY";
	static final String TARGET = "TARGET";
	static final String TARGET_SEARCH_DB = "DSNBDBSearch";
	static final String TARGET_CONTROLLER = "Response";
	static final String FLAG_SUBSCRIPTION = "FLAG_SUBSCRIPTION";
	static final String FLAG_SUBSCRIBERS = "FLAG_SUBSCRIBERS";
	

	@Before
	public void initialize() throws EntityException
	{
		searchDSMNController = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}
			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};

	}

	public void insertInBuffer(DataObject dataSource, String tableName, int position, String mainID, String textValue, String numberValue) throws Exception
	{
		dataSource.setParameter("TABLE", tableName);
		dataSource.setParameter("POSITION", position);
		dataSource.setParameter("MAINID", mainID);
		dataSource.setParameter("TEXT_VALUE", textValue);
		dataSource.setParameter("NUMBER_VALUE", numberValue);
		searchDSMNController.format(dataSource);
	}

	public void clearBuffer(DataObject dataSource) throws Exception
	{
		dataSource.setParameter(CLEAR, "clear");
		searchDSMNController.format(dataSource);
	}

	private void populateDataSourceToSearch(DataObject dataSource, String mainID)
	{
		dataSource.setParameter("FIELD_NAME", "MAINID");
		dataSource.setParameter("FIELD_QUERY", mainID);
		dataSource.setParameter("ORIGIN", "SearchAndValidation");
		dataSource.setParameter("REQUEST_NUMBER", 0);
		dataSource.setParameter("SESSION_ID", "A1S2D3F4G5H6J7K8L9Ç0");
		dataSource.setParameter("STATUS", "OK");
		dataSource.setParameter("SUBSCRIBER_NUMBER", 0);
		dataSource.setParameter(TARGET, "Response");
		dataSource.setParameter("FLAG_SUBSCRIBERS", 1);
		dataSource.setParameter("FLAG_SUBSCRIPTION", 1);
	}

	@Test
	public void foundSubscriberInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		insertInBuffer(dataSource, SUBSCRIBERS, 0, "553488474728", INPUT_TEXT_VALUE_SUBSCRIBERS, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "553488474728");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsString(SUBSCRIBER_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIBERS);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
	}

	@Test
	public void foundSubscriptionInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		insertInBuffer(dataSource, SUBSCRIPTIONS, 1, "553488474728", INPUT_TEXT_VALUE_SUBSCRIPTION, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "553488474728");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsString(SUBSCRIPTIONS_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIPTION);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
	}

	@Test
	public void notFoundSubscriberInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.parameterExists(SUBSCRIPTIONS_MEMORY_PARAM), false);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 1);
	}

	@Test
	public void notFoundSubscriptionInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.parameterExists("SUBSCRIBER_DATA_MEMORY"), false);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 1);
	}

	@Test
	public void testTargetToResponseWhenFoundSubscriptionAndSubscriberInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		insertInBuffer(dataSource, SUBSCRIBERS, 0, "553488474721", INPUT_TEXT_VALUE_SUBSCRIBERS, BUFFER_NUMBER_VALUE);
		insertInBuffer(dataSource, SUBSCRIPTIONS, 1, "553488474721", INPUT_TEXT_VALUE_SUBSCRIPTION, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsString(SUBSCRIBER_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIBERS);
		assertEquals(dataSource.getValueAsString(SUBSCRIPTIONS_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIPTION);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_CONTROLLER);

	}

	@Test
	public void foundSubscriberButNotFoundSubscriptionInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		insertInBuffer(dataSource, SUBSCRIBERS, 0, "553488474721", INPUT_TEXT_VALUE_SUBSCRIBERS, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsString(SUBSCRIBER_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIBERS);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 1);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
	}

	@Test
	public void foundSubscriptionButNotFoundSubscriberInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		insertInBuffer(dataSource, SUBSCRIPTIONS, 0, "553488474721", INPUT_TEXT_VALUE_SUBSCRIPTION, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsString(SUBSCRIPTIONS_MEMORY_PARAM).trim(), BUFFER_NUMBER_VALUE + "<|>" + RESULT_TEXT_VALUE_SUBSCRIPTION);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 1);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
	}

	@Test
	public void notFoundSubscriptionAndSubscriberInBuffer() throws Exception
	{
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "553488474721");

		searchDSMNController.format(dataSource);

		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 1);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 1);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
	}

	@Test
	public void foundSubscriberInContainer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		subscriberContainerMap.setParameter("551199770011", SUBSCRIBER_CONTAINER_INPUT);
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "551199770011");
		searchDSMNControllerContainerTest.format(dataSource);
		assertEquals(dataSource.getValueAsString(SUBSCRIBER_MEMORY_PARAM), SUBSCRIBER_CONTAINER_RESULT);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
	}

	@Test
	public void foundSubscriptionInContainer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		subscriptionContainerMap.setParameter("551199770011", SUBSCRIPTION_CONTAINER_INPUT);
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "551199770011");
		searchDSMNControllerContainerTest.format(dataSource);
		assertEquals(dataSource.getValueAsString(SUBSCRIPTIONS_MEMORY_PARAM), SUBSCRIPTION_CONTAINER_RESULT);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
	}

	@Test
	public void notFoundSubscriberInContainer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "551199770016");
		searchDSMNControllerContainerTest.format(dataSource);
		assertEquals(dataSource.parameterExists(SUBSCRIBER_MEMORY_PARAM), false);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 1);
	}

	@Test
	public void notFoundSubscriptionInContainer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "551199770016");
		searchDSMNControllerContainerTest.format(dataSource);
		assertEquals(dataSource.parameterExists(SUBSCRIPTIONS_MEMORY_PARAM), false);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 1);
	}

	@Test
	public void testTargetToResponseWhenFoundSubscriberAndSubscriptionInContainer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		subscriptionContainerMap.setParameter("551199770011", SUBSCRIPTION_CONTAINER_INPUT);
		subscriberContainerMap.setParameter("551199770011", SUBSCRIBER_CONTAINER_INPUT);
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		populateDataSourceToSearch(dataSource, "551199770011");
		searchDSMNControllerContainerTest.format(dataSource);

		assertEquals(dataSource.parameterExists(SUBSCRIPTIONS_MEMORY_PARAM), true);
		assertEquals(dataSource.parameterExists(SUBSCRIBER_MEMORY_PARAM), true);
		assertEquals(dataSource.getValueAsString(SUBSCRIBER_MEMORY_PARAM), SUBSCRIBER_CONTAINER_RESULT);
		assertEquals(dataSource.getValueAsString(SUBSCRIPTIONS_MEMORY_PARAM), SUBSCRIPTION_CONTAINER_RESULT);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_CONTROLLER);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
	}

	@Test
	public void foundSubscriberInContainerAndSubscriptionInBuffer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		subscriberContainerMap.setParameter("551199770011", SUBSCRIBER_CONTAINER_INPUT);
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		insertInBuffer(dataSource, SUBSCRIPTIONS, 1, "551199770011", INPUT_TEXT_VALUE_SUBSCRIPTION, BUFFER_NUMBER_VALUE);
		populateDataSourceToSearch(dataSource, "551199770011");
		searchDSMNControllerContainerTest.format(dataSource);

		assertEquals(dataSource.parameterExists(SUBSCRIPTIONS_MEMORY_PARAM), true);
		assertEquals(dataSource.parameterExists(SUBSCRIBER_MEMORY_PARAM), true);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_CONTROLLER);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 0);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);

	}

	@Test
	public void foundSubscriberInContainerAndNotFoundSubscriptionInBuffer() throws Exception
	{
		final DataObject subscriptionContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		final DataObject subscriberContainerMap = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
		clearBuffer(dataSource);
		subscriberContainerMap.setParameter("551199770011", SUBSCRIBER_CONTAINER_INPUT);
		SearchDSMNController searchDSMNControllerContainerTest = new SearchDSMNController()
		{
			@Override
			protected DataObject getSubscriberContainer() throws EntityException
			{
				return subscriberContainerMap;
			}

			@Override
			protected DataObject getSubscriptionContainer() throws EntityException
			{
				return subscriptionContainerMap;
			};
		};
		populateDataSourceToSearch(dataSource, "551199770011");
		searchDSMNControllerContainerTest.format(dataSource);

		assertEquals(dataSource.parameterExists(SUBSCRIPTIONS_MEMORY_PARAM), false);
		assertEquals(dataSource.parameterExists(SUBSCRIBER_MEMORY_PARAM), true);
		assertEquals(dataSource.getValueAsString(TARGET), TARGET_SEARCH_DB);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIPTION), 1);
		assertEquals(dataSource.getValueAsInt(FLAG_SUBSCRIBERS), 0);
	}
	
	@Test
	public void testFormatTimeStamp() throws ParseException{
		SearchDSMNController s = new SearchDSMNController();
		String result = s.formatTimeStampToSaveInBuffer(INPUT_TEXT_VALUE_SUBSCRIPTION).toString();
		assertEquals(TIMESTAMP_RESULT, result);
	}

	/*
	 * public void stressTest() throws Exception{ final int repetions = 10;
	 * 
	 * for(int i=0; i<repetions; i++){ final int j=i; new Thread(){
	 * 
	 * @Override public void run() { try { Thread.sleep(10-j); } catch
	 * (InterruptedException e) { e.printStackTrace(); } DataObject dataSource =
	 * new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
	 * long begin, end; begin = Calendar.getInstance().getTimeInMillis();
	 * for(int i = 0; i < SearchDSMNController.ByteBuffer.MAX_ENTITIES; i++){
	 * dataSource.setParameter("POSITION", i);
	 * dataSource.setParameter("MAINID","553488474728");
	 * dataSource.setParameter("TEXT_VALUE", TEXT_VALUE);
	 * dataSource.setParameter("NUMBER_VALUE", NUMBER_VALUE); try {
	 * searchDSMNController.format(dataSource); } catch (Exception e) {
	 * e.printStackTrace(); } } end = Calendar.getInstance().getTimeInMillis();
	 * System.out.println(j + " thread : " + (end-begin)); } }.start();
	 * 
	 * } }
	 * 
	 * public static void main(String[] args) throws Exception {
	 * SearchDSMNControllerTest test = new SearchDSMNControllerTest();
	 * test.initialize(); test.stressTest(); }
	 */

}
