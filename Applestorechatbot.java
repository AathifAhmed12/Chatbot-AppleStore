import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Applestorechatbot {

    HashMap<String, String> knowledge;
    JPanel chatPanel;
    JTextField inputField;
    JScrollPane scrollPane;
    String userName = "";
    String lastInput = "";
    int repeatCount = 0;

    public Applestorechatbot() {
        knowledge = new HashMap<>();
        knowledge.put("hello", "Hi there! Welcome to the Apple Store. how can i help you?");
        knowledge.put("hi", "Hi! Welcome to the Apple Store. how can i help you?");
        knowledge.put("your name", "I'm Apple, your Apple Store assistant!");
        knowledge.put("thank you", "You're welcome!");
        knowledge.put("need help", "Sure! What would you like help with?");
        knowledge.put("recommend phone", "Sure, tell me your preferences!");
        knowledge.put("good morning", "Good morning, have a nice day.");
        knowledge.put("good evening", "Good evening!, how can i help you ");
        knowledge.put("good night", "Good night!, how can i help you");

        createGUI();
    }

    void createGUI() {
        JFrame frame = new JFrame("Apple - Apple Talks");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Font emojiFont = new Font("Apple Color Emoji", Font.PLAIN, 15);

        // Chat Panel instead of JTextArea
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        inputField = new JTextField();
        inputField.setFont(emojiFont);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(emojiFont);

        ActionListener sendListener = e -> {
            String userText = inputField.getText().trim();
            if (!userText.isEmpty()) {
                addMessage("You: " + userText, true); // right side
                respond(userText);
                inputField.setText("");
            }
        };

        sendButton.addActionListener(sendListener);
        inputField.addActionListener(sendListener);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    void addMessage(String message, boolean isUser) {
        JLabel msgLabel = new JLabel(message);
        msgLabel.setOpaque(true);
        msgLabel.setBackground(isUser ? new Color(220, 248, 198) : new Color(240, 240, 240));
        msgLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        msgLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 12));
        msgLabel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));

        JPanel msgPanel = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));
        msgPanel.setBackground(Color.WHITE);
        msgPanel.add(msgLabel);

        chatPanel.add(msgPanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }

    void respond(String userInput) {
        String input = userInput.toLowerCase();
        String[] tokens = SimpleTokenizer.INSTANCE.tokenize(input);
        input = String.join(" ", tokens);

        if (input.equals(lastInput)) {
            repeatCount++;
        } else {
            repeatCount = 1;
            lastInput = input;
        }

        String emoji = "😊";
        if (repeatCount >= 4) {
            emoji = "😠";
        } else if (repeatCount >= 2) {
            emoji = "😐";
        }

        if (input.contains("exit") || input.contains("close")) {
            addMessage("Apple " + emoji + ": Goodbye! Closing the chatbot.", false);
int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation",
    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
if (confirm == JOptionPane.YES_OPTION)
            System.exit(0);
            return;
        }

        boolean answered = handleInput(input, emoji);
        if (!answered) {
            String teach = (String) JOptionPane.showInputDialog(
    null,
    "I don't know the answer. Can you teach/update me?",
    "Update data to Me",
    JOptionPane.PLAIN_MESSAGE,  
    null,                       
    null,
    null
);
            if (teach != null && !teach.trim().isEmpty()) {
                knowledge.put(input, teach.trim());
                addMessage("Apple " + emoji + ": Thanks! I’ll remember that.", false);
            } else {
                addMessage("Apple " + emoji + ": Okay, ask something else.", false);
            }
        }
    }

    boolean handleInput(String input, String emoji) {
        if (input.contains("bye") || input.contains("goodbye")) {
            addMessage("Apple " + emoji + ": Goodbye " + (userName.isEmpty() ? "!" : userName + "!"), false);
            return true;
        }

        if (input.contains("sorry")) {
            repeatCount = 0;
            lastInput = "";
            addMessage("Apple " + emoji + ": It's okay! Let's continue.", false);
            return true;
        }

        if (input.contains("name") && input.contains("your")) {
            addMessage("Apple " + emoji + ": I'm Apple! What's your name?", false);
		String name = (String) JOptionPane.showInputDialog(
    null,
    "What’s your name?",
    "Your Name",
    JOptionPane.PLAIN_MESSAGE,
    null,
    null,
    null
);
            if (name != null && !name.trim().isEmpty()) {
                userName = name.trim();
                addMessage("Apple " + emoji + ": Nice to meet you, " + userName + "!", false);
            }
            return true;
        }

        if (input.contains("how are you")) {
            String[] responses = {"I'm doing great!", "Feeling good!", "Good,Ready to help!"};
            addMessage("Apple " + emoji + ": " + responses[new Random().nextInt(responses.length)], false);
            return true;
        }

        if (input.contains("model") || input.contains("available models") || input.contains("available phones")
                || input.contains("iphones") || input.contains("apple phones")) {
            String series = extractSeries(input);
            if (!series.isEmpty()) {
                addMessage("Apple " + emoji + ": iPhone " + series + " series models:", false);
                showFromFileFiltered("models.txt", "iphone " + series);
            } else {
                addMessage("Apple " + emoji + ": Here are our iPhone models:", false);
                showFromFile("models.txt");
            }
            return true;
        }

        if (input.contains("price") || input.contains("cost")) {
            String series = extractSeries(input);
            if (!series.isEmpty()) {
                addMessage("Apple " + emoji + ": Prices for iPhone " + series + " series:", false);
                showFromFileFiltered("prices.txt", "iphone " + series);
            } else {
                addMessage("Apple " + emoji + ": Here's the iPhone price list:", false);
                showFromFile("prices.txt");
            }
            return true;
        }

        if (input.contains("color")) {
            String series = extractSeries(input);
            if (!series.isEmpty()) {
                addMessage("Apple " + emoji + ": Available colors for iPhone " + series + ":", false);
                showFromFileFiltered("colors.txt", "iphone " + series);
            } else {
                addMessage("Apple " + emoji + ": Here's the list of available colors:", false);
                showFromFile("colors.txt");
            }
            return true;
        }

        if (input.contains("branch") || input.contains("store") || input.contains("where i can purchase")
                || input.contains("locations")) {
            addMessage("Apple " + emoji + ": Here are our Apple Store branches:", false);
            showFromFile("branches.txt");
            return true;
        }

        if (input.contains("faq") || input.contains("frequent")) {
            addMessage("Apple " + emoji + ": Here are some frequently asked questions:", false);
            showFromFile("faq.txt");
            return true;
        }

        for (String key : knowledge.keySet()) {
            if (input.contains(key)) {
                addMessage("Apple " + emoji + ": " + knowledge.get(key), false);
                return true;
            }
        }

        return false;
    }

    void showFromFile(String fileName) {
        try (Scanner s = new Scanner(new File(fileName))) {
            while (s.hasNextLine()) {
                addMessage(" - " + s.nextLine(), false);
            }
        } catch (Exception e) {
            addMessage("Apple: Couldn't load data from " + fileName, false);
        }
    }

    void showFromFileFiltered(String fileName, String keyword) {
        try (Scanner s = new Scanner(new File(fileName))) {
            while (s.hasNextLine()) {
                String line = s.nextLine().toLowerCase();
                if (line.contains(keyword)) {
                    addMessage(" - " + line, false);
                }
            }
        } catch (Exception e) {
            addMessage("Apple: Error reading " + fileName, false);
        }
    }

    String extractSeries(String input) {
        for (int i = 10; i <= 16; i++) {
            if (input.contains("iphone " + i)) {
                return String.valueOf(i);
            }
        }
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Applestorechatbot::new);
} 
}
