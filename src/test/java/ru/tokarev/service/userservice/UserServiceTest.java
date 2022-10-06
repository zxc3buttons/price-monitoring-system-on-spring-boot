package ru.tokarev.service.userservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenUser_whenGetById_thenReturnUser() {
        //arrange
        User createdUser = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        given(userRepository.findById(1L)).willReturn(Optional.of(createdUser));

        //act
        User user = userService.getById(1L);

        //assert
        assertEquals(user, createdUser);
    }

    @Test
    void givenUsers_whenGetAll_thenReturnUsers() {
        //arrange
        User createdUser1 = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        User createdUser2 = new User(2L, "misha", "Misha",
                "Alikov", "misha@mail.ru", "123qwe",
                new Role(2L, "USER"), new Date(), new Date());
        given(userRepository.findAll()).willReturn(List.of(createdUser1, createdUser2));

        //act
        List<User> userList = userService.getAll();

        //assert
        assertEquals(userList, List.of(createdUser1, createdUser2));
    }

    @Test
    void givenCreatedUser_whenCreateUser_thenReturnCreatedUser() {

        //arrange
        User userToCreate = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L,
                "ROLE_UNDEFINED"),
                new Date(), new Date());
        given(userRepository.save(userToCreate)).willReturn(userToCreate);

        //act
        User user = userService.createUser(userToCreate);

        //assert
        assertEquals(user, userToCreate);

    }

    @Test
    void givenUserToUpdate_whenUpdateUser_thenReturnUpdatedUser() {

        //arrange
        User existingUser = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L, "UNDEFINED"),
                new Date(), new Date());
        User updUser = new User(1L, "olegqwe", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L, "UNDEFINED"),
                new Date(), new Date());
        given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
        given(userRepository.save(existingUser)).willReturn(updUser);

        //act
        User user = userService.updateUser(1L, existingUser);

        //assert
        assertEquals(user, updUser);

    }

    @Test
    void givenUserRoleToUpdate_whenUpdateUserRole_thenReturnUpdatedUserRole() {

        //arrange
        Role role = new Role(2L, "USER");
        User existingUser = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), role,
                new Date(), new Date());

        given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
        given(roleRepository.findById(2L)).willReturn(Optional.of(role));
        given(userRepository.save(existingUser)).willReturn(existingUser);

        //act
        User user = userService.updateUserRole(1L, existingUser);

        //assert
        assertEquals(user, existingUser);
    }

    @Test
    void givenUser_whenDeleteUser_thenNothing() {

        //arrange
        User existingUser = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
        willDoNothing().given(userRepository).deleteById(1L);

        //act
        userService.deleteUser(1L);

        //assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenNothing_whenFindUser_ThrowNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getById(1L);
        });
    }

    @Test
    void givenNothing_whenFindAll_ThrowNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getAll();
        });
    }

    @Test
    void givenNothing_whenUpdateUser_ThrowNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(1L, new User());
        });
    }

    @Test
    void givenNothing_whenUpdateUserRole_ThrowNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserRole(1L, new User());
        });
    }

    @Test
    void givenNothing_whenDeleteUser_ThrowNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });
    }

}