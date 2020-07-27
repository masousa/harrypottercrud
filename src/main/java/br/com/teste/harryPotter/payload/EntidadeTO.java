package br.com.teste.harryPotter.payload;

import br.com.teste.harryPotter.domain.Entidade;

public interface EntidadeTO <S extends Entidade> {
	public S build();
	
	public EntidadeTO<S> generateTO(S entidade); 
	
	
}
