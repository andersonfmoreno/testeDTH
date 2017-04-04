package test.br.com.ericsson.teltools.dth.smsResponse;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import br.com.ericsson.teltools.dth.smsResponse.SmsResponseController;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class SmsResponseControllerTest {

	SmsResponseController controller = new SmsResponseController();
	DataObject dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
	
	String sessionID = null;
	Map<String, Object> sessionMap = null;
	
	@Test
	public void format() throws Exception{
		
		dataSource.setParameter("short_message", "122120520,5000000,80");
		//dataSource.setParameter("short_message", "dailyPlan,0,sub");
		dataSource.setParameter("destination_addr", "551193498360");
		
		controller.format(dataSource);
		
		System.out.println(dataSource.getParameterContainer().toString());
		
		assertEquals(false, dataSource.parameterExists("STATUS"));
	}	
		
}

