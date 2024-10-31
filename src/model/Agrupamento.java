package model;

import java.util.List;

public class Agrupamento 
{
	private int idAgrupamento;
	private List<Expedica> expedicoes;

	public int getIdAgrupamento()
	{
		return idAgrupamento;
	}
	
	public void setIdAgrupamento(int idAgrupamento)
	{
		this.idAgrupamento = idAgrupamento;
	}
	
	public List<Expedica> getExpedicoes() 
	{
		return expedicoes;
	}

	public void setExpedicoes(List<Expedica> expedicoes) 
	{
		this.expedicoes = expedicoes;
	}

	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Agrupamento [expedicoes=");
		builder.append(expedicoes);
		builder.append("]");
		return builder.toString();
	}
	
	
}
