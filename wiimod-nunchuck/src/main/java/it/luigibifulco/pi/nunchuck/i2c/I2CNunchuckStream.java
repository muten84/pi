package it.luigibifulco.pi.nunchuck.i2c;

import it.luigibifulco.pi.nunchuck.NunchuckStream;
import it.luigibifulco.pi.nunchuck.Utils;

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
			// System.out.print("\rNUNCHUCK INITIALIZED!!");
		} catch (IOException e) {
			System.out.println("Error getting device with addres: " + ADDRESS);
			throw e;
		}

	}

	private void startRead() {
		NunchuckSignal.getInstance().runMatrix();
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

				NunchuckSignal.getInstance().update(jX, jY, aX, aY, aZ,
						data[5], checkButtonPressed(Utils.fromByte(data[5])));

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

	public static void main(String[] args) {
		System.out.println(Integer.toBinaryString(0x0c));
		System.out.println(Integer.toBinaryString(0x30));
		System.out.println(Integer.toBinaryString(0xc0));
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
		private int mask;

		private static NunchuckSignal INSTANCE;

		public static synchronized NunchuckSignal getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new NunchuckSignal(0, 0, 0, 0, 0, 0, "");
			}
			return INSTANCE;
		}

		public synchronized NunchuckSignal update(int jX, int jY, int aX,
				int aY, int aZ, int mask, String button) {
			if (INSTANCE == null) {
				return null;
			}

			INSTANCE.jX = jX;
			INSTANCE.jY = jY;
			INSTANCE.aX = aX;
			INSTANCE.aY = aY;
			INSTANCE.aZ = aZ;
			INSTANCE.mask = mask;
			INSTANCE.pressedButton = button;
			return INSTANCE;
		}

		public NunchuckSignal(int jX, int jY, int aX, int aY, int aZ, int mask,
				String pressedButton) {
			super();
			this.jX = jX;
			this.jY = jY;
			this.aX = aX;
			this.aY = aY;
			this.aZ = aZ;
			this.mask = mask;
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
									+ writeRow(jX, NunchuckStream.MAX_JX));
							buff.append("\n");
							buff.append("jY:|"
									+ writeRow(jY, NunchuckStream.MAX_JY));
							buff.append("\n");
							buff.append("aX:|"
									+ writeRow(aX, NunchuckStream.MAX_AX));
							buff.append("\n");
							buff.append("aY:|"
									+ writeRow(aY, NunchuckStream.MAX_AY));
							buff.append("\n");
							buff.append("aZ:|"
									+ writeRow(aZ, NunchuckStream.MAX_AY));

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

		@Override
		public String toString() {
			return "NunchuckSignal [jX=" + jX + ", jY=" + jY + ", aX=" + aX
					+ ", aY=" + aY + ", aZ=" + aZ + ", pressedButton="
					+ pressedButton + ", mask=" + mask + "]";
		}
	}
}
