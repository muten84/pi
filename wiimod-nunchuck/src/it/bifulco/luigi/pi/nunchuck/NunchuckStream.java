package it.bifulco.luigi.pi.nunchuck;

public interface NunchuckStream {

	/**
	 * Start read from nunchuck. Error if not initialized
	 */
	public void start();

	/**
	 * Stop read from nunchuck
	 */
	public void stop();

	/**
	 * Shutdown the bus. Previous initialize will be erased
	 */
	public void shutdown();

	/**
	 * Initialize the nunchuck. This method MUST be called before start the read
	 * 
	 * @throws Exception
	 */
	public void initialize() throws Exception;

}
