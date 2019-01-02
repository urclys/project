package cnrst.ma;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.BoursierEnum;
import cnrst.ma.entity.Formation;
import cnrst.ma.entity.FormationEnum;
import cnrst.ma.entity.Responsable;
import cnrst.ma.entity.Role;
import cnrst.ma.entity.RoleEnum;
import cnrst.ma.repository.*;
import cnrst.ma.services.FileStorageProperties;

@SpringBootApplication
@EnableAsync
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({ FileStorageProperties.class })
public class BourseApplication implements CommandLineRunner {
	@Autowired
	private BoursierRepository BoursierAccess;
	/*
	 * @Autowired private EditionRepository EditionAccess;
	 */
	@Autowired
	private ResponsableRepository ResponsableAccess;
	@Autowired
	private RoleRepository RoleAccess;
	@Autowired
	private FormationRepository FormationAccess;

	public static void main(String[] args) {
		SpringApplication.run(BourseApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// createData();
	}


	public void createData() {

		Role boursierRole = new Role();
		boursierRole.setRole(RoleEnum.Boursier);
		boursierRole = RoleAccess.save(boursierRole);
		Role responsableRole = new Role();
		responsableRole.setRole(RoleEnum.Responsable);
		responsableRole = RoleAccess.save(responsableRole);

		/*
		 * Edition edition2018= new Edition();
		 * edition2018.setDatedepart(LocalDate.now());
		 * edition2018.setDateFin(LocalDate.now());
		 * edition2018.setEdition(""+LocalDate.now().getYear()); edition2018 =
		 * EditionAccess.save(edition2018);
		 */

		Boursier boursier = new Boursier();
		boursier.setCIN("JC545799");
		boursier.setEmail("amazzal@gmail.com");
		boursier.setNom("AMAZZAL");
		boursier.setPrenom("El-habib");
		boursier.setDateNaissance(LocalDate.now());
		boursier.setDiscipline("INFO");
		boursier.setCodeConfidentielle("AMAZZAL");
		BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
		boursier.setPassword(bcpe.encode("123456"));
		boursier.setAdresse("AGADIR HAY ERAK 61");
		boursier.setEtatBoursier(BoursierEnum.Admis);
		// boursier.setEdition(edition2018);
		boursier.setRole(boursierRole);
		Formation formation = new Formation();
		formation.setEtablissement("EMI");
		formation.setSujetThese("Exemple these");
		formation.setType(FormationEnum.NLMD);
		formation = FormationAccess.save(formation);

		boursier.setFormation(formation);

		BoursierAccess.saveAndFlush(boursier);

		Boursier boursier2 = new Boursier();
		boursier2.setCIN("JC545799");
		boursier2.setEmail("qallidi@gmail.com");
		boursier2.setNom("Qallidi");
		boursier2.setPrenom("Med");
		boursier2.setDateNaissance(LocalDate.now());
		boursier2.setDiscipline("INFO");
		boursier2.setCodeConfidentielle("QALLIDI");
		boursier2.setPassword(bcpe.encode("123456"));
		boursier2.setAdresse("ASAFI");
		boursier2.setEtatBoursier(BoursierEnum.Admis);
		// boursier2.setEdition(edition2018);
		boursier2.setRole(boursierRole);
		Formation formation2 = new Formation();
		formation2.setEtablissement("EMI");
		formation2.setSujetThese("Exemple these 2");
		formation2.setType(FormationEnum.NLMD);
		formation2 = FormationAccess.save(formation);

		boursier2.setFormation(formation2);

		BoursierAccess.saveAndFlush(boursier2);

		Responsable responsable = new Responsable();
		responsable.setCIN("JC545799");
		responsable.setEmail("resp@gmail.com");
		responsable.setNom("Qallidi");
		responsable.setPrenom("Med");
		responsable.setPassword(bcpe.encode("123456"));
		responsable.setDateNaissance(LocalDate.now());
		responsable.setAdresse("Agadir 61");
		responsable.setRole(responsableRole);

		ResponsableAccess.saveAndFlush(responsable);
	}

}
