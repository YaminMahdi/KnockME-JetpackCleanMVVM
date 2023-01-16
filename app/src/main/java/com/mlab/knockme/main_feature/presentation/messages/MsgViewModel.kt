package com.mlab.knockme.main_feature.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.use_case.MsgUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MsgViewModel @Inject constructor(
    private val msgUseCases: MsgUseCases
) : ViewModel() {
    private val _state= MutableStateFlow<List<Msg>>(emptyList())
    val state = _state.asStateFlow()

    private var getMsgJob: Job? =null

    
    suspend fun getMeg(path: String) {
        getMsgJob?.cancel()
        getMsgJob= msgUseCases.getMsg(path)
            .onEach {
                _state.value =it
            }
            .launchIn(viewModelScope)
    }
}