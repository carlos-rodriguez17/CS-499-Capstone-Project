import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SlideShow extends JFrame {

    private JPanel slidePane;
    private JPanel textPane;
    private JPanel buttonPane;
    private CardLayout cardSlides;
    private CardLayout cardText;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 16);
    private static final Color BUTTON_BG = new Color(33, 150, 243); // Material Blue
    private static final Color BUTTON_HOVER = new Color(30, 136, 229);
    private static final Color BUTTON_PRESS = new Color(25, 118, 210); // Darker when clicked
    private static final Color TEXT_BG = new Color(30, 144, 255); // DodgerBlue

    public SlideShow() throws HeadlessException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Silent fail
        }
        initComponent();
    }

    private void initComponent() {
        cardSlides = new CardLayout();
        cardText = new CardLayout();

        slidePane = new JPanel(cardSlides);
        slidePane.setPreferredSize(new Dimension(800, 500));
        slidePane.setBackground(Color.WHITE);

        textPane = new JPanel(cardText);
        textPane.setBackground(TEXT_BG);
        textPane.setPreferredSize(new Dimension(800, 60));
        textPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        buttonPane.setBackground(new Color(245, 245, 245));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPrev = createStyledButton("Previous");
        btnNext = createStyledButton("Next");
        btnAdd = createStyledButton("Add");
        btnUpdate = createStyledButton("Update");
        btnDelete = createStyledButton("Delete");

        btnPrev.addActionListener(e -> goPrevious());
        btnNext.addActionListener(e -> goNext());
        btnAdd.addActionListener(e -> addSlide());
        btnUpdate.addActionListener(e -> updateSlide());
        btnDelete.addActionListener(e -> deleteSlide());

        buttonPane.add(btnPrev);
        buttonPane.add(btnNext);
        buttonPane.add(btnAdd);
        buttonPane.add(btnUpdate);
        buttonPane.add(btnDelete);

        setTitle("Top 5 Destinations SlideShow");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(slidePane, BorderLayout.CENTER);
        getContentPane().add(textPane, BorderLayout.NORTH);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        loadSlidesFromDatabase();
    }

    // Modern button with fade, scale, press, and glow effects
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(BUTTON_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(110, 38)); // Smaller size
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Initial opacity and scale
        btn.putClientProperty("opacity", 0.4f);
        btn.putClientProperty("scale", 1.0f);

        // Custom paint for rounded corners + glow
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                float opacity = (Float) btn.getClientProperty("opacity");
                float scale = (Float) btn.getClientProperty("scale");

                // Glow effect on hover
                if (opacity > 0.5f) {
                    g2.setColor(new Color(33, 150, 243, 80)); // Semi-transparent glow
                    g2.fillRoundRect(-5, -5, c.getWidth() + 10, c.getHeight() + 10, 25, 25);
                }

                // Main button background
                g2.setColor(btn.getBackground());
                int w = (int) (c.getWidth() * scale);
                int h = (int) (c.getHeight() * scale);
                int x = (c.getWidth() - w) / 2;
                int y = (c.getHeight() - h) / 2;
                g2.fillRoundRect(x, y, w, h, 20, 20);

                super.paint(g2, c);
                g2.dispose();
            }
        });

        // Hover + press effects
        btn.addMouseListener(new MouseAdapter() {
            Timer fadeTimer = new Timer(20, e -> {
                float opacity = (Float) btn.getClientProperty("opacity");
                opacity += 0.08f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                btn.putClientProperty("opacity", opacity);
                btn.repaint();
            });

            Timer fadeOutTimer = new Timer(20, e -> {
                float opacity = (Float) btn.getClientProperty("opacity");
                opacity -= 0.08f;
                if (opacity <= 0.4f) {
                    opacity = 0.4f;
                    ((Timer) e.getSource()).stop();
                }
                btn.putClientProperty("opacity", opacity);
                btn.repaint();
            });

            Timer scaleUpTimer = new Timer(20, e -> {
                float scale = (Float) btn.getClientProperty("scale");
                scale += 0.02f;
                if (scale >= 1.08f) {
                    scale = 1.08f;
                    ((Timer) e.getSource()).stop();
                }
                btn.putClientProperty("scale", scale);
                btn.repaint();
            });

            Timer scaleDownTimer = new Timer(20, e -> {
                float scale = (Float) btn.getClientProperty("scale");
                scale -= 0.02f;
                if (scale <= 1.0f) {
                    scale = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                btn.putClientProperty("scale", scale);
                btn.repaint();
            });

            @Override
            public void mouseEntered(MouseEvent e) {
                fadeOutTimer.stop();
                fadeTimer.start();
                scaleDownTimer.stop();
                scaleUpTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fadeTimer.stop();
                fadeOutTimer.start();
                scaleUpTimer.stop();
                scaleDownTimer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(BUTTON_PRESS);
                btn.putClientProperty("scale", 0.95f); // Slight shrink on press
                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(BUTTON_BG);
                btn.putClientProperty("scale", 1.08f); // Back to hover scale
                btn.repaint();
            }
        });

        return btn;
    }

    private void loadSlidesFromDatabase() {
        List<Slide> slides = DatabaseManager.getSlides();
        int index = 1;

        slidePane.removeAll();
        textPane.removeAll();

        for (Slide slide : slides) {
            JLabel lblSlide = new JLabel();
            lblSlide.setHorizontalAlignment(JLabel.CENTER);

            ImageIcon icon = null;
            try {
                if (slide.getImage().startsWith("/")) {
                    icon = new ImageIcon(getClass().getResource(slide.getImage()));
                } else {
                    icon = new ImageIcon(slide.getImage());
                }
                Image scaled = icon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH);
                lblSlide.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                lblSlide.setText("Image not found: " + slide.getImage());
            }

            JLabel lblText = new JLabel("<html><body><font size='5'>" +
                                        slide.getDescription() +
                                        "</font></body></html>");
            lblText.setHorizontalAlignment(JLabel.CENTER);
            lblText.setForeground(Color.WHITE);
            lblText.setFont(TITLE_FONT);

            slidePane.add(lblSlide, "card" + index);
            textPane.add(lblText, "cardText" + index);
            index++;
        }

        slidePane.revalidate();
        slidePane.repaint();
        textPane.revalidate();
        textPane.repaint();
    }

    private void goPrevious() {
        cardSlides.previous(slidePane);
        cardText.previous(textPane);
    }

    private void goNext() {
        cardSlides.next(slidePane);
        cardText.next(textPane);
    }

    private void addSlide() {
        String image = JOptionPane.showInputDialog(this, "Enter image filename (e.g. Slide6.jpg):");
        String description = JOptionPane.showInputDialog(this, "Enter description:");
        String url = JOptionPane.showInputDialog(this, "Enter URL (e.g. https://example.com):");

        if (image != null && description != null && url != null && !image.trim().isEmpty()) {
            DatabaseManager.addSlide(image.trim(), description.trim(), url.trim());
            slidePane.removeAll();
            textPane.removeAll();
            loadSlidesFromDatabase();
            JOptionPane.showMessageDialog(this, "Slide added!");
        }
    }

    private void updateSlide() {
        String idStr = JOptionPane.showInputDialog(this, "Enter slide ID to update:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            String image = JOptionPane.showInputDialog(this, "New image filename:");
            String description = JOptionPane.showInputDialog(this, "New description:");
            String url = JOptionPane.showInputDialog(this, "New URL:");

            if (image != null && description != null && url != null) {
                DatabaseManager.updateSlide(id, image.trim(), description.trim(), url.trim());
                slidePane.removeAll();
                textPane.removeAll();
                loadSlidesFromDatabase();
                JOptionPane.showMessageDialog(this, "Slide updated!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSlide() {
        String idStr = JOptionPane.showInputDialog(this, "Enter slide ID to delete:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete slide ID " + id + "?", "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.deleteSlide(id);
                slidePane.removeAll();
                textPane.removeAll();
                loadSlidesFromDatabase();
                JOptionPane.showMessageDialog(this, "Slide deleted!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SlideShow ss = new SlideShow();
            ss.setVisible(true);
        });
    }
}