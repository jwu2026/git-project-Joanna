import java.io.File;
import java.io.FileWriter;

public class GitWrapperTester {

    public static void main(String[] args) {
        try {
            GitWrapper git = new GitWrapper();
            git.init();

            File folder = new File("Folder");
            File sub = new File("Folder/sub");
            folder.mkdir();
            sub.mkdir();
            File f1 = new File("Folder/file1.txt");
            File f2 = new File("Folder/sub/file2.txt");
            f1.createNewFile();
            f2.createNewFile();
            FileWriter w1 = new FileWriter(f1);
            w1.write("file one");
            w1.close();
            FileWriter w2 = new FileWriter(f2);
            w2.write("file two");
            w2.close();
            git.add(f1.getPath());
            git.add(f2.getPath());
            git.commit("meee", "initial commit!");
            FileWriter w3 = new FileWriter(f1, false);
            w3.write("file one changed");
            w3.close();
            git.add(f1.getPath());
            git.commit("meee", "second commit!!");

        } catch (Exception e) {
            System.out.println("GitWrapper not working:(");
        }
    }
}
