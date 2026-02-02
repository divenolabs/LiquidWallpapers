package com.example.liquidwallpapers.di;

import com.example.liquidwallpapers.data.remote.UnsplashApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideUnsplashApiFactory implements Factory<UnsplashApi> {
  @Override
  public UnsplashApi get() {
    return provideUnsplashApi();
  }

  public static AppModule_ProvideUnsplashApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UnsplashApi provideUnsplashApi() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUnsplashApi());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideUnsplashApiFactory INSTANCE = new AppModule_ProvideUnsplashApiFactory();
  }
}
