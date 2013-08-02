package model;

import java.util.ArrayList;


/**
 * This class will store all the sampler state objects of each iteration
 * @author rajarshd
 *
 */
public class SamplerStateTracker {
	
	/**
	 * The current iteration number
	 */
	public static int current_iter;
	
	/**
	 * The maximum possible sampling iteration 
	 */
	public static int max_iter;
	
	/**
	 * List to hold the sampler states for each iteration
	 */
	public static ArrayList<SamplerState> samplerStates = new ArrayList<SamplerState>();
	
	

}
