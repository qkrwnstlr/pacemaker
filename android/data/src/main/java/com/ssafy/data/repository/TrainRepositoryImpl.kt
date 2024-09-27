package com.ssafy.data.repository

import android.content.Context
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.ERROR
import com.ssafy.data.source.train.TrainDataSource
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.response.ResponseResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainRepositoryImpl @Inject constructor(
    private val trainDataSource: TrainDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : TrainRepository {

    override suspend fun getCoaching(coachingRequest: CoachingRequest): ResponseResult<File> {
        val response = withContext(ioDispatcher) { trainDataSource.getCoaching(coachingRequest) }
        return response.toFile()
    }

    private fun Response<ResponseBody>.toFile(): ResponseResult<File> {
        body()?.let {
            if (isSuccessful) return ResponseResult.Success(it.byteStream().makeFile(), "")
            else return ResponseResult.Error(message = ERROR)
        }

        errorBody()?.let {
            val errorMessage = it.charStream().readLines().joinToString()
            if (errorMessage.isNotBlank()) return ResponseResult.Error(message = errorMessage)
        }

        return ResponseResult.Error(message = ERROR)
    }

    private fun InputStream.makeFile(): File {
        val uuid = UUID.randomUUID().toString()
        val file = File(context.cacheDir, "${uuid}.wav")
        FileOutputStream(file).use { outputStream ->
            copyTo(outputStream)
        }
        return file
    }

}