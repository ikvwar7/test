package test;

import test.db.DBManager;
import test.domain.Action;
import test.domain.action.strategies.ActionStrategy;

import java.util.*;

public class App {
    public static void main(String[] args) {
        dialog();
    }


    private static void dialog() {
        DBManager dbManager = DBManager.getDBManager();
        dbManager.creteTables();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);
            if (!actionStrategy.isPresent()) {
                System.out.println("Введите корректную команду");
                continue;
            }

            Action action = new Action(actionStrategy.get());
            Result result = action.process();
            System.out.println(result);
        }
    }
}