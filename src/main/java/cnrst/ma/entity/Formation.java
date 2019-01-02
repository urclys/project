package cnrst.ma.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Formation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;

	@Enumerated(EnumType.STRING)
	private FormationEnum Type;
	@NotNull
	private String Etablissement;
	@NotNull
	private String SujetThese;
	
	private String Theme;
	private String Motcles;

	public Formation() {

	}

	public Formation(FormationEnum type, String etablissement, String sujetThese,String theme,String motcles) {
		Type = type;
		Etablissement = etablissement;
		SujetThese = sujetThese;
		Motcles = motcles;
		Theme = theme;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public FormationEnum getType() {
		return Type;
	}

	public void setType(FormationEnum type) {
		Type = type;
	}

	public String getEtablissement() {
		return Etablissement;
	}

	public void setEtablissement(String etablissement) {
		Etablissement = etablissement;
	}

	public String getSujetThese() {
		return SujetThese;
	}

	public void setSujetThese(String sujetThese) {
		SujetThese = sujetThese;
	}

	public String getMotcles() {
		return Motcles;
	}

	public void setMotcles(String motcles) {
		Motcles = motcles;
	}

	public String getTheme() {
		return Theme;
	}

	public void setTheme(String theme) {
		Theme = theme;
	}
	

}
