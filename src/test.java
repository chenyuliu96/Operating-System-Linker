
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		fun(4);
	}
	static void fun(int x){
		if(x==0) {
			return;
		}
		System.out.println(x);
		fun(x-1);
		System.out.println(x);
		fun(x-1);
		System.out.println(x);
	}

}
