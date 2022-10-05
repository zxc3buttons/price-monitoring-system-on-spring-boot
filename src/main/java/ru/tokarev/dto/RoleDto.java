package ru.tokarev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotBlank(message = "Id is mandatory")
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;
}
