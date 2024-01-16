package medina.juanantonio.moviebrowser.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import medina.juanantonio.moviebrowser.R
import medina.juanantonio.moviebrowser.data.repository.ItunesRepository
import medina.juanantonio.moviebrowser.data.repository.ItunesRepositoryImpl
import medina.juanantonio.moviebrowser.data.database.MovieBrowserDb
import medina.juanantonio.moviebrowser.data.database.sources.CacheMovieLocalSource
import medina.juanantonio.moviebrowser.data.database.sources.CacheMovieLocalSourceImpl
import medina.juanantonio.moviebrowser.data.database.sources.FavoritesLocalSource
import medina.juanantonio.moviebrowser.data.database.sources.FavoritesLocalSourceImpl
import medina.juanantonio.moviebrowser.data.network.APIService
import medina.juanantonio.moviebrowser.data.network.sources.ItunesRemoteSource
import medina.juanantonio.moviebrowser.data.network.sources.ItunesRemoteSourceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideApiService(
        @ApplicationContext context: Context
    ): APIService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.MINUTES)
            .writeTimeout(20, TimeUnit.MINUTES)
            .readTimeout(20, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.itunes_api_base_url))
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun provideItunesRemoteSource(
        @ApplicationContext context: Context,
        apiService: APIService,
        dispatchers: CoroutineDispatchers
    ): ItunesRemoteSource {
        return ItunesRemoteSourceImpl(context, apiService, dispatchers)
    }

    @Provides
    @Singleton
    fun provideItunesRepository(
        remoteSource: ItunesRemoteSource,
        favoritesLocalSource: FavoritesLocalSource,
        cacheMovieLocalSource: CacheMovieLocalSource
    ): ItunesRepository {
        return ItunesRepositoryImpl(remoteSource, favoritesLocalSource, cacheMovieLocalSource)
    }

    @Provides
    @Singleton
    fun provideMovieBrowserDb(@ApplicationContext context: Context): MovieBrowserDb {
        return Room.databaseBuilder(context, MovieBrowserDb::class.java, "movie_browser.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoritesLocalSource(movieBrowserDb: MovieBrowserDb): FavoritesLocalSource {
        return FavoritesLocalSourceImpl(movieBrowserDb)
    }

    @Provides
    @Singleton
    fun provideCacheMovieLocalSource(movieBrowserDb: MovieBrowserDb): CacheMovieLocalSource {
        return CacheMovieLocalSourceImpl(movieBrowserDb)
    }
}