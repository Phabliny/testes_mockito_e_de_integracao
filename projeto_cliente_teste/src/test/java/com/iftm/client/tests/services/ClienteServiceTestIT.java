package com.iftm.client.tests.services;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

// para testar camada de serviço utilize essa notação, que carrega o contexto com os recursos do Spring boot
@SpringBootTest
@Transactional
public class ClienteServiceTestIT {
	@Autowired
	private ClientService servico;
	
	////////////////////////////////////////// delete
	/**
	 * Atividade A6
	 * Cenário de Teste : retornar vazio quando o id existir
	 * Entrada:
	 * 		- idExistente: 2
	 * Resultado:
	 * 		- void
	 */

	@Test
	public void testarApagarRetornaNadaQuandoIDExiste() {
		Long idExistente = 2l;
		Assertions.assertDoesNotThrow(()->{servico.delete(idExistente);});
	}
	
	/**
	 * Atividade A6
	 * Cenário de Teste : lançar uma EmptyResultDataAccessException quando o id não existir
	 * Entrada:
	 * 		- idExistente: 1000
	 * Resultado:
	 * 		- ResourceNotFoundException
	 */
	@Test
	public void testarApagarRetornaExceptionQuandoIDNaoExiste() {
		Long idNaoExistente = 20l;
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{servico.delete(idNaoExistente);});
	}	

	////////////////////////////////////////// findAllPaged

	@Test
	public void testarRetornaPaginaComTodosClientes() {
		PageRequest pageRequest = PageRequest.of(0, 4, Direction.valueOf("ASC"), "name");
		Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(4, resultado.getNumberOfElements());
	}	

	////////////////////////////////////////// findByIncome
	// findByIncome deveria retornar uma página com os clientes que tenham o Income informado (e chamar o método findByIncome do repository)
	@Test
	public void testarRetornaPaginaComClientesComIncomeInformado() {
		PageRequest pageRequest = PageRequest.of(0, 3, Direction.valueOf("ASC"), "name");
		Double entrada = 1500.0;

		Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(3, resultado.getNumberOfElements());
	}	


	///////////////////////////////////////// findById deveria
    // ◦ retornar um ClientDTO quando o id existir
	@Test
	public void testarRetornaClientDTOQuandoIdExiste() {
		Long id = 1l;

		Client cliente =new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);

		ClientDTO resultadoCliente = servico.findById(id);
		Assertions.assertNotNull(resultadoCliente);
		Assertions.assertEquals(cliente, resultadoCliente.toEntity());
	}	

    // ◦ lançar ResourceNotFoundException quando o id não existir

	@Test
	public void testarRetornaExceptionQuandoIdNaoExiste() {
		Long idNaoExistente = 20l;

		Assertions.assertThrows(ResourceNotFoundException.class, ()->servico.findById(idNaoExistente));
	}	

	///////////////////////////////////////// update deveria
	// ◦ retornar um ClientDTO quando o id existir

	@Test
		public void testarUpdateRetornaClientDtoQuandoIdExiste() {
			Client clienteAlterado = new Client(1L, "Conceição Evaristo", "00000000000", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);

			ClientDTO clienteDTO = servico.update(1L, new ClientDTO(clienteAlterado));

			Assertions.assertEquals(clienteAlterado, clienteDTO.toEntity());
		}

		// ◦ lançar uma ResourceNotFoundException quando o id não existir
		@Test
		public void testarUpdateRetornaExceptionIdNaoExiste() {
			Long idNaoExistente = 20l;
			Client clienteAlterado = new Client(13L, "Phabliny", "00000000000", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);
			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(idNaoExistente, new ClientDTO(clienteAlterado)));
		}

	////////////////////////////////////// insert 
	// ◦ deveria retornar um ClientDTO ao inserir um novo cliente
    @Test
		public void testRetornarUmClientDTOInserirNovoCliente() {
			Client cliente = new Client(13L, "Phabliny", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			ClientDTO dto = servico.insert(new ClientDTO(cliente));
			Assertions.assertEquals(dto.toEntity(), cliente);
		}
}