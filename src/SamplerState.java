/**
 * This class is for storing the state of the sampler for an iteration.
 * An object of this class represent the state of the sampler at one iteration.
 * @author rajarshd
 */
public class SamplerState {

	/**
	 * The number of data instances.
	 */
	private static Long num_data;
	
	/**
	 * This stores the customer link for each data point.
	 */
	private Long[] c;
	
	/**
	 * This stores the table assignment for each data point.
	 */
	private Long[] t;
	
	/**
	 * The number of occupied tables at this iteration
	 */
	private Long T;
	
	/**
	 * The total number of topics
	 */
	private Long K;
	/**
	 * This stores the topic assignments for each data point (which is basically the topic assignment at the given table they are sitting at)
	 */
	private Long[] k_c;
	
	/**
	 * This stores the topic assignments for each table;
	 */
	private Long[] k_t;
	
	/**
	 * This stores the number of tables(clusters) assigned to each topic.
	 */
	private Long[] m;
		
	

}
