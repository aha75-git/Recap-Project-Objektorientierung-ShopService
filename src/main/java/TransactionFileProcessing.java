import exception.OrderNotFoundException;

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
                } catch (OrderNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } );

    }

    private void processCommand(String command) throws OrderNotFoundException {
        String[] split = command.split(" ");
        switch (split[0]) {
            case "addOrder":
                this.addOrder(split);
                break;
            case "setStatus":
                String status = split[1];
                this.setStatus(split);
                break;
            case "printOrders":
                this.printOrders();
                break;
        }
    }

    private void addOrder(String[] split) throws OrderNotFoundException {
        String alias = split[1];
        List<String> productIds = new ArrayList<>(Arrays.asList(split).subList(2, split.length));
        this.orderMap.put(alias, this.shopService.addOrder(productIds));
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
