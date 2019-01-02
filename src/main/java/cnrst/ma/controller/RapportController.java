package cnrst.ma.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.BoursierEnum;
import cnrst.ma.entity.Edition;
import cnrst.ma.entity.Rapport;
import cnrst.ma.entity.RapportEnum;
import cnrst.ma.entity.RapportTypeEnum;
import cnrst.ma.exception.FileStorageException;
import cnrst.ma.exception.MyFileNotFoundException;
import cnrst.ma.repository.BoursierRepository;
import cnrst.ma.repository.EditionRepository;
import cnrst.ma.repository.RapportRepository;
import cnrst.ma.services.FileStorageService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import cnrst.ma.services.CompteDetails;
import cnrst.ma.services.EmailServiceImp;

@Controller
@RequestMapping(value = "/rapports")
public class RapportController {
	private static final Logger logger = LoggerFactory.getLogger(RapportController.class);

	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private EmailServiceImp emailServiceImp;
	@Autowired
	private RapportRepository RapportAccess;
	@Autowired
	private EditionRepository EditionAccess;
	@Autowired
	private BoursierRepository BoursierAccess;

	/*
	 * la liste des boursiers admis pour consulter leur rapports
	 */
	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "/rapports")
	public String rapports(Model model, @PageableDefault(value = 50, page = 0) Pageable pageable,
			@RequestParam(value = "formation", required = false) String formation,
			@RequestParam(value = "edition", required = false) Long edition) {

		Stream<Boursier> boursierStream = BoursierAccess.findAll(pageable).stream()
				.filter(b -> b.getEtatBoursier().equals(BoursierEnum.Active));
		if (formation != null && !formation.isEmpty()) {
			boursierStream = boursierStream
					.filter(b -> b.getFormation() != null && b.getFormation().getType().toString().equals(formation));
		}
		if (edition != null) {
			Optional<Edition> edtOpt = EditionAccess.findById(edition);
			if (edtOpt.isPresent()) {
				boursierStream = boursierStream
						.filter(b -> b.getEdition() != null && b.getEdition().getId() == edition);
			}
		}
		List<Boursier> boursiers = boursierStream.collect(Collectors.toList());
		logger.info("Boursier After filter:" + boursiers.toString());
		model.addAttribute("boursiers", boursiers);
		model.addAttribute("formation", formation);
		model.addAttribute("edition", edition);
		model.addAttribute("editions", EditionAccess.findAll());
		model.addAttribute("page", pageable.getPageNumber());
		model.addAttribute("pagesize", pageable.getPageSize());
		return "rapport/boursiers";
	}

	/*
	 * Afficher la liste des rapports d'un boursier spécifique Acteur: Responsable
	 * 
	 */
	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "/boursier/rapports")
	public String boursierrapports(Long id, Model model) {
		Optional<Boursier> boursierOpt = BoursierAccess.findById(id);

		// si le boursier n'est pas trouvé afficher la page 404
		if (!boursierOpt.isPresent())
			return "404";

		Boursier boursier = boursierOpt.get();
		List<Rapport> rapports = boursier.getRapports();
		// Trier par Semestre.(S6 -> S1)
		rapports = rapports.stream().sorted((r1, r2) -> {
			if (r1.getSemestre() == r2.getSemestre()) {
				if (r1.getType() == RapportTypeEnum.Semestriel)
					return 1;
				return -1;
			} else {
				return -1 * (r1.getSemestre().compareTo(r2.getSemestre()));
			}
		}).collect(Collectors.toList());

		Predicate<Rapport> rapportSemestrielActuelPred = r -> r.getSemestre() == r.getBoursier().getSemestreActuel()
				&& r.getType() == RapportTypeEnum.Semestriel;
		boolean showPasDeRapportSemestriel = !rapports.stream().anyMatch(rapportSemestrielActuelPred);
		Predicate<Rapport> rapportAnnuelActuelPred = r -> r.getDate().getYear() == LocalDate.now().getYear()
				&& r.getType() == RapportTypeEnum.Annuel;
		boolean showPasDeRapportAnnuel = !rapports.stream().anyMatch(rapportAnnuelActuelPred);

		model.addAttribute("showPasDeRapportSemestriel", showPasDeRapportSemestriel);
		model.addAttribute("showPasDeRapportAnnuel", showPasDeRapportAnnuel);
		model.addAttribute("rapports", rapports);
		model.addAttribute("boursier", boursier);
		model.addAttribute("title", "Rapports de " + boursier.toString());
		return "rapport/rapports";
	}

	/*
	 * Afficher la liste de mes Rapports -> i.e les rapport du boursier authentifié
	 * Acteur: boursier
	 */
	@PreAuthorize("hasAuthority('Boursier')")
	@RequestMapping(value = "/mesrapports")
	public String mesrapports(Model model, Authentication authentication) {
		CompteDetails compte = (CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();
		if (!boursierAuthentifie.getEtatBoursier().equals(BoursierEnum.Active)) {
			model.addAttribute("etatError", "Votre etat actuel est: " + boursierAuthentifie.getEtatBoursier() + " !");
		}

		List<Rapport> rapports = boursierAuthentifie.getRapports();
		// Trier par Semestre.(S6 -> S1)
		rapports = rapports.stream().sorted((r1, r2) -> {
			if (r1.getSemestre() == r2.getSemestre()) {
				if (r1.getType() == RapportTypeEnum.Semestriel)
					return 1;
				return -1;
			} else {
				return -1 * (r1.getSemestre().compareTo(r2.getSemestre()));
			}
		}).collect(Collectors.toList());

		boolean showGenerer = false;
		boolean showGenererAnnuel = false;
		boolean showModifierAnnuel = false;
		boolean showModifier = false;
		Predicate<Rapport> rapportSemestrielActuelPred = r -> r.getSemestre() == r.getBoursier().getSemestreActuel()
				&& r.getType() == RapportTypeEnum.Semestriel;
		Predicate<Rapport> rapportAnnuelActuelPred = r -> r.getDate().getYear() == LocalDate.now().getYear()
				&& r.getType() == RapportTypeEnum.Annuel;
		String warnMessage = "";
		if (!rapports.stream().anyMatch(rapportSemestrielActuelPred)) {
			warnMessage += "Pensez à  déposer votre rapport pour le semestre actuel ("
					+ boursierAuthentifie.getSemestreActuel() + ") ";
			showGenerer = true;
		} else {
			showModifier = rapports.stream().anyMatch(r -> r.getSemestre() == r.getBoursier().getSemestreActuel()
					&& r.getEtat() == RapportEnum.Livre && r.getType() == RapportTypeEnum.Semestriel);
		}
		if (!rapports.stream().anyMatch(rapportAnnuelActuelPred)) {
			warnMessage += "Pensez à  déposer votre rapport pour l'année actuel (" + LocalDate.now().getYear() + ")";
			showGenererAnnuel = true;
		} else {
			showModifierAnnuel = rapports.stream().anyMatch(r -> r.getDate().getYear() == LocalDate.now().getYear()
					&& r.getEtat() == RapportEnum.Livre && r.getType() == RapportTypeEnum.Annuel);
		}
		if (!warnMessage.isEmpty())
			model.addAttribute("error", warnMessage);
		model.addAttribute("showGenerer", showGenerer);
		model.addAttribute("showModifier", showModifier);
		model.addAttribute("showGenererAnnuel", showGenererAnnuel);
		model.addAttribute("showModifierAnnuel", showModifierAnnuel);
		model.addAttribute("semestreActuel", boursierAuthentifie.getSemestreActuel());
		model.addAttribute("boursier", boursierAuthentifie);
		model.addAttribute("rapports", rapports);
		model.addAttribute("title", "Mes rapports");
		model.addAttribute("linkActive",7);
		model.addAttribute("month",LocalDate.now().getMonthValue());

		return "rapport/mesrapports";
	}

	
	@PreAuthorize("hasAuthority('Boursier')")
	@RequestMapping(value = "/generer", method = RequestMethod.POST)
	public String genererRapportPost(Model model, RedirectAttributes attributes, String Nom, String GSM, String Email,
			RapportTypeEnum type, @RequestParam(name = "rapport", required = false) MultipartFile rapportFile,
			Authentication authentication) {

		/*
		 * Nom, GSM et mail de l'encadrant
		 * 
		 */
		CompteDetails compte = (CompteDetails) authentication.getPrincipal();
		Optional<Boursier> boursierAuthentifieOptional = BoursierAccess.findById(compte.getId());
		Boursier boursierAuthentifie = boursierAuthentifieOptional.get();

		if (Nom == null || Nom.isEmpty() || GSM == null || GSM.isEmpty() || GSM == null || GSM.isEmpty()) {
			attributes.addFlashAttribute("error", "Merci de remplir tous les champs");
			return "redirect:mesrapports";
		}

		String nomRapport = "Rapport " + type + " " + boursierAuthentifie.toString() + " "
				+ boursierAuthentifie.getCodeDossier() + boursierAuthentifie.getSemestreActuel().toString()
				+ boursierAuthentifie.getEdition().getEdition() + ".pdf";
		Optional<Rapport> rapportOptional = null;

		if (type.equals(RapportTypeEnum.Semestriel)) {
			Predicate<Rapport> rapportActuelPred = r -> (r.getSemestre() == r.getBoursier().getSemestreActuel()
					&& r.getType() == type);
			rapportOptional = boursierAuthentifie.getRapports().stream().filter(rapportActuelPred).findFirst();
		} else {
			Predicate<Rapport> rapportActuelPred = r -> (r.getDate().getYear() == LocalDate.now().getYear()
					&& r.getType() == type);
			rapportOptional = boursierAuthentifie.getRapports().stream().filter(rapportActuelPred).findFirst();
		}
		String nomRapportResume = null;
		if (rapportFile != null) {
			String ext = getExetenion(rapportFile.getOriginalFilename());
			if (ext == null
					|| !(ext.equalsIgnoreCase("pdf") || ext.equalsIgnoreCase("docx") || ext.equalsIgnoreCase("doc"))) {
				// Si le fichier n'est pas un document PDF ou Word,afficher un message d'alert
				attributes.addFlashAttribute("error",
						"Merci de bien vouloir charger un docuement de type: PDF ou WORD!");
				return "redirect:mesrapports";
			}
			nomRapportResume = "Rapport Resumé" + boursierAuthentifie.getCodeDossier()
					+ boursierAuthentifie.getSemestreActuel().toString() + "." + ext;
			// Stocker le fichier dans le repertoire spécifié dans l'attribut file.dir dans
			// app.prop
			try {
				fileStorageService.storeFile(rapportFile, nomRapport, "rapport");
			} catch (FileStorageException fse) {
				attributes.addFlashAttribute("error",
						"Un erreur est survenue,merci de bien vouloir notifier le responsable si ça persist");
				return "redirect:charger";
			}
		}

		saveOrUpdateRapport(boursierAuthentifie, rapportOptional,type, nomRapport, nomRapportResume);
		GenererRapport(boursierAuthentifie, nomRapport, type, Nom, GSM, Email);

		attributes.addFlashAttribute("success", "Le rapport a été généré avec succés.");
		return "redirect:mesrapports";

	}

	private void GenererRapport(Boursier boursierAuthentifie, String nomRapport, RapportTypeEnum type, String Nom,
			String GSM, String Email) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		initRapportParams(boursierAuthentifie, params, Nom, GSM, Email);
		try {
			generatePDFRapport(params, nomRapport, type);
		} catch (JRException e) {
			// TODO handle this exception
			e.printStackTrace();
		}
		try {
			emailServiceImp.sendDownLoadRapportMail(boursierAuthentifie, nomRapport, type);
		} catch (Exception e) {
			// TODO gerer cet exception
			e.printStackTrace();
		}
	}

	private void saveOrUpdateRapport(Boursier boursier, Optional<Rapport> rapportOptional, RapportTypeEnum type,
			String nomRapport, String nomRapportResume) {
		Rapport rapport = null;
		if (!rapportOptional.isPresent()) {
			/*
			 * Le boursier n'a pas encors générer le rapport. Alors creons le un nouveau
			 */
			rapport = new Rapport();
			rapport.setBoursier(boursier);
			rapport.setDate(LocalDate.now());
			rapport.setType(type);
			rapport.setEtat(RapportEnum.Livre);
			rapport.setSemestre(boursier.getSemestreActuel());
			rapport.setURIDocument(nomRapport);
			if (nomRapportResume != null)
				rapport.setURIResumeDocument(nomRapportResume);
			rapport = RapportAccess.saveAndFlush(rapport);
		} else {
			// Si le boursier a déja le rapport de ce semestre, donc il peut le modifier...
			rapport = rapportOptional.get();
			if (rapport.getEtat() == RapportEnum.Valide)
				return;
			rapport.setDate(LocalDate.now());
			rapport.setURIDocument(nomRapport);
			if (nomRapportResume != null)
				rapport.setURIResumeDocument(nomRapportResume);
			RapportAccess.saveAndFlush(rapport);
		}
	}

	private String getExetenion(String originalFilename) {
		logger.info("originalFilename = " + originalFilename);
		String[] tokens = originalFilename.split("\\.");
		for (String n : tokens)
			logger.info(n);
		if (tokens.length > 0) {
			return tokens[tokens.length - 1].trim();
		}
		return null;
	}

	private void generatePDFRapport(HashMap<String, Object> params, String nomRapport, RapportTypeEnum type)
			throws JRException {
		// Génerer le rapport PDF en utilisant Jasper...
		String jrxmlFile;
		if (type.equals(RapportTypeEnum.Annuel))
			jrxmlFile = "rapportAnnuel.jrxml";
		else
			jrxmlFile = "rapportSemestriel.jrxml";
		JasperReport JasperReport = JasperCompileManager.compileReport(jrxmlFile);
		JasperPrint jp = JasperFillManager.fillReport(JasperReport, params, new JREmptyDataSource());
		JasperExportManager.exportReportToPdfFile(jp,
				fileStorageService.getRapportStorageLocation().resolve(nomRapport).toString());

	}

	@RequestMapping(value = "/download/{fileName:.+}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Resource> downloadRapport(@PathVariable String fileName, HttpServletRequest request,
			HttpServletResponse resp) {
		Resource resource = null;
		try {
			resource = fileStorageService.loadFileAsResource(fileName, "rapport");
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

	/*
	 * Acteur : Responsable
	 */
	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "valider")
	public String valider(Long id, Model model) {
		Optional<Rapport> rapport = RapportAccess.findById(id);
		if (!rapport.isPresent())
			return "redirect:rapports/boursiers";
		Boursier boursier = rapport.get().getBoursier();
		Long idBoursier = boursier.getId();
		if (!rapport.get().getEtat().equals(RapportEnum.Valide)) {
			// Si pas encore valide
			rapport.get().setEtat(RapportEnum.Valide);
			RapportAccess.save(rapport.get());
			emailServiceImp.sendRapportValide(boursier, rapport.get());
		}
		return "redirect:boursier/rapports?id=" + idBoursier;
	}

	/*
	 * Acteur : Responsable
	 */
	@PreAuthorize("hasAuthority('Responsable')")
	@RequestMapping(value = "rejeter")
	public String rejeter(Long id, Model model) {
		Optional<Rapport> rapport = RapportAccess.findById(id);
		if (!rapport.isPresent())
			return "redirect:rapports/boursiers";
		Boursier boursier = rapport.get().getBoursier();
		Long idBoursier = boursier.getId();
		if (!rapport.get().getEtat().equals(RapportEnum.Rejete)) {
			rapport.get().setEtat(RapportEnum.Rejete);
			RapportAccess.save(rapport.get());
			emailServiceImp.sendRapportRejete(boursier, rapport.get());
		}
		return "redirect:boursier/rapports?id=" + idBoursier;
	}

	private void initRapportParams(Boursier boursier, HashMap<String, Object> params, String NomEncadrant,
			String GSMEncadrant, String EmailEncadrant) {
		String NumBoursier = boursier.getCodeDossier();// TODO Code boursier
		String Edition = boursier.getEdition().getEdition();
		String AnneeUniversitaire = boursier.getAnneeUniv();
		String NomBoursier = boursier.toString();
		String GSMBoursier = "";// TODO add GSM
		String MailBoursier = boursier.getEmail();
		String Etalissement = boursier.getFormation().getEtablissement();
		String Discipline = boursier.getDiscipline();
		String Theme = boursier.getFormation().getTheme();
		String Sujet = boursier.getFormation().getSujetThese();
		String MotCles = boursier.getFormation().getMotcles();
		params.put("NumBoursier", NumBoursier);
		params.put("Edition", Edition);
		params.put("AnneeUniversitaire", AnneeUniversitaire);
		params.put("NomBoursier", NomBoursier);
		params.put("GSMBoursier", GSMBoursier);
		params.put("MailBoursier", MailBoursier);
		params.put("Etalissement", Etalissement);
		params.put("Discipline", Discipline);
		params.put("Theme", Theme);
		params.put("Sujet", Sujet);
		params.put("MotCles", MotCles);
	}
}
