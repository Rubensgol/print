package geraConfig;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

public class CriaConfig
{
    public void geraConfig() throws IOException
    {
        Configuration config = Configuration.builder()
                        .baseUri("https://github.com/rubensgolSecret/print/raw/refs/heads/main/")
                        .basePath("${user.dir}/src/config/")
                        .file(FileMetadata.readFrom("src/config/print.jar").classpath())

                        .build();

        try (Writer out = Files.newBufferedWriter(Paths.get("src/config/config.xml"))) 
        {
            config.write(out);
        }
    }
}