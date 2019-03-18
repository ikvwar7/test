package test;

import org.apache.commons.lang3.StringUtils;

import test.db.DBManager;
import test.domain.action.strategies.*;
import test.domain.actions.object.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ArgumentsParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DBManager dbManager = DBManager.getDBManager();

    public static Optional<ActionStrategy> parse(String command) {

        String[] args = prepareArgs(command);

        switch (args[0]) {
            case "NEWPRODUCT":
                return Optional.ofNullable(createNewProductStrategy(args));
            case "PURCHASE":
                return Optional.ofNullable(createPurchaseDemandStrategy(args));
            case "DEMAND":
                return Optional.ofNullable(createPurchaseDemandStrategy(args));
            case "SALESREPORT":
                return Optional.ofNullable(createSalesReportStrategy(args));
            default:
                return Optional.empty();
        }
    }

    private static String[] prepareArgs(String command) {
        String[] args = command.split(" ");
        for (String arg : args) {
            StringUtils.trim(arg);
            if (arg.isEmpty()) {
                return null;
            }
        }
        return args;
    }

    private static ActionStrategy createNewProductStrategy(String[] args) {
        if (args.length != 2) return null;

        String name = args[1];
        return new NewProductStrategy(new NewProductObject(name), dbManager);
    }

    private static ActionStrategy createPurchaseDemandStrategy(String[] args) {
        if (args.length != 5) return null;

        String name = args[1];
        int amount;
        LocalDate date;
        Double price;

        try {
            amount = Integer.parseInt(args[2]);
            price = Double.parseDouble(args[3]);
            if (price < 0 || amount < 0) return null;

            date = LocalDate.parse(args[4], FORMATTER);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }

        if (args[0].equals("PURCHASE")) {
            return new PurchaseStrategy(new PurchaseOpbject(name, amount, date, price), dbManager);
        } else return new DemandStrategy(new DemandObject(name, amount, date, price), dbManager);
    }

    private static ActionStrategy createSalesReportStrategy(String[] args) {
        if (args.length != 3) return null;

        String name = args[1];
        LocalDate date;
        try {
            date = LocalDate.parse(args[2], FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
        return new SalesReportStrategy(new SalesReportObject(name, date), dbManager);
    }

}

