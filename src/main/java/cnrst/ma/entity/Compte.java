package cnrst.ma.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Type", discriminatorType = DiscriminatorType.STRING)
public class Compte implements Comparable<Compte>, Serializable {

	private static final long serialVersionUID = 8342402728866297595L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@NotNull
	private String CIN;
	@NotNull
	private String Nom;
	@NotNull
	private String Prenom;
	
	//TODO Add GSM
	
	@NotNull
	private LocalDate DateNaissance;
	@NotNull
	private String Adresse;
	@NotNull
	private String Email;
	@NotNull
	private String Password;
	@OneToOne(fetch = FetchType.EAGER)
	private Role Role;

	public Compte() {
	}

	public Compte(String cin, String nom, String prenom, String adresse, String email, String password,
			LocalDate dateNaissance) {
		CIN = cin;
		Nom = nom;
		Prenom = prenom;
		Adresse = adresse;
		Email = email;
		Password = password;
		DateNaissance = dateNaissance;
	}

	public Compte(Compte compte) {
		this.setAdresse(compte.getAdresse());
		this.setPassword(compte.getPassword());
		this.setEmail(compte.getEmail());
		this.setCIN(compte.getCIN());
		this.setDateNaissance(compte.getDateNaissance());
		this.setId(compte.getId());
		this.setNom(compte.getNom());
		this.setPrenom(compte.getPrenom());
		this.setRole(compte.getRole());
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getCIN() {
		return CIN;
	}

	public void setCIN(String cIN) {
		CIN = cIN;
	}

	public String getNom() {
		return Nom;
	}

	public void setNom(String nom) {
		Nom = nom;
	}

	public String getPrenom() {
		return Prenom;
	}

	public void setPrenom(String prenom) {
		Prenom = prenom;
	}
	
	public String getNomComplet() {
		return ""+Nom+" "+Prenom;
	}

	public String getAdresse() {
		return Adresse;
	}

	public void setAdresse(String adresse) {
		Adresse = adresse;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public LocalDate getDateNaissance() {
		return DateNaissance;
	}

	public void setDateNaissance(LocalDate dateNaissance) {
		DateNaissance = dateNaissance;
	}

	@Override
	public int compareTo(Compte o) {
		return this.getCIN().compareTo(o.getCIN());
	}

	@Override
	public String toString() {
		return Nom.toUpperCase() + " " + Prenom;
	}

	public Role getRole() {
		return Role;
	}

	public void setRole(Role role) {
		Role = role;
	}

}
