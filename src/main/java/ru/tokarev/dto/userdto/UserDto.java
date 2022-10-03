package ru.tokarev.dto.userdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.dto.RoleDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    @JsonProperty(value = "role", access = JsonProperty.Access.READ_ONLY)
    private RoleDto roleDto;
}
