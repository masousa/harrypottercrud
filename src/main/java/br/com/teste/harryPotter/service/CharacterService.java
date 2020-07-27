package br.com.teste.harryPotter.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import br.com.teste.harryPotter.dao.CharacterRepository;
import br.com.teste.harryPotter.domain.Character;
import br.com.teste.harryPotter.exception.HarryPotterServiceException;
import br.com.teste.harryPotter.rest.service.HarryPotterServiceRestTemplate;

@Service
public class CharacterService implements br.com.teste.harryPotter.service.Service<Character, ObjectId> {
	
	
	@Autowired
	private CharacterRepository characterRepository;
	
	@Autowired
	private HarryPotterServiceRestTemplate harryPotterService;

	@Override
	public MongoRepository<Character, ObjectId> getDao() {
		return characterRepository;
	}
	
	/***
	 * Método responsável por salvar e alterar um registro de um character do HarryPotter
	 * Este método pode disparar uma exceção do tipo {@link HarryPotterServiceException}
	 * quando não informada corretamente a escola a que este personagem pertence.
	 * Valores possíveis Gryffindor, Ravenclaw, Slytherin, Hufflepuff.
	 * Todos os valores para o cadastro deste registro são obrigatórios.
	 * 
	 */
	@Override
	public Character save(Character entidade) throws HarryPotterServiceException {
		String harryPotterHouse = harryPotterService.getKeyFromHouse(entidade.getHouse());
		entidade.setHouse(harryPotterHouse);
		return br.com.teste.harryPotter.service.Service.super.save(entidade);
	}
	
	/**
	 * Método que lista todos os alunos de uma determinada casa.
	 *
	 * @param houseId identificador das casas a serem consultadas
	 * @return Uma lista contendo os personagens de uma determinada casa
	 */
	public List<Character> findByHouse(String houseId) {
		return characterRepository.findByHouse(houseId);
	}
	
	
	

	

}
