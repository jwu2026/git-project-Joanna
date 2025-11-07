import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.zip.Deflater;

public class Git {

    static boolean compress = false;

    public static void main(String[] args) throws IOException {
        newRepo();

        // tester for hash
        File f = new File("computer.txt");
        try (FileWriter w = new FileWriter(f)) {
            w.write("computer");
        }
        System.out.println(generateHash("computer.txt"));

        // tester for blob
        createBlob("computer.txt");
        System.out.println(verifyBlob(generateHash("computer.txt")));
        cleanupBlob();
        System.out.println(verifyBlob(generateHash("computer.txt")));
        createBlob("computer.txt");
        System.out.println(verifyBlob(generateHash("computer.txt")));
        cleanupBlob();

        // tester for streeeeetch blob
        compress = false;
        createBlob("computer.txt");
        System.out.println(verifyBlob(generateHash("computer.txt")));
        cleanupBlob();
        System.out.println(verifyBlob(generateHash("computer.txt")));

        compress = true;
        createBlob("computer.txt");
        System.out.println(verifyBlob(generateHash("computer.txt")));
        cleanupBlob();
        System.out.println(verifyBlob(generateHash("computer.txt")));
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
            if (compress) {
                bytes = compression(path);
                if (bytes == null) {
                    return null;
                }
            }
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

    public static void createBlob(String path) throws IOException {
        try {
            String hash = generateHash(path);
            if (hash == null) {
                return;
            }
            File blob = new File("git/objects/" + hash);
            if (blob.exists()) {
                return;
            }
            byte[] b = Files.readAllBytes(Paths.get(path));
            if (compress) {
                b = compression(path);
                if (b == null) {
                    return;
                }
            }
            Files.write(blob.toPath(), b);
        } catch (Exception e) {
            System.err.println("Error in creating blob");
        }
    }

    public static boolean verifyBlob(String name) {
        File objects = new File("git/objects");
        if (!objects.exists()) {
            return false;
        }
        File[] files = objects.listFiles();
        if (files == null) {
            return false;
        }
        boolean temp = false;
        for (File file : files) {
            if (file.getName().equals(name)) {
                temp = true;
                break;
            }
        }
        return temp;
    }

    public static void cleanupBlob() {
        File objects = new File("git/objects");
        if (!objects.exists()) {
            return;
        }
        File[] blobs = objects.listFiles();
        if (blobs != null) {
            for (File blob : blobs) {
                if (blob.isFile()) {
                    blob.delete();
                }
            }
        }
    }

    public static byte[] compression(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            Deflater d = new Deflater();
            d.setInput(bytes);
            d.finish();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int l;
            while (!d.finished()) {
                l = d.deflate(buffer);
                if (l > 0) {
                    output.write(buffer, 0, l);
                }
            }
            d.end();
            return output.toByteArray();

        } catch (Exception e) {
            System.out.println("Error with compressing");
            return null;
        }
    }

}
