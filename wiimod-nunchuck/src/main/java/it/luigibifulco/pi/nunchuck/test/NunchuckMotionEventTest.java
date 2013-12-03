package it.luigibifulco.pi.nunchuck.test;

import it.luigibifulco.pi.nunchuck.event.NunchuckEvent;
import it.luigibifulco.pi.nunchuck.event.NunchuckEventListener;
import it.luigibifulco.pi.nunchuck.i2c.Nunchuck;
import it.luigibifulco.pi.nunchuck.service.NunchuckEventBus;
import it.luigibifulco.pi.nunchuck.service.SimpleNunchuckEventBus;

public class NunchuckMotionEventTest {

	public static void main(String[] args) {
		NunchuckEventBus bus = new SimpleNunchuckEventBus(new Nunchuck());

		bus.addMotionEventListener(new NunchuckEventListener() {

			@Override
			public void onMotionEvent(NunchuckEvent event) {
				System.out.println(event.getType() + " - " + event.getAccel());

			}
		});
		bus.init();
	}
}
