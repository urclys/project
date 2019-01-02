package cnrst.ma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cnrst.ma.services.CompteDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter{
	@Autowired
	private CompteDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
		.antMatchers("/boursiers/**").authenticated()
		.antMatchers("/rapports/**").authenticated()
		.antMatchers("/paiements/**").authenticated()
		.antMatchers("/editions/**").authenticated()
		.antMatchers("/formations/**").authenticated()
		.antMatchers("/contrats/**").authenticated()
		.anyRequest().permitAll()
		.and().formLogin().permitAll().loginPage("/connecter")
		.usernameParameter("email").passwordParameter("password")
		.loginProcessingUrl("/login")
	    .and().exceptionHandling().accessDeniedPage("/Access_Denied");
	}
}
