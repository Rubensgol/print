package model;

public class Expedica
{
	private Expedica retorno;
	private Expedicao expedicao;

	public Expedicao getExpedicao() 
	{
		return expedicao;
	}

	public void setExpedica(Expedicao expedicao) 
	{
		this.expedicao = expedicao;
	}

	public Expedica getRetorno() 
	{
		return retorno;
	}

	public void setRetorno(Expedica retorno) 
	{
		this.retorno = retorno;
	}
}
