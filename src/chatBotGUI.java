import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;

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
        
        setTitle("ChatBot");
        setSize(400, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Chat Panel Setup
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        userInput = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Setup Responses
        responses = new HashMap<>();
        responses.put("hello", new String[]{"Hey there!", "Hello! How can I help you?"});
        responses.put("book flight", new String[]{"I found 17 results for flights ✈️", "Looking up flights..."});
        responses.put("bye", new String[]{"Goodbye!", "See you soon!"});

        // Event Handling
        sendButton.addActionListener(e -> sendMessage());
        userInput.addActionListener(e -> sendMessage());

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void sendMessage() {
        String text = userInput.getText().trim();
        if (text.isEmpty()) return;

        chatPanel.add(new MessageBubble(text, true));
        userInput.setText("");
        scrollToBottom();

        // Bot typing simulation
        JLabel typingLabel = createTypingLabel();
        chatPanel.add(typingLabel);
        scrollToBottom();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                SwingUtilities.invokeLater(() -> {
                    chatPanel.remove(typingLabel);
                    chatPanel.add(new MessageBubble(getBotResponse(text.toLowerCase()), false));
                    scrollToBottom();
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
   
     private String getBotResponse(String input) {
        if (responses.containsKey(input)) {
            String[] replies = responses.get(input);
            return replies[random.nextInt(replies.length)];
        }
        return "I'm not sure how to respond to that. 🤔";
    }
     
      class MessageBubble extends JPanel {
        public MessageBubble(String text, boolean isUser) {
            setLayout(new BorderLayout());
            setOpaque(false);

            JTextArea textArea = new JTextArea(text);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textArea.setForeground(Color.WHITE);
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            Color bubbleColor = isUser ? new Color(0x1E90FF) : new Color(0x6A5ACD);
            JPanel bubble = new JPanel(new BorderLayout());
            bubble.setBackground(bubbleColor);
            bubble.setOpaque(true);
            bubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            bubble.add(textArea, BorderLayout.CENTER);
            bubble.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
            bubble.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bubbleColor, 10, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));

            JLabel avatar = new JLabel(new ImageIcon(isUser ? "user.png" : "bot.png"));

            if (isUser) {
                add(bubble, BorderLayout.EAST);
                add(avatar, BorderLayout.WEST);
            } else {
                add(avatar, BorderLayout.WEST);
                add(bubble, BorderLayout.CENTER);
            }

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
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
