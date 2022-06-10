package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

// para testar camada de serviço utilize essa notação, que carrega o contexto com os recursos do Spring boot
@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {
		
	@InjectMocks
	private ClientService servico;
	
	@Mock
	private ClientRepository rep; 
	
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
		Long idExistente = 1l;
		Mockito.doNothing().when(rep).deleteById(idExistente);
		Assertions.assertDoesNotThrow(()->{servico.delete(idExistente);});
		Mockito.verify(rep, Mockito.times(1)).deleteById(idExistente);
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
		Mockito.doThrow(EmptyResultDataAccessException.class).when(rep).deleteById(idNaoExistente);
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{servico.delete(idNaoExistente);});
		Mockito.verify(rep, Mockito.times(1)).deleteById(idNaoExistente);
	}	

	////////////////////////////////////////// findAllPaged

	@Test
	public void testarRetornaPaginaComTodosClientes() {
		PageRequest pageRequest = PageRequest.of(0, 4, Direction.valueOf("ASC"), "name");

		List <Client> lista = new ArrayList<Client>();

		lista.add(new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0));
		lista.add(new Client(2L, "Lázaro Ramos", "10619244881", 3800.0, Instant.parse("1996-12-23T07:00:00Z"), 2));
		lista.add(new Client(3L, "Clarice Lispector", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0));
		lista.add(new Client(4L, "Carolina Maria de Jesus", "10114274861", 1500.0, Instant.parse("1956-09-23T07:00:00Z"), 0));

		Page<Client> pag = new PageImpl<>(lista, pageRequest, 1);
		Mockito.when(rep.findAll(pageRequest)).thenReturn(pag);
		Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(4, resultado.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(rep, Mockito.times(1)).findAll(pageRequest);
	}	

	////////////////////////////////////////// findByIncome
	// findByIncome deveria retornar uma página com os clientes que tenham o Income informado (e chamar o método findByIncome do repository)
	@Test
	public void testarRetornaPaginaComClientesComIncomeInformado() {
		PageRequest pageRequest = PageRequest.of(0, 3, Direction.valueOf("ASC"), "name");
		Double entrada = 1500.0;
		List <Client> lista = new ArrayList<Client>();

		lista.add(new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0));
		lista.add(new Client(8L, "Yuval Noah Harari", "10619244881", 1500.0, Instant.parse("1956-09-23T07:00:00Z"), 0));
		lista.add(new Client(9L, "Chimamanda Adichie", "10114274861", 1500.0, Instant.parse("1956-09-23T07:00:00Z"), 0));

		Page<Client> pag = new PageImpl<>(lista, pageRequest, 1);
		Mockito.when(rep.findByIncome(entrada, pageRequest)).thenReturn(pag);
		Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertEquals(3, resultado.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
		}
		Mockito.verify(rep, Mockito.times(1)).findByIncome(entrada, pageRequest);
	}	

	///////////////////////////////////////// findById deveria
    // ◦ retornar um ClientDTO quando o id existir
	@Test
	public void testarRetornaClientDTOQuandoIdExiste() {
		Long id = 1l;
		Optional<Client> cliente = Optional.of(new Client());

		Mockito.when(rep.findById(id)).thenReturn(cliente);
		ClientDTO resultadoCliente = servico.findById(id);
		Assertions.assertNotNull(resultadoCliente);
		Assertions.assertEquals(cliente.get(), resultadoCliente.toEntity());
		Mockito.verify(rep, Mockito.times(1)).findById(id);
	}	

    // ◦ lançar ResourceNotFoundException quando o id não existir

	@Test
	public void testarRetornaExceptionQuandoIdNaoExiste() {
		Long idNaoExistente = 20L;

		Mockito.doThrow(ResourceNotFoundException.class).when(rep).findById(idNaoExistente);
		Assertions.assertThrows(ResourceNotFoundException.class, ()->servico.findById(idNaoExistente));
		Mockito.verify(rep, Mockito.times(1)).findById(idNaoExistente);
	}	

	///////////////////////////////////////// update deveria
	// ◦ retornar um ClientDTO quando o id existir

	@Test
		public void testarUpdateRetornaClientDtoQuandoIdExiste() {
			//Cenario de teste
			Long id = 1l;
			Client clienteOriginal = new Client(1L, "Conceição Evaristo", "10619244881", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);
			Mockito.when(rep.getOne(id)).thenReturn(clienteOriginal);

			Client clienteAlterado = new Client(1L, "Conceição Evaristo", "00000000000", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);
			Mockito.when(rep.save(clienteAlterado)).thenReturn(clienteAlterado);

			ClientDTO clienteDTO = servico.update(1L, new ClientDTO(clienteAlterado));

			Assertions.assertEquals(clienteAlterado, clienteDTO.toEntity());
			Mockito.verify(rep, Mockito.times(1)).getOne(id);
			Mockito.verify(rep, Mockito.times(1)).save(clienteAlterado);
		}

		// ◦ lançar uma ResourceNotFoundException quando o id não existir
		@Test
		public void testarUpdateRetornaExceptionIdNaoExiste() {
			Long idNaoExistente = 20l;
			Mockito.doThrow(ResourceNotFoundException.class).when(rep).getOne(idNaoExistente);

			Client clienteAlterado = new Client(13L, "Phabliny", "00000000000", 1500.0, Instant.parse("2020-07-13T20:50:00Z"), 0);
			Mockito.when(rep.save(clienteAlterado)).thenReturn(clienteAlterado);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(idNaoExistente, new ClientDTO(clienteAlterado)));

			Mockito.verify(rep, Mockito.times(1)).getOne(idNaoExistente);
			Mockito.verify(rep, Mockito.times(0)).save(clienteAlterado);
		}

	////////////////////////////////////// insert 
	// ◦ deveria retornar um ClientDTO ao inserir um novo cliente
    @Test
		public void testRetornarUmClientDTOInserirNovoCliente() {
			Client cliente = new Client(13L, "Phabliny", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1);
			Mockito.when(rep.save(cliente)).thenReturn(cliente);

			ClientDTO dto = servico.insert(new ClientDTO(cliente));

			Assertions.assertEquals(dto.toEntity(), cliente);
			Mockito.verify(rep, Mockito.times(1)).save(dto.toEntity());
		}
}