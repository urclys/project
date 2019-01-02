package cnrst.ma.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cnrst.ma.entity.Compte;

public class CompteDetails extends Compte implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	CompteDetails(final Compte compte){
		super(compte);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return RoleToGrantedAuthority();
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public String getUsername() {
		return super.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	private Collection<? extends GrantedAuthority> RoleToGrantedAuthority(){
		cnrst.ma.entity.Role role = super.getRole();
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		
		if(role!=null) {
			authorities.add(new SimpleGrantedAuthority(role.getRole().toString()));
		}
		return authorities;
	}
}
