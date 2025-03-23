package entity;

import java.sql.Date;
import java.util.Objects;

public class Gasto {
	
	private static long globalId = 0;
	
	private Valor entrada;
	private Valor saida;
	private String referencia;
	private Date date;
	private long id;
	private boolean isTotal;
	private boolean isBlank;

	public Gasto(Gasto gasto) {
		this(gasto.getDate(), gasto.getReferencia(), gasto.getEntrada(), gasto.getSaida());
	}

	public Gasto() {
		this(new Date(System.currentTimeMillis()), "", new Valor(), new Valor());
	}
	
	public Gasto(Date date) {
		this(date, "", new Valor(), new Valor());
	}

	public Gasto(String referencia) {
		this(new Date(System.currentTimeMillis()), referencia, new Valor(), new Valor());
	}

	public Gasto(Date date, String referencia) {
		this(date, referencia, new Valor(), new Valor());
	}

	public Gasto(Valor entrada, Valor saida) {
		this(new Date(System.currentTimeMillis()), "", entrada, saida);
	}

	public Gasto(Date date, Valor entrada, Valor saida) {
		this(date, "", entrada, saida);
	}

	public Gasto(String referencia, Valor entrada, Valor saida) {
		this(new Date(System.currentTimeMillis()), referencia, entrada, saida);
	}

	public Gasto(Date date, String referencia, Valor entrada, Valor saida) {
		this.entrada = new Valor(entrada);
		this.saida = new Valor(saida);
		this.referencia = referencia;
		this.date = new Date(date.getTime());
		isBlank = false;
		isTotal = false;
		id = globalId++;
	}
	
	public boolean isTotal() {
		return isTotal;
	}
	
	public Gasto asTotal() {
		isTotal = true;
		return this;
	}

	public boolean isBlank() {
		return isBlank;
	}
	
	public Gasto asBlank() {
		isBlank = true;
		return this;
	}

	public Valor getEntrada() {
		return entrada;
	}

	public void setEntrada(Valor valor) {
		entrada = valor;
	}

	public Valor getSaida() {
		return saida;
	}

	public void setSaida(Valor valor) {
		saida = valor;
	}
	
	public Valor getDiferenca() {
		return new Valor(entrada).decValor(saida);
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public int compareTo(Gasto gasto) {
		return getDiferenca().compareTo(gasto.getDiferenca());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gasto other = (Gasto) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "Gasto [Date=" + date + ", Entrada=" + entrada + ", Saida=" + saida + ", Referencia=" + referencia + "]";
	}

}
