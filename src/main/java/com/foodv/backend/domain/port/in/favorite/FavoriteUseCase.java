package com.foodv.backend.domain.port.in.favorite;

import com.foodv.backend.domain.model.favorite.FavoriteProduct;
import com.foodv.backend.domain.model.favorite.FavoriteStore;
import com.foodv.backend.domain.model.product.Product;
import com.foodv.backend.domain.model.store.Store;

import java.util.List;

public interface FavoriteUseCase {

    FavoriteProduct addProductFavorite(Long userId, Long productId);

    void removeProductFavorite(Long userId, Long productId);

    List<Product> findFavoriteProducts(Long userId);

    boolean isProductFavorite(Long userId, Long productId);

    FavoriteStore addStoreFavorite(Long userId, Long storeId);

    void removeStoreFavorite(Long userId, Long storeId);

    List<Store> findFavoriteStores(Long userId);

    boolean isStoreFavorite(Long userId, Long storeId);
}
