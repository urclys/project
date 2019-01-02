package cnrst.ma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cnrst.ma.entity.Edition;

public interface EditionRepository extends JpaRepository<Edition, Long> {
	public Edition findByEdition(String e);
}
