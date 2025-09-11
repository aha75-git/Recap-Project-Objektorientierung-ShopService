import exception.OrderNotFoundException;
import exception.ProductOutOfStock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
        Order expected = new Order("-1", List.of(new Product("1", "Apfel", 10)), OrderStatus.PROCESSING, Instant.now());
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
}
