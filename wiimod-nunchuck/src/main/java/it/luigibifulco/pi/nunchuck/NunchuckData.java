package it.luigibifulco.pi.nunchuck;

public class NunchuckData {
	private int jX;
	private int jY;
	private int aX;
	private int aY;
	private int aZ;
	private String pressedButton;
	private int mask;

	private static NunchuckData INSTANCE;

	public static synchronized NunchuckData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NunchuckData(0, 0, 0, 0, 0, 0, "");
		}
		return INSTANCE;
	}

	public synchronized NunchuckData update(int jX, int jY, int aX, int aY,
			int aZ, int mask, String button) {
		if (INSTANCE == null) {
			return null;
		}

		INSTANCE.jX = jX;
		INSTANCE.jY = jY;
		INSTANCE.aX = aX;
		INSTANCE.aY = aY;
		INSTANCE.aZ = aZ;
		INSTANCE.mask = mask;
		INSTANCE.pressedButton = button;
		return INSTANCE;
	}

	public NunchuckData(int jX, int jY, int aX, int aY, int aZ, int mask,
			String pressedButton) {
		super();
		this.jX = jX;
		this.jY = jY;
		this.aX = aX;
		this.aY = aY;
		this.aZ = aZ;
		this.mask = mask;
		this.pressedButton = pressedButton;

	}

	public int getjX() {
		return jX;
	}

	public void setjX(int jX) {
		this.jX = jX;
	}

	public int getjY() {
		return jY;
	}

	public void setjY(int jY) {
		this.jY = jY;
	}

	public int getaX() {
		return aX;
	}

	public void setaX(int aX) {
		this.aX = aX;
	}

	public int getaY() {
		return aY;
	}

	public void setaY(int aY) {
		this.aY = aY;
	}

	public int getaZ() {
		return aZ;
	}

	public void setaZ(int aZ) {
		this.aZ = aZ;
	}

	public String getPressedButton() {
		return pressedButton;
	}

	public void setPressedButton(String pressedButton) {
		this.pressedButton = pressedButton;
	}

	@Override
	public String toString() {
		return "NunchuckSignal [jX=" + jX + ", jY=" + jY + ", aX=" + aX
				+ ", aY=" + aY + ", aZ=" + aZ + ", pressedButton="
				+ pressedButton + ", mask=" + mask + "]";
	}
}
