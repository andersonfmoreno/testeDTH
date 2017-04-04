package br.com.ericsson.teltools.dth.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cmg.stdapp.javaformatter.DataObject;
import cmg.stdapp.javaformatter.definition.JavaFormatterBase;
import cmg.stdapp.javaformatter.definition.JavaFormatterInterface;

public class QueryResponse extends JavaFormatterBase implements
JavaFormatterInterface {
    static final String EMPTY = "";

    static final String NUM_STR_SEPARATOR = "<\\#>";
    static final String REGISTER_SEPARATOR = "<\\.>";
    static final String FIELD_SEPARATOR = "<;>";
    static final String HEADER_DELIMETER = "<:>";

    static final String NUM_STR_SEPARATOR_RECREATE = NUM_STR_SEPARATOR.replace("\\", "");

    static final String SUBSCRIBER_DB_PARAM = "SUBSCRIBER_DATA_DB";
    static final String SUBSCRIBER_MEMORY_PARAM = "SUBSCRIBER_DATA_MEMORY";
    static final String SUBSCRIBER_RESULT_PARAM = "SUBSCRIBER_DATA";

    static final String SUBSCRIPTIONS_RESULT_PARAM = "SUBSCRIPTIONS_DATA";
    static final String SUBSCRIPTIONS_DB_PARAM = "SUBSCRIPTIONS_DATA_DB";
    static final String SUBSCRIPTIONS_MEMORY_PARAM = "SUBSCRIPTIONS_DATA_MEMORY";

    static final String FIELD_NAME_PARAM = "FIELD_NAME";
    static final String STATUS_PARAM = "STATUS";
    static final String ORIGIN_PARAM = "ORIGIN";
    static final String TARGET_PARAM = "TARGET";

    static final String ERROR_SOURCE = "errorSource";
    static final String ERROR_DESCRIPTION = "errorDescription";
    static final String ERROR_TYPE = "errorType";
    static final String ERROR_CODE = "errorCode";

    static final Map<String, Object> EMPTY_MAP = new java.util.concurrent.ConcurrentHashMap<String, Object>(0);
    static final String[] EMPTY_STRING_ARRAY = new String[0];

    static final String[] KEYS_SUBSCRIBER = new String[] { "MAINID",
                                                           "CAMPAIGN_ID",
                                                           "PERIOD_CAMPAIGN",
                                                           "MAX_USES_IN_PERIOD_CAMPAIGN",
                                                           "USES_IN_PERIOD_CAMPAIGN",
                                                           "IMSI",
                                                           "IMEI",
                                                           "ICCID",
                                                           "PROPERTIES", 
                                                           "SUBSCRIBER_TYPE_ID",
                                                           "DEVICE_TYPE_ID",
                                                           "EMAIL",
                                                           "CAMPAIGN_NAME",
                                                           "PERIOD_TYPE",
                                                           "END_CURRENT_PERIOD_CAMPAIGN",
                                                           "START_NEXT_PERIOD_CAMPAIGN",
                                                           "START_TIMESTAMP_CAMPAIGN",
                                                           "END_TIMESTAMP_CAMPAIGN",
                                                           "deviceType",
                                                           "DEVICE_TYPE_DESCRIPTION",
                                                           "subscriberType",
                                                           "SUBSCRIBER_TYPE_DESCRIPTION" };

    static final String[] KEYS_SUBSCRIPTIONS = new String[] { "MAINID",
                                                            "PROPERTIES",
                                                            "MASTER_SUBSCRIPTION_ID",
                                                            "RENEWAL_RETRIES_LEFT",
                                                            "MAX_RENEWALS_LEFT",
                                                            "QOS_UPLOAD",
                                                            "QOS_DOWNLOAD",
                                                            "PRODUCT_ID",
                                                            "SUBSCRIPTION_POLICY_ID",
                                                            "POLICY_ID",
                                                            "PROMOTION_ID",
                                                            "PROMOTION_USES",
                                                            "SUBSCRIPTION_ID",
                                                            "LAST_RENEWAL_RETRY",
                                                            "STATUS_NAME",
                                                            "TIMESTAMP_POLICIE",
                                                            "END_TIMESTAMP",
                                                            "START_TIMESTAMP" 
                                                            };

    public void initialize() 
    {
        
    }

    public void format(DataObject dataSource) throws Exception 
    {
        try 
        {

            String subscriberDb = initializeParam(dataSource, SUBSCRIBER_DB_PARAM);
            String subscriberMemory = initializeParam(dataSource, SUBSCRIBER_MEMORY_PARAM);
            dataSource.setParameter(SUBSCRIBER_RESULT_PARAM, formatSubscriber(subscriberMemory, subscriberDb));

            String subscriptionsDb = initializeParam(dataSource, SUBSCRIPTIONS_DB_PARAM);
            String subscriptionsMemory = initializeParam(dataSource, SUBSCRIPTIONS_MEMORY_PARAM);
            dataSource.setParameter(SUBSCRIPTIONS_RESULT_PARAM, formatSubscriptions(subscriptionsMemory, subscriptionsDb));

            dataSource.setParameter(STATUS_PARAM, "OK");
            dataSource.setParameter(ORIGIN_PARAM, "subscriberAndSubscriptionQuery");
            dataSource.setParameter(TARGET_PARAM, "SearchAndValidation");
        } 
        catch (Exception e) 
        {
            dataSource.setParameter(STATUS_PARAM, "ERROR");
            dataSource.setParameter(ERROR_SOURCE, "dth");
            dataSource.setParameter(ERROR_CODE, 666L);
            dataSource.setParameter(ERROR_TYPE, "internal");
            dataSource.setParameter(ERROR_DESCRIPTION,
                    e.getMessage() + Arrays.toString(e.getStackTrace()));
        }

    }

    // TODO comment this
    private String initializeParam(DataObject dataSource, String paramName)  throws Exception 
    {
        if (!dataSource.parameterExists(paramName)) {
            return EMPTY;
        }
        String value = dataSource.getValueAsString(paramName);
        if (value == null) {
            return EMPTY;
        }
        return value;
    }

    private Map<String, Object> formatSubscriptions(String dataMemory, String dataDb) 
    {
        if (!dataMemory.isEmpty()) {
            return parseToMapList(dataMemory);
        }
        if (!dataDb.isEmpty()) {
            return parseToMapList(dataDb);
        }
        return EMPTY_MAP;
    }

    private Map<String, Object> parseToMapList(String data) {
        Map<String, Object> result = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        List<String> lines = splitLines(data);
        for (int index = 0; index < lines.size(); index++) {
            result.put(String.valueOf(index),
                    parseToMap(lines.get(index), KEYS_SUBSCRIPTIONS));
        }
        return result;
    }

    private List<String> splitLines(String data) {
        List<String> lines = new ArrayList<String>();
        String[] parts = data.split(HEADER_DELIMETER);
        String header = parts[0];

        parts = parts[1].split(NUM_STR_SEPARATOR);

        String[] numberFields = parts[0].split(REGISTER_SEPARATOR);

        String[] charFields = EMPTY_STRING_ARRAY;
        if (parts.length > 1) {
            charFields = parts[1].split(REGISTER_SEPARATOR);
        }

        for (int index = 0; index < numberFields.length; index++) {
            StringBuilder line = new StringBuilder();
            line.append(header);
            line.append(HEADER_DELIMETER);
            line.append(numberFields[index]);
            line.append(NUM_STR_SEPARATOR_RECREATE);

            if (charFields.length > index) {
                line.append(charFields[index]);
            } else {
                line.append(EMPTY);
            }

            lines.add(line.toString());
        }

        return lines;
    }

    private Map<String, Object> formatSubscriber(String dataMemory, String dataDb) {
        if (!dataMemory.isEmpty()) {
            return parseToMap(dataMemory, KEYS_SUBSCRIBER);
        }
        if (!dataDb.isEmpty()) {
            return parseToMap(dataDb, KEYS_SUBSCRIBER);
        }
        return EMPTY_MAP;
    }

    private Map<String, Object> parseToMap(String data, String[] mapKeys) {
        Map<String, Object> result = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        List<String> values = splitValues(data);
        for (int i = 0; i < mapKeys.length; i++) {
            result.put(mapKeys[i], values.get(i));
        }
        return result;
    }

    private List<String> splitValues(String data) {
        List<String> fields = new ArrayList<String>();

        String[] parts = data.split(HEADER_DELIMETER);
        fields.add(parts[0]);

        parts = parts[1].split(NUM_STR_SEPARATOR);
        fields.addAll(Arrays.asList(parts[0].split(FIELD_SEPARATOR)));
        if (parts.length > 1) {
            fields.addAll(Arrays.asList(parts[1].split(FIELD_SEPARATOR)));
        }
        return fields;
    }

}