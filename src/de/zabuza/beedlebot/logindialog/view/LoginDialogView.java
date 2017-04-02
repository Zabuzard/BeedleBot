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
		mFrame = frame;
		mContainer = frame.getContentPane();
		mInputElements = new LinkedList<>();
		initialize();
	}

	/**
	 * Adds an action listener to the login action.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addListenerToLoginAction(final ActionListener listener) {
		mLoginBtn.addActionListener(listener);
	}

	/**
	 * Adds an action listener to the settings action.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addListenerToSettingsAction(final ActionListener listener) {
		mSettingsBtn.addActionListener(listener);
	}

	/**
	 * Adds a window listener to the view window.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addWindowListener(final WindowListener listener) {
		mFrame.addWindowListener(listener);
	}

	/**
	 * Gets the selected input browser.
	 * 
	 * @return The selected input browser
	 */
	public EBrowser getBrowser() {
		return (EBrowser) mBrowserChoiceBox.getSelectedItem();
	}

	/**
	 * Gets the input password.
	 * 
	 * @return The input password
	 */
	public String getPassword() {
		return mPasswordField.getText();
	}

	/**
	 * Gets the input username.
	 * 
	 * @return The input username
	 */
	public String getUsername() {
		return mUsernameField.getText();
	}

	/**
	 * Gets the selected input world.
	 * 
	 * @return The selected input world
	 */
	public EWorld getWorld() {
		return (EWorld) mWorldChoiceBox.getSelectedItem();
	}

	/**
	 * Enables or disables all input fields.
	 * 
	 * @param enabled
	 *            Whether the fields should be enabled or disabled
	 */
	public void setAllInputEnabled(final boolean enabled) {
		for (final JComponent element : mInputElements) {
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
		mBrowserChoiceBox.setSelectedItem(browser);
	}

	/**
	 * Enables or disables the login button.
	 * 
	 * @param enabled
	 *            Whether the button should be enabled or disabled
	 */
	public void setLoginButtonEnabled(final boolean enabled) {
		mLoginBtn.setEnabled(enabled);
	}

	/**
	 * Sets the input password.
	 * 
	 * @param password
	 *            The password to set
	 */
	public void setPassword(final String password) {
		mPasswordField.setText(password);
	}

	/**
	 * Enables or disables the settings button.
	 * 
	 * @param enabled
	 *            Whether the button should be enabled or disabled
	 */
	public void setSettingsButtonEnabled(final boolean enabled) {
		mSettingsBtn.setEnabled(enabled);
	}

	/**
	 * Sets the input username.
	 * 
	 * @param username
	 *            The username to set
	 */
	public void setUsername(final String username) {
		mUsernameField.setText(username);
	}

	/**
	 * Sets the selected world.
	 * 
	 * @param world
	 *            The world to select
	 */
	public void setWorld(final EWorld world) {
		mWorldChoiceBox.setSelectedItem(world);
	}

	/**
	 * Initialize the contents of the view.
	 */
	private void initialize() {
		initializePanels();
		initializeLabels();
		initializeButtons();
		initializeInputFields();
	}

	/**
	 * Initialize the buttons.
	 */
	private void initializeButtons() {
		mLoginBtn = new JButton("Login");
		mLoginBtn.setBounds(50, 130, 100, 23);
		mMainPanel.add(mLoginBtn);

		mSettingsBtn = new LinkButton("Settings");
		mSettingsBtn.setBounds(130, 0, 80, 23);
		mTrailerPanel.add(mSettingsBtn);
	}

	/**
	 * Initialize the text fields.
	 */
	private void initializeInputFields() {
		mUsernameField = new JTextField();
		mUsernameField.setHorizontalAlignment(SwingConstants.LEFT);
		mUsernameField.setBounds(70, 0, 123, 20);
		mMainPanel.add(mUsernameField);
		mInputElements.add(mUsernameField);
		mUsernameField.setColumns(DEFAULT_FIELD_COLUMNS);

		mPasswordField = new JPasswordField();
		mPasswordField.setHorizontalAlignment(SwingConstants.LEFT);
		mPasswordField.setBounds(70, 30, 123, 20);
		mMainPanel.add(mPasswordField);
		mInputElements.add(mPasswordField);
		mPasswordField.setColumns(DEFAULT_FIELD_COLUMNS);

		mWorldChoiceBox = new JComboBox<>();
		for (final EWorld world : EWorld.values()) {
			mWorldChoiceBox.addItem(world);
			if (world == EWorld.ONE) {
				mWorldChoiceBox.setSelectedItem(world);
			}
		}
		mWorldChoiceBox.setBounds(70, 60, 123, 20);
		mMainPanel.add(mWorldChoiceBox);
		mInputElements.add(mWorldChoiceBox);

		mBrowserChoiceBox = new JComboBox<>();
		for (final EBrowser browser : EBrowser.values()) {
			// TODO Also support other browsers
			if (browser != EBrowser.CHROME) {
				continue;
			}
			mBrowserChoiceBox.addItem(browser);
			if (browser == EBrowser.CHROME) {
				mBrowserChoiceBox.setSelectedItem(browser);
			}
		}
		mBrowserChoiceBox.setBounds(70, 90, 123, 20);
		mMainPanel.add(mBrowserChoiceBox);
		mInputElements.add(mBrowserChoiceBox);
	}

	/**
	 * Initialize the labels.
	 */
	private void initializeLabels() {
		final JLabel usernameLbl = new JLabel("Username:");
		usernameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		usernameLbl.setBounds(0, 0, 65, 14);
		mMainPanel.add(usernameLbl);

		final JLabel passwordLbl = new JLabel("Password:");
		passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		passwordLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		passwordLbl.setBounds(0, 30, 65, 14);
		mMainPanel.add(passwordLbl);

		final JLabel worldChoiceLbl = new JLabel("World:");
		worldChoiceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		worldChoiceLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		worldChoiceLbl.setBounds(0, 60, 65, 14);
		mMainPanel.add(worldChoiceLbl);

		final JLabel browserChoiceLbl = new JLabel("Browser:");
		browserChoiceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		browserChoiceLbl.setFont(new Font(DEFAULT_FONT, Font.BOLD, DEFAULT_FONT_SIZE + 1));
		browserChoiceLbl.setBounds(0, 90, 65, 14);
		mMainPanel.add(browserChoiceLbl);
	}

	/**
	 * Initialize the panels.
	 */
	private void initializePanels() {
		mMainPanel = new JPanel();
		mMainPanel.setBounds(10, 10, WIDTH - 25, 160);
		mContainer.add(mMainPanel);
		mMainPanel.setLayout(null);

		mTrailerPanel = new JPanel();
		mTrailerPanel.setBounds(10, 170, WIDTH - 25, 50);
		mContainer.add(mTrailerPanel);
		mTrailerPanel.setLayout(null);
	}
}
