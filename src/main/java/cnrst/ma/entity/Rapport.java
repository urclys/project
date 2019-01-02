package cnrst.ma.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
public class Rapport implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;

	private String URIDocument;

	private String URIResumeDocument;
	
	private LocalDate Date;

	@Enumerated(EnumType.STRING)
	private SemestreEnum Semestre;

	@Enumerated(EnumType.STRING)
	private RapportTypeEnum Type;

	@Enumerated(EnumType.STRING)
	private RapportEnum Etat;

	@ManyToOne()
	private Boursier Boursier;

	public Rapport() {

	}

	public Rapport(String uRIDocument, LocalDate date, SemestreEnum semestre, RapportEnum etat, RapportTypeEnum type,String uRIResumeDocument) {
		super();
		URIResumeDocument = uRIResumeDocument;
		URIDocument = uRIDocument;
		Date = date;
		Semestre = semestre;
		Etat = etat;
		Type = type;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getURIDocument() {
		return URIDocument;
	}

	public void setURIDocument(String uRIDocument) {
		URIDocument = uRIDocument;
	}

	public LocalDate getDate() {
		return Date;
	}

	public void setDate(LocalDate date) {
		Date = date;
	}

	public SemestreEnum getSemestre() {
		return Semestre;
	}

	public void setSemestre(SemestreEnum semestre) {
		Semestre = semestre;
	}

	public RapportEnum getEtat() {
		return Etat;
	}

	public void setEtat(RapportEnum etat) {
		Etat = etat;
	}

	public Boursier getBoursier() {
		return Boursier;
	}

	public void setBoursier(Boursier boursier) {
		Boursier = boursier;
	}

	public RapportTypeEnum getType() {
		return Type;
	}

	public void setType(RapportTypeEnum type) {
		Type = type;
	}

	public String getURIResumeDocument() {
		return URIResumeDocument;
	}

	public void setURIResumeDocument(String uRIResumeDocument) {
		URIResumeDocument = uRIResumeDocument;
	}

}
