import exception.OrderNotFoundException;
import exception.ProductOutOfStock;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public Order addOrder(Map<String, Double> productIdsWithQuantity) throws OrderNotFoundException, ProductOutOfStock {
        List<Product> products = new ArrayList<>();
        //for (String productId : productIds) {
        for (Map.Entry<String, Double> entry : productIdsWithQuantity.entrySet()) {
            String productId = entry.getKey();
            Double quantity = entry.getValue();

            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new OrderNotFoundException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            Product product = productToOrder.get();
            if (product.isAvailable(quantity)) {
                Product productNew = product.reduceQuantity(quantity);
                productRepo.removeProduct(productId);
                productRepo.addProduct(productNew);
                products.add(productNew);
            } else {
                throw new ProductOutOfStock("Produkt nicht auf Lager");
            }
            //products.add(productToOrder.get());
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

    public void printOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

        this.orderRepo.getOrders().forEach(order -> {
            System.out.println("Order-ID: " + order.id());
            System.out.println("Bestell-Status: " + order.orderStatus());
            System.out.println("Bestelldatum: " + formatter.format(order.orderDate()));
            System.out.println("Produkte:");
            order.products().forEach(product -> System.out.println("\t" + product));
            System.out.println();
            System.out.println("#################################################");
            System.out.println();
        });
    }
}
