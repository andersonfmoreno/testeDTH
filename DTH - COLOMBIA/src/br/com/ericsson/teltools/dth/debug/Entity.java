package br.com.ericsson.teltools.dth.debug;

import java.util.Set;

import cmg.stdapp.javaformatter.DataObject;

public final class Entity implements cmg.stdapp.javaformatter.Entity
{
	private String key;
	private DataObject dataSource;

	public Entity(final String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setParentKey(final String paramString)
	{
	}

	@Override
	public String getParentKey()
	{
		return null;
	}

	@Override
	public void addChild(final cmg.stdapp.javaformatter.Entity paramEntity)
	{
	}

	@Override
	public void removeChild(final String paramString)
	{
	}

	@Override
	public cmg.stdapp.javaformatter.Entity[] getChildren()
	{
		return null;
	}

	@Override
	public long getRemainingLifetime()
	{
		return 0;
	}

	@Override
	public void setLifetime(final long paramLong)
	{
	}

	@Override
	public DataObject getData()
	{
		return dataSource;
	}

	@Override
	public void setData(final DataObject paramDataObject)
	{
		dataSource = paramDataObject;
	}

	@Override
	public cmg.stdapp.javaformatter.Entity getChild(final String paramString)
	{
		return null;
	}

	@Override
	public Set<String> getChildIds()
	{
		return null;
	}
}
