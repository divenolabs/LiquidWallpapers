package com.example.liquidwallpapers.di;

import com.example.liquidwallpapers.data.local.WallpaperDao;
import com.example.liquidwallpapers.data.remote.UnsplashApi;
import com.example.liquidwallpapers.data.repository.WallpaperRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideRepositoryFactory implements Factory<WallpaperRepository> {
  private final Provider<UnsplashApi> apiProvider;

  private final Provider<WallpaperDao> daoProvider;

  public AppModule_ProvideRepositoryFactory(Provider<UnsplashApi> apiProvider,
      Provider<WallpaperDao> daoProvider) {
    this.apiProvider = apiProvider;
    this.daoProvider = daoProvider;
  }

  @Override
  public WallpaperRepository get() {
    return provideRepository(apiProvider.get(), daoProvider.get());
  }

  public static AppModule_ProvideRepositoryFactory create(Provider<UnsplashApi> apiProvider,
      Provider<WallpaperDao> daoProvider) {
    return new AppModule_ProvideRepositoryFactory(apiProvider, daoProvider);
  }

  public static WallpaperRepository provideRepository(UnsplashApi api, WallpaperDao dao) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRepository(api, dao));
  }
}
