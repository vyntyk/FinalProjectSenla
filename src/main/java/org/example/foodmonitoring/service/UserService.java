package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.UserRequest;
import org.example.foodmonitoring.dto.UserResponse;
import org.example.foodmonitoring.entity.User;
import org.example.foodmonitoring.exception.UserNotFoundException;
import org.example.foodmonitoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Создание пользователя
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getName());
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    // Получение всех пользователей
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Удаление пользователя по ID
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Редактирование пользователя по ID
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(userRequest.getName());
            User updatedUser = userRepository.save(user);
            return mapToResponse(updatedUser);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    // Преобразование сущности Users в DTO UserResponse
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }

    public void createUser(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userRepository.save(user);
        }
    }

    public void authenticateUser(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userRepository.save(user);
        }
    }
}