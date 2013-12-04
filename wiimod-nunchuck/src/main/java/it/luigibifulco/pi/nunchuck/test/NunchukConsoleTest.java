package it.luigibifulco.pi.nunchuck.test;

import it.luigibifulco.pi.nunchuck.service.NunchuckConsole;

/**
 * Main test class
 * 
 * @author Luigi
 * 
 */
public class NunchukConsoleTest {

	public static void main(String[] args) throws Exception {

		NunchuckConsole console = new NunchuckConsole();
		console.show();

		// final NunchuckStream n = new I2CNunchuckStream();
		// n.initialize();
		// Thread.sleep(1000);
		// n.start();
		// final Thread mainThread = Thread.currentThread();
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		// public void run() {
		//
		// try {
		// mainThread.join();
		// n.stop();
		// n.shutdown();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// });
	}

}
