package annotaion


import org.charlie.utils.Printer

/**
 * Message를 읽어 해당 Message로 등록된 handler 가 있으면 실행시키는 예제
 */
fun main() {
    val processor = MessageProcessor()
    MessageHandlerExample(processor)

    println("=== Message Handler Demo ===")
    println("Processing message: 'hello'")
    processor.processMessage("hello")

    println("\nProcessing message: 'goodbye'")
    processor.processMessage("goodbye")

    println("\nProcessing message: '?'")
    processor.processMessage("?")

    println("\nProcessing message: 'unknown'")
    val result = processor.processMessage("unknown")
    if (!result) {
        println("No handler found for message: 'unknown'")
    }

    // Using the Printer class from utils
    val printer = Printer("Demo completed successfully!")
    printer.printMessage()
}
