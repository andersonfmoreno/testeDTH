package br.com.ericsson.teltools.dth.mocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cmg.services.mapevent.MapEventFactory;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.Entity;
import cmg.stdapp.javaformatter.JavaFormatterDataObjectImpl;

public class GenerateContainerMock {

	public final static String HEADER_SEPARATOR = "<=>";
	public final static String ROW_SEPARATOR = "<\\|>";
	public final static String KEY_VALUE_SEPARATOR = "<:>";

	MockJavaFormatterBase formater;

	public GenerateContainerMock(MockJavaFormatterBase formater) {
		this.formater = formater;
	}

	public void populateContainer(InputStream file) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				populateEntities(line);
			}
		} catch (Exception e) {
			throw new GenerateContainerMockException(e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void populateEntities(String composeStr) throws EntityException {
		String[] entitySplited = composeStr.split(HEADER_SEPARATOR);

		String headear = entitySplited[0];
		DataObject entity = new JavaFormatterDataObjectImpl(
				MapEventFactory.createDummyMapEvent());
		Entity flowCommands = formater.createEntity(headear);
		flowCommands.setData(entity);
		formater.putEntity(flowCommands);

		if (entityHasLines(entitySplited)) {
			for (String row : entitySplited[1].split(ROW_SEPARATOR)) {
				String[] data = row.split(KEY_VALUE_SEPARATOR);
				entity.setParameter(data[0], data[1]);
			}
		}
	}

	private boolean entityHasLines(String[] entity) {
		return entity.length > 1;
	}

	class GenerateContainerMockException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public GenerateContainerMockException(String msg) {
			super(msg);
		}
	}

}
