package com.example.liquidwallpapers.di;

import com.example.liquidwallpapers.data.local.WallpaperDao;
import com.example.liquidwallpapers.data.local.WallpaperDatabase;
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
public final class AppModule_ProvideWallpaperDaoFactory implements Factory<WallpaperDao> {
  private final Provider<WallpaperDatabase> dbProvider;

  public AppModule_ProvideWallpaperDaoFactory(Provider<WallpaperDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WallpaperDao get() {
    return provideWallpaperDao(dbProvider.get());
  }

  public static AppModule_ProvideWallpaperDaoFactory create(
      Provider<WallpaperDatabase> dbProvider) {
    return new AppModule_ProvideWallpaperDaoFactory(dbProvider);
  }

  public static WallpaperDao provideWallpaperDao(WallpaperDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWallpaperDao(db));
  }
}
