package it.bifulco.luigi.pi.i2c.nunchuck;

public class NunchukTest {

	public static void main(String[] args) throws Exception {
		final I2CNunchuckStream n = new I2CNunchuckStream();
		n.initialize();
		n.start();
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				try {
					mainThread.join();
					n.stop();
					n.shutdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
