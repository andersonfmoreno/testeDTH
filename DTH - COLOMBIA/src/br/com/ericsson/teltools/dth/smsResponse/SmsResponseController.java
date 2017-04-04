package br.com.ericsson.teltools.dth.smsResponse;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.ericsson.teltools.dth.mocks.MockJavaFormatterBase;
import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.definition.JavaFormatterInterface;

public class SmsResponseController extends MockJavaFormatterBase implements
		JavaFormatterInterface {

	static final String ERROR_CODE = "27000";
	static final java.math.BigInteger ESCALA = new java.math.BigInteger("1000");

	public void initialize() {
	}
	
	@Override
	public void format(DataObject dataSource) throws Exception {
		try {
	
			String mainID = dataSource.getValueAsString("destination_addr");
			String message = dataSource.getValueAsString("short_message");
			
			clearDataObject(dataSource);
			
			String[] values = message.split("\\,");
			String productName = values[0];
			java.math.BigInteger volumeConvertido = new java.math.BigInteger(values[1]);
			volumeConvertido = volumeConvertido.multiply(ESCALA);
			String volume = volumeConvertido.toString();
			String subscription = values[2];
			String percentage = subscription;

			if(checkParameters(mainID, productName, volume, subscription)){
				
				dataSource.setParameter("0.0.originatingNodeInfo", "SDP");
				
				if (subscription.toUpperCase().equals("SUB")) {
					executeSubscription(dataSource, mainID, productName);
				}
				else{
					executeTreshold(dataSource, mainID,productName,volume, percentage);
				}				
			}
			else
			{
				dataSource.setParameter("STATUS","ERROR");
				dataSource.setParameter("DATA_LOGIN", dataSource.getParameterContainer());				
				throw new Exception(String.format(
						"One or more mandatory parameter (mainID, productName, " +
							"volume, subscription) is empty"));
			}
			
		} catch (Exception e) {
			formatErrorResponse(dataSource, String.format("%s - %s %s",
					dataSource, e.getClass().toString(), e.getMessage(),
					Arrays.toString(e.getStackTrace())), ERROR_CODE);
		}
	}

	//private
	void formatErrorResponse(DataObject dataSource, String errorDescription,
			String errorCode) throws Exception {
		dataSource.setParameter("STATUS","ERROR");
		dataSource.setParameter("TARGET", "BackEndController");
		dataSource.setParameter("DESCRIPTION", errorDescription);
		dataSource.setParameter("ERROR_CODE", errorCode);
		
		dataSource.setParameter("DATA_LOGIN", dataSource.getParameterContainer());
	}
	
	//private
	Boolean checkParameters(String mainID, String productName, String volume, 
			String subscription){

		if(mainID == null || productName == null|| volume == null  || subscription == null){
			return false;
		}

		
		if(mainID.isEmpty() || productName.isEmpty() || volume.isEmpty() || subscription.isEmpty()){
			return false;
		}
		return true;
	}

	//private
	void executeSubscription(DataObject dataSource, String mainID, String productName) throws Exception {
		dataSource.setParameter("0.operation", "createNewSubscription");
		dataSource.setParameter("0.productName", "dailyPlan");
		dataSource.setParameter("0.0.MAINID", mainID);
		dataSource.setParameter("0.0.IMSI", "0");
		dataSource.setParameter("0.0.subscriberType", "dailyPlan");
		dataSource.setParameter("0.0.deviceType", "dailyPlan");
		dataSource.setParameter("0.externalData", "SDP");
	}

	//private
	void executeTreshold(DataObject dataSource, String mainID,
			String prodctName,String volume, String percentage) throws Exception {
		dataSource.setParameter("0.operation", "thresholdVolume");
		dataSource.setParameter("0.productName", prodctName);
		dataSource.setParameter("0.0.MAINID", mainID);
		dataSource.setParameter("0.0.thresholdVolume", volume);
		dataSource.setParameter("0.0.utilizationPercentage", percentage);
	}	
	
	//private
	void clearDataObject(final DataObject dataSource) throws Exception {
		Set<String> retainDataSource = new HashSet<String>();
		retainDataSource.add("__GlobalTransactionID");
		retainDataSource.add("WS_requestID");
		retainDataSource.add("HTTPSessionID");

		dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
	}
	
}