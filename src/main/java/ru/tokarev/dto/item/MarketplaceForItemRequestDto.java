package ru.tokarev.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceForItemRequestDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Pattern(regexp = "^[a-zA-Z]*$")
    private String name;

}
