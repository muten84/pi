package it.luigibifulco.pi.i2c.nunchuck;

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

	public void addSignalListener(NunchuckSignalListener listener) {
		if (this.listener == null) {
			this.listener = listener;
		}
	}

	private int getUnsigned(byte b) {
		return b & 0xff;
	}

	private static BitSet fromByte(byte b) {
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
				device.write((byte) 0x00);
				Thread.sleep(100);
				byte[] bytes = new byte[6];
				int read = device.read(bytes, 0, 6);
				if (read != 6) {
					continue;
				}
				int jX = getUnsigned(bytes[0]);
				int jY = getUnsigned(bytes[1]);
				int aX = getUnsigned(bytes[2]);
				int aY = getUnsigned(bytes[3]);
				int aZ = getUnsigned(bytes[4]);
				BitSet mask = fromByte(bytes[5]);

				System.out.println(NunchuckSignal.getInstance().update(jX, jY,
						aX, aY, aZ, checkButtonPressed(mask)));
			} catch (Exception e) {

			}
		}
		if (!active) {
			System.out.println("Read from nunchuck stopped");
		}
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
