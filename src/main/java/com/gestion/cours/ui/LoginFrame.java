package com.gestion.cours.ui;

import com.gestion.cours.model.User;
import com.gestion.cours.util.AuthenticationService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/**
 * Interface de connexion et d'inscription
 */
public class LoginFrame extends JFrame {
    private static final String TITLE = "Gestion de Cours 2025 - Connexion";
    private static final Dimension FRAME_SIZE = new Dimension(450, 600);
    
    private AuthenticationService authService;
    private JTabbedPane tabbedPane;
    
    // Composants pour la connexion
    private JTextField emailLoginField;
    private JPasswordField passwordLoginField;
    private JButton loginButton;
    private JLabel loginMessageLabel;
    
    // Composants pour l'inscription
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailRegisterField;
    private JPasswordField passwordRegisterField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> typeUtilisateurCombo;
    private JTextField telephoneField;
    private JButton registerButton;
    private JLabel registerMessageLabel;

    public LoginFrame() {
        this.authService = AuthenticationService.getInstance();
        
        initializeFrame();
        createComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeFrame() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_SIZE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Configurer le look and feel
        try {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createComponents() {
        tabbedPane = new JTabbedPane();
        
        // Créer les panneaux de connexion et d'inscription
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();
        
        tabbedPane.addTab("Connexion", loginPanel);
        tabbedPane.addTab("Inscription", registerPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Titre et logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Bienvenue", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 122, 183));
        
        JLabel subtitleLabel = new JLabel("Connectez-vous à votre compte", JLabel.CENTER);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Formulaire de connexion
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email :"), gbc);
        
        gbc.gridy = 1;
        emailLoginField = new JTextField(20);
        emailLoginField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formPanel.add(emailLoginField, gbc);
        
        // Mot de passe
        gbc.gridy = 2;
        formPanel.add(new JLabel("Mot de passe :"), gbc);
        
        gbc.gridy = 3;
        passwordLoginField = new JPasswordField(20);
        passwordLoginField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formPanel.add(passwordLoginField, gbc);
        
        // Bouton de connexion
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 10, 0);
        loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        loginButton.setBackground(new Color(51, 122, 183));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(200, 40));
        formPanel.add(loginButton, gbc);
        
        // Message d'erreur
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginMessageLabel = new JLabel(" ");
        loginMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        loginMessageLabel.setForeground(Color.RED);
        formPanel.add(loginMessageLabel, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Titre
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Créer un compte", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setForeground(new Color(51, 122, 183));
        
        JLabel subtitleLabel = new JLabel("Rejoignez notre plateforme d'apprentissage", JLabel.CENTER);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Formulaire d'inscription
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Nom
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Nom * :"), gbc);
        gbc.gridy = row++;
        nomField = new JTextField(20);
        formPanel.add(nomField, gbc);
        
        // Prénom
        gbc.gridy = row++;
        formPanel.add(new JLabel("Prénom * :"), gbc);
        gbc.gridy = row++;
        prenomField = new JTextField(20);
        formPanel.add(prenomField, gbc);
        
        // Email
        gbc.gridy = row++;
        formPanel.add(new JLabel("Email * :"), gbc);
        gbc.gridy = row++;
        emailRegisterField = new JTextField(20);
        formPanel.add(emailRegisterField, gbc);
        
        // Type d'utilisateur
        gbc.gridy = row++;
        formPanel.add(new JLabel("Je suis * :"), gbc);
        gbc.gridy = row++;
        typeUtilisateurCombo = new JComboBox<>(new String[]{"Étudiant", "Professeur"});
        formPanel.add(typeUtilisateurCombo, gbc);
        
        // Téléphone
        gbc.gridy = row++;
        formPanel.add(new JLabel("Téléphone :"), gbc);
        gbc.gridy = row++;
        telephoneField = new JTextField(20);
        formPanel.add(telephoneField, gbc);
        
        // Mot de passe
        gbc.gridy = row++;
        formPanel.add(new JLabel("Mot de passe * :"), gbc);
        gbc.gridy = row++;
        passwordRegisterField = new JPasswordField(20);
        formPanel.add(passwordRegisterField, gbc);
        
        // Confirmation mot de passe
        gbc.gridy = row++;
        formPanel.add(new JLabel("Confirmer mot de passe * :"), gbc);
        gbc.gridy = row++;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);
        
        // Bouton d'inscription
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 10, 0);
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        registerButton.setBackground(new Color(92, 184, 92));
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(200, 40));
        formPanel.add(registerButton, gbc);
        
