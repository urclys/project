package cnrst.ma.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.BoursierEnum;
import cnrst.ma.entity.FormationEnum;
import cnrst.ma.entity.Paiement;
import cnrst.ma.repository.BoursierRepository;
import cnrst.ma.repository.ContratRepository;
import cnrst.ma.repository.PaiementRepository;
import cnrst.ma.services.CompteDetails;
import cnrst.ma.services.EmailServiceImp;

@Controller
@RequestMapping(value = "/boursiers")
public class BoursierController {
	@Autowired
	BoursierRepository BoursierAccess;
	@Autowired
	ContratRepository ContratAccess;
	@Autowired
	PaiementRepository PaiementAccess;

	@Autowired
	private EmailServiceImp emailServiceImp;

	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "/")
	public String boursier(Model model) {
		return "boursier/boursiers";
	}

	@RequestMapping(value = "admission", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('Responsable')")
	public String admission(Model model) {
		Map<String, List<Boursier>> MapBoursiersAdmis = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnListePrincipale))
				.collect(Collectors.groupingBy(Boursier::getDiscipline, Collectors.collectingAndThen(
						Collectors.toCollection(() -> new ArrayList<>()), list -> new ArrayList<>(list))));
		Map<String, List<Boursier>> MapBoursiersEnAttente = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnListeAttente))
				.collect(Collectors.groupingBy(Boursier::getDiscipline, Collectors.collectingAndThen(
						Collectors.toCollection(() -> new ArrayList<>()), list -> new ArrayList<>(list))));
		List<List<Boursier>> BoursiersAdmis = MapBoursiersAdmis.values().stream().collect(Collectors.toList());
		List<List<Boursier>> BoursiersEnAttente = MapBoursiersEnAttente.values().stream().collect(Collectors.toList());
		model.addAttribute("BoursiersAdmis", BoursiersAdmis);
		model.addAttribute("BoursiersEnAttente", BoursiersEnAttente);
		model.addAttribute("title", "Admission des Boursiers");
		return "boursier/admission";
	}

	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "/boursier")
	public String paiement(Long id, Model model) {
		Optional<Boursier> boursier = BoursierAccess.findById(id);
		Boursier b = boursier.get();
		List<Paiement> paiements = b.getPaiements();
		model.addAttribute("paiements", paiements);
		model.addAttribute("boursier", b);
		model.addAttribute("title", "paiements");
		return "paiement/paiements";
	}

	/*
	 * Afficher les détails du boursier authentifié, le contrat est inclus Acteur:
	 * Boursier
	 */
	@PreAuthorize("hasAuthority('Boursier')")
	@RequestMapping(value = "/mesdetails")
	public String MesDetails(Model model, Authentication authentication) {
		CompteDetails compte = (CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		model.addAttribute("boursier", boursierAuthentifieOptional.get());
		model.addAttribute("title", "Mes information - Contrat");
		model.addAttribute("linkActive",8);
		return "boursier/mesdetails";
	}

	@RequestMapping(value = "admission/desister")
	@PreAuthorize("hasAuthority('Responsable')")
	public String DesisterBoursierAdmis(Long id, Model model) {
		Optional<Boursier> boursier = BoursierAccess.findById(id);
		Boursier b = boursier.get();
		Optional<Boursier> BoursierElu = BoursierAccess.findAll().stream()
				.filter(bou -> bou.getEtatBoursier().equals(BoursierEnum.EnListeAttente)
						&& bou.getDiscipline().equals(b.getDiscipline()))
				.findFirst();
		if (BoursierElu.isPresent()) {
			Boursier b1 = BoursierElu.get();
			b1.setEtatBoursier(BoursierEnum.EnListePrincipale);
			BoursierAccess.saveAndFlush(b1);
		}
		b.setEtatBoursier(BoursierEnum.EnListeAttente);
		BoursierAccess.saveAndFlush(b);
		return "redirect:";
	}

	@RequestMapping(value = "admission/admettre")
	@PreAuthorize("hasAuthority('Responsable')")
	public String AdmettreBoursierAdmis(Model model) {
		List<Boursier> boursiersAdmis = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnListePrincipale)).collect(Collectors.toList());
		boursiersAdmis.forEach(b -> {
			b.setEtatBoursier(BoursierEnum.Admis);
			emailServiceImp.sendAdmisMail(b);
		});
		BoursierAccess.saveAll(boursiersAdmis);
		return "redirect:/boursiers/admis";
	}

	/* afficher la liste des admis */
	@RequestMapping(value = "admis")
	@PreAuthorize("hasAuthority('Responsable')")
	public String boursiersAdmis(Model model, @PageableDefault(value = 50, page = 0) Pageable pageable) {
		List<Boursier> boursiers = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.Admis)).collect(Collectors.toList());
		if (boursiers == null) {
			model.addAttribute("error", "Liste des boursiers Admis est vide !");
		} else {
			model.addAttribute("page", pageable.getPageNumber());
			model.addAttribute("pagesize", pageable.getPageSize());
			model.addAttribute("title", "Liste des Admis");
			model.addAttribute("boursiers", boursiers);
		}
		return "boursier/admis";
	}

	@RequestMapping(value = "encours")
	@PreAuthorize("hasAuthority('Responsable')")
	public String boursiersEnCours(Model model, @PageableDefault(value = 50, page = 0) Pageable pageable) {
		List<Boursier> boursiers = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnCours)).collect(Collectors.toList());
		if (boursiers == null) {
			model.addAttribute("error", "Liste des boursiers En Cours est vide !");
		} else {
			model.addAttribute("page", pageable.getPageNumber());
			model.addAttribute("pagesize", pageable.getPageSize());
			model.addAttribute("title", "Liste des Boursiers En Cours");
			model.addAttribute("boursiers", boursiers);
			model.addAttribute("size", boursiers.size());
		}
		return "boursier/encours";
	}

	@RequestMapping(value = "quota")
	@PreAuthorize("hasAuthority('Responsable')")
	public String quota(@Param("quota") double quota, Model model) {
		double prorata = 0.0;
		AtomicInteger atomicInteger = new AtomicInteger(0);
		int size = BoursierAccess.findAll().stream().filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnCours))
				.collect(Collectors.toList()).size();
		/* calculer le prorata */
		prorata = quota / size;
		Map<String, List<Boursier>> MapboursiersEnCours = BoursierAccess.findAll().stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.EnCours))
				.collect(Collectors.groupingBy(Boursier::getDiscipline, Collectors.collectingAndThen(
						Collectors.toCollection(() -> new ArrayList<>()), list -> new ArrayList<>(list))));
		Collection<List<Boursier>> boursiers = MapboursiersEnCours.values();
		for (Iterator<List<Boursier>> iterator = boursiers.iterator(); iterator.hasNext();) {
			List<Boursier> bs = iterator.next();
			int total = bs.size();
			double pro = (total * prorata);
			double PourcentagePreLMD = 0.33;
			double PourcentagePreNLMD = 0.66;
			double PourcentageAttrLMD = 0.66;
			double PourcentageAttrNLMD = 0.66;
			/*
			 * Nombre des boursiers Preselectionnées (LMD / NLMD) et attributaires (LMD /
			 * NLMD)
			 */
			int PreLMD = (int) (pro * PourcentagePreLMD);
			int PreNLMD = (int) (pro * PourcentagePreNLMD);
			int AttrLMD = (int) (pro * PourcentageAttrLMD);
			int AttrNLMD = (int) (pro * PourcentageAttrNLMD);
			/* Nombre des boursiers En Liste d'attente (LMD / NLMD) */
			int AttLMD = PreLMD - AttrLMD;
			int AttNLMD = PreNLMD - AttrNLMD;
			List<Boursier> LMDboursiers = bs.stream().filter(b -> b.getFormation().getType().equals(FormationEnum.LMD))
					.collect(Collectors.toList());
			List<Boursier> NLMDboursiers = bs.stream()
					.filter(b -> b.getFormation().getType().equals(FormationEnum.NLMD)).collect(Collectors.toList());
			List<Boursier> PreLMDboursiers = LMDboursiers.subList(0, PreLMD);
			PreLMDboursiers.forEach(b -> b.setEtatBoursier(BoursierEnum.EnListePrincipale));
			/* Generer code de Boursier */
			PreLMDboursiers.forEach(b -> b.setCodeBoursier(String.format("%02d", atomicInteger.getAndIncrement()) + ""
					+ b.getFormation().getEtablissement() + "" + LocalDate.now().getYear()));
			BoursierAccess.saveAll(PreLMDboursiers);
			List<Boursier> PreNLMDboursiers = NLMDboursiers.subList(0, PreNLMD);
			PreNLMDboursiers.forEach(b -> b.setEtatBoursier(BoursierEnum.EnListePrincipale));
			/* Generer code de Boursier */
			PreNLMDboursiers.forEach(b -> b.setCodeBoursier(String.format("%02d", atomicInteger.getAndIncrement()) + ""
					+ b.getFormation().getEtablissement() + "" + LocalDate.now().getYear()));
			BoursierAccess.saveAll(PreNLMDboursiers);
			List<Boursier> AttLMDboursiers = LMDboursiers.subList(PreLMD, PreLMD + AttLMD);
			AttLMDboursiers.forEach(b -> b.setEtatBoursier(BoursierEnum.EnListeAttente));
			/* Generer code de Boursier */
			AttLMDboursiers.forEach(b -> b.setCodeBoursier(String.format("%02d", atomicInteger.getAndIncrement()) + ""
					+ b.getFormation().getEtablissement() + "" + LocalDate.now().getYear()));
			BoursierAccess.saveAll(AttLMDboursiers);
			List<Boursier> AttNLMDboursiers = NLMDboursiers.subList(PreNLMD, PreNLMD + AttNLMD);
			AttNLMDboursiers.forEach(b -> b.setEtatBoursier(BoursierEnum.EnListeAttente));
			/* Generer code de Boursier */
			AttNLMDboursiers.forEach(b -> b.setCodeBoursier(String.format("%02d", atomicInteger.getAndIncrement()) + ""
					+ b.getFormation().getEtablissement() + "" + LocalDate.now().getYear()));
			BoursierAccess.saveAll(AttNLMDboursiers);
			/* supprimer tout les boursiers non acceptées */
			List<Boursier> RejeteLMDboursiers = LMDboursiers.subList(PreLMD + AttLMD, LMDboursiers.size());
			BoursierAccess.deleteAll(RejeteLMDboursiers);
			List<Boursier> RejeteNLMDboursiers = NLMDboursiers.subList(PreNLMD + AttNLMD, NLMDboursiers.size());
			BoursierAccess.deleteAll(RejeteNLMDboursiers);
		}
		return "redirect:boursiers/admission";
	}

}
