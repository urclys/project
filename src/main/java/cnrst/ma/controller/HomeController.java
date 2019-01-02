package cnrst.ma.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cnrst.ma.entity.RoleEnum;
import cnrst.ma.services.CompteDetails;


@Controller
@RequestMapping(value = "")
public class HomeController {
	@RequestMapping(value = "/")
	@PreAuthorize("isAuthenticated()")
	public String index(Model model,Authentication authentication) {
		CompteDetails compte =(CompteDetails) authentication.getPrincipal();
		if(compte.getRole().getRole().equals(RoleEnum.Boursier))
			return "redirect:/boursiers/mesdetails";
		return "redirect:/editions/editions";
	}
	@RequestMapping(value = "/connecter")
	public String connecterForm(Model model) {
		model.addAttribute("title","Connection");
		model.addAttribute("linkActive",0);
		return "login/login";
	}
	@RequestMapping(value = "/Access_Denied",method=RequestMethod.GET)
	public String Access_Denied(Model model) {
		model.addAttribute("title","Access Denied");
		model.addAttribute("linkActive",0);
		return "login/Access_Denied";
	}
}
