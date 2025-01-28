/*
 * PagedResourcedModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.routes.modelAssembler;

import fr.iut.pathpilotapi.client.ClientRestController;
import fr.iut.pathpilotapi.itineraries.routes.Route;
import fr.iut.pathpilotapi.itineraries.routes.dto.RouteResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

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
                        WebMvcLinkBuilder.methodOn(ClientRestController.class)
                                .getAllClientsBySalesmanPageable(pageable)
                ).toUriComponentsBuilder()
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build()
                .toUriString();

        return Link.of(uri, rel);
    }
}

