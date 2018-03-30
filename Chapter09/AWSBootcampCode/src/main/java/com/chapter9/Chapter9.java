package com.chapter9;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.*;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

public class Chapter9 {

    private AmazonCloudWatch cloudWatch;
    private AWSLogs awsLogs;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;

    public Chapter9() {
        this.credentialsProvider = new CredentialsProvider();
        this.cloudWatch = AmazonCloudWatchClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.awsLogs = AWSLogsClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException {
        Chapter9 object = new Chapter9();

//        object.createMetricData();

//        object.createRDSAlarm();

//        object.createRDSDashboard();

        object.createLogGroup();
        object.createLogStream();
        object.putLogEvents();
    }

    private void createLogGroup() {
        String logGroupName = "aws-bootcamp";

        createLogGroup(logGroupName);
    }

    private void createLogGroup(String logGroupName) {
        CreateLogGroupRequest createLogGroupRequest =
                new CreateLogGroupRequest()
                .withLogGroupName(logGroupName);

        CreateLogGroupResult createLogGroupResult =
                awsLogs.createLogGroup(createLogGroupRequest);

        System.out.println(gson.toJson(createLogGroupResult));
    }

    private void createLogStream() {
        String logGroupName = "aws-bootcamp";

        String logStreamName = "chapter-9";

        createLogStream(logGroupName, logStreamName);
    }

    private void createLogStream(
            String logGroupName,
            String logStreamName) {
        CreateLogStreamRequest createLogStreamRequest =
                new CreateLogStreamRequest()
                        .withLogGroupName(logGroupName)
                        .withLogStreamName(logStreamName);

        CreateLogStreamResult createLogStreamResult =
                awsLogs.createLogStream(createLogStreamRequest);

        System.out.println(gson.toJson(createLogStreamResult));
    }

    private void putLogEvents() throws InterruptedException {
        String logGroupName = "aws-bootcamp";

        String logStreamName = "chapter-9";

        InputLogEvent inputLogEvent =
                new InputLogEvent()
                .withTimestamp(Instant.now().toEpochMilli())
                .withMessage("Hello World!!!");

        Thread.sleep(2000); /* This is just to demonstrate logs generated at different timestamp */

        InputLogEvent inputLogEvent2 =
                new InputLogEvent()
                .withTimestamp(Instant.now().toEpochMilli())
                .withMessage("Hello World - 2!!!");

        putLogEvents(
                logGroupName,
                logStreamName,
                inputLogEvent,
                inputLogEvent2);
    }

    private void putLogEvents(
            String logGroupName,
            String logStreamName,
            InputLogEvent... logEvents
            ) {
        PutLogEventsRequest putLogEventRequest =
                new PutLogEventsRequest()
                .withLogGroupName(logGroupName)
                .withLogStreamName(logStreamName)
                .withLogEvents(logEvents);

        PutLogEventsResult putLogEventsResult =
                awsLogs.putLogEvents(putLogEventRequest);

        System.out.println(gson.toJson(putLogEventsResult));
    }

    private void createMetricData() throws InterruptedException {
        String namespace = "Custom/AWSBootcampBook";

        String metricName = "NumberOfInvocations";

        Date timestamp = null;

        Dimension dimension =
                createDimension("ChapterNo", "9");
        Dimension dimension2 =
                createDimension("ChapterName", "All About CloudWatch");

        StandardUnit unit = StandardUnit.Count;

        double minLimit = 1D;
        double maxLimit = 1000D;
        Random random = new Random();
        double randomDataPointValue;

        for (double i = 0; i < 500; i++) {
            randomDataPointValue = minLimit + random.nextDouble()
                    * (maxLimit - minLimit);
            timestamp = new Date();
            putMetricData(
                    namespace,
                    metricName,
                    timestamp,
                    randomDataPointValue,
                    unit,
                    dimension,
                    dimension2
            );

            Thread.sleep(2000);
        }
    }

    private void putMetricData(
            String namespace,
            String metricName,
            Date timestamp,
            double value,
            StandardUnit unit,
            Dimension... dimensions) {

        MetricDatum metricDatum = new MetricDatum()
                .withMetricName(metricName)
                .withDimensions(dimensions)
                .withTimestamp(timestamp)
                .withUnit(unit)
                .withValue(value);

        PutMetricDataRequest putMetricDataRequest
                = new PutMetricDataRequest()
                .withMetricData(metricDatum)
                .withNamespace(namespace);

        PutMetricDataResult putMetricDataResult =
                cloudWatch.putMetricData(putMetricDataRequest);

        System.out.println(gson.toJson(putMetricDataResult));
    }

    private void createRDSDashboard() {
        String dashboardName = "AWS-Bootcamp-RDS-CPU-Dashboard";
        String dashboardBody = "{\"widgets\":[{\"type\":\"metric\",\"x\":0,\"y\":0,\"width\":12,\"height\":6,\"properties\":{\"metrics\":[[\"AWS/RDS\",\"CPUUtilization\",\"DBInstanceIdentifier\",\"awsbootcamp\"]],\"period\":300,\"stat\":\"Average\",\"region\":\"us-east-1\",\"title\":\"RDS Instance CPU\"}},{\"type\":\"metric\",\"x\":0,\"y\":7,\"width\":12,\"height\":6,\"properties\":{\"metrics\":[[\"AWS/RDS\",\"DatabaseConnections\",\"DBInstanceIdentifier\",\"awsbootcamp\"]],\"period\":300,\"stat\":\"Average\",\"region\":\"us-east-1\",\"title\":\"RDS Database Connections\"}}]}";

        createDashboard(dashboardName, dashboardBody);
    }

    private void createRDSAlarm() {
        String alarmName = "AWS-Bootcamp-RDS-High-CPU-Utilization";

        String alarmActionsARN = "arn:aws:sns:us-east-1:993735536778:aws-bootcamp";

        String dimensionName = "DBInstanceIdentifier";

        String dimensionValue = "awsbootcamp";

        String namespace = "AWS/RDS";

        String metricName = "CPUUtilization";

        Statistic statistic = Statistic.Average;

        ComparisonOperator comparisonOperator =
                ComparisonOperator.GreaterThanOrEqualToThreshold;

        Double threshold = 80.0;

        String unit = "Percent";

        Integer evaluationPeriod = 2;

        Integer period = 300;

        putMetricAlarm(alarmName,
                alarmActionsARN,
                dimensionName,
                dimensionValue,
                namespace,
                metricName,
                statistic,
                comparisonOperator,
                threshold,
                unit,
                evaluationPeriod,
                period);
    }

    public void createDashboard(String dashboardName,
                                String dashboardBody) {
        PutDashboardRequest putDashboardRequest =
                new PutDashboardRequest();

        putDashboardRequest
                .withDashboardName(dashboardName);

        putDashboardRequest.withDashboardBody(dashboardBody);

        PutDashboardResult putDashboardResult =
                cloudWatch.putDashboard(putDashboardRequest);

        System.out.println(gson.toJson(putDashboardResult));
    }

    public void putMetricAlarm(String alarmName,
                               String alarmActionsARN,
                               String dimensionName,
                               String dimensionValue,
                               String namespace,
                               String metricName,
                               Statistic statistic,
                               ComparisonOperator comparisonOperator,
                               Double threshold,
                               String unit,
                               Integer evaluationPeriod,
                               Integer period) {
        PutMetricAlarmRequest putMetricAlarmRequest =
                new PutMetricAlarmRequest();

        putMetricAlarmRequest.withAlarmName(alarmName);

        putMetricAlarmRequest.withActionsEnabled(true);

        putMetricAlarmRequest
            .withAlarmActions(alarmActionsARN);

        Dimension dimension =
                createDimension(dimensionName, dimensionValue);

        putMetricAlarmRequest.withDimensions(dimension);

        putMetricAlarmRequest.withNamespace(namespace);

        putMetricAlarmRequest.withMetricName(metricName);

        putMetricAlarmRequest.withStatistic(statistic);

        putMetricAlarmRequest.withComparisonOperator(comparisonOperator);

        putMetricAlarmRequest.withThreshold(threshold);

        putMetricAlarmRequest.withUnit(unit);

        putMetricAlarmRequest.withEvaluationPeriods(evaluationPeriod);

        putMetricAlarmRequest.withPeriod(period);

        PutMetricAlarmResult putMetricAlarmResult =
                cloudWatch.putMetricAlarm(putMetricAlarmRequest);

        System.out.println(gson.toJson(putMetricAlarmResult));
    }

    private Dimension createDimension(
            String dimensionName,
            String dimensionValue) {
        Dimension dimension = new Dimension();

        dimension.withName(dimensionName);

        dimension.withValue(dimensionValue);

        return dimension;
    }
}
