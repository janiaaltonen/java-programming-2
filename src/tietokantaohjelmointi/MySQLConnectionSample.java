package tietokantaohjelmointi;
/**
 * Simple MySQL program to perform create, read and delete operations
 * MySQL used over SQLite, because I need to learn MySQL for my internship
 * program doesn't check duplicate rows in db_table so one can have e.g. two rows of eggs
 * query parameters are not case sensitive e.g. "remove milk" and "remove MILK" results the same
 * shopping list cannot contain two rows of same product name
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class MySQLConnectionSample {

    private static boolean isShopping = true;
    private static final String URL = "jdbc:mysql://localhost:3306/java_example";
    private static final String DB_USER = "java";
    private static final String DB_PW = "java";


    static class Product {
        int id;
        String title;

        public Product (int id, String title){
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the shopping list app!");
        System.out.println("Available commands: \n list \n add [product name] \n remove [product name] \n help \n quit \n");

        MySQLConnectionSample.runProgram();
    }

    /* Method runs the loop until user decide to quit the program */
    private static void runProgram(){
        Scanner scanner = new Scanner(System.in);
        while (isShopping){
            System.out.print("> ");
            String command = scanner.next();
            String parameter = scanner.nextLine();
            MySQLConnectionSample.handleCommand(command, parameter.trim());
        }
    }

    /* Method handles user input by calling right method or prints appropriate text/info to user */
    private static void handleCommand(String command, String parameter){
        switch (command) {
            case "list":
                List<Product> products = MySQLConnectionSample.selectQuery();
                System.out.println("\nShopping list contents:");
                for (Product p : products) {
                    System.out.println("("+ p.getId() +") " + p.getTitle());
                }
                System.out.println();
                break;
            case "add":
                if (MySQLConnectionSample.insertOrDeleteQuery(command, parameter)) {
                    System.out.println("\nSuccessfully added " + parameter + "\n");
                } else {
                    System.out.println("Could not add " + parameter + ". List might contain it already.");
                }
                break;
            case "remove":
                if (MySQLConnectionSample.insertOrDeleteQuery(command, parameter)) {
                    System.out.println("\nSuccessfully removed " + parameter + "\n");
                } else {
                    System.out.println("Could not remove " + parameter);
                }
                break;
            case "help":
                System.out.println("\nWelcome to the shopping list app!");
                System.out.println("Available commands: \n list \n add [product name] \n remove [product name] \n help \n quit \n");
                break;
            case "quit":
                isShopping = false;
                System.out.println("\nBye!");
                break;
            default:
                System.out.println("Unrecognised command " + "\"" + command + "\". Type \"help\" for help.");
                break;
        }
    }

    /* query all the rows from db. Makes each row a Product and adds them to productsList */
    private static List<Product> selectQuery(){
        List<Product> products = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PW);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM shopping_list_item");
            while(rs.next()) {
                String title = rs.getString("title");
                int titleLength = title.length();
                String titleWithCapitalStart = title.substring(0, 1).toUpperCase() + title.substring(1, titleLength);
                Product product = new Product(rs.getInt("id"), titleWithCapitalStart);
                products.add(product);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return products;
    }

    /* Method checks first the command. Then adds new row to db if the parameter doesn't exist in db_table
     or deletes existing row if parameter is in db_table.
     Returns true if operation was successful and false if it wasn't */
    private static boolean insertOrDeleteQuery(String command, String parameter) {
        boolean successful = false;
        try {
            Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PW);
            PreparedStatement pstmt;
            int rowsAffected = 0;
            if (command.equals("add")) {
                String sql = "SELECT * FROM shopping_list_item WHERE title = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, parameter.toLowerCase());
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    sql = "INSERT INTO shopping_list_item(title) VALUES(?)";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, parameter.toLowerCase());
                    rowsAffected = pstmt.executeUpdate();
                }
            } else if (command.equals("remove")) {
                String sql = "DELETE FROM shopping_list_item WHERE title = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, parameter.toLowerCase());
                rowsAffected = pstmt.executeUpdate();
            }
            if (rowsAffected > 0){
                successful = true;
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return successful;
    }
}
