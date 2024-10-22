package org.gprojetos.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InterfaceGrafica extends JFrame {

    private DefaultListModel<String> listModel;
    private JList<String> listaProjetos;
    private static final String JSON_FILE = "projetos.json";
    private static final String SUGESTOES_FILE = "sugestoes.json";
    private JButton botaoIA;
    private JButton botaoApagar;
    private JButton botaoSugestoes;
    private Map<String, JSONObject> projetosData = new HashMap<>();
    private Map<String, String> sugestoesIA = new HashMap<>();
    private JPanel painelDetalhes;
    private String apiKey = "AIzaSyDSAlIa7dpHIA0EYIiv5lagGotN0F-xSeY";

    public InterfaceGrafica() {
        super("ZyonProjetos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);


        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

        }

        listModel = new DefaultListModel<>();
        listaProjetos = new JList<>(listModel);
        carregarProjetosDoArquivo();
        carregarSugestoesDoArquivo();


        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setBorder(BorderFactory.createTitledBorder("Lista de Projetos"));
        painelLista.setPreferredSize(new Dimension(200, 600));

        JScrollPane scrollPane = new JScrollPane(listaProjetos);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        painelLista.add(scrollPane, BorderLayout.CENTER);
        add(painelLista, BorderLayout.WEST);


        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBorder(new EmptyBorder(10, 10, 10, 10));


        JPanel painelConfiguracoes = new JPanel();
        painelConfiguracoes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configurações"));
        painelConfiguracoes.setLayout(new FlowLayout(FlowLayout.LEFT));

        botaoIA = new JButton("IA");
        botaoIA.setVisible(false);
        botaoIA.addActionListener(e -> enviarRequestParaAPI(listaProjetos.getSelectedValue()));

        botaoApagar = new JButton("Apagar");
        botaoApagar.setVisible(false);
        botaoApagar.addActionListener(this::apagarProjeto);

        botaoSugestoes = new JButton("Sugestões");
        botaoSugestoes.setVisible(false);
        botaoSugestoes.addActionListener(this::mostrarSugestao);

        painelConfiguracoes.add(botaoIA);
        painelConfiguracoes.add(botaoApagar);
        painelConfiguracoes.add(botaoSugestoes);
        painelCentral.add(painelConfiguracoes, BorderLayout.NORTH);


        painelDetalhes = new JPanel();
        painelDetalhes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Detalhes do Projeto"));
        painelDetalhes.setLayout(new BorderLayout());
        painelDetalhes.setVisible(false);
        painelCentral.add(painelDetalhes, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);


        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelZ = new JLabel("ZyonProjetos");
        labelZ.setFont(new Font("Arial", Font.BOLD, 16));
        painelNorte.add(labelZ);
        add(painelNorte, BorderLayout.NORTH);


        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoCriar = new JButton("Criar");
        botaoCriar.addActionListener(e -> criarNovoProjeto());
        painelSul.add(botaoCriar);
        add(painelSul, BorderLayout.SOUTH);


        listaProjetos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = !listaProjetos.isSelectionEmpty();
                botaoIA.setVisible(hasSelection);
                botaoApagar.setVisible(hasSelection);
                botaoSugestoes.setVisible(hasSelection);

                if (hasSelection) {
                    mostrarDetalhesDoProjeto(listaProjetos.getSelectedValue());
                } else {
                    painelDetalhes.setVisible(false);
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void carregarProjetosDoArquivo() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(JSON_FILE)) {
            Object obj = parser.parse(reader);
            JSONArray projetosArray = (JSONArray) obj;

            for (Object projetoObj : projetosArray) {
                JSONObject projeto = (JSONObject) projetoObj;
                String titulo = projeto.get("titulo").toString();
                listModel.addElement(titulo);
                projetosData.put(titulo, projeto);
            }

        } catch (IOException | ParseException e) {
            criarArquivoJSON();
        }
    }

    private void criarNovoProjeto() {
        JTextField tituloField = new JTextField(15);
        JTextArea descricaoArea = new JTextArea(5, 15);
        JTextField prazoField = new JTextField(15);
        JTextField gerenteField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.add(new JLabel("Título:"));
        panel.add(tituloField);
        panel.add(new JLabel("Descrição:"));
        panel.add(new JScrollPane(descricaoArea));
        panel.add(new JLabel("Prazo:"));
        panel.add(prazoField);
        panel.add(new JLabel("Gerente:"));
        panel.add(gerenteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Projeto", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String titulo = tituloField.getText();
            String descricao = descricaoArea.getText();
            String prazo = prazoField.getText();
            String gerente = gerenteField.getText();

            if (titulo.isEmpty() || descricao.isEmpty() || prazo.isEmpty() || gerente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JSONObject projetoDetails = new JSONObject();
            projetoDetails.put("titulo", titulo);
            projetoDetails.put("descricao", descricao);
            projetoDetails.put("prazo", prazo);
            projetoDetails.put("gerente", gerente);

            projetosData.put(titulo, projetoDetails);

            listModel.addElement(titulo);
            salvarProjetosNoArquivo();
        }
    }

    private void salvarProjetosNoArquivo() {
        JSONArray projetosArray = new JSONArray();
        for (String titulo : projetosData.keySet()) {
            projetosArray.add(projetosData.get(titulo));
        }

        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(projetosArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar projetos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarArquivoJSON() {
        JSONArray projetos = new JSONArray();
        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(projetos.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar arquivo de projetos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void apagarProjeto(ActionEvent e) {
        int selectedIndex = listaProjetos.getSelectedIndex();
        if (selectedIndex != -1) {
            int confirmacao = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja apagar o projeto '" + listModel.getElementAt(selectedIndex) + "'?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacao == JOptionPane.YES_OPTION) {
                String projetoRemovido = listModel.getElementAt(selectedIndex);
                listModel.remove(selectedIndex);
                projetosData.remove(projetoRemovido);
                salvarProjetosNoArquivo();
                botaoIA.setVisible(false);
                botaoApagar.setVisible(false);
                botaoSugestoes.setVisible(false); // Hide "Sugestões" button
                painelDetalhes.setVisible(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para apagar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarDetalhesDoProjeto(String nomeProjeto) {
        JSONObject projetoDetails = projetosData.get(nomeProjeto);
        if (projetoDetails != null) {
            painelDetalhes.removeAll();

            JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            detailsPanel.add(new JLabel("Título: " + projetoDetails.get("titulo").toString()));

            JTextArea descricaoArea = new JTextArea(projetoDetails.get("descricao").toString());
            descricaoArea.setEditable(false); // Make it read-only
            descricaoArea.setLineWrap(true);
            descricaoArea.setWrapStyleWord(true);
            JScrollPane descricaoScrollPane = new JScrollPane(descricaoArea);
            detailsPanel.add(new JLabel("Descrição:"));
            detailsPanel.add(descricaoScrollPane);

            detailsPanel.add(new JLabel("Prazo: " + projetoDetails.get("prazo").toString()));
            detailsPanel.add(new JLabel("Gerente: " + projetoDetails.get("gerente").toString()));

            JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
            painelDetalhes.add(detailsScrollPane, BorderLayout.CENTER); // Add to CENTER of painelDetalhes

            painelDetalhes.setVisible(true);
            painelDetalhes.revalidate();
            painelDetalhes.repaint();
        }
    }

    private void enviarRequestParaAPI(String nomeProjeto) {
        try {
            JSONObject projetoDetails = projetosData.get(nomeProjeto);
            if (projetoDetails == null) {
                JOptionPane.showMessageDialog(this, "Erro ao obter detalhes do projeto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descricao = projetoDetails.get("descricao").toString();
            String prazo = projetoDetails.get("prazo").toString();
            String gerente = projetoDetails.get("gerente").toString();

            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

            String body = String.format("{\"contents\":[{\"parts\":[{\"text\":\"Gere um ROADMAP do projeto %s cuja a descrição dele é %s com um prazo de entrega de %s e dando os creditos para o gerente %s\"}]}]}",
                    nomeProjeto, descricao, prazo, gerente);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Request sent successfully!");

                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Correctly parse the JSON response from Gemini
                    JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response.toString());
                    JSONArray candidates = (JSONArray) jsonResponse.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        JSONObject firstCandidate = (JSONObject) candidates.get(0);
                        JSONObject content = (JSONObject) firstCandidate.get("content"); // Get "content"
                        JSONArray parts = (JSONArray) content.get("parts"); // Get "parts"
                        if (parts != null && !parts.isEmpty()) {
                            JSONObject firstPart = (JSONObject) parts.get(0);
                            String generatedText = (String) firstPart.get("text"); // Get "text"

                            sugestoesIA.put(nomeProjeto, generatedText);
                            salvarSugestoesNoArquivo();

                            System.out.println("Resposta da API: " + generatedText);
                        } else {
                            // Handle the case where "parts" is empty or null
                            System.err.println("Gemini API response missing 'parts' array.");
                            JOptionPane.showMessageDialog(this, "A API não gerou uma sugestão.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // Handle the case where "candidates" is empty or null
                        System.err.println("Gemini API não retornou candidatos.");
                        JOptionPane.showMessageDialog(this, "A API não gerou uma sugestão.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (ParseException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao analisar a resposta da API.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                System.err.println("Erro ao enviar a requisição. Código de resposta: " + responseCode);
                JOptionPane.showMessageDialog(this, "Erro ao enviar requisição para a API.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao enviar requisição para a API.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarSugestao(ActionEvent e) {
        String selectedProject = listaProjetos.getSelectedValue();
        if (selectedProject != null) {
            String sugestao = sugestoesIA.get(selectedProject);
            if (sugestao != null) {
                // Create and show the suggestion window
                JFrame sugestaoFrame = new JFrame("Sugestão da IA para " + selectedProject);
                sugestaoFrame.setSize(600, 400);
                sugestaoFrame.setLocationRelativeTo(this);

                DefaultListModel<String> sugestaoListModel = new DefaultListModel<>();
                String[] sugestaoLines = sugestao.split("\n");
                for (String line : sugestaoLines) {
                    sugestaoListModel.addElement(line);
                }
                JList<String> sugestaoList = new JList<>(sugestaoListModel);
                sugestaoList.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(sugestaoList);
                sugestaoFrame.add(scrollPane, BorderLayout.CENTER);

                // Add "Enviar Whatsapp" button
                JButton enviarWhatsappButton = new JButton("Enviar Whatsapp");
                enviarWhatsappButton.addActionListener(event -> enviarSugestaoWhatsapp(sugestao));
                sugestaoFrame.add(enviarWhatsappButton, BorderLayout.SOUTH);

                sugestaoFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma sugestão encontrada para este projeto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um projeto.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void carregarSugestoesDoArquivo() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(SUGESTOES_FILE)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONObject) {
                JSONObject sugestoes = (JSONObject) obj;
                for (Object key : sugestoes.keySet()) {
                    String projeto = (String) key;
                    String sugestao = (String) sugestoes.get(key);
                    sugestoesIA.put(projeto, sugestao);
                }
            }
        } catch (IOException | ParseException e) {
        }
    }

    private void salvarSugestoesNoArquivo() {
        JSONObject sugestoesJson = new JSONObject();
        sugestoesJson.putAll(sugestoesIA);

        try (FileWriter file = new FileWriter(SUGESTOES_FILE)) {
            file.write(sugestoesJson.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar sugestões.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarSugestaoWhatsapp(String sugestao) {
        String numeroTelefone = JOptionPane.showInputDialog(this, "Digite o número de telefone (com código do país):", "Enviar Whatsapp", JOptionPane.PLAIN_MESSAGE);
        if (numeroTelefone != null && !numeroTelefone.isEmpty()) {
            try {
                // Construct the JSON payload
                JSONObject payload = new JSONObject();
                payload.put("number", numeroTelefone);
                payload.put("message", sugestao);

                // Send the request to your WhatsApp API
                String apiUrl = "http://localhost:3000/send-message";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = payload.toJSONString().getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(this, "Mensagem enviada para o WhatsApp com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.err.println("Erro ao enviar mensagem para o WhatsApp. Código de resposta: " + responseCode);
                    JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem para o WhatsApp.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem para o WhatsApp.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}