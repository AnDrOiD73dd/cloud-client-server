import db.DBHelper;

import java.sql.SQLException;

public class CloudServer {
    public static void main(String[] args) {
        DBHelper db = new DBHelper();
        try {
            db.openDb();
            db.createTables();
            db.closeDb();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
