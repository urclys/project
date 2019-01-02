package cnrst.ma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cnrst.ma.entity.Compte;

public interface CompteRepository extends JpaRepository<Compte,Long>{

	@Query("select c from Compte c where c.Email = ?1")
	public Optional<Compte> findByEmail(String Email);

}
