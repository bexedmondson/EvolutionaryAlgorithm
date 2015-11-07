import java.util.Random;

public class Solution {
	
	int fitnessMeasure;
	
	Item[] items;
	
	//default constructor not used
	
	//this constructor used when randomly generating solutions
	public Solution(int numberOfItems, int numberOfBins, int bpp) {
		
		//initialise array of items
		items = new Item[numberOfItems];
		Random randomGenerator = new Random();
		
		for (int i = 0; i < numberOfItems; i++) {
			//assign each item the appropriate weight
			items[i] = new Item();
			
			if (bpp == 1) {
				items[i].setWeight(i * 3);
			} else {
				items[i].setWeight(i * i);
			}
			
			//assign each item a random bin
			int binNumber = randomGenerator.nextInt(numberOfBins);
			items[i].setBinNumber(binNumber);
		}
	}
	
	//this constructor used when creating solutions from parents
	public Solution(Item[] items) {
		this.items = items;
	}

	public void calculateFitnessMeasure(int numberOfBins) {
		int[] bins = new int[numberOfBins];
		
		for (int i = 0; i < items.length; i++) {
			int itemBinNumber = items[i].getBinNumber();
			int itemWeight = items[i].getWeight();
			
			bins[itemBinNumber] += itemWeight;
		}
		
		//find max and min bin weight
		int maxBinWeight = Integer.MIN_VALUE;
		int minBinWeight = Integer.MAX_VALUE;
		
		for (int b = 0; b < numberOfBins; b++) {
			int binWeight = bins[b];
			
			if (binWeight > maxBinWeight) {
				maxBinWeight = binWeight;
			}
			
			if (binWeight < minBinWeight) {
				minBinWeight = binWeight;
			}
		}
		
		//use max and min bin weight to calculate fitness measure
		fitnessMeasure = maxBinWeight - minBinWeight;
	}

	public int getFitnessMeasure() {
		return fitnessMeasure;
	}
	
	public Item[] getItems() {
		return items;
	}
	
}