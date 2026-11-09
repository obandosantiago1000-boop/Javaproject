package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conecction {


    private static conecction instancia;


    private final String url = "jdbc:mysql://localhost:3306/banco";
    private final String user = "root";
    private final String password = "Santiago1081279586";


    private conecction() {
    }


    public static synchronized conecction getInstance() {

        if (instancia == null) {
            instancia = new conecction();
        }

        return instancia;
    }


    public Connection conectar() throws SQLException {

        return DriverManager.getConnection(url, user, password);
    }
}
