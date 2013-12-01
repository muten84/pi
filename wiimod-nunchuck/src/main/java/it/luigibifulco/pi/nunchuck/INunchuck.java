package it.luigibifulco.pi.nunchuck;

public interface INunchuck {

	void startListen(NunchuckListener listener) throws RuntimeException;

}
