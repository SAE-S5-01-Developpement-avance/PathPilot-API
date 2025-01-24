/*
 * ClientResponseModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client.modelAssembler;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientRestController;
import fr.iut.pathpilotapi.client.dto.ClientResponseModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientResponseModelAssembler extends RepresentationModelAssemblerSupport<Client, ClientResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public ClientResponseModelAssembler(ModelMapper modelMapper) {
        super(ClientRestController.class, ClientResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public ClientResponseModel toModel(Client entity) {
        ClientResponseModel clientResponseModel = modelMapper.map(entity, ClientResponseModel.class);

        clientResponseModel.add(
                linkTo(
                        methodOn(ClientRestController.class).getClientById(entity.getId())
                ).withSelfRel()
        );

        clientResponseModel.add(
                linkTo(
                        methodOn(ClientRestController.class).deleteClient(entity.getId())
                ).withRel("delete")
        );

        return clientResponseModel;
    }
}