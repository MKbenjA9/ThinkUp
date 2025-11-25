package com.example.thinkup.api

import com.example.thinkup.model.Idea
import retrofit2.http.*

interface IdeaApi {

    @GET("ideas")
    suspend fun getIdeas(): List<Idea>

    @POST("ideas")
    suspend fun saveIdea(@Body idea: Idea): Idea

    @DELETE("ideas/{id}")
    suspend fun deleteIdea(@Path("id") id: Long)

    @GET("ideas/author/{name}")
    suspend fun getIdeasByAuthor(@Path("name") name: String): List<Idea>
}
