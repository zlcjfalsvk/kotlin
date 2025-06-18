package annotaion


class MessageHandlerExample(private val processor: MessageProcessor) {

    init {
        registerHandlers()
    }

    /**
     * Processor를 주입 받아 handler 를 등록
     */
    private fun registerHandlers() {
        processor.registerHandler("hello", "hi", "hey", handler = { handleGreeting() })
        processor.registerHandler("bye", "goodbye", "see you", handler = { handleFarewell() })
        processor.registerHandler("help", "?", handler = { handleHelp() })
    }

    @MessageHandler("hello", "hi", "hey")
    fun handleGreeting() {
        println("Greeting received! Hello to you too!")
    }

    @MessageHandler("bye", "goodbye", "see you")
    fun handleFarewell() {
        println("Farewell received! Goodbye!")
    }

    @MessageHandler("help", "?")
    fun handleHelp() {
        println("Help requested. Here are the available commands:")
        println("- hello, hi, hey: Send a greeting")
        println("- bye, goodbye, see you: Send a farewell")
        println("- help, ?: Show this help message")
    }
}
