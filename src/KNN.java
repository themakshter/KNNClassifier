

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * The class containing the methods for constructing values from the data set in
 * the file and classifiying each reading
 * 
 * @author mak1g11
 * 
 */
public class KNN {
	private ArrayList<TunaData> diveData;
	private String[] dim_names;
	private int[] choices;
	private int dimensions, k;
	private double accuracy;

	/**
	 * Constructor for the class
	 */
	public KNN() {
		// ArrayList is initialised
		accuracy = 0;
		diveData = new ArrayList<TunaData>();

	}

	// This is present to try and experiment and check for data as otherwise
	// KNN_GUI class is to be used
	// public static void main(String[] args){
	// KNN knn = new KNN();
	// knn.buildDataset(new File("diveData.txt"));
	// knn.scaleResults();
	// for(int i = 1; i < 25;i++){
	// knn.setK(i);
	// int[] choice = {0,1,0,0,0,0,1,1};
	// knn.setChoices(choice);
	// knn.classifyAll();
	// System.out.println(knn.getAccuracy());
	// };
	// }

	/**
	 * For each data set given, makes a Tuna instance, gives it the column
	 * values and adds it to the global ArrayList.
	 * 
	 * @param f
	 *            File from which the data is going to be read.
	 */
	public void buildDataset(File f) {
		try {
			StringTokenizer st;
			BufferedReader bf = new BufferedReader(new FileReader(f));

			// Getting the names of the dimensions which will be the firt line
			// only
			st = new StringTokenizer(bf.readLine());
			// dynamically found out how many dimensions are there
			dimensions = st.countTokens() - 1;
			dim_names = new String[dimensions];
			for (int i = 0; i < dim_names.length; i++) {
				dim_names[i] = st.nextToken();
			}

			String s = bf.readLine();

			// While there is a non empty line in the file, dataset will
			// keep being built
			while (s != null) {

				st = new StringTokenizer(s);
				dimensions = st.countTokens() - 1;

				// dynamically determining how many column are needed
				TunaData t = new TunaData(dimensions);

				// puts data in each file
				for (int i = 0; i < t.getData().length; i++) {
					t.getData()[i] = Double.parseDouble(st.nextToken());
				}

				// classification is stored separately
				t.setClassification(st.nextToken());

				// added to universal ArrayList
				diveData.add(t);

				// next line
				s = bf.readLine();
			}

			// length choices will be same as no. of dimensions
			choices = new int[dim_names.length];
			// initially, all dimensions are chosen so 0 is added
			for (int i = 0; i < choices.length; i++) {
				choices[i] = 1;
			}

		} catch (IOException ioe) {
			System.err.println("Error while reading file");
		}

	}

	/**
	 * Getter for array containing dimension names
	 * 
	 * @return Array containing dimension names
	 */
	public String[] getDimensionNames() {
		return dim_names;
	}

	/**
	 * getter for accuracy
	 * 
	 * @return Accuracy of classifier
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * getter for choices selected
	 * 
	 * @return Array containing the choices selected by user
	 */
	public int[] getChoices() {
		return choices;
	}

	/**
	 * Setter for dimensions chosen
	 * 
	 * @param c
	 *            Array containing values corresponding to dimensions chosen
	 */
	public void setChoices(int[] c) {
		choices = c;
	}

	/**
	 * setter for value of k
	 * 
	 * @param i
	 *            value to be set for k
	 */
	public void setK(int i) {
		k = i;
	}

	/**
	 * Classifies each data set present in the ArrayList in comparison with the
	 * rest
	 * 
	 * @return Predicted and actual classification and accuracy in form of a
	 *         long String
	 */
	public String classifyAll() {
		int count = 0;
		String result = "";
		for (TunaData t : diveData) {
			// each individual one classified
			String classed = classify(t);
			// marked correct
			if (classed.equals(t.getClassification())) {
				count++;
			}
			// shows what was right and what agent predicted
			result += "Predicted: " + classed + " Actual: "
					+ t.getClassification() + "\n";
		}

		// accuracy in terms of correct answers and also
		// percentage, rounded off to 2dcm
		double percentage = ((double) count / (double) diveData.size()) * 100;
		accuracy = Double.parseDouble(new DecimalFormat("##.###")
				.format(percentage / 100));
		result += "Accuracy = " + count + " / " + diveData.size() + " ( "
				+ new DecimalFormat("##.###").format(percentage) + " %)";

		return result;
	}

