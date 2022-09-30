package ru.tokarev.entity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;

import javax.persistence.*;
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
    @SequenceGenerator(name = "item_serial_number_seq", sequenceName = "item_serial_number_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_serial_number_seq")
    @Column(name = "serial_number")
    private Long id;

    @Column(name = "price")
    private Integer price;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "marketplace_id")
    private Marketplace marketplace;
}
