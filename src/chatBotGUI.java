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
import okhttp3.*;


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

        // Chat panel setup
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Scroll pane for chat
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        userInput = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> sendMessage());

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        setVisible(true);

        // Show welcome message
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
                    displayMessage(getBotResponse(text.toLowerCase()), false);
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
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return label;
    }
   
    private String getBotResponse(String input) {
        return callChatGPTAPI(input);
    }

    private String callChatGPTAPI(String userMessage) {
    try {
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "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");
        conn.setDoOutput(true);

        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", userMessage);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageObj);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "gpt-3.5-turbo");
        jsonBody.add("messages", messagesArray);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        return jsonResponse.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

    } catch (Exception e) {
        e.printStackTrace();
        return "Sorry, there was an error connecting to the chatbot API.";
    }
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
    bubble.setBackground(isUser ? new Color(0xFF6F61) : new Color(0x6A5ACD));
    bubble.setForeground(Color.WHITE);
    bubble.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    bubble.setMaximumSize(new Dimension(260, Integer.MAX_VALUE));

    if (isUser) {
        wrapper.add(Box.createHorizontalGlue()); // push to the right
        wrapper.add(bubble);
    } else {
        wrapper.add(bubble);
        wrapper.add(Box.createHorizontalGlue()); // push to the left
    }

    chatPanel.add(wrapper);
    chatPanel.revalidate();
    chatPanel.repaint();
    scrollToBottom();
}

     
     private void scrollToBottom() {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
    SwingUtilities.invokeLater(() -> bar.setValue(bar.getMaximum()));
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
