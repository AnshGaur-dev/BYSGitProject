package com.example.bharatyatrisathi


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bharatyatrisathi.model.CompletionRequest
import com.example.bharatyatrisathi.model.CompletionResponse
import com.example.bharatyatrisathi.model.Message
import com.example.chatgptapp.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

class ChatGptViewModel : ViewModel(){

    private val _messageList = MutableLiveData<MutableList<Message>>()
    val messageList : LiveData<MutableList<Message>> get() = _messageList

    init {
        _messageList.value = mutableListOf()
    }

    fun addToChat(message : String , sentBy : String , timestamp : String){
        val currentList = _messageList.value ?: mutableListOf()
        currentList.add(Message(message,sentBy,timestamp))
        _messageList.postValue(currentList)
    }


    private fun addResponse(response : String){
        _messageList.value?.removeAt(_messageList.value?.size?.minus(1) ?: 0)
        addToChat(response,Message.SENT_BY_BOT,getCurrentTimestamp())
    }

    fun callApi(question : String){
        addToChat("Typing....",Message.SENT_BY_BOT,getCurrentTimestamp())
        val text="Please respond only with information related to medical support or tour planning. The user has requested: $question"
        val completionRequest = CompletionRequest(
            prompt = text,
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCompletions(completionRequest)
                handleApiResponse(response)
            }catch (e: SocketTimeoutException){
                addResponse("Timeout :  $e")
            }
        }
    }

    private suspend fun handleApiResponse(response: Response<CompletionResponse>) {
        withContext(Dispatchers.Main){
            if (response.isSuccessful){
                response.body()?.let { completionResponse ->
                    val result = completionResponse.response
                    if (result != null){
                        addResponse(result.trim())
                    }else{
                        addResponse("No choices found")
                    }
                }
            }else{
                addResponse("Failed to get response ${response.errorBody()}")
            }
        }

    }

    fun getCurrentTimestamp(): String {
       return SimpleDateFormat("hh mm a", Locale.getDefault()).format(Date())
    }


}