package dbdaoandjunit;


import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCShoppingListItemDao implements ShoppingListItemDao {

    private List<ShoppingListItem> items;
    private long defaultId;

    public JDBCShoppingListItemDao(){
        this.items = new ArrayList<>();
        this.defaultId = 0;
    }

    public long getDefaultId() {
        return defaultId;
    }

    protected Connection getConnection() throws SQLException {
        String url = System.getenv("URL_DB");
        String dbUser = System.getenv("USER_DB").split(":")[0];
        String dbPw = System.getenv("USER_DB").split(":")[1];
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUrl(url);
        return ds.getConnection(dbUser, dbPw);
    }

    @Override
    public List<ShoppingListItem> getAllItems() {
        items.clear();
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM shopping_list");
            while(rs.next()) {
                String title = rs.getString("title");
                int titleLength = title.length();
                String titleWithCapitalStart = title.substring(0, 1).toUpperCase() + title.substring(1, titleLength);
                ShoppingListItem item = new ShoppingListItem(rs.getLong("id"), titleWithCapitalStart);
                items.add(item);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return items;
    }

    /* could've been implemented like getAllItems() method with WHERE id = ? clause.
     * Especially in case, when db table includes lots of rows because it's not efficient to get them all
     * but had to try Stream API, which is new to me
     */
    @Override
    public ShoppingListItem getItem(long id) {
        List<ShoppingListItem> listItems = getAllItems();

        return listItems.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    /*
     *returns the corresponding item from the chosen index
     * other way to do this is get the full list and then with [list].get(index-1) to get matching item.
     * Idea of this method is that second item's id could be something else than 2 if items are added or removed
     */
    @Override
    public ShoppingListItem getItemByIndex(int index) {
        long id = 0;
        String titleWithCapitalStart = null;
        try {
            Connection conn = getConnection();
            PreparedStatement pStmt;
            String sql = "SELECT * FROM shopping_list LIMIT ? OFFSET ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, index);
            int offset = index -1;
            pStmt.setInt(2, offset);
            ResultSet rs = pStmt.executeQuery();
            while(rs.next()) {
                id = rs.getLong("id");
                String title = rs.getString("title");
                int titleLength = title.length();
                titleWithCapitalStart = title.substring(0, 1).toUpperCase() + title.substring(1, titleLength);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        if (titleWithCapitalStart != null) {
            return new ShoppingListItem(id, titleWithCapitalStart);
        }
        return null;
    }

    @Override
    public boolean addItem(ShoppingListItem newItem) {
        boolean successful = false;
        try {
            Connection conn = getConnection();
            PreparedStatement pStmt;
            int rowsAffected = 0;
            String sql = "SELECT * FROM shopping_list WHERE title = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, newItem.getTitle().toLowerCase());
            ResultSet rs = pStmt.executeQuery();
            if (!rs.next()) {
                sql = "INSERT INTO shopping_list(title) VALUES(?)";
                pStmt = conn.prepareStatement(sql);
                pStmt.setString(1,newItem.getTitle());
                //pStmt.setString(2,newItem.getTitle().toLowerCase());
                rowsAffected = pStmt.executeUpdate();
            }
            if (rowsAffected > 0){
                successful = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return successful;
    }

    @Override
    public boolean removeItem(ShoppingListItem item) {

        boolean successful = false;
        try {
            Connection conn = getConnection();
            PreparedStatement pStmt;
            int rowsAffected = 0;
            String sql = "DELETE FROM shopping_list WHERE title = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, item.getTitle().toLowerCase());
            rowsAffected = pStmt.executeUpdate();

            if (rowsAffected > 0){
                successful = true;
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return successful;
    }
}