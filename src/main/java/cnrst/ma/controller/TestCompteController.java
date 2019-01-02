package cnrst.ma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cnrst.ma.repository.CompteRepository;

@Component
public class TestCompteController {
	@Autowired
	private CompteRepository compteAccess;
	
	public boolean isCorrectLogin(String login) {
		return compteAccess.findByEmail(login).isPresent();
	}
	
}
