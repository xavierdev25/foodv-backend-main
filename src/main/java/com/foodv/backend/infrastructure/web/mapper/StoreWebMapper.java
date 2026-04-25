package com.foodv.backend.infrastructure.web.mapper;

import com.foodv.backend.domain.model.store.Store;
import com.foodv.backend.domain.port.in.store.CreateStoreUseCase;
import com.foodv.backend.domain.port.in.store.UpdateStoreUseCase;
import com.foodv.backend.infrastructure.web.dto.store.CreateStoreRequest;
import com.foodv.backend.infrastructure.web.dto.store.StoreResponse;
import com.foodv.backend.infrastructure.web.dto.store.UpdateStoreRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreWebMapper {

    StoreResponse toResponse(Store store);

    CreateStoreUseCase.CreateStoreCommand toCommand(CreateStoreRequest request);

    UpdateStoreUseCase.UpdateStoreCommand toCommand(UpdateStoreRequest request);
}
