package cash.just.support

import com.google.gson.Gson
import org.junit.Test

class SupportPageParsing {
    @Test
    fun pareJson() {
        val json = ClassLoader.getSystemResource("supportv1.json").readText()
        val support = Gson().fromJson(json, SupportResponse::class.java)
        assert(support.language == "es")
        assert(support.version == 1)
    }
}
