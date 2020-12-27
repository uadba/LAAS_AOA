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

	public IyiUyelerinListesi(int _listeUzunlugu, int _problemDimension, int _populationNumber, double[] _Ls,
			double[] _Hs) {

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
		Ls = new double[problemDimension];
		Hs = new double[problemDimension];
	}

	private void listeyiDoldur() {
		for (int m = 0; m < goodMembers.length; m++) {

			for (int d = 0; d < problemDimension; d++) {
				goodMembers[d][m] = Ls[d] + (Hs[d] - Ls[d]) * r.nextDouble();
				temp[d] = goodMembers[d][m];
			}

		}

	}

}
