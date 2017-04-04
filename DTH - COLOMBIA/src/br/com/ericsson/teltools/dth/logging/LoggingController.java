package br.com.ericsson.teltools.dth.logging;

/** HEADER STARTS **/
//Any changes between HEADER STARTS and HEADER ENDS will be discarded during validation and compilation
//Additions or alteration of import statements will be ignored and replaced with the original
import java.lang.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.atomic.*;

import cmg.stdapp.javaformatter.util.DataTypeHelper;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.Entity;
import cmg.stdapp.container.DbUnavailableException;
import cmg.stdapp.container.EntityException;

import cmg.stdapp.javaformatter.operations.merge.Merge;
import cmg.stdapp.javaformatter.operations.stringgenerator.StringGenerator;
import cmg.stdapp.javaformatter.operations.stringpad.StringPad;
import cmg.stdapp.javaformatter.operations.stringpad.StringPad.FillOptions;
import cmg.stdapp.javaformatter.operations.timeformatter.TimeFormatter;
import cmg.stdapp.javaformatter.operations.timeformatter.TimeUnits;

public class LoggingController extends cmg.stdapp.javaformatter.definition.JavaFormatterBase implements cmg.stdapp.javaformatter.definition.JavaFormatterInterface
/** HEADER ENDS **/
{
	final String LOGGING_ERROR = "10400";
	final String REMOVE_ALL_DATA_SOURCE = "10401";
	final String ORIGIN_NOT_FOUND = "10402";

	final String ERROR_SOURCE_MODULE = "Logging";
	final String RECORD_DELIMITER = "\\|";

	public void initialize()
	{
		// This method will be called once during creation of class, add any
		// initialization needed here
	}

	/**
	 * Executed for each MapEvent.
	 * 
	 * @param obj
	 *            , data object containing all parameters in the MapEvent. Also
	 *            contain helpful methods to get the parameters with suitable
	 *            datatypes
	 */
	@SuppressWarnings("unchecked")
	public void format(DataObject dataSource) throws Exception
	{
		String sessionID = dataSource.getValueAsString("SESSION_ID");
		try
		{

			if (dataSource.parameterExists("ORIGIN"))
			{
				if (!(dataSource.parameterExists("LOGGING_LIST")))
				{
					removeAllDataSource(dataSource);
					dataSource.setParameter("SESSION_ID", sessionID);
					dataSource.setParameter("STATUS", "ERROR");
					dataSource.setParameter("ORIGIN", "LoggingController");
					dataSource.setParameter("TARGET", "LoggingResponse");
					return;
				}

				Map<String, Object> loggingList = (Map<String, Object>) dataSource.getValue("LOGGING_LIST");

				if (loggingList.isEmpty())
				{
					removeAllDataSource(dataSource);
					dataSource.setParameter("SESSION_ID", sessionID);
					dataSource.setParameter("STATUS", "OK");
					dataSource.setParameter("ORIGIN", "LOGGING");
					dataSource.setParameter("TARGET", "LoggingResponse");

				}
				else
				{
					dataSource.setParameter("ORIGIN", "LOGGING");
					dataSource.setParameter("TARGET", "ConnectorLOGGING");
				}

			}
			else
			{
				throw createException(ORIGIN_NOT_FOUND, "internal", ERROR_SOURCE_MODULE, "Origin not found");
			}
		}
		catch (Exception error)
		{
			dataSource.setParameter("STATUS", "ERROR");
			dataSource.setParameter("ORIGIN", "LOGGING");
			dataSource.setParameter("TARGET", "LoggingResponse");
			dataSource.setParameter("SESSION_ID", sessionID);

			removeAllDataSource(dataSource);

			String errorCode = LOGGING_ERROR;
			String errorType = "internal";
			String errorSource = ERROR_SOURCE_MODULE;
			String errorDescription = formatErroDescription(error);

			if ((error.getMessage() != null) && (error.getMessage().matches("(.*)CORE_ERROR(.+)")))
			{
				String[] errors = error.getMessage().split(RECORD_DELIMITER);
				errorCode = errors[1];
				errorType = errors[2];
				errorSource = errors[3];
				errorDescription = errors[4];
			}

			dataSource.setParameter("ERROR_CODE", errorCode);
			dataSource.setParameter("ERROR_DESCRIPTION", errorDescription);
			dataSource.setParameter("ERROR_TYPE", errorType);
			dataSource.setParameter("ERROR_SOURCE", errorSource);

		}
	}

	private Exception createException(String errorCode, String errorType, String errorSource, String errorDescription) throws Exception
	{
		if ((errorDescription != null) && (errorDescription.matches("(.*)LOGGING_ERROR(.+)")))
		{
			return new Exception(errorDescription);
		}

		return new Exception("LOGGING_ERROR|" + errorCode + "|" + errorType + "|" + errorSource + "|" + errorDescription);
	}

	private void removeAllDataSource(final DataObject dataSource) throws Exception
	{
		try
		{
			HashSet<String> retainDataSource = new HashSet<String>();
			retainDataSource.add("__GlobalTransactionID");
			retainDataSource.add("WS_requestID");

			dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
		}
		catch (Exception e)
		{
			throw createException(REMOVE_ALL_DATA_SOURCE, "internal", ERROR_SOURCE_MODULE, formatErroDescription(e));
		}

	}

	private String formatErroDescription(Exception e)
	{
		return new StringBuilder(e.getClass().getCanonicalName()).append(":").append(e.getMessage()).append("(").append(Arrays.toString(e.getStackTrace())).append(")").toString();
	}

}
