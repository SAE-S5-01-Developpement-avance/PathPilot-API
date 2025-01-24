/*
 * PagedResourcedModelAssembler.java                                 23 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.client.modelAssembler;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientRestController;
import fr.iut.pathpilotapi.client.dto.ClientResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

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

        // Add self link
        pagedModel.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ClientRestController.class)
                        .getAllClientsBySalesmanPageable(null, entities.getPageable())
        ).withSelfRel());

        // Add next page link if available
        if (entities.hasNext()) {
            pagedModel.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ClientRestController.class)
                            .getAllClientsBySalesmanPageable(null, entities.nextPageable())
            ).withRel("next"));
        }

        // Add previous page link if available
        if (entities.hasPrevious()) {
            pagedModel.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ClientRestController.class)
                            .getAllClientsBySalesmanPageable(null, entities.previousPageable())
            ).withRel("previous"));
        }

        return pagedModel;
    }
}

