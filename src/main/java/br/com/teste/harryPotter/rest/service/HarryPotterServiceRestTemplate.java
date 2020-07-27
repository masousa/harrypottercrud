package br.com.teste.harryPotter.rest.service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import br.com.teste.harryPotter.exception.HarryPotterServiceException;
import br.com.teste.harryPotter.rest.domain.HarryPotterHouse;
import br.com.teste.harryPotter.utils.Utils;

@Component
public class HarryPotterServiceRestTemplate {

	Logger logger = LoggerFactory.getLogger(HarryPotterServiceRestTemplate.class);
	private final RestTemplate restTemplate;

	@Autowired
	public HarryPotterServiceRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/***
	 * Método que realiza uma consulta no endereço www.potterapi.com a partir do  identificador
	 * de uma casa.
	 * 
	 * Caso não seja informado algum destes valores um erro de objeto não encontrato é informado.
	 * @param value identificador de uma casa a partir da consulta de /house/houseId consultando a api www.potterapi.com
	 * @return um objeto {@link HarryPotterHouse} que representa uma casa do harrypotter obtida a partir do identificador a ser consultado na api www.potterapi.com 
	 * @throws HarryPotterServiceException
	 */
	public HarryPotterHouse getKeyFromHouseValue(String value) throws HarryPotterServiceException {
		
		ResponseEntity<String> response = restTemplate.exchange(MessageFormat.format(
				Utils.message("app.harrypotter.house.id"), value, Utils.message("harrypotter.key")), HttpMethod.GET,genaratedHttpEntity(String.class),String.class);

		logger.debug(response.getBody());
		Gson gson = new Gson();
		HarryPotterHouse hph = gson.fromJson(response.getBody(), HarryPotterHouse.class);
		if (null == hph.getMessage() || !Strings.isNotBlank(hph.getMessage())) {

			throw new HarryPotterServiceException(Utils.message("mensagem.erro.objeto.nao.encontrado"));
		} else {
			return hph;
		}

	}

	/****
	 * Lista todas as casas do HarryPotter consultando a api www.potterapi.com
	 * 
	 * @return uma lista de {@link HarryPotterHouse} com todas as casas da api www.potterapi.com
	 * @throws HarryPotterServiceException Caso exista erros na conexão com a api.
	 */
	public List<HarryPotterHouse> getHouses() throws HarryPotterServiceException {


		ResponseEntity<String> response = restTemplate.exchange(
				MessageFormat.format(Utils.message("app.harrypotter.houses"), Utils.message("harrypotter.key")),
				HttpMethod.GET, genaratedHttpEntity(String.class), String.class);

		logger.debug(response.getBody());
		Gson gson = new Gson();

		return Arrays.asList(gson.fromJson(response.getBody(), HarryPotterHouse[].class));

	}
	
	/***
	 * Método que retorna o identificador de uma casa a partir do nome da casa informada
	 * Valores possíveis Gryffindor, Ravenclaw, Slytherin, Hufflepuff.
	 * Caso seja informada uma casa de nome não contido nos valores possíveis uma exceção é
	 * dispararada {@linkplain HarryPotterServiceException} informando os valores possíveis para a inserção.
	 * @param value
	 * @return um identificador da casa passada como parametro
	 * @throws HarryPotterServiceException
	 */
	public String getKeyFromHouse(String value) throws HarryPotterServiceException {
		List<HarryPotterHouse> houses = getHouses();
		HarryPotterHouse house = houses.stream()
				.filter(h -> h.getName().equalsIgnoreCase(value) || h.get_id().equals(value)).findAny().orElse(null);
		if (null != house) {
			return house.get_id();
		}
		throw new HarryPotterServiceException(Utils.message("error.harrypotter.houses"));

	}

	/***
	 * Método privado que encapsula o cabeçalho para requisição de uma consulta externa à aplicação.
	 * @param s classe a ser utilizada como parametro a ser passado na conexão
	 * @return um objeto httpentity responsável por fazer uma conexão fora da aplicação
	 */
	@SuppressWarnings("rawtypes")
	private <S> HttpEntity  genaratedHttpEntity(Class<S> s) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		requestHeaders.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		
		return new HttpEntity<String>("parameters", requestHeaders);
	}

	

}
