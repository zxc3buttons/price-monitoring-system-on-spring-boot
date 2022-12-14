package ru.tokarev.dto.productdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryForProductRequestDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull
    private Long id;

    @NotBlank(message = "name is mandatory")
    private String name;

}
