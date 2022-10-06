package ru.tokarev.dto.userdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.dto.RoleDto;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleToUpdateRequestDto {

    @JsonProperty(value = "role")
    @NotNull
    private RoleDto roleDto;

}
