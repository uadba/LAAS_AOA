package silisyum;

import java.util.Iterator;
import java.util.Random;

public class ShrinkingRegion {

	private int numberofElements;
	private int problemDimension = 0;
	private int populationNumber;
	private int halfOfPopulation;
	public double[][] members;
	private double[] memberFitness;
	public double[][] siraliMembers;
	private double[] siraliMemberFitness;
	private double[] temp;
	public int bestMemberID = -1;
	public double fitnessOfBestMember = 0;
	public int maximumIterationNumber;
	private double okUzunluguBaslangici;
	private double okUzunluguBitisi;
	public int iterationIndex = 0;
	private double[] L;
	private double[] H;
	private double[] Ls;
	private double[] Hs;
	private boolean amplitudeIsUsed;
	private boolean phaseIsUsed;
	private boolean positionIsUsed;
	private Cost cost;
	private boolean iterationState = true;
	public double[] costValues;
	public boolean[] istikamet; // "true" ise buyuk olan indekse dogru
	public int[] rasgeleYonSayaci;
	Random r;
	double[] birimVektor;
	double bitisIcinDelta;
	// private IyiUyelerinListesi iuListesi;

	public ShrinkingRegion(int _numberofElements, int _populationNumber, int _maximumIterationNumber, double _F,
			double _Cr, double _okUzunluguBaslangici, double _okUzunluguBitisi, double[] _L, double[] _H,
			AntennaArray _aA, AntennaArray _aAForP, Mask _mask, boolean _amplitudeIsUsed, boolean _phaseIsUsed,
			boolean _positionIsUsed) {

		numberofElements = _numberofElements;
		populationNumber = _populationNumber;
		halfOfPopulation = populationNumber / 2;
		maximumIterationNumber = _maximumIterationNumber;
		okUzunluguBaslangici = _okUzunluguBaslangici;
		okUzunluguBitisi = _okUzunluguBitisi;
		L = _L;
		H = _H;
		amplitudeIsUsed = _amplitudeIsUsed;
		phaseIsUsed = _phaseIsUsed;
		positionIsUsed = _positionIsUsed;

		if (amplitudeIsUsed)
			problemDimension = numberofElements;
		if (phaseIsUsed)
			problemDimension += numberofElements;
		if (positionIsUsed)
			problemDimension += numberofElements;

		cost = new Cost(numberofElements, _aA, _aAForP, _amplitudeIsUsed, _phaseIsUsed, positionIsUsed);
		createArrays();
		initialize();
		costValues = new double[maximumIterationNumber];
	}

	private void createArrays() {
		members = new double[problemDimension][populationNumber];
		memberFitness = new double[populationNumber];
		siraliMembers = new double[problemDimension][halfOfPopulation];
		siraliMemberFitness = new double[halfOfPopulation];
		temp = new double[problemDimension];
		Ls = new double[problemDimension];
		Hs = new double[problemDimension];
		istikamet = new boolean[populationNumber / 2];
		rasgeleYonSayaci = new int[populationNumber / 2];
		r = new Random();
		birimVektor = new double[problemDimension];
	}

	private void initialize() {

		int delta = 0;
		if (amplitudeIsUsed) {
			for (int e = 0; e < numberofElements; e++) {
				Ls[e] = L[0];
				Hs[e] = H[0];
			}
			delta = numberofElements;
		}

		if (phaseIsUsed) {
			for (int e = 0; e < numberofElements; e++) {
				Ls[e + delta] = L[1];
				Hs[e + delta] = H[1];
			}
			delta += numberofElements;
		}

		if (positionIsUsed) {
			for (int e = 0; e < numberofElements; e++) {
				Ls[e + delta] = L[2];
				Hs[e + delta] = H[2];
			}
		}

		ilkDagitimiYap();
		
		iyileriListele();
		
		double enBuyukTersCost = 1/siraliMemberFitness[0];
		for (int m = 0; m < halfOfPopulation; m++) {
			System.out.println((1/siraliMemberFitness[m])/enBuyukTersCost);
		}

	}

	private void ilkDagitimiYap() {

		for (int m = 0; m < populationNumber; m++) {
			for (int d = 0; d < problemDimension; d++) {
				members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = members[d][m];
			}

			memberFitness[m] = cost.function(temp);
			if (memberFitness[m] < fitnessOfBestMember || bestMemberID == -1) {
				bestMemberID = m;
				fitnessOfBestMember = memberFitness[m];
			}
		}
		
	}

	private void iyileriListele() { // tabi ki yarisini
		for (int mOrdered = 0; mOrdered < halfOfPopulation; mOrdered++) {
			double currentBestInPool = -1;
			int currentBestIDInPool = 0;
			for (int m = 0; m < populationNumber; m++) {
				if (memberFitness[m] != -1) {
					if (memberFitness[m] < currentBestInPool || currentBestInPool == -1) {
						siraliMemberFitness[mOrdered] = memberFitness[m];
						currentBestInPool = memberFitness[m];
						currentBestIDInPool = m;
						for (int d = 0; d < problemDimension; d++) {							
							siraliMembers[d][mOrdered] = members[d][m];
						}						
					}				
				}
			}
			memberFitness[currentBestIDInPool] = -1;
		}
	}

	private void yeniJenerasyonunDagitiminiYap() {

		for (int m = 0; m < populationNumber; m++) {
			for (int d = 0; d < problemDimension; d++) {
				members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = members[d][m];
			}

			memberFitness[m] = cost.function(temp);
			if (memberFitness[m] < fitnessOfBestMember || bestMemberID == -1) {
				bestMemberID = m;
				fitnessOfBestMember = memberFitness[m];
			}
		}
		
	}
	
	public boolean iterate() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________
		
		

		// _______________________________________

		costValues[iterationIndex] = fitnessOfBestMember;
		iterationIndex++;

		if (iterationIndex == maximumIterationNumber)
			iterationState = false;

		return iterationState;
	}

}