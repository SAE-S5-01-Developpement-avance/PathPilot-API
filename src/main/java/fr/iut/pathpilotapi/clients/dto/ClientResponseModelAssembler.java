/*
 * ClientResponseModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.dto;

import fr.iut.pathpilotapi.clients.entity.Client;
import fr.iut.pathpilotapi.clients.ClientController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A Spring component that assembles a ClientResponseModel from a Client entity.
 * It extends RepresentationModelAssemblerSupport to provide HATEOAS links.
 */
@Component
public class ClientResponseModelAssembler extends RepresentationModelAssemblerSupport<Client, ClientResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public ClientResponseModelAssembler(ModelMapper modelMapper) {
        super(ClientController.class, ClientResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public ClientResponseModel toModel(Client entity) {
        ClientResponseModel clientResponseModel = modelMapper.map(entity, ClientResponseModel.class);

        clientResponseModel.add(
                linkTo(
                        methodOn(ClientController.class).getClientById(entity.getId())
                ).withSelfRel()
        );

        clientResponseModel.add(
                linkTo(
                        methodOn(ClientController.class).deleteClient(entity.getId())
                ).withRel("delete")
        );

        return clientResponseModel;
    }
}