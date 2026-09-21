package edu.umg.programacion2.agenda.dao;

import edu.umg.programacion2.agenda.config.ConexionBD;

import edu.umg.programacion2.agenda.modelo.Cita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CitaDAO {

    public Cita crear(Cita item) throws SQLException {

    	String sql = "INSERT INTO citas "
    	        + "(cliente, fecha_hora, servicio, duracion_minutos, estado, confirmacion) "
    	        + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getCliente());
            ps.setTimestamp(2, Timestamp.valueOf(item.getFechaHora()));
            ps.setString(3, item.getServicio());
            ps.setInt(4, item.getDuracionMinutos());
            ps.setString(5, item.getEstado());
            ps.setBoolean(6, item.isConfirmacion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        }

        return item;
    }

    public List<Cita> listarTodos() throws SQLException {

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT id, cliente, fecha_hora, servicio, "
                + "duracion_minutos, estado, confirmacion FROM citas ORDER BY fecha_hora";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cita cita = new Cita();

                cita.setId(rs.getInt("id"));
                cita.setCliente(rs.getString("cliente"));
                cita.setFechaHora(
                        rs.getTimestamp("fecha_hora").toLocalDateTime()
                );
                cita.setServicio(rs.getString("servicio"));
                cita.setDuracionMinutos(rs.getInt("duracion_minutos"));
                cita.setEstado(rs.getString("estado"));
                cita.setConfirmacion(rs.getBoolean("confirmacion")
                	);

                lista.add(cita);
            }
        }

        return lista;
    }

    public Optional<Cita> buscarPorId(int id) throws SQLException {

    	String sql = "SELECT id, cliente, fecha_hora, servicio, "
    	        + "duracion_minutos, estado, confirmacion FROM citas WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Cita cita = new Cita();

                    cita.setId(rs.getInt("id"));
                    cita.setCliente(rs.getString("cliente"));
                    cita.setFechaHora(
                            rs.getTimestamp("fecha_hora").toLocalDateTime()
                    );
                    cita.setServicio(rs.getString("servicio"));
                    cita.setDuracionMinutos(rs.getInt("duracion_minutos"));
                    cita.setEstado(rs.getString("estado"));
                    cita.setConfirmacion(rs.getBoolean("confirmacion"));

                    return Optional.of(cita);
                }
            }
        }

        return Optional.empty();
    }

    public boolean actualizar(Cita item) throws SQLException {

    	String sql = "UPDATE citas SET cliente = ?, fecha_hora = ?, "
    	        + "servicio = ?, duracion_minutos = ?, estado = ?, confirmacion = ? "
    	        + "WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getCliente());
            ps.setTimestamp(2, Timestamp.valueOf(item.getFechaHora()));
            ps.setString(3, item.getServicio());
            ps.setInt(4, item.getDuracionMinutos());
            ps.setString(5, item.getEstado());
            ps.setBoolean(6, item.isConfirmacion());
            ps.setInt(7, item.getId());
            

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM citas WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            

            return ps.executeUpdate() > 0;  
        }
    }
            	

        	
    
}