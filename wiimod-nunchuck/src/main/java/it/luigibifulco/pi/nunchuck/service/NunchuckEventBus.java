package it.luigibifulco.pi.nunchuck.service;

import it.luigibifulco.pi.nunchuck.event.NunchuckEvent;
import it.luigibifulco.pi.nunchuck.event.NunchuckEventListener;

public interface NunchuckEventBus {

	public void addMotionEventListener(NunchuckEventListener nel);

	public void fireEvent(NunchuckEvent event);

	void init();

}
