import exception.OrderNotFoundException;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);

        productRepo.addProduct(new Product("1", "Fussballschuhe"));
        productRepo.addProduct(new Product("2", "T-Shirts"));
        productRepo.addProduct(new Product("3", "Jacken"));
        productRepo.addProduct(new Product("4", "Waschmittel"));
        productRepo.addProduct(new Product("5", "Seife"));
        productRepo.addProduct(new Product("6", "Weichspüler"));

          /*
        Order order1 = new Order(idService.generateId());
        Order order2 = new Order(idService.generateId());
        Order order3 = new Order(idService.generateId());
*/
        List<String> productsIds1 = List.of("1", "2");
        List<String> productsIds2 = List.of("4", "5");
        List<String> productsIds3 = List.of("3", "2", "4", "6");

        try {
            shopService.addOrder(productsIds1);
            shopService.addOrder(productsIds2);
            shopService.addOrder(productsIds3);
        } catch (OrderNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
