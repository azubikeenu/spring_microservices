package common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BeerDto {
    private UUID id;

    private String beerName;

    private Long version;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String upc;
    @JsonFormat(shape = JsonFormat.Shape.STRING)

    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime lastModifiedDate;
    private BeerStyle beerStyle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer quantityOnHand;
}
