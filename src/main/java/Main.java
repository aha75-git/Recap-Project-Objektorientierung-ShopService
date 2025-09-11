import exception.OrderNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        OrderRepo orderRepo = new OrderMapRepo();
        ProductRepo productRepo = new ProductRepo();
        IdService idService = () -> UUID.randomUUID().toString();

        productRepo.addProduct(new Product("1", "Fussballschuhe", 7));
        productRepo.addProduct(new Product("2", "T-Shirts", 8));
        productRepo.addProduct(new Product("3", "Jacken", 5));
        productRepo.addProduct(new Product("4", "Waschmittel", 4));
        productRepo.addProduct(new Product("5", "Seife", 12));
        productRepo.addProduct(new Product("6", "Weichspüler", 5));

        ShopService shopService = new ShopService(productRepo, orderRepo, idService);

/*
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
*/
        TransactionFileProcessing fileProcessing = new TransactionFileProcessing(shopService);
        try {
            fileProcessing.processOrder("src/transactions.txt");
        } catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }
}
