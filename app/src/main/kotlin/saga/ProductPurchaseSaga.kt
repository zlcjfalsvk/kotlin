package saga

// 상품 구매 프로세스를 위한 SAGA 패턴 구현
// SAGA 패턴: 분산 트랜잭션을 관리하기 위한 패턴으로, 각 단계가 실패할 경우 보상 트랜잭션을 통해 이전 단계의 변경사항을 롤백

// 트랜잭션 상태를 나타내는 열거형
enum class TransactionStatus {
    SUCCESS, FAILED
}

// 트랜잭션 단계를 나타내는 인터페이스
interface SagaStep<T> {
    fun execute(context: T): TransactionStatus
    fun compensate(context: T)
}

// 상품 구매 프로세스의 컨텍스트 클래스
data class PurchaseContext(
    val userId: String,
    val productId: String,
    val quantity: Int,
    var orderId: String? = null,
    var paymentId: String? = null,
    var inventoryUpdated: Boolean = false,
    var shippingId: String? = null,
    var error: String? = null
)

// 주문 생성 단계
class CreateOrderStep : SagaStep<PurchaseContext> {
    override fun execute(context: PurchaseContext): TransactionStatus {
        println("Creating order for user ${context.userId}, product ${context.productId}, quantity ${context.quantity}")

        try {
            // 주문 생성 로직 (실제로는 데이터베이스에 저장)
            context.orderId = "ORD-${System.currentTimeMillis()}"
            println("Order created with ID: ${context.orderId}")
            return TransactionStatus.SUCCESS
        } catch (e: Exception) {
            context.error = "Failed to create order: ${e.message}"
            println(context.error)
            return TransactionStatus.FAILED
        }
    }

    override fun compensate(context: PurchaseContext) {
        // 주문 생성 취소 로직
        println("Compensating CreateOrder: Cancelling order ${context.orderId}")
        context.orderId = null
    }
}

// 결제 처리 단계
class ProcessPaymentStep : SagaStep<PurchaseContext> {
    override fun execute(context: PurchaseContext): TransactionStatus {
        println("Processing payment for order ${context.orderId}")

        try {
            // 결제 처리 로직 (실제로는 결제 게이트웨이 호출)
            context.paymentId = "PAY-${System.currentTimeMillis()}"
            println("Payment processed with ID: ${context.paymentId}")
            return TransactionStatus.SUCCESS
        } catch (e: Exception) {
            context.error = "Failed to process payment: ${e.message}"
            println(context.error)
            return TransactionStatus.FAILED
        }
    }

    override fun compensate(context: PurchaseContext) {
        // 결제 취소 로직
        println("Compensating ProcessPayment: Refunding payment ${context.paymentId}")
        context.paymentId = null
    }
}

// 재고 업데이트 단계
class UpdateInventoryStep : SagaStep<PurchaseContext> {
    override fun execute(context: PurchaseContext): TransactionStatus {
        println("Updating inventory for product ${context.productId}, quantity ${context.quantity}")

        try {
            // 재고 업데이트 로직 (실제로는 데이터베이스 업데이트)
            context.inventoryUpdated = true
            println("Inventory updated successfully")
            return TransactionStatus.SUCCESS
        } catch (e: Exception) {
            context.error = "Failed to update inventory: ${e.message}"
            println(context.error)
            return TransactionStatus.FAILED
        }
    }

    override fun compensate(context: PurchaseContext) {
        // 재고 복원 로직
        println("Compensating UpdateInventory: Restoring inventory for product ${context.productId}, quantity ${context.quantity}")
        context.inventoryUpdated = false
    }
}

// 배송 처리 단계
class ProcessShippingStep : SagaStep<PurchaseContext> {
    override fun execute(context: PurchaseContext): TransactionStatus {
        println("Processing shipping for order ${context.orderId}")

        try {
            // 배송 처리 로직 (실제로는 배송 서비스 호출)
            context.shippingId = "SHP-${System.currentTimeMillis()}"
            println("Shipping processed with ID: ${context.shippingId}")
            return TransactionStatus.SUCCESS
        } catch (e: Exception) {
            context.error = "Failed to process shipping: ${e.message}"
            println(context.error)
            return TransactionStatus.FAILED
        }
    }

    override fun compensate(context: PurchaseContext) {
        // 배송 취소 로직
        println("Compensating ProcessShipping: Cancelling shipping ${context.shippingId}")
        context.shippingId = null
    }
}

// SAGA 오케스트레이터 클래스
open class ProductPurchaseSaga {
    private val steps = mutableListOf<SagaStep<PurchaseContext>>()

    init {
        // 단계 등록
        steps.add(CreateOrderStep())
        steps.add(ProcessPaymentStep())
        steps.add(UpdateInventoryStep())
        steps.add(ProcessShippingStep())
    }

    open fun execute(context: PurchaseContext): Boolean {
        // 실행된 단계를 추적하기 위한 리스트
        val executedSteps = mutableListOf<SagaStep<PurchaseContext>>()

        // 모든 단계 실행
        for (step in steps) {
            val status = step.execute(context)
            executedSteps.add(step)

            if (status == TransactionStatus.FAILED) {
                // 실패 시 보상 트랜잭션 실행
                compensate(executedSteps, context)
                return false
            }
        }

        return true
    }

    private fun compensate(executedSteps: List<SagaStep<PurchaseContext>>, context: PurchaseContext) {
        // 역순으로 보상 트랜잭션 실행
        println("Starting compensation transactions...")
        for (step in executedSteps.reversed()) {
            step.compensate(context)
        }
        println("Compensation completed")
    }
}
