import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Panel for displaying the virus simulation grid.
 * Based on CritterPanel structure.
 */
public class VirusPanel extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    
    private static final Font FONT = new Font("Monospaced", Font.BOLD, Config.FONT_SIZE);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    
    private VirusModel model;
    
    /**
     * Constructs a panel to display the given model.
     */
    public VirusPanel(VirusModel model) {
        this.model = model;
        model.addObserver(this);
        
        setFont(FONT);
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(
            Config.FONT_SIZE * model.getWidth() + 1,
            Config.FONT_SIZE * model.getHeight() + Config.FONT_SIZE / 2
        ));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= model.getWidth(); x++) {
            g.drawLine(x * Config.FONT_SIZE, 0, x * Config.FONT_SIZE, getHeight());
        }
        for (int y = 0; y <= model.getHeight(); y++) {
            g.drawLine(0, y * Config.FONT_SIZE, getWidth(), y * Config.FONT_SIZE);
        }
        
        // Draw all people
        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                String text = model.getString(x, y);
                Color color = model.getColor(x, y);
                
                if (!text.equals(VirusModel.EMPTY)) {
                    int drawX = x * Config.FONT_SIZE + 2;
                    int drawY = (y + 1) * Config.FONT_SIZE - 2;
                    
                    // Draw shadow
                    g.setColor(Color.BLACK);
                    g.drawString(text, drawX + 1, drawY + 1);
                    
                    // Draw text
                    g.setColor(color);
                    g.drawString(text, drawX, drawY);
                }
            }
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }
}
