package com.mikescherbakov.languagetraineruserservice.dto;

import customer.User;
import java.util.List;

public class ApiResponse {

  public ApiResponse(User user, String comment) {
  }

  public ApiResponse(List<User> users, String format) {
  }

  public ApiResponse(Integer count, String format) {
  }

}
