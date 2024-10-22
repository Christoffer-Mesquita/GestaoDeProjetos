package org.gprojetos.gui;

import org.gprojetos.api.API;
import org.gprojetos.utils.CentralizaJanela;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JTextField nomeField;
    private JTextField emailField;

    public RegisterFrame() {
        super("Registrar");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
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
        JLabel tituloLabel = new JLabel("Crie sua Conta");
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
        JLabel confirmarSenhaLabel = new JLabel("Confirmar Senha:");
        panel.add(confirmarSenhaLabel, constraints);

        constraints.gridx = 1;
        confirmarSenhaField = new JPasswordField();
        confirmarSenhaField.setColumns(20);
        panel.add(confirmarSenhaField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        JLabel nomeLabel = new JLabel("Nome:");
        panel.add(nomeLabel, constraints);

        constraints.gridx = 1;
        nomeField = new JTextField();
        nomeField.setColumns(20);
        panel.add(nomeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        JLabel emailLabel = new JLabel("Email:");
        panel.add(emailLabel, constraints);

        constraints.gridx = 1;
        emailField = new JTextField();
        emailField.setColumns(20);
        panel.add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        JButton registrarButton = new JButton("Registrar");
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = usuarioField.getText();
                String senha = new String(senhaField.getPassword());
                String confirmarSenha = new String(confirmarSenhaField.getPassword());
                String nome = nomeField.getText();
                String email = emailField.getText();

                if (senha.equals(confirmarSenha)) {
                    boolean sucesso = API.getInstance().registrarUsuario(usuario, senha, nome, email);
                    if (sucesso) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Usuário registrado com sucesso!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Erro ao registrar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(registrarButton, constraints);

        setVisible(true);
    }
}