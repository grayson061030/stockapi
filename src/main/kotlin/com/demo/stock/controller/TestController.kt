import com.demo.stock.config.TagType
import com.demo.stock.dto.request.StockSimulationRequest
import com.demo.stock.dto.response.ApiResponse
import com.demo.stock.service.TestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class TestController(private val testService: TestService) {

    @PostMapping("/update-data")
    fun updateRandomData(
        @RequestParam(required = false) tagType: TagType? = null,
        @RequestParam(required = false, defaultValue = "10") count: Int = 10,
        @RequestParam(required = false, defaultValue = "5") minRate: Int = 5,
        @RequestParam(required = false, defaultValue = "20") maxRate: Int = 20
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val result = if (tagType != null) {
            // 특정 태그에 대한 데이터 업데이트
            val request = StockSimulationRequest(
                tagType = tagType,
                count = count,
                minRate = minRate,
                maxRate = maxRate
            )
            val modifiedStocks = testService.updateStocksByTag(request)

            mapOf(
                "tag" to tagType.name,
                "updatedCount" to modifiedStocks.size,
                "minRate" to minRate,
                "maxRate" to maxRate,
                "message" to "${tagType.name} 태그에 대해 ${modifiedStocks.size}개 주식 데이터가 변동률 ${minRate}%~${maxRate}% 범위에서 업데이트되었습니다."
            )
        } else {
            // 모든 태그에 대한 데이터 업데이트
            val updatedCount = testService.updateRandomStockData()

            mapOf(
                "updatedCount" to updatedCount,
                "message" to "총 ${updatedCount}개 주식 데이터가 랜덤하게 업데이트되었습니다."
            )
        }

        return ResponseEntity.ok(ApiResponse.success(result))
    }
}