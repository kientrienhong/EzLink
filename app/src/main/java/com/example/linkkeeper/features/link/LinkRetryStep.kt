package com.example.linkkeeper.features.link

sealed class LinkRetryStep(val nextStep: LinkRetryStep? = null) {
    private object Initial : LinkRetryStep(AddHttpsProtocol)
    private object AddHttpsProtocol : LinkRetryStep(AddHttpProtocol)
    private object AddHttpProtocol : LinkRetryStep(null)

    fun isLast(): Boolean = nextStep == null

    companion object {
        fun getInitialStep(): LinkRetryStep = Initial

        fun getCurrentUrlFormat(url: String, currentStep: LinkRetryStep): String = when (currentStep) {
            Initial -> url
            AddHttpsProtocol -> "https://$url"
            AddHttpProtocol -> "http://$url"
        }
    }
}