package com.chapter3;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sunil Gulabani on 01-10-2017.
 */
public class AutoScalingExample extends Chapter3 {
    private AmazonAutoScaling autoScaling;
    private AmazonCloudWatch cloudWatch;

    public AutoScalingExample() {
        super();

        this.autoScaling = AmazonAutoScalingClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.cloudWatch = AmazonCloudWatchClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();
    }

    public static void main(String[] args) {
        AutoScalingExample object = new AutoScalingExample();

        String launchConfigurationName = "AWS-Bootcamp";

        String imageId = "ami-c998b6b2";

        String instanceType = InstanceType.T2Micro.toString();

        String spotPrice = null;

        String iamInstanceProfile = null;

        boolean instanceMonitoringEnabled = false;

        String kernelId = null;

        String ramDiskId = null;

        String userData = null;

        boolean associatePublicIpAddress = true;

        List<String> securityGroups = Arrays.asList("sg-7c6ecf0f");

        String keyPairName = "aws-bootcamp";

        String blockDeviceName = "/dev/sda1";

        Integer volumeSize = 10;

        object.createLaunchConfiguration(
                launchConfigurationName,
                imageId,
                instanceType,
                spotPrice,
                iamInstanceProfile,
                instanceMonitoringEnabled,
                kernelId,
                ramDiskId,
                userData,
                associatePublicIpAddress,
                securityGroups,
                keyPairName,
                blockDeviceName,
                volumeSize
        );

        //------------------------------------------------------------------------------

        String autoScalingGroupName = "AWS-Bootcamp";

        Integer minimumSize = 2;

        String vpcZoneIdentified = "subnet-ec8ecfb6,subnet-3c985558,subnet-972769bb";

        List<String> loadBalancerNames = null;

        Integer healthCheckGracePeriod = 300;

        boolean instanceProtected = false;

        Integer maximumSize = 4;

        object.createAutoScalingGroup(
                autoScalingGroupName,
                launchConfigurationName,
                minimumSize,
                vpcZoneIdentified,
                loadBalancerNames,
                healthCheckGracePeriod,
                instanceProtected,
                maximumSize);

        //------------------------------------------------------------------------------

        String scaleInPolicyName = "Increase Group Size";

        Integer scaleInAdjustment = 1;

        Integer scaleInCoolDown = 300;

        String scaleInPolicyARN = object.putScalingPolicy(
                autoScalingGroupName,
                scaleInPolicyName,
                scaleInAdjustment,
                scaleInCoolDown);

        String scaleInAlarmName = "IncreaseGroupSize-AWS-Bootcamp";

        String scaleInDimensionName = "AutoScalingGroupName";

        String scaleInDimensionValue = autoScalingGroupName;

        String scaleInNamespace = "AWS/EC2";

        String scaleInMetricName = "CPUUtilization";

        Statistic scaleInStatistic = Statistic.Average;

        ComparisonOperator scaleInComparisonOperator =
                ComparisonOperator.GreaterThanOrEqualToThreshold;

        Double scaleInThreshold = 80.0;

        String scaleInUnit = "Percent";

        Integer scaleInEvaluationPeriod = 2;

        Integer scaleInPeriod = 300;

        object.putMetricAlarm(scaleInAlarmName,
                scaleInPolicyARN,
                scaleInDimensionName,
                scaleInDimensionValue,
                scaleInNamespace,
                scaleInMetricName,
                scaleInStatistic,
                scaleInComparisonOperator,
                scaleInThreshold,
                scaleInUnit,
                scaleInEvaluationPeriod,
                scaleInPeriod);

        //------------------------------------------------------------------------------

        String scaleOutPolicyName = "Decrease Group Size";

        Integer scaleOutAdjustment = -1;

        Integer scaleOutCoolDown = 300;

        String scaleOutPolicyARN = object.putScalingPolicy(
                autoScalingGroupName,
                scaleOutPolicyName,
                scaleOutAdjustment,
                scaleOutCoolDown);

        String scaleOutAlarmName = "DecreaseGroupSize-AWS-Bootcamp";

        String scaleOutDimensionName = "AutoScalingGroupName";

        String scaleOutDimensionValue = autoScalingGroupName;

        String scaleOutNamespace = "AWS/EC2";

        String scaleOutMetricName = "CPUUtilization";

        Statistic scaleOutStatistic = Statistic.Average;

        ComparisonOperator scaleOutComparisonOperator =
                ComparisonOperator.LessThanOrEqualToThreshold;

        Double scaleOutThreshold = 40.0;

        String scaleOutUnit = "Percent";

        Integer scaleOutEvaluationPeriod = 2;

        Integer scaleOutPeriod = 300;

        object.putMetricAlarm(scaleOutAlarmName,
                scaleOutPolicyARN,
                scaleOutDimensionName,
                scaleOutDimensionValue,
                scaleOutNamespace,
                scaleOutMetricName,
                scaleOutStatistic,
                scaleOutComparisonOperator,
                scaleOutThreshold,
                scaleOutUnit,
                scaleOutEvaluationPeriod,
                scaleOutPeriod);
    }