        // Message
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 0, 0);
        registerMessageLabel = new JLabel(" ");
        registerMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        registerMessageLabel.setForeground(Color.RED);
        formPanel.add(registerMessageLabel, gbc);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Ajouter des informations sur l'application en bas
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel infoLabel = new JLabel("Plateforme d'apprentissage en ligne - Gestion de Cours 2025");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        bottomPanel.add(infoLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // Connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Permettre la connexion avec Entrée
        passwordLoginField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Inscription
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        // Permettre l'inscription avec Entrée
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
    }

    private void handleLogin() {
        String email = emailLoginField.getText().trim();
        String password = new String(passwordLoginField.getPassword());
        
        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showLoginMessage("Veuillez remplir tous les champs", Color.RED);
            return;
        }
        
        if (!AuthenticationService.validerEmail(email)) {
            showLoginMessage("Adresse email invalide", Color.RED);
            return;
        }
        
        // Tentative de connexion
        loginButton.setEnabled(false);
        loginButton.setText("Connexion...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (authService.authentifier(email, password)) {
                    showLoginMessage("Connexion réussie !", Color.GREEN);
                    
                    // Ouvrir l'application principale
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame();
                        mainFrame.setVisible(true);
                        dispose();
                    });
                } else {
                    showLoginMessage("Email ou mot de passe incorrect", Color.RED);
                }
            } finally {
                loginButton.setEnabled(true);
                loginButton.setText("Se connecter");
            }
        });
    }

    private void handleRegister() {
        // Récupérer les données
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailRegisterField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String password = new String(passwordRegisterField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        User.TypeUtilisateur type = typeUtilisateurCombo.getSelectedIndex() == 0 ? 
            User.TypeUtilisateur.ETUDIANT : User.TypeUtilisateur.PROFESSEUR;
        
        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showRegisterMessage("Veuillez remplir tous les champs obligatoires (*)", Color.RED);
            return;
        }
        
        if (!AuthenticationService.validerEmail(email)) {
            showRegisterMessage("Adresse email invalide", Color.RED);
            return;
        }
        
        if (!AuthenticationService.validerMotDePasse(password)) {
            showRegisterMessage(AuthenticationService.getCriteresMotDePasse(), Color.RED);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showRegisterMessage("Les mots de passe ne correspondent pas", Color.RED);
            return;
        }
        
        // Créer l'utilisateur
        User newUser = new User(nom, prenom, email, password, type);
        if (!telephone.isEmpty()) {
            newUser.setTelephone(telephone);
        }
        
        // Tentative d'inscription
        registerButton.setEnabled(false);
        registerButton.setText("Inscription...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (authService.inscrire(newUser)) {
                    showRegisterMessage("Inscription réussie ! Vous pouvez maintenant vous connecter.", Color.GREEN);
                    clearRegisterForm();
                    // Basculer vers l'onglet de connexion
                    tabbedPane.setSelectedIndex(0);
                } else {
                    showRegisterMessage("Erreur lors de l'inscription. Cet email existe peut-être déjà.", Color.RED);
                }
            } finally {
                registerButton.setEnabled(true);
                registerButton.setText("S'inscrire");
            }
        });
    }

    private void showLoginMessage(String message, Color color) {
        loginMessageLabel.setText(message);
        loginMessageLabel.setForeground(color);
    }

    private void showRegisterMessage(String message, Color color) {
        registerMessageLabel.setText(message);
        registerMessageLabel.setForeground(color);
    }

    private void clearRegisterForm() {
        nomField.setText("");
        prenomField.setText("");
        emailRegisterField.setText("");
        telephoneField.setText("");
        passwordRegisterField.setText("");
        confirmPasswordField.setText("");
        typeUtilisateurCombo.setSelectedIndex(0);
    }
}