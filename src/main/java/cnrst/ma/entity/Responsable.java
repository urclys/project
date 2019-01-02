package cnrst.ma.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Responsable extends Compte {
	private static final long serialVersionUID = 1985515350478230364L;

	public Responsable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Responsable(String cin, String nom, String prenom, String adresse, String email, String password,
			LocalDate dateNaissance) {
		super(cin, nom, prenom, adresse, email, password, dateNaissance);
		// TODO Auto-generated constructor stub
	}

}
