package model;

import java.util.List;

public class RetornoEtiqueta 
{
	private RetornoEtiqueta retorno;
	private String status_processamento;
	private String status;
	private List<LinkEtiqueta> links;
	
	public RetornoEtiqueta getRetorno()
	{
		return retorno;
	}
	
	public void setRetornoEtiquet(RetornoEtiqueta retorno) 
	{
		this.retorno = retorno;
	}

	public String getStatus_processamento()
	{
		return status_processamento;
	}

	public void setStatus_processamento(String status_processamento) 
	{
		this.status_processamento = status_processamento;
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}
	
	public List<LinkEtiqueta> getLinks() 
	{
		return links;
	}

	public void setLinks(List<LinkEtiqueta> links) 
	{
		this.links = links;
	}
	
	
}
