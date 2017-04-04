package br.com.ericsson.teltools.dth.mocks;

import java.util.Arrays;

import java.util.Map;

import cmg.services.compiler.JavaActivityBase;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.Entity;
import cmg.stdapp.javaformatter.definition.JavaFormatterInterface;

public abstract class MockJavaFormatterBase extends JavaActivityBase implements JavaFormatterInterface
{
	private final Map<String, Entity> mockEntities = new java.util.concurrent.ConcurrentHashMap<String, Entity>();

	public final Entity createEntity(final String paramString)
	{
		return new MockEntity(paramString);
	}

	public final Entity getEntity(final String paramString) throws EntityException
	{
		return mockEntities.get(paramString);
	}

	public final void putEntity(final Entity entity) throws EntityException
	{
		mockEntities.put(entity.getKey(), entity);
	}

	public final boolean removeEntity(final String paramString) throws EntityException
	{
		return mockEntities.remove(paramString) != null;
	}

	public final boolean removeEntities(final String[] paramArrayOfString) throws EntityException
	{
		return mockEntities.keySet().removeAll(Arrays.asList(paramArrayOfString));
	}

	public final void updateEntity(final MockEntity paramEntity) throws EntityException
	{
		mockEntities.put(paramEntity.getKey(), paramEntity);
	}
}
