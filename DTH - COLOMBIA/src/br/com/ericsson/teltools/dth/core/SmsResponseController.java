package br.com.ericsson.teltools.dth.core;

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
            
            if(message.equals("ACTIVO")){
                dataSource.setParameter("0.productName","*");
                dataSource.setParameter("0.0.MAINID",mainID);
                dataSource.setParameter("0.operation","activateSubscriber");
                return;
            }
            
            clearDataObject(dataSource);
            
            String[] values = message.split("\\,");
            String productName = values[0];
            //java.math.BigInteger volumeConvertido = new java.math.BigInteger(values[1]);
            //volumeConvertido = volumeConvertido.multiply(ESCALA);
            //String volume = volumeConvertido.toString();
            String volume = values[1];
            String percentage = values[2];
            //String offerID = values[3];
            //String serviceClass = values[4];

            if(checkParameters(mainID, productName, volume, percentage)){
                
                dataSource.setParameter("0.0.originatingNodeInfo", "SDP");
                dataSource.setParameter("0.0.charged","yes");
                
                if (percentage.toUpperCase().equals("SUB")) {
                    executeSubscription(dataSource, mainID, productName);
                }
                else if(percentage.equals("100")) {
                    cancelSubscription(dataSource, mainID, productName);
                }
                //IN COLOMBIA WE DONT HAVE THIS OPERATION UNTIL NOW, WHEN WE HAVE DATA OPERATION PLEASE ACTIVE THIS ONE 
                //REMOVING THE COMMENTS BELOW TO EXECUTE A THRESHOLD OPERATION.
                //else{
                //    executeTreshold(dataSource, mainID,productName,volume, percentage);
                //}                
            }
            else
            {
                dataSource.setParameter("STATUS","ERROR");
                dataSource.setParameter("DATA_LOGIN", dataSource.getParameterContainer());                
                throw new Exception(String.format(
                        "One or more mandatory parameter (mainID, productName, " +
                            "subscription) is empty"));
            }
            
        } catch (Exception e) {
            formatErrorResponse(dataSource, String.format("%s - %s %s",
                    dataSource, e.getClass().toString(), e.getMessage(),
                    Arrays.toString(e.getStackTrace())), ERROR_CODE);
        }
    }

    private
    void formatErrorResponse(DataObject dataSource, String errorDescription,
            String errorCode) throws Exception {
        dataSource.setParameter("STATUS","ERROR");
        dataSource.setParameter("TARGET", "BackEndController");
        dataSource.setParameter("DESCRIPTION", errorDescription);
        dataSource.setParameter("ERROR_CODE", errorCode);
        
        dataSource.setParameter("DATA_LOGIN", dataSource.getParameterContainer());
    }
    
    private
    Boolean checkParameters(String mainID, String productName, String volume, 
            String subscription){

        if(mainID == null || productName == null || subscription == null){
            return false;
        }

        
        if(mainID.isEmpty() || productName.isEmpty() || subscription.isEmpty()){
            return false;
        }
        return true;
    }

    private
    void executeSubscription(DataObject dataSource, String mainID, String productName) throws Exception {
        dataSource.setParameter("0.operation", "createNewSubscription");
        dataSource.setParameter("0.productName", "dailyPlan");
        dataSource.setParameter("0.0.MAINID", mainID);
        dataSource.setParameter("0.0.IMSI", "0");
        dataSource.setParameter("0.0.subscriberType", "dailyPlan");
        dataSource.setParameter("0.0.deviceType", "dailyPlan");
        dataSource.setParameter("0.0.externalData", "SDP");
    }

    private
    void executeTreshold(DataObject dataSource, String mainID,
            String prodctName,String volume, String percentage) throws Exception {
        dataSource.setParameter("0.operation", "thresholdVolume");
        dataSource.setParameter("0.productName", prodctName);
        dataSource.setParameter("0.0.MAINID", mainID);
        dataSource.setParameter("0.0.thresholdVolume", volume);
        dataSource.setParameter("0.0.utilizationPercentage", percentage);
    }    

    private
    void cancelSubscription(DataObject dataSource, String mainID,
            String prodctName) throws Exception {
        dataSource.setParameter("0.operation", "cancelSubscription");
        dataSource.setParameter("0.productName", prodctName);
        dataSource.setParameter("0.0.MAINID", mainID);
        dataSource.setParameter("0.0.timeExpiredFlag", false);
        dataSource.setParameter("0.0.originatingNodeInfo", "WS");
        dataSource.setParameter("0.0.cancelType", "active");
        dataSource.setParameter("0.0.externalData", "SDP");        
    }    
    
    private
    void clearDataObject(final DataObject dataSource) throws Exception {
        Set<String> retainDataSource = new HashSet<String>();
        retainDataSource.add("__GlobalTransactionID");
        retainDataSource.add("WS_requestID");
        retainDataSource.add("HTTPSessionID");

        dataSource.getParameterContainer().keySet().retainAll(retainDataSource);
    }
    
}