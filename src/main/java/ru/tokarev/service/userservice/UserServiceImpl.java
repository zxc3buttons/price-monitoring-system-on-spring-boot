package ru.tokarev.service.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.entity.Role;
import ru.tokarev.entity.User;
import ru.tokarev.exception.userexception.UserBadRequestException;
import ru.tokarev.exception.userexception.UserExistsException;
import ru.tokarev.exception.userexception.UserNotFoundException;
import ru.tokarev.repository.RoleRepository;
import ru.tokarev.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with this id not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAll() {

        List<User> userList = Optional.of(userRepository.findAll()).orElseThrow(
                () -> new UserNotFoundException("Users not found"));

        if (userList.size() == 0) {
            throw new UsernameNotFoundException("Users not found");
        }

        return userList;
    }

    @Override
    @Transactional
    public User createUser(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserExistsException("User with this username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserExistsException("User with this email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = new Role();
        Long ROLE_UNDEFINED = 103L;
        role.setId(ROLE_UNDEFINED);
        role.setName("ROLE_UNDEFINED");
        user.setRole(role);

        user.setCreated(new Date());
        user.setUpdated(new Date());
        return Optional.of(userRepository.save(user)).orElseThrow(() -> new UserBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User with this id not found"));

        if (existsByUsername(user.getUsername()) && !user.getUsername().equals(existingUser.getUsername())) {
            throw new UserExistsException("Username already exists");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        if (existsByEmail(user.getEmail()) && !user.getEmail().equals(existingUser.getEmail())) {
            throw new UserExistsException("Email already exists");
        }

        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setUpdated(new Date());

        return Optional.of(userRepository.save(existingUser)).orElseThrow(
                () -> new UserBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public User updateUserRole(Long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with this id not found"));

        if (roleRepository.findById(user.getRole().getId()).isEmpty()) {
            throw new UserBadRequestException("Role with this id not found");
        }

        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(
                () -> new UserBadRequestException("Bad request"));

        existingUser.setRole(role);
        existingUser.setUpdated(new Date());

        return Optional.of(userRepository.save(existingUser)).orElseThrow(
                () -> new UserBadRequestException("Bad request"));

    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with this id not found")
        );

        userRepository.deleteById(user.getId());
    }

    private boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
