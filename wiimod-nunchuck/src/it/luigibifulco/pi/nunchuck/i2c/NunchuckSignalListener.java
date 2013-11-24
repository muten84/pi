package it.luigibifulco.pi.nunchuck.i2c;

import it.luigibifulco.pi.nunchuck.i2c.I2CNunchuckStream.NunchuckSignal;

public interface NunchuckSignalListener {

	public void onNunchChuckEvent(NunchuckSignal signal);
}
