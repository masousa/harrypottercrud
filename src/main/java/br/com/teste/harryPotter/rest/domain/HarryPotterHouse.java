package br.com.teste.harryPotter.rest.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HarryPotterHouse {
	private String _id;
	private String name;
	private String mascot;
	private String headOfHouse;
	private String houseGhost;
	private String founder;
	private String school;
	private String message;
}
