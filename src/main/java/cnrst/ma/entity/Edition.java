package cnrst.ma.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Edition implements Serializable, Comparable<Edition> {

	private static final long serialVersionUID = 2819758724602178610L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@NotNull
	private String edition;
	@NotNull
	private LocalDate datedepart;
	@NotNull
	private LocalDate datefin;
	@OneToMany(mappedBy = "Edition", cascade = {})
	private List<Boursier> Boursiers = new ArrayList<Boursier>();

	public Edition() {

	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public LocalDate getDateDepart() {
		return datedepart;
	}

	public void setDateDepart(LocalDate dateDepart) {
		datedepart = dateDepart;
	}

	public LocalDate getDateFin() {
		return datefin;
	}

	public void setDateFin(LocalDate dateFin) {
		datefin = datedepart.plusMonths(36);
	}

	public boolean addBoursier(Boursier arg0) {
		return Boursiers.add(arg0);
	}

	public boolean addAllBoursiers(Collection<? extends Boursier> arg1) {
		return Boursiers.addAll(arg1);
	}

	public boolean containsBoursier(Object arg0) {
		return Boursiers.contains(arg0);
	}

	public Boursier getBoursier(int arg0) {
		return Boursiers.get(arg0);
	}

	public boolean removeBoursier(Object arg0) {
		return Boursiers.remove(arg0);
	}

	public int sizeBoursiers() {
		return Boursiers.size();
	}

	public int sizeBoursiersLMD() {
		return Boursiers.stream()
				.filter(b -> b.getFormation().getType().equals(FormationEnum.LMD))
				.collect(Collectors.toList())
				.size();
	}

	public int sizeBoursiersNLMD() {
		return Boursiers.stream()
				.filter(b -> b.getFormation().getType().equals(FormationEnum.NLMD))
				.collect(Collectors.toList())
				.size();
	}

	@Override
	public int compareTo(Edition o) {
		// TODO Auto-generated method stub
		return this.getEdition().compareTo(o.getEdition());
	}

	public LocalDate getDatedepart() {
		return datedepart;
	}

	public void setDatedepart(LocalDate datedepart) {
		this.datedepart = datedepart;
	}

	public LocalDate getDatefin() {
		return datefin;
	}

	public void setDatefin(LocalDate datefin) {
		this.datefin = datefin;
	}

	public List<Boursier> getBoursiers() {
		return Boursiers;
	}

	public void setBoursiers(List<Boursier> boursiers) {
		Boursiers = boursiers;
	}

	@Override
	public String toString() {
		return edition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edition other = (Edition) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}
	
}
