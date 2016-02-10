package functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * 
 * @author fadihariri
 * @date January 12, 2016
 * @Description  methods for computations required for the Calculator project delivered for the Software Engineering course
 *
 */
public class Compute 
{
	private double pi;
	private double ln2;

	public Compute ()
  {
		computePi();
    computeLn2();
	}

	public static void main(String[] args) 
  {	
		Compute c = new Compute ();
		System.out.println (c.chebyshev(10,0.785398));
		//System.out.println (c.factorial(25).toString());
		//System.out.println (c.cos(32, 'd'));
		System.out.println (c.pi);
		System.out.println (c.ln2);
		System.out.println (c.factorial(5));
		System.out.println (c.computePowers (2, 100));
		int [] arr = c.getFraction(-2.5);
		System.out.println(arr[0]+"/"+arr[1]);
		System.out.println (c.powerOfX (3, 5.95));

		System.out.println (c.computePowers (2, -3));
		double [] arr2 = {9, 2, 5, 4, 12, 7, 8, 11, 9, 3, 7, 4, 12, 5, 4, 10, 9, 6, 9, 4};
		System.out.println (c.standardDev (arr2));
		System.out.println ("ln:"+c.ln(25));
		//System.out.println (arithGeomMean (24,6));
		System.out.println ("log10: "+c.log10(1000));
		System.out.println (c.nthroot(2,2));
	}

	/**
	 * Helper methods
	 */
	private int [] getFraction (double x)
  {
		/**
		 * basically we are returning the reduced fraction of a value
		 * 3.546 is equivalent to 3+ (546/1000)
		 * reduce by diving both num and denom by GCD
		 */
		int decimal = (int)x;
		String fractional="";
		String temp = String.format("%f", x);
		String sign = "+";
		//System.out.println (temp);
		boolean flag= false;

		for (int i=0; i <temp.length(); i++)
    {
      char currChar = temp.charAt(i); 
			if (currChar == '-') 
      {
        sign = "-";//reassign sign
      }

			if (currChar == '.')
      {
        flag = true;
        continue;
      }
			
      if (flag)
      {
				String s =  "" + temp.charAt(i);
				fractional += s;
			}
		}

    //no fraction
		if (Integer.parseInt(fractional) == 0)
    {
			return new int []{decimal, 1};
		}

		//System.out.println (fractional);
		String tens = "1";

		for (int i=0; i < fractional.length(); i++)
    {
      tens+= "0";
    }

		int numerator = Integer.parseInt(fractional);
		int denom = Integer.parseInt(tens);
		int div = gcd(numerator, denom);
		
    if (sign.equals("-"))
    {
			return new int []{-((-decimal * denom/div)+(numerator/div)),denom/div};
    }

		return new int []{((decimal * denom/div)+(numerator/div)),denom/div};
	}

	private  int gcd (int x, int y)
  {
		int div=1;
		int min =(x<y)?x:y;

		for (int i=1; i <= min; i++)
    {
			div=(x%i == 0 && y%i == 0)? i: div;
		}
    
		return div;
	}
	/**
	 * nth-root algorithm
	 * source: http://rosettacode.org/wiki/Nth_root#Java
	 */
	private  double nthroot(int n, double x) 
  {
		if(n % 2 ==0 && x< 0)
    {
      System.out.println ("Error! can not compute root...");System.exit(0);
    }

		int np = n - 1;
		double g1 = x;
		double g2 = iter(g1, np, n, x);
		
    while (g1 != g2) 
    {
			g1 = iter(g1, np, n, x);
			g2 = iter(iter(g2, np, n, x), np, n, x);
		}

		return g1;
	}

	private double iter(double g, int np, int n, double x) 
  {
		return (np * g + x / Math.pow(g, np)) / n;
	}

	//compute a^X
	private  double computePowers (double a, double x)
  {
		if (x == 0) return 1;

    //convert x into a reduced fraction m/n
		int [] assets = getFraction (x);

		//a^(m/n) = (a^m)^(1/n) = nth root of (a^m)
		double val = 1;//a=pi
		int m= (assets [0]<0)?-assets[0]:assets[0];
		int n = assets [1];
		
    //compute a^m
		for (int i=1; i <= m; i++)
    {
      val *= a;
    }

		if (assets[0] < 0)val = invert(val);
		//compute nth root of (a^m)
		
		return n == 1 ? val : nthroot(n,val);
	}

	/**
	 * Compute Powers
	 */
	//10^x 
	public  double powerOfTen (double x)
  {
		return computePowers(10,x);
	}
	
  //π^x
	public  double powerOfPi (double x)
  {
		return computePowers (this.pi, x);
	}
	
