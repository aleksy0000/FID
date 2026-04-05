package dbTest;

import db.Db;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DbTest {
    @TempDir
    Path tempDir;
    Db db = createTestDb();

    private Db createTestDb(){
        return new Db("data", "test.db");
    }

    //connect() tests

    @Test
    void connect_returns_open_connection() throws SQLException, ClassNotFoundException {

        try(Connection conn = db.connect()){
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void connect_should_enable_foreign_keys() throws Exception {
        Db db = createTestDb();

        try (Connection conn = db.connect()){
            Statement st =  conn.createStatement();
            ResultSet rs = st.executeQuery("PRAGMA foreign_keys;");

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    void connect_shouldAllowExecutingSql() throws Exception {
        Db db = createTestDb();

        try (Connection c = db.connect();
             Statement st = c.createStatement()) {

            assertDoesNotThrow(() ->
                    st.execute("CREATE TABLE IF NOT EXISTS test_table (id TEXT PRIMARY KEY)")
            );
        }
    }

    //init() tests

    @Test
    void init_shouldCreateTables() throws Exception {
        Db db = createTestDb();

        db.init();

        try (Connection c = db.connect()) {
            assertTrue(tableExists(c, "accounts"));
            assertTrue(tableExists(c, "transactions"));
            assertTrue(tableExists(c, "ledgerLines"));
        }
    }

    //@Test
    /*void init_shouldBeIdempotent() {
        Db db = createTestDb();

        assertDoesNotThrow(db::init);
        assertDoesNotThrow(db::init);
    }*/

    @Test
    void init_shouldCreateColumns() throws Exception {
        try (Connection c = db.connect()) {
            assertTrue(columnExists(c, "accounts", "accID"));
            assertTrue(columnExists(c, "accounts", "accName"));
            assertTrue(columnExists(c, "accounts", "accType"));
            assertTrue(columnExists(c, "accounts", "currency"));

            assertTrue(columnExists(c, "transactions", "transactionID"));
            assertTrue(columnExists(c, "transactions", "transactionDate"));
            assertTrue(columnExists(c, "transactions", "description"));

            assertTrue(columnExists(c, "ledgerLines", "accID"));
            assertTrue(columnExists(c, "ledgerLines", "transactionID"));
        }
    }

    private boolean tableExists(Connection c, String tableName) throws Exception {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private boolean columnExists(Connection c, String tableName, String columnName) throws Exception {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }


}
