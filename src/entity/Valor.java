package entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Valor implements Comparable<Valor> {

	private static long globalId = 0;

	private final long id;
	private BigDecimal valor;
	private Date date;

	public Valor() {
		this("0");
	}

	public Valor(Valor valor) {
		this(valor.getDate(), valor.getValor().toString());
	}

	public Valor(Date date, Valor valor) {
		this(date, valor.getValor().toString());
	}

	public Valor(String valor) {
		this(new Date(System.currentTimeMillis()), valor);
	}

	public Valor(Date date) {
		this(date, "0");
	}

	public Valor(Date date, String valor) {
		this.id = globalId++;
		setValor(new BigDecimal(valor));
		setDate(date);
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = new Date(date.getTime());
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Valor setValor(BigDecimal valor) {
		this.valor = new BigDecimal(valor.toPlainString());
		return this;
	}

	public Valor setValor(Valor valor) {
		return setValor(valor.getValor());
	}

	public Valor setValor(String valor) {
		return setValor(new BigDecimal(valor));
	}

	public Valor decValor(BigDecimal valor) {
		this.valor = this.valor.subtract(valor);
		return this;
	}

	public Valor decValor(Valor valor) {
		return decValor(valor.getValor());
	}

	public Valor incValor(BigDecimal valor) {
		this.valor = this.valor.add(valor);
		return this;
	}

	public Valor incValor(Valor valor) {
		return incValor(valor.getValor());
	}

	public void divideBy(int value) {
		valor = valor.divide(new BigDecimal("" + value), RoundingMode.HALF_UP);
	}
	
	public void multiplyBy(int value) {
		valor = valor.multiply(new BigDecimal("" + value));
	}
	
	public boolean isMaiorQue(Valor valor) {
		return this.valor.compareTo(valor.getValor()) > 0;
	}
	
	public boolean isMesmoValor(Valor valor) {
		return this.valor.compareTo(valor.getValor()) == 0;
	}
	
	public boolean isMenorQue(Valor valor) {
		return this.valor.compareTo(valor.getValor()) < 0;
	}
	
	public boolean isPositivo() {
		return valor.signum() > 0;
	}

	public boolean isZero() {
		return valor.signum() == 0;
	}

	public String getDateString() {
		return getDateString("dd/MM/yyyy");
	}

	public String getDateString(String format) {
		return new SimpleDateFormat(format).format(date); 
	}
	
	@Override
	public String toString() {
		return valor.toPlainString();
	}

	public String toRealFormat() {
		return "R$ " + valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor.stripTrailingZeros());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Valor other = (Valor)obj;
		return id == other.id;
	}

	@Override
	public int compareTo(Valor valor) {
		return this.valor.compareTo(valor.valor);
	}

}
