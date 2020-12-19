package silisyum;

import java.util.Random;

public class Arrow {

	private int numberofElements;
	private int problemDimension = 0;
	private int populationNumber;
	public double[][] members;
	private double[] memberFitness;
	private double[] temp;
	public int bestMember = -1;
	public double fitnessOfBestMember = 0;
	public int maximumIterationNumber;
	private double okUzunluguOrani;
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
	public boolean[] yon; // "true" ise buyuk olan indekse dogru
	Random r;
	double[] birimVektor;
	double bitisIcinDelta;

	public Arrow(int _numberofElements, int _populationNumber, int _maximumIterationNumber, double _F, double _Cr,
			double _okUzunluguOrani, double[] _L, double[] _H, AntennaArray _aA, AntennaArray _aAForP, Mask _mask,
			boolean _amplitudeIsUsed, boolean _phaseIsUsed, boolean _positionIsUsed) {

		numberofElements = _numberofElements;
		populationNumber = _populationNumber;
		maximumIterationNumber = _maximumIterationNumber;
		okUzunluguOrani = _okUzunluguOrani;
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
		temp = new double[problemDimension];
		Ls = new double[problemDimension];
		Hs = new double[problemDimension];
		yon = new boolean[populationNumber / 2];
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

		for (int m = 0; m < populationNumber; m += 2) {

			okDagit(m);

			// yonu belirle
			if (memberFitness[m] > memberFitness[m + 1]) // ikincisi yani okun ucu buyukse "true"
				yon[m / 2] = true;
			else
				yon[m / 2] = false;
		}

		// TEST farký belirlemek için TEST///////////////////////
//		double fark;
//		double ghipo;
//		for (int pm = 0; pm < populationNumber; pm += 2) {
//			ghipo=0;
//			for (int d = 0; d < problemDimension; d++) {
//				fark = members[d][pm] - members[d][pm+1];
//				double okUzunlugu = okUzunluguOrani*(Hs[d] - Ls[d]);
//				fark = fark/okUzunlugu;
//				ghipo += fark * fark;
//			}
//			ghipo = Math.sqrt(ghipo);
//			//System.out.println(ghipo);
//		}		
		// TEST --------------------- TEST///////////////////////
	}

	private void okDagit(int m) {

		// basllangiclarin atanmasi
		for (int d = 0; d < problemDimension; d++) {
			members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
			temp[d] = members[d][m];
		}

		memberFitness[m] = cost.function(temp);
		if (bestMember == -1) {
			bestMember = m;
			fitnessOfBestMember = memberFitness[m];
		} else if (memberFitness[m] < fitnessOfBestMember) {
			bestMember = m;
			fitnessOfBestMember = memberFitness[m];
		}

		// bitislerin atanmasi
		// Oncelikle rasgele bir yon belirleme islemi gerceklestirilmeli
		// Bunun için 1 ve -1 deðerleri arasýnda "d" adet rasgele deðer üretilmeli
		double hipotenus = 0;
		for (int d = 0; d < problemDimension; d++) {
			birimVektor[d] = Math.random() * 2 - 1;
			hipotenus += birimVektor[d] * birimVektor[d];
		}
		hipotenus = Math.sqrt(hipotenus);
		for (int d = 0; d < problemDimension; d++) {
			birimVektor[d] = birimVektor[d] / hipotenus;

			// the distance between tip and tail
			double okUzunlugu = okUzunluguOrani * (Hs[d] - Ls[d]);
			bitisIcinDelta = okUzunlugu * birimVektor[d];

			// if it exceeds the border, pull it into the safe area
			double yeniKonum = members[d][m] + bitisIcinDelta;
			if (yeniKonum > Hs[d] || yeniKonum < Ls[d]) {
				yeniKonum = members[d][m] - bitisIcinDelta;
			}
			members[d][m + 1] = yeniKonum;
			temp[d] = members[d][m + 1];
		}

		memberFitness[m + 1] = cost.function(temp);
		if (bestMember == -1) {
			bestMember = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		} else if (memberFitness[m + 1] < fitnessOfBestMember) {
			bestMember = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		}

	}

	public boolean iterate() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________
		double yedege_al_mem, test_mem;
		for (int m = 0; m < populationNumber; m += 2) {

			if (m != bestMember || (m + 1) != bestMember) { // en iyi uyeye dokunma
				if (memberFitness[m] >= memberFitness[m + 1] && yon[m / 2] != true) // yon degismis tekrar dagit
				{
					okDagit(m);
				}
				if (memberFitness[m] < memberFitness[m + 1] && yon[m / 2] != false) // bu da yon degistirmis
				{
					okDagit(m);					
				}
			}

			if (memberFitness[m + 1] < memberFitness[m]) { // eger sonraki adim daha iyi ise ileriye dogru gitmeye
																// devam et.
				for (int d = 0; d < problemDimension; d++) {
					yedege_al_mem = members[d][m + 1];
					test_mem = members[d][m + 1] + (members[d][m + 1] - members[d][m]);
					if (test_mem > Hs[d] || test_mem < Ls[d]) {
						test_mem = members[d][m + 1] - (members[d][m + 1] - members[d][m]);
					}
					members[d][m + 1] = test_mem;
					members[d][m] = yedege_al_mem;
					temp[d] = members[d][m + 1];
				}

				memberFitness[m] = memberFitness[m + 1];
				
				memberFitness[m + 1] = cost.function(temp);

				// Alternative path with a deviation. ***************************

				// **************************************************************

				if (memberFitness[m + 1] < fitnessOfBestMember) {
					bestMember = m + 1;
					fitnessOfBestMember = memberFitness[m + 1];
				}

			} else {
				for (int d = 0; d < problemDimension; d++) {
					yedege_al_mem = members[d][m];
					test_mem = members[d][m] + (members[d][m] - members[d][m + 1]);
					if (test_mem > Hs[d] || test_mem < Ls[d]) {
						test_mem = members[d][m] - (members[d][m] - members[d][m + 1]);
					}
					members[d][m] = test_mem;
					members[d][m + 1] = yedege_al_mem;
					temp[d] = members[d][m];
				}

				memberFitness[m + 1] = memberFitness[m];
				memberFitness[m] = cost.function(temp);
				
				if (memberFitness[m] < fitnessOfBestMember) {
					bestMember = m;
					fitnessOfBestMember = memberFitness[m];
				}
			}
		}

		// _______________________________________

		costValues[iterationIndex] = fitnessOfBestMember;
		iterationIndex++;

		if (iterationIndex == maximumIterationNumber)
			iterationState = false;

		return iterationState;
	}
}
