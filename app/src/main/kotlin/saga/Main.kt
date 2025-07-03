package saga

// 상품 구매 SAGA 패턴 구현 예제의 메인 함수
fun main() {
    println("=== Product Purchase SAGA Pattern Example ===")

    // 성공 시나리오 실행
    runSuccessScenario()

    println("\n=== Failure Scenario ===\n")

    // 실패 시나리오 실행
    runFailureScenario()
}

// 성공 시나리오 실행 함수
fun runSuccessScenario() {
    println("Running successful purchase scenario...")

    // 구매 컨텍스트 생성
    val context = PurchaseContext(
        userId = "user123",
        productId = "product456",
        quantity = 2
    )

    // SAGA 오케스트레이터 생성 및 실행
    val saga = ProductPurchaseSaga()
    val result = saga.execute(context)

    // 결과 출력
    if (result) {
        println("\nPurchase completed successfully!")
        println("Order ID: ${context.orderId}")
        println("Payment ID: ${context.paymentId}")
        println("Shipping ID: ${context.shippingId}")
    } else {
        println("\nPurchase failed: ${context.error}")
    }
}

// 실패 시나리오 실행 함수
fun runFailureScenario() {
    println("Running failure scenario with inventory update failure...")

    // 구매 컨텍스트 생성
    val context = PurchaseContext(
        userId = "user789",
        productId = "outOfStockProduct",
        quantity = 100  // 재고 부족 상황을 시뮬레이션하기 위한 큰 수량
    )

    // 실패 시나리오를 위한 커스텀 SAGA 오케스트레이터 생성
    ProductPurchaseSaga()

    // 기존 단계 대신 실패하는 재고 업데이트 단계를 사용하는 SAGA 생성
    val failingSaga = object : ProductPurchaseSaga() {
        override fun execute(context: PurchaseContext): Boolean {
            // 주문 생성 단계 실행
            val orderStep = CreateOrderStep()
            if (orderStep.execute(context) == TransactionStatus.FAILED) {
                return false
            }

            // 결제 처리 단계 실행
            val paymentStep = ProcessPaymentStep()
            if (paymentStep.execute(context) == TransactionStatus.FAILED) {
                // 보상 트랜잭션 실행
                orderStep.compensate(context)
                return false
            }

            // 재고 업데이트 단계에서 의도적으로 실패 발생
            println("Simulating inventory update failure...")
            context.error = "Insufficient inventory for product ${context.productId}"
            println(context.error)

            // 보상 트랜잭션 실행
            println("Starting compensation transactions...")
            paymentStep.compensate(context)
            orderStep.compensate(context)
            println("Compensation completed")

            return false
        }
    }

    // 실패 시나리오 실행
    val result = failingSaga.execute(context)

    // 결과 출력
    if (!result) {
        println("\nPurchase failed as expected: ${context.error}")
        println("Final state after compensation:")
        println("Order ID: ${context.orderId ?: "None (compensated)"}")
        println("Payment ID: ${context.paymentId ?: "None (compensated)"}")
        println("Inventory Updated: ${context.inventoryUpdated}")
        println("Shipping ID: ${context.shippingId ?: "None"}")
    } else {
        println("\nUnexpected success in failure scenario!")
    }
}

