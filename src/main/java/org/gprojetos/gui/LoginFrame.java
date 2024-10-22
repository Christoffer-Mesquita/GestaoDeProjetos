package org.gprojetos.gui;

import org.gprojetos.api.API;
import org.gprojetos.utils.CentralizaJanela;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField usuarioField;
    private JPasswordField senhaField;

    public LoginFrame() {
        super("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        CentralizaJanela.centralizar(this);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        JLabel tituloLabel = new JLabel("Faça seu Login");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(tituloLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel usuarioLabel = new JLabel("Usuário:");
        panel.add(usuarioLabel, constraints);

        constraints.gridx = 1;
        usuarioField = new JTextField();
        usuarioField.setColumns(20);
        panel.add(usuarioField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel senhaLabel = new JLabel("Senha:");
        panel.add(senhaLabel, constraints);

        constraints.gridx = 1;
        senhaField = new JPasswordField();
        senhaField.setColumns(20);
        panel.add(senhaField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = usuarioField.getText();
                String senha = new String(senhaField.getPassword());

                boolean autenticado = API.getInstance().autenticarUsuario(usuario, senha);

                if (autenticado) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login realizado com sucesso, bem-vindo!");
                    dispose();
                    new InterfaceGrafica();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton, constraints);

        constraints.gridy = 4;
        JButton registrarButton = new JButton("Registrar");
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame();
            }
        });
        panel.add(registrarButton, constraints);

        setVisible(true);
    }
}
