package com.odobo.grails.plugin.springsecurity.rest

import com.odobo.grails.plugin.springsecurity.rest.token.bearer.BearerTokenAuthenticationFailureHandler
import com.odobo.grails.plugin.springsecurity.rest.token.bearer.BearerTokenReader
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import spock.lang.Specification

class BearerTokenAuthenticationFailureHandlerSpec extends Specification {

    def handler = new BearerTokenAuthenticationFailureHandler()
    def mockBearerTokenReader = Mock(BearerTokenReader)

    def setup() {
        handler.tokenReader = mockBearerTokenReader
    }

    def "when bad credentials credentials are sent, it responds 401"() {
        given:
        def request  = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()

        when:
        def exception = new AuthenticationCredentialsNotFoundException( 'No credentials :-(' )
        handler.onAuthenticationFailure( request, response, exception )

        then:
        response.status == 401
        response.getHeader( 'WWW-Authenticate' ) == 'Bearer'
    }

    def "it will send a 401 status and WWW-Authenticate header with an error param when credentials are invalid"() {
        given:
        def request  = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()

        when:
        def exception = new TokenNotFoundException( 'Bad token :-(' )
        handler.onAuthenticationFailure( request, response, exception )

        then:
        1 * mockBearerTokenReader.findToken(request) >> "wrongToken"
        response.status == 401
        response.getHeader( 'WWW-Authenticate' ) == 'Bearer error="invalid_token"'
    }

}
