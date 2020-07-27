package br.com.teste.harryPotter.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.teste.harryPotter.domain.Entidade;
import br.com.teste.harryPotter.exception.HarryPotterServiceException;
import br.com.teste.harryPotter.utils.Utils;

/***
 * Classe auxiliar que permite abstrair os métodos básicos bem como adicionar
 * funcionalidades apropriadas de negócio.
 * @author matheus
 *
 * @param <E>
 * @param <S>
 */
public interface Service <E extends Entidade, S extends Serializable>{
    public static final Logger log = LogManager.getLogger(Service.class);
   
	public static final String OBJETONAOENCONTRADO =Utils.message("mensagem.erro.objeto.nao.encontrado");


	/**
	 * Método que lista todas os registros de uma determinada entidade
	 * @return Uma lista de entidades de um determinado tipo recem consultado
	 */
	public default List<E> all(){
		log.debug("method all called");
		List<E> listaResultado = new ArrayList<>();
		getDao().findAll().forEach(listaResultado::add);
		return listaResultado;
	}
	
	/**
	 * Método que permite a inserção/atualização do registro de uma entidade.
	 * 
	 * @param entidade Entidade a ser salva na base de dados
	 * @return A entidade salva na base de dados
	 * @throws HarryPotterServiceException pode ser lançado mediante à alguma exceção proveninente da base de dados
	 */
	public default E save(E entidade) throws HarryPotterServiceException {
		log.debug("method save called "+entidade.toString());
		return getDao().save(entidade);
	}
	
	/**
	 * Remove um registro de uma determinada entidade.
	 * 
	 * @param entidade Entidade a ser removida da base de dados
	 * @return O objeto removido da base de dados
	 */
	public default E delete(E entidade) {
		log.debug("method delete called "+entidade.toString());
		getDao().delete(entidade);
		return entidade;
	}
	
	
	public MongoRepository<E, S> getDao();

	public default Optional<E> byId(S id) {
		log.debug("method ById called");
		return getDao().findById(id);
	}

	/**
	 * Atualiza um determinado registro de uma entidade. 
	 * Sua diferença ao método {@link #save(Entidade)} está na validação
	 * da existência da chave primaria deste registro.
	 * @param entidade entidade a ser realizada na base de dados
	 * @return A entidade atualizada da base de dados
	 * @throws HarryPotterServiceException ocorrerá devido à alguma inconsistência na base de dados
	 * ou não encontrar a chave primária, ou concluir que a alteração em questão não se refere
	 * ao mesmo objeto.
	 */
	public default E update(E entidade) throws HarryPotterServiceException{
		log.debug("update method called");
		Optional<E> beforeOptionalEntity = byId((S) entidade.getId());
		if(beforeOptionalEntity.isPresent() && beforeOptionalEntity.get().getId().equals(entidade.getId())) {
			return save(entidade);
			
		}else {
			throw new HarryPotterServiceException(OBJETONAOENCONTRADO);
		}
		
	}

	
}