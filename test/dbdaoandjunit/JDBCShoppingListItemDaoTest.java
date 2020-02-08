package dbdaoandjunit;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JDBCShoppingListItemDaoTest {

    private JDBCShoppingListItemDao listItems = new JDBCShoppingListItemDao();

    @BeforeEach
    public void setUp() throws SQLException {
        Connection conn = listItems.getConnection();
        conn.prepareStatement("DELETE FROM shopping_list").executeUpdate();
        conn.prepareStatement("INSERT INTO shopping_list(id, title) VALUES(1, 'Milk'), (2, 'Eggs')").executeUpdate();
        conn.close();
    }

    @Test
    void allTitlesInShoppingList() {
        List<String> titles = new ArrayList<>();
        for (ShoppingListItem item : listItems.getAllItems()) {
            titles.add(item.getTitle());
        }
        List<String> expected = Arrays.asList("Milk", "Eggs");
        assertLinesMatch(expected, titles);
    }

    @Test
    void NumberOfItemsInShoppingList(){
        int numOfItems = listItems.getAllItems().size();
        assertEquals(2, numOfItems);
    }

    @Test
    void getItemTitleById() {
        String title = listItems.getItem(1).getTitle();
        assertEquals("Milk", title);
    }

    @Test
    void getItemIdById(){
        long id = listItems.getItem(2).getId();
        assertEquals(2, id);
    }

    @Test
    void getItemTitleByIndex() {
        String secondTitle = listItems.getItemByIndex(2).getTitle();
        assertEquals("Eggs", secondTitle);
    }

    @Test
    void addSuccessfullyNewItemsToShoppingList() {
        ShoppingListItem item = new ShoppingListItem(1, "Pasta");
        boolean successful = listItems.addItem(item);
        assertTrue(successful);

        ShoppingListItem item2 = new ShoppingListItem(1500, "Bread");
        boolean successful2 = listItems.addItem(item2);
        assertTrue(successful2);
    }

    @Test
    void cannotAddNewItemToShoppingListWithExistingTitle() {
        ShoppingListItem item = new ShoppingListItem(10, "Eggs");
        boolean successful = listItems.addItem(item);
        assertFalse(successful);

        ShoppingListItem item2 = new ShoppingListItem(1, "Pasta");
        boolean successful2 = listItems.addItem(item2);
        assertTrue(successful2);

        ShoppingListItem item3 = new ShoppingListItem(2, "Pasta");
        boolean successful3 = listItems.addItem(item3);
        assertFalse(successful3);
    }
    @Test
    void SuccessfullyRemoveItemFromShoppingList() {
        ShoppingListItem item = new ShoppingListItem(0, "Eggs");
        boolean successful = listItems.removeItem(item);
        assertTrue(successful);
    }

    @Test
    void UnSuccessfullyRemoveItemFromShoppingList() {
        ShoppingListItem item = new ShoppingListItem(0, "Pasta");
        boolean successful = listItems.removeItem(item);
        assertFalse(successful);
    }

    @Test
    void tryToRemoveTwoTimesSameItemFromShoppingList() {
        boolean successful;
        ShoppingListItem item = new ShoppingListItem(0, "Eggs");
        successful = listItems.removeItem(item);
        assertTrue(successful);
        ShoppingListItem item2 = new ShoppingListItem(0, "Eggs");
        successful = listItems.removeItem(item2);
        assertFalse(successful);
    }

}