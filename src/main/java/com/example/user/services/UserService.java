package com.example.user.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User service, business logic
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void init() {
        if(userRepository.findByAclContaining("ADMIN").isEmpty()) {
            userRepository.save(User.builder().username("admin").password(passwordEncoder.encode("password")).acl(List.of("ADMIN")).build());
        }
    }

    @Cacheable(value = "userCache")
    public List<User> findAll(String name, boolean sortOnBirthday) {
        log.info("-- Request to find all users --");
        log.warn("Fresh data...");
        //return userRepository.findAll();
        var users = userRepository.findAll();
        if(name != null) {
            users = users.stream()
                    .filter(user -> user.getFirstname().startsWith(name) ||
                            user.getLastname().startsWith(name))
                    .collect(Collectors.toList());
        }
        if(sortOnBirthday) {
            //users.sort((user1, user2) -> user1.getBirthday().compareTo(user2.getBirthday()));
            users.sort(Comparator.comparing(User::getBirthday));
        }
        return users;
    }

    public List<User> findAll(String fname, String lname, Boolean hasPet, boolean sortOnBirthday) {
        log.info("-- Request to find all users --");
        var userStream = userRepository.findAll().stream();

        if(fname != null) {
            userStream = userStream.filter(user -> user.getFirstname().contains(fname));
        }
        if(lname != null) {
            userStream = userStream.filter(user -> user.getLastname().contains(lname));
        }
        if(hasPet != null) {
            userStream = userStream.filter(user -> (user.getPet() != null && hasPet) || user.getPet() == null && !hasPet);
        }
        var users = userStream.collect(Collectors.toList());

        if(sortOnBirthday) {
            users.sort(Comparator.comparing(User::getBirthday));
        }
        return users;
    }

    @Cacheable(value = "userCache", key = "#id")
    public User findById(String id) {
        log.warn("Fresh data...");
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                        String.format("Could not find the user by id %s.", id)));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                String.format("Could not find the user by username %s.", username)));
    }

    @CachePut(value = "userCache", key = "#result.id")
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @CachePut(value = "userCache", key = "#id")
    public void update(String id, User user) {
        var isAdmin = checkAuthority("ADMIN");
        var isCurrentUser = SecurityContextHolder.getContext().getAuthentication()
                .getName().toLowerCase().equals(user.getUsername().toLowerCase());
        if(!isAdmin && !isCurrentUser) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only update your own details. Admin can update all users.");
        }
        if(!userRepository.existsById(id)) {
            log.error(String.format("Could not find the user by id %s.", id));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                    String.format("Could not find the user by id %s.", id));
        }
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @CacheEvict(value = "userCache", key = "#id")
    public void delete(String id) {
        if(!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                    String.format("Could not find the user by id %s.", id));
        }
        userRepository.deleteById(id);
    }

    private boolean checkAuthority(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().toUpperCase().equals("ROLE_" + role));
    }
}