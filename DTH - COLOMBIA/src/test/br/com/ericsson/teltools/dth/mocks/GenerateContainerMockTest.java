package test.br.com.ericsson.teltools.dth.mocks;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import br.com.ericsson.teltools.dth.mocks.GenerateContainerMock;
import br.com.ericsson.teltools.dth.mocks.MockJavaFormatterBase;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.exceptions.ParameterMissingException;

public class GenerateContainerMockTest {

	MockJavaFormatterBase formater = new MockJavaFormatterBase() {

		@Override
		public void initialize() {
		}

		@Override
		public void format(DataObject arg0) throws Exception {
		}
	};

	@Test
	public void emptyEntity() throws IOException, EntityException {
		InputStream file = getClass().getResourceAsStream(
				"ContainerMockTest.txt");

		GenerateContainerMock container = new GenerateContainerMock(formater);
		container.populateContainer(file);

		assertTrue(formater.getEntity("ACCUMULATOR").getChildIds() == null);
	}

	@Test
	public void emptyOneValue() throws IOException, EntityException,
			ParameterMissingException {
		InputStream file = getClass().getResourceAsStream(
				"ContainerMockTest.txt");

		GenerateContainerMock container = new GenerateContainerMock(formater);
		container.populateContainer(file);

		assertTrue(formater.getEntity("DB_INFOS").getData()
				.getValueAsString("TIMEZONE").equals("GMT-0500"));
	}
	
	@Test
	public void emptyManyValue() throws IOException, EntityException,
			ParameterMissingException {
		InputStream file = getClass().getResourceAsStream(
				"ContainerMockTest.txt");

		GenerateContainerMock container = new GenerateContainerMock(formater);
		container.populateContainer(file);

		assertTrue(formater.getEntity("SUBSCRIBER_TYPE").getData()
				.getValueAsString("postpaid").equals("2"));
		
		assertTrue(formater.getEntity("SUBSCRIBER_TYPE").getData()
				.getValueAsString("prepaid").equals("3"));		
	}	
}
