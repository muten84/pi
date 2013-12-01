package it.luigibifulco.pi.nunchuck.i2c;

import java.io.IOException;
import java.util.BitSet;

import it.luigibifulco.pi.nunchuck.INunchuck;
import it.luigibifulco.pi.nunchuck.NunchuckData;
import it.luigibifulco.pi.nunchuck.NunchuckListener;
import it.luigibifulco.pi.nunchuck.Utils;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class Nunchuck implements INunchuck {
	protected I2CBus i2cBus;

	protected I2CDevice device;

	private NunchuckListener listener;

	private final static int ADDRESS = 0x52;

	private boolean active = false;

	private Thread i2cIOThread;

	/**
	 * Start to listen if nunchuck is disactive the device wil be initialized
	 * for start the listening net call to this method does not take any effect.
	 * Call the stopListen method to shutdown the device
	 * 
	 * @param listener
	 */
	@Override
	public synchronized void startListen(NunchuckListener listener)
			throws RuntimeException {
		if (this.listener != null) {
			return;
		}
		this.listener = listener;
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		active = true;
		i2cIOThread = new Thread(new Runnable() {

			@Override
			public void run() {
				startRead();

			}
		});
		i2cIOThread.start();

	}

	public void stopListen() {
		active = false;
		try {
			i2cBus.close();
		} catch (IOException e) {

		}
		listener = null;

	}

	protected void init() throws Exception {
		try {
			i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);
		} catch (IOException e) {
			System.out.println("Error getting instance of i2c bus on "
					+ I2CBus.BUS_1);
			throw e;
		}

		try {

			i2cBus.getDevice(ADDRESS).write(new byte[] { 0x40, 0x00 }, 0, 2);
			if (device == null) {
				device = i2cBus.getDevice(ADDRESS);
			}
			// System.out.print("\rNUNCHUCK INITIALIZED!!");
		} catch (IOException e) {
			System.out.println("Error getting device with addres: " + ADDRESS);
			throw e;
		}
	}

	private void startRead() {
		while (active) {
			try {
				device.write((byte) 0x00);
				Thread.sleep(30);
				byte[] data = new byte[6];
				int read = 0;
				try {
					read = device.read(data, 0, 6);
				} catch (Exception e) {
					// System.out.print("\r" + e.getMessage());
				}
				if (read != 6) {
					continue;
				}

				int jX = Utils.getUnsigned(data[0]);
				int jY = Utils.getUnsigned(data[1]);
				// int aX = getUnsigned((byte) ((bytes[2] << 2) + ((bytes[5] &
				// 0x0c) >> 2)));
				int aX = getAccelX(data[2], data[5]);
				int aY = getAccelY(data[3], data[5]);
				int aZ = getAccelZ(data[4], data[5]);

				if (listener != null) {
					listener.onNuncuckData(NunchuckData.getInstance().update(
							jX, jY, aX, aY, aZ, data[5],
							checkButtonPressed(Utils.fromByte(data[5]))));
				}

				// System.out.print("\r"
				// + NunchuckSignal.getInstance().update(jX, jY, aX, aY,
				// aZ, data[5],
				// checkButtonPressed(fromByte(data[5]))));

			} catch (Exception e) {
				// System.out.print("\r" + e.getMessage());
				// e.printStackTrace();
			}
		}
		if (!active) {
			System.out.print("\rRead from nunchuck stopped");
		}
	}

	private int getAccelX(byte x, byte pad) {
		int a = (0x0000 | (Utils.getUnsigned(x) << 2));
		int b = ((Utils.getUnsigned(pad) & 0x0c) >> 2);
		return a + b;

	}

	private int getAccelY(byte y, byte pad) {
		int a = (0x0000 | (Utils.getUnsigned(y) << 2));
		int b = ((Utils.getUnsigned(pad) & 0x30) >> 4);
		return a + b;

	}

	private int getAccelZ(byte z, byte pad) {
		int a = (0x0000 | (Utils.getUnsigned(z) << 2));
		int b = ((Utils.getUnsigned(pad) & 0xc0) >> 6);
		return a + b;
	}

	private String checkButtonPressed(BitSet mask) {
		boolean bit1 = mask.get(0);
		boolean bit2 = mask.get(1);
		if (bit1 && bit2) {
			return "";
		} else if (bit1 && !bit2) {
			return "C";
		} else if (!bit1 && !bit2) {
			return "Z";
		} else if (!bit1 && bit2) {
			return "CZ";
		}
		return "";
	}

}
