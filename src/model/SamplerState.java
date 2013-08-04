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
	 * This stores the customer link for each data point. Each list represents a city/document etc.
	 */
	private ArrayList<ArrayList<Long>> c;
	
	/**
	 * This stores the table assignment for each data point.Each list represents a city/document etc.
	 */
	private ArrayList<ArrayList<Long>> t;
	
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
	private ArrayList<ArrayList<Long>> k_c;
	
	/**
	 * This stores the topic assignments for each table;
	 */
	private ArrayList<ArrayList<Long>> k_t;
	
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

	public HashMap<Long,Long> getM() {
		return m;
	}
	
	public void setM(HashMap<Long,Long> m) {
		this.m = m;
	}

	public ArrayList<ArrayList<Long>> getC() {
		return c;
	}

	public void setC(ArrayList<ArrayList<Long>> c) {
		this.c = c;
	}

	public ArrayList<ArrayList<Long>> getK_c() {
		return k_c;
	}

	public void setK_c(ArrayList<ArrayList<Long>> k_c) {
		this.k_c = k_c;
	}

	public ArrayList<ArrayList<Long>> getK_t() {
		return k_t;
	}

	public void setK_t(ArrayList<ArrayList<Long>> k_t) {
		this.k_t = k_t;
	}

	public ArrayList<ArrayList<Long>> get_t()
	{
		return t;
	}
	public void set_t(ArrayList<ArrayList<Long>> t) {
		this.t = t;
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
