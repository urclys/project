package cnrst.ma.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.BoursierEnum;
import cnrst.ma.entity.Contrat;
import cnrst.ma.entity.ContratEnum;
import cnrst.ma.exception.MyFileNotFoundException;
import cnrst.ma.repository.BoursierRepository;
import cnrst.ma.repository.ContratRepository;
import cnrst.ma.services.CompteDetails;
import cnrst.ma.services.FileStorageService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Controller
@RequestMapping(value = "/contrats")
public class ContratController {

	private static final Logger logger = LoggerFactory.getLogger(RapportController.class);
	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	BoursierRepository BoursierAccess;
	@Autowired
	ContratRepository ContratAccess;

	/*
	 * afficher le formulaire du contrat pour saisir les information de la banque
	 * .... Acteur : boursier
	 * 
	 */
	@RequestMapping(value = "/update")
	@PreAuthorize("hasAuthority('Boursier')")
	public String updateContrat(Model model,Authentication authentication) {
		CompteDetails compte =(CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();
		/*
		 * Si l'etat du boursier n'est pas Admis alors ne rien faire une fois l'etat du
		 * boursier est active il ne peut plus modifier le contrat
		 *
		 */
		if (!boursierAuthentifie.getEtatBoursier().equals(BoursierEnum.Admis)) {
			logger.info("le boursier n'est pas dans l'etat admis: Boursier " + boursierAuthentifie);
			return "redirect:/boursiers/mesdetails";
		}
		Contrat contrat = boursierAuthentifie.getContrat();
		if (contrat == null)
			contrat = new Contrat();
		else if (contrat.getEtat().equals(ContratEnum.Valide)) {
			logger.info("le contrat est déja validé: Boursier " + boursierAuthentifie);
			return "redirect:/boursiers/mesdetails";
		}
		model.addAttribute("contrat", contrat);
		model.addAttribute("title", "Modifier le contrat");
		model.addAttribute("linkActive",7);
		return "contrat/create";
	}

	/*
	 * Mettre à jour les infos bancaire Acteur : boursier
	 * 
	 */
	@PreAuthorize("hasAuthority('Boursier')")
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateContratPOST(RedirectAttributes attributes, @Valid Contrat contrat, Model model,
			@Param("RIB") String RIB, @Param("Banque") String Banque, @Param("Agence") String Agence,
			@Param("Ville") String Ville, @Param("CodeSwift") String CodeSwift, @Param("Id") Long id,
			BindingResult bindingResult,Authentication authentication) {
		if (bindingResult.hasErrors()) {
			return "redirect:/contrats/create";
		}
		CompteDetails compte =(CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();
		contrat.setAgence(Agence);
		contrat.setBanque(Banque);
		contrat.setCodeSwift(CodeSwift);
		contrat.setRIB(RIB);
		contrat.setVille(Ville);
		contrat = ContratAccess.save(contrat);

		boursierAuthentifie.setContrat(contrat);
		BoursierAccess.saveAndFlush(boursierAuthentifie);

		attributes.addFlashAttribute("success", "Le contrat a été modifié avec succés.");
		return "redirect:/boursiers/mesdetails";
	}

	/*
	 * Génerer et telecharger le contrat du boursier authentifié Acteur : Boursier
	 */
	@RequestMapping(value = "/download")
	@PreAuthorize("hasAuthority('Boursier')")
	public ResponseEntity<Resource> telechargerContrat(HttpServletRequest request,HttpServletResponse resp,Authentication authentication) {
		CompteDetails compte =(CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();
		try {
			generateContratPDF(boursierAuthentifie);
		} catch (JRException e) {
			logger.info("Exception lors de la génération du contrat:" + e.getMessage());
			return null;
		}

		String contratName = getNameContratByBoursier(boursierAuthentifie);

		Resource resource = null;
		try {
			resource = fileStorageService.loadFileAsResource(contratName, "contrat");
		} catch (MyFileNotFoundException mfnfe) {
			logger.info(mfnfe.getMessage());
			try {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Le fichier n'est pas disponible");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		if (resource == null) {
			return null;
		}
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
			return null;
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	private void generateContratPDF(Boursier boursier) throws JRException {
		JasperReport JasperReport = JasperCompileManager.compileReport("Contrat.jrxml");
		String NumBoursier = boursier.getCodeDossier();// GetCodeContrat !!!
		String NumDossier = boursier.getCodeDossier();
		String NomPrenom = boursier.toString();
		String CIN = boursier.getCIN();
		String contratName = getNameContratByBoursier(boursier);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("NumBoursier", NumBoursier);
		params.put("NumDossier", NumDossier);
		params.put("NomPrenom", NomPrenom);
		params.put("CIN", CIN);
		JasperPrint jp = JasperFillManager.fillReport(JasperReport, params, new JREmptyDataSource());
		JasperExportManager.exportReportToPdfFile(jp,fileStorageService.getContratStorageLocation().resolve(contratName).toString() );
	}

	private String getNameContratByBoursier(Boursier boursier) {
		String contratName = "Contrat " + boursier.getCodeDossier()+" "+LocalDate.now().getYear()+ ".pdf";
		return contratName;
	}
}
