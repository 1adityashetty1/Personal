import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestMoney {
    public static void main(String[] args) {
        Data mydata = tryLoadingMyData();
        if (mydata == null) {
            mydata = new Data();
        }
        if (args.length < 1) {
            System.out.println("Please enter a valid command. Type 'help' for a list of valid commands");
            return;
        }
        switch (args[0]) {
            case "add":
                System.out.println("Please enter a payment in the following format: payee,item");
                BufferedReader ar = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String entry = ar.readLine();
                    String[] pi = entry.split(",");
                    if (pi.length != 2) {
                        System.out.println("Invalid Input");
                        return;
                    }
                    System.out.println("Please enter amount and payers in the following format: 0.00,payer1,payer2,etc.");
                    String[] ap = ar.readLine().split(",");
                    if (ap.length < 2) {
                        System.out.println("Invalid Input");
                        return;
                    }
                    HashSet<String> payees = new HashSet();
                    for (int i = 1; i < ap.length; i++) {
                        payees.add(ap[i]);
                    }
                    mydata.addEntry(pi[0], pi[1], payees, Double.parseDouble(ap[0]));
                    break;
                } catch (IOException e) {
                    System.out.println("IOExeption");
                    break;
                }
            case "check":
                int k = 0;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                HashSet<Entry> markedfordeath = new HashSet();
                while (mydata.getBydate().size() > k) {
                    System.out.println(" ");
                    mydata.getBydate().get(k).print();
                    System.out.println(" ");
                    System.out.println("Would you like to (c)ontinue, (d)elete,(e)xit, (o)r change this entry?");
                    try {
                        String alpha = br.readLine();
                        switch (alpha) {
                            case "c":
                                k += 1;
                                break;
                            case "d":
                                markedfordeath.add(mydata.getBydate().get(k));
                                mydata.getBydate().remove(k);
                                k = k;
                                break;
                            case "e":
                                k = mydata.getBydate().size();
                                break;
                            case "o":
                                boolean cont = false;
                                while (!cont) {
                                    System.out.println("Please enter a payer");
                                    BufferedReader tr = new BufferedReader(new InputStreamReader(System.in));
                                    String beta = tr.readLine();
                                    System.out.println("Please enter an amount");
                                    Double gamma = Double.parseDouble(tr.readLine());
                                    mydata.reMap(mydata.getBydate().get(k), beta, gamma);
                                    System.out.println("Would you like to continue? [y] [n]");
                                    String delta = tr.readLine();
                                    switch (delta) {
                                        case "y":
                                            cont = true;
                                            break;
                                        case "n":
                                            break;
                                        default:
                                            System.out.println("Enter y or n");
                                            break;
                                    }
                                }
                                if (mydata.getBydate().get(k).getPayers().size() < 1) {
                                    mydata.getBydate().remove(k);
                                    k = k;
                                } else {
                                    k += 1;
                                }
                                break;
                            default:
                                System.out.println(" ");
                                System.out.println("Enter c,o,d,e");
                                break;
                        }
                    } catch (IOException e) {
                        System.out.println("Exception");
                    }

                }
                System.out.println(" ");
                System.out.println("There are no more entries");
                for (Entry e : markedfordeath) {
                    mydata.getByamount().remove(e);
                }
                break;
            case "greatest":
                if (mydata.getByamount().size() < 1) {
                    System.out.println("There are no entries");
                    return;
                }
                mydata.getByamount().first().print();
                break;
            case "oldest":
                if (mydata.getBydate().size() < 1) {
                    System.out.println("There are no entries");
                    return;
                }
                mydata.getBydate().peek().print();
                break;
            case "greatest-d":
                if (mydata.getByamount().size() < 1) {
                    System.out.println("There are no entries");
                    return;
                }
                mydata.removeGreatest();
                break;
            case "oldest-d":
                if (mydata.getBydate().size() < 1) {
                    System.out.println("There are no entries");
                    return;
                }
                mydata.removeOldest();
                break;
            case "help":
                System.out.println(" ");
                System.out.println("'check' - an interactive playthrough of all entries");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("'greatest' - shows entry with largest amount");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("'greatest-d' - deletes entry with largest amount");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("'oldest' - shows entry with oldest date");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("'oldest-d' - deletes entry with oldest date");
                System.out.println(" ");
                break;
            default:
                System.out.println("Please enter a valid command. Type 'help' for a list of valid commands");
                break;
        }

        saveMyData(mydata);
    }


    private static Data tryLoadingMyData() {
        Data last = null;
        File lastFile = new File("data.ser");
        if (lastFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(lastFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                last = (Data) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading Data.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading Data.";
                System.out.println(msg);
            }
        }
        return last;
    }

    private static void saveMyData(Data last) {
        if (last == null) {
            return;
        }
        try {
            File lastFile = new File("data.ser");
            FileOutputStream fileOut = new FileOutputStream(lastFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(last);
        } catch (IOException e) {
            String msg = "IOException while saving myData.";
            System.out.println(msg);
        }
    }
}


