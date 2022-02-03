package com.luiz.crudCli.services;

import com.luiz.crudCli.dto.ClientDto;
import com.luiz.crudCli.entities.Client;
import com.luiz.crudCli.repositories.ClientRepository;
import com.luiz.crudCli.services.execptions.DatabaseException;
import com.luiz.crudCli.services.execptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    @Autowired
    private ClientRepository repo;

    @Transactional(readOnly = true)
    public Page<ClientDto> findAllPaged(PageRequest pageRequest) {
        return repo.findAll(pageRequest).map(cli -> new ClientDto(cli));
    }

    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        Client cli = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ClientDto(cli);
    }

    @Transactional
    public ClientDto insert(ClientDto dto) {
        Client obj = new Client();
        parseClientDtoToClient(dto, obj);
        obj = repo.save(obj);
        return new ClientDto(obj);
    }

    @Transactional
    public ClientDto update(Long id, ClientDto dto) {
        try {
            Client obj = repo.getById(id);
            parseClientDtoToClient(dto, obj);
            obj = repo.save(obj);
            return new ClientDto(obj);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void parseClientDtoToClient(ClientDto dto, Client obj) {
        obj.setName(dto.getName());
        obj.setCpf(dto.getCpf());
        obj.setIncome(dto.getIncome());
        obj.setBirthDate(dto.getBirthDate());
        obj.setChildren(dto.getChildren());
    }
}
