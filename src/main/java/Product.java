import lombok.With;

@With
public record Product(
        String id,
        String name,
        double quantity
) {
    public boolean isAvailable(double requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }

    public Product reduceQuantity(double amount) {
        if (isAvailable(amount)) {
            return withQuantity(this.quantity - amount);
        }
        return this;
    }
}
