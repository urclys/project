package cnrst.ma.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Contrat implements Serializable {

	private static final long serialVersionUID = 2L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@NotNull
	private String RIB;
	@NotNull
	private String Banque;
	@NotNull
	private String Agence;
	@NotNull
	private String Ville;
	
	@Enumerated(EnumType.STRING)
	private ContratEnum Etat = ContratEnum.Initialise;
	@NotNull
	private String CodeSwift;
	
	
	public Contrat() {
		
	}
	
	public Contrat(String rIB, String banque, String agence, String ville, String codeSwift) {
		super();
		RIB = rIB;
		Banque = banque;
		Agence = agence;
		Ville = ville;
		CodeSwift = codeSwift;
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getRIB() {
		return RIB;
	}
	public void setRIB(String rIB) {
		RIB = rIB;
	}
	public String getBanque() {
		return Banque;
	}
	public void setBanque(String banque) {
		Banque = banque;
	}
	public String getAgence() {
		return Agence;
	}
	public void setAgence(String agence) {
		Agence = agence;
	}
	public String getVille() {
		return Ville;
	}
	public void setVille(String ville) {
		Ville = ville;
	}
	public ContratEnum getEtat() {
		return Etat;
	}
	public void setEtat(ContratEnum etat) {
		Etat = etat;
	}
	public String getCodeSwift() {
		return CodeSwift;
	}
	public void setCodeSwift(String codeSwift) {
		CodeSwift = codeSwift;
	}

	@Override
	public String toString() {
		return "Contrat [Id=" + Id + ", RIB=" + RIB + ", Banque=" + Banque + ", Agence=" + Agence + ", Ville=" + Ville
				+ ", Etat=" + Etat + ", CodeSwift=" + CodeSwift + "]";
	}
	
}
