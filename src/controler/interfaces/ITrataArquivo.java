package controler.interfaces;

import java.util.List;

public interface ITrataArquivo 
{
    public void salvaTxt(List<String> nfLidas);

    public List<String> carregaArquivo();
}
