package de.zabuza.beedlebot.logindialog.view;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.zabuza.beedlebot.logging.ILogger;
import de.zabuza.beedlebot.logging.LoggerFactory;
import de.zabuza.sparkle.freewar.EWorld;
import de.zabuza.sparkle.webdriver.EBrowser;

/**
 * View of the login dialog.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LoginDialogView {
	/**
	 * Height of the view.
	 */
	public static final int HEIGHT = 225;
	/**
	 * Width of the view.
	 */
	public static final int WIDTH = 225;
	/**
	 * The default amount of columns for fields of the view.
	 */
	private static final int DEFAULT_FIELD_COLUMNS = 10;
	/**
	 * The default font of the view.
	 */
	private static final String DEFAULT_FONT = "Tahoma";
	/**
	 * The default font size of the view.
	 */
	private static final int DEFAULT_FONT_SIZE = 11;
	/**
	 * The browser choice of the view.
	 */
	private JComboBox<EBrowser> mBrowserChoiceBox;
	/**
	 * Container of the view.
	 */
	private final Container mContainer;
	/**
	 * The frame of the view.
	 */
	private final JFrame mFrame;
	/**
	 * List of all input elements.
	 */
	private final List<JComponent> mInputElements;
	/**
	 * The logger to use for logging.
	 */
	private ILogger mLogger;
	/**
	 * Login button of the view.
	 */
	private JButton mLoginBtn;
	/**
	 * The main panel of the view.
	 */
	private JPanel mMainPanel;
	/**
	 * Password field of the view.
	 */
	private JTextField mPasswordField;
	/**
	 * Settings button of the view.
	 */
	private JButton mSettingsBtn;
	/**
	 * The trailer panel of the view.
	 */
	private JPanel mTrailerPanel;
	/**
	 * Username field of the view.
	 */
	private JTextField mUsernameField;
	/**
	 * The world choice of the view.
	 */
	private JComboBox<EWorld> mWorldChoiceBox;

	/**
	 * Creates the view.
	 * 
	 * @param frame
	 *            Frame of the view
	 */
	public LoginDialogView(final JFrame frame) {
		this.mFrame = frame;
		this.mContainer = frame.getContentPane();
		this.mInputElements = new LinkedList<>();
		this.mLogger = LoggerFactory.getLogger();
		initialize();
	}

	/**
	 * Adds an action listener to the login action.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addListenerToLoginAction(final ActionListener listener) {
		this.mLoginBtn.addActionListener(listener);
	}

	/**
	 * Adds an action listener to the settings action.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addListenerToSettingsAction(final ActionListener listener) {
		this.mSettingsBtn.addActionListener(listener);
	}

	/**
	 * Adds a window listener to the view window.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addWindowListener(final WindowListener listener) {
		this.mFrame.addWindowListener(listener);
	}

	/**
	 * Gets the selected input browser.
	 * 
	 * @return The selected input browser
	 */
	public EBrowser getBrowser() {
		return (EBrowser) this.mBrowserChoiceBox.getSelectedItem();
	}

	/**
	 * Gets the login button.
	 * 
	 * @return The login button to get
	 */
	public JButton getLoginButton() {
		return this.mLoginBtn;
	}

	/**
	 * Gets the input password.
	 * 
	 * @return The input password
	 */
	public String getPassword() {
		return this.mPasswordField.getText();
	}

	/**
	 * Gets the input username.
	 * 
	 * @return The input username
	 */
	public String getUsername() {
		return this.mUsernameField.getText();
	}

	/**
	 * Gets the selected input world.
	 * 
	 * @return The selected input world
	 */
	public EWorld getWorld() {
		return (EWorld) this.mWorldChoiceBox.getSelectedItem();
	}

	/**
	 * Enables or disables all input fields.
	 * 
	 * @param enabled
	 *            Whether the fields should be enabled or disabled
	 */
	public void setAllInputEnabled(final boolean enabled) {
		for (final JComponent element : this.mInputElements) {
			element.setEnabled(enabled);
		}
	}

	/**
	 * Sets the selected browser.
	 * 
	 * @param browser
	 *            The browser to select
	 */
	public void setBrowser(final EBrowser browser) {
		this.mBrowserChoiceBox.setSelectedItem(browser);
	}

	/**
	 * Enables or disables the login button.
	 * 
	 * @param enabled
	 *            Whether the button should be enabled or disabled
	 */
	public void setLoginButtonEnabled(final boolean enabled) {
		this.mLoginBtn.setEnabled(enabled);
	}

	/**
	 * Sets the input password.
	 * 
	 * @param password
	 *            The password to set
	 */
	public void setPassword(final String password) {
		this.mPasswordField.setText(password);
	}

	/**
	 * Enables or disables the settings button.
	 * 
	 * @param enabled
	 *            Whether the button should be enabled or disabled
	 */
	public void setSettingsButtonEnabled(final boolean enabled) {
		this.mSettingsBtn.setEnabled(enabled);
	}

	/**
	 * Sets the input username.
	 * 
	 * @param username
	 *            The username to set
	 */
	public void setUsername(final String username) {
		this.mUsernameField.setText(username);
	}

	/**
	 * Sets the selected world.
	 * 
	 * @param world
	 *            The world to select
	 */
	public void setWorld(final EWorld world) {
		this.mWorldChoiceBox.setSelectedItem(world);
	}

	/**
	 * Initialize the contents of the view.
	 */
	private void initialize() {
		if (this.mLogger.isDebugEnabled()) {
			this.mLogger.logDebug("Initializing LoginDialogView");
		}

		initializePanels();
		initializeLabels();
		initializeButtons();
		initializeInputFields();
	}

	/**
	 * Initialize the buttons.
	 */
	private void initializeButtons() {
		this.mLoginBtn = new JButton("Login");
		this.mLoginBtn.setBounds(50, 130, 100, 23);
		this.mMainPanel.add(this.mLoginBtn);

		this.mSettingsBtn = new LinkButton("Settings");
		this.mSettingsBtn.setBounds(130, 0, 80, 23);
		this.mTrailerPanel.add(this.mSettingsBtn);
	}

	/**
	 * Initialize the text fields.
	 */
	private void initializeInputFields() {
		this.mUsernameField = new JTextField();
		this.mUsernameField.setHorizontalAlignment(SwingConstants.LEFT);
		this.mUsernameField.setBounds(70, 0, 123, 20);
		this.mMainPanel.add(this.mUsernameField);
		this.mInputElements.add(this.mUsernameField);
		this.mUsernameField.setColumns(DEFAULT_FIELD_COLUMNS);

		this.mPasswordField = new JPasswordField();
		this.mPasswordField.setHorizontalAlignment(SwingConstants.LEFT);
		this.mPasswordField.setBounds(70, 30, 123, 20);
		this.mMainPanel.add(this.mPasswordField);
		this.mInputElements.add(this.mPasswordField);
		this.mPasswordField.setColumns(DEFAULT_FIELD_COLUMNS);

		this.mWorldChoiceBox = new JComboBox<>();
		for (final EWorld world : EWorld.values()) {
			this.mWorldChoiceBox.addItem(world);
			if (world == EWorld.ONE) {
				this.mWorldChoiceBox.setSelectedItem(world);
			}
		}
		this.mWorldChoiceBox.setBounds(70, 60, 123, 20);
		this.mMainPanel.add(this.mWorldChoiceBox);
		this.mInputElements.add(this.mWorldChoiceBox);

		this.mBrowserChoiceBox = new JComboBox<>();
		for (final EBrowser browser : EBrowser.values()) {
			this.mBrowserChoiceBox.addItem(browser);
			if (browser == EBrowser.CHROME) {
				this.mBrowserChoiceBox.setSelectedItem(browser);
			}
		}
		this.mBrowserChoiceBox.setBounds(70, 90, 123, 20);
		this.mMainPanel.add(this.mBrowserChoiceBox);
		this.mInputElements.add(this.mBrowserChoiceBox);
	}

	/**
	 * Initialize the labels.
	 */
	private void initializeLabels() {
		final JLabel usernameLbl = new JLabel("Username:");
		usernameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		usernameLbl.setBounds(0, 0, 65, 14);
		this.mMainPanel.add(usernameLbl);

		final JLabel passwordLbl = new JLabel("Password:");
		passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		passwordLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		passwordLbl.setBounds(0, 30, 65, 14);
		this.mMainPanel.add(passwordLbl);

		final JLabel worldChoiceLbl = new JLabel("World:");
		worldChoiceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		worldChoiceLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		worldChoiceLbl.setBounds(0, 60, 65, 14);
		this.mMainPanel.add(worldChoiceLbl);

		final JLabel browserChoiceLbl = new JLabel("Browser:");
		browserChoiceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		browserChoiceLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		browserChoiceLbl.setBounds(0, 90, 65, 14);
		this.mMainPanel.add(browserChoiceLbl);
	}

	/**
	 * Initialize the panels.
	 */
	private void initializePanels() {
		this.mMainPanel = new JPanel();
		this.mMainPanel.setBounds(10, 10, WIDTH - 25, 160);
		this.mContainer.add(this.mMainPanel);
		this.mMainPanel.setLayout(null);

		this.mTrailerPanel = new JPanel();
		this.mTrailerPanel.setBounds(10, 170, WIDTH - 25, 50);
		this.mContainer.add(this.mTrailerPanel);
		this.mTrailerPanel.setLayout(null);
	}
}
