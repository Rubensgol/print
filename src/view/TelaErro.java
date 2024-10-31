package view;

import javax.swing.JOptionPane;

import model.EnumRetorno;

public  class TelaErro extends JOptionPane
{
	private static final long serialVersionUID = -8627812068520409197L;
	
	public TelaErro(EnumRetorno retorno)
	{
		showMessageDialog(null, retorno.getDescricao(), 
        		retorno.getTitulo(), JOptionPane.WARNING_MESSAGE);
	}
}
