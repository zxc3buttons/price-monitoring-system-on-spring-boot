package ru.tokarev.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    @NotBlank(message = "username is mandatory")
    private String username;

    @NotBlank(message = "first name is mandatory")
    private String firstName;

    @NotBlank(message = "last name is mandatory")
    private String lastName;

    @NotBlank(message = "email is mandatory")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "password is mandatory")
    @Size(min=8, max=16, message="password length is from 8 to 16")
    private String password;
}
