import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class TestMoney {
    public static void main(String[] args) {
        Data mydata = tryLoadingMyData();
        if (mydata == null) {
            mydata = new Data();
        }
        if(args.length < 1){
            System.out.println("Please enter a valid command. Type 'help' for a list of valid commands");
            return;
        }
        switch (args[0]) {
            case "check":
                int k = 0;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                HashSet<Entry> markedfordeath = new HashSet();
                while (mydata.getBydate().size()>k){
                mydata.getBydate().get(k).print();
                System.out.println("Would you like to (c)ontinue, (d)elete,(e)xit, (o)r change this entry?");
                try {
                    String alpha = br.readLine();
                    switch (alpha) {
                        case "c":
                            k += 1;
                            break;
                        case "d":
                            markedfordeath.add(mydata.getBydate().get(k));
                            k += 1;
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
                            k += 1;
                            break;
                        default:
                            System.out.println("Enter c,o,d,e");
                            break;
                    }
                } catch (IOException e){
                    System.out.println("Exception");
                }

            }
             System.out.println("There are no more entries");
            for (Entry e : markedfordeath) {
                mydata.getByamount().remove(e);
            }
            mydata.update();
                break;
            case "greatest":
                mydata.getByamount().first().print();
                break;
            case "oldest":
                mydata.getBydate().peek().print();
                break;
            case "greatest-d":
                mydata.removeGreatest();
                mydata.update();
                break;
            case "oldest-d":
                mydata.removeOldest();
                mydata.update();
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
                byamount.add(e);
            }
        }
        for (Entry s : bydate) {
            if (!byamount.contains(s)) {
                byamount.remove(s);
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
        if (cost <= 0.0) {
            payers.remove(payer);
        } else {
            payers.put(payer, cost);
        }
        amount -= (orig - cost);
    }

    public int compareTo(Object o) {
        if (this.amount == ((Entry) o).amount)
            return 0;
        else if ((this.amount) < ((Entry) o).amount)
            return 1;
        else
            return -1;
    }

    public void print() {
        System.out.println("====");
        System.out.println(this.date);
        System.out.print("   " + this.payee);
        System.out.print("   " + this.amount);
        System.out.print("  " + this.item);
        System.out.println(this.payers);
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