  //e^x 
	public  double powerofE (double x)
  {
		return computePowers (Math.E,x);
	}
	
  //√x 
	public  double squareRoot (double x)
  {
		return nthroot (2, x);
	}
	
  //σ (Standard Deviation)
	//square root (1/N * for all X in array[sum += (Xi -mean)^2])
	public  double standardDev (double [] values)
  {
		//compute average
		double mean = 0;
		for (int i=0; i < values.length; i++)
    {
      mean += values[i];
    }

		mean = mean/values.length;

		double sum=0;
		//compute sum of squares (Xi - mean)^2
		for (int i=0; i < values.length; i++){sum += computePowers((values[i]-mean),2);}
		
    //compute SD
		return nthroot(2, sum / values.length);
	}

	// x^y
	public  double powerOfX (double x, double y)
  {
		return computePowers (x,y);
	}

	/**
	 * Logarithms
	 */

	//ln(x); 
	public double ln(double z)
  {
		//return 2*ln_helper1(z,7);
		return ln_helper2(z);
	}
	
  //based on the area hyperbolic tangent function
	//ln(z)=2*[sum of series from n=0, infinity][(1/2n+1)*[((z-1)/(z+1))^(2n+1)]
	private  double ln_helper1(double z, int n)
  {
		if (n == 0) return 1;

		return ((1.0 / (2 * n + 1)) * computePowers(((z - 1) / (z + 1)), 2 * n + 1)) + ln_helper1(z, n - 1);
	}

	//based on arithmetic-geometric mean
	//https://en.wikipedia.org/wiki/Arithmetic–geometric_mean#Other_applications
	//https://en.wikipedia.org/wiki/Logarithm#Calculation
	//ln(x)=[pi/(2*M(1,(2^(2-m)) /x))]-m*ln(2)
	//m is such that x*2^m > 2^(p/2); p is precision
	private  double ln_helper2 (double x)
  {
		//compute m with 0.000000001 precision
		double m= 0;
		double p=70;
		double precision= 100000000000000.0;

		//System.out.println (x*computePowers (2,m));
		//System.out.println (computePowers (2, p/2));
		while (x*computePowers (2,m) < computePowers (2, p/2))
    {
			m+=1;
		}

		double pi= this.pi;
		double agm = arithGeomMean (1, (computePowers(2, (2-m)))/x, precision);
		m *= 0.6931471805599453;// (m*ln(2))
		return (pi/(2*agm))-m;
	}

	private  double arithGeomMean (double x, double y, double precision)
  {
		double a = x, g = y;//a0, g0
		double tempa =a, tempg=g;
		//precision 8 digits
		while (a * (precision) != g*(precision))
    {
			a = 0.5*(tempa+tempg);
			g= nthroot (2,tempa*tempg);
			tempa=a; tempg=g;
		}

		return a;
	}

	//log10(x)
	public  double log10 (double x)
  {
		//ln(x)/ln(10)
		return ln(x) / ln(10);
	}

	/**
	 * Compute ln2, pi
	 */
	//source:http://mathworld.wolfram.com/PiFormulas.html
	//BBP type formula
	private void computePi (){
		double val=0;
		int precision = 1000;
		for (int i=0; i <= precision; i++){
			val += (1/computePowers(16,i))*((4.0/(8*i+1))- (2.0/(8*i+4)) - (1.0/(8*i+5)) -(1.0/(8*i+6))  );
		}
		this.pi= val;
	}

	//source:http://www.mathisfunforum.com/viewtopic.php?id=12674
	private void computeLn2 ()
  {
		double val =0;int p=1000;
		
    //compute ln2
		for (int i = 1; i <= p; i++)
    {
			val += 1 / (computePowers(2, i) * i);
		}

		this.ln2 = val;
	}

	/**
	 * Trigonometry
	 */

	//helper functions to convert between degrees, dms (degree in hour minute sec), radians
	private  double degToRad (double deg)
  {
    //pi= 180;
		return deg * pi / 180;
	}

	private  double radToDeg (double rad)
  {
		return rad * 180 / pi;
	}

	private double gradToRad (double grad)
  {
		return grad * pi / 200;
	}

	private double radToGrad (double rad)
  {
		return rad * 200 / pi;
	}

	private  int [] degToDms (double deg)
  {
		return new int []{1};
	}

	public BigDecimal factorial(int x)
  {
		BigDecimal d = BigDecimal.valueOf(1.0);

		for (int i=1; i <= x; i++)
    {
			d = d.multiply(BigDecimal.valueOf(i));
		}

		return d;
	}

}
