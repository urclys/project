package cnrst.ma;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cnrst.ma.controller.TestCompteController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BourseApplicationTests {
	
	@Autowired
	TestCompteController compteController;
	@Test
	public void testIsCorrectLogin() {
		assertTrue(compteController.isCorrectLogin("cnrst1@gmail.com"));
		
	}

}
