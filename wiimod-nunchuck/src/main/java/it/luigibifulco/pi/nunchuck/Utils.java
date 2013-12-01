package it.luigibifulco.pi.nunchuck;

import java.util.BitSet;

public class Utils {

	public static int getUnsigned(byte b) {
		return b & 0xff;
	}

	public static BitSet fromByte(byte b) {
		BitSet bits = new BitSet(8);
		for (int i = 0; i < 8; i++) {
			bits.set(i, (b & 1) == 1);
			b >>= 1;
		}
		return bits;
	}

}
