package it.luigibifulco.pi.nunchuck;

/**
 * Interface Operation for start and stop the nunchuck stream
 * 
 * @author Luigi
 * 
 */
public interface NunchuckStream {

	public final static int MIN_JX = 0;
	public final static int MAX_JX = 255;
	public final static int MIN_JY = 0;
	public final static int MAX_JY = 255;
	public final static int MIN_AX = 0;
	public final static int MAX_AX = 1024;
	public final static int MIN_AY = 0;
	public final static int MAX_AY = 1024;
	public final static int MIN_AZ = 0;
	public final static int MAX_AZ = 1024;

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
