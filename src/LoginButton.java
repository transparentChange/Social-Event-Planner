import javax.swing.JButton;

public class LoginButton extends JButton {
	private boolean login;
	
	LoginButton(int gridX, int gridY) {
		this.login = true;
		
		setText("Login");
	}
	
	public boolean isLoginButton() {
		return login;
	}
	
	public void switchButtonState() {
		login = !login;
		if (login) {
			this.setText("Login");
		} else {
			this.setText("Create Account");
		}
	}
}
