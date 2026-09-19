package edu.umg.programacion2.agenda.ui;

import edu.umg.programacion2.agenda.dao.CitaDAO;
import edu.umg.programacion2.agenda.modelo.Cita;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JCheckBox chkConfirmacionLlamada;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JLabel lblModo;
    private JLabel lblTotalCitas;

    private final CitaDAO citaDAO;
    private int idSeleccionado = 0;

    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Paleta visual del spa
    private final Color fondoGeneral = new Color(249, 246, 248);
    private final Color rosaPrincipal = new Color(205, 132, 163);
    private final Color rosaOscuro = new Color(132, 73, 101);
    private final Color rosaMedio = new Color(232, 190, 208);
    private final Color rosaClaro = new Color(249, 232, 239);
    private final Color bordeSuave = new Color(230, 219, 224);
    private final Color textoPrincipal = new Color(65, 56, 61);
    private final Color textoSecundario = new Color(125, 112, 119);
    private final Color blanco = Color.WHITE;

    public VentanaPrincipal() {
        citaDAO = new CitaDAO();

        setTitle("Gigi Spa | Agenda de Citas");
        setSize(1120, 730);
        setMinimumSize(new Dimension(980, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
        cargarCitas();
        actualizarModoEdicion(false);
    }

    private void crearInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 18));
        panelPrincipal.setBackground(fondoGeneral);
        panelPrincipal.setBorder(new EmptyBorder(0, 24, 20, 24));

        panelPrincipal.add(crearEncabezado(), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.add(crearTarjetaFormulario(), BorderLayout.NORTH);
        contenido.add(crearTarjetaTabla(), BorderLayout.CENTER);

        panelPrincipal.add(contenido, BorderLayout.CENTER);
        panelPrincipal.add(crearPanelBotones(), BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradiente = new GradientPaint(
                        0, 0, rosaOscuro,
                        getWidth(), getHeight(), rosaPrincipal
                );

                g2.setPaint(gradiente);
                g2.fillRoundRect(0, -24, getWidth(), getHeight() + 24, 28, 28);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(new EmptyBorder(24, 20, 22, 20));

        JLabel lblNombre = new JLabel("GIGI SPA");
        lblNombre.setFont(new Font("Serif", Font.BOLD, 31));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Agenda de Citas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(255, 244, 249));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Bienestar, belleza y momentos para ti");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(255, 232, 242));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(lblNombre);
        encabezado.add(Box.createVerticalStrut(2));
        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(lblSubtitulo);

        return encabezado;
    }

    private JPanel crearTarjetaFormulario() {
        RoundedPanel tarjeta = new RoundedPanel(blanco, 22);
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.setBorder(new EmptyBorder(16, 20, 18, 20));

        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setOpaque(false);

        JLabel titulo = new JLabel("Datos de la cita");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(rosaOscuro);

        lblModo = new JLabel("Nueva cita");
        lblModo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblModo.setForeground(rosaOscuro);
        lblModo.setOpaque(true);
        lblModo.setBackground(rosaClaro);
        lblModo.setBorder(new EmptyBorder(5, 10, 5, 10));

        tituloPanel.add(titulo, BorderLayout.WEST);
        tituloPanel.add(lblModo, BorderLayout.EAST);
        tarjeta.add(tituloPanel, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        txtCliente = crearCampo();
        txtFechaHora = crearCampo();
        txtFechaHora.setToolTipText("Formato: dd/MM/yyyy HH:mm");

        cmbServicio = new JComboBox<>(new String[]{
                "Masaje relajante",
                "Limpieza facial",
                "Tratamiento corporal",
                "Aromaterapia",
                "Masaje de piedras calientes"
        });
        estilizarCombo(cmbServicio);

        txtDuracion = crearCampo();

        cmbEstado = new JComboBox<>(new String[]{
                "pendiente",
                "confirmada",
                "cancelada"
        });
        estilizarCombo(cmbEstado);
        
        chkConfirmacionLlamada =
                new JCheckBox("Requiere confirmación por llamada");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 14);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(crearGrupoCampo("Cliente", txtCliente,
                "Nombre de la persona que solicita la cita"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        formulario.add(crearGrupoCampo("Fecha y hora", txtFechaHora,
                "Formato: dd/MM/yyyy HH:mm"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 0, 14);
        formulario.add(crearGrupoCampo("Servicio", cmbServicio,
                "Selecciona el servicio solicitado"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 0, 0, 0);

        JPanel filaDerecha = new JPanel(new GridLayout(1, 2, 12, 0));
        filaDerecha.setOpaque(false);
        filaDerecha.add(crearGrupoCampo("Duración (min)", txtDuracion,
                "Debe ser mayor que 0"));
        filaDerecha.add(crearGrupoCampo("Estado", cmbEstado,
                "Se habilita al editar"));
        formulario.add(filaDerecha, gbc);

        tarjeta.add(formulario, BorderLayout.CENTER);

        JLabel nota = new JLabel("Las citas nuevas se guardan automáticamente con estado pendiente.");
        nota.setFont(new Font("SansSerif", Font.ITALIC, 11));
        nota.setForeground(textoSecundario);
        tarjeta.add(nota, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearGrupoCampo(String etiqueta, JComponent componente, String ayuda) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(textoPrincipal);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lblAyuda = new JLabel(ayuda);
        lblAyuda.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblAyuda.setForeground(textoSecundario);
        lblAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(componente);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblAyuda);

        return panel;
    }

    private JPanel crearTarjetaTabla() {
        RoundedPanel tarjeta = new RoundedPanel(blanco, 22);
        tarjeta.setLayout(new BorderLayout(0, 12));
        tarjeta.setBorder(new EmptyBorder(15, 16, 16, 16));

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);

        JLabel titulo = new JLabel("Citas registradas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(rosaOscuro);

        lblTotalCitas = new JLabel("0 citas registradas");
        lblTotalCitas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTotalCitas.setForeground(textoSecundario);

        superior.add(titulo, BorderLayout.WEST);
        superior.add(lblTotalCitas, BorderLayout.EAST);
        tarjeta.add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Cliente",
                        "Fecha y hora",
                        "Servicio",
                        "Duración",
                        "Estado",
                        "Confirmacion"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(34);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.setForeground(textoPrincipal);
        tabla.setBackground(blanco);
        tabla.setGridColor(new Color(239, 233, 236));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(new Color(246, 224, 234));
        tabla.setSelectionForeground(rosaOscuro);
        tabla.setAutoCreateRowSorter(true);

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(rosaOscuro);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabla.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(145);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(85);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);

        tabla.getColumnModel().getColumn(0).setCellRenderer(new RendererTabla(true));
        tabla.getColumnModel().getColumn(1).setCellRenderer(new RendererTabla(false));
        tabla.getColumnModel().getColumn(2).setCellRenderer(new RendererTabla(true));
        tabla.getColumnModel().getColumn(3).setCellRenderer(new RendererTabla(false));
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DuracionRenderer());
        tabla.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDatosSeleccionados();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(bordeSuave));
        scroll.getViewport().setBackground(blanco);
        scroll.setBackground(blanco);

        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        izquierda.setOpaque(false);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        derecha.setOpaque(false);

        btnNuevo = crearBoton("Nuevo", rosaClaro, rosaOscuro, new Color(241, 214, 227));
        btnLimpiar = crearBoton("Limpiar", Color.WHITE, rosaOscuro, rosaClaro);
        btnGuardar = crearBoton("Guardar", rosaPrincipal, Color.WHITE, new Color(188, 113, 146));
        btnActualizar = crearBoton("Actualizar", rosaOscuro, Color.WHITE, new Color(112, 58, 83));
        btnEliminar = crearBoton("Eliminar", new Color(205, 105, 121), Color.WHITE,
                new Color(184, 83, 101));

        izquierda.add(btnNuevo);
        izquierda.add(btnLimpiar);
        derecha.add(btnGuardar);
        derecha.add(btnActualizar);
        derecha.add(btnEliminar);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        btnNuevo.addActionListener(e -> {
            limpiarFormulario();
            txtCliente.requestFocusInWindow();
        });

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarCita());
        btnActualizar.addActionListener(e -> actualizarCita());
        btnEliminar.addActionListener(e -> eliminarCita());

        return panel;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setForeground(textoPrincipal);
        campo.setBackground(Color.WHITE);
        campo.setPreferredSize(new Dimension(160, 38));

        Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bordeSuave, 1),
                new EmptyBorder(7, 10, 7, 10)
        );

        Border foco = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rosaPrincipal, 2),
                new EmptyBorder(6, 9, 6, 9)
        );

        campo.setBorder(normal);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(foco);
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(normal);
            }
        });

        return campo;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setBackground(blanco);
        combo.setForeground(textoPrincipal);
        combo.setPreferredSize(new Dimension(160, 38));
        combo.setBorder(BorderFactory.createLineBorder(bordeSuave, 1));
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor, Color hover) {
        ModernButton boton = new ModernButton(texto, fondo, hover);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 18, 10, 18));
        return boton;
    }

    private void cargarCitas() {
        modeloTabla.setRowCount(0);

        try {
            List<Cita> citas = citaDAO.listarTodos();

            for (Cita cita : citas) {
                modeloTabla.addRow(new Object[]{
                        cita.getId(),
                        cita.getCliente(),
                        cita.getFechaHora().format(formato),
                        cita.getServicio(),
                        cita.getDuracionMinutos(),
                        cita.getEstado()
                       
                });
            }

            actualizarContador();

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

            LocalDateTime fechaHora = LocalDateTime.parse(
                    txtFechaHora.getText().trim(),
                    formato
            );

            Cita cita = new Cita();
            cita.setCliente(txtCliente.getText().trim());
            cita.setFechaHora(fechaHora);
            cita.setServicio(cmbServicio.getSelectedItem().toString());
            cita.setDuracionMinutos(Integer.parseInt(txtDuracion.getText().trim()));
            cita.setEstado("pendiente");
            cita.setConfirmacion(chkConfirmacionLlamada.isSelected()
            	);

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

            LocalDateTime fechaHora = LocalDateTime.parse(
                    txtFechaHora.getText().trim(),
                    formato
            );

            Cita cita = new Cita();
            cita.setId(idSeleccionado);
            cita.setCliente(txtCliente.getText().trim());
            cita.setFechaHora(fechaHora);
            cita.setServicio(cmbServicio.getSelectedItem().toString());
            cita.setDuracionMinutos(Integer.parseInt(txtDuracion.getText().trim()));
            cita.setEstado(cmbEstado.getSelectedItem().toString());
            cita.setConfirmacion(chkConfirmacionLlamada.isSelected()
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
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
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
                    "El nombre del cliente es obligatorio.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE
            );
            txtCliente.requestFocusInWindow();
            return false;
        }

        int duracion;

        try {
            duracion = Integer.parseInt(txtDuracion.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "La duración debe ser un número entero.",
                    "Duración inválida",
                    JOptionPane.WARNING_MESSAGE
            );
            txtDuracion.requestFocusInWindow();
            return false;
        }

        if (duracion <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "La duración debe ser mayor que 0.",
                    "Duración inválida",
                    JOptionPane.WARNING_MESSAGE
            );
            txtDuracion.requestFocusInWindow();
            return false;
        }

        try {
            LocalDateTime fechaHora = LocalDateTime.parse(
                    txtFechaHora.getText().trim(),
                    formato
            );

            if (fechaHora.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "La fecha y hora no pueden estar en el pasado.",
                        "Fecha inválida",
                        JOptionPane.WARNING_MESSAGE
                );
                txtFechaHora.requestFocusInWindow();
                return false;
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha debe tener el formato dd/MM/yyyy HH:mm.",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE
            );
            txtFechaHora.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void cargarDatosSeleccionados() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tabla.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        txtCliente.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtFechaHora.setText(modeloTabla.getValueAt(fila, 2).toString());
        cmbServicio.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
        txtDuracion.setText(modeloTabla.getValueAt(fila, 4).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 5).toString());

        actualizarModoEdicion(true);
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;

        txtCliente.setText("");
        txtFechaHora.setText("");
        cmbServicio.setSelectedIndex(0);
        txtDuracion.setText("");
        cmbEstado.setSelectedItem("pendiente");
        tabla.clearSelection();

        actualizarModoEdicion(false);
    }

    private void actualizarModoEdicion(boolean editando) {
        if (lblModo == null) {
            return;
        }

        if (editando) {
            lblModo.setText("Editando cita #" + idSeleccionado);
            cmbEstado.setEnabled(true);
            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            getRootPane().setDefaultButton(btnActualizar);
        } else {
            lblModo.setText("Nueva cita");
            cmbEstado.setSelectedItem("pendiente");
            cmbEstado.setEnabled(false);
            btnGuardar.setEnabled(true);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            getRootPane().setDefaultButton(btnGuardar);
        }
    }

    private void actualizarContador() {
        int total = modeloTabla.getRowCount();
        lblTotalCitas.setText(total == 1
                ? "1 cita registrada"
                : total + " citas registradas");
    }

    private class RendererTabla extends DefaultTableCellRenderer {
        private final boolean centrado;

        RendererTabla(boolean centrado) {
            this.centrado = centrado;
            setBorder(new EmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            setHorizontalAlignment(centrado ? SwingConstants.CENTER : SwingConstants.LEFT);
            setFont(new Font("SansSerif", Font.PLAIN, 12));

            if (isSelected) {
                setBackground(new Color(246, 224, 234));
                setForeground(rosaOscuro);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(253, 249, 251));
                setForeground(textoPrincipal);
            }

            return this;
        }
    }

    private class DuracionRenderer extends RendererTabla {
        DuracionRenderer() {
            super(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component componente = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            if (value != null) {
                setText(value.toString() + " min");
            }

            return componente;
        }
    }

    private class EstadoRenderer extends DefaultTableCellRenderer {
        EstadoRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(5, 8, 5, 8));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            String estado = value == null ? "" : value.toString().toLowerCase();
            setFont(new Font("SansSerif", Font.BOLD, 11));

            if (isSelected) {
                setBackground(new Color(235, 210, 222));
                setForeground(rosaOscuro);
            } else if ("confirmada".equals(estado)) {
                setBackground(new Color(224, 244, 232));
                setForeground(new Color(46, 112, 72));
            } else if ("cancelada".equals(estado)) {
                setBackground(new Color(251, 228, 232));
                setForeground(new Color(151, 58, 73));
            } else {
                setBackground(new Color(255, 244, 214));
                setForeground(new Color(136, 103, 28));
            }

            setText(capitalizar(estado));
            return this;
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private static class RoundedPanel extends JPanel {
        private final Color colorFondo;
        private final int arco;

        RoundedPanel(Color colorFondo, int arco) {
            this.colorFondo = colorFondo;
            this.arco = arco;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 12));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, arco, arco);

            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, arco, arco);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class ModernButton extends JButton {
        private final Color colorNormal;
        private final Color colorHover;
        private boolean hover;

        ModernButton(String texto, Color colorNormal, Color colorHover) {
            super(texto);
            this.colorNormal = colorNormal;
            this.colorHover = colorHover;

            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondo;
            if (!isEnabled()) {
                fondo = new Color(222, 216, 219);
            } else {
                fondo = hover ? colorHover : colorNormal;
            }

            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
