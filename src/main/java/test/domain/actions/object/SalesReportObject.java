package test.domain.actions.object;

import java.time.LocalDate;

public class SalesReportObject extends ActionObject {
    private LocalDate date;

    public SalesReportObject(String name, LocalDate date) {
        super(name);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "{ date=" + date +
                ", name='" + name + '\'' +
                '}';
    }
}
