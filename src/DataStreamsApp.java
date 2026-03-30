import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.*;

public class DataStreamsApp extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private JLabel fileLabel;
    private JLabel statusLabel;

    private String currentFilePath = null;

    public DataStreamsApp() {
        super("DataStreams – Java Stream File Search");
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        // ── Color palette
        Color bgDark    = new Color(30, 32, 40);
        Color bgCard    = new Color(40, 43, 54);
        Color accent    = new Color(99, 179, 237);
        Color accentAlt = new Color(154, 230, 180);
        Color textMain  = new Color(230, 232, 240);
        Color textMuted = new Color(130, 135, 155);
        Color border    = new Color(60, 65, 80);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(bgDark);
        setContentPane(root);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(22, 24, 31));
        topBar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("DataStreams  ·  Java Stream File Search");
        title.setFont(new Font("Courier New", Font.BOLD, 16));
        title.setForeground(accent);

        fileLabel = new JLabel("No file loaded");
        fileLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
        fileLabel.setForeground(textMuted);

        topBar.add(title,     BorderLayout.WEST);
        topBar.add(fileLabel, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        centerPanel.setBackground(bgDark);
        centerPanel.setBorder(new EmptyBorder(12, 16, 8, 16));

        originalTextArea = buildTextArea(textMain, bgCard);
        filteredTextArea = buildTextArea(accentAlt, bgCard);

        centerPanel.add(buildScrollPane("Original File", originalTextArea, accent,   bgCard, border));
        centerPanel.add(buildScrollPane("Filtered Results", filteredTextArea, accentAlt, bgCard, border));

        root.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(22, 24, 31));
        bottomPanel.setBorder(new EmptyBorder(10, 20, 14, 20));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(new Color(22, 24, 31));

        JLabel searchLabel = new JLabel("Search String:");
        searchLabel.setFont(new Font("Courier New", Font.BOLD, 13));
        searchLabel.setForeground(textMain);

        searchField = new JTextField(28);
        searchField.setFont(new Font("Courier New", Font.PLAIN, 13));
        searchField.setBackground(bgCard);
        searchField.setForeground(textMain);
        searchField.setCaretColor(accent);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                new EmptyBorder(4, 8, 4, 8)));

        searchField.addActionListener(e -> doSearch());

        searchRow.add(searchLabel);
        searchRow.add(searchField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttonRow.setBackground(new Color(22, 24, 31));

        loadButton   = buildButton("Load File",   accent,    bgCard);
        searchButton = buildButton("Search",      accentAlt, bgCard);
        quitButton   = buildButton("Quit",        new Color(250, 120, 120), bgCard);

        searchButton.setEnabled(false);

        loadButton.addActionListener(e   -> doLoad());
        searchButton.addActionListener(e -> doSearch());
        quitButton.addActionListener(e   -> System.exit(0));

        buttonRow.add(loadButton);
        buttonRow.add(searchButton);
        buttonRow.add(quitButton);


        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Courier New", Font.ITALIC, 11));
        statusLabel.setForeground(textMuted);

        bottomPanel.add(searchRow);
        bottomPanel.add(buttonRow);
        bottomPanel.add(statusLabel);

        root.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTextArea buildTextArea(Color fg, Color bg) {
        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Courier New", Font.PLAIN, 12));
        ta.setForeground(fg);
        ta.setBackground(bg);
        ta.setCaretColor(fg);
        ta.setEditable(false);
        ta.setLineWrap(false);
        ta.setMargin(new Insets(8, 10, 8, 10));
        return ta;
    }

    private JPanel buildScrollPane(String title, JTextArea ta,
                                   Color titleColor, Color bg, Color borderColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(8, 0, 0, 0)));

        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Courier New", Font.BOLD, 13));
        lbl.setForeground(titleColor);
        lbl.setBorder(new EmptyBorder(0, 6, 6, 0));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(bg);
        styleScrollBar(sp.getVerticalScrollBar(),   bg);
        styleScrollBar(sp.getHorizontalScrollBar(), bg);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(sp,  BorderLayout.CENTER);
        return panel;
    }

    private void styleScrollBar(JScrollBar bar, Color bg) {
        bar.setBackground(bg);
        bar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor      = new Color(70, 75, 95);
                trackColor      = bg;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
    }

    private JButton buildButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Courier New", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1),
                new EmptyBorder(6, 18, 6, 18)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg.darker()); btn.setForeground(Color.WHITE); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg);         btn.setForeground(fg); }
        });
        return btn;
    }

    private void doLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a Text File");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentFilePath = chooser.getSelectedFile().getAbsolutePath();
            fileLabel.setText(chooser.getSelectedFile().getName());

            originalTextArea.setText("");
            filteredTextArea.setText("");

            try (Stream<String> lines = Files.lines(Paths.get(currentFilePath))) {
                lines.forEach(line -> originalTextArea.append(line + "\n"));
                statusLabel.setText("Loaded: " + currentFilePath);
                searchButton.setEnabled(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file:\n" + ex.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error loading file.");
                currentFilePath = null;
                searchButton.setEnabled(false);
            }
        }
    }

    private void doSearch() {
        if (currentFilePath == null) {
            JOptionPane.showMessageDialog(this,
                    "Please load a file first.", "No File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String searchStr = searchField.getText().trim();
        if (searchStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search string.", "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }

        filteredTextArea.setText("");

        try (Stream<String> lines = Files.lines(Paths.get(currentFilePath))) {

            List<String> matches = lines
                    .filter(line -> line.contains(searchStr))   // lambda filter
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                filteredTextArea.setText("(no lines matched \"" + searchStr + "\")");
                statusLabel.setText("Search complete – 0 matches for \"" + searchStr + "\"");
            } else {
                matches.forEach(line -> filteredTextArea.append(line + "\n"));
                statusLabel.setText("Search complete – " + matches.size()
                        + " line(s) matched \"" + searchStr + "\"");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error during search.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DataStreamsApp();
        });
    }
}
