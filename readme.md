# HarryPotter Crud

## Para Construção e execução do projeto
## Compilando a aplicação
   ``mvn clean install``
## Testando a aplicação
   Executar o comando:
     ```mvn test ```
   
   Obs: Para testar a aplicação é necessário configurar o application_test.properties que fica na pasta src/main/test/resources, informando o banco de dados a ser utilizado para os testes. Caso não queira verificar os testes basta adicionar: -Dmaven.test.skip=true a qualquer comando maven aqui informado.
## Fazendo a aplicação funcionar
   `` mvn spring-boot:run ``  
   Acessar a página principal do projeto  ``http://localhost:3000``
### DOCKER
#### Para executar o projeto utilizando docker
     Na raiz do projeto executar: 
     sudo docker build -t maven .
     sudo docker run --rm -it -p 3000:3000 maven:latest
## Acessando a aplicação
    Após a execução basta entrar na pagina principal: localhost:3000
    Lá existe um link que descreve os endpoints da aplicação, podendo também a realização destes
   
## EndPoints
### Cadastrar Personagem -> ``http://localhost:3000/characters/save``
    Exemplo: 
            {
             			 "house": "string",
             			 "name": "string",
             			 "patronus": "string",
             			 "role": "string",
			  		  "school": "string"
			}

### Remover Personagem -> ``http://localhost:3000/characters/delete``
    Exemplo: 
            {
	
			"id":"string"
			
		    }
### Encontrar usuário por email -> ``http://localhost:3000/characters/update``
    Exemplo: 
            {
             			 "house": "string",
             			 "id":string
             			 "name": "string",
             			 "patronus": "string",
             			 "role": "string",
			  		  "school": "string"
			}
### Listar todos os personagens -> ``http://localhost:3000/characters/all``


### Obter um único personagem -> ``http://localhost:3000/characters/byIdTO/{id}``
    Exemplo: 
            {
			
			"id":"string"
		    }
### Obter digito único opcional usuário -> ```http://localhost:3000/characters/find?house=identificador da casa```

## Gerando o JavaDOC
   Dentro da raiz do projeto executar: 
		``` mvn javadoc:javadoc```
		
