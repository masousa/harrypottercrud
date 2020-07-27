package br.com.teste.harryPotter.controller;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.AbstractController;

import br.com.teste.harryPotter.controller.domain.Message;
import br.com.teste.harryPotter.controller.domain.MessageType;
import br.com.teste.harryPotter.domain.Entidade;
import br.com.teste.harryPotter.exception.HarryPotterServiceException;
import br.com.teste.harryPotter.payload.EntidadeTO;
import br.com.teste.harryPotter.payload.TO;
import br.com.teste.harryPotter.utils.Utils;
import io.swagger.annotations.ApiOperation;
/**
 * Classe Generica que permite a abstração dos métodos básicos na construção de uma API REST
 * @author matheus
 *
 * @param <S> Entidade representada pela API
 * @param <R> Tipo da chave primaria da entidade
 * @param <T> Payload representando a entidade
 */
public interface AbstractTOController <S extends Entidade, R extends Serializable, T extends EntidadeTO<S>> extends AbstractViewController<S, R> {
    public static final Logger log = LogManager.getLogger(AbstractController.class);
    
    
	public static final String OBJETONAOENCONTRADO =Utils.message("mensagem.erro.objeto.nao.encontrado");
    
    
    @ApiOperation(value="endpoint que permite a criação ou atualização de uma determinada entidade")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
	public default ResponseEntity<S> save(@RequestBody T entidade,HttpServletRequest request,Authentication authentication){
		try {
			S entity= getService().save(entidade.build());
			return new ResponseEntity(generateTO(entity), HttpStatus.OK);
		} catch (HarryPotterServiceException e) {
			return new ResponseEntity(new Message(e.getMessage(),MessageType.ERROR), HttpStatus.BAD_REQUEST);
		}
	}
    
    @ApiOperation(value="endpoint que permite a criação ou atualização de uma determinada entidade")
   	@SuppressWarnings({ "unchecked", "rawtypes" })
   	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
   	public default ResponseEntity<S> update(@RequestBody T entidade,HttpServletRequest request,Authentication authentication){
   		try {
   			S entity= getService().update(entidade.build());
   			return new ResponseEntity(generateTO(entity), HttpStatus.OK);
   		} catch (HarryPotterServiceException e) {
   			return new ResponseEntity(new Message(e.getMessage(),MessageType.ERROR), HttpStatus.BAD_REQUEST);
   		}
   	}
	
    @ApiOperation(value="endpoint que permite remover uma determinada entidade do projeto")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces= MediaType.APPLICATION_JSON_VALUE)
	public default ResponseEntity<S> delete(@RequestBody S entidade,Authentication authentication){
		 S entidadeBeforeRemoved =getService().delete(entidade);
		return new ResponseEntity(entidadeBeforeRemoved, HttpStatus.OK);
	}
	
	
    
    @ApiOperation(value="endpoint que permite lista todos os elementos de uma determinada entidade convertendo os mesmos em payloads")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/allTO", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public default ResponseEntity<List<S>> allTO() {
		List<T> entidadesTO = new ArrayList<>();
		getService().all().forEach(ent -> {
			try {
				entidadesTO.add(generateTO(ent));
			} catch (HarryPotterServiceException e) {
				log.error(e.getMessage(),e);
			}
		});
		return new ResponseEntity(entidadesTO, HttpStatus.OK);
	}
	
	@ApiOperation(value="endpoint que permite a consulta de uma determinada entidade, passando como parametro o seu identificador. Pode lançar uma exceção caso o identificador não seja válido")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/byIdTO/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public default ResponseEntity<T> idTO(@PathVariable(name="id") R id) {
		
		try {
			Optional<S> byId = getService().byId(id);
			if(byId.isPresent()) {
				return new ResponseEntity( generateTO(byId.get()), HttpStatus.OK);
			}else {
				return new ResponseEntity(new Message(OBJETONAOENCONTRADO, br.com.teste.harryPotter.controller.domain.MessageType.ERROR), HttpStatus.BAD_REQUEST);
			}
		} catch (HarryPotterServiceException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@SuppressWarnings("unchecked")
	public default T generateTO(S entidade) throws HarryPotterServiceException {
		try {
			Class<T> classeTO;
			if(entidade.getClass().isAnnotationPresent(TO.class)) {
				TO to = entidade.getClass().getAnnotation(TO.class);
				classeTO = (Class<T>) Class.forName(to.TOClass().getName());
				return classeTO.getConstructor(entidade.getClass()).newInstance(entidade);
			}else {
				String erro = "Não existe uma anotação TO associada à entidade";
				log.error(erro);
				throw new HarryPotterServiceException(erro);
				
			}
			
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			log.error(e.getMessage(),e);
			throw new HarryPotterServiceException(e.getMessage());
			
		}
	}
}
	
