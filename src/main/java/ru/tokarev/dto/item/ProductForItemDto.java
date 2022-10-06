package ru.tokarev.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductForItemDto {

    @NotNull(message= "id may not be empty")
    @Range(min = 1)
    private Long id;

    private String name;

}