class Data implements Serializable {
    private LinkedList<Entry> bydate;
    private TreeSet<Entry> byamount;

    public Data() {
        byamount = new TreeSet();
        bydate = new LinkedList();
    }

    public TreeSet<Entry> getByamount() {
        return byamount;
    }

    public LinkedList<Entry> getBydate() {
        return bydate;
    }

    public void update() {
        for (Entry e : byamount) {
            if (!bydate.contains(e)) {
                bydate.add(e);
            }
        }
        for (int i = 0; i < bydate.size(); i++) {
            Entry s = bydate.get(i);
            if (!((Set) byamount).contains(s)) {
                bydate.remove(s);
            }
        }
    }

    public void addEntry(String payee, String item, Collection<String> collect, Double totalcost) {
        Entry g = new Entry(payee, item);
        g.setPayers(collect, totalcost / collect.size());
        bydate.add(g);
        byamount.add(g);
    }

    public void setPayers(Entry g, Collection<String> collect, Double cost) {
        g.setPayers(collect, cost);

    }

    public void reMap(Entry g, String payer, Double cost) {
        byamount.remove(g);
        g.reMap(payer, cost);
        byamount.add(g);
    }

    public void removeOldest() {
        byamount.remove(bydate.remove());

    }

    public void removeGreatest() {
        bydate.remove(byamount.pollFirst());
    }

}

class Entry implements Serializable, Comparable {
    private String payee;
    private String item;
    private Double amount;
    private HashMap<String, Double> payers;
    private String date;

    public Entry(String payee, String item) {
        payers = new HashMap<String, Double>();
        this.payee = payee;
        this.item = item;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public HashMap<String, Double> getPayers() {
        return payers;
    }

    public void setPayers(Collection<String> collect, Double cost) {
        for (String s : collect) {
            payers.put(s, cost);
        }
        this.amount = (payers.size() * cost);
    }

    public void reMap(String payer, Double cost) {
        Double orig = payers.get(payer);
        if (orig == null) {
            orig = 0.00;
        }
        if (cost <= 0.0) {
            payers.remove(payer);
        } else {
            payers.put(payer, cost);
        }
        amount -= (orig - cost);
    }

    public int compareTo(Object o) {

        if (equals(o)) {
            return 0;
        } else if ((this.amount) < ((Entry) o).amount)
            return 1;
        else
            return -1;
    }

    public void print() {
        System.out.println("====");
        System.out.println(this.date);
        System.out.print("   " + this.payee);
        System.out.print("   " + this.amount);
        System.out.print("  " + this.item + " ");
        System.out.println(this.payers);
        System.out.println("====");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Entry entry = (Entry) object;

        if (!date.equals(entry.date)) return false;
        if (!item.equals(entry.item)) return false;
        if (!payee.equals(entry.payee)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + payee.hashCode();
        result = 31 * result + item.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}
