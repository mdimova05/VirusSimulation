import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Main GUI for the virus simulation.
 * Based on CritterGui structure but simplified.
 */
@SuppressWarnings("deprecation")
public class VirusSimulation implements ActionListener {
    private static final String TITLE = "Virus Spread Simulation";
    
    private VirusModel model;
    private VirusPanel panel;
    private JFrame frame;
    private javax.swing.Timer timer;
    private JButton go, stop, tick, reset;
    private JLabel statusLabel;
    private JPanel eastPanel;
    private Map<String, ClassPanel> classPanels;
    
    /**
     * Main method to start the simulation.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VirusSimulation sim = new VirusSimulation();
            sim.start();
        });
    }
    
    /**
     * Constructs the simulation GUI.
     */
    public VirusSimulation() {
        // Create model
        model = new VirusModel(Config.DEFAULT_WIDTH, Config.DEFAULT_HEIGHT);
        
        // Add initial population
        model.add(Config.INITIAL_VACCINATED_COUNT, VaccinatedPerson.class);
        model.add(1, SuperSpreader.class);
        model.add(Config.INITIAL_INFECTED - 1, SickPerson.class);
        model.add(Config.DEFAULT_PERSON_COUNT - Config.INITIAL_INFECTED, HealthyPerson.class);
        
        // Create panel
        panel = new VirusPanel(model);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Create timer
        timer = new javax.swing.Timer(Config.UPDATE_DELAY_MS, this);
        timer.setCoalesce(true);
        
        // Create control buttons
        JPanel controlPanel = new JPanel();
        go = createButton("Go", 'G', controlPanel);
        go.setBackground(Color.GREEN);
        stop = createButton("Stop", 'S', controlPanel);
        stop.setBackground(new Color(255, 96, 96));
        tick = createButton("Tick", 'T', controlPanel);
        tick.setBackground(Color.YELLOW);
        reset = createButton("Reset", 'R', controlPanel);
        
        // Create status label
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        updateStatusLabel();
        
        // Create east panel for statistics
        classPanels = new HashMap<>();
        eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        updateClassPanels();
        
        // Create frame
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel();
        centerPanel.add(panel);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(controlPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.WEST);
        
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(eastPanel), BorderLayout.EAST);
        
        doEnabling();
    }
    
    /**
     * Starts the simulation GUI.
     */
    public void start() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        
        if (src == go) {
            timer.start();
        } else if (src == stop) {
            timer.stop();
        } else if (src == tick) {
            model.update();
            updateStatusLabel();
            updateClassPanels();
        } else if (src == reset) {
            timer.stop();
            model.reset();
            
            // Re-add initial population
            model.add(Config.INITIAL_VACCINATED_COUNT, VaccinatedPerson.class);
            model.add(1, SuperSpreader.class);
            model.add(Config.INITIAL_INFECTED - 1, SickPerson.class);
            model.add(Config.DEFAULT_PERSON_COUNT - Config.INITIAL_INFECTED, HealthyPerson.class);
            
            updateStatusLabel();
            updateClassPanels();
        } else if (src == timer) {
            model.update();
            updateStatusLabel();
            updateClassPanels();
        }
        
        doEnabling();
    }
    
    /**
     * Updates which buttons are enabled.
     */
    private void doEnabling() {
        boolean running = timer.isRunning();
        go.setEnabled(!running);
        stop.setEnabled(running);
        tick.setEnabled(!running);
        reset.setEnabled(!running);
    }
    
    /**
     * Updates the status label.
     */
    private void updateStatusLabel() {
        statusLabel.setText("Moves: " + model.getMoveCount());
    }
    
    /**
     * Updates the class statistics panels.
     */
    private void updateClassPanels() {
        Set<String> classNames = model.getClassNames();
        
        // Add new panels if needed
        for (String className : classNames) {
            if (!classPanels.containsKey(className)) {
                ClassPanel cp = new ClassPanel(className);
                classPanels.put(className, cp);
                eastPanel.add(cp);
            }
        }
        
        // Update all panels
        for (ClassPanel cp : classPanels.values()) {
            cp.update();
        }
        
        eastPanel.revalidate();
        eastPanel.repaint();
    }
    
    /**
     * Helper to create a button.
     */
    private JButton createButton(String text, char mnemonic, Container parent) {
        JButton button = new JButton(text);
        button.setMnemonic(mnemonic);
        button.addActionListener(this);
        parent.add(button);
        return button;
    }
    
    /**
     * Panel showing statistics for one person class.
     */
    private class ClassPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private String className;
        private JLabel label;
        
        ClassPanel(String className) {
            this.className = className;
            setBorder(BorderFactory.createTitledBorder(getShortName()));
            label = new JLabel();
            label.setFont(new Font("Monospaced", Font.PLAIN, 11));
            add(label);
            update();
        }
        
        void update() {
            int count = model.getCount(className);
            int deaths = model.getDeaths(className);
            label.setText(String.format("<html>Alive: %3d<br>Dead: %3d</html>", count, deaths));
        }
        
        String getShortName() {
            // Get just the class name without package
            int lastDot = className.lastIndexOf('.');
            return lastDot >= 0 ? className.substring(lastDot + 1) : className;
        }
    }
}
