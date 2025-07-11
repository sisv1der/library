package com.example.library;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryApplicationTests {


	@Disabled("Disabled in CI – requires real DB")
	@Test
	void contextLoads() {
	}

}
