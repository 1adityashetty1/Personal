public class CS70casino {
    public CS70casino() {
    }

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("E(x) = " + Double.toString(new CS70casino().Ex()));
    }

    public double Ex() {
        double prev = 1.0;
        double curr;
        double exp = 0;
        for (double i = 0; i < 13; i++) {
            curr = (52.0 - (4 * i)) / (52.0 - i);
            int k = (int) i;
            System.out.println("Round " + (Integer.toString(k + 1)));
            System.out.println(curr);
            double roundprob = prev * curr;
            exp += (roundprob);
            System.out.println(roundprob);
            System.out.println(exp);
            prev = roundprob;
            System.out.println("");
        }
        return exp;

    }
}