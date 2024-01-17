package com.mikescherbakov.languagetraineruserservice.controller;

import com.mikescherbakov.languagetraineruserservice.dto.ApiResponse;
import com.mikescherbakov.languagetraineruserservice.repository.UserRepository;
import customer.User;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository repository;

  @Autowired
  public UserController(UserRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public Mono<ApiResponse> getAllUsers() {
    return repository.findAll()
        .collectList()
        .map(users -> new ApiResponse(users,
            MessageFormat.format("{0} result found", users.size())));
  }

  @GetMapping("/count")
  public Mono<ApiResponse> userCount() {
    return repository.count()
        .map(count -> new ApiResponse(count, MessageFormat.format("Count of Users: {0}", count)));
  }

  @GetMapping("/{id}")
  public Mono<ApiResponse> getByUserId(@PathVariable String id) {
    return repository.findById(id)
        .map(user -> new ApiResponse(user, MessageFormat.format("Result found: {0}", user)))
        .defaultIfEmpty(new ApiResponse(null, "User not found"));
  }

  @PostMapping
  public Mono<ResponseEntity<ApiResponse>> create(@RequestBody Mono<User> user) {
    return user
        .flatMap(repository::save)
        .map(user1 -> ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse(user1, "User successfully created")));
  }

  @PutMapping("/{id}")
  public Mono<ApiResponse> update(@PathVariable String id, @RequestBody Mono<User> user) {
    return user
        .map(user1 -> {
          user1.setId(id);
          return user1;
        })
        .flatMap(repository::update)
        .map(userUpdated -> new ApiResponse(userUpdated, "User successfully updated"));
  }

  @DeleteMapping("/{id}")
  public Mono<ApiResponse> delete(@PathVariable String id) {
    return repository.delete(id)
        .map(userDeleted -> new ApiResponse(userDeleted, "User successfully deleted"));
  }
}
