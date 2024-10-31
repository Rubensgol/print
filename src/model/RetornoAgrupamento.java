package model;

import java.util.List;

public class RetornoAgrupamento
{
	private int status_processamento;
    private String status;
	private RetornoAgrupamento retorno;
	private List<Agrupamento> agrupamentos;
    
    public int getStatus_processamento() 
    {
		return status_processamento;
	}

	public void setStatus_processamento(int status_processamento) 
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

	public RetornoAgrupamento getRetorno() 
	{
		return retorno;
	}

	public void setRetorno(RetornoAgrupamento retorno) 
	{
		this.retorno = retorno;
	}

	public List<Agrupamento> getAgrupamentos()
	{
		return agrupamentos;
	}

	public void setAgrupamentos(List<Agrupamento> agrupamentos)
	{
		this.agrupamentos = agrupamentos;
	}
}
