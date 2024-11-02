package util;

public class UrlsTiny
{
	public static String getSeparacao(String token)
	{
    	StringBuilder urlParaChamada = new StringBuilder()
				.append("https://api.tiny.com.br/api2/separacao.pesquisa.php?");

    	urlParaChamada.append("token")
		  			  .append("=")
				  	  .append(token)
				  	  .append("&")
				  	  .append("formato")
				  	  .append("=")
				  	  .append("JSON")
				  	  .append("&")
				      .append("dataInicial")
				      .append("=")
				      .append(Util.getDataFormatada())
				      .append("&")
				      .append("dataFinal")
				      .append("=")
				      .append(Util.getDataFormatada())
				      .append("&")
				      .append("pagina")
				      .append("=")
				      .append(1)
				      .append("&")
				      .append("situacao")
				      .append("=")
				      .append("3");

    	return urlParaChamada.toString();
	}
	
    public static String getEtiqueta(String token, int idExpedicao)
    {
    	StringBuilder urlParaChamada = new StringBuilder()
				.append("https://api.tiny.com.br/api2/expedicao.obter.etiquetas.impressao.php?");

		urlParaChamada.append("token")
		  			  .append("=")
				  	  .append(token)
				  	  .append("&")
				  	  .append("formato")
				  	  .append("=")
				  	  .append("JSON")
				  	  .append("&")
				      .append("idExpedicao")
				      .append("=")
				      .append(idExpedicao);

		return urlParaChamada.toString();
    }
    
    public static String getExpedicao(String token, int idNota, String tipoObjeto)
    {
    	StringBuilder urlParaChamada = new StringBuilder()
    					.append("https://api.tiny.com.br/api2/expedicao.obter.php?");
    	
    	urlParaChamada.append("token")
		  			  .append("=")
				  	  .append(token)
				  	  .append("&")
				  	  .append("formato")
				  	  .append("=")
				  	  .append("JSON")
				  	  .append("&")
				      .append("idObjeto")
				      .append("=")
				      .append(idNota)
				      .append("&")
				      .append("tipoObjeto")
				      .append("=")
				      .append(tipoObjeto);

    	return urlParaChamada.toString();
    }
}
