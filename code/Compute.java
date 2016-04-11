/**
 * @author Fadi Hariri, Maryna Kalachova, Nicholas Hillier, Navdeep Singh, Savithru Teja
 * This class provides a set of methods that computes the constants pi and the natural logarithm of 2,
 * as well as the square root, powers of 10, powers of a variable, sine, factorials and logarithms.
 */
public class Compute {
    protected double pi;
    protected double ln2;

    /**
     * Initialize the computation of pi and ln2 constants.
     */
    public Compute() {
        computePi();
        computeLogN();
    }

    /**
     * Helper methods
     */
    private int[] getFraction(double x) {
        /**
         * basically we are returning the reduced fraction of a value
         * 3.546 is equivalent to 3+ (546/1000)
         * reduce by diving both num and denom by GCD
         */
        int decimal = (int) x;
        String fractional = "";
        String temp = String.format("%f", x);
        String sign = "+";
        boolean flag = false;

        for (int i = 0; i < temp.length(); i++) {
            char currChar = temp.charAt(i);
            if (currChar == '-') {
                sign = "-";//reassign sign
            }

            if (currChar == '.') {
                flag = true;
                continue;
            }

            if (flag) {
                String s = "" + temp.charAt(i);
                fractional += s;
            }
        }

        //minimize fraction [v imp]
        if (fractional.length() > 3) {
            fractional = fractional.substring(0, 2);
        }
        //no fraction
        if (Integer.parseInt(fractional) == 0) {
            return new int[]{decimal, 1};
        }

        String tens = "1";

        for (int i = 0; i < fractional.length(); i++) {
            tens += "0";
        }

        int numerator = Integer.parseInt(fractional);
        int denominator = Integer.parseInt(tens);
        int div = greatestCommonDenominator(numerator, denominator);

        if (sign.equals("-")) {
            return new int[]{(decimal == 0 ? -1 : 1) * ((-decimal * denominator / div) + (numerator / div)), denominator / div};
        }

        return new int[]{((decimal * denominator / div) + (numerator / div)), denominator / div};
    }

    private int greatestCommonDenominator(int x, int y) {
        int div = 1;
        int min = (x < y) ? x : y;

        for (int i = 1; i <= min; i++) {
            div = (x % i == 0 && y % i == 0) ? i : div;
        }

        return div;
    }

    /**
     * nth-root algorithm
     * source: http://rosettacode.org/wiki/Nth_root#Java
     */
    private double nthRoot(int n, double x) {
        if (n % 2 == 0 && x < 0) {
            System.out.println("Error! can not compute root...");
            System.exit(0);
        }

        int np = n - 1;
        double g1 = x;
        double g2 = iterate(g1, np, n, x);

        while (g1 != g2) {
            g1 = iterate(g1, np, n, x);
            g2 = iterate(iterate(g2, np, n, x), np, n, x);
        }

        return g1;
    }

    private double iterate(double g, int np, int n, double x) {
        return (np * g + x / Math.pow(g, np)) / n;
    }

    private double computePowers(double a, double x) {
        if (x == 0) return 1;

        //convert x into a reduced fraction m/n
        int[] assets = getFraction(x);

        //a^(m/n) = (a^m)^(1/n) = nth root of (a^m)
        double val = 1;
        int m = (assets[0] < 0) ? -assets[0] : assets[0];
        int n = assets[1];

        //compute a^m
        for (int i = 1; i <= m; i++) {
            val *= a;
        }

        if (assets[0] < 0) val = invert(val);
        //compute nth root of (a^m)

        return n == 1 ? val : nthRoot(n, val);
    }

    /**
     * @param x a double
     * @return double corresponding to 10^x
     */
    public double powerOfTen(double x) {
        return computePowers(10, x);
    }

    /**
     * @param x a double
     * @return double corresponding to _/x
     */
    public double squareRoot(double x) {
        return nthRoot(2, x);
    }

    /**
     * @param x a double representing a base
     * @param y a double representing a power
     * @return double corresponding to x^y
     */
    public double powerOfX(double x, double y) {
        return computePowers(x, y);
    }

