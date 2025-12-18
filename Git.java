import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Git {

    private static final String gitDirectory = "git";
    private static final String objDirectory = "git/objects";
    private static final String indexPath = "git/index";
    private static final String headPath = "git/HEAD";
    private static final String workinglistPath = "git/workingList";

    static boolean compress = false;

    public static void main(String[] args) throws IOException {
        newRepo();
        cleanup();

        // // tester for hash
        // File f = new File("computer.txt");
        // try (FileWriter w = new FileWriter(f)) {
        // w.write("computer");
        // }
        // System.out.println(generateHash("computer.txt"));

        // // tester for blob
        // createBlob("computer.txt");
        // System.out.println(verifyBlob(generateHash("computer.txt")));
        // cleanupBlob();
        // System.out.println(verifyBlob(generateHash("computer.txt")));
        // createBlob("computer.txt");
        // System.out.println(verifyBlob(generateHash("computer.txt")));
        // cleanupBlob();

        // // tester for streeeeetch blob
        // compress = false;
        // createBlob("computer.txt");
        // System.out.println(verifyBlob(generateHash("computer.txt")));
        // cleanupBlob();
        // System.out.println(verifyBlob(generateHash("computer.txt")));

        // compress = true;
        // createBlob("computer.txt");
        // System.out.println(verifyBlob(generateHash("computer.txt")));
        // cleanupBlob();
        // System.out.println(verifyBlob(generateHash("computer.txt")));

        // // tester for index
        // indexTester();
        // cleanup();

        // // tester for tree
        // treeTester();
        // cleanup();

        // tester for tree from index
        // treeIndexTester();

        // tester for commit
        commitTester();
    }

    public static void treeIndexTester() {
        try {
            cleanup();
            newRepo();
            Files.createDirectories(Path.of("testing/docs"));
            Files.writeString(Path.of("testing/README.md"), "readme");
            Files.writeString(Path.of("testing/docs/I.txt"), "I");
            Files.writeString(Path.of("testing/docs/Love.txt"), "Love");
            Files.writeString(Path.of("testing/Traveling.txt"), "Traveling");
            createBlob("testing/README.md");
            updateIndex("testing/README.md");
            createBlob("testing/docs/I.txt");
            updateIndex("testing/docs/I.txt");
            createBlob("testing/docs/Love.txt");
            updateIndex("testing/docs/Love.txt");
            createBlob("testing/Traveling.txt");
            updateIndex("testing/Traveling.txt");
            treeIndex();
            System.out.println(Files.readString(Path.of(workinglistPath)));
            File[] obj = new File(objDirectory).listFiles();
            for (File x : obj) {
                System.out.println("  " + x.getName());
            }
        } catch (Exception e) {
            System.out.println("treeIndex tester didn't work");
        }
    }

    public static void newRepo() throws IOException {
        File git = new File(gitDirectory);
        File objects = new File(objDirectory);
        File index = new File(indexPath);
        File HEAD = new File(headPath);

        if (git.exists() && objects.exists() && index.exists() && HEAD.exists()) {
            System.out.println("Git Repository Already Exists");
            return;
        }
        if (!git.exists()) {
            git.mkdir();
        }
        if (!objects.exists()) {
            objects.mkdir();
        }
        if (!index.exists()) {
            index.createNewFile();
        }
        if (!HEAD.exists()) {
            HEAD.createNewFile();
        }
        System.out.println("Git Repository Created");
    }

    public static String generateHash(String path) throws IOException {
        if (!Files.exists(Paths.get(path))) {
            throw new IOException("Path doesn't exist");
        }
        if (Files.isDirectory(Paths.get(path))) {
            throw new IOException("Path is a directory");
        }
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        if (compress) {
            bytes = compression(path);
            if (bytes == null) {
                return null;
            }
        }
        try {
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
            File blob = new File(objDirectory + "/" + hash);
            if (blob.exists()) {
                return;
            }
            byte[] b = Files.readAllBytes(Paths.get(path));
            Files.write(blob.toPath(), b);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error in creating blob");
        }
    }

    public static boolean verifyBlob(String name) {
        File objects = new File(objDirectory);
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
        File objects = new File(objDirectory);
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
            Deflater deflator = new Deflater();
            deflator.setInput(bytes);
            deflator.finish();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int written;
            while (!deflator.finished()) {
                written = deflator.deflate(buffer);
                if (written > 0) {
                    output.write(buffer, 0, written);
                }
            }
            deflator.end();
            return output.toByteArray();

        } catch (Exception e) {
            System.out.println("Error with compressing");
            return null;
        }
    }

    public static void updateIndex(String path) throws IOException {
        String hash = generateHash(path);
        if (hash == null) {
            return;
        }
        File indexFile = new File(indexPath);
        if (!indexFile.exists()) {
            return;
        }
        Path root = Paths.get("").toAbsolutePath(), filePath = Paths.get(path).toAbsolutePath();
        String rel = root.relativize(filePath)
                .toString()
                .replace(File.separatorChar, '/');
        String newE = hash + " " + rel, line;

        BufferedReader reader = new BufferedReader(new FileReader(indexFile));
        List<String> lines = new ArrayList<>();
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            int idx = line.indexOf(' ');
            if (idx != -1) {
                String oldH = line.substring(0, idx), oldP = line.substring(idx + 1);
                if (oldP.equals(rel)) {
                    found = true;
                    if (oldH.equals(hash)) {
                        reader.close();
                        return;
                    }
                    line = newE;
                }
            }
            lines.add(line);
        }
        reader.close();
        if (!found) {
            lines.add(newE);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, false));
        for (int i = 0; i < lines.size(); i++) {
            writer.write(lines.get(i));
            writer.newLine();
        }
        writer.close();
    }

    public static void indexTester() {
        try {
            cleanup();
            newRepo();
            Files.createDirectories(Path.of("a"));
            Files.createDirectories(Path.of("b"));
            Files.writeString(Path.of("a/hello.txt"), "hola");
            Files.writeString(Path.of("b/hello.txt"), "hola");
            createBlob("a/hello.txt");
            updateIndex("a/hello.txt");
            createBlob("b/hello.txt");
            updateIndex("b/hello.txt");
            Files.writeString(Path.of("hellooooo.txt"), "hola");
            createBlob("hellooooo.txt");
            updateIndex("hellooooo.txt");
            Files.writeString(Path.of("hellooooo.txt"), "hola edited");
            createBlob("hellooooo.txt");
            updateIndex("hellooooo.txt");
            System.out.println(Files.readString(Path.of(indexPath)));
        } catch (Exception e) {
            System.out.println("didn't go thru");
        }
    }

    public static void cleanup() {
        try {
            File objects = new File(objDirectory);
            if (objects.exists()) {
                for (File f : objects.listFiles()) {
                    f.delete();
                }
            }
            File index = new File(indexPath);
            if (index.exists()) {
                new FileWriter(index, false).close();
            }
            File[] files = new File(".").listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".txt")) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in cleaning up");
        }
    }

    public static String createTree(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        File[] children = dir.listFiles();
        if (children != null) {
            java.util.Arrays.sort(children);
        }
        StringBuilder tree = new StringBuilder();
        if (children == null) {
            return null;
        }

        for (File child : children) {
            String path = child.getAbsolutePath();
            if (child.isFile()) {
                createBlob(path);
                String hash = generateHash(path);
                if (tree.length() > 0) {
                    tree.append("\n");
                }
                tree.append("blob ").append(hash).append(" ").append(child.getName());
                System.out.println("Added file " + path);
            } else if (child.isDirectory()) {
                String sub = createTree(path);
                if (tree.length() > 0) {
                    tree.append("\n");
                }
                tree.append("tree ").append(sub).append(" ").append(child.getName());
                System.out.println("Added directory " + path);
            }
        }

        File temp = new File(directoryPath + "/tree");
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        writer.write(tree.toString());
        writer.close();
        String HASH = generateHash(temp.getPath());
        File TREE = new File(objDirectory + "/" + HASH);
        Files.write(TREE.toPath(), Files.readAllBytes(temp.toPath()));
        temp.delete();
        return HASH;
    }

    public static void treeTester() {
        try {
            cleanup();
            newRepo();
            Files.createDirectories(Path.of("x"));
            Files.createDirectories(Path.of("x/y"));
            Files.writeString(Path.of("x/a.txt"), "Kyoto");
            Files.writeString(Path.of("x/y/b.txt"), "Osaka");
            String hash = createTree("x");
            System.out.println(Files.readString(Path.of(objDirectory + "/" + hash)));
        } catch (Exception e) {
            System.out.println("didn't go thru");
        }
    }

    public static void treeIndex() throws IOException {
        WorkingList();
        while (SubDirExist()) {
            String leaf = find();
            if (leaf.length() == 0) {
                break;
            }
            processDir(leaf);
        }
        String root = processDir("");
        if (root == null) {
            File temp = File.createTempFile("empty", ".txt");
            new FileWriter(temp, false).close();
            String empty = generateHash(temp.getPath());
            File obj = new File(objDirectory + "/" + empty);
            if (!obj.exists()) {
                Files.write(obj.toPath(), Files.readAllBytes(temp.toPath()));
            }
            temp.delete();

            BufferedWriter FINAL = new BufferedWriter(new FileWriter(workinglistPath, false));
            FINAL.write("tree " + empty + " root");
            FINAL.close();
            return;
        }
        BufferedWriter FINAL = new BufferedWriter(new FileWriter(workinglistPath, false));
        FINAL.write("tree " + root + " root");
        FINAL.close();
    }

    private static void WorkingList() throws IOException {
        File indexFile = new File(indexPath);
        if (!indexFile.exists()) {
            BufferedWriter empty = new BufferedWriter(new FileWriter(workinglistPath, false));
            empty.write("");
            empty.close();
            return;
        }
        BufferedReader indexReader = new BufferedReader(new FileReader(indexFile));
        ArrayList<String> entries = new ArrayList<String>();
        while (indexReader.ready()) {
            String line = indexReader.readLine();
            if (line == null || line.length() == 0) {
                continue;
            }
            entries.add("blob " + line);
        }
        indexReader.close();

        Collections.sort(entries, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                String patha = extractPath(a), pathb = extractPath(b);
                return patha.compareTo(pathb);
            }
        });
        File workingFile = new File(workinglistPath), parent = workingFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(workingFile, false));
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                out.write("\n");
            }
            out.write(entries.get(i));
        }
        out.close();
    }

    private static boolean SubDirExist() throws IOException {
        File workingFile = new File(workinglistPath);
        if (!workingFile.exists()) {
            return false;
        }
        BufferedReader reader = new BufferedReader(new FileReader(workingFile));
        boolean found = false;
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null || line.length() == 0) {
                continue;
            }
            String path = extractPath(line);
            if (path.indexOf('/') != -1) {
                found = true;
                break;
            }
        }
        reader.close();
        return found;
    }

    private static String find() throws IOException {
        File workinglist = new File(workinglistPath);
        if (!workinglist.exists()) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new FileReader(workinglist));
        String deepest = "";
        int max = -100;
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null || line.length() == 0) {
                continue;
            }
            String path = extractPath(line), parent = findFolder(path);
            if (parent.length() == 0) {
                continue;
            }
            int depth = countChar(parent, '/');
            if (depth > max) {
                max = depth;
                deepest = parent;
            }
        }
        reader.close();
        return deepest;
    }

    private static String processDir(String dirPath) throws IOException {
        File workingFile = new File(workinglistPath);
        if (!workingFile.exists()) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new FileReader(workingFile));
        ArrayList<String> children = new ArrayList<String>();
        ArrayList<String> others = new ArrayList<String>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null || line.length() == 0) {
                continue;
            }
            String path = extractPath(line), parent = findFolder(path);
            if (parent.equals(dirPath)) {
                children.add(line);
            } else {
                others.add(line);
            }
        }
        reader.close();
        if (children.size() == 0) {
            return null;
        }
        Collections.sort(children, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return extractPath(a).compareTo(extractPath(b));
            }
        });
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            String line = children.get(i);
            builder.append(extractType(line)).append(" ");
            builder.append(extractSha(line)).append(" ");
            builder.append(last(extractPath(line)));
            if (i < children.size() - 1) {
                builder.append("\n");
            }
        }
        String tree = builder.toString();
        File temp = File.createTempFile("treeobj", ".txt");
        BufferedWriter treeWriter = new BufferedWriter(new FileWriter(temp));
        treeWriter.write(tree);
        treeWriter.close();
        String treeSHA = generateHash(temp.getPath());
        Path objDir = Path.of(objDirectory);
        if (!Files.exists(objDir)) {
            Files.createDirectories(objDir);
        }
        File finalObj = new File(objDirectory + "/" + treeSHA);
        if (!finalObj.exists()) {
            Files.write(finalObj.toPath(), Files.readAllBytes(temp.toPath()));
        }
        temp.delete();
        File tempWL = File.createTempFile("tempWL", ".txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempWL, false));
        if (dirPath.equals("")) {
            writer.write("tree " + treeSHA + " root");
        } else {
            for (int i = 0; i < others.size(); i++) {
                if (i > 0)
                    writer.write("\n");
                writer.write(others.get(i));
            }
            if (others.size() > 0) {
                writer.write("\n");
            }
            writer.write("tree " + treeSHA + " " + dirPath);
        }
        writer.close();
        workingFile.delete();
        tempWL.renameTo(workingFile);
        return treeSHA;
    }

    private static String extractType(String line) {
        return line.substring(0, line.indexOf(' '));
    }

    private static String extractSha(String line) {
        return line.substring(line.indexOf(' ') + 1, line.indexOf(' ', line.indexOf(' ') + 1));
    }

    private static String extractPath(String line) {
        return line.substring(line.indexOf(' ', line.indexOf(' ') + 1) + 1);
    }

    private static String findFolder(String path) {
        if (path.lastIndexOf('/') == -1) {
            return "";
        }
        return path.substring(0, path.lastIndexOf('/'));
    }

    private static int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count += 1;
            }
        }
        return count;
    }

    private static String last(String path) {
        if (path.lastIndexOf('/') == -1) {
            return path;
        }
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String commit(String author, String message) {
        try {
            treeIndex();
            File workinglist = new File(workinglistPath);
            if (!workinglist.exists()) {
                return null;
            }
            BufferedReader reader1 = new BufferedReader(new FileReader(workinglist));
            String line = reader1.readLine();
            reader1.close();
            if (line == null || line.length() == 0) {
                return null;
            }
            String[] parts = line.split(" ");
            if (parts.length < 2) {
                return null;
            }
            String root = parts[1], parent = "";
            File HEAD = new File(headPath);
            if (HEAD.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(HEAD));
                String hLine = reader.readLine();
                reader.close();
                if (hLine != null && hLine.trim().length() > 0) {
                    parent = hLine.trim();
                }
            }

            StringBuilder commit = new StringBuilder();
            commit.append("tree: ").append(root).append("\n");
            commit.append("parent: ").append(parent).append("\n");
            commit.append("author: ").append(author).append("\n");
            commit.append("date: ").append(new java.util.Date().toString()).append("\n");
            commit.append("message: ").append(message);

            File temp = File.createTempFile("commit", ".txt");
            BufferedWriter tempWrite = new BufferedWriter(new FileWriter(temp));
            tempWrite.write(commit.toString());
            tempWrite.close();
            String sha = generateHash(temp.getPath());
            File obj = new File(objDirectory + "/" + sha);
            new File(objDirectory).mkdirs();
            Files.write(obj.toPath(), Files.readAllBytes(temp.toPath()));
            temp.delete();
            BufferedWriter head = new BufferedWriter(new FileWriter(HEAD, false));
            head.write(sha);
            head.close();
            System.out.println(sha);
            return sha;

        } catch (Exception e) {
            System.out.println("can't make commit");
            return null;
        }
    }

    public static void commitTester() {
        try {
            cleanup();
            newRepo();
            Files.createDirectories(Path.of("testing/docs"));
            Files.writeString(Path.of("testing/README.md"), "readme");
            Files.writeString(Path.of("testing/docs/I.txt"), "I");
            Files.writeString(Path.of("testing/docs/Love.txt"), "Love");

            createBlob("testing/README.md");
            updateIndex("testing/README.md");
            createBlob("testing/docs/I.txt");
            updateIndex("testing/docs/I.txt");
            createBlob("testing/docs/Love.txt");
            updateIndex("testing/docs/Love.txt");

            commit("me", "first");

            Files.writeString(Path.of("testing/README.md"), "readme2");
            createBlob("testing/README.md");
            updateIndex("testing/README.md");

            commit("me", "second");

        } catch (Exception e) {
            System.out.println("commitTester is not working");
        }
    }

}
