import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Runner {
  public static void swapVersionNumber() throws IOException {
    Path path = Paths.get("src/main/java/User.java");
    Charset charset = StandardCharsets.UTF_8;

    String content = new String(Files.readAllBytes(path), charset);
    if (content.contains("serialVersionUID = 1L;")) {
      content = content.replace("serialVersionUID = 1L;", "serialVersionUID = 2L;");
    } else if (content.contains("serialVersionUID = 2L;")) {
      content = content.replace("serialVersionUID = 2L;", "serialVersionUID = 1L;");
    }
    Files.write(path, content.getBytes(charset));
  }

  public static void main(String[] args) {
    try {
      runProcess("pwd");
      runProcess("javac -cp src -d bin src/main/java/UserFileSerialize.java src/main/java/User.java");
      runProcess("java -cp bin UserFileSerialize write");
      runProcess("java -cp bin UserFileSerialize read");
      swapVersionNumber();
      runProcess("javac -cp src -d bin src/main/java/UserFileSerialize.java src/main/java/User.java");
      runProcess("java -cp bin UserFileSerialize read");
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  // https://www.journaldev.com/937/compile-run-java-program-another-java-program
  private static void printLines(String cmd, InputStream ins) throws IOException {
    String line = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
      System.out.println(cmd + " " + line);
    }
  }

  private static void runProcess(String command) throws IOException, InterruptedException {
    Process pro = Runtime.getRuntime().exec(command);
    printLines(command + " stdout:", pro.getInputStream());
    printLines(command + " stderr:", pro.getErrorStream());
    pro.waitFor();
    System.out.println(command + " exitValue() " + pro.exitValue());
  }
}
