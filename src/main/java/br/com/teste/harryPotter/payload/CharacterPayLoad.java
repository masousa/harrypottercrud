package br.com.teste.harryPotter.payload;

import javax.validation.constraints.NotNull;

import br.com.teste.harryPotter.domain.Character;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CharacterPayLoad implements EntidadeTO<Character>{
	
	@FieldProperties(isIdValue=true)
	private String id;
	@NotNull(message="name is a mandatory field")
	private String name;
    private String role;
    private String school;
    private String house;
    private String patronus;

    
    public CharacterPayLoad(Character entidade) {
		generateTO(entidade);
	}
    
    
	@Override
	public Character build() {
		return new BuilderTOEntityMongoDb<Character,CharacterPayLoad>().buildEntidade(this, new Character());
	}

	@Override
	public EntidadeTO<Character> generateTO(Character entidade) {
		return new BuilderTOEntityMongoDb<Character,CharacterPayLoad>().buildTO(entidade, this);
	}

}
