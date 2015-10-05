package es.puravida.tae;

public class Main {

    public static final int ITERACIONES = 30;

    public static double calculaValorInversion(double cuota, double interes, int plazos){
        double ret =0;
        for(int n=1; n <= plazos; n++){
            ret += (cuota/ Math.pow( (1+interes), n));
        }
        return ret;
    }

    public static int closeTo( double a , double b){
        double diff = a-b;
        if( 0 == diff || Math.abs(diff) < 0.01){
            return 0;
        }
        return a < b ? -1 : 1;
    }

    public static double calculaTae( double prestamo, double comision, int plazos ){
        boolean forward=true;
        double interes = 0.001;
        double step = 0.001;
        int deep=1;

        double cuota = prestamo / plazos;
        double objetivo = prestamo - comision;
        double lastVac=calculaValorInversion( cuota, interes, plazos);
        for(int iter=0; iter<ITERACIONES; iter++){

            int close = closeTo(lastVac, objetivo);
            if( close == 0) {
                double tae = ( Math.pow(1+interes, 12) -1 )*100;
                return tae;
            }

            // estamos por encima del objetivo, poco interes,
            if( close > 0 ){
                if( !forward ){
                    deep = deep * 10;
                    forward = true;
                }
                interes += (step/deep);
            }

            // estamos por debajo del objetivo, mucho interes
            if( close < 0 ){
                // si hay que retrodecer cuando ibamos para adelante hay que profundizar
                if( forward ){
                    deep = deep * 10;
                    forward = false;
                }
                interes -= (step/deep);
            }

            lastVac=calculaValorInversion( cuota, interes, plazos);
        }

        return Double.NaN;
    }


    public static void assertTae( double prestamo, double comision, int plazos, double expected){
        double tae;
        if( closeTo(tae=calculaTae(prestamo,comision,plazos), expected) != 0 ){
            throw new RuntimeException(String.format("TAE de %f eur a %d meses y apertura %f debia ser %f pero dice %f",
                    prestamo, plazos, comision, expected, tae));
        }
    }

    public static void main(String[] args) {
	// write your code here
        assertTae(500,10,10,4.52);
        assertTae(500,20,10, 9.37);
        assertTae(647,10,10, 3.47);

    }
}
