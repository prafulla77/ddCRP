package test;

/**
 * Represents a venue in the test data
 * @author rajarshd
 *
 */
public class TestSample {

	private int city_index; //city of the venue
	private int list_index; //index of the venue within the list of venues in a city
	private double venue_category; // the observed category of the venue
	
	
	public TestSample(int list_index, int city_index, double venue_category)
	{
		list_index = list_index;
		city_index = city_index;
		venue_category = venue_category;
	}
}
