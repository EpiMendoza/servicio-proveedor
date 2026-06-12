package com.pay.pal;

import org.springframework.boot.SpringApplication;

public class TestPalApplication {

	public static void main(String[] args) {
		SpringApplication.from(PalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
