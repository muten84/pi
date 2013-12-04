package it.luigibifulco.pi.nunchuck.test;

import it.luigibifulco.pi.nunchuck.NunchuckData;
import it.luigibifulco.pi.nunchuck.NunchuckListener;
import it.luigibifulco.pi.nunchuck.i2c.Nunchuck;

public class NunchuckReadDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Nunchuck().startListen(new NunchuckListener() {

			@Override
			public void onNuncuckData(NunchuckData data) {
				System.out.println(data);

			}
		});
	}

}
