package com.chapter2;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.*;
import com.core.CredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Chapter2 {

    private AmazonIdentityManagement amazonIdentityManagement;
    private CredentialsProvider credentialsProvider;
    private Gson gson;

    public Chapter2() {
        this.credentialsProvider = new CredentialsProvider();
        this.amazonIdentityManagement = AmazonIdentityManagementClientBuilder
                .standard()
                .withClientConfiguration(credentialsProvider.getClientConfiguration())
                .withCredentials(credentialsProvider.getCredentials())
                .withRegion(credentialsProvider.getRegions())
                .build();

        this.gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Chapter2 object = new Chapter2();

        String policyName = "AmazonS3FullAccess";
        String description = "S3 Full Access On my-bucket";
        String policyDocument = object.readFromFile("policyDocument.json");
        String policyARN = object.createPolicy(policyName, description, policyDocument);

        object.getPolicy(policyARN);

        object.listPolicies();

        String newPolicyDocument = object.readFromFile("newPolicyDocument.json");
        object.updatePolicy(policyARN, newPolicyDocument);

        ListPolicyVersionsResult listPolicyVersionsResult = object.listPolicyVersions(policyARN);

        object.getPolicyVersion(policyARN, "v1");

        String roleName = "AmazonS3FullAccess";
        String roleDescription = "S3 Full Access";
        String assumeRolePolicyDocument = object.readFromFile("assumeRolePolicyDocument.json");
        String roleARN = object.createRole(roleName, roleDescription, assumeRolePolicyDocument);

        object.listRoles();

        object.getRole(roleName);

        object.attachRolePolicy(roleName, policyARN);

        object.listAttachedRolePolicies(roleName);

        object.detachRolePolicy(roleName, policyARN);

        object.listAttachedRolePolicies(roleName);

        String inlineRolePolicyName = "S3FullAccessOnMyBucket2";
        String inlineRolePolicyDocument = object.readFromFile("newPolicyDocument.json");
        object.putRolePolicy(roleName, inlineRolePolicyName, inlineRolePolicyDocument);

        object.listRolePolicies(roleName);

        object.getRolePolicy(roleName, inlineRolePolicyName);

        object.deleteRolePolicy(roleName, inlineRolePolicyName);

        String groupName = "Developer";

        object.createGroup(groupName);

        object.getGroup(groupName);

        object.attachGroupPolicy(groupName, policyARN);

        object.listAttachedGroupPolicies(groupName);

        object.detachGroupPolicy(groupName, policyARN);

        object.listAttachedGroupPolicies(groupName);

        String inlineGroupPolicyName = "S3FullAccessOnMyBucket2";
        String inlineGroupPolicyDocument = object.readFromFile("newPolicyDocument.json");
        object.putGroupPolicy(groupName, inlineGroupPolicyName, inlineGroupPolicyDocument);

        object.listGroupPolicies(groupName);

        object.getGroupPolicy(groupName, inlineGroupPolicyName);

        object.deleteGroupPolicy(groupName, inlineGroupPolicyName);

        String userName = "Sunil";
        object.createUser(userName);

        String accessKeyId = object.createAccessKey(userName);

        object.listAccessKeys(userName);

        object.deleteAccessKey(userName, accessKeyId);

        object.createLoginProfile(userName, "abcd1234");

        object.getLoginProfile(userName);

        Thread.sleep(10000);

        object.deleteLoginProfile(userName);

        object.listUsers();

        object.addUserToGroup(userName, groupName);

        object.listGroupsForUser(userName);

        object.removeUserFromGroup(userName, groupName);

        object.listGroupsForUser(userName);

        object.attachUserPolicy(userName, policyARN);

        object.listAttachedUserPolicies(userName);

        object.detachUserPolicy(userName, policyARN);

        object.listAttachedUserPolicies(userName);

        object.deleteUser(userName);

        object.deleteGroup(groupName);

        object.deleteRole(roleName);

        object.deletePolicy(policyARN, listPolicyVersionsResult);
    }

    public String createPolicy(String policyName, String description, String policyDocument) {
        System.out.println("########################### Create Policies ###########################");

        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest();
        createPolicyRequest.setPolicyName(policyName);
        createPolicyRequest.setDescription(description);
        createPolicyRequest.setPolicyDocument(policyDocument);

        CreatePolicyResult createPolicyResult = amazonIdentityManagement.createPolicy(createPolicyRequest);
        System.out.println(gson.toJson(createPolicyResult));

        return createPolicyResult.getPolicy().getArn();
    }

    public GetPolicyResult getPolicy(String policyARN) {
        System.out.println("########################### Get Policy ###########################");
        GetPolicyRequest getPolicyRequest =  new GetPolicyRequest();
        getPolicyRequest.setPolicyArn(policyARN);

        GetPolicyResult getPolicyResult = amazonIdentityManagement.getPolicy(getPolicyRequest);
        System.out.println(gson.toJson(getPolicyResult));

        return getPolicyResult;
    }

    public void listPolicies() {
        System.out.println("########################### List Policies ###########################");
        ListPoliciesRequest listPoliciesRequest = new ListPoliciesRequest();
        listPoliciesRequest.setScope(PolicyScopeType.Local);

        ListPoliciesResult listPoliciesResult = amazonIdentityManagement.listPolicies(listPoliciesRequest);
        System.out.println(gson.toJson(listPoliciesResult));
    }

    public String updatePolicy(String policyARN, String policyDocument) {
        System.out.println("########################### Update Policy ###########################");
        CreatePolicyVersionRequest createPolicyVersionRequest = new CreatePolicyVersionRequest();
        createPolicyVersionRequest.setPolicyArn(policyARN);
        createPolicyVersionRequest.setPolicyDocument(policyDocument);
        createPolicyVersionRequest.setSetAsDefault(true);

        CreatePolicyVersionResult createPolicyVersionResult =
                amazonIdentityManagement.createPolicyVersion(createPolicyVersionRequest);
        System.out.println(gson.toJson(createPolicyVersionResult));

        return createPolicyVersionResult.getPolicyVersion().getVersionId();
    }

    public ListPolicyVersionsResult listPolicyVersions(String policyARN) {
        System.out.println("########################### List Policy Versions ###########################");
        ListPolicyVersionsRequest listPolicyVersionsRequest = new ListPolicyVersionsRequest();
        listPolicyVersionsRequest.setPolicyArn(policyARN);
        ListPolicyVersionsResult listPolicyVersionsResult =
                amazonIdentityManagement.listPolicyVersions(listPolicyVersionsRequest);
        System.out.println(gson.toJson(listPolicyVersionsResult));

        return listPolicyVersionsResult;
    }

    public void getPolicyVersion(String policyARN, String versionId) {
        System.out.println("########################### Get Policy Version ###########################");
        GetPolicyVersionRequest getPolicyVersionRequest = new GetPolicyVersionRequest();
        getPolicyVersionRequest.setPolicyArn(policyARN);
        getPolicyVersionRequest.setVersionId(versionId);
        GetPolicyVersionResult getPolicyVersionResult = amazonIdentityManagement.getPolicyVersion(getPolicyVersionRequest);
        System.out.println(gson.toJson(getPolicyVersionResult));
    }

    public void deletePolicyVersion(String policyARN, String versionId) {
        System.out.println("########################### Delete Policy Version ###########################");
        DeletePolicyVersionRequest deletePolicyVersionRequest = new DeletePolicyVersionRequest();
        deletePolicyVersionRequest.setPolicyArn(policyARN);
        deletePolicyVersionRequest.setVersionId(versionId);
        DeletePolicyVersionResult deletePolicyVersionResult =
                amazonIdentityManagement.deletePolicyVersion(deletePolicyVersionRequest);
        System.out.println(gson.toJson(deletePolicyVersionResult));
    }

    public void deletePolicy(String policyARN, ListPolicyVersionsResult listPolicyVersionsResult) {
        List<String> policyVersionIdListWithoutDefaultVersion = listPolicyVersionsResult.getVersions().stream()
                .filter(policyVersion -> !policyVersion.isDefaultVersion())
                .map(policyVersion -> policyVersion.getVersionId())
                .collect(Collectors.toList());

        policyVersionIdListWithoutDefaultVersion.forEach(versionId -> {
            deletePolicyVersion(policyARN, versionId);
        });

        System.out.println("########################### Delete Policy ###########################");
        DeletePolicyRequest deletePolicyRequest = new DeletePolicyRequest();
        deletePolicyRequest.setPolicyArn(policyARN);
        DeletePolicyResult deletePolicyResult = amazonIdentityManagement.deletePolicy(deletePolicyRequest);
        System.out.println(gson.toJson(deletePolicyResult));
    }

    public String createRole(String roleName, String description, String assumeRolePolicyDocument) {
        System.out.println("########################### Create Role ###########################");
        CreateRoleRequest createRoleRequest = new CreateRoleRequest();
        createRoleRequest.setRoleName(roleName);
        createRoleRequest.setDescription(description);
        createRoleRequest.setAssumeRolePolicyDocument(assumeRolePolicyDocument);

        CreateRoleResult createRoleResult = amazonIdentityManagement.createRole(createRoleRequest);
        System.out.println(gson.toJson(createRoleResult));

        return createRoleResult.getRole().getArn();
    }

    public void getRole(String roleName) {
        System.out.println("########################### Get Role ###########################");
        GetRoleRequest getRoleRequest = new GetRoleRequest();
        getRoleRequest.setRoleName(roleName);

        GetRoleResult getRoleResult = amazonIdentityManagement.getRole(getRoleRequest);
        System.out.println(gson.toJson(getRoleResult));
    }

    public void listRoles() {
        System.out.println("########################### List Roles ###########################");
        ListRolesResult listRolesResult = amazonIdentityManagement.listRoles();
        System.out.println(gson.toJson(listRolesResult));
    }

    public void listRolePolicies(String roleName) {
        System.out.println("########################### List Inline Role Policies ###########################");
        ListRolePoliciesRequest listRolePoliciesRequest = new ListRolePoliciesRequest();
        listRolePoliciesRequest.setRoleName(roleName);
        ListRolePoliciesResult listRolePoliciesResult = amazonIdentityManagement.listRolePolicies(listRolePoliciesRequest);
        System.out.println(gson.toJson(listRolePoliciesResult));
    }

    public void getRolePolicy(String roleName, String policyName) {
        System.out.println("########################### Get Inline Role Policy ###########################");
        GetRolePolicyRequest getRolePolicyRequest = new GetRolePolicyRequest();
        getRolePolicyRequest.setRoleName(roleName);
        getRolePolicyRequest.setPolicyName(policyName);
        GetRolePolicyResult getRolePolicyResult = amazonIdentityManagement.getRolePolicy(getRolePolicyRequest);
        System.out.println(gson.toJson(getRolePolicyResult));
    }

    public void putRolePolicy(String roleName, String policyName, String policyDocument) {
        System.out.println("########################### Create Inline Role Policies ###########################");
        PutRolePolicyRequest putRolePolicyRequest = new PutRolePolicyRequest();
        putRolePolicyRequest.setRoleName(roleName);
        putRolePolicyRequest.setPolicyName(policyName);
        putRolePolicyRequest.setPolicyDocument(policyDocument);

        PutRolePolicyResult putRolePolicyResult = amazonIdentityManagement.putRolePolicy(putRolePolicyRequest);
        System.out.println(gson.toJson(putRolePolicyResult));
    }

    public void deleteRolePolicy(String roleName, String policyName) {
        System.out.println("########################### Delete Inline Role Policies ###########################");
        DeleteRolePolicyRequest deleteRolePolicyRequest = new DeleteRolePolicyRequest();
        deleteRolePolicyRequest.setRoleName(roleName);
        deleteRolePolicyRequest.setPolicyName(policyName);
        DeleteRolePolicyResult deleteRolePolicyResult = amazonIdentityManagement.deleteRolePolicy(deleteRolePolicyRequest);
        System.out.println(gson.toJson(deleteRolePolicyResult));
    }

    public void listAttachedRolePolicies(String roleName) {
        System.out.println("########################### List Attached Role Policies ###########################");

        ListAttachedRolePoliciesRequest listAttachedRolePoliciesRequest = new ListAttachedRolePoliciesRequest();
        listAttachedRolePoliciesRequest.setRoleName(roleName);

        ListAttachedRolePoliciesResult listRolePoliciesResult = amazonIdentityManagement.listAttachedRolePolicies(listAttachedRolePoliciesRequest);
        System.out.println(gson.toJson(listRolePoliciesResult));
    }

    public void attachRolePolicy(String roleName, String policyARN) {
        System.out.println("########################### Attach Role Policy ###########################");

        AttachRolePolicyRequest attachRolePolicyRequest = new AttachRolePolicyRequest();
        attachRolePolicyRequest.setRoleName(roleName);
        attachRolePolicyRequest.setPolicyArn(policyARN);

        AttachRolePolicyResult attachRolePolicyResult = amazonIdentityManagement.attachRolePolicy(attachRolePolicyRequest);
        System.out.println(gson.toJson(attachRolePolicyResult));
    }

    public void detachRolePolicy(String roleName, String policyARN) {
        System.out.println("########################### Detach Role Policy ###########################");
        DetachRolePolicyRequest detachRolePolicyRequest = new DetachRolePolicyRequest();
        detachRolePolicyRequest.setRoleName(roleName);
        detachRolePolicyRequest.setPolicyArn(policyARN);

        DetachRolePolicyResult detachRolePolicyResult = amazonIdentityManagement.detachRolePolicy(detachRolePolicyRequest);
        System.out.println(gson.toJson(detachRolePolicyResult));
    }

    public void deleteRole(String roleName) {
        System.out.println("########################### Delete Role ###########################");
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest();
        deleteRoleRequest.setRoleName(roleName);

        DeleteRoleResult deleteRoleResult = amazonIdentityManagement.deleteRole(deleteRoleRequest);
        System.out.println(gson.toJson(deleteRoleResult));
    }

    public void createGroup(String groupName) {
        System.out.println("########################### Create Group ###########################");
        CreateGroupRequest createGroupRequest = new CreateGroupRequest();
        createGroupRequest.setGroupName(groupName);

        CreateGroupResult createGroupResult = amazonIdentityManagement.createGroup(createGroupRequest);
        System.out.println(gson.toJson(createGroupResult));
    }

    public void attachGroupPolicy(String groupName, String policyARN) {
        System.out.println("########################### Attach Group Policy ###########################");
        AttachGroupPolicyRequest attachGroupPolicyRequest = new AttachGroupPolicyRequest();
        attachGroupPolicyRequest.setGroupName(groupName);
        attachGroupPolicyRequest.setPolicyArn(policyARN);

        AttachGroupPolicyResult attachGroupPolicyResult = amazonIdentityManagement.attachGroupPolicy(attachGroupPolicyRequest);
        System.out.println(gson.toJson(attachGroupPolicyResult));
    }

    public void detachGroupPolicy(String groupName, String policyARN) {
        System.out.println("########################### Detach Group Policy ###########################");
        DetachGroupPolicyRequest detachGroupPolicyRequest = new DetachGroupPolicyRequest();
        detachGroupPolicyRequest.setGroupName(groupName);
        detachGroupPolicyRequest.setPolicyArn(policyARN);

        DetachGroupPolicyResult detachGroupPolicyResult = amazonIdentityManagement.detachGroupPolicy(detachGroupPolicyRequest);
        System.out.println(gson.toJson(detachGroupPolicyResult));
    }

    public void getGroup(String groupName) {
        System.out.println("########################### Get Group ###########################");
        GetGroupRequest getGroupRequest = new GetGroupRequest();
        getGroupRequest.setGroupName(groupName);

        GetGroupResult getGroupResult = amazonIdentityManagement.getGroup(getGroupRequest);
        System.out.println(gson.toJson(getGroupResult));
    }

    public void listAttachedGroupPolicies(String groupName) {
        System.out.println("########################### List Attached Group Policies ###########################");
        ListAttachedGroupPoliciesRequest listAttachedGroupPoliciesRequest = new ListAttachedGroupPoliciesRequest();
        listAttachedGroupPoliciesRequest.setGroupName(groupName);

        ListAttachedGroupPoliciesResult listAttachedGroupPoliciesResult = amazonIdentityManagement.listAttachedGroupPolicies(listAttachedGroupPoliciesRequest);
        System.out.println(gson.toJson(listAttachedGroupPoliciesResult));
    }

    public void putGroupPolicy(String groupName, String policyName, String policyDocument) {
        System.out.println("########################### Put Group Policies ###########################");
        PutGroupPolicyRequest putGroupPolicyRequest = new PutGroupPolicyRequest();
        putGroupPolicyRequest.setGroupName(groupName);
        putGroupPolicyRequest.setPolicyName(policyName);
        putGroupPolicyRequest.setPolicyDocument(policyDocument);
        PutGroupPolicyResult putGroupPolicyResult = amazonIdentityManagement.putGroupPolicy(putGroupPolicyRequest);
        System.out.println(gson.toJson(putGroupPolicyResult));
    }

    public void listGroupPolicies(String groupName) {
        System.out.println("########################### List Group Policies ###########################");
        ListGroupPoliciesRequest listGroupPoliciesRequest = new ListGroupPoliciesRequest();
        listGroupPoliciesRequest.setGroupName(groupName);
        ListGroupPoliciesResult listGroupPoliciesResult = amazonIdentityManagement.listGroupPolicies(listGroupPoliciesRequest);
        System.out.println(gson.toJson(listGroupPoliciesResult));
    }

    public void getGroupPolicy(String groupName, String policyName) {
        System.out.println("########################### Get Group Policy ###########################");
        GetGroupPolicyRequest getGroupPolicyRequest = new GetGroupPolicyRequest();
        getGroupPolicyRequest.setGroupName(groupName);
        getGroupPolicyRequest.setPolicyName(policyName);
        GetGroupPolicyResult getGroupPolicyResult = amazonIdentityManagement.getGroupPolicy(getGroupPolicyRequest);
        System.out.println(gson.toJson(getGroupPolicyResult));
    }

    public void deleteGroupPolicy(String groupName, String policyName) {
        System.out.println("########################### Delete Group Policies ###########################");
        DeleteGroupPolicyRequest deleteGroupPolicyRequest = new DeleteGroupPolicyRequest();
        deleteGroupPolicyRequest.setGroupName(groupName);
        deleteGroupPolicyRequest.setPolicyName(policyName);
        DeleteGroupPolicyResult deleteGroupPolicyResult = amazonIdentityManagement.deleteGroupPolicy(deleteGroupPolicyRequest);
        System.out.println(gson.toJson(deleteGroupPolicyResult));
    }

    public void deleteGroup(String groupName) {
        System.out.println("########################### List Attached Group Policies ###########################");

        DeleteGroupRequest deleteGroupRequest = new DeleteGroupRequest();
        deleteGroupRequest.setGroupName(groupName);

        DeleteGroupResult deleteGroupResult = amazonIdentityManagement.deleteGroup(deleteGroupRequest);
        System.out.println(gson.toJson(deleteGroupResult));
    }

    public void createUser(String userName) {
        System.out.println("########################### Create User ###########################");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUserName(userName);

        CreateUserResult createUserResult = amazonIdentityManagement.createUser(createUserRequest);
        System.out.println(gson.toJson(createUserResult));
    }

    public String createAccessKey(String userName) {
        System.out.println("########################### Create Access Key ###########################");
        CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest();
        createAccessKeyRequest.setUserName(userName);

        CreateAccessKeyResult createAccessKeyResult = amazonIdentityManagement.createAccessKey(createAccessKeyRequest);
        System.out.println(gson.toJson(createAccessKeyResult));

        return createAccessKeyResult.getAccessKey().getAccessKeyId();
    }

    public void listAccessKeys(String userName) {
        System.out.println("########################### List Access Keys ###########################");
        ListAccessKeysRequest listAccessKeysRequest = new ListAccessKeysRequest();
        listAccessKeysRequest.setUserName(userName);
        ListAccessKeysResult listAccessKeysResult = amazonIdentityManagement.listAccessKeys(listAccessKeysRequest);
        System.out.println(gson.toJson(listAccessKeysResult));
    }

    public void deleteAccessKey(String userName, String accessKeyId) {
        System.out.println("########################### Delete Access Key ###########################");
        DeleteAccessKeyRequest deleteAccessKeyRequest = new DeleteAccessKeyRequest();
        deleteAccessKeyRequest.setUserName(userName);
        deleteAccessKeyRequest.setAccessKeyId(accessKeyId);
        DeleteAccessKeyResult deleteAccessKeyResult = amazonIdentityManagement.deleteAccessKey(deleteAccessKeyRequest);
        System.out.println(gson.toJson(deleteAccessKeyResult));
    }

    public void createLoginProfile(String userName, String password) {
        System.out.println("########################### Create Login Profile ###########################");
        CreateLoginProfileRequest createLoginProfileRequest = new CreateLoginProfileRequest();
        createLoginProfileRequest.setUserName(userName);
        createLoginProfileRequest.setPassword(password);
        createLoginProfileRequest.setPasswordResetRequired(true);

        CreateLoginProfileResult createLoginProfileResult = amazonIdentityManagement.createLoginProfile(createLoginProfileRequest);
        System.out.println(gson.toJson(createLoginProfileResult));
    }

    public void getLoginProfile(String userName) {
        System.out.println("########################### Get Login Profile ###########################");
        GetLoginProfileRequest getLoginProfileRequest = new GetLoginProfileRequest();
        getLoginProfileRequest.setUserName(userName);
        GetLoginProfileResult getLoginProfileResult = amazonIdentityManagement.getLoginProfile(getLoginProfileRequest);
        System.out.println(gson.toJson(getLoginProfileResult));
    }

    public void deleteLoginProfile(String userName) {
        System.out.println("########################### Delete Login Profile ###########################");
        DeleteLoginProfileRequest deleteLoginProfileRequest = new DeleteLoginProfileRequest();
        deleteLoginProfileRequest.setUserName(userName);
        DeleteLoginProfileResult deleteLoginProfileResult = amazonIdentityManagement.deleteLoginProfile(deleteLoginProfileRequest);
        System.out.println(gson.toJson(deleteLoginProfileResult));
    }

    public void listUsers() {
        System.out.println("########################### List Users ###########################");
        ListUsersResult listUsersResult = amazonIdentityManagement.listUsers();
        System.out.println(gson.toJson(listUsersResult));
    }

    public void addUserToGroup(String userName, String groupName) {
        System.out.println("########################### Add User to Group ###########################");
        AddUserToGroupRequest addUserToGroupRequest = new AddUserToGroupRequest();
        addUserToGroupRequest.setUserName(userName);
        addUserToGroupRequest.setGroupName(groupName);

        AddUserToGroupResult addUserToGroupResult = amazonIdentityManagement.addUserToGroup(addUserToGroupRequest);
        System.out.println(gson.toJson(addUserToGroupResult));
    }

    public void listGroupsForUser(String userName) {
        System.out.println("########################### List Groups for User ###########################");

        ListGroupsForUserRequest listGroupsForUserRequest = new ListGroupsForUserRequest();
        listGroupsForUserRequest.setUserName(userName);

        ListGroupsForUserResult listGroupsForUserResult = amazonIdentityManagement.listGroupsForUser(listGroupsForUserRequest);
        System.out.println(gson.toJson(listGroupsForUserResult));
    }

    public void removeUserFromGroup(String userName, String groupName) {
        System.out.println("########################### Remove User from Group ###########################");
        RemoveUserFromGroupRequest removeUserFromGroupRequest = new RemoveUserFromGroupRequest();
        removeUserFromGroupRequest.setUserName(userName);
        removeUserFromGroupRequest.setGroupName(groupName);

        RemoveUserFromGroupResult removeUserFromGroupResult = amazonIdentityManagement.removeUserFromGroup(removeUserFromGroupRequest);
        System.out.println(gson.toJson(removeUserFromGroupResult));
    }

    public void attachUserPolicy(String userName, String policyARN) {
        System.out.println("########################### Attach User Policy ###########################");
        AttachUserPolicyRequest attachUserPolicyRequest = new AttachUserPolicyRequest();
        attachUserPolicyRequest.setUserName(userName);
        attachUserPolicyRequest.setPolicyArn(policyARN);

        AttachUserPolicyResult attachUserPolicyResult = amazonIdentityManagement.attachUserPolicy(attachUserPolicyRequest);
        System.out.println(gson.toJson(attachUserPolicyResult));
    }

    public void listAttachedUserPolicies(String userName) {
        System.out.println("########################### List Attached User Policies ###########################");
        ListAttachedUserPoliciesRequest listAttachedUserPoliciesRequest = new ListAttachedUserPoliciesRequest();
        listAttachedUserPoliciesRequest.setUserName(userName);

        ListAttachedUserPoliciesResult listAttachedUserPoliciesResult = amazonIdentityManagement.listAttachedUserPolicies(listAttachedUserPoliciesRequest);
        System.out.println(gson.toJson(listAttachedUserPoliciesResult));
    }

    public void detachUserPolicy(String userName, String policyARN) {
        System.out.println("########################### Detach User Policy ###########################");
        DetachUserPolicyRequest detachUserPolicyRequest = new DetachUserPolicyRequest();
        detachUserPolicyRequest.setUserName(userName);
        detachUserPolicyRequest.setPolicyArn(policyARN);

        DetachUserPolicyResult detachUserPolicyResult = amazonIdentityManagement.detachUserPolicy(detachUserPolicyRequest);
        System.out.println(gson.toJson(detachUserPolicyResult));
    }

    public void deleteUser(String userName) {
        System.out.println("########################### Delete User ###########################");
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
        deleteUserRequest.setUserName(userName);

        DeleteUserResult deleteUserResult = amazonIdentityManagement.deleteUser(deleteUserRequest);
        System.out.println(gson.toJson(deleteUserResult));
    }

    private String readFromFile(String fileName) throws URISyntaxException, IOException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return new String(Files.readAllBytes(path));
    }
}
