package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.ClientRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.ClientResponse;
import br.com.ufrn.imd.Project_Manager.model.Client;
import br.com.ufrn.imd.Project_Manager.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    private ClientResponse toClientResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getCompany(),
                client.getEmail(),
                client.getPhoneNumber()
        );
    }

    public Page<ClientResponse> searchClients(String name, String company, String email, Pageable pageable) {
        Page<Client> clientsPage = clientRepository.searchClients(name, company, email, pageable);
        return clientsPage.map(this::toClientResponse);
    }

    public ClientResponse getClientById(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found!"));
        return toClientResponse(client);
    }

    @Transactional
    public ClientResponse createClient(ClientRequest clientRequest) {
        if ((clientRequest.name() == null || clientRequest.name().isEmpty())
        || (clientRequest.company() == null || clientRequest.company().isEmpty())
        || (clientRequest.email() == null || clientRequest.email().isEmpty())) {
            throw new RuntimeException("Nome, empresa ou email do clientRequeste não podem ser vazios.");
        }

        Client existsClient = clientRepository.findByNameAndCompanyAndEmailAllIgnoreCase(
                clientRequest.name(), clientRequest.company(), clientRequest.email())
                .orElse(null);

        if (existsClient != null) {
            throw new RuntimeException("Conflito: Cliente com o mesmo nome, empresa e email já existe.");
        }

        Client client = new Client(
                clientRequest.name(),
                clientRequest.company(),
                clientRequest.email(),
                clientRequest.phoneNumber()
        );

        Client savedClient = clientRepository.save(client);
        return toClientResponse(savedClient);
    }

    @Transactional
    public ClientResponse updateClient(Long clientId, ClientRequest clientRequest) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found!"));

        Client existsClient = clientRepository.findByNameAndCompanyAndEmailAllIgnoreCase(
                clientRequest.name(), clientRequest.company(), clientRequest.email())
                .orElse(null);

        if (existsClient != null && !existsClient.getId().equals(clientId)) {
            throw new RuntimeException("Conflito: Cliente com o mesmo nome, empresa e email já existe.");
        }

        if (clientRequest.name() != null) {
            client.setName(clientRequest.name());
        }
        if (clientRequest.company() != null) {
            client.setCompany(clientRequest.company());
        }
        if (clientRequest.email() != null) {
            client.setEmail(clientRequest.email());
        }


        if (clientRequest.phoneNumber() != null) {
            client.setPhoneNumber(clientRequest.phoneNumber());
        }

        Client updatedClient = clientRepository.save(client);
        return toClientResponse(updatedClient);
    }

    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found!"));
        clientRepository.delete(client);
    }

}
