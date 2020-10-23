package server;

class Base { 

  private int convertToInt(char ch) {
   if(ch >= '0' && ch <= '9')
      return (int)ch - '0';
   else
      return (int)ch - 'A' + 10;
  }
  
  private char convertToBase(int n) {
    if (n >= 0 && n <= 9) 
        return (char)(n + 48); 
    else
        return (char)(n - 10 + 65);
  }

  /* convert a number from base-N to base-10 */
  private int toDecimal(String num, int base) {
    int len = num.length();
    int exp = 1;
    int decimalVal = 0;

    for(int i = len - 1; i >= 0; i--) {
      int _tmp = convertToInt(num.charAt(i));
      if(_tmp >= base) {
         throw new java.lang.RuntimeException("Incorrect value provided for the given base!");
       }
       decimalVal += _tmp * exp;
       exp *= base;
    }
    return decimalVal;
  }

  /* convert a number from base-10 to base-N */
  private String toBaseN(int num, int base) {
    StringBuilder baseN = new StringBuilder("");
     while(num > 0) {
        baseN.append(convertToBase(num % base));
        num /= base;
     }
     return baseN.reverse().toString();
  }

  public String add(String num1, String num2, int base) {
    int num1Int = toDecimal(num1, base);
    int num2Int = toDecimal(num2, base);
    return toBaseN(num1Int + num2Int, base);
  }

  public String substract(String num1, String num2, int base) {
    int num1Int = toDecimal(num1, base);
    int num2Int = toDecimal(num2, base);
    return toBaseN(num1Int - num2Int, base);
  }
	 
} 
