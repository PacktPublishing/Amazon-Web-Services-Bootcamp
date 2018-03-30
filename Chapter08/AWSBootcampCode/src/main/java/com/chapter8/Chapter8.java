package com.chapter8;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sunil Gulabani on 14-01-2018.
 */
public class Chapter8 {

    private AmazonSNS amazonSNS;
    protected CredentialsProvider credentialsProvider;
    protected Gson gson;

    public Chapter8() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonSNS = AmazonSNSClientBuilder
                .standard()
                //.withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        Chapter8 object = new Chapter8();

        String topicName = "aws-bootcamp";

        String topicARN = object.getTopicARN(topicName);

        object.deleteTopic(topicARN);

        topicARN = object.createTopic(topicName);

        String emailProtocol = "email";

        String emailEndpoint = "sunil.gulabani1@gmail.com";

        object.subscribe(topicARN, emailProtocol, emailEndpoint);

        String subject = "AWS Bootcamp Notification";

        String message = "Hello, \n\nThis is test notification from SNS!";

        object.publish(topicARN, subject, message);
    }

    private String getTopicARN(String topicName) {
        ListTopicsResult result = amazonSNS.listTopics();

        System.out.println(gson.toJson(result));

        for (Topic topic : result.getTopics()) {
            if(topic.getTopicArn().contains(topicName)) {
                return topic.getTopicArn();
            }
        }

        throw new RuntimeException("No topics found!!!");
    }

    private String createTopic(String topicName) {
        CreateTopicRequest request =
                new CreateTopicRequest()
                        .withName(topicName);

        CreateTopicResult result =
                amazonSNS.createTopic(request);

        System.out.println(gson.toJson(result));

        return result.getTopicArn();
    }

    private void subscribe(
            String topicArn,
            String protocol,
            String endpoint) {
        System.out.println("--------------------------------");

        SubscribeRequest request =
                new SubscribeRequest()
                        .withTopicArn(topicArn)
                        .withProtocol(protocol)
                        .withEndpoint(endpoint);

        SubscribeResult result =
                amazonSNS.subscribe(request);

        System.out.println(gson.toJson(result));
    }

    private void publish(
            String topicARN,
            String subject,
            String message) {
        System.out.println("--------------------------------");

        PublishRequest request =
                new PublishRequest()
                        .withTopicArn(topicARN)
                        .withSubject(subject)
                        .withMessage(message);

        PublishResult result =
                amazonSNS.publish(request);

        System.out.println(gson.toJson(result));
    }

    private void deleteTopic(String topicARN) {
        System.out.println("--------------------------------");

        DeleteTopicRequest request =
                new DeleteTopicRequest()
                        .withTopicArn(topicARN);

        DeleteTopicResult result =
                amazonSNS.deleteTopic(request);

        System.out.println(gson.toJson(result));
    }
}
