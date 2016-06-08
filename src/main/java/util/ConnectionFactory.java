package util;

import dao.LivrariaDAOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author BBEIRIGO
 */
public class ConnectionFactory {

    public static Connection getConnection(String server, String port, String db, String user, String password) throws LivrariaDAOException {
            String url = "jdbc:mysql://"+server+":"+port+"/"+db;
        try {
            //Ajuda a identificar a falta do driver JDBC
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new LivrariaDAOException("Impossível conectar-se a url: "+url+"?user="+user+"&password="+password,e);
        }
    }

    public static void closeConnection(Connection conn, Statement strnt, ResultSet rs) throws LivrariaDAOException {
        close(conn, strnt, rs);
    }

    public static void closeConnection(Connection conn, Statement strnt) throws LivrariaDAOException {
        close(conn, strnt, null);
    }

    public static void closeConnection(Connection conn) throws LivrariaDAOException {
        close(conn, null, null);
    }

    private static void close(Connection conn, Statement strnt, ResultSet rs) throws LivrariaDAOException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (strnt != null) {
                strnt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            throw new LivrariaDAOException("Impossível terminar conexão.", e);
        }

    }

}
