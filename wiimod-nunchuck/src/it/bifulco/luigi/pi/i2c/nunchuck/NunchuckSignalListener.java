package it.bifulco.luigi.pi.i2c.nunchuck;

import it.bifulco.luigi.pi.i2c.nunchuck.Nunchuck.NunchuckSignal;

public interface NunchuckSignalListener {

	public void onNunchChuckEvent(NunchuckSignal signal);
}
