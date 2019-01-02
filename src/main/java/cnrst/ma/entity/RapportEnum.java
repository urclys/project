package cnrst.ma.entity;

public enum RapportEnum {
	Livre("Livre"),Valide("Valide"),Rejete("Rejete");
	
	private final String value;

	private RapportEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
