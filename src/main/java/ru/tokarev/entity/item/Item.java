package ru.tokarev.entity.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
@Entity
public class Item implements Serializable {

    @Id
    @SequenceGenerator(name = "item_id_seq", sequenceName = "item_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "price")
    @NotNull(message= "price may not be empty")
    @Range(min = 1)
    private Integer price;

    @Column(name = "date_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateStart;

    @Column(name = "date_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEnd;

    @NotNull
    @Valid
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Valid
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "marketplace_id")
    private Marketplace marketplace;
}
