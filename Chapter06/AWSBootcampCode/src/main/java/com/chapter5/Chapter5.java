package com.chapter5;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.CreateDBClusterRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.DBInstance;
import com.chapter3.Chapter3;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class Chapter5 {
    private AmazonRDS amazonRDS;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;

    public Chapter5() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonRDS = AmazonRDSClientBuilder
                .standard()
                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) {
        Chapter5 object = new Chapter5();

        /*Chapter3 chapter3 = new Chapter3();

        String groupName = "aws-bootcamp-mysql";
        String groupDescription = "Security Group for AWS Bootcamp MySQL";
        String securityGroupId = chapter3.createSecurityGroup(groupName, groupDescription);
        int mysqlPort = 3306;
        String cidr = "0.0.0.0/0";
        chapter3.authorizeSecurityGroupIngress(groupName, mysqlPort, cidr);*/

        String securityGroupId = "sg-e4248e91";

        String rdsInstanceName = "AWSBootcamp";
        String dbInstanceClass = "db.t2.micro";
        String dbEngine = "MySQL";
        boolean multiAZEnabled = false;
        String masterUsername = "awsbootcamp";
        String masterPassword = "abcd12345";
        String dbName = "awsbootcampdb";
        String storageType = "gp2"; // standard, gp2, io1
        Integer storageCapacity = 20;

        object.createDBInstance(
                rdsInstanceName,
                dbInstanceClass,
                dbEngine,
                multiAZEnabled,
                masterUsername,
                masterPassword,
                dbName,
                storageType,
                storageCapacity,
                Arrays.asList(securityGroupId));
    }

    public void createDBCluster() {
        CreateDBClusterRequest createDBClusterRequest
                = new CreateDBClusterRequest();

        DBCluster dbCluster = amazonRDS.createDBCluster(createDBClusterRequest);

        System.out.println(gson.toJson(dbCluster));
    }

    public void createDBInstance(
            String rdsInstanceName,
            String dbInstanceClass,
            String dbEngine,
            boolean multiAZEnabled,
            String masterUsername,
            String masterPassword,
            String dbName,
            String storageType,
            int storageCapacityInGB,
            List<String> dbSecurityGroups) {

        CreateDBInstanceRequest createDBInstanceRequest
                = new CreateDBInstanceRequest();

        createDBInstanceRequest
                .withDBInstanceIdentifier(rdsInstanceName);

        createDBInstanceRequest
                .withDBInstanceClass(dbInstanceClass);

        createDBInstanceRequest.withEngine(dbEngine);

        createDBInstanceRequest.withMultiAZ(multiAZEnabled);

        createDBInstanceRequest
                .withMasterUsername(masterUsername);

        createDBInstanceRequest
                .withMasterUserPassword(masterPassword);

        createDBInstanceRequest.withDBName(dbName);

        createDBInstanceRequest.withStorageType(storageType);

        createDBInstanceRequest
                .withAllocatedStorage(storageCapacityInGB);

        createDBInstanceRequest
                .withPubliclyAccessible(true);

        createDBInstanceRequest
                .withBackupRetentionPeriod(0);

        createDBInstanceRequest
                .setVpcSecurityGroupIds(dbSecurityGroups);

        DBInstance dbInstance =
                amazonRDS.createDBInstance(createDBInstanceRequest);

        System.out.println(gson.toJson(dbInstance));
    }
}
