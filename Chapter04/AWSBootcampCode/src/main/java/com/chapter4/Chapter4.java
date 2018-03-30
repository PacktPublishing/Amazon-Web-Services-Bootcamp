package com.chapter4;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.iterable.S3Versions;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Chapter4 {
    private AmazonS3 amazonS3;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;
    private static final Integer SLEEP_TIME = 60000;

    public Chapter4() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Chapter4 object = new Chapter4();

        String bucketName = "aws-bootcamp-packt";
        /*object.createBucket(
                bucketName,
                Region.US_Standard);*/

/*        String emailId = "sunil.gulabani1@gmail.com";
        String awsCanonicalId = "";
        AccessControlList accessControlList =
                object.createAccessControlList(awsCanonicalId, emailId);
        object.createBucket(
                "aws-bootcamp-packt",
                Region.valueOf(object.credentialsProvider.getRegions().getName()),
                accessControlList,
                CannedAccessControlList.BucketOwnerFullControl
                );*/

//        object.listBuckets();

//        String folderName = "PACKT";
//        object.createFolder(bucketName, folderName);

        File file = new File("F:\\Github_Repo\\Books\\Packt\\AWS-Bootcamp\\AWSBootcampCode\\misc\\index.html");
        String keyName = "PACKT/index.html";
//        object.putObject(bucketName, keyName, file);

        String downloadedFileName = "packt-index.html";
        object.getObject(bucketName, keyName, downloadedFileName);
    }

    public void createBucket(
            String bucketName,
            Region region) {
        CreateBucketRequest createBucketRequest =
                new CreateBucketRequest(bucketName, region);

        Bucket bucket =
                amazonS3.createBucket(createBucketRequest);

        System.out.println(gson.toJson(bucket));
    }

    public void createBucket(
            String bucketName,
            Region region,
            AccessControlList accessControlList,
            CannedAccessControlList cannedAccessControlList) {
        CreateBucketRequest createBucketRequest =
                new CreateBucketRequest(bucketName, region);

        if (accessControlList != null) {
            createBucketRequest
                    .withAccessControlList(accessControlList);
        }

        if (cannedAccessControlList != null) {
            createBucketRequest
                    .withCannedAcl(cannedAccessControlList);
        }

        Bucket bucket =
                amazonS3.createBucket(createBucketRequest);

        System.out.println(gson.toJson(bucket));
    }

    public AccessControlList createAccessControlList(
            String awsMemberCanonicalId,
            String emailId) {
        AccessControlList accessControlList =
                new AccessControlList();

        accessControlList.grantPermission(
                GroupGrantee.AllUsers, Permission.Read);

        CanonicalGrantee canonicalGrantee =
                new CanonicalGrantee(awsMemberCanonicalId);

        accessControlList.grantPermission(
                canonicalGrantee, Permission.FullControl);

        EmailAddressGrantee emailAddressGrantee =
                new EmailAddressGrantee(emailId);

        accessControlList.grantPermission(
                emailAddressGrantee, Permission.Write);

        Owner owner = new Owner();

        owner.setDisplayName(emailId);

        owner.setId(awsMemberCanonicalId);

        accessControlList.setOwner(owner);

        return accessControlList;
    }

    public void listBuckets() {
        List<Bucket> listBucketsResponse =
                amazonS3.listBuckets();

        System.out.println(gson.toJson(listBucketsResponse));
    }

    public void setBucketLoggingConfiguration(
            String bucketName,
            String destinationBucketName,
            String logFilePrefix) {
        BucketLoggingConfiguration loggingConfiguration =
                new BucketLoggingConfiguration();

        loggingConfiguration.setDestinationBucketName(destinationBucketName);

        loggingConfiguration.setLogFilePrefix(logFilePrefix);

        SetBucketLoggingConfigurationRequest request =
                new SetBucketLoggingConfigurationRequest(
                        bucketName, loggingConfiguration);

        amazonS3.setBucketLoggingConfiguration(request);

    }

    public void getBucketLoggingConfiguration(String bucketName) {
        BucketLoggingConfiguration bucketLoggingConfiguration =
                amazonS3.getBucketLoggingConfiguration(bucketName);

        System.out.println(gson.toJson(bucketLoggingConfiguration));
    }

    public void setBucketWebsiteConfiguration(
            String bucketName,
            String indexDocument,
            String errorDocument) {
        BucketWebsiteConfiguration configuration =
                new BucketWebsiteConfiguration();

        configuration.setIndexDocumentSuffix(indexDocument);

        configuration.setErrorDocument(errorDocument);

        amazonS3.setBucketWebsiteConfiguration(bucketName, configuration);
    }

    public void setBucketWebsiteConfiguration(
            String bucketName,
            String hostName) {
        BucketWebsiteConfiguration configuration =
                new BucketWebsiteConfiguration();

        RedirectRule redirectRule = new RedirectRule();

        redirectRule.setHostName(hostName);

        redirectRule.setProtocol("http");

        configuration.setRedirectAllRequestsTo(redirectRule);

        amazonS3.setBucketWebsiteConfiguration(bucketName, configuration);
    }

    public void getBucketWebsiteConfiguration(String bucketName) {
        BucketWebsiteConfiguration bucketWebsiteConfiguration =
                amazonS3.getBucketWebsiteConfiguration(bucketName);

        System.out.println(gson.toJson(bucketWebsiteConfiguration));
    }

    public void setBucketNotificationConfiguration_SQS(
            String bucketName,
            S3Event s3Event,
            String queueARN,
            String prefixValue,
            String suffixValue) {
        BucketNotificationConfiguration
                bucketNotificationConfiguration =
                    getBucketNotificationConfiguration(bucketName);

        QueueConfiguration queueConfiguration = new QueueConfiguration();

        queueConfiguration.withQueueARN(queueARN);

        queueConfiguration.addEvent(s3Event);

        queueConfiguration.withFilter(
                createFilters(prefixValue, suffixValue));

        bucketNotificationConfiguration.addConfiguration(
                "SQSS3Notification", queueConfiguration);

        amazonS3.setBucketNotificationConfiguration(
                bucketName, bucketNotificationConfiguration);

    }

    public void setBucketNotificationConfiguration_SNS(
            String bucketName,
            S3Event s3Event,
            String topicARN,
            String prefixValue,
            String suffixValue) {
        BucketNotificationConfiguration bucketNotificationConfiguration =
                getBucketNotificationConfiguration(bucketName);

        TopicConfiguration topicConfiguration = new TopicConfiguration();

        topicConfiguration.addEvent(s3Event);

        topicConfiguration.withTopicARN(topicARN);

        topicConfiguration.withFilter(
                createFilters(prefixValue, suffixValue));

        bucketNotificationConfiguration.addConfiguration(
                "SNSS3Notification", topicConfiguration);

        amazonS3.setBucketNotificationConfiguration(
                bucketName, bucketNotificationConfiguration);
    }

    public void setBucketNotificationConfiguration_Lambda(
            String bucketName,
            S3Event s3Event,
            String lambdaFunctionARN,
            String prefixValue,
            String suffixValue) {
        BucketNotificationConfiguration bucketNotificationConfiguration =
                getBucketNotificationConfiguration(bucketName);

        LambdaConfiguration lambdaConfiguration =
                new LambdaConfiguration(lambdaFunctionARN);

        lambdaConfiguration.addEvent(s3Event);

        lambdaConfiguration.withFilter(
                createFilters(prefixValue, suffixValue));

        bucketNotificationConfiguration.addConfiguration(
                "LambdaFunctionS3Notification", lambdaConfiguration);

        amazonS3.setBucketNotificationConfiguration(
                bucketName, bucketNotificationConfiguration);
    }

    private Filter createFilters(String prefixValue, String suffixValue) {
        Filter filter = null;

        if(!StringUtils.isNullOrEmpty(prefixValue) &&
                !StringUtils.isNullOrEmpty(suffixValue)) {

            List<FilterRule> filterRules = new ArrayList<>();

            if(!StringUtils.isNullOrEmpty(prefixValue)) {

                FilterRule prefixFilterRule = new FilterRule();

                prefixFilterRule.setName("prefix");

                prefixFilterRule.setValue(prefixValue);

                filterRules.add(prefixFilterRule);
            }

            if(!StringUtils.isNullOrEmpty(suffixValue)) {

                FilterRule suffixFilterRule = new FilterRule();

                suffixFilterRule.setName("suffix");

                suffixFilterRule.setValue(suffixValue);

                filterRules.add(suffixFilterRule);
            }

            S3KeyFilter s3KeyFilter = new S3KeyFilter();

            s3KeyFilter.setFilterRules(filterRules);

            filter = new Filter();

            filter.setS3KeyFilter(s3KeyFilter);
        }

        return filter;
    }

    public BucketNotificationConfiguration getBucketNotificationConfiguration(
            String bucketName) {
        BucketNotificationConfiguration bucketNotificationConfiguration =
                amazonS3.getBucketNotificationConfiguration(bucketName);

        System.out.println(gson.toJson(bucketNotificationConfiguration));

        return bucketNotificationConfiguration;
    }

    public void setBucketVersioningConfiguration(
            String bucketName,
            String status) {
        BucketVersioningConfiguration configuration =
                new BucketVersioningConfiguration();

        configuration.setStatus(status);

        SetBucketVersioningConfigurationRequest
                setBucketVersioningConfigurationRequest =
                    new SetBucketVersioningConfigurationRequest(
                        bucketName, configuration);

        amazonS3.setBucketVersioningConfiguration(
                setBucketVersioningConfigurationRequest);
    }

    public void getBucketVersioningConfiguration(String bucketName) {
        BucketVersioningConfiguration bucketVersioningConfiguration =
                amazonS3.getBucketVersioningConfiguration(bucketName);

        System.out.println(gson.toJson(bucketVersioningConfiguration));
    }

    public void setBucketLifecycleConfiguration(String bucketName) {
        List<BucketLifecycleConfiguration.Rule> rules = new ArrayList<>();

        rules.add(expirationRuleWithDate());

        rules.add(expirationRuleWithDays());

        rules.add(moveOldObjectsToGlacierRule());

        rules.add(nonCurrentVersionTransitionsAndExpirationRule());

        rules.add(abortIncompleteMultipartUploadRule());

        BucketLifecycleConfiguration bucketLifecycleConfiguration =
                new BucketLifecycleConfiguration();

        bucketLifecycleConfiguration.setRules(rules);

        SetBucketLifecycleConfigurationRequest
                setBucketLifecycleConfigurationRequest =
                    new SetBucketLifecycleConfigurationRequest(
                            bucketName, bucketLifecycleConfiguration);

        amazonS3.setBucketLifecycleConfiguration(
                setBucketLifecycleConfigurationRequest);

    }

    public void getBucketLifecycleConfiguration(String bucketName) {
        BucketLifecycleConfiguration bucketLifecycleConfiguration =
                amazonS3.getBucketLifecycleConfiguration(bucketName);

        System.out.println(gson.toJson(bucketLifecycleConfiguration));
    }

    public void deleteBucketLifecycleConfiguration(String bucketName) {
        amazonS3.deleteBucketLifecycleConfiguration(bucketName);
    }

    private BucketLifecycleConfiguration.Rule expirationRuleWithDate() {
        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule();

        rule.setId("Expiration Action with Date");

        rule.setPrefix("chapter1");

        rule.setStatus(BucketLifecycleConfiguration.ENABLED);

        rule.setExpirationDate(getDate());

        return rule;
    }

    private BucketLifecycleConfiguration.Rule expirationRuleWithDays() {
        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule();

        rule.setId("Expiration Action with Days");

        rule.setPrefix("chapter2");

        rule.setStatus(BucketLifecycleConfiguration.ENABLED);

        rule.setExpirationInDays(30);

        return rule;
    }

    private BucketLifecycleConfiguration.Rule moveOldObjectsToGlacierRule() {
        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule();

        rule.setId("Move old objects to Glacier Action");

        rule.setPrefix("chapter3");

        rule.setStatus(BucketLifecycleConfiguration.ENABLED);

        List<BucketLifecycleConfiguration.Transition> transitionList =
                new ArrayList<>();

        BucketLifecycleConfiguration.Transition transition =
                new BucketLifecycleConfiguration.Transition();

        transition.setDate(getDate());

        transition.setStorageClass(StorageClass.Glacier);

        transitionList.add(transition);

        rule.setTransitions(transitionList);

        return rule;
    }

    private Date getDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        gmt.set(Calendar.HOUR_OF_DAY, 0);

        gmt.set(Calendar.MINUTE, 0);

        gmt.set(Calendar.SECOND, 0);

        gmt.set(Calendar.MILLISECOND, 0);

        long millis = gmt.getTimeInMillis();

        return new Date(millis);
    }

    private BucketLifecycleConfiguration.Rule
        nonCurrentVersionTransitionsAndExpirationRule() {

        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule();

        rule.setId("Non-Current Versioned Object Actions");

        rule.setPrefix("chapter4");

        rule.setStatus(BucketLifecycleConfiguration.ENABLED);

        BucketLifecycleConfiguration.NoncurrentVersionTransition
                nonCurrentVersionTransition =
                    new BucketLifecycleConfiguration.NoncurrentVersionTransition();

        nonCurrentVersionTransition.setDays(30);

        nonCurrentVersionTransition.setStorageClass(
                StorageClass.StandardInfrequentAccess);

        List<BucketLifecycleConfiguration.NoncurrentVersionTransition>
                nonCurrentVersionTransitionList = new ArrayList<>();

        nonCurrentVersionTransitionList.add(nonCurrentVersionTransition);

        rule.setNoncurrentVersionTransitions(nonCurrentVersionTransitionList);

        rule.setNoncurrentVersionExpirationInDays(40);

        return rule;
    }

    private BucketLifecycleConfiguration.Rule
        abortIncompleteMultipartUploadRule() {

        BucketLifecycleConfiguration.Rule rule =
                new BucketLifecycleConfiguration.Rule();

        rule.setId("Abort Incomplete Multipart Action");

        rule.setPrefix("chapter5");

        rule.setStatus(BucketLifecycleConfiguration.ENABLED);

        AbortIncompleteMultipartUpload abortIncompleteMultipartUpload =
                new AbortIncompleteMultipartUpload();

        abortIncompleteMultipartUpload.setDaysAfterInitiation(1);

        rule.setAbortIncompleteMultipartUpload(abortIncompleteMultipartUpload);

        return rule;
    }

    public void setBucketReplicationConfiguration(
            String bucketName,
            String roleARN,
            String destinationBucketARN) {
        ReplicationDestinationConfig destination =
                new ReplicationDestinationConfig();

        destination.setBucketARN(destinationBucketARN);

        destination.setStorageClass(StorageClass.Standard);

        ReplicationRule replicationRule = new ReplicationRule();

        replicationRule.setPrefix("");

        replicationRule.setStatus(ReplicationRuleStatus.Enabled);

        replicationRule.setDestinationConfig(destination);

        Map<String, ReplicationRule> rules = new HashMap<>();

        rules.put("S3 bucket replication", replicationRule);

        BucketReplicationConfiguration configuration =
                new BucketReplicationConfiguration();

        configuration.setRoleARN(roleARN);

        configuration.setRules(rules);

        amazonS3.setBucketReplicationConfiguration(bucketName, configuration);

    }

    public void getBucketReplicationConfiguration(String bucketName) {
        BucketReplicationConfiguration bucketReplicationConfiguration =
                amazonS3.getBucketReplicationConfiguration(bucketName);

        System.out.println(gson.toJson(bucketReplicationConfiguration));
    }

    public void deleteBucketReplicationConfiguration(String bucketName) {
        amazonS3.deleteBucketReplicationConfiguration(bucketName);
    }

    public void setBucketTaggingConfiguration(String bucketName) {
        BucketTaggingConfiguration configuration =
                new BucketTaggingConfiguration();

        TagSet tagSet1 = new TagSet();

        tagSet1.setTag("Chapter", "5");

        tagSet1.setTag("Name", "AWSS3");

        List<TagSet> tagSets = new ArrayList<>();

        tagSets.add(tagSet1);

        configuration.setTagSets(tagSets);

        amazonS3.setBucketTaggingConfiguration(bucketName, configuration);

    }

    public void getBucketTaggingConfiguration(String bucketName) {
        BucketTaggingConfiguration bucketTaggingConfiguration =
                amazonS3.getBucketTaggingConfiguration(bucketName);

        System.out.println(gson.toJson(bucketTaggingConfiguration));
    }

    public void enableRequesterPays(String bucketName) {
        amazonS3.enableRequesterPays(bucketName);
    }

    public void isRequesterPaysEnabled(String bucketName) {
        boolean isRequesterPaysEnabled =
                amazonS3.isRequesterPaysEnabled(bucketName);

        System.out.println(isRequesterPaysEnabled);
    }

    public void disableRequesterPays(String bucketName) {
        amazonS3.disableRequesterPays(bucketName);
    }

    public void setBucketAccelerateConfiguration(
            String bucketName,
            BucketAccelerateStatus bucketAccelerateStatus) {
        BucketAccelerateConfiguration bucketAccelerateConfiguration =
                new BucketAccelerateConfiguration(bucketAccelerateStatus);

        amazonS3.setBucketAccelerateConfiguration(
                bucketName, bucketAccelerateConfiguration);

    }

    public void getBucketAccelerateConfiguration(String bucketName) {
        BucketAccelerateConfiguration bucketAccelerateConfiguration =
                amazonS3.getBucketAccelerateConfiguration(bucketName);

        System.out.println(gson.toJson(bucketAccelerateConfiguration));
    }

    public void deleteObjects(String bucketName) {
        S3Objects s3ObjectSummaries =
                S3Objects.inBucket(amazonS3, bucketName);

        s3ObjectSummaries.forEach(objectSummary -> {

            deleteObject(bucketName, objectSummary.getKey());

        });
    }

    public void deleteBucket(String bucketName) {
        S3Versions s3VersionSummaries =
                S3Versions.inBucket(amazonS3, bucketName);

        for (S3VersionSummary version : s3VersionSummaries) {

            String key = version.getKey();

            String versionId = version.getVersionId();

            amazonS3.deleteVersion(bucketName, key, versionId);
        }

        //		amazonS3.deleteBucket(bucketName);
        DeleteBucketRequest deleteBucketRequest =
                new DeleteBucketRequest(bucketName);

        amazonS3.deleteBucket(deleteBucketRequest);

    }

    public void createFolder(
            String bucketName,
            String folderName) {
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentLength(0);

        InputStream emptyContent =
                new ByteArrayInputStream(new byte[0]);

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(
                        bucketName, folderName + "/",
                        emptyContent, metadata);

        PutObjectResult putObjectResult =
                amazonS3.putObject(putObjectRequest);

        System.out.println(gson.toJson(putObjectResult));
    }

    public void deleteFolder(String bucketName, String folderName) {
        DeleteObjectRequest deleteObjectRequest =
                new DeleteObjectRequest(bucketName, folderName + "/");

        amazonS3.deleteObject(deleteObjectRequest);
    }

    public void listObjects(String bucketName) {
        ObjectListing response = amazonS3.listObjects(bucketName);

        System.out.println(gson.toJson(response));
    }

    public void putObject(
            String bucketName,
            String keyName,
            File file) throws IOException {

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, keyName, file);

        PutObjectResult putObjectResult =
                amazonS3.putObject(putObjectRequest);

        System.out.println(gson.toJson(putObjectResult));
    }

    public void deleteObject(
            String bucketName,
            String keyName) {

        DeleteObjectRequest deleteObjectRequest =
                new DeleteObjectRequest(bucketName, keyName);

        amazonS3.deleteObject(deleteObjectRequest);
    }

    public void getObject(
            String bucketName,
            String keyName,
            String downloadedFileName) throws IOException {

        S3Object s3Object = amazonS3.getObject(
                new GetObjectRequest(bucketName, keyName));

        InputStream inputStream = s3Object.getObjectContent();

        String objectData = IOUtils.toString(inputStream);

        System.out.println(objectData);

        Path path = Paths.get(downloadedFileName);

        Files.write(path, objectData.getBytes());

        inputStream.close();
    }

}
