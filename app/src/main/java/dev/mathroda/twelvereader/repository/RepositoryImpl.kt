package dev.mathroda.twelvereader.repository

import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.network.NetworkService
import dev.mathroda.twelvereader.network.dto.toVoice
import dev.mathroda.twelvereader.network.utils.ApiServiceException
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val service: NetworkService
): Repository {

    override fun getVoices(pageSize: Int, language: String): Flow<Resource<List<Voice>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = service.getSharedVoices(pageSize, language)
                    .voices
                    .map { it.toVoice() }
                emit(Resource.Success(response))
            }catch (e: ApiServiceException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            }catch (e: Throwable ){
                emit(Resource.Error("Something went wrong"))
            }
        }
    }

}