package guru.sfg.beer.order.service.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BeerDto {
  private UUID id;

  @NotBlank
  private String beerName;

  private Long version;

  @NotBlank
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private String upc;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Positive
  @NotNull
  private BigDecimal price;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  private OffsetDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  private OffsetDateTime lastModifiedDate;

  @NotNull
  private BeerStyle beerStyle;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer quantityOnHand;
}
