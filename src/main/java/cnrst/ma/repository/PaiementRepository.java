package cnrst.ma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cnrst.ma.entity.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement,Long>{

}
