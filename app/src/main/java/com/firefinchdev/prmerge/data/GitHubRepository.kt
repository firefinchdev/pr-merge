package com.firefinchdev.prmerge.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHubRepository {

    private val api: GitHubApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GitHubApi::class.java)
    }

    suspend fun approvePR(
        owner: String,
        repo: String,
        prNumber: Int,
        token: String
    ): Result<ReviewResponse> {
        return try {
            val response = api.approvePR(
                owner = owner,
                repo = repo,
                prNumber = prNumber,
                token = "token $token",
                review = ReviewRequest()
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to approve PR: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mergePR(
        owner: String,
        repo: String,
        prNumber: Int,
        token: String
    ): Result<MergeResponse> {
        return try {
            val response = api.mergePR(
                owner = owner,
                repo = repo,
                prNumber = prNumber,
                token = "token $token"
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to merge PR: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun extractPRInfo(prLink: String): PRInfo? {
        val regex = Regex("github\\.com/([^/]+)/([^/]+)/pull/(\\d+)")
        val matchResult = regex.find(prLink)

        return if (matchResult != null) {
            val (owner, repo, prNumber) = matchResult.destructured
            PRInfo(owner, repo, prNumber.toInt())
        } else {
            null
        }
    }
}

data class PRInfo(
    val owner: String,
    val repo: String,
    val prNumber: Int
)