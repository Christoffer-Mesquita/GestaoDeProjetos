package org.gprojetos.utils;

import javax.swing.*;
import java.awt.*;

public class CentralizaJanela {

    public static void centralizar(Window janela) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = janela.getSize();

        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;

        janela.setLocation(x, y);
    }
}