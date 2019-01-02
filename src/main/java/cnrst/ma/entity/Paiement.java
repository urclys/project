package cnrst.ma.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Paiement implements Serializable {

	private static final long serialVersionUID = -4849785954836412377L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	@NotNull
	private double Montant;

	private LocalDate Date;

	@ManyToMany(mappedBy = "Paiements")
	private List<Boursier> Boursiers;

	public Paiement() {
	}

	public Paiement(double montant, LocalDate date) {
		Montant = montant;
		Date = date;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public double getMontant() {
		return Montant;
	}

	public void setMontant(double montant) {
		Montant = montant;
	}

	public LocalDate getDate() {
		return Date;
	}

	public void setDate(LocalDate date) {
		Date = date;
	}

	@Transient
	public String getFormattedMonthYearDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
		return Date.format(formatter);
	}
}
