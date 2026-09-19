
	package edu.umg.programacion2.agenda.config;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.SQLException;

	public class ConexionBD {

	    private static final String URL = "jdbc:mysql://localhost:3306/agenda_citas";
	    private static final String USUARIO = "root";
	    private static final String PASSWORD = "Escriba su contraseña aqui";

	    public static Connection obtenerConexion() throws SQLException {
	        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
	    }
	}

