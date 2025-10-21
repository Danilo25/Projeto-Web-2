package br.com.ufrn.imd.Project_Manager.dtos;

public record AddressRequest(
        String publicPlace, 
        String district,   
        String city,    
        String state,      
        String zipCode,    
        Long userId         
) {}
