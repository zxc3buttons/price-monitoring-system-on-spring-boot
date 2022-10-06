package ru.tokarev.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import ru.tokarev.dto.MarketplaceDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @JsonProperty("product")
    @NotNull
    @Valid
    private ProductForItemDto productForItemDto;

    @NotNull(message= "price may not be empty")
    @Range(min = 1)
    private Integer price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dateStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dateEnd;

    @JsonProperty("marketplace")
    @NotNull
    @Valid
    private MarketplaceForItemRequestDto marketplaceForItemRequestDto;
}
