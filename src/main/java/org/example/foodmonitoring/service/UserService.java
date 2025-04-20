package org.example.foodmonitoring.service;

import jakarta.transaction.Transactional;
import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.dto.UserRequest;
import org.example.foodmonitoring.dto.UserResponse;
import org.example.foodmonitoring.entity.Role;
import org.example.foodmonitoring.entity.User;
import org.example.foodmonitoring.repository.RoleRepository;
import org.example.foodmonitoring.repository.UserRepository;
import org.example.foodmonitoring.security.PasswordEncoderProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderProvider encoderProvider;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoderProvider encoderProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoderProvider = encoderProvider;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoderProvider.getEncoder().encode(request.getPassword()));
        Role role = roleRepository.findByName(request.getRole()).orElseThrow();
        user.setRoles(Set.of(role));
        userRepository.save(user);

        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void register(UserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoderProvider.getEncoder().encode(dto.getPassword()));

        Role role = roleRepository.findByName(dto.getRole()).orElseThrow();
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            userRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoderProvider.getEncoder().encode(request.getPassword()));

        Role role = roleRepository.findByName(request.getRole()).orElseThrow();
        user.setRoles(Set.of(role));

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }
}
