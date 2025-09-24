package geraConfig;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.update4j.Configuration;
import org.update4j.FileMetadata;


public class GenerateUpdateConfig 
{
    public static void main(String[] args) throws Exception
    {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));

        // Args:
        // 0: baseUri (required)
        // 1: artifact local path (e.g., artifacts/app-jar/app.jar) (required)
        // 2: main class (optional, default test.Program)
        // 3: output path for config.xml (optional, default src/config/generated-config.xml)

        if (args.length < 2) 
        {
            System.err.println("Usage: GenerateUpdateConfig <baseUri> <artifactLocalPath> [mainClass] [outputPath]");
            return;
        }

        String baseUri = args[0];
        String artifactLocalPath = args[1];
        String mainClass = args.length > 2 ? args[2] : "test.Program";
        Path output = args.length > 3 ? Paths.get(args[3]) : projectRoot.resolve("src/config/generated-config.xml");

        Path artifact = Paths.get(artifactLocalPath);

        if (!Files.exists(artifact)) 
        {
            System.err.println("Artifact not found: " + artifact.toAbsolutePath());
            return;
        }

        // Choose a user-writable basePath by default (Windows vs others)
        String os = System.getProperty("os.name", "").toLowerCase();
        String basePath;

        if (os.contains("win"))
            basePath = "${user.home}/AppData/Local/print/app/"; 
        else
            basePath = "${user.home}/.print/app/";

        // Build file metadata: source is artifact local path; target path inside basePath is fixed name 'app.jar'
        List<org.update4j.FileMetadata.Reference> files = new ArrayList<>();
        try
         {
            String checksum = sha256(artifact);
            org.update4j.FileMetadata.Reference fm = FileMetadata
                .readFrom(artifact.toString())
                .path("app.jar")
                .classpath();
            files.add(fm);
            System.out.println("Added: app.jar checksum=" + checksum);
        } 
        catch (IOException e) 
        {
            throw new RuntimeException(e);
        }

        // Set baseUri/basePath and provide the application main class.
        // update4j DefaultLauncher expects either a main class or arguments.
        // Provide the default launcher main class explicitly so Configuration.launch()
        // can start the updated application.
        Configuration.Builder builder = Configuration.builder()
            .baseUri(baseUri)
            .basePath(basePath)
            .property("main.class", mainClass)
            .property("default.launcher.main.class", mainClass);

        System.out.println("Set default.launcher.main.class = " + mainClass);

        for (org.update4j.FileMetadata.Reference fm : files)
            builder = builder.file(fm);

        Configuration cfg = builder.build();


        if (output.getParent() != null && !Files.exists(output.getParent()))
            Files.createDirectories(output.getParent());
        
        try (Writer w = Files.newBufferedWriter(output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            cfg.write(w);
        }

        System.out.println("Wrote: " + output.toAbsolutePath());
    }

    private static String sha256(Path p) throws IOException 
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] data = Files.readAllBytes(p);
            byte[] digest = md.digest(data);
            return bytesToHex(digest);
        } 
        catch (Exception e) 
        {
            throw new IOException(e);
        }
    }

    private static String bytesToHex(byte[] bytes)
    {
        try (Formatter formatter = new Formatter()) 
        {
            for (byte b : bytes) 
                formatter.format("%02x", b);

            return formatter.toString();
        }
    }
}
