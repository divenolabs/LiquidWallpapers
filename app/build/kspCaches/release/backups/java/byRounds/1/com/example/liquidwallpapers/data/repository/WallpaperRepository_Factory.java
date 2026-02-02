package com.example.liquidwallpapers.data.repository;

import com.example.liquidwallpapers.data.local.WallpaperDao;
import com.example.liquidwallpapers.data.remote.UnsplashApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class WallpaperRepository_Factory implements Factory<WallpaperRepository> {
  private final Provider<UnsplashApi> apiProvider;

  private final Provider<WallpaperDao> daoProvider;

  public WallpaperRepository_Factory(Provider<UnsplashApi> apiProvider,
      Provider<WallpaperDao> daoProvider) {
    this.apiProvider = apiProvider;
    this.daoProvider = daoProvider;
  }

  @Override
  public WallpaperRepository get() {
    return newInstance(apiProvider.get(), daoProvider.get());
  }

  public static WallpaperRepository_Factory create(Provider<UnsplashApi> apiProvider,
      Provider<WallpaperDao> daoProvider) {
    return new WallpaperRepository_Factory(apiProvider, daoProvider);
  }

  public static WallpaperRepository newInstance(UnsplashApi api, WallpaperDao dao) {
    return new WallpaperRepository(api, dao);
  }
}
