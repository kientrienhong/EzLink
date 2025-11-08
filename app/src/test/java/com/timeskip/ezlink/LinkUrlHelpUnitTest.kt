package com.timeskip.ezlink

import com.timeskip.ezlink.features.link.LinkUrlHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LinkUrlHelpUnitTest {
    @Test
    fun getDomain1() {
        val url = "https://www.$EXPECTED_DOMAIN_RESULT"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain ==EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain2() {
        val url = "www.$EXPECTED_DOMAIN_RESULT"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain3() {
        val url = "https://www.$EXPECTED_DOMAIN_RESULT/search?q=hello+world"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain4() {
        val url = "https://www.$EXPECTED_DOMAIN_RESULT/search?q=hello+world"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain5() {
        val url = "http://www.$EXPECTED_DOMAIN_RESULT/search?q=hello+world"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain6() {
        val url = "http://$EXPECTED_DOMAIN_RESULT/search?q=hello+world"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain7() {
        val url = "$EXPECTED_DOMAIN_RESULT/search?q=hello+world"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain8() {
        val url = EXPECTED_DOMAIN_RESULT
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    @Test
    fun getDomain9() {
        val url = "https://$EXPECTED_DOMAIN_RESULT/share/p/16v3BRkMem/"
        val domain = LinkUrlHelper.getDomain(url)
        assert(domain == EXPECTED_DOMAIN_RESULT)
    }

    private companion object {
        const val EXPECTED_DOMAIN_RESULT = "google.com"
    }
}