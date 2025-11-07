import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class gitTesting {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            createFiles();
            try {
                Git.newRepo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (verify()) {
                System.out.println("Repository was initialized");
            }
            deleteGit();
        }
    }

    public static void createFiles() {
        try (FileWriter w1 = new FileWriter("first.txt"); FileWriter w2 = new FileWriter("sec.txt")) {
            w1.write("hi");
            w2.write("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verify() {
        File git = new File("git");
        File objects = new File("git/objects");
        File index = new File("git/index");
        File head = new File("git/HEAD");
        return git.exists() && objects.exists() && index.exists() && head.exists();
    }

    public static void deleteGit() {
        File[] files = {
                new File("first.txt"),
                new File("sec.txt"),
                new File("git/HEAD"),
                new File("git/index"),
                new File("git/objects"),
                new File("git")
        };
        for (File f : files) {
            f.delete();
        }
    }
}
