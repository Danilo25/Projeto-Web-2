package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.AddressRequest;
import br.com.ufrn.imd.Project_Manager.dtos.AddressResponse;
import br.com.ufrn.imd.Project_Manager.model.Address;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.AddressRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Optional;

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
    public AddressResponse getAddressByUserId(Long userId) {
        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Address not found for user ID: " + userId)); // Use exceção customizada
        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse createAddressForUser(AddressRequest addressRequest) {
        if (addressRequest.userId() == null) {
            throw new IllegalArgumentException("User ID cannot be null when creating an address.");
        }

        if (addressRepository.findByUserId(addressRequest.userId()).isPresent()) {
            throw new RuntimeException("User already has an address.");
        }

        User user = userRepository.findById(addressRequest.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + addressRequest.userId()));

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
    public AddressResponse updateAddress(Long addressId, AddressRequest addressRequest) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));

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

        if (addressRequest.userId() != null && !addressRequest.userId().equals(existingAddress.getUser().getId())) {
             User newUser = userRepository.findById(addressRequest.userId())
                .orElseThrow(() -> new RuntimeException("New User not found with ID: " + addressRequest.userId()));
             if (addressRepository.findByUserId(addressRequest.userId()).isPresent()) {
                 throw new RuntimeException("The new user specified already has an address.");
             }
             existingAddress.setUser(newUser);
        }

        Address updatedAddress = addressRepository.save(existingAddress);
        return toAddressResponse(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));
        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public Optional<AddressResponse> findAddressResponseByUserId(Long userId) {
        // Usa o método findByUserId do repositório
        return addressRepository.findByUserId(userId) 
                .map(this::toAddressResponse); // Converte para AddressResponse se existir
    }

    @Transactional
    public AddressResponse createOrUpdateAddressForUser(Long userId, AddressRequest addressRequest) {
        // Verifica se já existe um endereço para este usuário
        Optional<Address> existingAddressOpt = addressRepository.findByUserId(userId);

        if (existingAddressOpt.isPresent()) {
            // Se existe, atualiza
            Address existingAddress = existingAddressOpt.get();
            
            existingAddress.setPublicPlace(addressRequest.publicPlace());
            existingAddress.setDistrict(addressRequest.district());
            existingAddress.setCity(addressRequest.city());
            existingAddress.setState(addressRequest.state());
            existingAddress.setZipCode(addressRequest.zipCode());
            
            Address updatedAddress = addressRepository.save(existingAddress);
            return toAddressResponse(updatedAddress);
        } else {
            // Se não existe, cria um novo (reutilizando a lógica de createAddressForUser)
            // Garante que o ID do usuário no request está correto
            AddressRequest requestWithUserId = new AddressRequest(
                addressRequest.publicPlace(),
                addressRequest.district(),
                addressRequest.city(),
                addressRequest.state(),
                addressRequest.zipCode(),
                userId // Garante o ID do usuário
            );
            return createAddressForUser(requestWithUserId);
        }
    }
}
