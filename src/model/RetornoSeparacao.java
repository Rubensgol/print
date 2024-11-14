package model;

import java.util.List;

public class RetornoSeparacao
{
	private int status_processamento, codigo_erro, numero_paginas;
	private String status;
	private RetornoSeparacao retorno;
	private List<Separacao> separacoes;
    
    public int getStatusProcessamento() 
    {
		return status_processamento;
	}

	public void setStatusProcessamento(int status_processamento) 
	{
		this.status_processamento = status_processamento;
	}

	public int getCodigoErro()
	{
		return codigo_erro;
	}

	public void setCodigoErro(int codigo_erro) 
	{
		this.codigo_erro = codigo_erro;
	}

	public void setSeparacoes(List<Separacao> separacoes)
	{
		this.separacoes = separacoes;
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

	public RetornoSeparacao getRetorno() 
	{
		return retorno;
	}

	public void setRetorno(RetornoSeparacao retorno) 
	{
		this.retorno = retorno;
	}

	public List<Separacao> getSeparacoes()
	{
		return separacoes;
	}

	public void setAgrupamentos(List<Separacao> separacoes)
	{
		this.separacoes = separacoes;
	}

	public int getNumeroPaginas()
	{
		return numero_paginas;
	}

	public void setNumeroPaginas(int numero_paginas)
	{
		this.numero_paginas = numero_paginas;
	}
}
