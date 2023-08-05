import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage

class BirdsVIewModel: ViewModel() {
    private val _uiState = MutableStateFlow<BirdsUIState>(BirdsUIState())
    val uiState = _uiState.asStateFlow()

    private val httpClient = HttpClient{
        install(ContentNegotiation){
            json()
        }
    }

    init {
        updateImages()
    }

    override fun onCleared() {
        httpClient.close()
        super.onCleared()
    }

    private fun updateImages() {
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images= images)
            }
        }
    }

    fun selectCategory(category: String){
        _uiState.update {
            it.copy(
                selectedCategory = category
            )
        }
    }

    private suspend fun getImages(): List<BirdImage>{
        return httpClient.get("http://sebastianaigner.github.io/demo-image-api/pictures.json").body()
    }

}


data class BirdsUIState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
){
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}