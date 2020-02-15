public class StudentLoginButton extends LoginButton {
	private boolean login;
	
	StudentLoginButton(int gridX, int gridY) {
		super(gridX, gridY);
		
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
