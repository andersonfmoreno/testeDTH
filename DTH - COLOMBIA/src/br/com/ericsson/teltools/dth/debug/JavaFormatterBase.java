package br.com.ericsson.teltools.dth.debug;

import java.util.Arrays;

import java.util.Map;

import cmg.services.compiler.JavaActivityBase;
import cmg.stdapp.container.EntityException;
import cmg.stdapp.javaformatter.definition.JavaFormatterInterface;

public abstract class JavaFormatterBase extends JavaActivityBase implements JavaFormatterInterface
{
	private final Map<String, Entity> entities = new java.util.concurrent.ConcurrentHashMap<String, Entity>();

	public final Entity createEntity(final String paramString)
	{
		return new Entity(paramString);
	}

	public final Entity getEntity(final String paramString) throws EntityException
	{
		return entities.get(paramString);
	}

	public final void putEntity(final Entity entity) throws EntityException
	{
		entities.put(entity.getKey(), entity);
	}

	public final boolean removeEntity(final String paramString) throws EntityException
	{
		return entities.remove(paramString) != null;
	}

	public final boolean removeEntities(final String[] paramArrayOfString) throws EntityException
	{
		return entities.keySet().removeAll(Arrays.asList(paramArrayOfString));
	}

	public final void updateEntity(final Entity paramEntity) throws EntityException
	{
		entities.put(paramEntity.getKey(), paramEntity);
	}
}
