package it.luigibifulco.pi.nunchuck.event;

public class NunchuckEvent {

	public final static int JOYSTICK_UP = 0x00;
	public final static int JOYSTICK_DOWN = 0x01;

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
