package dbdaoandjunit;

import java.util.Scanner;

public class ShoppingListUI {

    private static boolean isShopping = true;

    public static void main(String[] args) {
        System.out.println("Welcome to the shopping list app!");
        System.out.println("Available commands: \n list \n list [id of the item] \n get [number of the item's place in list] \n add [product name] " +
                "\n remove [product name] \n help \n quit \n");

        ShoppingListUI.runShoppingList();
    }

    private static void runShoppingList(){
        Scanner scanner = new Scanner(System.in);
        while (isShopping){
            System.out.print("> ");
            String command = scanner.next();
            String parameter = scanner.nextLine();
            ShoppingListUI.handleInputAndResponse(command, parameter.trim());
        }
    }

    private static void handleInputAndResponse(String command, String parameter){
        JDBCShoppingListItemDao listItems = new JDBCShoppingListItemDao();
        switch (command){
            case "list":
                if (parameter.length() > 0) {
                    if(listItems.getItem(Long.parseLong(parameter)) != null) {
                        System.out.println("\nShopping list content with id " +  "\"" + parameter + "\" is:");
                        System.out.println(listItems.getItem(Long.parseLong(parameter)).getTitle());
                        System.out.println();
                    }
                    else {
                        System.out.println("\nShopping list has no content with id " + "\"" + parameter + "\"" );
                    }
                }
                else {
                    System.out.println("\nShopping list contents:");
                    for (ShoppingListItem item : listItems.getAllItems()) {
                        System.out.println("(" + item.getId() + ") " + item.getTitle());
                    }
                    System.out.println();
                }
                break;
            case "get":
                if (Integer.parseInt(parameter) == 0) {
                    System.out.println("\nShopping list has no " + parameter + ". content");
                    System.out.println();
                    break;
                }
                if (listItems.getItemByIndex(Integer.parseInt(parameter)) != null) {
                    System.out.println(parameter + ". content in the shopping list is:\n" + listItems.getItemByIndex(Integer.parseInt(parameter)).getTitle());
                    System.out.println();
                }
                else {
                    System.out.println("\nShopping list has no " + parameter + ". content");
                }
                break;
            case "add":
                if (listItems.addItem(new ShoppingListItem(listItems.getDefaultId(), parameter))) {
                    System.out.println("\nSuccessfully added " + parameter + "\n");
                } else {
                    System.out.println("Could not add " + parameter + ". Your shopping list might contain it already.");
                }
                break;
            case "remove":
                if (listItems.removeItem(new ShoppingListItem(listItems.getDefaultId(), parameter))) {
                    System.out.println("\nSuccessfully removed " + parameter + "\n");
                } else {
                    System.out.println("Could not remove " + parameter);
                }
                break;
            case "help":
                System.out.println("\nWelcome to the shopping list app!");
                System.out.println("Available commands: \n list \n list [id of the item] \n get [number of the item's place in list] \n add [product name] " +
                        "\n remove [product name] \n help \n quit \n");
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

}
