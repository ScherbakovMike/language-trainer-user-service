package com.mikescherbakov.languagetraineruserservice.repository;

import customer.User;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final DynamoDbAsyncTable<User> userTable;

  public Flux<User> findAll() {
    return Flux.from(userTable.scan().items());
  }

  public Mono<User> findById(String id) {
    return Mono.fromFuture(userTable.getItem(getKeyBuild(id)));
  }

  public Mono<User> delete(String id) {
    return Mono.fromCompletionStage(userTable.deleteItem(getKeyBuild(id)));
  }

  public Mono<Integer> count() {
    ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
        .addAttributeToProject("id").build();
    AtomicInteger counter = new AtomicInteger(0);
    return Flux.from(userTable.scan(scanEnhancedRequest))
        .doOnNext(page -> counter.getAndAdd(page.items().size()))
        .then(Mono.defer(() -> Mono.just(counter.get())));
  }

  public Mono<User> update(User entity) {
    var updateRequest = UpdateItemEnhancedRequest.builder(User.class).item(entity).build();
    return Mono.fromCompletionStage(userTable.updateItem(updateRequest));
  }

  public Mono<User> save(User entity) {
    var newEntity = new User(UUID.randomUUID().toString(), entity.getFirstName(),
        entity.getLastName());
    var putRequest = PutItemEnhancedRequest.builder(User.class).item(newEntity).build();
    return Mono.fromCompletionStage(userTable.putItem(putRequest).thenApply(x -> newEntity));
  }

  private Key getKeyBuild(String id) {
    return Key.builder().partitionValue(id).build();
  }
}
