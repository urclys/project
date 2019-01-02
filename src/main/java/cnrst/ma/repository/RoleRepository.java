package cnrst.ma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cnrst.ma.entity.Role;
import cnrst.ma.entity.RoleEnum;

public interface RoleRepository extends JpaRepository<Role,Long>{
	
	@Query("select r from Role r where r.Role = ?1")
	public Optional<Role> findByRole(RoleEnum role);
}
