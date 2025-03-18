/*
 * RouteResponseModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.routes.Route;
import fr.iut.pathpilotapi.routes.RouteController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A Spring component that assembles a RouteResponseModel from a Route entity.
 * It extends RepresentationModelAssemblerSupport to provide HATEOAS links.
 */
@Component
public class RouteResponseModelAssembler extends RepresentationModelAssemblerSupport<Route, RouteResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public RouteResponseModelAssembler(ModelMapper modelMapper) {
        super(RouteController.class, RouteResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public RouteResponseModel toModel(Route entity) {
        RouteResponseModel routeResponseModel = modelMapper.map(entity, RouteResponseModel.class);

        routeResponseModel.add(
                linkTo(
                        methodOn(RouteController.class).getRoute(entity.getId())
                ).withSelfRel()
        );

        return routeResponseModel;
    }
}