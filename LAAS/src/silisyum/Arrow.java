package silisyum;

import java.util.Random;

public class Arrow {

	private int numberofElements;
	private int problemDimension = 0;
	private int populationNumber;
	public double[][] members;
	private double[] memberFitness;
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
	public boolean[] yon; // "true" ise buyuk olan indekse dogru
	Random r;
	double[] birimVektor;
	double bitisIcinDelta;
	// private IyiUyelerinListesi iuListesi;

	public Arrow(int _numberofElements, int _populationNumber, int _maximumIterationNumber, double _F, double _Cr,
			double _okUzunluguBaslangici, double _okUzunluguBitisi, double[] _L, double[] _H, AntennaArray _aA,
			AntennaArray _aAForP, Mask _mask, boolean _amplitudeIsUsed, boolean _phaseIsUsed, boolean _positionIsUsed) {

		numberofElements = _numberofElements;
		populationNumber = _populationNumber;
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

			ilkDagitimiYap(m);

		}
	}

	private void ilkDagitimiYap(int m) {

		// Okun kuyrugu
		okunKuyrugunuBelirle(m);

		// Okun ucu
		okunUcunuBelirle(m);

		// Ok ne tarafa dogru?
		okunYonunuBelirle(m);
	}

	private void okunKuyrugunuBelirle(int m) {
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

	private void okunUcunuBelirle(int m) {
		double hipotenus = 0;
		for (int d = 0; d < problemDimension; d++) {
			birimVektor[d] = Math.random() * 2 - 1;
			hipotenus += birimVektor[d] * birimVektor[d];
		}
		hipotenus = Math.sqrt(hipotenus);
		for (int d = 0; d < problemDimension; d++) {
			birimVektor[d] = birimVektor[d] / hipotenus;

			// the distance between tip and tail
			double carpan = 0.2; // iterasyonIndeksineOranla(okUzunluguBaslangici, okUzunluguBitisi, false);
			double okUzunlugu = carpan * (Hs[d] - Ls[d]);
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
		if (memberFitness[m + 1] < fitnessOfBestMember || bestMemberID == -1) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		}
	}

	private void okunYonunuBelirle(int m) {
		if (memberFitness[m] < memberFitness[m + 1])
		{
			double temp; // genel
			temp = memberFitness[m];
			memberFitness[m] = memberFitness[m + 1];
			memberFitness[m+1] = temp;
			
			for (int d = 0; d < problemDimension; d++) {
				temp = members[d][m];
				members[d][m] = members[d][m + 1];
				members[d][m+1] = temp;
			}			
		}
	}

	public boolean iterate() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________

		for (int m = 0; m < populationNumber; m += 2) {

			ilkDagitimiYap(m);

		}

		// _______________________________________

		costValues[iterationIndex] = fitnessOfBestMember;
		iterationIndex++;

		if (iterationIndex == maximumIterationNumber)
			iterationState = false;

		return iterationState;
	}

}
