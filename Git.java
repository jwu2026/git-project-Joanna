import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Git {
    public static void main(String[] args) throws IOException {
        newRepo();

        // tester
        File f = new File("computer.txt");
        try (FileWriter w = new FileWriter(f)) {
            w.write("computer");
        }
        System.out.println(generateHash("computer.txt"));
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

    public static String generateHash(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hb = digest.digest(bytes);

            StringBuilder builder = new StringBuilder();
            for (byte b : hb) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            System.err.println("Error in generating hash :(");
            return null;
        }
    }
}
