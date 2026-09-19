package edu.umg.programacion2.agenda.ui;

import edu.umg.programacion2.agenda.dao.CitaDAO;
import edu.umg.programacion2.agenda.modelo.Cita;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private JTextField txtCliente;
    private JTextField txtFechaHora;
    private JComboBox<String> cmbServicio;
    private JTextField txtDuracion;
    private JComboBox<String> cmbEstado;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private CitaDAO citaDAO;

    private int idSeleccionado = 0;

    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Color rosaFondo = new Color(252, 245, 248);
    private final Color rosaPrincipal = new Color(210, 145, 170);
    private final Color rosaOscuro = new Color(155, 91, 119);
    private final Color rosaClaro = new Color(247, 225, 235);
    private final Color blanco = Color.WHITE;
    private final Color grisTexto = new Color(85, 75, 80);

    public VentanaPrincipal() {

        citaDAO = new CitaDAO();

        setTitle("Gigi Spa | Agenda de Citas");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
        cargarCitas();
    }

    private void crearInterfaz() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(rosaFondo);
        panelPrincipal.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(rosaFondo);

        JLabel lblNombre = new JLabel("GIGI SPA");
        lblNombre.setFont(new Font("Serif", Font.BOLD, 30));
        lblNombre.setForeground(rosaOscuro);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Agenda de Citas");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblTitulo.setForeground(grisTexto);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Bienestar, belleza y momentos para ti");
        lblSubtitulo.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblSubtitulo.setForeground(rosaPrincipal);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(lblNombre);
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(lblSubtitulo);

        panelPrincipal.add(encabezado, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 12, 10));
        panelFormulario.setBackground(blanco);
        panelFormulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                rosaClaro, 1
                        ),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel lblCliente = crearEtiqueta("Cliente");
        panelFormulario.add(lblCliente);

        txtCliente = crearCampo();
        panelFormulario.add(txtCliente);

        JLabel lblFecha = crearEtiqueta("Fecha y hora");
        panelFormulario.add(lblFecha);

        txtFechaHora = crearCampo();
        txtFechaHora.setToolTipText(
                "Formato: dd/MM/yyyy HH:mm"
        );
        panelFormulario.add(txtFechaHora);

        JLabel lblServicio = crearEtiqueta("Servicio");
        panelFormulario.add(lblServicio);

        cmbServicio = new JComboBox<>(
                new String[]{
                        "Masaje relajante",
                        "Limpieza facial",
                        "Tratamiento corporal",
                        "Aromaterapia",
                        "Masaje de piedras calientes"
                }
        );
        estilizarCombo(cmbServicio);
        panelFormulario.add(cmbServicio);

        JLabel lblDuracion = crearEtiqueta("Duración (minutos)");
        panelFormulario.add(lblDuracion);

        txtDuracion = crearCampo();
        panelFormulario.add(txtDuracion);

        JLabel lblEstado = crearEtiqueta("Estado");
        panelFormulario.add(lblEstado);

        cmbEstado = new JComboBox<>(
                new String[]{
                        "pendiente",
                        "confirmada",
                        "cancelada"
                }
        );
        estilizarCombo(cmbEstado);
        panelFormulario.add(cmbEstado);

        JPanel panelCentro = new JPanel(new BorderLayout(15, 15));
        panelCentro.setBackground(rosaFondo);

        panelCentro.add(panelFormulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Cliente",
                        "Fecha y hora",
                        "Servicio",
                        "Duración",
                        "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setRowHeight(30);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setForeground(grisTexto);
        tabla.setBackground(blanco);
        tabla.setGridColor(rosaClaro);
        tabla.setSelectionBackground(rosaClaro);
        tabla.setSelectionForeground(rosaOscuro);

        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        tabla.getTableHeader().setBackground(rosaPrincipal);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setPreferredSize(
                new Dimension(0, 35)
        );

        DefaultTableCellRenderer centrado =
                new DefaultTableCellRenderer();

        centrado.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tabla.getColumnModel().getColumn(0)
                .setCellRenderer(centrado);

        tabla.getColumnModel().getColumn(4)
                .setCellRenderer(centrado);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createLineBorder(
                        rosaClaro, 1
                )
        );

        panelCentro.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        5
                )
        );

        panelBotones.setBackground(rosaFondo);

        JButton btnNuevo = crearBoton(
                "Nuevo",
                rosaClaro,
                rosaOscuro
        );

        JButton btnGuardar = crearBoton(
                "Guardar",
                rosaPrincipal,
                Color.WHITE
        );

        JButton btnActualizar = crearBoton(
                "Actualizar",
                rosaPrincipal,
                Color.WHITE
        );

        JButton btnEliminar = crearBoton(
                "Eliminar",
                new Color(225, 170, 190),
                Color.WHITE
        );

        JButton btnLimpiar = crearBoton(
                "Limpiar",
                Color.WHITE,
                rosaOscuro
        );

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(
                e -> limpiarFormulario()
        );

        btnLimpiar.addActionListener(
                e -> limpiarFormulario()
        );

        btnGuardar.addActionListener(
                e -> guardarCita()
        );

        btnActualizar.addActionListener(
                e -> actualizarCita()
        );

        btnEliminar.addActionListener(
                e -> eliminarCita()
        );

        tabla.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                cargarDatosSeleccionados();
            }

        });

        setContentPane(panelPrincipal);
    }

    private JLabel crearEtiqueta(String texto) {

        JLabel etiqueta = new JLabel(texto);

        etiqueta.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        etiqueta.setForeground(rosaOscuro);

        return etiqueta;
    }

    private JTextField crearCampo() {

        JTextField campo = new JTextField();

        campo.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(230, 210, 220)
                        ),
                        new EmptyBorder(5, 8, 5, 8)
                )
        );

        return campo;
    }

    private void estilizarCombo(
            JComboBox<String> combo
    ) {

        combo.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        combo.setBackground(blanco);
        combo.setForeground(grisTexto);
    }

    private JButton crearBoton(
            String texto,
            Color fondo,
            Color textoColor
    ) {

        JButton boton = new JButton(texto);

        boton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        9, 18, 9, 18
                )
        );

        return boton;
    }

    private void cargarCitas() {

        modeloTabla.setRowCount(0);

        try {

            List<Cita> citas = citaDAO.listarTodos();

            for (Cita cita : citas) {

                modeloTabla.addRow(
                        new Object[]{
                                cita.getId(),
                                cita.getCliente(),
                                cita.getFechaHora().format(formato),
                                cita.getServicio(),
                                cita.getDuracionMinutos(),
                                cita.getEstado()
                        }
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar las citas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void guardarCita() {

        try {

            if (!validarCampos()) {
                return;
            }

            LocalDateTime fechaHora =
                    LocalDateTime.parse(
                            txtFechaHora.getText().trim(),
                            formato
                    );

            Cita cita = new Cita();

            cita.setCliente(
                    txtCliente.getText().trim()
            );

            cita.setFechaHora(fechaHora);

            cita.setServicio(
                    cmbServicio.getSelectedItem().toString()
            );

            cita.setDuracionMinutos(
                    Integer.parseInt(
                            txtDuracion.getText().trim()
                    )
            );

            cita.setEstado("pendiente");

            citaDAO.crear(cita);

            JOptionPane.showMessageDialog(
                    this,
                    "Cita guardada correctamente.",
                    "Gigi Spa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCitas();

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha debe tener el formato dd/MM/yyyy HH:mm.",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar la cita.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizarCita() {

        if (idSeleccionado == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una cita de la tabla."
            );

            return;
        }

        try {

            if (!validarCampos()) {
                return;
            }

            LocalDateTime fechaHora =
                    LocalDateTime.parse(
                            txtFechaHora.getText().trim(),
                            formato
                    );

            Cita cita = new Cita();

            cita.setId(idSeleccionado);

            cita.setCliente(
                    txtCliente.getText().trim()
            );

            cita.setFechaHora(fechaHora);

            cita.setServicio(
                    cmbServicio.getSelectedItem().toString()
            );

            cita.setDuracionMinutos(
                    Integer.parseInt(
                            txtDuracion.getText().trim()
                    )
            );

            cita.setEstado(
                    cmbEstado.getSelectedItem().toString()
            );

            citaDAO.actualizar(cita);

            JOptionPane.showMessageDialog(
                    this,
                    "Cita actualizada correctamente.",
                    "Gigi Spa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCitas();

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha debe tener el formato dd/MM/yyyy HH:mm.",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar la cita.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarCita() {

        if (idSeleccionado == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una cita de la tabla."
            );

            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar físicamente esta cita?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                citaDAO.eliminar(idSeleccionado);

                JOptionPane.showMessageDialog(
                        this,
                        "Cita eliminada correctamente.",
                        "Gigi Spa",
                        JOptionPane.INFORMATION_MESSAGE
                );

                limpiarFormulario();
                cargarCitas();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar la cita.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private boolean validarCampos() {

        if (txtCliente.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del cliente es obligatorio."
            );

            return false;
        }

        int duracion;

        try {

            duracion = Integer.parseInt(
                    txtDuracion.getText().trim()
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "La duración debe ser un número entero."
            );

            return false;
        }

        if (duracion <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "La duración debe ser mayor que 0."
            );

            return false;
        }

        try {

            LocalDateTime fechaHora =
                    LocalDateTime.parse(
                            txtFechaHora.getText().trim(),
                            formato
                    );

            if (fechaHora.isBefore(
                    LocalDateTime.now()
            )) {

                JOptionPane.showMessageDialog(
                        this,
                        "La fecha y hora no pueden estar en el pasado."
                );

                return false;
            }

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fecha debe tener el formato dd/MM/yyyy HH:mm."
            );

            return false;
        }

        return true;
    }

    private void cargarDatosSeleccionados() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idSeleccionado =
                Integer.parseInt(
                        modeloTabla.getValueAt(
                                fila, 0
                        ).toString()
                );

        txtCliente.setText(
                modeloTabla.getValueAt(
                        fila, 1
                ).toString()
        );

        txtFechaHora.setText(
                modeloTabla.getValueAt(
                        fila, 2
                ).toString()
        );

        cmbServicio.setSelectedItem(
                modeloTabla.getValueAt(
                        fila, 3
                ).toString()
        );

        txtDuracion.setText(
                modeloTabla.getValueAt(
                        fila, 4
                ).toString()
        );

        cmbEstado.setSelectedItem(
                modeloTabla.getValueAt(
                        fila, 5
                ).toString()
        );
    }

    private void limpiarFormulario() {

        idSeleccionado = 0;

        txtCliente.setText("");
        txtFechaHora.setText("");

        cmbServicio.setSelectedIndex(0);

        txtDuracion.setText("");

        cmbEstado.setSelectedItem("pendiente");

        tabla.clearSelection();
    }
}