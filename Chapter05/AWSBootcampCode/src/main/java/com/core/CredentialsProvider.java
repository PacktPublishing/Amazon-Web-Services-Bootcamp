package com.core;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class CredentialsProvider {
    private Regions regions;

    public CredentialsProvider() {
        regions = Regions.US_EAST_1;
    }

    public ClientConfiguration getClientConfiguration() {
        return new ClientConfiguration()
                .withProxyUsername("")
                .withProxyPassword("")
                .withProtocol(Protocol.HTTPS)
                .withProxyHost("")
                .withProxyPort(80);
    }

    public AWSCredentialsProvider getCredentials() {
//        return new AWSStaticCredentialsProvider(new BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY"));
        return new ProfileCredentialsProvider("aws-bootcamp");
    }

    public Regions getRegions() {
        return regions;
    }
}
