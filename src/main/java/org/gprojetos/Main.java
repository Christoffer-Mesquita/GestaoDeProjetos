package org.gprojetos;

import org.gprojetos.gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao definir o LookAndFeel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}