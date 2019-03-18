package test.domain.actions.object;

import java.time.LocalDate;

public class DemandObject extends ActionObject {
    private int amount;
    private LocalDate date;
    private Double price;

    public DemandObject(String name, int amount, LocalDate date, Double price) {
        super(name);
        this.amount = amount;
        this.date = date;
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "{ amount=" + amount +
                ", date=" + date +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
