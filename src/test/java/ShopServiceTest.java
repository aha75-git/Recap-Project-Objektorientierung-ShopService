import exception.OrderNotFoundException;
import exception.ProductOutOfStock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() throws OrderNotFoundException, ProductOutOfStock {
        //GIVEN
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);
        //List<String> productsIds = List.of("1");
        Map<String, Double> productsIdsWithQuantity = Map.of("1", 8.0);

        //WHEN
        productRepo.addProduct(new Product("1", "Apfel", 10));
        Order actual = shopService.addOrder(productsIdsWithQuantity);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel", 2)), OrderStatus.PROCESSING, Instant.now());
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectNull() {
        //GIVEN
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);
        //List<String> productsIds = List.of("1", "2");
        Map<String, Double> productsIdsWithQuantity = Map.of("1", 8.0, "2", 5.0);

        //WHEN

        //THEN
        //assertNull(actual);
        try {
            Order actual = shopService.addOrder(productsIdsWithQuantity);
            fail();
        } catch (OrderNotFoundException | ProductOutOfStock e) {
            assertTrue(true);
        }
    }

    @Test
    void getOrdersByStatusTest() throws OrderNotFoundException, ProductOutOfStock {
        //GIVEN
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);
        //List<String> productsIds = List.of("1");
        Map<String, Double> productsIdsWithQuantity1 = Map.of("1", 8.0,  "2", 5.0, "3", 2.0);
        Map<String, Double> productsIdsWithQuantity2 = Map.of("2", 1.0, "3", 1.0);

        //WHEN
        productRepo.addProduct(new Product("1", "Apfel", 10));
        productRepo.addProduct(new Product("2", "Birne", 12));
        productRepo.addProduct(new Product("3", "Mango", 7));
        Order actual1 = shopService.addOrder(productsIdsWithQuantity1);
        Order actual2 = shopService.addOrder(productsIdsWithQuantity2);

        //THEN
        //Order expected = new Order("-1", List.of(new Product("1", "Apfel", 2)), OrderStatus.PROCESSING, Instant.now());
        List<Order> orders = shopService.getOrdersByStatus(OrderStatus.PROCESSING);
        assertEquals(2, orders.size());
    }

    @Test
    void getOldestOrderPerStatusTest() throws OrderNotFoundException, ProductOutOfStock, InterruptedException {
        //GIVEN
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);
        //List<String> productsIds = List.of("1");
        Map<String, Double> productsIdsWithQuantity1 = Map.of("1", 8.0,  "2", 5.0, "3", 2.0);
        Map<String, Double> productsIdsWithQuantity2 = Map.of("2", 1.0, "3", 1.0);
        Map<String, Double> productsIdsWithQuantity3 = Map.of("1", 1.0, "2", 1.0);

        //WHEN
        productRepo.addProduct(new Product("1", "Apfel", 10));
        productRepo.addProduct(new Product("2", "Birne", 12));
        productRepo.addProduct(new Product("3", "Mango", 7));
        Order actual1 = shopService.addOrder(productsIdsWithQuantity1);
        Thread.sleep(2000);
        Order actual2 = shopService.addOrder(productsIdsWithQuantity2);
        Thread.sleep(2000);
        Order actual3 = shopService.addOrder(productsIdsWithQuantity3);

        //THEN
        //Order expected = new Order("-1", List.of(new Product("1", "Apfel", 2)), OrderStatus.PROCESSING, Instant.now());
        Map<OrderStatus, Order> oldestOrderPerStatusMap = shopService.getOldestOrderPerStatus();
        System.out.println(oldestOrderPerStatusMap);
        assertEquals(actual1, oldestOrderPerStatusMap.get(OrderStatus.PROCESSING));
    }
}
