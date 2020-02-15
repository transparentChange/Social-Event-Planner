public class StudentLoginButton extends LoginButton {
	private boolean login;
	
	StudentLoginButton(int gridX, int gridY) {
		super(gridX, gridY);
		
		setText("Login");
	}
	
	public boolean isLoginButton() {
		return login;
	}
	
	public void switchButtonState() {
		login = !login;
		System.out.println("switched");
		if (login) {
			this.setText("Login");
		} else {
			this.setText("Create Account");
		}
	}
}
