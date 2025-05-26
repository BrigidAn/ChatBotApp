import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;
import javax.swing.border.EmptyBorder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import com.google.gson.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Properties;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ochim
 */
public class chatBotGUI extends javax.swing.JFrame {
    private JPanel chatPanel;
    private JTextField userInput;
    private JScrollPane scrollPane;
    private HashMap<String, String[]> responses;
    private Random random = new Random();

   
    public chatBotGUI() {
        initComponents();
        
        // Set window properties
        setTitle("Flight ChatBot");
        setSize(400, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0xE0FBFC)); // Light blue

        // Chat panel setup
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        userInput = new JTextField();
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
        displayMessage("Hi! I'm your flight assistant bot. How can I help you today? ✈️", false);
    }
  
     private void sendMessage() {
        String text = userInput.getText().trim();
        if (text.isEmpty()) return;

        displayMessage(text, true);
        userInput.setText("");

        JLabel typingLabel = createTypingLabel();
        chatPanel.add(typingLabel);
        scrollToBottom();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                SwingUtilities.invokeLater(() -> {
                    chatPanel.remove(typingLabel);
                    String response = handleInput(text.toLowerCase());
                    displayMessage(response, false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
      
    private JLabel createTypingLabel() {
        JLabel label = new JLabel("Bot is typing...");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        label.setBorder(new EmptyBorder(5, 15, 5, 15));
        return label;
    }
   
     private String handleInput(String input) {
        if (input.contains("flight") && input.contains("from") && input.contains("to")) {
            // Example: "Find me a flight from LON to NYC on 2024-06-15"
            String[] parts = input.split(" ");
            String from = "", to = "", date = "2025-07-01"; // Default date
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("from") && i + 1 < parts.length) {
                    from = parts[i + 1].toUpperCase();
                } else if (parts[i].equals("to") && i + 1 < parts.length) {
                    to = parts[i + 1].toUpperCase();
                } else if (parts[i].matches("\\d{4}-\\d{2}-\\d{2}")) {
                    date = parts[i];
                }
            }
            return searchFlights(from, to, date);
        }

        return "Sorry, I didn't understand that. Try asking something like: 'Find me a flight from LON to NYC on 2025-07-01'.";
    }
    
  
     private Properties loadConfig() {
    Properties prop = new Properties();
    try (FileInputStream fis = new FileInputStream("config.properties")) {
        prop.load(fis);
        System.out.println("✅ Loaded API Key: " + prop.getProperty("RAPIDAPI_KEY"));
    } catch (Exception e) {
        System.out.println("❌ Unable to load config.properties");
        e.printStackTrace();
    }
    return prop;
}





    
    private void displayMessage(String message, boolean isUser) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);

        JTextArea bubble = new JTextArea(message);
        bubble.setLineWrap(true);
        bubble.setWrapStyleWord(true);
        bubble.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bubble.setEditable(false);
        bubble.setFocusable(false);
        bubble.setBackground(isUser ? new Color(0x005f73) : new Color(0x94d2bd));
        bubble.setForeground(Color.WHITE);
        bubble.setBorder(new EmptyBorder(6, 10, 6, 10));
        bubble.setMaximumSize(new Dimension(260, Integer.MAX_VALUE));
        bubble.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isUser) {
            wrapper.add(Box.createHorizontalGlue());
            wrapper.add(bubble);
        } else {
            wrapper.add(bubble);
            wrapper.add(Box.createHorizontalGlue());
        }

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }


     
     private void scrollToBottom() {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
    SwingUtilities.invokeLater(() -> bar.setValue(bar.getMaximum()));
    }
     
    private String searchFlights(String from, String to, String departDate) {
    try {
        Properties config = loadConfig();
        String apiKey = config.getProperty("RAPIDAPI_KEY");
        String host = config.getProperty("RAPIDAPI_HOST");

        String apiUrl = "https://flights-sky.p.rapidapi.com/google/flights/get-booking-results"
                        + from + "&arrivalAirportCode=" + to + "&departureDate=" + departDate;

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-RapidAPI-Key", apiKey);
        conn.setRequestProperty("X-RapidAPI-Host", host);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            return "❌ Error: Failed to fetch flight data. HTTP code " + responseCode;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        conn.disconnect();

        // For demonstration, just return a preview
        return "✈️ Sample Flight Data: " + response.substring(0, Math.min(500, response.length())) + "...";

    } catch (Exception e) {
        e.printStackTrace();
        return "❌ Error: Exception while fetching flight data.";
    }
}



     public static void main(String[] args) {
        SwingUtilities.invokeLater(chatBotGUI::new);
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(chatBotGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(chatBotGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(chatBotGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(chatBotGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new chatBotGUI().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
