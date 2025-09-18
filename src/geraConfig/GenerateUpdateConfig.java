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

/**
 * Scans target/ for application artifacts and generates an update4j config XML
 * with SHA-256 checksums. Writes to src/config/generated-config.xml
 */
public class GenerateUpdateConfig {

    public static void main(String[] args) throws Exception {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path target = projectRoot.resolve("target");
        if (!Files.exists(target)) {
            System.err.println("target/ not found. Run mvn package first.");
            return;
        }

        List<org.update4j.FileMetadata.Reference> files = new ArrayList<>();

        // find jars in target
        Files.list(target).filter(p -> p.getFileName().toString().endsWith(".jar")).forEach(p -> {
            try {
                String checksum = sha256(p);
                org.update4j.FileMetadata.Reference fm = FileMetadata.readFrom(p.toString()).classpath();
                files.add(fm);
                System.out.println("Added: " + p.getFileName() + " checksum=" + checksum);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    String baseUri = args.length > 0 ? args[0] : "https://example.com/print/releases/latest/";

    Configuration.Builder builder = Configuration.builder()
        .baseUri(baseUri)
        .basePath("${user.dir}/");

        for (org.update4j.FileMetadata.Reference fm : files) {
            builder = builder.file(fm);
        }

        Configuration cfg = builder.build();

        Path out = projectRoot.resolve("src/config/generated-config.xml");
        try (Writer w = Files.newBufferedWriter(out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            cfg.write(w);
        }

        System.out.println("Wrote: " + out);
    }

    private static String sha256(Path p) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] data = Files.readAllBytes(p);
            byte[] digest = md.digest(data);
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
