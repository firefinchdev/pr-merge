package com.firefinchdev.prmerge.data

import retrofit2.Response
import retrofit2.http.*

interface GitHubApi {

    @POST("repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun approvePR(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") prNumber: Int,
        @Header("Authorization") token: String,
        @Body review: ReviewRequest
    ): Response<ReviewResponse>

    @PUT("repos/{owner}/{repo}/pulls/{pull_number}/merge")
    suspend fun mergePR(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") prNumber: Int,
        @Header("Authorization") token: String
    ): Response<MergeResponse>
}

data class ReviewRequest(
    val event: String = "APPROVE"
)

data class ReviewResponse(
    val id: Long,
    val state: String
)

data class MergeResponse(
    val sha: String,
    val merged: Boolean,
    val message: String
)