package br.com.teste.harryPotter.controller;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.teste.harryPotter.domain.Entidade;
import br.com.teste.harryPotter.service.Service;
import io.swagger.annotations.ApiOperation;



/***
 * Classe genérica que permite a abstração de funcionalidades de consultas de uma determinada
 * entidade
 * @author matheus
 *
 * @param <S> Entidade que representada pela aplicação
 * @param <R> Representa a chave primaria de entidade S
 */
public interface AbstractViewController<S extends Entidade, R extends Serializable> {
	public static final Logger log = LogManager.getLogger(AbstractViewController.class);
	
	@ApiOperation(value="endpoint que permite a listagem de todos os objetos de uma determinada entidade")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public default ResponseEntity<List<S>> all() {

		return new ResponseEntity(getService().all(), HttpStatus.OK);
	}
	
	@ApiOperation(value="endpoint que permite a consulta de uma determinada entidade, passando como parametro o seu identificador")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/byId/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public default ResponseEntity<S> id(@PathVariable(name="id") R id) {

		return new ResponseEntity(getService().byId(id), HttpStatus.OK);
	}
	

	public Service<S,R> getService();

}
