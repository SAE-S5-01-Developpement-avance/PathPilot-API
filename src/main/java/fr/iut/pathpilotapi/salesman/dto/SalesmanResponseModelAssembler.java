/*
 * ClientResponseModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.salesman.dto;

import fr.iut.pathpilotapi.auth.controllers.AuthenticationController;
import fr.iut.pathpilotapi.auth.dto.LoginUserRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SalesmanResponseModelAssembler extends RepresentationModelAssemblerSupport<Salesman, SalesmanResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public SalesmanResponseModelAssembler(ModelMapper modelMapper) {
        super(AuthenticationController.class, SalesmanResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public SalesmanResponseModel toModel(Salesman entity) {
        SalesmanResponseModel salesmanResponseModel = modelMapper.map(entity, SalesmanResponseModel.class);

        salesmanResponseModel.add(
                linkTo(
                        methodOn(AuthenticationController.class).authenticate(new LoginUserRequestModel(entity.getEmailAddress(), entity.getPassword()))
                ).withSelfRel()
        );

        return salesmanResponseModel;
    }
}