package it.luigibifulco.pi.nunchuck.i2c;

import it.luigibifulco.pi.nunchuck.i2c.I2CNunchuckStream.NunchuckSignal;

public interface NunchuckSignalListener {

	public final static int JX_ZERO = 114;
	public final static int JY_ZERO = 123;
	public final static int AX_ZERO = 495;
	public final static int AY_ZERO = 446;
	public final static int AZ_ZERO = 550;

	public void onNunchChuckEvent(NunchuckSignal signal);
}
