package com.gestion.cours.ui;

import com.gestion.cours.ui.panels.*;
import com.gestion.cours.util.DatabaseConfig;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Fenêtre principale de l'application de gestion de cours
 */
public class MainFrame extends JFrame {
    private static final String TITLE = "Gestion de Cours d'Apprentissage";
    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 800);
    
    private JTabbedPane tabbedPane;
    
    // Panels pour les différentes fonctionnalités
    private CoursPanel coursPanel;
    private EtudiantPanel etudiantPanel;
    private ProfesseurPanel professeurPanel;
    private InscriptionPanel inscriptionPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        initializeFrame();
        createMenuBar();
        createTabbedPane();
        setupEventListeners();
        
        // Vérifier la connexion à la base de données au démarrage
        verifyDatabaseConnection();
    }
    
    private void initializeFrame() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(DEFAULT_SIZE);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        
        // Configurer le look and feel
        try {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Fallback au look and feel système
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception ex) {
                // Ignorer, utiliser le défaut
            }
        }
    }
    
    private Image createAppIcon() {
        // Créer une icône simple pour l'application
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner un livre ouvert stylisé
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRoundRect(4, 8, 24, 16, 4, 4);
        g2d.setColor(Color.WHITE);
        g2d.drawLine(16, 12, 16, 20);
        g2d.drawLine(8, 14, 14, 14);
        g2d.drawLine(18, 14, 24, 14);
        g2d.drawLine(8, 18, 14, 18);
        g2d.drawLine(18, 18, 24, 18);
        
        g2d.dispose();
        return icon;
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.setMnemonic('F');
        
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.setMnemonic('Q');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        // Menu Gestion
        JMenu gestionMenu = new JMenu("Gestion");
        gestionMenu.setMnemonic('G');
        
        JMenuItem coursItem = new JMenuItem("Cours");
        coursItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JMenuItem etudiantItem = new JMenuItem("Étudiants");
        etudiantItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JMenuItem professeurItem = new JMenuItem("Professeurs");
        professeurItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        JMenuItem inscriptionItem = new JMenuItem("Inscriptions");
        inscriptionItem.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        
        gestionMenu.add(coursItem);
        gestionMenu.add(etudiantItem);
        gestionMenu.add(professeurItem);
        gestionMenu.addSeparator();
        gestionMenu.add(inscriptionItem);
        
        // Menu Outils
        JMenu toolsMenu = new JMenu("Outils");
        toolsMenu.setMnemonic('O');
        
        JMenuItem refreshItem = new JMenuItem("Actualiser");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> refreshAllPanels());
        
        JMenuItem dbTestItem = new JMenuItem("Tester la connexion DB");
        dbTestItem.addActionListener(e -> testDatabaseConnection());
        
        toolsMenu.add(refreshItem);
        toolsMenu.addSeparator();
        toolsMenu.add(dbTestItem);
        
        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        helpMenu.setMnemonic('A');
        
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(gestionMenu);
        menuBar.add(toolsMenu);
        menuBar.add(Box.createHorizontalGlue()); // Pousser le menu Aide à droite
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Créer et ajouter les panels
        dashboardPanel = new DashboardPanel();
        coursPanel = new CoursPanel();
        etudiantPanel = new EtudiantPanel();
        professeurPanel = new ProfesseurPanel();
        inscriptionPanel = new InscriptionPanel();
        
        tabbedPane.addTab("📊 Tableau de bord", dashboardPanel);
        tabbedPane.addTab("📚 Cours", coursPanel);
        tabbedPane.addTab("👨‍🎓 Étudiants", etudiantPanel);
        tabbedPane.addTab("👨‍🏫 Professeurs", professeurPanel);
        tabbedPane.addTab("📝 Inscriptions", inscriptionPanel);
        
        // Ajouter listener pour actualiser les panels quand on change d'onglet
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof RefreshablePanel) {
                ((RefreshablePanel) selectedComponent).refresh();
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Créer la barre de statut
        createStatusBar();
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(0, 25));
        
        JLabel statusLabel = new JLabel("Prêt");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        JLabel connectionLabel = new JLabel("DB: Connecté");
        connectionLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        connectionLabel.setForeground(new Color(0, 128, 0));
        
        statusBar.add(statusLabel, BorderLayout.CENTER);
        statusBar.add(connectionLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Gérer la fermeture de l'application
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Raccourcis clavier globaux
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F5"), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });
    }
    
    private void verifyDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return DatabaseConfig.getInstance().testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (!connected) {
                        showDatabaseErrorDialog();
                    }
                } catch (Exception e) {
                    showDatabaseErrorDialog();
                }
            }
        };
        worker.execute();
    }
    
    private void showDatabaseErrorDialog() {
        String message = "Impossible de se connecter à la base de données.\n" +
                "Veuillez vérifier :\n" +
                "- Que MySQL est démarré\n" +
                "- Les paramètres de connexion dans database.properties\n" +
                "- Que la base de données 'gestion_cours_db' existe";
                
        JOptionPane.showMessageDialog(this, message, "Erreur de connexion", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void refreshAllPanels() {
        if (dashboardPanel != null) dashboardPanel.refresh();
        if (coursPanel != null) coursPanel.refresh();
        if (etudiantPanel != null) etudiantPanel.refresh();
        if (professeurPanel != null) professeurPanel.refresh();
        if (inscriptionPanel != null) inscriptionPanel.refresh();
        
        JOptionPane.showMessageDialog(this, "Données actualisées", "Information", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void testDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return DatabaseConfig.getInstance().testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    String message = connected ? 
                            "Connexion à la base de données réussie !" :
                            "Échec de la connexion à la base de données.";
                    int messageType = connected ? 
                            JOptionPane.INFORMATION_MESSAGE : 
                            JOptionPane.ERROR_MESSAGE;
                            
                    JOptionPane.showMessageDialog(MainFrame.this, message, 
                            "Test de connexion", messageType);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Erreur lors du test de connexion : " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void showAboutDialog() {
        String message = "Gestion de Cours d'Apprentissage\n" +
                "Version 1.0.0\n\n" +
                "Application de gestion complète pour :\n" +
                "- Cours et formations\n" +
                "- Étudiants et professeurs\n" +
                "- Inscriptions et suivi\n\n" +
                "Développé avec Java et MySQL";
                
        JOptionPane.showMessageDialog(this, message, "À propos", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir quitter l'application ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
                
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}