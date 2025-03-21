/*
 * PagedResourcedModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.dto;

import fr.iut.pathpilotapi.clients.entity.Client;
import fr.iut.pathpilotapi.clients.ClientController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * A Spring component that assembles a paged model of ClientResponseModel objects
 * from a Page of Client entities.
 */
@Component
public class ClientPagedModelAssembler implements RepresentationModelAssembler<Page<Client>, PagedModel<ClientResponseModel>> {

    private final ClientResponseModelAssembler clientResponseModelAssembler;

    public ClientPagedModelAssembler(ClientResponseModelAssembler clientResponseModelAssembler) {
        this.clientResponseModelAssembler = clientResponseModelAssembler;
    }

    @Override
    public PagedModel<ClientResponseModel> toModel(Page<Client> entities) {
        PagedModel<ClientResponseModel> pagedModel = PagedModel.of(
                entities.map(clientResponseModelAssembler::toModel).getContent(),
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
                        WebMvcLinkBuilder.methodOn(ClientController.class)
                                .getAllClientsBySalesmanPageable(pageable)
                ).toUriComponentsBuilder()
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build()
                .toUriString();

        return Link.of(uri, rel);
    }
}

