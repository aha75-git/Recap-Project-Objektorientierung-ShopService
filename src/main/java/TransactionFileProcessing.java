import exception.OrderNotFoundException;
import exception.ProductOutOfStock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TransactionFileProcessing {
    private final Map<String, Order> orderMap = new HashMap<>();
    private final ShopService shopService;

    public TransactionFileProcessing(ShopService shopService) {
        this.shopService = shopService;
    }

    public void processOrder(String filePath) throws IOException {

            Files.lines(Path.of(filePath)).forEach(command -> {
                try {
                    processCommand(command);
                } catch (OrderNotFoundException | ProductOutOfStock e) {
                    System.out.println(e.getMessage());
                }
            } );

    }

    private void processCommand(String command) throws OrderNotFoundException, ProductOutOfStock {
        String[] split = command.split(" ");
        switch (split[0]) {
            case "addOrder":
                this.addOrder(split);
                break;
            case "setStatus":
                this.setStatus(split);
                break;
            case "printOrders":
                this.printOrders();
                break;
        }
    }

    private void addOrder(String[] split) throws OrderNotFoundException, ProductOutOfStock {
        String alias = split[1];
        Map<String, Double> productIdsWithQuantity = new HashMap<>();
        List<String> productIds = new ArrayList<>(Arrays.asList(split).subList(2, split.length));
        for (String productsOrder : productIds) {
            String[] productArr = productsOrder.split("-");
            productIdsWithQuantity.put(productArr[0], Double.parseDouble(productArr[1]));
        }
        this.orderMap.put(alias, this.shopService.addOrder(productIdsWithQuantity));
    }

    private void setStatus(String[] split) throws OrderNotFoundException {
        String alias = split[1];
        String status = split[2];
        Order order = this.orderMap.get(alias);
        this.shopService.updateOrder(order.id(), Enum.valueOf(OrderStatus.class, status));
    }

    private void printOrders() {
        this.shopService.printOrders();
    }
}
