package controler.business.atualizar.update4j;

import java.net.URL;
import org.update4j.Configuration;

/**
 * Simple bootstrapper example that shows how to read a remote update4j config
 * and trigger the update flow implemented in AtualizaUpdate4j.
 */
public class BootstrapperExample {

    public static void main(String[] args) {
        try {
            // URL to the published config.xml (serve this over HTTPS)
            URL cfgUrl = new URL("https://example.com/print/releases/latest/config.xml");

            // Read the configuration from the remote URL using a Reader
            try (java.io.InputStream is = cfgUrl.openStream();
                 java.io.Reader reader = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
                Configuration config = Configuration.read(reader);

                // Use the existing wrapper that implements the update logic
                AtualizaUpdate4j updater = new AtualizaUpdate4j(config);

                if (updater.temAtualizacao()) {
                    System.out.println("Atualização disponível. Aplicando...");
                    updater.atualiza();
                    System.out.println("Atualização aplicada.");
                } else {
                    System.out.println("Nenhuma atualização encontrada.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro verificando atualizações: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
