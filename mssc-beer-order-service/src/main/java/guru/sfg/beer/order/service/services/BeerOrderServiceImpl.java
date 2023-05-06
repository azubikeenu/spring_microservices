package guru.sfg.beer.order.service.services;

import common.model.BeerOrderDto;
import common.model.BeerOrderPagedList;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

  private final BeerOrderRepository beerOrderRepository;
  private final CustomerRepository customerRepository;
  private final BeerOrderMapper beerOrderMapper;

  private final BeerOrderManager beerOrderManager;

  @Override
  // This lists all orders by a particular customer
  public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
    Optional<Customer> customerOptional = customerRepository.findById(customerId);

    if (customerOptional.isPresent()) {
      Page<BeerOrder> beerOrderPage =
          beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

      // Page number of beerOrders List
      final int pageNumber = beerOrderPage.getPageable().getPageNumber();


      // Page size of beerOrders List
      final int pageSize = beerOrderPage.getPageable().getPageSize();

      // BeerOrderDto collection
      final List<BeerOrderDto> content =
          beerOrderPage.stream().map(beerOrderMapper::beerOrderToDto).collect(Collectors.toList());

      // create a PagedList
      return new BeerOrderPagedList(
          content, PageRequest.of(pageNumber, pageSize), beerOrderPage.getTotalElements());

    } else {
      return null;
    }
  }

  @Transactional
  @Override
  public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
    Optional<Customer> customerOptional = customerRepository.findById(customerId);

    if (customerOptional.isPresent()) {
      BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
      beerOrder.setId(null); // should not be set by outside client
      beerOrder.setCustomer(customerOptional.get());
      beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
      // create a bidirectional relationship
      beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
      final BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
      log.debug("Saved Beer Order: " + savedBeerOrder.getId());
      return beerOrderMapper.beerOrderToDto(savedBeerOrder);
    }
    System.out.println("############# I RAN");
    // todo add exception type
    throw new RuntimeException("Customer Not Found");
  }

  @Override
  public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
    return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
  }

  @Override
  public void pickupOrder(UUID customerId, UUID orderId) {
      beerOrderManager.beerOrderPickedUp(orderId);
  }

  private BeerOrder getOrder(UUID customerId, UUID orderId) {
    Optional<Customer> customerOptional = customerRepository.findById(customerId);

    if (customerOptional.isPresent()) {
      Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

      if (beerOrderOptional.isPresent()) {
        BeerOrder beerOrder = beerOrderOptional.get();

        // fall to exception if customer id's do not match - order not for customer
        if (beerOrder.getCustomer().getId().equals(customerId)) {
          return beerOrder;
        }
      }
      throw new RuntimeException("Beer Order Not Found");
    }
    throw new RuntimeException("Customer Not Found");
  }
}
