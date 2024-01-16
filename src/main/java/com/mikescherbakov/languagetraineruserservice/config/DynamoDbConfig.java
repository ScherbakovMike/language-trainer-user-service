package com.mikescherbakov.languagetraineruserservice.config;

import customer.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@RequiredArgsConstructor
@Configuration
public class DynamoDbConfig {

  public static final String USER_TABLE_NAME = "users";

  @Bean
  public DynamoDbAsyncClient dynamoDbAsyncClient() {
    var region = Region.US_EAST_1;
    return DynamoDbAsyncClient.builder()
        .region(region)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }

  @Bean
  public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient() {
    return DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbAsyncClient())
        .build();
  }

  @Bean
  public DynamoDbAsyncTable<User> userTable(DynamoDbEnhancedAsyncClient dynamoDbClient) {
    return dynamoDbClient.table(USER_TABLE_NAME, TableSchema.fromBean(User.class));
  }
}