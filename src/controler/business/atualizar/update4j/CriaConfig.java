package controler.business.atualizar.update4j;

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
                        .baseUri("https://github.com/rubensgolSecret/print/blob/main")
                        .basePath("${user.dir}/print")
                        .file(FileMetadata.readFrom("print.jar").path("print.jar").classpath())

                        .build();

        try (Writer out = Files.newBufferedWriter(Paths.get("config.xml"))) 
        {
            config.write(out);
        }
    }
}