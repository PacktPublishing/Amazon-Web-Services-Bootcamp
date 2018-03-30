package com.chapter7;

import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder;
import com.amazonaws.services.elasticache.model.*;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Chapter7 {
    private AmazonElastiCache amazonElastiCache;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;

    public Chapter7() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonElastiCache = AmazonElastiCacheClientBuilder
                .standard()
//                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        Chapter7 object = new Chapter7();

//        object.createMemcachedCluster();
//        Thread.sleep(300000);
//        object.performMemcachedOperations();

        //-----------------------------------
        // Redis

//        object.createRedisSingleNode();
//        Thread.sleep(600000);
//        object.performRedisOperations();
        object.createRedisCacheWithClusterDisabled();
        object.createRedisCacheWithClusterEnabled();

        System.out.println("------------------------------ THE END ------------------------------");
    }

    private void createMemcachedCluster() throws InterruptedException {
        /*String groupName = "aws-bootcamp-elasticache";

        String groupDescription =
                "Security Group for AWS Bootcamp Elasticache";

        String cidr = "0.0.0.0/0";

        Chapter3 chapter3 = new Chapter3();

        String groupId = chapter3.createSecurityGroup(
                groupName, groupDescription);

        chapter3.authorizeSecurityGroupIngress(
                groupName, memcachePort, cidr);*/

        String groupId = "sg-2e5a905a";

        String cacheClusterId = "aws-bootcamp-memcached";

        String memcachedEngine = "memcached";

        String cacheNodeType = "cache.t2.micro";

        Integer numCacheNodes = 2;

        AZMode aZMode = AZMode.SingleAz;

        int memcachePort = 11211;

        String memcachedVersion = "1.4.34";

        createCacheCluster(
                cacheClusterId,
                memcachedEngine,
                memcachedVersion,
                memcachePort,
                cacheNodeType,
                numCacheNodes,
                aZMode,
                groupId);
    }

    private void performMemcachedOperations() throws IOException, ExecutionException, InterruptedException {
        String cacheClusterId = "aws-bootcamp";

        Endpoint endpoint = getEndpoint(cacheClusterId);

        ElasticCacheClient client =
                new ElasticCacheClient(
                        endpoint.getAddress(),
                        endpoint.getPort());

        String cacheKey = "Book-Name";

        String cacheValue = "AWS Bootcamp";

        int expiryTime = 60*60*24*30;

        boolean addStatus =
                client.add(
                        cacheKey,
                        expiryTime,
                        cacheValue);

        System.out.println("Add Status: " + addStatus);

        String returnedCacheValue =
                (String) client.get(cacheKey);

        System.out.println("Returned Cache Value: " + returnedCacheValue);

        cacheValue = "AWS Bootcamp - Packt Publishing";

        boolean replaceStatus =
                client.replace(
                        cacheKey,
                        expiryTime,
                        cacheValue);

        System.out.println("Replace Status: " + replaceStatus);

        boolean deleteStatus =
                client.delete(cacheKey);

        System.out.println("Delete Status: " + deleteStatus);
    }

    private void createRedisSingleNode() {
        String redisCacheClusterId = "aws-bootcamp-redis";

        String redisEngine = "redis";

        String redisVersion = "3.2.10";

        int redisPort = 6379;

        String redisCacheNodeType = "cache.t2.micro";

        Integer redisNumCacheNodes = 1;

        AZMode redisAZMode = AZMode.SingleAz;

        String groupId = "sg-2e5a905a";

        createCacheCluster(
                redisCacheClusterId,
                redisEngine,
                redisVersion,
                redisPort,
                redisCacheNodeType,
                redisNumCacheNodes,
                redisAZMode,
                groupId);
    }

    private void performRedisOperations() {
        String redisCacheClusterId = "aws-bootcamp-redis";

        Endpoint endpoint2 = getEndpoint(redisCacheClusterId);

        RedisClient redisClient =
                new RedisClient(
                        endpoint2.getAddress(),
                        endpoint2.getPort());

        String cacheKey2 = "Book-Name";

        String cacheValue2 = "AWS Bootcamp";

        redisClient.add(cacheKey2, cacheValue2);

        String returnedCacheValue2 = redisClient.get(cacheKey2);

        System.out.println("Returned Cache Value: " + returnedCacheValue2);

        cacheValue2 = "AWS Bootcamp - Packt Publishing";

        redisClient.add(cacheKey2, cacheValue2);

        returnedCacheValue2 = redisClient.get(cacheKey2);

        System.out.println("Returned Cache Value: " + returnedCacheValue2);

        redisClient.delete(cacheKey2);

        returnedCacheValue2 = redisClient.get(cacheKey2);

        System.out.println("Returned Cache Value: " + returnedCacheValue2);
    }

    private void createRedisCacheWithClusterDisabled() {
        String replicationGroupId = "aws-bootcamp-redis-d";

        String replicationGroupDescription = "AWS Bootcamp redis cluster disabled";

        int numCacheClusters = 3;

        String engine = "redis";

        String engineVersion = "3.2.10";

        int redisPort = 6379;

        String cacheParameterGroupName = "default.redis3.2";

        String cacheNodeType = "cache.t2.micro";

        String subnetGroupName = "default";

        String securityGroupId = "sg-2e5a905a";

        createRedisCacheWithClusterDisabled(
                replicationGroupId,
                replicationGroupDescription,
                numCacheClusters,
                engine,
                engineVersion,
                redisPort,
                cacheParameterGroupName,
                cacheNodeType,
                subnetGroupName,
                securityGroupId);
    }

    private void createRedisCacheWithClusterEnabled() {
        String replicationGroupId = "aws-bootcamp-redis-e";

        String replicationGroupDescription = "AWS Bootcamp redis cluster enabled";

        String engine = "redis";

        String engineVersion = "3.2.10";

        int redisPort = 6379;

        String cacheParameterGroupName = "default.redis3.2.cluster.on";

        String cacheNodeType = "cache.t2.micro";

        String subnetGroupName = "default";

        String securityGroupId = "sg-2e5a905a";

        int numNodeGroups = 2;

        int replicasPerNodeGroup = 2;

        createRedisCacheWithClusterEnabled(
                replicationGroupId,
                replicationGroupDescription,
                engine,
                engineVersion,
                redisPort,
                cacheParameterGroupName,
                cacheNodeType,
                numNodeGroups,
                replicasPerNodeGroup,
                subnetGroupName,
                securityGroupId);
    }

    private void createRedisCacheWithClusterEnabled(
            String replicationGroupId,
            String replicationGroupDescription,
            String engine,
            String engineVersion,
            Integer port,
            String cacheParameterGroupName,
            String cacheNodeType,
            int numNodeGroups,
            int replicasPerNodeGroup,
            String subnetGroupName,
            String securityGroupId) {
        System.out.println("-------------------------------------------");

        CreateReplicationGroupRequest request =
                new CreateReplicationGroupRequest()
                        .withReplicationGroupId(replicationGroupId)
                        .withReplicationGroupDescription(
                                replicationGroupDescription)
                        .withEngine(engine)
                        .withEngineVersion(engineVersion)
                        .withPort(port)
                        .withCacheParameterGroupName(cacheParameterGroupName)
                        .withCacheNodeType(cacheNodeType)
                        .withNumNodeGroups(numNodeGroups)
                        .withReplicasPerNodeGroup(replicasPerNodeGroup)
                        .withCacheSubnetGroupName(subnetGroupName)
                        .withSecurityGroupIds(securityGroupId);

        ReplicationGroup replicationGroup =
                amazonElastiCache.createReplicationGroup(request);
        System.out.println(gson.toJson(replicationGroup));
    }

    private void createRedisCacheWithClusterDisabled(
            String replicationGroupId,
            String replicationGroupDescription,
            Integer numCacheClusters,
            String engine,
            String engineVersion,
            Integer port,
            String cacheParameterGroupName,
            String cacheNodeType,
            String subnetGroupName,
            String securityGroupId) {
        System.out.println("-------------------------------------------");

        CreateReplicationGroupRequest request =
                new CreateReplicationGroupRequest()
                .withReplicationGroupId(replicationGroupId)
                .withReplicationGroupDescription(
                        replicationGroupDescription)
                .withNumCacheClusters(numCacheClusters)
                .withEngine(engine)
                .withEngineVersion(engineVersion)
                .withPort(port)
                .withCacheParameterGroupName(cacheParameterGroupName)
                .withCacheNodeType(cacheNodeType)
                .withCacheSubnetGroupName(subnetGroupName)
                .withSecurityGroupIds(securityGroupId);

        ReplicationGroup replicationGroup =
                amazonElastiCache.createReplicationGroup(request);
        System.out.println(gson.toJson(replicationGroup));
    }

    private void createCacheCluster(
            String cacheClusterId,
            String engine,
            String engineVersion,
            int port,
            String cacheNodeType,
            Integer numCacheNodes,
            AZMode aZMode,
            String securityGroupId) {
        System.out.println("-------------------------------------------");
        CreateCacheClusterRequest request =
                new CreateCacheClusterRequest()
                        .withCacheClusterId(cacheClusterId)
                        .withPort(port)
                        .withEngine(engine)
                        .withEngineVersion(engineVersion)
                        .withCacheNodeType(cacheNodeType)
                        .withNumCacheNodes(numCacheNodes)
                        .withAZMode(aZMode)
                        .withSecurityGroupIds(securityGroupId)
                ;

        CacheCluster cacheCluster =
                amazonElastiCache.createCacheCluster(request);

        System.out.println(gson.toJson(cacheCluster));
    }

    private Endpoint getEndpoint(String cacheClusterId) {
        System.out.println("-------------------------------------------");
        DescribeCacheClustersRequest request =
                new DescribeCacheClustersRequest()
                        .withCacheClusterId(cacheClusterId);

        DescribeCacheClustersResult result = amazonElastiCache.describeCacheClusters(request);

        System.out.println("Status: " + result.getCacheClusters().get(0).getCacheClusterStatus());

        return result.getCacheClusters().get(0).getConfigurationEndpoint();
    }
}
