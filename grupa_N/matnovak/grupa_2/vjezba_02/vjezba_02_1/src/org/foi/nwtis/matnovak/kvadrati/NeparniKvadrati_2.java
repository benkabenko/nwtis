package org.foi.nwtis.matnovak.kvadrati;

public class NeparniKvadrati_2 extends Kvadrati {
	
	public NeparniKvadrati_2(int odBroja, int doBroja)
	{
		super(odBroja, doBroja);
	}
	
	public void ispis() {
		int pocetak = this.odBroja % 2 == 0 ? this.odBroja + 1 
												: this.odBroja;
		
		for(int i=pocetak;i <= this.doBroja;i+=4) {
//			System.out.println(i + " * " + i + " = " + i*i);
			System.out.printf("%3d * %3d = %3d\n", i, i, i*i);
		}
	}
}