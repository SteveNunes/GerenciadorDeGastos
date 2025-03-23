package entity;

import java.util.ArrayList;
import java.util.List;

public class Produto {
	
	private String nome;
	private List<Valor> precos;

	public Produto(String nome) {
		this(nome, null);
	}

	public Produto(String nome, Valor preco) {
		precos = new ArrayList<>();
		setNome(nome);
		if (preco != null)
			addPreco(preco);
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void addPreco(Valor preco) {
		precos.add(preco);
	}
	
	public void delPreco(Valor valor) {
		for (Valor p : precos)
			if (p.isMesmoValor(valor)) {
				precos.remove(p);
				return;
			}
	}
	
	public Valor getPrecoMinimo() {
		Valor p = new Valor("999999999999");
		for (Valor p2 : precos)
			if (p2.compareTo(p) < 0)
				p = p2;
		return p;
	}

	public Valor getPrecoMaximo() {
		Valor p = new Valor();
		for (Valor p2 : precos)
			if (p2.compareTo(p) > 0)
				p = p2;
		return p;
	}

	public Valor getPrecoMedio() {
		Valor p = new Valor();
		for (Valor p2 : precos)
			p.incValor(p2);
		p.divideBy(precos.size());
		return p;
	}

	public List<Valor> getPrecos() {
		return precos;
	}

	@Override
	public String toString() {
		return "Produto [nome=" + nome + ", precos=" + precos + "]";
	}
	
	

}
