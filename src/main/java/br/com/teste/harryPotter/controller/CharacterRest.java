package br.com.teste.harryPotter.controller;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.teste.harryPotter.controller.domain.Message;
import br.com.teste.harryPotter.controller.domain.MessageType;
import br.com.teste.harryPotter.domain.Character;
import br.com.teste.harryPotter.exception.HarryPotterServiceException;
import br.com.teste.harryPotter.payload.CharacterPayLoad;
import br.com.teste.harryPotter.service.CharacterService;
import br.com.teste.harryPotter.service.Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/characters")
@Api(value="Entidade Character. Permite a criação, atualização, listagem, busca e remoção de um personagem do filme Harry Potter")
public class CharacterRest implements AbstractTOController<br.com.teste.harryPotter.domain.Character,ObjectId,CharacterPayLoad>
{

	
	@Autowired
	private CharacterService characterService;

	public Service<Character,ObjectId> getService() {
		return characterService;
	}
	
	 @ApiOperation(value="endpoint que permite lista todos os personagens de uma determinada casa.")
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@RequestMapping(value = "/find", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public  ResponseEntity<List<CharacterPayLoad>> findByHouse(@RequestParam(name="house")String houseId ) {
		 
			List<Character> entidades = new ArrayList<>(characterService.findByHouse(houseId));
			if(!entidades.isEmpty()) {
				List<CharacterPayLoad> entidadesTO = new ArrayList<>(); 
				entidades.forEach(ent -> {
					try {
						entidadesTO.add(generateTO(ent));
					} catch (HarryPotterServiceException e) {
						log.error(e.getMessage(),e);
					}
				});
				return new ResponseEntity(entidadesTO, HttpStatus.OK);
				
			}else {
				return new ResponseEntity(new Message(OBJETONAOENCONTRADO, MessageType.WARNING), HttpStatus.OK);
			}
			
		}
	
	



}