    /**
     * Logarithms
     */
    private double logN(double z) {
        return logNHelper(z);
    }

    //based on arithmetic-geometric mean
    //https://en.wikipedia.org/wiki/Arithmetic–geometric_mean#Other_applications
    //https://en.wikipedia.org/wiki/Logarithm#Calculation
    //logN(x)=[pi/(2*M(1,(2^(2-m)) /x))]-m*logN(2)
    //m is such that x*2^m > 2^(p/2); p is precision
    private double logNHelper(double x) {
        double m = 0;
        double p = 70;
        double precision = 100000000000000.0;

        while (x * computePowers(2, m) < computePowers(2, p / 2)) {
            m += 1;
        }

        double pi = this.pi;
        double mean = mean(1, computePowers(2, (2 - m)) / x, precision);
        m *= 0.6931471805599453;
        return (pi / (2 * mean)) - m;
    }

    private double mean(double x, double y, double precision) {
        double output = x;
        double g = y;
        double xTemp = output;
        double yTemp = g;

        while (output * precision != g * precision) {
            output = 0.5 * (xTemp + yTemp);
            g = nthRoot(2, xTemp * yTemp);
            xTemp = output;
            yTemp = g;
        }

        return output;
    }

    /**
     * @param x double representing a power
     * @return double corresponding to log10(x)
     */
    public double log10(double x) { return logN(x) / logN(10); }

    /**
     * Compute ln2, pi
     */
    //source:http://mathworld.wolfram.com/PiFormulas.html
    //BBP type formula
    private void computePi() {
        double val = 0;
        int precision = 1000;
        for (int i = 0; i <= precision; i++) {
            val += (1 / computePowers(16, i)) * ((4.0 / (8 * i + 1)) - (2.0 / (8 * i + 4)) - (1.0 / (8 * i + 5)) - (1.0 / (8 * i + 6)));
        }
        this.pi = val;
    }

    //source:http://www.mathisfunforum.com/viewtopic.php?id=12674
    private void computeLogN() {
        double val = 0;
        int p = 1000;

        for (int i = 1; i <= p; i++) {
            val += 1 / (computePowers(2, i) * i);
        }

        this.ln2 = val;
    }

    /**
     * Trigonometry
     * Taylor series
     * Preconditions: angle is in degree so convert to rads first
     * source:http://mathonweb.com/help_ebook/html/algorithms.htm
     *
     */
    /**
     * @param angle in degrees
     * @return double corresponding to sin(angle). The method converts the angle to radians prior to computation.
     */
    public double sin(double angle) {
        double ang = angle % 360; //make in range of 360
        double ang2 = ang % 180; //make in range of 180
        //source:http://mathonweb.com/help_ebook/html/algorithms.htm
        double ang3 = (ang2 > 90) ? 180 - ang2 : ang2;
        double a = degToRad(ang3);//convert to radians

        double val = 0;
        int precision = 1000;
        boolean alternate = true;
        for (int i = 1; i <= precision; i += 2) {
            double temp = powerOfX(a, i) / factorial(i);
            val = (alternate) ? val + temp : val - temp;
            alternate = !alternate;
        }
        return (ang > 180) ? -val : val;//if angle [after minimization] is < 180 then return as is else return the symmetric value.
    }

    /**
     * Factorial
     *
     * @param val a non-floating point double
     * @return double Factorial of val.
     */
    public double factorial(double val) {
        if (val == 1) return val;
        return val * factorial(val - 1);
    }

    /**
     * absolute value
     *
     * @return absolute value of x
     */
    private double abs(double x) {
        return (x < 0) ? -x : x;
    }

    //helper functions to convert between degrees, dms (degree in hour minute sec), radians
    private double degToRad(double deg) {
        return deg * pi / 180;
    }

    private double radToDeg(double rad) {
        return rad * 180 / pi;
    }

    private double gradToRad(double grad) {
        return grad * pi / 200;
    }

    private double radToGrad(double rad) {
        return rad * 200 / pi;
    }

    private int[] degToDms(double deg) {
        return new int[]{1};
    }

    private double invert(double d) {
        return 1 / d;
    }
}
