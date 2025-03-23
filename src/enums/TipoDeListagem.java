package enums;

public enum TipoDeListagem {
	
	TUDO("Tudo"),
	RESUMO_MES("Resumo do mês"),
	RESUMO_ANO("Resumo do ano");
	
	private String value;
	
	private TipoDeListagem(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
}
