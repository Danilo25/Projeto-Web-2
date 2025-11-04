package br.com.ufrn.imd.Project_Manager.dtos.api;

public record AddressRequest(
        String publicPlace, 
        String district,   
        String city,    
        String state,      
        String zipCode    
) {}
