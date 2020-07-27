package br.com.teste.harryPotter.controler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import br.com.teste.harryPotter.Boot;
import br.com.teste.harryPotter.domain.Character;
import br.com.teste.harryPotter.payload.CharacterPayLoad;
import br.com.teste.harryPotter.service.CharacterService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK,classes= Boot.class)

@AutoConfigureMockMvc
@TestPropertySource(
  locations = "classpath:application_test.properties")
public class CharacterRestTest {
    public static final Logger log = LogManager.getLogger(CharacterRestTest.class);

 
    @Autowired
    private MockMvc mvc;
    
    
    @Autowired
    private CharacterService characterService;
    
    
    @After
    public void removeInsertedValues() {
    	List<Character> listaInseridos = characterService.all();
    	for (Character character : listaInseridos) {
    		characterService.delete(character);
			
		}
    }
 
    
    
    
    /***
     * Cenário 01
     * Todas as informações obrigatórias preenchidas para um novo personagem.
     * Resultado seperado é a inserção deste registro na base de dados com o 
     * identificador da casa selecionada.
     * @throws Exception 
     */
    
    
    @Test
    public void inserir_novo_personagem()  {
    	CharacterPayLoad payLoad = new CharacterPayLoad();
    	payLoad.setName("Character name");
    	payLoad.setPatronus("swan");
    	payLoad.setHouse("gryffindor");
    	payLoad.setRole("student");
    	payLoad.setSchool("gryffindor");
    	
    	try {
    	String json = new Gson().toJson(payLoad);
    	mvc.perform(post("/characters/save/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(status().isOk());
    	
    	}catch(Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	
    }
    
    /****
     * Cenário: Inserir um novo personagem sem uma escola reconhecida.
     * Comportamento desejado: a inserção não será realizada ocasionando um erro
     */
    
    @Test
    public void inserir_novo_personagem_com_escola_nao_reconhecida()  {
    	CharacterPayLoad payLoad = new CharacterPayLoad();
    	payLoad.setName("Character name");
    	payLoad.setPatronus("swan");
    	payLoad.setHouse("house To test");
    	payLoad.setRole("student");
    	payLoad.setSchool("house");
    	
    	try {
    	String json = new Gson().toJson(payLoad);
    	mvc.perform(post("/characters/save/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(status().is(400));
    	
    	}catch(Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	
    }
    
    /**
     * Cenário:
     * Inserir dados do personagem sem nome 
     * 
     * **/
    @Test
    public void inserir_novo_personagem_sem_dados_obrigatorios()  {
    	CharacterPayLoad payLoad = new CharacterPayLoad();
    	payLoad.setPatronus("swan");
    	payLoad.setHouse("gryffindor");
    	payLoad.setRole("student");
    	payLoad.setSchool("gryffindor");
    	
    	try {
    	String json = new Gson().toJson(payLoad);
    	mvc.perform(post("/characters/save/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(status().is(400));
    	
    	}catch(Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	
    }
    
    
    /**
     * Cenário permitir a alteração de um personagem cadastrado com todas as informações
     * cadastradas
     * Resultado a alteração do registro informado.
     * **/
    @Test
    public void alterar_novo_personagem() throws Exception {
    	Character character = new Character();
    	character.setName("Character name");
    	character.setPatronus("swan");
    	character.setHouse("gryffindor");
    	character.setRole("student");
    	character.setSchool("gryffindor");
    	characterService.save(character);
    	
    	
    	CharacterPayLoad payLoad= new CharacterPayLoad(character);
    	payLoad.setPatronus("bird");
    	String json = new Gson().toJson(payLoad);
    	
    	
    	mvc.perform(put("/characters/update/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(status().isOk());

    	mvc.perform(
    			get("/characters/byId/{id}", payLoad.getId()))
    	    .andExpect(status().isOk())
    	    .andExpect(jsonPath("patronus").value("bird"));
    	
    	
    		
    	
    	characterService.delete(character);
    }
    
    /****
     * Cenário: Inserir um usuário alterar um valor seu identificador por um inexistente.
     * Resultado esperado: erro informando que não foi possível encontrar o identificador
     * @throws Exception
     */
    
    @Test
    public void alterar_personagem_inexistente() throws Exception {
    	Character character = new Character();
    	character.setName("Character name");
    	character.setPatronus("swan");
    	character.setHouse("gryffindor");
    	character.setRole("student");
    	character.setSchool("gryffindor");
    	characterService.save(character);
    	
    	CharacterPayLoad payLoad= new CharacterPayLoad(character);
    	String chavePayLoad =payLoad.getId(); 
    	payLoad.setPatronus("bird");
    	payLoad.setId("5f1dc5347373c46a15af9d19");
    	String json = new Gson().toJson(payLoad);
    	
    	
    	mvc.perform(put("/characters/update/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(jsonPath("typeMessage").exists());

    	mvc.perform(
    			get("/characters/byId/{id}", chavePayLoad))
    	    .andExpect(status().isOk())
    	    .andExpect(jsonPath("patronus").value("swan"));
    	
    	
    		
    	
    	characterService.delete(character);
    }
    
    
    /**
     * Cenário permitir remoção de um personagem cadastrado 
     * Resultado ao consultar pelo seu identificador nada será encontrado.
     * **/
    @Test
    public void remover_personagem() throws Exception {
    	Character character = new Character();
    	character.setName("Character name");
    	character.setPatronus("swan");
    	character.setHouse("gryffindor");
    	character.setRole("student");
    	character.setSchool("gryffindor");
    	characterService.save(character);
    	
    	
    	CharacterPayLoad payLoad= new CharacterPayLoad(character);
    	String json = new Gson().toJson(payLoad);
    	
    	
    	mvc.perform(delete("/characters/delete/")
    			.contentType(MediaType.APPLICATION_JSON_VALUE)
    			.accept(MediaType.APPLICATION_JSON_VALUE)
    			.content(json))
        		.andDo(print())
        		.andExpect(status().isOk());

    	mvc.perform(
    			get("/characters/byId/{id}", payLoad.getId()))
    	    .andExpect(status().isOk());
    	
    }
    
    /**
     * Buscar um personagem por seu identificador inválido
     * ***/
    @Test
    public void buscar_personagem_nao_existente() throws Exception {
    	
    	mvc.perform(
    			get("/characters/byIdTO/{id}", "5f1dc5347373c46a15af9d19"))
    	    .andExpect(status().is(400))
    	    .andExpect(jsonPath("typeMessage").exists());
    	
    }
    
   
}
