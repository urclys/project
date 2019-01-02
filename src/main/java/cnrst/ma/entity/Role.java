package cnrst.ma.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	
	@Enumerated(EnumType.STRING)
	private RoleEnum Role;
	
	
	public Role() {
		
	}
	public Role(RoleEnum role) {
		super();
		Role = role;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public RoleEnum getRole() {
		return Role;
	}
	public void setRole(RoleEnum role) {
		Role = role;
	}
}
