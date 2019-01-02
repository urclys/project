package cnrst.ma.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Boursier extends Compte implements Serializable {

	private static final long serialVersionUID = -2179315949212946596L;

	private String CodeConfidentielle;
	
	private String CodeBoursier;
	
	@Enumerated(EnumType.STRING)
	private BoursierEnum EtatBoursier;

	@OneToOne(fetch = FetchType.EAGER)
	private Contrat Contrat;
	@NotNull
	private String Discipline;
	@OneToOne(fetch = FetchType.EAGER)
	private Formation Formation;

	@ManyToOne(cascade = {})
	private Edition Edition;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Paiement> Paiements;

	@OneToMany(mappedBy = "Boursier", cascade = {})
	private List<Rapport> Rapports;

	public Boursier() {
		super();
	}

	public Boursier(String cin, String nom, String prenom, String adresse, String email, String password,
			LocalDate dateNaissance, String code) {
		super(cin, nom, prenom, adresse, email, password, dateNaissance);
		CodeConfidentielle = code;
		EtatBoursier = BoursierEnum.Active;
	}

	public String getCodeConfidentielle() {
		return CodeConfidentielle;
	}

	public void setCodeConfidentielle(String codeConfidentielle) {
		CodeConfidentielle = codeConfidentielle;
	}

	public BoursierEnum getEtatBoursier() {
		return EtatBoursier;
	}

	public void setEtatBoursier(BoursierEnum etatBoursier) {
		EtatBoursier = etatBoursier;
	}

	public Contrat getContrat() {
		return Contrat;
	}

	public void setContrat(Contrat contrat) {
		Contrat = contrat;
	}

	public List<Rapport> getRapports() {
		return Rapports;
	}

	public List<Paiement> getPaiements() {
		return Paiements;
	}

	public void setPaiements(List<Paiement> paiements) {
		Paiements = paiements;
	}
	
	public String getCodeBoursier() {
		return CodeBoursier;
	}

	public void setCodeBoursier(String codeBoursier) {
		CodeBoursier = codeBoursier;
	}

	public void setRapports(List<Rapport> rapports) {
		Rapports = rapports;
	}

	public Formation getFormation() {
		return Formation;
	}

	public void setFormation(Formation formation) {
		Formation = formation;
	}

	public Edition getEdition() {
		return Edition;
	}

	public void setEdition(Edition edition) {
		Edition = edition;
	}

	public String getDiscipline() {
		return Discipline;
	}

	public void setDiscipline(String discipline) {
		Discipline = discipline;
	}

	public boolean addRapport(Rapport arg0) {
		return Rapports.add(arg0);
	}

	public Rapport getRapport(int arg0) {
		return Rapports.get(arg0);
	}

	public boolean removeRapport(Object arg0) {
		return Rapports.remove(arg0);
	}

	public boolean removeAllRapports(Collection<?> arg0) {
		return Rapports.removeAll(arg0);
	}

	public int sizeRapports() {
		return Rapports.size();
	}

	public boolean addPaiement(Paiement arg0) {
		return Paiements.add(arg0);
	}

	public boolean addAllPaiements(Collection<? extends Paiement> arg0) {
		return Paiements.addAll(arg0);
	}

	public Paiement getPaiement(int arg0) {
		return Paiements.get(arg0);
	}

	public boolean removePaiement(Object arg0) {
		return Paiements.remove(arg0);
	}

	public String getCodeDossier() {
		/*
		 * Faire les calculs ou bien retourner un attribut si ca existe
		 * 
		 */
		return "01EMI";
	}

	public SemestreEnum getSemestreActuel() {
		// TODO calculer le semestre [9-01]-[02-06]
		return SemestreEnum.S2;
	}

	public String getAnneeUniv() {
		// TODO calculer l'annee univ
		String AnneUniv = "";
		LocalDate dateAujourdhui = LocalDate.now();
		if (dateAujourdhui.getMonth().getValue() >= 9) {
			// Si on est en septembe, c'est le debut de l'année, donc l'annee univ est:
			AnneUniv = dateAujourdhui.getYear() + "/" + (dateAujourdhui.getYear() + 1);
		} else {
			AnneUniv = (dateAujourdhui.getYear() - 1) + "/" + dateAujourdhui.getYear();
		}
		return AnneUniv;
	}
}
