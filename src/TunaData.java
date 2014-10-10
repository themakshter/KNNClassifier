

/**
 * This is the class which is meant to represent each data entry in the
 * diveData.txt
 * 
 * @author mak1g11
 * 
 */
public class TunaData {
	private String classification;
	private double[] data;

	/**
	 * Constructor for the Data Set
	 * 
	 * @param n
	 *            Number of dimensions present in the Data Set
	 */
	public TunaData(int n) {
		data = new double[n];
	}

	/**
	 * getter for data
	 * 
	 * @return Array containing dimensions
	 */
	public double[] getData() {
		return data;
	}

	/**
	 * getter for the classification
	 * 
	 * @return Classification of the TunaData instance
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * setter for classification
	 * 
	 * @param c
	 *            Classification to be set
	 */
	public void setClassification(String c) {
		classification = c;
	}
}
