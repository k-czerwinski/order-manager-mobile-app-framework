package pl.edu.agh.framework.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow

abstract class CommonListViewModel<T> : CommonViewModel<List<T>>()
abstract class CommonViewModel<T> : ViewModel() {
    protected val state = MutableStateFlow<State<T>>(State.Empty)

    /**
     * This function perform call to API and update state with the result.
     * In the inherited class this function should be used to initialize viewmodel:
     *     init {
     *         viewModelScope.launch {
     *             fetchData()
     *         }
     *     }
     */
    protected abstract suspend fun fetchData()

    fun reset() {
        state.value = State.Empty
    }

    sealed class State<out R> {
        object Empty : State<Nothing>()
        object Loading : State<Nothing>()
        data class Success<out R>(val data: R) : State<R>()
        data class Error(val message: String) : State<Nothing>()
    }

    companion object {
        inline fun <reified VM : ViewModel> provideFactory(
            crossinline creator: () -> VM
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(VM::class.java)) {
                    return creator() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}