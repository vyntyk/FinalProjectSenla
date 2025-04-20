package org.example.foodmonitoring.exception;

public class UserNotFoundException extends Throwable {
    public UserNotFoundException(String id) {
        if (id == null) {
            System.out.println("User not found");
        } else {
            System.out.println("User with id " + id + " not found");
        }
    }
}
