package silisyum;

import java.util.Random;

public class IyiUyelerinListesi {

	int listeUzunlugu;
	int enIyiUye;
	int enKotuUye;
	private double[][] goodMembers;
	private double[] goodMemberFitness;
	private double[] temp;
	private int problemDimension;
	private int populationNumber;
	private double[] Ls;
	private double[] Hs;
	private Random r;
	private Cost cost;

	public IyiUyelerinListesi(int _listeUzunlugu, int _problemDimension, int _populationNumber, double[] _Ls,
			double[] _Hs, Cost _cost) {

		listeUzunlugu = _listeUzunlugu;
		problemDimension = _problemDimension;
		populationNumber = _populationNumber;
		Ls = _Ls;
		Hs = _Hs;

		enIyiUye = -1;
		enKotuUye = -1;
		r = new Random();

		goodMembers = new double[problemDimension][populationNumber];
		goodMemberFitness = new double[populationNumber];
		temp = new double[problemDimension];

		cost = _cost;

	}

	public void listeyiDoldur() {
		for (int m = 0; m < populationNumber; m++) {
			for (int d = 0; d < problemDimension; d++) {
				goodMembers[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = goodMembers[d][m];
			}
			goodMemberFitness[m] = cost.function(temp);
		}
		
		enleriTespitEt();
		
		// bu belki kalkabilir
		for (int kez = 0; kez < 10; kez++) {
			for (int d = 0; d < problemDimension; d++) {
				temp[d] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
			}
			bunuKabulEderMisin(cost.function(temp), temp);
		}		

	}

	private void enleriTespitEt() {
		for (int m = 0; m < populationNumber; m++) {

			if (enIyiUye == -1) {
				enIyiUye = m;
			}

			if (goodMemberFitness[m] < goodMemberFitness[enIyiUye]) {
				enIyiUye = m;
			}

			if (enKotuUye == -1) {
				enKotuUye = m;
			}

			if (goodMemberFitness[m] > goodMemberFitness[enKotuUye]) {
				enKotuUye = m;
			}

		}
	}

	public void bunuKabulEderMisin(double costDegeri, double[] theVector) {
		if (costDegeri < goodMemberFitness[enKotuUye]) {
			goodMemberFitness[enKotuUye] = costDegeri;
			for (int d = 0; d < theVector.length; d++) {
				goodMembers[d][enKotuUye] = theVector[d];
			}
			enleriTespitEt();
		}
	}

	public double[] rasgeleBirUyeSec() {

		int hangisi = r.nextInt(populationNumber);

		double[] uye = new double[problemDimension];

		for (int d = 0; d < uye.length; d++) {
			uye[d] = goodMembers[d][hangisi];
		}

		return uye;

	}

}