    public void createLaunchConfiguration(
            String launchConfigurationName,
            String imageId,
            String instanceType,
            String spotPrice,
            String iamInstanceProfile,
            boolean instanceMonitoringEnabled,
            String kernelId,
            String ramDiskId,
            String userData,
            boolean associatePublicIpAddress,
            List<String> securityGroups,
            String keyPairName,
            String blockDeviceName,
            Integer volumeSize
    ) {
        CreateLaunchConfigurationRequest
                createLaunchConfigurationRequest =
                new CreateLaunchConfigurationRequest();

        createLaunchConfigurationRequest
                .withLaunchConfigurationName(launchConfigurationName);

        createLaunchConfigurationRequest.withImageId(imageId);

        createLaunchConfigurationRequest.withInstanceType(instanceType);

        if(!StringUtils.isNullOrEmpty(spotPrice)) {
            createLaunchConfigurationRequest.withSpotPrice(spotPrice);
        }

        if(!StringUtils.isNullOrEmpty(iamInstanceProfile)) {
            createLaunchConfigurationRequest
                    .withIamInstanceProfile(iamInstanceProfile);
        }

        InstanceMonitoring instanceMonitoring = new InstanceMonitoring();

        instanceMonitoring.withEnabled(instanceMonitoringEnabled);
        createLaunchConfigurationRequest
                .withInstanceMonitoring(instanceMonitoring);

        if(!StringUtils.isNullOrEmpty(kernelId)) {
            createLaunchConfigurationRequest.withKernelId(kernelId);
        }

        if(!StringUtils.isNullOrEmpty(ramDiskId)) {
            createLaunchConfigurationRequest.withRamdiskId(ramDiskId);
        }

        if(!StringUtils.isNullOrEmpty(userData)) {
            createLaunchConfigurationRequest.withUserData(userData);
        }

        createLaunchConfigurationRequest
                .withAssociatePublicIpAddress(associatePublicIpAddress);

        BlockDeviceMapping blockDeviceMapping =
                createBlockDeviceMapping(blockDeviceName, volumeSize);

        createLaunchConfigurationRequest
                .withBlockDeviceMappings(blockDeviceMapping);

        createLaunchConfigurationRequest
                .withSecurityGroups(securityGroups);

        createLaunchConfigurationRequest
                .withKeyName(keyPairName);

        CreateLaunchConfigurationResult
                createLaunchConfigurationResult =
                autoScaling.createLaunchConfiguration(
                        createLaunchConfigurationRequest);

        System.out.println(gson.toJson(createLaunchConfigurationResult));
    }

    private BlockDeviceMapping createBlockDeviceMapping(
            String blockDeviceName,
            Integer volumeSize) {
        BlockDeviceMapping blockDeviceMapping = new BlockDeviceMapping();

        blockDeviceMapping.withDeviceName(blockDeviceName);

        Ebs ebs = new Ebs();

        ebs.withVolumeSize(volumeSize);

        blockDeviceMapping.withEbs(ebs);

        return blockDeviceMapping;
    }

    public void createAutoScalingGroup(
            String autoScalingGroupName,
            String launchConfigurationName,
            Integer minimumSize,
            String vpcZoneIdentified,
            List<String> loadBalancerNames,
            Integer healthCheckGracePeriod,
            boolean instanceProtected,
            Integer maximumSize) {

        CreateAutoScalingGroupRequest
                createAutoScalingGroupRequest =
                new CreateAutoScalingGroupRequest();

        createAutoScalingGroupRequest
                .withLaunchConfigurationName(launchConfigurationName);

        createAutoScalingGroupRequest
                .withAutoScalingGroupName(autoScalingGroupName);

        createAutoScalingGroupRequest.withDesiredCapacity(minimumSize);

        createAutoScalingGroupRequest
                .withVPCZoneIdentifier(vpcZoneIdentified);

        if(!CollectionUtils.isNullOrEmpty(loadBalancerNames)) {
            createAutoScalingGroupRequest
                    .withLoadBalancerNames(loadBalancerNames);
        }

        createAutoScalingGroupRequest
                .withHealthCheckGracePeriod(healthCheckGracePeriod);

        createAutoScalingGroupRequest
                .withNewInstancesProtectedFromScaleIn(instanceProtected);

        createAutoScalingGroupRequest.withMinSize(minimumSize);

        createAutoScalingGroupRequest.withMaxSize(maximumSize);

        CreateAutoScalingGroupResult
                createAutoScalingGroupResult =
                autoScaling.createAutoScalingGroup(
                        createAutoScalingGroupRequest);

        System.out.println(gson.toJson(createAutoScalingGroupResult));
    }

    public String putScalingPolicy(
            String autoScalingGroupName,
            String policyName,
            Integer scalingAdjustment,
            Integer coolDown) {
        PutScalingPolicyRequest putScalingPolicyRequest =
                new PutScalingPolicyRequest();

        putScalingPolicyRequest
                .withAutoScalingGroupName(autoScalingGroupName);

        putScalingPolicyRequest.withPolicyName(policyName);

        putScalingPolicyRequest.withPolicyType("SimpleScaling");

        putScalingPolicyRequest.withAdjustmentType("ChangeInCapacity");

        putScalingPolicyRequest.withScalingAdjustment(scalingAdjustment);

        putScalingPolicyRequest.withCooldown(coolDown);

        PutScalingPolicyResult putScalingPolicyResult =
                autoScaling.putScalingPolicy(putScalingPolicyRequest);

        System.out.println(gson.toJson(putScalingPolicyResult));

        return putScalingPolicyResult.getPolicyARN();
    }

    public void putMetricAlarm(String alarmName,
                               String autoScalingPolicyARN,
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
                .withAlarmActions(autoScalingPolicyARN);

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
