package controler.business.atualizar.update4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.UpdateOptions;
import org.update4j.UpdateOptions.ArchiveUpdateOptions;
import org.update4j.service.UpdateHandler;

import controler.interfaces.IAtualiza;

public class AtualizaUpdate4j implements IAtualiza, UpdateHandler
{
    private Configuration config;

    public AtualizaUpdate4j(Configuration config)
    {
        this.config = config;
    }

    @Override
    public void atualiza() 
    {
        try {
            System.out.println("AtualizaUpdate4j: baseUri=" + config.getBaseUri() + " basePath=" + config.getBasePath());
            System.out.println("Arquivos no config:");
            config.getFiles().forEach(f -> System.out.println(" - " + f.getPath() + " checksum=" + f.getChecksum()));
        } catch (Exception ex) {
            System.err.println("Erro lendo config para log: " + ex.getMessage());
        }

        Path zip = Paths.get("print-update.zip");
        ArchiveUpdateOptions arqv;

        try 
        {
            // Check again before attempting update to avoid unnecessary work
            if (!temAtualizacao()) {
                System.out.println("Nenhuma atualização disponível; pulando atualização.");
                return;
            }

            arqv = UpdateOptions.archive(zip).updateHandler(AtualizaUpdate4j.this);

            var result = config.update(arqv);
            if (result.getException() == null) {
                System.out.println("Atualização baixada com sucesso; instalando...");
                Archive.read(zip).install();

                System.out.println("Instalação concluída; lançando nova versão...");
                config.launch();

                // After launching the updated application, exit this JVM so we don't keep the old UI open
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.exit(0);
            } else {
                System.err.println("Falha ao aplicar atualização: " + result.getException());
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
		}
    }

    @Override
    public boolean temAtualizacao()  
    {
        try 
        {
            return config.requiresUpdate();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        return false;
    }
}
