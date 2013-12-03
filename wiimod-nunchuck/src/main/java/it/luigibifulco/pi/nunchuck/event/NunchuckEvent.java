package it.luigibifulco.pi.nunchuck.event;

public class NunchuckEvent {

	public final static int JOYSTICK_UP = 0x00;
	public final static int JOYSTICK_DOWN = 0x01;
	public final static int JOYSTICK_LEFT = 0x02;
	public final static int JOYSTICK_RIGHT = 0x03;
	public final static int JOYSTICK_CENTER = 0x04;
	public final static int ACCEL_X = 0x05;
	public final static int ACCEL_Y = 0x06;
	public final static int ACCEL_Z = 0x07;

	private int type;
	private int accel;

	public NunchuckEvent(int type, int accel) {
		this.type = type;
		this.accel = accel;
	}

	public int getAccel() {
		return accel;
	}

	public void setAccel(int accel) {
		this.accel = accel;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
