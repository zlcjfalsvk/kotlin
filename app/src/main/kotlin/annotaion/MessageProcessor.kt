package annotaion


class MessageProcessor {
    private val handlers = mutableMapOf<String, IMessageHandler>()

    fun registerHandler(vararg messages: String, handler: IMessageHandler) {
        messages.forEach { message ->
            handlers[message] = handler
        }
    }

    fun processMessage(message: String): Boolean {
        return handlers[message]?.let {
            it.handle()
            true
        } ?: false
    }
}
