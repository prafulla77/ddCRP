package Likelihood;

import java.util.ArrayList;

/**
 * Generic interface for computing likelihood of the data.
 * Your likelihood implementation should implement this interface
 * @author rajarshd
 *
 */
public interface Likelihood {
	
	/**
	 * Method for computing log-likelihood of the data at a table.
	 * @param table_members list of indexes of the observation.
	 * @param list_index index of the list, the observation at the tables belong to.
	 * @return
	 */
	public double computeTableLogLikelihood(ArrayList<Integer> table_members,int list_index);

}
