package model;

public enum EnumRetorno 
{
	SUCESSO("Sucesso","Sucesso!"), 
	SUCESSO_EM_BRANCO("Sucesso","Sucesso sem registro!"), 
	ERRO_TOKEN("Token Invalido","Token Invalido!"), 
	ERRO_HEADER("Parametro invalido","Algum parametro foi passado de forma incorreta!"), 
	ERROR_404("Problema de conex�o","Problema de conex�o com o servidor!"), 
	ERRO_EM_BRANCO("TOKEN EM BRANCO","TOKEN N�O PODE FICAR EM BRANCO!");
	
	private EnumRetorno(String descricao, String titulo) 
	{
		this.descricao = descricao;
	}
	
	private String descricao, titulo;
	
	public String getDescricao()
	{
		return this.descricao;
	}
	
	public String getTitulo()
	{
		return this.titulo;
	}
}
