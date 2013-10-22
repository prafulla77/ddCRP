package model;

import java.util.ArrayList;

/**
 * Class to store the hyper-parameters.
 * @author rajarshd
 *
 */
public class HyperParameters {
	
	/**
	 * The Dirichlet hyper params
	 */
	private ArrayList<Double> dirichletParam;
	
	/**
	 * The self link probability in the ddCRP prior
	 */
	private double selfLinkProb;

	public HyperParameters(ArrayList<Double> dirichlet, double ddcrp) {
		dirichletParam = dirichlet;
		selfLinkProb = ddcrp;
	}

	/**
	 * Getter for the dirichlet parameter
	 */
	public ArrayList<Double> getDirichletParam() {
		return(dirichletParam);
	}

  /**
   * Getter for the self link probability
   */
	public double getSelfLinkProb() {
		return(selfLinkProb);
	}
}
