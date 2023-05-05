package guru.sfg.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import common.model.BeerDto;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.services.beer.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;


@ExtendWith(WireMockExtension.class)
@SpringBootTest
@MockBean(TastingRoomService.class)
public class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    Customer testCustomer;

    UUID beerId = UUID.randomUUID();

    @Autowired
    WireMockServer wireMockServer;

    private final UriTemplate beerByUpcUriTemplate =  new UriTemplate(BeerServiceImpl.BEER_UPC_PATH_V1);
    private final Map<String, String> uriVariables = new HashMap<>();
    private URI beerUri;

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        @Bean(destroyMethod = "stop")

        public WireMockServer wireMockServer(){
            WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8083));
            wireMockServer.start();
            return wireMockServer;
        }
    }

    @BeforeEach
    void setUp(){
        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());
    }


    @Test
    void newToAllocated() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        uriVariables.put("upc" , "12345");
        beerUri = beerByUpcUriTemplate.expand(uriVariables);
        var url = beerUri.toString();
        wireMockServer.stubFor(get(url)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        final BeerOrder beerOrder = createBeerOrder();
         BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
            assertThat(foundOrder).isNotNull();
            assertThat(foundOrder.getOrderStatus()).isEqualTo(BeerOrderStatusEnum.ALLOCATED);
        });

        await().untilAsserted(()-> {
            BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
           Objects.requireNonNull(foundOrder).getBeerOrderLines().forEach(beerOrderLine -> {
               assertThat(beerOrderLine.getOrderQuantity()).isEqualTo(beerOrderLine.getQuantityAllocated());
           });
        });

    }



    public BeerOrder createBeerOrder(){
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());
        beerOrder.setBeerOrderLines(lines);

        return beerOrder;
    }

}
