/*
 * RoutePagedModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.routes.Route;
import fr.iut.pathpilotapi.routes.RouteController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * A Spring component that assembles a PagedModel of RouteResponseModel from a Page of Route entities.
 * It extends RepresentationModelAssembler to provide HATEOAS links.
 */
@Component
public class RoutePagedModelAssembler implements RepresentationModelAssembler<Page<Route>, PagedModel<RouteResponseModel>> {

    private final RouteResponseModelAssembler routeResponseModelAssembler;

    public RoutePagedModelAssembler(RouteResponseModelAssembler routeResponseModelAssembler) {
        this.routeResponseModelAssembler = routeResponseModelAssembler;
    }

    @Override
    public PagedModel<RouteResponseModel> toModel(Page<Route> entities) {
        PagedModel<RouteResponseModel> pagedModel = PagedModel.of(
                entities.map(routeResponseModelAssembler::toModel).getContent(),
                new PagedModel.PageMetadata(
                        entities.getSize(),
                        entities.getNumber(),
                        entities.getTotalElements(),
                        entities.getTotalPages()
                )
        );

        // Add self link with pagination parameters
        pagedModel.add(buildPageLink(entities.getPageable(), "self"));

        // Add next page link if available, with pagination parameters
        if (entities.hasNext()) {
            pagedModel.add(buildPageLink(entities.nextPageable(), "next"));
        }

        // Add previous page link if available, with pagination parameters
        if (entities.hasPrevious()) {
            pagedModel.add(buildPageLink(entities.previousPageable(), "previous"));
        }

        return pagedModel;
    }

    private Link buildPageLink(Pageable pageable, String rel) {
        // Build the URI with pagination parameters explicitly
        String uri = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(RouteController.class)
                                .getRoutesFromSalesman(pageable)
                ).toUriComponentsBuilder()
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build()
                .toUriString();

        return Link.of(uri, rel);
    }
}

