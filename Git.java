import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) throws IOException {
        newRepo();
    }

    public static void newRepo() throws IOException {
        File git = new File("git");
        File objects = new File("git/objects");
        File index = new File("git/index");
        File HEAD = new File("git/HEAD");

        if (git.exists() && objects.exists() && index.exists() && HEAD.exists()) {
            System.out.println("Git Repository exists");
        } else {
            git.mkdir();
            objects.mkdir();
            index.createNewFile();
            HEAD.createNewFile();
            System.out.println("Git Repository created");
        }
    }
}
