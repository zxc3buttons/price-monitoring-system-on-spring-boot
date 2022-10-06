package ru.tokarev.dto.userdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.dto.RoleDto;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "username is mandatory")
    private String username;

    @NotBlank(message = "first name is mandatory")
    @Pattern(regexp = "^[a-zA-Z]*$")
    private String firstName;

    @NotBlank(message = "last name is mandatory")
    @Pattern(regexp = "^[a-zA-Z]*$")
    private String lastName;

    @NotBlank(message = "email is mandatory")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "password is mandatory")
    @Size(min=8, max=16, message="password length is from 8 to 16")
    private String password;

    @JsonProperty(value = "role")
    @NotNull
    @Valid
    private RoleDto roleDto;
}
