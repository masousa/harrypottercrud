package br.com.teste.harryPotter.payload;

import org.bson.types.ObjectId;

import br.com.teste.harryPotter.domain.Entidade;
import br.com.teste.harryPotter.utils.BuilderTOObject;

public class BuilderTOEntityMongoDb <T extends Entidade , S extends EntidadeTO<T>> extends BuilderTOObject<T, S>{

	

	@Override
	protected Object convertTOIDMethod(Object value) {
		
		return new ObjectId(value.toString());
	}

	@Override
	protected Object convertIDMethodTO(Object value) {
		
		return ((ObjectId)value).toHexString();
	}
	

}
