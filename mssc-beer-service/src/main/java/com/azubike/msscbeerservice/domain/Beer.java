package com.azubike.msscbeerservice.domain;

import com.azubike.msscbeerservice.web.model.BeerStyle;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Beer {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
  @Type(type = "uuid-char")
  private UUID id;

  @Version private Long version;

  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdDate;

  @UpdateTimestamp
  @Column(updatable = false)
  private Timestamp lastModifiedDate;

  private String beerName;

  @Enumerated(EnumType.STRING)
  private BeerStyle beerStyle;

  @Column(unique = true)
  private String upc;

  private BigDecimal price;


  private Integer minOnHand;

  private Integer quantityToBrew;
}
