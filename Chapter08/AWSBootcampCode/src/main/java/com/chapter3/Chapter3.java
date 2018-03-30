package com.chapter3;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.*;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chapter3 {

    private AmazonEC2 amazonEC2;
    private AmazonElasticLoadBalancing elbClient;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;
    private static final Integer SLEEP_TIME = 60000;

    public Chapter3() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonEC2 = AmazonEC2ClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.elbClient = AmazonElasticLoadBalancingClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException {
        Chapter3 object = new Chapter3();

        /*String groupName = "aws-bootcamp";

        String groupDescription = "Security Group for AWS Bootcamp";

        String groupId = object.createSecurityGroup(groupName, groupDescription);

        object.describeSecurityGroups(null, null);

        int sshPort = 22;

        String cidr = "0.0.0.0/0";

        object.authorizeSecurityGroupIngress(groupName, sshPort, cidr);

        object.describeSecurityGroups(groupId, null);

        object.revokeSecurityGroupIngress(groupName, sshPort, cidr);

        object.describeSecurityGroups(groupId, null);

        //--------------------------------------------------------------------------------
        String keyPairName = "aws-bootcamp";

        String keyPairPrivateMaterial = object.createKeyPair(keyPairName);

        object.describeKeyPairs(keyPairName);

        //--------------------------------------------------------------------------------

        String imageId = "ami-c998b6b2";

        object.describeImages(imageId);

        int minInstanceCount = 1;

        int maxInstanceCount = 1;

        String instanceId = object.runInstances(
                imageId,
                InstanceType.T2Micro,
                minInstanceCount,
                maxInstanceCount,
                groupName,
                keyPairName);

        Thread.sleep(SLEEP_TIME);

        object.describeInstances(instanceId);*/

        String groupId = "sg-7c6ecf0f";
        String instanceId = "i-00021afadb836cb96";


        String availabilityZone = "us-east-1d";
        String loadBalancerName = "AWS-Bootcamp";

        String dnsName = object.createLoadBalancer(
                loadBalancerName,
                Arrays.asList(groupId),
                Arrays.asList(availabilityZone));

        Thread.sleep(SLEEP_TIME);

        String loadBalancerProtocol = "HTTP";

        String instanceProtocol = "HTTP";

        Integer instancePort = 80;

        Integer loadBalancerPort = 80;

        object.createLoadBalancerListeners(
                loadBalancerName,
                loadBalancerProtocol,
                instanceProtocol,
                loadBalancerPort,
                instancePort);

        Thread.sleep(SLEEP_TIME);

        int healthyThreshold = 2;

        int unhealthyThreshold = 2;

        int interval = 30;

        String target = "TCP:80";

        int timeout = 5;

        object.configureHealthCheck(
                loadBalancerName,
                healthyThreshold,
                unhealthyThreshold,
                interval,
                target,
                timeout);

        Thread.sleep(SLEEP_TIME);

        object.registerInstancesWithLoadBalancer(
                loadBalancerName,
                Arrays.asList(instanceId));

        Thread.sleep(SLEEP_TIME * 3);

        object.describeLoadBalancers(loadBalancerName);
        object.describeInstanceHealth(loadBalancerName, Arrays.asList(instanceId));

//        object.deleteLoadBalancer(loadBalancerName);

        Thread.sleep(SLEEP_TIME);

        //--------------------------------------------------------------------------------

        /*object.stopInstances(instanceId);

        Thread.sleep(SLEEP_TIME * 2);

        object.startInstances(instanceId);

        Thread.sleep(SLEEP_TIME * 2);

        object.terminateInstances(instanceId);

        Thread.sleep(SLEEP_TIME * 2);

        object.describeInstances(instanceId);

        //--------------------------------------------------------------------------------
        object.deleteKeyPair(keyPairName);

        object.deleteSecurityGroupBasedOnGroupId(groupId);
//        object.deleteSecurityGroupBasedOnGroupName(groupName);*/

    }

    public String createLoadBalancer(String loadBalancerName,
                                     List<String> securityGroups,
                                     List<String> availabilityZones) {
        System.out.println("########################### Create Load Balancer ###########################");

        CreateLoadBalancerRequest createLoadBalancerRequest =
                new CreateLoadBalancerRequest();

        createLoadBalancerRequest.withLoadBalancerName(loadBalancerName);

        String protocol = "HTTP";
        int loadBalancerPort = 80;
        int instancePort = 80;
        Listener http = new Listener(protocol, loadBalancerPort, instancePort);

        createLoadBalancerRequest.withListeners(http);

        createLoadBalancerRequest.withSecurityGroups(securityGroups);

        createLoadBalancerRequest.withAvailabilityZones(availabilityZones);

        CreateLoadBalancerResult createLoadBalancerResult =
                elbClient.createLoadBalancer(createLoadBalancerRequest);

        System.out.println(gson.toJson(createLoadBalancerResult));

        return createLoadBalancerResult.getDNSName();
    }

    public void applySecurityGroupsToLoadBalancer(String loadBalancerName, List<String> securityGroups) {
        System.out.println("########################### Apply Security Groups to Load Balancer ###########################");

        ApplySecurityGroupsToLoadBalancerRequest applySecurityGroupsToLoadBalancerRequest =
                new ApplySecurityGroupsToLoadBalancerRequest();

        applySecurityGroupsToLoadBalancerRequest.withLoadBalancerName(loadBalancerName);

        applySecurityGroupsToLoadBalancerRequest.withSecurityGroups(securityGroups);

        ApplySecurityGroupsToLoadBalancerResult applySecurityGroupsToLoadBalancerResult =
                elbClient.applySecurityGroupsToLoadBalancer(
                        applySecurityGroupsToLoadBalancerRequest);

        System.out.println(gson.toJson(applySecurityGroupsToLoadBalancerResult));
    }

    public void createLoadBalancerListeners(
            String loadBalancerName,
            String loadBalancerProtocol,
            String instanceProtocol,
            Integer instancePort,
            Integer loadBalancerPort) {
        System.out.println("########################### Create Load Balancer Listeners ###########################");

        CreateLoadBalancerListenersRequest
                createLoadBalancerListenersRequest =
                new CreateLoadBalancerListenersRequest();

        createLoadBalancerListenersRequest
                .setLoadBalancerName(loadBalancerName);

        Listener listener = createListener(
                loadBalancerProtocol,
                instanceProtocol,
                instancePort,
                loadBalancerPort);

        createLoadBalancerListenersRequest
                .withListeners(Arrays.asList(listener));

        CreateLoadBalancerListenersResult
                createLoadBalancerListenersResult = elbClient
                        .createLoadBalancerListeners(
                                createLoadBalancerListenersRequest);

        System.out.println(gson.toJson(createLoadBalancerListenersResult));
    }

    private Listener createListener(
            String loadBalancerProtocol,
            String instanceProtocol,
            Integer instancePort,
            Integer loadBalancerPort) {
        Listener listener = new Listener();

        listener.withInstanceProtocol(instanceProtocol);

        listener.withProtocol(loadBalancerProtocol);

        listener.withInstancePort(instancePort);

        listener.withLoadBalancerPort(loadBalancerPort);

        return listener;
    }

    public void deleteLoadBalancer(String loadBalancerName) {
        System.out.println("########################### Delete Load Balancer ###########################");

        DeleteLoadBalancerRequest deleteLoadBalancerRequest =
                new DeleteLoadBalancerRequest();

        deleteLoadBalancerRequest.withLoadBalancerName(loadBalancerName);

        DeleteLoadBalancerResult deleteLoadBalancerResult =
                elbClient.deleteLoadBalancer(deleteLoadBalancerRequest);

        System.out.println(gson.toJson(deleteLoadBalancerResult));
    }

    public void describeLoadBalancers(String loadBalancerName) {
        System.out.println("########################### Describe Load Balancers ###########################");

        DescribeLoadBalancersRequest describeLoadBalancersRequest =
                new DescribeLoadBalancersRequest();

        if(!StringUtils.isNullOrEmpty(loadBalancerName)) {
            describeLoadBalancersRequest.withLoadBalancerNames(Arrays.asList(loadBalancerName));
        }

        DescribeLoadBalancersResult describeLoadBalancersResult =
                elbClient.describeLoadBalancers(describeLoadBalancersRequest );

        System.out.println(gson.toJson(describeLoadBalancersResult));
    }

    public void configureHealthCheck(String loadBalancerName,
                                     int healthyThreshold,
                                     int unhealthyThreshold,
                                     int interval,
                                     String target,
                                     int timeout) {
        System.out.println("########################### Configure Health Check ###########################");

        ConfigureHealthCheckRequest configureHealthCheckRequest =
                new ConfigureHealthCheckRequest();

        configureHealthCheckRequest.withHealthCheck(
                createHealthCheck(
                        healthyThreshold,
                        unhealthyThreshold,
                        interval,
                        target,
                        timeout));

        configureHealthCheckRequest
                .withLoadBalancerName(loadBalancerName);

        ConfigureHealthCheckResult configureHealthCheckResult = elbClient
                .configureHealthCheck(configureHealthCheckRequest);

        System.out.println(gson.toJson(configureHealthCheckResult));
    }

    private HealthCheck createHealthCheck(
            int healthyThreshold,
            int unhealthyThreshold,
            int interval,
            String target,
            int timeout) {
        HealthCheck healthCheck = new HealthCheck();

        healthCheck.withHealthyThreshold(healthyThreshold);

        healthCheck.withInterval(interval);

        healthCheck.withTarget(target);

        healthCheck.withTimeout(timeout);

        healthCheck.withUnhealthyThreshold(unhealthyThreshold);

        return healthCheck;
    }

    public void describeInstanceHealth(String loadBalancerName, List<String> instanceIds) {
        System.out.println("########################### Describe Instance Health ###########################");

        DescribeInstanceHealthRequest describeInstanceHealthRequest =
                new DescribeInstanceHealthRequest();

        describeInstanceHealthRequest.withLoadBalancerName(loadBalancerName);

        describeInstanceHealthRequest.withInstances(getELBInstanceList(instanceIds));

        DescribeInstanceHealthResult describeInstanceHealthResult =
                elbClient.describeInstanceHealth(describeInstanceHealthRequest);

        System.out.println(gson.toJson(describeInstanceHealthResult));
    }

    public void deleteLoadBalancerListeners(String loadBalancerName, Integer loadBalancePort) {
        System.out.println("########################### Delete Load Balancer Listeners ###########################");

        DeleteLoadBalancerListenersRequest deleteLoadBalancerListenersRequest =
                new DeleteLoadBalancerListenersRequest();

        deleteLoadBalancerListenersRequest.withLoadBalancerName(loadBalancerName);

        deleteLoadBalancerListenersRequest.withLoadBalancerPorts(Arrays.asList(loadBalancePort));

        DeleteLoadBalancerListenersResult deleteLoadBalancerListenersResult =
                elbClient.deleteLoadBalancerListeners(deleteLoadBalancerListenersRequest);

        System.out.println(gson.toJson(deleteLoadBalancerListenersResult));
    }

    public void registerInstancesWithLoadBalancer(
            String loadBalancerName,
            List<String> instanceIds) {
        System.out.println("########################### Register Instance with ELB ###########################");

        RegisterInstancesWithLoadBalancerRequest
                registerInstancesWithLoadBalancerRequest =
                new RegisterInstancesWithLoadBalancerRequest();

        registerInstancesWithLoadBalancerRequest
                .withLoadBalancerName(loadBalancerName);

        registerInstancesWithLoadBalancerRequest
                .withInstances(getELBInstanceList(instanceIds));

        RegisterInstancesWithLoadBalancerResult
                registerInstancesWithLoadBalancerResult =
                elbClient.registerInstancesWithLoadBalancer(
                        registerInstancesWithLoadBalancerRequest);

        System.out.println(gson.toJson(registerInstancesWithLoadBalancerResult));
    }

    public void deregisterInstancesFromLoadBalancer(String loadBalancerName, List<String> instanceIds) {
        System.out.println("########################### Deregister Instance from ELB ###########################");

        DeregisterInstancesFromLoadBalancerRequest deregisterInstancesFromLoadBalancerRequest =
                new DeregisterInstancesFromLoadBalancerRequest();

        deregisterInstancesFromLoadBalancerRequest.withLoadBalancerName(loadBalancerName);

        deregisterInstancesFromLoadBalancerRequest.withInstances(getELBInstanceList(instanceIds));

        DeregisterInstancesFromLoadBalancerResult deregisterInstancesFromLoadBalancerResult =
                elbClient.deregisterInstancesFromLoadBalancer(deregisterInstancesFromLoadBalancerRequest);

        System.out.println(gson.toJson(deregisterInstancesFromLoadBalancerResult));
    }

    public void addTags(String loadBalancerName, List<com.amazonaws.services.elasticloadbalancing.model.Tag> tags) {
        System.out.println("########################### Add ELB Tags ###########################");

        AddTagsRequest addTagsRequest = new AddTagsRequest();

        addTagsRequest.withLoadBalancerNames(Arrays.asList(loadBalancerName));

        addTagsRequest.withTags(tags);

        AddTagsResult addTagsResult = elbClient.addTags(addTagsRequest );

        System.out.println(gson.toJson(addTagsResult));
    }

    public void describeTags(String loadBalancerName) {
        System.out.println("########################### Describe ELB Tags ###########################");

        com.amazonaws.services.elasticloadbalancing.model.DescribeTagsRequest describeTagsRequest =
                new com.amazonaws.services.elasticloadbalancing.model.DescribeTagsRequest();

        describeTagsRequest.withLoadBalancerNames(Arrays.asList(loadBalancerName));

        com.amazonaws.services.elasticloadbalancing.model.DescribeTagsResult describeTagsResult =
                elbClient.describeTags(describeTagsRequest);

        System.out.println(gson.toJson(describeTagsResult));
    }

    public void removeTags(String loadBalancerName, String key) {
        System.out.println("########################### Remove ELB Tags ###########################");

        TagKeyOnly tagKeyOnly = new TagKeyOnly();

        tagKeyOnly.withKey(key);

        RemoveTagsRequest removeTagsRequest = new RemoveTagsRequest();

        removeTagsRequest.withLoadBalancerNames(Arrays.asList(loadBalancerName));

        removeTagsRequest.withTags(Arrays.asList(tagKeyOnly));

        RemoveTagsResult removeTagsResult = elbClient.removeTags(removeTagsRequest);

        System.out.println(gson.toJson(removeTagsResult));
    }

    private List<com.amazonaws.services.elasticloadbalancing.model.Instance>
        getELBInstanceList(List<String> instanceIds) {

        List<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceList =
                new ArrayList<>();

        if(instanceIds!=null && !instanceIds.isEmpty()) {

            for(String instanceId: instanceIds){

                instanceList.add(
                        new com.amazonaws.services.elasticloadbalancing.model.Instance()
                                .withInstanceId(instanceId));

            }
        }

        return instanceList;
    }

    public String createSecurityGroup(String groupName, String groupDescription) {
        System.out.println("########################### Create Security Group ###########################");

        CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();

        createSecurityGroupRequest.withGroupName(groupName);

        createSecurityGroupRequest.withDescription(groupDescription);

        CreateSecurityGroupResult createSecurityGroupResult = amazonEC2.createSecurityGroup(createSecurityGroupRequest);

        System.out.println(gson.toJson(createSecurityGroupResult));

        return createSecurityGroupResult.getGroupId();
    }

    public void authorizeSecurityGroupIngress(String groupName, int port, String cidr) {
        System.out.println("########################### Authorize Security Group Ingress ###########################");

        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();

        authorizeSecurityGroupIngressRequest.withGroupName(groupName);

        /*IpPermission ipPermission = createIpPermission(port, cidr);

        authorizeSecurityGroupIngressRequest.withIpPermissions(ipPermission);*/

        authorizeSecurityGroupIngressRequest.withIpProtocol("tcp");

        authorizeSecurityGroupIngressRequest.withCidrIp(cidr);

        authorizeSecurityGroupIngressRequest.withToPort(port);

        authorizeSecurityGroupIngressRequest.withFromPort(port);

        AuthorizeSecurityGroupIngressResult authorizeSecurityGroupIngressResult = amazonEC2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

        System.out.println(gson.toJson(authorizeSecurityGroupIngressResult));
    }

    private IpPermission createIpPermission(int port, String cidr) {
        IpPermission ipPermission = new IpPermission();

        ipPermission.withIpProtocol("tcp");

        ipPermission.withFromPort(port);

        ipPermission.withToPort(port);

        ipPermission.withIpv4Ranges(new IpRange().withCidrIp(cidr));

        return ipPermission;
    }

    public void revokeSecurityGroupIngress(String groupName, int port, String cidr) {
        System.out.println("########################### Revoke Security Group Ingress ###########################");

        RevokeSecurityGroupIngressRequest revokeSecurityGroupIngressRequest = new RevokeSecurityGroupIngressRequest();

        revokeSecurityGroupIngressRequest.withGroupName(groupName);

        revokeSecurityGroupIngressRequest.withIpProtocol("tcp");

        revokeSecurityGroupIngressRequest.withCidrIp(cidr);

        revokeSecurityGroupIngressRequest.withToPort(port);

        revokeSecurityGroupIngressRequest.withFromPort(port);

        RevokeSecurityGroupIngressResult revokeSecurityGroupIngressResult = amazonEC2.revokeSecurityGroupIngress(revokeSecurityGroupIngressRequest);

        System.out.println(gson.toJson(revokeSecurityGroupIngressResult));

    }

    public void describeSecurityGroups(String securityGroupId, String securityGroupName) {
        System.out.println("########################### Describe Security Groups ###########################");

        DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();

        if(!StringUtils.isNullOrEmpty(securityGroupId)) {
            describeSecurityGroupsRequest.withGroupIds(securityGroupId);
        }

        if(!StringUtils.isNullOrEmpty(securityGroupName)) {
            describeSecurityGroupsRequest.withGroupNames(securityGroupName);
        }

//        describeSecurityGroupsRequest.setFilters();

        DescribeSecurityGroupsResult describeSecurityGroupsResult = amazonEC2.describeSecurityGroups(describeSecurityGroupsRequest);
//        DescribeSecurityGroupsResult describeSecurityGroupsResult = amazonEC2.describeSecurityGroups();

        System.out.println(gson.toJson(describeSecurityGroupsResult));
    }

    public void deleteSecurityGroupBasedOnGroupName(String groupName) {
        System.out.println("########################### Delete Security Group Based on Group Name ###########################");

        DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();

        deleteSecurityGroupRequest.withGroupName(groupName);

        DeleteSecurityGroupResult deleteSecurityGroupResult = amazonEC2.deleteSecurityGroup(deleteSecurityGroupRequest);

        System.out.println(gson.toJson(deleteSecurityGroupResult));
    }

    public void deleteSecurityGroupBasedOnGroupId(String groupId) {
        System.out.println("########################### Delete Security Group Based on Group Id ###########################");

        DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();

        deleteSecurityGroupRequest.withGroupId(groupId);

        DeleteSecurityGroupResult deleteSecurityGroupResult = amazonEC2.deleteSecurityGroup(deleteSecurityGroupRequest);

        System.out.println(gson.toJson(deleteSecurityGroupResult));
    }

    public String createKeyPair(String keyName) {
        System.out.println("###########################  ###########################");

        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();

        createKeyPairRequest.withKeyName(keyName);

        CreateKeyPairResult createKeyPairResult = amazonEC2.createKeyPair(createKeyPairRequest);

        System.out.println(gson.toJson(createKeyPairResult));

        KeyPair keyPair = createKeyPairResult.getKeyPair();

        String privateKey = keyPair.getKeyMaterial();

        return privateKey;
    }

    public void describeKeyPairs(String keyName) {
        System.out.println("########################### Describe Key Pairs ###########################");

        DescribeKeyPairsRequest describeKeyPairsRequest = new DescribeKeyPairsRequest();

        if(!StringUtils.isNullOrEmpty(keyName)) {
            describeKeyPairsRequest.withKeyNames(keyName);
        }

        DescribeKeyPairsResult describeKeyPairsResult = amazonEC2.describeKeyPairs(describeKeyPairsRequest);
//        DescribeKeyPairsResult describeKeyPairsResult = amazonEC2.describeKeyPairs();

        System.out.println(gson.toJson(describeKeyPairsResult));
    }

    public void deleteKeyPair(String keyName) {
        System.out.println("########################### Delete Key Pair ###########################");

        DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest();

        deleteKeyPairRequest.withKeyName(keyName);

        DeleteKeyPairResult deleteKeyPairResult = amazonEC2.deleteKeyPair(deleteKeyPairRequest);

        System.out.println(gson.toJson(deleteKeyPairResult));
    }

    public void importKeyPair(String keyName, String publicKeyMaterial) {
        System.out.println("########################### Import Key Pair ###########################");

        ImportKeyPairRequest importKeyPairRequest = new ImportKeyPairRequest();

        importKeyPairRequest.withKeyName(keyName);

        importKeyPairRequest.withPublicKeyMaterial(publicKeyMaterial);

        ImportKeyPairResult importKeyPairResult = amazonEC2.importKeyPair(importKeyPairRequest);

        System.out.println(gson.toJson(importKeyPairResult));
    }

    public void describeImages(String imageId) {
        System.out.println("########################### Describe Images ###########################");

        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();

        if(!StringUtils.isNullOrEmpty(imageId)) {
            describeImagesRequest.withImageIds(imageId);
        }

        DescribeImagesResult describeImagesResult = amazonEC2.describeImages(describeImagesRequest);
//        DescribeImagesResult describeImagesResult = amazonEC2.describeImages();

        System.out.println(gson.toJson(describeImagesResult));
    }

    public String runInstances(String amiImageId,
                               InstanceType instanceType,
                               int minInstanceCount,
                               int maxInstanceCount,
                               String securityGroupName,
                               String keyPairName) {
        System.out.println("########################### Run Instances ###########################");

        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        runInstancesRequest.withImageId(amiImageId);

        runInstancesRequest.withInstanceType(instanceType);

        runInstancesRequest.withKeyName(keyPairName);

        runInstancesRequest.withSecurityGroups(securityGroupName);

        runInstancesRequest.withMinCount(minInstanceCount);

        runInstancesRequest.withMaxCount(maxInstanceCount);

        RunInstancesResult runInstancesResult =
                amazonEC2.runInstances(runInstancesRequest);

        System.out.println(gson.toJson(runInstancesResult));

        return runInstancesResult
                .getReservation()
                .getInstances()
                .get(0)
                .getInstanceId();
    }

    public void startInstances(String instanceId) {
        System.out.println("########################### Start Instances ###########################");

        StartInstancesRequest startInstancesRequest = new StartInstancesRequest();

        startInstancesRequest.withInstanceIds(instanceId);

        StartInstancesResult startInstancesResult = amazonEC2.startInstances(startInstancesRequest);

        System.out.println(gson.toJson(startInstancesResult));
    }

    public void stopInstances(String instanceId) {
        System.out.println("########################### Stop Instances ###########################");

        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();

        stopInstancesRequest.withInstanceIds(instanceId);

        StopInstancesResult stopInstancesResult = amazonEC2.stopInstances(stopInstancesRequest);

        System.out.println(gson.toJson(stopInstancesResult));
    }

    public void terminateInstances(String instanceId) {
        System.out.println("########################### Terminate Instances ###########################");

        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();

        terminateInstancesRequest.withInstanceIds(instanceId);

        TerminateInstancesResult terminateInstancesResult = amazonEC2.terminateInstances(terminateInstancesRequest);

        System.out.println(gson.toJson(terminateInstancesResult));
    }

    public List<String> describeInstances(String instanceId) {
        System.out.println("########################### Describe Instances ###########################");

        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();

        if(!StringUtils.isNullOrEmpty(instanceId)) {
            describeInstancesRequest.withInstanceIds(instanceId);
        }

        DescribeInstancesResult describeInstancesResult = amazonEC2.describeInstances(describeInstancesRequest);

        System.out.println(gson.toJson(describeInstancesResult));

        List<String> instanceIdList = new ArrayList<>();

        if(!CollectionUtils.isNullOrEmpty(describeInstancesResult.getReservations())) {

            for(Reservation reservation: describeInstancesResult.getReservations()) {

                List<com.amazonaws.services.ec2.model.Instance> instances = reservation.getInstances();

                if(instances!=null && !instances.isEmpty()) {

                    for(com.amazonaws.services.ec2.model.Instance instance: instances) {

                        instanceIdList.add(instance.getInstanceId());

                    }
                }
            }
        }

        return instanceIdList;
    }
}
