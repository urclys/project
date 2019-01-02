package cnrst.ma.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cnrst.ma.entity.Compte;
import cnrst.ma.repository.CompteRepository;

@Service
public class CompteDetailsService implements UserDetailsService {

	@Autowired
	private CompteRepository CompteAccess;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Compte> OptionalCompte = CompteAccess.findByEmail(username);
		// If the user doesn't existe return an exception..
		OptionalCompte.orElseThrow(() -> new UsernameNotFoundException("Compte n'existe pas"));
		// else return the new compteDetails
		return OptionalCompte.map(CompteDetails::new).get();
	}

}
