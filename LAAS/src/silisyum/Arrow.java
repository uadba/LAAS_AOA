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
	public boolean[] istikamet; // "true" ise buyuk olan indekse dogru
	public int[] rasgeleYonSayaci;
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
		okunYonunuDuzelt(m);

		istikamet[m / 2] = true;
		rasgeleYonSayaci[m / 2] = 0;
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
			double carpan = iterasyonIndeksineOranla(okUzunluguBaslangici, okUzunluguBitisi, false);
			//if(m==0 && d==0) System.out.println(carpan);
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

	private void okunYonunuDuzelt(int m) {
		if (memberFitness[m] < memberFitness[m + 1]) {
			if (m == bestMemberID)
				bestMemberID = m + 1;
			double yedek; // genel
			yedek = memberFitness[m];
			memberFitness[m] = memberFitness[m + 1];
			memberFitness[m + 1] = yedek;

			for (int d = 0; d < problemDimension; d++) {
				yedek = members[d][m];
				members[d][m] = members[d][m + 1];
				members[d][m + 1] = yedek;
			}
		}
	}

	public boolean iterate() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________
		for (int m = 0; m < populationNumber; m += 2) {
			if (istikamet[m / 2] == true) {
				for (int d = 0; d < problemDimension; d++) {
					temp[d] = members[d][m + 1] + (members[d][m + 1] - members[d][m]);
					if (temp[d] > Hs[d] || temp[d] < Ls[d]) {
						temp[d] = members[d][m + 1] - (members[d][m + 1] - members[d][m]);
					}
				}

				double testMaliyet = cost.function(temp);
				if (testMaliyet < memberFitness[m + 1]) // yeni konumun degeri daha iyi ise
				{
					double yedek; // genel
					yedek = memberFitness[m + 1];
					memberFitness[m + 1] = testMaliyet;
					memberFitness[m] = yedek;

					for (int d = 0; d < problemDimension; d++) {
						yedek = members[d][m + 1];
						members[d][m + 1] = temp[d];
						members[d][m] = yedek;
					}

					if (memberFitness[m + 1] < fitnessOfBestMember) {
						bestMemberID = m + 1;
						fitnessOfBestMember = memberFitness[m + 1];
					}
				} else // yeni konumun degeri daha iyi DEGIL ise
				{
					memberFitness[m] = memberFitness[m + 1];
					for (int d = 0; d < problemDimension; d++) {
						members[d][m] = members[d][m + 1];
					}

					if (m + 1 == bestMemberID) {
						bestMemberID = m;
					}

					istikamet[m / 2] = false;
				}
			} else {
				int tahammulSiniri = 300;
				if(rasgeleYonSayaci[m/2] > tahammulSiniri)
				{
					//okunKuyrugunuBelirle(m);
					for (int d = 0; d < problemDimension; d++) {
						members[d][m] = members[d][bestMemberID];
					}
					memberFitness[m] = fitnessOfBestMember;
					rasgeleYonSayaci[m/2] = 0;
				}

				// Okun ucu
				okunUcunuBelirle(m);

				// Ok ne tarafa dogru?
				okunYonunuDuzelt(m);

				istikamet[m / 2] = true;
				rasgeleYonSayaci[m / 2]++;
			}
		}

		// _______________________________________

		costValues[iterationIndex] = fitnessOfBestMember;
		iterationIndex++;

		if (iterationIndex == maximumIterationNumber)
			iterationState = false;

		return iterationState;
	}

	private double iterasyonIndeksineOranla(double baslangic, double bitis, boolean artarak_mi) {
		double giden;

		if (artarak_mi == true)
			giden = baslangic + (bitis - baslangic) * ((double) (iterationIndex + 1) / maximumIterationNumber);
		else
			giden = baslangic - (baslangic - bitis) * ((double) (iterationIndex + 1) / maximumIterationNumber);
		
//		double f = (-(iterationIndex+1)+1000)*(0.9/1000);
//		double g = Math.cos(((double)iterationIndex)/20);
//		giden = Math.abs(f*g)+0.0001;
		
		return giden;
	}

}
