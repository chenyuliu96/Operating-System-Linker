import java.util.*;
public class D {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ArrayList<Loan> LoanList = new ArrayList<Loan>();
		ArrayList<String> result = new ArrayList<String>();
		boolean flag = true;
		while(flag) {
			String info = sc.nextLine();
			String[] infoarr = info.split(" ");
			int month = Integer.parseInt(infoarr[0]);
			if(month<0) {
				break;
			}
			Loan Aloan = new Loan();
			double downpayment = Double.parseDouble(infoarr[1]);
			double loan = Double.parseDouble(infoarr[2]);
			double carVal = loan + downpayment;
			int depR = Integer.parseInt(infoarr[3]);
			Aloan.duration = month;
			Aloan.currentCarVal = carVal;
			Aloan.borrowerOwn = loan;
			Aloan.monthlyPay = loan/month;
			Aloan.depTime = depR;
			Aloan.depArr = new double[month+1];
			int counter =0;
			for(int i=0;i<Aloan.depTime; i++) {
				String x = sc.nextLine();
				String[] x1 = x.split(" ");
				int whichmonth = Integer.parseInt(x1[0]);
				double deprate = Double.parseDouble("0"+x1[1]);
				Aloan.depArr[whichmonth] = deprate;
								
			}
			LoanList.add(Aloan);
			for(int i=0;i<Aloan.depArr.length;i++) {
				if(Aloan.depArr[i] ==0) {
					Aloan.depArr[i] = Aloan.depArr[i-1];
				}
				
			}
		}
		for(Loan x :LoanList ) {
			int currentMonth = 1;
			
			x.updateCurrentCarVal(x.depArr[0]);
			if(x.currentCarVal > x.borrowerOwn) {
				result.add("0 months");
				continue;
			}
			for(int i=currentMonth;i < x.depArr.length;i++) {
				x.updateBorrowerOwn();
				x.updateCurrentCarVal(x.depArr[i]);
				if(x.currentCarVal > x.borrowerOwn) {
					if(i==1) {
						result.add("1 month");
					}
					else {
						result.add(i+" months");
					}
					break;
				}
			}
		}
		for(String x: result) {
			System.out.println(x);
		}
	}
	
	}
class Loan{
	int duration;
	double currentCarVal;
	double borrowerOwn;
	double monthlyPay;
	double downPayment;
	int depTime;
	double depArr[];
	
	public void updateBorrowerOwn() {
		this.borrowerOwn = this.borrowerOwn - this.monthlyPay;
	}
	public void updateCurrentCarVal(double dep) {
		this.currentCarVal = this.currentCarVal * (1-dep);
	}
	public String toString() {
		return "this duration is "+ this.duration;
	}

}