/*
 * ItineraryPagedModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.dto;

import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryController;
import fr.iut.pathpilotapi.routes.RouteController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A Spring component that assembles an ItineraryResponseModel from an Itinerary entity.
 * It extends RepresentationModelAssemblerSupport to provide HATEOAS links.
 */
@Component
public class ItineraryResponseModelAssembler extends RepresentationModelAssemblerSupport<Itinerary, ItineraryResponseModel> {

    private final ModelMapper modelMapper;

    @Autowired
    public ItineraryResponseModelAssembler(ModelMapper modelMapper) {
        super(RouteController.class, ItineraryResponseModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public ItineraryResponseModel toModel(Itinerary entity) {
        ItineraryResponseModel itineraryResponseModel = modelMapper.map(entity, ItineraryResponseModel.class);

        itineraryResponseModel.add(
                linkTo(
                        methodOn(ItineraryController.class).getItinerary(entity.getId())
                ).withSelfRel()
        );

        itineraryResponseModel.add(
                linkTo(
                        methodOn(ItineraryController.class).deleteItinerary(entity.getId())
                ).withSelfRel()
        );

        return itineraryResponseModel;
    }
}