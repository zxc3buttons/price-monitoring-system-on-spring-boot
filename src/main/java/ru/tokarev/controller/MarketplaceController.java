package ru.tokarev.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.ApiErrorDto;
import ru.tokarev.dto.MarketplaceDto;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.service.marketplaceservice.MarketPlaceService;
import ru.tokarev.utils.MapperUtil;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/api/marketplaces")
@RestController
public class MarketplaceController {

    private final MarketPlaceService marketPlaceService;

    private final ModelMapper modelMapper;

    @Autowired
    public MarketplaceController(MarketPlaceService marketPlaceService, ModelMapper modelMapper) {
        this.marketPlaceService = marketPlaceService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<MarketplaceDto>> getAll() {

        List<Marketplace> marketplaceList = marketPlaceService.getAll();
        List<MarketplaceDto> marketplaceDtoList = MapperUtil.
                convertList(marketplaceList, this::convertToMarketPlaceDto);

        log.info("Response for GET request /marketplaces with data {}", marketplaceDtoList);
        for (MarketplaceDto marketPlaceDto : marketplaceDtoList) {
            log.info("id {}, name {}", marketPlaceDto.getId(), marketPlaceDto.getName());
        }

        return new ResponseEntity<>(marketplaceDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<MarketplaceDto> getById(@PathVariable Long id) {

        log.info("GET request /marketplaces/{} with data {}", id, id);

        Marketplace marketPlace = marketPlaceService.getById(id);
        MarketplaceDto marketPlaceDto = convertToMarketPlaceDto(marketPlace);

        log.info("Response for GET request /marketplaces with data: id {}, name {}",
                marketPlaceDto.getId(), marketPlaceDto.getName());

        return new ResponseEntity<>(marketPlaceDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<MarketplaceDto> createMarketPlace(@Valid @RequestBody MarketplaceDto marketPlaceDto) {

        log.info("POST request /marketplaces with data: name {}", marketPlaceDto.getName());

        Marketplace marketPlace = convertToMarketPlaceEntity(marketPlaceDto);
        Marketplace createdMarketplace = marketPlaceService.createMarketPlace(marketPlace);
        MarketplaceDto createdMarketplaceDto = convertToMarketPlaceDto(createdMarketplace);

        log.info("Response for POST request /marketplaces with data: id {}, name {}",
                createdMarketplaceDto.getId(), createdMarketplaceDto.getName());

        return new ResponseEntity<>(createdMarketplaceDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<List<MarketplaceDto>> createMarketplaces(
            @Valid @RequestBody List<MarketplaceDto> marketplaceDtoList) {

        log.info("POST request /marketplaces with data {}", marketplaceDtoList);
        for (MarketplaceDto marketPlaceDto : marketplaceDtoList) {
            log.info("name {}", marketPlaceDto.getName());
        }

        List<Marketplace> marketplaceList = MapperUtil.convertList(marketplaceDtoList, this::convertToMarketPlaceEntity);
        List<Marketplace> createdMarketplaceList = marketPlaceService.createMarketPlaces(marketplaceList);
        List<MarketplaceDto> createdMarketplaceDtoList = MapperUtil.convertList(
                createdMarketplaceList, this::convertToMarketPlaceDto);

        log.info("Response for POST request /marketplaces with data {}", createdMarketplaceDtoList);
        for (MarketplaceDto marketPlaceDto : createdMarketplaceDtoList) {
            log.info("id {}, name {}", marketPlaceDto.getId(), marketPlaceDto.getName());
        }

        return new ResponseEntity<>(createdMarketplaceDtoList, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<MarketplaceDto> updateMarketPlace(@PathVariable Long id,
                                                            @Valid @RequestBody MarketplaceDto marketPlaceDto) {

        log.info("PATCH request /marketplaces/{} with data: name {}", id, marketPlaceDto.getName());

        Marketplace marketPlace = convertToMarketPlaceEntity(marketPlaceDto);
        Marketplace updatedMarketplace = marketPlaceService.updateMarketPlace(id, marketPlace);
        MarketplaceDto updatedMarketplaceDto = convertToMarketPlaceDto(updatedMarketplace);

        log.info("Response for PATCH request /marketplaces/{} with data: name {}", id, marketPlaceDto.getName());

        return new ResponseEntity<>(updatedMarketplaceDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = MarketplaceDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public ResponseEntity<String> deleteMarketPlace(@PathVariable Long id) {

        log.info("DELETE request /marketplaces/{} with data {}", id, id);

        marketPlaceService.deleteMarketPlace(id);

        log.info("Response for DELETE request /marketplaces/{} with message {}", id,
                "Marketplace deleted successfully");

        return ResponseEntity.ok().body("Marketplace deleted successfully");
    }

    private MarketplaceDto convertToMarketPlaceDto(Marketplace marketPlace) {
        return modelMapper.map(marketPlace, MarketplaceDto.class);
    }

    private Marketplace convertToMarketPlaceEntity(MarketplaceDto marketPlaceDto) {
        return modelMapper.map(marketPlaceDto, Marketplace.class);
    }
}
