package it.luigibifulco.pi.nunchuck.service;

import java.util.concurrent.atomic.AtomicReference;

import it.luigibifulco.pi.nunchuck.INunchuck;
import it.luigibifulco.pi.nunchuck.NunchuckData;
import it.luigibifulco.pi.nunchuck.NunchuckListener;
import it.luigibifulco.pi.nunchuck.NunchuckStream;
import it.luigibifulco.pi.nunchuck.i2c.Nunchuck;

public class NunchuckConsole {

	private INunchuck nuncuck;

	AtomicReference<NunchuckData> refData = new AtomicReference<NunchuckData>(
			NunchuckData.getInstance());

	public NunchuckConsole() {
		this.nuncuck = new Nunchuck();
	}

	public void show() {
		nuncuck.startListen(new NunchuckListener() {

			@Override
			public void onNuncuckData(NunchuckData data) {
				refData.set(data);
			}
		});
		runMatrix();

	}

	public void runMatrix() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {

					}

					try {
						String clear = "";
						StringBuffer buff = new StringBuffer();
						buff.append("jX:|"
								+ writeRow(refData.get().getjX(),
										NunchuckStream.MAX_JX));
						buff.append("\n");
						buff.append("jY:|"
								+ writeRow(refData.get().getjY(),
										NunchuckStream.MAX_JY));
						buff.append("\n");
						buff.append("aX:|"
								+ writeRow(refData.get().getaX(),
										NunchuckStream.MAX_AX));
						buff.append("\n");
						buff.append("aY:|"
								+ writeRow(refData.get().getaY(),
										NunchuckStream.MAX_AY));
						buff.append("\n");
						buff.append("aZ:|"
								+ writeRow(refData.get().getaZ(),
										NunchuckStream.MAX_AY));

						int len = buff.toString().toCharArray().length + 25;
						for (int i = 0; i < len; i++) {
							clear += "\b";
						}
						for (int i = 0; i < len; i++) {
							clear += "\b";
						}
						System.out.print("\r" + clear);
						System.out.print("\r" + buff.toString());

					} catch (Exception e) {

					}
				}
			}
		}).start();

	}

	private String writeRow(int value, int max) {
		StringBuffer buffer = new StringBuffer();
		String base = "--------------------------------------------------------------";

		if (value <= 0) {
			return "+-------------------------------------------------------------";
		}

		double pos = Math.round((value * base.length()) / max);
		if (pos <= 0 || pos > base.length()) {
			return "+-------------------------------------------------------------";
		}
		// System.out.print("\r" + pos);

		buffer.append(base.substring(0, ((int) pos) - 1));
		buffer.append("+");
		buffer.append(base.substring(((int) pos), base.length() - 1));
		return buffer.toString();
	}

}
