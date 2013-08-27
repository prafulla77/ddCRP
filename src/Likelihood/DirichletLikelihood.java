/**
 * 
 */
package Likelihood;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.special.Gamma;

import model.HyperParameters;

import data.Data;

/**
 * Dirichlet Likelihood for the collapsed Gibbs Sampler
 * @author rajarshd
 *
 */
public class DirichletLikelihood implements Likelihood {

	
	@Override
	public double computeTableLogLikelihood(ArrayList<Integer> table_members,
			int list_index) {
		
		//get the observations
		ArrayList<ArrayList<Double>> list_observations = Data.getObservations();
		ArrayList<Double> observations = list_observations.get(list_index);
		HashMap<Double,Integer> venue_category_count = new HashMap<Double,Integer>(); //this map will store the index of the venue category and the respective counts of the table members
		//creating the map
		for(int i=0;i<table_members.size();i++)
		{
			Double venue_category = observations.get(table_members.get(i).intValue());
			if(venue_category_count.get(venue_category) == null) //new category			
				venue_category_count.put(venue_category, 1);
			else
				venue_category_count.put(venue_category, venue_category_count.get(venue_category) + 1 );	
		}
		
		//get the dirichlet hyper-parameter
		ArrayList<Double> dirichletParams = HyperParameters.dirichletParam;
		double sum_alpha = 0, sum_venue_cat_alpha=0, sum_log_gamma_sum_venue_cat_alpha = 0, sum_log_gamma_sum_alpha = 0;
		
		for(int i=0;i<dirichletParams.size();i++) //loop for each possible venue category
		{
			Integer category_count = venue_category_count.get(new Double(i));
			if(category_count == null) 
				category_count = 0; //in case no venue of a certain category isnt present, the count is 0
			
			sum_alpha = sum_alpha + dirichletParams.get(i);
			sum_venue_cat_alpha = sum_venue_cat_alpha + dirichletParams.get(i) + category_count;
			sum_log_gamma_sum_venue_cat_alpha = sum_log_gamma_sum_venue_cat_alpha + Gamma.logGamma(dirichletParams.get(i)+category_count);
			sum_log_gamma_sum_alpha = sum_log_gamma_sum_alpha + Gamma.logGamma(dirichletParams.get(i));
			
		}
		
		double log_likelihood = sum_log_gamma_sum_venue_cat_alpha + Gamma.logGamma(sum_alpha) - (Gamma.logGamma(sum_venue_cat_alpha) + sum_log_gamma_sum_alpha );
		
		return log_likelihood;
	}

}
