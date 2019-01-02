package cnrst.ma.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.Paiement;
import cnrst.ma.repository.BoursierRepository;
import cnrst.ma.repository.PaiementRepository;
import cnrst.ma.services.CompteDetails;

@Controller
@RequestMapping(value = "/paiements")
public class PaiementController {
	@Autowired
	PaiementRepository PaiementAccess;
	@Autowired
	BoursierRepository BoursierAccess;

	@RequestMapping(value = "/")
	@PreAuthorize("hasAuthority('Responsable')")
	public String paiements(Model model) {
		List<Paiement> paiements = PaiementAccess.findAll();
		model.addAttribute("paiements", paiements);
		model.addAttribute("title", "paiements");
		return "paiement/paiements";
	}

	/*
	 * Afficher la liste des paiement du boursier authentifié
	 * 
	 * Acteur: Boursier
	 * 
	 */
	@PreAuthorize("hasAuthority('Boursier')")
	@RequestMapping(value = "/mespaiements")
	public String mespaiements(Model model, @PageableDefault(value = 12, page = 0) Pageable pageable,Authentication authentication) {
		CompteDetails compte =(CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();
		/*
		 * Récupérer la liste des paiement du boursier,et la trié par date desc
		 */
		List<Paiement> paiements = boursierAuthentifie.getPaiements().stream()
				.sorted(Comparator.comparing(Paiement::getDate).reversed()).collect(Collectors.toList());
		;
		/*
		 * Diviser la liste des paiements en sous-liste (Page)
		 * 
		 */
		if (pageable.getOffset() >= paiements.size() && !paiements.isEmpty()) {
			// Si la page depasse e nombre disponible,rediriger vers la 1ere
			return "redirect:mespaiements";
		}
		Page<Paiement> paiementsPage;
		int start = (int) pageable.getOffset();
		int end = (start + pageable.getPageSize()) > paiements.size() ? paiements.size()
				: (start + pageable.getPageSize());
		paiementsPage = new PageImpl<Paiement>(paiements.subList(start, end), pageable, paiements.size());
		model.addAttribute("paiements", paiementsPage.getContent());
		model.addAttribute("title", "Mes paiements");
		model.addAttribute("page", pageable.getPageNumber());
		int[] pages = new int[paiementsPage.getTotalPages()];
		model.addAttribute("pages", pages);
		model.addAttribute("size", pageable.getPageSize());
		model.addAttribute("linkActive",6);
		return "paiement/mespaiements";
	}

	/*
	 * Afficher la liste des paiement du boursier authentifié
	 * 
	 * Acteur: Responsable
	 * 
	 */
	@RequestMapping(value = "/paiements")
	@PreAuthorize("hasAuthority('Responsable')")
	public String paiement(Long id, Model model, @PageableDefault(value = 12, page = 0) Pageable pageable) {
		Optional<Boursier> boursier = BoursierAccess.findById(id);
		List<Paiement> paiements = boursier.get().getPaiements().stream()
				.sorted(Comparator.comparing(Paiement::getDate).reversed()).collect(Collectors.toList());

		model.addAttribute("paiements", paiements);
		model.addAttribute("page", pageable.getPageNumber());
		model.addAttribute("pagesize", pageable.getPageSize());
		model.addAttribute("title", "paiements");
		return "paiement/paiements";
	}
}
