package silisyum;

import java.util.Random;

public class Arrow2 {

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

	public Arrow2(int _numberofElements, int _populationNumber, int _maximumIterationNumber, double _F, double _Cr,
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
			members[d][m] = temp[d];
		}

		memberFitness[m] = cost.function(temp);
		if (bestMemberID == -1) {
			bestMemberID = m;
			fitnessOfBestMember = memberFitness[m];
		} else if (memberFitness[m] < fitnessOfBestMember) {
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
		if (bestMemberID == -1) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		} else if (memberFitness[m + 1] < fitnessOfBestMember) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		}

	}

	private void okunYonunuBelirle(int m) {
		if (memberFitness[m] >= memberFitness[m + 1]) // okun ucu daha kucukse "true" (yon ileri [dogru] anlaminda)
			yon[m / 2] = true;
		else
			yon[m / 2] = false;
	}

	public boolean iterate() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________
		double yedege_al_mem, test_mem;
		for (int m = 0; m < populationNumber; m += 2) {
			if (yon[m / 2] == true) { // yon okun ucuna ([m+1]'e) dogru ise
				
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

				if (memberFitness[m + 1] < fitnessOfBestMember) {
					bestMemberID = m + 1;
					fitnessOfBestMember = memberFitness[m + 1];
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

	private double iterasyonIndeksineOranla(double baslangic, double bitis, boolean artarak_mi) {
		double giden;

		if (artarak_mi == true)
			giden = baslangic + (bitis - baslangic) * ((double) (iterationIndex + 1) / maximumIterationNumber);
		else
			giden = baslangic - (baslangic - bitis) * ((double) (iterationIndex + 1) / maximumIterationNumber);
		return giden;
	}

	public boolean iterate2() {

		// Buraya iteratif algoritmayi yazacaksin.
		// _______________________________________
		double yedege_al_mem, test_mem;
		for (int m = 0; m < populationNumber; m += 2) {

			if (m != bestMemberID && (m + 1) != bestMemberID) { // en iyi uyeye dokunma
				if (memberFitness[m] >= memberFitness[m + 1] && yon[m / 2] != true) // yon degismis tekrar dagit
				{
					for (int d = 0; d < problemDimension; d++) {
						temp[d] = members[d][m + 1];
					}
					okDagit2(m, 1);

					// yonu belirle
//					if (memberFitness[m] >= memberFitness[m + 1]) // kuyruk buyukse "true" (yon dogru anlaminda)
//						yon[m / 2] = true;
//					else
//						yon[m / 2] = false;
				}
				if (memberFitness[m] < memberFitness[m + 1] && yon[m / 2] != false) // bu da yon degistirmis
				{
					for (int d = 0; d < problemDimension; d++) {
						temp[d] = members[d][m];
					}
					okDagit2(m, 1);

					// yonu belirle
//					if (memberFitness[m] >= memberFitness[m + 1]) // kuyruk buyukse "true" (yon dogru anlaminda)
//						yon[m / 2] = true;
//					else
//						yon[m / 2] = false;
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
					bestMemberID = m + 1;
					fitnessOfBestMember = memberFitness[m + 1];
				}

			} else { // burada ok geri geri gidiyor.
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
					bestMemberID = m;
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

	private void okDagit2(int m, int hangiAsama) {

		// baslangiclarin atanmasi
		if (hangiAsama == 0) { // ilk defa ataniyor
			for (int d = 0; d < problemDimension; d++) {
				members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = members[d][m];
				members[d][m] = temp[d];

				memberFitness[m] = cost.function(temp);
				if (bestMemberID == -1) {
					bestMemberID = m;
					fitnessOfBestMember = memberFitness[m];
				} else if (memberFitness[m] < fitnessOfBestMember) {
					bestMemberID = m;
					fitnessOfBestMember = memberFitness[m];
				}
			}
		} else { // iterasyon icindeyken buraya gel.
			double rasgele = Math.random();
			double eniyiDegeriSecmeOlasiligi = iterasyonIndeksineOranla(0, 1, true);
			if (rasgele < eniyiDegeriSecmeOlasiligi) {
				for (int d = 0; d < problemDimension; d++) {
					members[d][m] = members[d][bestMemberID];
					memberFitness[m] = memberFitness[bestMemberID];
				}
			} else {
				//////// ---------- burasi okun tekrar kendi merkezi olacak -------------------
				//////// BASLA
//				for (int d = 0; d < problemDimension; d++) {
//					members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
//					temp[d] = members[d][m];
//				}
				//////// ---------- burasi okun tekrar kendi merkezi olacak -------------------
				//////// BITIS
			}
		}

		// bitislerin atanmasi
		// Oncelikle rasgele bir yon belirleme islemi gerceklestirilmeli
		// Bunun icin 1 ve -1 degerleri arasinda "d" adet rasgele deger uretilmeli
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
		if (bestMemberID == -1) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		} else if (memberFitness[m + 1] < fitnessOfBestMember) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		}

	}

	private void okDagit(int m, int hangiAsama) {

		// baslangiclarin atanmasi
		if (hangiAsama == 0) {
			for (int d = 0; d < problemDimension; d++) {
				members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = members[d][m];
				members[d][m] = temp[d];
			}
		} else {
			double rasgele = Math.random();
			double eniyiDegeriSecmeOlasiligi = iterasyonIndeksineOranla(0, 1, true);
			if (rasgele < eniyiDegeriSecmeOlasiligi) {
				for (int d = 0; d < problemDimension; d++) {
					members[d][m] = members[d][bestMemberID];
					temp[d] = members[d][m];
				}
			} else {
				for (int d = 0; d < problemDimension; d++) {
					members[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
					temp[d] = members[d][m];
				}
			}
		}

		memberFitness[m] = cost.function(temp);
		if (bestMemberID == -1) {
			bestMemberID = m;
			fitnessOfBestMember = memberFitness[m];
		} else if (memberFitness[m] < fitnessOfBestMember) {
			bestMemberID = m;
			fitnessOfBestMember = memberFitness[m];
		}

		// bitislerin atanmasi
		// Oncelikle rasgele bir yon belirleme islemi gerceklestirilmeli
		// Bunun icin 1 ve -1 degerleri arasinda "d" adet rasgele deger uretilmeli
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
		if (bestMemberID == -1) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		} else if (memberFitness[m + 1] < fitnessOfBestMember) {
			bestMemberID = m + 1;
			fitnessOfBestMember = memberFitness[m + 1];
		}

	}

}
