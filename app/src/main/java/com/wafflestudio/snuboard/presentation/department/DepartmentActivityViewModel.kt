package com.wafflestudio.snuboard.presentation.department

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wafflestudio.snuboard.domain.model.Department
import com.wafflestudio.snuboard.domain.usecase.GetDepartmentInfoUseCase
import com.wafflestudio.snuboard.utils.ErrorResponse
import com.wafflestudio.snuboard.utils.Event
import com.wafflestudio.snuboard.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DepartmentActivityViewModel
@Inject
constructor(
        private val getDepartmentInfoUseCase: GetDepartmentInfoUseCase,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _departmentInfo = MutableLiveData<Department>()

    val departmentInfo: LiveData<Department>
        get() = _departmentInfo

    fun getDepartmentInfo(departmentId: Int) {
        getDepartmentInfoUseCase
                .getDepartmentInfo(departmentId)
                .subscribe({
                    when (it) {
                        is Department -> {
                            _departmentInfo.value = it
                        }
                        is ErrorResponse -> {
                            SingleEvent.triggerToast.value = Event(it.message)
                            Timber.e(it.message)
                        }
                    }
                }, {
                    Timber.e(it)
                })
    }

}