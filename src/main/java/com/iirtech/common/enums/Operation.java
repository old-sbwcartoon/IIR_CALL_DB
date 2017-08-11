package com.iirtech.common.enums;

//연산과 관련한 enum들 예시,이 부분을 나중에 파싱하는 연산 enum들로 교체하면 좋을듯 
public enum Operation {
	

	PLUS("+") {
        double apply(double x, double y) {
             return x + y;
        }
   },
   MINUS("-") {
        double apply(double x, double y) {
             return x - y;
        }
   },
   TIMES("*") {
        double apply(double x, double y) {
             return x * y;
        }
   },
   DIVIDE("/") {
        double apply(double x, double y) {
             return x / y;
        }
   };
   private final String symbol;

   Operation(String symbol) {
        this.symbol = symbol;
   }

   @Override
   public String toString() {
        return symbol;
   }

   abstract double apply(double x, double y);

//   public static void main(String[] args) {
//        double x = Double.parseDouble("1");
//        double y = Double.parseDouble("2");
//        for (Operation op : Operation.values())
//             System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
//   }
}
