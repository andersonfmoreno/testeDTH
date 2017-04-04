package test.br.com.ericsson.teltools.dth.core;

import org.junit.BeforeClass;
import org.junit.Test;

import br.com.ericsson.teltools.dth.core.LoaderContainer;
import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class LoaderContainerTest {

	static LoaderContainer formater;
	static DataObject dataSource;
	
	@BeforeClass
	public static void init() {
		formater = new LoaderContainer();
		dataSource = new JavaFormatterDataObjectImpl(MapEventFactory.createDummyMapEvent());
	}
	
	@Test()
	public void fail() throws Exception{
		dataSource.setParameter("ENTITY_NAME", "VAZIO");
		dataSource.setParameter("ENTITY_VALUE", "");
		
		formater.format(dataSource);
	}

}
