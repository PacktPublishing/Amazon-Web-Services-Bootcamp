package com.chapter6;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.*;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.util.StringUtils;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chapter6 {
    private AmazonDynamoDB amazonDynamoDB;
    private DynamoDB dynamoDB;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;

    public Chapter6() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                //.withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.dynamoDB = new DynamoDB(amazonDynamoDB);

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException {
        Chapter6 object = new Chapter6();

        String tableName = "awsbootcamp";

        Long readCapacity = 1L;

        Long writeCapacity = 1L;

        String partitionKeyName = "id";

        String partitionKeyType = ScalarAttributeType.N.toString();

        String sortKeyName = "name";

        String sortKeyType = ScalarAttributeType.S.toString();

        /*object.createTable(tableName,
                readCapacity,
                writeCapacity,
                partitionKeyName,
                partitionKeyType,
                sortKeyName,
                sortKeyType,
                false,
                null);

        Thread.sleep(60000);*/

        //------------------ CREATE ITEM ------------------
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", new AttributeValue().withN("1"));
        item.put("name", new AttributeValue().withS("Sunil"));
        item.put("lastName", new AttributeValue().withS("Gulabani"));
        object.createItem(tableName, item);

        Item item2 = new Item()
                .withPrimaryKey("id", 2, "name", "Priya")
                .withString("lastName", "Gulabani")
                .withString("birthDate", "05/05/1988");
        object.createItem2(tableName, item2);

        //------------------ RETRIEVE ------------------
        Map<String, AttributeValue> retrieveKey = new HashMap<>();
        retrieveKey.put("id", new AttributeValue().withN("1"));
        retrieveKey.put("name", new AttributeValue().withS("Sunil"));
        object.retrieveItem(tableName, retrieveKey);

        object.retrieveItem2(
                tableName,
                "id", // partition key
                2, // partition key value
                "name", // sort key
                "Priya" // sort key value
        );

        //------------------ QUERY ------------------

        String keyConditionExpression
                = "#nameKey = :nameValue and #idKey = :idValue";

        String filterExpressionForQuery =
                "#lastNameKey = :lastNameValue";

        Map<String, String> expressionAttributesNames
                = new HashMap<>();

        expressionAttributesNames.put("#idKey", "id");

        expressionAttributesNames.put("#nameKey", "name");

        expressionAttributesNames.put("#lastNameKey", "lastName");

        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<>();

        expressionAttributeValues.put(":idValue",
                new AttributeValue().withN("1"));

        expressionAttributeValues.put(":nameValue",
                new AttributeValue().withS("Sunil"));

        expressionAttributeValues.put(":lastNameValue",
                new AttributeValue().withS("Gulabani"));

        object.queryTable(
                tableName,
                keyConditionExpression,
                filterExpressionForQuery,
                expressionAttributesNames,
                expressionAttributeValues);

        //---------------------

        String keyConditionExpression2
                = "#idKey = :idValue and #nameKey = :nameValue";

        String filterExpressionForQuery2 =
                "#lastNameKey = :lastNameValue";

        Map<String, String> nameMap = new HashMap<>();

        nameMap.put("#idKey", "id");

        nameMap.put("#nameKey", "name");

        nameMap.put("#lastNameKey", "lastName");

        Map<String, Object> valueMap = new ValueMap()
                .withNumber(":idValue", 2)
                .withString(":nameValue","Priya")
                .withString(":lastNameValue", "Gulabani");

        object.queryTable2(tableName,
                keyConditionExpression2,
                filterExpressionForQuery2,
                nameMap,
                valueMap);

        //------------------ SCAN ------------------

        String filterExpression = "lastName = :lastNameValue and id = :id";

        Map<String, AttributeValue> expressionAttributeValues2 =
                new HashMap<>();

        expressionAttributeValues2.put(":lastNameValue",
                new AttributeValue().withS("Gulabani"));

        expressionAttributeValues2.put(":id",
                new AttributeValue().withN("1"));

        object.scanTable(
                tableName,
                filterExpression,
                expressionAttributeValues2);

        //------------------

        ScanFilter scanFilter =
                new ScanFilter("lastName").eq("Gulabani");
        object.scanTable2(tableName, scanFilter);

        String filterExpression2 = "birthDate = :birthDate";

        ValueMap valueMap2 = new ValueMap()
                .withString(":birthDate","05/05/1988");

        object.scanTable2(tableName, filterExpression2, valueMap2);

        //------------------ UPDATE ------------------
        Map<String, AttributeValue> updateKey = new HashMap<>();
        updateKey.put("id", new AttributeValue().withN("1"));
        updateKey.put("name", new AttributeValue().withS("Sunil"));

        Map<String, AttributeValueUpdate> itemToUpdate = new HashMap<>();
        AttributeValueUpdate lastNameValueUpdate =
                new AttributeValueUpdate()
                        .withValue(new AttributeValue().withS("G."))
                        .withAction(AttributeAction.PUT);
        itemToUpdate.put("lastName", lastNameValueUpdate);

        AttributeValueUpdate birthDateValueUpdate =
                new AttributeValueUpdate()
                        .withValue(new AttributeValue().withS("26/08/1987"))
                        .withAction(AttributeAction.PUT);
        itemToUpdate.put("birthDate", birthDateValueUpdate);

        object.updateItem(tableName, updateKey, itemToUpdate);

        //------------------------------------------------------
        List<AttributeUpdate> attrUpdate = new ArrayList<>();
        attrUpdate.add(new AttributeUpdate("lastName").put("G."));
        attrUpdate.add(new AttributeUpdate("birthDate").put("05/05/1989"));

        object.updateItem2(
                tableName,
                "id",
                2,
                "name",
                "Priya",
                attrUpdate);

        //------------------ DELETE ------------------
        Map<String, AttributeValue> deleteKey = new HashMap<>();
        deleteKey.put("id", new AttributeValue().withN("1"));
        deleteKey.put("name", new AttributeValue().withS("Sunil"));
        object.deleteItem(tableName, deleteKey);

        object.deleteItem2(
                tableName,
                "id",
                2,
                "name",
                "Priya");
    }

    public void scanTable(
            String tableName,
            String filterExpression,
            Map<String, AttributeValue> expressionAttributeValues) {
        ScanRequest request = new ScanRequest()
                .withTableName(tableName)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = amazonDynamoDB.scan(request);

        System.out.println(gson.toJson(result));
    }

    public void scanTable2(
            String tableName,
            ScanFilter... scanFilters) {

        Table table = dynamoDB.getTable(tableName);

        ScanSpec spec = new ScanSpec()
                .withScanFilters(scanFilters);

        ItemCollection<ScanOutcome> itemCollection = table.scan(spec);

        for (Item item : itemCollection) {
            System.out.println(gson.toJson(item));
        }
    }

    public void scanTable2(
            String tableName,
            String filterExpression,
            ValueMap valueMap) {

        Table table = dynamoDB.getTable(tableName);

        ScanSpec spec = new ScanSpec()
                .withFilterExpression(filterExpression)
                .withValueMap(valueMap);

        ItemCollection<ScanOutcome> itemCollection = table.scan(spec);

        for (Item item : itemCollection) {
            System.out.println(gson.toJson(item));
        }
    }

    public void queryTable(
            String tableName,
            String keyConditionExpression,
            String filterExpression,
            Map<String, String> expressionAttributesNames,
            Map<String, AttributeValue> expressionAttributeValues
            ) {

        QueryRequest request = new QueryRequest()
                .withTableName(tableName)
                .withKeyConditionExpression(keyConditionExpression)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        QueryResult result = amazonDynamoDB.query(request);

        System.out.println(gson.toJson(result));
    }

    public void queryTable2(
            String tableName,
            String keyConditionExpression,
            String filterExpression,
            Map<String, String> nameMap,
            Map<String, Object> valueMap
    ) {
        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(keyConditionExpression)
                .withFilterExpression(filterExpression)
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> itemCollection =
                table.query(spec);

        for (Item item : itemCollection) {
            System.out.println(gson.toJson(item));
        }
    }

    public void createTable(
            String tableName,
            Long readCapacity, Long writeCapacity,
            String partitionKeyName, String partitionKeyType,
            String sortKeyName, String sortKeyType,
            boolean streamEnabled, StreamViewType streamViewType) {

        ProvisionedThroughput provisionedThroughput =
                createProvisionedThroughput(
                        readCapacity, writeCapacity);

        List<KeySchemaElement> keySchemaElements =
                new ArrayList<>();

        KeySchemaElement partitionKeySchemaElement =
                createKeySchemaElement(
                        partitionKeyName, KeyType.HASH);

        keySchemaElements.add(partitionKeySchemaElement);

        List<AttributeDefinition> attributeDefinitions =
                new ArrayList<>();

        AttributeDefinition partitionKeyAttrDef =
                createAttributeDefinition(
                        partitionKeyName, partitionKeyType);

        attributeDefinitions.add(partitionKeyAttrDef);

        if (!StringUtils.isNullOrEmpty(sortKeyName)) {
            KeySchemaElement sortKeySchemaElement =
                    createKeySchemaElement(
                            sortKeyName, KeyType.RANGE);

            keySchemaElements.add(sortKeySchemaElement);


            AttributeDefinition sortKeyAttrDef =
                    createAttributeDefinition(
                            sortKeyName, sortKeyType);

            attributeDefinitions.add(sortKeyAttrDef);
        }

        StreamSpecification streamSpecification =
                createStreamSpecification(
                        streamEnabled, streamViewType);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withProvisionedThroughput(provisionedThroughput)
                .withKeySchema(keySchemaElements)
                .withAttributeDefinitions(attributeDefinitions)
                .withStreamSpecification(streamSpecification);


        CreateTableResult result =
                amazonDynamoDB.createTable(request);

        System.out.println(gson.toJson(result));
    }

    private StreamSpecification createStreamSpecification(
            boolean streamEnabled,
            StreamViewType streamViewType) {
        StreamSpecification streamSpecification =
                new StreamSpecification()
                        .withStreamEnabled(streamEnabled);

        if (streamEnabled) {
            streamSpecification.withStreamViewType(streamViewType);
        }

        return streamSpecification;
    }

    private AttributeDefinition createAttributeDefinition(
            String attributeName, String attributeType) {
        return new AttributeDefinition()
                .withAttributeName(attributeName)
                .withAttributeType(attributeType);
    }

    private KeySchemaElement createKeySchemaElement(
            String attributeName,
            KeyType keyType) {
        return new KeySchemaElement()
                .withAttributeName(attributeName)
                .withKeyType(keyType);
    }

    private ProvisionedThroughput createProvisionedThroughput(
            Long readCapacity, Long writeCapacity) {
        ProvisionedThroughput provisionedThroughput
                = new ProvisionedThroughput();

        provisionedThroughput.withReadCapacityUnits(readCapacity);

        provisionedThroughput.withWriteCapacityUnits(writeCapacity);

        return provisionedThroughput;
    }

    public void createItem(
            String tableName,
            Map<String, AttributeValue> item) {
        PutItemRequest request = new PutItemRequest()
                .withTableName(tableName)
                .withItem(item);

        PutItemResult result = amazonDynamoDB.putItem(request);

        System.out.println(gson.toJson(result));
    }

    public void createItem2(String tableName, Item item) {
        Table table = dynamoDB.getTable(tableName);

        PutItemSpec spec = new PutItemSpec()
                .withItem(item);

        PutItemOutcome outcome = table.putItem(spec);

        System.out.println(gson.toJson(outcome));
    }

    public void retrieveItem(
            String tableName,
            Map<String, AttributeValue> key) {
        GetItemRequest request =
                new GetItemRequest()
                        .withTableName(tableName)
                        .withKey(key)
                        .withConsistentRead(true);

        GetItemResult result = amazonDynamoDB.getItem(request);

        System.out.println(gson.toJson(result));
    }

    public void retrieveItem2(
            String tableName,
            String hashKeyName,
            Integer hashKeyValue,
            String rangeKeyName,
            String rangeKeyValue) {
        Table table = dynamoDB.getTable(tableName);

        PrimaryKey primaryKey = new PrimaryKey(
                hashKeyName, hashKeyValue,
                rangeKeyName, rangeKeyValue);

        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(primaryKey)
                .withConsistentRead(true);

        Item item = table.getItem(spec);

        System.out.println(gson.toJson(item));
    }

    public void updateItem(
            String tableName,
            Map<String, AttributeValue> key,
            Map<String, AttributeValueUpdate> itemToUpdate) {

        UpdateItemRequest request =
                new UpdateItemRequest()
                        .withTableName(tableName)
                        .withKey(key)
                        .withAttributeUpdates(itemToUpdate);

        UpdateItemResult result = amazonDynamoDB.updateItem(request);

        System.out.println(result);
    }

    public void updateItem2(
            String tableName,
            String hashKeyName,
            Integer hashKeyValue,
            String rangeKeyName,
            String rangeKeyValue,
            List<AttributeUpdate> attrUpdate
    ) {
        Table table = dynamoDB.getTable(tableName);

        PrimaryKey primaryKey = new PrimaryKey(
                hashKeyName, hashKeyValue,
                rangeKeyName, rangeKeyValue);

        UpdateItemSpec spec =
                new UpdateItemSpec()
                        .withPrimaryKey(primaryKey)
                        .withAttributeUpdate(attrUpdate);

        UpdateItemOutcome outcome =
                table.updateItem(spec);

        System.out.println(gson.toJson(outcome));
    }

    public void deleteItem(
            String tableName,
            Map<String, AttributeValue> key) {
        DeleteItemRequest request =
                new DeleteItemRequest()
                        .withTableName(tableName)
                        .withKey(key);

        DeleteItemResult result = amazonDynamoDB.deleteItem(request);

        System.out.println(gson.toJson(result));
    }

    public void deleteItem2(
            String tableName,
            String hashKeyName,
            Integer hashKeyValue,
            String rangeKeyName,
            String rangeKeyValue) {
        Table table = dynamoDB.getTable(tableName);

        PrimaryKey primaryKey = new PrimaryKey(
                hashKeyName, hashKeyValue,
                rangeKeyName, rangeKeyValue);

        DeleteItemSpec spec =
                new DeleteItemSpec()
                        .withPrimaryKey(primaryKey);

        DeleteItemOutcome outcome =
                table.deleteItem(spec);

        System.out.println(gson.toJson(outcome));
    }
}
