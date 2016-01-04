import java.io.*;


import java.text.SimpleDateFormat;
import java.util.*;

public class Gitlet {


    public static void main(String[] args) {
        HashSet<String> additions = tryLoadingMyadditions();
        HashSet<String> removed = tryLoadingMyRemoved();
        CommitTree tree = tryLoadingMyTree();
        Commit last = tryLoadingMyCommit();

        switch (args[0]) {
            case "init":
                init(".gitlet/");
                if (last == null) {
                    additions = new HashSet<String>();
                    removed = new HashSet<String>();
                    last = commit(null, "initial commit", additions, removed, -1);
                    tree = new CommitTree(last, "master");
                    saveMyTree(tree);
                    saveMyCommit(last);
                    saveMyAdditions(additions);
                    saveMyRemoved(removed);
                }
                break;
            case "add":
                if (tree == null) {

                    break;
                }
                if (args.length == 2) {
                    add(args[1], removed, additions, last);
                }
                saveMyAdditions(additions);
                saveMyRemoved(removed);
                break;
            case "commit":
                if (tree == null) {

                    break;
                }
                if (args.length == 2) {
                    if ((!additions.isEmpty()) || (!removed.isEmpty())) {

                        last = commit(last, args[1], removed, additions, tree.getEvermade());
                        additions.clear();
                        removed.clear();
                        tree.getIdmap().put(last.getId(), last);
                        tree.setEvermade(tree.getEvermade() + 1);
                        tree.getBranches().put(tree.getCurrbranch(), last);
                    } else {
                        System.out.println("No changes added to the commit.");
                    }

                } else {
                    System.out.println("Please enter a commit message");
                }
                saveMyTree(tree);
                saveMyCommit(last);
                saveMyAdditions(additions);
                saveMyRemoved(removed);
                break;
            case "remove":
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    remove(args[1], additions, removed, last);
                } else {
                    System.out.println("Input Needed");
                }
                saveMyCommit(last);
                saveMyAdditions(additions);
                saveMyRemoved(removed);
                break;

            case "log":
                if (tree == null) {
                    break;
                }
                log(last);

                break;
            case "global-log":
                if (tree == null) {

                    break;
                }
                Glog(tree);
                saveMyTree(tree);
                break;
            case "find":
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    getbymsg(args[1], tree);
                }
                saveMyTree(tree);
                break;
            case "status":
                if (tree == null) {

                    break;
                }
                Status(tree, additions, removed);
                saveMyTree(tree);

