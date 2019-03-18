package test.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.domain.Result;
import test.domain.actions.object.DemandObject;
import test.domain.actions.object.NewProductObject;
import test.domain.actions.object.PurchaseOpbject;
import test.domain.actions.object.SalesReportObject;

import java.sql.*;

public class DBManager {
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);

    private final String DRIVER_NAME = "org.h2.Driver";
    private final String DB_URL = "jdbc:h2:mem:";
    private static Connection conn;

    private final String PURCHASE = "PURCHASE";

    public static DBManager getDBManager() {
        return new DBManager();
    }

    private DBManager() {
        if (conn == null)
            conn = createH2Connection();
    }

    private Connection createH2Connection() {
        try {
            Class.forName(DRIVER_NAME);
            return DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            System.exit(-1);
            return null;
        }
    }

    public void creteTables() {
        String createActionTable = "CREATE TABLE IF NOT EXISTS Action(" +
                "id NUMBER auto_increment ," +
                "name Varchar(200)," +
                "amount NUMBER," +
                "action_kind VARCHAR(20)," +
                "date DATE ," +
                "price DOUBLE," +
                "PRIMARY KEY(id))";
        try {
            Statement createTable = conn.createStatement();
            createTable.execute(createActionTable);

            String createProductTable = "CREATE TABLE IF NOT EXISTS Product(" +
                    "name Varchar(200))";
            createTable.execute(createProductTable);

            String createSaleTable = "CREATE TABLE IF NOT EXISTS Profit(" +
                    "name Varchar(200)," +
                    "profit DOUBLE," +
                    "sale_date DATE)";
            createTable.execute(createSaleTable);
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void dropTables() {
        String dropActionTable = "DROP TABLE IF EXISTS Action";
        try {
            Statement dropTable = conn.createStatement();
            dropTable.execute(dropActionTable);

            String dropProductTable = "DROP TABLE IF EXISTS Product";
            dropTable.execute(dropProductTable);

            String dropSaleTable = "DROP TABLE IF EXISTS Profit";
            dropTable.execute(dropSaleTable);
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Result addProduct(NewProductObject newProductObject) {
        String name = newProductObject.getName();
        try {
            if (isProductExist(name)) return new Result("ERROR");

            String insertProduct = "Insert into product (name) Values(?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertProduct);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            logger.debug("Add new product {}", newProductObject);
            return new Result("OK");
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return new Result("ERROR");
    }

    public Result purchase(PurchaseOpbject purchaseOpbject) {
        try {
            String name = purchaseOpbject.getName();
            int amount = purchaseOpbject.getAmount();
            Date date = Date.valueOf(purchaseOpbject.getDate());
            Double price = purchaseOpbject.getPrice();

            if (!isProductExist(name)) return new Result("ERROR");

            if (!isDateValid(date, name)) return new Result("ERROR");

            String insertPurchase = "Insert into  Action (name, amount, action_kind, date, price) " +
                    "Values (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(insertPurchase);
            statement.setString(1, name);
            statement.setInt(2, amount);
            statement.setString(3, PURCHASE);
            statement.setDate(4, date);
            statement.setDouble(5, price);
            statement.executeUpdate();

            logger.debug("Purchase product {}", purchaseOpbject);
            return new Result("OK");
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return new Result("ERROR");
    }

    public Result demand(DemandObject demandObject) {
        try {
            String name = demandObject.getName();
            int amount = demandObject.getAmount();
            Date date = Date.valueOf(demandObject.getDate());
            Double price = demandObject.getPrice();

            if (!isProductExist(name)) return new Result("ERROR");
            if (!isAmountValid(amount, date)) return new Result("ERROR");

            double profit = demand(amount, date, price);

            insetIntoSaleTable(name, profit, date);

            logger.debug("Demand product {}", demandObject);
            return new Result("OK");
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return new Result("ERROR");
    }

    public Result salesReport(SalesReportObject salesReportObject) {
        try {
            String name = salesReportObject.getName();
            Date date = Date.valueOf(salesReportObject.getDate());

            String selectProfit = "SELECT SUM(profit) FROM Profit WHERE name = ? and sale_date <= ?";
            PreparedStatement statement = conn.prepareStatement(selectProfit);
            statement.setString(1, name);
            statement.setDate(2, date);
            ResultSet profit = statement.executeQuery();

            double salesReport = 0;
            while (profit.next()) {
                salesReport = profit.getDouble(1);
            }

            logger.debug("SalesReport {}", salesReportObject);
            return new Result(Double.toString(salesReport));
        } catch (SQLException ex) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return new Result("ERROR");
    }

    /**
     * проверяет продавали ли товар с указанным именем раньше введённой даты
     */
    private boolean isDateValid(Date date, String name) throws SQLException {
        String selectMinDate = "SELECT MIN(date) FROM Action WHERE name = ?";
        PreparedStatement stat = conn.prepareStatement(selectMinDate);
        stat.setString(1, name);
        ResultSet minDate = stat.executeQuery();
        Date min = null;
        while (minDate.next()) {
            if (minDate.getDate(1) == null) return true;
            min = minDate.getDate(1);
        }

        return !date.before(min);
    }

    private boolean isAmountValid(int amount, Date date) throws SQLException {
        String selectAmount = "SELECT SUM(amount) FROM Action WHERE action_kind = 'PURCHASE' and date < ?";
        PreparedStatement statement = conn.prepareStatement(selectAmount);
        statement.setDate(1, date);
        ResultSet amountSet = statement.executeQuery();
        while (amountSet.next()) {
            if (amountSet.getInt(1) < amount) return false;
        }

        return true;
    }

    private boolean isProductExist(String name) throws SQLException {
        String selectProduct = "Select * FROM Product WHERE name = ?";
        PreparedStatement statement = conn.prepareStatement(selectProduct);
        statement.setString(1, name);
        ResultSet productSet = statement.executeQuery();

        return productSet.isBeforeFirst();
    }

    private double demand(int amount, Date date, double price) throws SQLException {
        String selectPurchase = "SELECT * FROM Action WHERE action_kind = 'PURCHASE' and date < ?";
        PreparedStatement purchaseStatement = conn.prepareStatement(selectPurchase);
        purchaseStatement.setDate(1, date);

        Statement updateDbStatments = conn.createStatement();
        double revenue = 0;
        int amountToSale = 0;
        ResultSet purhaseSet = purchaseStatement.executeQuery();

        while (purhaseSet.next()) {
            conn.setAutoCommit(false);
            int id = purhaseSet.getInt(1);
            amountToSale += purhaseSet.getInt(3);//число проданных товаров
            if (amount - amountToSale >= 0) {
                revenue += purhaseSet.getInt(3) * purhaseSet.getDouble(6);//amount *  price
                String update = "DELETE FROM Action WHERE id = " + id;
                updateDbStatments.addBatch(update);

                if (amount == amountToSale) break;
            } else if (amount - amountToSale < 0) {//если поле amount в строке не 0, надо обновить его
                revenue += (amount - amountToSale + purhaseSet.getInt(3)) * purhaseSet.getDouble(6);
                String update = "UPDATE Action SET amount = " + (amountToSale - amount) + "WHERE id = " + id;
                updateDbStatments.addBatch(update);
                break;
            }
        }
        updateDbStatments.executeBatch();
        conn.commit();
        double profit = price * amount - revenue;

        return profit;
    }

    private void insetIntoSaleTable(String name, double profit, Date saleDate) throws SQLException {
        String intoSale = "INSERT INTO Profit (name, profit, sale_date) VALUES (?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(intoSale);
        statement.setString(1, name);
        statement.setDouble(2, profit);
        statement.setDate(3, saleDate);
        statement.executeUpdate();
    }
}