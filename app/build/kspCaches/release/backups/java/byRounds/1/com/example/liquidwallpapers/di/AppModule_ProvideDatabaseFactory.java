package com.example.liquidwallpapers.di;

import android.app.Application;
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
public final class AppModule_ProvideDatabaseFactory implements Factory<WallpaperDatabase> {
  private final Provider<Application> appProvider;

  public AppModule_ProvideDatabaseFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public WallpaperDatabase get() {
    return provideDatabase(appProvider.get());
  }

  public static AppModule_ProvideDatabaseFactory create(Provider<Application> appProvider) {
    return new AppModule_ProvideDatabaseFactory(appProvider);
  }

  public static WallpaperDatabase provideDatabase(Application app) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDatabase(app));
  }
}
