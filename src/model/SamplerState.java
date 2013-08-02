package model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

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
	private ArrayList<Long> c;
	
	/**
	 * This stores the table assignment for each data point.
	 */
	private ArrayList<Long> t;
	
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
	private ArrayList<Long> k_c;
	
	/**
	 * This stores the topic assignments for each table;
	 */
	private ArrayList<Long> k_t;
	
	/**
	 * This stores the number of tables(clusters) assigned to each topic.
	 */
	private HashMap<Long,Long> m;
	/**
	 * 
	 * Getters and Setters
	 * 
	 */
	
	public static Long getNum_data() {
		return num_data;
	}

	public static void setNum_data(Long num_data) {
		SamplerState.num_data = num_data;
	}

	public ArrayList<Long> getC() {
		return c;
	}

	public void setC(ArrayList<Long> c) {
		this.c = c;
	}
	/**
	 * get the table assignments for each data point
	 * @return
	 */
	public ArrayList<Long> get_t() {
		return t;
	}
	/**
	 * set the table assignments for each data point
	 * @return
	 */
	public void set_t(ArrayList<Long> t) {
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

	public ArrayList<Long> getK_c() {
		return k_c;
	}

	public void setK_c(ArrayList<Long> k_c) {
		this.k_c = k_c;
	}

	public ArrayList<Long> getK_t() {
		return k_t;
	}

	public void setK_t(ArrayList<Long> k_t) {
		this.k_t = k_t;
	}

	public HashMap<Long,Long> getM() {
		return m;
	}

	public void setM(HashMap<Long,Long> m) {
		this.m = m;
	}

	/**
	 * Prints the object state
	 */
	public void prettyPrint(PrintStream out)
	{
		out.println("Total number of observations are "+SamplerState.num_data);
		out.println("Total number of tables are "+T);
		out.println("Total number of topics "+K);
	}
	
	
}
