package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.AddressRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.AddressResponse;
import br.com.ufrn.imd.Project_Manager.model.Address;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.AddressRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getPublicPlace(),
                address.getDistrict(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getUser() != null ? address.getUser().getId() : null
        );
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> searchAddresses(String city, String state, String zipCode, String district, String publicPlace, Pageable pageable) {
        Specification<Address> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(publicPlace)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("publicPlace")), "%" + publicPlace.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(district)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), "%" + district.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(city)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(state)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("state")), "%" + state.toLowerCase() + "%"));
            }
             if (StringUtils.hasText(zipCode)) {
                 predicates.add(criteriaBuilder.like(root.get("zipCode"), zipCode + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return addressRepository.findAll(spec, pageable).map(this::toAddressResponse);
    }

    @Transactional
    public AddressResponse createAddressForUser(AddressRequest addressRequest, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: Usuário autenticado com ID '" + currentUserId + "' não encontrado no banco de dados.")); // Mensagem mais clara

        if (addressRepository.findByUserId(currentUserId).isPresent()) {
            throw new RuntimeException("Conflito: Você (usuário ID: " + currentUserId + ") já possui um endereço cadastrado.");
        }

        boolean addressAlreadyExists = addressRepository.existsAdress(
                addressRequest.publicPlace(),
                addressRequest.city(),
                addressRequest.state(),
                addressRequest.zipCode()
        );
        if (addressAlreadyExists) {
             throw new RuntimeException("Conflito: Um endereço com os mesmos dados (logradouro, cidade, estado, CEP) já existe.");
        }
        Address newAddress = new Address();
        newAddress.setPublicPlace(addressRequest.publicPlace());
        newAddress.setDistrict(addressRequest.district());
        newAddress.setCity(addressRequest.city());
        newAddress.setState(addressRequest.state());
        newAddress.setZipCode(addressRequest.zipCode());
        newAddress.setUser(user);

        Address savedAddress = addressRepository.save(newAddress);
        return toAddressResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateMyAddress(Long currentUserId, AddressRequest addressRequest) {
        Address existingAddress = addressRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: Nenhum endereço encontrado para você (usuário ID: " + currentUserId + "). Crie um endereço primeiro."));

        if (StringUtils.hasText(addressRequest.publicPlace())) {
            existingAddress.setPublicPlace(addressRequest.publicPlace());
        }
        if (StringUtils.hasText(addressRequest.district())) {
            existingAddress.setDistrict(addressRequest.district());
        }
        if (StringUtils.hasText(addressRequest.city())) {
            existingAddress.setCity(addressRequest.city());
        }
        if (StringUtils.hasText(addressRequest.state())) {
            existingAddress.setState(addressRequest.state());
        }
        if (StringUtils.hasText(addressRequest.zipCode())) {
            existingAddress.setZipCode(addressRequest.zipCode());
        }
        Address updatedAddress = addressRepository.save(existingAddress);
        return toAddressResponse(updatedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponse getMyAddress(Long currentUserId) {
         Address address = addressRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: Nenhum endereço cadastrado para você (usuário ID: " + currentUserId + ").")); 
        return toAddressResponse(address);
    }
    
    @Transactional
    public void deleteMyAddress(Long currentUserId) {
         Address address = addressRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Não encontrado: Nenhum endereço cadastrado para você (usuário ID: " + currentUserId + ")."));
        addressRepository.delete(address);
    }


}
