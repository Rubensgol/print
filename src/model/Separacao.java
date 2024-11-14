package model;

public class Separacao
{
	private int id;
	private int idOrigem;
	private String objOrigem;
	private int idOrigemVinc;
	private String objOrigemVinc;

	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}

	public int getIdOrigem() 
	{
		return idOrigem;
	}

	public void setIdOrigem(int idOrigem) 
	{
		this.idOrigem = idOrigem;
	}

	public String getObjOrigem() 
	{
		return objOrigem;
	}

	public void setObjOrigem(String objOrigem) 
	{
		this.objOrigem = objOrigem;
	}

	public int getIdOrigemVinc()
	{
		return idOrigemVinc;
	}

	public void setIdOrigemVinc(int idOrigemVinc)
	{
		this.idOrigemVinc = idOrigemVinc;
	}

	public String getObjOrigemVinc()
	{
		return objOrigemVinc;
	}
}
