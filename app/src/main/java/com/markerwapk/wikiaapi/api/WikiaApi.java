package com.markerwapk.wikiaapi.api;

import com.markerwapk.wikiaapi.models.WikiaHubs;
import com.markerwapk.wikiaapi.models.WikisList;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Markerwapk on 15.06.16.
 */
public interface WikiaApi {

    @GET("WikiaHubs/HubsV3List?lang=en")
    Observable<WikiaHubs> getWikiaHubs();

    @GET("Wikis/List?expand=1")
    Observable<WikisList> getWikis(@Query("hub") String hub, @Query("limit") int limit,@Query("batch") int batch);
}