                saveMyAdditions(additions);
                saveMyRemoved(removed);
                break;
            case "checkout":
                if (tree == null) {

                    break;
                }
                if (!areyousure()) {
                    break;
                }
                if (args.length >= 2) {
                    if (args.length == 3) {
                        checkout2(Integer.parseInt(args[1]), args[2], tree);
                        saveMyTree(tree);
                        saveMyCommit(last);
                        break;
                    }
                    if (tree.getBranches().containsKey(args[1])) {
                        checkout3(args[1], tree);
                        saveMyTree(tree);
                        saveMyCommit(last);
                        break;
                    }
                    checkout1(args[1], last);
                    saveMyTree(tree);
                    saveMyCommit(last);
                    break;
                }
                System.out.println("Input Needed");
                break;
            case "branch":
                if (tree == null) {

                    break;
                }
                if (args.length == 2) {
                    branch(args[1], last, tree);
                } else {
                    System.out.println("Input Needed");
                }
                saveMyTree(tree);
                saveMyCommit(last);
                break;
            case "rm-branch":
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    rbranch(args[1], tree);
                } else {
                    System.out.println("Input Needed");
                }
                saveMyTree(tree);
                break;
            case "reset":
                if (!areyousure()) {
                    break;
                }
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    reset(Integer.parseInt(args[1]), tree);
                }
                saveMyTree(tree);
                break;
            case "merge":
                if (!areyousure()) {
                    break;
                }
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    merge(args[1], tree.getCurrbranch(), tree);
                }
                saveMyTree(tree);
                break;
            case "rebase":
                if (!areyousure()) {
                    break;
                }
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    rebase(args[1], tree.getCurrbranch(), tree);
                }
                saveMyTree(tree);
                saveMyCommit(last);
                break;
            case "i-rebase":
                if (!areyousure()) {
                    break;
                }
                if (tree == null) {

                    break;
                }
                if (args[1] != null) {
                    irebase(args[1], tree.getCurrbranch(), tree);
                }
                saveMyTree(tree);
                saveMyCommit(last);
                break;


        }
    }


    public static void init(String p) {
        File file = new File(p);
        if (!file.exists()) {
            if (file.mkdir()) {

                return;
            } else {

                return;
            }

        }
        if (p.equals(".gitlet/")) {
            System.out.println("A gitlet version control system already exists in current directory");
            return;
        }

    }

    public static void Copy(String source, String destination) {


        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            File afile = new File(source);
            File bfile = new File(destination);

            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(bfile);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {

                outStream.write(buffer, 0, length);

            }

            inStream.close();
            outStream.close();
            return;


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean comparebyByte(String file1, String file2) throws IOException {

        File f1 = new File(file1);
        File f2 = new File(file2);
        FileInputStream fis1 = new FileInputStream(f1);
        FileInputStream fis2 = new FileInputStream(f2);
        if (f1.length() == f2.length()) {
            int n = 0;
            byte[] b1;
            byte[] b2;
            while ((n = fis1.available()) > 0) {
                if (n > 80) n = 80;
                b1 = new byte[n];
                b2 = new byte[n];
                int res1 = fis1.read(b1);
                int res2 = fis2.read(b2);
                if (Arrays.equals(b1, b2) == false) {
                      /*taken from java online guiide*/
                    return false;
                }
            }
        } else return false; // length is not matched.
        return true;
    }

    public static boolean compare(String file1, String file2) {
        try {
            if (comparebyByte(file1, file2) == true) {
                return true;
            }


        } catch (IOException e) {
            System.out.println("Error");
        }
        return false;
    }


    public static void add(String file, HashSet<String> removed, HashSet<String> additions, Commit last) {
        File filename = new File(file);
        if (!filename.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (removed.contains(file)) {
            removed.remove(file);
            return;
        }
        if (last.getVersion().keySet().contains(file)) {
            if (compare(file, ".gitlet/" + Integer.toString(last.getId()) + "/" + file)) {

                return;
            }
            additions.add(file);

        }

        additions.add(file);
    }

    public static Commit commit(Commit Last, String msg, HashSet<String> removed, HashSet<String> additions, int number) {

        Commit temp = new Commit(msg, Last, additions, removed, number);

        init(".gitlet/" + Integer.toString(temp.getId()));
        for (String s : temp.getVersion().keySet()) {

            if (temp.getVersion().get(s) == temp.getId()) {
                if (s.contains("/")) {
                    init(".gitlet/" + Integer.toString(temp.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                }
                Copy(s, ".gitlet/" + Integer.toString(temp.getId()) + "/" + s);
            } else {
                if (s.contains("/")) {
                    init(".gitlet/" + Integer.toString(temp.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                }

                Copy(".gitlet/" + Integer.toString(temp.getVersion().get(s)) + "/" + s, ".gitlet/" + Integer.toString(temp.getId()) + "/" + s);
            }

        }
        return temp;
        //create a commit.ser file
        // if a new branch is being instantiated, create a new branch.


    }

    public static void remove(String file, HashSet<String> additions, HashSet<String> removed, Commit Last) {
        int test = 0;
        if (additions.contains(file)) {
            additions.remove(file);
            test++;
            return;
        }
        if (Last.getVersion().containsKey(file)) {
            removed.add(file);
            test++;
            return;
        }
        if (test == 0) {
            System.out.println("No reason to remove the file.");
        }

    }

    public static void log(Commit last) {
        Commit c = last;
        while (c.getParent() != null) {
            System.out.println("====");
            System.out.println("Commit " + Integer.toString(c.getId()) + ".");
            System.out.println(c.getTime());
            System.out.println(c.getMessage());
            System.out.println("");
            c = c.getParent();
        }
        System.out.println("====");
        System.out.println("Commit " + Integer.toString(c.getId()) + ".");
        System.out.println(c.getTime());
        System.out.println(c.getMessage());
        System.out.println("");
    }

    private static void rlog(Commit c) {
        System.out.println("====");
        System.out.println("Commit " + Integer.toString(c.getId()) + ".");
        System.out.println(c.getTime());
        System.out.println(c.getMessage());
        System.out.println("");
    }

    public static void Glog(CommitTree tree) {
        for (int i : tree.getIdmap().keySet()) {
            Gloghelper(tree.getIdmap().get(i));
        }
    }

    private static void Gloghelper(Commit h) {

        System.out.println("====");
        System.out.println("Commit " + Integer.toString(h.getId()));
        System.out.println(h.getTime());
        System.out.println(h.getMessage());
        System.out.println("");
    }

    public static void getbymsg(String message, CommitTree tree) {
        HashSet<Integer> temp = new HashSet<>();
        for (int id : tree.getIdmap().keySet()) {
            if (message.equals(tree.getIdmap().get(id).getMessage())) {
                temp.add(id);
            }
        }
        System.out.println(temp);
    }

    public static void Status(CommitTree tree, HashSet<String> additions, HashSet<String> removed) {

        System.out.println("=== Branches ===");
        for (String s : tree.getBranches().keySet()) {
            if (tree.getCurrbranch().equals(s)) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
            System.out.println("");
        }
        System.out.println("=== Staged Files ===");
        for (String s : additions) {
            System.out.println(s);
        }
        System.out.println("");
        System.out.println("=== Files Marked for Removal ===");
        for (String s : removed) {
            System.out.println(s);
        }
        System.out.println("");
    }


    private static Commit Statushelper(Commit Last, CommitTree tree) {
        Commit d = Last;
        System.out.println(d.getParent());
        System.out.println(tree.getBranches());

        while (!(tree.getBranches().containsValue(d))) {
            d = d.getParent();
        }
        return d;
    }

    public static void checkout1(String file, Commit branchhead) {
        //checks for branch head deletes file in working directory/copies from specified branch directory
        if (branchhead.getVersion().containsKey(file)) {

            Copy(".gitlet/" + Integer.toString(branchhead.getId()) + "/" + file, file);
            return;
        }
        System.out.println("File does not exist in the most recent commit, or no such branch exists.");


    }

    public static void checkout2(int id, String file, CommitTree tree) {
        //checks idmap, if id exists, deletes file in working directory, copies from specified id directory

        if (tree.getIdmap().containsKey(id)) {
            if (tree.getIdmap().get(id).getVersion().containsKey(file)) {
                Copy(".gitlet/" + Integer.toString(id) + "/" + file, file);
                return;
            }
            System.out.println("File does not exist in that commit.");
            return;
        }
        System.out.println("No commit with that id exists.");
    }

    public static void checkout3(String branch, CommitTree tree) {
        //checks branches, if branch name exists, deletes files in working directory// copies files from branch directory
        if (tree.getBranches().containsKey(branch)) {
            if (!branch.equals(tree.getCurrbranch())) {
                for (String file : tree.getBranches().get(branch).getVersion().keySet()) {
                    checkout1(file, tree.getBranches().get(branch));
                }
                tree.setCurrbranch(branch);
                return;
            }
            System.out.println("No need to checkout the current branch.");
            return;
        }
        System.out.println("File does not exist in the most recent commit, or no such branch exists.");

    }

    public static void branch(String branch, Commit Head, CommitTree tree) {
        if (tree.getBranches().containsKey(branch)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        tree.getBranches().put(branch, Head);
        Head.getStart().add(branch);
    }

    public static void rbranch(String branch, CommitTree tree) {
        if (branch.equals(tree.getCurrbranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (tree.getBranches().containsKey(branch)) {
            tree.getBranches().remove(branch);
            for (int c : tree.getIdmap().keySet()) {
                if (tree.getIdmap().get(c).getStart().contains(branch)) {
                    tree.getIdmap().get(c).getStart().remove(branch);
                }
            }
            return;
        }
        System.out.println("A branch with that name does not exist.");
    }

    public static void reset(Integer id, CommitTree tree) {
        if (tree.getIdmap().containsKey(id)) {
            for (String file : tree.getIdmap().get(id).getVersion().keySet()) {
                checkout2(id, file, tree);
            }
            tree.getBranches().put(tree.getCurrbranch(), tree.getIdmap().get(id));
            return;
        }
        System.out.println("No commit with that id exists.");
    }

    public static void merge(String given, String current, CommitTree tree) {
        if (given.equals(current)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (!tree.getBranches().containsKey(given)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Commit a = tree.getBranches().get(given);
        Commit b = tree.getBranches().get(current);
        Commit sample = findcommonancestor(a, b, given, current, tree);
        for (String s : a.getVersion().keySet()) {

            if (sample.getVersion().get(s) == (b.getVersion().get(s)) && (!sample.getVersion().get(s).equals(a.getVersion().get(s)))) {
                if (s.contains("/")) {
                    init(".gitlet/" + Integer.toString(b.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                }
                Copy(".gitlet/" + Integer.toString(a.getId()) + "/" + s, ".gitlet/" + Integer.toString(b.getId()) + "/" + s);
                Copy(".gitlet/" + Integer.toString(a.getId()) + "/" + s, s);
            }
            if (!(sample.getVersion().get(s).equals(b.getVersion().get(s))) && (!sample.getVersion().get(s).equals(b.getVersion().get(s)))) {
                Copy(".gitlet/" + Integer.toString(b.getId()) + "/" + s, ".gitlet/" + Integer.toString(b.getId()) + "/" + s);
            }

        }
    }


    private static Commit findcommonancestor(Commit a, Commit b, String given, String current, CommitTree tree) {
        Commit c = a;

        if (c.equals(b)) {
            return c;
        }
        while (c.getParent() != null) {
            if (c.getParent().getStart().contains(given) || c.getParent().getStart().contains(current)) {
                return c;


            }

        }
        return c;
    }

    public static void rebase(String given, String current, CommitTree tree) {
        ArrayList<Commit> transfer = new ArrayList<Commit>();
        Commit A = tree.getBranches().get(given);
        Commit B = tree.getBranches().get(current);
        Commit ancestor = findcommonancestor(A, B, given, current, tree);
        if (ancestor.equals(A)) {
            tree.getBranches().put(tree.getCurrbranch(), A);
            return;
        }
        Commit C = B;

        if (!tree.getBranches().containsValue(A) || !tree.getBranches().containsValue(B)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (B.equals(tree.getBranches().get(A))) {
            System.out.println("Cannot rebase a branch onto itself.");
            return;
        }
        while (C != ancestor) {
            C = new Commit(C);
            transfer.add(C);
            C = C.getParent();
        }

        for (int i = 0; i < transfer.size(); i++) {

            Commit d = transfer.get(i);
            init(".gitlet/" + Integer.toString(d.getId()));
            d.setParent(transfer.get(i + 1));
            tree.getIdmap().put(d.getId(), d);
            for (String s : d.getVersion().keySet()) {
                if (s.contains("/")) {
                    init(".gitlet/" + Integer.toString(d.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                }
                Copy(".gitlet/" + Integer.toString(d.getVersion().get(s)) + "/" + s, ".gitlet/" + Integer.toString(d.getId()) + "/" + s);

            }
            for (String s : A.getVersion().keySet()) {


                if (!A.getVersion().get(s).equals(ancestor.getVersion().get(s))) {
                    if (s.contains("/")) {
                        init(".gitlet/" + Integer.toString(d.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                    }
                    Copy(".gitlet/" + Integer.toString(A.getId()) + "/" + s, ".gitlet/" + Integer.toString(d.getId()) + "/" + s);
                    d.getVersion().put(s, A.getVersion().get(s));

                }
            }

            transfer.get(transfer.size() - 1).setParent(A);
            tree.getBranches().put(tree.getCurrbranch(), transfer.get(0));
        }
        checkout3(current, tree);

    }

    public static void irebase(String given, String current, CommitTree tree) {
        ArrayList<Commit> transfer = new ArrayList<Commit>();
        Commit A = tree.getBranches().get(given);
        Commit B = tree.getBranches().get(current);
        Commit ancestor = findcommonancestor(A, B, given, current, tree);
        Commit C = B;
        if (ancestor.equals(A)) {
            tree.getBranches().put(tree.getCurrbranch(), A);
            return;
        }
        if (!tree.getBranches().containsValue(A) || !tree.getBranches().containsValue(B)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (B.equals(tree.getBranches().get(A))) {
            System.out.println("Cannot rebase a branch onto itself.");
            return;
        }
        while (C != ancestor) {

            C = new Commit(C);
            System.out.print("Would you like to (c)ontinue, (s)kip this commit, or change this commit's (m)essage?");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String c = br.readLine();

                switch (c) {
                    case "c":
                        rlog(C);
                        transfer.add(C);
                        C = C.getParent();
                        break;

                    case "s":
                        transfer.add(C);
                        C = C.getParent();
                        break;
                    case "m":
                        System.out.println("Please enter a new message for this commit.");
                        BufferedReader tr = new BufferedReader(new InputStreamReader(System.in));
                        C.setMessage(tr.readLine());
                        C = C.getParent();
                        tr.close();
                    default:
                        System.out.println("enter input");
                }
            } catch (IOException e) {

            }


        }


        for (int i = 0; i < transfer.size(); i++) {

            Commit d = transfer.get(i);
            init(".gitlet/" + Integer.toString(d.getId()));
            d.setParent(transfer.get(i + 1));
            tree.getIdmap().put(d.getId(), d);
            for (String s : d.getVersion().keySet()) {
                if (s.contains("/")) {
                    init(".gitlet/" + Integer.toString(d.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                }
                Copy(".gitlet/" + Integer.toString(d.getVersion().get(s)) + "/" + s, ".gitlet/" + Integer.toString(d.getId()) + "/" + s);

            }
            for (String s : A.getVersion().keySet()) {


                if (!A.getVersion().get(s).equals(ancestor.getVersion().get(s))) {
                    if (s.contains("/")) {
                        init(".gitlet/" + Integer.toString(d.getId()) + "/" + s.substring(0, s.lastIndexOf("/")));

                    }
                    Copy(".gitlet/" + Integer.toString(A.getId()) + "/" + s, ".gitlet/" + Integer.toString(d.getId()) + "/" + s);
                    d.getVersion().put(s, A.getVersion().get(s));

                }
            }

            transfer.get(transfer.size() - 1).setParent(A);
            tree.getBranches().put(tree.getCurrbranch(), transfer.get(0));
        }
        checkout3(current, tree);

    }


    private static Commit tryLoadingMyCommit() {
        Commit last = null;
        File lastFile = new File("last.ser");
        if (lastFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(lastFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                last = (Commit) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading Last.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading Last.";
                System.out.println(msg);
            }
        }
        return last;
    }

    private static boolean areyousure() {
        System.out.println("Warning: The command you entered may alter the files in your working directory. Uncommitted changes may be lost. Are you sure you want to continue? (yes/no)");


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String answer = null;

        try {
            answer = br.readLine();
            switch (answer) {
                case "yes":
                    return true;

                case "no":
                    return false;
            }
        } catch (IOException ioe) {

            System.exit(1);
        }

        return false;
    }


    private static void saveMyCommit(Commit last) {
        if (last == null) {
            return;
        }
        try {
            File lastFile = new File("last.ser");
            FileOutputStream fileOut = new FileOutputStream(lastFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(last);
        } catch (IOException e) {
            String msg = "IOException while saving myCat.";
            System.out.println(msg);
        }
    }

    private static CommitTree tryLoadingMyTree() {
        CommitTree tree = null;
        File treeFile = new File("tree.ser");
        if (treeFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(treeFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                tree = (CommitTree) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading Tree.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading Tree.";
                System.out.println(msg);
            }
        }
        return tree;
    }

    private static void saveMyTree(CommitTree tree) {
        if (tree == null) {
            return;
        }
        try {
            File lastFile = new File("tree.ser");
            FileOutputStream fileOut = new FileOutputStream(lastFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(tree);
        } catch (IOException e) {
            String msg = "IOException while saving tree.";
            System.out.println(msg);
        }
    }

    private static HashSet<String> tryLoadingMyadditions() {
        HashSet<String> additions = null;
        File lastFile = new File("additions.ser");
        if (lastFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(lastFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                additions = (HashSet<String>) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading additions.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading additions.";
                System.out.println(msg);
            }
        }
        return additions;
    }

    private static void saveMyAdditions(HashSet<String> additions) {
        if (additions == null) {
            return;
        }
        try {
            File lastFile = new File("additions.ser");
            FileOutputStream fileOut = new FileOutputStream(lastFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(additions);
        } catch (IOException e) {
            String msg = "IOException while saving additions.";
            System.out.println(msg);
        }
    }

    private static HashSet<String> tryLoadingMyRemoved() {
        HashSet<String> last = null;
        File lastFile = new File("removed.ser");
        if (lastFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(lastFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                last = (HashSet<String>) objectIn.readObject();
            } catch (IOException e) {
                String msg = "IOException while loading additions.";
                System.out.println(msg);
            } catch (ClassNotFoundException e) {
                String msg = "ClassNotFoundException while loading additions.";
                System.out.println(msg);
            }
        }
        return last;
    }

    private static void saveMyRemoved(HashSet<String> removed) {
        if (removed == null) {
            return;
        }
        try {
            File lastFile = new File("removed.ser");
            FileOutputStream fileOut = new FileOutputStream(lastFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(removed);
        } catch (IOException e) {
            String msg = "IOException while saving additions.";
            System.out.println(msg);
        }
    }
}


class Commit implements Serializable {
    public Commit(String msg, Commit Node, HashSet<String> files, HashSet<String> remove, int evermade) {

        this.message = msg;
        id = evermade + 1;
        version = new HashMap<>();

        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()); //taken from StackOverflow
        if (Node == null) {
            parent = null;
            for (String s : files) {
                version.put(s, id);
            }
            removed = new HashSet<>();
            start = new HashSet<>();
            start.add("master");
            return;
        }
        start = new HashSet<>();
        setParent(Node);
        Set<String> copyset = Node.getVersion().keySet();
        for (String s : copyset) {
            if (files.contains(s)) { //adds files from old set that have been updated
                version.put(s, this.id);
                files.remove(s);
                break;
            }
            if (!remove.contains(s)) { //adds files that have not been updated
                version.put(s, Node.getVersion().get(s));
            }

        }
        for (String file : files) { //adds new files
            version.put(file, this.id);
        }


        this.removed = remove;

    }

    public Commit(Commit C) {
        this.start = C.getStart();
        this.id = C.getId() * 101;
        this.version = C.getVersion();
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        this.removed = C.getRemoved();

    }

    public void setParent(Commit parent) {
        this.parent = parent;

    }


    public Commit getParent() {
        return parent;
    }


    public HashSet<String> getRemoved() {
        return removed;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private Commit parent;


    public HashSet<String> getStart() {
        return start;
    }

    public void setStart(HashSet<String> start) {
        this.start = start;
    }

    private HashSet<String> start; //records branches that start at this commit
    private HashSet<String> removed;
    private HashMap<String, Integer> version;
    private String time;

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public HashMap<String, Integer> getVersion() {
        return version;
    }
}


class CommitTree implements Serializable {
    public HashMap<String, Commit> getBranches() {
        return branches;
    }

    private HashMap<String, Commit> branches;

    public HashMap<Integer, Commit> getIdmap() {
        return idmap;
    }

    private HashMap<Integer, Commit> idmap;

    public void setCurrbranch(String currbranch) {
        this.currbranch = currbranch;
    }

    private String currbranch;

    public int getEvermade() {
        return evermade;
    }

    private int evermade;


    public String getCurrbranch() {
        return currbranch;
    }

    public CommitTree(Commit first, String master) {
        branches = new HashMap<String, Commit>();
        getBranches().put(master, first);
        evermade = 0;
        idmap = new HashMap<>();
        idmap.put(evermade, first);
        currbranch = "master";
    }

    public void setEvermade(int evermade) {
        this.evermade = evermade;
    }
}
