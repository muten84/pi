package it.luigibifulco.pi.i2c.nunchuck;

import it.luigibifulco.pi.i2c.nunchuck.I2CNunchuckStream.NunchuckSignal;

public interface NunchuckSignalListener {

	public void onNunchChuckEvent(NunchuckSignal signal);
}
