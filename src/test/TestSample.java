package test;

/**
 * Represents a venue in the test data
 * @author rajarshd
 *
 */
public class TestSample {

	private int cityIndex; //city of the venue
	private int listIndex; //index of the venue within the list of venues in a city
	private double venueCategory; // the observed category of the venue  QUESTION: WHY DOUBLE?

		
	public TestSample(int listIndex, int cityIndex, double venueCategory)
	{
		listIndex = listIndex;
		cityIndex = cityIndex;
		venueCategory = venueCategory;
	}

	public int getCityIndex() { return cityIndex; }
	public int getListIndex() { return listIndex; }
	public double getVenueCategory() { return venueCategory; }

}
