package com.example.liquidwallpapers.ui.screens.details;

import com.example.liquidwallpapers.data.repository.WallpaperRepository;
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
public final class DetailViewModel_Factory implements Factory<DetailViewModel> {
  private final Provider<WallpaperRepository> repositoryProvider;

  public DetailViewModel_Factory(Provider<WallpaperRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DetailViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DetailViewModel_Factory create(Provider<WallpaperRepository> repositoryProvider) {
    return new DetailViewModel_Factory(repositoryProvider);
  }

  public static DetailViewModel newInstance(WallpaperRepository repository) {
    return new DetailViewModel(repository);
  }
}
