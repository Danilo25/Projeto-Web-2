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

    public Page<AddressResponse> searchAddresses(String city, String state, String zipCode, String district, String publicPlace, Long userId, Pageable pageable) {
        Page<Address> addresses = addressRepository.searchAddresses(city, state, zipCode, district, publicPlace, userId, pageable);
        return addresses.map(this::toAddressResponse);
    }

    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado!"));
        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest addressRequest) {
        User user = userRepository.findById(addressRequest.userId())
                .orElseThrow(() -> new RuntimeException("Usuário com ID '" + addressRequest.userId() + "' não encontrado."));

        if (addressRepository.findByUserId(addressRequest.userId()).isPresent()) {
            throw new RuntimeException("Conflito: O usuário com ID '" + addressRequest.userId() + "' já possui um endereço cadastrado.");
        }

        boolean addressAlreadyExists = addressRepository.existsAddress(
                addressRequest.publicPlace(),
                addressRequest.city(),
                addressRequest.state(),
                addressRequest.zipCode()
        );
        if (addressAlreadyExists) {
             throw new RuntimeException("Conflito: Um endereço com os mesmos dados (logradouro, cidade, estado, CEP) já existe.");
        }
        Address newAddress = new Address(
                addressRequest.publicPlace(),
                addressRequest.district(),
                addressRequest.city(),
                addressRequest.state(),
                addressRequest.zipCode(),
                user
        );

        Address savedAddress = addressRepository.save(newAddress);
        return toAddressResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest addressRequest) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço com ID '" + id + "' não encontrado."));

        if (addressRequest.userId() == null || !addressRequest.userId().equals(existingAddress.getUser().getId())) {
            throw new IllegalArgumentException("Erro de autorização: O ID do usuário na requisição não corresponde ao dono do endereço.");
        }

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

    @Transactional
    public void deleteAddress(Long id) {
         Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço com ID '" + id + "' não encontrado."));
         addressRepository.delete(address);
    }


}
