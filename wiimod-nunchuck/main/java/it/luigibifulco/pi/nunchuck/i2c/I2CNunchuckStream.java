package it.luigibifulco.pi.nunchuck.i2c;

import it.luigibifulco.pi.nunchuck.NunchuckStream;

import java.io.IOException;
import java.util.BitSet;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Realization of the nunchusk stream based on i2c protocol
 * 
 * @author Luigi
 * 
 */
public class I2CNunchuckStream implements NunchuckStream {

	private I2CBus i2cBus;

	private NunchuckSignalListener listener;

	private I2CDevice device;

	private final static int ADDRESS = 0x52;

	private boolean active = false;

	public I2CNunchuckStream() {

	}

	@Override
	public void start() {
		active = true;
		startRead();
	}

	@Override
	public void stop() {
		active = false;
	}

	@Override
	public void shutdown() {
		device = null;
		try {
			i2cBus.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize() throws Exception {
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
			System.out.println("NUNCHUCK INITIALIZED!!");
		} catch (IOException e) {
			System.out.println("Error getting device with addres: " + ADDRESS);
			throw e;
		}

	}

	public void setSignalListener(NunchuckSignalListener listener) {
		if (this.listener == null) {
			this.listener = listener;
		}
	}

	private int getUnsigned(byte b) {
		return b & 0xff;
	}

	private BitSet fromByte(byte b) {
		BitSet bits = new BitSet(8);
		for (int i = 0; i < 8; i++) {
			bits.set(i, (b & 1) == 1);
			b >>= 1;
		}
		return bits;
	}

	private void startRead() {
		while (active) {
			try {
				Thread.sleep(100);
				device.write((byte) 0x00);
				Thread.sleep(10);
				byte[] data = new byte[6];
				int read = device.read(data, 0, 6);
				if (read != 6) {
					continue;
				}
				int jX = getUnsigned(data[0]);
				int jY = getUnsigned(data[1]);
				// int aX = getUnsigned((byte) ((bytes[2] << 2) + ((bytes[5] &
				// 0x0c) >> 2)));
				int aX = getAccelX(data[2], data[5]);
				int aY = getAccelY(data[3], data[5]);
				int aZ = getAccelZ(data[4], data[5]);
				/*
				 * accel_x = (data2 << 2) + ((data5 & 0x0c) >> 2) accel_y =
				 * (data3 << 2) + ((data5 & 0x30) >> 4) accel_z = (data4 << 2) +
				 * ((data5 & 0xc0) >> 6)
				 */

				BitSet mask = fromByte(data[5]);

				System.out.print("\r"
						+ NunchuckSignal.getInstance().update(jX, jY, aX, aY,
								aZ, checkButtonPressed(mask)));
			} catch (Exception e) {

			}
		}
		if (!active) {
			System.out.println("Read from nunchuck stopped");
		}
	}

	private int getAccelX(byte x, byte pad) {
		return (0x0000 | (getUnsigned(x) << 2)
				+ ((getUnsigned(pad) & 0x0c) >> 2));

	}

	private int getAccelY(byte y, byte pad) {
		return (0x0000 | (getUnsigned(y) << 2)
				+ ((getUnsigned(pad) & 0x30) >> 4));

	}

	private int getAccelZ(byte y, byte pad) {
		return (0x0000 | (getUnsigned(y) << 2)
				+ ((getUnsigned(pad) & 0xc0) >> 6));

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

	private boolean checkCPressed(BitSet mask) {
		return !(mask.get(1));
	}

	public NunchuckSignal getSignal() {
		return null;
	}

	private static enum SumType {
		X, Y, Z;
	}

	public static class NunchuckSignal {
		private int jX;
		private int jY;
		private int aX;
		private int aY;
		private int aZ;
		private String pressedButton;

		private static NunchuckSignal INSTANCE;

		public static synchronized NunchuckSignal getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new NunchuckSignal(0, 0, 0, 0, 0, "");
			}
			return INSTANCE;
		}

		public synchronized NunchuckSignal update(int jX, int jY, int aX,
				int aY, int aZ, String button) {
			if (INSTANCE == null) {
				return null;
			}

			INSTANCE.jX = jX;
			INSTANCE.jY = jY;
			INSTANCE.aX = aX;
			INSTANCE.aY = aY;
			INSTANCE.aZ = aZ;
			INSTANCE.pressedButton = button;
			return INSTANCE;
		}

		public NunchuckSignal(int jX, int jY, int aX, int aY, int aZ,
				String pressedButton) {
			super();
			this.jX = jX;
			this.jY = jY;
			this.aX = aX;
			this.aY = aY;
			this.aZ = aZ;
			this.pressedButton = pressedButton;

		}

		public int getjX() {
			return jX;
		}

		public void setjX(int jX) {
			this.jX = jX;
		}

		public int getjY() {
			return jY;
		}

		public void setjY(int jY) {
			this.jY = jY;
		}

		public int getaX() {
			return aX;
		}

		public void setaX(int aX) {
			this.aX = aX;
		}

		public int getaY() {
			return aY;
		}

		public void setaY(int aY) {
			this.aY = aY;
		}

		public int getaZ() {
			return aZ;
		}

		public void setaZ(int aZ) {
			this.aZ = aZ;
		}

		public String getPressedButton() {
			return pressedButton;
		}

		public void setPressedButton(String pressedButton) {
			this.pressedButton = pressedButton;
		}

		@Override
		public String toString() {
			return "NunchuckSignal [jX=" + jX + ", jY=" + jY + ", aX=" + aX
					+ ", aY=" + aY + ", aZ=" + aZ + ", pressedButton="
					+ pressedButton + "]";
		}

	}
}
