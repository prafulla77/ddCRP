package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import model.SamplerStateTracker;

import org.la4j.matrix.sparse.CRSMatrix;

import data.Data;

/**
 * This class is for splitting the data into test and train data.
 * @author rajarshd
 *
 */
public class Test {

	private static ArrayList<ArrayList<TestSample>> test_samples_all_cities = new ArrayList<ArrayList<TestSample>>();
	private static ArrayList<HashMap<Integer,Integer>> test_venue_ids_all_cities = new ArrayList<HashMap<Integer,Integer>>();  //list of maps, each for a city
	
	/**
	 * This method generates 100 random test venue id's for each city and populates a map  
	 */
	public static void generateTestSamples() {
		
		ArrayList<ArrayList<Double>> all_observations = Data.getObservations();
		ArrayList<CRSMatrix> distanceMatrices = Data.getDistanceMatrices();
		
		//generate 100 indexes (venues for each city)
		int num_test = 100;
		Random r = new Random();
		for(int i=0;i<all_observations.size();i++) //for each city, generate 100 random points which are to be taken out
		{
			ArrayList<TestSample> test_samples = new ArrayList<TestSample>();
			HashMap<Integer,Integer> venue_ids = new HashMap<Integer,Integer>(); //reason for storing in a map is to decrease the lookup time
			for(int count=0;count<num_test;count++)
			{
				int venue_index = r.nextInt(all_observations.get(i).size()); //venue index to be removed as test data			
				TestSample t = new TestSample(venue_index, i, all_observations.get(i).get(venue_index)); //1st arg: venue_index within a city; 2nd arg: city index, 3rd arg: venue category
				test_samples.add(t);				
				//Now add to the map of test venues
				venue_ids.put(venue_index, 0); //the key is the venue_id, the value is useless
			}
			test_samples_all_cities.add(test_samples); //all the test samples for a city
			test_venue_ids_all_cities.add(venue_ids);
		}
		
		
	}
	/**
	 * Returns all the test samples for all city
	 * @return
	 */
	public static ArrayList<ArrayList<TestSample>> getTest_samples_all_cities() {
		return test_samples_all_cities;
	}
	
	/**
	 * Retrns a list of @see TestSample for a city (the test venues for a city)
	 * @param city_index
	 * @return
	 */
	public static ArrayList<TestSample> getTest_samples(int city_index) {
		
		return test_samples_all_cities.get(city_index);
	}
	/**
	 * Returns a map of the venue_ida which are in the test set
	 * @param city_index
	 * @return
	 */
	public static HashMap<Integer, Integer> getTest_venue_ids(int city_index) {
		return test_venue_ids_all_cities.get(city_index);
	}

}
