package br.com.teste.harryPotter.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.teste.harryPotter.domain.Character;

@Repository
public interface CharacterRepository extends MongoRepository<br.com.teste.harryPotter.domain.Character, ObjectId>{
	/*
	 * Lista todos os personagens de uma determinada casa.
	 * possui uma consulta similar: db.character.find({"house":"<casa>"})
	 * @param house
	 * @return
	 */
	public List<Character> findByHouse(String house);

}
