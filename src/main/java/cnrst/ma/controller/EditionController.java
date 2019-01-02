package cnrst.ma.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.BoursierEnum;
import cnrst.ma.entity.Edition;
import cnrst.ma.entity.FormationEnum;
import cnrst.ma.entity.Paiement;
import cnrst.ma.repository.BoursierRepository;
import cnrst.ma.repository.EditionRepository;
import cnrst.ma.repository.PaiementRepository;

@Controller
@RequestMapping(value = "/editions")
public class EditionController {
	@Autowired
	EditionRepository EditionAccess;
	@Autowired
	BoursierRepository BoursierAccess;
	@Autowired
	PaiementRepository PaiementAccess;
	
	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "/editions")
	public String edition(Model model) {
		List<Edition> editions = EditionAccess.findAll();
		model.addAttribute("editions", editions);
		model.addAttribute("paiement", new Paiement());
		model.addAttribute("title", "Editions");
		return "edition/editions";
	}

	@RequestMapping(value = "/creerEdition")
	@PreAuthorize("hasAuthority('Responsable')")
	public String creerEdition(Model model) {
		List<Boursier> boursiers = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.Admis)).collect(Collectors.toList());
		if (boursiers.isEmpty()) {
			List<Edition> editions = EditionAccess.findAll();
			model.addAttribute("editions", editions);
			model.addAttribute("paiement", new Paiement());
			model.addAttribute("title", "Editions");
			return "edition/editions";
		} else {
			Edition edition = new Edition();
			edition.setDateDepart(LocalDate.now());
			edition.setDateFin(LocalDate.now());
			edition.setEdition("" + LocalDate.now().getYear());
			EditionAccess.save(edition);
			Edition e = EditionAccess.findByEdition("" + LocalDate.now().getYear());
			for (Boursier b : boursiers) {
				b.setEdition(e);
				/* generer code confidentielle d'un boursier avec UUID GEEK ! */
				b.setCodeConfidentielle(UUID.randomUUID().toString());
				b.setEtatBoursier(BoursierEnum.Active);
				BoursierAccess.saveAndFlush(b);
			}
			/*
			 * donner la liste des editions qui n'ont pas terminer leur periode de 36 mois
			 * GEEK ! 
			 */
			List<Edition> editions = EditionAccess.findAll();
			model.addAttribute("editions", editions);
			model.addAttribute("paiement", new Paiement());
			model.addAttribute("paiements", new Paiement());
			model.addAttribute("title", "Editions");
			return "edition/editions";
		}
	}

	@RequestMapping(value = "/edition/view")
	@PreAuthorize("hasAuthority('Responsable')")
	public String view(Long id, Model model) {
		Optional<Edition> edition = EditionAccess.findById(id);
		Edition e = edition.get();
		List<Boursier> BoursiersLMD = edition.get().getBoursiers().stream()
				.filter(b -> b.getFormation().getType().equals(FormationEnum.LMD)).collect(Collectors.toList());
		List<Boursier> BoursiersNLMD = edition.get().getBoursiers().stream()
				.filter(b -> b.getFormation().getType().equals(FormationEnum.NLMD)).collect(Collectors.toList());
		model.addAttribute("boursiersLMD", BoursiersLMD);
		model.addAttribute("boursiersNLMD", BoursiersNLMD);
		model.addAttribute("edition", e);
		model.addAttribute("lmd", BoursiersLMD.size() + "/200");
		model.addAttribute("nlmd", BoursiersNLMD.size() + "/100");
		model.addAttribute("title", "Edition | " + e.getEdition());
		/* Test Chart */
		Integer LMD = BoursiersLMD.size();
		Integer NLMD = BoursiersNLMD.size();

		model.addAttribute("LMD", LMD);
		model.addAttribute("NLMD", NLMD);
		Map<String, Integer> MapBoursiers = edition.get().getBoursiers().stream().collect(Collectors.groupingBy(
				Boursier::getDiscipline,
				Collectors.collectingAndThen(Collectors.toCollection(() -> new ArrayList<>()), list -> list.size())));
		model.addAttribute("Map", MapBoursiers);
		/* End Test */
		return "edition/view";
	}

	/*
	 * Verser le montant mensuel de toutes les editions
	 */
	@RequestMapping(value = "/editions/save")
	@PreAuthorize("hasAuthority('Responsable')")
	public String versementEditions(@Valid Paiement paiements, @Param("montant") double montant, Model model,
			BindingResult bidingResult) {
		if (bidingResult.hasErrors()) {
			return "redirect:creerEdition";
		} else {
			Paiement p = new Paiement();
			p.setDate(LocalDate.now());
			p.setMontant(montant);

			List<Edition> editions = EditionAccess.findAll();
			editions.forEach(e -> e.getBoursiers().forEach(b -> b.addPaiement(p)));
			PaiementAccess.saveAndFlush(p);
		}
		return "redirect:/editions/creerEdition";
	}

	/*
	 * Verser le montant mensuel de toutes les editions
	 */
	@RequestMapping(value = "/edition/save")
	@PreAuthorize("hasAuthority('Responsable')")
	public String versementEdition(@Valid Paiement paiement, @Param("id") Long id,
			@Param("montant") double montant, Model model, BindingResult bidingResult) {
		if (bidingResult.hasErrors()) {
			return "redirect:/editions/creerEdition";
		} else {
			Paiement p = new Paiement();
			p.setDate(LocalDate.now());
			p.setMontant(montant);

			Optional<Edition> edition = EditionAccess.findById(id);
			Edition e = edition.get();
			e.getBoursiers().forEach(b -> b.addPaiement(p));
			PaiementAccess.saveAndFlush(p);
		}
		return "redirect:/editions/creerEdition";
	}
}
