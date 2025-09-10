import exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public Order addOrder(List<String> productIds) throws OrderNotFoundException {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new OrderNotFoundException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            products.add(productToOrder.get());
        }

        Order newOrder = new Order(idService.generateId(), products, OrderStatus.PROCESSING, Instant.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return this.orderRepo.getOrders().stream()
                .filter(order -> order.orderStatus().equals(status))
                .toList();
    }

    public void updateOrder(String orderId, OrderStatus orderStatus) throws OrderNotFoundException {
        var order = this.orderRepo.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Bestellung mit der Id: " + orderId + " konnte nicht gefunden werden!");
        }

        this.orderRepo.removeOrder(orderId); // Kompatibilät zur List
        this.orderRepo.addOrder(order.withOrderStatus(orderStatus));
    }

    public Map<OrderStatus, Order> getOldestOrderPerStatus() {
        return this.orderRepo.getOrders().stream()
                .collect(Collectors.groupingBy(Order::orderStatus,
                        Collectors.minBy(Comparator.comparing(Order::orderDate))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().orElse(null)));
    }
}
