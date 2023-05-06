package guru.sfg.beer.inventory.service.services;

import common.model.BeerOrderDto;
import common.model.BeerOrderLineDto;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
@Slf4j
public class AllocationServiceImpl implements AllocationService {
    private final BeerInventoryRepository beerInventoryRepository;
    @Override
    public Boolean allocateOrder(final BeerOrderDto beerOrderDto) {
        log.debug("Allocating OrderId: " + beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLine -> {
            // only allocate beers whose order quantity is greater than the quantity allocated
            if ((((beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0)
                    - (beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0)) > 0)) {
                allocateBeerOrderLine(beerOrderLine);
            }
            totalOrdered.set(totalOrdered.get() + beerOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + (beerOrderLine.getQuantityAllocated() != null ?
                    beerOrderLine.getQuantityAllocated() : 0));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());
       // only return true when there is a full allocation ie quantity demanded === quantity recieved
        return totalOrdered.get() == totalAllocated.get();

    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLine) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLine.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            // gets the quantity of beer stored in inventory
            int inventory = (beerInventory.getQuantityOnHand() == null) ? 0 : beerInventory.getQuantityOnHand();
            // the order quantity or demanded quantity
            int orderQty = (beerOrderLine.getOrderQuantity() == null) ? 0 : beerOrderLine.getOrderQuantity();
            // the allocated quantity
            int allocatedQty = (beerOrderLine.getQuantityAllocated() == null) ? 0 : beerOrderLine.getQuantityAllocated();
            // quantity demanded - quantity received
            int qtyToAllocate = orderQty - allocatedQty;


            if (inventory >= qtyToAllocate) { // full allocation
                // decrement the inventory by the quantity to allocate
                inventory = inventory - qtyToAllocate;
                // allocate quantity
                beerOrderLine.setQuantityAllocated(orderQty);
                // set the inventory
                beerInventory.setQuantityOnHand(inventory);
                beerInventoryRepository.save(beerInventory);
            } else if (inventory > 0) { //partial allocation
                // since the quantityToAllocate is greater than the quantity in stock , partial allocation
                beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
                // set the quantity in stock to 0
                beerInventory.setQuantityOnHand(0);
            }

            if (beerInventory.getQuantityOnHand() == 0) {
                // remove the inventory record if its zero
                beerInventoryRepository.delete(beerInventory);
            }
        });

    }
}
