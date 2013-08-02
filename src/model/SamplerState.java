package model;
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

	/**
	 * Getters and Setters
	 */
	public static Long getNum_data() {
		return num_data;
	}

	public static void setNum_data(Long num_data) {
		SamplerState.num_data = num_data;
	}

	public Long[] getC() {
		return c;
	}

	public void setC(Long[] c) {
		this.c = c;
	}
	/**
	 * Method to return the table assignment of each observation
	 * @return
	 */
	public Long[] get_t() {
		return t;
	}

	/**
	 * To set the table assignment of each observation
	 * @param t
	 */
	public void set_t(Long[] t) {
		this.t = t;
	}

	public Long getT() {
		return T;
	}

	public void setT(Long t) {
		T = t;
	}

	public Long getK() {
		return K;
	}

	public void setK(Long k) {
		K = k;
	}

	public Long[] getK_c() {
		return k_c;
	}

	public void setK_c(Long[] k_c) {
		this.k_c = k_c;
	}

	public Long[] getK_t() {
		return k_t;
	}

	public void setK_t(Long[] k_t) {
		this.k_t = k_t;
	}

	public Long[] getM() {
		return m;
	}

	public void setM(Long[] m) {
		this.m = m;
	}
		
	

}
