import java.util.Random;

public class NICCoursework1 {	
	static Random randomGenerator = new Random();
	
	public static void main(String[] args) {
		
		int bpp = 1; //this is changed to 2 when running trials for BPP2
		int numberOfIterations = 10000;
		
		int numberOfBins; //number of bins, b
		if (bpp == 1) {
			numberOfBins = 10;
		} else {
			numberOfBins = 100;
		}
		
		int numberOfItems = 500; //number of items in each solution, n
		int populationSize = 10; //population size, p
		int mutationParameter = 1; //mutation parameter, k
		
		boolean crossover = true; //whether two solutions per iteration will be crossed over
		boolean mutation = true; //whether two solutions per iteration will be mutated
		
		
		//algorithm begins here
		//solution generation happens only once, so is not in loop
		Solution[] solutions = generateSolutions(populationSize, numberOfItems, numberOfBins, bpp);	
		
		for (int iteration = 0; iteration < numberOfIterations; iteration++) {
			//calculate fitness of all solutions
			for (int i = 0; i < populationSize; i++) {
				solutions[i].calculateFitnessMeasure(numberOfBins);
			}
		
			//binary tournament selection twice
			Solution parentA = binaryTournamentSelection(solutions);
			Solution parentB = binaryTournamentSelection(solutions);
			
			Solution[] children;
		
			//selected solutions crossed over
			if (crossover) {
				//numberOfItems entered as argument so that programme doesn't have to find length of Item array every iteration
				children = crossoverSolutions(parentA, parentB, numberOfItems);
			} else {
				children = new Solution[2];
				children[0] = copySolution(parentA, numberOfItems);
				children[1] = copySolution(parentB, numberOfItems);
			}
			
			//mutate both children according to parameter set at start of programme
			if (mutation) {
				mutate(children[0], mutationParameter, numberOfBins);
				mutate(children[1], mutationParameter, numberOfBins);
			}
			
			//calculate fitness of potential new solutions for comparison with existing population
			for (int i = 0; i < 2; i++) {
				children[i].calculateFitnessMeasure(numberOfBins);
			}
			
			//replace weakest in population with new candidate solutions, provided new solutions are better
			weakestReplacement(solutions, children[0]);
			weakestReplacement(solutions, children[1]);
		}
		
		//get best and worst values
		int indexOfBest = 0;
		int indexOfWorst = 0;
		
		for (int i = 0; i < solutions.length; i++) {
			if (solutions[i].getFitnessMeasure() < solutions[indexOfBest].getFitnessMeasure()) {
				indexOfBest = i;
			} else if (solutions[i].getFitnessMeasure() > solutions[indexOfWorst].getFitnessMeasure()) {
				indexOfWorst = i;
			}
		}
		System.out.println("BEST FITNESS: " + solutions[indexOfBest].getFitnessMeasure());
		System.out.println("WORST FITNESS: " + solutions[indexOfWorst].getFitnessMeasure());
		
	}
	private static Solution[] generateSolutions(int sizeOfPopulation, int numberOfItems, int numberOfBins, int bpp) {
		
		//create array to store potential solutions
		Solution[] solutions = new Solution[sizeOfPopulation];
		
		//create solution objects
		for (int i = 0; i < sizeOfPopulation; i++) {
			Solution solution = new Solution(numberOfItems, numberOfBins, bpp);
			
			solutions[i] = solution;
		}
		
		return solutions;
	}
	
	private static Solution binaryTournamentSelection(Solution[] solutions) {		
		
		//use random generator to select two solutions from population
		Solution solution1 = solutions[randomGenerator.nextInt(solutions.length)];
		
		Solution solution2 = solutions[randomGenerator.nextInt(solutions.length)];
		
		//fitness measure is minimised and best solution is returned
		//tiebreaker is random because selection of A and B is random
		if (solution1.getFitnessMeasure() < solution2.getFitnessMeasure()) {
			return solution1;
		} else {
			return solution2;
		}
	}
	
	private static Solution[] crossoverSolutions(Solution parentA, Solution parentB, int numberOfItems) {
		
		//use random generator to select crossover point for Item array
		int crossoverPoint = randomGenerator.nextInt(numberOfItems);
		
		//get Item arrays to cross over
		Item[] itemListParentA = parentA.getItems();
		Item[] itemListParentB = parentB.getItems();
		
		//instantiate new arrays to return as children
		Item[] itemListChildC = new Item[numberOfItems];
		Item[] itemListChildD = new Item[numberOfItems];
		
		//put Items in appropriate arrays, copying Items rather than assigning, 
		//to protect parents that stay in population from incorrect mutation
		for (int i = 0; i < numberOfItems; i++) {
			if (i < crossoverPoint) {
				itemListChildC[i] = copyItem(itemListParentA[i]);
				itemListChildD[i] = copyItem(itemListParentB[i]);
			} else {
				itemListChildD[i] = copyItem(itemListParentA[i]);
				itemListChildC[i] = copyItem(itemListParentB[i]);				
			}
		}
		
		//use Item arrays to create Solutions
		Solution[] children = new Solution[2];
		children[0] = new Solution(itemListChildC);
		children[1] = new Solution(itemListChildD);
		
		return children;
	}

	private static Item copyItem(Item itemToBeCopied) {
		
		//straightforward copying of all attributes of Item to new Item
		Item newItem = new Item();
		
		newItem.setBinNumber(itemToBeCopied.getBinNumber());
		newItem.setWeight(itemToBeCopied.getWeight());
		
		return newItem;
	}

	private static Solution copySolution(Solution solutionToBeCopied, int numberOfItems) {
		
		Item[] newItemArray = new Item[numberOfItems];
		Item[] solutionItemArray = solutionToBeCopied.getItems();
		
		for (int i = 0; i < numberOfItems; i++) {
			newItemArray[i] = copyItem(solutionItemArray[i]);
		}
		
		Solution newSolution = new Solution(newItemArray);
		
		return newSolution;
	}


	private static void mutate(Solution solution, int mutationParameter, int numberOfBins) {
		
		//get array of Items to potentially be mutated
		Item[] itemList = solution.getItems();
		
		for (int i = 0; i < mutationParameter; i++) {
			//use random generator to select Item to mutate and to generate random bin for assignment
			int indexOfItemToChange = randomGenerator.nextInt(itemList.length);
			
			int newBinNumber = randomGenerator.nextInt(numberOfBins);
			
			itemList[indexOfItemToChange].setBinNumber(newBinNumber);
		}
	}

	private static void weakestReplacement(Solution[] currentSolutions, Solution candidateSolution) {
		
		int indexOfWorst = 0;
		
		//find worst solution in population (fitness measure is being minimised)
		for (int i = 0; i < currentSolutions.length; i++) {
			if (currentSolutions[i].getFitnessMeasure() > currentSolutions[indexOfWorst].getFitnessMeasure()) {
				indexOfWorst = i;
			}
		}
		
		//if candidate solution is fitter than worst solution, replace worst solution with candidate
		if (candidateSolution.getFitnessMeasure() < currentSolutions[indexOfWorst].getFitnessMeasure()) {
			
			currentSolutions[indexOfWorst] = candidateSolution;
			
		} else if (candidateSolution.getFitnessMeasure() == currentSolutions[indexOfWorst].getFitnessMeasure()) {
			//use random generator to break ties
			if (randomGenerator.nextBoolean()) {
				currentSolutions[indexOfWorst] = candidateSolution;
			}
			
		}
	}
	
}