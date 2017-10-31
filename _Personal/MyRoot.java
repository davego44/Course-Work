import java.math.BigDecimal;

public class MyRoot {

	public static void main(String[] args) {
		System.out.println(my_sqrt(Double.parseDouble(args[0])));
		System.out.println(my_cbrt(Double.parseDouble(args[0])));
		System.out.println(my_fthrt(Double.parseDouble(args[0])));
	}

	public static double my_cbrt(double n)
	{
		double x = 1.0;
		for(int i=0; i<10; i++)
		{
			x = (2.0*x + n / (x*x)) / 3.0;
		}
		return (new BigDecimal(x)).setScale(7, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double my_sqrt(double n)
	{
		double x = 1.0;
		for(int i=0; i<10; i++)
		{
			x = (x + n / x) / 2.0;
		}
		return (new BigDecimal(x)).setScale(7, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double my_fthrt(double n)
	{
		double x = 1.0;
		for(int i=0; i<10; i++)
		{
			x = (3.0*x + n / (x*x*x)) / 4.0;
		}
		return (new BigDecimal(x)).setScale(7, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}