	/**
	 * Classifies Tuna dataset by comparing it to the rest of the datasets
	 * 
	 * @param t
	 *            Tuna data set to be compared
	 * @return Predicted Classification
	 */
	public String classify(TunaData t) {
		// HashMap to bind the distance between each set to the set's
		// classification
		HashMap<Double, String> binding = new HashMap<Double, String>();
		double eDistance;
		for (TunaData t2 : diveData) {
			// setting distance to zero every time
			eDistance = 0;

			// skips same data value
			if (t2 == t) {
				continue;
			}

			// iterating through the colum
			for (int i = 0; i < t.getData().length; i++) {

				// if(i ==0 || i==4){
				// continue;
				// }
				// difference between two values

				// for the gui,skips the dimensions which has been unchecked
				if (choices[i] != 1) {
					continue;
				}

				// calculates using the formula for Euclidean distance
				// for n dimensions
				double diffSquared = (t2.getData()[i] - t.getData()[i])
						* (t2.getData()[i] - t.getData()[i]);

				// if needed, this code can be uncommented and
				// manipulated to check the different accuracies
				// if (i == 7) {
				// diffSquared *= 2;
				// }
				// if (i == 6) {
				// diffSquared *= .5;
				// }

				eDistance += diffSquared;
			}

			// added to HashMap linking each Euclidean distance value with
			// correct classification
			binding.put(Math.sqrt(eDistance), t2.getClassification());
		}

		// KNN
		return kClassifier(k, binding);

	}

	/**
	 * Finds the k least(closest) values , puts their classifications in an
	 * array to find the most common classification
	 * 
	 * @param k
	 *            number of least values to find
	 * @param binding
	 *            HashMap containing the bindings between the distance and
	 * @return Predicted Classification
	 */
	public String kClassifier(int k, HashMap<Double, String> binding) {
		HashMap<Double, String> data = binding;
		double min;
		String k1Class = "";
		String classification;
		// depends on k
		String[] classifications = new String[k];
		// finds the least minimum values and their classifications attached
		for (int i = 0; i < classifications.length; i++) {
			min = findMin(data);
			classification = data.get(min);
			// in case there is a tie , go for k=1 so classification of smallest
			// distance stored
			if (i == 0) {
				k1Class = classification;
			}
			// removed from HashMap to find next minimum
			data.remove(min);

			// adds classifications to array
			classifications[i] = classification;
		}

		classification = getClassification(classifications);

		// checks for tie, returns classification of closest data set
		if (classification.equals("")) {
			classification = k1Class;
		}
		return classification;
	}

	/**
	 * Returns the minimum value present in the keys of the HashMap
	 * 
	 * @param data
	 *            HashMap containing all the double values
	 * @return Minimum value present in HashMap
	 */
	public double findMin(HashMap<Double, String> data) {
		// value of min is set to maximum so that it always changes
		double min = Double.MAX_VALUE;
		for (double i : data.keySet()) {
			if (i < min) {
				min = i;
			}
		}
		return min;
	}

	/**
	 * Traverses through the list, finding the classifications in majority
	 * 
	 * @param list
	 *            Array of classification strings
	 * @return Classification in majority
	 */
	public String getClassification(String[] list) {
		// checks which classification is in majority,
		// returns empty string if tie
		int s = 0;
		int t = 0;
		int u = 0;
		int v = 0;
		int tx = 0;
		int max = 0;
		String result = "";
		for (String st : list) {
			if (st.equals("T")) {
				t += 1;
			} else if (st.equals("V")) {
				v += 1;
			} else if (st.equals("S")) {
				s += 1;
			} else if (st.equals("TX")) {
				tx += 1;
			} else if (st.equals("U")) {
				u += 1;
			}
		}
		int max1 = Math.max(t, v);
		int max2 = Math.max(Math.max(u, s), tx);
		max = Math.max(max1, max2);
		// if more than one is maximum that means there is a tie
		if (max == max1 && max == max2) {
			result = "";
		} else {
			if (max == u) {
				result = "U";
			} else if (max == s) {
				result = "S";
			} else if (max == v) {
				result = "V";
			} else if (max == t) {
				result = "T";
			} else if (max == tx) {
				result = "TX";
			}

		}
		return result;
	}

	/**
	 * Scales each column of the Tuna data set
	 */
	public void scaleResults() {
		TunaData t = new TunaData(dimensions);
		for (int i = 0; i < t.getData().length; i++) {
			scaleEach(i);
		}
	}

	/**
	 * Iterates through a column, finding the mean, standard deviation and the
	 * standard, replacing the original value with the standardised one
	 * 
	 * @param i
	 *            Number of the column to be scaled
	 */
	public void scaleEach(int i) {
		double total = 0;
		double mean = 0;
		double count = 0;
		double std = 0;
		// mean
		for (TunaData t : diveData) {
			total += t.getData()[i];
			count++;
		}
		mean = total / count;
		// standard deviation
		total = 0;
		for (TunaData t : diveData) {
			total += (t.getData()[i] - mean) * (t.getData()[i] - mean);
		}
		std = Math.sqrt(total / count);
		// standard score
		for (TunaData t : diveData) {
			t.getData()[i] = ((t.getData()[i] - mean) / std);
		}
	}

}
