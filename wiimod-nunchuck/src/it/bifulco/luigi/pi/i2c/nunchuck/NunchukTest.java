package it.bifulco.luigi.pi.i2c.nunchuck;

public class NunchukTest {

	public static void main(String[] args) throws Exception {
		final Nunchuck n = new Nunchuck();
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				try {
					mainThread.join();
					n.shutdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
