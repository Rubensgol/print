package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.update4j.Configuration;

import controler.interfaces.IAtualiza;
import controler.interfaces.IImprimir;
import controler.interfaces.ITrataArquivo;
import controler.business.TrataArquivo;
import controler.business.atualizar.update4j.AtualizaUpdate4j;
import controler.business.comunicaTiny.Comunica;
import controler.business.imprimir.ImprimirDesktop;
import model.EnumRetorno;
import model.LinkEtiqueta;

public class Tela extends JFrame
{
	private static final long serialVersionUID = 7163765538988263870L;

	private JPanel panel, panelBotao;
	private JTextField tToken;
	private JLabel lToken;
	private JButton bIniciar, bParar, bAtualizar;
	private JProgressBar pBuscando;

	private Container c;
	private Comunica comunica;
	private IImprimir imprimir;
	private List<LinkEtiqueta> links;
	private boolean buscando = false;
	private ITrataArquivo aTrataArquivo;
	private IAtualiza atualiza;

	public Tela(List<Integer> lidas, Configuration config)
	{
		try 
		{
			c = getContentPane();
			comunica = new Comunica(lidas);
			imprimir = new ImprimirDesktop();
			aTrataArquivo = new TrataArquivo();
			atualiza = new AtualizaUpdate4j(config);
 
			panel = new JPanel(new FlowLayout());
            panel.setSize(new Dimension(300,300));

            lToken = new JLabel("Digite o Token:");
            panel.add(lToken);

            tToken = new JTextField(20);
            panel.add(tToken);

            c.add(panel, BorderLayout.NORTH);

			bIniciar = new JButton("Iniciar");
			bIniciar.setVisible(true);
			bIniciar.addActionListener(e ->
			{
					buscando = true;
					setBotoes(buscando);	

					if (tToken.getText() == null || tToken.getText().trim().equals(""))
					{
						new TelaErro(EnumRetorno.ERRO_EM_BRANCO);
						buscando = false;
						setBotoes(buscando);

						return;
					}

					SwingUtilities.invokeLater(() ->
					{
						try
						{
							while (buscando)
							{
								EnumRetorno retorno = comunica.verificaConexao(tToken.getText());
		
								if (retorno == EnumRetorno.SUCESSO || retorno == EnumRetorno.SUCESSO_EM_BRANCO)
								{
									setBotoes(buscando);		
		
									links = comunica.getEtiquetas(tToken.getText());
		
									for (LinkEtiqueta link : links)
										imprimir.imprimir(link.getLink());
									
									aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
								}
								else
								{
									new TelaErro(EnumRetorno.ERRO_TOKEN);
									buscando = false;
									setBotoes(buscando);
									break;
								}
		
								TimeUnit.MILLISECONDS.sleep(5000);
							}
						}
						catch (Exception e1) 
						{
						  e1.printStackTrace();
						}
					});
			});
			
			panelBotao = new JPanel(new FlowLayout());
			panelBotao.add(bIniciar);

			bParar = new JButton("Parar");
			bParar.addActionListener(e ->
			{
				buscando = false;
				setBotoes(buscando);
				aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
			});

			bParar.setEnabled(buscando);
			panelBotao.add(bParar);

			bAtualizar = new JButton("Atualizar");
			bAtualizar.addActionListener(e ->
			{
				if (atualiza.temAtualizacao())
					atualiza.atualiza();
				else
					System.out.println("nao vai");
			});

			panelBotao.add(bAtualizar);
	   
			c.add(panelBotao, BorderLayout.CENTER);

			pBuscando = new JProgressBar();
			c.add(pBuscando, BorderLayout.SOUTH);

			setTitle("Imprimir etiquetas");
			setIconImage(getIcone());
			setSize(new Dimension(340,338));
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} 
		catch (Exception e) 
		{
		  e.printStackTrace();
		}
	}
	
	
	private void setBotoes(boolean buscando)
	{
		tToken.setEnabled(! buscando);
		bIniciar.setEnabled(! buscando);
		bParar.setEnabled(buscando);
		pBuscando.setIndeterminate(buscando);
	}

	private Image getIcone()
	{
		ImageIcon icon = new ImageIcon("src/print_ico.png");

		return icon.getImage();
	}
}