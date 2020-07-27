package br.com.teste.harryPotter.domain;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.teste.harryPotter.payload.CharacterPayLoad;
import br.com.teste.harryPotter.payload.TO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//https://github.com/dextra/challenges/blob/master/backend/README-PT.md
@Document(collection="character")
@Getter
@Setter
@NoArgsConstructor
@TO(TOClass=CharacterPayLoad.class)
public class Character  extends Entidade{
	
	private static final long serialVersionUID = 2995388653474744808L;
	@Id
	private ObjectId id;
	@NotNull(message="name is a mandatory field")
	private String name;
	@NotNull(message="rols is a mandatory field")
    private String role;
	@NotNull(message="school is a mandatory field")
    private String school;
	@NotNull(message="house is a mandatory field")
    private String house;
	@NotNull(message="patronus is a mandatory field")
    private String patronus;
	

}
