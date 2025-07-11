package com.example.library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryApplicationTests {


	@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
	@Test
	void contextLoads() {
	}

}
