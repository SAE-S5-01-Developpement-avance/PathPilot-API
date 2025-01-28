/*
 * ClientResponseModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.routes.modelAssembler;

import fr.iut.pathpilotapi.itineraries.routes.Route;
import fr.iut.pathpilotapi.itineraries.routes.RouteRestController;
import fr.iut.pathpilotapi.itineraries.routes.dto.RouteResponseModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RouteResponseModelAssembler extends RepresentationModelAssemblerSupport<Route, RouteResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public RouteResponseModelAssembler(ModelMapper modelMapper) {
        super(RouteRestController.class, RouteResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public RouteResponseModel toModel(Route entity) {
        RouteResponseModel routeResponseModel = modelMapper.map(entity, RouteResponseModel.class);

        routeResponseModel.add(
                linkTo(
                        methodOn(RouteRestController.class).getRoute(entity.getId())
                ).withSelfRel()
        );

        return routeResponseModel;
    }
}