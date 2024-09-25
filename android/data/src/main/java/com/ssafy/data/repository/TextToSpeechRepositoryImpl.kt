package com.ssafy.data.repository

import android.content.Context
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.ERROR
import com.ssafy.data.source.tts.TextToSpeechDataSource
import com.ssafy.domain.repository.TextToSpeechRepository
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
class TextToSpeechRepositoryImpl @Inject constructor(
    private val textToSpeechDataSource: TextToSpeechDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : TextToSpeechRepository {

    override suspend fun getTTS(message: String, coachIndex: Long): ResponseResult<File> {
        val response = withContext(ioDispatcher) {
            textToSpeechDataSource.getTTS(message, coachIndex)
        }
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
