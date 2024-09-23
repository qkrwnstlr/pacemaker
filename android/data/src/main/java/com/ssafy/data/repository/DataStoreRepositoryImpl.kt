package com.ssafy.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    DataStoreRepository {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val imgUrlKey = stringPreferencesKey("imgUrl")

    override suspend fun setImgUrl(newImgUrl: String) {
        context.datastore.edit { preference ->
            preference[imgUrlKey] = newImgUrl
        }
    }

    override suspend fun saveUser(user: User) {
        context.datastore.edit { preferences ->
            preferences[stringPreferencesKey("user_id")] = user.uid
            preferences[stringPreferencesKey("user_name")] = user.name
            preferences[intPreferencesKey("cadence")] = user.cadence
            preferences[intPreferencesKey("distance")] = user.distance
            preferences[intPreferencesKey("height")] = user.height
            preferences[intPreferencesKey("minute")] = user.minute
            preferences[intPreferencesKey("pace")] = user.pace
            preferences[intPreferencesKey("weight")] = user.weight
            preferences[intPreferencesKey("year")] = user.year
            preferences[intPreferencesKey("trainCount")] = user.trainCount
            preferences[intPreferencesKey("trainTime")] = user.trainTime
            preferences[floatPreferencesKey("trainDistance")] = user.trainDistance
            preferences[stringPreferencesKey("gender")] = user.gender
            preferences[longPreferencesKey("coachNumber")] = user.coachNumber
        }
    }

    override fun getImgUrl(): Flow<String> =
        context.datastore.data.catch { emit(emptyPreferences()) }
            .map { preference -> preference[imgUrlKey] ?: "" }

    override suspend fun clearImgUrl() {
        context.datastore.edit {
            it.clear()
        }
    }
}
