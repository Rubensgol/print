package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import model.EnumRetorno;
import model.Expedica;
import model.LinkEtiqueta;
import model.RetornoEtiqueta;
import model.RetornoSeparacao;
import model.Separacao;
import util.UrlsTiny;
import util.Util;

public class Comunica
{
	private List<Integer> nfLidas;

	public Comunica(List<Integer> nfLidas)
	{
		this.nfLidas = nfLidas;
	}

	public List<LinkEtiqueta> getEtiquetas(String token) throws IOException
	{
		List<LinkEtiqueta> links = new ArrayList<>();

		RetornoSeparacao retornoSepara = getSeparacao(token);
		
		if (retornoSepara != null && retornoSepara.getRetorno() != null)
		{
			retornoSepara = retornoSepara.getRetorno();

			// fazer verificacoes dos retornos
			if (retornoSepara.getStatusProcessamento() == 3 && retornoSepara.getCodigoErro() == 0)
			{
				for (Separacao separa : retornoSepara.getSeparacoes())
				{
					if(nfLidas.contains(separa.getIdOrigem()))
					{
						continue;
					}

					Expedica retornoExpedica = getExpedicao(token, separa.getIdOrigem(), separa.getObjOrigem());

					if (retornoExpedica != null && retornoExpedica.getRetorno() != null &&
						retornoExpedica.getRetorno().getExpedicao() != null)
					{
						RetornoEtiqueta retornEtiqueta = getEtiqueta(token, retornoExpedica.getRetorno().getExpedicao().getId());

						if (retornEtiqueta != null && retornEtiqueta.getRetorno() != null)
						{
							retornEtiqueta = retornEtiqueta.getRetorno();

							for (LinkEtiqueta link : retornEtiqueta.getLinks())
								links.add(link);

							nfLidas.add(separa.getIdOrigem());
						}
					}
				}
			}
		}

        return links;
	}
	
	private RetornoEtiqueta getEtiqueta(String token, int idExpedi) throws IOException
	{
		Gson gson = new Gson();
		URL url = new URL(UrlsTiny.getEtiqueta(token, idExpedi));
		HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
        	return null;

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);
        
        RetornoEtiqueta retorno = gson.fromJson(jsonEmString, RetornoEtiqueta.class);
        
        return retorno;
	}
	
	private Expedica getExpedicao(String token, int idNota, String tipoObjeto) throws IOException
	{
        URL url = new URL(UrlsTiny.getExpedicao(token, idNota, tipoObjeto));
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
        	return null;

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);

        Gson gson = new Gson();
        Expedica retorno = gson.fromJson(jsonEmString, Expedica.class);

       return retorno;
	}
	
	private RetornoSeparacao getSeparacao(String token) throws IOException
	{
        URL url = new URL(UrlsTiny.getSeparacao(token));
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

        if (conexao.getResponseCode() != 200)
        	return null;

        BufferedReader resposta = new BufferedReader(new InputStreamReader((conexao.getInputStream())));
        String jsonEmString = Util.converteJsonEmString(resposta);

        Gson gson = new Gson();
        RetornoSeparacao retorno = gson.fromJson(jsonEmString, RetornoSeparacao.class);

       return retorno;
	}
	
    public EnumRetorno verificaConexao(String token) throws Exception
    {
    	RetornoSeparacao retorno = getSeparacao(token);
    	
    	if (retorno == null || retorno.getRetorno() == null)
    		return EnumRetorno.ERROR_404;
    	
    	retorno = retorno.getRetorno();
    	
    	if (retorno.getStatusProcessamento() == 2 && retorno.getCodigoErro() == 31)
    		return EnumRetorno.ERRO_TOKEN;
    	else if (retorno.getStatusProcessamento() == 3)
    		return EnumRetorno.SUCESSO;
		else if (retorno.getStatusProcessamento() == 1 && retorno.getCodigoErro() == 32)
			return EnumRetorno.SUCESSO_EM_BRANCO;
    	else
    		return EnumRetorno.ERRO_HEADER;
    }

	public List<Integer> getNfLidas()
	{
		return nfLidas;
	}
}
